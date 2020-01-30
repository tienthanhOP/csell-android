package csell.com.vn.csell.views.product.activity;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.ProductController;
import csell.com.vn.csell.views.friend.adapter.ProductsFavoriteFirebaseAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.models.Product;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteProductsActivity extends AppCompatActivity implements ProductController.OnGetProductFollowsListener {

    private RecyclerView rvListFavorite;
    private SwipeRefreshLayout loadingRefreshing;
    private LinearLayoutManager mLayoutManager;
    private FileSave fileSave;
    private FileSave filePut;

    private ProductsFavoriteFirebaseAdapter mFirebaseAdapter;

    private ArrayList<Product> productList;
    private FancyButton btnBack;
    private TextView titleToolbar;
    private CoordinatorLayout layoutFavorireProduct;
    private Context mContext;
    private ProgressBar progressBar;
    private boolean firstLoad;
    private ProductController productController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_products);
        mContext = this;
        fileSave = new FileSave(this, Constants.GET);
        filePut = new FileSave(this, Constants.PUT);
        productController = new ProductController(this);

        productList = new ArrayList<>();
        initView();
        initEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void initEvent() {
        if (Utilities.isNetworkConnected(mContext)) {
            if (Utilities.getInetAddressByName() != null)
                rvListFavorite.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if (mLayoutManager.findLastCompletelyVisibleItemPosition() == productList.size() - 1) {
                            loadMoreProductFollows();
                        }
                    }

                });
        }
        btnBack.setOnClickListener(v -> finishAfterTransition());
        titleToolbar.setText(R.string.product_interest);
        loadingRefreshing.setOnRefreshListener(this::getProductFollows);
    }

    private void hideProgressBar(boolean b) {
        if (b) {
            if (firstLoad) {
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                    loadingRefreshing.setAnimation(animation);
                    progressBar.setVisibility(View.GONE);
                    loadingRefreshing.setVisibility(View.VISIBLE);
                    firstLoad = false;
                }, 500);
            } else {
                progressBar.setVisibility(View.GONE);
                loadingRefreshing.setVisibility(View.VISIBLE);
            }
        } else {
            progressBar.setVisibility(View.VISIBLE);
            loadingRefreshing.setVisibility(View.GONE);
        }
    }

    private void initView() {
        progressBar = findViewById(R.id.progress_bar);
        loadingRefreshing = findViewById(R.id.loading_refreshing);
        firstLoad = true;
        hideProgressBar(false);

        layoutFavorireProduct = findViewById(R.id.layout_favorite_product);
        btnBack = findViewById(R.id.btn_back_navigation);
        titleToolbar = findViewById(R.id.custom_TitleToolbar);
        rvListFavorite = findViewById(R.id.rv_favorite_list_product);
        mLayoutManager = new LinearLayoutManager(this);
        rvListFavorite.setLayoutManager(mLayoutManager);
        mFirebaseAdapter = new ProductsFavoriteFirebaseAdapter(this, productList);
        rvListFavorite.setAdapter(mFirebaseAdapter);
        btnBack.setText(getResources().getString(R.string.title_back_vn));
        getProductFollows();

    }

    private void getProductFollows() {
        try {
            if (Utilities.isNetworkConnected(mContext)) {
                if (Utilities.getInetAddressByName() != null) {
                    productController.getProductFollows(0, 10, false, this);
                } else {
                    Snackbar.make(layoutFavorireProduct, getResources().getString(R.string.Please_check_your_network_connection),
                            Snackbar.LENGTH_LONG).show();
                    if (!TextUtils.isEmpty(fileSave.getCacheFavoriteProduct())) {
                        productList.clear();
                        productList.addAll(new Gson().fromJson(fileSave.getCacheFavoriteProduct(), new TypeToken<ArrayList<Product>>() {
                        }.getType()));
                        mFirebaseAdapter.notifyDataSetChanged();
                    }
                    hideProgressBar(true);
                }
            } else {
                Snackbar.make(layoutFavorireProduct, getResources().getString(R.string.Please_check_your_network_connection),
                        Snackbar.LENGTH_LONG).show();
                if (!TextUtils.isEmpty(fileSave.getCacheFavoriteProduct())) {
                    productList.clear();
                    productList.addAll(new Gson().fromJson(fileSave.getCacheFavoriteProduct(), new TypeToken<ArrayList<Product>>() {
                    }.getType()));
                    mFirebaseAdapter.notifyDataSetChanged();
                }
                hideProgressBar(true);
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void loadMoreProductFollows() {
        try {
            productController.getProductFollows(productList.size(), 5, true, this);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onGetProductFollowsSuccess(List<Product> data, boolean loadMore) {
        if (loadMore) {
            productList.addAll(data);
            filePut.putCacheFavoriteProduct(new Gson().toJson(productList));
            mFirebaseAdapter.notifyDataSetChanged();
        } else {
            productList.clear();
            productList.addAll(data);
            filePut.putCacheFavoriteProduct(new Gson().toJson(productList));
            mFirebaseAdapter.notifyDataSetChanged();
        }
        hideProgressBar(true);
        loadingRefreshing.setRefreshing(false);
    }

    @Override
    public void onGetProductFollowsFailure(boolean loadMore) {
        loadingRefreshing.setRefreshing(false);
        hideProgressBar(true);
    }

    @Override
    public void onConnectGetProductFollowsFailure(boolean loadMore) {
        hideProgressBar(true);
        loadingRefreshing.setRefreshing(false);
    }
}
