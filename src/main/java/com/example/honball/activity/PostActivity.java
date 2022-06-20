package com.example.honball.activity;

import static com.example.honball.Util.INTENT_PATH;
import static com.example.honball.adapter.CommentAdapter.setListViewHeight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.honball.CommentInfo;
import com.example.honball.CommunityInfo;
import com.example.honball.FirebaseHelper;
import com.example.honball.MemberInfo;
import com.example.honball.R;
import com.example.honball.adapter.CommentAdapter;
import com.example.honball.adapter.CommunityAdapter;
import com.example.honball.listener.OnPostListener;
import com.example.honball.view.ReadContentsView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

public class PostActivity extends CommonActivity {

    private static final String TAG = "PostActivity";
    private CommunityInfo communityInfo;
    private FirebaseHelper firebaseHelper;
    private ReadContentsView readContentsView;
    private RelativeLayout loaderLayout;
    private LinearLayout layout_contents;
    private CommentInfo commentInfo;
    private ArrayList<CommentInfo> commentArrayList;
    private ArrayList<CommentInfo> commentArrayList2;
    private CommentAdapter commentAdapter;
    private Context mContext;
    private LinearLayout layout_post;
    private Activity activity;
    private boolean updating;
    private Button btn_delete_comment;
    private CommunityAdapter communityAdapter;
    private String profilePath;
    private ImageView comment_photo;
    private String photoUrl;
    private String nickname;

    private StyledPlayerView styledPlayerView;
    private ImageView fullscreenButton;
    private boolean fullscreen = false;
    private MemberInfo memberInfo;

    ListView comment_list;
    EditText comment_edit;
    TextView ci_nickname_text;
    CommentAdapter ca;
    int count = 0;
    private int successCount;

    View footer;
    private FirebaseUser user;
    private LinearLayout parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        this.mContext = getApplicationContext();

        parent = findViewById(R.id.layout_post);
        commentInfo = (CommentInfo) getIntent().getSerializableExtra("commentInfo");  // communityInfo 값 가져오기
        communityInfo = (CommunityInfo) getIntent().getSerializableExtra("communityInfo");  // communityInfo 값 가져오기
        layout_contents = findViewById(R.id.layout_contents);
        readContentsView = findViewById(R.id.readContentsView);
        loaderLayout = findViewById(R.id.loaderLayout);
        layout_post = (LinearLayout) findViewById(R.id.layout_post);

        firebaseHelper = new FirebaseHelper(this);
        firebaseHelper.setOnPostListener(onPostListener);

        commentArrayList = new ArrayList<>();
        comment_list = (ListView) findViewById(R.id.comment_list);

        btn_delete_comment = (Button) findViewById(R.id.comment_delete);
//        btn_delete_comment.setOnClickListener(this);

