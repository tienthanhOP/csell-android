package csell.com.vn.csell.views.customer.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.CustomersController;
import csell.com.vn.csell.models.CustomerRetroV1;
import csell.com.vn.csell.models.DataGroupCustomers;
import csell.com.vn.csell.models.GroupCustomerRetroV1;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.customer.activity.AddGroupCustomerActivity;
import csell.com.vn.csell.views.customer.activity.AddOrEditCustomerActivity;
import csell.com.vn.csell.views.customer.activity.ManageGroupCustomerActivity;
import csell.com.vn.csell.views.customer.adapter.CustomerRecentAdapter;
import csell.com.vn.csell.views.customer.adapter.CustomersFirebaseAdapter;
import csell.com.vn.csell.views.customer.adapter.GroupFirebaseAdapter;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

////import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
////import com.firebase.ui.firestore.FirestoreRecyclerOptions;
//import com.google.firebase.firestore.DocumentChange;
//import com.google.firebase.firestore.FirebaseFirestore;
////import com.google.firebase.firestore.Query;

public class CustomersFragment extends Fragment implements CustomersController.OnGetCustomersListener {

    public static final int RESULT_CODE_ADD = 997;
    public static ArrayList<GroupCustomerRetroV1> listGroup;
    public CustomerClickFloatButton onClickFloatButton;
    Animation slideDown, slideUp;
    private RecyclerView rvCustomer;
    private RecyclerView rvRecent;
    private GroupFirebaseAdapter mGroupAdapter;
    private TextView tvManageGroup;
    private RecyclerView rvGroup;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout btnCreateGroup;
    private ProgressBar progressBar;
    private FileSave fileGet;
    private FileSave filePut;
    private CustomersFirebaseAdapter mFirebaseAdapter;
    private CustomerRecentAdapter mRecentAdapter;
    private GetAPI getAPI;
//    private int skip = 0;
//    private boolean isLoadMore = true;

    private ArrayList<CustomerRetroV1> dataCustomer;
    private ArrayList<CustomerRetroV1> dataRecentCustomer;
    //    private SQLCustomers sqlCustomers;
    private int countAllCustomer = 0;
    private LinearLayout frameRecent;
    private NestedScrollView scrollView;
    private Context mContext;
    private CoordinatorLayout layoutCustomer;
    private boolean firstLoad;
    private CoordinatorLayout layoutContent;
    private BaseActivityTransition baseActivityTransition;
    private CustomersController customersController;

    public CustomersFragment() {
        onClickFloatButton = new CustomerClickFloatButton();
    }

    public static CustomersFragment getInstance() {
        return new CustomersFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getContext(), new Crashlytics());
        fileGet = new FileSave(getActivity(), Constants.GET);
        filePut = new FileSave(getActivity(), Constants.PUT);
        baseActivityTransition = new BaseActivityTransition(getActivity());
        customersController = new CustomersController(getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_customer, container, false);
        mContext = getActivity();
        initView(rootView);
        setupWindowAnimations();
        addEvent();
        return rootView;
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(getActivity()).inflateTransition(R.transition.fade);
        getActivity().getWindow().setEnterTransition(transition);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CODE_ADD || requestCode == Constants.RESULT_CODE_DELETE_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                getListGroup("");
//                getListRecent();
                getListCustomer(0);
            }
        }
    }

    public void initView(View rootView) {
        progressBar = rootView.findViewById(R.id.progress_bar);
        layoutContent = rootView.findViewById(R.id.layout_content);
        firstLoad = true;
        hideProgressBar(false);

        layoutCustomer = rootView.findViewById(R.id.layout_customer);
        rvRecent = rootView.findViewById(R.id.rv_reccent_friend);
        frameRecent = rootView.findViewById(R.id.frame_recent);
        LinearLayoutManager mLayoutRecent = new LinearLayoutManager(getActivity());
        rvRecent.setLayoutManager(mLayoutRecent);
//        sqlCustomers = new SQLCustomers(getActivity());
//        countAllCustomer = sqlCustomers.getCountAllCustomer();
        tvManageGroup = rootView.findViewById(R.id.tv_manage_group);
        btnCreateGroup = rootView.findViewById(R.id.btn_create_group);
        rvCustomer = rootView.findViewById(R.id.expand_lv_contact_customer);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvCustomer.setLayoutManager(mLayoutManager);

        slideDown = AnimationUtils.loadAnimation(Objects.requireNonNull(getActivity()).getApplicationContext(),
                R.anim.slide_down);
        slideUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                R.anim.slide_up);
        rvGroup = rootView.findViewById(R.id.rv_group);
        rvGroup.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        listGroup = new ArrayList<>();
        dataCustomer = new ArrayList<>();
        dataRecentCustomer = new ArrayList<>();
        getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
        scrollView = rootView.findViewById(R.id.scroll_view);

        displayListGroups();
        displayListCustomersRecent();
        displayListCustomers();

        getListGroup("");
