package csell.com.vn.csell.views.csell.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.models.NoteV1;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.note.activity.NoteDetailActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.views.note.fragment.CalendarDetailActivity;
import csell.com.vn.csell.models.Note;

/**
 * Created by chuc.nq on 4/16/2018.
 */

public class TimeLineTodayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<NoteV1> data;
    private Context context;
    private boolean canClickItem;
    private BaseActivityTransition baseActivityTransition;

    public TimeLineTodayAdapter(Context context, ArrayList<NoteV1> data, boolean clickItem) {
        this.data = data;
        this.context = context;
        this.canClickItem = clickItem;
        baseActivityTransition = new BaseActivityTransition(context);
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_today_note, parent, false);
        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            DataViewHolder holderItem = (DataViewHolder) holder;
            if (position % 2 == 0) {
                holderItem.viewNote.setBackgroundColor(context.getResources().getColor(R.color.opacity_grey));
            } else {
                holderItem.viewNote.setBackgroundColor(context.getResources().getColor(R.color.white_100));
            }

            holderItem.tvContentNote.setText(data.get(position).getName());
            String timestring = data.get(position).getNoticeAt()+"";
            Calendar reminder;
            if (context instanceof CalendarDetailActivity) {
                reminder = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                reminder.setTime(sdf.parse(timestring));// all done
            } else {
                reminder = Utilities.convertDateStringToCalendarAllType(data.get(position).getNoticeAt()+"");
            }
            String time;
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

            holderItem.tvTime.setText(time);

            if (canClickItem) {
                holderItem.itemView.setOnClickListener(v -> {
                    Intent note = new Intent(context, NoteDetailActivity.class);
                    note.putExtra("UPDATE_RESULT_NOTE", true);
                    note.putExtra(Constants.KEY_PASSINGDATA_NOTE_OBJ, data.get(position));
                    baseActivityTransition.transitionTo(note, 0);
                });
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class DataViewHolder extends RecyclerView.ViewHolder {
        TextView tvContentNote, tvTime;
        RelativeLayout viewNote;

        DataViewHolder(View itemView) {
            super(itemView);
            viewNote = itemView.findViewById(R.id.viewNote);
            tvTime = itemView.findViewById(R.id.item_today_note_tv_time);
            tvContentNote = itemView.findViewById(R.id.item_today_note_tv_content);
        }
    }
}
