package csell.com.vn.csell.mycustoms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.social.adapter.ListLikesFirebaseAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityFirebase;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FirebaseDBUtil;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.models.UserRetro;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListUserLikeCommentsDialog extends BottomSheetDialog {

    private RecyclerView rvUsers;
    private ListLikesFirebaseAdapter likesFirebaseAdapter;
    private ProgressBar progress_loading;
    private Button btnReload;
    private boolean isComments;
    private Context context;
    private ArrayList<UserRetro> lsFriends;
    private ArrayList<String> lsUserIds;
    private FileSave fileSave;
    private LinearLayoutManager mLayoutManager;

    public ListUserLikeCommentsDialog(@NonNull Context context, ArrayList<String> lsUsers, boolean isComments) {
        super(context, R.style.full_screen_dialog_no_status_bar);

        setContentView(R.layout.dialog_list_friend_like);
        this.context = context;
        this.isComments = isComments;
        lsFriends = new ArrayList<>();
        lsUserIds = new ArrayList<>();
        fileSave = new FileSave(context, Constants.GET);
        this.setCanceledOnTouchOutside(true);
        try {
            rvUsers = findViewById(R.id.rv_friend_like);
            progress_loading = findViewById(R.id.progress_loading);
            btnReload = findViewById(R.id.btnReload);
            rvUsers.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(context);
            rvUsers.setLayoutManager(mLayoutManager);
            likesFirebaseAdapter = new ListLikesFirebaseAdapter(context, lsFriends);
            rvUsers.setAdapter(likesFirebaseAdapter);
            //custom position dialog
            Window dialogWindow = getWindow();
            WindowManager.LayoutParams lp;
            if (dialogWindow != null) {
                lp = dialogWindow.getAttributes();
                dialogWindow.setGravity(Gravity.BOTTOM);
                dialogWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                lp.windowAnimations = R.style.DialogCreateAnimation;
                lp.width = WindowManager.LayoutParams.MATCH_PARENT; // Width
                lp.height = WindowManager.LayoutParams.MATCH_PARENT; // Height
                dialogWindow.setAttributes(lp);
            }

            progress_loading.setVisibility(View.GONE);
            initEvent();
            if (isComments) {
                getDataFriendComments();
            } else {
                lsUserIds.addAll(lsUsers);
                getDataFriendLikes();
            }

        } catch (Exception e) {
            Crashlytics.logException(e
            );
        }
    }

    private void getDataFriendLikes() {
        if (lsUserIds.size() > 10) {
            List<String> subList = new ArrayList<>();
            subList.addAll(lsUserIds.subList(0, 10));
            getListUser(subList);

            for (int i = 0; i < subList.size(); i++) {
                lsUserIds.remove(i);
            }

        } else {
            getListUser(lsUserIds);
        }
    }

    private void initEvent() {
        btnReload.setOnClickListener(v -> {
            btnReload.setVisibility(View.GONE);
            progress_loading.setVisibility(View.VISIBLE);
            if (isComments) {
                getDataFriendComments();
            } else {
                getDataFriendLikes();
            }
        });

        rvUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager.findLastCompletelyVisibleItemPosition() == lsFriends.size() - 1 && lsFriends.size() > 10) {
                    getListUser(lsUserIds);
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void getDataFriendComments() {
        DatabaseReference mDatabaseRef = FirebaseDBUtil.getDatebase().getReference();
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    mDatabaseRef.child(EntityFirebase.Field_User_Comments).child(fileSave.getProductIdCurrentSelect())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    try {
                                        if (dataSnapshot.getChildrenCount() > 0) {
                                            Object o = dataSnapshot.getValue();

                                            HashMap<String, Object> mapComments = (HashMap<String, Object>) o;
                                            try {
                                                assert mapComments != null;
                                                lsUserIds.addAll(mapComments.keySet());
                                                if (lsUserIds.size() > 10) {
                                                    List<String> subList = new ArrayList<>();
                                                    subList.addAll(lsUserIds.subList(0, 10));
                                                    getListUser(subList);
                                                    for (int i = 0; i < subList.size(); i++) {
                                                        lsUserIds.remove(i);
                                                    }
                                                } else {
                                                    getListUser(lsUserIds);
                                                }

                                            } catch (Exception e) {
                                                if (BuildConfig.DEBUG)
                                                    Log.d("GET_COMMENTS", e.getMessage());
                                            }
                                        } else {
                                            progress_loading.setVisibility(View.GONE);
                                        }
                                    } catch (Exception ignored) {

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Crashlytics.logException(databaseError.toException());
                                    progress_loading.setVisibility(View.GONE);
                                }
                            });
                    return null;
                }
            }.execute();

        } catch (Exception e) {
            Crashlytics.logException(e);
        }

    }

    private void getListUser(List<String> lsUid) {
        try {
            GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileSave.getToken());
            if (getAPI != null) {
                progress_loading.setVisibility(View.VISIBLE);
                String uids = TextUtils.join(",", lsUid);
                Call<JSONResponse<List<UserRetro>>> jsonResponseCall = getAPI.getUsersSocial(fileSave.getProductIdCurrentSelect(), uids);
                jsonResponseCall.enqueue(new Callback<JSONResponse<List<UserRetro>>>() {
                    @Override
                    public void onResponse(Call<JSONResponse<List<UserRetro>>> call, Response<JSONResponse<List<UserRetro>>> response) {
                        btnReload.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            JSONResponse<List<UserRetro>> result = response.body();
                            if (result != null && result.getSuccess() != null) {
                                if (result.getSuccess()) {
                                    if (result.getData() != null) {
                                        if (result.getData().size() > 0) {
                                            lsFriends.addAll(result.getData());
                                            likesFirebaseAdapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        btnReload.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                        } else {
                            btnReload.setVisibility(View.VISIBLE);
                        }
                        progress_loading.setVisibility(View.GONE);
                        jsonResponseCall.cancel();
                    }

                    @Override
                    public void onFailure(Call<JSONResponse<List<UserRetro>>> call, Throwable t) {
                        jsonResponseCall.cancel();
                        btnReload.setVisibility(View.VISIBLE);
                        progress_loading.setVisibility(View.GONE);
                    }
                });
            }
        } catch (Exception e) {
            progress_loading.setVisibility(View.GONE);
            Crashlytics.logException(e);
        }

    }

}
