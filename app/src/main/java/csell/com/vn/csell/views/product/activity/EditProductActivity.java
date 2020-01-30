package csell.com.vn.csell.views.product.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.firebase.database.ServerValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.apis.JSONProductV1;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.commons.PushNotifications;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.EntityFirebase;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.NotiController;
import csell.com.vn.csell.controllers.UploadImageController;
import csell.com.vn.csell.libraries.compressors.Luban;
import csell.com.vn.csell.libraries.compressors.OnCompressListener;
import csell.com.vn.csell.models.CustomerRetro;
import csell.com.vn.csell.models.ImageSuffix;
import csell.com.vn.csell.models.ImageUpload;
import csell.com.vn.csell.models.PostTypeV1;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.models.ProductResponseModel;
import csell.com.vn.csell.models.ProductResponseV1;
import csell.com.vn.csell.models.Properties;
import csell.com.vn.csell.models.PropertyValue;
import csell.com.vn.csell.models.UploadImageRetro;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.mycustoms.SpacesItemDecoration;
import csell.com.vn.csell.sqlites.SQLCategories;
import csell.com.vn.csell.sqlites.SQLCustomers;
import csell.com.vn.csell.sqlites.SQLPostTypesV1;
import csell.com.vn.csell.sqlites.SQLProperties;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.product.adapter.ChooseImageAdapter;
import csell.com.vn.csell.views.product.adapter.CurrencyAdapter;
import csell.com.vn.csell.views.product.adapter.EditPropertiesAdapter;
import csell.com.vn.csell.views.product.adapter.PostTypeAdapter;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProductActivity extends AppCompatActivity implements NotiController.OnSendNotiListener, UploadImageController.OnUploadImagesListener {

    public static HashMap<String, Object> paramsProduct;
    public static HashMap<String, Object> paramsProperty;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout fromNotePrivateAndFriend;
    @SuppressLint("StaticFieldLeak")
    public static ImageView img_persion_1;
    @SuppressLint("StaticFieldLeak")
    public static ImageView img_persion_2;
    @SuppressLint("StaticFieldLeak")
    public static ImageView img_persion_3;
    @SuppressLint("StaticFieldLeak")
    public static TextView txtListFriendName;
    public static List<ImageSuffix> lsImagesDefault;
    public static boolean isSuccessUploadImage;
    public static boolean isSelectImages;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    public ChooseImageAdapter chooseImageAdapter;
    public ProgressBar progressBarLoading;
    public TextView txtCountSelectImage;
    public Product product = null;
    public TextView txtPost;
    ArrayList<ImageSuffix> imageSuffixes;
    private RecyclerView recyclerViewImageUpload;
    private FancyButton btnBackNavigation;
    private FancyButton btnSaveNavigation;
    private FancyButton btnDeleted;
    private TextView titleToolbar;
    private RecyclerView rvProperties;
    private EditPropertiesAdapter editPropertiesAdapter;
    private List<Properties> properties;
    private SQLProperties sqlProperties;
    private EditText edtProductName;
    private EditText edtPrice;
    private Spinner spnCurrency;
    private RelativeLayout layout_price;
    private EditText edtDescription;
    private TextView txtEditDescription;
    private ArrayList<String> listImageBase64;
    private List<String> listURLImagesAPI;
    private FileSave fileGet;
    private boolean isEdit = false;
    private boolean isReup = false;
    private int timeout = 0;
    private ScrollView scrollView;
    private RelativeLayout frameShare;
    private ProgressBar progressLoading;
    private LinearLayout fromLoadding;
    private RecyclerView rvPostType;
    private SQLCustomers sqlCustomers;
    private SQLPostTypesV1 sqlPostTypes;
    private List<PostTypeV1> lsPostType;
    private PostTypeAdapter postTypeAdapter;
    private TextView tvEditNoteReup;
    private SQLCategories sqlCategories;
    private Dialog dialogDelete;
    private int position = -1;
    private String price = "";
    private String currency;
    private LinearLayout layoutEditImage;
    private ProgressBar progressBar;
    private RelativeLayout layoutContent;
    private BaseActivityTransition baseActivityTransition;
    private NotiController notiController;
    private UploadImageController uploadImageController;

    public static void updateViewNoteFriend(List<UserRetro> lsFriends) {
        fromNotePrivateAndFriend.setVisibility(View.VISIBLE);
//        checkAddNotePrivate(p)
        if (lsFriends.size() >= 3) {
            img_persion_1.setVisibility(View.VISIBLE);
            img_persion_2.setVisibility(View.VISIBLE);
            img_persion_3.setVisibility(View.VISIBLE);
            GlideApp.with(mContext)
                    .load(lsFriends.get(0).getAvatar() == null ? R.drawable.ic_logo : lsFriends.get(0).getAvatar())
                    .error(R.drawable.ic_logo)
                    .placeholder(R.drawable.ic_logo)
                    .into(img_persion_1);
            GlideApp.with(mContext)
                    .load(lsFriends.get(1).getAvatar() == null ? R.drawable.ic_logo : lsFriends.get(1).getAvatar())
                    .error(R.drawable.ic_logo)
                    .placeholder(R.drawable.ic_logo)
                    .into(img_persion_2);
            GlideApp.with(mContext)
                    .load(lsFriends.get(2).getAvatar() == null ? R.drawable.ic_logo : lsFriends.get(2).getAvatar())
                    .error(R.drawable.ic_logo)
                    .placeholder(R.drawable.ic_logo)
                    .into(img_persion_3);
            txtListFriendName.setText(lsFriends.get(0).getDisplayname() + " " + MainActivity.mainContext.getString(R.string.text_and) + " " + (lsFriends.size() - 1)
                    + " " + MainActivity.mainContext.getString(R.string.text_with_persons_orther));
        } else if (lsFriends.size() == 2) {
            img_persion_1.setVisibility(View.VISIBLE);
            img_persion_2.setVisibility(View.VISIBLE);
            img_persion_3.setVisibility(View.GONE);
            GlideApp.with(mContext)
                    .load(lsFriends.get(0).getAvatar() == null ? R.drawable.ic_logo : lsFriends.get(0).getAvatar())
                    .error(R.drawable.ic_logo)
                    .placeholder(R.drawable.ic_logo)
                    .into(img_persion_1);
            GlideApp.with(mContext)
                    .load(lsFriends.get(1).getAvatar() == null ? R.drawable.ic_logo : lsFriends.get(1).getAvatar())
                    .error(R.drawable.ic_logo)
                    .placeholder(R.drawable.ic_logo)
                    .into(img_persion_2);

            txtListFriendName.setText(lsFriends.get(0).getDisplayname() + " " + MainActivity.mainContext.getString(R.string.text_and) + " " + (lsFriends.size() - 1)
                    + " " + MainActivity.mainContext.getString(R.string.text_with_persons_orther));
        } else if (lsFriends.size() == 1) {
            img_persion_1.setVisibility(View.VISIBLE);
            img_persion_2.setVisibility(View.GONE);
            img_persion_3.setVisibility(View.GONE);
            GlideApp.with(mContext)
                    .load(lsFriends.get(0).getAvatar() == null ? R.drawable.ic_logo : lsFriends.get(0).getAvatar())
                    .error(R.drawable.ic_logo)
                    .placeholder(R.drawable.ic_logo)
                    .into(img_persion_1);

            txtListFriendName.setText(lsFriends.get(0).getDisplayname() + " ");
        } else {
            fromNotePrivateAndFriend.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        mContext = this;
        Fabric.with(this, new Crashlytics());
        funFindViewById();
        setupWindowAnimations();
        initView();
        initEvent();
        getDataToEdit();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    private void hideProgressBar(boolean b) {
        if (b) {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                progressBar.setVisibility(View.GONE);
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                layoutContent.startAnimation(animation);
                layoutContent.setVisibility(View.VISIBLE);
            }, 500);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            layoutContent.setVisibility(View.GONE);
        }
    }

    private void funFindViewById() {
        uploadImageController = new UploadImageController(this);
        notiController = new NotiController(this);
        baseActivityTransition = new BaseActivityTransition(this);

        progressBar = findViewById(R.id.progress_bar);
        layoutContent = findViewById(R.id.layout_content);
        hideProgressBar(false);

        layoutEditImage = findViewById(R.id.form_edit_image);
        recyclerViewImageUpload = findViewById(R.id.recyclerViewImageUpload);
        txtPost = findViewById(R.id.txtPost);
        btnBackNavigation = findViewById(R.id.btn_back_navigation);
        btnBackNavigation.setText(getString(R.string.title_back_vn));
        btnSaveNavigation = findViewById(R.id.btn_save_navigation);
        btnDeleted = findViewById(R.id.btnDeleted);
        rvProperties = findViewById(R.id.rv_properties);
        edtPrice = findViewById(R.id.edtPrice);
        spnCurrency = findViewById(R.id.spnCurrency);
        layout_price = findViewById(R.id.layout_price);
        txtCountSelectImage = findViewById(R.id.txtCountSelectImage);
        edtProductName = findViewById(R.id.edtProductName);
        edtDescription = findViewById(R.id.edtDescription);
        txtEditDescription = findViewById(R.id.txtEditDescription);
        titleToolbar = findViewById(R.id.custom_TitleToolbar);
        progressBarLoading = findViewById(R.id.progress_bar_loading);
        scrollView = findViewById(R.id.scrollView);
        frameShare = findViewById(R.id.frame_share);
        fromNotePrivateAndFriend = findViewById(R.id.fromNotePrivateAndFriend);
        img_persion_1 = findViewById(R.id.img_persion_1);
        img_persion_2 = findViewById(R.id.img_persion_2);
        img_persion_3 = findViewById(R.id.img_persion_3);
        txtListFriendName = findViewById(R.id.txtListFriendName);
        rvPostType = findViewById(R.id.rv_postType);
        progressLoading = findViewById(R.id.progress_loading);
        fromLoadding = findViewById(R.id.from_loadding);
        tvEditNoteReup = findViewById(R.id.tv_edit_note_private);
    }

    private void getDataToEdit() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try {
                product = (Product) bundle.getSerializable(Constants.KEY_PASSINGDATA_PRODUCT_OBJ);
                position = bundle.getInt(Constants.TEMP_POSITION, -1);

                if (!product.getCatid().startsWith(Utilities.SIM)) {
                    layoutEditImage.setVisibility(View.VISIBLE);
                } else {
                    layoutEditImage.setVisibility(View.GONE);
                }

                if (product.getReup() != null) {
                    isReup = product.getReup();
                }

                isEdit = bundle.getBoolean(Constants.KEY_PASSINGDATA_EDIT_PRODUCT, false);
                loadPrice();
                if (isEdit) {
                    titleToolbar.setText(getString(R.string.update_information));
                    btnSaveNavigation.setText(getString(R.string.save));
                    setValue(product, isEdit);
                } else {
                    titleToolbar.setText(getString(R.string.reup_product));
                    btnDeleted.setVisibility(View.GONE);
                    btnSaveNavigation.setText(getString(R.string.text_continue_edit));
                    setValue(product, isEdit);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        }
    }

    private void loadPrice() {

        try {
            int index = 0;

            ArrayList<PropertyValue> lsCurrency = Utilities.CURRENCY(this);
            for (int i = 0; i < lsCurrency.size(); i++) {
                if (product.getCurrency() != null) {
                    if (lsCurrency.get(i).value.toLowerCase().equals(product.getCurrency().toLowerCase())) {
                        index = i;
                        break;
                    }
                } else {
                    index = 0; // 0: VND
                    break;
                }
            }

            CurrencyAdapter adapter = new CurrencyAdapter(this, lsCurrency);
            spnCurrency.setAdapter(adapter);
            spnCurrency.setSelection(index);

            spnCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        paramsProduct.put(EntityAPI.FIELD_CURRENCY, lsCurrency.get(position).value);
                        currency = lsCurrency.get(position).value;
                        product.setCurrency(currency);
                    } catch (Exception e) {
                        Log.e("TEST_", e.toString());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

    }

    private void setValue(Product product, boolean isEdit) {

        try {
            if (product.getImages() == null || product.getImages().size() == 0) {
                product.setImages(lsImagesDefault);
            }

            lsImagesDefault.addAll(product.getImages());
            txtCountSelectImage.setText(getString(R.string.text_number_of_images) + " " + lsImagesDefault.size() + "/10");

            chooseImageAdapter = new ChooseImageAdapter(EditProductActivity.this, lsImagesDefault, null, false);

            recyclerViewImageUpload.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerViewImageUpload.addItemDecoration(new SpacesItemDecoration(3, 0, false));
            recyclerViewImageUpload.setAdapter(chooseImageAdapter);

            if (!TextUtils.isEmpty(product.getTitle())) {
                edtProductName.setText(product.getTitle());
            }

            if (!TextUtils.isEmpty(product.getDescription())) {
                edtDescription.setText(product.getDescription());
            }

            if (product.getPrice() != null) {
                if (product.getPrice() == 0) {
                    edtPrice.setText(null);
                } else {
                    edtPrice.setText(product.getPrice() + "");
                    edtPrice.setSelection(product.getPrice().toString().length());
                }
            } else {
                edtPrice.setText(null);
                edtPrice.setHint(getString(R.string.enter_price_or_leave_blank));
            }

            if (product.getType() != null) {
                if (product.getType() == 2) {
                    layout_price.setVisibility(View.GONE);
                }
            }


            if (isEdit) {
                if (!product.getCatid().startsWith(Utilities.SIM_LIST_MONTH)) {
                    layout_price.setVisibility(View.VISIBLE);
                    properties = sqlProperties.getPropertiesByCate(product.getCatid().split("_")[0]);
                    editPropertiesAdapter = new EditPropertiesAdapter(EditProductActivity.this, properties, product, isEdit);
                    rvProperties.setAdapter(editPropertiesAdapter);
                } else {
                    layout_price.setVisibility(View.GONE);
                }
            }
            hideProgressBar(true);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }

    }

    private void initView() {
        lsImagesDefault = new ArrayList<>();
        imageSuffixes = new ArrayList<>();
        properties = new ArrayList<>();
        fileGet = new FileSave(this, Constants.GET);
        listImageBase64 = new ArrayList<>();
        listURLImagesAPI = new ArrayList<>();
        paramsProduct = new HashMap<>();
        paramsProperty = new HashMap<>();
        sqlProperties = new SQLProperties(this);

        btnSaveNavigation.setText(getString(R.string.save));
        btnSaveNavigation.setVisibility(View.VISIBLE);
        titleToolbar.setText(getString(R.string.update_information));
        rvProperties.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvProperties.setLayoutManager(mLayoutManager);
        frameShare.setVisibility(View.GONE);
        rvPostType.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(this);
        mLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        rvPostType.setLayoutManager(mLayoutManager2);
        sqlPostTypes = new SQLPostTypesV1(this);
        sqlCategories = new SQLCategories(this);
        sqlCustomers = new SQLCustomers(this);
        dialogDelete = new Dialog(this);
        loadViewShared();
    }

    private void loadViewShared() {
        new Handler().postDelayed(() -> {
            lsPostType = new ArrayList<>();
            lsPostType = sqlPostTypes.getAllPostType();
            postTypeAdapter = new PostTypeAdapter(this, lsPostType, true);
            rvPostType.setAdapter(postTypeAdapter);
        }, 1000);

    }

    private void deleteProductConfirm() {
        try {

            dialogDelete.setContentView(R.layout.dialog_delete_product);

            FancyButton btnCancel = dialogDelete.findViewById(R.id.btnCancel);
            FancyButton btnConfirm = dialogDelete.findViewById(R.id.btnConfirm);
            ProgressBar progressLoading = dialogDelete.findViewById(R.id.progress_loading);
            btnCancel.setOnClickListener(v -> dialogDelete.dismiss());

            btnConfirm.setOnClickListener(v -> removeProduct(progressLoading, btnConfirm));
            //custom position dialog
            Window dialogWindow = dialogDelete.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            dialogWindow.setGravity(Gravity.CENTER);
            dialogWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

//            lp.windowAnimations = R.style.DialogCreateAnimation;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT; // Width
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // Height


            dialogWindow.setAttributes(lp);

            dialogDelete.show();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void removeProduct(ProgressBar progressBar, FancyButton btnConfirm) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            btnConfirm.setEnabled(false);
            PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
            if (postAPI != null) {
                Call<HashMap<String, Object>> jsonResponseCall = postAPI.removeProduct(fileGet.getProductIdCurrentSelect());
                jsonResponseCall.enqueue(new Callback<HashMap<String, Object>>() {
                    @Override
                    public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
//                        if (response.isSuccessful()) {
//                            HashMap<String, Object> result = response.body();
//                            if (result != null) {
//                                if (result.getCode() == 0) {
//                                    Intent intent = new Intent();
//                                    intent.putExtra(Constants.TEMP_POSITION, position);
//                                    intent.putExtra(Constants.TEMP_REMOVE_PRODUCT, true);
//                                    setResult(Constants.EDIT_PRODUCT_RESULT, intent);
//                                    finishAfterTransition();
//                                }
//                                String msg = response.body().getMessage();
//                                if (!TextUtils.isEmpty(msg)) {
//                                    Toast.makeText(EditProductActivity.this, msg, Toast.LENGTH_LONG).show();
//                                }
//                            } else {
//                                if (response.errorBody() != null) {
//                                    try {
//                                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
//                                        String msg = (String) jsonObject.get(Constants.ERROR);
//                                        showProgress(false);
//                                        if (!TextUtils.isEmpty(msg)) {
//                                            Toast.makeText(EditProductActivity.this, msg, Toast.LENGTH_LONG).show();
//                                        }
//                                    } catch (JSONException | IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                    Toast.makeText(EditProductActivity.this, result.getMessage() + "", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                        } else {
//                            Toast.makeText(EditProductActivity.this, "Thử lại", Toast.LENGTH_SHORT).show();
//                        }
//
//                        progressBar.setVisibility(View.GONE);
//                        btnConfirm.setEnabled(true);
//                        jsonResponseCall.cancel();
//                        dialogDelete.dismiss();
                    }

                    @Override
                    public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                        jsonResponseCall.cancel();
                        progressBar.setVisibility(View.GONE);
                        btnConfirm.setEnabled(true);
                        Crashlytics.logException(t);
                        Toast.makeText(EditProductActivity.this, getString(R.string.text_error_unknown), Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            btnConfirm.setEnabled(true);
            Crashlytics.logException(e);
        }
    }

    private void initEvent() {

        btnDeleted.setOnClickListener(v -> deleteProductConfirm());

        txtPost.setOnClickListener(v -> {

            if (!isSelectImages) {
                btnSaveNavigation.setVisibility(View.GONE);
                if (frameShare.getVisibility() == View.VISIBLE) {
                    scrollView.setVisibility(View.GONE);
                    showProgress(true);
                    try {
                        if (isReup) {
                            Toast.makeText(this, "Bạn đã reup sản phẩm này rồi", Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        } else {
                            reupProduct();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }
                } else {
                    scrollView.setVisibility(View.GONE);
                    frameShare.setVisibility(View.VISIBLE);
                }
            } else {
                if (isSuccessUploadImage) {
                    btnSaveNavigation.setVisibility(View.GONE);
                    if (frameShare.getVisibility() == View.VISIBLE) {
                        scrollView.setVisibility(View.GONE);
                        showProgress(true);
                        try {
                            if (isReup) {
                                Toast.makeText(this, "Bạn đã reup sản phẩm này rồi", Toast.LENGTH_SHORT).show();
                                showProgress(false);
                            } else {
                                reupProduct();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }
                    } else {
                        scrollView.setVisibility(View.GONE);
                        frameShare.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.loading_image), Toast.LENGTH_SHORT).show();
                }
            }


        });

        btnSaveNavigation.setOnClickListener(v -> {

            if (!isSelectImages) {
                if (isEdit) {
                    btnSaveNavigation.setVisibility(View.VISIBLE);
                    String productName = edtProductName.getText().toString();
                    String description = edtDescription.getText().toString();
                    if (!TextUtils.isEmpty(edtPrice.getText().toString())) {
                        price = edtPrice.getText().toString();
                    }

                    if (TextUtils.isEmpty(productName.trim())) {
                        edtProductName.setError(getString(R.string.text_error_empty));
                        return;
                    }

                    if (TextUtils.isEmpty(description.trim())) {
                        edtDescription.setError(getString(R.string.text_error_empty));
                        return;
                    }

                    showProgress(true);
                    updateProduct();

                } else {
                    btnSaveNavigation.setVisibility(View.GONE);
                    Utilities.hideSoftKeyboard(EditProductActivity.this);
                    frameShare.setVisibility(View.VISIBLE);
                }
            } else {
                if (isSuccessUploadImage) {
                    if (isEdit) {
                        btnSaveNavigation.setVisibility(View.VISIBLE);
                        String productName = edtProductName.getText().toString();
                        String description = edtDescription.getText().toString();
                        if (!TextUtils.isEmpty(edtPrice.getText().toString())) {
                            price = edtPrice.getText().toString();
                        }

                        if (TextUtils.isEmpty(productName.trim())) {
                            edtProductName.setError(getString(R.string.text_error_empty));
                            return;
                        }

                        if (TextUtils.isEmpty(description.trim())) {
                            edtDescription.setError(getString(R.string.text_error_empty));
                            return;
                        }

                        showProgress(true);
                        updateProduct();

                    } else {
                        btnSaveNavigation.setVisibility(View.GONE);
                        Utilities.hideSoftKeyboard(EditProductActivity.this);
                        frameShare.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.loading_image), Toast.LENGTH_SHORT).show();
                }
            }

        });

        btnBackNavigation.setOnClickListener(v -> {
            onBackPressed();
            btnSaveNavigation.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);
        });

        edtProductName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    paramsProduct.put(EntityAPI.FIELD_TITLE, s.toString());
                }
            }
        });

        edtPrice.addTextChangedListener(onTextChangedListener());

        txtEditDescription.setOnClickListener(v -> {
            Intent intent = new Intent(this, InputDescriptionActivity.class);
            intent.putExtra(Constants.KEY_DESCRIPTION_VALUE, edtDescription.getText().toString());
            baseActivityTransition.transitionTo(intent, Constants.KEY_DESCRIPTION);
        });

        tvEditNoteReup.setOnClickListener(v -> {
            Intent edit = new Intent(this, ShareNotePrivateActivity.class);
            edit.putExtra(Constants.KEY_PASSINGDATA_NOTE_PRIVATE, Utilities.contentPrivateNote);
            edit.putExtra(Constants.KEY_PASSINGDATA_LIST_FRIEND_SHARE_NOTE, Utilities.lsFriendsNotePrivate);
            edit.putExtra(Constants.IS_EDIT_PRIVATE_NOTE_REUP_PRODUCT, true);
            baseActivityTransition.transitionTo(edit, 0);
        });
    }

    private void selectImagePicker() {
        if (lsImagesDefault.size() != 10) {
            ImagePicker.create(this)
                    .folderMode(true) // folder mode (false by default)
                    .toolbarFolderTitle("Folder") // folder selection title
                    .toolbarImageTitle("Tap to select") // image selection title
                    .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
                    .multi() // multi mode (default mode)
                    .limit(10 - lsImagesDefault.size()) // max images can be selected (99 by default)
                    .showCamera(true) // show camera or not (true by default)
                    .imageDirectory("csell") // directory name for captured image  ("Camera" folder by default)
                    .theme(R.style.ImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
                    .enableLog(false) // disabling log
                    .start(); // start image picker activity with request code
        } else {
            Toast.makeText(this, getString(R.string.text_error_enogh_image), Toast.LENGTH_LONG).show();
        }

    }

    private void updateProduct() {
        try {
            PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
            if (postAPI != null) {
                // item id
                EditProductActivity.paramsProduct.clear();
                EditProductActivity.paramsProduct.put(EntityAPI.FIELD_ITEM_ID, product.getItemid());
                List<ImageSuffix> listImage = new ArrayList<>();
                if (lsImagesDefault != null) {
                    listImage = lsImagesDefault;
                }
                EditProductActivity.paramsProduct.put(EntityAPI.FIELD_IMAGES.toLowerCase(), listImage);
                EditProductActivity.paramsProduct.put(EntityAPI.FIELD_CURRENCY, currency);
                EditProductActivity.paramsProduct.put(EntityAPI.FIELD_PROPERTIES, EditProductActivity.paramsProperty);
                EditProductActivity.paramsProduct.put(EntityAPI.FIELD_DESCRIPTION.toLowerCase(), edtDescription.getText().toString());
                EditProductActivity.paramsProduct.put(EntityAPI.FIELD_TITLE, edtProductName.getText().toString().trim());
                if (!TextUtils.isEmpty(price)) {
                    try {
                        Long longval;
                        if (price.contains(",")) {
                            price = price.replaceAll(",", "");
                        }

                        longval = Long.parseLong(price);
                        EditProductActivity.paramsProduct.put(EntityAPI.FIELD_PRICE, longval);
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                    }
                }

//                Call<JSONResponse<List<ProductResponseModel>>> jsonResponseCall = postAPI.updateProduct(product.getItemid(), EditProductActivity.paramsProduct);
//                jsonResponseCall.enqueue(new Callback<JSONResponse<List<ProductResponseModel>>>() {
//                    @Override
//                    public void onResponse(Call<JSONResponse<List<ProductResponseModel>>> call, Response<JSONResponse<List<ProductResponseModel>>> response) {
//                        if (response.isSuccessful()) {
//                            if (response.body().getSuccess() != null) {
//                                if (response.body().getSuccess()) {
//                                    Toast.makeText(EditProductActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                                    setResult(Constants.EDIT_PRODUCT_RESULT);
//                                    uploadImage();
//                                } else {
//                                    Utilities.refreshToken(MainActivity.mainContext, response.body().getMessage() + "");
//                                    showProgress(false);
//                                    Toast.makeText(EditProductActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                            String msg = response.body().getError();
//                            if (!TextUtils.isEmpty(msg)) {
//                                Toast.makeText(EditProductActivity.this, msg, Toast.LENGTH_LONG).show();
//                            }
//                        } else {
//                            if (response.errorBody() != null) {
//                                try {
//                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
//                                    String msg = (String) jsonObject.get(Constants.ERROR);
//                                    showProgress(false);
//                                    if (!TextUtils.isEmpty(msg)) {
//                                        Toast.makeText(EditProductActivity.this, msg, Toast.LENGTH_LONG).show();
//                                    }
//                                } catch (JSONException | IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//
//                        jsonResponseCall.cancel();
//                    }
//
//                    @Override
//                    public void onFailure(Call<JSONResponse<List<ProductResponseModel>>> call, Throwable t) {
//                        jsonResponseCall.cancel();
//                        showProgress(false);
//                        if (timeout == 2) {
//                            timeout = 0;
//                            Toast.makeText(EditProductActivity.this, R.string.failure_connect_server, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

            }


        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void reupProduct() {

        try {
            EditProductActivity.paramsProduct.put(EntityAPI.FIELD_CAT_ID, product.getCatid());

            EditProductActivity.paramsProduct.put(EntityAPI.FIELD_CAT_NAME, product.getCatName());
            String[] items = product.getCatid().split("_");
            String root = sqlCategories.getCategoryByIdAndLevel(items[0], 1).category_name;
            EditProductActivity.paramsProduct.put(EntityAPI.FIELD_ROOT_CAT_NAME, root);

            String sub = sqlCategories.getCategoryByIdAndLevel(items[1], 2).category_name;
            EditProductActivity.paramsProduct.put(EntityAPI.FIELD_SUB_CAT_NAME, sub);

            if (product.getCatid().startsWith(Utilities.LAND_PROJECT)) {
                EditProductActivity.paramsProduct.put(EntityAPI.FIELD_PROJECT_ID, product.getProjectid());
                EditProductActivity.paramsProduct.put(EntityAPI.FIELD_PROJECT_NAME, product.getProjectName());
            }

            EditProductActivity.paramsProduct.put(EntityAPI.FIELD_IMAGES, product.getImages());
            EditProductActivity.paramsProduct.put(EntityFirebase.FieldOriginal_item, product.getItemid());
            EditProductActivity.paramsProduct.put(EntityFirebase.FieldReupUserId, product.getUserInfo().getUid());
            EditProductActivity.paramsProduct.put(EntityAPI.FIELD_CURRENCY, product.getCurrency());
            EditProductActivity.paramsProduct.put(EntityAPI.FIELD_DISTRICT.toLowerCase(), product.getDistrict());
            EditProductActivity.paramsProduct.put(EntityAPI.FIELD_CITY.toLowerCase(), product.getCity());
            EditProductActivity.paramsProduct.put(EntityAPI.FIELD_LOCATION.toLowerCase(), product.getLocation());
            EditProductActivity.paramsProduct.put(EntityAPI.FIELD_PROPERTIES, product.getProperties());
            EditProductActivity.paramsProduct.put(EntityAPI.FIELD_DESCRIPTION.toLowerCase(), edtDescription.getText().toString());

            if (Utilities.lsFriendsNotePrivate.size() > 0) {
                EditProductActivity.paramsProduct.put(EntityAPI.FIELD_NOTE_PRIVATE, Utilities.contentPrivateNote);
                EditProductActivity.paramsProduct.put(EntityAPI.FIELD_CAN_SEE_NOTE_PRIVATE, Utilities.lsFriendsNotePrivate);
            }

            HashMap<String, Object> ownerMap = new HashMap<>();

            ownerMap.put(EntityAPI.FIELD_OWNER_ORIGINAL_PRICE, product.getPrice());
            ownerMap.put(EntityAPI.FIELD_CURRENCY, product.getCurrency());

            CustomerRetro customerRetro = sqlCustomers.getCustomerById(product.getUserInfo().getUid());
            if (customerRetro != null) {
                ownerMap.put(EntityAPI.FIELD_OWNER_ID, product.getUserInfo().getUid());
                EditProductActivity.paramsProduct.put(EntityAPI.FIELD_OWNER_INFO, ownerMap);
                reupPostAPI();
            } else {
                CustomerRetro customer = new CustomerRetro();

                if (!TextUtils.isEmpty(product.getUserInfo().getDisplayname())) {
                    String newName = Utilities.upperFirstLetter(product.getUserInfo().getDisplayname());
                    customer.setName(newName);
                }

                List<String> phones = new ArrayList<>();
                if (product.getUserInfo().getPhone() != null) {
                    phones.add(product.getUserInfo().getPhone());
                }
                customer.setPhone(phones.size() != 0 ? phones : null);

                List<String> emails = new ArrayList<>();
                if (product.getUserInfo().getEmail() != null) {
                    emails.add(product.getUserInfo().getEmail());
                }
                customer.setEmail(emails.size() != 0 ? emails : null);
                customer.isAdded = null;
                customer.isSelectedGroup = null;
                customer.setCustId(null);
                reupPostAPI();
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void reupPostAPI() {
        PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        Call<JSONResponse<ProductResponseModel>> jsonResponseCall = postAPI.reupProduct(EditProductActivity.paramsProduct);
        jsonResponseCall.enqueue(new Callback<JSONResponse<ProductResponseModel>>() {
            @Override
            public void onResponse(Call<JSONResponse<ProductResponseModel>> call, Response<JSONResponse<ProductResponseModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() != null) {
                        if (response.body().getSuccess()) {
                            //send noti
                            if (Utilities.lsFriendsNotePrivate.size() > 0) {
                                sendNotiShareNotePrivate(response.body().getData());
                                checkAddNotePrivate(response.body().getData().getItemid(), EditProductActivity.paramsProduct.get(EntityAPI.FIELD_TITLE).toString());
                            }
                            Toast.makeText(EditProductActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            product.setUserInfo(new UserRetro(fileGet.getUserId(), fileGet.getDisplayName(), fileGet.getUserAvatar()));
                            product.setImage(product.getImages().get(0));

                            product.setTitle(paramsProduct.get(EntityAPI.FIELD_TITLE).toString() + "");
                            product.setDescription(paramsProduct.get(EntityAPI.FIELD_DESCRIPTION).toString() + "");
                            try {
                                product.setPrivacyType((Integer) paramsProduct.get(EntityAPI.FIELD_PRIVACY_TYPE));
                                product.setPrice((Long) paramsProduct.get(EntityAPI.FIELD_PRICE));
                            } catch (Exception ignored) {

                            }
                            intent.putExtra(Constants.KEY_PASSINGDATA_REUP_PRODUCT, product);
                            setResult(Constants.REUP_PRODUCT_RESULT);
                            onBackPressed();
                        } else {
//                                Utilities.refreshToken(MainActivity.mainContext, response.body().getMessage() + "");
                            showProgress(false);
                            Toast.makeText(EditProductActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    String msg = response.body().getError();
                    if (!TextUtils.isEmpty(msg)) {
                        Toast.makeText(EditProductActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = (String) jsonObject.get(Constants.ERROR);
                            showProgress(false);
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(EditProductActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                showProgress(false);
                jsonResponseCall.cancel();
            }

            @Override
            public void onFailure(Call<JSONResponse<ProductResponseModel>> call, Throwable t) {
                showProgress(false);
                jsonResponseCall.cancel();
                if (BuildConfig.DEBUG) Log.d(getClass().getSimpleName(), t.toString());
                if (timeout == 2) {
                    timeout = 0;
                    Toast.makeText(EditProductActivity.this, "Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                } else {
                    timeout++;
                    reupProduct();
                }
            }
        });
    }

    public void checkAddNotePrivate(String productId, String productName) {
        if (Utilities.lsFriendsNotePrivate.size() != 0) {
            PushNotifications push = new PushNotifications(MainActivity.mainContext);
            for (UserRetro item : Utilities.lsFriendsNotePrivate) {
                HashMap<String, Object> data = new HashMap<>();
                data.put(EntityAPI.FIELD_NOTIFICATION_TITLE, "Share Note Private");
                data.put(EntityAPI.FIELD_NOTIFICATION_BODY, fileGet.getDisplayName() + " "
                        + MainActivity.mainContext.getString(R.string.text_body_shared_note_private) + " "
                        + productName);
                data.put(EntityAPI.FIELD_NOTIFICATION_TARGET_NAME, productName);
                data.put(EntityAPI.FIELD_NOTIFICATION_ACTION, Utilities.ACTION_NOTE_PRIVATE);
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

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            try {
                isSelectImages = true;
                listImageBase64.clear();
                ArrayList<Image> images;
                images = (ArrayList<Image>) ImagePicker.getImages(data);

                new resizeImage(images).execute();


            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }
        } else if (resultCode == Constants.KEY_DESCRIPTION) {
            if (data != null) {
                String des = data.getStringExtra(Constants.KEY_DESCRIPTION_VALUE);
                edtDescription.setText(des + "");
            }
        }

    }

    public void uploadImageGetURL(ArrayList<String> listImageBase) {
        try {

            List<ImageUpload> uploadList = new ArrayList<>();
            for (int i = 0; i < listImageBase.size(); i++) {
                uploadList.add(new ImageUpload(fileGet.getUserId() + System.currentTimeMillis() + i + "", listImageBase.get(i)));
            }
            progressBarLoading.setVisibility(View.VISIBLE);

            UploadImageRetro imageRetro = new UploadImageRetro();
            imageRetro.setType(2);
            imageRetro.setImages(uploadList);

            uploadImage(imageRetro);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            progressBarLoading.setVisibility(View.GONE);
        }
    }


    @SuppressLint("StaticFieldLeak")
    private void uploadImage() {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
                if (postAPI != null) {
                    HashMap<String, Object> imgMap = new HashMap<>();
                    imgMap.put(EntityAPI.FIELD_ITEM_ID, product.getItemid());
                    imgMap.put(EntityAPI.FIELD_IMAGES, lsImagesDefault);
                    Call<JSONResponse<List<ProductResponseModel>>> jsonResponseCall = postAPI.updateImages(imgMap);
                    jsonResponseCall.enqueue(new Callback<JSONResponse<List<ProductResponseModel>>>() {
                        @Override
                        public void onResponse(Call<JSONResponse<List<ProductResponseModel>>> call, Response<JSONResponse<List<ProductResponseModel>>> response) {
                            if (response.isSuccessful()) {
                                JSONResponse<List<ProductResponseModel>> result = response.body();
                                if (result.getSuccess() != null) {
                                    if (!result.getSuccess()) {
                                        Utilities.refreshToken(EditProductActivity.this, result.getMessage().toLowerCase() + "");
                                    } else {
                                        showProgress(false);
                                        List<String> listPath = new ArrayList<>();
                                        for (int i = 0; i < lsImagesDefault.size(); i++) {
                                            listPath.add(lsImagesDefault.get(i).getPath());
                                        }
                                        Intent intent = new Intent();
                                        intent.putStringArrayListExtra(Constants.IMAGE_PRODUCT_LIST, (ArrayList<String>) listPath);
                                        intent.setAction(Constants.ACTION_UPDATE_IMAGE);
                                        sendBroadcast(intent);
                                        onBackPressed();
                                    }

                                    String msg = response.body().getError();
                                    if (!TextUtils.isEmpty(msg)) {
                                        Toast.makeText(EditProductActivity.this, msg, Toast.LENGTH_LONG).show();
                                    }
                                }

                            } else {
                                if (response.errorBody() != null) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                        String msg = (String) jsonObject.get(Constants.ERROR);
                                        showProgress(false);
                                        if (!TextUtils.isEmpty(msg)) {
                                            Toast.makeText(EditProductActivity.this, msg, Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JSONResponse<List<ProductResponseModel>>> call, Throwable t) {
                            Toast.makeText(EditProductActivity.this, R.string.failure_connect_server, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return null;
            }
        }.execute();

    }

    @Override
    public void onBackPressed() {
        if (frameShare.getVisibility() == View.VISIBLE) {
            scrollView.setVisibility(View.VISIBLE);
            frameShare.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
            finishAfterTransition();
        }

    }

    private void showProgress(boolean check) {
        if (check) {
            progressLoading.setVisibility(View.VISIBLE);
            frameShare.setEnabled(false);
            frameShare.setAlpha(0.5f);
            scrollView.setEnabled(false);
            scrollView.setVisibility(View.GONE);
            scrollView.setAlpha(0.5f);
        } else {
            progressLoading.setVisibility(View.GONE);
            frameShare.setEnabled(true);
            frameShare.setAlpha(1);
            scrollView.setVisibility(View.VISIBLE);
            scrollView.setEnabled(true);
            scrollView.setAlpha(1);
        }
    }

    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) return;
                edtPrice.removeTextChangedListener(this);
                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }

                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);
                    paramsProduct.put(EntityAPI.FIELD_PRICE, longval);
                    edtPrice.setText(formattedString);
                    edtPrice.setSelection(edtPrice.getText().length());

                } catch (NumberFormatException nfe) {
                    edtPrice.setText(s.toString());
                    edtPrice.setSelection(edtPrice.getText().length());
                }

                edtPrice.addTextChangedListener(this);
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fromNotePrivateAndFriend = null;
        paramsProduct = null;
    }

    private void sendNotiShareNotePrivate(ProductResponseModel data) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put(EntityAPI.FIELD_TYPE_SEND_EMAIL, 1);
            map.put(EntityAPI.FIELD_CONTENT, "đã chia sẻ nội dung riêng tư cho bạn từ " + data.getTitle());
            String keyNoti = data.getItemid() + ":" + fileGet.getUserId();
            map.put(EntityAPI.FIELD_KEY, keyNoti);
            List<String> lstUsers = new ArrayList<>();
            for (UserRetro userRetro : Utilities.lsFriendsNotePrivate) {
                lstUsers.add(userRetro.getUid());
            }
            map.put(EntityAPI.FIELD_LIST_USERS, lstUsers);

            notiController.sendNoti(map, this);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onSendNotiSuccess() {

    }

    @Override
    public void onErrorSendNoti() {

    }

    @Override
    public void onSendNotiFailure() {

    }

    @Override
    public void onConnectSendNotiFailure() {

    }

    @Override
    public void onUploadImagesSuccess(List<String> data) {
        try {
            listURLImagesAPI = data;

            if (listURLImagesAPI.size() == listImageBase64.size()) {
                progressBarLoading.setVisibility(View.GONE);
                product.setImages(lsImagesDefault);
            }

            for (int i = 0; i < listURLImagesAPI.size(); i++) {
                imageSuffixes.get(i).setPath(listURLImagesAPI.get(i));
            }

            lsImagesDefault.addAll(imageSuffixes);
            imageSuffixes.clear();

            isSuccessUploadImage = true;
            txtCountSelectImage.setText(getResources().getString(R.string.image_number) + " " + product.getImages().size() + "/10");
            chooseImageAdapter.notifyDataSetChanged();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onErrorUploadImages() {

    }

    @Override
    public void onUploadImagesFailure() {

    }

    @Override
    public void onConnectUploadImagesFailure() {

    }

    private void uploadImage(UploadImageRetro imageRetro) {
        try {
            uploadImageController.uploadImages(imageRetro, this);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class resizeImage extends AsyncTask<Void, Void, Void> {

        private ArrayList<Image> lstImage;

        resizeImage(ArrayList<Image> lst) {
            this.lstImage = lst;
        }

        @Override
        protected void onPreExecute() {
            progressBarLoading.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @SuppressLint("CheckResult")
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Image img = lstImage.get(0);

                Luban.compress(mContext, new File(img.getPath()))
                        .setMaxSize(1024)                // limit the final image size（unit：Kb）
                        .setMaxHeight(1920)             // limit image height
                        .setMaxWidth(1080)              // limit image width
                        .putGear(Luban.CUSTOM_GEAR)     // use CUSTOM GEAR compression mode
                        .launch(new OnCompressListener() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onSuccess(File file) {
                                try {

                                    lstImage.remove(0);
                                    Bitmap bm = BitmapFactory.decodeFile(file.getPath());
                                    ArrayList<String> suffix = new ArrayList<>();

                                    int width = bm.getWidth();
                                    for (Integer w : Utilities.SUFFIXES) {
                                        if (w <= width) {
                                            suffix.add(String.valueOf(w));
                                        }
                                    }

                                    String base64 = Utilities.getEncoded64ImageStringFromBitmap(bm);
                                    listImageBase64.add(base64);
                                    imageSuffixes.add(new ImageSuffix(base64, suffix));

                                    if (lstImage.size() > 0) {
                                        new resizeImage(lstImage).execute();
                                    } else {
                                        uploadImageGetURL(listImageBase64);
                                    }


                                } catch (Exception e) {
                                    Crashlytics.logException(e);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Crashlytics.logException(e);
                            }
                        });

            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    Log.d("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }

            return null;
        }
    }
}
