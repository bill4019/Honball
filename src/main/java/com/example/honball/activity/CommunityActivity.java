package com.example.honball.activity;

import static com.example.honball.Util.GALLERY_IMAGE;
import static com.example.honball.Util.GALLERY_VIDEO;
import static com.example.honball.Util.INTENT_MEDIA;
import static com.example.honball.Util.INTENT_PATH;
import static com.example.honball.Util.isImageFile;
import static com.example.honball.Util.isStorageUrl;
import static com.example.honball.Util.isVideoFile;
import static com.example.honball.Util.showToast;
import static com.example.honball.Util.storageUrlToName;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.honball.CommunityInfo;
import com.example.honball.R;
import com.example.honball.Util;
import com.example.honball.view.ContentsItemView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

public class CommunityActivity extends CommonActivity {

    //전역
    private static final String TAG = "CommunityActivity";
    private FirebaseUser user;
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    private int pathCount;
    private int successCount;
    private ImageView selectedImageView;
    private EditText selectedEditText;
    private EditText et_content;
    private EditText et_title;
    private RelativeLayout loaderLayout;
    private RelativeLayout layout_edit_contents;
    private CommunityInfo communityInfo;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        setToolbarTitle("Honball");

        //초기화
        parent = findViewById(R.id.layout_contents);
        layout_edit_contents = findViewById(R.id.layout_edit_contents);
        loaderLayout = findViewById(R.id.loaderLayout);
        et_content = findViewById(R.id.et_content);
        et_title = findViewById(R.id.et_title);

        findViewById(R.id.btn_write).setOnClickListener(onClickListener);
        findViewById(R.id.btn_image).setOnClickListener(onClickListener);
        findViewById(R.id.btn_video).setOnClickListener(onClickListener);
        findViewById(R.id.btn_edit_image).setOnClickListener(onClickListener);
        findViewById(R.id.btn_edit_video).setOnClickListener(onClickListener);
        findViewById(R.id.btn_delete).setOnClickListener(onClickListener);
        layout_edit_contents.setOnClickListener(onClickListener);
        et_content.setOnFocusChangeListener(onFocusChangeListener);

