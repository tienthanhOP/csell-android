package csell.com.vn.csell.views.csell.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.UserController;
import csell.com.vn.csell.models.LoginResponse;
import csell.com.vn.csell.models.ResLogin;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.account.activity.EditUserActivity;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.views.account.fragment.ChangePasswordFragment;
import csell.com.vn.csell.views.csell.fragment.IntroduceFragment;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.interfaces.ShowNotePrivate;
import csell.com.vn.csell.models.Device;
import csell.com.vn.csell.mycustoms.InputPasswordPrivateMode;
import csell.com.vn.csell.sqlites.SQLContactLocal;
import csell.com.vn.csell.sqlites.SQLCustomers;
import csell.com.vn.csell.sqlites.SQLFriends;
import csell.com.vn.csell.sqlites.SQLCacheProduct;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends AppCompatActivity implements ShowNotePrivate, UserController.OnLoginListener {

    private ListView lvListSetup;
    private Button btnLogOut;
    private FancyButton btnBack;
    @SuppressLint("StaticFieldLeak")
    public static TextView tvTitle;
    private FileSave filePut;
    private FirebaseAuth mFirebaseAuth;
    private LinearLayout layout_logout;
    private ProgressBar progress_loading;
    private FileSave fileGet;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private InputPasswordPrivateMode dialog;
    private Context mContext;
    @SuppressLint("StaticFieldLeak")
    public static Switch mSwitchFingerprint;
    private BaseActivityTransition baseActivityTransition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mContext = this;
        Fabric.with(mContext, new Crashlytics());

        initView();
        setupWindowAnimations();
        addEvent();

    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    private void initView() {
        baseActivityTransition = new BaseActivityTransition(this);
        filePut = new FileSave(this, Constants.PUT);
        fileGet = new FileSave(this, Constants.GET);
        btnBack = findViewById(R.id.btn_back_navigation);
        btnBack.setText(MainActivity.mainContext.getResources().getString(R.string.title_back_vn));
        tvTitle = findViewById(R.id.custom_TitleToolbar);
        tvTitle.setText(MainActivity.mainContext.getResources().getString(R.string.setting));
        btnLogOut = findViewById(R.id.btn_log_out);
        layout_logout = findViewById(R.id.layout_logout);
        progress_loading = findViewById(R.id.progress_loading);
        lvListSetup = findViewById(R.id.lv_setup);
        mSwitchFingerprint = findViewById(R.id.switch_btn);

        ArrayList<String> list = new ArrayList<>();

        list.add(getString(R.string.title_user_info));
        list.add(getString(R.string.title_change_password));
//        list.add(getString(R.string.title_setup_noti));
        list.add(getString(R.string.title_language));
        list.add(getString(R.string.title_introduce));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.list_item,
                R.id.tv_item
                , list);
        lvListSetup.setAdapter(adapter);
        mFirebaseAuth = FirebaseAuth.getInstance();

        mSwitchFingerprint.setShowText(false);

        if (fileGet.getUserId().equals(fileGet.getUserIdFingferprint()) && fileGet.isFingerprint()) {
            mSwitchFingerprint.setTrackResource(R.drawable.private_mode_on);
            mSwitchFingerprint.setChecked(true);
        } else {
            mSwitchFingerprint.setTrackResource(R.drawable.private_mode_off);
            mSwitchFingerprint.setChecked(false);
        }
    }

    private void addEvent() {

        btnBack.setOnClickListener(v -> onBackPressed());

        btnLogOut.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(R.string.logout_account)
                .setMessage(R.string.you_sure_want_logout)
                .setPositiveButton("Đồng ý", (dialog1, which) -> {
                    progress_loading.setVisibility(View.VISIBLE);
                    resetData();
                })
                .setNegativeButton("Hủy", (dialog1, which) -> dialog1.dismiss())
                .setCancelable(false)
                .show());

