package csell.com.vn.csell.views.note.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.CustomerRetro;
import csell.com.vn.csell.models.NoteV1;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.customer.activity.ContactCustomerDetailActivity;
import csell.com.vn.csell.views.note.activity.NoteDetailActivity;
import csell.com.vn.csell.views.social.activity.DetailProductActivity;

/**
 * Created by chuc.nq on 3/2/2018.
 */

public class TimeLineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<NoteV1> data;
    private Context context;
    private boolean isSeeTimeLineProduct;
    private int typeTimeLine;
    private BaseActivityTransition baseActivityTransition;
    private long mLastClickTime = 0;

    //typeTimeLine = 1: noteEdit, 2: noteProduct, 3: noteCustomer
    public TimeLineAdapter(Context context, ArrayList<NoteV1> data, boolean chooseProduct, int typeTimeLine) {
        this.data = data;
        this.context = context;
        this.isSeeTimeLineProduct = chooseProduct;
        this.typeTimeLine = typeTimeLine;
        baseActivityTransition = new BaseActivityTransition(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_note_customer, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            ViewHolder holderItem = (ViewHolder) holder;
            holderItem.tvContentNote.setText(data.get(position).getContent());
            holderItem.tvTitle.setText(data.get(position).getName());

            if (isSeeTimeLineProduct) {
                if (TextUtils.isEmpty(data.get(position).getCustomer().getFieldName())) {
                    holderItem.tvCustomer.setVisibility(View.GONE);
                    holderItem.layoutInfo.setVisibility(View.GONE);
                } else {
                    holderItem.layoutInfo.setVisibility(View.VISIBLE);
                    holderItem.tvCustomer.setVisibility(View.VISIBLE);
                    holderItem.tvCustomer.setText(data.get(position).getCustomer().getFieldName());

                    holderItem.layoutInfo.setOnClickListener(v -> {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        try {
                            CustomerRetro customerRetro = new CustomerRetro();
                            customerRetro.setCustId(data.get(position).getCustomer().getFieldId());
                            customerRetro.setName(data.get(position).getCustomer().getFieldName());

                            Intent detail = new Intent(context, ContactCustomerDetailActivity.class);
                            detail.putExtra(Constants.TEMP_CUSTOMER_KEY, data.get(position).getCustomer().getFieldId());
                            detail.putExtra(Constants.KEY_PASSINGDATA_CUSTOMER_OBJ, customerRetro);
                            baseActivityTransition.transitionTo(detail, 0);
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                        }
                    });
                }
            } else {
                if (TextUtils.isEmpty(data.get(position).getName())) {
                    holderItem.tvCustomer.setVisibility(View.GONE);
                    holderItem.layoutInfo.setVisibility(View.GONE);
                } else {
                    holderItem.layoutInfo.setVisibility(View.VISIBLE);
                    holderItem.tvCustomer.setVisibility(View.VISIBLE);
                    holderItem.tvCustomer.setText(data.get(position).getName());

                    if (!TextUtils.isEmpty(data.get(position).getId())) {
                        holderItem.layoutInfo.setOnClickListener(v -> {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();

                            Intent detail = new Intent(context, DetailProductActivity.class);
                            detail.putExtra(Constants.TEMP_PRODUCT_KEY, data.get(position).getId());
                            detail.putExtra(Constants.IS_MY_PRODUCT, true);
                            baseActivityTransition.transitionTo(detail, 0);
                        });
                    }
                }
            }

            Calendar reminder;
            if (TextUtils.isEmpty(data.get(position).getNoticeAt() + "")) {
                reminder = Utilities.convertDateStringToCalendarAllType(data.get(position).getNoticeAt() + "");
            } else {
                reminder = Utilities.convertDateStringToCalendarAllType(data.get(position).getNoticeAt() + "");
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
            String day = String.valueOf(reminder.get(Calendar.DAY_OF_MONTH));
            String month = (reminder.get(Calendar.MONTH) + 1) + "";

            holderItem.tvDay.setText(day);
            if (Integer.parseInt(month) < 10) {
                String newMonth = month.replace("0", "");
                holderItem.tvMonth.setText(context.getString(R.string.text_month) + " " + newMonth);
            } else {
                holderItem.tvMonth.setText(context.getString(R.string.text_month) + " " + month);
            }
            holderItem.tvMonth.setVisibility(View.VISIBLE);
            holderItem.tvDay.setVisibility(View.VISIBLE);

            if (position > 0) {
                Date date1 = Utilities.convertDateStringToCalendarAllType(data.get(position - 1).getNoticeAt() + "").getTime();
                Date date2 = Utilities.convertDateStringToCalendarAllType(data.get(position).getNoticeAt() + "").getTime();
                if (Utilities.compareDateSame(date1, date2)) {
                    holderItem.tvMonth.setVisibility(View.GONE);
                    holderItem.tvDay.setVisibility(View.GONE);
                }
            }

            holder.itemView.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                Intent note = new Intent(context, NoteDetailActivity.class);
                note.putExtra("UPDATE_RESULT_NOTE", true);
                note.putExtra("TYPE_TIME_LINE", typeTimeLine);
                note.putExtra(Constants.KEY_PASSINGDATA_NOTE_OBJ, data.get(position));
                baseActivityTransition.transitionTo(note, Constants.KEY_UPDATE_NOTE_RESULT);
            });

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvCustomer, tvContentNote, tvMonth, tvTime, tvTitle;
        View tvLine;
        RelativeLayout viewNote;
        LinearLayout layoutInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.item_note_customer_tv_day);
            viewNote = itemView.findViewById(R.id.viewNote);
            tvMonth = itemView.findViewById(R.id.item_note_customer_tv_month);
            tvTime = itemView.findViewById(R.id.item_note_customer_tv_time);
            tvCustomer = itemView.findViewById(R.id.item_note_customer_tv_product_name);
            tvContentNote = itemView.findViewById(R.id.item_note_customer_tv_content_note);
            tvTitle = itemView.findViewById(R.id.tv_title_note);
            tvLine = itemView.findViewById(R.id.line_space);
            layoutInfo = itemView.findViewById(R.id.layout_info);
//            line_month = itemView.findViewById(R.id.line_month);
        }
    }
}
