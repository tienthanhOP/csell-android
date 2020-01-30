package csell.com.vn.csell.views.product.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.product.adapter.SpinnerCateProjectAdapter;
import csell.com.vn.csell.views.friend.adapter.ProductAllProjectAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.models.Category;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.sqlites.SQLCategories;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAllCateProjectFragment extends Fragment {


    RecyclerView rv_product;
    ProgressBar progress_loading;
    SwipeRefreshLayout loading_refreshing;
    private LinearLayoutManager mLayoutManager;
    @SuppressLint("StaticFieldLeak")
    private static ProductAllProjectAdapter productAllProjectAdapter;

    private Category category;
    private Spinner spnCatLandProject;
    private SpinnerCateProjectAdapter spinnerCateProjectAdapter;

    private List<Category> categoryList;
    private static List<Product> productList;
    private String projectId = "";
    private String catFilter = "";
    SQLCategories sqlCategories;
    private FileSave fileGet;
    private static int _skip = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category = new Category();
        productList = new ArrayList<>();
        categoryList = new ArrayList<>();
        sqlCategories = new SQLCategories(getActivity());
        fileGet = new FileSave(getActivity(), Constants.GET);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_by_project, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();
    }

    public void initView(View view) {

        progress_loading = view.findViewById(R.id.progress_loading);
        loading_refreshing = view.findViewById(R.id.loading_refreshing);
        spnCatLandProject = view.findViewById(R.id.spnCatLandProject);

        rv_product = view.findViewById(R.id.rv_product);
        rv_product.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rv_product.setLayoutManager(mLayoutManager);

        category.category_id = "";
        category.category_name = "" + MainActivity.mainContext.getResources().getString(R.string.all);


        categoryList.add(category);
        categoryList.addAll(sqlCategories.getListCategoryByLevel(Utilities.LAND_PROJECT, 3));

        spinnerCateProjectAdapter = new SpinnerCateProjectAdapter(getActivity(), categoryList);
        spnCatLandProject.setAdapter(spinnerCateProjectAdapter);
        spnCatLandProject.setSelection(0);

        productAllProjectAdapter = new ProductAllProjectAdapter(getActivity(), productList);
        rv_product.setAdapter(productAllProjectAdapter);
        progress_loading.setVisibility(View.VISIBLE);
        Bundle intent = getArguments();
        if (intent != null) {
            projectId = intent.getString("PROJECT_ID");
        }
//        getProuducts(0, catFilter, project);

    }

    private void initEvent() {
        loading_refreshing.setOnRefreshListener(() -> {
            productList.clear();
            getProducts(0, catFilter, projectId);
            loading_refreshing.setRefreshing(false);
        });

        rv_product.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager.findLastCompletelyVisibleItemPosition() == productList.size() - 1) {
                    loadMoreData();
                }
            }
        });

        spnCatLandProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    productList.clear();
                    progress_loading.setVisibility(View.VISIBLE);
                    catFilter = categoryList.get(position).category_id;
                    getProducts(0, catFilter, projectId);
                } else {
                    productList.clear();
                    progress_loading.setVisibility(View.VISIBLE);
                    catFilter = categoryList.get(position).category_id;
                    getProducts(0, catFilter, projectId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadMoreData() {
        progress_loading.setVisibility(View.GONE);
        getProducts(_skip, catFilter, projectId);
    }

    private void getProducts(int skip, String cat, String project) {
        GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
        if (getAPI != null) {
            Call<JSONResponse<List<Product>>> jsonResponseCall = getAPI.getProductByProject(project, 2, cat, skip, productList.size() + 10);
            jsonResponseCall.enqueue(new Callback<JSONResponse<List<Product>>>() {
                @Override
                public void onResponse(Call<JSONResponse<List<Product>>> call, Response<JSONResponse<List<Product>>> response) {
                    if (response.isSuccessful()) {
                        JSONResponse<List<Product>> result = response.body();
                        if (result.getSuccess() != null) {
                            if (result.getSuccess()) {
                                if (_skip == 0) {
                                    productList.clear();
                                    productList.addAll(result.getData());
                                } else {
                                    productList.addAll(result.getData());
                                }
                                productAllProjectAdapter.notifyDataSetChanged();
                                _skip += productList.size();
                            } else {
                                Utilities.refreshToken(getActivity(), result.getMessage().toLowerCase() + "");
                                Toast.makeText(getActivity(), "" + result.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
//                        String msg = response.body().getError();
//                        if (!TextUtils.isEmpty(msg)) {
//                            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
//                        }
                    }
                    progress_loading.setVisibility(View.GONE);
                    jsonResponseCall.cancel();
                }

                @Override
                public void onFailure(Call<JSONResponse<List<Product>>> call, Throwable t) {
                    jsonResponseCall.cancel();
                    progress_loading.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public static void displayList(Product product) {
        try {
            productList.add(0, product);
            productAllProjectAdapter.notifyDataSetChanged();
            _skip = productList.size();
        } catch (Exception e) {
            Crashlytics.logException(e);

        }
    }

}
