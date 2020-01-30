package csell.com.vn.csell.mycustoms;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import csell.com.vn.csell.R;
import csell.com.vn.csell.views.note.activity.AddNoteProductActivity;

/**
 * Created by Chuc Ngo on 2/5/2018.
 */

@SuppressWarnings("unused")
@SuppressLint("ValidFragment")
public class TimePickerFragment extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener {

    TextView tvTimeUse;
    Context mContext;

    public TimePickerFragment(Context context, TextView tvTimeUse) {
        TimePickerFragment.this.tvTimeUse = tvTimeUse;
        mContext = context;
    }

    public TimePickerFragment() {
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
        final Calendar c = Calendar.getInstance();

        int hour, minute;

        if (!TextUtils.isEmpty(AddNoteProductActivity.timeNote)) {
            String[] times = AddNoteProductActivity.timeNote.split(":");
            hour = Integer.parseInt(times[0]);
            minute = Integer.parseInt(times[1]);
        } else {
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }
        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        if (hour < 10) {
            if (minute < 10) {
                TimePickerFragment.this.tvTimeUse.setText("0" + hour + ":0" + minute);
                AddNoteProductActivity.timeNote = "0" + hour + ":0" + minute;
            } else {
                TimePickerFragment.this.tvTimeUse.setText("0" + hour + ":" + minute);
                AddNoteProductActivity.timeNote = "0" + hour + ":" + minute;
            }
        } else {
            if (minute < 10) {
                TimePickerFragment.this.tvTimeUse.setText(hour + ":0" + minute);
                AddNoteProductActivity.timeNote = hour + ":0" + minute;
            } else {
                TimePickerFragment.this.tvTimeUse.setText(hour + ":" + minute);
                AddNoteProductActivity.timeNote = hour + ":" + minute;
            }
        }


        AddNoteProductActivity.layoutDate.setBackground(mContext.getDrawable(R.drawable.background_blue_border));
        AddNoteProductActivity.layoutTime.setBackground(mContext.getDrawable(R.drawable.background_blue_border));
        AddNoteProductActivity.layoutRemind.setBackground(mContext.getDrawable(R.drawable.background_blue_border));
        AddNoteProductActivity.edtAlarm.setTextColor(mContext.getResources().getColor(R.color.dark_blue_100));

        if (AddNoteProductActivity.tvDate.getText().toString().equalsIgnoreCase(mContext.getResources().getString(R.string.day))) {
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String getDate = dateFormat.format(new Date());
            AddNoteProductActivity.tvDate.setText(getDate);
            AddNoteProductActivity.tvDate.setTextColor(mContext.getResources().getColor(R.color.dark_blue_100));
            AddNoteProductActivity.tvTime.setTextColor(mContext.getResources().getColor(R.color.dark_blue_100));
        }
    }
}
