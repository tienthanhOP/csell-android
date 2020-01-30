package csell.com.vn.csell.views.account.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.csell.adapter.LocationSortAdapter;
import csell.com.vn.csell.views.csell.adapter.SpinnerCategoryAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.UserController;
import csell.com.vn.csell.interfaces.GetDetail;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.models.Category;
import csell.com.vn.csell.models.Location;
import csell.com.vn.csell.models.Field;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.sqlites.SQLCategories;
import csell.com.vn.csell.sqlites.SQLLocations;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.//writeBatch;

public class EditUserActivity extends AppCompatActivity implements GetDetail {

    private EditText edtName, edtPhone, edtEmail, edtDob, edtUserName;
    private String dobTemp = "";
    @SuppressLint("StaticFieldLeak")
    public static Spinner edtChooseJobs;
    private ToggleButton btnEmail, btnAddress, btnUserName;
    private FancyButton btnBack;
    private FancyButton btnSave;
    private TextView tvTitle;

    private FileSave fileGet;
    private FileSave filePut;

    private ProgressBar progressBar;
    private LinearLayout layout;

    private LocationSortAdapter adapterCity;
    private String cityName = "";
    private Integer cityId;
    private List<Location> lsCity;
    private Spinner spnCity;
    private ArrayList<Category> dataCategory;
    private SQLCategories sqlCategories;

    public static ArrayList<String> listJobName;
    public static List<Field> listJobs;

    private RelativeLayout layoutCity, layoutJob;
    private AdapterView.OnItemSelectedListener adapterView;
    private SpinnerCategoryAdapter adapterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        Fabric.with(this, new Crashlytics());

        initView();
        setupWindowAnimations();
        getDetail();
        addEvent();

    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    private void getDetail() {
        UserController userController = new UserController(this, this);
        userController.getDetail();
    }

    private void initView() {
        edtChooseJobs = findViewById(R.id.edtJob);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtDob = findViewById(R.id.edtDob);
        edtUserName = findViewById(R.id.edtUserName);
        tvTitle = findViewById(R.id.custom_TitleToolbar);
        tvTitle.setText(getString(R.string.edit_info));
        btnUserName = findViewById(R.id.btnUserName);
        btnAddress = findViewById(R.id.btnAddress);
        btnEmail = findViewById(R.id.btnEmail);
        btnBack = findViewById(R.id.btn_back_navigation);
        btnBack.setText(getString(R.string.title_back_vn));
        btnSave = findViewById(R.id.btn_save_navigation);
        btnSave.setText(MainActivity.mainContext.getResources().getString(R.string.save));
        fileGet = new FileSave(this, Constants.GET);
        filePut = new FileSave(this, Constants.PUT);
        layout = findViewById(R.id.layout_container);
        progressBar = findViewById(R.id.progress_bar);

        spnCity = findViewById(R.id.spn_city);
        SQLLocations sqlLocations = new SQLLocations(this);
        lsCity = new ArrayList<>();
        lsCity.clear();

        Location city = new Location();
        city.setLocation_name(getString(R.string.city));
        city.setLocation_id(-1);
        city.setParent_id(-1);
        city.setLocation_level(-1);

        lsCity.add(0, city);
        lsCity.addAll(sqlLocations.getAllCity(1));
        adapterCity = new LocationSortAdapter(this, lsCity);
        spnCity.setAdapter(adapterCity);
        spnCity.setSelection(0);

        sqlCategories = new SQLCategories(this);
        dataCategory = new ArrayList<>();
        dataCategory = sqlCategories.getListCategoryByLevel(null, 1);
        listJobName = new ArrayList<>();
        listJobs = new ArrayList<>();
        layoutCity = findViewById(R.id.layout_city);
        layoutJob = findViewById(R.id.layout_job);

        adapterView = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    cityName = lsCity.get(position).getLocation_name();
                    cityId = lsCity.get(position).getLocation_id();
                } else {
                    cityName = "";
                    cityId = -1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        hideSoftKeyboard(layout);

        adapterCategory = new SpinnerCategoryAdapter(this, dataCategory);
        edtChooseJobs.setAdapter(adapterCategory);
    }

