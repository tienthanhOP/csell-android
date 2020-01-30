package csell.com.vn.csell.views.friend.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

//import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
//import com.firebase.ui.firestore.ObservableSnapshotArray;
////import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.friend.activity.ChatActivity;
import csell.com.vn.csell.views.friend.activity.FriendDetailsActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityFirebase;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FirebaseDBUtil;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.views.friend.adapter.viewholder.FriendViewHolder;

/**
 * Created by chuc.nq on 4/1/2018.
 */

public class FriendsFirebaseAdapter extends RecyclerView.Adapter<FriendViewHolder> {

    private Context context;
    private ArrayList<UserRetro> data;
    private DatabaseReference dbReference;
    private FileSave fileGet;
    private String roomId;
    private BaseActivityTransition baseActivityTransition;
    private long mLastClickTime = 0;

    public FriendsFirebaseAdapter(Context context, ArrayList<UserRetro> list) {
        this.context = context;
        this.data = list;
        fileGet = new FileSave(context, Constants.GET);
        dbReference = FirebaseDBUtil.getDatebase().getReference();
        baseActivityTransition = new BaseActivityTransition(context);
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tvName.setText(data.get(position).getDisplayname());
//        if (!TextUtils.isEmpty(data.get(position).getPhone())) {
//            holder.tvStatus.setText(data.get(position).getPhone());
//        }

        GlideApp.with(context)
                .load(!TextUtils.isEmpty(data.get(position).getAvatar()) ? data.get(position).getAvatar() : R.drawable.ic_logo)
                .centerCrop()
                .error(R.drawable.ic_logo)
                .placeholder(R.drawable.ic_logo)
                .into(holder.imgAvatar);

        holder.itemView.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            Intent detail = new Intent(context, FriendDetailsActivity.class);
            detail.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, data.get(position));
            baseActivityTransition.transitionTo(detail, 0);
        });

        holder.layoutSendMessage.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            roomId = "";
            if (data.get(position).getUid() != null) {
                String fUid = data.get(position).getUid();
                dbReference.child(EntityFirebase.FieldRooms)
                        .child(fileGet.getUserId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() > 0) {
                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                        if (dataSnapshot1.getKey().equals(fUid)) {
                                            if (dataSnapshot1.getValue() != null)
                                                roomId = dataSnapshot1.getValue() != null ? (String) dataSnapshot1.getValue() : null;
                                        }
                                    }
                                } else {
                                    roomId = null;
                                }

                                if (TextUtils.isEmpty(roomId)) {
                                    roomId = fileGet.getUserId() + fUid;
                                    dbReference.child(EntityFirebase.FieldRooms).child(fileGet.getUserId()).child(fUid).setValue(roomId);
                                    dbReference.child(EntityFirebase.FieldRooms).child(fUid).child(fileGet.getUserId()).setValue(roomId);
                                } else {
//                                    dbReference.child(EntityFirebase.TableOnlineChat).child(fileGet.getUserId()).setValue(roomId);
//                                    dbReference.child(EntityFirebase.TableOnlineChat).keepSynced(false);
                                    dbReference.child(EntityFirebase.TableDirectMessages).child(fUid).keepSynced(false);
                                }

                                if (!TextUtils.isEmpty(roomId)) {
                                    Intent chat = new Intent(context, ChatActivity.class);
                                    chat.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, data.get(position));
                                    chat.putExtra(Constants.KEY_PASSINGDATA_ROOM_ID, roomId);
                                    chat.putExtra(Constants.KEY_IS_PENDING, false);
                                    baseActivityTransition.transitionTo(chat, 0);
                                } else
                                    Toast.makeText(context, R.string.cant_not_find_friend, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            }
        });

        holder.btnChat.setOnClickListener(v -> holder.layoutSendMessage.performClick());

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
