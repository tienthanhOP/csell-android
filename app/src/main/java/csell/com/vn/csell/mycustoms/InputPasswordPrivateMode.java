package csell.com.vn.csell.mycustoms;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.firebase.firestore.FirebaseFirestore;

import com.crashlytics.android.Crashlytics;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.UserController;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.csell.activity.SettingActivity;
import csell.com.vn.csell.views.social.activity.DetailProductActivity;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.commons.FingerprintHandler;
import csell.com.vn.csell.interfaces.HideDialogInputPassword;
import csell.com.vn.csell.interfaces.ShowNotePrivate;
import csell.com.vn.csell.models.LoginResponse;
import retrofit2.Response;

import static android.content.Context.FINGERPRINT_SERVICE;

/**
 * Created by cuong.nv on 2/23/2018.
 */

public class InputPasswordPrivateMode extends Dialog implements HideDialogInputPassword {

    private TextView tvCancel, tvConfirm;
    private TextView tvMessage;
    private ImageView imgFingerprint;
    private TextView tvTitle;
    private EditText edtInputPassword;
    private FileSave fileGet;
    private FileSave filePut;
    private Context mContext;
    private int timeout = 0;
    private String actionCall;
    private ShowNotePrivate listener;
    private static final String KEY_STORE_ALIAS = "key_fingerprint";
    private static final String KEY_STORE = "AndroidKeyStore";
    private KeyStore keyStore;
    private Cipher cipher;
    private TextView verifyByPassWord;
    private CancellationSignal cancellationSignal;
    private Stage mStage = Stage.FINGERPRINT;
    private FingerprintHandler fingerprintHandler;
    private UserController userController;
    private UserController.OnLoginListener mCallBackLogin;

    @TargetApi(Build.VERSION_CODES.M)
    public InputPasswordPrivateMode(@NonNull Context context, ShowNotePrivate click, String actionCall,
                                    UserController.OnLoginListener callBackLogin) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mContext = context;
        this.actionCall = actionCall;
        if (click != null) {
            listener = click;
        }
        this.mCallBackLogin = callBackLogin;

        Objects.requireNonNull(getWindow()).requestFeature(Window.FEATURE_NO_TITLE);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // Width
        lp.height = WindowManager.LayoutParams.MATCH_PARENT; // Height
        getWindow().setAttributes(lp);
        this.setCanceledOnTouchOutside(false);
        setContentView(R.layout.activity_input_password);

        initview();

        edtInputPassword.setOnEditorActionListener((v, actionId, event) -> {

            if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                checkPassword();
                return true;
            } else if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || event == null
                    || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                checkPassword();
                return true;
            }

