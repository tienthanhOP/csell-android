package csell.com.vn.csell.controllers;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocialController {
    private Context mContext;
    private FileSave fileGet;

    public SocialController(Context mContext) {
        this.mContext = mContext;
        fileGet = new FileSave(mContext, Constants.GET);
    }

    public interface OnGetNewfeedsListener {
        void onGetNewFeedsSuccess(List<Product> data, boolean isLoadMore);

        void onGetNewFeedsFailure(boolean isLoadMore);

        void onConnectGetNewFeedsSuccess(boolean isLoadMore);
    }

    public void getNewfeeds(String cat, int skip, int limit, boolean isLoadMore, OnGetNewfeedsListener listener) {
        GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
        Call<JSONResponse<List<Product>>> jsonResponseCall = getAPI.getNewfeeds(cat, skip, limit);

        jsonResponseCall.enqueue(new Callback<JSONResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<JSONResponse<List<Product>>> call, Response<JSONResponse<List<Product>>> response) {
                if (response.isSuccessful()) {
                    JSONResponse<List<Product>> result = response.body();
                    if (result != null) {
                        if (result.getSuccess() != null) {
                            if (result.getSuccess()) {
                                if (result.getData() != null) {
                                    listener.onGetNewFeedsSuccess(result.getData(), isLoadMore);
                                }
                            } else {
                                Utilities.refreshToken(MainActivity.mainContext, result.getMessage().toLowerCase() + "");
                            }
                        }

                    }
                    String msg = response.body().getError();
                    if (!TextUtils.isEmpty(msg)) {
                        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                    }
                    jsonResponseCall.cancel();
                } else {
                    listener.onGetNewFeedsFailure(isLoadMore);
                    if (response.errorBody() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = (String) jsonObject.get(Constants.ERROR);
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONResponse<List<Product>>> call, Throwable t) {
                jsonResponseCall.cancel();
                listener.onConnectGetNewFeedsSuccess(isLoadMore);
                Crashlytics.logException(t);
            }
        });
    }

    public interface OnFilterNewfeedListener {
        void onFilterNewfeedSuccess(List<Product> data, boolean isLoadMore);

        void onFilterNewfeedFailure(boolean isLoadMore);

        void onConnectFilterNewfeedSuccess(boolean isLoadMore);
    }

    public void filterNewfeed(int skip, int limit, String rootName, String subName, String catName, String priceMin,
                              String priceMax, String city, String district, boolean isLoadMore, OnFilterNewfeedListener listener) {
        GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
        Call<JSONResponse<List<Product>>> jsonResponseCall = getAPI.filterNewfeed(skip, limit, rootName, subName, catName,
                priceMin, priceMax, city, district);

        jsonResponseCall.enqueue(new Callback<JSONResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<JSONResponse<List<Product>>> call, Response<JSONResponse<List<Product>>> response) {
                if (response.isSuccessful()) {
                    JSONResponse<List<Product>> result = response.body();
                    if (result.getSuccess() != null) {
                        if (result.getSuccess()) {
                            listener.onFilterNewfeedSuccess(result.getData(), isLoadMore);
                        } else {
                            Utilities.refreshToken(mContext, result.getMessage().toLowerCase() + "");
                        }
                    }
                    String msg = response.body().getError();
                    if (!TextUtils.isEmpty(msg)) {
                        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                    }
                    jsonResponseCall.cancel();
                } else {
                    listener.onFilterNewfeedFailure(isLoadMore);
                    if (response.errorBody() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = (String) jsonObject.get(Constants.ERROR);
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONResponse<List<Product>>> call, Throwable t) {
                jsonResponseCall.cancel();
                listener.onConnectFilterNewfeedSuccess(isLoadMore);
                Crashlytics.logException(t);
            }
        });
    }
}
