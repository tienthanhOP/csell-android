package csell.com.vn.csell.views.friend.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.FriendsController;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.friend.activity.FriendDetailsActivity;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.EntityFirebase;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FirebaseDBUtil;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.models.LastMessage;
import csell.com.vn.csell.models.FriendResponse;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.sqlites.SQLFriends;
import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by cuong.nv on 3/2/2018.
 */

public class ListFriendsRequestAdapter extends RecyclerView.Adapter<ListFriendsRequestAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FriendResponse> data;
    private ArrayList<FriendResponse> mListFilter;
    private FileSave fileGet;
    private SQLFriends sqlFriends;
    private boolean isLimit;
    private DatabaseReference mDatabaseRef;
    private BaseActivityTransition baseActivityTransition;
    private FriendsController friendsController;
    private FriendsController.OnAcceptFriendListener callBackAcceptFriend;
    private FriendsController.OnUnFriendListener callBackUnFriend;

    public ListFriendsRequestAdapter(Context context, ArrayList<FriendResponse> data, boolean isLimit,
                                     FriendsController.OnAcceptFriendListener callBackAcceptFriend,
                                     FriendsController.OnUnFriendListener callBackUnFriend) {
        this.context = context;
        this.data = data;
        this.isLimit = isLimit;
        baseActivityTransition = new BaseActivityTransition(context);
        fileGet = new FileSave(context, Constants.GET);
        sqlFriends = new SQLFriends(context);
        Fabric.with(context, new Crashlytics());
        mDatabaseRef = FirebaseDBUtil.getDatebase().getReference();
        this.mListFilter = new ArrayList<>();
        this.mListFilter.addAll(data);
        friendsController = new FriendsController(context);
        this.callBackAcceptFriend = callBackAcceptFriend;
        this.callBackUnFriend = callBackUnFriend;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_accept_friend, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            holder.btnAccept.setText(context.getString(R.string.agree));

            holder.bindItem(position);

            UserRetro friend = data.get(position).getFriendInfo().get(0);

            holder.tvFriendName.setText(friend.getDisplayname());

            if (data.get(position).getFriendGeneral() != 0) {
                holder.tvFriendStatus.setVisibility(View.VISIBLE);
                holder.tvFriendStatus.setText(data.get(position).getFriendGeneral() + " " + context.getString(R.string.title_friend_general));
            } else {
                holder.tvFriendStatus.setVisibility(View.GONE);
            }

            holder.btnAccept.setOnClickListener(v -> acceptFriend(position, data.get(position)));
            holder.btnRemove.setOnClickListener(v -> {
                denyFriend(position, data.get(position));
                data.remove(position);
                notifyDataSetChanged();
            });

            GlideApp.with(context)
                    .load(!TextUtils.isEmpty(friend.getAvatar()) ? friend.getAvatar() : R.drawable.ic_logo)
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo)
                    .into(holder.imgAvatar);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }

    }


    @Override
    public int getItemCount() {
        if (isLimit) {
            if (data.size() > 2)
                return 2;
            else
                return data.size();
        } else
            return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imgAvatar;
        TextView tvFriendName;
        TextView tvFriendStatus;
        FancyButton btnAccept;
        FancyButton btnRemove;
        LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_avatar_contact_friend);
            tvFriendName = itemView.findViewById(R.id.tv_name_friend);
            tvFriendStatus = itemView.findViewById(R.id.tv_status_friend);
            btnAccept = itemView.findViewById(R.id.btn_add_friend);
            btnRemove = itemView.findViewById(R.id.btn_remove_friend);
            layout = itemView.findViewById(R.id.item_contact_friend);
        }


        void bindItem(final int position) {
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, FriendDetailsActivity.class);
                intent.putExtra(Constants.KEY_PASSINGDATA_ACCEPT, true);
                intent.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, data.get(position).getFriendInfo().get(0));
                intent.putExtra(Constants.KEY_POSITON_LIST, position);
                baseActivityTransition.transitionTo(intent, 0);
            });
        }
    }

    private void acceptFriend(int position, FriendResponse friend) {
        try {
            friendsController.acceptFriend(friend.getFriendInfo().get(0), position, callBackAcceptFriend);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void putNotiAcceptFriend(UserRetro friend) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", 3);
//        map.put("key", friend.getUid());
        map.put("key", friend.getUsername());
        List<String> lstUser = new ArrayList<>();
        lstUser.add(friend.getUid());
        map.put("users", lstUser);

        PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        Call<JSONResponse<Object>> sendNoti = postAPI.sendNoti(map);

        sendNoti.enqueue(new Callback<JSONResponse<Object>>() {
            @Override
            public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess() != null) {
                            if (response.body().getSuccess()) {
                                if (BuildConfig.DEBUG)
                                    Log.e(getClass().getName(), response.body().getMessage());
                            } else {
                                if (BuildConfig.DEBUG)
                                    Log.e(getClass().getName(), response.body().getMessage());
                                Utilities.refreshToken(MainActivity.mainContext, response.body().getMessage().toLowerCase() + "");
                            }
                        }
                    } else {

                    }
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {

            }
        });
    }

    private void denyFriend(int position, FriendResponse friend) {
        try {
            friendsController.unFriend(friend.getFriendInfo().get(0), true, false, position, callBackUnFriend);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }

    }

    public void findRequest(String s) {

        data.clear();
        if (TextUtils.isEmpty(s)) {
            data.addAll(this.mListFilter);
        } else {
            for (FriendResponse local : this.mListFilter) {
                if (local.getFriendInfo().get(0).getDisplayname().toLowerCase().contains(s.toLowerCase())
                        || local.getFriendInfo().get(0).getUsername().toLowerCase().contains(s)) {
                    data.add(local);
                }
            }
        }
        notifyDataSetChanged();

    }

