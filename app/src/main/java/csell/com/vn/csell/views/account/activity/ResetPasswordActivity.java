package csell.com.vn.csell.views.account.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.HashMap;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.UserController;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.LoginActivity;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.//writeBatch;

public class ResetPasswordActivity extends AppCompatActivity implements UserController.OnSendVerifyEmailListener {

    UserController userController;
    private EditText txtPassword, txtRePassword, txtCode;
    private LinearLayout btnResend;
    private TextView txtBack;
    private TextInputLayout txtInputPassword, txtInputRePassword;
    private Button btnResetPassword;
    private ProgressBar login_progress;
    private LinearLayout linearLayout;
    private String phone;
    private Integer code = 0;
    private int timeout = 0;
    private BaseActivityTransition baseActivityTransition;
    private TextView tvCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        Fabric.with(this, new Crashlytics());
        userController = new UserController(this);
        initView();
        setupWindowAnimations();
        loadData();
        addEvent();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    private void initView() {
        baseActivityTransition = new BaseActivityTransition(this);
        tvCountDownTimer = findViewById(R.id.tv_count_downTimer);
        txtPassword = findViewById(R.id.txt_new_password);
        txtRePassword = findViewById(R.id.txt_confirm_new_password);
        txtCode = findViewById(R.id.txt_confirm_code);
        txtBack = findViewById(R.id.btn_back_new_password);

        btnResend = findViewById(R.id.btn_resend);

        txtInputPassword = findViewById(R.id.txt_input_password);
        txtInputRePassword = findViewById(R.id.txt_input_repassword);

        if (Build.VERSION.SDK_INT >= 21) {
            txtInputPassword.setPasswordVisibilityToggleDrawable(R.drawable.asl_password_visibility);
            txtInputRePassword.setPasswordVisibilityToggleDrawable(R.drawable.asl_password_visibility);
        } else {
            txtInputPassword.setPasswordVisibilityToggleDrawable(R.drawable.ic_eye_24dp);
            txtInputRePassword.setPasswordVisibilityToggleDrawable(R.drawable.ic_eye_24dp);
        }

        btnResetPassword = findViewById(R.id.btn_reset_password);
        login_progress = findViewById(R.id.login_progress);
        linearLayout = findViewById(R.id.linearLayout);
    }

