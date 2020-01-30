package csell.com.vn.csell.views.friend.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.FriendsController;
import csell.com.vn.csell.views.friend.adapter.FriendProductAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.models.ProductCountResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendDetailsProductFragment extends Fragment implements FriendsController.OnGetFriendGroupProductListener {

    // UI
    private RecyclerView rvListPost;
    private TextView txtNotFoundProduct;
    private FriendProductAdapter mAdapter;
    private FileSave fileGet;
    public static List<ProductCountResponse> productCountResponses;
    public static List<List<ProductCountResponse>> dataLists = new ArrayList<>();
    private FriendsController friendsController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_friend_detail_post, container, false);
        initView(rootView);
        friendsController = new FriendsController(getActivity());
        dataLists.clear();
        loadDataProduct(1, null, false);
        return rootView;
    }

    private void initView(View rootView) {
        rvListPost = rootView.findViewById(R.id.rv_list_post_friend_detail);
        txtNotFoundProduct = rootView.findViewById(R.id.txt_info_productFriend);
        fileGet = new FileSave(getActivity(), Constants.GET);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvListPost.setLayoutManager(linearLayoutManager);
        rvListPost.setHasFixedSize(true);
    }

    private void loadDataProduct(int level, String catId, boolean isSubCat) {
        productCountResponses = new ArrayList<>();
        productCountResponses.clear();
        mAdapter = new FriendProductAdapter(getActivity(), friendsController, this, productCountResponses, isSubCat);
        rvListPost.setAdapter(mAdapter);

        if (TextUtils.isEmpty(fileGet.getProjectCurrent())) {
            friendsController.getFriendGroupProduct(fileGet.getFriendId(), level, catId, "", this);
        } else {
            friendsController.getFriendGroupProduct(fileGet.getFriendId(), level, catId, fileGet.getProjectCurrentFriend(), this);
        }
    }

    public void reloadData() {
        productCountResponses.clear();
        loadDataProduct(1, null, false);
        mAdapter.notifyDataSetChanged();
    }

    public void updateViewTypeRoot() {
        mAdapter.isSubCate = false;
    }

    public void updateAdapter() {
        if (dataLists.size() > 1) {
            productCountResponses.clear();
            productCountResponses.addAll(dataLists.get(dataLists.size() - 1));
            mAdapter.notifyDataSetChanged();
        } else {
            if (dataLists.size() != 0) {
                productCountResponses.clear();
                productCountResponses.addAll(dataLists.get(0));
                mAdapter.notifyDataSetChanged();
            }

        }
    }

    @Override
    public void onGetFriendGroupProductSuccess(List<ProductCountResponse> data) {
        try {
            mAdapter.reloadData(data);
            if (productCountResponses.size() == 0) {
                txtNotFoundProduct.setVisibility(View.VISIBLE);
            } else {
                txtNotFoundProduct.setVisibility(View.GONE);
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGetFriendGroupProductFailure(Response<JSONResponse<List<ProductCountResponse>>> response) {
        try {
            if (response.errorBody() != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                    String msg = (String) jsonObject.get(Constants.ERROR);
                    if (!TextUtils.isEmpty(msg)) {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onConnectGetFriendGroupProductFailure() {

    }
}
