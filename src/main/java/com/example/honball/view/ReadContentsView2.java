package com.example.honball.view;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.honball.CommunityInfo;
import com.example.honball.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ReadContentsView2 extends LinearLayout {
    private Context context2;
    private LayoutInflater layoutInflater2;
    private int moreView = -1;
    private ArrayList<ExoPlayer> playerArrayList = new ArrayList<>();
    private String nickname;

    public ReadContentsView2(Context context2) {
        super(context2);
        this.context2 = context2;
        initView();
    }

    public ReadContentsView2(Context context2, @Nullable AttributeSet attributeSet2) {
        super(context2, attributeSet2);
        this.context2 = context2;
        initView();
    }

    private void initView() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);

        layoutInflater2 = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater2.inflate(R.layout.view_community_nickname, this, true);
    }


    public void setNickname(CommunityInfo communityInfo) {
        final String writer = communityInfo.getWriter();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").document(writer)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            TextView tv_nickname = findViewById(R.id.tv_nickname);
                            ImageView community_photo = (ImageView) findViewById(R.id.community_photo);
                            nickname = document.getData().get("nickname").toString();
                            tv_nickname.setText(nickname);

                            Log.e("nickname", nickname);

                            Glide.with(ReadContentsView2.this).load(document.getData().get("photoUrl").toString()).centerCrop().apply(new RequestOptions().circleCrop()).into(community_photo);

                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

}