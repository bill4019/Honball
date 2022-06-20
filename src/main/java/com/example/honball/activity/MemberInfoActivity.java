package com.example.honball.activity;

import static com.example.honball.Util.INTENT_PATH;
import static com.example.honball.Util.showToast;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.honball.MemberInfo;
import com.example.honball.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MemberInfoActivity extends CommonActivity {
    private static final String TAG = "MemberInfoActivity";
    private ImageView profileImageView;
    private FirebaseUser user;
    private String profilePath;
    // Firebase
    private FirebaseAuth mAuth;
    private EditText et_id, et_pass, et_passCheck;
    private AlertDialog dialog;
    private RelativeLayout loaderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);
        setToolbarTitle("Honball");

        loaderLayout = findViewById(R.id.loaderLayout);
        mAuth = FirebaseAuth.getInstance();
        profileImageView = findViewById(R.id.profileimage);
        profileImageView.setOnClickListener(onClickListener);

        findViewById(R.id.btn_checkInfo).setOnClickListener(onClickListener);
        findViewById(R.id.btn_edit_video).setOnClickListener(onClickListener);
        findViewById(R.id.btn_edit_image).setOnClickListener(onClickListener);

        memberInfo();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    profilePath = data.getStringExtra(INTENT_PATH);
                    Log.e("로그 : ", "profilePath :" + profilePath);
                    // 사진 resizing
                    Glide.with(this).load(profilePath).centerCrop().override(500).into(profileImageView);
                    CardView cardView = findViewById(R.id.cv_cardview);
                    cardView.setVisibility(View.GONE);
                }
                break;
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_checkInfo:
                    Log.e("클릭", " 클릭");
                    storageUploader();
                    break;
                case R.id.profileimage:
                    CardView cardView = findViewById(R.id.cv_cardview);
                    if (cardView.getVisibility() == View.VISIBLE) {
                        cardView.setVisibility(View.GONE);
                    } else {
                        cardView.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.btn_edit_image:
                    myStartActivity(CameraActivity.class);
                    break;
                case R.id.btn_edit_video:
                    myStartActivity(GalleryActivity.class);
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myStartActivity(GalleryActivity.class);
                    showToast(MemberInfoActivity.this,"권한이 설정되었습니다.");
                } else {
                    showToast(MemberInfoActivity.this,"권한이 거부되었습니다.");
                }
                break;
            }
        }
    }

    private void storageUploader() {
        final String name = ((EditText) findViewById(R.id.et_name)).getText().toString();
        final String nickname = ((EditText) findViewById(R.id.et_nickname)).getText().toString();
        final String birth = ((EditText) findViewById(R.id.et_birth)).getText().toString();
        final String phone = ((EditText) findViewById(R.id.et_phone)).getText().toString();
        EditText getName = findViewById(R.id.et_name);

        if (name.length() > 0 && nickname.length() > 0 && birth.length() > 5 && phone.length() > 9) {
            loaderLayout.setVisibility(View.VISIBLE);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();
            final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");

            if (profilePath == null) {
                MemberInfo memberInfo = new MemberInfo(name, nickname, phone, birth);
                storeUploader(memberInfo);
            } else {
                try {
                    InputStream stream = new FileInputStream(new File(profilePath));
                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                Log.e("성공", "성공 : " + downloadUri);
                                MemberInfo memberInfo = new MemberInfo(name, nickname, phone, birth, downloadUri.toString());
                                storeUploader(memberInfo);
                            } else {
                                showToast(MemberInfoActivity.this, "회원정보를 보내는데 실패하였습니다.");
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    Log.e("로그", "에러" + e.toString());
                }
            }
        } else {
            showToast(MemberInfoActivity.this,"모두 입력해주세요.");
        }
    }

    private void storeUploader(MemberInfo memberInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(memberInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(MemberInfoActivity.this,"회원정보 등록을 성공하였습니다.");
                        loaderLayout.setVisibility(View.GONE);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(MemberInfoActivity.this, "회원정보 등록을 실패하였습니다.");
                        loaderLayout.setVisibility(View.GONE);
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void memberInfo(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        EditText name = findViewById(R.id.et_name);
                        EditText nickname = findViewById(R.id.et_nickname);
                        EditText phone = findViewById(R.id.et_phone);
                        EditText birth = findViewById(R.id.et_birth);
                        ImageView profileimage = (ImageView) findViewById(R.id.profileimage);
                        Log.d(TAG, "Cached document data: " + document.getData().get("name").toString());
                        name.setText(document.getData().get("name").toString());
                        nickname.setText(document.getData().get("nickname").toString());
                        phone.setText(document.getData().get("phone").toString());
                        birth.setText(document.getData().get("birth").toString());
                        if(document.getData().get("photoUrl").toString() == null){
                            profileimage.setImageResource(R.drawable.ic_baseline_camera_alt_24);
                        }else{
                            Glide.with(MemberInfoActivity.this).load(document.getData().get("photoUrl").toString()).centerCrop().into(profileimage);
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
}