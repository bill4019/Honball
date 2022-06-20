package com.example.honball.view;

import static android.content.ContentValues.TAG;
import static com.example.honball.adapter.CommentAdapter.setListViewHeight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.honball.CommentInfo;
import com.example.honball.CommunityInfo;
import com.example.honball.R;
import com.example.honball.activity.PostActivity;
import com.example.honball.adapter.CommentAdapter;
import com.example.honball.adapter.CommunityAdapter;
import com.example.honball.fragment.HomeFragment;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReadContentsView extends LinearLayout {
    private Context context;
    private LayoutInflater layoutInflater;
    private int moreView = -1;
    private ArrayList<ExoPlayer> playerArrayList = new ArrayList<>();
    private ArrayList<CommentInfo> commentArrayList;
    private ArrayList<CommunityInfo> communityArrayList;
    private FirebaseUser user;
    private String writer;
    private String nickname;
    private FirebaseFirestore firebaseFirestore;

    public ReadContentsView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ReadContentsView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        initView();
    }


    private void initView() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);

        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_community, this, true);
    }

    public void setMoreView(int moreView) {
        this.moreView = moreView;
    }

    public void setTest(CommunityInfo communityInfo) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        final String id = communityInfo.getId();

        CollectionReference collectionReference = firebaseFirestore.collection("posts").document(id).collection("comment");
        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            commentArrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                commentArrayList.add(new CommentInfo(
                                        document.getData().get("comment").toString(),
                                        new Date((document.getDate("commentWriteDay").getTime())),
                                        document.getId(),
                                        writer
                                ));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        TextView tv_comment = findViewById(R.id.tv_comment);
                        int commentSize = commentArrayList.size();
                        tv_comment.setText("댓글 " + commentSize + " 개");
                        Log.e("댓글 수", String.valueOf(commentSize));
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    public void setCommunityInfo(CommunityInfo communityInfo) {
        //Date
        TextView tv_write_day = findViewById(R.id.tv_write_day);
        tv_write_day.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(communityInfo.getWriteDay()));

        long curTime = System.currentTimeMillis();
        long regTime = communityInfo.getWriteDay().getTime();
        long diffTime = (curTime - regTime) / 1000;
        if (diffTime < TIME_MAXIMUM.SEC) {
            tv_write_day.setText("방금 전");
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            tv_write_day.setText(diffTime + "분 전");
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            tv_write_day.setText(diffTime + "시간 전");
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
            tv_write_day.setText(diffTime + "일 전");
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
            tv_write_day.setText(diffTime + "달 전");
        } else {
            tv_write_day.setText(diffTime + "년 전");
        }

        Log.e("로그 : ", "profilePath :" + communityInfo);
        //contents
        LinearLayout layout_contents = findViewById(R.id.layout_contents);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentsList = communityInfo.getContents();
        ArrayList<String> formatList = communityInfo.getFormats();


        for (int i = 0; i < contentsList.size(); i++) {
            if (i == moreView) {
                TextView textView = new TextView(context);
                textView.setLayoutParams(layoutParams);
                textView.setText("더보기...");
                layout_contents.addView(textView);
                break;
            }

            String contents = contentsList.get(i);
            String formats = formatList.get(i);

            if (formats.equals("image")) {
                ImageView imageView = (ImageView) layoutInflater.inflate(R.layout.view_contents_image, this, false);
                layout_contents.addView(imageView);
                Glide.with(this).load(contents).override(1000).thumbnail(0.1f).into(imageView); //thumbnail-사진로딩 전 미리 보여주기(10%)
            } else if (formats.equals("video")) {
                StyledPlayerView styledPlayerView = (StyledPlayerView) layoutInflater.inflate(R.layout.view_contents_video_player, this, false);

                ExoPlayer player = new ExoPlayer.Builder(context).build();

                playerArrayList.add(player);

                styledPlayerView.setPlayer(player);

                // Build the media item.
                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(contents));
                // Set the media item to be played.
                player.setMediaItem(mediaItem);
                // Prepare the player.
                player.prepare();
                // Start the playback.
                player.play();

                player.setPlayWhenReady(false);

                layout_contents.addView(styledPlayerView);
            } else {
                TextView textView = (TextView) layoutInflater.inflate(R.layout.view_contents_text, this, false);
                textView.setText(contents);
                layout_contents.addView(textView);
                Log.e("text", contents);
            }
        }
    }

    public ArrayList<ExoPlayer> getPlayerArrayList() {
        return playerArrayList;
    }

    private static class TIME_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

}