    private void addEvent() {

        btnBack.setOnClickListener(v -> onBackPressed());

        edtChooseJobs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category category = sqlCategories.getCategoryById(dataCategory.get(position).category_id);
                Field field = new Field();
                field.setFieldname(category.category_name);
                field.setFieldid(category.category_id);

                listJobs.clear();
                listJobs.add(field);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edtDob.setOnClickListener(v -> {
            DatePickerDialog.OnDateSetListener callback = (view, year, monthOfYear, dayOfMonth) -> {
                edtDob.setText(
                        dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                dobTemp = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            };
            if (!TextUtils.isEmpty(fileGet.getDateOfBirth())) {
                Calendar dobCal = Utilities.convertDateStringToCalendarAllType(fileGet.getDateOfBirth());
                DatePickerDialog pic = new DatePickerDialog(
                        EditUserActivity.this,
                        callback, dobCal.get(Calendar.YEAR), dobCal.get(Calendar.MONTH), dobCal.get(Calendar.DAY_OF_MONTH));
                pic.setTitle(getString(R.string.title_choose_dob));
                pic.show();
            } else {

                DatePickerDialog pic = new DatePickerDialog(
                        EditUserActivity.this,
                        callback, 2018, 1, 1);
                pic.setTitle(getString(R.string.title_choose_dob));
                pic.show();
            }

        });

        btnSave.setOnClickListener(v -> {

            try {
                String displayName = edtName.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                String dob = dobTemp;

                if (TextUtils.isEmpty(displayName)) {
                    edtName.setError(getString(R.string.please_do_not_leave_it_blank));
                    return;
                }

                if (TextUtils.isEmpty(phone.trim())) {
                    edtPhone.setError(getString(R.string.please_do_not_leave_it_blank));
                    return;
                }

                if (!phone.startsWith("0") || phone.length() < 10 || phone.length() > 11) {
                    edtPhone.setError(getString(R.string.invalid_phone_number));
                    return;
                }

                if (TextUtils.isEmpty(cityName)) {
                    Toast.makeText(this, getString(R.string.text_error_city), Toast.LENGTH_LONG).show();
                    return;
                }

                showProgress(true);

                UserRetro userRetro = new UserRetro();
                userRetro.setDisplayname(displayName);
                userRetro.setFields(listJobs);
                userRetro.setDob(dob);
                userRetro.setCity(cityName);
                userRetro.setPhone(phone);

                filePut.putDisplayName(displayName);
                filePut.putUserCityName(cityName);
                filePut.putUserCityId(cityId);
                filePut.putDob(dob);
                filePut.putUserPhone(phone);

                List<String> strings = new ArrayList<>(listJobs.size());
                for (Field field : listJobs) {
                    strings.add(field.getFieldid());
                }

                Set<String> foo = new HashSet<>(strings);
                filePut.putUserJob(foo);


                completeUpdate(userRetro);

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                showProgress(false);
            }

        });

        layoutCity.setOnClickListener(v -> spnCity.setOnItemSelectedListener(adapterView));

        spnCity.setOnItemSelectedListener(adapterView);

//        edtPhone.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus){
//
//                edtPhone.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//                        if (!TextUtils.isEmpty(s.toString())){
//                            if (s.toString().startsWith("+")){
//                                edtPhone.setText("+84");
//                            }else {
//                                edtPhone.setText("0");
//                            }
//                        }else {
//                            edtPhone.setText(fileGet.getUserPhone());
//                        }
//
//                    }
//                });
//
//            }
//        });
    }

    private void completeUpdate(UserRetro userRetro) {
        showProgress(true);
        PostAPI api = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        Call<JSONResponse<Object>> updateInfo = api.updateInfo(userRetro);
        updateInfo.enqueue(new Callback<JSONResponse<Object>>() {
            @Override
            public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {

                            if (response.body().getSuccess() != null) {
                                if (response.body().getSuccess()) {
                                    showProgress(false);
                                    finishAfterTransition();
                                    Toast.makeText(EditUserActivity.this, getString(R.string.text_success_update_user), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(EditUserActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    showProgress(false);
                                }
                            }

                            if (response.body().getErrorCode() != null) {
                                if (response.body().getErrorCode() == 506) {
                                    String msg = response.body().getError();
                                    Toast.makeText(EditUserActivity.this, msg, Toast.LENGTH_LONG).show();
                                }
                            }

                        }


                    } else {

                        if (response.code() == 400) {
                            try {
                                if (response.errorBody() != null) {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = (String) jsonObject.get(Constants.ERROR);

                                    if (!TextUtils.isEmpty(msg)) {
                                        Toast.makeText(EditUserActivity.this, msg, Toast.LENGTH_LONG).show();
                                    }
                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    showProgress(false);
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                    Crashlytics.logException(e);
                    showProgress(false);
                }
            }

            @Override
            public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
                Toast.makeText(EditUserActivity.this, getString(R.string.text_error_unknown), Toast.LENGTH_LONG).show();
                showProgress(false);
                Crashlytics.logException(t);
            }
        });

    }

    private void loadData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            try {
                String userName = fileGet.getUserName();
                edtUserName.setText(!userName.equals("") ? userName : "");
                String displayName = fileGet.getDisplayName();
                edtName.setText(!displayName.equals("") ? displayName : "");
                String phone = fileGet.getUserPhone();
                edtPhone.setText(!phone.equals("") ? phone : "");
                String email = fileGet.getUserEmail();
                edtEmail.setText(!email.equals("") ? email : "");

                String cityName = fileGet.getUserCityName();
                if (!TextUtils.isEmpty(cityName)) {
                    for (int i = 0; i < lsCity.size(); i++) {
                        if (lsCity.get(i).getLocation_name().equalsIgnoreCase(cityName)) {
                            spnCity.setSelection(i);
                            break;
                        }
                    }
                }

                Set<String> set = fileGet.getUserJob();
                List<String> list = new ArrayList<>(set);

                if (dataCategory != null) {
                    for (int i = 0; i < dataCategory.size(); i++) {
                        if (list.get(0).equalsIgnoreCase(dataCategory.get(i).category_id)) {
                            edtChooseJobs.setSelection(i);
                        }
                    }
                }

                String dob = fileGet.getDateOfBirth();
                if (!TextUtils.isEmpty(dob)) {
                    Calendar cal = Utilities.convertDateStringToCalendarAllType(dob);
//                    edtDob.setText(cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH));
                    edtDob.setText(cal.get(Calendar.DAY_OF_MONTH) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR));
                    dobTemp = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
                }

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        edtChooseJobs = null;
        listJobs = null;
        listJobName = null;
    }

    private void showProgress(boolean check) {
        if (check) {
            layout.setAlpha(0.5f);
            layout.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            layout.setAlpha(1);
            layout.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void showSoftKeyboard(View view) {
//        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        view.requestFocus();
//        inputMethodManager.showSoftInput(view, 0);

//        InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(view, 0);
        }

//        InputMethodManager imm = (InputMethodManager)
//                getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        edtChooseJobs = null;
    }

    @Override
    public void onGetDetail(UserRetro userRetro) {
        loadData();
    }
}
