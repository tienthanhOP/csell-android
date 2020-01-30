package csell.com.vn.csell.views.customer.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.customer.adapter.UserGroupFirebaseAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.models.GroupCustomerRetro;
import csell.com.vn.csell.sqlites.SQLCustomers;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupCustomerDetailActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvAvatar;
    @SuppressLint("StaticFieldLeak")
    public static TextView tvNumberCustomers;
    private FancyButton btnBack, btnSave;
    private Button btnAdd;
    private RecyclerView rvListUser;
    private UserGroupFirebaseAdapter mAdapter;

    private LinearLayout btnSendMessage, btnSendEmail;

    private String key;
    private GroupCustomerRetro groupNew;

    public static List<String> emailList;
    public static String phoneList = "";
    private ArrayList<Object> data;
    public static SQLCustomers sqlCustomers;
    private FileSave fileGet;
    private ProgressBar progressBar;
    private RelativeLayout layoutContent;
    private boolean firstLoad;
    private BaseActivityTransition baseActivityTransition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        Fabric.with(this, new Crashlytics());

        initView();
        setupWindowAnimations();
        loadData();
        addEvent();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    @Override
    protected void onDestroy() {
        GroupCustomerDetailActivity.emailList.clear();
        GroupCustomerDetailActivity.phoneList = "";
        super.onDestroy();
    }

    private void hideProgressBar(boolean b) {
        if (b) {
            if (firstLoad) {
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                    layoutContent.setAnimation(animation);
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
    }

    private void initView() {
        baseActivityTransition = new BaseActivityTransition(this);

        progressBar = findViewById(R.id.progress_bar);
        layoutContent = findViewById(R.id.layout_content);
        firstLoad = true;
        hideProgressBar(false);

        fileGet = new FileSave(this, Constants.GET);
        sqlCustomers = new SQLCustomers(this);
        tvName = findViewById(R.id.tv_name_group);
        tvNumberCustomers = findViewById(R.id.tv_number_customer);
        tvAvatar = findViewById(R.id.tv_avatar);
        btnBack = findViewById(R.id.btn_back_navigation);
        btnBack.setText(getString(R.string.title_back_vn));
        btnBack.setText(MainActivity.mainContext.getResources().getString(R.string.customer));
        btnSave = findViewById(R.id.btn_save_navigation);
        btnSave.setIconResource(getString(R.string.font_icon_toolbar_right_edit));
        btnSave.setText(MainActivity.mainContext.getResources().getString(R.string.edit));
        btnAdd = findViewById(R.id.btn_add);
        rvListUser = findViewById(R.id.rv_list_user_group);
        rvListUser.setLayoutManager(new LinearLayoutManager(this));
        groupNew = new GroupCustomerRetro();
        btnSendEmail = findViewById(R.id.btn_send_email_group);
        btnSendMessage = findViewById(R.id.btn_send_message_group);
        emailList = new ArrayList<>();


        data = new ArrayList<>();
        displayListUser();
    }

    private void displayListUser() {
        mAdapter = new UserGroupFirebaseAdapter(this, data);
        rvListUser.setAdapter(mAdapter);
    }

    private void loadData() {
        try {
            Bundle bundle = getIntent().getExtras();

            if (bundle != null) {

                key = bundle.getString(Constants.TEMP_GROUP_KEY, "");
                groupNew = (GroupCustomerRetro) bundle.getSerializable(Constants.KEY_PASSINGDATA_GROUP_OBJ);

                if (!key.equalsIgnoreCase("")) {

                    if (groupNew != null) {
                        tvName.setText(groupNew.getGroupName());
                        tvAvatar.setText((groupNew.getGroupName().charAt(0) + "").toUpperCase());
                    }
                    GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
                    Call<JSONResponse<List<GroupCustomerRetro>>> getDetail = getAPI.getDetailGroup(key);
                    getDetail.enqueue(new Callback<JSONResponse<List<GroupCustomerRetro>>>() {
                        @Override
                        public void onResponse(Call<JSONResponse<List<GroupCustomerRetro>>> call, Response<JSONResponse<List<GroupCustomerRetro>>> response) {
                            try {
                                if (response.isSuccessful()) {

                                    if (response.body() != null && response.body().getSuccess() != null) {
                                        if (response.body().getSuccess()) {
                                            GroupCustomerRetro group = response.body().getData().get(0);
                                            groupNew = group;
                                            displayInfo(group);
                                        } else {
                                            Toast.makeText(GroupCustomerDetailActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(GroupCustomerDetailActivity.this, getString(R.string.text_error_unknown), Toast.LENGTH_LONG).show();
                                }
                                hideProgressBar(true);
                                getDetail.cancel();
                            } catch (Exception e) {
                                hideProgressBar(true);
                                if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                            }
                        }

                        @Override
                        public void onFailure(Call<JSONResponse<List<GroupCustomerRetro>>> call, Throwable t) {
                            hideProgressBar(true);
                            getDetail.cancel();
                            if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
                        }
                    });

                }

            }
        } catch (Exception e) {
            hideProgressBar(true);
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void displayInfo(GroupCustomerRetro group) {
        tvName.setText(group.getGroupName());
        tvAvatar.setText((group.getGroupName().charAt(0) + "").toUpperCase());

        if (group.getListCustomer() != null) {
            data.clear();
            data.addAll(group.getListCustomer());
            mAdapter.notifyDataSetChanged();

            btnSendMessage.setOnClickListener(v -> sendMessage(v, phoneList));

            btnSendEmail.setOnClickListener(v -> sendEmail(v, emailList));
        }

    }

    private void addEvent() {

        btnBack.setOnClickListener(v -> onBackPressed());

        btnSave.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddGroupCustomerActivity.class);
            intent.putExtra(Constants.TEMP_GROUP_KEY, key);
            intent.putExtra(Constants.KEY_PASSINGDATA_GROUP_OBJ, groupNew);
            baseActivityTransition.transitionTo(intent, Constants.KEY_EDIT_GROUP_RESULT);
        });

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddGroupCustomerActivity.class);
            intent.putExtra(Constants.TEMP_GROUP_KEY, key);
            intent.putExtra(Constants.KEY_PASSINGDATA_GROUP_OBJ, groupNew);
            baseActivityTransition.transitionTo(intent, Constants.KEY_EDIT_GROUP_RESULT);
        });

        btnSendMessage.setOnClickListener(v -> sendMessage(v, phoneList));

        btnSendEmail.setOnClickListener(v -> sendEmail(v, emailList));

    }

    private void sendMessage(View v, String phoneList) {
        try {
            if (!TextUtils.isEmpty(phoneList)) {

                String phoneSub = phoneList.substring(0, phoneList.length() - 1);
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);

                if (android.os.Build.MANUFACTURER.equalsIgnoreCase(getString(R.string.Samsung))) {
                    String phoneReplace = phoneSub.replace(";", ",");
                    sendIntent.putExtra(getString(R.string.address), phoneReplace);
                } else {
                    sendIntent.putExtra(getString(R.string.address), phoneSub);
                }

                sendIntent.putExtra(getString(R.string.sms_body), "");
                sendIntent.setType(getString(R.string.type_intent_sms));
                startActivity(sendIntent);
            } else {
                Snackbar.make(v, getString(R.string.text_error_group_no_phone), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.action), null).show();
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.text_error_sms_failed),
                    Toast.LENGTH_LONG).show();
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void sendEmail(View v, List<String> emailList) {
        try {

            if (emailList.size() != 0) {

                String[] listEmail = new String[emailList.size()];
                listEmail = emailList.toArray(listEmail);

                final Intent emailLauncher = new Intent(Intent.ACTION_SEND_MULTIPLE);
                emailLauncher.setType(getString(R.string.type_intent_mail));
                emailLauncher.putExtra(Intent.EXTRA_EMAIL, listEmail);
                emailLauncher.putExtra(Intent.EXTRA_SUBJECT, "Kính gửi quý khách");
                emailLauncher.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(emailLauncher);
            } else {
                Snackbar.make(v, getString(R.string.text_error_group_no_email), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.text_error_email_failed),
                    Toast.LENGTH_LONG).show();
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.KEY_EDIT_GROUP_RESULT) {
            boolean isDeleted = Objects.requireNonNull(data.getExtras()).getBoolean(Constants.KEY_IS_DELETED_GROUP);
            if (!isDeleted) {

                groupNew = (GroupCustomerRetro) data.getSerializableExtra(Constants.KEY_PASSINGDATA_GROUP_OBJ);
                phoneList = "";
                displayInfo(groupNew);

            } else {
                finishAfterTransition();
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
    }

    @Override
    protected void onResume() {
        mAdapter.notifyDataSetChanged();
        super.onResume();
    }
}
