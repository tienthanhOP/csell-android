package csell.com.vn.csell.views.csell.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityFirebase;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FirebaseDBUtil;
import csell.com.vn.csell.constants.FragmentTag;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.CustomersController;
import csell.com.vn.csell.controllers.FriendsController;
import csell.com.vn.csell.controllers.NoteController;
import csell.com.vn.csell.controllers.UserController;
import csell.com.vn.csell.interfaces.GetDetail;
import csell.com.vn.csell.interfaces.ShowNotePrivate;
import csell.com.vn.csell.libraries.badgeview.Badge;
import csell.com.vn.csell.libraries.badgeview.BadgeView;
import csell.com.vn.csell.models.CustomerRetroV1;
import csell.com.vn.csell.models.FriendResponse;
import csell.com.vn.csell.models.LastMessage;
import csell.com.vn.csell.models.Note;
import csell.com.vn.csell.models.NoteV1;
import csell.com.vn.csell.models.Notification;
import csell.com.vn.csell.models.ProductCountResponse;
import csell.com.vn.csell.models.ResLogin;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.models.Version;
import csell.com.vn.csell.mycustoms.CommentDialog;
import csell.com.vn.csell.mycustoms.ExpandableHeightListView;
import csell.com.vn.csell.mycustoms.InputPasswordPrivateMode;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.account.activity.PersonalPageActivity;
import csell.com.vn.csell.views.csell.adapter.TimeLineTodayAdapter;
import csell.com.vn.csell.views.customer.activity.AddCustomerFromContactActivity;
import csell.com.vn.csell.views.customer.fragment.CustomersFragment;
import csell.com.vn.csell.views.filter.FilterProductActivity;
import csell.com.vn.csell.views.friend.activity.ChatActivity;
import csell.com.vn.csell.views.friend.activity.FindFriendsActivity;
import csell.com.vn.csell.views.friend.activity.FriendDetailsActivity;
import csell.com.vn.csell.views.friend.fragment.FriendManagementFragment;
import csell.com.vn.csell.views.friend.fragment.HistoryMessages;
import csell.com.vn.csell.views.friend.fragment.HomeFriendFragment;
import csell.com.vn.csell.views.note.activity.AddNoteProductActivity;
import csell.com.vn.csell.views.note.fragment.CalendarDetailActivity;
import csell.com.vn.csell.views.notification.adapter.NotificationsAdapter;
import csell.com.vn.csell.views.notification.fragment.NotificationsFragment;
import csell.com.vn.csell.views.product.activity.FavoriteProductsActivity;
import csell.com.vn.csell.views.product.fragment.ProductCollectionsFragment;
import csell.com.vn.csell.views.social.activity.DetailProductActivity;
import csell.com.vn.csell.views.social.fragment.SocialFragment;
import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Response;

//import com.google.firebase.firestore.DocumentChange;
//////import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.//ListenerRegistration;

