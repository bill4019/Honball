package com.example.honball.adapter;

import static android.content.ContentValues.TAG;
import static com.example.honball.Util.showToast;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.honball.CommentInfo;
import com.example.honball.CommunityInfo;
import com.example.honball.FirebaseHelper;
import com.example.honball.MemberInfo;
import com.example.honball.R;
import com.example.honball.activity.CommunityActivity;
import com.example.honball.activity.LoginActivity;
import com.example.honball.activity.MainCommunityActivity;
import com.example.honball.activity.MemberInfoActivity;
import com.example.honball.activity.MypageActivity;
import com.example.honball.activity.PostActivity;
import com.example.honball.fragment.HomeFragment;
import com.example.honball.listener.OnPostListener;
import com.example.honball.view.ReadContentsView;
import com.example.honball.view.ReadContentsView2;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.MainViewHolder> {
    public ArrayList<CommunityInfo> mDataset;
    private Activity activity;
    private final int MORE_Contents = 2;     //더보기 (두줄 제한)
    private FirebaseHelper firebaseHelper;
    private ArrayList<ArrayList<ExoPlayer>> playerArrayListArrayList = new ArrayList<>();
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private CommentInfo commentInfo;

    static class MainViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        MainViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public CommunityAdapter(Activity activity, ArrayList<CommunityInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;

        firebaseHelper = new FirebaseHelper(activity);
    }

    public void setOnPostListener(OnPostListener onPostListener){
        firebaseHelper.setOnPostListener(onPostListener);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public CommunityAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community, parent, false);
        final MainViewHolder mainViewHolder = new MainViewHolder(cardView);
        Log.e("로그 : ", "로그 : " + viewType);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PostActivity.class);
                intent.putExtra("communityInfo", mDataset.get(mainViewHolder.getAbsoluteAdapterPosition()));
                activity.startActivity(intent);
            }
        });

        cardView.findViewById(R.id.cv_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view, mainViewHolder.getBindingAdapterPosition());
            }
        });

        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MainViewHolder holder, int position) {
        // Title
        CardView cardView = holder.cardView;
        TextView tv_title = cardView.findViewById(R.id.tv_title);

        CommunityInfo communityInfo = mDataset.get(position);
        tv_title.setText(communityInfo.getTitle());

        ReadContentsView readContentsView = cardView.findViewById(R.id.readContentsView);
        ReadContentsView2 readContentsView2 = cardView.findViewById(R.id.readContentsView2);

        readContentsView2.setNickname(communityInfo);
        //contents
        LinearLayout layout_contents = cardView.findViewById(R.id.layout_contents);

        if (layout_contents.getTag() == null || !layout_contents.getTag().equals(communityInfo)) {
            layout_contents.setTag(communityInfo);
            layout_contents.removeAllViews();

            readContentsView.setMoreView(MORE_Contents);
            readContentsView.setTest(communityInfo);
            readContentsView.setCommunityInfo(communityInfo);

            ArrayList<ExoPlayer> playerArrayList = readContentsView.getPlayerArrayList();
            if(playerArrayList != null){
                playerArrayListArrayList.add(playerArrayList);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void showPopup(View v, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                CommunityInfo communityInfo = mDataset.get(position);
                String writer = communityInfo.getWriter();
                switch (menuItem.getItemId()) {
                    case R.id.menu_modify:
                        if(writer.equals(uid) == false){
                            Log.e("작성자", communityInfo.getWriter());
                            Log.e("작성자", uid);
                            Log.e("수정 불가", "수정 불가");
                            return false;
                        }else{
                            Log.e("작성자", communityInfo.getWriter());
                            myStartActivity(CommunityActivity.class, mDataset.get(position));
                            return true;
                        }
                    case R.id.menu_delete:
                        if(writer.equals(uid) == false){
                            Log.e("작성자", communityInfo.getWriter());
                            Log.e("작성자", uid);
                            Log.e("삭제 불가", "삭제 불가");
                            return false;
                        }else{
                            firebaseHelper.storageDelete(mDataset.get(position));
                            return true;
                        }
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post, popup.getMenu());
        popup.show();
    }

    private void myStartActivity(Class c, CommunityInfo communityInfo) {
        Intent intent = new Intent(activity, c);
        intent.putExtra("communityInfo", communityInfo);
        activity.startActivity(intent);
    }

    public void playerStop(){
        for(int i = 0; i < playerArrayListArrayList.size(); i++){
            ArrayList<ExoPlayer> playerArrayList = playerArrayListArrayList.get(i);
            for(int j = 0; j < playerArrayList.size(); j++){
                ExoPlayer player = playerArrayList.get(j);
                if(player.getPlayWhenReady()){
                    player.setPlayWhenReady(false);
                }
            }
        }
    }
}