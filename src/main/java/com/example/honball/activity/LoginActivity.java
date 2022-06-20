package com.example.honball.activity;

import static com.example.honball.Util.showToast;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.honball.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends CommonActivity {

    // Firebase
    private FirebaseAuth mAuth;
    private static final String TAG = "RegisterActivity";


    private EditText et_id, et_pass, et_passCheck;
    private AlertDialog dialog;

    private Button btn_join;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setToolbarTitle("Honball");

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btn_join).setOnClickListener(onClickListener);
        findViewById(R.id.btn_login).setOnClickListener(onClickListener);
        findViewById(R.id.btn_resetPass).setOnClickListener(onClickListener);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startLoginActivity();
        }
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_login:
                    Log.e("클릭", " 클릭");
                    login();
                    break;
                case R.id.btn_join:
                    Log.e("클릭", " 클릭");
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_resetPass:
                    Log.e("클릭", " 클릭");
                    resetPassActivity();
            }
        }
    };

    private void login() {
        String email = ((EditText) findViewById(R.id.login_id)).getText().toString();
        String password = ((EditText) findViewById(R.id.login_password)).getText().toString();

        if (email.length() > 0 && password.length() > 0) {
            final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
            loaderLayout.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loaderLayout.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();

                                showToast(LoginActivity.this, "로그인 성공");
                                myStartActivity(MainCommunityActivity.class);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                showToast(LoginActivity.this, "Authentication failed.");
                            }
                        }
                    });

        } else {
            showToast(LoginActivity.this,"이메일 또는 비밀번호를 입력해주세요");
        }

    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, MypageActivity.class);
        startActivity(intent);
        finish();
    }

    private void resetPassActivity() {
        Intent intent = new Intent(this, ResetPassActivity.class);
        startActivity(intent);
    }
}