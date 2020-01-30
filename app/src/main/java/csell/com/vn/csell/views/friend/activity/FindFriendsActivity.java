package csell.com.vn.csell.views.friend.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.BuildConfig;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DatabaseReference;
//////import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.FriendsController;
import csell.com.vn.csell.views.friend.adapter.FindFriendAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FirebaseDBUtil;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.models.UserRetro;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindFriendsActivity extends AppCompatActivity implements FriendsController.OnSendRequestListener, FriendsController.OnAcceptFriendListener {

    private RecyclerView rvFindFriends;
    @SuppressLint("StaticFieldLeak")
    public static FindFriendAdapter adapter;
    private FancyButton btnBackNavigation;
    private FancyButton btnSaveNavigation;
    private EditText edtSearch;

    private String search = "";
    public static int _skip = 0;
    private Timer timer = new Timer();
    private final long DELAY = 500; // milliseconds
    private FileSave fileSave;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar progressLoadmoreFriend;
    public static DatabaseReference mDatabaseRef;
    private TextView tvNotFound;
    private ImageView imgDelete;

    //corner_top_white search
    ArrayList<UserRetro> dataFriends = new ArrayList<>();
    private RelativeLayout fromSearch;
    private TextView txtLoadMore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        Fabric.with(this, new Crashlytics());

        fileSave = new FileSave(FindFriendsActivity.this, Constants.GET);
        initView();
        initEvent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        txtLoadMore = findViewById(R.id.txt_loadMore);
        fromSearch = findViewById(R.id.from_search);
        fromSearch.setVisibility(View.VISIBLE);
        tvNotFound = findViewById(R.id.txt_not_found);
        mDatabaseRef = FirebaseDBUtil.getDatebase().getReference();
        rvFindFriends = findViewById(R.id.rv_findfriends);

        btnBackNavigation = findViewById(R.id.btn_back_navigation);
        btnSaveNavigation = findViewById(R.id.btn_save_navigation);

        btnBackNavigation.setIconResource(getString(R.string.icon_back));

        edtSearch = findViewById(R.id.txt_search);
        btnSaveNavigation.setVisibility(View.GONE);
        btnSaveNavigation.setIconResource("");
        btnSaveNavigation.setText(getString(R.string.title_find));

        progressLoadmoreFriend = findViewById(R.id.progress_loadmore_friend);
        rvFindFriends.setVisibility(View.VISIBLE);
        mLayoutManager = new LinearLayoutManager(FindFriendsActivity.this);
        rvFindFriends.setLayoutManager(mLayoutManager);
        rvFindFriends.setHasFixedSize(true);
        adapter = new FindFriendAdapter(this, dataFriends, this, this);
        rvFindFriends.setAdapter(adapter);

        imgDelete = findViewById(R.id.img_delete);

        edtSearch.setFocusableInTouchMode(true);
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
                if (!TextUtils.isEmpty(s)) {
                    imgDelete.setVisibility(View.VISIBLE);
                } else {
                    searchFriend(0);
                    imgDelete.setVisibility(View.GONE);
                    txtLoadMore.setVisibility(View.GONE);
                }
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (s.toString().length() > 1) {
                            search = s.toString();
                            _skip = 0;
                            searchFriend(0);
                        }
                    }
                }, DELAY);
            }
        });

        txtLoadMore.setOnClickListener(v -> {
            txtLoadMore.setVisibility(View.GONE);
            progressLoadmoreFriend.setVisibility(View.VISIBLE);
            searchFriend(_skip);
        });

        btnBackNavigation.setOnClickListener(v -> {
            finishAfterTransition();
        });

        imgDelete.setOnClickListener(v -> edtSearch.setText(""));

    }

    public void searchFriend(int skip) {

        try {

            String text = edtSearch.getText().toString().trim();
            GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileSave.getToken());
            Call<JSONResponse<List<UserRetro>>> searchFriend = getAPI.searchFriend(text, skip, 10);
            searchFriend.enqueue(new Callback<JSONResponse<List<UserRetro>>>() {
                @Override
                public void onResponse(Call<JSONResponse<List<UserRetro>>> call, Response<JSONResponse<List<UserRetro>>> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getSuccess() != null) {
                                if (response.body().getSuccess()) {

                                    if (response.body().getData().size() == 0) {
                                        if (_skip == 0) {
                                            showError(true);
                                            dataFriends.clear();
                                            progressLoadmoreFriend.setVisibility(View.GONE);
                                            txtLoadMore.setVisibility(View.GONE);
                                            return;
                                        } else {
                                            if (dataFriends.size() == 0) {
                                                showError(true);
                                            }
                                            progressLoadmoreFriend.setVisibility(View.GONE);
                                            txtLoadMore.setVisibility(View.GONE);
                                            return;
                                        }
                                    }
                                    showError(false);
                                    rvFindFriends.setVisibility(View.VISIBLE);
                                    _skip += response.body().getData().size();
                                    if (skip == 0) {
                                        dataFriends.clear();
                                        dataFriends.addAll(response.body().getData());
                                    } else {
                                        dataFriends.addAll(response.body().getData());
                                    }
                                    progressLoadmoreFriend.setVisibility(View.GONE);
                                    txtLoadMore.setVisibility(View.VISIBLE);
                                    adapter.notifyDataSetChanged();

                                }
                                //                                else {
                                //                                Toast.makeText(FindFriendsActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                //                                }
                            }

                            String msg;
                            if (response.body() != null) {
                                msg = response.body().getError();
                                if (!TextUtils.isEmpty(msg)) {
                                    dataFriends.clear();
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            if (response.errorBody() != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = (String) jsonObject.get(Constants.ERROR);
                                    if (!TextUtils.isEmpty(msg)) {
                                        Toast.makeText(FindFriendsActivity.this, msg, Toast.LENGTH_LONG).show();
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
                public void onFailure(Call<JSONResponse<List<UserRetro>>> call, Throwable t) {
                    if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
                }
            });

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }

    }

    private void showError(boolean check) {
        if (check) {
            tvNotFound.setVisibility(View.VISIBLE);
        } else {
            tvNotFound.setVisibility(View.GONE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
    }

    @Override
    public void onSendRequestSuccess(UserRetro friend, int position) {
        try {
            adapter.handleSuccessSendRequest(friend, position);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onErrorSendRequest() {

    }

    @Override
    public void onSendRequestFailure() {

    }

    @Override
    public void onConnectSendRequestFailure() {

    }

    @Override
    public void onAcceptFriendSuccess(UserRetro friend, int position) {
        try {
            adapter.handleSuccessAcceptFriend(friend, position);
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
}
