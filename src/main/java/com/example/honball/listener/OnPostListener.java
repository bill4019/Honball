package com.example.honball.listener;

import com.example.honball.CommunityInfo;

public interface OnPostListener {
    void onDelete(CommunityInfo communityInfo);
    void onModify();
}
