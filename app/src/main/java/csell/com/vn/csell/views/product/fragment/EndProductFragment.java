package csell.com.vn.csell.views.product.fragment;

import android.annotation.SuppressLint;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.ProductController;
import csell.com.vn.csell.interfaces.RetryConnect;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.models.ProductCountResponse;
import csell.com.vn.csell.models.ProductResponseV1;
import csell.com.vn.csell.mycustoms.DisconnectDialog;
import csell.com.vn.csell.sqlites.SQLCacheProduct;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.friend.adapter.EndProductsAdapter;
import csell.com.vn.csell.views.product.activity.SelectCategoryActivity;
import io.fabric.sdk.android.Fabric;

/**
 * Created by cuong.nv on 3/16/2018.
 */

public class EndProductFragment extends Fragment implements RetryConnect, ProductController.OnGetListProductsListener {

    public RecyclerView rvEndProduct;
    public EndProductsAdapter endProductsAdapter;
    public ProductClickFloatButton onClickFloatButton;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar progressBar;
    //    private SwipeRefreshLayout loading_refreshing;
    private int skip = 0;
    private ProductCountResponse productCount;
    private List<Product> lsProducts;
    private FileSave fileGet;
    private SQLCacheProduct sqlCacheProduct;
    private BaseActivityTransition baseActivityTransition;
    private ProductController productController;
    private RelativeLayout layoutContent;
    private boolean firstLoad;

    public EndProductFragment() {
        onClickFloatButton = new ProductClickFloatButton();
    }

    public static EndProductFragment getInstance() {
        return new EndProductFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getContext(), new Crashlytics());
        sqlCacheProduct = new SQLCacheProduct(getActivity());
        lsProducts = new ArrayList<>();
        baseActivityTransition = new BaseActivityTransition(getActivity());
        productController = new ProductController(getActivity());
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

    @SuppressLint("SetTextI18n")
    private void initView(View root) {
        layoutContent = root.findViewById(R.id.layout_content);
        progressBar = root.findViewById(R.id.progress_bar);
        firstLoad = true;
        hideProgressBar(false);

        progressBar.setVisibility(View.VISIBLE);
        layoutContent.setVisibility(View.GONE);

        fileGet = new FileSave(getActivity(), Constants.GET);
        FileSave filePut = new FileSave(getActivity(), Constants.PUT);
        Bundle bundle = getArguments();
        if (bundle != null) {
            productCount = (ProductCountResponse) bundle.getSerializable(Constants.KEY_PASSINGDATA_PRODUCT_COUNT);
        }
        filePut.putCurrentCreateAtEndProduct(true);
        TextView txtTitleProduct = root.findViewById(R.id.title_product);
//        loading_refreshing = root.findViewById(R.id.loading_refreshing);
        rvEndProduct = root.findViewById(R.id.rc_end_product);
        rvEndProduct.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(rvEndProduct.getContext());
        rvEndProduct.setLayoutManager(mLayoutManager);

        if (productCount != null) {
            if (!TextUtils.isEmpty(productCount.getCatid())) {
                if (productCount.getCatid().startsWith(Utilities.SIM)) {
                    endProductsAdapter = new EndProductsAdapter(getActivity(), lsProducts, true);
                    rvEndProduct.setAdapter(endProductsAdapter);
                } else {
                    endProductsAdapter = new EndProductsAdapter(getActivity(), lsProducts, false);
                    rvEndProduct.setAdapter(endProductsAdapter);
                }
            } else {
                endProductsAdapter = new EndProductsAdapter(getActivity(), lsProducts, false);
                rvEndProduct.setAdapter(endProductsAdapter);
            }
        }

        loadDataProducts();

        if (Utilities.lsSelectGroupProduct.size() > 0) {
            txtTitleProduct.setText(Utilities.lsSelectGroupProduct.get(Utilities.lsSelectGroupProduct.size() - 1).getCatName() + "");
        }
    }

