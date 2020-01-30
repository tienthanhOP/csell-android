package csell.com.vn.csell.commons;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.Toast;

import csell.com.vn.csell.R;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.interfaces.HideDialogInputPassword;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context mContext;
    private String actionCall;
    private HideDialogInputPassword mListener;
    private CancellationSignal mCancellationSignal;

    public FingerprintHandler(Context context, String actionCall, HideDialogInputPassword mListener) {
        this.mContext = context;
        this.actionCall = actionCall;
        this.mListener = mListener;
    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {

        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);

    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
//        this.update("Xác thực lỗi. " + errString, false);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("Xác thực thất bại. ", false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
//        this.update("Lỗi. " + helpString, false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
//        this.update("Xác thực thành công. ", true);
        if (actionCall.equals(Constants.PRIVATE_MODE)) {
            Toast.makeText(mContext, R.string.turn_on_private_mode, Toast.LENGTH_LONG).show();
            Utilities.isPrivateMode = true;
            MainActivity.switchButton.setChecked(true);
            Utilities.changeStatusBarColor((Activity) mContext, Utilities.isPrivateMode);
        }
//        else if (actionCall.equals(Constants.LOGIN_FINGERPRINT)) {
//
//        }
    }

    private void update(String s, boolean b) {
//        ScanFingerprint.tvMessage.setText(s);
        if (b) {
            mListener.isHideDialog(true);
        } else {
            mListener.isHideDialog(false);
        }
        Toast.makeText(mContext, "" + s, Toast.LENGTH_SHORT).show();
    }
}
