package csell.com.vn.csell.views.account.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.UserController;
import csell.com.vn.csell.views.csell.activity.LoginActivity;

public class VerifyActivity extends AppCompatActivity implements UserController.OnActiveAccountListener, UserController.OnSendVerifyEmailListener {
    private String phone;
    private EditText edt_code_verify;
    private LinearLayout btnResend;
    private TextView tvCountDownTimer;
    private UserController userController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        Intent intent = getIntent();
        phone = intent.getStringExtra(EntityAPI.FIELD_PHONE);

        TextView tvNoti = findViewById(R.id.tv_noti);
        tvNoti.setText(getString(R.string.please_enter_6_digit_code_sent_to_your_registered_email, phone.substring(phone.length() - 3)));

        tvCountDownTimer = findViewById(R.id.tv_count_downTimer);

        btnResend = findViewById(R.id.btn_resend);
        btnResend.setOnClickListener(view -> {
            sendCodeVerify(phone);
            new CountDownTimer(60000, 1000) {
                public void onTick(long millisUntilFinished) {
                    tvCountDownTimer.setVisibility(View.VISIBLE);
                    btnResend.setClickable(false);
                    tvCountDownTimer.setText("(" + millisUntilFinished / 1000 + "s)");
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
                    tvCountDownTimer.setVisibility(View.GONE);
                    btnResend.setClickable(true);
                }
            }.start();
        });

        userController = new UserController(this);
        edt_code_verify = findViewById(R.id.edt_code_verify);

        Button btnVerify = findViewById(R.id.btn_verify);
        btnVerify.setOnClickListener(view -> userController.activeAccount(phone, edt_code_verify.getText().toString().trim(), VerifyActivity.this));
    }

    @Override
    public void onActiveAccountSuccess() {
        try {
            Intent intent = new Intent(VerifyActivity.this, LoginActivity.class);
            startActivity(intent);
            finishAfterTransition();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onErrorActiveAccount() {

    }

    @Override
    public void onActiveAccountFailure() {

    }

    @Override
    public void onConnectActiveAccountFailure() {

    }

    private void sendCodeVerify(String phone) {
        try {
            if (BuildConfig.DEBUG) {
                onSendVerifyPhoneSuccess(phone);
            } else {
                userController.sendVerifyPhone(phone, false, this);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    @Override
    public void onSendVerifyPhoneSuccess(String email) {
        Toast.makeText(this, "Gửi mã thành công", Toast.LENGTH_SHORT).show();
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