            return false;
        });

        edtInputPassword.setOnClickListener(v -> tvMessage.setText(mContext.getResources().getString(R.string.please_input_password)));

        verifyByPassWord.setOnClickListener(v -> {
            try {
                fingerprintHandler.stopListening();
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
            edtInputPassword.setVisibility(View.VISIBLE);
            imgFingerprint.setVisibility(View.GONE);
            verifyByPassWord.setVisibility(View.GONE);
            tvTitle.setText(mContext.getResources().getString(R.string.verify_password));
            tvMessage.setText(mContext.getResources().getString(R.string.please_input_password));
            imgFingerprint.setVisibility(View.GONE);
            tvConfirm.setVisibility(View.VISIBLE);

            tvConfirm.setOnClickListener(v1 -> checkPassword());
        });

        try {
            switch (actionCall) {
                case Constants.PRIVATE_MODE:
                    if (fileGet.getUserId().equals(fileGet.getUserIdFingferprint()) && fileGet.isFingerprint()) {
                        FingerprintManager fingerprintManager = (FingerprintManager) mContext.getSystemService(FINGERPRINT_SERVICE);
                        tvTitle.setText(mContext.getResources().getString(R.string.title_input_password));
                        edtInputPassword.setVisibility(View.GONE);
                        imgFingerprint.setVisibility(View.VISIBLE);
                        verifyByPassWord.setVisibility(View.VISIBLE);
                        tvConfirm.setVisibility(View.GONE);

                        generateAuthenticationKey();
                        if (isCipherInitialized()) {
                            cancellationSignal = new CancellationSignal();

                            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                            fingerprintHandler = new FingerprintHandler(mContext, Constants.PRIVATE_MODE, this);
                            fingerprintHandler.startAuth(fingerprintManager, cryptoObject);
                        }

                        tvCancel.setOnClickListener(v -> {
                            this.dismiss();
                            try {
                                fingerprintHandler.stopListening();
                            } catch (Exception e) {
                                Crashlytics.logException(e);
                            }
                            MainActivity.switchButton.setChecked(false);
                        });

                    } else {
                        edtInputPassword.setVisibility(View.VISIBLE);
                        imgFingerprint.setVisibility(View.GONE);
                        verifyByPassWord.setVisibility(View.GONE);
                        tvTitle.setText(mContext.getResources().getString(R.string.verify_password));
                        tvMessage.setText(mContext.getResources().getString(R.string.please_input_password));
                        imgFingerprint.setVisibility(View.GONE);
                        tvConfirm.setVisibility(View.VISIBLE);

                        tvConfirm.setOnClickListener(v -> checkPassword());

                        tvCancel.setOnClickListener(v -> {
                            this.dismiss();
                            MainActivity.switchButton.setChecked(false);
                        });
                    }
                    break;
                case Constants.CONFIRM_PASSWORD:
                    imgFingerprint.setVisibility(View.GONE);
                    verifyByPassWord.setVisibility(View.GONE);
                    tvTitle.setText(mContext.getResources().getString(R.string.verify_password));
                    tvMessage.setText(mContext.getResources().getString(R.string.please_input_password));
                    tvConfirm.setVisibility(View.VISIBLE);

                    tvConfirm.setOnClickListener(v -> checkPassword());

                    tvCancel.setOnClickListener(v -> {
                        SettingActivity.mSwitchFingerprint.setTrackResource(R.drawable.private_mode_off);
                        SettingActivity.mSwitchFingerprint.setChecked(false);
                        this.dismiss();
                    });
                    break;
                default:
                    imgFingerprint.setVisibility(View.GONE);
                    verifyByPassWord.setVisibility(View.GONE);
                    tvTitle.setText(mContext.getResources().getString(R.string.verify_password));
                    tvMessage.setText(mContext.getResources().getString(R.string.please_input_password));

                    tvConfirm.setVisibility(View.VISIBLE);

                    tvConfirm.setOnClickListener(v -> checkPassword());

                    tvCancel.setOnClickListener(v -> this.dismiss());
                    break;
            }
        } catch (Resources.NotFoundException e) {
            Crashlytics.logException(e);
        }

        tvMessage.setOnClickListener(v -> {
            if (fileGet.getUserId().equals(fileGet.getUserIdFingferprint()) && fileGet.isFingerprint()) {
                CancellationSignal cancellationSignal = new CancellationSignal();
                cancellationSignal.cancel();
            }
        });

    }

    private void initview() {
        userController = new UserController(mContext);

        tvCancel = findViewById(R.id.tv_cancel);
        tvConfirm = findViewById(R.id.tv_confirm);
        tvMessage = findViewById(R.id.tv_message);
        tvTitle = findViewById(R.id.title_dialog_input_pass);
        imgFingerprint = findViewById(R.id.img_fingerprint);
        edtInputPassword = findViewById(R.id.edt_input_password);
        verifyByPassWord = findViewById(R.id.tv_tutorial_verify);
        fileGet = new FileSave(mContext, Constants.GET);
        filePut = new FileSave(mContext, Constants.PUT);
        SpannableString content = new SpannableString(mContext.getResources().getString(R.string.verify_by_password));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        verifyByPassWord.setText(content);
    }

    @SuppressLint("StaticFieldLeak")
    private void checkPassword() {
        new AsyncTask<Void, Void, Void>() {
            @SuppressLint("WrongThread")
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    userController.login(fileGet.getUserName(), edtInputPassword.getText().toString().trim(), mCallBackLogin);
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
                    Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                }
                return null;
            }
        }.execute();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean isCipherInitialized() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" +
                    KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get cipher instance", e);
        }

        try {
            keyStore.load(null);
            final SecretKey key = (SecretKey) keyStore.getKey(KEY_STORE_ALIAS, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException |
                IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void generateAuthenticationKey() {

        getKeyStoreInstance();

        final KeyGenerator keyGenerator = getKeyGenerator();

        try {
            keyStore.load(null);

            final KeyGenParameterSpec parameterSpec = getKeyGenParameterSpec();

            keyGenerator.init(parameterSpec);
            keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private KeyGenParameterSpec getKeyGenParameterSpec() {
        final int purposes = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT;
        return new KeyGenParameterSpec.Builder(KEY_STORE_ALIAS, purposes)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build();
    }

    private void getKeyStoreInstance() {
        try {
            keyStore = KeyStore.getInstance(KEY_STORE);
        } catch (Exception ignored) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private KeyGenerator getKeyGenerator() {
        final KeyGenerator keyGenerator;

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }

        return keyGenerator;
    }

    @Override
    public void isHideDialog(Boolean b) {
        if (b) {
            if (actionCall.equals(Constants.CONFIRM_PASSWORD)) {
                SettingActivity.mSwitchFingerprint.setTrackResource(R.drawable.private_mode_on);
                SettingActivity.mSwitchFingerprint.setChecked(true);
            }
            InputPasswordPrivateMode.this.dismiss();
        }
    }

    public enum Stage {
        FINGERPRINT,
        NEW_FINGERPRINT_ENROLLED,
        PASSWORD
    }

    public void handeResponseSuccess(Response<JSONResponse<LoginResponse>> response) {
        try {
            filePut = new FileSave(mContext, Constants.PUT);
            filePut.putToken(response.body().getData().getToken());
            if (BuildConfig.DEBUG)
                Log.w("TOKEN_HEADER", response.body().getData().getToken());

            if (actionCall.equals(Constants.PRIVATE_MODE)) {
                Toast.makeText(mContext, R.string.turn_on_private_mode, Toast.LENGTH_LONG).show();
                Utilities.isPrivateMode = true;
                InputPasswordPrivateMode.this.dismiss();
                MainActivity.switchButton.setChecked(true);
                Utilities.changeStatusBarColor((Activity) mContext, Utilities.isPrivateMode);

                if (mContext instanceof DetailProductActivity) {
                    listener.showNote();
                }
            } else {
                Toast.makeText(mContext, R.string.activeted_fingerprint, Toast.LENGTH_LONG).show();
                filePut.putFingerprint(true);
                filePut.putUserIFingerprint(fileGet.getUserId());
                InputPasswordPrivateMode.this.dismiss();
            }

            String msg = null;
            if (response.body() != null) {
                msg = response.body().getError();
            }
            if (!TextUtils.isEmpty(msg)) {
                tvMessage.setText(msg + "");
            }
        } catch (Exception ignored) {

        }
    }

    public void handleResponseFailure() {
        try {
            edtInputPassword.setError(mContext.getString(R.string.incorrect_password));

            if (actionCall.equals(Constants.PRIVATE_MODE)) {
                Utilities.isPrivateMode = false;
                MainActivity.switchButton.setChecked(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleResponseConnectFailure() {
        Toast.makeText(mContext, mContext.getString(R.string.text_error_unknown), Toast.LENGTH_LONG).show();
    }
}
