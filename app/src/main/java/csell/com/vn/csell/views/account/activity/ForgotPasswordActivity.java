package csell.com.vn.csell.views.account.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.Random;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.UserController;
import csell.com.vn.csell.views.BaseActivityTransition;
import io.fabric.sdk.android.Fabric;

public class ForgotPasswordActivity extends AppCompatActivity implements UserController.OnSendVerifyEmailListener {

    Button btnSend;
    TextView btnBack;
    EditText txtPhone, txtCaptCha;
    TextView tvCaptcha;
    Button btnSubmit, btnResend;
    EditText txtVerifyToken;
    LinearLayout layoutPasswordVerify;
    int captchaCode;
    FileSave filePut;
    UserController userController;
    private TextView txt_signup;
    private ProgressBar login_progress;
    private RelativeLayout relaLayout;
    private int numberCaptcha;
    private ImageView imgRefreshCaptcha;
    private BaseActivityTransition baseActivityTransition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Fabric.with(this, new Crashlytics());
        userController = new UserController(this);
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

        txt_signup = findViewById(R.id.txt_signup);
        btnSend = findViewById(R.id.btn_send_email_reset_password);
        txtPhone = findViewById(R.id.txt_phone_verify);
        txtCaptCha = findViewById(R.id.txt_captcha_verify);
        tvCaptcha = findViewById(R.id.tv_number_captcha);
        btnBack = findViewById(R.id.btn_back_reset_password);

        Random random = new Random();
        numberCaptcha = random.nextInt(899999) + 100000;
        tvCaptcha.setText(String.valueOf(numberCaptcha));

        login_progress = findViewById(R.id.login_progress);
        relaLayout = findViewById(R.id.linearLayout);

        btnSubmit = findViewById(R.id.btn_submit_token);
        txtVerifyToken = findViewById(R.id.txt_verify_token);
        btnResend = findViewById(R.id.btn_resend_token);
        layoutPasswordVerify = findViewById(R.id.frame_password_verify);

        filePut = new FileSave(this, Constants.PUT);
        imgRefreshCaptcha = findViewById(R.id.img_refresh_capcha);
    }

    private void addEvent() {

        btnSend.setOnClickListener(v -> {

            try {
                sendCodeVerify();
//                if (isValidEmail(txtPhone.getText().toString()) &&
//                        txtCaptCha.getText().toString().equals(String.valueOf(numberCaptcha))) {
//                    sendCodeVerify();
//                } else {
//                    if (!isValidEmail(txtPhone.getText().toString())) {
//                        txtPhone.requestFocus();
//                        txtPhone.setError(getString(R.string.text_error_invalid_email));
//                    } else {
//                        if (TextUtils.isEmpty(txtCaptCha.getText().toString()) ||
//                                !txtCaptCha.getText().toString().equals(String.valueOf(numberCaptcha))) {
//
//                            txtCaptCha.requestFocus();
//                            txtCaptCha.setError(getString(R.string.text_error_invalid_captcha));
//                        }
//                    }
//                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }

        });

        btnBack.setOnClickListener(v -> {
            finishAfterTransition();

        });

        txt_signup.setOnClickListener(v -> {
            Intent register = new Intent(ForgotPasswordActivity.this, RegisterActivity.class);
            baseActivityTransition.transitionTo(register, Constants.REGISTER_ACTIVITY_RESULT);
            finishAfterTransition();
        });

        btnSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(txtVerifyToken.getText().toString())) {
                txtVerifyToken.setError(getString(R.string.text_error_empty_code));
            } else if (txtVerifyToken.getText().toString().length() != 6) {
                txtVerifyToken.setError(getString(R.string.text_error_invalid_captcha));
            } else {
                activeAccount();
            }
        });
        btnResend.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(txtPhone.getText().toString().trim())) {
                sendCodeVerify();
            }
        });

        imgRefreshCaptcha.setOnClickListener(v -> {
            Random random = new Random();
            numberCaptcha = random.nextInt(99999 - 10000);
            tvCaptcha.setText(String.valueOf(numberCaptcha));
        });

    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(boolean show) {
        if (show) {
            relaLayout.setAlpha((float) 0.5);
            btnSend.setAlpha((float) 0.5);
            login_progress.setVisibility(View.VISIBLE);
        } else {
            login_progress.setVisibility(View.GONE);
            relaLayout.setAlpha(1);
            btnSend.setAlpha(1);
        }
    }

    @Override
    public void onBackPressed() {
        if (layoutPasswordVerify.getVisibility() == View.VISIBLE) {
            layoutPasswordVerify.setVisibility(View.GONE);
            relaLayout.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.VISIBLE);
        } else {
            finishAfterTransition();
        }
    }

    private void sendCodeVerify() {
        try {
            if (BuildConfig.DEBUG) {
                onSendVerifyPhoneSuccess(txtPhone.getText().toString().trim());
            } else {
                userController.sendVerifyPhone(txtPhone.getText().toString().trim(), true, this);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void activeAccount() {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        intent.putExtra(Constants.KEY_PHONE, txtPhone.getText().toString());
//        intent.putExtra(Constants.KEY_CODE, Integer.parseInt(verify));
        baseActivityTransition.transitionTo(intent, 0);
    }

    @Override
    public void onSendVerifyPhoneSuccess(String email) {
        try {
//            layoutPasswordVerify.setVisibility(View.VISIBLE);
//            relaLayout.setVisibility(View.GONE);
//            btnSend.setVisibility(View.GONE);
            activeAccount();
        } catch (Exception ignored) {
            ignored.getMessage();
        }
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
