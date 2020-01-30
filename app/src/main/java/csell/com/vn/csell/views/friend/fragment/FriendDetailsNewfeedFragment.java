package csell.com.vn.csell.views.friend.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.interfaces.OnLoadmoreListener;
import csell.com.vn.csell.views.friend.activity.FriendDetailsActivity;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.social.adapter.SocialNewFeedsFirebaseAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.models.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by User on 5/1/2018.
 */

public class FriendDetailsNewfeedFragment extends Fragment {

    private RecyclerView rvNewfeed;
    private ProgressBar progressBar;
    private SocialNewFeedsFirebaseAdapter mAdapter;
    private RelativeLayout layoutContent;
    private FileSave fileGet;

    private List<Product> data;

    private int timeout = 0;
    private Context mContext;
    private TextView txtNotFoundProduct;
    private boolean firstLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_friend_detail_newfeed, container, false);
        initView(rootView);
        addEvent();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void addEvent() {

    }

    private void hideProgressBar(boolean b) {
        if (b) {
            if (firstLoading) {
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    progressBar.setVisibility(View.GONE);
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    layoutContent.startAnimation(animation);
                    layoutContent.setVisibility(View.VISIBLE);
                    firstLoading = false;
                }, 500);
            } else {
                progressBar.setVisibility(View.GONE);
                layoutContent.setVisibility(View.VISIBLE);
            }
        } else {
            progressBar.setVisibility(View.VISIBLE);
            layoutContent.setVisibility(View.GONE);
        }
    }

    private void initView(View rootView) {
        layoutContent = rootView.findViewById(R.id.layout_content);
        progressBar = rootView.findViewById(R.id.progress_bar);
        firstLoading = true;
        hideProgressBar(false);

        txtNotFoundProduct = rootView.findViewById(R.id.txt_info_productFriend2);
        fileGet = new FileSave(getActivity(), Constants.GET);
        rvNewfeed = rootView.findViewById(R.id.rv_list_newfeed_friend);
        rvNewfeed.setLayoutManager(new LinearLayoutManager(getActivity()));
        mContext = getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        data = new ArrayList<>();
        mAdapter = new SocialNewFeedsFirebaseAdapter(getActivity(), data, false, new OnLoadmoreListener() {
            @Override
            public void onLoadMore(int count) {

                if (FriendDetailsActivity.friend != null) {
                    getNewFeeds(FriendDetailsActivity.friend.getUid(), count, true);
                } else {
                    getNewFeeds(fileGet.getUserId(), count, true);
                }
            }
        });
        rvNewfeed.setAdapter(mAdapter);

        if (FriendDetailsActivity.friend != null) {
            getNewFeeds(FriendDetailsActivity.friend.getUid(), 0, false);
        } else {
            getNewFeeds(fileGet.getUserId(), 0, false);
        }

    }

    private void getNewFeeds(String userId, int skip, boolean isLoadMore) {
        GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
        Call<JSONResponse<List<Product>>> productResponse = getAPI.getFriendNewFeed(userId, skip, 10);
        productResponse.enqueue(new Callback<JSONResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<JSONResponse<List<Product>>> call, Response<JSONResponse<List<Product>>> response) {
                if (response.isSuccessful()) {
                    JSONResponse<List<Product>> result = response.body();
                    if (result.getSuccess() != null) {
                        if (result.getSuccess()) {
                            if (result.getData() != null) {
                                if (result.getData().size() == 0 && data.size() == 0) {

                                    rvNewfeed.setVisibility(View.GONE);
                                    txtNotFoundProduct.setVisibility(View.VISIBLE);
                                } else {
                                    rvNewfeed.setVisibility(View.VISIBLE);
                                    txtNotFoundProduct.setVisibility(View.GONE);
                                    if (isLoadMore) {
                                        if (result.getData().size() > 0) {
                                            data.addAll(result.getData());
                                            mAdapter.notifyDataSetChanged();
                                        } else {
                                            mAdapter.hideLoadmore();
                                        }

                                    } else {
                                        data.clear();
                                        data.addAll(result.getData());
                                        mAdapter.notifyDataSetChanged();
                                    }


//                                    Toast.makeText(mContext, skip + "/" + data.size(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Utilities.refreshToken(MainActivity.mainContext, result.getMessage().toLowerCase() + "");
                        }
                    }
                } else {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = (String) jsonObject.get(Constants.ERROR);
                            if (!TextUtils.isEmpty(msg)) {
                                if (mContext != null) {
                                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                        }
                    }
                }
                hideProgressBar(true);
                productResponse.cancel();
            }

            @Override
            public void onFailure(Call<JSONResponse<List<Product>>> call, Throwable t) {
                productResponse.cancel();

                hideProgressBar(true);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.RESULT_CODE_UPDATE_PRIVACY_TYPE) {
            try {
                if (data != null) {
                    int positon = data.getIntExtra(Constants.TEMP_POSITION, -1);
                    int postType = data.getIntExtra(Constants.POST_TYPE, -1);
                    if (positon != -1 && postType != -1) {
                        this.data.get(positon).setPrivacyType(postType);
                        mAdapter.notifyItemChanged(positon);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        } else if (resultCode == Constants.RESULT_CODE_FOLLOW_PRODUCT) {
            try {
                if (data != null) {
                    if (data.getIntExtra(Constants.TEMP_POSITION, -1) != -1) {
                        this.data.get(data.getIntExtra(Constants.TEMP_POSITION, -1)).setFollowItem(data.getBooleanExtra(Constants.RESULT_FOLLOW, false));
                        mAdapter.notifyItemChanged(data.getIntExtra(Constants.TEMP_POSITION, -1));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }
    }
}
