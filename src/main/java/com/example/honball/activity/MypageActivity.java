package com.example.honball.activity;

import static com.example.honball.Util.showToast;

import androidx.annotation.NonNull;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.honball.MemberInfo;
import com.example.honball.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MypageActivity extends CommonActivity {
    private static final String TAG = "MypageActivity";
    private FirebaseAuth mAuth ;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        setToolbarTitle("마이페이지");

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startLoginActivity();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        findViewById(R.id.btn_changepass).setOnClickListener(onClickListener);
        findViewById(R.id.btn_logout).setOnClickListener(onClickListener);
        findViewById(R.id.btn_info).setOnClickListener(onClickListener);
        Button btn_withdrawal = findViewById(R.id.btn_withdrawal);
        btn_withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        if(user == null){
            myStartActivity(RegisterActivity.class);
        }else{
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document != null){
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                                myStartActivity(MemberInfoActivity.class);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_logout:
                    FirebaseAuth.getInstance().signOut();
                    showToast(MypageActivity.this, "로그아웃 되었습니다");
                    startLoginActivity();
                    break;
                case R.id.btn_info:
                    myStartActivity(MemberInfoActivity.class);
                    break;
                case R.id.btn_changepass:
                    myStartActivity(ResetPassActivity.class);
                    break;
            }
        }
    };

    private void deleteMember(){

    }

    private void showDialog() {
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(MypageActivity.this)
                .setTitle("회원탈퇴")
                .setMessage("정말 탈퇴하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User account deleted.");
                                            showToast(MypageActivity.this, "회원탈퇴가 완료되었습니다.");
                                            finish();
                                            myStartActivity(LoginActivity.class);
                                        }else{
                                            Log.d(TAG, "회원탈퇴 실패");
                                            showToast(MypageActivity.this, "회원탈퇴를 실패했습니다.");
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MypageActivity.this, "안 끔", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainCommunityActivity.class);
        startActivity(intent);
        finish();
    }

    private void startLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}