//    void addListReadOnly(ArrayList<FriendResponse> readOnly){
//        this.mListFilter.clear();
//        this.mListFilter.addAll(readOnly);
//    }

    public void handleSuccessAcceptFriend(UserRetro friend, int position) {
        try {
            sqlFriends.insertAddFriend(friend);

            if (Utilities.lsFriendRequest.size() != 0) {
                Utilities.lsFriendRequest.remove(position);
            }
            data.remove(position);

            notifyDataSetChanged();
            putNotiAcceptFriend(friend);
            mDatabaseRef.child(EntityFirebase.TableLastMessages)
                    .child(fileGet.getUserId())
                    .orderByChild(EntityFirebase.FieldSender).equalTo(friend.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Long count = dataSnapshot.getChildrenCount();
                            if (count == 0) return;
                            if (count != 0) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    LastMessage lastMessage = snapshot.getValue(LastMessage.class);
                                    if (lastMessage.is_pending) {

                                        String roomId = snapshot.getKey();

                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put(EntityFirebase.FieldIsPending, false);
                                        map.put(EntityFirebase.FieldIsAccepted, true);
                                        mDatabaseRef.child(EntityFirebase.TableLastMessages)
                                                .child(fileGet.getUserId())
                                                .child(roomId)
                                                .updateChildren(map);

                                        mDatabaseRef.child(EntityFirebase.TableDirectMessages)
                                                .child(roomId)
                                                .child(lastMessage.message_id)
                                                .updateChildren(map);

                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                //Do something after 100ms
                mDatabaseRef.child(EntityFirebase.TableLastMessages)
                        .child(friend.getUid())
                        .orderByChild(EntityFirebase.FieldSender).equalTo(fileGet.getUserId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Long count = dataSnapshot.getChildrenCount();
                                if (count == 0) return;
                                if (count != 0) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        LastMessage lastMessage = snapshot.getValue(LastMessage.class);
                                        if (lastMessage.is_pending) {

                                            String roomId = snapshot.getKey();

                                            HashMap<String, Object> map1 = new HashMap<>();
                                            map1.put(EntityFirebase.FieldIsPending, false);
                                            map1.put(EntityFirebase.FieldIsAccepted, true);
                                            mDatabaseRef.child(EntityFirebase.TableLastMessages)
                                                    .child(friend.getUid())
                                                    .child(roomId)
                                                    .updateChildren(map1);

                                            mDatabaseRef.child(EntityFirebase.TableDirectMessages)
                                                    .child(roomId)
                                                    .child(lastMessage.message_id)
                                                    .updateChildren(map1);

                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }, 1000);
        } catch (Exception ignored) {

        }
    }

    public void handleSuccessUnFriend(UserRetro friend, int position) {
        try {
            sqlFriends.deleteFriend(friend.getUid());
            if (Utilities.lsFriendRequest.size() != 0) {
                Utilities.lsFriendRequest.remove(position);
            }
            notifyDataSetChanged();
        } catch (Exception ignored) {

        }
    }
}
