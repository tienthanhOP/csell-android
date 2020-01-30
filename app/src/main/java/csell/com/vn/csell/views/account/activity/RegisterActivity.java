package csell.com.vn.csell.views.account.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.UserController;
import csell.com.vn.csell.models.Category;
import csell.com.vn.csell.models.FieldV1;
import csell.com.vn.csell.models.Location;
import csell.com.vn.csell.models.ResUser;
import csell.com.vn.csell.sqlites.SQLCategories;
import csell.com.vn.csell.sqlites.SQLLocations;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.LoginActivity;
import csell.com.vn.csell.views.csell.adapter.LocationSortAdapter;
import csell.com.vn.csell.views.csell.adapter.SpinnerCategoryAdapter;
import io.fabric.sdk.android.Fabric;

public class RegisterActivity extends AppCompatActivity implements UserController.OnRegisterListener, UserController.OnSendVerifyEmailListener, UserController.OnActiveAccountListener {

    public static List<FieldV1> listJobs;
    public static ArrayList<String> listJobName;
    private TextInputEditText edtUsername, edtPhone, edtEmail, edtPassword, edtRepassword, edtDisplayName;
    private EditText edt_code_verify;
    private Spinner edtChooseJobs;
    private TextView txt_signin, txtBack;
    private Button btnRegister;
    private Button btn_verify;
    private ProgressBar progressBarRegister;
    private RelativeLayout frame_register;
    private ScrollView frame_register_verify;
    private Spinner spnCity;
    private ArrayList<Category> dataCategory;
    private SQLCategories sqlCategories;
    private Location city;
    private LocationSortAdapter adapterCity;
    private String cityName = "";
    private List<Location> lsCity;
    private SQLLocations sqlLocations;
    private LinearLayout btnResend;
    private String email = "";
    private boolean isActiveAccount = false;
    private BaseActivityTransition baseActivityTransition;
    private UserController userController;

    private TextView btnCarJob;
    private TextView btnBdsJob;
    private TextView btnSimJob;
    private boolean isCarJob;
    private boolean isBdsJob;
    private boolean isSimJob;
    private TextView tvNoti;
    private TextView tvCountDownTimer;

