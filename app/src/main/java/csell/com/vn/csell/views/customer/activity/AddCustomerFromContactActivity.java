package csell.com.vn.csell.views.customer.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
//import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.customer.adapter.AddCustomerFromContactAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.GetContacts;
import csell.com.vn.csell.interfaces.GetContactsListener;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.models.ContactLocal;
import csell.com.vn.csell.models.CustomerRetro;
import csell.com.vn.csell.sqlites.SQLContactLocal;
import csell.com.vn.csell.sqlites.SQLCustomers;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_CONTACTS;


public class AddCustomerFromContactActivity extends AppCompatActivity implements GetContactsListener {
    private int REQUEST_READ_CONTACTS = 444;
    private RecyclerView rvSuggestContact;
    LinearLayoutManager linearLayoutManager;
    private AddCustomerFromContactAdapter addCustomerFromContactAdapter;
    private ArrayList<ContactLocal> data;
    private SQLContactLocal db;
    private TextView tvError;
    private EditText txtSearch;
    private ImageView btnBack;
    private Button btnSave;
    private ProgressBar progressBar;
    private ProgressBar progressBarCount;
    private LinearLayout layoutCount;
    private TextView tvCount;
    public static boolean firstPermission = false;
    private FileSave fileGet;
    private SQLCustomers sqlCustomers;
    private ArrayList<CustomerRetro> listCustomer;
    private int timeout = 0;
    public static ArrayList<ContactLocal> listIDChoose;
    @SuppressLint("StaticFieldLeak")
    public static Button tvAddMultiple;
    private boolean pickAll = false;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout layoutAdd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_contact);
        Fabric.with(this, new Crashlytics());

        initView();
        setupWindowAnimations();
        addEvent();
        listCustomer = new ArrayList<>();
        data = new ArrayList<>();
        addCustomerFromContactAdapter = new AddCustomerFromContactAdapter(this, data);
        rvSuggestContact.setAdapter(addCustomerFromContactAdapter);

        if (!mayRequestContacts()) {
            //show dialog permission
            requestPermission();
            return;
        }

        try {
            new GetContacts(this, AddCustomerFromContactActivity.this).execute();
        } catch (UnsupportedEncodingException e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }

    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }


    private void initView() {
        layoutAdd = findViewById(R.id.layout_add);
        rvSuggestContact = findViewById(R.id.rv_suggest_contact);
        linearLayoutManager = new LinearLayoutManager(this);
        rvSuggestContact.setLayoutManager(linearLayoutManager);
        rvSuggestContact.setHasFixedSize(true);
        tvError = findViewById(R.id.tv_error);
        progressBarCount = findViewById(R.id.progress_bar_count);
        tvCount = findViewById(R.id.tv_count_progress);
        layoutCount = findViewById(R.id.layout_progress_count);
        txtSearch = findViewById(R.id.edt_search);
        btnBack = findViewById(R.id.btn_back_navigation);
        btnSave = findViewById(R.id.btn_save_navigation);
        btnSave.setText(getString(R.string.add_all));
        listIDChoose = new ArrayList<>();
        db = new SQLContactLocal(this);
        progressBar = findViewById(R.id.progress_bar_sugges_customer);
        fileGet = new FileSave(this, Constants.GET);
        sqlCustomers = new SQLCustomers(this);
        tvAddMultiple = findViewById(R.id.btnAddMultiple);
    }

    private void addEvent() {

        btnBack.setOnClickListener(v -> onBackPressed());

        btnSave.setOnClickListener(v -> {
            AddCustomerFromContactAdapter.isSearching = false;
            if (!pickAll) {

                for (ContactLocal local : data) {
//                    if (!local.isSelectedGroup) {
                        local.isSelectedGroup = true;
//                    }
                }

                btnSave.setText(getString(R.string.title_unpick_all));
                listIDChoose.clear();
                addCustomerFromContactAdapter.notifyDataSetChanged();

                listIDChoose.clear();
                listIDChoose.addAll(data);

                AddCustomerFromContactActivity.tvAddMultiple.setText(getString(R.string.add) + " "
                        + AddCustomerFromContactActivity.listIDChoose.size() + "/" + data.size());
                AddCustomerFromContactActivity.tvAddMultiple.setTextColor(getResources().getColor(R.color.white_100));
                AddCustomerFromContactActivity.tvAddMultiple.setBackgroundColor(getResources().getColor(R.color.red_100));

                linearLayoutManager.scrollToPositionWithOffset(data.size() - 1, data.size());

                pickAll = true;
            } else {
                for (ContactLocal local : data) {
                    if (local.isSelectedGroup) {
                        local.isSelectedGroup = false;
                    }

                }
                btnSave.setText(getString(R.string.title_pick_all));
                listIDChoose.clear();
                addCustomerFromContactAdapter.notifyDataSetChanged();

                tvAddMultiple.setText(getString(R.string.add));
                tvAddMultiple.setTextColor(getResources().getColor(R.color.dark_blue_100));
                tvAddMultiple.setBackgroundColor(getResources().getColor(R.color.white_background_100));

                pickAll = false;
            }
        });

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                addCustomerFromContactAdapter.findContact(s.toString());
            }
        });

        tvAddMultiple.setOnClickListener(v -> addAllContacts());

    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ActivityCompat.checkSelfPermission(this, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    new GetContacts(this, AddCustomerFromContactActivity.this).execute();
                } catch (UnsupportedEncodingException e) {
                    if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                    Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void requestPermission() {
        int checkContact = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int checkWContact = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS);
        int permissionGranted = PackageManager.PERMISSION_GRANTED;
        String[] permissions = new String[]{
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS};
        //check Android 6+
        if (Build.VERSION.SDK_INT >= 23) {
            //check permission granted
            if (checkContact != permissionGranted || checkWContact != permissionGranted) {
                //request Permissions
                int PERMISSION_CONSTANT = 1;
                requestPermissions(permissions, PERMISSION_CONSTANT);
            }
        }
    }

    private void showProgress(boolean show) {
        if (show) {
            rvSuggestContact.setAlpha((float) 0.5);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            rvSuggestContact.setAlpha(1);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            sqlCustomers = null;
            listIDChoose.clear();
            tvAddMultiple = null;
            finishAfterTransition();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void displayList(ArrayList<ContactLocal> arrContacts) {

        if (arrContacts.size() == 0) {
            btnSave.setVisibility(View.GONE);
            layoutAdd.setVisibility(View.GONE);
            return;
        }

        data.clear();
        data.addAll(arrContacts);
        Utilities.sortListContactLocal(data);
        ArrayList<ContactLocal> tempContacts = new ArrayList<>();
        tempContacts.addAll(data);
        for (int i = 0; i < data.size(); i++) {

            boolean check = false;

            if (!TextUtils.isEmpty(data.get(i).getEmail())) {
                String emailRep = data.get(i).getEmail();
                boolean kq = sqlCustomers.checkExistedEmail(emailRep);
                if (kq) {
                    db.updateAddedbyContactLocalId(data.get(i).getId());
                    int index = tempContacts.indexOf(data.get(i));
                    if (index != -1) tempContacts.remove(index);
                    check = true;
                }
            }

            if (!TextUtils.isEmpty(data.get(i).getPhone1())) {
                String phoneRep = data.get(i).getPhone1().replace(" ", "");
                boolean kq = sqlCustomers.checkExistedPhone(phoneRep);
                if (kq) {
                    if (!check) {
                        db.updateAddedbyContactLocalId(data.get(i).getId());
                        int index = tempContacts.indexOf(data.get(i));
                        if (index != -1) tempContacts.remove(index);
                    }

                }
            }

            if (!TextUtils.isEmpty(data.get(i).getPhone2())) {
                String phoneRep = data.get(i).getPhone2().replace(" ", "");
                boolean kq = sqlCustomers.checkExistedPhone(phoneRep);
                if (kq) {
                    if (!check) {
                        db.updateAddedbyContactLocalId(data.get(i).getId());
                        int index = tempContacts.indexOf(data.get(i));
                        if (index != -1) tempContacts.remove(index);
                    }

                }
            }
        }
        data.clear();
        data.addAll(tempContacts);

        addCustomerFromContactAdapter.notifyDataSetChanged();
        addCustomerFromContactAdapter.updateList(data);
    }

    @Override
    public void onGetContactsSuccess(ArrayList<ContactLocal> arrContacts) {
        if (!db.checkExistData()) {
            displayList(arrContacts);
            if (data.size() != 0) {
                new saveToSQLite().execute();
            }
            listCustomer.clear();
            listCustomer.addAll(addToListCustomer(data));
        } else {
            new checkExistsId(arrContacts).execute();
        }
    }

    private ArrayList<CustomerRetro> addToListCustomer(ArrayList<ContactLocal> arr) {
        ArrayList<CustomerRetro> lst = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {

            CustomerRetro customerNew = new CustomerRetro();
            if (!TextUtils.isEmpty(arr.get(i).getContactName())) {
                String newName = Utilities.upperFirstLetter(arr.get(i).getContactName());
                customerNew.setName(newName);
            }

            String email = arr.get(i).getEmail();
            List<String> listEmail = new ArrayList<>();
            if (!TextUtils.isEmpty(email)) {
                listEmail.add(email);
            }
            customerNew.setEmail(listEmail.size() != 0 ? listEmail : new ArrayList<>());

            String phone = arr.get(i).getPhone1();
            String phone2 = arr.get(i).getPhone2();

            List<String> listPhone = new ArrayList<>();
            if (!TextUtils.isEmpty(phone)) {
                listPhone.add(phone.replace(" ", ""));
                if (!TextUtils.isEmpty(phone2)) {
                    listPhone.add(phone2.replace(" ", ""));
                }
            }

            customerNew.setPhone(listPhone.size() != 0 ? listPhone : new ArrayList<>());
            customerNew.setDob(null);
            customerNew.setJobs(null);
            customerNew.setTags(null);
            customerNew.setAddress(null);
            customerNew.setNeed(null);
            customerNew.isAdded = null;
            customerNew.isSelectedGroup = null;
            customerNew.setCustId(null);

            lst.add(customerNew);
        }
        Utilities.sortListCustomer(lst);

        return lst;
    }

    @SuppressLint("StaticFieldLeak")
    class saveToSQLite extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            db.insertAllContactLocalTransaction(data);
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    class checkExistsId extends AsyncTask<Void, Void, ArrayList<ContactLocal>> {

        private ArrayList<ContactLocal> arr;

        checkExistsId(ArrayList<ContactLocal> list) {
            this.arr = list;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected ArrayList<ContactLocal> doInBackground(Void... voids) {

            int max = Integer.parseInt(db.getMaxId());
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).getId() > max) {
                    db.insertContactLocal(arr.get(i));
                } else break;
            }

            return db.getAllContactLocal();
        }

        @Override
        protected void onPostExecute(ArrayList<ContactLocal> list) {
            super.onPostExecute(list);
            showProgress(false);
            displayList(list);
            listCustomer.clear();
            listCustomer.addAll(addToListCustomer(list));
        }
    }

    private void addAllContacts() {

        if (listIDChoose.size() == 0) {
            Toast.makeText(this, getString(R.string.text_error_add_customer), Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<CustomerRetro> list = new ArrayList<>();
        list.addAll(addToListCustomer(listIDChoose));

        showPrgressCount(true);
        HashMap<String, Object> map = new HashMap<>();
        map.put(EntityAPI.FIELD_CUSTOMERS, list);

        PostAPI api = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        Call<JSONResponse<List<CustomerRetro>>> addList = api.addListCustomer(map);
        addList.enqueue(new Callback<JSONResponse<List<CustomerRetro>>>() {
            @Override
            public void onResponse(Call<JSONResponse<List<CustomerRetro>>> call, Response<JSONResponse<List<CustomerRetro>>> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getSuccess() != null) {
                            if (response.body().getSuccess()) {
                                ArrayList<CustomerRetro> lst = new ArrayList<>();
                                lst.addAll(response.body().getData());
                                sqlCustomers.insertListCustomers(lst);
                                for (ContactLocal local : listIDChoose) {
                                    db.updateAddedbyContactLocalId(local.getId());
                                    local.setAdded(true);
                                    data.remove(local);
                                }
                                Toast.makeText(AddCustomerFromContactActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                addCustomerFromContactAdapter.notifyDataSetChanged();
                                addCustomerFromContactAdapter.updateList(data);
                                showPrgressCount(false);

                                if (data.size() == 0) {
                                    layoutAdd.setVisibility(View.GONE);
                                    btnSave.setVisibility(View.GONE);
                                } else {
                                    layoutAdd.setVisibility(View.VISIBLE);
                                    btnSave.setVisibility(View.VISIBLE);
                                }

                                listIDChoose.clear();

                                tvAddMultiple.setText(getString(R.string.add));
                                tvAddMultiple.setTextColor(getResources().getColor(R.color.white_background_100));
                                tvAddMultiple.setBackgroundColor(getResources().getColor(R.color.white_background_100));
                            } else {


                                Utilities.refreshToken(MainActivity.mainContext, response.body().getMessage().toLowerCase() + "");
                                showPrgressCount(false);
                                Toast.makeText(AddCustomerFromContactActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                    } else {
                        try {
                            JSONObject jsonObject;
                            if (response.errorBody() != null) {
                                jsonObject = new JSONObject(response.errorBody().string());
                                String msg = (String) jsonObject.get(Constants.ERROR);
                                if (!TextUtils.isEmpty(msg)) {
                                    Toast.makeText(AddCustomerFromContactActivity.this, msg, Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                    showPrgressCount(false);
                    addList.cancel();
                } catch (Exception e) {
                    showPrgressCount(false);
                    if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                    Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                }
            }

            @Override
            public void onFailure(Call<JSONResponse<List<CustomerRetro>>> call, Throwable t) {
                addList.cancel();
                showPrgressCount(false);
                if (timeout < 1) {
                    timeout++;
                    addAllContacts();
                } else {
                    Toast.makeText(AddCustomerFromContactActivity.this, getString(R.string.text_error_unknown), Toast.LENGTH_LONG).show();
                    timeout = 0;
                    showPrgressCount(false);
                }
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
                Crashlytics.logException(t);
            }
        });
    }

    private void showPrgressCount(boolean check) {
        if (check) {
            layoutCount.setVisibility(View.VISIBLE);
            progressBarCount.setMax(data.size());
            rvSuggestContact.setAlpha((float) 0.5);
        } else {
            layoutCount.setVisibility(View.GONE);
            rvSuggestContact.setAlpha(1);
        }
    }

}