//        mSwitchFingerprint.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                mSwitchFingerprint.setTrackResource(R.drawable.private_mode_on);
//                mSwitchFingerprint.setShowText(true);
//            } else {
//                mSwitchFingerprint.setTrackResource(R.drawable.private_mode_off);
//                mSwitchFingerprint.setShowText(false);
//            }
//        });

        mSwitchFingerprint.setOnClickListener(v -> {
            if (mSwitchFingerprint.isChecked()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
                    keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

                    if (!fingerprintManager.isHardwareDetected()) {
                        Toast.makeText(this, R.string.fingerprint_not_deteted_in_device, Toast.LENGTH_SHORT).show();
                        mSwitchFingerprint.setChecked(false);
                    } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)
                            != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, R.string.permission_not_granted_fingerprint, Toast.LENGTH_SHORT).show();
                        mSwitchFingerprint.setChecked(false);
                    } else if (!keyguardManager.isKeyguardSecure()) {
                        Toast.makeText(this, R.string.add_lock_fingerprint_in_setting, Toast.LENGTH_SHORT).show();
                        mSwitchFingerprint.setChecked(false);
                    } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                        Toast.makeText(this, R.string.you_should_add_atleast_1_finggerprint_to_use, Toast.LENGTH_SHORT).show();
                        mSwitchFingerprint.setChecked(false);
                    } else {
                        dialog = new InputPasswordPrivateMode(mContext, this, Constants.CONFIRM_PASSWORD, this);
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                } else {
                    Toast.makeText(this, R.string.not_support_os_less_than_6, Toast.LENGTH_SHORT).show();
                    mSwitchFingerprint.setChecked(false);
                }
            } else {
                new AlertDialog.Builder(mContext)
                        .setTitle("Đã được kích hoạt!")
                        .setMessage("Bạn muốn tắt tính năng này?")
                        .setPositiveButton("Đồng ý", (dialog, which) -> {
                            mSwitchFingerprint.setTrackResource(R.drawable.private_mode_off);
                            mSwitchFingerprint.setChecked(false);
                            filePut.putFingerprint(false);
                            Toast.makeText(this, "Bạn đã tắt tính năng đăng nhập bằng vân tay", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Hủy", (dialog, which) -> {
                            mSwitchFingerprint.setTrackResource(R.drawable.private_mode_on);
                            mSwitchFingerprint.setChecked(true);
                            dialog.dismiss();
                        })
                        .setCancelable(false)
                        .show();
            }
        });

        lvListSetup.setOnItemClickListener((parent, view, position, id) -> {

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

            switch (position) {
                case 0:
                    Intent intent = new Intent(this, EditUserActivity.class);
                    baseActivityTransition.transitionTo(intent, 0);
                    break;
                case 1:
                    transaction.add(R.id.frame_setting, new ChangePasswordFragment(), "ChangePasswordFragment")
                            .addToBackStack("ChangePasswordFragment")
                            .commit();
                    btnLogOut.setVisibility(View.GONE);
                    break;
                case 2:
                    Toast.makeText(mContext, "Sắp ra mắt...", Toast.LENGTH_SHORT).show();
//                    transaction.add(R.id.frame_setting, new ChangeLanguageFragment(), "ChangeLanguageFragment")
//                            .addToBackStack("ChangeLanguageFragment")
//                            .commit();
                    btnLogOut.setVisibility(View.GONE);
                    break;
                case 3:
                    transaction.add(R.id.frame_setting, new IntroduceFragment(), "IntroduceFragment")
                            .addToBackStack("IntroduceFragment")
                            .commit();
                    btnLogOut.setVisibility(View.GONE);
                    break;
            }
        });
    }

    private void resetData() {

        try {
            layout_logout.setVisibility(View.VISIBLE);
            progress_loading.setVisibility(View.VISIBLE);

            filePut.putAutoLogin(false);
            filePut.cleanFileSave();
//            filePut.putUserId("");
//            filePut.putUserAvatar("");
//            filePut.putDisplayName("");
//            filePut.putUserCover("");
//            filePut.putUserName("");
//            filePut.putUserPhone("");
//            filePut.putDob("");
//            filePut.putUserJob(null);
//            filePut.putSelectCategoryJSON(null);
//            filePut.putRootCategoryId("");
//            if (mFirebaseAuth.getCurrentUser() != null) {
//                mFirebaseAuth.signOut();
//            }
//            filePut.putToken("");
//            filePut.putActiveMain(false);
//            filePut.putIsCreateProduct(false);

            SQLCacheProduct sqlCacheProduct = new SQLCacheProduct(this);
            sqlCacheProduct.clearData();
            SQLFriends sqlFriends = new SQLFriends(this);
            sqlFriends.clearData();
            SQLCustomers sqlCustomers = new SQLCustomers(this);
            sqlCustomers.clearData();
            SQLContactLocal sqlContactLocal = new SQLContactLocal(this);
            sqlContactLocal.clearData();
//            removeDevice();

            RetrofitClient.retrofit = null;
            layout_logout.setVisibility(View.GONE);
            progress_loading.setVisibility(View.GONE);
            setResult(Constants.LOGOUT_RESULT);
            finishAfterTransition();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
            Crashlytics.logException(e);
        }
    }

    private void removeDevice() {
        Device device = new Device();
        String token = FirebaseInstanceId.getInstance().getToken();
        if (TextUtils.isEmpty(token)) {
            device.setDeviceType(2);
            device.setDeviceId(fileGet.getDeviceId());
        } else {
            device.setDeviceType(2);
            device.setDeviceId(token);
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("devices", device.toMap());

        PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        Call<JSONResponse<Object>> removeDevice = postAPI.removeDevice(map);
        removeDevice.enqueue(new Callback<JSONResponse<Object>>() {
            @Override
            public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getSuccess() != null) {
                        if (response.body().getSuccess()) {
                            if (BuildConfig.DEBUG)
                                Log.e(getClass().getName(), response.body().getMessage());
                        } else {
                            if (BuildConfig.DEBUG)
                                Log.e(getClass().getName(), response.body().getMessage());
                        }
                    }
                }

                RetrofitClient.retrofit = null;
                layout_logout.setVisibility(View.GONE);
                progress_loading.setVisibility(View.GONE);
                setResult(Constants.LOGOUT_RESULT);
                finishAfterTransition();
                removeDevice.cancel();
            }

            @Override
            public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                removeDevice.cancel();

                RetrofitClient.retrofit = null;
                layout_logout.setVisibility(View.GONE);
                progress_loading.setVisibility(View.GONE);
                setResult(Constants.LOGOUT_RESULT);
                finishAfterTransition();

                if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
                Crashlytics.logException(t);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Fragment fragmentPass = getSupportFragmentManager().findFragmentByTag("ChangePasswordFragment");
        Fragment fragmentLanguage = getSupportFragmentManager().findFragmentByTag("ChangeLanguageFragment");
        Fragment fragmentIntro = getSupportFragmentManager().findFragmentByTag("IntroduceFragment");
        hideKeyboard();
        if (fragmentPass != null || fragmentLanguage != null || fragmentIntro != null) {
            getSupportFragmentManager().popBackStack();
            if (fragmentPass != null) {
                getSupportFragmentManager().beginTransaction().remove(fragmentPass).commit();
            } else if (fragmentLanguage != null) {
                getSupportFragmentManager().beginTransaction().remove(fragmentLanguage).commit();
            } else {
                getSupportFragmentManager().beginTransaction().remove(fragmentIntro).commit();
            }

            tvTitle.setText(MainActivity.mainContext.getResources().getString(R.string.setting));
            btnLogOut.setVisibility(View.VISIBLE);
            return;
        }
        super.onBackPressed();
        finishAfterTransition();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void showNote() {

    }

    @Override
    public void onLoginSuccess(ResLogin response) {
        try {
//            dialog.handeResponseSuccess(response);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onAcountActive() {

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
