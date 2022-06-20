package com.example.honball.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.honball.CommentInfo;
import com.example.honball.CommunityInfo;
import com.example.honball.MemberInfo;
import com.example.honball.R;
import com.example.honball.activity.PostActivity;
import com.example.honball.view.ReadContentsView;

import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private Activity mActivity;
    private ArrayList<CommentInfo> commentArrayList;
    private PostActivity ma;
    private PostActivity postActivity;
    private int pos;

    private static final String TAG = "PostActivity";

    public CommentAdapter(Context mContext, Activity mActivity, PostActivity mc, ArrayList<CommentInfo> commentInfo) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.commentArrayList = commentInfo;
        this.ma = mc;
    }

    @Override
    public int getCount() {
        return commentArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            int res = 0;
            res = R.layout.item_comment;
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(res, parent, false);
        }
        pos = position;
        if (commentArrayList.size() != 0) {
            TextView ci_nickname_text = (TextView) convertView.findViewById(R.id.ci_nickname_text);
            ci_nickname_text.setText(commentArrayList.get(pos).getNickname());
            TextView ci_content_text = (TextView) convertView.findViewById(R.id.ci_content_text);
            ci_content_text.setText(commentArrayList.get(pos).getComment());
            ImageView comment_photo = (ImageView) convertView.findViewById(R.id.comment_photo);

            if(commentArrayList.get(pos).getPhotoUrl() == null){
                comment_photo.setImageResource(R.drawable.ic_baseline_camera_alt_24);
            }else{
                Glide.with(convertView).load(commentArrayList.get(pos).getPhotoUrl()).centerCrop().apply(new RequestOptions().circleCrop()).into(comment_photo);
            }
        }
        return convertView;
    }

    @Override
    public void onClick(View view) {
        final int tag = Integer.parseInt(view.getTag().toString());
        switch (view.getId()) {
            case R.id.comment_delete:
                Log.e("댓글삭제버튼 : ", "클릭");
                AlertDialog.Builder alertDlg = new AlertDialog.Builder(mActivity);
                alertDlg.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        postActivity.delete_comment(tag);
                        Toast.makeText(mContext, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDlg.setNegativeButton("취소", null);
                alertDlg.setTitle("댓글 삭제");
                alertDlg.setMessage("정말 삭제 하시겠습니까?");
                alertDlg.show();
                break;
        }
    }

    public static void setListViewHeight(ListView listView) {
        CommentAdapter commentAdapter = (CommentAdapter) listView.getAdapter();
        if (commentAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < commentAdapter.getCount(); i++) {
            View listItem = commentAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (commentAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}