package csell.com.vn.csell.constants;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.apis.JSONResponse;
import retrofit2.Response;

public class CheckErrors {
    public static void handleResponseError(Object response) {
        Response<JSONResponse<Object>> result = (Response<JSONResponse<Object>>) response;
        if (result.code() == 400) {
            try {
                if (result.errorBody() != null) {
                    JSONObject jsonObject = new JSONObject(result.errorBody().string());
                    String msg = (String) jsonObject.get(Constants.ERROR);

                    if (!TextUtils.isEmpty(msg)) {
                        Toast.makeText(MainActivity.mainContext, msg, Toast.LENGTH_LONG).show();
                    }
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                int errorCode;
                if (result.body() != null) {
                    errorCode = result.body().getErrorCode();
                    switch (errorCode) {
//                    case 100:

//                        break;
                        case 101:
                            new AlertDialog.Builder(MainActivity.mainContext)
                                    .setTitle("Phiên sử dụng hết hạn")
                                    .setMessage("Vui lòng đăng nhập lại")
                                    .setPositiveButton("Đồng ý", (dialog, which) -> {
                                        FileSave fileSave = new FileSave(MainActivity.mainContext, Constants.PUT);
                                        fileSave.putAutoLogin(false);
                                        fileSave.putToken("");
                                        fileSave.putExpired(true);
                                        dialog.dismiss();
                                        PackageManager packageManager = MainActivity.mainContext.getPackageManager();
                                        Intent intent = packageManager.getLaunchIntentForPackage(MainActivity.mainContext.getPackageName());
                                        if (intent != null) {
                                            ComponentName componentName = intent.getComponent();
                                            Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                                            MainActivity.mainContext.startActivity(mainIntent);
                                            System.exit(0);
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();

                            break;
//                    case 0:
//                        break;
//                    case 0:
//                        break;
//                    case 0:
//                        break;
//                    case 0:
//                        break;
//                    case 0:
//                        break;
//                    case 0:
//                        break;
                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
    }
}