//        getListRecent();
        getListCustomer(0);
    }

    private void displayListCustomersRecent() {
        mRecentAdapter = new CustomerRecentAdapter(mContext, dataRecentCustomer);
        rvRecent.setAdapter(mRecentAdapter);
    }

    private void displayListGroups() {
        mGroupAdapter = new GroupFirebaseAdapter(mContext, listGroup);
        rvGroup.setAdapter(mGroupAdapter);
    }

    private void displayListCustomers() {
        mFirebaseAdapter = new CustomersFirebaseAdapter(mContext, dataCustomer);
        rvCustomer.setAdapter(mFirebaseAdapter);
        loadListFilter();
    }

    @SuppressLint("StaticFieldLeak")
    private void loadListFilter() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
//                ArrayList<CustomerRetro> lst = new ArrayList<>();
//                lst.addAll(sqlCustomers.getAllCustomer());
//                mFirebaseAdapter.updateList(lst);
                return null;
            }
        }.execute();
    }

    private void getListCustomer(int skip) {
        if (Utilities.isNetworkConnected(mContext)) {
            if (Utilities.getInetAddressByName() != null) {
                try {
//                    if (countAllCustomer == 0) {
                    customersController.getCustomers(skip, 20, "", "", "", false, this);
//                    } else {
//                        dataCustomer.addAll(sqlCustomers.getItems(skip));
//                        sortCustomer(dataCustomer);
//                        mFirebaseAdapter.notifyDataSetChanged();
//                        hideProgressBar(true);
//                    }
                } catch (Exception ignored) {
                    hideProgressBar(true);
                }
            } else {
                Snackbar.make(layoutCustomer, getResources().getString(R.string.Please_check_your_network_connection),
                        Snackbar.LENGTH_LONG).show();
                hideProgressBar(true);
            }
        } else {
            Snackbar.make(layoutCustomer, getResources().getString(R.string.Please_check_your_network_connection),
                    Snackbar.LENGTH_LONG).show();
            hideProgressBar(true);
        }
    }

    private void addEvent() {
        MainActivity.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    mFirebaseAdapter.findContact(s.toString().trim());
                } else {
                    mFirebaseAdapter.findContact("");
                }
            }
        });

        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) {
                // Scroll Down
                if (MainActivity.fab.isShown()) {
                    MainActivity.fab.hide();
                }
            } else if (scrollY < oldScrollY) {
                // Scroll Up
                if (!MainActivity.fab.isShown()) {
                    MainActivity.fab.show();
                }
            }
        });

