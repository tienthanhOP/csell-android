package csell.com.vn.csell.views.product.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.esafirm.imagepicker.features.ImagePicker;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FirebaseDBUtil;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.CategoryRequest;
import csell.com.vn.csell.models.ItemCategory;
import csell.com.vn.csell.models.ProductCountResponse;
import csell.com.vn.csell.models.ProductOwner;
import csell.com.vn.csell.sqlites.SQLCategoriesV1;
import csell.com.vn.csell.views.product.adapter.SelectGroupProductHorizontalAdapter;
import csell.com.vn.csell.views.product.fragment.ChooseImageUploadFragment;
import csell.com.vn.csell.views.product.fragment.InputEmptyPostFragment;
import csell.com.vn.csell.views.product.fragment.PrivateInfoProductFragment;
import csell.com.vn.csell.views.product.fragment.SelectRootCategoryFragment;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;

@SuppressLint("StaticFieldLeak")
public class SelectCategoryActivity extends AppCompatActivity implements SelectGroupProductHorizontalAdapter.OnClickParentListener {
    public static boolean selectProject = false;
    public static int level = 1;
    public static HashMap<String, Object> paramsProduct = new HashMap<>();
    public static HashMap<String, Object> paramsProductPrivate = new HashMap<>();//owner product PrivateInfoFragment
    public static HashMap<String, Object> paramsProperty = new HashMap<>();// attributes - MoreInfoFragment
    public static List<String> paramsImages = new ArrayList<>();
    public static List<CategoryRequest> paramsCategories = new ArrayList<>();
    public static boolean isAddProductFromProjectDetail = false;
    public static String currentFragment = null;
    public static ArrayList<String> lsCate;
    public static SelectGroupProductHorizontalAdapter horizontalAdapter;
    public TextView titleToolbar;
    public RecyclerView rvSelectCategory;
    public ProgressBar progressBarCreate;
    public List<ItemCategory> groupsData;
    public SelectRootCategoryFragment selectRootCategoryFragment = null;
    private FancyButton btnBackNavigation;
    private FancyButton btnSaveNavigation;
    private boolean isEmptyPost = false;
    private boolean isAddOwner = false;
    private ProductOwner owner = null;
    private FileSave filePut;
    private SQLCategoriesV1 sqlCategoriesV1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);
        Fabric.with(this, new Crashlytics());
        lsCate = new ArrayList<>();
        sqlCategoriesV1 = new SQLCategoriesV1(this);
        Intent intent = getIntent();
        if (intent != null) {
            isAddOwner = intent.getBooleanExtra(Constants.KEY_PASSING_OWNER_INFO, false);
            owner = (ProductOwner) intent.getSerializableExtra(Constants.KEY_INTENT_PASSING_OWNER);
            isAddProductFromProjectDetail = intent.getBooleanExtra(Constants.KEY_ADD_PRODUCT_FROM_PROJECT_DETAIL, false);
            isEmptyPost = intent.getBooleanExtra(Constants.POST_EMPTY_KEY, false);
        }

        initView();
        setupWindowAnimations();
        initEvent();

        if (isAddOwner) {
            replaceViewOwnerInfo();
        } else {
            filePut = new FileSave(this, Constants.PUT);
            paramsProduct = new HashMap<>();
            paramsProperty = new HashMap<>();
            DatabaseReference dbReference = FirebaseDBUtil.getDatebase().getReference();
            Utilities.PRODUCT_KEY = dbReference.push().getKey();

            selectRootCategoryFragment = new SelectRootCategoryFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.IS_EMTPY_POST, isEmptyPost);
            selectRootCategoryFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_frame_created, selectRootCategoryFragment)
                    .commit();
        }
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    private void initView() {
        btnBackNavigation = findViewById(R.id.btn_back_navigation);
        btnBackNavigation.setText(getString(R.string.title_back_vn));
        btnSaveNavigation = findViewById(R.id.btn_save_navigation);
        titleToolbar = findViewById(R.id.custom_TitleToolbar);
        btnSaveNavigation.setVisibility(View.GONE);
        updateTitleToolbar(getString(R.string.add_product));

        progressBarCreate = findViewById(R.id.progress_bar_create);
        progressBarCreate.setVisibility(View.GONE);
        rvSelectCategory = findViewById(R.id.rvSelectCategory);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        rvSelectCategory.setLayoutManager(layoutManager);
        groupsData = new ArrayList<>();
        horizontalAdapter = new SelectGroupProductHorizontalAdapter(this, groupsData, this);
        horizontalAdapter.addItemCategory(
                new ItemCategory(null, "Tất cả", 0, null, null, ""));
        rvSelectCategory.setAdapter(horizontalAdapter);
    }

    private void initEvent() {
        btnBackNavigation.setOnClickListener(v -> onBackPressed());
        if (isEmptyPost) {
            btnSaveNavigation.setVisibility(View.VISIBLE);
            btnSaveNavigation.setText(getString(R.string.skip));
            btnSaveNavigation.setOnClickListener(v -> {
                if (getSupportFragmentManager().findFragmentByTag("InputEmptyPostFragment") == null) {
                    InputEmptyPostFragment inputEmptyPostFragment = new InputEmptyPostFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_frame_created, inputEmptyPostFragment, "InputEmptyPostFragment")
                            .addToBackStack("InputEmptyPostFragment")
                            .commit();
                } else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_frame_created, getSupportFragmentManager()
                                    .findFragmentByTag("InputEmptyPostFragment"), "InputEmptyPostFragment")
                            .addToBackStack("InputEmptyPostFragment")
                            .commit();
                }
            });
        }
    }

    private void replaceViewOwnerInfo() {
        rvSelectCategory.setVisibility(View.GONE);
        PrivateInfoProductFragment fragment = new PrivateInfoProductFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_PASSING_OWNER_INFO, true);
        bundle.putSerializable(Constants.KEY_INTENT_PASSING_OWNER, owner);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_frame_created, fragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(ChooseImageUploadFragment.class.getSimpleName());
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }

        }
    }

