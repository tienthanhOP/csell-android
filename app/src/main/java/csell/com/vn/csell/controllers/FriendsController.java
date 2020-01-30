package csell.com.vn.csell.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.commons.PushNotifications;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.models.ProductCountResponse;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.models.FriendResponse;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.sqlites.SQLFriends;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsController {

    private Context context;
    private FileSave fileGet;

    public FriendsController(Context context) {
        this.context = context;
        fileGet = new FileSave(context, Constants.GET);
        Fabric.with(context, new Crashlytics());
    }

    public interface OnGetFriendsListener {
        void onGetFriendsSuccess(ArrayList<UserRetro> data);

        void onGetFriendsFailure();

        void onConnectGetFriendsFailure();
    }

    @SuppressLint("StaticFieldLeak")
    public void getFriends(int skip, int limit, OnGetFriendsListener listener) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    SQLFriends sqlFriends = new SQLFriends(context);
                    if (sqlFriends.checkExistData()) {
                        return null;
                    } else {
                        GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
                        if (getAPI != null) {
                            Call<JSONResponse<List<FriendResponse>>> jsonResponseCall = getAPI.getFriends(skip, limit);
                            jsonResponseCall.enqueue(new Callback<JSONResponse<List<FriendResponse>>>() {
                                @Override
                                public void onResponse(Call<JSONResponse<List<FriendResponse>>> call, Response<JSONResponse<List<FriendResponse>>> response) {
                                    try {
                                        if (response.isSuccessful()) {
                                            if ((response.body() != null)) {
                                                JSONResponse<List<FriendResponse>> result = response.body();
                                                if (result.getSuccess() != null) {
                                                    if (result.getSuccess()) {
                                                        if (result.getData() != null) {
                                                            List<FriendResponse> lst = result.getData();
                                                            if (lst.size() > 0) {
                                                                sqlFriends.clearData();
                                                                for (FriendResponse friendResponse : lst) {
                                                                    if (friendResponse.getFriendInfo() != null) {
                                                                        if (friendResponse.getFriendInfo().size() > 0) {
                                                                            sqlFriends.insertFriends1(friendResponse.getFriendInfo());
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        listener.onGetFriendsSuccess(sqlFriends.getAllFriend1());
                                                    } else {
                                                        listener.onGetFriendsFailure();
                                                    }
                                                }
                                            }
                                        } else {
                                            listener.onGetFriendsFailure();
                                        }
                                    } catch (Exception ignored) {

                                    }
                                }

                                @Override
                                public void onFailure(Call<JSONResponse<List<FriendResponse>>> call, Throwable t) {
                                    listener.onConnectGetFriendsFailure();
                                    Crashlytics.logException(t);
                                }
                            });
                        }
                    }

                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.d(getClass().getSimpleName(), e.getMessage());
                    Crashlytics.logException(e);

                }
                return null;
            }
        }.execute();

    }

    public void addFriendRequest(UserRetro friend) {

        if (friend == null) {
            Toast.makeText(context, context.getResources().getString(R.string.user_not_exists), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(friend.getUid())) {
            Toast.makeText(context, context.getResources().getString(R.string.user_not_exists), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
            if (postAPI != null) {
                HashMap<String, Object> map = new HashMap<>();
                map.put(EntityAPI.FIELD_RECEIVER_ID, friend.getUid());
                Call<JSONResponse<Object>> jsonResponseCall = postAPI.sendRequest(map);
                jsonResponseCall.enqueue(new Callback<JSONResponse<Object>>() {
                    @Override
                    public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                        if (response.isSuccessful()) {
                            JSONResponse<Object> result = response.body();
                            if (result != null) {

                                if (result.getSuccess()) {
                                    PushNotifications pushNotifications = new PushNotifications(context);
                                    pushNotifications.putNotiAcceptFriend(friend);
                                }

                                if (TextUtils.isEmpty(result.getMessage()))
                                    Toast.makeText(context, "" + result.getMessage() + "", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(context, "" + response.message(), Toast.LENGTH_SHORT).show();
                        }
                        jsonResponseCall.cancel();
                    }

                    @Override
                    public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                        jsonResponseCall.cancel();
                        addFriendRequest(friend);
                    }
                });
            }


        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d(getClass().getSimpleName(), e.getMessage());
            Crashlytics.logException(e);
        }
    }

    public void denyFriendRequest(UserRetro friend) {
        if (friend == null) {
            Toast.makeText(context, context.getResources().getString(R.string.user_not_exists), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(friend.getUid())) {
            Toast.makeText(context, context.getResources().getString(R.string.user_not_exists), Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
            if (postAPI != null) {
                HashMap<String, Object> map = new HashMap<>();
                map.put(EntityAPI.FIELD_FRIEND_ID, friend.getUid());
                Call<JSONResponse<Object>> jsonResponseCall = postAPI.unFriend(map);
                jsonResponseCall.enqueue(new Callback<JSONResponse<Object>>() {
                    @Override
                    public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                        if (response.isSuccessful()) {
                            JSONResponse<Object> result = response.body();
                            if (result != null) {
                                if (TextUtils.isEmpty(result.getMessage()))
                                    Toast.makeText(context, "" + result.getMessage() + "", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(context, "" + response.message(), Toast.LENGTH_SHORT).show();
                        }
                        jsonResponseCall.cancel();
                    }

                    @Override
                    public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                        jsonResponseCall.cancel();
                    }
                });
            }


        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d(getClass().getSimpleName(), e.getMessage());
            Crashlytics.logException(e);
        }
    }

    public interface OnGetFriendsRequestListener {
        void onGetFriendsRequestSuccess(List<FriendResponse> data);

        void onGetFriendsRequestFailure();

        void onConnectGetFriendsRequestFailure();
    }

    public void getFriendsRequest(int skip, int limit, OnGetFriendsRequestListener listener) {
        try {
            GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
            Call<JSONResponse<List<FriendResponse>>> getRequest = getAPI.getFriendRequest(skip, limit);
            getRequest.enqueue(new Callback<JSONResponse<List<FriendResponse>>>() {
                @Override
                public void onResponse(Call<JSONResponse<List<FriendResponse>>> call, Response<JSONResponse<List<FriendResponse>>> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getSuccess() != null) {
                            if (response.body().getData() != null)
                                listener.onGetFriendsRequestSuccess(response.body().getData());
                        }
                    } else {
                        listener.onGetFriendsRequestFailure();
                    }
                }

                @Override
                public void onFailure(Call<JSONResponse<List<FriendResponse>>> call, Throwable t) {
                    listener.onConnectGetFriendsRequestFailure();
                }
            });
        } catch (Exception ignored) {

        }
    }

    public interface OnGetFriendDetailListener {
        void onGetFriendDetailSuccess(UserRetro data);

        void onGetFriendDetailFailure();

        void onConnectGetFriendDetailFailure();
    }

    public void getFriendDetail(String uid, OnGetFriendDetailListener listener) {
        try {
            GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
            Call<JSONResponse<List<UserRetro>>> getDetail = getAPI.getFriendDetail(uid);
            getDetail.enqueue(new Callback<JSONResponse<List<UserRetro>>>() {
                @Override
                public void onResponse(Call<JSONResponse<List<UserRetro>>> call, Response<JSONResponse<List<UserRetro>>> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getSuccess() != null) {
                                if (response.body().getSuccess()) {
                                    if (response.body().getData().size() != 0) {
                                        listener.onGetFriendDetailSuccess(response.body().getData().get(0));
                                    }
                                }
                            }
                        } else {
                            listener.onGetFriendDetailFailure();
                        }
                        getDetail.cancel();
                    } catch (Exception e) {
                        listener.onGetFriendDetailFailure();
                        if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                        Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                    }
                }

                @Override
                public void onFailure(Call<JSONResponse<List<UserRetro>>> call, Throwable t) {
                    listener.onConnectGetFriendDetailFailure();
                }
            });
        } catch (Exception e) {
            Crashlytics.logException(e);
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
        }
    }

    public interface OnGetFriendGroupProductListener {
        void onGetFriendGroupProductSuccess(List<ProductCountResponse> data);

        void onGetFriendGroupProductFailure(Response<JSONResponse<List<ProductCountResponse>>> response);

        void onConnectGetFriendGroupProductFailure();
    }

    public void getFriendGroupProduct(String friendUid, int level, String catId, String projectId, OnGetFriendGroupProductListener listener) {
        try {
            GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
            Call<JSONResponse<List<ProductCountResponse>>> finalJsonResponseCall = getAPI.getFriendGroupProduct(friendUid, level, catId, projectId);
            finalJsonResponseCall.enqueue(new Callback<JSONResponse<List<ProductCountResponse>>>() {
                @Override
                public void onResponse(Call<JSONResponse<List<ProductCountResponse>>> call, Response<JSONResponse<List<ProductCountResponse>>> response) {
                    if (response.isSuccessful()) {
                        JSONResponse<List<ProductCountResponse>> result = response.body();
                        if (result.getSuccess() != null) {
                            if (result.getSuccess()) {
                                listener.onGetFriendGroupProductSuccess(result.getData());
                            }
                        }
                    } else {
                        listener.onGetFriendGroupProductFailure(response);
                    }
                    finalJsonResponseCall.cancel();
                }

                @Override
                public void onFailure(Call<JSONResponse<List<ProductCountResponse>>> call, Throwable t) {
                    listener.onConnectGetFriendGroupProductFailure();
                }
            });
        } catch (Exception ignored) {

        }
    }

    public interface OnGetFriendListProductListener {
        void onGetFriendListProductSuccess(List<Product> data, boolean isLoadMore);

        void onGetFriendListProductFailure(Response<JSONResponse<List<Product>>> response);

        void onConnectGetFriendListProductFailure();
    }

    public void getFriendListProduct(String userId, String categoryId, int skip, int limit, boolean isLoadMore, OnGetFriendListProductListener listener) {
        try {
            GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
            if (getAPI != null) {
                Call<JSONResponse<List<Product>>> jsonResponseCall = getAPI
                        .getFriendListProduct(userId, categoryId, skip, limit);
                jsonResponseCall.enqueue(new Callback<JSONResponse<List<Product>>>() {
                    @Override
                    public void onResponse(Call<JSONResponse<List<Product>>> call, Response<JSONResponse<List<Product>>> response) {
                        if (response.isSuccessful()) {
                            JSONResponse<List<Product>> result = response.body();
                            if (result.getSuccess() != null) {
                                if (result.getSuccess()) {
                                    listener.onGetFriendListProductSuccess(result.getData(), isLoadMore);
                                }
                            }
                        } else {
                            listener.onGetFriendListProductFailure(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<JSONResponse<List<Product>>> call, Throwable t) {
                        listener.onConnectGetFriendListProductFailure();
                    }
                });
            }
        } catch (Exception ignored) {

        }
    }

    public interface OnSendRequestListener {
        void onSendRequestSuccess(UserRetro friend, int position);

        void onErrorSendRequest();

        void onSendRequestFailure();

        void onConnectSendRequestFailure();
    }

    public void sendRequest(UserRetro friend, int position, OnSendRequestListener listener) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put(EntityAPI.FIELD_RECEIVER_ID, friend.getUid());
            PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
            Call<JSONResponse<Object>> sendRequest = postAPI.sendRequest(map);
            sendRequest.enqueue(new Callback<JSONResponse<Object>>() {
                @Override
                public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body().getSuccess() != null) {
                                if (response.body().getSuccess()) {
                                    listener.onSendRequestSuccess(friend, position);
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                } else {
                                    listener.onErrorSendRequest();
                                    Utilities.refreshToken(MainActivity.mainContext, response.body().getMessage().toLowerCase() + "");
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }

                            String msg = response.body().getError();
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            listener.onSendRequestFailure();
                            if (response.errorBody() != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = (String) jsonObject.get(Constants.ERROR);
                                    if (!TextUtils.isEmpty(msg)) {
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                        Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                    }
                }

                @Override
                public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                    listener.onConnectSendRequestFailure();
                    if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
                }
            });
        } catch (Exception ignored) {

        }
    }

    public interface OnAcceptFriendListener {
        void onAcceptFriendSuccess(UserRetro friend, int position);

        void onErrorAcceptFriend();

        void onAcceptFriendFailure();

        void onConnectAcceptFriendFailure();
    }

    public void acceptFriend(UserRetro friend, int position, OnAcceptFriendListener listener) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put(EntityAPI.FIELD_FRIEND_ID, friend.getUid());
            PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
            Call<JSONResponse<Object>> accept = postAPI.acceptFriend(map);
            accept.enqueue(new Callback<JSONResponse<Object>>() {
                @Override
                public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getSuccess() != null) {
                                if (response.body().getSuccess()) {
                                    listener.onAcceptFriendSuccess(friend, position);
                                } else {
                                    listener.onErrorAcceptFriend();
                                    Utilities.refreshToken(context, response.body().getMessage().toLowerCase() + "");
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }

                            String msg;
                            if (response.body() != null) {
                                msg = response.body().getError();
                                if (!TextUtils.isEmpty(msg)) {
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            listener.onAcceptFriendFailure();
                            if (response.errorBody() != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = (String) jsonObject.get(Constants.ERROR);
                                    if (!TextUtils.isEmpty(msg)) {
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        listener.onConnectAcceptFriendFailure();
                        if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                        Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                    }
                }

                @Override
                public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                    listener.onConnectAcceptFriendFailure();
                }
            });
        } catch (Exception ignored) {

        }
    }

    public interface OnUnFriendListener {
        void onUnFriendSuccess(UserRetro friend, boolean isFriend, boolean isRequested, int position);

        void onErrorUnFriend();

        void onUnFriendFailure();

        void onConnectUnFriendFailure();
    }

    public void unFriend(UserRetro friend, boolean isFriend, boolean isRequested, int position, OnUnFriendListener listener) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put(EntityAPI.FIELD_FRIEND_ID, friend.getUid());
            PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
            retrofit2.Call<JSONResponse<Object>> unFriend = postAPI.unFriend(map);

            unFriend.enqueue(new Callback<JSONResponse<Object>>() {
                @Override
                public void onResponse(retrofit2.Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                    try {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            if (response.body().getSuccess() != null) {
                                if (response.body().getSuccess()) {
                                    listener.onUnFriendSuccess(friend, isFriend, isRequested, position);
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                } else {
                                    listener.onErrorUnFriend();
                                    Utilities.refreshToken(context, response.body().getMessage().toLowerCase() + "");
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                listener.onUnFriendFailure();
                            }

                            String msg = response.body().getError();
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                            }

                        } else {
                            listener.onUnFriendFailure();
                            if (response.errorBody() != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = (String) jsonObject.get(Constants.ERROR);
                                    if (!TextUtils.isEmpty(msg)) {
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        listener.onConnectUnFriendFailure();
                        if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                        Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<JSONResponse<Object>> call, Throwable t) {
                    if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
                    listener.onConnectUnFriendFailure();
                }
            });
        } catch (Exception ignored) {

        }
    }
}