//        rvCustomer.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
//                int lastVisible = layoutManager.findLastVisibleItemPosition();
//
//                if (lastVisible == dataCustomer.size() - 1 && isLoadMore) {
////                    if (countAllCustomer == 0) {
//                    if (mLayoutManager.findLastCompletelyVisibleItemPosition() == dataCustomer.size() - 1 && isLoadMore) {
//                        getListCustomer(skip);
//                        isLoadMore = false;
//                    }
////                    }
//                }
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//        });

        btnCreateGroup.setOnClickListener(view ->
                baseActivityTransition.transitionTo(new Intent(getActivity(), AddGroupCustomerActivity.class), 0));

        tvManageGroup.setOnClickListener(v ->
                baseActivityTransition.transitionTo(new Intent(getActivity(), ManageGroupCustomerActivity.class), 0));
    }

    @Override
    public void onGetRecentCustomersSuccess(List<CustomerRetroV1> data) {
        if (data != null) {
            if (data.size() == 0) {
                return;
            }
            dataRecentCustomer.clear();
            dataRecentCustomer.addAll(data.subList(0, 5));
//          filePut.putCacheCustomerRecent(new Gson().toJson(dataRecentCustomer));
            mRecentAdapter.notifyDataSetChanged();
            hideProgressBar(true);
        } else {
            hideProgressBar(true);
        }
    }

    @Override
    public void onGetCustomersSuccess(List<CustomerRetroV1> data) {
        try {
//            if (data.size() == 0) {
//                isLoadMore = false;
//                if (skip == 0) {
//                    dataCustomer.clear();
//                }
//            } else {
//                skip += data.size();
////                sqlCustomers.insertListCustomers(data);
//                if (skip == 0) {
//                    isLoadMore = false;
//                    dataCustomer.clear();
//                    dataCustomer.addAll(data);
//                } else {
//                    isLoadMore = true;
//                    dataCustomer.addAll(data);
//                }
//            }
            dataCustomer.clear();
            dataCustomer.addAll(data);
            sortCustomer(dataCustomer);
            mFirebaseAdapter.notifyDataSetChanged();
            hideProgressBar(true);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void onGetCustomersFailure(boolean listRecent) {
        if (!listRecent) {
//            isLoadMore = false;
            hideProgressBar(true);
        }
    }

    @Override
    public void onConnectGetCustomersFailure(boolean listRecent) {
        if (!listRecent) {
            hideProgressBar(true);
        }
    }

    private void getListGroup(String keyword) {
        Call<DataGroupCustomers> getListGroup = getAPI.getListGroup(keyword);
        getListGroup.enqueue(new Callback<DataGroupCustomers>() {
            @Override
            public void onResponse(Call<DataGroupCustomers> call, Response<DataGroupCustomers> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getGroups() != null) {
                                listGroup.clear();
                                listGroup.addAll(response.body().getGroups());
//                                filePut.putCacheGroupCustomer(new Gson().toJson(listGroup));
                                mGroupAdapter.notifyDataSetChanged();
                            } else {
//                                Utilities.refreshToken(MainActivity.mainContext, response.body().getMessage().toLowerCase() + "");
//                                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.text_error_unknown), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                    Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                }
            }

            @Override
            public void onFailure(Call<DataGroupCustomers> call, Throwable t) {
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onResume() {
        super.onResume();
//        new AsyncTask<Void, Void, Void>() {
//
//            @Override
//            protected Void doInBackground(Void... voids) {
//                dataCustomer.clear();
//                countAllCustomer = sqlCustomers.getCountAllCustomer();
//                dataCustomer.addAll(sqlCustomers.getAllCustomer());
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                sortCustomer(dataCustomer);
//                mFirebaseAdapter.notifyDataSetChanged();
//            }
//        }.execute();
//
//        loadListFilter();
//
//        if (Utilities.isNetworkConnected(mContext)) {
//            if (Utilities.getInetAddressByName() != null) {
//                getListGroup();
//                getListRecent();
//            } else {
//                Snackbar.make(layoutCustomer, getResources().getString(R.string.Please_check_your_network_connection),
//                        Snackbar.LENGTH_LONG).show();
//                if (!TextUtils.isEmpty(fileGet.getCacheCustomerRecent())) {
//                    dataRecentCustomer.clear();
//                    dataRecentCustomer.addAll(new Gson().fromJson(fileGet.getCacheCustomerRecent(), new TypeToken<ArrayList<CustomerRetro>>() {
//                    }.getType()));
//                    mRecentAdapter.notifyDataSetChanged();
//                }
//                if (!TextUtils.isEmpty(fileGet.getCacheGroupCustomer())) {
//                    listGroup.clear();
//                    listGroup.addAll(new Gson().fromJson(fileGet.getCacheGroupCustomer(), new TypeToken<ArrayList<GroupCustomerRetro>>() {
//                    }.getType()));
//                    mGroupAdapter.notifyDataSetChanged();
//                }
//            }
//        } else {
//            Snackbar.make(layoutCustomer, getResources().getString(R.string.Please_check_your_network_connection),
//                    Snackbar.LENGTH_LONG).show();
//            if (!TextUtils.isEmpty(fileGet.getCacheCustomerRecent())) {
//                dataRecentCustomer.clear();
//                dataRecentCustomer.addAll(new Gson().fromJson(fileGet.getCacheCustomerRecent(), new TypeToken<ArrayList<CustomerRetro>>() {
//                }.getType()));
//                mRecentAdapter.notifyDataSetChanged();
//            }
//            if (!TextUtils.isEmpty(fileGet.getCacheGroupCustomer())) {
//                listGroup.clear();
//                listGroup.addAll(new Gson().fromJson(fileGet.getCacheGroupCustomer(), new TypeToken<ArrayList<GroupCustomerRetro>>() {
//                }.getType()));
//                mGroupAdapter.notifyDataSetChanged();
//            }
//        }
    }

    private void sortCustomer(ArrayList<CustomerRetroV1> list) {
        Collections.sort(list, (obj1, obj2) -> {
            return obj1.getName().compareToIgnoreCase(obj2.getName()); // To compare string values
        });
    }

//    private void getListRecent() {
//        try {
//            customersController.getCustomers(0, 20, "", "", "", true, this);
//        } catch (Exception ignored) {
//            ignored.getMessage();
//        }
//    }

    public void hideProgressBar(boolean loading) {
        if (loading) {
            if (firstLoad) {
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    layoutContent.startAnimation(animation);
                    firstLoad = false;
                    progressBar.setVisibility(View.GONE);
                    layoutContent.setVisibility(View.VISIBLE);
                }, 1000);
            } else {
                progressBar.setVisibility(View.GONE);
                layoutContent.setVisibility(View.VISIBLE);
            }
        } else {
            progressBar.setVisibility(View.VISIBLE);
            layoutContent.setVisibility(View.GONE);
        }
    }

    public class CustomerClickFloatButton implements View.OnClickListener {
        Context mContext;

        CustomerClickFloatButton() {
        }

        public CustomerClickFloatButton getInstance(Context context) {
            this.mContext = context;
            return this;
        }

        @Override
        public void onClick(View v) {
//            Vao thang man hinh them
            Intent intent = new Intent(mContext, AddOrEditCustomerActivity.class);
            startActivityForResult(intent, RESULT_CODE_ADD);

//            Dialog dialog = new Dialog(mContext);
//            dialog.setContentView(R.layout.dialog_customer_choose);
//
//            Button btnImport = dialog.findViewById(R.id.btn_import_contact);
//            Button btnCreate = dialog.findViewById(R.id.btn_create_contact);
//            Button btnCancel = dialog.findViewById(R.id.btn_cancel_dialog_choose);
//
//            btnImport.setOnClickListener(v1 -> {
//
//                Intent intent = new Intent(mContext, AddCustomerFromContactActivity.class);
//                baseActivityTransition.transitionTo(intent, 0);
//                dialog.dismiss();
//
//            });
//
//            btnCreate.setOnClickListener(v1 -> {
//
//                Intent intent = new Intent(mContext, AddOrEditCustomerActivity.class);
//                baseActivityTransition.transitionTo(intent, 0);
//                dialog.dismiss();
//
//            });
//
//            btnCancel.setOnClickListener(v1 -> dialog.dismiss());
//
//            //custom position dialog
//            Window dialogWindow = dialog.getWindow();
//            WindowManager.LayoutParams lp;
//            if (dialogWindow != null) {
//                lp = dialogWindow.getAttributes();
//                dialogWindow.setGravity(Gravity.BOTTOM);
//                dialogWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//
//                lp.windowAnimations = R.style.DialogCreateAnimation;
//                lp.width = WindowManager.LayoutParams.MATCH_PARENT; // Width
//                lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // Height
//
//                dialogWindow.setAttributes(lp);
//            }
//
//            dialog.show();
        }
    }
}
