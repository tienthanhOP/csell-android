package csell.com.vn.csell.views.product.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.commons.PushNotifications;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.ProductController;
import csell.com.vn.csell.interfaces.RetryConnect;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.models.ProductResponseV1;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.sqlites.SQLCacheProduct;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.product.activity.SelectCategoryActivity;
import csell.com.vn.csell.views.product.adapter.CollectionsProductRecyclerAdapterV1;
import io.fabric.sdk.android.Fabric;

////import com.firebase.ui.firestore.FirestoreRecyclerOptions;
//////import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
////import com.google.firebase.firestore.Query;


public class ProductCollectionsFragment extends Fragment implements RetryConnect, ProductController.OnGetListProductsListener, CollectionsProductRecyclerAdapterV1.OnLoadMoreListener {

    //    public static List<List<ProductCountResponse>> dataLists;
//    public CollectionsProductRecyclerAdapter collectionsProductRecyclerAdapter;
    public ProductClickFloatButton onClickFloatButton;
    private CollectionsProductRecyclerAdapterV1 collectionsProductRecyclerAdapterV1;
    private RecyclerView rvAllTab;
    private SwipeRefreshLayout loading_refreshing;
    private FileSave fileGet;
    private FileSave filePut;
    private LinearLayout from_add_first;
    private TextView txtAddFirst;
    private List<ProductResponseV1> productResponseV1List;
    private CoordinatorLayout layoutCollection;
    private SQLCacheProduct sqlCacheProduct;
    private ProgressBar progressBar;
    private CoordinatorLayout layoutContent;
    private boolean firstLoad;
    private int skip = 0;
    private int limit = 20;
    private BaseActivityTransition baseActivityTransition;
    private ProductController productController;
    private LinearLayoutManager mLayoutManager;
    private int countProduct;

    public ProductCollectionsFragment() {
        onClickFloatButton = new ProductClickFloatButton();
    }

    public static ProductCollectionsFragment getInstance() {
        return new ProductCollectionsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getContext(), new Crashlytics());
        fileGet = new FileSave(getActivity(), Constants.GET);
        filePut = new FileSave(getActivity(), Constants.PUT);
//        dataLists = new ArrayList<>();
        productResponseV1List = new ArrayList<>();
        sqlCacheProduct = new SQLCacheProduct(getActivity());
        baseActivityTransition = new BaseActivityTransition(getActivity());
        productController = new ProductController(getActivity());
//        sqlCategories = new SQLCategories(getActivity());