@SuppressLint("StaticFieldLeak")
public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, NavigationView.OnNavigationItemSelectedListener, ShowNotePrivate,
        GetDetail, CustomersController.OnGetCustomersListener, FriendsController.OnGetFriendsRequestListener, NoteController.OnGetNoteTodayListener,
        UserController.OnLoginListener {

    //UI
    public static String CURRENT_PAGE = null;
    public static String FAVORITE_PAGE = "";
    public static BottomNavigationViewEx bottomNavigationViewEx;
    public static FloatingActionButton fab;
    public static FancyButton btnDrawer;
    public static FancyButton btnSort;
    public static EditText edtSearch;
    public static RelativeLayout from_search;
    //    public static SelectGroupProductHorizontalAdapter groupProductAdapter;
    public static Switch switchButton;
    public static int Level = 1;
    public static int MaxLevel = 0;
    public static RelativeLayout fromTitle;
    public static TextView tvTitle;
    public static Context mainContext;
    public static int positionGroupProductHorizontalAdapter = 0;
    //TAG
    public String TAG_ALL_PRODUCT = "AllTabFragment";
    public String TAG_CUSTOMER = "CustomersFragment";
    public String TAG_SOCIAL = "SocialFragment";
    public String TAG_HOME_FRIEND = "FriendFragment";
    public String TAG_NOTIFICATION = "NotificationsFragment";
    public String TAG_FAVORITE = "FavoriteProductsFragment";
    public String TAG_DETAIL_CALENDAR = "CalendarDetailActivity";
    public String TAG_FRIEND_MANAGEMENT = "FriendManagementFragment";
    //fragment
    public ProductCollectionsFragment productCollectionsFragment;
    public CustomersFragment customersFragment;
    public SocialFragment socialFragment;
    public HomeFriendFragment homeFriendFragment;
    public NotificationsFragment notificationsFragment;
    //variable
    public int countPendingMessage = 0;
    public BadgeView badgeViewFriend;
    public BadgeView badgeViewNotification;
    InputPasswordPrivateMode dialog;
    private FileSave fileGet;
    private long mBackPressed = 0;
    private Animation rotate_forward, rotate_backward;
    private View fabBGLayout;
    private boolean isFabOpen = false;
    private ExpandableHeightListView lvListSetup;
    private TextView tvDisplayName;
    private BottomNavigationViewEx.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private TextView tvDetailCalendar;
    private ArrayList<NoteV1> dataNotes;
    private TimeLineTodayAdapter mAdapter;
    private RecyclerView rvNotes;
    private RecyclerView rvSelectGroupProduct;
    private DrawerLayout drawer;
    private LinearLayout btnAddNote;
    private CircleImageView imgAvatar;
    private DatabaseReference dbReference;
    private ChildEventListener valueEventListener;
    private ValueEventListener listenerMessPending;
    private ValueEventListener listenerCountNotifications;
    private Query queryNoti;
    private Query querySubMessPending;
    private Query countNotifications;
    private ImageView imgCover;
    private Button btnAdd;
    private RelativeLayout layoutProfile;
    private FileSave filePut;
    private TextView tvExpTime;
    private TextView btnUpgrade;
    private List<String> lstAvatarPending;
    private BaseActivityTransition baseActivityTransition;
    private FriendsController friendsController;
    private NoteController noteController;

    @Override
    public void onAcountActive() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        friendsController = new FriendsController(this);
        noteController = new NoteController(this);
        setupWindowAnimations();
        mainContext = this;
        Fabric.with(this, new Crashlytics());
        try {
            if (BuildConfig.DEBUG)
                Log.w("DEVICE_ID", FirebaseInstanceId.getInstance().getToken() + "");
        } catch (Exception e) {
            Log.w("DEVICE_ID", "fail");
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        baseActivityTransition = new BaseActivityTransition(this);
        fileGet = new FileSave(MainActivity.this, Constants.GET);
        dbReference = FirebaseDBUtil.getDatebase().getReference();

        listenerVersion();
        notificationConfig();

        initView();
        addEvent();

        bottomNavigationViewEx = findViewById(R.id.navi_BottomBar);
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);


//        changeTextSizeBottomBar();
        CURRENT_PAGE = Constants.PRODUCTS_PAGE;

        addSelectAllCate();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, productCollectionsFragment, TAG_ALL_PRODUCT)
                .commit();
        fab.show();
        fab.setOnClickListener(productCollectionsFragment.onClickFloatButton.getInstance(MainActivity.this));

    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    private void listenerVersion() {

        dbReference.child(EntityFirebase.FieldVersions).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {
                    Version version = dataSnapshot.getValue(Version.class);

                    if (version != null && SplashActivity.versionApp < version.getApkVersion()) {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainContext);

                        alertDialog.setTitle(mainContext.getResources().getString(R.string.there_are_new_updates))
                                .setMessage(mainContext.getResources().getString(R.string.restart_the_application_to_use))
                                .setPositiveButton(getResources().getString(R.string.agree), (dialog1, which) -> {
                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }
                                });

                        if (version.isApkRequire()) {
                            alertDialog.setCancelable(false)
                                    .show();
                        } else {
                            alertDialog.setCancelable(true);
                            alertDialog.setNegativeButton(getResources().getString(R.string.close), (dialog1, which) -> dialog1.dismiss()).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("ResourceType")
    public void initView() {
        try {
            tvExpTime = findViewById(R.id.tv_exp_time);
            btnUpgrade = findViewById(R.id.btn_upgrade);
            filePut = new FileSave(this, Constants.PUT);
            btnDrawer = findViewById(R.id.btn_back_navigation);
            btnSort = findViewById(R.id.btn_save_navigation);
            edtSearch = findViewById(R.id.txt_search);
            from_search = findViewById(R.id.from_search);
            badgeViewFriend = new BadgeView(MainActivity.this);
            badgeViewNotification = new BadgeView(MainActivity.this);

            fab = findViewById(R.id.fab);

            fabBGLayout = findViewById(R.id.fabBGLayout);

            rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
            rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);

            drawer = findViewById(R.id.drawer_layout);

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            btnDrawer.setOnClickListener(v -> {
                if (drawer.isDrawerOpen(GravityCompat.START))
                    drawer.closeDrawer(GravityCompat.START);
                else
                    drawer.openDrawer(GravityCompat.START);
            });

            lvListSetup = navigationView.findViewById(R.id.lv_setup);
            lvListSetup.setExpanded(true);
            tvDisplayName = navigationView.findViewById(R.id.tv_display_name);
            ArrayList<String> list = new ArrayList<>();

            productCollectionsFragment = new ProductCollectionsFragment();
            customersFragment = new CustomersFragment();
            socialFragment = new SocialFragment();
            homeFriendFragment = new HomeFriendFragment();
            notificationsFragment = new NotificationsFragment();

            list.add(getString(R.string.product_interest));
            list.add(getString(R.string.setting));

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    R.layout.list_item,
                    R.id.tv_item
                    , list);

            lvListSetup.setAdapter(adapter);

            tvDisplayName.setText(fileGet.getDisplayName());

            tvDetailCalendar = findViewById(R.id.tv_detail_calendar);
            dataNotes = new ArrayList<>();
            mAdapter = new TimeLineTodayAdapter(this, dataNotes, true);
            rvNotes = findViewById(R.id.rv_notes_today);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setReverseLayout(true);
            rvNotes.setLayoutManager(mLayoutManager);
            rvNotes.setAdapter(mAdapter);

            switchButton = findViewById(R.id.switch_btn);
            switchButton.setTrackResource(R.drawable.private_mode_off);
            switchButton.setShowText(false);
            switchButton.setChecked(false);

            rvSelectGroupProduct = findViewById(R.id.rvSelectGroupProduct);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL, false);
            rvSelectGroupProduct.setLayoutManager(layoutManager);
//            groupProductAdapter = new SelectGroupProductHorizontalAdapter(this, Utilities.lsSelectGroupProduct);
//            rvSelectGroupProduct.setAdapter(groupProductAdapter);
            btnAddNote = findViewById(R.id.btn_add_note);

//            tvCountCustomer = findViewById(R.id.tv_count_customer);
//            tvCountFriend = findViewById(R.id.tv_count_friend);
//            tvCountProduct = findViewById(R.id.tv_count_product);
            imgAvatar = findViewById(R.id.img_avatar_user);
            hideSearch(false);
            GlideApp.with(this)
                    .load(!TextUtils.isEmpty(fileGet.getUserAvatar()) ? fileGet.getUserAvatar() : "")
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo)
                    .into(imgAvatar);
            fromTitle = findViewById(R.id.from_title);
            tvTitle = findViewById(R.id.title_toolbar);
            imgCover = findViewById(R.id.img_cover);
            GlideApp.with(this)
                    .load(!TextUtils.isEmpty(fileGet.getUserCover()) ? fileGet.getUserCover() : R.drawable.background_gradient)
                    .placeholder(R.drawable.noimage)
                    .error(R.drawable.background_gradient)
                    .into(imgCover);

            btnAdd = findViewById(R.id.btn_add);
            layoutProfile = findViewById(R.id.layout_profile);

