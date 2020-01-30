package csell.com.vn.csell.views.social.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.firebase.database.DatabaseReference;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.commons.OnSwipeTouchListener;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FirebaseDBUtil;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.ProductController;
import csell.com.vn.csell.interfaces.GetProduct;
import csell.com.vn.csell.interfaces.RetryConnect;
import csell.com.vn.csell.models.NoteV1;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.models.ProductResponseV1;
import csell.com.vn.csell.mycustoms.DisconnectDialog;
import csell.com.vn.csell.sqlites.SQLFriends;
import csell.com.vn.csell.sqlites.SQLLanguage;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.csell.adapter.ViewPagerAdapter;
import csell.com.vn.csell.views.note.activity.AddNoteProductActivity;
import csell.com.vn.csell.views.product.activity.EditProductActivityV1;
import csell.com.vn.csell.views.product.fragment.TabDetailProductFragment;
import csell.com.vn.csell.views.product.fragment.TimeLineProductFragment;
import csell.com.vn.csell.views.social.adapter.CategoryAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailProductActivity extends AppCompatActivity implements GetProduct, RetryConnect, ProductController.OnDeleteProductListener {

    public static String productKey;
    public static String productName;
    public static boolean isMyProduct;
    public static boolean turnPrivateMode;
    private LinearLayout btnBack, btnEdit;
    private Context mContext;
    private FileSave fileGet;
    private FrameLayout layout_sim;
    private ImageView img_sim;
    private TextView txtDetailSim;
    //    private Product productFeatures;
    private ProductResponseV1 productResponseV1;
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            String url = productResponseV1.getImages().get(position);
            if (url != null) {
                GlideApp.with(mContext).load(url).into(imageView);
            }
        }
    };
    private AppBarLayout appBarLayout;
    private View layoutOverlay;
    private TextView txtNameProduct;
    private TextView txtPriceProduct;
    private ImageView imgTypePost;
    private TextView txtPostTypeName;
    private LinearLayout layout_post_type;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TimeLineProductFragment timeline;
    private TabDetailProductFragment detailProductFragment;
    private CarouselView carouselView;
    private Toolbar toolbar;
    private ProductController productController;
    private FloatingActionButton fab;
    private FloatingActionButton fabMessage;
    private FloatingActionButton fabCall;
    private int position;
    private TextView txtReadMore;
    private boolean isProductSim;
    private SQLLanguage sqlLanguage;
    private SQLFriends sqlFriends;
    private String uidSendNoti;
    private boolean isOpenFromNoti;
    private LinearLayout layoutOwner;
    private TextView txtNameOwner;
    private CircleImageView imgAvatarOwner;
    private ImageView imgMore;
    private boolean isPending;
    private String roomId;
    private DatabaseReference dbReference;
    private boolean isReup;
    private boolean isFollowed;
    private FileSave filePut;
    private CoordinatorLayout layoutDetail;
    private RelativeLayout layoutImage;
    private ProgressBar progressBar;
    private BaseActivityTransition baseActivityTransition;
    private boolean isUpdateNote;
    private boolean isEdit;
    private RecyclerView rcvCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product2);

        mContext = this;

        dbReference = FirebaseDBUtil.getDatebase().getReference();
        fileGet = new FileSave(mContext, Constants.GET);
        filePut = new FileSave(mContext, Constants.PUT);
        sqlLanguage = new SQLLanguage(mContext);
        sqlFriends = new SQLFriends(mContext);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        //set navigation bar translucent
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //now the status bar is translucent too, which we have to fix

        //first we get status bar height
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

        if (hasBackKey && hasHomeKey) {
            // no navigation bar, unless it is enabled in the settings
        } else {
            // 99% sure there's a navigation bar
            FrameLayout mainLayout = findViewById(R.id.main_layout);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, 0, 0, statusBarHeight);
            mainLayout.setLayoutParams(lp);
        }

        Intent intent = getIntent();
        if (intent != null) {
            productResponseV1 = (ProductResponseV1) intent.getSerializableExtra(Constants.TEMP_PRODUCT);
            productKey = intent.getStringExtra(Constants.TEMP_PRODUCT_KEY);
            position = intent.getIntExtra(Constants.TEMP_POSITION, -1);
            isMyProduct = intent.getBooleanExtra(Constants.IS_MY_PRODUCT, false);
            turnPrivateMode = intent.getBooleanExtra(Constants.TURN_ON_PRIVATE_MODE, false);
            uidSendNoti = intent.getStringExtra(Constants.TEMP_PRODUCT_UID);
            isOpenFromNoti = intent.getBooleanExtra(Constants.OPEN_FROM_NOTI, false);
            filePut.putProductIdCurrentSelect(productKey);
        }

        initView();
        setupWindowAnimations();
        initEvent();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        imgMore.setOnClickListener(v -> eventFollowPost(isFollowed, isReup));

        btnBack.setOnClickListener(view -> {
            Intent data = new Intent();
            data.putExtra("IS_EDIT", isEdit);
            data.putExtra("POSITION_EDIT", position);
            data.putExtra("PRODUCT_EDIT", productResponseV1);
            setResult(Constants.EDIT_PRODUCT_RESULT, data);
            finish();
        });

        btnEdit.setOnClickListener(v -> {
            if (productResponseV1 == null) {
                Toast.makeText(this, "" + getString(R.string.information_does_not_exist), Toast.LENGTH_SHORT).show();
                return;
            }

            Intent edit = new Intent(DetailProductActivity.this, EditProductActivityV1.class);
            edit.putExtra(Constants.KEY_PASSINGDATA_TYPE_EDIT_PRODUCT, 1);
            edit.putExtra(Constants.KEY_PASSINGDATA_EDIT_PRODUCT, true);
            edit.putExtra(Constants.KEY_PASSINGDATA_PRODUCT_OBJ, productResponseV1);
            edit.putExtra(Constants.TEMP_POSITION, position);
            baseActivityTransition.transitionTo(edit, Constants.EDIT_PRODUCT_RESULT);
        });

        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                //Collapsed
                layoutOverlay.setVisibility(View.VISIBLE);
                if (isProductSim) {
                    txtReadMore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                    txtDetailSim.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                    txtDetailSim.setGravity(Gravity.BOTTOM);
                }
            } else {
                //Expanded
                layoutOverlay.setVisibility(View.GONE);
                if (isProductSim) {
                    txtReadMore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    txtDetailSim.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

                    txtDetailSim.post(() -> {
                        int lineCount = txtDetailSim.getLineCount();

                        if (lineCount > 9) {
                            txtDetailSim.setGravity(Gravity.CENTER_VERTICAL);
                        } else {
                            txtDetailSim.setGravity(Gravity.CENTER);
                        }

                        if (lineCount == 0 || lineCount > 9) {
                            txtDetailSim.setMaxLines(9);
                            txtReadMore.setVisibility(View.VISIBLE);
                        } else {
                            txtReadMore.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        toolbar.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeRight() {
                if (carouselView.getCurrentItem() > 0)
                    carouselView.setCurrentItem(carouselView.getCurrentItem() - 1);
            }

            public void onSwipeLeft() {
                if (carouselView.getCurrentItem() < productResponseV1.getImages().size())
                    carouselView.setCurrentItem(carouselView.getCurrentItem() + 1);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fab.setOnClickListener(v -> requestPermission());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    fab.setVisibility(View.VISIBLE);
                } else {
                    fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        txtReadMore.setOnClickListener(v -> {

        });
    }

    private void initView() {
        layoutDetail = findViewById(R.id.layout_detail);
        layoutImage = findViewById(R.id.layout_image);
        progressBar = findViewById(R.id.progress_bar);
        hideImage(true);
        hidePlaceHolder(false);

        layoutOwner = findViewById(R.id.layout_owner);
        txtNameOwner = findViewById(R.id.img_name_owner);
        imgAvatarOwner = findViewById(R.id.img_avatar_owner);
        imgMore = findViewById(R.id.img_more);
        txtReadMore = findViewById(R.id.txtReadMore);
        fab = findViewById(R.id.fab);
        fabCall = findViewById(R.id.fab_call);
        fabMessage = findViewById(R.id.fab_message);
        toolbar = findViewById(R.id.main_toolbar);
        carouselView = findViewById(R.id.carouselView);
        layoutOverlay = findViewById(R.id.layout_overlay);
        appBarLayout = findViewById(R.id.tool_bar_detail_product);
        RelativeLayout layoutButtonToolbar = findViewById(R.id.layout_button_toolbar);
        btnBack = findViewById(R.id.btn_back);
        btnEdit = findViewById(R.id.btn_edit);
        layout_sim = findViewById(R.id.layout_sim);
        img_sim = findViewById(R.id.img_sim);
        txtDetailSim = findViewById(R.id.txtDetailSim);
        txtNameProduct = findViewById(R.id.txt_product_name);
        txtPriceProduct = findViewById(R.id.txt_price_product);
        imgTypePost = findViewById(R.id.imgTypePost);
        txtPostTypeName = findViewById(R.id.txtPostTypeName);
        layout_post_type = findViewById(R.id.layout_post_type);
        viewPager = findViewById(R.id.view_pager_detail_product);
        tabLayout = findViewById(R.id.tab_layout_detail_product);

        rcvCat = findViewById(R.id.rcv_cat);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        rcvCat.setLayoutManager(flexboxLayoutManager);
        CategoryAdapter categoryAdapter = new CategoryAdapter();
        categoryAdapter.setCategoryRequestList(productResponseV1.getCategories());
        rcvCat.setAdapter(categoryAdapter);

        baseActivityTransition = new BaseActivityTransition(this);

        setupViewPager(viewPager);
//        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);

        layoutOverlay.setVisibility(View.GONE);

        carouselView.setRadius(10);

        //padding button toolbar with status
        layoutButtonToolbar.setPadding(Utilities.dpToPx(mContext, 10), Utilities.getStatusBarHeight(mContext) + Utilities.dpToPx(mContext, 3),
                Utilities.dpToPx(mContext, 16), 0);

//        getDataProductOnView();///


        productController = new ProductController(this, this, this);

        try {
            if (Utilities.isNetworkConnected(mContext)) {
                if (Utilities.getInetAddressByName() != null) {
                    if (isMyProduct) {
                        //get detail
                        productController.getDetailProduct(productKey);
                        productController.getNoteProducts(productKey);
                        hidePlaceHolder(true);
                    } else {
//                        social
//                        if (isOpenFromNoti) {
//                            productController.getProductNewfeed(uidSendNoti, productKey);
//                        } else {
//                            if (productResponseV1.getUserInfo().getUid() == null)
//                                productResponseV1.getUserInfo().setUid(fileGet.getFriendId());
//
//                            productController.getProductNewfeed(productResponseV1.getUserInfo().getUid(), productKey);
//                        }

                    }
                } else {
                    hidePlaceHolder(true);
                    DisconnectDialog dialog = new DisconnectDialog(mContext, this);
                    dialog.setCancelable(false);
                    dialog.show();
                }
            } else {
                hidePlaceHolder(true);
                DisconnectDialog dialog = new DisconnectDialog(mContext, this);
                dialog.setCancelable(false);
                dialog.show();
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void transDataToTabInfo(ProductResponseV1 productResponseV1) {
        try {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.KEY_DETAIL_PRODUCT, productResponseV1);
            if (detailProductFragment == null)
                detailProductFragment = new TabDetailProductFragment();
            detailProductFragment.setArguments(bundle);
            detailProductFragment.reloadViewTabInfo(productResponseV1);
        } catch (Exception e) {
            Log.e("error: ", e.toString());
        }
    }

    private void transDataToTabTimeline(List<NoteV1> lstNote) {
        try {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.KEY_NOTE_PRODUCT, (Serializable) lstNote);
            timeline.setArguments(bundle);
            timeline.updateView(lstNote);
        } catch (Exception e) {
            Log.e("error: ", e.toString());
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        try {
            if (isMyProduct) {
                if (productResponseV1 != null) {
                    if (productResponseV1.getPrivacy() != null) {
//                        if (productFeatures.getType() == 2) {// tin vu vo
//                            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//                            detailProductFragment = new TabDetailProductFragment();
//                            adapter.addFrag(detailProductFragment, getString(R.string.information));
//                            viewPager.setAdapter(adapter);
//                        } else {
                        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
                        detailProductFragment = new TabDetailProductFragment();
                        timeline = new TimeLineProductFragment();
                        adapter.addFrag(detailProductFragment, getString(R.string.information));
                        adapter.addFrag(timeline, getString(R.string.note));
                        viewPager.setAdapter(adapter);
//                        }
                    } else {
                        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
                        timeline = new TimeLineProductFragment();
                        detailProductFragment = new TabDetailProductFragment();
                        adapter.addFrag(detailProductFragment, getString(R.string.information));
                        adapter.addFrag(timeline, getString(R.string.note));
                        viewPager.setAdapter(adapter);
                    }
                } else {
                    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
                    timeline = new TimeLineProductFragment();
                    detailProductFragment = new TabDetailProductFragment();
                    adapter.addFrag(detailProductFragment, getString(R.string.information));
                    adapter.addFrag(timeline, getString(R.string.note));
                    viewPager.setAdapter(adapter);
                }
            } else {
                ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
                detailProductFragment = new TabDetailProductFragment();
                adapter.addFrag(detailProductFragment, getString(R.string.information));
                viewPager.setAdapter(adapter);
            }


//            transDataToTabInfo();
        } catch (Exception e) {
            Log.e("test_type", e.toString());
        }
    }

    private void getDataProductOnView(ProductResponseV1 productResponseV1) {
        try {
            if (productResponseV1.getCategories().get(0).getId().startsWith(Utilities.SIM)) {
                isProductSim = true;

                carouselView.setVisibility(View.GONE);
                layout_sim.setVisibility(View.VISIBLE);

                String url = "";
                if (productResponseV1.getImages() != null) {
                    for (String item : productResponseV1.getImages()) {
                        if (!TextUtils.isEmpty(item)) {
                            url = item;
                            break;
                        }
                    }
                }

                GlideApp.with(mContext)
                        .load(url)
                        .error(R.drawable.bg_3)
                        .into(img_sim);

                txtDetailSim.setText(productResponseV1.getContent().trim());
                txtDetailSim.post(() -> {
                    int lineCount = txtDetailSim.getLineCount();

                    if (lineCount > 9) {
                        txtDetailSim.setGravity(Gravity.CENTER_VERTICAL);
                    } else {
                        txtDetailSim.setGravity(Gravity.CENTER);
                    }

                    if (lineCount == 0 || lineCount > 9) {
                        txtDetailSim.setMaxLines(9);
                        txtReadMore.setVisibility(View.VISIBLE);
                    } else {
                        txtReadMore.setVisibility(View.GONE);
                    }
                });

            } else {
//                if (productFeatures.getType() == 2) { //tin vu vo
//                    isProductSim = true; // hien thi view ben preview giong view sim
//
//                    tabLayout.setVisibility(View.GONE);
//
//                    carouselView.setVisibility(View.GONE);
//                    layout_sim.setVisibility(View.VISIBLE);
//
//                    GlideApp.with(mContext)
//                            .load(productFeatures.getBackground())
//                            .error(R.drawable.bg_3)
//                            .into(img_sim);
//
//                    txtDetailSim.setText(productFeatures.getDescription().trim());
//                    txtDetailSim.post(() -> {
//                        int lineCount = txtDetailSim.getLineCount();
//
//                        if (lineCount > 9) {
//                            txtDetailSim.setGravity(Gravity.CENTER_VERTICAL);
//                        } else {
//                            txtDetailSim.setGravity(Gravity.CENTER);
//                        }
//
//                        if (lineCount == 0 || lineCount > 9) {
//                            txtDetailSim.setMaxLines(9);
//                            txtReadMore.setVisibility(View.VISIBLE);
//                        } else {
//                            txtReadMore.setVisibility(View.GONE);
//                        }
//                    });
//                } else {

                isProductSim = false;

                carouselView.setVisibility(View.VISIBLE);
                layout_sim.setVisibility(View.GONE);
                layout_sim.removeAllViews();
                if (productResponseV1.getImages() != null) {
                    if (productResponseV1.getImages().size() > 0) {
                        carouselView.setImageListener(imageListener);
                        carouselView.setPageCount(productResponseV1.getImages().size());
                    }
                }
//                }

            }
            hideImage(false);

            updateTitlePrice(productResponseV1.getName() + "", productResponseV1.getPrice(), productResponseV1.getCurrency());

            if (isMyProduct) {
                if (productResponseV1.getPrivacy() == null) {
                    layout_post_type.setVisibility(View.GONE);
                } else {
                    layout_post_type.setVisibility(View.VISIBLE);
                    switch (productResponseV1.getPrivacy()) {
                        case "public":
                            imgTypePost.setImageResource(R.drawable.ic_public_black);
                            txtPostTypeName.setText(MainActivity.mainContext.getResources().getString(R.string.title_public));
                            break;
                        case "onlyme":
                            imgTypePost.setImageResource(R.drawable.ic_only_me);
                            txtPostTypeName.setText(MainActivity.mainContext.getResources().getString(R.string.title_only_me));
                            break;
                    }
                }
            } else {
                layout_post_type.setVisibility(View.VISIBLE);

//                if (productFeatures.getReup() != null) {
//                    isReup = productFeatures.getReup();
//                }
//                if (productFeatures.getFollowItem() != null) {
//                    isFollowed = productFeatures.getFollowItem();
//                }
//
//                if (productFeatures.getPrivacyType() == null) {
//                    imgTypePost.setImageResource(R.drawable.ic_public_black);
//                    txtPostTypeName.setText(MainActivity.mainContext.getResources().getString(R.string.title_public));
//                } else {
//                    switch (productFeatures.getPrivacyType()) {
//                        case Utilities.TYPE_POST_PUBLIC:
//                            imgTypePost.setImageResource(R.drawable.ic_public_black);
//                            txtPostTypeName.setText(MainActivity.mainContext.getResources().getString(R.string.title_public));
//                            break;
//                        case Utilities.TYPE_POST_ONLY_ME:
//                            imgTypePost.setImageResource(R.drawable.ic_only_me);
//                            txtPostTypeName.setText(MainActivity.mainContext.getResources().getString(R.string.title_only_me));
//                            break;
//                    }
//                }
            }
        } catch (Exception e) {
            Log.d("XXX", e.getMessage());
            Crashlytics.logException(e);
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateTitlePrice(String title, Long price, String currency) {
        txtNameProduct.setText(title + "");
        txtPriceProduct.setText(price + " " + currency);
        productName = title + "";

        if (price == null || price == 0) {
            txtPriceProduct.setText(getString(R.string.contact));
        } else {
            txtPriceProduct.setText(Utilities.formatMoney(price, currency));
        }
    }

    public void requestPermission() {
        int checkReadCal = ActivityCompat.checkSelfPermission(DetailProductActivity.this, Manifest.permission.READ_CALENDAR);
        int checkWriteCal = ActivityCompat.checkSelfPermission(DetailProductActivity.this, Manifest.permission.WRITE_CALENDAR);
        int permissionGranted = PackageManager.PERMISSION_GRANTED;

        String[] permissions = new String[]{
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR};
        //check Android 6+
        if (Build.VERSION.SDK_INT >= 23) {
            //check permission granted
            if (checkReadCal != permissionGranted || checkWriteCal != permissionGranted) {
                //request Permissions
                ActivityCompat.requestPermissions(DetailProductActivity.this, permissions, 1);
            } else {
                Intent note = new Intent(DetailProductActivity.this, AddNoteProductActivity.class);
                note.putExtra("NOTE_IN_PRODUCT", true);
                note.putExtra(Constants.KEY_PRODUCT_ID, fileGet.getProductIdCurrentSelect());
                note.putExtra(Constants.KEY_PRODUCT_NAME, fileGet.getProductNameCurrentSelect());
                baseActivityTransition.transitionTo(note, Constants.ADD_NOTE_ACTIVITY_RESULT);
            }
        } else {
            Intent note = new Intent(DetailProductActivity.this, AddNoteProductActivity.class);
            note.putExtra("NOTE_IN_PRODUCT", true);
            note.putExtra(Constants.KEY_PRODUCT_ID, fileGet.getProductIdCurrentSelect());
            note.putExtra(Constants.KEY_PRODUCT_NAME, fileGet.getProductNameCurrentSelect());
            baseActivityTransition.transitionTo(note, Constants.ADD_NOTE_ACTIVITY_RESULT);
        }
    }

    private void previewImage(ProductResponseV1 product) {
        carouselView.setImageClickListener(position -> {
            Intent preview = new Intent(mContext, PreviewImageActivity.class);
            preview.putExtra(Constants.PREVIEW, (Serializable) product.getImages());
            preview.putExtra(Constants.PREVIEW_POSITION, position);
            preview.putExtra(Constants.PREVIEW_DES, product.getContent());
            preview.putExtra(Constants.PREVIEW_IS_SIM, isProductSim);
            baseActivityTransition.transitionTo(preview, 0);
        });
        txtReadMore.setOnClickListener(v -> {
            Intent preview = new Intent(mContext, PreviewImageActivity.class);
            if (productResponseV1.getPrivacy() != null) {
//                if (productFeatures.getType() == 2) {
//                    List<ImageSuffix> lstTemp = new ArrayList<>();
//                    lstTemp.add(new ImageSuffix(product.getBackground(), null));
//                    preview.putExtra(Constants.PREVIEW, (Serializable) lstTemp);
//                } else
//                    preview.putExtra(Constants.PREVIEW, (Serializable) product.getImages());
            } else {
                preview.putExtra(Constants.PREVIEW, (Serializable) product.getImages());
            }
            preview.putExtra(Constants.PREVIEW_POSITION, 0);
            preview.putExtra(Constants.PREVIEW_DES, product.getContent());
            preview.putExtra(Constants.PREVIEW_IS_SIM, isProductSim);
            baseActivityTransition.transitionTo(preview, 0);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Constants.KEY_UPDATE_NOTE_RESULT || requestCode == Constants.ADD_NOTE_ACTIVITY_RESULT) {
                isUpdateNote = true;
                productController.getDetailProduct(productKey);
                productController.getNoteProducts(productKey);
            } else if (resultCode == Constants.EDIT_PRODUCT_RESULT) {
                if (data != null) {
                    boolean remove = data.getBooleanExtra(Constants.TEMP_REMOVE_PRODUCT, false);
                    if (remove) {
                        setResult(Constants.RESULT_CODE_REMOVE_PRODUCT, data);
                        finishAfterTransition();
                    }
                } else {
//                    detailProductFragment.onActivityResult(requestCode, resultCode, data);
                    isEdit = true;
                    productController.getDetailProduct(productKey);
                    productController.getNoteProducts(productKey);
                }
            }else if (resultCode == Constants.RESULT_CODE_REMOVE_PRODUCT_V1){
                Intent intent = new Intent();
                intent.putExtra("POSITION_DELETE", position);
                setResult(Constants.RESULT_CODE_REMOVE_PRODUCT_V1, intent);
                finishAfterTransition();
            }

            if (resultCode == Constants.REUP_PRODUCT_RESULT) {
                isReup = true;
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
            Crashlytics.logException(e);
        }
    }

    @SuppressLint("SetTextI18n")
    private void showInfoOwner(Product product) {
        try {
            if (isMyProduct) {
                if (productResponseV1.getPrivacy() != null) {
//                    if (productFeatures.getType() == 2) {
//                        tabLayout.setVisibility(View.GONE);
//                        btnEdit.setVisibility(View.GONE);
//                    } else {
//                        tabLayout.setVisibility(View.VISIBLE);
//                        btnEdit.setVisibility(View.VISIBLE);
//                    }
                } else {
                    tabLayout.setVisibility(View.VISIBLE);
                    btnEdit.setVisibility(View.VISIBLE);
                }
                fabCall.setVisibility(View.GONE);
                fabMessage.setVisibility(View.GONE);
                layoutOwner.setVisibility(View.GONE);
                imgMore.setVisibility(View.GONE);
            } else {
                tabLayout.setVisibility(View.GONE);
                fabCall.setVisibility(View.VISIBLE);
                fabMessage.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.GONE);
                layoutOwner.setVisibility(View.VISIBLE);
                imgMore.setVisibility(View.VISIBLE);

                txtNameOwner.setText(product.getUserInfo().getDisplayname() + "");
                GlideApp.with(mContext).load(product.getUserInfo().getAvatar()).error(R.drawable.ic_logo).into(imgAvatarOwner);

                layoutOwner.setOnClickListener(v -> {
//                    if (productFeatures != null) {
//                        UserRetro friend = new UserRetro();
//                        friend.setUid(productFeatures.getUserInfo().getUid());
//                        friend.setUsername(productFeatures.getUserInfo().getUsername());
//                        friend.setPhone(productFeatures.getUserInfo().getPhone());
//                        friend.setAvatar(productFeatures.getUserInfo().getAvatar());
//                        friend.setDisplayname(productFeatures.getUserInfo().getDisplayname());
//
//                        Intent detail = new Intent(this, FriendDetailsActivity.class);
//                        detail.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, friend);
//                        baseActivityTransition.transitionTo(detail, 0);
//                    }
                });

                fabCall.setOnClickListener(v -> {
//                    if (productFeatures != null) {
//                        if (TextUtils.isEmpty(productFeatures.getUserInfo().getPhone())) {
//                            Snackbar.make(v, getString(R.string.text_error_owner_no_phone), Snackbar.LENGTH_LONG).setAction(getString(R.string.action), null).show();
//                            return;
//                        }
//                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", productFeatures.getUserInfo().getPhone().trim(), null));
//                        baseActivityTransition.transitionTo(intent, 0);
//                    }
                });

                fabMessage.setOnClickListener(v -> {
//                    if (productFeatures != null) {
//                        UserRetro f = sqlFriends.checkIsFriendById(productFeatures.getUserInfo().getUid());
//                        isPending = f == null;
//                        roomId = "";
//                        if (productFeatures.getUserInfo().getUid() != null) {
//                            String fUid = productFeatures.getUserInfo().getUid();
//                            dbReference.child(EntityFirebase.FieldRooms)
//                                    .child(fileGet.getUserId())
//                                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            if (dataSnapshot.getChildrenCount() > 0) {
//                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                                                    if (dataSnapshot1.getKey().equals(fUid)) {
//                                                        if (dataSnapshot1.getValue() != null)
//                                                            roomId = dataSnapshot1.getValue() != null ? (String) dataSnapshot1.getValue() : null;
//                                                    }
//                                                }
//                                            } else {
//                                                roomId = null;
//                                            }
//
//                                            if (TextUtils.isEmpty(roomId)) {
//                                                roomId = fileGet.getUserId() + fUid;
//                                                dbReference.child(EntityFirebase.FieldRooms).child(fileGet.getUserId()).child(fUid).setValue(roomId);
//                                                dbReference.child(EntityFirebase.FieldRooms).child(fUid).child(fileGet.getUserId()).setValue(roomId);
//                                            } else {
//                                                dbReference.child(EntityFirebase.TableOnlineChat).child(fileGet.getUserId()).setValue(roomId);
//                                                dbReference.child(EntityFirebase.TableOnlineChat).keepSynced(false);
//                                                dbReference.child(EntityFirebase.TableDirectMessages).child(fUid).keepSynced(false);
//                                            }
//
//                                            if (!TextUtils.isEmpty(roomId)) {
//                                                Intent chat = new Intent(DetailProductActivity.this, ChatActivity.class);
//                                                chat.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, productFeatures.getUserInfo());
//                                                chat.putExtra(Constants.KEY_PASSINGDATA_PRODUCT_OBJ, productFeatures);
//                                                chat.putExtra(Constants.KEY_PASSINGDATA_ROOM_ID, roomId);
//                                                chat.putExtra(Constants.KEY_IS_PENDING, isPending);
//                                                baseActivityTransition.transitionTo(chat, 0);
//                                            } else
//                                                Toast.makeText(DetailProductActivity.this, R.string.cant_not_find_friend, Toast.LENGTH_SHORT).show();
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//                                        }
//                                    });
//                        }
//                    }
                });
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void eventFollowPost(boolean isFollowed, boolean isReup) {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_follow_product_choose);

        Button btnFollow = dialog.findViewById(R.id.btn_follow_product);
        Button btnReUp = dialog.findViewById(R.id.btn_reup_product);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel_dialog_choose);

        if (isFollowed) {
            btnFollow.setText(MainActivity.mainContext.getResources().getString(R.string.un_follow_product));
        } else {
            btnFollow.setText(MainActivity.mainContext.getResources().getString(R.string.follow_product));
        }

        btnFollow.setOnClickListener(v1 -> {
//            if (productFeatures != null) {
//                if (!fileGet.getUserId().equalsIgnoreCase(productFeatures.getUserInfo().getUid())) {
//                    if (isFollowed) {
//                        productFeatures.setFollowItem(false);
//                        unfollowPost(productFeatures);
//                    } else {
//                        productFeatures.setFollowItem(true);
//                        followPost(productFeatures);
//                    }
//                } else {
//                    Toast.makeText(this, getString(R.string.text_error_follow), Toast.LENGTH_LONG).show();
//                }
//
//                dialog.dismiss();
//            }


        });

        btnReUp.setOnClickListener(v1 -> {
//            if (productFeatures != null) {
//                if (isReup) {
//                    Toast.makeText(this, "Mỗi sản phẩm chỉ được phép reup 1 lần", Toast.LENGTH_LONG).show();
//                } else {
//                    if (!fileGet.getUserId().equals(productFeatures.getUserInfo().getUid())) {
//                        if (productFeatures.getType() != null) {
//                            if (productFeatures.getType() == 2) {
//                                Toast.makeText(this, getString(R.string.text_error_reup_empty_post), Toast.LENGTH_LONG).show();
//                            } else {
//                                reupPost(productFeatures);
//                            }
//                        } else {
//                            if (productFeatures.getCatid().startsWith("land") || productFeatures.getCatid().startsWith("car")) {
//                                reupPost(productFeatures);
//                            } else {
//                                Toast.makeText(this, getString(R.string.text_error_reup_empty_post), Toast.LENGTH_LONG).show();
//                            }
//                        }
//
//                    } else if (fileGet.getUserId().equals(productFeatures.getUserInfo().getUid())) {
//                        Toast.makeText(this, getString(R.string.text_error_reup), Toast.LENGTH_LONG).show();
//                    }
//                }
//                dialog.dismiss();
//            }
        });

        btnCancel.setOnClickListener(v1 -> dialog.dismiss());

        //custom position dialog
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        lp.windowAnimations = R.style.DialogCreateAnimation;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // Width
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // Height

        dialogWindow.setAttributes(lp);

        dialog.show();

    }

    private void reupPost(Product product) {
//        Intent intent = new Intent(this, EditProductActivity.class);
//        intent.putExtra(Constants.KEY_PASSINGDATA_PRODUCT_OBJ, product);
//        intent.putExtra(Constants.KEY_PASSINGDATA_EDIT_PRODUCT, false);
//        intent.putExtra(Constants.KEY_PASSINGDATA_PRODUCT_FOLLOW_OBJ, product);
//        intent.putExtra(Constants.KEY_IS_FOLLOWED, isFollowed);
//        intent.putExtra(Constants.KEY_IS_REUP, isReup);
//        baseActivityTransition.transitionTo(intent, Constants.EDIT_PRODUCT_RESULT);
    }

    private void followPost(Product product) {

        try {
            PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
            if (postAPI != null) {

                HashMap<String, Object> mapFollow = new HashMap<>();
                mapFollow.put(EntityAPI.FIELD_ITEM_ID, product.getItemid());
                mapFollow.put(EntityAPI.FIELD_RE_UP_OWNER_ID, product.getUserInfo().getUid());

                Call<JSONResponse<Object>> jsonResponse = postAPI.followPost(mapFollow);
                jsonResponse.enqueue(new Callback<JSONResponse<Object>>() {
                    @Override
                    public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                        if (response.isSuccessful()) {
                            JSONResponse<Object> result = response.body();
                            if (result.getSuccess() != null) {
                                if (result.getSuccess()) {
                                    isFollowed = true;
                                    Toast.makeText(DetailProductActivity.this, getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();

                                } else {
                                    String msg = response.body().getError();
                                    if (!TextUtils.isEmpty(msg)) {
                                        Toast.makeText(DetailProductActivity.this, msg, Toast.LENGTH_LONG).show();
                                    }
                                    Utilities.refreshToken(DetailProductActivity.this, result.getMessage().toLowerCase() + "");
                                }
                            } else {
                                if (response.errorBody() != null) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                        String msg = (String) jsonObject.get(Constants.ERROR);
                                        if (!TextUtils.isEmpty(msg)) {
                                            Toast.makeText(DetailProductActivity.this, msg, Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                        try {
                            Crashlytics.logException(t);
                        } catch (Exception ignored) {

                        }
                    }
                });

            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

    }

    private void unfollowPost(Product product) {
        try {
            PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
            if (postAPI != null) {

                HashMap<String, Object> mapFollow = new HashMap<>();
                mapFollow.put(EntityAPI.FIELD_RE_UP_OWNER_ID, product.getUserInfo().getUid());

                Call<JSONResponse<Object>> jsonResponse = postAPI.unfollowPost(productKey, mapFollow);
                jsonResponse.enqueue(new Callback<JSONResponse<Object>>() {
                    @Override
                    public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                        if (response.isSuccessful()) {
                            JSONResponse<Object> result = response.body();
                            if (result != null) {
                                if (result.getSuccess() != null) {
                                    if (result.getSuccess()) {
                                        isFollowed = false;
                                        Toast.makeText(DetailProductActivity.this, getResources().getString(R.string.success), Toast.LENGTH_SHORT).show();

                                    } else {
                                        Utilities.refreshToken(DetailProductActivity.this, result.getMessage().toLowerCase() + "");
                                        Toast.makeText(DetailProductActivity.this, getResources().getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            String msg = response.body().getError();
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(DetailProductActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (response.errorBody() != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = (String) jsonObject.get(Constants.ERROR);
                                    if (!TextUtils.isEmpty(msg)) {
                                        Toast.makeText(DetailProductActivity.this, msg, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException | IOException ignored) {

                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                        try {
                            Crashlytics.logException(t);
                        } catch (Exception ignored) {

                        }
                    }
                });

            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

    }

    @Override
    public void onGetDetail(ProductResponseV1 product) {
        try {
            productResponseV1 = product;
            getDataProductOnView(product);
            transDataToTabInfo(product);
            previewImage(product);

            filePut.putProductUserId(fileGet.getUserId());

            if (!isEdit)
                hidePlaceHolder(true);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void onGetNoteProduct(List<NoteV1> lstNote) {
        try {
//            if (productResponseV1.getPrivacy() != null) {
////                if (productFeatures.getType() != 2)
////                    transDataToTabTimeline(lstNote);
//            } else {
            transDataToTabTimeline(lstNote);
//            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onGetDetailNewfeed(Product product) {
//        int type = 1;
//        try {
//            if (productFeatures == null)
//                productFeatures = new Product();
//
//            if (product.getType() != null)
//                type = product.getType();
//
//            productFeatures = product;
//
//            productFeatures.setType(type);
//            productFeatures.setItemid(fileGet.getProductIdCurrentSelect());
//
//            showInfoOwner(productFeatures);
//            getDataProductOnView();
//            transDataToTabInfo();
//            previewImage(productFeatures);
//
//            filePut.putProductUserId(product.getUserInfo().getUid());
//
//            hidePlaceHolder(true);
//        } catch (Exception e) {
//            Log.d("XXX", e.getMessage());
//            if (product.getType() != null)
//                type = product.getType();
//            productFeatures = product;
//
//            productFeatures.setType(type);
//            productFeatures.setItemid(fileGet.getProductIdCurrentSelect());
//
//            showInfoOwner(productFeatures);
//            getDataProductOnView();
//            transDataToTabInfo();
//            previewImage(productFeatures);
//
//            filePut.putProductUserId(product.getUserInfo().getUid());
//
//            hidePlaceHolder(true);
//        }
    }

    @Override
    public void onRetryConnect() {
        if (isMyProduct) {
            productController.getDetailProduct(productKey);
            productController.getNoteProducts(productKey);
        } else {
            if (isOpenFromNoti) {
//                productController.getProductNewfeed(uidSendNoti, productKey);
            } else {
//                productController.getProductNewfeed(productResponseV1.getOwner().getId(), productKey);
            }
        }
    }

    @Override
    public void onBackRetryConnect() {
        onBackPressed();
    }

    private void hidePlaceHolder(boolean b) {
        if (b) {
            layoutDetail.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            layoutDetail.startAnimation(animation);
        } else {
            layoutDetail.setVisibility(View.INVISIBLE);
        }
    }

    private void hideImage(boolean b) {
        if (b) {
            progressBar.setVisibility(View.VISIBLE);
            layoutImage.setVisibility(View.INVISIBLE);
        } else {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                Animation animation = AnimationUtils.loadAnimation(this,
                        R.anim.fade_in);
                layoutImage.startAnimation(animation);
                layoutImage.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }, 500);
        }
    }

    @Override
    public void onDeleteProductSuccess() {

    }

    @Override
    public void onDeleteProductFailure() {

    }
}
