package csell.com.vn.csell.views.customer.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.apis.JSONNote;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.CustomerRetroV1;
import csell.com.vn.csell.models.Note;
import csell.com.vn.csell.models.NoteV1;
import csell.com.vn.csell.models.ResCustomers;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.adapter.ViewPagerAdapter;
import csell.com.vn.csell.views.customer.fragment.CustomerDetailsInformationFragment;
import csell.com.vn.csell.views.customer.fragment.TimeLineCustomerFragment;
import csell.com.vn.csell.views.note.activity.AddNoteProductActivity;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactCustomerDetailActivity extends AppCompatActivity {

    public static CustomerRetroV1 customer;
    public static ArrayList<NoteV1> dataNotes;
    public static boolean isEdited = false;
    public FloatingActionButton fab;
    private Context mContext;
    private FancyButton btnBackNavigation;
    private FancyButton btnEditNavigation;
    private TextView titleToolbar;
    private LinearLayout imgCall, imgSMS, imgMail;
    private TextView tvFullName;
    private TabLayout tabLayout;
    private AppBarLayout mAppBarLayout;
    private LinearLayout layoutInfo, layoutImageView;
    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;
    private FileSave fileSave;
    private String email, email2, phone, phone2, hashtag;
    private String key, contactName;
    //    private SQLCustomers sqlCustomers;
    private int timeout = 0;
    private int tabSelected = 0;
    private CoordinatorLayout layoutDetailContract;
    private TextView tvNotification;
    private ImageView imgAvatar;
    private TimeLineCustomerFragment timeLineCustomerFragment;
    private CustomerDetailsInformationFragment customerDetailsInformationFragment;
    private BaseActivityTransition baseActivityTransition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_customer_detail);
        Fabric.with(this, new Crashlytics());
        mContext = this;

        initView();
        setupWindowAnimations();
        eventView();
        loadData();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    private void initView() {
        fileSave = new FileSave(this, Constants.GET);
        baseActivityTransition = new BaseActivityTransition(this);

        layoutDetailContract = findViewById(R.id.layout_detail_contract);

        tvNotification = findViewById(R.id.tv_notification);

        mAppBarLayout = findViewById(R.id.layout_bar);
        layoutInfo = findViewById(R.id.layout_info);
        layoutImageView = findViewById(R.id.layout_image_view);

        imgAvatar = findViewById(R.id.img_avatar_customer);

        btnBackNavigation = findViewById(R.id.btn_back_navigation);
        btnBackNavigation.setText(getString(R.string.title_back_vn));
        btnEditNavigation = findViewById(R.id.btn_save_navigation);
        titleToolbar = findViewById(R.id.custom_TitleToolbar);
//        titleToolbar.setText(getString(R.string.add_note));
        tabLayout = findViewById(R.id.tab_layout_customer_detail);
        viewPager = findViewById(R.id.view_pager_customer_detail);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.animate();
        customer = new CustomerRetroV1();
        //header
        fab = findViewById(R.id.img_date_note);
        imgCall = findViewById(R.id.img_call);
        imgSMS = findViewById(R.id.img_sms);
        imgMail = findViewById(R.id.img_mail);
        tvFullName = findViewById(R.id.tv_name_customer_detail);
        btnEditNavigation.setIconResource(getString(R.string.font_icon_toolbar_right_edit));
//        titleToolbar.setText(getString(R.string.detail));
//        sqlCustomers = new SQLCustomers(this);

        dataNotes = new ArrayList<>();
    }

    private void setupViewPager(ViewPager viewPager) {
        timeLineCustomerFragment = new TimeLineCustomerFragment();
        customerDetailsInformationFragment = new CustomerDetailsInformationFragment();
        pagerAdapter.addFrag(timeLineCustomerFragment, "Ghi chú");
        pagerAdapter.addFrag(customerDetailsInformationFragment, "Thông tin");
        viewPager.setAdapter(pagerAdapter);

    }

    private void eventView() {

        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                //Collapsed
                tvFullName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                layoutInfo.setLayoutParams(new LinearLayout.LayoutParams(Utilities.dpToPx(mContext, 30),
                        Utilities.dpToPx(mContext, 30)));

            } else {
                //Expanded
                tvFullName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                layoutInfo.setLayoutParams(new LinearLayout.LayoutParams(Utilities.dpToPx(mContext, 64),
                        Utilities.dpToPx(mContext, 64)));

            }
        });

        imgCall.setOnClickListener(v -> {
            try {
                if (phone != null) {
                    if (!TextUtils.isEmpty(phone2)) {

                        Dialog dialog = new Dialog(this);
                        dialog.setContentView(R.layout.dialog_customer_choose);

                        Button btnImport = dialog.findViewById(R.id.btn_import_contact);
                        Button btnCreate = dialog.findViewById(R.id.btn_create_contact);
                        Button btnCancel = dialog.findViewById(R.id.btn_cancel_dialog_choose);

                        btnImport.setText(phone);
                        btnCreate.setText(phone2);

                        btnImport.setOnClickListener(v1 -> {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone.trim(), null));
                            baseActivityTransition.transitionTo(intent, 0);
                        });

                        btnCreate.setOnClickListener(v1 -> {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone2.trim(), null));
                            baseActivityTransition.transitionTo(intent, 0);
                        });

                        btnCancel.setOnClickListener(v1 -> {
                            dialog.dismiss();
                        });

                        //custom position dialog
                        Window dialogWindow = dialog.getWindow();
                        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                        dialogWindow.setGravity(Gravity.BOTTOM);
                        dialogWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        lp.windowAnimations = R.style.DialogCreateAnimation;
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // Width
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // Height


                        dialogWindow.setAttributes(lp);

                        dialog.show();

                    } else {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone.trim(), null));
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(ContactCustomerDetailActivity.this, getString(R.string.customer_not_phone_number), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }

        });
        imgSMS.setOnClickListener(v -> {
            try {
                if (phone.length() != 0) {

                    if (!TextUtils.isEmpty(phone2)) {

                        Dialog dialog = new Dialog(this);
                        dialog.setContentView(R.layout.dialog_customer_choose);

                        Button btnImport = dialog.findViewById(R.id.btn_import_contact);
                        Button btnCreate = dialog.findViewById(R.id.btn_create_contact);
                        Button btnCancel = dialog.findViewById(R.id.btn_cancel_dialog_choose);

                        btnImport.setText(phone);
                        btnCreate.setText(phone2);

                        btnImport.setOnClickListener(v1 -> {
                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", phone.trim(), null));
                            baseActivityTransition.transitionTo(intent, 0);
                        });

                        btnCreate.setOnClickListener(v1 -> {
                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", phone2.trim(), null));
                            baseActivityTransition.transitionTo(intent, 0);
                        });

                        btnCancel.setOnClickListener(v1 -> {
                            dialog.dismiss();
                        });

                        //custom position dialog
                        Window dialogWindow = dialog.getWindow();
                        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                        dialogWindow.setGravity(Gravity.BOTTOM);
                        dialogWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        lp.windowAnimations = R.style.DialogCreateAnimation;
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // Width
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // Height


                        dialogWindow.setAttributes(lp);

                        dialog.show();

                    } else {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", phone.trim(), null));
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(ContactCustomerDetailActivity.this, getString(R.string.customer_not_phone_number), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            }
        });
        imgMail.setOnClickListener(v -> {
            List<String> listEmailTemp = new ArrayList<>();
            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(email2)) {
                Snackbar.make(v, getString(R.string.customer_does_not_have_email), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            } else {
                if (!TextUtils.isEmpty(email)) {
                    listEmailTemp.add(email);
                }
                if (!TextUtils.isEmpty(email2)) {
                    listEmailTemp.add(email2);
                }
            }

            if (listEmailTemp.size() != 0) {

                String[] listEmail = new String[listEmailTemp.size()];
                listEmail = listEmailTemp.toArray(listEmail);

                final Intent emailLauncher = new Intent(Intent.ACTION_SEND_MULTIPLE);
                emailLauncher.setType(getString(R.string.type_intent_mail));
                emailLauncher.putExtra(Intent.EXTRA_EMAIL, listEmail);
                emailLauncher.putExtra(Intent.EXTRA_SUBJECT, "Kính gửi quý khách");
                emailLauncher.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(emailLauncher);
            } else {
                Snackbar.make(v, getString(R.string.customer_does_not_have_email), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        btnBackNavigation.setOnClickListener(view -> {
            if (isEdited) {
                setResult(Activity.RESULT_OK);
            }
            isEdited = false;
            onBackPressed();
        });

        fab.setOnClickListener(v -> requestPermission());

        btnEditNavigation.setOnClickListener(v -> {
            Intent edit = new Intent(ContactCustomerDetailActivity.this, AddOrEditCustomerActivity.class);
            edit.putExtra(Constants.EDIT_CUSTOMER_TAG, key);
            edit.putExtra(Constants.KEY_PASSINGDATA_CUSTOMER_OBJ, customer);
            baseActivityTransition.transitionTo(edit, Constants.DELETED_OR_EDIT_CONTACT_ACTIVITY_RESULT);
        });
    }

    private void loadData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try {
                tabSelected = getIntent().getIntExtra(Constants.KEY_POSITION_TAB_SELECT_CUSTOMER_DETAIL, 0);
                viewPager.setCurrentItem(tabSelected);
                key = getIntent().getStringExtra(Constants.TEMP_CUSTOMER_KEY);
                customer = (CustomerRetroV1) getIntent().getSerializableExtra(Constants.KEY_PASSINGDATA_CUSTOMER_OBJ);
                if (key != null) {
                    customer.setId(key);
                    contactName = customer.getName();
                    tvFullName.setText(customer.getName());
                    getDetail();
                } else {
                    layoutDetailContract.setVisibility(View.GONE);
                    tvNotification.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }
        }
    }

    private void getDetail() {
        GetAPI api = RetrofitClient.createService(GetAPI.class, "");
        Call<ResCustomers> getDetail = api.getDetailCustomer(key);
        getDetail.enqueue(new Callback<ResCustomers>() {
            @Override
            public void onResponse(Call<ResCustomers> call, Response<ResCustomers> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        CustomerRetroV1 customerRetro = response.body().getCustomer();
                        customerRetro.setId(key);
//                        sqlCustomers.insertAddCustomer(customerRetro);
                        customer = customerRetro;
                        displayData(customerRetro);

                        customer.setId(key);
                        contactName = customer.getName();
                        tvFullName.setText(customer.getName());


                        Call<JSONNote<List<NoteV1>>> getNoteByCusId = api.getListNote("", key);
                        getNoteByCusId.enqueue(new Callback<JSONNote<List<NoteV1>>>() {
                            @Override
                            public void onResponse(Call<JSONNote<List<NoteV1>>> call, Response<JSONNote<List<NoteV1>>> response) {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        if (response.body().getNote().size() != 0) {
                                            TimeLineCustomerFragment.layoutAddNote.setVisibility(View.GONE);
                                            fab.show();
                                            TimeLineCustomerFragment.rvHistoryNote.setVisibility(View.VISIBLE);
                                        }
                                        dataNotes.clear();
                                        dataNotes.addAll(response.body().getNote());
                                        Utilities.sortListNote(dataNotes);
                                        timeLineCustomerFragment.updateViewTimeLine();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JSONNote<List<NoteV1>>> call, Throwable t) {

                            }
                        });//
                    }
//                    String msg = response.body().getErrors().get(0).getMessages().get(0);
//                    if (!TextUtils.isEmpty(msg)) {
//                        Toast.makeText(ContactCustomerDetailActivity.this, msg, Toast.LENGTH_LONG).show();
//                    }
                } else {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = (String) jsonObject.get(Constants.ERROR);
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(ContactCustomerDetailActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                getDetail.cancel();
            }

            @Override
            public void onFailure(Call<ResCustomers> call, Throwable t) {
                getDetail.cancel();
                if (timeout < 2) {
                    timeout++;
                    getDetail();
                } else {
                    Toast.makeText(ContactCustomerDetailActivity.this, getString(R.string.text_error_unknown), Toast.LENGTH_LONG).show();
                    timeout = 0;
                }
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
                Crashlytics.logException(t);
            }
        });
    }

    private void displayData(CustomerRetroV1 customer) {
        if (customer.getEmails() != null && customer.getEmails().size() != 0) {
            email = "";
            email2 = "";
            if (customer.getEmails().size() != 0) {
                if (customer.getEmails().size() > 1) {
                    email = customer.getEmails().get(0).getAddress();
                    email2 = customer.getEmails().get(1).getAddress();
                } else {
                    email = customer.getEmails().get(0).getAddress();
                }
            }
        }

        if (customer.getPhones() != null && customer.getPhones().size() != 0) {
            phone = "";
            phone2 = "";
            if (customer.getPhones().size() != 0) {
                if (customer.getPhones().size() > 1) {
                    phone = customer.getPhones().get(0).getAddress();
                    phone2 = customer.getPhones().get(1).getAddress();
                } else {
                    phone = customer.getPhones().get(0).getAddress();
                }
            }
        }

        if (customer.getHashtag() != null && customer.getHashtag().size() != 0) {
            hashtag = "";
            for (int i = 0; i < customer.getHashtag().size(); i++) {
                if (i == 0) {
                    hashtag += customer.getHashtag().get(0);
                } else {
                    hashtag += "," + customer.getHashtag().get(i);
                }
            }
        }
        String dob = "";
        CustomerDetailsInformationFragment.checkExistsData(customerDetailsInformationFragment.layoutHashtag, customerDetailsInformationFragment.tvHashtag, hashtag);
        CustomerDetailsInformationFragment.checkExistsData(customerDetailsInformationFragment.layoutPhone, customerDetailsInformationFragment.tvPhone, phone);
        CustomerDetailsInformationFragment.checkExistsData(customerDetailsInformationFragment.layoutPhone2, customerDetailsInformationFragment.tvPhone2, phone2);
        CustomerDetailsInformationFragment.checkExistsData(customerDetailsInformationFragment.layoutEmail, customerDetailsInformationFragment.tvEmail, email);
        CustomerDetailsInformationFragment.checkExistsData(customerDetailsInformationFragment.layoutEmail2, customerDetailsInformationFragment.tvEmail2, email2);
        CustomerDetailsInformationFragment.checkExistsData(customerDetailsInformationFragment.layoutAddress, customerDetailsInformationFragment.tvAddress, customer.getAddress());
        if (!TextUtils.isEmpty(customer.getBirthday()) && !customer.getBirthday().equals("Invalid date")) {
            Calendar dobCal = Utilities.convertDateStringToCalendarAllType(customer.getBirthday());
            dob = dobCal.get(Calendar.DAY_OF_MONTH) + "-" + (dobCal.get(Calendar.MONTH) + 1) + "-" + dobCal.get(Calendar.YEAR);
        }
        CustomerDetailsInformationFragment.checkExistsData(customerDetailsInformationFragment.layoutDob, customerDetailsInformationFragment.tvDob, dob);
//        CustomerDetailsInformationFragment.checkExistsData(customerDetailsInformationFragment.layoutJobs, customerDetailsInformationFragment.tvJobs, customer.getJobs());
        if (!TextUtils.isEmpty(customer.getNote())) {
            customerDetailsInformationFragment.layoutAddNeed.setVisibility(View.GONE);
        } else {
            customerDetailsInformationFragment.layoutAddNeed.setVisibility(View.VISIBLE);
        }
        CustomerDetailsInformationFragment.checkExistsData(customerDetailsInformationFragment.layoutNeed, customerDetailsInformationFragment.tvNeed, customer.getNote());
    }

    public void createNote() {

        int checkReadCal = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        int checkWriteCal = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        int permissionGranted = PackageManager.PERMISSION_GRANTED;

        String[] permissions = new String[]{
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR};
        //check Android 6+
        if (Build.VERSION.SDK_INT >= 23) {
            //check permission granted
            if (checkReadCal != permissionGranted
                    || checkWriteCal != permissionGranted) {
                //request Permissions
                ActivityCompat.requestPermissions(this, permissions, 1);
            } else {
                Intent note = new Intent(this, AddNoteProductActivity.class);
                note.putExtra("NOTE_IN_CUSTOMER", true);
                note.putExtra(Constants.KEY_CONTACT_ID, customer.getId());
                note.putExtra(Constants.KEY_CONTACT_NAME, customer.getName());
                baseActivityTransition.transitionTo(note, Constants.ADD_NOTE_ACTIVITY_RESULT);
            }
        } else {
            Intent note = new Intent(this, AddNoteProductActivity.class);
            note.putExtra("NOTE_IN_CUSTOMER", true);
            note.putExtra(Constants.KEY_CONTACT_ID, customer.getId());
            note.putExtra(Constants.KEY_CONTACT_NAME, customer.getName());
            baseActivityTransition.transitionTo(note, Constants.ADD_NOTE_ACTIVITY_RESULT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Constants.DELETED_OR_EDIT_CONTACT_ACTIVITY_RESULT) {
                boolean isDelete = data.getBooleanExtra(Constants.KEY_IS_DELETED_CUSTOMER, false);
                if (isDelete) {
                    setResult(Activity.RESULT_OK);
                    finishAfterTransition();
                } else {
                    key = data.getStringExtra(Constants.TEMP_CUSTOMER_KEY);
                    customer = (CustomerRetroV1) data.getSerializableExtra(Constants.KEY_PASSINGDATA_CUSTOMER_OBJ);
                    tvFullName.setText(customer.getName());

                    displayData(customer);
                }
            } else if (resultCode == Constants.ADD_NOTE_ACTIVITY_RESULT) {
                NoteV1 note = (NoteV1) data.getSerializableExtra(Constants.KEY_PASSINGDATA_NOTE_OBJ);
                dataNotes.add(note);
                Utilities.sortListNote(dataNotes);
                timeLineCustomerFragment.updateViewTimeLine();
            } else if (resultCode == Constants.KEY_UPDATE_NOTE_RESULT) {
                boolean isDeleted = data.getBooleanExtra(Constants.KEY_IS_DELETED, false);
                Note note = (Note) data.getSerializableExtra(Constants.KEY_PASSINGDATA_NOTE_OBJ);
                if (!isDeleted) {
                    getDetail();
//                    for (Note note1 : dataNotes) {
//                        if (note1.getNoteid().equals(note.getNoteid())) {
//                            note1.setCustname(!TextUtils.isEmpty(note.getCustname()) ? note.getCustname() : "");
//                            note1.setCustid(!TextUtils.isEmpty(note.getCustid()) ? note.getCustid() : "");
//                            note1.setContent(!TextUtils.isEmpty(note.getContent()) ? note.getContent() : "");
//                            note1.setTitle(!TextUtils.isEmpty(note.getTitle()) ? note.getTitle() : "");
//                            note1.setDatereminder(!TextUtils.isEmpty(note.getDatereminder()) ? note.getDatereminder() : "");
//                            break;
//                        }
//                    }
                } else {
                    for (int i = 0; i < dataNotes.size(); i++) {
                        if (dataNotes.get(i).getId().equals(note.getNoteid())) {
                            dataNotes.remove(i);
                            break;
                        }
                    }
                }


                Utilities.sortListNote(dataNotes);
                timeLineCustomerFragment.updateViewTimeLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void requestPermission() {
        int checkReadCal = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        int checkWriteCal = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        int permissionGranted = PackageManager.PERMISSION_GRANTED;

        String[] permissions = new String[]{
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR};
        //check Android 6+
        if (Build.VERSION.SDK_INT >= 23) {
            //check permission granted
            if (checkReadCal != permissionGranted
                    || checkWriteCal != permissionGranted) {
                //request Permissions
                ActivityCompat.requestPermissions(this, permissions, 1);
            } else {
                createNote();
            }
        } else {
            createNote();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
    }
}