        //제목 focus일때 사진 맨위로
        et_title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    selectedEditText = null;
                }
            }
        });
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        util = new Util(this);
        communityInfo = (CommunityInfo) getIntent().getSerializableExtra("communityInfo");  // communityInfo 값 가져오기
        postInit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.add(path);

                    ContentsItemView contentsItemView = new ContentsItemView(this);

                    if (selectedEditText == null) {
                        parent.addView(contentsItemView);
                    } else {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            if (parent.getChildAt(i) == selectedEditText.getParent()) {
                                parent.addView(contentsItemView, i + 1);
                                break;
                            }
                        }
                    }

                    contentsItemView.setImage(path);
                    contentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            layout_edit_contents.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) view;
                        }
                    });
                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);
                }
                break;
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.set(parent.indexOfChild((View) selectedImageView.getParent()) - 1, path);
                    Glide.with(this).load(path).override(1000).into(selectedImageView);
                    Log.e(TAG, "클릭");
                }
                break;
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_write:
                    communityUpdate();
                    break;
                case R.id.btn_image:
                    myStartActivity(GalleryActivity.class, GALLERY_IMAGE, 0);
                    break;
                case R.id.btn_video:
                    myStartActivity(GalleryActivity.class, GALLERY_VIDEO, 0);
                    break;
                case R.id.layout_edit_contents:
                    if (layout_edit_contents.getVisibility() == View.VISIBLE) {
                        layout_edit_contents.setVisibility(View.GONE);
                    }
                    break;
                case R.id.btn_edit_image:
                    myStartActivity(GalleryActivity.class, GALLERY_IMAGE, 1);
                    layout_edit_contents.setVisibility(View.GONE);
                    break;
                case R.id.btn_edit_video:
                    myStartActivity(GalleryActivity.class, GALLERY_VIDEO, 1);
                    layout_edit_contents.setVisibility(View.GONE);
                    break;
                case R.id.btn_delete:
                    final View selectedView = (View) selectedImageView.getParent();
                    String path = pathList.get(parent.indexOfChild(selectedView) - 1);
                    if(isStorageUrl(path)){
                        // Create a reference to the file to delete
                        StorageReference desertRef = storageRef.child("posts/" + storageUrlToName(path));
                        // Delete the file
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showToast(CommunityActivity.this, "storage 삭제했습니다.");
                                // File deleted successfully
                                pathList.remove(parent.indexOfChild(selectedView) - 1);
                                parent.removeView(selectedView);
                                layout_edit_contents.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                showToast(CommunityActivity.this,"storage 삭제를 실패했습니다.");
                                // Uh-oh, an error occurred!
                            }
                        });
                    }else{
                        pathList.remove(parent.indexOfChild(selectedView) - 1);
                        parent.removeView(selectedView);
                        layout_edit_contents.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                selectedEditText = (EditText) view;
            }
        }
    };

    private void communityUpdate() {
        final String title = ((EditText) findViewById(R.id.et_title)).getText().toString();

        if (title.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);
            final ArrayList<String> contentsList = new ArrayList<>();
            final ArrayList<String> formatList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            final DocumentReference documentReference = communityInfo == null ? firebaseFirestore.collection("posts").document()
                    : firebaseFirestore.collection("posts").document(communityInfo.getId());    //3항연산자
            final Date date = communityInfo == null ? new Date() : communityInfo.getWriteDay();

            for (int i = 0; i < parent.getChildCount(); i++) {
                LinearLayout linearLayout = (LinearLayout) parent.getChildAt(i);
                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View view = linearLayout.getChildAt(j);
                    if (view instanceof EditText) {
                        String text = ((EditText) view).getText().toString();
                        if (text.length() > 0) {
                            contentsList.add(text);
                            formatList.add("text");
                        }
                    } else if (!isStorageUrl(pathList.get(pathCount))) {
                        String path = pathList.get(pathCount);
                        successCount++;
                        contentsList.add(path);

                        if(isImageFile(path)){
                            formatList.add("image");
                        }else if(isVideoFile(path)){
                            formatList.add("video");
                        }else{
                            formatList.add("text");
                        }

                        String[] pathArray = path.split("\\.");
                        final StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + "" + pathArray[pathArray.length - 1]);
                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));

                            //메타데이터
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentsList.size() - 1)).build();

                            UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));

                                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            successCount--;
                                            contentsList.set(index, uri.toString());
                                            if (successCount == 0) {
                                                //완료
                                                CommunityInfo communityInfo = new CommunityInfo(title, contentsList, formatList, user.getUid(), date);
                                                communityUploader(documentReference, communityInfo);
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (FileNotFoundException e) {
                            Log.e("로그", "에러" + e.toString());
                        }
                        pathCount++;
                    }
                }
            }
            if (successCount == 0) {
                //완료
                communityUploader(documentReference, new CommunityInfo(title, contentsList, formatList, user.getUid(), date));
            }
        } else {
            showToast(CommunityActivity.this, "제목을 입력해주세요.");
        }
    }

    private void communityUploader(DocumentReference documentReference, final CommunityInfo communityInfo) {
        documentReference.set(communityInfo.getCommunityInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loaderLayout.setVisibility(View.GONE);
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Intent intent = new Intent();
                        intent.putExtra("communityInfo", communityInfo);
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


    // 게시글 수정 함수
    private void postInit() {
        if (communityInfo != null) {
            et_title.setText(communityInfo.getTitle());
            ArrayList<String> contentsList = communityInfo.getContents();
            for (int i = 0; i < contentsList.size(); i++) {
                String contents = contentsList.get(i);
                if (isStorageUrl(contents)) { //url 형식이 맞는지 체크
                    pathList.add(contents);
                    ContentsItemView contentsItemView = new ContentsItemView(this);

                    parent.addView(contentsItemView);

                    contentsItemView.setImage(contents);
                    contentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            layout_edit_contents.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) view;
                        }
                    });
                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);
                    if (i < contentsList.size() - 1) {
                        String nextContents = contentsList.get(i + 1);
                        if (!isStorageUrl(nextContents)) {
                            contentsItemView.setText(nextContents);
                        }
                    }
                } else if (i == 0) {
                    et_content.setText(contents);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void myStartActivity(Class c, int media, int requestCode) {
        Intent intent = new Intent(this, c);
        intent.putExtra(INTENT_MEDIA, media);
        startActivityForResult(intent, requestCode);
    }
}