    private void loadData() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString(Constants.KEY_PHONE);
        }

    }

    private void sendCodeVerify() {
        try {
            if (BuildConfig.DEBUG) {
                Log.i("sendCodeVerify", "sendCodeVerify: ");
                onSendVerifyPhoneSuccess(phone);
            } else {
                userController.sendVerifyPhone(phone, true, this);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void addEvent() {

        txtBack.setOnClickListener(v -> onBackPressed());
        btnResend.setOnClickListener(view -> {
            sendCodeVerify();
            new CountDownTimer(60000, 1000) {
                public void onTick(long millisUntilFinished) {
                    tvCountDownTimer.setVisibility(View.VISIBLE);
                    btnResend.setClickable(false);
                    tvCountDownTimer.setText("(" + millisUntilFinished / 1000 + ")");
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
                    tvCountDownTimer.setVisibility(View.GONE);
                    btnResend.setClickable(true);
                }
            }.start();
        });

        btnResetPassword.setOnClickListener(v -> {

            String password = txtPassword.getText().toString().trim();
            String rePassword = txtRePassword.getText().toString().trim();
            String code = txtCode.getText().toString().trim();

            if (TextUtils.isEmpty(password)) {
                txtPassword.requestFocus();
                txtPassword.setError(getString(R.string.text_error_empty));
                return;
            }

            if (TextUtils.isEmpty(rePassword)) {
                txtRePassword.requestFocus();
                txtRePassword.setError(getString(R.string.text_error_empty));
                return;
            }

            if (TextUtils.isEmpty(code)) {
                txtCode.requestFocus();
                txtCode.setError(getString(R.string.text_error_empty));
                return;
            }

            if (password.trim().length() <= 5 || rePassword.trim().length() <= 5) {
                if (password.trim().length() <= 5) {
                    txtPassword.requestFocus();
                    txtPassword.setError(getString(R.string.text_error_length_password));
                } else if (rePassword.trim().length() <= 5) {
                    txtRePassword.requestFocus();
                    txtRePassword.setError(getString(R.string.text_error_length_password));
                } else {
                    txtRePassword.requestFocus();
                    txtPassword.setError(getString(R.string.text_error_length_password));
                    txtRePassword.setError(getString(R.string.text_error_length_password));
                }
                return;
            }

            if (code.trim().length() != 6) {
                txtCode.setError(getString(R.string.text_error_empty_active_code));
                return;
            }

            if (password.equals(rePassword)) {
                showProgress(true);
                resetPassword(password, code);
            } else {
                txtRePassword.requestFocus();
                txtRePassword.setError(getString(R.string.text_error_confirm_password));
            }
        });
    }


    private void resetPassword(String newPassword, String code) {
        try {
            PostAPI api = RetrofitClient.createServiceLogin(PostAPI.class);
            HashMap<String, Object> map = new HashMap<>();
            map.put(EntityAPI.FIELD_PHONE, phone);
            map.put(EntityAPI.FIELD_CODE, code);
            map.put(EntityAPI.FIELD_NEW_PASSWORD, Utilities.encryptString(newPassword));
            Call<HashMap<String, Object>> reset = api.resetPassword(map);
            reset.enqueue(new Callback<HashMap<String, Object>>() {
                @Override
                public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
//                            if ((Double) response.body().get("code") == 0) {
                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                baseActivityTransition.transitionTo(intent, 0);
                                finishAfterTransition();
                                Toast.makeText(ResetPasswordActivity.this, getString(R.string.update_password_success), Toast.LENGTH_LONG).show();
//                            } else {
//                                Toast.makeText(ResetPasswordActivity.this, (String) response.body().get("message"), Toast.LENGTH_LONG).show();
//                            }
                        }
//
//                        String msg = null;
//                        if (response.body() != null) {
//                            msg = response.body().getError();
//                        }
//                        if (!TextUtils.isEmpty(msg)) {
//                            Toast.makeText(ResetPasswordActivity.this, msg, Toast.LENGTH_LONG).show();
//                        }
                    } else {
//                        try {
//                            JSONObject jsonObject;
//                            if (response.errorBody() != null) {
//                                jsonObject = new JSONObject(response.errorBody().string());
//                                String msg = (String) jsonObject.get(Constants.ERROR);
//                                if (!TextUtils.isEmpty(msg)) {
//                                    Toast.makeText(ResetPasswordActivity.this, msg, Toast.LENGTH_LONG).show();
//                                }
//                            }
//                        } catch (JSONException | IOException e) {
//                            e.printStackTrace();
//                        }
                    }
                }

                @Override
                public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
//                    if (timeout < 1) {
//                        timeout++;
//                        resetPassword(newPassword);
//                    } else {
//                        timeout = 0;
//                        Toast.makeText(ResetPasswordActivity.this, R.string.failure_connect_server, Toast.LENGTH_LONG).show();
//                    }
                    Crashlytics.logException(t);
                }
            });

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        } finally {
            showProgress(false);
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(boolean show) {
        if (show) {
            linearLayout.setAlpha((float) 0.5);
            login_progress.setVisibility(View.VISIBLE);
            btnResetPassword.setAlpha((float) 0.5);
        } else {
            login_progress.setVisibility(View.GONE);
            linearLayout.setAlpha(1);
            btnResetPassword.setAlpha(1);
        }
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        super.onBackPressed();
    }

    @Override
    public void onSendVerifyPhoneSuccess(String phone) {

    }

    @Override
    public void onErrorSendVerifyEmail() {

    }

    @Override
    public void onSendVerifyEmailFailure() {

    }

    @Override
    public void onConnectSendVerifyEmailFailure() {

    }
}