        uiUpdate();
        fullScreenMode(communityInfo);
        insertComment();
        longClick();
        comment_photo(communityInfo);
        init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    communityInfo = (CommunityInfo) data.getSerializableExtra("communityInfo");
                    layout_contents.removeAllViews();
                    uiUpdate();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //오른쪽 상단 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                firebaseHelper.storageDelete(communityInfo);
                return true;
            case R.id.menu_modify:
                myStartActivity(CommunityActivity.class, communityInfo);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void longClick() {
        comment_list.setAdapter(commentAdapter);
        comment_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView parent, View v, int position, long id) {
                Log.d("LONG CLICK", "OnLongClickListener");
                PopupMenu popup = new PopupMenu(PostActivity.this, v);
                getMenuInflater().inflate(R.menu.post, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem a_item) {
                        final CommentInfo commentInfo = (CommentInfo) commentAdapter.getItem(position);
                        switch (a_item.getItemId()) {
                            case R.id.menu_modify:
                                Log.e("댓글 수정", "수정");
                                break;
                            case R.id.menu_delete:
                                Log.e("댓글 삭제", "삭제");
                                commentDelete(commentInfo);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
                return true;
            }
        });
    }

    private void insertComment() {
        findViewById(R.id.btn_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("댓글등록 : ", "클릭");
                commentUpdate(communityInfo);
                finish();
                overridePendingTransition(0, 0);//인텐트 효과 없애기
                Intent intent = getIntent(); //인텐트
                startActivity(intent); //액티비티 열기
                overridePendingTransition(0, 0);//인텐트 효과 없애기
            }
        });
    }
   /* @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_comment:
                Log.e("댓글등록 : ", "클릭");
                commentUpdate(communityInfo);
                *//*
                finish();
                overridePendingTransition(0, 0);//인텐트 효과 없애기
                Intent intent = getIntent(); //인텐트
                startActivity(intent); //액티비티 열기
                overridePendingTransition(0, 0);//인텐트 효과 없애기
                 *//*
                break;
            case R.id.comment_delete:
                Log.e("댓글삭제 : ", "클릭");
        }
    }*/


    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(CommunityInfo communityInfo) {
            Log.e("로그 : ", "삭제 성공");
        }

        @Override
        public void onModify() {
            Log.e("로그 : ", "수정 성공");
        }
    };

    private void uiUpdate() {
        setToolbarTitle("Honball");
        readContentsView.setCommunityInfo(communityInfo);
    }

    public void fullScreenMode(CommunityInfo communityInfo) {
        ArrayList<String> contentsList = communityInfo.getContents();
        ArrayList<String> formatList = communityInfo.getFormats();

        for (int i = 0; i < contentsList.size(); i++) {
            String formats = formatList.get(i);

            if (formats.equals("video")) {
                styledPlayerView = findViewById(R.id.video_player);
                fullscreenButton = styledPlayerView.findViewById(R.id.exo_fullscreen_icon);
                fullscreenButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (fullscreen) {
                            fullscreenButton.setImageDrawable(ContextCompat.getDrawable(PostActivity.this, R.drawable.ic_baseline_fullscreen_24));
                            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().show();
                            }
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) styledPlayerView.getLayoutParams();
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                            styledPlayerView.setLayoutParams(params);
                            fullscreen = false;
                        } else {
                            fullscreenButton.setImageDrawable(ContextCompat.getDrawable(PostActivity.this, R.drawable.ic_baseline_fullscreen_exit_24));
                            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().hide();
                            }
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) styledPlayerView.getLayoutParams();
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            params.height = (int) (800 * getApplicationContext().getResources().getDisplayMetrics().density);
                            styledPlayerView.setLayoutParams(params);
                            fullscreen = true;
                        }
                    }
                });
            }
        }
    }

    public void init() {
        comment_list = (ListView) findViewById(R.id.comment_list);
        setTest(false, commentInfo);
        setList();
        setFooter(); //footer 세팅
    }

    private void setTest(final boolean clear, CommentInfo commentInfo) {
        updating = true;
        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        final String id = communityInfo.getId();
        Date date = commentArrayList.size() == 0 || clear ? new Date() : commentArrayList.get(commentArrayList.size() - 1).getCommentWriteDay();

        CollectionReference collectionReference = firebaseFirestore.collection("posts").document(id).collection("comment");
        collectionReference.orderBy("commentWriteDay", Query.Direction.DESCENDING).whereLessThan("commentWriteDay", date).limit(10).get()
                //firebaseFirestore.collection("posts").document(id).collection("comment").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (clear) {
                                commentArrayList.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                firebaseFirestore.collection("users").document(document.getData().get("writer").toString())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        photoUrl = document.getData().get("photoUrl").toString();
                                                        nickname = document.getData().get("nickname").toString();
                                                    } else {
                                                        Log.d(TAG, "No such document !!!");
                                                    }
                                                } else {
                                                    Log.d(TAG, "get failed with ", task.getException());
                                                }
                                                commentArrayList.add(new CommentInfo(
                                                        document.getData().get("comment").toString(),
                                                        nickname,
                                                        new Date((document.getDate("commentWriteDay").getTime())),
                                                        document.getId(),
                                                        document.getData().get("writer").toString(),
                                                        photoUrl
                                                ));
                                                commentAdapter = new CommentAdapter(mContext, PostActivity.this, PostActivity.this, commentArrayList);
                                                commentAdapter.notifyDataSetChanged();
                                                comment_list.setAdapter(commentAdapter);
                                                setListViewHeight(comment_list);
                                            }
                                        });

                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updating = false;
                    }
                });
    }

    private void comment_photo(final CommunityInfo communityInfo) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        final String id = communityInfo.getId();


    }

    private void commentUpdate(final CommunityInfo communityInfo) {
        final String comment = ((EditText) findViewById(R.id.comment_edit)).getText().toString();
        final String id = communityInfo.getId();
        final String writer = user.getUid();
        Log.e("community Id : ", id);

        if (comment.length() > 0) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            final DocumentReference documentReference = commentInfo == null ? firebaseFirestore.collection("posts").document(id).collection("comment").document()
                    : firebaseFirestore.collection("posts").document(id).collection("comment").document();
            final Date date = commentInfo == null ? new Date() : commentInfo.getCommentWriteDay();
/*
            for(int i = 0; i < parent.getChildCount(); i++){
                LinearLayout linearLayout = (LinearLayout) parent.getChildAt(i);
                for(int j = 0; j < linearLayout.getChildCount(); j++){
                    View view = linearLayout.getChildAt(j);
                    String text = ((EditText) view).getText().toString();
                    commentList.add(text);
                }
            }
 */
            /*
            firebaseFirestore.collection("users")
                    .whereEqualTo("nickname", true)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

             */

            DocumentReference docRef = firebaseFirestore.collection("users").document(user.getUid());
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    MemberInfo memberInfo = documentSnapshot.toObject(MemberInfo.class);
                    String nickname = memberInfo.getNickname();
                    String photoUrl = memberInfo.getPhotoUrl();

                    CommentInfo commentInfo = new CommentInfo(comment, date, documentReference.getId(), writer);
                    commentUploader(documentReference, commentInfo);
                    Log.e("documentReference : ", documentReference.getId());

                    Log.e("writer : ", writer);
                }
            });
        }
    }

    private void commentUploader(DocumentReference documentReference, final CommentInfo commentInfo) {
        documentReference.set(commentInfo.getCommentInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loaderLayout.setVisibility(View.GONE);
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Intent intent = new Intent();
                        intent.putExtra("commentInfo", commentInfo);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loaderLayout.setVisibility(View.GONE);
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void setList() {
        ca = new CommentAdapter(getApplicationContext(), PostActivity.this, this, commentArrayList);
        comment_list.setAdapter(ca);
        comment_list.setSelection(commentArrayList.size() - 1);
        comment_list.setDivider(null);
        comment_list.setSelectionFromTop(0, 0);
    }

    public void delete_comment(int p) {
        //commentArrayList.remove(p);
        //commentAdapter.notifyDataSetChanged();
        //commentDelete();
    }

    private void setFooter() {
        comment_edit = findViewById(R.id.comment_edit);
        Button commentinput_btn = findViewById(R.id.btn_comment);
    }

    private void commentDelete(CommentInfo commentInfo) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        final String id = communityInfo.getId();
        final String commentId = commentInfo.getId();
        Log.e("communityInfo Id : ", id);

        Log.e("commentInfo Id : ", commentId);


        CollectionReference collectionReference = firebaseFirestore.collection("posts").document(id).collection("comment");
        collectionReference.get()
                //firebaseFirestore.collection("posts").document(id).collection("comment").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Task<Void> voidTask = firebaseFirestore.collection("posts").document(id).collection("comment").document(commentId)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error deleting document", e);
                                        }
                                    });
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updating = true;
                        commentAdapter.notifyDataSetChanged();
                        finish();
                        overridePendingTransition(0, 0);//인텐트 효과 없애기
                        Intent intent = getIntent(); //인텐트
                        startActivity(intent); //액티비티 열기
                        overridePendingTransition(0, 0);//인텐트 효과 없애기
                    }
                });

    }

    private void myStartActivity(Class c, CommunityInfo communityInfo) {
        Intent intent = new Intent(this, c);
        intent.putExtra("communityInfo", communityInfo);
        startActivityForResult(intent, 0);
    }

}