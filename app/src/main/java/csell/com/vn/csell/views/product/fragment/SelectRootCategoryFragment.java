package csell.com.vn.csell.views.product.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.controllers.ProjectsController;
import csell.com.vn.csell.models.CategoryRequest;
import csell.com.vn.csell.models.ItemCategory;
import csell.com.vn.csell.models.Location;
import csell.com.vn.csell.models.ProductCountResponse;
import csell.com.vn.csell.models.Project;
import csell.com.vn.csell.sqlites.SQLCategoriesV1;
import csell.com.vn.csell.sqlites.SQLLocations;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.adapter.LocationSortAdapter;
import csell.com.vn.csell.views.product.activity.SelectCategoryActivity;
import csell.com.vn.csell.views.product.adapter.SelectCategoryAdapter;
import csell.com.vn.csell.views.project.activity.AddProjectActivity;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by cuong.nv on 3/9/2018.
 */

public class SelectRootCategoryFragment extends Fragment implements ProjectsController.OnGetListProjectsListener,
        SelectCategoryAdapter.OnClickCategoryListener {

    public static boolean isProject = false;
    //    ProjectsController projectsController = null;
    public static GridLayoutManager gridLayoutManager;
    public static RecyclerView rvRoot;
    public static SelectCategoryAdapter selectCategoryAdapter;
    public static List<ItemCategory> dataCategories;
    public boolean runLoadMore = true;
    public ArrayList<ArrayList<ProductCountResponse>> totalLists;
    EditText edtSearch;
    ProgressBar progressLoading;
    RecyclerView.OnScrollListener onScrollListener = null;
    TextWatcher searchCategory = null, searchProject = null;
    ArrayList<ProductCountResponse> listProductCountResponse;
    private boolean isEmptyPost = false;
    private FileSave filePut;
    private FileSave fileGet;
    private Button btnAddProject;
    private LinearLayout fromCreateProject;
    private Spinner spnCity;
    private Spinner spnDistrict;
    private LocationSortAdapter adapterDistrict;
    private SQLLocations sqlLocations;
    private String _city = "";
    private String _district = "";
    private List<Location> lsCity;
    private List<Location> lsDistrict;
    private ArrayList<ProductCountResponse> readOnlyCate;
    private SQLCategoriesV1 sqlCategoriesV1;
    private LinearLayout fromCityDistrict;
    private boolean isFilter = false;
    private int posCity = 0;
    private int posDistrict = 0;
    private FancyButton btnSaveToolbar;
    private BaseActivityTransition baseActivityTransition;
    private ProjectsController projectsController;

    public SelectRootCategoryFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getContext(), new Crashlytics());
        baseActivityTransition = new BaseActivityTransition(getActivity());
        sqlCategoriesV1 = new SQLCategoriesV1(getActivity());
        fileGet = new FileSave(getActivity(), Constants.GET);
        filePut = new FileSave(getActivity(), Constants.PUT);
        totalLists = new ArrayList<>();
        lsCity = new ArrayList<>();
        lsDistrict = new ArrayList<>();
        sqlLocations = new SQLLocations(getContext());
        projectsController = new ProjectsController(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_root_category, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            isEmptyPost = bundle.getBoolean(Constants.IS_EMTPY_POST);
        }
        initView(rootView);
        initEvent();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        getListCategory();
    }

    private void initView(View root) {
        btnSaveToolbar = getActivity().findViewById(R.id.btn_save_navigation);
        spnCity = root.findViewById(R.id.spn_city);
        spnDistrict = root.findViewById(R.id.spn_district);
        btnAddProject = root.findViewById(R.id.btnAddProject);
        fromCreateProject = root.findViewById(R.id.from_create_project);
        fromCreateProject.setVisibility(View.GONE);
        readOnlyCate = new ArrayList<>();
        rvRoot = root.findViewById(R.id.lv_root_category);
        edtSearch = root.findViewById(R.id.edtSearch);
        progressLoading = root.findViewById(R.id.progress_loading);
        fromCityDistrict = root.findViewById(R.id.from_city_district);
        listProductCountResponse = new ArrayList<>();

        ProductCountResponse countResponse = new ProductCountResponse();
        countResponse.setCatName(getString(R.string.all));
        listProductCountResponse.add(countResponse);

        dataCategories = new ArrayList<>();
        rvRoot.setHasFixedSize(true);

        edtSearch.setVisibility(View.VISIBLE);

        dataCategories = sqlCategoriesV1.getItemCategoryListByParentIdAndGroup(
                SelectCategoryActivity.horizontalAdapter.getLastItemCategoryList().getCategoryId(),
                SelectCategoryActivity.horizontalAdapter.getLastItemCategoryList().getCategoryGroup() + 1);
        selectCategoryAdapter = new SelectCategoryAdapter(getActivity(), dataCategories, this);
        rvRoot.setAdapter(selectCategoryAdapter);

        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        if (dataCategories.get(0).getCategoryGroup() != 1 && !dataCategories.get(0).getCategoryId().startsWith("land")) {
            gridLayoutManager.setSpanCount(1);
        } else {
            gridLayoutManager.setSpanCount(2);
        }
        rvRoot.setLayoutManager(gridLayoutManager);
        ((SelectCategoryActivity) getActivity()).updateTitleToolbar(getString(R.string.title_toolbar_select_cate));

        searchCategory = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isFilter = !TextUtils.isEmpty(s);
                searchCate(s.toString().trim());
            }
        };

        searchProject = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isFilter = !TextUtils.isEmpty(s) || !TextUtils.isEmpty(_city);
                searchProject(s.toString().trim());
            }
        };

    }

    public void loadColumsRecyclerView() {
        GridLayoutManager.SpanSizeLookup spanSizeLookup = new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int position) {
                switch (selectCategoryAdapter.getItemViewType(position)) {
                    case 1:
                        return gridLayoutManager.getSpanCount();
                    case 2:
                    case 3:
                        return 1;
                    default:
                        return 1;
                }
            }
        };

        gridLayoutManager.setSpanSizeLookup(spanSizeLookup);

