package csell.com.vn.csell.views.note.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.NoteController;
import csell.com.vn.csell.models.CustomerRetro;
import csell.com.vn.csell.models.NoteV1;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.customer.activity.ContactCustomerDetailActivity;
import csell.com.vn.csell.views.social.activity.DetailProductActivity;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.google.firebase.firestore.FirebaseFirestore;

public class NoteDetailActivity extends AppCompatActivity implements NoteController.OnUpdateNoteListener {

    public static String resultNote = "";
    boolean isDeleted = false;
    private FancyButton btnBack;
    private FancyButton btnEdit;
    private TextView titleToolbar;
    private TextView tvTitleNote, tvContentNote;
    private LinearLayout layoutInfo, layoutProduct;
    private TextView tvHour, tvDate, tvInfo, tvProduct;
    private NoteV1 note = null;
    private int typeTimeLine = 1;
    private EditText edtResult;
    private TextView tvDone;
    private LinearLayout layout;
    private ProgressBar progressBar;
    private PostAPI postAPI;
    private GetAPI getAPI;
    private String noteId;
    private int timeoutDetail = 0, timeoutUpdateStatus = 0;
    private LinearLayout layoutContent;
    private BaseActivityTransition baseActivityTransition;
    private NoteController noteController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        Fabric.with(this, new Crashlytics());

        initView();
        setupWindowAnimations();
        addEvent();
        loadData();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    private void initView() {
        noteController = new NoteController(this);
        baseActivityTransition = new BaseActivityTransition(this);

        layoutContent = findViewById(R.id.layout_content);
        btnBack = findViewById(R.id.btn_back_navigation);
        btnBack.setText(getString(R.string.title_back_vn));
        titleToolbar = findViewById(R.id.custom_TitleToolbar);
        btnEdit = findViewById(R.id.btn_save_navigation);
        btnEdit.setVisibility(View.VISIBLE);
        btnEdit.setIconResource(getString(R.string.font_icon_toolbar_right_edit));
        titleToolbar.setVisibility(View.VISIBLE);
        titleToolbar.setText("");
        tvTitleNote = findViewById(R.id.tv_title_note);
        tvContentNote = findViewById(R.id.tv_content_note);
        layoutInfo = findViewById(R.id.layout_info);
        layoutProduct = findViewById(R.id.layout_product);
        tvHour = findViewById(R.id.tv_hour);
        tvDate = findViewById(R.id.tv_date);
        tvInfo = findViewById(R.id.tv_info);
        tvProduct = findViewById(R.id.tv_product_name);
        edtResult = findViewById(R.id.edt_result_note);
        tvDone = findViewById(R.id.tv_done);
        FileSave fileGet = new FileSave(this, Constants.GET);
        layout = findViewById(R.id.layout);
        progressBar = findViewById(R.id.progress_bar);
        postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
    }

