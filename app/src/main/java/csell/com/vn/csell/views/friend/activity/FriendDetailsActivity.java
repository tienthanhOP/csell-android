package csell.com.vn.csell.views.friend.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.FriendsController;
import csell.com.vn.csell.controllers.NotiController;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.csell.adapter.ViewPagerContactAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.EntityFirebase;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FirebaseDBUtil;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.views.friend.fragment.FriendDetailsInformationFragment;
import csell.com.vn.csell.views.friend.fragment.FriendDetailsNewfeedFragment;
import csell.com.vn.csell.views.friend.fragment.FriendDetailsProductFragment;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.models.LastMessage;
import csell.com.vn.csell.models.FriendInfo;
import csell.com.vn.csell.models.ProductCountResponse;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.sqlites.SQLFriends;
import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by chuc.nq on 2/8/2018.
 */

public class FriendDetailsActivity extends AppCompatActivity implements FriendsController.OnGetFriendDetailListener, FriendsController.OnSendRequestListener, FriendsController.OnAcceptFriendListener, FriendsController.OnUnFriendListener, NotiController.OnSendNotiListener {

    private FancyButton btnBack;
    private ViewPager viewPager;
    private FileSave filePut;
    private FancyButton btnDenyFriend;
    private LinearLayout btnAddFriend;
    private ImageView imgAddFriend;
    private TextView tvAddFriend;
    private FancyButton btnAcceptFriend;
    private LinearLayout btnFollowFriend;
    private LinearLayout btnCallFriend;
    private LinearLayout btnSmsFriend;
    private FancyButton btnSendRequest;
    private LinearLayout lineaLayoutAcceptDeny;
    private TextView tvFriendFullname;
    private CircleImageView imgAvatar;

    private ViewPagerContactAdapter contactFriendDetailAdapter;
    public static boolean isAcceptFriend = false;
    public boolean isSelectUserComment = false;
    private FileSave fileSave;
    private int position;
    private String roomId = null;

    public static UserRetro friend = null;
    private SQLFriends sqlFriends;

