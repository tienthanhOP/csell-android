package csell.com.vn.csell.controllers;

import android.annotation.SuppressLint;
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
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.CustomerRetroV1;
import csell.com.vn.csell.models.DataCustomerV1;
import csell.com.vn.csell.models.ResCustomers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomersController {

    private Context context;
    private FileSave fileGet;

    public CustomersController(Context context) {
        this.context = context;
        fileGet = new FileSave(context, Constants.GET);
    }

    @SuppressLint("StaticFieldLeak")
    public void getCustomers(int skip, int limit, String keyword, String phone, String emails, boolean listRecent, OnGetCustomersListener listener) {
        try {
            GetAPI getAPI = RetrofitClient.createService(GetAPI.class, "");
            Call<DataCustomerV1> getListCustomer = getAPI.getCustomers(skip, limit, keyword, phone, emails);
            getListCustomer.enqueue(new Callback<DataCustomerV1>() {
                @Override
                public void onResponse(Call<DataCustomerV1> call, Response<DataCustomerV1> response) {
                    if (response.isSuccessful()) {
                        if ((response.body() != null)) {
                            if (response.body().getCode() != null) {
                                if (response.body().getCode() == 0.0) {
                                    if (response.body().getInteracts() != null) {
                                        listener.onGetRecentCustomersSuccess(response.body().getInteracts());
                                    }
                                    if (response.body().getCustomers() != null) {
                                        listener.onGetCustomersSuccess(response.body().getCustomers());
                                    }
                                } else {
                                    listener.onGetCustomersFailure(listRecent);
                                }
                            } else {
                                listener.onGetCustomersFailure(listRecent);
                            }
                        }
                    } else {
                        listener.onGetCustomersFailure(listRecent);
                    }
                }

                @Override
                public void onFailure(Call<DataCustomerV1> call, Throwable t) {
                    listener.onConnectGetCustomersFailure(listRecent);
                }
            });
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    public void addCustomer(HashMap<String, Object> customer, OnAddCustomerListener listener) {
        PostAPI api = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        Call<ResCustomers> add = api.addCustomer(customer);
        add.enqueue(new Callback<ResCustomers>() {
            @Override
            public void onResponse(Call<ResCustomers> call, Response<ResCustomers> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getCode() == 0) {
                                listener.onAddCustomerSuccess(response.body().getCustomer());
                            } else {
                                listener.onErrorAddCustomer();
//                                Utilities.refreshToken(MainActivity.mainContext, response.body().getMessage().toLowerCase() + "");
//                                if (!response.body().getMessage().equals(context.getString(R.string.text_error_session_expired))) {
//                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
//                                }
                            }
                        }
                        String msg;
                        if (response.body() != null) {
                            msg = response.body().getErrors().get(0).getMessages().get(0);
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                            }
                        }
                        add.cancel();
                    } else {
                        listener.onAddCustomerFailure();
                        if (response.errorBody() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = (String) jsonObject.get(Constants.ERROR);
                                if (!TextUtils.isEmpty(msg)) {
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException | IOException ignored) {
                                ignored.getMessage();
                            }
                        }
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                    Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                }
            }

            @Override
            public void onFailure(Call<ResCustomers> call, Throwable t) {
                add.cancel();
                listener.onConnectAddCustomerFailure();
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
                Crashlytics.logException(t);
            }
        });
    }

    public void updateCustomer(String id, HashMap<String, Object> map, OnUpdateCustomerListener listener) {
        PostAPI api = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        Call<ResCustomers> update = api.updateCustomer(id, map);
        update.enqueue(new Callback<ResCustomers>() {
            @Override
            public void onResponse(Call<ResCustomers> call, Response<ResCustomers> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getCode() == 0) {
                            listener.onUpdateCustomerSuccess(response.body(), response.body().getCustomer());
                        } else {
                            listener.onErrorUpdateCustomer();
//                            Utilities.refreshToken(context, response.body().getMessage().toLowerCase() + "");
//                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    listener.onUpdateCustomerFailure();
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
                update.cancel();
            }

            @Override
            public void onFailure(Call<ResCustomers> call, Throwable t) {
                update.cancel();
                listener.onConnectUpdateCustomerFailure();
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
                Crashlytics.logException(t);
            }
        });
    }

    public interface OnGetCustomersListener {
        void onGetCustomersSuccess(List<CustomerRetroV1> data);

        void onGetRecentCustomersSuccess(List<CustomerRetroV1> data);

        void onGetCustomersFailure(boolean listRecent);

        void onConnectGetCustomersFailure(boolean listRecent);
    }

    public interface OnAddCustomerListener {
        void onAddCustomerSuccess(CustomerRetroV1 customerNew);

        void onErrorAddCustomer();

        void onAddCustomerFailure();

        void onConnectAddCustomerFailure();
    }

    public interface OnUpdateCustomerListener {
        void onUpdateCustomerSuccess(ResCustomers response, CustomerRetroV1 customerNew);

        void onErrorUpdateCustomer();

        void onUpdateCustomerFailure();

        void onConnectUpdateCustomerFailure();
    }
}