        collectionsProductRecyclerAdapterV1 = new CollectionsProductRecyclerAdapterV1(this);
        collectionsProductRecyclerAdapterV1.setLoadMoreListener(this);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_all, container, false);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initEvent();
    }

    private void initView(View rootView) {
        progressBar = rootView.findViewById(R.id.progress_bar);
        layoutContent = rootView.findViewById(R.id.layout_content);
        firstLoad = true;
        hideProgressBar(false);

        layoutCollection = rootView.findViewById(R.id.container_all_tab);
        rvAllTab = rootView.findViewById(R.id.rv_tab_all);
        loading_refreshing = rootView.findViewById(R.id.loading_refreshing);
        from_add_first = rootView.findViewById(R.id.from_add_first);
        txtAddFirst = rootView.findViewById(R.id.txtAddFirst);
        rvAllTab.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvAllTab.setLayoutManager(mLayoutManager);
        collectionsProductRecyclerAdapterV1.setProductResponseV1List(productResponseV1List);
        rvAllTab.setAdapter(collectionsProductRecyclerAdapterV1);
    }

    @SuppressLint("StaticFieldLeak")
    public void nextFragment() {
        try {
            productController.getListProducts(skip, limit, true, this);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void initEvent() {
        loading_refreshing.setOnRefreshListener(this::reloadData);

        txtAddFirst.setOnClickListener(v -> {
            Intent selectCategoty = new Intent(getActivity(), SelectCategoryActivity.class);
            baseActivityTransition.transitionTo(selectCategoty, Constants.ADD_PRODUCT_RESULT);
        });

        MainActivity.fab.setOnClickListener(v -> {
            Intent selectCategoty = new Intent(getActivity(), SelectCategoryActivity.class);
            baseActivityTransition.transitionTo(selectCategoty, Constants.ADD_PRODUCT_RESULT);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nextFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Constants.ADD_PRODUCT_RESULT) {
//            if (data != null) {
//                Product product = (Product) data.getSerializableExtra(Constants.KEY_PASSINGDATA_ADD_PRODUCT);
//                if (product != null) {
//                    showAlertCreate(true, product.getItemid(), product.getTitle());
//                }
//            }
            reloadData();
        } else if (resultCode == Constants.EDIT_PRODUCT_RESULT) {
            if (data != null) {
                if (data.getBooleanExtra("IS_EDIT", false)) {
                    int positionEdit = data.getIntExtra("POSITION_EDIT", -1);
                    productResponseV1List.remove(positionEdit);
                    ProductResponseV1 productEdit = (ProductResponseV1) data.getSerializableExtra("PRODUCT_EDIT");
                    productResponseV1List.add(positionEdit, productEdit);
                    collectionsProductRecyclerAdapterV1.notifyItemChanged(positionEdit);
                }
            }
        } else if (resultCode == Constants.RESULT_CODE_REMOVE_PRODUCT_V1) {
            if (data != null) {
                int positionDelete = data.getIntExtra("POSITION_DELETE", -1);
                productResponseV1List.remove(positionDelete);
                collectionsProductRecyclerAdapterV1.notifyItemRemoved(positionDelete);
            }
        }
    }

    @Override
    public void onRetryConnect() {
        nextFragment();
    }

    @Override
    public void onBackRetryConnect() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
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

    private void loadMoreProducts(int skip, int limit) {
        try {
            productController.getListProducts(skip, limit, true, this);
        } catch (Exception e) {
            hideProgressBar(true);
            Crashlytics.logException(e);
        }
    }

    public void reloadData() {
        try {
            productController.getListProducts(0, limit, false, this);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public void showAlertCreate(boolean success, String pid, String pname) {
        if (success) {
            reloadData();
            checkAddNotePrivate(pid, pname);
        } else {
            Toast.makeText(MainActivity.mainContext, "Tạo sản phẩm thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkAddNotePrivate(String productId, String productName) {
        if (Utilities.lsFriendsNotePrivate.size() != 0) {
            FileSave fileGet = new FileSave(MainActivity.mainContext, Constants.GET);
            PushNotifications push = new PushNotifications(MainActivity.mainContext);
            for (UserRetro item : Utilities.lsFriendsNotePrivate) {
                HashMap<String, Object> data = new HashMap<>();
                data.put(EntityAPI.FIELD_NOTIFICATION_TITLE, "Share Note Private");
                data.put(EntityAPI.FIELD_NOTIFICATION_BODY, fileGet.getDisplayName() + " "
                        + MainActivity.mainContext.getString(R.string.text_body_shared_note_private) + " "
                        + productName);
                data.put(EntityAPI.FIELD_NOTIFICATION_TARGET_NAME, productName);

                data.put(EntityAPI.FIELD_DATE_CREATED, ServerValue.TIMESTAMP);
                data.put(EntityAPI.FIELD_NOTIFICATION_DATA, productId);
                data.put(EntityAPI.FIELD_NOTIFICATION_SENDER, fileGet.getUserId());
                data.put(EntityAPI.FIELD_NOTIFICATION_SENDER_NAME, fileGet.getDisplayName());
                data.put(EntityAPI.FIELD_NOTIFICATION_SENDER_AVATAR, fileGet.getUserAvatar());
                data.put(EntityAPI.FIELD_NOTIFICATION_IS_SEEN, false);
                if (!TextUtils.isEmpty(item.getUid()))
                    push.pushAddNotePrivate(data, item.getUid());
            }
            Utilities.contentPrivateNote = "";
            Utilities.lsFriendsNotePrivate.clear();
        }
    }

    @Override
    public void onGetListProductsSuccess(List<ProductResponseV1> data, int count, boolean loadMore) {
        countProduct = count;
        if (loadMore) {
            if (data.size() > 0) {
                productResponseV1List.addAll(data);
                collectionsProductRecyclerAdapterV1.notifyDataSetChanged();
                collectionsProductRecyclerAdapterV1.setMoreDataAvailable(true);
                skip = productResponseV1List.size();
            } else {
                collectionsProductRecyclerAdapterV1.setMoreDataAvailable(false);
            }
        } else {
            if (data.size() > 0) {
                productResponseV1List.clear();
                productResponseV1List.addAll(data);
                collectionsProductRecyclerAdapterV1.notifyDataSetChanged();
                collectionsProductRecyclerAdapterV1.setMoreDataAvailable(true);
                skip = productResponseV1List.size();
            } else {
                collectionsProductRecyclerAdapterV1.setMoreDataAvailable(false);
            }
        }
        loading_refreshing.setRefreshing(false);
        hideProgressBar(true);
    }

    @Override
    public void onGetListProductsFailure() {

    }

    @Override
    public void onConnectGetListProductsFailure() {

    }

    @Override
    public void onLoadMore() {
        rvAllTab.post(() -> loadMoreProducts(skip, limit));
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
                filePut.putCurrentCreateAtAllTab(true);
                Intent selectCategoty = new Intent(mContext, SelectCategoryActivity.class);
                baseActivityTransition.transitionTo(selectCategoty, Constants.ADD_PRODUCT_RESULT);
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            }
        }
    }
}
