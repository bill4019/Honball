package com.example.honball;

import static com.example.honball.Util.isStorageUrl;
import static com.example.honball.Util.showToast;
import static com.example.honball.Util.storageUrlToName;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.honball.listener.OnPostListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FirebaseHelper {
    private Activity activity;
    private OnPostListener onPostListener;
    private int successCount;

    public FirebaseHelper(Activity activity) {
        this.activity = activity;
    }

    public void setOnPostListener(OnPostListener onPostListener){
        this.onPostListener = onPostListener;
    }

    public void storageDelete(final CommunityInfo communityInfo) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        final String id = communityInfo.getId();
        Log.e("로그 : ", "삭제" + id);
        //Firebase Storage 삭제
        ArrayList<String> contentsList = communityInfo.getContents();
        for (int i = 0; i < contentsList.size(); i++) { //이미지가 있을 때 삭제하는 로직
            String contents = contentsList.get(i);
            if (isStorageUrl(contents)) { //url 형식이 맞는지 체크
                successCount++;
                // Create a reference to the file to delete
                StorageReference desertRef = storageRef.child("posts/" + storageUrlToName(contents));
                // Delete the file
                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        successCount--;
                        storeDelete(id, communityInfo);
                        // File deleted successfully
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        showToast(activity, "storage 삭제를 실패했습니다.");
                        // Uh-oh, an error occurred!
                    }
                });
            }
        }
        storeDelete(id, communityInfo);  //이미지가 없을 때 삭제 로직
    }

    private void storeDelete(String id, CommunityInfo communityInfo) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        if (successCount == 0) {
            firebaseFirestore.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(activity, "삭제되었습니다.");
                            onPostListener.onDelete(communityInfo);
                            //postUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast(activity, "삭제를 실패했습니다.");
                        }
                    });
        }
    }
}
