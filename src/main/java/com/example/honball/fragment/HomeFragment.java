package com.example.honball.fragment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.honball.CommentInfo;
import com.example.honball.CommunityInfo;
import com.example.honball.R;
import com.example.honball.activity.CommunityActivity;
import com.example.honball.activity.MemberInfoActivity;
import com.example.honball.adapter.CommunityAdapter;
import com.example.honball.listener.OnPostListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {

    private static final String TAG = "MainCommunityActivity";
    private FirebaseFirestore firebaseFirestore;
    private CommunityAdapter communityAdapter;
    private ArrayList<CommunityInfo> communityList;
    private boolean updating;
    private boolean topScrolled;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            //String nickname = memberInfo.getNickname();
                            //showId.setText(nickname);
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                            myStartActivity(MemberInfoActivity.class);
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        communityList = new ArrayList<>();
        communityAdapter = new CommunityAdapter(getActivity(), communityList);
        communityAdapter.setOnPostListener(onPostListener);

        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        view.findViewById(R.id.btn_writeform).setOnClickListener(onClickListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(communityAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();

                if (newState == 1 && firstVisibleItemPosition == 0) {
                    topScrolled = true;
                }
                if (newState == 0 && topScrolled) {
                    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);

                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            communityList.clear();
                            postUpdate(true);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
                Log.e("로그 : ", "lllll" + newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();

                if (totalItemCount - 3 <= lastVisibleItemPosition && !updating) {
                    postUpdate(false);
                }

                if (0 < firstVisibleItemPosition) {
                    topScrolled = false;
                }
            }
        });
        postUpdate(false);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
        //communityAdapter.playerStop();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_writeform:
                    myStartActivity(CommunityActivity.class);
                    break;
            }
        }
    };

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(CommunityInfo communityInfo) {
            communityList.remove(communityInfo);
            communityAdapter.notifyDataSetChanged();
            Log.e("로그 : ", "삭제 성공");
        }

        @Override
        public void onModify() {
            Log.e("로그 : ", "수정 성공");
        }
    };

    //액션 후 화면 새로고침 함수
    private void postUpdate(final boolean clear) {
        updating = true;
        Date date = communityList.size() == 0 || clear ? new Date() : communityList.get(communityList.size() - 1).getWriteDay();
        CollectionReference collectionReference = firebaseFirestore.collection("posts");
        collectionReference.orderBy("writeDay", Query.Direction.DESCENDING).whereLessThan("writeDay", date).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (clear) {
                                communityList.clear();
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                communityList.add(new CommunityInfo(
                                        document.getData().get("title").toString(),
                                        (ArrayList<String>) document.getData().get("contents"),
                                        (ArrayList<String>) document.getData().get("formats"),
                                        document.getData().get("writer").toString(),
                                        new Date(document.getDate("writeDay").getTime()),
                                        document.getId()
                                ));
                                communityAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updating = false;
                    }
                });
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        startActivityForResult(intent, 0);
    }
}