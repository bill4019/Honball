package com.example.honball.activity;

import static com.example.honball.Util.GALLERY_IMAGE;
import static com.example.honball.Util.GALLERY_VIDEO;
import static com.example.honball.Util.INTENT_MEDIA;
import static com.example.honball.Util.showToast;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.honball.R;
import com.example.honball.adapter.GalleryAdapter;

import java.util.ArrayList;

public class GalleryActivity extends CommonActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        setToolbarTitle("Honball");

        //갤러리 권한 체크
        //권한이 없을 때
        if (ContextCompat.checkSelfPermission(GalleryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GalleryActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            if (ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                showToast(GalleryActivity.this, getResources().getString(R.string.please_grant_permission));
            }
            //권한이 있을 때
        } else {
            recyclerInit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recyclerInit();
                } else {
                    finish();
                    showToast(GalleryActivity.this,getResources().getString(R.string.please_grant_permission));
                }
                break;
            }
        }
    }

    private void recyclerInit(){
        final int numberOfColumns = 3;

        RecyclerView recyclerView = findViewById(R.id.RV_gallery);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        RecyclerView.Adapter mAdapter = new GalleryAdapter(this, getImagesPath(this));
        recyclerView.setAdapter(mAdapter);
    }

    public ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data;
        String PathOfImage = null;
        String[] projection;

        Intent intent = getIntent();
        final int media = intent.getIntExtra(INTENT_MEDIA, GALLERY_IMAGE);
        if(media == GALLERY_VIDEO){ //비디오 버튼 클릭 시 비디오 목록으로
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            projection = new String[] { MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME };
        }else{  //사진 버튼 클릭 시 사진 목록으로로
           uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[] { MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
        }

        cursor = activity.getContentResolver().query(uri, projection, null,null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(0, PathOfImage);
        }
        return listOfAllImages;
    }
}