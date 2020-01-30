package csell.com.vn.csell.controllers;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.models.UserRetro;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotiController {
    private Context mContext;
    private FileSave fileGet;

    public NotiController(Context mContext) {
        this.mContext = mContext;
        fileGet = new FileSave(mContext, Constants.GET);
    }

    public interface OnSendNotiListener {
        void onSendNotiSuccess();

        void onErrorSendNoti();

        void onSendNotiFailure();

        void onConnectSendNotiFailure();
    }

    public void sendNoti(HashMap<String, Object> map, OnSendNotiListener listener) {
        PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        Call<JSONResponse<Object>> sendNoti = postAPI.sendNoti(map);

        sendNoti.enqueue(new Callback<JSONResponse<Object>>() {
            @Override
            public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getSuccess() != null) {
                            if (response.body().getSuccess()) {
                                listener.onSendNotiSuccess();
                                if (BuildConfig.DEBUG)
                                    Log.e(getClass().getName(), response.body().getMessage());
                            } else {
                                listener.onErrorSendNoti();
                                if (BuildConfig.DEBUG)
                                    Log.e(getClass().getName(), response.body().getMessage());
                            }
                        }
                    } else {
                        listener.onSendNotiFailure();
                    }
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                listener.onConnectSendNotiFailure();
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
            }
        });
    }
}
