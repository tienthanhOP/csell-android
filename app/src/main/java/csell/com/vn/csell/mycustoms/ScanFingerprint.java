package csell.com.vn.csell.mycustoms;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

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

import csell.com.vn.csell.R;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.LoginActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.commons.FingerprintHandler;
import csell.com.vn.csell.interfaces.HideDialogInputPassword;

import static android.content.Context.FINGERPRINT_SERVICE;

public class ScanFingerprint extends Dialog implements HideDialogInputPassword {

    private TextView tvClose;
    private Context mContext;
    private TextView tvMessage;
    private FileSave fileSave;
    private FileSave filePut;
    private FingerprintHandler fingerprintHandler;
    // Variable used for storing the key in the Android Keystore container
    private static final String KEY_STORE_ALIAS = "key_fingerprint";
    private static final String KEY_STORE = "AndroidKeyStore";

    private KeyStore keyStore;
    private Cipher cipher;
    private BaseActivityTransition baseActivityTransition;

    @TargetApi(Build.VERSION_CODES.M)
    public ScanFingerprint(@NonNull Context context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mContext = context;
        Objects.requireNonNull(getWindow()).requestFeature(Window.FEATURE_NO_TITLE);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(lp);
        this.setCanceledOnTouchOutside(false);
        setContentView(R.layout.scan_fingerprint);
        fileSave = new FileSave(mContext, Constants.GET);
        filePut = new FileSave(mContext, Constants.PUT);
        baseActivityTransition = new BaseActivityTransition(context);

        initView();
        initEvent();

        FingerprintManager fingerprintManager = (FingerprintManager) mContext.getSystemService(FINGERPRINT_SERVICE);
        generateAuthenticationKey();

        if (isCipherInitialized()) {
            final FingerprintManager.CryptoObject cryptoObject =
                    new FingerprintManager.CryptoObject(cipher);
            fingerprintHandler = new FingerprintHandler(mContext, Constants.LOGIN_FINGERPRINT, this);
            fingerprintHandler.startAuth(fingerprintManager, cryptoObject);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initEvent() {
        tvClose.setOnClickListener(v -> {
            fingerprintHandler.stopListening();
            dismiss();
        });
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
        } catch (Exception e) {
            e.printStackTrace();
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

    private void initView() {
        tvClose = findViewById(R.id.tv_close);
        tvMessage = findViewById(R.id.tv_message);
    }

    @Override
    public void isHideDialog(Boolean b) {
        if (b) {
            ScanFingerprint.this.dismiss();
            filePut.putAutoLogin(true);
            nextView();
        }
    }

    private void nextView() {
        boolean autoLogin = fileSave.isAutoLogin();
        boolean uid = TextUtils.isEmpty(fileSave.getUserId());

        if (autoLogin && !uid) {
            try {

                Utilities.signInWithToken(fileSave.getTokenFirebase(), (Activity) mContext);

//                Intent i = new Intent(mContext, MainActivity.class);
//                Bundle bundle = ((Activity) mContext).getIntent().getExtras();
//                if (bundle != null) {
//
//
//                    String type = bundle.getString("type_noti");
//                    String key = bundle.getString("key_noti");
//
//                    if (!TextUtils.isEmpty(type) && !TextUtils.isEmpty(key)) {
//                        i.putExtra("type_noti", type);
//                        i.putExtra("key_noti", key);
//                    }
//                }
//                mContext.startActivity(i);
            } catch (Exception e) {
                Intent i = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(i);
                ((Activity) mContext).finishAfterTransition();
                Crashlytics.logException(new Exception("SplashActivity: " + e));
            }

        } else {
            Intent i = new Intent(mContext, LoginActivity.class);
            baseActivityTransition.transitionTo(i, 0);
            ((Activity) mContext).finishAfterTransition();
        }
    }
}
