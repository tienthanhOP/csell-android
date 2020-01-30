package csell.com.vn.csell.views.notification.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.FriendsController;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.friend.activity.ListFriendRequestActivity;
import csell.com.vn.csell.views.friend.adapter.ListFriendsRequestAdapter;
import csell.com.vn.csell.views.social.activity.DetailProductActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.EntityFirebase;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FirebaseDBUtil;
import csell.com.vn.csell.constants.TimeAgo;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.Notification;
import csell.com.vn.csell.models.FriendResponse;
import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;

import static android.graphics.Typeface.BOLD;

/**
 * Created by cuong.nv on 3/6/2018.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private FileSave fileSave;
    private FileSave filePut;
    private ArrayList<Notification> data;
    private ArrayList<FriendResponse> dataRequest;
    private DatabaseReference dbReference;
    private BaseActivityTransition baseActivityTransition;
    private FriendsController.OnAcceptFriendListener callBackAcceptFriend;
    private ListFriendsRequestAdapter listFriendsRequestAdapter;
    private FriendsController.OnUnFriendListener callBackUnFriend;
    private long mLastClickTime = 0;

    public NotificationsAdapter(Context context, ArrayList<Notification> data, ArrayList<FriendResponse> dataRequest,
                                FriendsController.OnAcceptFriendListener callBackAcceptFriend,
                                FriendsController.OnUnFriendListener callBackUnFriend) {
        this.context = context;
        this.data = data;
        this.dataRequest = dataRequest;
        fileSave = new FileSave(context, Constants.GET);
        filePut = new FileSave(context, Constants.PUT);
        Fabric.with(context, new Crashlytics());
        dbReference = FirebaseDBUtil.getDatebase().getReference();
        baseActivityTransition = new BaseActivityTransition(context);
        this.callBackAcceptFriend = callBackAcceptFriend;
        this.callBackUnFriend = callBackUnFriend;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolderItem(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {

            ViewHolderItem viewHolderItem = (ViewHolderItem) holder;
            Notification notiTemp = data.get(position);

            try {

                if (data.size() >= 2) {
                    if (position == 1) {
                        if (dataRequest.size() > 0) {
                            listFriendsRequestAdapter = new ListFriendsRequestAdapter(context, dataRequest, true, callBackAcceptFriend,
                                    callBackUnFriend);
                            viewHolderItem.rvAcceptFriends.setAdapter(listFriendsRequestAdapter);
                            viewHolderItem.layoutItemAcceptFriend.setVisibility(View.VISIBLE);
                        } else {
                            viewHolderItem.layoutItemAcceptFriend.setVisibility(View.GONE);
                        }
                    } else {
                        viewHolderItem.layoutItemAcceptFriend.setVisibility(View.GONE);
                    }
                } else {
                    if (dataRequest.size() > 0) {
                        listFriendsRequestAdapter = new ListFriendsRequestAdapter(context, dataRequest, true, callBackAcceptFriend,
                                callBackUnFriend);
                        viewHolderItem.rvAcceptFriends.setAdapter(listFriendsRequestAdapter);
                        viewHolderItem.layoutItemAcceptFriend.setVisibility(View.VISIBLE);
                    } else {
                        viewHolderItem.layoutItemAcceptFriend.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                Log.d("XXX", e.getMessage());
            }

            try {

                if (position == 0) {
                    viewHolderItem.headerNoti.setVisibility(View.VISIBLE);
                    viewHolderItem.line.setVisibility(View.VISIBLE);
                } else {
                    viewHolderItem.headerNoti.setVisibility(View.GONE);
                    viewHolderItem.line.setVisibility(View.GONE);
                }
                if (position > 1) viewHolderItem.layoutItemAcceptFriend.setVisibility(View.GONE);
                if (notiTemp.is_seen != null) {
                    if (!notiTemp.is_seen)
                        viewHolderItem.layoutItem.setBackgroundColor(context.getResources().getColor(R.color.bg_notification_unread));
                    else
                        viewHolderItem.layoutItem.setBackgroundColor(0);
                }

                GlideApp.with(context)
                        .load(notiTemp.sender_avatar)
                        .placeholder(R.drawable.ic_logo)
                        .error(R.drawable.ic_logo)
                        .into(viewHolderItem.iconNotification);

                String target = notiTemp.target_name;
                String sender = notiTemp.sender_name;


                SpannableStringBuilder stringBuilder = new SpannableStringBuilder("");
                stringBuilder.append(sender).append(" ").setSpan(new StyleSpan(BOLD), 0, sender.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                if (notiTemp.action.equalsIgnoreCase(Constants.NOTI_NOTE_PRIVATE)) {
                    stringBuilder.append(context.getResources().getString(R.string.text_body_shared_note_private)).append(" ");
                } else if (notiTemp.action.equalsIgnoreCase(Constants.NOTI_LIKE)) {
                    stringBuilder.append(context.getResources().getString(R.string.text_body_liked)).append(" ").append(context.getResources().getString(R.string.text_body_of_you)).append(" ");
                } else {
                    stringBuilder.append(context.getResources().getString(R.string.text_body_comment)).append(" ").append(context.getResources().getString(R.string.text_body_of_you)).append(" ");
                }

                stringBuilder.append(target).setSpan(new StyleSpan(BOLD), (stringBuilder.length() - 1) - target.length(),
                        stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                viewHolderItem.tvContentNotification.setText(stringBuilder);

            } catch (Exception e) {
                viewHolderItem.tvContentNotification.setText(notiTemp.body);
            }

            if (notiTemp.date_created != null) {
                String time = TimeAgo.toRelative(notiTemp.date_created, Calendar.getInstance().getTimeInMillis(), 1);
                viewHolderItem.tvNotificationTime.setText(time);
            }

            viewHolderItem.tvSeeAll.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                Intent intent = new Intent(context, ListFriendRequestActivity.class);
                baseActivityTransition.transitionTo(intent, 0);
            });

            viewHolderItem.itemView.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                String action = data.get(position).action;

                try {
                    if (action != null) {

                        if (data.get(position).id_noti != null) {
                            dbReference.child(EntityFirebase.TableNotifications).child(fileSave.getUserId())
                                    .child(data.get(position).id_noti).child(EntityAPI.FIELD_NOTIFICATION_IS_SEEN).setValue(true)
                                    .addOnSuccessListener(aVoid -> {
                                        data.get(position).is_seen = true;
                                        notifyItemChanged(position);
                                    });
                        }

                        boolean isMe = data.get(position).sender.equals(fileSave.getUserId());
                        switch (data.get(position).action) {

                            case Utilities.ACTION_LIKE:
                                Intent like = new Intent(context, DetailProductActivity.class);
                                like.putExtra(Constants.TEMP_PRODUCT_KEY, data.get(position).data);
                                like.putExtra(Constants.TEMP_PRODUCT_UID, data.get(position).sender);
                                like.putExtra(Constants.OPEN_FROM_NOTI, true);
                                like.putExtra(Constants.IS_MY_PRODUCT, true);
                                filePut.putProductIdCurrentSelect(data.get(position).data);
                                baseActivityTransition.transitionTo(like, 0);
                                break;
                            case Utilities.ACTION_COMMENT:
                                Intent comment = new Intent(context, DetailProductActivity.class);
                                comment.putExtra(Constants.TEMP_PRODUCT_KEY, data.get(position).data);
                                comment.putExtra(Constants.TEMP_PRODUCT_UID, data.get(position).sender);
                                comment.putExtra(Constants.OPEN_FROM_NOTI, true);
                                comment.putExtra(Constants.IS_MY_PRODUCT, isMe);
                                filePut.putProductIdCurrentSelect(data.get(position).data);
                                baseActivityTransition.transitionTo(comment, 0);
                                Utilities.IsNotificationComment = true;
                                break;
                            case Utilities.ACTION_NOTE_PRIVATE:
                                Intent note = new Intent(context, DetailProductActivity.class);
                                note.putExtra(Constants.TURN_ON_PRIVATE_MODE, true);
                                note.putExtra(Constants.TEMP_PRODUCT_KEY, data.get(position).data);
                                note.putExtra(Constants.TEMP_PRODUCT_UID, data.get(position).sender);
                                note.putExtra(Constants.OPEN_FROM_NOTI, true);
                                note.putExtra(Constants.IS_MY_PRODUCT, isMe);
                                filePut.putProductIdCurrentSelect(data.get(position).data);
                                baseActivityTransition.transitionTo(note, 0);
                                break;
                            default:
                                break;
                        }
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.product_deleted_by_user), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                    Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                }

            });
        } catch (
                Exception e)

        {
            Crashlytics.logException(e);
        }
    }

    class ViewHolderItem extends RecyclerView.ViewHolder {

        CircleImageView iconNotification;
        CircleImageView iconSubNotification;
        TextView tvContentNotification;
        TextView tvNotificationTime;
        TextView tvSeeAll;
        TextView headerNoti;
        View line;
        RecyclerView rvAcceptFriends;
        LinearLayout layoutItem;
        LinearLayout layoutItemAcceptFriend;

        public ViewHolderItem(View itemView) {
            super(itemView);
            iconNotification = itemView.findViewById(R.id.item_notification_icon);
            iconSubNotification = itemView.findViewById(R.id.item_notification_icon_sub);
            tvContentNotification = itemView.findViewById(R.id.item_notification_name);
            tvNotificationTime = itemView.findViewById(R.id.item_notification_time);
            tvSeeAll = itemView.findViewById(R.id.txtSeeAll);
            headerNoti = itemView.findViewById(R.id.header_noti);
            line = itemView.findViewById(R.id.line);
            layoutItem = itemView.findViewById(R.id.layout_item_noti);
            layoutItemAcceptFriend = itemView.findViewById(R.id.layout_item_accept_friend);
            rvAcceptFriends = itemView.findViewById(R.id.rv_acceptFriends);
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            rvAcceptFriends.setLayoutManager(layoutManager);
            rvAcceptFriends.setHasFixedSize(true);
        }

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void handleSuccessAcceptFriend(UserRetro friend, int position) {
        try {
            listFriendsRequestAdapter.handleSuccessAcceptFriend(friend, position);
        } catch (Exception ignored) {

        }
    }

    public void handleSuccessUnFriend(UserRetro friend, int position) {
        try {
            listFriendsRequestAdapter.handleSuccessUnFriend(friend, position);
        } catch (Exception ignored) {

        }
    }
}
