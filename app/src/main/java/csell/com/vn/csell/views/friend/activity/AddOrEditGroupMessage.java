package csell.com.vn.csell.views.friend.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.FriendsController;
import csell.com.vn.csell.views.friend.adapter.ChooseFriendAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.models.FriendResponse;
import csell.com.vn.csell.models.UserRetro;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddOrEditGroupMessage extends AppCompatActivity implements FriendsController.OnGetFriendsListener {

    private RecyclerView rvListFriend;
    private ChooseFriendAdapter adapterFriend;
    private ArrayList<UserRetro> dataFriend;
    private FileSave fileGet;
    private Context mContext;
    private FancyButton btnBack;
    private FriendsController friendsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_group_message);

        mContext = this;
        fileGet = new FileSave(mContext, Constants.GET);
        friendsController = new FriendsController(this);

        initView();
        initEvent();
    }

    private void initView() {
        rvListFriend = findViewById(R.id.rv_choose_friend);
        btnBack = findViewById(R.id.btn_back_navigation);

        dataFriend = new ArrayList<>();
        getFriend();

    }

    private void initEvent() {

        btnBack.setOnClickListener(v -> onBackPressed());
        adapterFriend = new ChooseFriendAdapter(this, dataFriend);
        rvListFriend.setHasFixedSize(true);
        rvListFriend.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        rvListFriend.setAdapter(adapterFriend);
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
            dataFriend.clear();
            dataFriend.addAll(data);
            adapterFriend.notifyDataSetChanged();
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
