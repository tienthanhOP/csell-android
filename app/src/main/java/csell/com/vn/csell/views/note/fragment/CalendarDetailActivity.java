package csell.com.vn.csell.views.note.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
////import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.NoteController;
import csell.com.vn.csell.models.NoteV1;
import csell.com.vn.csell.views.csell.adapter.TimeLineTodayAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.models.Note;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by chuc.nq on 4/16/2018.
 */

public class CalendarDetailActivity extends AppCompatActivity implements NoteController.OnGetNoteTodayListener {

    private FileSave fileGet;

    //    private static final String TAG = "CalendarDetailActivity";
//    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("EEEE, dd MMM", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM", Locale.getDefault());
    //    private SimpleDateFormat dateFormatForMonthNumber = new SimpleDateFormat("MM", Locale.getDefault());
    private SimpleDateFormat dateFormatForYear = new SimpleDateFormat("yyyy", Locale.getDefault());
    //    private SimpleDateFormat dateFormatForDate = new SimpleDateFormat("dd", Locale.getDefault());
    private boolean shouldShow = false;
    private CompactCalendarView compactCalendarView;

    final List<String> mutableBookings = new ArrayList<>();

    private ListView bookingsListView;
    private ImageView slideCalendarBut;
    private ImageView showCalendarWithAnimationBut;

    private TextView tvMonth, tvYear, tvDate;
    private FancyButton btnBack;
    private ArrayAdapter adapter;

    private TimeLineTodayAdapter mAdapter;
    private ArrayList<NoteV1> dataNotes;
    private RecyclerView rvListEvents;

    private ProgressBar progressBar;
    private RelativeLayout layout;
    private RelativeLayout layoutControl;
    private NoteController noteController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_detail);
        noteController = new NoteController(this);
        initView();
        initEvent();
    }

    private void initEvent() {
        final View.OnClickListener showCalendarOnClickLis = getCalendarShowLis();
        layoutControl.setOnClickListener(showCalendarOnClickLis);

        final View.OnClickListener exposeCalendarListener = getCalendarExposeLis();
        layoutControl.setOnClickListener(exposeCalendarListener);

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                setCurrentDateTime(dateClicked);
                displayListEvent(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                setCurrentDateTime(firstDayOfNewMonth);
            }
        });

        btnBack.setOnClickListener(view -> onBackPressed());

    }

    private void displayListEvent(Date dateClicked) {
        List<Event> bookingsFromMap = compactCalendarView.getEvents(dateClicked);
        if (bookingsFromMap != null) {
            dataNotes.clear();
            for (Event booking : bookingsFromMap) {
//                can lam
//                NoteV1 noteProduct = new NoteV1();
////                long s = booking.getTimeInMillis();
//                String format = Utilities.convertTimeMillisToDateStringyyyyMMddHHmmss(booking.getTimeInMillis());
//                noteProduct.setDatereminder(format);
//                noteProduct.setTitle((String) booking.getData());
//                dataNotes.add(noteProduct);
//                String timeString = dataNotes.get(0).getDatereminder();
//                Calendar reminder = Utilities.convertDateStringToCalendarAllType(dataNotes.get(0).getDatereminder());
            }
            mAdapter.notifyDataSetChanged();
        }
    }


    private void initView() {
        layoutControl = findViewById(R.id.calendar_control_buttons_2);
        progressBar = findViewById(R.id.progress_bar);
        layout = findViewById(R.id.layout);
        btnBack = findViewById(R.id.btn_back_navigation);
        fileGet = new FileSave(this, Constants.GET);
        tvMonth = findViewById(R.id.tv_month);
        tvYear = findViewById(R.id.btn_save_navigation);
        tvDate = findViewById(R.id.tv_date);
        bookingsListView = findViewById(R.id.bookings_listview);
        slideCalendarBut = findViewById(R.id.slide_calendar);
        showCalendarWithAnimationBut = findViewById(R.id.show_with_animation_calendar);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mutableBookings);

        bookingsListView.setAdapter(adapter);
        compactCalendarView = findViewById(R.id.compactcalendar_view);

        compactCalendarView.setUseThreeLetterAbbreviation(false);
        compactCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        compactCalendarView.setLocale(TimeZone.getDefault(), Locale.ENGLISH);

        dataNotes = new ArrayList<>();
        rvListEvents = findViewById(R.id.rv_list_event);
        rvListEvents.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TimeLineTodayAdapter(this, dataNotes, false);

        rvListEvents.setAdapter(mAdapter);

        loadEventsForMonth();
        compactCalendarView.invalidate();
        setCurrentDateTime(compactCalendarView.getFirstDayOfCurrentMonth());

    }

    private void loadEventsForMonth() {
        try {
            showProgress(true);

            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            String dateStart = year + "-01-01";
            String dateEnd = year + "-12-30";

            noteController.getNoteToday(0, 1000, dateStart, dateEnd, this);
        } catch (Exception ignored) {

        }
    }

    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private void setCurrentDateTime(Date date) {
        tvMonth.setText(dateFormatForMonth.format(date));
        tvYear.setText(dateFormatForYear.format(date));
        tvDate.setText(dateFormatForDisplaying.format(date));
    }

    @NonNull
    private View.OnClickListener getCalendarShowLis() {
        return v -> {
            if (!compactCalendarView.isAnimating()) {
                if (shouldShow) {
                    compactCalendarView.showCalendar();
                    slideCalendarBut.setVisibility(View.GONE);
                    showCalendarWithAnimationBut.setVisibility(View.VISIBLE);
                } else {
                    compactCalendarView.hideCalendar();
                    slideCalendarBut.setVisibility(View.VISIBLE);
                    showCalendarWithAnimationBut.setVisibility(View.GONE);
                }
                shouldShow = !shouldShow;
            }
        };
    }

    @NonNull
    private View.OnClickListener getCalendarExposeLis() {
        return v -> {
            if (!compactCalendarView.isAnimating()) {
                if (shouldShow) {
                    compactCalendarView.showCalendarWithAnimation();
                    slideCalendarBut.setVisibility(View.GONE);
                    showCalendarWithAnimationBut.setVisibility(View.VISIBLE);
                } else {
                    compactCalendarView.hideCalendarWithAnimation();
                    slideCalendarBut.setVisibility(View.VISIBLE);
                    showCalendarWithAnimationBut.setVisibility(View.GONE);
                }
                shouldShow = !shouldShow;
            }
        };
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
    public void onGetNoteTodaySuccess(Response<JSONResponse<List<Note>>> response) {
        try {
            if (response.body().getSuccess() != null) {
                if (response.body().getSuccess()) {
                    for (Note note : response.body().getData()) {
                        Event event1 = new Event(Color.argb(255, 169, 68, 65),
                                Utilities.convertDateStringToCalendarAllType(note.getDatereminder()).getTimeInMillis(), note.getTitle());
                        compactCalendarView.addEvent(event1);
                    }
                    displayListEvent(new Date());
                    showProgress(false);
                } else {
                    Toast.makeText(CalendarDetailActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            String msg = response.body().getError();
            if (!TextUtils.isEmpty(msg)) {
                Toast.makeText(CalendarDetailActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGetNoteTodayFailure(Response<JSONResponse<List<Note>>> response) {
        try {
            if (response.errorBody() != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                    String msg = (String) jsonObject.get(Constants.ERROR);
                    showProgress(false);
                    if (!TextUtils.isEmpty(msg)) {
                        Toast.makeText(CalendarDetailActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onConnectGetNoteTodayFailure() {

    }
}
