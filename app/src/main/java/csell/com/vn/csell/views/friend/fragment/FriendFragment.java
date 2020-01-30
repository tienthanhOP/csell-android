package csell.com.vn.csell.views.friend.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.FriendsController;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.friend.activity.ListFriendRequestActivity;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.friend.adapter.FriendsFirebaseAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.models.FriendResponse;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.sqlites.SQLFriends;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by chuc.nq on 2/7/2018.
 */

public class FriendFragment extends Fragment implements FriendsController.OnGetFriendsListener {
    RecyclerView rvFriend;
    @SuppressLint("StaticFieldLeak")
    public static FriendsFirebaseAdapter friendsAdaper;
    public static ArrayList<UserRetro> friendArrayList;
    public static ArrayList<String> callLogList;
    private FileSave fileSave;
    private CoordinatorLayout layoutFriend;
    private SQLFriends sqlFriends;
    private SwipeRefreshLayout loading_refreshing;
    private LinearLayout layoutFriendRequets;
    private TextView txtFriendRequets;
    private BaseActivityTransition baseActivityTransition;
    private FriendsController friendsController;

    public static FriendFragment getInstance() {
        return new FriendFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendArrayList = new ArrayList<>();
        Fabric.with(getContext(), new Crashlytics());
        baseActivityTransition = new BaseActivityTransition(getActivity());
        friendsController = new FriendsController(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        initView(rootView);
        initEvent();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void initView(View rootView) {
        txtFriendRequets = rootView.findViewById(R.id.txt_friend_request);
        layoutFriendRequets = rootView.findViewById(R.id.layout_friend_request);
        layoutFriend = rootView.findViewById(R.id.layout_friend);
        loading_refreshing = rootView.findViewById(R.id.loading_refreshing);
        sqlFriends = new SQLFriends(getActivity());
        rvFriend = rootView.findViewById(R.id.rv_friend);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvFriend.setLayoutManager(linearLayoutManager);
        rvFriend.setHasFixedSize(true);
        callLogList = new ArrayList<>();

        fileSave = new FileSave(getActivity().getBaseContext(), Constants.GET);

        displayListFriends();
    }

    private void displayListFriends() {
        friendsAdaper = new FriendsFirebaseAdapter(getActivity(), friendArrayList);
        rvFriend.setAdapter(friendsAdaper);
    }

    private void initEvent() {

        layoutFriendRequets.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListFriendRequestActivity.class);
            baseActivityTransition.transitionTo(intent, 0);
        });

        loading_refreshing.setOnRefreshListener(() -> {
            if (Utilities.isNetworkConnected(MainActivity.mainContext)) {
                if (Utilities.getInetAddressByName() != null) {
                    getFriend();
                    loading_refreshing.setRefreshing(false);
                } else {
                    Snackbar.make(layoutFriend, getResources().getString(R.string.Please_check_your_network_connection),
                            Snackbar.LENGTH_LONG).show();
                    loading_refreshing.setRefreshing(false);
                }
            } else {
                Snackbar.make(layoutFriend, getResources().getString(R.string.Please_check_your_network_connection),
                        Snackbar.LENGTH_LONG).show();
                loading_refreshing.setRefreshing(false);
            }

        });
    }

    public static void hasDuplicates(ArrayList<String> data) {
        final ArrayList<String> usedNames = new ArrayList<>();
        Iterator<String> it = data.iterator();
        while (it.hasNext()) {
            String s = it.next();

            if (usedNames.contains(s)) {
                it.remove();

            } else {
                usedNames.add(s);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sqlFriends.checkExistData()) {
            friendArrayList.clear();
            friendArrayList.addAll(sqlFriends.getAllFriend1());
            friendsAdaper.notifyDataSetChanged();
        } else {
            friendArrayList.clear();
            friendsAdaper.notifyDataSetChanged();
        }
    }

    public void getFriend() {

        try {
            friendsController.getFriends(0, 1000, this);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGetFriendsSuccess(ArrayList<UserRetro> data) {
        try {
            friendArrayList.clear();
            friendArrayList.addAll(data);
            sqlFriends.clearData();
            sqlFriends.insertFriends1(friendArrayList);
            friendsAdaper.notifyDataSetChanged();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGetFriendsFailure() {

    }

    @Override
    public void onConnectGetFriendsFailure() {

    }
}
