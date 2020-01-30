package csell.com.vn.csell.views.csell.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.UserController;
import csell.com.vn.csell.models.Device;
import csell.com.vn.csell.models.ResLogin;
import csell.com.vn.csell.models.ResUser;
import csell.com.vn.csell.mycustoms.ScanFingerprint;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.account.activity.ForgotPasswordActivity;
import csell.com.vn.csell.views.account.activity.RegisterActivity;
import csell.com.vn.csell.views.account.activity.VerifyActivity;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity implements UserController.OnLoginListener, UserController.OnSendVerifyEmailListener {


    // UI references.
    TextInputLayout tvAccountError;
    // Choose authentication providers
    @SuppressLint("WrongConstant")
    List<AuthUI.IdpConfig> providers = Arrays.asList(
//            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
//            new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build());
    Dialog dialog;
    private TextInputEditText edtAccount;
    private TextInputEditText mPasswordView;
    private TextView txt_signup;
    private LinearLayout linearLayoutFromLogin;
    private ProgressBar progressBar;
    private Button btnSignIn;
    private Button btnForgotPass, btnSigninFirebaseUI;
    private TextInputLayout txtInputPass;
    private FirebaseAuth mAuth;
    private ScanFingerprint scanFingerprint;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private int RC_SIGN_IN = 500;
    private FileSave fileGet;
    private ImageView imgLoginFingerprint;
    private BaseActivityTransition baseActivityTransition;
    private UserController userController;
    private int timeout = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Fabric.with(this, new Crashlytics());
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
        setupWindowAnimations();
        initEvent();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    private void initView() {
        userController = new UserController(this);
        baseActivityTransition = new BaseActivityTransition(this);

        tvAccountError = findViewById(R.id.tvAccountError);
        edtAccount = findViewById(R.id.account);
        edtAccount.setImeOptions(EditorInfo.IME_ACTION_DONE);
        linearLayoutFromLogin = findViewById(R.id.linear_layout_from_login);
        txt_signup = findViewById(R.id.txt_signup);
        progressBar = findViewById(R.id.login_progress);
        imgLoginFingerprint = findViewById(R.id.login_by_fingerprint);
        mPasswordView = findViewById(R.id.password);
        mPasswordView.setImeActionLabel("Sign In", KeyEvent.KEYCODE_ENTER);
        mPasswordView.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;

            if (actionId == KeyEvent.KEYCODE_ENTER) {
                attemptLogin();
                handled = true;
            }
            return handled;
        });

        btnSignIn = findViewById(R.id.email_sign_in_button);
        btnForgotPass = findViewById(R.id.btn_forgot_password);
        btnSigninFirebaseUI = findViewById(R.id.btn_sign_in_firebaseui);
        txtInputPass = findViewById(R.id.text_input_password);

        if (Build.VERSION.SDK_INT >= 21) {
            txtInputPass.setPasswordVisibilityToggleDrawable(R.drawable.asl_password_visibility);
        } else {
            txtInputPass.setPasswordVisibilityToggleDrawable(R.drawable.ic_eye_24dp);
        }

        mAuth = FirebaseAuth.getInstance();
        //mFireStoreRef = FirebaseDBUtil.getFirestore();
        fileGet = new FileSave(this, Constants.GET);
        String userEmail = fileGet.getUserEmail();
        edtAccount.setText(userEmail.equals("") ? "" : userEmail);
        if (TextUtils.isEmpty(userEmail)) {
            edtAccount.setText("");
        } else {
            edtAccount.setText(userEmail);
            edtAccount.setSelection(userEmail.length());
        }

        if (fileGet.getUserId().equals(fileGet.getUserIdFingferprint()) && fileGet.isFingerprint()) {
            imgLoginFingerprint.setVisibility(View.VISIBLE);
        } else {
            imgLoginFingerprint.setVisibility(View.GONE);
        }
    }

    private void initEvent() {

        if (fileGet.getUserId().equals(fileGet.getUserIdFingferprint()) && fileGet.isFingerprint()) {
            if (imgLoginFingerprint.getVisibility() == View.VISIBLE) {
                scanFingerprint = new ScanFingerprint(this);
                scanFingerprint.setCancelable(false);
                scanFingerprint.show();
            }
        }

        imgLoginFingerprint.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
                keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

//                if (!fingerprintManager.isHardwareDetected()) {
//                    Toast.makeText(this, R.string.fingerprint_not_deteted_in_device, Toast.LENGTH_SHORT).show();
//
//                } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)
//                        != PackageManager.PERMISSION_GRANTED) {
//
//                    Toast.makeText(this, R.string.permission_not_granted_fingerprint, Toast.LENGTH_SHORT).show();
//
//                } else if (!keyguardManager.isKeyguardSecure()) {
//
//                    Toast.makeText(this, R.string.add_lock_fingerprint_in_setting, Toast.LENGTH_SHORT).show();
//
//                } else if (!fingerprintManager.hasEnrolledFingerprints()) {
//
//                    Toast.makeText(this, R.string.you_should_add_atleast_1_finggerprint_to_use, Toast.LENGTH_SHORT).show();
//
//                } else {
//                new AlertDialog.Builder(this)
//                        .setTitle("Đăng nhập bằng vân tay!")
//                        .setMessage("Bạn muốn đăng nhập vào tài khoản " + fileGet.getUserName() + "?")
//                        .setPositiveButton("Đồng ý", (dialog, which) -> {
                scanFingerprint = new ScanFingerprint(this);
                scanFingerprint.setCancelable(false);
                scanFingerprint.show();
//                        })
//                        .setNegativeButton("Hủy", (dialog, which) -> {
//                            dialog.dismiss();
//                        })
//                        .setCancelable(false)
//                        .show();
            }