//
//    @Override
//    public void onBackPressed() {
//        if (groupsData.size() == 2) {
//            btnSaveNavigation.setVisibility(View.GONE);
//        }
//        try {
//            if (currentFragment != null) {
//                switch (currentFragment) {
//                    case "SelectRootCategoryFragment":
//                        if (groupsData.size() <= 1) {
//                            finishAfterTransition();
//                        } else {
//                            selectRootCategoryFragment.backCategory(true);
//                            groupsData.remove(groupsData.size() - 1);
//                            horizontalAdapter.notifyDataSetChanged();
//                        }
//                        break;
//                    case "InputEmptyPostFragment":
//                        finishAfterTransition();
//                        break;
//                    default:
//                        super.onBackPressed();
//                        updateTitleToolbarBack();
//                        break;
//                }
//
//            }
//        } catch (Exception e) {
//            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
//            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
//            finishAfterTransition();
//        }
//
//    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            level = 1;
            filePut.putIsCreateProduct(false);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == Constants.PERMISSION_CONSTANT) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //do something
            } else {
                Toast.makeText(this, "Quyền truy cập bị từ chối", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void requestPermission() {

        int checkCameraPer = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int checkWiteExPer = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int checkReadExPer = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionGranted = PackageManager.PERMISSION_GRANTED;

        String[] permissions = new String[]{
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE};
        //check Android 6+
        if (Build.VERSION.SDK_INT >= 23) {
            //check permission granted
            if (checkCameraPer != permissionGranted
                    || checkWiteExPer != permissionGranted
                    || checkReadExPer != permissionGranted
            ) {
                //request Permissions
                ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSION_CONSTANT);

            }
        }
    }

    public void updateTitleToolbar(String title) {
        Utilities.lsTitleCreate.add(title);
        titleToolbar.setText(title);
    }

    private void updateTitleToolbarBack() {
        if (Utilities.lsTitleCreate.size() > 0) {
            if (Utilities.lsTitleCreate.size() == 1) {
                titleToolbar.setText(Utilities.lsTitleCreate.get(0));
            } else {
                Utilities.lsTitleCreate.remove(Utilities.lsTitleCreate.size() - 1);
                titleToolbar.setText(Utilities.lsTitleCreate.get(Utilities.lsTitleCreate.size() - 1));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            filePut.putIsCreateProduct(true);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            filePut.putIsCreateProduct(false);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public void addHeader(ProductCountResponse item) {
//        groupsData.add(item);
        horizontalAdapter.notifyItemInserted(groupsData.size() - 1);
        rvSelectCategory.scrollToPosition(groupsData.size() - 1);
    }

    public void removeHeader(int position) {
        if (position == 0) {
            btnSaveNavigation.setVisibility(View.GONE);
        }
        if (currentFragment != null) {
            int j = groupsData.size() - 1;
//            if (position == j) {
            if (position != j) {

//            } else {
                if (currentFragment.equals("SelectRootCategoryFragment")) {
                    Log.d("dataa", "removeHeader: " + position);
                    boolean check;
                    check = j <= position + 1;
                    while (j > position) {
                        groupsData.remove(groupsData.size() - 1);
                        selectRootCategoryFragment.backCategory(check);
                        j--;
                    }
                }
//                else {
//                    headerClick = true;
//                    onBackPressed();
//                    position++;
//                    groupsData.remove(groupsData.size() - 1);
//                    while (j > position) {
//                        groupsData.remove(groupsData.size() - 1);
//                        selectRootCategoryFragment.backCategory();
//                        j--;
//                    }
//                    headerClick = false;
            }
//            }
            horizontalAdapter.notifyDataSetChanged();
        }
    }

    public ProductCountResponse getHeader() {
        ProductCountResponse p = new ProductCountResponse();
        try {
//            p = groupsData.get(groupsData.size() - 1);
        } catch (Exception e) {
            return null;
        }
        return p;
    }

    @Override
    public void clickParent(ItemCategory itemCategory) {
        groupsData = sqlCategoriesV1.getItemCategoryListByParentIdAndGroup(
                itemCategory.getCategoryId(),
                itemCategory.getCategoryGroup() + 1);
        SelectRootCategoryFragment.selectCategoryAdapter.setItemCategoryList(groupsData);
        SelectRootCategoryFragment.selectCategoryAdapter.notifyDataSetChanged();
        if (groupsData.size() > 0) {
            if (groupsData.get(0).getCategoryGroup() != 1
                    && !groupsData.get(0).getCategoryId().startsWith("land")) {
                SelectRootCategoryFragment.gridLayoutManager.setSpanCount(1);
            } else {
                SelectRootCategoryFragment.gridLayoutManager.setSpanCount(2);
            }
            SelectRootCategoryFragment.rvRoot.setLayoutManager(SelectRootCategoryFragment.gridLayoutManager);

            SelectRootCategoryFragment fragment = new SelectRootCategoryFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            manager.popBackStack();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            transaction.replace(R.id.container_frame_created, fragment).commit();
        }

        horizontalAdapter.removeItemCategorySaveLast(itemCategory);
        horizontalAdapter.notifyDataSetChanged();
    }
}

