package csell.com.vn.csell.views.account.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.esafirm.imagepicker.features.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.UserController;
import csell.com.vn.csell.models.ResUser;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.LoginActivity;
import csell.com.vn.csell.views.csell.adapter.LocationSortAdapter;
import csell.com.vn.csell.views.csell.adapter.SpinnerCategoryAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.models.Category;
import csell.com.vn.csell.models.Location;
import csell.com.vn.csell.models.Field;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.sqlites.SQLCategories;
import csell.com.vn.csell.sqlites.SQLLocations;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateInfoAccountActivity extends AppCompatActivity implements UserController.OnRegisterListener, UserController.OnSendVerifyEmailListener, UserController.OnActiveAccountListener {

    private EditText edtUserName, edtDisplayName, edtEmail, edtPhone;
    private Spinner tvJob;
    private Spinner spnCity;
    private Button btnDone;
    private SQLCategories sqlCategories;
    private SQLLocations sqlLocations;
    private List<Location> lsCity;
    public static List<Field> listJobs;
    public static ArrayList<String> listJobName;
    private Location city;
    private LocationSortAdapter adapterCity;
    private SpinnerCategoryAdapter adapterCategory;
    private String cityName = "";
    public static HashMap<String, Object> mapJobs;
    private ArrayList<Category> dataCategory;
    private ProgressBar progressBar;
    private RelativeLayout layout;
    private boolean checkEmail = false, checkPhone = false, checkUserName = false;
    //    private CountryListSpinner spnCountry;
    private ImageView imgAvatar;
    private LinearLayout frameInfo, frameAvatar;
    private TextView tvDone;
    private ArrayList<String> listPathImageResize = new ArrayList<>();
    private FileSave fileGet;
    private String avatarUrl = null;
    private ProgressBar progressImage;

    private EditText edtCode;
    private Button btnVerify;
    private LinearLayout frameVerify;
    UserRetro userRetro = null;
    String codeId = null;
    private EditText edtPassword, edtRepassword;
    private BaseActivityTransition baseActivityTransition;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);
        Fabric.with(this, new Crashlytics());
        userController = new UserController(this);
        initView();
        setupWindowAnimations();
        addEvent();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    private void initView() {
        try {
            baseActivityTransition = new BaseActivityTransition(this);

            edtUserName = findViewById(R.id.edt_username);
            edtDisplayName = findViewById(R.id.edt_displayname);
            edtEmail = findViewById(R.id.edt_email);
            edtPhone = findViewById(R.id.edt_phone);
            tvJob = findViewById(R.id.edt_job);
            spnCity = findViewById(R.id.spn_city);
            btnDone = findViewById(R.id.btn_done);

            FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
            sqlCategories = new SQLCategories(this);
            sqlLocations = new SQLLocations(this);
            listJobs = new ArrayList<>();
            listJobName = new ArrayList<>();

            if (firebaseUser != null) {
                edtDisplayName.setText(!TextUtils.isEmpty(firebaseUser.getDisplayName()) ? firebaseUser.getDisplayName() : "");
                edtEmail.setText(!TextUtils.isEmpty(firebaseUser.getEmail()) ? firebaseUser.getEmail() : "");
                edtPhone.setText(!TextUtils.isEmpty(firebaseUser.getPhoneNumber()) ? firebaseUser.getPhoneNumber() : "");
            }

            mapJobs = new HashMap<>();
            lsCity = new ArrayList<>();
            lsCity.clear();

            city = new Location();
            city.setLocation_name(getString(R.string.city));
            city.setLocation_id(-1);
            city.setParent_id(-1);
            city.setLocation_level(-1);

            lsCity.add(0, city);
            lsCity.addAll(sqlLocations.getAllCity(1));
            adapterCity = new LocationSortAdapter(this, lsCity);
            spnCity.setAdapter(adapterCity);
            spnCity.setSelection(0);

            dataCategory = new ArrayList<>();
            dataCategory = sqlCategories.getListCategoryByLevel(null, 1);

            Category category = new Category();
            category.category_name = "Lĩnh vực hoạt động";
            category.category_id = "-1";
            dataCategory.add(0, category);


            adapterCategory = new SpinnerCategoryAdapter(this, dataCategory);
            tvJob.setAdapter(adapterCategory);
            tvJob.setSelection(0);

            sqlLocations = new SQLLocations(this);

            progressBar = findViewById(R.id.progress_bar);
            layout = findViewById(R.id.layout);
//            spnCountry = findViewById(R.id.spinner_phone);
//            spnCountry.setSelectedForCountry(Locale.getDefault(), "+84");
            frameInfo = findViewById(R.id.frame_info);
            frameAvatar = findViewById(R.id.frame_avatar);
            tvDone = findViewById(R.id.tv_done);
            imgAvatar = findViewById(R.id.img_avatar);
            fileGet = new FileSave(this, Constants.GET);
            progressImage = findViewById(R.id.progress_image);

            edtCode = findViewById(R.id.edt_code_verify);
            btnVerify = findViewById(R.id.btn_verify);
            frameVerify = findViewById(R.id.frame_register_verify);
            edtPassword = findViewById(R.id.edt_password);
            edtRepassword = findViewById(R.id.edt_repassword);

            if (firebaseUser != null && !TextUtils.isEmpty(Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString())) {
                avatarUrl = firebaseUser.getPhotoUrl().toString();
                frameAvatar.setVisibility(View.VISIBLE);
                GlideApp.with(this)
                        .load(firebaseUser.getPhotoUrl())
                        .placeholder(R.drawable.ic_logo)
                        .error(R.drawable.ic_logo)
                        .into(imgAvatar);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
            Crashlytics.logException(e);
        }

    }

    private void addEvent() {

        tvJob.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    Category category = sqlCategories.getCategoryById(dataCategory.get(position).category_id);
                    Field field = new Field();
                    field.setFieldname(category.category_name);
                    field.setFieldid(category.category_id);

                    listJobs.clear();
                    listJobs.add(field);

                } else {
                    listJobs.clear();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnDone.setOnClickListener(v -> {

            try {
                String userName = edtUserName.getText().toString().trim();
                String displayName = edtDisplayName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String repassword = edtRepassword.getText().toString().trim();

                if (TextUtils.isEmpty(userName)) {
                    edtUserName.requestFocus();
                    edtUserName.setError(getString(R.string.text_error_empty));
                    return;
                }

                if (TextUtils.isEmpty(displayName)) {
                    edtDisplayName.requestFocus();
                    edtDisplayName.setError(getString(R.string.text_error_empty));
                    return;
                }

                if (!isValidEmail(email)) {
                    edtEmail.requestFocus();
                    edtEmail.setError(getString(R.string.email_address_incorrect));
                    return;
                }

                if (TextUtils.isEmpty(phone.trim())) {
                    edtPhone.requestFocus();
                    edtPhone.setError(getString(R.string.please_do_not_leave_it_blank));
                    return;
                }

                if (!phone.startsWith("0") || phone.length() > 11 || phone.length() < 10) {
                    edtPhone.requestFocus();
                    edtPhone.setError(getString(R.string.invalid_phone_number));
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    edtPassword.requestFocus();
                    edtPassword.setError(getString(R.string.please_do_not_leave_it_blank));
                    return;
                }
                if (TextUtils.isEmpty(repassword)) {
                    edtPassword.requestFocus();
                    edtRepassword.setError(getString(R.string.please_do_not_leave_it_blank));
                    return;
                }
                if (!password.equals(repassword)) {
                    edtRepassword.requestFocus();
                    edtRepassword.setError(getString(R.string.passwords_do_not_match));
                    return;
                }

                if (listJobs.size() == 0) {
                    Toast.makeText(this, getString(R.string.text_error_job), Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(cityName)) {
                    Toast.makeText(this, getString(R.string.text_error_city), Toast.LENGTH_LONG).show();
                    return;
                }

                userRetro = new UserRetro();
                userRetro.setDisplayname(displayName);
                userRetro.setUsername(userName);
                userRetro.setCity(cityName);
                userRetro.setPhone(phone.trim());
                userRetro.setFields(listJobs);
                userRetro.setEmail(email);
                userRetro.setDob(null);
                userRetro.setAddress(null);
                userRetro.setPassword(Utilities.encryptString(password));

                if (!TextUtils.isEmpty(avatarUrl)) {
                    userRetro.setAvatar(avatarUrl);
                }

                if (!checkEmail && !checkPhone && !checkUserName) {
                    showProgress(true);
                    updateInfo(userRetro);
                }

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }

        });

        btnVerify.setOnClickListener(v -> {

            if (edtCode.getText().toString().trim().length() < 6) {
                edtCode.requestFocus();
                edtCode.setError(getString(R.string.text_error_length_code));
                return;
            }
            activeAccount(edtCode.getText().toString().trim());
        });

        spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    cityName = lsCity.get(position).getLocation_name();
                } else {
                    cityName = "";

                    if (lsCity.size() == 1) {

//                        Toast.makeText(RegisterActivity.this, "Dữ liệu đang được cập nhật! Xin vui lòng đợi trong giây lát!", Toast.LENGTH_LONG).show();

                        lsCity.clear();
                        city = new Location();
                        city.setLocation_name(getString(R.string.city));
                        city.setLocation_id(-1);
                        city.setParent_id(-1);
                        city.setLocation_level(-1);

                        lsCity.add(0, city);
                        lsCity.addAll(sqlLocations.getAllCity(1));
                        adapterCity.notifyDataSetChanged();
                        adapterCity = new LocationSortAdapter(UpdateInfoAccountActivity.this, lsCity);
                        spnCity.setAdapter(adapterCity);
                        spnCity.setSelection(0);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imgAvatar.setOnClickListener(view -> {
            ImagePicker.create(this)
                    .folderMode(true) // folder mode (false by default)
                    .toolbarFolderTitle("Folder") // folder selection title
                    .toolbarImageTitle("Tap to select") // image selection title
                    .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
                    .limit(1) // max images can be selected (99 by default)
                    .showCamera(true) // show camera or not (true by default)
                    .imageDirectory("csell") // directory name for captured image  ("Camera" folder by default)
                    .theme(R.style.ImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
                    .enableLog(false) // disabling log
                    .start(); // start image picker activity with request code
        });

        edtUserName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String username = edtUserName.getText().toString().trim();
                if (!TextUtils.isEmpty(username)) {
                    boolean atleastOneAlpha = username.matches(".*[a-zA-Z]+.*");
                    if (!atleastOneAlpha) {
                        edtUserName.setError(getString(R.string.text_error_alphabet_username));
                        return;
                    }

                    checkExistInfo(edtUserName.getText().toString().trim());
                }
            }
        });

        edtPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!TextUtils.isEmpty(edtPhone.getText().toString().trim())) {
                    checkExistInfo(edtPhone.getText().toString().trim());
                }
            }
        });

        edtEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!TextUtils.isEmpty(edtEmail.getText().toString().trim())) {
                    checkExistInfo(edtEmail.getText().toString().trim());
                }
            }
        });

    }

    private void updateInfo(UserRetro paramsUsers) {
//        try {
//            UserController userController = new UserController(this);
//            userController.register(paramsUsers, this);
//        } catch (Exception ignored) {
//
//        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void showProgress(boolean check) {
        if (check) {
            progressBar.setVisibility(View.VISIBLE);
            layout.setAlpha(0.5f);
            layout.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            layout.setAlpha(1);
            layout.setEnabled(true);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            avatarUrl = "";
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    Utilities.resizeImageFromCamera(data, listPathImageResize);
                    return null;
                }
            }.execute();

            progressImage.setVisibility(View.VISIBLE);
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                }

                @Override
                protected Void doInBackground(Void... voids) {
                    uploadImageGetURL(listPathImageResize);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
//                    showProgress(false);
                }
            }.execute();
        }
    }

    private void uploadImageGetURL(ArrayList<String> listPathImage) {

        try {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef;
            String userId = fileGet.getUserId() + "";
            for (int i = 0; i < listPathImage.size(); i++) {
                Long time = System.currentTimeMillis();
                String path;
                path = userId + "/Avatar/" + time;
                storageRef = storage.getReference(path);

                Uri file = Uri.fromFile(new File(listPathImage.get(i)));
                UploadTask uploadTask = storageRef.putFile(file);

                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    Uri uri = taskSnapshot.getDownloadUrl();
                    URL p = null;
                    try {
                        if (uri != null) {
                            p = new URL(uri.toString());
                        }
                    } catch (MalformedURLException ignored) {

                    }
                    progressImage.setVisibility(View.GONE);
                    if (p != null) {
                        avatarUrl = p.toString();
                    }

                    GlideApp.with(this)
                            .load(avatarUrl)
                            .placeholder(R.drawable.ic_logo)
                            .error(R.drawable.ic_logo)
                            .into(imgAvatar);

                }).addOnFailureListener(e -> {
                    if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
                    progressImage.setVisibility(View.GONE);
                });
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            progressImage.setVisibility(View.GONE);
        }
    }

    private void sendCodeVerify() {
        try {
            userController.sendVerifyPhone(edtEmail.getText().toString().trim(), false, this);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void activeAccount(String verify) {
        try {
            userController.activeAccount(edtEmail.getText().toString().trim(), verify, this);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onBackPressed() {
        if (frameVerify.getVisibility() == View.VISIBLE) {

            try {
                final AlertDialog.Builder builder = new AlertDialog.Builder(UpdateInfoAccountActivity.this);
                final String message = getString(R.string.alert_message_exit_register);

                builder.setMessage(message).setTitle(getString(R.string.alert_title_exit_register))
                        .setNegativeButton(getString(R.string.revoke),
                                (dialog, which) -> dialog.dismiss())
                        .setPositiveButton(getString(R.string.agree),
                                (dialog, which) -> {
                                    finishAfterTransition();
                                    super.onBackPressed();
                                });
                builder.create().show();

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }

//            frameVerify.setVisibility(View.GONE);
//            frameInfo.setVisibility(View.VISIBLE);
//            btnDone.setVisibility(View.VISIBLE);
        } else {
            tvJob = null;
            listJobs = null;
            listJobName = null;
            mapJobs = null;
            super.onBackPressed();
        }
    }

    private void checkExistInfo(String value) {
        try {
            GetAPI getAPI = RetrofitClient.createServiceLogin(GetAPI.class);

            if (Utilities.isValidEmail(value)) {
                Call<JSONResponse<Object>> check = getAPI.verifyAccount(value);
                check.enqueue(new Callback<JSONResponse<Object>>() {
                    @Override
                    public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                        handleResponse(response, check, value);
                    }

                    @Override
                    public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                        check.cancel();
                    }
                });
            }
        } catch (Exception ignored) {

        }
    }

    private void handleResponse(Response<JSONResponse<Object>> response, Call<JSONResponse<Object>> check, String value) {
        if (response.isSuccessful()) {
            if (response.body() != null && response.body().getSuccess() != null) {

                boolean success = response.body().getSuccess();

                if (Utilities.isValidEmail(value)) {
                    if (success) {
                        edtEmail.setError(null);
                        edtEmail.setCompoundDrawables(null, null, null, null);
                        edtEmail.setCompoundDrawablesWithIntrinsicBounds(null, null,
                                getResources().getDrawable(R.drawable.ic_check_circle_green), null);
                        checkEmail = false;
                    }
                } else if (TextUtils.isDigitsOnly(value)) {
                    if (success) {
                        edtPhone.setError(null);
                        edtPhone.setCompoundDrawables(null, null, null, null);

                        edtPhone.setCompoundDrawablesWithIntrinsicBounds(null, null,
                                getResources().getDrawable(R.drawable.ic_check_circle_green), null);
                        checkPhone = false;
                    }
                } else {
                    if (success) {
                        edtUserName.setError(null);
                        edtUserName.setCompoundDrawables(null, null, null, null);
                        edtUserName.setCompoundDrawablesWithIntrinsicBounds(null, null,
                                getResources().getDrawable(R.drawable.ic_check_circle_green), null);
                        checkUserName = false;
                    }
                }

            }

        } else {
            if (response.errorBody() != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                    String msg = (String) jsonObject.get(Constants.ERROR);

                    if (Utilities.isValidEmail(value)) {
                        if (msg.equals(getString(R.string.text_error_existed_account))) {
                            checkEmail = true;
                            edtEmail.setError(getString(R.string.text_error_existed_email));
                        }
                    } else if (TextUtils.isDigitsOnly(value)) {
                        if (msg.equals(getString(R.string.text_error_existed_account))) {
                            checkPhone = true;
                            edtPhone.setError(getString(R.string.text_error_existed_phone));
                        }
                    } else {
                        if (msg.equals(getString(R.string.text_error_existed_account))) {
                            checkUserName = true;
                            edtUserName.setError(getString(R.string.text_error_existed_account));
                        }
                    }

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        check.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tvJob = null;
    }

    @Override
    public void onRegisterSuccess(ResUser paramsUsers) {
        try {
            checkUserName = false;
            checkEmail = false;
            checkPhone = false;
            FileSave fileSave = new FileSave(UpdateInfoAccountActivity.this, Constants.PUT);
            //                        fileSave.putUserId(uid);
            fileSave.putUserEmail(paramsUsers.getEmail());
            fileSave.putDisplayName(paramsUsers.getName());
            fileSave.putUserName(paramsUsers.getName());
            fileSave.putUserPhone(paramsUsers.getPhone());
            fileSave.putUserCityName(paramsUsers.getProvince().getName());
            fileSave.putAutoLogin(true);
            fileSave.putUserAccount(paramsUsers.getName());
            List<String> strings = new ArrayList<>(listJobs.size());
            for (Field field : listJobs) {
                strings.add(field.getFieldid());
            }

            Set<String> foo = new HashSet<>(strings);
            fileSave.putUserJob(foo);
            sendCodeVerify();
            showProgress(false);
        } catch (Exception ignored) {
            showProgress(false);
        }
    }

    @Override
    public void onErrorRegister(int errorCode) {

    }

    @Override
    public void onRegisterFailure(String msg) {
        try {
            if (msg.equals(getString(R.string.text_error_existed_username))) {
                checkUserName = true;
                edtUserName.setError(getString(R.string.text_error_existed_username));
            } else if (msg.equals(getString(R.string.text_error_existed_email))) {
                checkEmail = true;
                edtEmail.setError(getString(R.string.text_error_existed_email));
            } else if (msg.equals(getString(R.string.text_error_existed_phone))) {
                checkPhone = true;
                edtPhone.setError(getString(R.string.text_error_existed_phone));
            }

            if (!checkUserName) {
                edtUserName.setError(null);
                edtUserName.setCompoundDrawables(null, null, null, null);
                edtUserName.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getResources().getDrawable(R.drawable.ic_check_circle_green), null);
            }

            if (!checkPhone) {
                edtPhone.setError(null);
                edtPhone.setCompoundDrawables(null, null, null, null);
                edtPhone.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getResources().getDrawable(R.drawable.ic_check_circle_green), null);
            }

            if (!checkEmail) {
                edtEmail.setError(null);
                edtEmail.setCompoundDrawables(null, null, null, null);
                edtEmail.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getResources().getDrawable(R.drawable.ic_check_circle_green), null);
            }
            showProgress(false);
        } catch (Exception ignored) {
            showProgress(false);
        }
    }

    @Override
    public void onConnectRegisterFailure() {
        showProgress(false);
    }

    @Override
    public void onSendVerifyPhoneSuccess(String email) {
        try {
            frameInfo.setVisibility(View.GONE);
            btnDone.setVisibility(View.GONE);
            frameVerify.setVisibility(View.VISIBLE);
            frameAvatar.setVisibility(View.GONE);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onErrorSendVerifyEmail() {

    }

    @Override
    public void onSendVerifyEmailFailure() {

    }

    @Override
    public void onConnectSendVerifyEmailFailure() {

    }

    @Override
    public void onActiveAccountSuccess() {
        try {
            Intent intent = new Intent(UpdateInfoAccountActivity.this, LoginActivity.class);
            baseActivityTransition.transitionTo(intent, 0);
            finishAfterTransition();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onErrorActiveAccount() {

    }

    @Override
    public void onActiveAccountFailure() {

    }

    @Override
    public void onConnectActiveAccountFailure() {

    }
}
