package csell.com.vn.csell.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;

/**
 * Created by cuong on 4/5/2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

       if(BuildConfig.DEBUG) Log.d(TAG, "DEVICE_ID: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        FileSave filePut = new FileSave(this, Constants.PUT);
        filePut.putDeviceId(token);
    }
}