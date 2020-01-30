package csell.com.vn.csell.mycustoms;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import csell.com.vn.csell.R;
import csell.com.vn.csell.views.note.activity.AddNoteProductActivity;

@SuppressWarnings("unused")
@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {
    TextView tvDate;
    Context mContext;


    public DatePickerFragment(Context context, TextView tvDateUse) {
        DatePickerFragment.this.tvDate = tvDateUse;
        mContext = context;
    }

    public DatePickerFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        int year, month, day;
        Calendar c = Calendar.getInstance();
        if (!TextUtils.isEmpty(AddNoteProductActivity.dateNote)) {
            String[] dates = AddNoteProductActivity.dateNote.split("-");
            year = Integer.parseInt(dates[2]);
            month = Integer.parseInt(dates[1]) - 1;
            day = Integer.parseInt(dates[0]);
        } else {

            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dialog.getDatePicker().setMinDate(c.getTimeInMillis());

        return dialog;
    }

    @SuppressLint("SetTextI18n")
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (month < 9) {
            DatePickerFragment.this.tvDate.setText(day + "-0" + (month + 1) + "-" + year);
            AddNoteProductActivity.dateNote = day + "-0" + (month + 1) + "-" + year;
        } else {
            DatePickerFragment.this.tvDate.setText(day + "-" + (month + 1) + "-" + year);
            AddNoteProductActivity.dateNote = day + "-" + (month + 1) + "-" + year;
        }

        AddNoteProductActivity.layoutDate.setBackground(mContext.getDrawable(R.drawable.background_blue_border));
        AddNoteProductActivity.layoutTime.setBackground(mContext.getDrawable(R.drawable.background_blue_border));
        AddNoteProductActivity.layoutRemind.setBackground(mContext.getDrawable(R.drawable.background_blue_border));
        AddNoteProductActivity.edtAlarm.setTextColor(mContext.getResources().getColor(R.color.dark_blue_100));

        if (AddNoteProductActivity.tvTime.getText().toString().equalsIgnoreCase(mContext.getResources().getString(R.string.hour))) {
            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
            String time = mdformat.format(calendar.getTime());
            AddNoteProductActivity.tvTime.setTextColor(mContext.getResources().getColor(R.color.dark_blue_100));
            AddNoteProductActivity.tvDate.setTextColor(mContext.getResources().getColor(R.color.dark_blue_100));
        }
    }
}