package com.example.honball.activity;

import static com.example.honball.Util.showToast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.honball.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends CommonActivity {

    // Firebase
    private FirebaseAuth mAuth;
    private static final String TAG = "RegisterActivity";

    private EditText et_id, et_pass, et_passCheck;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
                    register();
                    break;
            }
        }
    };

    private void register() {

        String email = ((EditText) findViewById(R.id.et_id)).getText().toString();
        String password = ((EditText) findViewById(R.id.et_pass)).getText().toString();
        String passCheck = ((EditText) findViewById(R.id.et_passcheck)).getText().toString();

        if (email.length() > 0 && password.length() > 0 && passCheck.length() > 0) {
            if (password.equals(passCheck)) {
                final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
                loaderLayout.setVisibility(View.VISIBLE);
                // [START create_user_with_email]
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                loaderLayout.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    // 성공 UI logic
                                    Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, MemberInfoActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    if (task.getException() != null) {
                                        showToast(RegisterActivity.this, task.getException().toString());
                                    }
                                }
                            }
                        });
                // [END create_user_with_email]
            } else {
                showToast(RegisterActivity.this, "비밀번호가 일치하지 않습니다");
            }
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast(RegisterActivity.this, "아이디는 이메일 형식입니다");
        } else {
            showToast(RegisterActivity.this, "이메일 또는 비밀번호를 입력해주세요");
        }

    }
}