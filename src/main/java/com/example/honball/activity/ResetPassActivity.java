package com.example.honball.activity;

import static com.example.honball.Util.showToast;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.honball.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassActivity extends CommonActivity {

    // Firebase
    private FirebaseAuth mAuth;

    private EditText et_id, et_pass, et_passCheck;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        setToolbarTitle("Honball");

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btn_checkInfo).setOnClickListener(onClickListener);


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_checkInfo:
                    Log.e("클릭", " 클릭");
                    reset();
                    break;
            }
        }
    };

    private void reset() {
        String email = ((EditText) findViewById(R.id.et_id)).getText().toString();
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("kr");

        if (email.length() > 0) {
            final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
            loaderLayout.setVisibility(View.VISIBLE);
            Log.e("email", email);
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loaderLayout.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                showToast(ResetPassActivity.this, "이메일을 보냈습니다.");
                            }
                        }
                    });

        } else {
            showToast(ResetPassActivity.this, "이메일을 입력해주세요");
        }
    }
}