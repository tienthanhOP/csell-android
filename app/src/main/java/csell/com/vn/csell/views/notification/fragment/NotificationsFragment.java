package csell.com.vn.csell.views.notification.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.FriendsController;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.notification.adapter.NotificationsAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityFirebase;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FirebaseDBUtil;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.models.Notification;
import csell.com.vn.csell.models.FriendResponse;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by cuong.nv on 3/6/2018.
 */

@SuppressLint("StaticFieldLeak")
public class NotificationsFragment extends Fragment implements FriendsController.OnGetFriendsRequestListener, FriendsController.OnAcceptFriendListener,
        FriendsController.OnUnFriendListener {

    private RecyclerView rvNotifitcation;
    public NotificationsAdapter notificationsAdapter;
    public ArrayList<Notification> dataNotifications;
    private SwipeRefreshLayout loading_refreshing;
    public DatabaseReference dbReference = null;
    public static String lastkey = "";
    public FileSave fileSave;
    private LinearLayoutManager mLayoutManager;
    private GetAPI getAPI;
    public ArrayList<FriendResponse> listFriendRequest;
    private ProgressBar progressBar;
    private CoordinatorLayout layoutNotification;
    private boolean firstLoad;
    private FriendsController friendsController;
    public FriendsController.OnAcceptFriendListener callBackAcceptFriend;
    public FriendsController.OnUnFriendListener callBackUnFriend;

    public static NotificationsFragment getInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getContext(), new Crashlytics());

        callBackAcceptFriend = this;
        callBackUnFriend = this;
        fileSave = new FileSave(getActivity(), Constants.GET);
        dbReference = FirebaseDBUtil.getDatebase().getReference();
        friendsController = new FriendsController(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        fileSave = new FileSave(getActivity(), Constants.GET);
        initView(rootView);
        initEvent();
        return rootView;

    }

    private void initView(View root) {
        progressBar = root.findViewById(R.id.progress_loading);
        loading_refreshing = root.findViewById(R.id.loading_refreshing);
        firstLoad = true;
        hideProgressBar(false);

        layoutNotification = root.findViewById(R.id.layout_notification_fragment);
        rvNotifitcation = root.findViewById(R.id.rv_notification);
        getAPI = RetrofitClient.createService(GetAPI.class, fileSave.getToken());

        rvNotifitcation.addItemDecoration(Utilities.getLineDivider(getActivity()));
        rvNotifitcation.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        rvNotifitcation.setLayoutManager(mLayoutManager);

        if (dataNotifications == null || listFriendRequest == null) {

            try {
                if (dataNotifications == null)
                    dataNotifications = new ArrayList<>();

                if (listFriendRequest == null)
                    listFriendRequest = new ArrayList<>();

                if (notificationsAdapter == null) {
                    notificationsAdapter = new NotificationsAdapter(getActivity(), dataNotifications, listFriendRequest, callBackAcceptFriend,
                            callBackUnFriend);
                }

                rvNotifitcation.setAdapter(notificationsAdapter);

                getNotifications();
            } catch (JsonSyntaxException e) {
                Crashlytics.logException(e);
            }
        } else {
            if (dataNotifications.size() > 1) {
                Notification item = dataNotifications.get(0);
                Notification item2 = dataNotifications.get(1);
                if (item2.notification_id.equals(item.notification_id)) {
                    dataNotifications.remove(0);
                }
            }

            notificationsAdapter = new NotificationsAdapter(getActivity(), dataNotifications, listFriendRequest, callBackAcceptFriend,
                    callBackUnFriend);
            rvNotifitcation.setAdapter(notificationsAdapter);
            notificationsAdapter.notifyDataSetChanged();
            hideProgressBar(true);
        }

    }

    private void initEvent() {

        rvNotifitcation.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager.findLastCompletelyVisibleItemPosition() == dataNotifications.size() - 1) {//                    progressBar.setVisibility(View.VISIBLE);
                    if (Utilities.isNetworkConnected(MainActivity.mainContext)) {
                        if (Utilities.getInetAddressByName() != null) {
                            loadMoreNotifications(lastkey);
                        }
                    }
                }
            }
        });

        loading_refreshing.setOnRefreshListener(this::getNotifications);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void getNotifications() {

        if (Utilities.isNetworkConnected(MainActivity.mainContext)) {
            if (Utilities.getInetAddressByName() != null) {
                getListFriendRequest();
            } else {
                Snackbar.make(layoutNotification, getResources().getString(R.string.Please_check_your_network_connection),
                        Snackbar.LENGTH_LONG).show();
                if (!TextUtils.isEmpty(fileSave.getCacheNotiRequest())) {
                    listFriendRequest.clear();
                    listFriendRequest.addAll(new Gson().fromJson(fileSave.getCacheNotiRequest(),
                            new TypeToken<ArrayList<FriendResponse>>() {
                            }.getType()));
                    loading_refreshing.setRefreshing(false);
                    notificationsAdapter.notifyDataSetChanged();
                }
            }
        } else {
            Snackbar.make(layoutNotification, getResources().getString(R.string.Please_check_your_network_connection),
                    Snackbar.LENGTH_LONG).show();
            if (!TextUtils.isEmpty(fileSave.getCacheNotiRequest())) {
                listFriendRequest.clear();
                listFriendRequest.addAll(new Gson().fromJson(fileSave.getCacheNotiRequest(),
                        new TypeToken<ArrayList<FriendResponse>>() {
                        }.getType()));
                loading_refreshing.setRefreshing(false);
                notificationsAdapter.notifyDataSetChanged();
            }
        }

        Query query1;

        dbReference.child(EntityFirebase.TableNotifications).keepSynced(true);
        query1 = dbReference.child(EntityFirebase.TableNotifications).child(fileSave.getUserId()).orderByKey()
                .limitToLast(10);
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    dataNotifications.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        Notification notification = item.getValue(Notification.class);
                        notification.notification_id = item.getKey();
                        if (dataNotifications.size() == 0) {
                            dataNotifications.add(notification);
                        } else {
                            dataNotifications.add(0, notification);
                        }

                    }

                    loading_refreshing.setRefreshing(false);
                    lastkey = dataNotifications.get(dataNotifications.size() - 1).notification_id;
                    hideProgressBar(true);
                    notificationsAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    hideProgressBar(true);
                    if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                    Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (BuildConfig.DEBUG)
                    Log.d("" + getClass().getName(), databaseError.getMessage());
            }
        });
    }

    public void hideProgressBar(boolean b) {
        if (b) {
            progressBar.setVisibility(View.GONE);
            loading_refreshing.setVisibility(View.VISIBLE);
            if (firstLoad) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                loading_refreshing.startAnimation(animation);
                firstLoad = false;
            }
        } else {
            progressBar.setVisibility(View.VISIBLE);
            loading_refreshing.setVisibility(View.GONE);
        }
    }

    private void loadMoreNotifications(String key) {
        dbReference.child(EntityFirebase.TableNotifications).child(fileSave.getUserId()).orderByKey().endAt(key).limitToLast(6)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                ArrayList<Notification> temp = new ArrayList<>();
                                for (DataSnapshot item : dataSnapshot.getChildren()) {
                                    if (item.getKey().equals(key)) continue;
                                    Notification notification = item.getValue(Notification.class);
                                    notification.notification_id = item.getKey();
                                    if (temp.size() == 0) {
                                        temp.add(notification);
                                    } else {
                                        temp.add(0, notification);
                                    }
                                }

                                dataNotifications.addAll(temp);
                                notificationsAdapter.notifyDataSetChanged();
//                                notificationsAdapterNew.notifyDataSetChanged();
                                lastkey = dataNotifications.get(dataNotifications.size() - 1).notification_id;
                                progressBar.setVisibility(View.GONE);
                            } else {
                                lastkey = "";
                            }

                        } catch (Exception e) {
                            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (BuildConfig.DEBUG)
                            Log.d("" + getClass().getName(), databaseError.getMessage() + "");
                    }
                });
    }

    private void getListFriendRequest() {
        friendsController.getFriendsRequest(0, 2, this);
    }

    public void reloadDataNoti() {
        try {
            if (notificationsAdapter != null)
                notificationsAdapter.notifyDataSetChanged();
            hideProgressBar(true);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGetFriendsRequestSuccess(List<FriendResponse> data) {
        try {
            listFriendRequest.clear();
            listFriendRequest.addAll(data);
            FileSave filePut = new FileSave(getActivity(), Constants.PUT);
            filePut.putCacheNotiRequest(new Gson().toJson(listFriendRequest));
            if (listFriendRequest.size() > 0)
                notificationsAdapter.notifyDataSetChanged();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGetFriendsRequestFailure() {

    }

    @Override
    public void onConnectGetFriendsRequestFailure() {

    }

    @Override
    public void onAcceptFriendSuccess(UserRetro friend, int position) {
        try {
            notificationsAdapter.handleSuccessAcceptFriend(friend, position);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onErrorAcceptFriend() {

    }

    @Override
    public void onAcceptFriendFailure() {

    }

    @Override
    public void onConnectAcceptFriendFailure() {

    }

    @Override
    public void onUnFriendSuccess(UserRetro friend, boolean isFriend, boolean isRequested, int position) {
        try {
            notificationsAdapter.handleSuccessUnFriend(friend, position);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onErrorUnFriend() {

    }

    @Override
    public void onUnFriendFailure() {

    }

    @Override
    public void onConnectUnFriendFailure() {

    }
}
