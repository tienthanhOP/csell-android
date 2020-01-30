package csell.com.vn.csell.views.note.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.NoteController;
import csell.com.vn.csell.models.CustomerRetro;
import csell.com.vn.csell.models.FieldV1;
import csell.com.vn.csell.models.NoteV1;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.mycustoms.DatePickerFragment;
import csell.com.vn.csell.mycustoms.DialogPickerProduct;
import csell.com.vn.csell.mycustoms.TimePickerFragment;
import csell.com.vn.csell.sqlites.SQLCustomers;
import csell.com.vn.csell.views.customer.adapter.CustomerAutoCompletedTextAdapter;
import csell.com.vn.csell.views.note.fragment.CalendarDetailActivity;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNoteProductActivity extends AppCompatActivity implements NoteController.OnUpdateNoteListener {

    @SuppressLint("StaticFieldLeak")
    public static TextView tvTime;
    @SuppressLint("StaticFieldLeak")
    public static TextView tvDate;
    @SuppressLint("StaticFieldLeak")
    public static EditText edtAlarm;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout layoutTime, layoutDate, layoutRemind;
    public static String dateNote = "", timeNote = "";
    public static String productId = "";
    public static String productName = "";
    static ArrayList<CustomerRetro> listCutomers;
    static ArrayList<Product> countryNames;
    public LinearLayout layoutCustomer;
    public String contactId = "";
    public String contactName = "";
    private FancyButton btnBackNavigation;
    private FancyButton btnSaveNavigation;
    private TextView titleToolbar;
    private EditText titleNote;
    private EditText description;
    private TextView tv_selectProduct;
    private ImageView imgProduct;
    private boolean isEditNote = false;
    private NoteV1 noteProduct = null;
    private Button btnDeleteNote;
    private AutoCompleteTextView autoEdtCustomer;
    private SQLCustomers sqlCustomers;
    private View line;
    private ImageView imgShowCustomer;
    private TextView tvCount, tvCountTitle;
    private FileSave fileGet;

    private Context context;
    private NoteController noteController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note_product);
        context = this;
        Fabric.with(this, new Crashlytics());
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        noteController = new NoteController(this);
        initView();
        setupWindowAnimations();
        eventView();
        loadDataToEdit();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    private void initView() {
        fileGet = new FileSave(this, Constants.GET);
        line = findViewById(R.id.line);

        layoutTime = findViewById(R.id.layout_time);
        layoutDate = findViewById(R.id.layout_date);
        layoutRemind = findViewById(R.id.layout_remind);

        layoutCustomer = findViewById(R.id.layout_selectCustomer);
        tv_selectProduct = findViewById(R.id.tv_selectProduct);
        imgProduct = findViewById(R.id.imgView2);
        titleNote = findViewById(R.id.edt_note_title);
        description = findViewById(R.id.edt_description_note);
        tvTime = findViewById(R.id.tv_set_time_note);
        tvDate = findViewById(R.id.tv_set_date_note);
        edtAlarm = findViewById(R.id.edt_set_alarm);

        listCutomers = new ArrayList<>();
        countryNames = new ArrayList<>();

        btnBackNavigation = findViewById(R.id.btn_back_navigation);
        btnBackNavigation.setText(getString(R.string.title_back_vn));
        btnSaveNavigation = findViewById(R.id.btn_save_navigation);
        btnSaveNavigation.setText(getString(R.string.save));
        titleToolbar = findViewById(R.id.custom_TitleToolbar);
        titleToolbar.setText(getString(R.string.add_note));
        btnDeleteNote = findViewById(R.id.btn_delete_note);

        autoEdtCustomer = findViewById(R.id.tv_selectCustomer);
        sqlCustomers = new SQLCustomers(this);
        imgShowCustomer = findViewById(R.id.imgView3);
        tvCount = findViewById(R.id.tv_count);
        tvCountTitle = findViewById(R.id.tv_count_title);

        productId = "";

        getCustomerAndProduct();
    }

    private void getCustomerAndProduct() {

        listCutomers = sqlCustomers.getAllCustomer();
        CustomerAutoCompletedTextAdapter customerAdapter = new CustomerAutoCompletedTextAdapter(this, R.layout.item_customer_autocomplete, listCutomers);
        autoEdtCustomer.setAdapter(customerAdapter);
        GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
        Call<JSONResponse<List<Product>>> getProduct = getAPI.getAllProduct(1, 0, 2000);
        getProduct.enqueue(new Callback<JSONResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<JSONResponse<List<Product>>> call, Response<JSONResponse<List<Product>>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() != null) {
                        if (response.body().getSuccess()) {

                            List<Product> lst = response.body().getData();
                            for (int i = 0; i < lst.size(); i++) {

                                if (TextUtils.isEmpty(lst.get(i).getTitle())) {
                                    lst.remove(i);
                                }
                            }
                            countryNames.addAll(lst);

                        } else {
                            Toast.makeText(AddNoteProductActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    String msg = response.body().getError();
                    if (!TextUtils.isEmpty(msg)) {
                        Toast.makeText(AddNoteProductActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = (String) jsonObject.get(Constants.ERROR);
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(AddNoteProductActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONResponse<List<Product>>> call, Throwable t) {

            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void eventView() {
        btnBackNavigation.setOnClickListener(v -> onBackPressed());

        btnSaveNavigation.setOnClickListener(v -> {

            if (TextUtils.isEmpty(titleNote.getText().toString())) {
                titleNote.requestFocus();
                titleNote.setError(getString(R.string.text_error_note_tite));
                return;
            }

//            if (TextUtils.isEmpty(contactId) && TextUtils.isEmpty(productId)) {
//                Toast.makeText(this, getString(R.string.text_error_choose_customer_or_product), Toast.LENGTH_LONG).show();
//                return;
//            }

            addReminderInCalendar();
        });

        layoutTime.setOnClickListener(v -> {
            TimePickerFragment timePickerFragment = new TimePickerFragment(this, tvTime);
            timePickerFragment.show(AddNoteProductActivity.this.getFragmentManager(), "timePicker");
        });

        layoutDate.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(this, tvDate);
            newFragment.show(getFragmentManager(), "datePicker");
        });

        layoutRemind.setOnClickListener(v -> edtAlarm.requestFocus());

        tv_selectProduct.setOnClickListener(v -> {
            DialogPickerProduct dialogPickerProduct = new DialogPickerProduct(this, tv_selectProduct, countryNames);
            dialogPickerProduct.show();
        });

        btnDeleteNote.setOnClickListener(v -> {
            if (noteProduct != null) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dalete_note)
                        .setMessage(R.string.you_want_delete_this_note)
                        .setPositiveButton("Đồng ý", (dialog, which) -> deleteNote(noteProduct.getId()))
                        .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss()).setCancelable(false)
                        .show();
            }
        });

        autoEdtCustomer.setOnItemClickListener((parent, view, position, id) -> {
            contactName = listCutomers.get(position).getName();
            contactId = listCutomers.get(position).getCustId();
        });

        imgShowCustomer.setOnClickListener(v -> autoEdtCustomer.showDropDown());

        autoEdtCustomer.setOnTouchListener((v, event) -> {
            autoEdtCustomer.showDropDown();
            return false;
        });

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    int count = description.getText().length();
                    tvCount.setText(count + "/200");
                } else {
                    tvCount.setText("0/200");
                }
            }
        });

        titleNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    int count = titleNote.getText().length();
                    tvCountTitle.setText(count + "/100");
                } else {
                    tvCountTitle.setText("0/100");
                }
            }
        });

    }

    private void addReminderInCalendar() {

        try {
            String title = titleNote.getText().toString().trim();
            String des = description.getText().toString().trim();
            //format dd-MM-yyyy HH:mm
            String dateReminder;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date date = new Date();
            String dateTemp = f.format(date);
            String[] strDateTemp = dateTemp.split(" ");

            if (tvDate.getText().toString().trim().equalsIgnoreCase(getString(R.string.day))) {
                if (tvTime.getText().toString().trim().equalsIgnoreCase(getString(R.string.hour))) {
                    dateReminder = dateTemp;
                } else {
                    dateReminder = strDateTemp[0] + " " + tvTime.getText().toString();
                }
            } else {
                if (tvTime.getText().toString().trim().equalsIgnoreCase(getString(R.string.hour))) {
                    dateReminder = tvDate.getText().toString() + " " + strDateTemp[1];
                } else {
                    dateReminder = tvDate.getText().toString() + " " + tvTime.getText().toString();
                }
            }

            if (TextUtils.isEmpty(autoEdtCustomer.getText().toString().trim()) && TextUtils.isEmpty(contactId)) {
                contactName = "";
                contactId = "";
            }

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            try {
                Date mDate = sdf.parse(dateReminder);
                String promptBefore = edtAlarm.getText().toString().trim();
                Date datePromptBefore = new Date(mDate.getTime() - 60 * 60000);
                if (!promptBefore.equals("")) {
                    datePromptBefore = new Date(mDate.getTime() - Integer.parseInt(promptBefore) * 60000);
                }

                long timeMili = mDate.getTime();
                long timePromptBefore = datePromptBefore.getTime();
                if (isEditNote) {
                    saveNote(timeMili, title, des, timePromptBefore);
                } else {
                    createNote(timeMili, title, des, timePromptBefore, true);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                Uri EVENTS_URI = Uri.parse(getCalendarUriBase(true) + "events");
                ContentResolver cr = getContentResolver();
                TimeZone timeZone = TimeZone.getDefault();
                Calendar beginTime = Calendar.getInstance();

                String[] dateTime = dateReminder.split(" ");
                String[] dateMonthYear = dateTime[0].split("-");
                int day = Integer.parseInt(dateMonthYear[0]);
                int indexMonth = Integer.parseInt(dateMonthYear[1]) - 1;
                int year = Integer.parseInt(dateMonthYear[2]);
                String[] time = dateTime[1].split(":");
                int hour = Integer.parseInt(time[0]);
                int minute = Integer.parseInt(time[1]);

                beginTime.set(year, indexMonth, day, hour, minute);

                long begin = beginTime.getTimeInMillis();
                // Inserting an event in calendar.
                ContentValues values = null;
                Uri REMINDERS_URI = null;
                try {
                    values = new ContentValues();
                    values.put(CalendarContract.Events.CALENDAR_ID, 1);
                    values.put(CalendarContract.Events.TITLE, title);
                    values.put(CalendarContract.Events.DESCRIPTION, des);
                    values.put(CalendarContract.Events.ALL_DAY, 0);
                    values.put(CalendarContract.Events.DTSTART, begin);
                    values.put(CalendarContract.Events.DTEND, begin + 60 * 60 * 1000);
                    values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
                    values.put(CalendarContract.Events.HAS_ALARM, 1);
                    Uri event = cr.insert(EVENTS_URI, values);

                    REMINDERS_URI = Uri.parse(getCalendarUriBase(true) + "reminders");
                    values = new ContentValues();
                    values.put(CalendarContract.Reminders.EVENT_ID, Long.parseLong(event != null ? event.getLastPathSegment() : null));
                    values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

                    if (!TextUtils.isEmpty(edtAlarm.getText().toString()))
                        values.put(CalendarContract.Reminders.MINUTES, Integer.parseInt(edtAlarm.getText().toString()));
                    else
                        values.put(CalendarContract.Reminders.MINUTES, 60);
                } catch (NumberFormatException e) {
                    Crashlytics.logException(e);
                }

                cr.insert(REMINDERS_URI, values);
//                    createNote(null, title, des, contactName, contactId + "");

            } catch (Exception e) {
                Crashlytics.logException(e);
//                    Toast.makeText(this, "Quyền bị từ chối", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void saveNote(Long time, String title, String des, Long promptBefore) {

        HashMap<String, Object> note = new HashMap<>();

//        note.put("id", noteProduct.getId());
        noteProduct.setName(title);
        note.put("name", title);
        noteProduct.setContent(des);
        note.put("content", des);

        if (!TextUtils.isEmpty(productId)) {
            HashMap<String, String> product = new HashMap<>();
            product.put("id", productId);
            product.put("name", productName);
            note.put("product", product);

            noteProduct.setProduct(new FieldV1(productId, productName));
        }

        if (!TextUtils.isEmpty(contactId)) {
            HashMap<String, String> customer = new HashMap<>();
            customer.put("id", contactId);
            customer.put("name", contactName);
            note.put("customer", customer);

            noteProduct.setCustomer(new FieldV1(contactId, contactName));
        }

        note.put("appointmentAt", time);
        noteProduct.setAppointmentAt(time);

        note.put("noticeAt", time);
        noteProduct.setNoticeAt(promptBefore);

        note.put("note", noteProduct.getNote());

        updateNote(note);
    }

    private void updateNote(HashMap<String, Object> note) {
        try {
            noteController.updateNote(note, noteProduct.getId(), this);
        } catch (Exception ignored) {
            ignored.getMessage();
        }
    }

    private void createNote(Long time, String title, String content, Long promptBefore, boolean isNoticed) {

        HashMap<String, Object> note = new HashMap<>();


        note.put("content", content);
        note.put("name", title);

        note.put("appointmentAt", time);
        note.put("noticeAt", promptBefore);

        addNote(note);
    }

    private void addNote(HashMap<String, Object> note) {
        PostAPI api = RetrofitClient.createService(PostAPI.class, "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjEwMDAiLCJ1c2VybmFtZSI6InRoYW5ocHQiLCJwaG9uZSI6Iis4NDc4MjQ3NDQyNyIsImVtYWlsIjoidGhhbmgucHRAY3NlbGwuY29tLnZuIiwibmFtZSI6IlRow6BuaCBQaOG6oW0iLCJhdmF0YXIiOm51bGwsImdyb3VwIjoidXNlciIsImV4cCI6MTU3MzAwNDI2NCwiaWF0IjoxNTcyOTE3ODY0LCJhdWQiOiJ3ZWIiLCJpc3MiOiJhdXRoLmNzZWxsLnZuL3VzZXIvd2ViIn0.ABhRLdMSm83MwH8W9LyZRN_3N4hPuLBlWEHXrcPOWAz1Q5B1AKO7K-uoACldyFDjsi_eK_OTgrZXrkiM4-Gy_D9XT0e6tx3XHSMv1ONTF7540uDSXUC7-AdiXKms-SuSIUWGL8aAaVVKeijAN64GkPX-vrHM8PJS3y_z8u3Nba6Eq5rYAbjF6A7Xl6V4l7WiN1JZ5o4uxO2HnPb-Fwf4FoKTBr5HcPtXLBs_dZm0dAhOWpPYCObA1hSeQiOLk4Hz7gT8lh_XhKsib-C2U_AaBW1b5vttTgdmNrQu6z7caW9ojn-obB_eGNKC-9kzaTPUecTTrTCrmiF7mTGL1shxlw");
        Call<HashMap<String, Object>> addNote = api.addNote(note);
        addNote.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if ((int) (response.body().get("code")) == 0) {
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(response.body().get("note"));
                            NoteV1 note1 = gson.fromJson(jsonElement, NoteV1.class);
                            Intent intent = new Intent();
                            intent.putExtra(Constants.KEY_PASSINGDATA_NOTE_OBJ, note1);
                            intent.putExtra(Constants.KEY_IS_DELETED, false);
                            setResult(Constants.ADD_NOTE_ACTIVITY_RESULT, intent);

                            onBackPressed();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.i("onResponse", "onFailure: " + t.getMessage());
            }
        });
    }

    private void deleteNote(String noteId) {
        PostAPI api = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        Call<HashMap<String, Object>> removeNote = api.removeNote(noteId);
        removeNote.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Intent intent = new Intent();
                            intent.putExtra(Constants.KEY_IS_DELETED, true);
                            setResult(Constants.KEY_UPDATE_NOTE_RESULT, intent);
                            onBackPressed();
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = (String) jsonObject.get(Constants.ERROR);
                                if (!TextUtils.isEmpty(msg)) {
                                    Toast.makeText(AddNoteProductActivity.this, msg, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    removeNote.cancel();
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                    Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                removeNote.cancel();
                Crashlytics.logException(t);
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
            }
        });
    }

    /**
     * Returns Calendar Base URI, supports both new and old OS.
     */
    private String getCalendarUriBase(boolean eventUri) {
        Uri calendarURI = null;
        try {
            calendarURI = (eventUri) ? Uri.parse("content://com.android.calendar/") : Uri
                    .parse("content://com.android.calendar/calendars");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendarURI.toString();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        dateNote = null;
        timeNote = null;
        super.onBackPressed();
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    private void loadDataToEdit() {
        try {
            Bundle bundle = getIntent().getExtras();

            if (bundle != null) {
                boolean noteInProduct = bundle.getBoolean("NOTE_IN_PRODUCT", false);
                boolean noteInCustomer = bundle.getBoolean("NOTE_IN_CUSTOMER", false);
                boolean noteInEdit = bundle.getBoolean("NOTE_IN_EDIT", false);

                if (noteInProduct) {
                    imgProduct.setVisibility(View.GONE);
                    tv_selectProduct.setVisibility(View.GONE);
                    layoutCustomer.setVisibility(View.VISIBLE);
                    imgShowCustomer.setVisibility(View.VISIBLE);
                    autoEdtCustomer.setVisibility(View.VISIBLE);
                    productId = bundle.getString(Constants.KEY_PRODUCT_ID, "");
                    productName = bundle.getString(Constants.KEY_PRODUCT_NAME, "");
                } else if (noteInCustomer) {
                    layoutCustomer.setVisibility(View.GONE);
                    imgShowCustomer.setVisibility(View.GONE);
                    autoEdtCustomer.setVisibility(View.GONE);
                    imgProduct.setVisibility(View.VISIBLE);
                    tv_selectProduct.setVisibility(View.VISIBLE);
                    contactId = bundle.getString(Constants.KEY_CONTACT_ID, "");
                    contactName = bundle.getString(Constants.KEY_CONTACT_NAME, "");
                } else if (noteInEdit) {
                    titleToolbar.setText(getResources().getString(R.string.edit_note));
                    layoutCustomer.setVisibility(View.VISIBLE);
                    imgShowCustomer.setVisibility(View.VISIBLE);
                    autoEdtCustomer.setVisibility(View.VISIBLE);
                    imgProduct.setVisibility(View.VISIBLE);
                    tv_selectProduct.setVisibility(View.VISIBLE);
                }

                noteProduct = (NoteV1) bundle.getSerializable(Constants.KEY_PASSINGDATA_NOTE_OBJ);
                if (noteProduct != null) {
                    isEditNote = true;

                    Long appointmentAt = noteProduct.getAppointmentAt();
                    Long noticeAt = noteProduct.getNoticeAt();

                    if (appointmentAt != null && noticeAt != null) {
                        edtAlarm.setText((appointmentAt - noticeAt) / 60 + "");
                    } else {
                        edtAlarm.setText("60");
                    }

                    if (noteProduct.getCustomer().getFieldId() != null) {
                        layoutCustomer.setAlpha(1);
//                    tvSelectCustomer.setText(noteProduct.ContactName);
                        autoEdtCustomer.setText(noteProduct.getCustomer().getFieldName());
                        contactId = noteProduct.getCustomer().getFieldId();
                        contactName = noteProduct.getCustomer().getFieldName();
                    } else {
                        imgShowCustomer.setVisibility(View.GONE);
                    }

                    if (noteProduct.getProduct().getFieldId() != null) {
                        tv_selectProduct.setText(noteProduct.getProduct().getFieldName());
                        productId = noteProduct.getProduct().getFieldId();
                        productName = noteProduct.getProduct().getFieldName();
                    } else {
                        imgProduct.setVisibility(View.GONE);
                        line.setVisibility(View.GONE);
                    }

                    if (noteProduct.getNoticeAt() != null) {
                        String timestring = Utilities.convertUnixTime(noteProduct.getNoticeAt());
                        Calendar reminder;
                        String time = null;
                        try {
                            if (context instanceof CalendarDetailActivity) {
                                reminder = Calendar.getInstance();
                                SimpleDateFormat sdf;
                                if (timestring.contains("T"))
                                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                else
                                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                reminder.setTime(sdf.parse(timestring));
                            } else {
                                if (timestring.contains("T"))
                                    reminder = Utilities.convertDateStringToCalendarAllType(Utilities.convertUnixTime(noteProduct.getNoticeAt()));
                                else
                                    reminder = Utilities.convertDateStringToCalendarAllType2(Utilities.convertUnixTime(noteProduct.getNoticeAt()));

                            }
                            time = null;
                            if (reminder.get(Calendar.HOUR_OF_DAY) < 10) {
                                if (reminder.get(Calendar.MINUTE) < 10)
                                    time = "0" + reminder.get(Calendar.HOUR_OF_DAY) + ":" + "0" + reminder.get(Calendar.MINUTE);
                                else
                                    time = "0" + reminder.get(Calendar.HOUR_OF_DAY) + ":" + reminder.get(Calendar.MINUTE);
                            } else {
                                if (reminder.get(Calendar.MINUTE) < 10)
                                    time = reminder.get(Calendar.HOUR_OF_DAY) + ":" + "0" + reminder.get(Calendar.MINUTE);
                                else
                                    time = reminder.get(Calendar.HOUR_OF_DAY) + ":" + reminder.get(Calendar.MINUTE);
                            }

                            dateNote = reminder.get(Calendar.DAY_OF_MONTH) + "-" + (reminder.get(Calendar.MONTH) + 1) + "-" + reminder.get(Calendar.YEAR);

                        } catch (Exception ignored) {
                            ignored.getMessage();
                        }

                        tvDate.setText(dateNote);
                        tvTime.setText(time);
                    }

                    btnDeleteNote.setVisibility(View.VISIBLE);
                    titleNote.setText(noteProduct.getName());
                    description.setText(noteProduct.getContent());
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void onUpdateNoteSuccess() {
        try {
            Intent intent = new Intent();
            intent.putExtra(Constants.KEY_PASSINGDATA_NOTE_OBJ, noteProduct);
            intent.putExtra(Constants.KEY_IS_DELETED, false);
            setResult(Constants.KEY_UPDATE_NOTE_RESULT, intent);
            onBackPressed();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onErrorUpdateNote() {

    }

    @Override
    public void onUpdateNoteFailure() {

    }

    @Override
    public void onConnectUpdateNoteFailure() {

    }
}
