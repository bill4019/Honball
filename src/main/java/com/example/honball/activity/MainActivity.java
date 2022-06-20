package com.example.honball.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.example.honball.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;  //바텀 네비게이션 뷰
    private FragmentManager fm;
    private FragmentTransaction ft;
    private LoginActivity login;
    private CommunityActivity community;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findViewById(R.id.btn_writeform).setOnClickListener(onClickListener);

    }




    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
              /*  case R.id.community:
                    ft.replace(R.id.frameLayout, frag1).commitAllowingStateLoss();
                    break;
                case R.id.map:
                    myStartActivity(MapActivity.class);
                case R.id.btn_mypage:
                    myStartActivity(LoginActivity.class);
               */
            }
        }
    };

    private void startCameraActivity(){
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent);
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}