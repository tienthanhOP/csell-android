package csell.com.vn.csell.views.product.activity;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import csell.com.vn.csell.controllers.FriendsController;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.sqlites.SQLFriends;
import csell.com.vn.csell.views.friend.adapter.FriendAdapter;
import csell.com.vn.csell.views.product.fragment.EndCreateFragment;
import csell.com.vn.csell.views.product.fragment.TabDetailProductFragment;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;

//import com.google.firebase.firestore.FirebaseFirestore;

public class ShareNotePrivateActivity extends AppCompatActivity implements FriendsController.OnGetFriendsListener {

    String contentNote = "";
    ArrayList<UserRetro> list = null;
    private FancyButton btnBackNavigation;
    private FancyButton btnSaveNavigation;
    private TextView titleToolbar;
    private RecyclerView rvFriends;
    //    private SwipeRefreshLayout loading_refreshing;
    private TextInputEditText edtNote;
    private EditText edtSearch;
    private SQLFriends sqlFriends;
    private ArrayList<UserRetro> lsFriends;
    private FileSave fileGet;
//    private boolean isCreatePrivateNote = false;
    private FriendAdapter friendAdapter;
    //private FirebaseFirestore mFireStoreRef;
    private TextInputLayout txtInptError;
    private ProgressBar progressBar;
    private LinearLayout layout;