    private void loadDataProducts() {
        try {
            if (Utilities.isNetworkConnected(getActivity())) {
                if (Utilities.getInetAddressByName() != null) {
                    loadData();
                } else {
                    loadDataCache();
                }
            } else {
                loadDataCache();
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void loadDataCache() {
        try {
            lsProducts.clear();
            if (TextUtils.isEmpty(fileGet.getProjectCurrent())) {
                String dataTemp = sqlCacheProduct.getDataByCatId(productCount.getCatid());
                if (!TextUtils.isEmpty(dataTemp)) {
                    lsProducts.addAll(new Gson().fromJson(dataTemp,
                            new TypeToken<List<Product>>() {
                            }.getType()));
                    endProductsAdapter.notifyDataSetChanged();
                } else {
                    DisconnectDialog disconnectDialog = new DisconnectDialog(getActivity(), this);
                    disconnectDialog.setCancelable(false);
                    disconnectDialog.show();
                }
            } else {
                String dataTemp = sqlCacheProduct.getDataByCatId(fileGet.getProjectCurrent() + "_" + productCount.getCatid());
                if (!TextUtils.isEmpty(dataTemp)) {
                    lsProducts.addAll(new Gson().fromJson(dataTemp,
                            new TypeToken<List<Product>>() {
                            }.getType()));
                    endProductsAdapter.notifyDataSetChanged();
                } else {
                    DisconnectDialog disconnectDialog = new DisconnectDialog(getActivity(), this);
                    disconnectDialog.setCancelable(false);
                    disconnectDialog.show();
                }
            }

//            loading_refreshing.setRefreshing(false);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void loadData() {
        try {
            productController.getListProducts(1, productCount.getCatid() + "", fileGet.getProjectCurrent(), 0, 10,
                    false, this);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void initEvent() {

        rvEndProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                try {
                    if (mLayoutManager.findLastCompletelyVisibleItemPosition() == lsProducts.size() - 1) {
                        if (Utilities.isNetworkConnected(getActivity())) {
                            if (Utilities.getInetAddressByName() != null) {
                                if (skip >= 10) {
                                    hideProgressBar(false);
                                    loadMoreProducts(skip, lsProducts.size() + 5);
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
        });

//        loading_refreshing.setOnRefreshListener(this::loadDataProducts);

    }

    private void loadMoreProducts(int _skip, int limit) {
        try {
            productController.getListProducts(1, productCount.getCatid() + "", fileGet.getProjectCurrent(), _skip, limit,
                    true, this);
        } catch (Exception e) {
            hideProgressBar(true);
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onRetryConnect() {
        loadData();
    }

    @Override
    public void onBackRetryConnect() {

    }

    @Override
    public void onGetListProductsSuccess(List<ProductResponseV1> data, int count, boolean loadMore) {
        if (loadMore) {
            if (data.size() > 0) {
//                lsProducts.addAll(data);
                endProductsAdapter.notifyDataSetChanged();
                skip = lsProducts.size();
            }
        } else {
            lsProducts.clear();
//            lsProducts.addAll(data);
            endProductsAdapter.notifyDataSetChanged();
            //save cache
            try {
                if (TextUtils.isEmpty(fileGet.getProjectCurrent()))
                    sqlCacheProduct.insertCache(productCount.getCatid(), new Gson().toJson(lsProducts));
                else
                    sqlCacheProduct.insertCache(fileGet.getProjectCurrent() + "_" + productCount.getCatid(),
                            new Gson().toJson(lsProducts));
            } catch (Exception e) {
                Crashlytics.logException(e);
            }

            skip = lsProducts.size();
        }
        hideProgressBar(true);
    }

    @Override
    public void onGetListProductsFailure() {
        hideProgressBar(true);
    }

    @Override
    public void onConnectGetListProductsFailure() {
        hideProgressBar(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Constants.RESULT_CODE_REMOVE_PRODUCT) {
                try {
                    if (data != null) {
                        int pos = data.getIntExtra(Constants.TEMP_POSITION, -1);
                        if (pos != -1) {
                            lsProducts.remove(pos);
                            endProductsAdapter.notifyItemRemoved(pos);
                            if (lsProducts.size() == 0) {
                                ((MainActivity) getActivity()).productCollectionsFragment.reloadData();
                                MainActivity.fab.setOnClickListener(((MainActivity) getActivity()).productCollectionsFragment.onClickFloatButton.getInstance(getActivity()));
                            }
                            Toast.makeText(getActivity(), "Đã xóa", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.d("error", "onActivityResult: " + e);
                }

            } else if (resultCode == Constants.ADD_PRODUCT_RESULT) {
                if (data != null) {
                    Product product = (Product) data.getSerializableExtra(Constants.KEY_PASSINGDATA_ADD_PRODUCT);
                    if (product != null) {
                        lsProducts.add(0, product);
                        endProductsAdapter.notifyDataSetChanged();
                        skip = lsProducts.size();
                    }
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            if (BuildConfig.DEBUG) Log.e(getClass().getSimpleName(), e.toString() + "");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class ProductClickFloatButton implements View.OnClickListener {
        Context mContext;

        ProductClickFloatButton() {
        }

        public ProductClickFloatButton getInstance(Context context) {
            this.mContext = context;
            return this;
        }

        @Override
        public void onClick(View v) {
            try {

                Intent selectCategoty = new Intent(getActivity(), SelectCategoryActivity.class);
                baseActivityTransition.transitionTo(selectCategoty, Constants.ADD_PRODUCT_RESULT);

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }

        }
    }
}
