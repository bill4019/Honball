package com.example.honball.activity;

import static android.content.ContentValues.TAG;
import static com.example.honball.Util.showToast;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.honball.R;
import com.example.honball.adapter.CommunityAdapter;
import com.example.honball.fragment.HomeFragment;
import com.example.honball.fragment.MapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainCommunityActivity extends CommonActivity {
    private CommunityAdapter communityAdapter;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_community);
        setToolbarTitle("Honball");
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (data != null) {
                    init();
                    break;
                }
        }
    }

    private void init() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, homeFragment)
                .commit();

        if (firebaseUser == null) {
            myStartActivity(LoginActivity.class);
        } else {
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.home:
                            HomeFragment homeFragment = new HomeFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, homeFragment)
                                    .commit();
                            return true;
                        case R.id.map:
                            MapFragment mapFragment = new MapFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, mapFragment)
                                    .commit();
                            return true;
                        case R.id.info:
                            myStartActivity(MypageActivity.class);
                            return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
        finish();
    }
}