    private boolean isEditPrivateNoteInDetail;
    private boolean isEditPrivateNoteReup;
    private boolean isEditEndCreateFragment;
    private boolean isAddPrivateNote = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_note_private);
        Fabric.with(this, new Crashlytics());
        Utilities.lsFriendsNotePrivate.clear();
        sqlFriends = new SQLFriends(this);
        if (!sqlFriends.checkExistData()) {
            try {
                FriendsController friendsController = new FriendsController(this);
                friendsController.getFriends(0, 5000, this);
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.d(getClass().getSimpleName(), e.getMessage());
                Crashlytics.logException(new Exception(getClass().getSimpleName() + "/" + e));
            }
        }
        lsFriends = new ArrayList<>();
        list = new ArrayList<>();
        fileGet = new FileSave(this, Constants.GET);
        initView();
        setupWindowAnimations();
        initEvent();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    private void initView() {
        txtInptError = findViewById(R.id.txtInptError);
        btnBackNavigation = findViewById(R.id.btn_back_navigation);
        btnBackNavigation.setText(getString(R.string.title_back_vn));
        btnSaveNavigation = findViewById(R.id.btn_save_navigation);
        btnSaveNavigation.setVisibility(View.VISIBLE);
        btnSaveNavigation.setText(getString(R.string.done));
        titleToolbar = findViewById(R.id.custom_TitleToolbar);
        titleToolbar.setText(getString(R.string.add_private_content));
        rvFriends = findViewById(R.id.rvFriends);
//        loading_refreshing = findViewById(R.id.loading_refreshing);
        edtSearch = findViewById(R.id.edtSearch);
        edtNote = findViewById(R.id.edt_note_private);
        edtNote.requestFocus();
        rvFriends.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvFriends.setLayoutManager(mLayoutManager);
        progressBar = findViewById(R.id.progress_bar);
        layout = findViewById(R.id.layout);
        lsFriends.clear();
//        Chua lam chuc nang ban be
//        lsFriends.addAll(sqlFriends.getAllFriend1());
        friendAdapter = new FriendAdapter(this, lsFriends);
        rvFriends.setAdapter(friendAdapter);
        loadData();
    }


    private void initEvent() {

        btnBackNavigation.setOnClickListener(v -> {
            Utilities.hideSoftKeyboard(ShareNotePrivateActivity.this);
            setResult(-10);
            finishAfterTransition();
        });

        btnSaveNavigation.setOnClickListener(v -> {
            Utilities.hideSoftKeyboard(this);
            if (TextUtils.isEmpty(edtNote.getText().toString()) && Utilities.lsFriendsNotePrivate.size() == 0) {
                Utilities.contentPrivateNote = "";
                Utilities.lsFriendsNotePrivate.clear();

                if (isEditEndCreateFragment) {
                    EndCreateFragment.updateViewNoteFriend(Utilities.lsFriendsNotePrivate);
                } else if (isEditPrivateNoteInDetail) {
                    TabDetailProductFragment.updateViewNoteFriend(Utilities.lsFriendsNotePrivate);
                    TabDetailProductFragment.updatePrivateNote();
                    showProgress(false);
                    finishAfterTransition();
                } else if (isEditPrivateNoteReup) {
                    EditProductActivity.updateViewNoteFriend(Utilities.lsFriendsNotePrivate);
                }

                finishAfterTransition();
                return;
            }

            if (TextUtils.isEmpty(edtNote.getText().toString())) {
                txtInptError.setError(getString(R.string.text_error_content_note_private));
                edtNote.requestFocus();
                return;
            }

//            Chua lam chuc nang ban be
//            if (Utilities.lsFriendsNotePrivate.size() == 0) {
//                txtInptError.setError(getString(R.string.text_error_friend_note_private));
//                return;
//            }

            txtInptError.setError(null);


            Utilities.contentPrivateNote = edtNote.getText().toString();
            showProgress(true);
            if (isAddPrivateNote) {
                try {

                    if (EndCreateFragment.fromNotePrivateAndFriend != null) {
                        EndCreateFragment.updateViewNoteFriend(Utilities.lsFriendsNotePrivate);
                    } else if (TabDetailProductFragment.fromNotePrivateAndFriend != null) {
                        TabDetailProductFragment.updateViewNoteFriend(Utilities.lsFriendsNotePrivate);
                        TabDetailProductFragment.updatePrivateNote();
                    } else if (EditProductActivity.fromNotePrivateAndFriend != null) {
                        EditProductActivity.updateViewNoteFriend(Utilities.lsFriendsNotePrivate);
                    }
                } catch (Exception e) {
                    //re-up
                    if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
                    Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                    showProgress(false);
                }
                showProgress(false);
                finishAfterTransition();

            } else {

                if (isEditPrivateNoteInDetail) {
                    updateNotePrivate();
                    TabDetailProductFragment.updateViewNoteFriend(Utilities.lsFriendsNotePrivate);
                    TabDetailProductFragment.updatePrivateNote();
                    showProgress(false);
                    finishAfterTransition();
                } else if (isEditPrivateNoteReup) {
                    checkAndAddNotePrivateReupProduct();
                    EditProductActivity.updateViewNoteFriend(Utilities.lsFriendsNotePrivate);
                    showProgress(false);
                    finishAfterTransition();
                } else if (isEditEndCreateFragment) {
                    checkAndAddNotePrivateNewProduct();
                    EndCreateFragment.updateViewNoteFriend(Utilities.lsFriendsNotePrivate);
                    showProgress(false);
                    finishAfterTransition();
                }

            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                friendAdapter.findFriend(s.toString().trim());
            }
        });

//        loading_refreshing.setOnRefreshListener(() -> {
//
//            lsFriends.clear();
//            lsFriends.addAll(sqlFriends.getAllFriend1());
//            friendAdapter.notifyDataSetChanged();
//            loading_refreshing.setRefreshing(false);
//
//        });
    }

    private void checkAndAddNotePrivateNewProduct() {
        if (SelectCategoryActivity.paramsProduct != null) {
            SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_NOTE_PRIVATE, edtNote.getText().toString());
            List<UserRetro> listMap = new ArrayList<>(Utilities.lsFriendsNotePrivate);

            SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_CAN_SEE_NOTE_PRIVATE, listMap);
        }
    }

    private void checkAndAddNotePrivateReupProduct() {
        if (EditProductActivity.paramsProduct != null) {
            EditProductActivity.paramsProduct.put(EntityAPI.FIELD_NOTE_PRIVATE, edtNote.getText().toString());
            List<UserRetro> listMap = new ArrayList<>(Utilities.lsFriendsNotePrivate);
            EditProductActivity.paramsProduct.put(EntityAPI.FIELD_CAN_SEE_NOTE_PRIVATE, listMap);
        }
    }


    private void updateNotePrivate() {
        Utilities.contentPrivateNote = edtNote.getText().toString().trim();
    }

    private void checkAddNotePrivate(String productId, String productName) {
        if (Utilities.lsFriendsNotePrivate.size() != 0) {
            PushNotifications push = new PushNotifications(this);
            for (UserRetro item : Utilities.lsFriendsNotePrivate) {
                HashMap<String, Object> data = new HashMap<>();
                data.put(EntityAPI.FIELD_NOTIFICATION_TITLE, "Share Note Private");
                data.put(EntityAPI.FIELD_NOTIFICATION_BODY, this.getString(R.string.text_body_shared_note_private)
                        + " "
                        + fileGet.getDisplayName() + " "
                        + this.getString(R.string.text_body_in) + " "
                        + productName);
                data.put(EntityAPI.FIELD_NOTIFICATION_TARGET_NAME, productName);
                data.put(EntityAPI.FIELD_NOTIFICATION_DATE_CREATED, ServerValue.TIMESTAMP);
                data.put(EntityAPI.FIELD_NOTIFICATION_DATA, productId);
                data.put(EntityAPI.FIELD_NOTIFICATION_SENDER, fileGet.getUserId());
                data.put(EntityAPI.FIELD_NOTIFICATION_SENDER_NAME, fileGet.getDisplayName());
                data.put(EntityAPI.FIELD_NOTIFICATION_SENDER_AVATAR, fileGet.getUserAvatar());
                data.put(EntityAPI.FIELD_NOTIFICATION_IS_SEEN, false);
                if (!TextUtils.isEmpty(item.getUid()))
                    push.pushAddNotePrivate(data, item.getUid());
            }
        }
    }

    private void loadData() {


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
//            Utilities.lsFriendsNotePrivate.clear();

            //edit private note
            isEditPrivateNoteInDetail = bundle.getBoolean(Constants.IS_EDIT_PRIVATE_NOTE_DETAIL_PRODUCT, false);
            isEditPrivateNoteReup = bundle.getBoolean(Constants.IS_EDIT_PRIVATE_NOTE_REUP_PRODUCT, false);
            isEditEndCreateFragment = bundle.getBoolean(Constants.IS_EDIT_PRIVATE_NOTE_CREATE_PRODUCT, false);

            if (isEditPrivateNoteInDetail || isEditPrivateNoteReup || isEditEndCreateFragment) {
                titleToolbar.setText(getString(R.string.title_toolbar_edit_note_private));
            }

            //create private note
            isAddPrivateNote = bundle.getBoolean(Constants.KEY_ADD_NOTE_PRIVATE, false);

            contentNote = bundle.getString(Constants.KEY_PASSINGDATA_NOTE_PRIVATE);
            list = (ArrayList<UserRetro>) bundle.getSerializable(Constants.KEY_PASSINGDATA_LIST_FRIEND_SHARE_NOTE);

            edtNote.setText(!TextUtils.isEmpty(contentNote) ? contentNote : "");
            edtNote.setSelection(!TextUtils.isEmpty(contentNote) ? contentNote.length() : 0);

            if (list != null) {
                for (UserRetro friend : lsFriends) {
                    for (int i = 0; i < list.size(); i++) {

                        if (friend.getUid().equals(list.get(i).getUid())) {
                            friend.IsSelected = true;
                            Utilities.lsFriendsNotePrivate.add(friend);
                        }
                    }
                }
            }

        }
    }

    private void showProgress(boolean check) {
//        if (check) {
//            progressBar.setVisibility(View.VISIBLE);
//            layout.setEnabled(false);
//            layout.setAlpha(0.5f);
//        } else {
//            progressBar.setVisibility(View.GONE);
//            layout.setEnabled(true);
//            layout.setAlpha(1f);
//        }
    }

    @Override
    public void onGetFriendsSuccess(ArrayList<UserRetro> data) {
        try {
            lsFriends.clear();
            lsFriends.addAll(data);
            friendAdapter.notifyDataSetChanged();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGetFriendsFailure() {

    }

    @Override
    public void onConnectGetFriendsFailure() {

    }
}
