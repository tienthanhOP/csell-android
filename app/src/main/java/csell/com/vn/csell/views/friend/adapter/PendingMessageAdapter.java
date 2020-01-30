package csell.com.vn.csell.views.friend.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.friend.activity.ChatActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityFirebase;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FirebaseDBUtil;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.LastMessage;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.views.friend.adapter.viewholder.FindFriendViewHolder;

public class PendingMessageAdapter extends FirebaseRecyclerAdapter<LastMessage, FindFriendViewHolder.PendingMessageViewHolder> {

    private Context mContext;
    private FileSave fileGet;
    private ObservableSnapshotArray data;
    private DatabaseReference mFirebaseRef;
    private BaseActivityTransition baseActivityTransition;

    public PendingMessageAdapter(Context context, FirebaseRecyclerOptions<LastMessage> options) {
        super(options);
        this.mContext = context;
        this.data = options.getSnapshots();
        fileGet = new FileSave(context, Constants.GET);
        mFirebaseRef = FirebaseDBUtil.getDatebase().getReference();
        baseActivityTransition = new BaseActivityTransition(context);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull FindFriendViewHolder.PendingMessageViewHolder holder, int position, @NonNull LastMessage model) {
        try {
            if (model != null) {
                //room chat
                String roomId = getSnapshots().getSnapshot(position).getKey();
                String userId = fileGet.getUserId();
                if (userId.equals(model.sender)) {
                    holder.txtReceiverName.setText(model.receiver_name);
                    holder.txtLastMessage.setText(mContext.getString(R.string.text_last_mess_sender) + ": " + model.message_content);
                } else {
                    holder.txtLastMessage.setText("" + model.message_content);
                    holder.txtReceiverName.setText(model.sender_name);
                }

                if (!model.is_seen) {
                    holder.txtLastMessage.setTextColor(mContext.getResources().getColor(R.color.text_last_message_friend));
                } else {
                    holder.txtLastMessage.setTextColor(mContext.getResources().getColor(R.color.text_last_message_friend_unread));
                }

//                holder.txtReceiverName.setText(model.SenderName);
//                String time = Utilities.convertDateTimeToString(model.LastDate);
//                String convert = TimeAgo.toRelative(model.LastDate, Calendar.getInstance().getTime(), 1);
//                holder.txtLastTime.setText(convert);

                GlideApp.with(mContext)
                        .load(model.sender_avatar)
                        .placeholder(R.drawable.ic_logo)
                        .error(R.drawable.ic_logo)
                        .into(holder.img_avatar_receiver);

                holder.itemView.setOnClickListener(v -> {
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
                    chat.putExtra(Constants.KEY_IS_PENDING, true);
                    baseActivityTransition.transitionTo(chat, 0);
                });

                holder.btnAccept.setOnClickListener(view -> {

                    HashMap<String, Object> map = new HashMap<>();
                    map.put(EntityFirebase.FieldIsPending, false);
                    map.put(EntityFirebase.FieldIsAccepted, true);
                    mFirebaseRef.child(EntityFirebase.TableLastMessages)
                            .child(fileGet.getUserId())
                            .child(roomId)
                            .updateChildren(map);

                    mFirebaseRef.child(EntityFirebase.TableLastMessages)
                            .child(fileGet.getUserId())
                            .child(roomId)
                            .child(EntityFirebase.FieldMessageId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String messageId = dataSnapshot.getValue().toString();
                                    mFirebaseRef.child(EntityFirebase.TableDirectMessages)
                                            .child(roomId)
                                            .child(messageId)
                                            .updateChildren(map);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                });

                holder.btnDeny.setOnClickListener(view -> {

                    mFirebaseRef.child(EntityFirebase.TableLastMessages)
                            .child(fileGet.getUserId())
                            .child(roomId)
                            .removeValue();

                    mFirebaseRef.child(EntityFirebase.TableLastMessages)
                            .child(model.sender)
                            .child(roomId)
                            .removeValue();

                    mFirebaseRef.child(EntityFirebase.TableDirectMessages)
                            .child(roomId)
                            .removeValue();

                });

            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    @NonNull
    @Override
    public FindFriendViewHolder.PendingMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pending_message, parent, false);
        return new FindFriendViewHolder.PendingMessageViewHolder(itemView);
    }
}