//        if (gridLayoutManager.getSpanCount() == 2)
//            rvRoot.addItemDecoration(new SpacesItemDecoration(2, 10, false));
//        else
//            rvRoot.addItemDecoration(new SpacesItemDecoration(1, 0, false));
    }

    private void searchProject(String s) {
        loadMore(0, s, _city, _district, isFilter);
    }

    private void OnClickView(View v, int position) {
//        try {
//            try {
//                ((SelectCategoryActivity) Objects.requireNonNull(getActivity())).addHeader(dataCategories.get(position));
//                listProductCountResponse.add(dataCategories.get(position));
//
//            } catch (Exception ignored) {
//
//            }
//
//            try {
//                filePut.putPrefixCateTemp(dataCategories.get(position).getCatName());
//                filePut.putRootCategoryId(dataCategories.get(position).getCatid());
//            } catch (Exception ignored) {
//
//            }
//
//            if (!TextUtils.isEmpty(dataCategories.get(position).getCatid())) {
//                rvRoot.removeOnScrollListener(onScrollListener);
//                fromCityDistrict.setVisibility(View.GONE);
//
//                Category cate = sqlCategories.getCategoryById(dataCategories.get(position).getCatid());
//                CategoryRequest categoryRequest = new CategoryRequest(cate.category_id, cate.category_name, cate.level);
//                HashMap<String, Object> map = new HashMap<>();
//                map.put("id", cate.category_id);
//                map.put("name", cate.category_name);
//                map.put("group", cate.level);
//                SelectCategoryActivity.paramsCategories.add(map);

//                for (int i = 0; i < SelectCategoryActivity.paramsCategories.size(); i++) {
//                    if (SelectCategoryActivity.paramsCategories.get(i).getGroup().equals(categoryRequest.getGroup())) {
//                        SelectCategoryActivity.paramsCategories.subList(i, SelectCategoryActivity.paramsCategories.size()).clear();
//                        SelectCategoryActivity.paramsCategories.add(categoryRequest);
//                        break;
//                    }
//                }
//                int level = cate.level;
//                if (level == 2) {
//                    if (cate.category_id.startsWith(Constants.KEY_CLASS_CAR)) {
//                        SelectCategoryAdapter.imgGray = cate.image;
//                    } else {
//                        SelectCategoryAdapter.imgGray = "";
//                    }
//                } else if (level == 1) {
//                    filePut.putCategoryIdPostEmpty(cate.category_id);
//                    filePut.putCategoryNamePostEmpty(cate.category_name);
//                }
//                level++;
//                filePut.putRootCategoryId(cate.category_id);
//
//                filePut.putRootCategoryDisplayName(cate.category_name);
//                if (TextUtils.isEmpty(fileGet.getRootCategoryId())) {
//                    filePut.putCategoryIdPostEmpty(fileGet.getRootCategoryId());
//                }
//                if (level > cate.max_level) {
//                    if (isEmptyPost) {
//                        btnSaveToolbar.setVisibility(View.GONE);
//                        InputEmptyPostFragment inputEmptyPostFragment = new InputEmptyPostFragment();
//                        FragmentManager manager = getActivity().getSupportFragmentManager();
//                        FragmentTransaction transaction = manager.beginTransaction();
//                        manager.popBackStack();
//                        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
//                        transaction.replace(R.id.container_frame_created, inputEmptyPostFragment, "InputEmptyPostFragment")
//                                .commit();
//                    } else {
//                        BasicInfoFragment fragment = new BasicInfoFragment();
//                        FragmentManager manager = getActivity().getSupportFragmentManager();
//                        FragmentTransaction transaction = manager.beginTransaction();
//                        manager.popBackStack();
//                        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
//                        transaction.replace(R.id.container_frame_created, fragment)
//                                .commit();
//                    }
//                } else {
//                    btnSaveToolbar.setVisibility(View.VISIBLE);
//                    if (TextUtils.isEmpty(fileGet.getRootCategoryId())) {
//                        filePut.putCategoryIdPostEmpty(fileGet.getRootCategoryId());
//                    }
//                    dataCategories.clear();
//                    readOnlyCate.clear();
//                    ArrayList<ProductCountResponse> temp = new ArrayList<>();
//                    ArrayList<Category> tempCat;
////                    if (!cate.category_id.equals(Utilities.LAND_PROJECT)) {
////                        projectsController = new ProjectsController(getActivity(), this);
////                    } else {
//                    tempCat = new ArrayList<>(sqlCategories.getListCategoryByLevel(cate.category_id, level));
//                    for (Category item : tempCat) {
//                        ProductCountResponse p = new ProductCountResponse();
//                        p.setCatid(item.category_id);
//                        p.setCatName(item.category_name);
//                        temp.add(p);
//                    }
////                    }
//
//                    if (temp.size() > 0) {
//                        progressLoading.setVisibility(View.GONE);
//                        totalLists.add(temp);
//
//                        dataCategories.addAll(temp);
//                        readOnlyCate.addAll(dataCategories);
//
//                    }
//                }
//                adapter.notifyDataSetChanged();
//            } else {
//                fromCityDistrict.setVisibility(View.GONE);
//                rvRoot.removeOnScrollListener(onScrollListener);
//                filePut.putProjectIdPostEmpty(dataCategories.get(position).getProjectid());
//                filePut.putProjectNamePostEmpty(dataCategories.get(position).getProjectName());
//                filePut.putProjectId(dataCategories.get(position).getProjectid());
//                filePut.putProjectName(dataCategories.get(position).getProjectName());
//                filePut.putProjectCity(dataCategories.get(position).city.toLowerCase());
//                filePut.putProjectDistrict(dataCategories.get(position).district.toLowerCase());
//
//                dataCategories.clear();
//                readOnlyCate.clear();
//                ArrayList<ProductCountResponse> temp = new ArrayList<>();
//
//                ArrayList<Category> tempCat;
//                tempCat = new ArrayList<>(sqlCategories.getListCategoryByLevel(Utilities.LAND_PROJECT, 3));
//                for (Category item : tempCat) {
//                    ProductCountResponse p = new ProductCountResponse();
//                    p.setCatid(item.category_id);
//                    p.setCatName(item.category_name);
//                    temp.add(p);
//                }
//
//                if (temp.size() > 0) {
//                    progressLoading.setVisibility(View.GONE);
//
//                    dataCategories.addAll(temp);
//                    readOnlyCate.addAll(dataCategories);
//
//                    totalLists.add(temp);
//                }
//                adapter.notifyDataSetChanged();
//
//                if (searchProject != null) {
//                    edtSearch.removeTextChangedListener(searchProject);
//                }
//                edtSearch.addTextChangedListener(searchCategory);
//            }
//
//            if (edtSearch != null) {
//                edtSearch.setText("");
//                edtSearch.clearFocus();
//            }
//
//        } catch (Exception e) {
//            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
//            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
//        }
//
//        ((SelectCategoryActivity) getActivity()).updateTitleToolbar(getString(R.string.title_toolbar_select_cate));

    }

    private void initEvent() {

        edtSearch.addTextChangedListener(searchCategory);

        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dataCategories.size() > 14) {
                    if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == dataCategories.size() - 1) {
                        if (TextUtils.isEmpty(edtSearch.getText().toString().trim())) {
                            if (runLoadMore)
                                loadMore(dataCategories.size(), "", _city, "", isFilter);
                        } else {
                            if (runLoadMore)
                                loadMore(dataCategories.size(), edtSearch.getText().toString().trim() + "", _city, "", isFilter);
                        }
                        Log.e("corner_top_white", dataCategories.size() + "|" + _city + "|" + isFilter);
                    }
                }

            }
        };

        btnAddProject.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddProjectActivity.class);
            intent.putExtra("projectName", edtSearch.getText().toString().trim());
            intent.putExtra("positionCity", posCity);
            intent.putExtra("positionDistrict", posDistrict);
            baseActivityTransition.transitionTo(intent, Constants.RESULT_CODE_ADD_PROJECT);
        });

    }

    private void loadMore(int skip, String key, String city, String district, boolean isFilter) {
        try {
            progressLoading.setVisibility(View.VISIBLE);
            if (isFilter) {
                projectsController.getListProject(skip, key, city, district, isFilter, this);
            } else {
                projectsController.getListProject(skip, "", "", "", isFilter, this);
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void initEventLocation(Location location) {

        spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int _id = lsCity.get(position).getLocation_id();
                if (adapterDistrict == null) {
                    adapterDistrict = new LocationSortAdapter(getActivity(), lsDistrict);
                }
                runLoadMore = true;
                if (position > 0) {
                    posCity = position;
                    isFilter = true;
                    spnDistrict.setEnabled(true);
                    _city = lsCity.get(position).getLocation_name();
                    _district = "";

                    lsDistrict.clear();
                    lsDistrict.add(0, location);
                    lsDistrict.addAll(sqlLocations.getAllDistrictByCity(lsCity.get(position).getLocation_id(), 2));
                    adapterDistrict.notifyDataSetChanged();
                    spnDistrict.setAdapter(adapterDistrict);
                    if (TextUtils.isEmpty(edtSearch.getText().toString().trim()))
                        loadMore(0, "", _city, "", isFilter);
                    else
                        loadMore(0, edtSearch.getText().toString().trim() + "", _city, "", isFilter);
                } else {

                    if (_id == -1) {
                        lsDistrict.clear();
                        lsDistrict.add(0, location);
                        adapterDistrict.notifyDataSetChanged();
                        spnDistrict.setEnabled(false);
                        _district = "";
                        _city = "";
                        if (TextUtils.isEmpty(edtSearch.getText().toString().trim()))
                            loadMore(0, "", "", "", isFilter);
                        else
                            loadMore(0, edtSearch.getText().toString().trim() + "", "", "", isFilter);
                    }
                    adapterDistrict.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int _id = lsCity.get(position).getLocation_id();
                if (position > 0) {
                    posDistrict = position;
                    _district = lsDistrict.get(position).getLocation_name();
                    if (TextUtils.isEmpty(edtSearch.getText().toString().trim()))
                        loadMore(0, "", _city, _district, isFilter);
                    else
                        loadMore(0, edtSearch.getText().toString().trim() + "", _city, _district, isFilter);
                } else {
                    if (_id == -1) {
                        if (!spnDistrict.isEnabled()) return;
                        _district = "";
                        if (TextUtils.isEmpty(edtSearch.getText().toString().trim()))
                            loadMore(0, "", _city, _district, isFilter);
                        else
                            loadMore(0, edtSearch.getText().toString().trim() + "", _city, _district, isFilter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void searchCate(String search) {
//        ProductCountResponse p = ((SelectCategoryActivity) getActivity()).getHeader();
//        if (p != null) {
//
//            if (TextUtils.isEmpty(p.getCatid())) {
//                checkAndAddToList(search);
//            } else {
//                if (!p.getCatid().equals(Utilities.LAND_PROJECT)) {
//                    checkAndAddToList(search);
//                }
//            }
//        }
    }

    private void checkAndAddToList(String search) {
//        dataCategories.clear();
//
//        if (search.isEmpty()) {
//            dataCategories.addAll(readOnlyCate);
//        } else {
//            search = search.toLowerCase();
//            for (ProductCountResponse item : readOnlyCate) {
//                if (!TextUtils.isEmpty(item.getCatName())) {
//                    if (item.getCatName().toLowerCase().contains(search)) {
//                        dataCategories.add(item);
//                    }
//                } else if (!TextUtils.isEmpty(item.getProjectName())) {
//                    if (item.getProjectName().toLowerCase().contains(search)) {
//                        dataCategories.add(item);
//                    }
//                }
//            }
//        }
//
//        adapter.notifyDataSetChanged();
    }

    private void getListCategory() {
//        try {
//            dataCategories.clear();
//            ArrayList<Category> tempCat = new ArrayList<>();
//            ArrayList<ProductCountResponse> temp = new ArrayList<>();
//            tempCat.addAll(sqlCategories.getListCategoryByLevel(null, SelectCategoryActivity.level));
//            for (Category item : tempCat) {
//                ProductCountResponse p = new ProductCountResponse();
//                p.setCatid(item.category_id);
//                p.setCatName(item.category_name);
//                temp.add(p);
//            }
//
//            if (temp.size() > 0) {
//
//                dataCategories.addAll(temp);
//                adapter.notifyDataSetChanged();
//                progressLoading.setVisibility(View.GONE);
//                readOnlyCate.addAll(dataCategories);
//                totalLists.add(temp);
//
//            }
//
//
//        } catch (Exception e) {
//            progressLoading.setVisibility(View.GONE);
//            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
//            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
//        }
    }

    public void backCategory(boolean loadProject) {
//        try {
//
//            if (totalLists.size() > 1) {
//
//                ProductCountResponse lastProduct = listProductCountResponse.get(listProductCountResponse.size() - 1);
//
//                if (!TextUtils.isEmpty(lastProduct.getCatid())) {
////                    if (!lastProduct.getCatid().equals(Utilities.LAND_PROJECT)) {
////                        totalLists.remove(totalLists.size() - 1);
////                    }
//                    totalLists.remove(totalLists.size() - 1);
//                    if (totalLists.size() == 2)
//                        fromCityDistrict.setVisibility(View.GONE);
//                    dataCategories.clear();
//                    readOnlyCate.clear();
//                    dataCategories.addAll(totalLists.get(totalLists.size() - 1));
//                    readOnlyCate.addAll(dataCategories);
//                    adapter.notifyDataSetChanged();
//
//                    progressLoading.setVisibility(View.GONE);
//
//                } else {
//                    totalLists.remove(totalLists.size() - 1);
//                }
//
//            } else {
//                getActivity().finishAfterTransition();
//            }
//
//            listProductCountResponse.remove(listProductCountResponse.size() - 1);
//            if (listProductCountResponse.size() > 0) {
//
////                if (listProductCountResponse.get(listProductCountResponse.size() - 1).getCatid().equals(Utilities.LAND_PROJECT)) {
////
////                    if (loadProject) {
////
////                        edtSearch.removeTextChangedListener(searchCategory);
////                        if (searchProject != null) {
////                            edtSearch.addTextChangedListener(searchProject);
////                        }
////
////                        fromCityDistrict.setVisibility(View.VISIBLE);
////
////                        rvRoot.addOnScrollListener(onScrollListener);
////                        runLoadMore = true;
////                        loadMore(0, "", _city, _district, isFilter);
////                    }
////
////
////                } else {
////
////                    if (searchProject != null) {
////                        edtSearch.removeTextChangedListener(searchProject);
////                    }
////                    edtSearch.addTextChangedListener(searchCategory);
////
////                    fromCreateProject.setVisibility(View.GONE);
////
////                    fromCityDistrict.setVisibility(View.GONE);
////                    rvRoot.removeOnScrollListener(onScrollListener);
////                }
//                if (searchProject != null) {
//                    edtSearch.removeTextChangedListener(searchProject);
//                }
//                edtSearch.addTextChangedListener(searchCategory);
//
//                fromCreateProject.setVisibility(View.GONE);
//
//                fromCityDistrict.setVisibility(View.GONE);
//                rvRoot.removeOnScrollListener(onScrollListener);
//            }
//
//
//            edtSearch.setText("");
//            edtSearch.clearFocus();
//
//        } catch (Exception e) {
//            Log.e("TEST_", e.getMessage());
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        btnSaveToolbar.setVisibility(View.GONE);
        SelectCategoryActivity.currentFragment = "SelectRootCategoryFragment";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Constants.RESULT_CODE_ADD_PROJECT) {
//
//            if (data != null) {
//                Project project = (Project) data.getSerializableExtra("projectadd");
//                if (project != null) {
//
//                    ProductCountResponse p = new ProductCountResponse();
//                    p.setProjectid(project.getProjectid());
//                    p.setProjectName(project.getProjectName());
//                    p.city = project.getCity();
//                    p.district = project.getDistrict();
//
//                    dataCategories.add(0, p);
//                    adapter.notifyDataSetChanged();
//                }
//            }
//        }
    }

    @Override
    public void onGetListProjectsSuccess(List<Project> data, int skip, boolean isFilter) {
//        if (isFilter) {
//            if (data != null && data.size() > 0) {
//                fromCreateProject.setVisibility(View.GONE);
//                if (skip == 0) {
//                    dataCategories.clear();
//                    runLoadMore = true;
//                    ArrayList<ProductCountResponse> temp = new ArrayList<>();
//                    for (Project item : data) {
//                        ProductCountResponse p = new ProductCountResponse();
//                        p.setProjectid(item.getProjectid());
//                        p.setProjectName(item.getProjectName());
//                        p.setImage(item.getImage());
//                        p.city = item.getCity();
//                        p.district = item.getDistrict();
//                        temp.add(p);
//                    }
//                    dataCategories.addAll(temp);
//                } else {
//                    runLoadMore = true;
//                    ArrayList<ProductCountResponse> temp = new ArrayList<>();
//                    for (Project item : data) {
//                        ProductCountResponse p = new ProductCountResponse();
//                        p.setProjectid(item.getProjectid());
//                        p.setProjectName(item.getProjectName());
//                        p.setImage(item.getImage());
//                        p.city = item.getCity();
//                        p.district = item.getDistrict();
//                        temp.add(p);
//                    }
//                    if (temp.get(temp.size() - 1) == dataCategories.get(dataCategories.size() - 1))
//                        runLoadMore = false;
//                    else
//                        dataCategories.addAll(temp);
//                }
//            } else {
//                if (data.size() == 0 && dataCategories.size() == 0) {
//                    fromCreateProject.setVisibility(View.VISIBLE);
//                }
//                if (skip == 0)
//                    dataCategories.clear();
//
//                runLoadMore = false;
//            }
//
//            adapter.notifyDataSetChanged();
//            progressLoading.setVisibility(View.GONE);
//        } else {
//            if (data.size() > 0) {
//
//                if (skip == 0) {
//                    dataCategories.clear();
//                }
//
//                ArrayList<ProductCountResponse> temp = new ArrayList<>();
//                for (Project item : data) {
//                    ProductCountResponse p = new ProductCountResponse();
//                    p.setProjectid(item.getProjectid());
//                    p.setProjectName(item.getProjectName());
//                    p.setImage(item.getImage());
//                    p.city = item.getCity();
//                    p.district = item.getDistrict();
//                    temp.add(p);
//                }
//                dataCategories.addAll(temp);
//                adapter.notifyDataSetChanged();
//            }
//        }
    }

    @Override
    public void onGetListProjectsFailure() {
        runLoadMore = false;
        progressLoading.setVisibility(View.GONE);
    }

    @Override
    public void onConnectListProjectsFailure() {
        progressLoading.setVisibility(View.GONE);
    }

    @Override
    public void clickCategory(ItemCategory itemCategory) {
        dataCategories = sqlCategoriesV1.getItemCategoryListByParentIdAndGroup(
                itemCategory.getCategoryId(),
                itemCategory.getCategoryGroup() + 1);
        selectCategoryAdapter.setItemCategoryList(dataCategories);
        selectCategoryAdapter.notifyDataSetChanged();


        if (dataCategories.size() > 0) {
            if (dataCategories.get(0).getCategoryGroup() != 1 && !dataCategories.get(0).getCategoryId().startsWith("land")) {
                gridLayoutManager.setSpanCount(1);
            } else {
                gridLayoutManager.setSpanCount(2);
            }
            rvRoot.setLayoutManager(gridLayoutManager);
        } else {
            SelectCategoryActivity.paramsCategories.add(new CategoryRequest(
                    itemCategory.getCategoryId(),
                    itemCategory.getCategoryName(),
                    itemCategory.getCategoryGroup()));
            List<ItemCategory> itemCategoryList = sqlCategoriesV1.getListParentCategoryByParentId(itemCategory.getRankParentName());
            for (int i = 0; i < itemCategoryList.size(); i++) {
                SelectCategoryActivity.paramsCategories.add(new CategoryRequest(
                        itemCategoryList.get(i).getCategoryId(),
                        itemCategoryList.get(i).getCategoryName(),
                        itemCategoryList.get(i).getCategoryGroup()
                ));
            }
            BasicInfoFragment fragment = new BasicInfoFragment();
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            manager.popBackStack();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            transaction.replace(R.id.container_frame_created, fragment).commit();
            filePut.putRootCategoryId(itemCategory.getCategoryId());
        }

        SelectCategoryActivity.horizontalAdapter.addItemCategory(itemCategory);
        SelectCategoryActivity.horizontalAdapter.notifyDataSetChanged();
    }
}
