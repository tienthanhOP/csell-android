package csell.com.vn.csell.views.friend.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;

import java.util.Calendar;
import java.util.Date;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.friend.activity.ChatActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.TimeAgo;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.LastMessage;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.views.friend.adapter.viewholder.LastMessageViewHolder;

/**
 * Created by cuong.nv on 3/4/2018.
 */

public class HistoryMessageAdapter extends FirebaseRecyclerAdapter<LastMessage, LastMessageViewHolder> {

    private Context mContext;
    private FileSave fileSave;
    private ObservableSnapshotArray data;
    private BaseActivityTransition baseActivityTransition;
    private long mLastClickTime = 0;

    public HistoryMessageAdapter(Context context, @NonNull FirebaseRecyclerOptions<LastMessage> options) {
        super(options);
        this.mContext = context;
        fileSave = new FileSave(mContext, Constants.GET);
        data = options.getSnapshots();
        baseActivityTransition = new BaseActivityTransition(context);
    }

    @NonNull
    @Override
    public LastMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_message, parent, false);
        return new LastMessageViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull LastMessageViewHolder holder, int position, @NonNull LastMessage model) {

        try {
            if (model != null) {
                //room chat
                String roomId = getSnapshots().getSnapshot(position).getKey();
                String userId = fileSave.getUserId();

                if (model.is_pending) {
                    holder.hideLayout();
                } else {
                    holder.showLayout();
                }

                if (!model.sender.equalsIgnoreCase(fileSave.getUserId())) {
                    if (model.count_unread > 0) {
                        holder.txtCountUnread.setVisibility(View.VISIBLE);
                        holder.txtCountUnread.setText(model.count_unread + "");
                        holder.txtLastMessage.setTextColor(mContext.getResources().getColor(R.color.text_last_message_friend_unread));
                    } else {
                        holder.txtCountUnread.setVisibility(View.GONE);
                        holder.txtLastMessage.setTextColor(mContext.getResources().getColor(R.color.text_last_message_friend));
                    }
                } else {
                    holder.txtCountUnread.setVisibility(View.GONE);
                    holder.txtLastMessage.setTextColor(mContext.getResources().getColor(R.color.text_last_message_friend));
                }

                if (userId.equals(model.sender)) {
                    holder.txtReceiverName.setText(model.receiver_name);
                    holder.txtLastMessage.setText(mContext.getString(R.string.text_last_mess_sender) + ": " + model.message_content);
                    GlideApp.with(mContext)
                            .load(!TextUtils.isEmpty(model.receiver_avatar) ? model.receiver_avatar : R.drawable.ic_logo)
                            .placeholder(R.drawable.ic_logo)
                            .error(R.drawable.ic_logo)
                            .into(holder.img_avatar_receiver);
                } else {
                    holder.txtLastMessage.setText("" + model.message_content);
                    holder.txtReceiverName.setText(model.sender_name);
                    GlideApp.with(mContext)
                            .load(!TextUtils.isEmpty(model.sender_avatar) ? model.sender_avatar : R.drawable.ic_logo)
                            .placeholder(R.drawable.ic_logo)
                            .error(R.drawable.ic_logo)
                            .into(holder.img_avatar_receiver);

                }

//                holder.txtReceiverName.setText(model.SenderName);
//                String time = Utilities.convertDateTimeToString(model.LastDate);
//                String convert = TimeAgo.toRelative(model.LastDate, Calendar.getInstance().getTime(), 1);
//                holder.txtLastTime.setText(convert);


                holder.itemView.setOnClickListener(v -> {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    UserRetro friendNew = new UserRetro();
                    if (userId.equals(model.sender)) {
                        friendNew.RoomChatId = roomId;
                        friendNew.setUid(model.receiver);
                        friendNew.setDisplayname(model.receiver_name);
                        friendNew.setAvatar(model.receiver_avatar);
                    } else {
                        friendNew.RoomChatId = roomId;
                        friendNew.setUid(model.sender);
                        friendNew.setDisplayname(model.sender_name);
                        friendNew.setAvatar(model.sender_avatar);
                    }

                    Intent chat = new Intent(mContext, ChatActivity.class);
                    chat.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, friendNew);
                    chat.putExtra(Constants.KEY_PASSINGDATA_ROOM_ID, roomId);
                    chat.putExtra(Constants.KEY_IS_PENDING, false);
                    baseActivityTransition.transitionTo(chat, 0);
                });

                Date date = new Date(model.last_date);
                holder.txtLastTime.setText(TimeAgo.toRelative(date, Calendar.getInstance().getTime(), 1));

            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }
}
