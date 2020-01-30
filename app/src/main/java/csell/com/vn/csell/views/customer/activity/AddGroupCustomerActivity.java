package csell.com.vn.csell.views.customer.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.models.CustomerRetroV1;
import csell.com.vn.csell.models.FieldCus;
import csell.com.vn.csell.models.GroupCustomerRetroV1;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.customer.adapter.ChooseCustomerAdapter;
import csell.com.vn.csell.views.customer.adapter.PickedCustomerAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.views.customer.fragment.CustomersFragment;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.models.CustomerRetro;
import csell.com.vn.csell.models.GroupCustomerRetro;
import csell.com.vn.csell.sqlites.SQLCustomers;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGroupCustomerActivity extends AppCompatActivity {

    private RecyclerView rvChooseCustomer;
    @SuppressLint("StaticFieldLeak")
    public static RecyclerView rvChoose;
    private TextView tvAddGroup, tvDeleteGroup;
    private RelativeLayout layoutContainer;
    private EditText edtTextGroupName;
    private EditText edtSearch;
    private TextView txtGroupIcon;
    private FancyButton btnBack, btnSave;
    private ChooseCustomerAdapter mAdapter;
    @SuppressLint("StaticFieldLeak")
    public static PickedCustomerAdapter mAdapterPicked;
    public static ArrayList<CustomerRetroV1> listIDChoose;
    public static ArrayList<CustomerRetroV1> listIDRemove;
    private FileSave fileGet;

    private ProgressBar progressBarSave;

    LinearLayoutManager mLayout;
    private String groupId;
    private GroupCustomerRetroV1 groupNew;
    private boolean isEditGroup = false;
    private TextView tvTitle;
    private ProgressBar progressBarMemberUser;
    private SQLCustomers sqlCustomers;
    private ArrayList<CustomerRetroV1> listCustomer;
    private PostAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_customer);
        Fabric.with(this, new Crashlytics());

        fileGet = new FileSave(this, Constants.GET);
        initView();
        setupWindowAnimations();
        loadList();
        loadDataToEdit();
        addEvent();
        mAdapterPicked = new PickedCustomerAdapter(this, listIDChoose);
        rvChoose.setAdapter(mAdapterPicked);

    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    private void initView() {
        rvChooseCustomer = findViewById(R.id.rv_choose_customers);
        mLayout = new LinearLayoutManager(this);
        rvChooseCustomer.setLayoutManager(mLayout);
        rvChooseCustomer.setHasFixedSize(true);
        tvAddGroup = findViewById(R.id.btnAddGroup);
        tvDeleteGroup = findViewById(R.id.btnDeleteGroup);
        btnBack = findViewById(R.id.btn_back_navigation);
        btnBack.setText(MainActivity.mainContext.getResources().getString(R.string.cancel));
        btnSave = findViewById(R.id.btn_save_navigation);
        btnSave.setVisibility(View.GONE);
        edtTextGroupName = findViewById(R.id.editGroupName);
        edtSearch = findViewById(R.id.edt_search);
        progressBarSave = findViewById(R.id.progress_bar_save);
        progressBarMemberUser = findViewById(R.id.progress_bar_memeber_user);
        layoutContainer = findViewById(R.id.layout_container);
        tvTitle = findViewById(R.id.tv_title);
        sqlCustomers = new SQLCustomers(this);
        listCustomer = new ArrayList<>();

        txtGroupIcon = findViewById(R.id.icon_group);

        listIDChoose = new ArrayList<>();
        listIDRemove = new ArrayList<>();

        rvChoose = findViewById(R.id.rv_choose);
        rvChoose.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapterPicked = new PickedCustomerAdapter(this, listIDChoose);

        rvChoose.setAdapter(mAdapterPicked);
        groupNew = new GroupCustomerRetroV1();
        api = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
    }

    private void loadList() {
//        listCustomer = sqlCustomers.getAllCustomer();
//        Utilities.sortListCustomer(listCustomer);
//        mAdapter = new ChooseCustomerAdapter(this, listCustomer);
//        rvChooseCustomer.setAdapter(mAdapter);
    }

    private void loadDataToEdit() {

        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {

                groupId = bundle.getString(Constants.TEMP_GROUP_KEY, "");
                if (!groupId.equals("")) {
                    isEditGroup = true;
                    tvTitle.setText(MainActivity.mainContext.getResources().getString(R.string.edit_group));
                    tvAddGroup.setVisibility(View.GONE);
                    tvDeleteGroup.setVisibility(View.VISIBLE);
                    rvChoose.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.VISIBLE);
                    btnSave.setText(MainActivity.mainContext.getResources().getString(R.string.done));
                    btnSave.setEnabled(true);

                    groupNew = (GroupCustomerRetroV1) bundle.getSerializable(Constants.KEY_PASSINGDATA_GROUP_OBJ);
                    if (groupNew != null) {
                        edtTextGroupName.setText(groupNew.getName());
                        txtGroupIcon.setText((groupNew.getName().charAt(0) + "").toUpperCase());
                    }

                    new loadListMemberGroup().execute();
                }

            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }

    }

    private void addEvent() {

        edtTextGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 1) {
                    txtGroupIcon.setText((charSequence.charAt(0) + "").toUpperCase());
                } else {
                    txtGroupIcon.setText("A");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAdapter.findContact(s.toString().trim().toLowerCase());
            }
        });

        tvAddGroup.setOnClickListener(v -> {
            if (listIDChoose.size() < 1) {
                Toast.makeText(AddGroupCustomerActivity.this, getString(R.string.text_error_group_member), Toast.LENGTH_SHORT).show();
            } else {
                if (edtTextGroupName.getText().toString().trim().length() == 0) {
                    Toast.makeText(AddGroupCustomerActivity.this, getString(R.string.text_error_input_group_name), Toast.LENGTH_SHORT).show();
                } else {
                    showProgress(true);
                    createGroup(v);
                }
            }
        });

        btnBack.setOnClickListener(view -> {
            resetData();
            onBackPressed();
        });

        btnSave.setOnClickListener(v -> {

            if (TextUtils.isEmpty(edtTextGroupName.getText().toString().trim())) {
                Toast.makeText(this, getString(R.string.text_error_input_group_name), Toast.LENGTH_LONG).show();
                return;
            }

            HashMap<String, Object> map = new HashMap<>();
            map.put("group_name", edtTextGroupName.getText().toString().trim());

            List<FieldCus> groupUser = new ArrayList<>();

//            for (CustomerRetroV1 cus : listIDChoose) {
//                groupUser.add(cus.);
//            }
            map.put("customers", groupUser);
            map.put("groupId", groupId);

            showProgress(true);
            Call<JSONResponse<Object>> addGroup = api.updateGroup(map);
            addGroup.enqueue(new Callback<JSONResponse<Object>>() {
                @Override
                public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getSuccess() != null) {
                                if (response.body().getSuccess()) {
                                    Snackbar.make(v, getString(R.string.text_success_save_group), Snackbar.LENGTH_LONG).setAction(getString(R.string.action), null).show();
                                    showProgress(false);

                                    groupNew.setName(edtTextGroupName.getText().toString().trim());
                                    groupNew.setId(groupId);
                                    groupNew.setCustomers(groupUser);

                                    for (GroupCustomerRetroV1 groupRetro : CustomersFragment.listGroup) {
                                        if (groupRetro.getId().equals(groupId)) {
                                            groupRetro.setCustomers(groupUser);
                                            groupRetro.setName(edtTextGroupName.getText().toString().trim());
                                        }
                                    }

                                    Intent intent = new Intent();
                                    intent.putExtra(Constants.KEY_PASSINGDATA_GROUP_OBJ, groupNew);
                                    intent.putExtra(Constants.KEY_IS_DELETED_GROUP, false);
                                    setResult(Constants.KEY_EDIT_GROUP_RESULT, intent);
                                    AddGroupCustomerActivity.this.finishAfterTransition();
                                    resetData();
                                } else {
                                    Toast.makeText(AddGroupCustomerActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    showProgress(false);
                                }
                            }
                            String msg;
                            if (response.body() != null) {
                                msg = response.body().getError();
                                if (!TextUtils.isEmpty(msg)) {
                                    Toast.makeText(AddGroupCustomerActivity.this, msg, Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            if (response.errorBody() != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = (String) jsonObject.get(Constants.ERROR);
                                    showProgress(false);
                                    if (!TextUtils.isEmpty(msg)) {
                                        Toast.makeText(AddGroupCustomerActivity.this, msg, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException | IOException ignored) {

                                }
                            }
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
                        Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                        showProgress(false);
                    }
                }

                @Override
                public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                    Toast.makeText(AddGroupCustomerActivity.this, getString(R.string.text_error_unknown), Toast.LENGTH_LONG).show();
                    if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), t.toString());
                    showProgress(false);
                }
            });
        });

        tvDeleteGroup.setOnClickListener(view -> {

            try {
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddGroupCustomerActivity.this);
                final String message = getString(R.string.alert_message_deleted_group);

                builder.setMessage(message).setTitle(getString(R.string.delete_group))
                        .setNegativeButton(getString(R.string.revoke),
                                (dialog, which) -> dialog.dismiss())
                        .setPositiveButton(getString(R.string.agree),
                                (dialog, which) -> {
                                    showProgress(true);

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("groupId", groupId);

                                    showProgress(true);
                                    Call<JSONResponse<Object>> addGroup = api.removeGroup(groupId);
                                    addGroup.enqueue(new Callback<JSONResponse<Object>>() {
                                        @Override
                                        public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                                            try {
                                                if (response.isSuccessful()) {
                                                    if (response.body() != null && response.body().getSuccess() != null) {
                                                        if (response.body().getSuccess()) {
                                                            showProgress(false);
                                                            Intent intent = new Intent();
                                                            intent.putExtra(Constants.KEY_IS_DELETED_GROUP, true);
                                                            setResult(Constants.KEY_EDIT_GROUP_RESULT, intent);
                                                            Intent intent1 = new Intent();
                                                            intent1.setAction(Constants.ACTION_UPDATE_CUSTOMER);
                                                            sendBroadcast(intent1);
                                                            finishAfterTransition();
                                                        } else {
                                                            Toast.makeText(AddGroupCustomerActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                                            showProgress(false);
                                                        }
                                                    }

                                                    String msg;
                                                    if (response.body() != null) {
                                                        msg = response.body().getError();
                                                        if (!TextUtils.isEmpty(msg)) {
                                                            Toast.makeText(AddGroupCustomerActivity.this, msg, Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                } else {
                                                    if (response.errorBody() != null) {
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                                            String msg = (String) jsonObject.get(Constants.ERROR);
                                                            showProgress(false);
                                                            if (!TextUtils.isEmpty(msg)) {
                                                                Toast.makeText(AddGroupCustomerActivity.this, msg, Toast.LENGTH_LONG).show();
                                                            }
                                                        } catch (JSONException | IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            } catch (Exception e) {
                                                if (BuildConfig.DEBUG)
                                                    Log.e("" + getClass().getName(), e.getMessage());
                                                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                                                showProgress(false);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                                            Toast.makeText(AddGroupCustomerActivity.this, getString(R.string.text_error_unknown), Toast.LENGTH_LONG).show();
                                            if (BuildConfig.DEBUG)
                                                Log.e("" + getClass().getName(), t.toString());
                                            showProgress(false);
                                        }
                                    });
                                });
                builder.create().show();

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }
        });
    }

    private void resetData() {

        try {
            listIDChoose.clear();
            listIDRemove.clear();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void createGroup(View v) {

        try {

            if (!isEditGroup) {

                HashMap<String, Object> map = new HashMap<>();
                map.put("group_name", edtTextGroupName.getText().toString().trim());

                List<Object> groupUser = new ArrayList<>();

//                for (CustomerRetro cus : listIDChoose) {
//                    groupUser.add(cus.toMapGroup());
//                }
                map.put("customers", groupUser);
                showProgress(true);
                Call<JSONResponse<Object>> addGroup = api.addGroupCustomer(map);
                addGroup.enqueue(new Callback<JSONResponse<Object>>() {
                    @Override
                    public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null && response.body().getSuccess() != null) {
                                    if (response.body().getSuccess()) {
                                        Snackbar.make(v, getString(R.string.text_success_save_group), Snackbar.LENGTH_LONG).setAction(getString(R.string.action), null).show();
                                        showProgress(false);

                                        groupNew.setName(edtTextGroupName.getText().toString().trim());
                                        groupNew.setId(groupId);
//                                        groupNew.setCustomers(groupUser);

                                        CustomersFragment.listGroup.add(groupNew);

                                        Intent intent = new Intent();
                                        intent.putExtra(Constants.KEY_GROUP_NAME, edtTextGroupName.getText().toString());
                                        intent.putExtra(Constants.KEY_IS_DELETED_GROUP, false);
                                        setResult(Constants.KEY_EDIT_GROUP_RESULT, intent);
                                        AddGroupCustomerActivity.this.finishAfterTransition();
                                        resetData();
                                    } else {
                                        Utilities.refreshToken(MainActivity.mainContext, response.body().getMessage().toLowerCase() + "");
                                        Toast.makeText(AddGroupCustomerActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                        showProgress(false);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
                            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                            showProgress(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                        Toast.makeText(AddGroupCustomerActivity.this, getString(R.string.text_error_unknown), Toast.LENGTH_LONG).show();
                        if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), t.toString());
                        showProgress(false);
                    }
                });
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        resetData();
        finishAfterTransition();
    }

    private void showProgress(boolean show) {
        if (show) {
            layoutContainer.setAlpha((float) 0.5);
            layoutContainer.setEnabled(false);
            progressBarSave.setVisibility(View.VISIBLE);
            progressBarSave.setAlpha(1);
        } else {
            progressBarSave.setVisibility(View.GONE);
            layoutContainer.setEnabled(true);
            layoutContainer.setAlpha(1);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class loadListMemberGroup extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarMemberUser.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
//                List<FieldCus> data = groupNew.getCustomers();
//
//                for (Object object : data) {
//                    LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) object;
//
//                    for (int i = 0; i < listCustomer.size(); i++) {
//                        if (map.get("custid").toString().equals(listCustomer.get(i).getCustId())) {
//                            listCustomer.get(i).isSelectedGroup = true;
//                            listIDChoose.add(listCustomer.get(i));
//                            break;
//                        }
//                    }
//
//
//                }
//
//                mAdapter.notifyDataSetChanged();
//                mAdapter.updateList(listCustomer);
//                mAdapterPicked.notifyDataSetChanged();
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBarMemberUser.setVisibility(View.GONE);
        }
    }
}
