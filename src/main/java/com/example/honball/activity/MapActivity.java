package com.example.honball.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import com.example.honball.R;
import com.example.honball.fragment.HomeFragment;
import com.example.honball.fragment.MapFragment;

import net.daum.mf.map.api.MapView;

public class MapActivity extends CommonActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setToolbarTitle("Honball");

        init();
    }

    private void init(){
        MapFragment mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mapFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainCommunityActivity.class);
        startActivity(intent);
        finish();
    }
}