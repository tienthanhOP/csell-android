package csell.com.vn.csell.views.friend.fragment;

import android.annotation.SuppressLint;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.FriendsController;
import csell.com.vn.csell.views.friend.activity.FriendDetailsActivity;
import csell.com.vn.csell.views.friend.adapter.EndProductsAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.models.ProductCountResponse;
import csell.com.vn.csell.sqlites.SQLProducts;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendEndProductFragment extends Fragment implements FriendsController.OnGetFriendListProductListener {

    @SuppressLint("StaticFieldLeak")
    public static RecyclerView rvEndProduct;
    private LinearLayoutManager mLayoutManager;

    private ProgressBar progressBar;
    //    private SwipeRefreshLayout loading_refreshing;
    private int skip = 0;
    private ProductCountResponse productCount;
    private FileSave fileGet;
    public EndProductsAdapter endProductsAdapter;
    private List<Product> lsProducts;
    private FriendsController friendsController;
    private RelativeLayout layoutContent;
    private boolean firstLoad;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getContext(), new Crashlytics());
        lsProducts = new ArrayList<>();
        fileGet = new FileSave(getActivity(), Constants.GET);
        FileSave filePut = new FileSave(getActivity(), Constants.PUT);
        filePut.putCurrentCreateAtFriendEndProduct(true);
        friendsController = new FriendsController(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_end_product, container, false);
    }

    public void hideProgressBar(boolean loading) {
        if (loading) {
            if (firstLoad) {
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    progressBar.setVisibility(View.GONE);
                    layoutContent.setVisibility(View.VISIBLE);
                    firstLoad = false;
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

    private void initView(View root) {
        layoutContent = root.findViewById(R.id.layout_content);
        progressBar = root.findViewById(R.id.progress_bar);
        firstLoad = true;
        hideProgressBar(false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            productCount = (ProductCountResponse) bundle.getSerializable(Constants.KEY_PASSINGDATA_PRODUCT_COUNT);
        }
//        loading_refreshing = root.findViewById(R.id.loading_refreshing);
        rvEndProduct = root.findViewById(R.id.rc_end_product);
        rvEndProduct.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(rvEndProduct.getContext());
        rvEndProduct.setLayoutManager(mLayoutManager);

        if (productCount != null) {
            if (!TextUtils.isEmpty(productCount.getCatid())) {
                if (productCount.getCatid().startsWith(Utilities.SIM)) {
                    endProductsAdapter = new EndProductsAdapter(getActivity(), lsProducts, true);

                } else {
                    endProductsAdapter = new EndProductsAdapter(getActivity(), lsProducts, false);

                }
            } else {
                endProductsAdapter = new EndProductsAdapter(getActivity(), lsProducts, false);

            }

        }


        rvEndProduct.setAdapter(endProductsAdapter);

        loadDataProducts();

    }

    private void loadDataProducts() {
        try {
            friendsController.getFriendListProduct(FriendDetailsActivity.friend.getUid(),
                    productCount.getCatid() + "", 0, 100, false, this);
        } catch (Exception ignored) {

        }
    }

    private void loadMoreProducts(int _skip, int limit) {
        try {
            friendsController.getFriendListProduct(FriendDetailsActivity.friend.getUid(),
                    productCount.getCatid() + "", _skip, limit, true, this);
        } catch (Exception ignored) {

        }
    }

    private void initEvent() {

        rvEndProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager.findLastCompletelyVisibleItemPosition() == lsProducts.size() - 1) {
                    if (skip >= 10) {
                        loadMoreProducts(skip, lsProducts.size() + 10);
                    }
                }
            }
        });

//        loading_refreshing.setOnRefreshListener(() -> {
//            loadDataProducts();
//        });

    }

    @Override
    public void onGetFriendListProductSuccess(List<Product> data, boolean isLoadMore) {
        try {
            if (isLoadMore) {
                lsProducts.addAll(data);
                endProductsAdapter.notifyDataSetChanged();
                skip = lsProducts.size();
            } else {
                lsProducts.addAll(data);
                for (Product product : lsProducts) {
                    product.setCatid(productCount.getCatid());
                    product.setType(1);
                }
                endProductsAdapter.notifyDataSetChanged();
                skip = lsProducts.size();
            }
            hideProgressBar(true);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGetFriendListProductFailure(Response<JSONResponse<List<Product>>> response) {
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
            hideProgressBar(true);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onConnectGetFriendListProductFailure() {
        hideProgressBar(true);
    }
}
