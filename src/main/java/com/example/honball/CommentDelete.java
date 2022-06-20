package com.example.honball;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.honball.listener.OnPostListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CommentDelete {
    private Activity activity;
    private OnPostListener onPostListener;
    private CommunityInfo communityInfo;

    public CommentDelete(Activity activity){
        this.activity = activity;
    }


    public void setOnPostListener(OnPostListener onPostListener){
        this.onPostListener = onPostListener;
    }


}