    public RegisterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Fabric.with(this, new Crashlytics());
        userController = new UserController(this);
        initView();
        setupWindowAnimations();
        initEnvent();
        loadDataToActiveAccount();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    private void initView() {
        baseActivityTransition = new BaseActivityTransition(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        txtBack = findViewById(R.id.btn_back_register);

        btnCarJob = findViewById(R.id.btn_car_job);
        btnBdsJob = findViewById(R.id.btn_bds_job);
        btnSimJob = findViewById(R.id.btn_sim_job);

        edtUsername = findViewById(R.id.edt_username);
        edtChooseJobs = findViewById(R.id.edt_chooseJobs);
        edt_code_verify = findViewById(R.id.edt_code_verify);
        progressBarRegister = findViewById(R.id.progress_bar_register);
        frame_register = findViewById(R.id.frame_register);
        frame_register_verify = findViewById(R.id.frame_register_verify);
        edtPhone = findViewById(R.id.edt_phone);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtRepassword = findViewById(R.id.edt_repassword);
        edtDisplayName = findViewById(R.id.edt_displayname);
        btnRegister = findViewById(R.id.btn_register);
        btn_verify = findViewById(R.id.btn_verify);
        txt_signin = findViewById(R.id.txt_signin);
        frame_register_verify.setVisibility(View.GONE);
        listJobName = new ArrayList<>();
        sqlCategories = new SQLCategories(this);
        listJobs = new ArrayList<>();
        dataCategory = new ArrayList<>();
        dataCategory = sqlCategories.getListCategoryByLevel(null, 1);

        Category category = new Category();
        category.category_name = "Lĩnh vực hoạt động";
        category.category_id = "-1";
        dataCategory.add(0, category);

        SpinnerCategoryAdapter adapterCategory = new SpinnerCategoryAdapter(this, dataCategory);
        edtChooseJobs.setAdapter(adapterCategory);
        edtChooseJobs.setSelection(0);

        spnCity = findViewById(R.id.spn_city);
        sqlLocations = new SQLLocations(this);
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
        btnResend = findViewById(R.id.btn_resend);

        tvNoti = findViewById(R.id.tv_noti);
        tvCountDownTimer = findViewById(R.id.tv_count_downTimer);
    }

    private void initEnvent() {
        btnCarJob.setOnClickListener(view -> {
            if (!isCarJob) {
                btnCarJob.setBackgroundResource(R.drawable.bg_soild_note_green);
                btnCarJob.setTextColor(Color.WHITE);
                isCarJob = true;
            } else {
                btnCarJob.setBackgroundResource(R.drawable.bg_border_note_green);
                btnCarJob.setTextColor(Color.parseColor("#808080"));
                isCarJob = false;
            }
        });

        btnBdsJob.setOnClickListener(view -> {
            if (!isBdsJob) {
                btnBdsJob.setBackgroundResource(R.drawable.bg_soild_note_green);
                btnBdsJob.setTextColor(Color.WHITE);
                isBdsJob = true;
            } else {
                btnBdsJob.setBackgroundResource(R.drawable.bg_border_note_green);
                btnBdsJob.setTextColor(Color.parseColor("#808080"));
                isBdsJob = false;
            }
        });

        btnSimJob.setOnClickListener(view -> {
            if (!isSimJob) {
                btnSimJob.setBackgroundResource(R.drawable.bg_soild_note_green);
                btnSimJob.setTextColor(Color.WHITE);
                isSimJob = true;
            } else {
                btnSimJob.setBackgroundResource(R.drawable.bg_border_note_green);
                btnSimJob.setTextColor(Color.parseColor("#808080"));
                isSimJob = false;
            }
        });
        txtBack.setOnClickListener(v -> onBackPressed());

        txt_signin.setOnClickListener(v -> finishAfterTransition());

        btnRegister.setOnClickListener(v -> {
            try {
                String userName = edtUsername.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String repassword = edtRepassword.getText().toString().trim();
                String displayName = edtDisplayName.getText().toString().trim();

//                if (userName.length() < 6) {
//                    edtUsername.requestFocus();
//                    edtUsername.setError(getString(R.string.text_error_length_username));
//                    return;
//                }

                if (TextUtils.isEmpty(displayName)) {
                    edtDisplayName.requestFocus();
                    edtDisplayName.setError(getString(R.string.please_do_not_leave_it_blank));
                    return;
                }
//                if (!Utilities.isValidEmail(email)) {
//                    edtEmail.requestFocus();
//                    edtEmail.setError(getString(R.string.invalid_email));
//                    return;
//                }
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
                    edtRepassword.requestFocus();
                    edtRepassword.setError(getString(R.string.please_do_not_leave_it_blank));
                    return;
                }
                if (!password.equals(repassword)) {
                    edtRepassword.requestFocus();
                    edtRepassword.setError(getString(R.string.passwords_do_not_match));
                    return;
                }

                if (password.length() < 6) {
                    edtPassword.requestFocus();
                    edtPassword.setError(getString(R.string.text_error_length_password));
                    return;
                }

                if (repassword.length() < 6) {
                    edtRepassword.requestFocus();
                    edtRepassword.setError(getString(R.string.text_error_length_password));
                    return;
                }

//                if (listJobs.size() == 0) {
//                    Toast.makeText(this, getString(R.string.text_error_job), Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                if (TextUtils.isEmpty(cityName)) {
//                    Toast.makeText(this, getString(R.string.text_error_city), Toast.LENGTH_LONG).show();
//                    return;
//                }

                if (isCarJob) {
                    listJobs.add(new FieldV1("car", "Ô tô"));
                }
                if (isBdsJob) {
                    listJobs.add(new FieldV1("land", "Bất động sản"));
                }
                if (isSimJob) {
                    listJobs.add(new FieldV1("sim", "Sim số"));
                }

                showProgress(true);
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", displayName);
                map.put("phone", phone);
                map.put("password", Utilities.encryptString(password));
                if (listJobs.size() > 0) {
                    map.put("fields", listJobs);
                }

                registerWithApi(map);
                listJobs.clear();

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.toString());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }

        });

        btn_verify.setOnClickListener(v -> {

            if (edt_code_verify.getText().toString().trim().length() < 6) {
                edt_code_verify.setError(getString(R.string.text_error_length_code));
                return;
            }

            activeAccount(edt_code_verify.getText().toString().trim());

        });

        spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    cityName = lsCity.get(position).getLocation_name();
                } else {
                    cityName = "";

                    if (lsCity.size() == 1) {

                        lsCity.clear();
                        city = new Location();
                        city.setLocation_name(getString(R.string.city));
                        city.setLocation_id(-1);
                        city.setParent_id(-1);
                        city.setLocation_level(-1);

                        lsCity.add(0, city);
                        lsCity.addAll(sqlLocations.getAllCity(1));
                        adapterCity.notifyDataSetChanged();
                        adapterCity = new LocationSortAdapter(RegisterActivity.this, lsCity);
                        spnCity.setAdapter(adapterCity);
                        spnCity.setSelection(0);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    if (s.toString().contains(" ")) {
                        String removeSpace = s.toString().replace("0", "");
                        edtUsername.setText(removeSpace);
                    }
                }
            }
        });

        btnResend.setOnClickListener(view -> {
            sendCodeVerify(edtPhone.getText().toString().trim());
            new CountDownTimer(60000, 1000) {
                public void onTick(long millisUntilFinished) {
                    tvCountDownTimer.setVisibility(View.VISIBLE);
                    btnResend.setClickable(false);
                    tvCountDownTimer.setText("(" + millisUntilFinished / 1000 + ")");
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
                    tvCountDownTimer.setVisibility(View.GONE);
                    btnResend.setClickable(true);
                }
            }.start();
        });
        btnResend.setClickable(false);
    }

    private void activeAccount(String verify) {
        try {
            userController.activeAccount(edtPhone.getText().toString().trim(), verify, this);
        } catch (Exception ignored) {
            ignored.getMessage();
        }
    }

    private void registerWithApi(HashMap<String, Object> user) {

        if (BuildConfig.DEBUG) {
            ResUser a = new ResUser();
            a.setPhone("09888888888");
               /*"id": "1019",
        "name": "Thành Phạm",
        "email": null,
        "phone": "+841744122731",
        "group": "user",
        "avatar": null,
        "cover": null,
        "birthday": null,
        "gender": "unknown",
        "fields": [
            {
                "id": "car",
                "name": "Ô tô"
            }
        ],
        "country": "VN",
        "province": null,
        "district": null,
        "ward": null,
        "setting": {
            "notification": true,
            "language": "vi"
        },
        "totalPoint": 10,
        "lastPurchase": null,
        "expiredAt": null,
        "createdAt": 1574043236,
        "updatedAt": 1574043236
    }*/

            onRegisterSuccess(a);
        } else {
            userController.register(user, this);
        }
    }

    private void sendCodeVerify(String phone) {
        try {
            if (BuildConfig.DEBUG) {
                onSendVerifyPhoneSuccess(phone);
            } else {
                userController.sendVerifyPhone(phone, false, this);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    @Override
    public void onBackPressed() {
        if (!isActiveAccount) {
            if (frame_register_verify.getVisibility() == View.VISIBLE) {

                try {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
            } else {
                edtChooseJobs = null;
                listJobName = null;
                listJobs = null;
                finishAfterTransition();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void showProgress(boolean show) {
        if (show) {
            progressBarRegister.setVisibility(View.VISIBLE);
            frame_register.setAlpha(0.5f);
            frame_register_verify.setAlpha(0.5f);
            btnRegister.setAlpha(0.5f);
        } else {
            progressBarRegister.setVisibility(View.GONE);
            frame_register.setAlpha(1);
            frame_register_verify.setAlpha(1);
            btnRegister.setAlpha(1);
        }
    }

    private void loadDataToActiveAccount() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            email = bundle.getString(Constants.KEY_EMAIL);
            isActiveAccount = bundle.getBoolean(Constants.KEY_ACTIVE_ACCOUNT, false);

            frame_register.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
            frame_register_verify.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        edtChooseJobs = null;
    }

    @Override
    public void onRegisterSuccess(ResUser user) {
        try {
//            FileSave filePut = new FileSave(RegisterActivity.this, Constants.PUT);
//            filePut.putUserId(user.getId());
//            filePut.putUserName(user.getName());
//            filePut.putDisplayName(user.getName());
//            filePut.putUserEmail(user.getEmail());
//            filePut.putUserPhone(user.getPhone());
//            filePut.putUserAvatar(user.getAvatar() == null ? "" : user.getAvatar());
//            filePut.putUserCover(user.getCover() == null ? "" : user.getCover());
//            filePut.putUserAccount(user.getName());
//            if (user.getBirthday() != null) {
//                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//                String dob = dateFormat.format(Utilities.convertTimeMillisToDate(user.getBirthday()));
//                filePut.putDob(dob);
//            }
//
//            if (user.getFields() != null) {
//                List<String> strings = new ArrayList<>(user.getFields().size());
//                for (FieldV1 field : user.getFields()) {
//                    strings.add(field.getFieldId());
//                }
//
//                Set<String> foo = new HashSet<>(strings);
//                filePut.putUserJob(foo);
//            }
//            // check null ễcptrion
//
//
//
//            filePut.putLocationCityId(user.getProvince().getCode());
//            filePut.putUserCityName(user.getProvince().getName());
            showProgress(false);
            sendCodeVerify(user.getPhone());
        } catch (Exception ignored) {
            showProgress(false);
        }
    }

    @Override
    public void onErrorRegister(int errorCode) {
        try {
            showProgress(false);
            switch (errorCode) {
                case 371:
                    edtEmail.requestFocus();
                    break;
                case 372:
                    edtPhone.requestFocus();
                    break;
                case 376:
                    edtUsername.requestFocus();
                    break;
                default:
                    break;
            }
        } catch (Exception ignored) {
            showProgress(false);
        }
    }

    @Override
    public void onRegisterFailure(String msg) {
        try {
            if (msg.equals(getString(R.string.text_error_existed_username))) {
                edtUsername.setError(getString(R.string.text_error_existed_username));
            } else if (msg.equals(getString(R.string.text_error_existed_email))) {
                edtEmail.setError(getString(R.string.text_error_existed_email));
            } else if (msg.equals(getString(R.string.text_error_existed_phone))) {
                edtPhone.setError(getString(R.string.text_error_existed_phone));
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
    public void onSendVerifyPhoneSuccess(String phone) {
        try {
            frame_register.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
            frame_register_verify.setVisibility(View.VISIBLE);
            tvNoti.setText(getString(R.string.please_enter_6_digit_code_sent_to_your_registered_email, phone.substring(phone.length() - 3)));

        } catch (Exception ignored) {
            ignored.getMessage();
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
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
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
