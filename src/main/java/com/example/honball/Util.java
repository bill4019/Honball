package com.example.honball;

import android.app.Activity;
import android.util.Patterns;
import android.widget.Toast;

import java.net.URLConnection;

public class Util {

    public Util(Activity activity){
        /* */
    }

    public static final String INTENT_PATH = "path";
    public static final String INTENT_MEDIA = "media";

    public static final int GALLERY_IMAGE = 0;
    public static final int GALLERY_VIDEO = 1;

    public static void showToast(Activity activity, String msg){
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean isStorageUrl(String url){
        return Patterns.WEB_URL.matcher(url).matches() && url.contains("https://firebasestorage.googleapis.com/v0/b/honball-b907f.appspot.com/o/posts");
    }

    public static String storageUrlToName(String url){
        /*
        String[] list = url.split("\\?");
        String[] list2 = list.split("posts");
        String name = list2[list2.length-1].replace("%2F", "/");
        */
        return url.split("\\?")[0].split("posts")[url.split("\\?")[0].split("posts").length-1].replace("%2F", "/");
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }
}
