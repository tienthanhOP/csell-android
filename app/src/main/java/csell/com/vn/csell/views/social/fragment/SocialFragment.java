package csell.com.vn.csell.views.social.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.SocialController;
import csell.com.vn.csell.interfaces.OnLoadmoreListener;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.product.activity.SelectCategoryActivity;
import csell.com.vn.csell.views.social.adapter.SocialNewFeedsFirebaseAdapter;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.mycustoms.FilterSocialDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class SocialFragment extends Fragment implements SocialController.OnGetNewfeedsListener, SocialController.OnFilterNewfeedListener {

    // UI
    private RecyclerView rvListSocial;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout loading_refreshing;
    public SocialNewFeedsFirebaseAdapter newfeedAdapter;
    public ArrayList<String> listProductID = null;
    private ArrayList<Product> listProducts = null;
    public int status = 0;
    private FileSave fileGet;
    private FileSave filePut;
    public boolean firstCheck = false;
    private boolean notifyData = false;

    public SocialFragment getInstance() {
        return new SocialFragment();
    }

    private LinearLayout layoutShare;
    private CircleImageView imgAvatar;
    Button btnCreatePostEmpty;
    Button btnCreateProduct;
    Dialog dialogCreateFeed;
    private String rootCat = "", subCat = "", cat = "";
    private String city = "", district = "";
    Long priceMin, priceMax;
    boolean isFilter = false;
    private CoordinatorLayout layoutSocialFragment;
    private ProgressBar progressBar;
    private CoordinatorLayout layoutContent;
    private boolean firstLoad;
    private BaseActivityTransition baseActivityTransition;
    private SocialController socialController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_social, container, false);
        initView(view);

        addEvent();
        return view;
    }

    private void initView(View view) {
        socialController = new SocialController(getActivity());
        baseActivityTransition = new BaseActivityTransition(getActivity());

        progressBar = view.findViewById(R.id.progress_bar);
        layoutContent = view.findViewById(R.id.layout_content);
        firstLoad = true;
        hideProgressBar(false);

        layoutSocialFragment = view.findViewById(R.id.layout_social_fragment);
        loading_refreshing = view.findViewById(R.id.loading_refreshing);
        layoutShare = view.findViewById(R.id.layout_share_social);
        imgAvatar = view.findViewById(R.id.img_avatar_user);
        rvListSocial = view.findViewById(R.id.rv_list_social);
        fileGet = new FileSave(getActivity(), Constants.GET);
        filePut = new FileSave(getActivity(), Constants.PUT);

        mLayoutManager = new LinearLayoutManager(rvListSocial.getContext());
        rvListSocial.setLayoutManager(mLayoutManager);
        rvListSocial.setHasFixedSize(true);

        firstCheck = true;
        listProductID = new ArrayList<>();
        // khởi tạo list mới
        listProducts = new ArrayList<>();
        newfeedAdapter = new SocialNewFeedsFirebaseAdapter(getActivity(), listProducts, true, new OnLoadmoreListener() {
            @Override
            public void onLoadMore(int positon) {
                loadMoreNewFeeds(positon, isFilter);
            }
        });
        rvListSocial.setAdapter(newfeedAdapter);

        try {
            if (Utilities.isNetworkConnected(getActivity())) {
                if (Utilities.getInetAddressByName() != null) {
                    getNewFeeds(false);
                } else {
                    Snackbar.make(layoutSocialFragment, getResources().getString(R.string.Please_check_your_network_connection), Snackbar.LENGTH_LONG).show();
                    if (!TextUtils.isEmpty(fileGet.getCacheSocial())) {
                        listProducts.clear();
                        listProducts.addAll(new Gson().fromJson(fileGet.getCacheSocial(), new TypeToken<ArrayList<Product>>() {
                        }.getType()));
                        newfeedAdapter.notifyDataSetChanged();
                    }
                    hideProgressBar(true);
                }
            } else {
                Snackbar.make(layoutSocialFragment, getResources().getString(R.string.Please_check_your_network_connection), Snackbar.LENGTH_LONG).show();
                if (!TextUtils.isEmpty(fileGet.getCacheSocial())) {
                    listProducts.clear();
                    listProducts.addAll(new Gson().fromJson(fileGet.getCacheSocial(), new TypeToken<ArrayList<Product>>() {
                    }.getType()));
                    newfeedAdapter.notifyDataSetChanged();
                }
                hideProgressBar(true);
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void addEvent() {
        layoutShare.setOnClickListener(v -> displaydialogCreateFeed());

        loading_refreshing.setOnRefreshListener(() -> {
            if (Utilities.isNetworkConnected(getActivity())) {
                if (Utilities.getInetAddressByName() != null) {
                    getNewFeeds(isFilter);
                } else {
                    Snackbar.make(layoutSocialFragment, getResources().getString(R.string.Please_check_your_network_connection), Snackbar.LENGTH_LONG).show();
                    if (!TextUtils.isEmpty(fileGet.getCacheSocial())) {
                        listProducts.clear();
                        listProducts.addAll(new Gson().fromJson(fileGet.getCacheSocial(), new TypeToken<ArrayList<Product>>() {
                        }.getType()));
                        newfeedAdapter.notifyDataSetChanged();
                    }
                    hideProgressBar(true);
                    loading_refreshing.setRefreshing(false);
                }
            } else {
                Snackbar.make(layoutSocialFragment, getResources().getString(R.string.Please_check_your_network_connection), Snackbar.LENGTH_LONG).show();
                if (!TextUtils.isEmpty(fileGet.getCacheSocial())) {
                    listProducts.clear();
                    listProducts.addAll(new Gson().fromJson(fileGet.getCacheSocial(), new TypeToken<ArrayList<Product>>() {
                    }.getType()));
                    newfeedAdapter.notifyDataSetChanged();
                }
                hideProgressBar(true);
                loading_refreshing.setRefreshing(false);
            }
        });

//        rvListSocial.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                try {
//                    if (mLayoutManager.findLastCompletelyVisibleItemPosition() == listProducts.size() - 1) {
//                        loadMoreNewFeeds(listProducts.size(), isFilter);
//                    }
//                } catch (Exception ignored) {
//
//                }
//            }
//        });
    }

    public void scrollTop() {
        if (rvListSocial != null) {
            rvListSocial.scrollToPosition(0);
        }
    }

    private void getNewFeeds(boolean isFilter) {
        try {
            newfeedAdapter.setLoaded(false);

            Set<String> set = fileGet.getUserJob();
            List<String> list = new ArrayList<>(set);

            if (!isFilter) {
                socialController.getNewfeeds(list.get(0), 0, 5, false, this);
            } else {

                if (priceMin != null && priceMax == null) {
                    priceMax = priceMin * 1000000000;
                } else if (priceMin == null && priceMax != null) {
                    priceMin = 0L;
                }

                if (priceMin != null && priceMax != null) {
                    if (priceMin > priceMax) {
                        long max = priceMax;
                        long min = priceMin;
                        priceMin = max;
                        priceMax = min;
                    }
                }

                socialController.filterNewfeed(0, 5, rootCat, subCat, cat,
                        priceMin != null ? priceMin + "" : "",
                        priceMax != null ? priceMax + "" : "",
                        city, district, false, this);
            }
        } catch (Exception e) {
            loading_refreshing.setRefreshing(false);
            hideProgressBar(true);
            Crashlytics.logException(e);
        }
    }

    private void hideProgressBar(boolean hide) {
        try {
            if (hide) {
                if (firstLoad) {
                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                        layoutContent.startAnimation(animation);
                        firstLoad = false;
                        progressBar.setVisibility(View.GONE);
                        layoutContent.setVisibility(View.VISIBLE);
                    }, 500);
                } else {
                    progressBar.setVisibility(View.GONE);
                    layoutContent.setVisibility(View.VISIBLE);
                }
            } else {
                progressBar.setVisibility(View.VISIBLE);
                layoutContent.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }
    }

    private void loadMoreNewFeeds(int skip, boolean isFilter) {
        try {
            Set<String> set = fileGet.getUserJob();
            List<String> list = new ArrayList<>(set);

            if (!isFilter) {
                socialController.getNewfeeds(list.get(0), skip, 5, true, this);
            } else {
                socialController.filterNewfeed(skip, 5, rootCat, subCat, cat,
                        priceMin != 0 ? priceMin + "" : "",
                        priceMax != 0 ? priceMin + "" : "",
                        city, district, true, this);
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void displaydialogCreateFeed() {

        dialogCreateFeed = new Dialog(getActivity(), R.style.DialogShareAnimation);

        dialogCreateFeed.setContentView(R.layout.dialog_post_social);

        btnCreatePostEmpty = dialogCreateFeed.findViewById(R.id.btn_share_post);
        btnCreateProduct = dialogCreateFeed.findViewById(R.id.btn_share_new_post);
        CircleImageView imgAvatar = dialogCreateFeed.findViewById(R.id.img_avatar_user);

        GlideApp.with(getActivity())
//                    .load(fileGet.getUserAvatar())
                .load(!TextUtils.isEmpty(fileGet.getUserAvatar()) ? fileGet.getUserAvatar() : "")
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(imgAvatar);

        addEventDialogPost();

        //custom position dialog
        Window dialogWindow = dialogCreateFeed.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.TOP);
        dialogWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

//        lp.windowAnimations = R.style.DialogCreateAnimation;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // Width
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // Height


        dialogWindow.setAttributes(lp);

        dialogCreateFeed.show();
    }

    private void addEventDialogPost() {

        btnCreateProduct.setOnClickListener(v -> {
            dialogCreateFeed.dismiss();
            Intent selectCategoty = new Intent(getActivity(), SelectCategoryActivity.class);
            selectCategoty.putExtra(Constants.POST_EMPTY_KEY, false);
            baseActivityTransition.transitionTo(selectCategoty, Constants.ADD_PRODUCT_RESULT);
        });

        btnCreatePostEmpty.setOnClickListener(v -> {
            dialogCreateFeed.dismiss();
            Intent intent = new Intent(getActivity(), SelectCategoryActivity.class);
            intent.putExtra(Constants.POST_EMPTY_KEY, true);
            baseActivityTransition.transitionTo(intent, Constants.POST_EMPTY);
        });
    }

    public void nextFilterActivity() {
        FilterSocialDialog filterSocialDialog = new FilterSocialDialog(Objects.requireNonNull(getActivity()));
        filterSocialDialog.show();
//        Intent intent = new Intent(activity, FilterActivity.class);
//        startActivityForResult(intent, Constants.KEY_FILTER_ACTIVITY_RESULT);
    }

    @Override
    public void onStop() {
        super.onStop();
//        Utilities.queryFilter = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (notifyData) {
            notifyData = false;
        } else {
            GlideApp.with(this)
                    .load(!TextUtils.isEmpty(fileGet.getUserAvatar()) ? fileGet.getUserAvatar() : R.drawable.ic_logo)
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo)
                    .into(imgAvatar);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.REUP_PRODUCT_RESULT) {
            if (data != null) {
                Product product = (Product) data.getSerializableExtra(Constants.KEY_PASSINGDATA_REUP_PRODUCT);
                if (product != null) {
                    int pos = fileGet.getCurrentPositionSocial();
                    if (pos != -1) {
                        listProducts.get(pos).setReup(true);
                        filePut.putCacheSocial(new Gson().toJson(listProducts));
                        newfeedAdapter.notifyItemChanged(pos);
                    }
                    listProducts.add(0, product);
                    notifyData = true;
                    filePut.putCacheSocial(new Gson().toJson(listProducts));
                    newfeedAdapter.notifyItemChanged(0);

                }
            }
        } else if (resultCode == Constants.POST_EMPTY) {
//            Toast.makeText(MainActivity.mainContext, "Post Empty", Toast.LENGTH_SHORT).show();
            if (data != null) {
                Product product = (Product) data.getSerializableExtra(Constants.KEY_PASSINGDATA_EMPTY_POST);
                if (product != null) {
                    listProducts.add(0, product);
                    notifyData = true;
                    filePut.putCacheSocial(new Gson().toJson(listProducts));
                    newfeedAdapter.notifyDataSetChanged();
                }
            }
        } else if (resultCode == Constants.KEY_FILTER_ACTIVITY_RESULT) {
            isFilter = data.getBooleanExtra(Constants.KEY_FILTER, false);
            if (isFilter) {
                rootCat = !TextUtils.isEmpty(data.getStringExtra(Constants.KEY_FILTER_ROOT_CATEGORY)) ?
                        data.getStringExtra(Constants.KEY_FILTER_ROOT_CATEGORY) : "";
                subCat = !TextUtils.isEmpty(data.getStringExtra(Constants.KEY_FILTER_SUB_CATEGORY)) ?
                        data.getStringExtra(Constants.KEY_FILTER_SUB_CATEGORY) : "";
                cat = !TextUtils.isEmpty(data.getStringExtra(Constants.KEY_FILTER_CATEGORY)) ?
                        data.getStringExtra(Constants.KEY_FILTER_CATEGORY) : "";
                city = !TextUtils.isEmpty(data.getStringExtra(Constants.KEY_FILTER_CITY)) ?
                        data.getStringExtra(Constants.KEY_FILTER_CITY) : "";
                district = !TextUtils.isEmpty(data.getStringExtra(Constants.KEY_FILTER_DISTRICT)) ?
                        data.getStringExtra(Constants.KEY_FILTER_DISTRICT) : "";
                priceMin = data.getSerializableExtra(Constants.KEY_FILTER_PRICE_MIN) != null ?
                        (Long) data.getSerializableExtra(Constants.KEY_FILTER_PRICE_MIN) : null;
                priceMax = data.getSerializableExtra(Constants.KEY_FILTER_PRICE_MAX) != null ?
                        (Long) data.getSerializableExtra(Constants.KEY_FILTER_PRICE_MAX) : null;
            }
            getNewFeeds(isFilter);
        } else if (resultCode == Constants.ADD_PRODUCT_RESULT) {
            if (data != null) {
                Product product = (Product) data.getSerializableExtra(Constants.KEY_PASSINGDATA_ADD_PRODUCT);
                if (product != null) {
                    getNewFeeds(isFilter);
                }
            }
        } else if (resultCode == Constants.RESULT_CODE_UPDATE_PRIVACY_TYPE) {
            try {
                if (data != null) {
                    int positon = data.getIntExtra(Constants.TEMP_POSITION, -1);
                    int postType = data.getIntExtra(Constants.POST_TYPE, -1);
                    if (positon != -1 && postType != -1) {
                        listProducts.get(positon).setPrivacyType(postType);
                        filePut.putCacheSocial(new Gson().toJson(listProducts));
                        newfeedAdapter.notifyItemChanged(positon);
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
                        listProducts.get(data.getIntExtra(Constants.TEMP_POSITION, -1)).setFollowItem(data.getBooleanExtra(Constants.RESULT_FOLLOW, false));
                        filePut.putCacheSocial(new Gson().toJson(listProducts));
                        newfeedAdapter.notifyItemChanged(data.getIntExtra(Constants.TEMP_POSITION, -1));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onGetNewFeedsSuccess(List<Product> data, boolean isLoadMore) {
        if (isLoadMore) {
            listProducts.addAll(data);
            filePut.putCacheSocial(new Gson().toJson(listProducts));

            if (data.size() != 0) {
                newfeedAdapter.setLoaded(false);
                newfeedAdapter.removeLastItem();

                if (!rvListSocial.isComputingLayout()) {
                    newfeedAdapter.removeLastItem();
                } else {
                    rvListSocial.getHandler().post(() -> {
                        if (!rvListSocial.isComputingLayout()) {
                            newfeedAdapter.update(data);
                        } else {
                            rvListSocial.getHandler().post(() -> newfeedAdapter.update(data));
                        }
                    });
                }
            } else {
                newfeedAdapter.hideLoadmore();
            }
        } else {
            listProducts.clear();
            listProducts.addAll(data);
            filePut.putCacheSocial(new Gson().toJson(listProducts));
            newfeedAdapter.setData(data);
            loading_refreshing.setRefreshing(false);
            hideProgressBar(true);
        }
    }

    @Override
    public void onGetNewFeedsFailure(boolean isLoadMore) {
        if (!isLoadMore) {
            loading_refreshing.setRefreshing(false);
            hideProgressBar(true);
        }
    }

    @Override
    public void onConnectGetNewFeedsSuccess(boolean isLoadMore) {
        if (!isLoadMore) {
            loading_refreshing.setRefreshing(false);
            hideProgressBar(true);
        }
    }

    @Override
    public void onFilterNewfeedSuccess(List<Product> data, boolean isLoadMore) {
        if (isLoadMore) {
            listProducts.addAll(data);
            filePut.putCacheSocial(new Gson().toJson(listProducts));
            newfeedAdapter.notifyDataSetChanged();
        } else {
            listProducts.clear();
            listProducts.addAll(data);
            filePut.putCacheSocial(new Gson().toJson(listProducts));
            newfeedAdapter.notifyDataSetChanged();
        }
        loading_refreshing.setRefreshing(false);
        hideProgressBar(true);
    }

    @Override
    public void onFilterNewfeedFailure(boolean isLoadMore) {
        if (!isLoadMore) {
            loading_refreshing.setRefreshing(false);
            hideProgressBar(true);
        }
    }

    @Override
    public void onConnectFilterNewfeedSuccess(boolean isLoadMore) {
        if (!isLoadMore) {
            loading_refreshing.setRefreshing(false);
            hideProgressBar(true);
        }
    }

}