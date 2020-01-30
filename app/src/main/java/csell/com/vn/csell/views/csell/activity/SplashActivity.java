package csell.com.vn.csell.views.csell.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.commons.DownloadFile;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.StringCompare;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.interfaces.CheckDownload;
import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity implements CheckDownload {

    public static int versionApp = 1;
    public ProgressBar progressBar;
    private FileSave fileGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utilities.setLocale2(this, Constants.ISO_CODE_VN);

        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);

        FrameLayout placeholder = findViewById(R.id.placeholder);

        fileGet = new FileSave(this, Constants.GET);
        VideoView videoView = findViewById(R.id.videoView);
        ImageView imgLogo = findViewById(R.id.logo_csell);

        String path = "android.resource://" + getPackageName() + "/" + R.raw.video_csell;
        videoView.setVideoURI(Uri.parse(path));
        videoView.start();

        videoView.setOnErrorListener((mp, what, extra) -> {
            //logo_csell
            Log.e("11111", "Error");
            imgLogo.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            imgLogo.setVisibility(View.VISIBLE);
            return true;
        });

        videoView.setOnPreparedListener(mp -> mp.setOnInfoListener((mp1, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                Log.e("11111", "Really start");
                placeholder.setVisibility(View.GONE);
                return true;
            }
            return false;
        }));

        RelativeLayout layoutSplash = findViewById(R.id.layout_splash);
        progressBar = findViewById(R.id.progress_bar_splash);

        boolean expired = fileGet.getExpired();

        if (expired) {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
            finishAfterTransition();
        } else {
            if (Utilities.isNetworkConnected(this)) {
                if (Utilities.getInetAddressByName() != null) {
                    loadingData();
                } else {
                    Snackbar.make(layoutSplash, getResources().getString(R.string.Please_check_your_network_connection),
                            Snackbar.LENGTH_SHORT).show();
                    if (fileGet.isAutoLogin())
                        nextActivity(500);
                }
            } else {
                Snackbar.make(layoutSplash, getResources().getString(R.string.Please_check_your_network_connection),
                        Snackbar.LENGTH_SHORT).show();
                if (fileGet.isAutoLogin())
                    nextActivity(500);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @SuppressLint("StaticFieldLeak")
    private void loadingData() {
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(10);
                super.onPreExecute();
            }

            @SuppressLint("WrongThread")
            @Override
            protected Boolean doInBackground(String... value) {
                try {
                    FileSave filePut = new FileSave(SplashActivity.this, Constants.PUT);
                    String deviceId = FirebaseInstanceId.getInstance().getToken();
                    filePut.putDeviceId(deviceId);

                    DownloadFile downloadFile = new DownloadFile(SplashActivity.this, "version", SplashActivity.this);
                    downloadFile.execute(Constants.JSON_URL_VERSION);

                } catch (Exception e) {
                    return false;
                }
                return true;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (!aBoolean) {
                    Toast.makeText(SplashActivity.this, getResources().getString(R.string.Please_check_your_network_connection), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    public void onGetStatus(boolean b, String val, boolean isSuccess) {
        runOnUiThread(() -> {
            if (b)
                switch (val) {
                    case StringCompare.version:
                        if (isSuccess) {
                            nextActivity(500);
                        } else {
                            progressBar.setProgress(100);
                            checkDownloaded(false);
                        }
                        break;
                    case StringCompare.cate:

                        progressBar.setProgress(15);
                        checkDownloaded(isSuccess);
                        break;
                    case StringCompare.property:

                        progressBar.setProgress(30);
                        checkDownloaded(isSuccess);
                        break;
                    case StringCompare.location:

                        progressBar.setProgress(45);
                        checkDownloaded(isSuccess);
                        break;
                    case StringCompare.privacy:

                        progressBar.setProgress(60);
                        checkDownloaded(isSuccess);
                        break;
                    case StringCompare.language:

                        progressBar.setProgress(75);
                        checkDownloaded(isSuccess);
                        break;
                    case StringCompare.filter:

                        progressBar.setProgress(100);
                        checkDownloaded(isSuccess);
                        break;
                }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void checkDownloaded(boolean isSuccess) {
        if (isSuccess) {
            if (progressBar.getProgress() == 100) {
                progressBar.setProgress(100);
                nextActivity(500);
            }
        } else {
            progressBar.setProgress(100);
            nextActivity(2000);
        }
    }

    public void nextActivity(int delay) {
        new Handler().postDelayed(this::checkVersion, delay);
    }

    private void checkVersion() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionApp = pInfo.versionCode;
            if (BuildConfig.DEBUG) {
                versionApp = fileGet.getAPKVersion(); // o version nao thi sua so o version do de DEV
            }
            if (fileGet.getAPKVersion() > versionApp) {
                onUpdateNewVersion();
            } else {
                nextView();
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public void nextView() {
        boolean autoLogin = fileGet.isAutoLogin();
        boolean uid = TextUtils.isEmpty(fileGet.getUserId());
        if (autoLogin && !uid) {
            try {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {

                    String type = bundle.getString("type_noti");
                    String key = bundle.getString("key_noti");

                    if (!TextUtils.isEmpty(type) && !TextUtils.isEmpty(key)) {
                        i.putExtra("type_noti", type);
                        i.putExtra("key_noti", key);
                    }
                }
                startActivity(i);
                finishAfterTransition();
            } catch (Exception e) {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finishAfterTransition();
                Crashlytics.logException(new Exception("SplashActivity: " + e));
            }
        } else {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finishAfterTransition();
        }
    }

    private void onUpdateNewVersion() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        try {
            alertDialog.setTitle("Có phiên bản mới, cập nhập ngay?");
            alertDialog.setPositiveButton("Đồng ý",
                    (dialog, which) -> {
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    });

            alertDialog.setNegativeButton("Chưa phải bây giờ", (dialog1, which) -> {
                dialog1.dismiss();
                nextView();
            });
            alertDialog.setCancelable(false);
            alertDialog.show();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }
}