//            CustomersController customersController = new CustomersController(this);
//            customersController.getCustomers(0, 1000,"", "", "", false, this);
            getDetail();

            Intent intent = getIntent();
            loadDataIntent(intent);
            hideSearch(true);
        } catch (Resources.NotFoundException e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void addEvent() {

        fabBGLayout.setOnClickListener(view -> {
        });

        btnSort.setOnClickListener(view -> {

            switch (CURRENT_PAGE) {
                case Constants.PRODUCTS_PAGE:
                    Intent intent = new Intent(this, FilterProductActivity.class);
                    startActivityForResult(intent, 1203);
                    break;
                case Constants.CONTACTS_PAGE:

                    break;
                case Constants.SOCIAL_PAGE:

                    socialFragment.nextFilterActivity();
                    Toast.makeText(mainContext, "Coming soon!", Toast.LENGTH_SHORT).show();
                    break;
                case Constants.FRIENDS_PAGE:
                    break;
                case Constants.NOTIFICATION_PAGE:

                    break;
                default:
                    break;
            }
        });

        mOnNavigationItemSelectedListener = item -> {
            switch (item.getItemId()) {
                case R.id.navigation_product:
                    bottomNavigationViewEx.setEnabled(false);
                    hideSearch(true);
                    btnSort.setVisibility(View.VISIBLE);
                    btnSort.setIconResource("\uf0b0");
                    if (CURRENT_PAGE.equals(Constants.PRODUCTS_PAGE)) {
                        rvSelectGroupProduct.setVisibility(View.GONE);
                        doubleBackStack();
                        return true;
                    }
                    CURRENT_PAGE = Constants.PRODUCTS_PAGE;

                    pushFragments(TAG_ALL_PRODUCT, productCollectionsFragment, 1);
                    rvSelectGroupProduct.setVisibility(View.GONE);
                    fab.hide();
                    fab.show();
                    fab.setImageResource(R.drawable.ic_add_black);
                    fab.setOnClickListener(productCollectionsFragment.onClickFloatButton.getInstance(MainActivity.this));
                    fromTitle.setVisibility(View.GONE);
                    bottomNavigationViewEx.setEnabled(true);
                    return true;

                case R.id.navigation_contact:
                    bottomNavigationViewEx.setEnabled(false);
                    MainActivity.edtSearch.setOnClickListener(null);
                    MainActivity.edtSearch.setFocusableInTouchMode(true);
                    MainActivity.edtSearch.setEnabled(true);
                    hideSearch(true);
                    btnSort.setVisibility(View.GONE);
                    if (CURRENT_PAGE.equals(Constants.CONTACTS_PAGE)) {
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        return true;
                    }
                    CURRENT_PAGE = Constants.CONTACTS_PAGE;

                    pushFragments(TAG_CUSTOMER, customersFragment, 3);
                    rvSelectGroupProduct.setVisibility(View.GONE);
                    fab.show();
                    fab.setImageResource(R.drawable.ic_person_add_white);
                    fab.setOnClickListener(v -> {
                        if (isFabOpen) {
                            fab.startAnimation(rotate_forward);
                            isFabOpen = true;
                        } else {
                            fab.startAnimation(rotate_backward);
                            isFabOpen = false;

                        }
                    });
                    fab.setOnClickListener(customersFragment.onClickFloatButton.getInstance(MainActivity.this));
                    fromTitle.setVisibility(View.GONE);
                    bottomNavigationViewEx.setEnabled(true);
                    return true;
                case R.id.navigation_social:
                    bottomNavigationViewEx.setEnabled(false);
                    hideSearch(false);
                    btnSort.setVisibility(View.VISIBLE);
                    btnSort.setIconResource("\uf15d");
                    if (CURRENT_PAGE.equals(Constants.SOCIAL_PAGE)) {
                        socialFragment.scrollTop();
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        return true;
                    }
                    CURRENT_PAGE = Constants.SOCIAL_PAGE;

                    pushFragments(TAG_SOCIAL, socialFragment, 5);
                    rvSelectGroupProduct.setVisibility(View.GONE);
                    fab.hide();
                    fromTitle.setVisibility(View.GONE);
                    bottomNavigationViewEx.setEnabled(true);
                    return true;
                case R.id.navigation_friend:
                    bottomNavigationViewEx.setEnabled(false);
                    edtSearch.setText("");
                    edtSearch.clearFocus();
                    edtSearch.setEnabled(false);
                    edtSearch.setFocusableInTouchMode(false);
                    edtSearch.setOnClickListener(v -> showDialogSearchFriend());
                    hideSearch(false);
                    btnSort.setVisibility(View.GONE);
                    addBadAt(3, 0);
                    if (CURRENT_PAGE.equals(Constants.FRIENDS_PAGE)) {
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        return true;

                    }
                    CURRENT_PAGE = Constants.FRIENDS_PAGE;

                    pushFragments(TAG_HOME_FRIEND, homeFriendFragment, 7);
                    rvSelectGroupProduct.setVisibility(View.GONE);
                    fab.hide();
                    fromTitle.setVisibility(View.GONE);
                    bottomNavigationViewEx.setEnabled(true);
                    return true;
                case R.id.navigation_notification:
                    bottomNavigationViewEx.setEnabled(false);
                    hideSearch(true);
                    btnSort.setVisibility(View.GONE);
                    tvTitle.setText(mainContext.getResources().getString(R.string.notification));
                    fromTitle.setVisibility(View.VISIBLE);
                    dbReference.child(EntityFirebase.TableCountNotifications).child(fileGet.getUserId()).setValue(0);

                    addBadAt(4, 0);
                    if (CURRENT_PAGE.equals(Constants.NOTIFICATION_PAGE)) {
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        return true;
                    }
                    CURRENT_PAGE = Constants.NOTIFICATION_PAGE;
                    pushFragments(TAG_NOTIFICATION, notificationsFragment, 9);
                    rvSelectGroupProduct.setVisibility(View.GONE);
                    fab.hide();
                    bottomNavigationViewEx.setEnabled(true);
                    return true;

            }
            return false;
        };

        lvListSetup.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    baseActivityTransition.transitionTo(new Intent(MainActivity.this, FavoriteProductsActivity.class), 0);
                    FAVORITE_PAGE = Constants.FAVORITE_PAGE;
                    fab.hide();

                    drawer.closeDrawer(GravityCompat.START);
                    break;
                case 1:
                    Intent intent = new Intent(this, SettingActivity.class);
                    baseActivityTransition.transitionTo(intent, Constants.LOGOUT_RESULT);
                    break;
                default:
                    drawer.closeDrawer(GravityCompat.START);
                    break;
            }
        });

        tvDetailCalendar.setOnClickListener(v -> {
            Intent calendar = new Intent(this, CalendarDetailActivity.class);
            baseActivityTransition.transitionTo(calendar, 0);
            drawer.closeDrawer(GravityCompat.START);
        });

        switchButton.setChecked(false);
        switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switchButton.setTrackResource(R.drawable.private_mode_on);
                if (!Utilities.isPrivateMode) {
                    dialog = new InputPasswordPrivateMode(this, this, Constants.PRIVATE_MODE, this);
                    dialog.setCancelable(false);
                    dialog.show();
                } else {
                    Utilities.changeStatusBarColor(this, Utilities.isPrivateMode);
                }
            } else {
                switchButton.setTrackResource(R.drawable.private_mode_off);
                Utilities.isPrivateMode = false;
                Utilities.changeStatusBarColor(this, Utilities.isPrivateMode);
            }

        });

        btnAddNote.setOnClickListener(view -> {
            // sang màn hình add note

            requestPermission();

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }

        });

        btnAdd.setOnClickListener(view -> btnAddNote.performClick());

        layoutProfile.setOnClickListener(v -> {
            if (FriendDetailsActivity.friend != null)
                FriendDetailsActivity.friend = null;
            Intent intent = new Intent(this, PersonalPageActivity.class);
            baseActivityTransition.transitionTo(intent, 0);
        });

        btnUpgrade.setOnClickListener(v -> Toast.makeText(mainContext, getResources().getString(R.string.comming_soon), Toast.LENGTH_SHORT).show());

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void backLikeHomeHardware() {
        moveTaskToBack(true);
    }

    @Override
    public void onBackPressed() {
        try {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            fromTitle.setVisibility(View.GONE);

            if (FAVORITE_PAGE.equalsIgnoreCase(Constants.FAVORITE_PAGE)) {
                FAVORITE_PAGE = "";
                FragmentManager ft = getSupportFragmentManager();
                Fragment fragment = ft.findFragmentByTag(TAG_FAVORITE);
                if (fragment != null) {
                    ft.popBackStack();
                }
                if (CURRENT_PAGE.equals(Constants.PRODUCTS_PAGE)) {
                    rvSelectGroupProduct.setVisibility(View.GONE);
                }

            } else {

                if (!CURRENT_PAGE.equals(Constants.PRODUCTS_PAGE)) {

                    if (CURRENT_PAGE.equalsIgnoreCase(Constants.FRIENDS_PAGE)) {
                        if (!FriendManagementFragment.back()) {
                            backLikeHomeHardware();
                        }
                    } else {
                        backLikeHomeHardware();
                    }

                }
//                else {
//                    rvSelectGroupProduct.setVisibility(View.GONE);
//                    if (Utilities.lsSelectGroupProduct.size() > 1) {
//
//                        if (fileGet.getCurrentCreateAtEndProduct()) {
//                            filePut.putCurrentCreateAtEndProduct(false);
//                            getSupportFragmentManager().popBackStack();
//                            backHeader();
//                        } else {
//                            productCollectionsFragment.collectionsProductRecyclerAdapter.backData();
//                            backHeader();
//                        }
//                    } else {
//
////                        productCollectionsFragment.reloadData();
//                        if (getSupportFragmentManager().findFragmentByTag("AllTabFragment") != null) {
//                            backLikeHomeHardware();
//                        } else {
//                            super.onBackPressed();
//                        }
//                    }
//                }
            }

            if (dialog != null) {
                if (dialog.isShowing()) {
                    switchButton.setChecked(false);
                }
            }

        } catch (Exception e) {
            backLikeHomeHardware();
        }

    }

    private void backHeader() {
//        fab.setOnClickListener(productCollectionsFragment.onClickFloatButton.getInstance(MainActivity.this));
//        if (Utilities.lsSelectGroupProduct.size() == 1) {
//            groupProductAdapter.notifyDataSetChanged();
//        } else {
//            Utilities.lsSelectGroupProduct.remove(Utilities.lsSelectGroupProduct.size() - 1);
//            groupProductAdapter.notifyDataSetChanged();
//        }
    }

    public void onBackStack(int position) {
        try {

            if (position == 0) {
                productCollectionsFragment.reloadData();
            } else {
                int sizeHeader = Utilities.lsSelectGroupProduct.size();
//                int sizeCollection = ProductCollectionsFragment.dataLists.size();
                if (fileGet.getCurrentCreateAtEndProduct()) {
                    filePut.putCurrentCreateAtEndProduct(false);
                    getSupportFragmentManager().popBackStack();
                }
                for (int i = position + 1; i < sizeHeader; i++) {
                    Utilities.lsSelectGroupProduct.remove(Utilities.lsSelectGroupProduct.size() - 1);
                }

//                groupProductAdapter.notifyDataSetChanged();
//                productCollectionsFragment.collectionsProductRecyclerAdapter.updateCollection(position, sizeCollection, true);
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d(getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }

    }

    @Override
    public void onBackStackChanged() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount > 0) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        } else {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == Constants.LOGOUT_RESULT) {
                removeListener();

                Intent login = new Intent(this, LoginActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
                finishAfterTransition();
            } else if (resultCode == Constants.RESULT_CODE_REMOVE_PRODUCT) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(FragmentTag.TAG_END_PRODUCT);
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            } else if (resultCode == Constants.RESULT_CODE_UPDATE_PRIVACY_TYPE) {
                SocialFragment fragment = (SocialFragment) getSupportFragmentManager().findFragmentByTag(TAG_SOCIAL);
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }

            } else if (resultCode == Constants.RESULT_CODE_FOLLOW_PRODUCT) {

                SocialFragment fragment = (SocialFragment) getSupportFragmentManager().findFragmentByTag(TAG_SOCIAL);
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }

            } else if (resultCode == Constants.RESULT_CODE_DELETE_EDIT) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_CUSTOMER);
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            } else if (resultCode == Constants.ADD_PRODUCT_RESULT) {
                Fragment fragmentCollectionProduct = getSupportFragmentManager().findFragmentByTag(TAG_ALL_PRODUCT);
                if (fragmentCollectionProduct != null) {
                    fragmentCollectionProduct.onActivityResult(requestCode, resultCode, data);
                }
            } else if (resultCode == Constants.EDIT_PRODUCT_RESULT) {
                Fragment fragmentCollectionProduct = getSupportFragmentManager().findFragmentByTag(TAG_ALL_PRODUCT);
                if (fragmentCollectionProduct != null) {
                    fragmentCollectionProduct.onActivityResult(requestCode, resultCode, data);
                }
            } else if (resultCode == Constants.RESULT_CODE_REMOVE_PRODUCT_V1) {
                Fragment fragmentCollectionProduct = getSupportFragmentManager().findFragmentByTag(TAG_ALL_PRODUCT);
                if (fragmentCollectionProduct != null) {
                    fragmentCollectionProduct.onActivityResult(requestCode, resultCode, data);
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeListener();

    }

    private void removeListener() {
        try {
            Utilities.isPrivateMode = false;
            switchButton.setChecked(false);
            if (valueEventListener != null) {
                queryNoti.removeEventListener(valueEventListener);
            }
            if (listenerMessPending != null) {
                querySubMessPending.removeEventListener(listenerMessPending);
            }

            if (listenerCountNotifications != null) {

                countNotifications.removeEventListener(listenerCountNotifications);
            }

//            if (registration != null){
//                registration.remove();
//            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 444:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AddCustomerFromContactActivity.firstPermission = true;
                }
                break;
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    Intent note = new Intent(MainActivity.this, AddNoteProductActivity.class);
                    baseActivityTransition.transitionTo(note, 0);
                } else {
                    // permission denied, boo! Disable the
                    showDialogMessage();
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showDialogMessage() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    Intent note = new Intent(MainActivity.this, AddNoteProductActivity.class);
                    baseActivityTransition.transitionTo(note, 0);
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Thông báo từ lịch đã bị từ chối").setPositiveButton("OK", dialogClickListener)
                .show();
    }

    public void pushFragments(String tag, Fragment fragment, int pos) {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

        fab.show();

        if (manager.findFragmentByTag(tag) == null) {
            if (!tag.equalsIgnoreCase(TAG_FAVORITE)) {
                transaction.add(R.id.container, fragment, tag);
            } else {
                transaction.add(R.id.container, fragment, tag)
                        .addToBackStack(tag);
            }
        }

        Fragment fragment1 = manager.findFragmentByTag(TAG_FRIEND_MANAGEMENT);
        if (fragment1 != null) {
            manager.popBackStack(TAG_FRIEND_MANAGEMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }


        Fragment fragmentHome = manager.findFragmentByTag(TAG_ALL_PRODUCT);
        Fragment fragmentCustomer = manager.findFragmentByTag(TAG_CUSTOMER);
        Fragment fragmentSocial = manager.findFragmentByTag(TAG_SOCIAL);
        Fragment fragmentFriend = manager.findFragmentByTag(TAG_HOME_FRIEND);
        Fragment fragmentNoti = manager.findFragmentByTag(TAG_NOTIFICATION);
        Fragment fragmentFacovrite = manager.findFragmentByTag(TAG_FAVORITE);
        Fragment fragmentDetailCalendar = manager.findFragmentByTag(TAG_DETAIL_CALENDAR);
        // Hide all Fragment
        if (fragmentHome != null) {
            transaction.hide(fragmentHome);
        }
        if (fragmentCustomer != null) {
            transaction.hide(fragmentCustomer);
        }

        if (fragmentSocial != null) {
            transaction.hide(fragmentSocial);
        }

        if (fragmentFriend != null) {
            transaction.hide(fragmentFriend);
        }

        if (fragmentNoti != null) {
            transaction.hide(fragmentNoti);
        }

        if (fragmentFacovrite != null) {
            transaction.hide(fragmentFacovrite);
        }

        if (fragmentDetailCalendar != null) {
            transaction.remove(fragmentDetailCalendar);
        }

        // Show  current Fragment
        switch (tag) {

            case "AllTabFragment":
//            case "EndProductFragment":
                if (fragmentHome != null) {
                    transaction.show(fragmentHome);
                }
                break;

            case "CustomersFragment":
                if (fragmentCustomer != null) {
                    transaction.show(fragmentCustomer);
                }
                break;

            case "SocialFragment":
                if (fragmentSocial != null) {
                    transaction.show(fragmentSocial);
                }
                break;

            case "FriendFragment":
                if (fragmentFriend != null) {
                    transaction.show(fragmentFriend);
                }
                break;

            case "NotificationsFragment":
                if (fragmentNoti != null) {
                    transaction.show(fragmentNoti);
                }
                break;

            case "FavoriteProductsFragment":
                if (fragmentFacovrite != null) {
                    transaction.show(fragmentFacovrite);
                }
                break;
        }

        transaction.commitAllowingStateLoss();
//        reveal(pos);
    }

    //android 8.0++
    private void notificationConfig() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                        channelName, NotificationManager.IMPORTANCE_LOW));
            }
        }

    }

    private void getNoteToday() {
        try {
            String dateNow = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            noteController.getNoteToday(0, 100, dateNow, dateNow, this);
        } catch (Exception e) {
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (BuildConfig.DEBUG) Log.w("TOKEN_HEADER", fileGet.getToken());
            filePut.putActiveMain(true);
            mainContext = this;
            try {
                getNoteToday();
                tvDisplayName.setText(fileGet.getDisplayName());
                String avatar = fileGet.getUserAvatar();
                String cover = fileGet.getUserCover();
                GlideApp.with(this)
                        .load(!TextUtils.isEmpty(avatar) ? avatar : R.drawable.ic_logo)
                        .placeholder(R.drawable.ic_logo)
                        .error(R.drawable.ic_logo)
                        .into(imgAvatar);
                GlideApp.with(this)
                        .load(!TextUtils.isEmpty(cover) ? cover : R.drawable.background_gradient)
                        .placeholder(R.drawable.noimage)
                        .error(R.drawable.background_gradient)
                        .into(imgCover);
                //            Long currentTime = System.currentTimeMillis();
                //            Long minus = currentTime - fileGet.getTimeRefreshToken();
                //            if (minus > 3000000) {
                //                Utilities.refreshToken(this);
                //            }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                Crashlytics.logException(e);
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public Badge addBadAt(int position, int number) {
        if (position == 3) {
            return badgeViewFriend
                    .setBadgeNumber(number)
                    .setGravityOffset(12, 2, true)
                    .bindTarget(bottomNavigationViewEx.getBottomNavigationItemView(position))
                    .setOnDragStateChangedListener((dragState, badge, targetView) -> {
//                        if (Badge.OnDragStateChangedListener.STATE_SUCCEED == dragState)
                    });
        } else if (position == 4) {
            return badgeViewNotification
                    .setBadgeNumber(number)
                    .setGravityOffset(12, 2, true)
                    .bindTarget(bottomNavigationViewEx.getBottomNavigationItemView(position))
                    .setOnDragStateChangedListener((dragState, badge, targetView) -> {
//                        if (Badge.OnDragStateChangedListener.STATE_SUCCEED == dragState)
                    });
        } else {
            return null;
        }
    }

    public void subscribeNotification() {

        try {
            countNotifications = dbReference.child(EntityFirebase.TableCountNotifications).child(fileGet.getUserId());
            listenerCountNotifications = countNotifications.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot subDataSnapshot) {
                    if (subDataSnapshot.getValue() != null) {
                        Long countTemp = (Long) subDataSnapshot.getValue();
                        String strTemp = countTemp + "";
                        int count = Integer.parseInt(strTemp);
                        addBadAt(4, count);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } catch (NumberFormatException e) {
            Crashlytics.logException(e);
        }

        try {
            queryNoti = dbReference.child(EntityFirebase.TableNotifications).child(fileGet.getUserId()).orderByKey()
                    .limitToLast(1);

            valueEventListener = queryNoti.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    try {
                        try {
                            if (notificationsFragment == null)
                                notificationsFragment = new NotificationsFragment();

                            Notification notification = dataSnapshot.getValue(Notification.class);
                            if (notification != null) {
                                notification.notification_id = dataSnapshot.getKey();
                            }
                            if (notificationsFragment.dataNotifications == null) {
                                notificationsFragment.dataNotifications = new ArrayList<>();
                            }


//                            if (notificationsFragment.notificationsAdapterNew == null) {
//                                notificationsFragment.notificationsAdapterNew = new NotificationsAdapter(notificationsFragment.getContext(), notificationsFragment.dataNotifications,
                            //
//                                        notificationsFragment.listFriendRequest, false);
//                            }
                            notificationsFragment.dataNotifications.add(0, notification);
                            if (notificationsFragment.notificationsAdapter == null) {
                                notificationsFragment.notificationsAdapter = new NotificationsAdapter(notificationsFragment.getContext(), notificationsFragment.dataNotifications,
                                        notificationsFragment.listFriendRequest, notificationsFragment.callBackAcceptFriend,
                                        notificationsFragment.callBackUnFriend);
                            }
                            notificationsFragment.notificationsAdapter.notifyDataSetChanged();
//                            notificationsFragment.notificationsAdapterNew.notifyDataSetChanged();
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                        }


                        //                    Utilities.pushNotificatonLocal(MainActivity.this, title, body);
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                        Crashlytics.logException(e);
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (BuildConfig.DEBUG)
                        Log.d(getClass().getSimpleName(), databaseError.getMessage());
                }
            });
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

    }

    private void subsribeMessages() {
        try {
            querySubMessPending = dbReference.child(EntityFirebase.TableLastMessages).child(fileGet.getUserId())
                    .orderByChild(EntityFirebase.FieldIsPending).equalTo(true);
            listenerMessPending = querySubMessPending.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        lstAvatarPending = new ArrayList<>();

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            LastMessage lastMessage = dataSnapshot1.getValue(LastMessage.class);
                            if (lastMessage != null) {
                                lstAvatarPending.add(lastMessage.sender_avatar);
                            }
                        }

                        countPendingMessage = (int) dataSnapshot.getChildrenCount();
                        if (dataSnapshot.getChildrenCount() > 0) {
                            HistoryMessages.updateCountPendingMessage(MainActivity.this, countPendingMessage, lstAvatarPending);
                            addBadAt(3, countPendingMessage);
                        } else {
                            HistoryMessages.updateCountPendingMessage(MainActivity.this, countPendingMessage, lstAvatarPending);
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                        Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (BuildConfig.DEBUG)
                        Log.d("" + getClass().getName(), databaseError.getMessage());
                }
            });
        } catch (Exception ignored) {

        }

    }

    @SuppressLint("SetTextI18n")
    public void updateCountPendingMessage(View layout, TextView textView) {
        try {
            if (countPendingMessage == 0) {
                layout.setVisibility(View.GONE);
                textView.setText("");
            } else {
                layout.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.text_you_have) + " " + countPendingMessage + " " + getString(R.string.text_count_message_pending));
            }
        } catch (Exception ignored) {

        }
    }

    public void hideSearch(boolean isHide) {
        if (isHide) {
            edtSearch.setEnabled(false);
            edtSearch.setVisibility(View.GONE);
            from_search.setBackground(null);
            from_search.setVisibility(View.INVISIBLE);
        } else {
            edtSearch.setEnabled(true);
            edtSearch.setVisibility(View.VISIBLE);
            from_search.setBackgroundResource(R.drawable.border_radius_txtsearch);
            from_search.setVisibility(View.VISIBLE);
        }

    }

    private void doubleBackStack() {
        if (FAVORITE_PAGE.equalsIgnoreCase(Constants.FAVORITE_PAGE)) {
            FAVORITE_PAGE = "";
            FragmentManager ft = getSupportFragmentManager();
            Fragment fragment = ft.findFragmentByTag(TAG_FAVORITE);
            if (fragment != null) {
                ft.popBackStack();
                rvSelectGroupProduct.setVisibility(View.GONE);
            }
        } else {
            if (mBackPressed + 500 > System.currentTimeMillis()) {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                productCollectionsFragment.reloadData();
            } else {
                if (Utilities.lsSelectGroupProduct.size() > 1) {
                    onBackStack(MainActivity.positionGroupProductHorizontalAdapter);

                } else {
                    productCollectionsFragment.reloadData();
                }

            }

            mBackPressed = System.currentTimeMillis();
        }

    }

    public void addSelectAllCate() {
        Utilities.lsSelectGroupProduct.clear();
        ProductCountResponse p = new ProductCountResponse();
        p.setCatid("-1");
        p.setCatName(mainContext.getString(R.string.product));
        Utilities.lsSelectGroupProduct.add(p);

//        groupProductAdapter.notifyDataSetChanged();
    }

    @SuppressLint("StaticFieldLeak")
    private void getDetail() {
        UserController userController = new UserController(this, this);
        userController.getDetail();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadDataIntent(intent);
    }

    private void loadDataIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            try {
                String type = bundle.getString("type_noti");
                String key = bundle.getString("key_noti");

                if (!TextUtils.isEmpty(type)) {

                    switch (Integer.parseInt(type)) {
                        case 1:
                            //share note private
                            String[] str = key != null ? key.split(":") : new String[0];
                            String productKey = str[0];
                            String uid = str[1];
                            FileSave filePut = new FileSave(this, Constants.PUT);
                            filePut.putProductIdCurrentSelect(productKey);
                            Intent detail = new Intent(this, DetailProductActivity.class);
                            if (!Utilities.isPrivateMode) {
                                detail.putExtra(Constants.TURN_ON_PRIVATE_MODE, true);
                            }
                            detail.putExtra(Constants.TEMP_PRODUCT_KEY, productKey);
                            detail.putExtra(Constants.TEMP_PRODUCT_UID, uid);
                            detail.putExtra(Constants.OPEN_FROM_NOTI, true);
                            startActivity(detail);
                            break;

                        case 2:
                            //send request

                            String[] itemsRequest = new String[0];
                            if (key != null) {
                                itemsRequest = key.split(":");
                            }

                            UserRetro friend = new UserRetro();
                            friend.setUid(itemsRequest[0]);
                            friend.setUsername(itemsRequest[1]);
                            friend.setDisplayname(itemsRequest[2]);

                            Intent request = new Intent(this, FriendDetailsActivity.class);
                            request.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, friend);
                            startActivity(request);

                            break;

                        case 3:
                            //accept friend

                            String[] itemsAccept = new String[0];
                            if (key != null) {
                                itemsAccept = key.split(":");
                            }

                            UserRetro friend1 = new UserRetro();
                            friend1.setUid(itemsAccept[0]);
                            friend1.setUsername(itemsAccept[1]);
                            friend1.setDisplayname(itemsAccept[2]);

                            Intent accept = new Intent(this, FriendDetailsActivity.class);
                            accept.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, friend1);
                            startActivity(accept);

                            break;

                        case 4:
                            //comment
                            String[] keyCommentTemp = new String[0];
                            if (key != null) {
                                keyCommentTemp = key.split(",");
                            }
                            String productId = keyCommentTemp[0];
                            String productName = keyCommentTemp[1];
                            CommentDialog commentDialog = new CommentDialog(mainContext, productId, productName);
                            commentDialog.show();
                            break;

                        case 5:
                            //message

                            try {
                                hideSearch(true);
//                                btnSort.setVisibility(View.GONE);
                                addBadAt(3, 0);
                                if (CURRENT_PAGE.equals(Constants.FRIENDS_PAGE)) {
                                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                }
                                CURRENT_PAGE = Constants.FRIENDS_PAGE;

                                pushFragments(TAG_HOME_FRIEND, homeFriendFragment, 7);
                                rvSelectGroupProduct.setVisibility(View.GONE);
                                fab.hide();
                                fromTitle.setVisibility(View.GONE);
                                bottomNavigationViewEx.setCurrentItem(3);
                            } catch (Exception e) {
                                Log.e(getClass().getName(), e.toString());
                            }

                            if (key != null) {
                                String[] friendTemp = key.split(",");
                                UserRetro f = new UserRetro();
                                f.RoomChatId = friendTemp[0];
                                f.setUid(friendTemp[1]);
                                f.setDisplayname(friendTemp[2]);

                                try {
                                    Intent chat = new Intent(MainActivity.this, ChatActivity.class);
                                    chat.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, f);
                                    chat.putExtra(Constants.KEY_PASSINGDATA_ROOM_ID, friendTemp[0]);
                                    chat.putExtra(Constants.KEY_IS_PENDING, false);
                                    startActivity(chat);
                                } catch (Exception e) {
                                    Crashlytics.logException(e);
                                }
                            }

                            break;
                    }

                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                Crashlytics.logException(e);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void showNote() {

    }

    public void requestPermission() {
        int checkReadCal = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        int checkWriteCal = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        int permissionGranted = PackageManager.PERMISSION_GRANTED;

        String[] permissions = new String[]{
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR};
        //check Android 6+
        if (Build.VERSION.SDK_INT >= 23) {
            //check permission granted
            if (checkReadCal != permissionGranted || checkWriteCal != permissionGranted) {
                //request Permissions
                ActivityCompat.requestPermissions(this, permissions, 1);
            } else {
                Intent note = new Intent(MainActivity.this, AddNoteProductActivity.class);
                baseActivityTransition.transitionTo(note, 0);
            }
        } else {
            Intent note = new Intent(MainActivity.this, AddNoteProductActivity.class);
            baseActivityTransition.transitionTo(note, 0);
        }
    }

    @Override
    public void onGetDetail(UserRetro userRetro) {

        try {
            if (userRetro.getExpirationTime() != null) {
                filePut.putExpirationTime(userRetro.getExpirationTime());
                String exp = Utilities.convertTimeMillisToDateStringddMMyyyyHHmmss(userRetro.getExpirationTime());
                String[] times = exp.split(" ");
                tvExpTime.setText(times[0]);
            }
//
            Intent enterWEB = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.LINK_ACTIVE_CODE));
            String CHANNEL_ID = "csell.com.vn.csell.ONE";
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationManager notificationManager;
            NotificationCompat.Builder notificationBuilder;
            String contentTitle = "Tài khoản hết hạn";
            String contentText = "Tài khoản của bạn đã hết hạn sử dụng, chạm để kích hoạt lại";
            PendingIntent pendingIntent = PendingIntent.getActivity(mainContext, 0, enterWEB, 0);

            long timeRemainingByMs = userRetro.getExpirationTime() - userRetro.getDateSystem();

            if (timeRemainingByMs > 0) {
                int timeRemainingByDay = (int) (timeRemainingByMs / (1000 * 60 * 60 * 24));

                if (timeRemainingByDay < 30) {
                    // Create the NotificationChannel, but only on API 26+ because
                    // the NotificationChannel class is new and not in the support library
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            CharSequence name = "title";
                            String description = "description";
                            int importance = NotificationManager.IMPORTANCE_DEFAULT;
                            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                            channel.setDescription(description);
                            // Register the channel with the system; you can't change the importance
                            // or other notification behaviors after this
                            notificationManager = getSystemService(NotificationManager.class);
                            if (notificationManager != null) {
                                notificationManager.createNotificationChannel(channel);
                            }

                            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_csell_logo_notification)
                                    .setContentTitle(contentTitle)
                                    .setContentText(contentText)
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .bigText(contentText))
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);
                        } else {
                            notificationBuilder =
                                    new NotificationCompat.Builder(this, CHANNEL_ID)
                                            .setSmallIcon(R.drawable.ic_csell_logo_notification)
                                            .setContentTitle(contentTitle)
                                            .setContentText(contentText)
                                            .setStyle(new NotificationCompat.BigTextStyle()
                                                    .bigText(contentText))
                                            .setAutoCancel(true)
                                            .setSound(defaultSoundUri)
                                            .setContentIntent(pendingIntent);

                            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        }

                        if (notificationManager != null) {
                            notificationManager.notify("CSELL", 0 /* ID of notification */, notificationBuilder.build());
                        }
                    } catch (Exception ignored) {

                    }
                }
            } else {
                new AlertDialog.Builder(mainContext)
                        .setTitle(contentTitle)
                        .setMessage(contentText)
                        .setCancelable(false)
                        .setPositiveButton(mainContext.getString(R.string.agree), (dialog1, which) -> startActivity(enterWEB)).show();
            }

            subsribeMessages();
            getNotifications();
            subscribeNotification();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }


    }

    private void getNotifications() {

        if (notificationsFragment.listFriendRequest == null) {
            notificationsFragment.listFriendRequest = new ArrayList<>();
        }

        if (notificationsFragment.dataNotifications == null) {
            notificationsFragment.dataNotifications = new ArrayList<>();
        }


        if (Utilities.isNetworkConnected(MainActivity.mainContext)) {
            if (Utilities.getInetAddressByName() != null) {
                getListFriendRequest();
            } else {
                if (!TextUtils.isEmpty(fileGet.getCacheNotiRequest())) {

                    notificationsFragment.listFriendRequest.clear();
                    notificationsFragment.listFriendRequest.addAll(new Gson().fromJson(fileGet.getCacheNotiRequest(),
                            new TypeToken<ArrayList<FriendResponse>>() {
                            }.getType()));
                }
            }
        } else {
            if (!TextUtils.isEmpty(fileGet.getCacheNotiRequest())) {
                notificationsFragment.listFriendRequest.clear();
                notificationsFragment.listFriendRequest.addAll(new Gson().fromJson(fileGet.getCacheNotiRequest(),
                        new TypeToken<ArrayList<FriendResponse>>() {
                        }.getType()));
            }
        }

        Query query1;

        dbReference.child(EntityFirebase.TableNotifications).keepSynced(true);
        query1 = dbReference.child(EntityFirebase.TableNotifications).child(fileGet.getUserId()).orderByKey()
                .limitToLast(10);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    notificationsFragment.dataNotifications.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        Notification notification = item.getValue(Notification.class);
                        if (notification != null) {
                            notification.notification_id = item.getKey();
                        }
                        if (notificationsFragment.dataNotifications.size() == 0) {
                            notificationsFragment.dataNotifications.add(notification);
                        } else {
                            notificationsFragment.dataNotifications.add(0, notification);
                        }
                    }
                    NotificationsFragment.lastkey = notificationsFragment.dataNotifications
                            .get(notificationsFragment.dataNotifications.size() - 1).notification_id;

                    if (notificationsFragment.dataNotifications.size() > 0) {
                        filePut.putCacheNoti(new Gson().toJson(notificationsFragment.dataNotifications));
                    }

                    notificationsFragment.reloadDataNoti();

                } catch (Exception e) {
                    Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (BuildConfig.DEBUG)
                    Log.d("" + getClass().getName(), databaseError.getMessage());
            }
        });
    }

    private void getListFriendRequest() {
        try {
            friendsController.getFriendsRequest(0, 2, this);
        } catch (Exception ignored) {

        }
    }

    private void showDialogSearchFriend() {
        Intent intent = new Intent(mainContext, FindFriendsActivity.class);
        baseActivityTransition.transitionTo(intent, 0);
    }

    @Override
    public void onGetCustomersSuccess(List<CustomerRetroV1> data) {

    }

    @Override
    public void onGetRecentCustomersSuccess(List<CustomerRetroV1> data) {

    }

    @Override
    public void onGetCustomersFailure(boolean listRecent) {

    }

    @Override
    public void onConnectGetCustomersFailure(boolean listRecent) {

    }

    @Override
    public void onGetFriendsRequestSuccess(List<FriendResponse> data) {
        try {
            notificationsFragment.listFriendRequest.clear();
            notificationsFragment.listFriendRequest.addAll(data);
            filePut.putCacheNotiRequest(new Gson().toJson(notificationsFragment.listFriendRequest));
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGetFriendsRequestFailure() {

    }

    @Override
    public void onConnectGetFriendsRequestFailure() {

    }

    @Override
    public void onGetNoteTodaySuccess(Response<JSONResponse<List<Note>>> response) {
        try {
            if (response.body() != null && response.body().getSuccess() != null) {
                if (response.body().getSuccess()) {
                    dataNotes.clear();
//                    dataNotes.addAll(response.body().getData());
                    Utilities.sortListNote(dataNotes);
                    mAdapter.notifyDataSetChanged();

                    if (dataNotes.size() < 5) {
                        rvNotes.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                    } else {
                        float height = getResources().getDimension(R.dimen.height_rv_note);
                        ViewGroup.LayoutParams params_new = rvNotes.getLayoutParams();
                        params_new.height = (int) height;
                        rvNotes.setLayoutParams(params_new);
                    }

                } else {
                    String msg = response.body().getMessage();
                    if (msg.equals(getResources().getString(R.string.text_error_session_expired))) {
                        //refresh token
                        removeListener();

                        Intent login = new Intent(MainActivity.this, LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);
                        finishAfterTransition();
                    } else {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGetNoteTodayFailure(Response<JSONResponse<List<Note>>> response) {

    }

    @Override
    public void onConnectGetNoteTodayFailure() {

    }

    @Override
    public void onLoginSuccess(ResLogin response) {
        try {
//            dialog.handeResponseSuccess(response);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onErrorLogin() {

    }

    @Override
    public void onLoginFailure(String account) {
        try {
            dialog.handleResponseFailure();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onConnectLoginFailure() {
        dialog.handleResponseConnectFailure();
    }
}