//            } else {
//                Toast.makeText(this, R.string.not_support_os_less_than_6, Toast.LENGTH_SHORT).show();
//            }
        });

        btnSignIn.setOnClickListener(v -> {
            // with Firebase
            attemptLogin();
        });

        txt_signup.setOnClickListener(v -> {
            Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
            baseActivityTransition.transitionTo(register, Constants.REGISTER_ACTIVITY_RESULT);
        });

        btnForgotPass.setOnClickListener(v -> {
            Intent forgotPassword = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            baseActivityTransition.transitionTo(forgotPassword, Constants.KEY_FORGOT_PASSWORD);
        });

        btnSigninFirebaseUI.setOnClickListener(v -> {
            // with Firebase
            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
//                            .setLogo(R.drawable.ic_logo_auth)
                            .build(),
                    RC_SIGN_IN);
        });

        edtAccount.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == 6 && !TextUtils.isEmpty(edtAccount.getText().toString())) {
                mPasswordView.requestFocus();
//                switch (keyCode) {
//                    case KeyEvent.KEYCODE_DPAD_CENTER:
//                    case KeyEvent.KEYCODE_ENTER:
//
//                        return true;
//                    default:
//                        break;
//                }
            }
            return false;
        });

        mPasswordView.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
                handled = true;
            }
            return handled;
        });

    }

    private void attemptLogin() {
//        Bo qua login
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        if (BuildConfig.DEBUG) {
            actionLogin("0999999999", "123456");
        } else {
            edtAccount.setError(null);
            txtInputPass.setError(null);

            String email = edtAccount.getText().toString().trim();
            String password = mPasswordView.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                edtAccount.requestFocus();
                edtAccount.setError(getString(R.string.please_do_not_leave_it_blank));
                return;
            }

            if (TextUtils.isEmpty(password)) {
                txtInputPass.requestFocus();
                txtInputPass.setError(getString(R.string.text_error_empty));
                return;
            }

            if (password.length() < 6) {
                txtInputPass.requestFocus();
                txtInputPass.setError(getString(R.string.password_contains_at_least_6_characters));
                return;
            }

            if (isEmailValid(email)) {
                if (!Utilities.isValidEmail(email)) {
                    edtAccount.requestFocus();
                    edtAccount.setError(getString(R.string.text_error_invalid_email));
                    return;
                }
            }

            showProgress(true);
            actionLogin(email.trim(), password.trim());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.REGISTER_ACTIVITY_RESULT) {
            if (data == null) return;
            String account = data.getStringExtra(Constants.KEY_PASSINGDATA_ACCOUNT_REGISTER);
            if (!TextUtils.isEmpty(account)) {
                edtAccount.setText(account);
            }
        }
        // Firebase UI
        try {
            if (requestCode == RC_SIGN_IN) {
                if (resultCode == RESULT_OK) {
                    showProgress(true);

                    Objects.requireNonNull(mAuth.getCurrentUser()).getIdToken(true)
                            .addOnCompleteListener(task -> {
                                if (task.isComplete() && task.isSuccessful()) {
                                    String idToken = task.getResult().getToken();

                                    loginSocial("google", idToken);
                                } else {
                                    showProgress(false);
                                }
                            })
                            .addOnFailureListener(e -> {
                                showProgress(false);
                                Toast.makeText(this, getString(R.string.text_error_unknown), Toast.LENGTH_LONG).show();
                                Crashlytics.logException(e);
                                if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                            });

                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            showProgress(false);
        }
    }

    private void loginSocial(String type, String token) {
//        PostAPI api = RetrofitClient.createServiceLogin(PostAPI.class);
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("type", type);
//        map.put("token", token);
//        Call<ResLogin> login = api.loginSocial(map);
//        login.enqueue(new Callback<ResLogin>() {
//            @Override
//            public void onResponse(Call<ResLogin> call, Response<ResLogin> response) {
//                if (response.isSuccessful()) {
//                    if (response.body() != null) {
//                        if (response.body().getSuccess() != null) {
//                            if (response.body().getSuccess()) {
//
//                                if (response.body().getData() != null) {
//                                    UserRetro userRetro = response.body().getData().getUserInfo();
//                                    putFileAndLogin(userRetro, response.body().getData().getToken(), response.body().getData().getTokenFirebase());
//                                    if (BuildConfig.DEBUG)
//                                        Log.w("TOKEN_HEADER", response.body().getData().getToken());
//                                }
//
//                                Intent main = new Intent(LoginActivity.this, MainActivity.class);
//                                startActivity(main);
//                                finishAfterTransition();
//                            }
//                        }
//
//                        try {
//                            String msg = response.body().getError();
//                            if (!TextUtils.isEmpty(msg)) {
//
//                                if (response.body().getErrorCode() == 301) {
//                                    Intent intent = new Intent(LoginActivity.this, UpdateInfoAccountActivity.class);
//                                    baseActivityTransition.transitionTo(intent, 0);
//                                } else if (response.body().getErrorCode() == 351) {
//                                    Intent intent = new Intent(LoginActivity.this, UpdateInfoAccountActivity.class);
//                                    baseActivityTransition.transitionTo(intent, 0);
//                                }
//                            }
//                        } catch (Exception e) {
//                            Toast.makeText(LoginActivity.this, "Hệ thống đang bận, vui lòng thử lại", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                } else {
//                    if (response.errorBody() != null) {
//                        showProgress(false);
//                        try {
//                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
//                            String msg = (String) jsonObject.get(Constants.ERROR);
//
//                            if (msg.equals(getString(R.string.text_error_update_info))) {
//                                Intent intent = new Intent(LoginActivity.this, UpdateInfoAccountActivity.class);
//                                baseActivityTransition.transitionTo(intent, 0);
//                            } else if (msg.equals(getString(R.string.text_error_account_not_active))) {
//                                try {
//
//                                    dialog = new Dialog(LoginActivity.this);
//                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                                    dialog.setContentView(R.layout.layout_active_account);
//
//                                    EditText edtEmail = dialog.findViewById(R.id.edt_email);
//                                    Button btnSend = dialog.findViewById(R.id.btn_send);
//                                    Button btnCancel = dialog.findViewById(R.id.btn_cancel);
//                                    String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
//                                    if (!TextUtils.isEmpty(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
//                                        edtEmail.setText(email);
//                                    }
//
//                                    btnCancel.setOnClickListener(v -> dialog.dismiss());
//
//                                    btnSend.setOnClickListener(v -> {
//                                        if (!Utilities.isValidEmail(edtEmail.getText().toString().trim())) {
//                                            edtEmail.setError(getString(R.string.text_error_invalid_email));
//                                            return;
//                                        }
//
//                                        sendCodeVerify(!TextUtils.isEmpty(email) ? email : edtEmail.getText().toString().trim());
//                                    });
//
//                                    dialog.show();
//
//                                } catch (Exception e) {
//                                    if (BuildConfig.DEBUG)
//                                        Log.d("" + getClass().getName(), e.getMessage());
//                                    Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
//                                }
//                            } else {
//                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
//                            }
//
//                        } catch (JSONException | IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                login.cancel();
//                showProgress(false);
//            }
//
//            @Override
//            public void onFailure(Call<ResLogin> call, Throwable t) {
//                login.cancel();
//                if (timeout < 1) {
//                    timeout++;
//                    loginSocial(type, token);
//                } else {
//                    timeout = 0;
//                    showProgress(false);
//                    Toast.makeText(LoginActivity.this, getString(R.string.text_error_unknown), Toast.LENGTH_LONG).show();
//                }
//                if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
//                Crashlytics.logException(t);
//            }
//        });
    }

    private void actionLogin(String account, String password) {
        try {
            userController.login(account, password, this);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            showProgress(false);
        }
    }

    private void putFileAndLogin(ResUser data, String token, String tokenFirebase) {
        try {
            FileSave fileSave = new FileSave(LoginActivity.this, Constants.PUT);
            fileSave.putExpired(false);
            fileSave.putToken(token);
            fileSave.putTokenFirebase(tokenFirebase);
            fileSave.putUserId(data.getId());
            if (BuildConfig.DEBUG) Log.w("UID", data.getId());
            fileSave.putUserEmail(data.getEmail());
            fileSave.putDisplayName(data.getName());
            Utilities.USER_NAME = "" + data.getName();
            fileSave.putUserName(data.getName());
            fileSave.putUserAvatar(data.getAvatar() != null ? data.getAvatar() : "");
            fileSave.putUserCover(data.getCover() != null ? data.getCover() : "");

            String dob = Utilities.convertTimeMillisToDateStringddMMyyyy(data.getBirthday());
            fileSave.putDob(dob);

            fileSave.putUserCityName(data.getProvince().getName() != null ? data.getProvince().getName() : "");
            fileSave.putUserPhone(data.getPhone() != null ? data.getPhone() : "");
            fileSave.putAutoLogin(true);
            fileSave.putUserAccount(data.getName());

//            List<FieldV1> list = data.getFields();
//            if (list != null) {
//                List<String> lstJob = new ArrayList<>();
//                for (FieldV1 field : list) {
//                    lstJob.add(field.getFieldId());
//                }
//
//                Set<String> foo = new HashSet<>(lstJob);
//                fileSave.putUserJob(foo);
//                fileSave.putTimeRefreshToken(System.currentTimeMillis());
//            }

//            addDeviceServer();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void addDeviceServer() {
        if (!TextUtils.isEmpty(fileGet.getDeviceId())) {
            HashMap<String, Object> map = new HashMap<>();
            Device device = new Device();
            device.setDeviceType(2);
            device.setDeviceId(fileGet.getDeviceId());
            if (BuildConfig.DEBUG) Log.e("", "add device");
            map.put("devices", device);

            PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
            Call<JSONResponse<Object>> addDevice = postAPI.addDevice(map);
            addDevice.enqueue(new Callback<JSONResponse<Object>>() {
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
                }

                @Override
                public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                    if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
                    Crashlytics.logException(t);
                }
            });
        }

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(boolean show) {
        if (show) {
            linearLayoutFromLogin.setAlpha((float) 0.5);
            edtAccount.setEnabled(false);
            mPasswordView.setEnabled(false);
            btnSigninFirebaseUI.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            edtAccount.setEnabled(true);
            mPasswordView.setEnabled(true);
            btnSigninFirebaseUI.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            linearLayoutFromLogin.setAlpha(1);
        }
    }

    @Override
    public void onAcountActive() {
        Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
        intent.putExtra(EntityAPI.FIELD_PHONE, edtAccount.getText().toString().trim());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        baseActivityTransition.transitionTo(intent, 0);
        Toast.makeText(LoginActivity.this, getString(R.string.text_error_account_not_active), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String email = fileGet.getUserPhone();
        if (!TextUtils.isEmpty(email)) {
            edtAccount.setText(email);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void sendCodeVerify(String email) {
        try {
            userController.sendVerifyPhone(email, false, this);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    @Override
    public void onLoginSuccess(ResLogin response) {
        try {
            ResUser user = response.getUser();
            String token = response.getToken().getAccess().getToken();
//            String tokenFirebase = response.getToken().getRefresh().getToken();
            String tokenFirebase = "";
            if (BuildConfig.DEBUG)
                Log.w("TOKEN_HEADER", token);
            putFileAndLogin(user, token, tokenFirebase);
//            Utilities.signInWithToken(loginResponse.getTokenFirebase(), LoginActivity.this);
            Intent intent = new Intent(this, MainActivity.class);
            baseActivityTransition.startActivity(intent);
//            startActivity(intent);
        } catch (Exception ignored) {
            showProgress(false);
        }
    }

    @Override
    public void onErrorLogin() {
        showProgress(false);
    }

    @Override
    public void onLoginFailure(String account) {
        try {
            dialog = new Dialog(LoginActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_active_account);

            EditText edtEmail = dialog.findViewById(R.id.edt_email);
            Button btnSend = dialog.findViewById(R.id.btn_send);
            Button btnCancel = dialog.findViewById(R.id.btn_cancel);

            if (isEmailValid(account)) {
                edtEmail.setText(account);
            }

            btnCancel.setOnClickListener(v -> dialog.dismiss());

            btnSend.setOnClickListener(v -> {
                if (!Utilities.isValidEmail(edtEmail.getText().toString().trim())) {
                    edtEmail.setError(getString(R.string.text_error_invalid_email));
                    return;
                }

                sendCodeVerify(isEmailValid(account) ? account : edtEmail.getText().toString().trim());
            });
            showProgress(false);
            dialog.show();
        } catch (Exception e) {
            showProgress(false);
        }
    }

    @Override
    public void onConnectLoginFailure() {
        showProgress(false);
    }

    @Override
    public void onSendVerifyPhoneSuccess(String email) {
//        try {
//            dialog.dismiss();
//            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//            intent.putExtra(Constants.KEY_ACTIVE_ACCOUNT, true);
//            intent.putExtra(Constants.KEY_EMAIL, email);
//            baseActivityTransition.transitionTo(intent, 0);
//            showProgress(false);
//        } catch (Exception ignored) {
//            showProgress(false);
//        }
    }

    @Override
    public void onErrorSendVerifyEmail() {
        showProgress(false);
    }

    @Override
    public void onSendVerifyEmailFailure() {
        showProgress(false);
    }

    @Override
    public void onConnectSendVerifyEmailFailure() {
        showProgress(false);
    }
}

