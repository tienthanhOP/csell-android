package csell.com.vn.csell.controllers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.CheckErrors;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.interfaces.GetDetail;
import csell.com.vn.csell.models.Field;
import csell.com.vn.csell.models.ResLogin;
import csell.com.vn.csell.models.ResUser;
import csell.com.vn.csell.models.UserRetro;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserController {

    private Context mContext;
    private FileSave fileGet;
    private GetDetail mListener;

    public UserController(Context context, GetDetail mListener) {
        mContext = context;
        this.mListener = mListener;
        fileGet = new FileSave(context, Constants.GET);
        Fabric.with(context, new Crashlytics());
    }

    public UserController(Context mContext) {
        this.mContext = mContext;
    }

    public void getDetail() {
        GetAPI api = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
        Call<JSONResponse<UserRetro>> getDetail = api.getDetail();
        getDetail.enqueue(new Callback<JSONResponse<UserRetro>>() {
            @Override
            public void onResponse(Call<JSONResponse<UserRetro>> call, Response<JSONResponse<UserRetro>> response) {
                if (response.isSuccessful()) {

                    try {
                        if ((response.body() != null ? response.body().getSuccess() : null) != null) {
                            if (response.body().getSuccess()) {
                                UserRetro userRetro = response.body().getData();
                                putFileAndLogin(userRetro);

                                mListener.onGetDetail(userRetro);
                            } else {
                                String msg = response.body().getMessage();
                                if (msg.equals(mContext.getResources().getString(R.string.text_error_session_expired))) {

                                } else {
                                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            CheckErrors.handleResponseError(response);
                        }

                    } catch (Exception ignored) {

                    }

                    // gia han code

                } else {

                }

                getDetail.cancel();
            }

            @Override
            public void onFailure(Call<JSONResponse<UserRetro>> call, Throwable t) {
                getDetail.cancel();
            }
        });
    }

    private void putFileAndLogin(UserRetro data) {
        try {
            FileSave fileSave = new FileSave(mContext, Constants.PUT);
            fileSave.putUserEmail(data.getEmail());
            fileSave.putDisplayName(data.getDisplayname());
            Utilities.USER_NAME = "" + data.getUsername();
            fileSave.putUserName(data.getUsername());
            fileSave.putUserAvatar(data.getAvatar() != null ? data.getAvatar() : "");
            fileSave.putUserCover(data.getCover() != null ? data.getCover() : "");
            String dob = data.getDob();

            fileSave.putDob(dob);

            fileSave.putUserCityName(data.getCity() != null ? data.getCity() : "");
            fileSave.putUserPhone(data.getPhone() != null ? data.getPhone() : "");
            fileSave.putAutoLogin(true);
            fileSave.putUserAccount(data.getUsername());

            List<Field> list = data.getFields();
            if (list == null) return;
            List<String> lstJob = new ArrayList<>();
            for (Field field : list) {
                lstJob.add(field.getFieldid());
            }

            Set<String> foo = new HashSet<>(lstJob);
            fileSave.putUserJob(foo);
            fileSave.putTimeRefreshToken(System.currentTimeMillis());

            if (data.getExpirationTime() != null) {
                fileSave.putExpirationTime(data.getExpirationTime());
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    public void login(String account, String password, OnLoginListener listener) {
        HashMap<String, Object> map = new HashMap<>();
        if (!BuildConfig.DEBUG) {
            map.put("username", account);
            map.put("password", Utilities.encryptString(password));
        } else {
            map.put("username", account);
            map.put("password", password);
        }

        PostAPI api = RetrofitClient.createServiceLogin(PostAPI.class);
        Call<ResLogin> login = api.login(map);
        login.enqueue(new Callback<ResLogin>() {
            @Override
            public void onResponse(Call<ResLogin> call, Response<ResLogin> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getCode() == 0) {
                                if (response.body() != null) {
                                    listener.onLoginSuccess(response.body());
                                }
                            } else if (response.body().getCode() == 401) {
                                listener.onAcountActive();
                            } else {
                                String msg = response.body().getMessage();
                                if (!TextUtils.isEmpty(msg)) {
                                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                                    listener.onErrorLogin();
                                }
                            }
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = (String) jsonObject.get(Constants.ERROR);
                                if (msg.equals(mContext.getString(R.string.text_error_account_not_active))) {
                                    try {
                                        listener.onLoginFailure(account);
                                    } catch (Exception e) {
                                        if (BuildConfig.DEBUG)
                                            Log.d("" + getClass().getName(), e.getMessage());
                                        Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                                    }
                                } else {
                                    listener.onLoginFailure(account);
                                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException | IOException e) {
//                                listener.onLoginFailure(account);
                            }
                        }
                    }
                    login.cancel();
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onFailure(Call<ResLogin> call, Throwable t) {
                listener.onConnectLoginFailure();
                login.cancel();
                Toast.makeText(mContext, R.string.failure_connect_server, Toast.LENGTH_LONG).show();
                Crashlytics.logException(t);
            }
        });
    }

    public void register(HashMap<String, Object> user, OnRegisterListener listener) {
        PostAPI api = RetrofitClient.createServiceLogin(PostAPI.class);
        Call<ResLogin> register = api.register(user);

        register.enqueue(new Callback<ResLogin>() {
            @Override
            public void onResponse(Call<ResLogin> call, Response<ResLogin> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getCode() == 0) {
                                listener.onRegisterSuccess(response.body().getUser());
                            } else {
                                Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                listener.onErrorRegister(response.body().getCode());
                            }
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = (String) jsonObject.get(Constants.ERROR);
                                if (!TextUtils.isEmpty(msg)) {
                                    listener.onRegisterFailure(msg);
                                }
                                try {
                                    if (response.body() != null && response.body().getCode() != 0) {
                                        Toast.makeText(mContext, msg + "", Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception ignored) {

                                }
                            } catch (JSONException | IOException ignored) {

                            }
                        }
                    }
                    register.cancel();
                } catch (Exception ignored) {
                    ignored.getMessage();
                }
            }

            @Override
            public void onFailure(Call<ResLogin> call, Throwable t) {
                listener.onConnectRegisterFailure();
                register.cancel();
            }
        });

    }

    public void sendVerifyPhone(String phone, boolean isForgot, OnSendVerifyEmailListener listener) {
        PostAPI api = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        HashMap<String, Object> map = new HashMap<>();
        map.put(EntityAPI.FIELD_PHONE, phone);
        Call<HashMap<String, Object>> sendPhoneVerify = api.sendVerifyPhone(map);
        sendPhoneVerify.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if ((Double) response.body().get("code") == 0) {
                            listener.onSendVerifyPhoneSuccess(phone);
                        } else {
                            String msg;
                            if (response.body() != null) {
                                msg = (String) response.body().get("message");
                                if (!TextUtils.isEmpty(msg)) {
                                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                } else {
                    listener.onSendVerifyEmailFailure();
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                listener.onConnectSendVerifyEmailFailure();
                Toast.makeText(mContext, mContext.getString(R.string.text_error_unknown), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void activeAccount(String phone, String code, OnActiveAccountListener listener) {
        PostAPI api = RetrofitClient.createServiceLogin(PostAPI.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put(EntityAPI.FIELD_EMAIL, phone);
        map.put(EntityAPI.FIELD_CODE, Integer.parseInt(code));
        Call<HashMap<String, Object>> activeAccount = api.activeAccount(map);
        activeAccount.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        listener.onActiveAccountSuccess();
                        if ((Double) response.body().get("code") == 0) {
//                            listener.onActiveAccountSuccess();
//                            Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        } else {
//                            Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

//                    String msg = null;
//                    if (response.body() != null) {
//                        msg = response.body().getError();
//                    }
//                    if (!TextUtils.isEmpty(msg)) {
//                        listener.onErrorActiveAccount();
//                        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
//                    }
                } else {
                    listener.onActiveAccountFailure();
//                    if (response.errorBody() != null) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
//                            String msg = (String) jsonObject.get(Constants.ERROR);
//
//                            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
//
//                        } catch (JSONException | IOException e) {
//                            e.printStackTrace();
//                        }
//                    }

                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                listener.onConnectActiveAccountFailure();
                Toast.makeText(mContext, mContext.getString(R.string.text_error_unknown), Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface OnLoginListener {
        void onLoginSuccess(ResLogin response);

        void onAcountActive();

        void onErrorLogin();

        void onLoginFailure(String account);

        void onConnectLoginFailure();
    }

    public interface OnRegisterListener {
        void onRegisterSuccess(ResUser user);

        void onErrorRegister(int errorCode);

        void onRegisterFailure(String msg);

        void onConnectRegisterFailure();
    }

    public interface OnSendVerifyEmailListener {
        void onSendVerifyPhoneSuccess(String email);

        void onErrorSendVerifyEmail();

        void onSendVerifyEmailFailure();

        void onConnectSendVerifyEmailFailure();
    }

    public interface OnActiveAccountListener {
        void onActiveAccountSuccess();

        void onErrorActiveAccount();

        void onActiveAccountFailure();

        void onConnectActiveAccountFailure();
    }
}