    private void addEvent() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddNoteProductActivity.class);
            switch (typeTimeLine) {
                case 1:
                    intent.putExtra("NOTE_IN_EDIT", true);
                    break;
                case 2:
                    intent.putExtra("NOTE_IN_PRODUCT", true);
                    break;
                case 3:
                    intent.putExtra("NOTE_IN_CUSTOMER", true);
                    break;
                default:
                    intent.putExtra("NOTE_IN_EDIT", true);
                    break;
            }
            if (!TextUtils.isEmpty(resultNote)) {
                note.setNote(resultNote);
            }
            intent.putExtra(Constants.KEY_PASSINGDATA_NOTE_OBJ, note);
            baseActivityTransition.transitionTo(intent, Constants.KEY_UPDATE_NOTE_RESULT);
        });

        edtResult.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    resultNote = s.toString();
                    tvDone.setVisibility(View.VISIBLE);
                } else {
                    tvDone.setVisibility(View.GONE);
                }
            }
        });

        tvDone.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(resultNote)) {
                showProgress(true);

                note.setNote(resultNote);
                note.setUpdatedAt(new Date().getTime());
                HashMap hashMap = new HashMap();
                hashMap.put("note", resultNote);
                updateStatusNote(hashMap, note.getId());
            } else {
                Toast.makeText(this, getString(R.string.text_error_empty_result_note), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateStatusNote(HashMap<String, Object> note, String noteId) {
        try {
            noteController.updateNote(note, noteId, this);
        } catch (Exception ignored) {
            showProgress(false);
        }
    }

    private void loadData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            note = (NoteV1) bundle.getSerializable(Constants.KEY_PASSINGDATA_NOTE_OBJ);
            typeTimeLine = bundle.getInt("TYPE_TIME_LINE");
            noteId = note.getId();
            getDetail();
        }
    }

    private void getDetail() {
        Call<NoteV1> detailNote = getAPI.getDetailNote(note.getId());
        detailNote.enqueue(new Callback<NoteV1>() {
            @Override
            public void onResponse(Call<NoteV1> call, Response<NoteV1> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            note = response.body();
                            note.setId(noteId);
                            setValue(note, false);

                            if (!TextUtils.isEmpty(note.getCustomer().getFieldId())) {
                                layoutInfo.setOnClickListener(v -> {

                                    CustomerRetro customerRetro = new CustomerRetro();
                                    customerRetro.setCustId(note.getCustomer().getFieldId());
                                    customerRetro.setName(note.getCustomer().getFieldName());

                                    Intent detail = new Intent(NoteDetailActivity.this, ContactCustomerDetailActivity.class);
                                    detail.putExtra(Constants.TEMP_CUSTOMER_KEY, note.getCustomer().getFieldId());
                                    detail.putExtra(Constants.KEY_PASSINGDATA_CUSTOMER_OBJ, customerRetro);
                                    baseActivityTransition.transitionTo(detail, 0);
                                });
                            }

                            if (!TextUtils.isEmpty(note.getProduct().getFieldId())) {
                                layoutProduct.setOnClickListener(v -> {
                                    Intent detail = new Intent(NoteDetailActivity.this, DetailProductActivity.class);
                                    detail.putExtra(Constants.IS_MY_PRODUCT, true);
                                    detail.putExtra(Constants.TEMP_PRODUCT_KEY, note.getProduct().getFieldId());
                                    baseActivityTransition.transitionTo(detail, 0);
                                });
                            }
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = (String) jsonObject.get(Constants.ERROR);
                                showProgress(false);
                                if (!TextUtils.isEmpty(msg)) {
                                    Toast.makeText(NoteDetailActivity.this, msg, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    detailNote.cancel();
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                    Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                }
            }

            @Override
            public void onFailure(Call<NoteV1> call, Throwable t) {
                detailNote.cancel();
                if (timeoutDetail < 2) {
                    timeoutDetail++;
                    getDetail();
                } else {
                    timeoutDetail = 0;
                    Toast.makeText(NoteDetailActivity.this, getString(R.string.text_error_unknown), Toast.LENGTH_LONG).show();
                }
                Crashlytics.logException(t);
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
            }
        });
    }

    private void setValue(NoteV1 note, boolean isUpdate) {
        note.setId(noteId);

        if (!TextUtils.isEmpty(note.getNote())) {
            edtResult.setText(note.getNote());
            tvDone.setVisibility(View.VISIBLE);
        } else {
            edtResult.setText("");
            tvDone.setVisibility(View.GONE);
        }

        tvTitleNote.setText(!TextUtils.isEmpty(note.getName()) ? note.getName() : "");
        if (!TextUtils.isEmpty(note.getContent())) {
            layoutContent.setVisibility(View.VISIBLE);
            tvContentNote.setText(note.getContent());
        } else {
            layoutContent.setVisibility(View.GONE);
        }
        Calendar calendar;
        if (isUpdate) {
            calendar = Utilities.localTime(Utilities.convertUnixTime(note.getNoticeAt()));
        } else {
            calendar = Utilities.convertDateStringToCalendarAllType(Utilities.convertUnixTime(note.getNoticeAt()));
        }

        String timeNote;
        if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
            if (calendar.get(Calendar.MINUTE) < 10)
                timeNote = "0" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + "0" + calendar.get(Calendar.MINUTE);
            else
                timeNote = "0" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        } else {
            if (calendar.get(Calendar.MINUTE) < 10)
                timeNote = calendar.get(Calendar.HOUR_OF_DAY) + ":" + "0" + calendar.get(Calendar.MINUTE);
            else
                timeNote = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        }

        String dateNote = calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR);

        tvDate.setText(dateNote);
        tvHour.setText(timeNote);

        if (!TextUtils.isEmpty(note.getProduct().getFieldName())) {
            tvProduct.setText(note.getProduct().getFieldName());
            layoutProduct.setVisibility(View.VISIBLE);
        } else {
            layoutProduct.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(note.getCustomer().getFieldName())) {
            tvInfo.setText(note.getCustomer().getFieldName());
            layoutInfo.setVisibility(View.VISIBLE);
        } else {
            layoutInfo.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.KEY_UPDATE_NOTE_RESULT) {

            try {
                isDeleted = data.getBooleanExtra(Constants.KEY_IS_DELETED, false);
                if (!isDeleted) {
                    NoteV1 note1 = (NoteV1) data.getSerializableExtra(Constants.KEY_PASSINGDATA_NOTE_OBJ);
                    note = note1;
                    note.setId(noteId);
                    setValue(note1, true);
                } else {
                    onBackPressed();
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.toString());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }
        }
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
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_PASSINGDATA_NOTE_OBJ, note);
        intent.putExtra(Constants.KEY_IS_DELETED, isDeleted);
        setResult(Constants.KEY_UPDATE_NOTE_RESULT, intent);
        super.onBackPressed();
    }

    @Override
    public void onUpdateNoteSuccess() {
        showProgress(false);
        Toast.makeText(NoteDetailActivity.this, getString(R.string.text_success_save_result_note), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorUpdateNote() {
        showProgress(false);
    }

    @Override
    public void onUpdateNoteFailure() {
        showProgress(false);
    }

    @Override
    public void onConnectUpdateNoteFailure() {
        showProgress(false);
    }
}
