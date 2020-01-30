package csell.com.vn.csell.views.friend.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.FriendsController;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.friend.adapter.ListFriendsRequestAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.views.friend.fragment.HomeFriendFragment;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.models.FriendResponse;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by cuong.nv on 3/2/2018.
 */

public class ListFriendRequestActivity extends AppCompatActivity implements FriendsController.OnGetFriendsRequestListener, FriendsController.OnAcceptFriendListener, FriendsController.OnUnFriendListener {

    private ListFriendsRequestAdapter adapter;
    private FancyButton btnBackNavigation;
    private ArrayList<FriendResponse> listFriendRequest;
    private FileSave fileGet;
    private SwipeRefreshLayout refreshLayout;
    private EditText edtSearch;
    private RecyclerView rvAcceptFriends;
    private FriendsController friendsController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_friends);
        Fabric.with(this, new Crashlytics());
        fileGet = new FileSave(this, Constants.GET);
        friendsController = new FriendsController(this);
        initView();
        initEvent();
    }


    private void initEvent() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    adapter.findRequest(s.toString().trim());
                } else {
                    adapter.findRequest("");
                }
            }
        });

        btnBackNavigation.setOnClickListener(v -> {
            onBackPressed();
        });

        refreshLayout.setOnRefreshListener(this::getListFriendRequest);
    }

    private void initView() {
        edtSearch = findViewById(R.id.txt_search);
        btnBackNavigation = findViewById(R.id.btn_back_navigation);
        btnBackNavigation.setText(getString(R.string.title_back_vn));
        refreshLayout = findViewById(R.id.loading_refreshing);
        FancyButton btnSaveNavigation = findViewById(R.id.btn_save_navigation);
        btnSaveNavigation.setVisibility(View.GONE);
        TextView titleToolbar = findViewById(R.id.custom_TitleToolbar);
        titleToolbar.setText(getString(R.string.friend_request));
        rvAcceptFriends = findViewById(R.id.rv_acceptFriends);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvAcceptFriends.setLayoutManager(layoutManager);
        rvAcceptFriends.setHasFixedSize(true);

        listFriendRequest = new ArrayList<>();
        getListFriendRequest();
    }

    @Override
    public void onBackPressed() {
        try {
            MainActivity.bottomNavigationViewEx.setCurrentItem(3);
            HomeFriendFragment.currentTab = 2;
            finishAfterTransition();
        } catch (Exception e) {
            finishAfterTransition();
        }
    }

    private void getListFriendRequest() {
        try {
            friendsController.getFriendsRequest(0, 1000, this);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGetFriendsRequestSuccess(List<FriendResponse> data) {
        try {
            listFriendRequest.clear();
            listFriendRequest.addAll(data);
            refreshLayout.setRefreshing(false);
            adapter = new ListFriendsRequestAdapter(ListFriendRequestActivity.this, listFriendRequest, false, this, this);
            rvAcceptFriends.setAdapter(adapter);
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
        adapter.handleSuccessAcceptFriend(friend, position);
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
        adapter.handleSuccessUnFriend(friend, position);
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