    private FileSave fileGet;
    private ProgressBar progressBar;
    private CoordinatorLayout layout;
    private DatabaseReference mDatabaseRef;
    private ImageView imgCover;
    private TextView tvFollow;
    private ImageView imgFollow;
    public static int Level = 1;
    public static int MaxLevel = 0;
    public boolean notFriend;
    public FriendDetailsProductFragment friendDetailsProductFragment;
    private DatabaseReference dbReference;
    private AppBarLayout appBarLayout;
    private View layoutOverlay;
    private Context mContext;
    private RelativeLayout layoutAvatar;
    private BaseActivityTransition baseActivityTransition;
    private FriendsController friendsController;
    private NotiController notiController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);
        mContext = this;
        Fabric.with(this, new Crashlytics());
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        friendsController = new FriendsController(this);
        notiController = new NotiController(this);
        initView();
        setupWindowAnimations();
        getDataIntent();
        initEvent();
        addSelectAllCate();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    @Override
    protected void onDestroy() {
        if (FriendDetailsActivity.friend != null) {
            FriendDetailsActivity.friend = null;
        }
        super.onDestroy();
    }

    @SuppressLint("SetTextI18n")
    private void getDataIntent() {

        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                isAcceptFriend = bundle.getBoolean(Constants.KEY_PASSINGDATA_ACCEPT, false);
                isSelectUserComment = bundle.getBoolean(Constants.KEY_PASSINGDATA_SELECT_REPLY_COMMENT_USER, false);
                position = bundle.getInt(Constants.KEY_POSITON_LIST, -1);
                friend = (UserRetro) bundle.getSerializable(Constants.KEY_PASSINGDATA_FRIEND_ID);
                if (friend != null) {
                    filePut.putFriendId(friend.getUid());
                    tvFriendFullname.setText(friend.getDisplayname() + "");
                }
                CheckStatusFriend();
                GlideApp.with(this)
                        .load(!TextUtils.isEmpty(friend.getAvatar()) ? friend.getAvatar() : R.drawable.ic_logo)
                        .placeholder(R.drawable.ic_logo)
                        .error(R.drawable.ic_logo)
                        .into(imgAvatar);

                SQLFriends sqlFriends = new SQLFriends(this);
                UserRetro userRetro = sqlFriends.checkIsFriendById(friend.getUid());
                if (userRetro != null) {
                    notFriend = TextUtils.isEmpty(userRetro.getUid());
                } else {
                    notFriend = true;
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }

        try {
            DatabaseReference mFirebaseRef = FirebaseDBUtil.getDatebase().getReference();
            mFirebaseRef.child(EntityFirebase.FieldRooms)
                    .child(fileSave.getUserId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                if (dataSnapshot.getChildrenCount() > 0) {
                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                        if (dataSnapshot1.getKey().equals(friend.getUid())) {
                                            friend.RoomChatId = dataSnapshot1.getValue() != null ? (String) dataSnapshot1.getValue() : null;
                                        }
                                    }
                                } else {
                                    friend.RoomChatId = null;
                                }

                                if (friend.RoomChatId != null) {
                                    roomId = friend.RoomChatId;
                                } else {
                                    roomId = fileSave.getUserId() + friend.getUid();
                                }

                                if (roomId != null) {
                                    mFirebaseRef.child(EntityFirebase.TableDirectMessages)
                                            .child(roomId)
                                            .keepSynced(false);
                                }
                            } catch (Exception e) {
                                Crashlytics.logException(e);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            showProgress(false);
        }
    }

    private void initEvent() {
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                //  Collapsed
                layoutOverlay.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.GONE);
                layoutAvatar.setLayoutParams(new RelativeLayout.LayoutParams(Utilities.dpToPx(mContext, 46),
                        Utilities.dpToPx(mContext, 46)));
            } else {
                //Expanded
                layoutOverlay.setVisibility(View.GONE);
                btnBack.setVisibility(View.VISIBLE);
                layoutAvatar.setLayoutParams(new RelativeLayout.LayoutParams(Utilities.dpToPx(mContext, 116),
                        Utilities.dpToPx(mContext, 116)));
            }
        });

        btnBack.setOnClickListener(v -> {
            FriendDetailsActivity.friend = null;
            onBackPressed();
        });

        btnSendRequest.setOnClickListener(v -> addFriendRequest(friend));

        btnAcceptFriend.setOnClickListener(v -> addFriendRequest(friend));

        btnDenyFriend.setOnClickListener(v -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(FriendDetailsActivity.this);
            final String message = getString(R.string.alert_message_remove_request);

            builder.setMessage(message).setTitle(getString(R.string.alert_title_remove_request))
                    .setNegativeButton(getString(R.string.revoke),
                            (dialog, which) -> dialog.dismiss())
                    .setPositiveButton(getString(R.string.agree),
                            (dialog, which) -> denyFriend(friend, false, true));
            builder.create().show();
        });

        btnFollowFriend.setOnClickListener(v -> {

            try {
                if (friend.getFriendInfo() == null) {
                    followFriend(friend);
                } else {
                    if (!friend.getFriendInfo().getFollowByMe()) {
                        followFriend(friend);
                    } else {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(FriendDetailsActivity.this);
                        final String message = getString(R.string.alert_message_unfollow);

                        builder.setMessage(message).setTitle(getString(R.string.alert_title_unfollow))
                                .setNegativeButton(getString(R.string.revoke),
                                        (dialog, which) -> dialog.dismiss())
                                .setPositiveButton(getString(R.string.agree),
                                        (dialog, which) -> unfollowFriend(friend));
                        builder.create().show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }

        });

        btnCallFriend.setOnClickListener(v -> {
            if (friend.getPhone() != null) {
                if (!TextUtils.isEmpty(friend.getPhone())) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", friend.getPhone(), null));
                    baseActivityTransition.transitionTo(intent, 0);
                } else {
                    Snackbar.make(v, getString(R.string.text_error_input_friend_no_phone), Snackbar.LENGTH_LONG).setAction(getString(R.string.action), null).show();
                }
            } else {
                Snackbar.make(v, getString(R.string.text_error_input_friend_no_phone), Snackbar.LENGTH_LONG).setAction(getString(R.string.action), null).show();
            }
        });

        btnSmsFriend.setOnClickListener(v -> {
            roomId = "";
            if (friend.getUid() != null) {
                String fUid = friend.getUid();
                dbReference.child(EntityFirebase.FieldRooms)
                        .child(fileGet.getUserId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() > 0) {
                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                        if (dataSnapshot1.getKey().equals(fUid)) {
                                            if (dataSnapshot1.getValue() != null)
                                                roomId = dataSnapshot1.getValue() != null ? (String) dataSnapshot1.getValue() : null;
                                        }
                                    }
                                } else {
                                    roomId = null;
                                }

                                if (TextUtils.isEmpty(roomId)) {
                                    roomId = fileGet.getUserId() + fUid;
                                    dbReference.child(EntityFirebase.FieldRooms).child(fileGet.getUserId()).child(fUid).setValue(roomId);
                                    dbReference.child(EntityFirebase.FieldRooms).child(fUid).child(fileGet.getUserId()).setValue(roomId);
                                } else {
                                    dbReference.child(EntityFirebase.TableOnlineChat).child(fileGet.getUserId()).setValue(roomId);
                                    dbReference.child(EntityFirebase.TableOnlineChat).keepSynced(false);
                                    dbReference.child(EntityFirebase.TableDirectMessages).child(fUid).keepSynced(false);
                                }

                                if (!TextUtils.isEmpty(roomId)) {
                                    Intent chat = new Intent(FriendDetailsActivity.this, ChatActivity.class);
                                    chat.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, friend);
                                    chat.putExtra(Constants.KEY_PASSINGDATA_ROOM_ID, roomId);
                                    chat.putExtra(Constants.KEY_IS_PENDING, notFriend);
                                    baseActivityTransition.transitionTo(chat, 0);
                                } else
                                    Toast.makeText(FriendDetailsActivity.this, R.string.cant_not_find_friend, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            }
        });

        btnAddFriend.setOnClickListener(v -> {

            try {
                if (friend.getFriendInfo().getIsAccepted()) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(FriendDetailsActivity.this);
                    final String message = getString(R.string.alert_message_unfriend);

                    builder.setMessage(message).setTitle(getString(R.string.alert_title_unfriend))
                            .setNegativeButton(getString(R.string.revoke),
                                    (dialog, which) -> dialog.dismiss())
                            .setPositiveButton(getString(R.string.agree),
                                    (dialog, which) -> denyFriend(friend, true, false));
                    builder.create().show();
                } else {
                    if (friend.getFriendInfo().getRequestedBy().equals(fileGet.getUserId())) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(FriendDetailsActivity.this);
                        final String message = getString(R.string.alert_message_remove_request);

                        builder.setMessage(message).setTitle(getString(R.string.alert_title_remove_request))
                                .setNegativeButton(getString(R.string.revoke),
                                        (dialog, which) -> dialog.dismiss())
                                .setPositiveButton(getString(R.string.agree),
                                        (dialog, which) -> denyFriend(friend, false, true));

                        builder.create().show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }

        });

    }

    private void unfollowFriend(UserRetro friend) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(EntityAPI.FIELD_FRIEND_ID, friend.getUid());
        PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        Call<JSONResponse<Object>> follow = postAPI.unfollowFriend(map);
        follow.enqueue(new Callback<JSONResponse<Object>>() {
            @Override
            public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getSuccess() != null) {
                            if (response.body().getSuccess()) {
                                CheckStatusFriend();
                            } else {
                                Toast.makeText(FriendDetailsActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                        String msg;
                        if (response.body() != null) {
                            msg = response.body().getError();
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(FriendDetailsActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = (String) jsonObject.get(Constants.ERROR);
                                showProgress(false);
                                if (!TextUtils.isEmpty(msg)) {
                                    Toast.makeText(FriendDetailsActivity.this, msg, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                    Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                }
            }

            @Override
            public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
            }
        });
    }

    private void followFriend(UserRetro friend) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(EntityAPI.FIELD_FRIEND_ID, friend.getUid());
        PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        Call<JSONResponse<Object>> follow = postAPI.followFriend(map);
        follow.enqueue(new Callback<JSONResponse<Object>>() {
            @Override
            public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getSuccess() != null) {
                            if (response.body().getSuccess()) {
                                CheckStatusFriend();
                            } else {
                                Toast.makeText(FriendDetailsActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        String msg;
                        if (response.body() != null) {
                            msg = response.body().getError();
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(FriendDetailsActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = (String) jsonObject.get(Constants.ERROR);
                                showProgress(false);
                                if (!TextUtils.isEmpty(msg)) {
                                    Toast.makeText(FriendDetailsActivity.this, msg, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                    Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                }
            }

            @Override
            public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
            }
        });
    }

    private void initView() {
        baseActivityTransition = new BaseActivityTransition(this);

        mDatabaseRef = FirebaseDBUtil.getDatebase().getReference();
        imgAvatar = findViewById(R.id.img_avatar_contact_friend_detail);
        layoutAvatar = findViewById(R.id.layout_avatar);
        appBarLayout = findViewById(R.id.app_bar);
        btnBack = findViewById(R.id.btn_back_navigation);
        layoutOverlay = findViewById(R.id.layout_overlay);
        btnFollowFriend = findViewById(R.id.btn_follow_friend);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        imgAddFriend = findViewById(R.id.img_add_friend);
        tvAddFriend = findViewById(R.id.tv_add_friend);
        btnDenyFriend = findViewById(R.id.btnDenyFriend);
        btnAcceptFriend = findViewById(R.id.btnAcceptFriend);
        btnCallFriend = findViewById(R.id.btn_call_friend);
        btnSmsFriend = findViewById(R.id.btn_sms_friend);
        lineaLayoutAcceptDeny = findViewById(R.id.linear_layout_accept_deny);
        viewPager = findViewById(R.id.viewpage_contact_friend_detail);
        tvFriendFullname = findViewById(R.id.tv_friend_fullname);

        dbReference = FirebaseDBUtil.getDatebase().getReference();
        filePut = new FileSave(this, Constants.PUT);
        fileSave = new FileSave(FriendDetailsActivity.this, Constants.GET);

        friendDetailsProductFragment = new FriendDetailsProductFragment();
        contactFriendDetailAdapter = new ViewPagerContactAdapter(getSupportFragmentManager());
        contactFriendDetailAdapter.addFrag(new FriendDetailsNewfeedFragment(), getString(R.string.newfeed));
        contactFriendDetailAdapter.addFrag(friendDetailsProductFragment, getString(R.string.product));
        contactFriendDetailAdapter.addFrag(new FriendDetailsInformationFragment(), getString(R.string.information));

        TabLayout tabLayout = findViewById(R.id.tablayout_contact_friend_detail);
        viewPager.setAdapter(contactFriendDetailAdapter);
        tabLayout.setupWithViewPager(viewPager);
        sqlFriends = new SQLFriends(this);
        fileGet = new FileSave(this, Constants.GET);
        btnSendRequest = findViewById(R.id.btnSendFriend);
        progressBar = findViewById(R.id.progress_bar);
        layout = findViewById(R.id.layout);
        imgCover = findViewById(R.id.img_cover);
        GlideApp.with(this)
                .load(R.drawable.background_gradient)
                .placeholder(R.drawable.noimage)
                .error(R.drawable.background_gradient)
                .into(imgCover);


        tvFollow = findViewById(R.id.tv_follow);
        imgFollow = findViewById(R.id.img_follow);

        btnBack.setPadding(Utilities.dpToPx(mContext, 10), Utilities.getStatusBarHeight(mContext) + Utilities.dpToPx(mContext, 3),
                Utilities.dpToPx(mContext, 16), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataIntent();
    }

    @Override
    public void onBackPressed() {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem == 1) {
            if (Utilities.lsSelectGroupProductFriend.size() > 1) {
                onBackStack(Utilities.lsSelectGroupProductFriend.size() - 1);
            } else {
                super.onBackPressed();
            }
        } else {
            finishAfterTransition();
        }
    }

    private void backLikeHomeHardware() {
        super.onBackPressed();
    }

    public void CheckStatusFriend() {
        try {
            friendsController.getFriendDetail(friend.getUid(), this);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void displayInfo(UserRetro userRetro) {
        tvFriendFullname.setText(userRetro.getDisplayname());
        GlideApp.with(this)
                .load(userRetro.getAvatar())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(imgAvatar);

        GlideApp.with(this)
                .load(userRetro.getCover())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.background_gradient)
                .into(imgCover);


    }

    private void hideAllAndShowSendRequest() {
        btnSendRequest.setVisibility(View.VISIBLE);
        btnAddFriend.setVisibility(View.GONE);
        btnFollowFriend.setVisibility(View.VISIBLE);
//        btnFollowFriend.setTextColor(getResources().getColor(R.color.dark_blue_50));
//        btnFollowFriend.setText(getString(R.string.follow_user));
//        btnFollowFriend.setIconColor(getResources().getColor(R.color.dark_blue_50));

        tvFollow.setText(getString(R.string.follow_user));
        tvFollow.setTextColor(getResources().getColor(R.color.dark_blue_50));
        imgFollow.setImageDrawable(getResources().getDrawable(R.drawable.icon_friend_follow));

        lineaLayoutAcceptDeny.setVisibility(View.GONE);
    }

    private void addFriendRequest(UserRetro friend) {
        try {
            showProgress(true);
            if (friend.getFriendInfo() != null) {

                try {
                    if (!friend.getFriendInfo().getIsAccepted()) {
                        if (friend.getFriendInfo().getIsRequested() && friend.getFriendInfo().getRequestedBy().equals(friend.getUid())) {
                            acceptFriend(friend);
                        } else if (!friend.getFriendInfo().getIsRequested()) {
                            sendRequest(friend);
                        }
                    }

                } catch (Exception e) {
                    showProgress(false);
                    if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
                    Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                }

            } else {
                sendRequest(friend);
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            showProgress(false);
        }
    }

    private void acceptFriend(UserRetro friend) {
        try {
            friendsController.acceptFriend(friend, 0, this);
        } catch (Exception e) {
            showProgress(false);
        }
    }

    private void sendRequest(UserRetro friend) {
        try {
            friendsController.sendRequest(friend, 0, this);
        } catch (Exception e) {
            showProgress(false);
        }
    }

    private void putNotiSendRequest(UserRetro friend) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put("type", 2);
//        map.put("key", friend.getUid());
            String keyData = fileGet.getUserId() + ":" + fileGet.getUserName() + ":" + fileGet.getDisplayName();
            map.put("key", keyData);
            List<String> lstUser = new ArrayList<>();
            lstUser.add(friend.getUid());
            map.put("users", lstUser);

            notiController.sendNoti(map, this);
        } catch (Exception ignored) {

        }
    }

    private void putNotiAcceptFriend(UserRetro friend) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", 3);
//        map.put("key", friend.getUid());
        String keyData = fileGet.getUserId() + ":" + fileGet.getUserName() + ":" + fileGet.getDisplayName();
        map.put("key", keyData);
        List<String> lstUser = new ArrayList<>();
        lstUser.add(friend.getUid());
        map.put("users", lstUser);

        notiController.sendNoti(map, this);
    }

    private void denyFriend(UserRetro friend, boolean isFriend, boolean isRequested) {
        try {
            showProgress(true);
            friendsController.unFriend(friend, isFriend, isRequested, 0, this);
        } catch (Exception e) {
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            showProgress(false);
        }
    }

    private void showProgress(boolean check) {

        if (check) {
            progressBar.setVisibility(View.VISIBLE);
            layout.setAlpha(0.5f);
            layout.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            layout.setAlpha(1);
            layout.setEnabled(true);
        }

    }

    private void showError(boolean check) {
        if (check) {
            layout.setAlpha(0.5f);
            layout.setEnabled(false);
        } else {
            layout.setAlpha(1);
            layout.setEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.RESULT_CODE_UPDATE_PRIVACY_TYPE) {
            try {
                Fragment fragment = contactFriendDetailAdapter.getItem(0);
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            }

        } else if (resultCode == Constants.RESULT_CODE_FOLLOW_PRODUCT) {
            try {
                Fragment fragment = contactFriendDetailAdapter.getItem(0);
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
    }

    public void onBackStack(int position) {
        try {

            int size = FriendDetailsProductFragment.dataLists.size();
            if (position == 0) {
                addSelectAllCate();

                for (int i = 0; i < size; i++) {
                    if (FriendDetailsProductFragment.dataLists.size() == 1) {
                        friendDetailsProductFragment.reloadData();
                        break;
                    }
                    FriendDetailsProductFragment.dataLists.remove(FriendDetailsProductFragment.dataLists.size() - 1);
                }
            } else {
                if (fileGet.getCurrentCreateAtFriendEndProduct()) {
                    getSupportFragmentManager().popBackStack();
                    filePut.putCurrentCreateAtFriendEndProduct(false);
                    if (position >= size) {
                        Utilities.lsSelectGroupProductFriend.remove(Utilities.lsSelectGroupProductFriend.size() - 1);
                    } else {
                        Utilities.lsSelectGroupProductFriend.remove(Utilities.lsSelectGroupProductFriend.size() - 1);
                        if (Utilities.lsSelectGroupProductFriend.get(0).getCatid().equals("-1")) {

                            for (int i = position; i < size; i++) {
                                FriendDetailsProductFragment.dataLists.remove(FriendDetailsProductFragment.dataLists.size() - 1);
                                Utilities.lsSelectGroupProductFriend.remove(Utilities.lsSelectGroupProductFriend.size() - 1);
                            }
                        } else {
                            for (int i = position + 1; i < size; i++) {
                                FriendDetailsProductFragment.dataLists.remove(FriendDetailsProductFragment.dataLists.size() - 1);
                                Utilities.lsSelectGroupProductFriend.remove(Utilities.lsSelectGroupProductFriend.size() - 1);
                            }
                        }
                    }

                } else {

                    if (Utilities.lsSelectGroupProductFriend.get(0).getCatid().equals("-1")) {

                        for (int i = position; i < size; i++) {
                            FriendDetailsProductFragment.dataLists.remove(FriendDetailsProductFragment.dataLists.size() - 1);
                            Utilities.lsSelectGroupProductFriend.remove(Utilities.lsSelectGroupProductFriend.size() - 1);
                        }
                    } else {
                        for (int i = position + 1; i < size; i++) {
                            FriendDetailsProductFragment.dataLists.remove(FriendDetailsProductFragment.dataLists.size() - 1);
                            Utilities.lsSelectGroupProductFriend.remove(Utilities.lsSelectGroupProductFriend.size() - 1);
                        }
                    }

                }

            }

            if (Utilities.lsSelectGroupProductFriend.size() > 0) {
                filePut.putSelectCateCurrentFriend(Utilities.lsSelectGroupProductFriend.get(Utilities.lsSelectGroupProductFriend.size() - 1).getCatid());
            }

            if (Utilities.lsSelectGroupProductFriend.size() == 1) {
                friendDetailsProductFragment.updateViewTypeRoot();
            }

            friendDetailsProductFragment.updateAdapter();

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d(getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void addSelectAllCate() {
        Utilities.lsSelectGroupProductFriend.clear();
        ProductCountResponse p = new ProductCountResponse();
        p.setCatid("-1");
        p.setCatName(getResources().getString(R.string.all));
        Utilities.lsSelectGroupProductFriend.add(p);
    }

    @Override
    public void onGetFriendDetailSuccess(UserRetro data) {
        try {
            friend = data;
            FriendInfo friendInfo = data.getFriendInfo();

            if (friendInfo != null) {

                if (friendInfo.getIsAccepted()) {
                    btnAddFriend.setVisibility(View.VISIBLE);
                    tvAddFriend.setText(getString(R.string.friend));
                    imgAddFriend.setImageDrawable(getResources().getDrawable(R.drawable.icon_friend));
                    tvAddFriend.setTextColor(getResources().getColor(R.color.text_green_personal));
                    lineaLayoutAcceptDeny.setVisibility(View.GONE);
                    btnSendRequest.setVisibility(View.GONE);
                    sqlFriends.insertAddFriend(friend);
                } else {
                    if (friendInfo.getIsRequested()) {
                        if (friendInfo.getRequestedBy().equals(fileGet.getUserId())) {
                            btnAddFriend.setVisibility(View.VISIBLE);
                            imgAddFriend.setImageDrawable(getResources().getDrawable(R.drawable.icon_friend_request));
                            btnAddFriend.setEnabled(true);
                            tvAddFriend.setText(MainActivity.mainContext.getResources().getString(R.string.title_send_request));
                            tvAddFriend.setTextColor(getResources().getColor(R.color.text_green_personal));
                            lineaLayoutAcceptDeny.setVisibility(View.GONE);
                            btnSendRequest.setVisibility(View.GONE);
                        } else {
                            btnAddFriend.setVisibility(View.GONE);
                            lineaLayoutAcceptDeny.setVisibility(View.VISIBLE);
                            btnSendRequest.setVisibility(View.GONE);
                        }
                    } else {
                        btnSendRequest.setVisibility(View.VISIBLE);
                        btnAddFriend.setVisibility(View.GONE);
                        lineaLayoutAcceptDeny.setVisibility(View.GONE);
                    }
                }
                btnFollowFriend.setVisibility(View.VISIBLE);
                if (friendInfo.getFollowByMe()) {
                    tvFollow.setText(getString(R.string.following));
                    tvFollow.setTextColor(getResources().getColor(R.color.text_green_personal));
                    imgFollow.setImageDrawable(getResources().getDrawable(R.drawable.icon_friend_following));
                } else {
                    tvFollow.setText(getString(R.string.follow_user));
                    tvFollow.setTextColor(getResources().getColor(R.color.text_name_option));
                    imgFollow.setImageDrawable(getResources().getDrawable(R.drawable.icon_friend_follow));
                }

            } else {
                hideAllAndShowSendRequest();
            }
            displayInfo(data);

            showError(false);
            showProgress(false);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGetFriendDetailFailure() {
        showError(false);
        showProgress(false);
    }

    @Override
    public void onConnectGetFriendDetailFailure() {
        showError(false);
        showProgress(false);
    }

    @Override
    public void onSendRequestSuccess(UserRetro friend, int position) {
        try {
            CheckStatusFriend();
            putNotiSendRequest(friend);
            showProgress(false);
        } catch (Exception e) {
            showProgress(false);
        }
    }

    @Override
    public void onErrorSendRequest() {
        showProgress(false);
    }

    @Override
    public void onSendRequestFailure() {
        showProgress(false);
    }

    @Override
    public void onConnectSendRequestFailure() {
        showProgress(false);
    }

    @Override
    public void onAcceptFriendSuccess(UserRetro friend, int position) {
        try {
            sqlFriends.insertAddFriend(friend);
            if (Utilities.lsFriendRequest.size() != 0) {
                Utilities.lsFriendRequest.remove(friend);
            }
            CheckStatusFriend();
            putNotiAcceptFriend(friend);
            showProgress(false);
            mDatabaseRef.child(EntityFirebase.TableLastMessages)
                    .child(fileGet.getUserId())
                    .orderByChild(EntityFirebase.FieldSender).equalTo(friend.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Long count = dataSnapshot.getChildrenCount();
                            if (count == 0) return;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                LastMessage lastMessage = snapshot.getValue(LastMessage.class);
                                if (lastMessage != null && lastMessage.is_pending) {

                                    String roomId = snapshot.getKey();

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put(EntityFirebase.FieldIsPending, false);
                                    map.put(EntityFirebase.FieldIsAccepted, true);
                                    mDatabaseRef.child(EntityFirebase.TableLastMessages)
                                            .child(fileGet.getUserId())
                                            .child(roomId)
                                            .updateChildren(map);

                                    mDatabaseRef.child(EntityFirebase.TableDirectMessages)
                                            .child(roomId)
                                            .child(lastMessage.message_id)
                                            .updateChildren(map);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                //Do something after 100ms
                mDatabaseRef.child(EntityFirebase.TableLastMessages)
                        .child(friend.getUid())
                        .orderByChild(EntityFirebase.FieldSender).equalTo(fileGet.getUserId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Long count = dataSnapshot.getChildrenCount();
                                if (count == 0) return;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    LastMessage lastMessage = snapshot.getValue(LastMessage.class);
                                    if (lastMessage != null && lastMessage.is_pending) {

                                        String roomId = snapshot.getKey();

                                        HashMap<String, Object> map1 = new HashMap<>();
                                        map1.put(EntityFirebase.FieldIsPending, false);
                                        map1.put(EntityFirebase.FieldIsAccepted, true);
                                        mDatabaseRef.child(EntityFirebase.TableLastMessages)
                                                .child(friend.getUid())
                                                .child(roomId)
                                                .updateChildren(map1);

                                        mDatabaseRef.child(EntityFirebase.TableDirectMessages)
                                                .child(roomId)
                                                .child(lastMessage.message_id)
                                                .updateChildren(map1);
                                        showProgress(false);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }, 1000);
        } catch (Exception e) {
            showProgress(false);
        }
    }

    @Override
    public void onErrorAcceptFriend() {
        showProgress(false);
    }

    @Override
    public void onAcceptFriendFailure() {
        showProgress(false);
    }

    @Override
    public void onConnectAcceptFriendFailure() {
        showProgress(false);
    }

    @Override
    public void onUnFriendSuccess(UserRetro friend, boolean isFriend, boolean isRequested, int position) {
        try {
            CheckStatusFriend();

            if (isFriend) {
                sqlFriends.deleteFriend(friend.getUid());

                FriendDetailsActivity.friend = null;
            } else {
                if (isRequested) {
                    if (Utilities.lsFriendRequest.size() != 0) {
                        Utilities.lsFriendRequest.remove(position);
                    }

                    lineaLayoutAcceptDeny.setVisibility(View.GONE);
                }
            }

            btnAddFriend.setVisibility(View.GONE);
            btnSendRequest.setVisibility(View.VISIBLE);
            showProgress(false);
        } catch (Exception e) {
            showProgress(false);
        }
    }

    @Override
    public void onErrorUnFriend() {
        showProgress(false);
    }

    @Override
    public void onUnFriendFailure() {
        showProgress(false);
    }

    @Override
    public void onConnectUnFriendFailure() {
        showProgress(false);
    }

    @Override
    public void onSendNotiSuccess() {

    }

    @Override
    public void onErrorSendNoti() {

    }

    @Override
    public void onSendNotiFailure() {

    }

    @Override
    public void onConnectSendNotiFailure() {

    }
}
