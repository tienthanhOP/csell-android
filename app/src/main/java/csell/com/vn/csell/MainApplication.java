package csell.com.vn.csell;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import csell.com.vn.csell.constants.FirebaseDBUtil;
import io.fabric.sdk.android.Fabric;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDBUtil.getDatebase().setPersistenceEnabled(true);
        Fabric.with(getApplicationContext(), new Crashlytics());

    }
}
