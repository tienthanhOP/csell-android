package csell.com.vn.csell.views.customer.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.CustomersController;
import csell.com.vn.csell.models.CustomerRetroV1;
import csell.com.vn.csell.models.FieldCus;
import csell.com.vn.csell.models.GroupCustomerRetroV1;
import csell.com.vn.csell.models.ResCustomers;
import csell.com.vn.csell.models.ResDelete;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.customer.adapter.ChooseGroupCustomerFirebaseAdapter;
import csell.com.vn.csell.views.customer.adapter.PickedGroupAdapter;
import csell.com.vn.csell.views.customer.fragment.CustomersFragment;
import csell.com.vn.csell.views.social.adapter.HashtagAdapter;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//////import com.google.firebase.firestore.DocumentSnapshot;
////import com.google.firebase.firestore.FirebaseFirestore;

public class AddOrEditCustomerActivity extends AppCompatActivity
        implements CustomersController.OnAddCustomerListener,
        CustomersController.OnUpdateCustomerListener {

    @SuppressLint("StaticFieldLeak")
    public static RecyclerView rvPickedGroup;
    @SuppressLint("StaticFieldLeak")
    public static PickedGroupAdapter mAdapterPickedGroup;
    public static ArrayList<GroupCustomerRetroV1> listIDGroupChoose;
    public static ArrayList<GroupCustomerRetroV1> listIDGroupRemove;
    @SuppressLint("StaticFieldLeak")
    public static RecyclerView rvHashtag;
    CustomerRetroV1 customerNew;
    CustomerRetroV1 customerIntent;
    String dobStr = "";
    private EditText edtFullName;
    private EditText edtPhone;
    private EditText edtPhone2;
    private LinearLayout tvPhone2, tvEmail2;
    private EditText edtEmail, edtEmail2;
    private EditText edtHashtag;
    private EditText edtNeed;
    private EditText edtAddress;
    private TextView edtDob;
    private EditText edtJobs;
    private TextView tvAvatarContact;
    private ImageView imgDob, imgEdit;
    private ImageButton btnCancel;
    private Dialog dialog;
    private FancyButton btnBackNavigation;
    private Button btnSave;
    private FancyButton btnSaveNavigation;
    private TextView titleToolbar;
    private TextView tvDeleteCustomer;
    private ProgressBar progressBar;
    private LinearLayout layoutContainer;
    private String name;
    private String email, email2;
    private String phone, phone2;
    private String need;
    private String dob;
    private String dobTemp;
    private String address;
    private String jobs;
    private String key = "";
    //    private SQLCustomers sqlCustomers;
    private LinearLayout tvAddNeed, tvAddPhone, tvAddEmail, tvAddHashtag, tvAddAddress, tvAddDob, tvAddJob;
    private LinearLayout layoutPhone, layoutEmail, layoutDob, layoutHashtag;
    private HashtagAdapter hashtagAdapter;
    private ArrayList<String> dataHashtag;
    private RelativeLayout frameGroup;
    private int timeoutEdit = 0, timeoutAdd = 0;
    private FileSave fileGet;
    private Context mContext;
    private BaseActivityTransition baseActivityTransition;
    private CustomersController customersController;

    public static boolean isValidEmail(CharSequence target) {
        return (!Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        mContext = this;
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
        setupWindowAnimations();
        eventClick();
        getDataToEdit();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    private void getDataToEdit() {
        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                key = bundle.getString(Constants.EDIT_CUSTOMER_TAG);
                customerIntent = (CustomerRetroV1) bundle.getSerializable(Constants.KEY_PASSINGDATA_CUSTOMER_OBJ);

                if (!TextUtils.isEmpty(key)) {
                    btnSaveNavigation.setVisibility(View.VISIBLE);
                    frameGroup.setVisibility(View.GONE);
                    tvDeleteCustomer.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.GONE);
                    btnSaveNavigation.setText(MainActivity.mainContext.getResources().getString(R.string.title_toolbar_done_vn));
                    btnSaveNavigation.setEnabled(true);
                    setValue(customerIntent);
                    titleToolbar.setText(getString(R.string.update_information));
                } else {
                    btnSaveNavigation.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void setValue(CustomerRetroV1 customerIntent) {
        try {
            edtFullName.setText(customerIntent.getName() == null ? "" : customerIntent.getName());
            if (customerIntent.getPhones() != null) {
                if (customerIntent.getPhones().size() != 0) {
                    tvAddPhone.setVisibility(View.GONE);
                    layoutPhone.setVisibility(View.VISIBLE);
                    edtPhone.setText(
                            customerIntent.getPhones().get(0) == null ? "" :
                                    customerIntent.getPhones().get(0).getAddress());

                    if (customerIntent.getPhones().size() > 1) {
                        tvPhone2.setVisibility(View.GONE);
                        layoutPhone.setVisibility(View.VISIBLE);
                        edtPhone2.setVisibility(View.VISIBLE);
                        edtPhone2.setText(
                                customerIntent.getPhones().get(1) == null ? "" :
                                        customerIntent.getPhones().get(1).getAddress());
                    } else {
                        edtPhone2.setVisibility(View.GONE);
                    }
                }
            }

            if (customerIntent.getEmails() != null) {
                if (customerIntent.getEmails().size() != 0) {
                    tvAddEmail.setVisibility(View.GONE);
                    layoutEmail.setVisibility(View.VISIBLE);
                    edtEmail.setText(
                            customerIntent.getEmails().get(0) == null ? "" :
                                    customerIntent.getEmails().get(0).getAddress());

                    if (customerIntent.getEmails().size() > 1) {
                        tvEmail2.setVisibility(View.GONE);
                        layoutEmail.setVisibility(View.VISIBLE);
                        edtEmail2.setVisibility(View.VISIBLE);
                        edtEmail2.setText(
                                customerIntent.getEmails().get(1) == null ? "" :
                                        customerIntent.getEmails().get(1).getAddress());
                    } else {
                        edtEmail2.setVisibility(View.GONE);
                    }
                }
            }

            if (customerIntent.getHashtag() != null) {
                if (customerIntent.getHashtag().size() != 0) {
                    tvAddHashtag.setVisibility(View.GONE);

                    dataHashtag.addAll(customerIntent.getHashtag());
                    hashtagAdapter.notifyDataSetChanged();
                    rvHashtag.setVisibility(View.VISIBLE);
                    layoutHashtag.setVisibility(View.VISIBLE);
                }
            }

            if (!TextUtils.isEmpty(customerIntent.getNote())) {
                tvAddNeed.setVisibility(View.GONE);
                edtNeed.setVisibility(View.VISIBLE);
                edtNeed.setText(
                        customerIntent.getNote() == null ? "" :
                                customerIntent.getNote());
            }

            if (!TextUtils.isEmpty(customerIntent.getAddress())) {
                tvAddAddress.setVisibility(View.GONE);
                edtAddress.setVisibility(View.VISIBLE);
                edtAddress.setText(
                        customerIntent.getAddress() == null ? "" :
                                customerIntent.getAddress());
            }

//            if (!TextUtils.isEmpty(customerIntent.getJobs())) {
//                tvAddJob.setVisibility(View.GONE);
//                edtJobs.setVisibility(View.VISIBLE);
//                edtJobs.setText(
//                        customerIntent.getJobs() == null ? "" :
//                                customerIntent.getJobs());
//            }

            if (!TextUtils.isEmpty(customerIntent.getBirthday()) && !customerIntent.getBirthday().equals("Invalid date")) {
                Calendar dobCal = Utilities.convertDateStringToCalendarAllType(customerIntent.getBirthday());
//                dobStr = dobCal.get(Calendar.YEAR) + "-" + (dobCal.get(Calendar.MONTH) + 1) + "-" + dobCal.get(Calendar.DAY_OF_MONTH);
                dobStr = dobCal.get(Calendar.DAY_OF_MONTH) + "-" + (dobCal.get(Calendar.MONTH) + 1) + "-" + dobCal.get(Calendar.YEAR);

                tvAddDob.setVisibility(View.GONE);
                layoutDob.setVisibility(View.VISIBLE);
                edtDob.setVisibility(View.VISIBLE);
                edtDob.setText(dobStr);
                dobTemp = dobCal.get(Calendar.YEAR) + "-" + (dobCal.get(Calendar.MONTH) + 1) + "-" + dobCal.get(Calendar.DAY_OF_MONTH);
            }

            // get list group cua contact
//            mFireStoreRef//.collection(EntityFirebase.TableContacts)
//                    .document(fileGet.getUserId())
//                    //.collection(EntityFirebase.TableUserContacts)
//                    .document(key)
//                    //.collection(EntityFirebase.TableGroupContacts)
//                    .get()
//                    .addOnSuccessListener(queryDocumentSnapshots -> {
//
//                        listIDGroupChoose.clear();
//                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
//
//                            for (GroupNew group : CustomersFragment.listGroup) {
//                                if (snapshot.getId().equalsIgnoreCase(group.GroupId)) {
//                                    rvPickedGroup.setVisibility(View.VISIBLE);
//                                    group.isSelected = true;
//                                    listIDGroupChoose.add(group);
//                                }
//                            }
//                        }
//                    });

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void initView() {
        customersController = new CustomersController(this);
        baseActivityTransition = new BaseActivityTransition(this);

        fileGet = new FileSave(this, Constants.GET);
        layoutPhone = findViewById(R.id.layout_phone);
        layoutDob = findViewById(R.id.layout_dob);
        layoutEmail = findViewById(R.id.layout_email);
        tvAvatarContact = findViewById(R.id.slider_img_product);
        edtFullName = findViewById(R.id.edt_username);
        edtPhone = findViewById(R.id.edt_phone);
        edtEmail = findViewById(R.id.edt_email);
        edtHashtag = findViewById(R.id.edt_hashtag);
        edtAddress = findViewById(R.id.edt_address);
        edtJobs = findViewById(R.id.edt_jobs);
        edtDob = findViewById(R.id.edt_dob);
        imgDob = findViewById(R.id.img_dob);
        imgEdit = findViewById(R.id.img_edit);
        edtNeed = findViewById(R.id.edt_need);
        tvPhone2 = findViewById(R.id.tvPhone2);
        tvEmail2 = findViewById(R.id.textView9);
        edtEmail2 = findViewById(R.id.edt_email2);
        edtPhone2 = findViewById(R.id.edt_phone2);
        btnBackNavigation = findViewById(R.id.btn_back_navigation);
        btnSaveNavigation = findViewById(R.id.btn_save_navigation);
        btnSave = findViewById(R.id.btn_save_customer);
        titleToolbar = findViewById(R.id.custom_TitleToolbar);
        titleToolbar.setText(MainActivity.mainContext.getResources().getString(R.string.add_customer));
        tvDeleteCustomer = findViewById(R.id.tv_deleteCustomer);
        tvDeleteCustomer.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);
        progressBar = findViewById(R.id.progress_bar);
        layoutContainer = findViewById(R.id.layout_container);
        btnBackNavigation.setIconPadding(0, 0, 0, 0);
        btnBackNavigation.setText(MainActivity.mainContext.getResources().getString(R.string.cancel));

        btnSaveNavigation.setText(MainActivity.mainContext.getResources().getString(R.string.title_toolbar_done_vn));
        btnSaveNavigation.setVisibility(View.GONE);

        listIDGroupRemove = new ArrayList<>();
        listIDGroupChoose = new ArrayList<>();
        rvPickedGroup = findViewById(R.id.rv_picked_group);
        rvPickedGroup.setLayoutManager(new LinearLayoutManager(this));
        mAdapterPickedGroup = new PickedGroupAdapter(this, listIDGroupChoose);
        rvPickedGroup.setAdapter(mAdapterPickedGroup);
//        sqlCustomers = new SQLCustomers(this);

        tvAddNeed = findViewById(R.id.tv_add_need);
        tvAddAddress = findViewById(R.id.tv_add_address);
        tvAddDob = findViewById(R.id.tv_add_dob);
        tvAddEmail = findViewById(R.id.tv_add_email);
        tvAddHashtag = findViewById(R.id.tv_add_hashtag);
        tvAddJob = findViewById(R.id.tv_add_job);
        tvAddPhone = findViewById(R.id.tv_add_phone);
        layoutHashtag = findViewById(R.id.layout_hashtag);
        dataHashtag = new ArrayList<>();
        rvHashtag = findViewById(R.id.rv_hash_tag);
        rvHashtag.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        hashtagAdapter = new HashtagAdapter(dataHashtag);
        rvHashtag.setAdapter(hashtagAdapter);
        frameGroup = findViewById(R.id.frame_group);
    }

    private void eventClick() {

        btnBackNavigation.setOnClickListener(view -> {
            resetData();
            finishAfterTransition();
        });

//        edtPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher()); // format hashtag number

        edtFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    tvAvatarContact.setText((s.charAt(0) + "").toUpperCase());
                } else {
//                    tvAvatarContact.setText("A");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tvPhone2.setOnClickListener(v -> {
            edtPhone2.setVisibility(View.VISIBLE);
            tvPhone2.setVisibility(View.GONE);
        });

        tvEmail2.setOnClickListener(v -> {
            edtEmail2.setVisibility(View.VISIBLE);
            tvEmail2.setVisibility(View.GONE);
        });

        btnSave.setOnClickListener(v -> {
            try {
                HashMap<String, Object> customerMap = createNewCus();
                if (customerMap == null) return;
                addCustomer(customerMap);
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                showProgress(false);
            }

        });

        btnSaveNavigation.setOnClickListener(v -> {
            try {
                HashMap<String, Object> customerMap1 = createNewCus();
                if (customerMap1 == null) return;
                updateCustomer(key, customerMap1);

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                showProgress(false);
            }


        });

        imgDob.setOnClickListener(v -> {
            try {
                edtDob.setVisibility(View.VISIBLE);
                @SuppressLint("SetTextI18n") DatePickerDialog.OnDateSetListener callback = (view, year, monthOfYear, dayOfMonth) -> {
                    edtDob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + (year));
                    dobTemp = year + "-" + (monthOfYear + 1) + "-" + (dayOfMonth);
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.set(year, monthOfYear, dayOfMonth);
//                    calendar.getTimeInMillis();
//                    Log.i("eventClick", "eventClick: "+calendar.getTimeInMillis());
                };

                if (customerIntent != null) {
                    if (!TextUtils.isEmpty(customerIntent.getBirthday())) {
                        Calendar dobCal = Utilities.convertDateStringToCalendarAllType(customerIntent.getBirthday());
                        DatePickerDialog pic = new DatePickerDialog(
                                AddOrEditCustomerActivity.this,
                                callback, dobCal.get(Calendar.YEAR), dobCal.get(Calendar.MONTH) + 1, dobCal.get(Calendar.DAY_OF_MONTH));
                        pic.setTitle(getString(R.string.title_choose_dob));
                        pic.show();
                    } else {
                        DatePickerDialog pic = new DatePickerDialog(
                                AddOrEditCustomerActivity.this,
                                callback, 2018, 1, 1);
                        pic.setTitle(getString(R.string.title_choose_dob));
                        pic.show();
                    }
                } else {
                    DatePickerDialog pic = new DatePickerDialog(
                            AddOrEditCustomerActivity.this,
                            callback, 2018, 1, 1);
                    pic.setTitle(getString(R.string.title_choose_dob));
                    pic.show();
                }

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                Crashlytics.logException(e);
            }

        });

        edtDob.setVisibility(View.GONE);
        edtDob.setText("");
        dobTemp = "";

        tvDeleteCustomer.setOnClickListener(v -> {

            try {
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddOrEditCustomerActivity.this);
                final String message = getString(R.string.do_you_want_to_delete_this_customer);

                builder.setMessage(message).setTitle(getString(R.string.delete_customer))
                        .setNegativeButton(getString(R.string.revoke),
                                (dialog, which) -> dialog.dismiss())
                        .setPositiveButton(getString(R.string.agree),
                                (dialog, which) -> {
                                    PostAPI api = RetrofitClient.createService(PostAPI.class, "");
                                    Call<ResDelete> remove = api.removeCustomer(key);
                                    remove.enqueue(new Callback<ResDelete>() {
                                        @Override
                                        public void onResponse(Call<ResDelete> call, Response<ResDelete> response) {
                                            if (response.isSuccessful()) {
                                                if (response.body() != null) {
                                                    if (response.body().getCode() == 0) {
//                                                        sqlCustomers.deleteCustomer(key);
                                                        resetData();
                                                        Intent intent = new Intent();
                                                        intent.putExtra(Constants.KEY_IS_DELETED_CUSTOMER, true);
                                                        setResult(Constants.DELETED_OR_EDIT_CONTACT_ACTIVITY_RESULT, intent);
                                                        finishAfterTransition();
//                                                    } else {
//                                                        Utilities.refreshToken(MainActivity.mainContext, response.body().getMessage().toLowerCase() + "");
//                                                        Toast.makeText(AddOrEditCustomerActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Toast.makeText(AddOrEditCustomerActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                }

//                                                String msg;
//                                                if (response.body() != null) {
//                                                    msg = response.body().getError();
//                                                    if (!TextUtils.isEmpty(msg)) {
//                                                        Toast.makeText(AddOrEditCustomerActivity.this, msg, Toast.LENGTH_LONG).show();
//                                                    }
//                                                }
                                            } else {
                                                if (response.errorBody() != null) {
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                                        String msg = (String) jsonObject.get(Constants.ERROR);
                                                        showProgress(false);
                                                        if (!TextUtils.isEmpty(msg)) {
                                                            Toast.makeText(AddOrEditCustomerActivity.this, msg, Toast.LENGTH_LONG).show();
                                                        }
                                                    } catch (JSONException | IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResDelete> call, Throwable t) {
                                            if (BuildConfig.DEBUG)
                                                Log.e(getClass().getName(), t.toString());
                                        }
                                    });
                                });
                builder.create().show();

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }
        });

        imgEdit.setOnClickListener(view -> {
            if (CustomersFragment.listGroup.size() != 0) {
                displayDialogChoose();
            }

        });

        tvAddNeed.setOnClickListener(v -> {
            edtNeed.setVisibility(View.VISIBLE);
            tvAddNeed.setVisibility(View.GONE);
        });

        tvAddPhone.setOnClickListener(v -> {
            layoutPhone.setVisibility(View.VISIBLE);
            tvAddPhone.setVisibility(View.GONE);
        });

        tvAddEmail.setOnClickListener(v -> {
            layoutEmail.setVisibility(View.VISIBLE);
            tvAddEmail.setVisibility(View.GONE);
        });

        tvAddHashtag.setOnClickListener(v -> {
//            edtHashtag.setVisibility(View.VISIBLE);
            layoutHashtag.setVisibility(View.VISIBLE);
            tvAddHashtag.setVisibility(View.GONE);
        });

        tvAddJob.setOnClickListener(v -> {
            edtJobs.setVisibility(View.VISIBLE);
            tvAddJob.setVisibility(View.GONE);
        });

        tvAddAddress.setOnClickListener(v -> {
            edtAddress.setVisibility(View.VISIBLE);
            tvAddAddress.setVisibility(View.GONE);
        });

        tvAddDob.setOnClickListener(v -> {
            layoutDob.setVisibility(View.VISIBLE);
            tvAddDob.setVisibility(View.GONE);
        });

        edtHashtag.setOnEditorActionListener((v, actionId, event) -> {

            try {
                if (event == null || actionId == 6) {
                    String hashtag = edtHashtag.getText().toString();
                    if (!TextUtils.isEmpty(hashtag)) {
                        dataHashtag.add(hashtag.trim());
                        hashtagAdapter.notifyDataSetChanged();
                        rvHashtag.setVisibility(View.VISIBLE);
                        edtHashtag.setText("");
                    }
                } else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    String hashtag = edtHashtag.getText().toString();
                    if (!TextUtils.isEmpty(hashtag)) {
                        dataHashtag.add(hashtag.trim());
                        hashtagAdapter.notifyDataSetChanged();
                        rvHashtag.setVisibility(View.VISIBLE);
                        edtHashtag.setText("");
                    }
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }

            return false;

        });

    }

    private HashMap<String, Object> createNewCus() {
        name = edtFullName.getText().toString().trim();
        email = edtEmail.getText().toString().trim() + "";
        email2 = edtEmail2.getText().toString().trim() + "";
        phone = edtPhone.getText().toString().trim();
        phone2 = edtPhone2.getText().toString().trim() + "";
        need = edtNeed.getText().toString().trim();
        address = edtAddress.getText().toString();
//                jobs = edtJobs.getText().toString().trim();
//                dob = edtDob.getText().toString().trim();
        dob = dobTemp;

        if (name.length() == 0) {
            edtFullName.requestFocus();
            edtFullName.setError(MainActivity.mainContext.getResources().getString(R.string.please_enter_full_name));
            return null;
        }

        if (TextUtils.isEmpty(phone.trim())) {
            if (layoutPhone.getVisibility() == View.GONE) {
                layoutPhone.setVisibility(View.VISIBLE);
                tvAddPhone.setVisibility(View.GONE);
            }

//                    if (TextUtils.isEmpty(phone2.trim())) {
//                        edtPhone.requestFocus();
//                        edtPhone.setError(getString(R.string.please_do_not_leave_it_blank));
//                        return;
//                    }
        } else {
//                    boolean kq = sqlCustomers.checkExistedPhone(phone.trim());
//                    if (kq) {
//                        edtPhone.requestFocus();
//                        edtPhone.setError(getString(R.string.text_error_existed_phone));
//                        return;
//                    }
        }

        if (!TextUtils.isEmpty(phone.trim())) {
            if (!phone.startsWith("0") || phone.length() > 11 || phone.length() < 10) {
                edtPhone.requestFocus();
                edtPhone.setError(getString(R.string.invalid_phone_number));
                return null;
            }
        }

        if (!TextUtils.isEmpty(phone2.trim())) {
            if (!phone2.startsWith("0") || phone2.length() > 11 || phone2.length() < 10) {
                edtPhone2.requestFocus();
                edtPhone2.setError(getString(R.string.invalid_phone_number));
                return null;
            } else {
//                        boolean kq = sqlCustomers.checkExistedPhone(phone2.trim());
//                        if (kq) {
//                            edtPhone2.requestFocus();
//                            edtPhone2.setError(getString(R.string.text_error_existed_phone));
//                            return;
//                        }
            }

            if (phone2.equals(phone)) {
                edtPhone2.requestFocus();
                edtPhone2.setError(getString(R.string.text_error_duplicate_phone));
                return null;
            }
        }

        if (!TextUtils.isEmpty(email)) {
            if (isValidEmail(email)) {
                edtEmail.requestFocus();
                edtEmail.setError(getString(R.string.text_error_invalid_email));
                return null;
            } else {
//                        boolean kq = sqlCustomers.checkExistedEmail(email.trim());
//                        if (kq) {
//                            edtEmail.requestFocus();
//                            edtEmail.setError(getString(R.string.text_error_existed_email));
//                            return;
//                        }
            }
            if (email.equals(email2)) {
                edtEmail2.requestFocus();
                edtEmail2.setError(getString(R.string.text_error_duplicate_email));
                return null;
            }
        }

        if (!TextUtils.isEmpty(email2)) {
            if (isValidEmail(email2)) {
                edtEmail2.requestFocus();
                edtEmail2.setError(getString(R.string.text_error_invalid_email));
                return null;
            } else {
//                        boolean kq = sqlCustomers.checkExistedEmail(email2.trim());
//                        if (kq) {
//                            edtEmail2.requestFocus();
//                            edtEmail2.setError(getString(R.string.text_error_existed_email));
//                            return;
//                        }
            }
        }

        showProgress(true);

        customerNew = new CustomerRetroV1();
        String newName = Utilities.upperFirstLetter(name);
        ArrayList<FieldCus> listEmail = new ArrayList<>();
        if (!TextUtils.isEmpty(email)) {
            listEmail.add(new FieldCus(email, "home", true));
        }
        if (!TextUtils.isEmpty(email2)) {
            listEmail.add(new FieldCus(email2, "home", true));
        }

        ArrayList<FieldCus> listPhone = new ArrayList<>();
        if (!TextUtils.isEmpty(phone)) {
            listPhone.add(new FieldCus(phone, "home", true));
        }
        if (!TextUtils.isEmpty(phone2)) {
            listPhone.add(new FieldCus(phone2, "home", true));
        }

        if (!TextUtils.isEmpty(edtHashtag.getText().toString().trim())) {
            dataHashtag.add(edtHashtag.getText().toString().trim());
        }

        HashMap<String, Object> customerMap = new HashMap<>();
        customerMap.put("name", newName.equals("") ? null : newName);
        customerMap.put("phones", listPhone);
        customerMap.put("emails", listEmail);
        if (!edtDob.getText().equals("")) {
            customerMap.put("birthday", dob);
        }
        customerMap.put("address", address.equals("") ? null : address);
        customerMap.put("note", need.equals("") ? null : need);
        customerMap.put("hashtag", dataHashtag);

//                List<String> lstGroup = new ArrayList<>();
//                if (listIDGroupChoose.size() != 0) {
//                    for (GroupCustomerRetroV1 groupRetro : listIDGroupChoose) {
//                        lstGroup.add(groupRetro.getId());
//                    }
//                    customerNew.setGroups(lstGroup);
//                }
        return customerMap;
    }

    private void updateCustomer(String id, HashMap<String, Object> map) {
        try {
            customersController.updateCustomer(id, map, this);
        } catch (Exception ignored) {
            ignored.getMessage();
        }
    }

    private void addCustomer(HashMap<String, Object> customerNew) {
        try {
            customersController.addCustomer(customerNew, this);
        } catch (Exception ignored) {
            ignored.getMessage();
        }
    }

    private void resetData() {
        for (GroupCustomerRetroV1 group : CustomersFragment.listGroup) {
            group.isSelected = false;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
        resetData();
        finishAfterTransition();
    }

    private void displayDialogChoose() {
        dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_choose_group);
        btnCancel = dialog.findViewById(R.id.btn_cancel);
        RecyclerView rvChooseGroup = dialog.findViewById(R.id.rv_group_choose);
        rvChooseGroup.setLayoutManager(new LinearLayoutManager(this));
        ChooseGroupCustomerFirebaseAdapter mAdapterChooseGroup = new ChooseGroupCustomerFirebaseAdapter(this, CustomersFragment.listGroup);
        rvChooseGroup.setAdapter(mAdapterChooseGroup);

        addEventDialog();

        dialog.show();

    }

    private void addEventDialog() {
        btnCancel.setOnClickListener(view -> dialog.dismiss());
    }

    private void showProgress(boolean show) {
        if (show) {
            layoutContainer.setAlpha((float) 0.5);
            layoutContainer.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            layoutContainer.setEnabled(true);
            layoutContainer.setAlpha(1);
        }
    }

    @Override
    public void onAddCustomerSuccess(CustomerRetroV1 customerNew) {
        Intent data = new Intent();
        setResult(Activity.RESULT_OK, data);
        finish();
//        try {
////            String custId = (String) response.body().getData();
////            customerNew.setCustId(custId);
////            sqlCustomers.insertAddCustomer(customerNew);
//
//            new AlertDialog.Builder(mContext)
//                    .setTitle(R.string.add_contact)
//                    .setMessage(getString(R.string.would_you_like_to_add_customer) + name + getString(R.string.into_your_phone_or_not))
//                    .setPositiveButton(getString(R.string.agree), (dialog, which) -> {
//                        Intent intent = new Intent(Intent.ACTION_INSERT);
//                        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
//
//                        intent.putExtra(ContactsContract.Intents.Insert.NAME, customerNew.getName());
//                        if (customerNew.getPhones() != null) {
//                            switch (customerNew.getPhones().size()) {
//                                case 0:
//                                    break;
//                                case 1:
//                                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, customerNew.getPhones().get(0));
//                                    break;
//                                case 2:
//                                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, customerNew.getPhones().get(0));
//                                    intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, customerNew.getPhones().get(1));
//                                    break;
//                            }
//                        }
//
//                        if (customerNew.getEmails() != null) {
//                            switch (customerNew.getEmails().size()) {
//                                case 0:
//                                    break;
//                                case 1:
//                                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, customerNew.getEmails().get(0));
//                                    break;
//                                case 2:
//                                    intent.putExtra(ContactsContract.Intents.Insert.EMAIL, customerNew.getEmails().get(0));
//                                    intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_EMAIL, customerNew.getEmails().get(1));
//                                    break;
//                            }
//                        }
//                        baseActivityTransition.transitionTo(intent, 0);
//                        resetData();
//                        showProgress(false);
//                        onBackPressed();
//                    })
//                    .setNegativeButton(getResources().getString(R.string.close), (dialog, which) -> {
//                        dialog.dismiss();
//                        resetData();
//                        showProgress(false);
//                        onBackPressed();
//                    })
//                    .setCancelable(false)
//                    .show();
//            showProgress(false);
//        } catch (Resources.NotFoundException ignored) {
//            ignored.getMessage();
//        }
    }

    @Override
    public void onErrorAddCustomer() {
        showProgress(false);
    }

    @Override
    public void onAddCustomerFailure() {
        showProgress(false);
    }

    @Override
    public void onConnectAddCustomerFailure() {
        showProgress(false);
    }

    @Override
    public void onUpdateCustomerSuccess(ResCustomers response, CustomerRetroV1 customerRetro) {
        try {
//            sqlCustomers.insertAddCustomer(customerRetro);
            resetData();
            showProgress(false);
            Intent intent = new Intent();
            intent.putExtra(Constants.TEMP_CUSTOMER_KEY, key);
            intent.putExtra(Constants.KEY_PASSINGDATA_CUSTOMER_OBJ, customerRetro);
            setResult(Constants.DELETED_OR_EDIT_CONTACT_ACTIVITY_RESULT, intent);
            finishAfterTransition();
            showProgress(false);
        } catch (Exception ignored) {
            ignored.getMessage();
        }
    }

    @Override
    public void onErrorUpdateCustomer() {
        showProgress(false);
    }

    @Override
    public void onUpdateCustomerFailure() {
        showProgress(false);
    }

    @Override
    public void onConnectUpdateCustomerFailure() {
        showProgress(false);
    }
}
