package csell.com.vn.csell.controllers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.apis.JSONNote;
import csell.com.vn.csell.apis.JSONProductV1;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.apis.ProductResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.interfaces.GetProduct;
import csell.com.vn.csell.models.NoteV1;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.models.ProductCountResponse;
import csell.com.vn.csell.models.ProductResponseV1;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductController {

    private Context mContext;
    private GetProduct mListener;
    private OnDeleteProductListener onDeleteProductListener;
    private FileSave fileGet;

    public ProductController(Context mContext, GetProduct mListener, OnDeleteProductListener onDeleteProductListener) {
        this.mContext = mContext;
        this.mListener = mListener;
        this.onDeleteProductListener = onDeleteProductListener;

        fileGet = new FileSave(mContext, Constants.GET);
    }

    public ProductController(Context mContext) {
        this.mContext = mContext;
        fileGet = new FileSave(mContext, Constants.GET);
    }

    public void getDetailProduct(String productId) {
        try {
            GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
            if (getAPI != null) {
                Call<ProductResponseV1> jsonResponseCall = getAPI.getDetailProduct(productId);
                jsonResponseCall.enqueue(new Callback<ProductResponseV1>() {
                    @Override
                    public void onResponse(Call<ProductResponseV1> call, Response<ProductResponseV1> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                mListener.onGetDetail(response.body());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductResponseV1> call, Throwable t) {
                        Crashlytics.logException(t);
                        jsonResponseCall.cancel();
                    }
                });
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d(getClass().getSimpleName(), e.getMessage());
        }
    }

    public void getNoteProducts(String productId) {
        try {
            GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
            if (getAPI != null) {
                Call<JSONNote<List<NoteV1>>> jsonResponseCall = getAPI.getListNote(productId, "");
                jsonResponseCall.enqueue(new Callback<JSONNote<List<NoteV1>>>() {
                    @Override
                    public void onResponse(Call<JSONNote<List<NoteV1>>> call, Response<JSONNote<List<NoteV1>>> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                mListener.onGetNoteProduct(response.body().getNote());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JSONNote<List<NoteV1>>> call, Throwable t) {
                        Crashlytics.logException(t);
                        jsonResponseCall.cancel();
                    }
                });
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d(getClass().getSimpleName(), e.getMessage());
        }
    }

    public void getProductNewfeed(String uid, String pid) {
        try {
            GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());

            if (getAPI != null) {

                Call<JSONResponse<List<Product>>> jsonResponseCall = getAPI.getNewfeedDetail(uid, pid);
                jsonResponseCall.enqueue(new Callback<JSONResponse<List<Product>>>() {
                    @Override
                    public void onResponse(Call<JSONResponse<List<Product>>> call, Response<JSONResponse<List<Product>>> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                JSONResponse<List<Product>> result = response.body();
                                if (result.getSuccess() != null) {
                                    if (result.getSuccess()) {
                                        if (result.getData() != null) {
                                            if (result.getData().size() > 0) {
                                                mListener.onGetDetailNewfeed(result.getData().get(0));
                                            }
                                        }
                                    } else {
                                        Utilities.refreshToken(mContext, result.getMessage().toLowerCase() + "");
                                        Toast.makeText(mContext, result.getMessage() + "", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                        jsonResponseCall.cancel();
                    }

                    @Override
                    public void onFailure(Call<JSONResponse<List<Product>>> call, Throwable t) {
                        Crashlytics.logException(t);
                        jsonResponseCall.cancel();
                    }
                });
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d(getClass().getSimpleName(), e.getMessage());
        }
    }

    public void getGroupProducts(int type, int level, String catId, String projectId, boolean fromAdapter, OnGetGroupProductsListener listener) {
        try {
            GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
            Call<JSONResponse<List<ProductCountResponse>>> jsonResponseCall;
            if (getAPI != null) {
                jsonResponseCall = getAPI.getGroupProducts(type, level, catId, projectId);

                jsonResponseCall.enqueue(new Callback<JSONResponse<List<ProductCountResponse>>>() {
                    @Override
                    public void onResponse(Call<JSONResponse<List<ProductCountResponse>>> call, Response<JSONResponse<List<ProductCountResponse>>> response) {
                        if (response.isSuccessful()) {
                            JSONResponse<List<ProductCountResponse>> result = response.body();
                            if (result.getSuccess() != null) {
                                if (result.getSuccess()) {
                                    if (result.getData().size() > 0) {
                                        listener.onGetGroupProductsSuccess(result.getData(), level, catId, fromAdapter);
                                    }
                                }
                            }

                            String msg = response.body().getError();
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                            }

                        } else {
                            listener.onGetGroupProductsFailure();
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
                        listener.onGetGroupProducts(fromAdapter);
                        jsonResponseCall.cancel();
                    }

                    @Override
                    public void onFailure(Call<JSONResponse<List<ProductCountResponse>>> call, Throwable t) {
                        listener.onConnectGetGroupProductsFailure(fromAdapter);
                        jsonResponseCall.cancel();
                        ((MainActivity) mContext).productCollectionsFragment.reloadData();
                    }
                });
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public void getListProducts(int type, String cat, String projectId, int skip, int limit, boolean loadMore, OnGetListProductsListener listener) {
        try {
            GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
            if (getAPI != null) {
                try {
                    Call<JSONResponse<List<Product>>> jsonResponseCall = getAPI
                            .getListProducts(type, cat, projectId, skip, limit);
                    jsonResponseCall.enqueue(new Callback<JSONResponse<List<Product>>>() {
                        @Override
                        public void onResponse(Call<JSONResponse<List<Product>>> call, Response<JSONResponse<List<Product>>> response) {
                            if (response.isSuccessful()) {
                                JSONResponse<List<Product>> result = response.body();
                                if (result.getSuccess() != null) {
                                    if (result.getSuccess()) {
//                                        listener.onGetListProductsSuccess(result.getData(), loadMore);
                                    } else {
                                        Utilities.refreshToken(mContext, result.getMessage().toLowerCase() + "");
                                        Toast.makeText(mContext, result.getMessage() + "", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                String msg = response.body().getError();
                                if (!TextUtils.isEmpty(msg)) {
                                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                                }

                            } else {
                                listener.onGetListProductsFailure();
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
                            listener.onConnectGetListProductsFailure();
                        }
                    });
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            }
        } catch (Exception ignored) {

        }
    }

    public void getListProducts(int skip, int limit, boolean loadMore, OnGetListProductsListener listener) {
        GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
        Call<ProductResponse> call = getAPI.getListProducts("", "", "", "", "0", skip, limit, "");
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        listener.onGetListProductsSuccess(response.body().getProductResponseV1List(), response.body().getCount(), loadMore);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                if (BuildConfig.DEBUG)
                    Log.d(getClass().getSimpleName(), t.getMessage() + "");
                Crashlytics.logException(t);
                listener.onGetListProductsFailure();
                Toast.makeText(MainActivity.mainContext, MainActivity.mainContext.getResources().getString(R.string.pls_try_again),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getProductFollows(int skip, int limit, boolean loadMore, OnGetProductFollowsListener listener) {
        GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
        if (getAPI != null) {
            Call<JSONResponse<List<Product>>> jsonResponse = getAPI.getProductFollows(skip, limit);
            jsonResponse.enqueue(new Callback<JSONResponse<List<Product>>>() {
                @Override
                public void onResponse(Call<JSONResponse<List<Product>>> call, Response<JSONResponse<List<Product>>> response) {
                    if (response.isSuccessful()) {
                        JSONResponse<List<Product>> result = response.body();
                        if (result.getSuccess() != null) {
                            if (result.getSuccess()) {
                                if (result.getData() != null) {
                                    listener.onGetProductFollowsSuccess(result.getData(), loadMore);
                                }
                            } else {
                                Utilities.refreshToken(mContext, result.getMessage().toLowerCase() + "");
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.pls_try_again),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        String msg = response.body().getError();
                        if (!TextUtils.isEmpty(msg)) {
                            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                        }

                    } else {
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
                        listener.onGetProductFollowsFailure(loadMore);
                    }

                }

                @Override
                public void onFailure(Call<JSONResponse<List<Product>>> call, Throwable t) {
                    listener.onConnectGetProductFollowsFailure(loadMore);
                    Crashlytics.logException(t);
                }
            });
        }
    }

    public void updateProduct(String id, HashMap<String, Object> map) {
        PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        Call<JSONProductV1<ProductResponseV1>> call = postAPI.updateProduct(id, map);
        call.enqueue(new Callback<JSONProductV1<ProductResponseV1>>() {
            @Override
            public void onResponse(Call<JSONProductV1<ProductResponseV1>> call, Response<JSONProductV1<ProductResponseV1>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getCode() == 0) {
                            mListener.onGetDetail(response.body().getProduct());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONProductV1<ProductResponseV1>> call, Throwable t) {
                Crashlytics.logException(t);
            }
        });
    }

    public void removeProduct(String id) {
        try {
            PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
            Call<HashMap<String, Object>> call = postAPI.removeProduct(id);
            call.enqueue(new Callback<HashMap<String, Object>>() {
                @Override
                public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            onDeleteProductListener.onDeleteProductSuccess();
                        }
                    }
                }

                @Override
                public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                    onDeleteProductListener.onDeleteProductFailure();
                }
            });
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public interface OnGetGroupProductsListener {
        void onGetGroupProductsSuccess(List<ProductCountResponse> data, int level, String catId, boolean fromAdapter);

        void onGetGroupProductsFailure();

        void onGetGroupProducts(boolean fromAdapter);

        void onConnectGetGroupProductsFailure(boolean fromAdapter);
    }

    public interface OnGetListProductsListener {
        void onGetListProductsSuccess(List<ProductResponseV1> data, int count, boolean loadMore);

        void onGetListProductsFailure();

        void onConnectGetListProductsFailure();
    }

    public interface OnGetProductFollowsListener {
        void onGetProductFollowsSuccess(List<Product> data, boolean loadMore);

        void onGetProductFollowsFailure(boolean loadMore);

        void onConnectGetProductFollowsFailure(boolean loadMore);
    }

    public interface OnDeleteProductListener {
        void onDeleteProductSuccess();

        void onDeleteProductFailure();
    }
}
