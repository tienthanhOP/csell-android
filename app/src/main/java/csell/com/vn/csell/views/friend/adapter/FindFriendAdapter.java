package csell.com.vn.csell.views.friend.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

//import com.google.firebase.firestore.//DocumentReference;
//import com.google.firebase.firestore.FieldValue;
//import com.google.firebase.firestore.//writeBatch;

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
import csell.com.vn.csell.views.friend.activity.FindFriendsActivity;
import csell.com.vn.csell.views.friend.activity.FriendDetailsActivity;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.account.activity.PersonalPageActivity;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.EntityFirebase;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FirebaseDBUtil;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.commons.PushNotifications;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.models.LastMessage;
import csell.com.vn.csell.models.FriendInfo;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.sqlites.SQLFriends;
import csell.com.vn.csell.views.friend.adapter.viewholder.FindFriendViewHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by cuong.nv on 3/1/2018.
 */

public class FindFriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<UserRetro> data;
    private FileSave fileGet;
    private SQLFriends sqlFriends;
    private DatabaseReference mDatabaseRef;
    private BaseActivityTransition baseActivityTransition;
    private FriendsController friendsController;
    private FriendsController.OnSendRequestListener callBackSendRequest;
    private FriendsController.OnAcceptFriendListener callBackAcceptFriend;

    public FindFriendAdapter(Context context, ArrayList<UserRetro> data, FriendsController.OnSendRequestListener callBackSendRequest,
                             FriendsController.OnAcceptFriendListener callBackAcceptFriend) {
        this.context = context;
        this.data = data;
        fileGet = new FileSave(context, Constants.GET);
        mDatabaseRef = FirebaseDBUtil.getDatebase().getReference();
        sqlFriends = new SQLFriends(context);
        baseActivityTransition = new BaseActivityTransition(context);
        friendsController = new FriendsController(context);
        this.callBackSendRequest = callBackSendRequest;
        this.callBackAcceptFriend = callBackAcceptFriend;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FindFriendViewHolder(itemView);
    }


    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            FindFriendViewHolder holderItem = (FindFriendViewHolder) holder;
            holderItem.txtName.setText(data.get(position).getDisplayname());
            holderItem.tvtStatus.setVisibility(View.VISIBLE);
            if (data.get(position).getUsername() != null) {
                holderItem.tvtStatus.setText(data.get(position).getUsername());
            }
            holderItem.btnAddFriend.setVisibility(View.VISIBLE);
            GlideApp.with(context)
                    .load(data.get(position).getAvatar())
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo)
                    .into(holderItem.imgAvatar);

            UserRetro friend = data.get(position);
            FriendInfo friendInfo = friend.getFriendInfo();

            if (friendInfo != null) {

                if (friendInfo.getIsAccepted()) {
                    holderItem.btnAddFriend.setText(context.getString(R.string.friend));
                    holderItem.btnAddFriend.setTextColor(ContextCompat.getColor(context, R.color.black_main));
                    holderItem.btnAddFriend.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_75));
                } else {
                    if (friendInfo.getIsRequested()) {
                        if (friendInfo.getRequestedBy().equals(fileGet.getUserId())) {
                            holderItem.btnAddFriend.setText(context.getString(R.string.title_send_request));
                            holderItem.btnAddFriend.setTextColor(ContextCompat.getColor(context, R.color.black_main));
                            holderItem.btnAddFriend.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_75));
                        } else {
                            holderItem.btnAddFriend.setText(context.getString(R.string.accept_friend));
                            holderItem.btnAddFriend.setTextColor(ContextCompat.getColor(context, R.color.white_100));
                            holderItem.btnAddFriend.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_main));
                        }
                    }
                }
            } else {
                if (fileGet.getUserId().equals(data.get(position).getUid()))
                    holderItem.btnAddFriend.setVisibility(View.GONE);
                else {
                    holderItem.btnAddFriend.setVisibility(View.VISIBLE);
                    holderItem.btnAddFriend.setText(context.getString(R.string.add_friend));
                    holderItem.btnAddFriend.setTextColor(ContextCompat.getColor(context, R.color.white_100));
                    holderItem.btnAddFriend.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_main));
                }
            }

            holderItem.btnAddFriend.setOnClickListener(v -> {
                if (holderItem.btnAddFriend.getText().equals(context.getString(R.string.add_friend))) {
                    sendRequest(data.get(position), position);
                } else if (holderItem.btnAddFriend.getText().equals(context.getString(R.string.accept_friend))) {
                    acceptFriend(data.get(position), position);
                } else if (holderItem.btnAddFriend.getText().equals(context.getString(R.string.title_send_request))) {
                    Intent detail = new Intent(context, FriendDetailsActivity.class);
                    detail.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, data.get(position));
                    baseActivityTransition.transitionTo(detail, 0);
                } else if (holderItem.btnAddFriend.getText().equals(context.getString(R.string.friend))) {
                    Intent detail = new Intent(context, FriendDetailsActivity.class);
                    detail.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, data.get(position));
                    baseActivityTransition.transitionTo(detail, 0);
                } else {
                    Intent detail = new Intent(context, PersonalPageActivity.class);
                    baseActivityTransition.transitionTo(detail, 0);
                }
            });

            if (data.get(position).getFriendGeneral() != null) {
                if (data.get(position).getFriendGeneral() != 0) {
                    holderItem.tvtStatus.setText(data.get(position).getFriendGeneral() + " " + context.getString(R.string.title_friend_general));
                }
            }

            holderItem.itemView.setOnClickListener(v -> {
                if (fileGet.getUserId().equals(data.get(position).getUid())) {
                    Intent detail = new Intent(context, PersonalPageActivity.class);
                    baseActivityTransition.transitionTo(detail, 0);
                } else {
                    Intent detail = new Intent(context, FriendDetailsActivity.class);
                    detail.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, data.get(position));
                    baseActivityTransition.transitionTo(detail, 0);
                }
            });
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    private void sendRequest(UserRetro friend, int position) {
        try {
            friendsController.sendRequest(friend, position, callBackSendRequest);
        } catch (Exception ignored) {

        }
    }

    private void putNotiSendRequest(UserRetro friend) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", 2);
//        map.put("key", friend.getUid());
        String keyData = fileGet.getUserId() + ":" + fileGet.getUserName() + ":" + fileGet.getDisplayName();
        map.put("key", keyData);
        List<String> lstUser = new ArrayList<>();
        lstUser.add(friend.getUid());
        map.put("users", lstUser);

        PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        Call<JSONResponse<Object>> sendNoti = postAPI.sendNoti(map);

        sendNoti.enqueue(new Callback<JSONResponse<Object>>() {
            @Override
            public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() != null) {
                        if (response.body().getSuccess()) {
                            if (BuildConfig.DEBUG)
                                Log.e(getClass().getName(), response.body().getMessage());
                        } else {
                            Utilities.refreshToken(MainActivity.mainContext, response.body().getMessage().toLowerCase() + "");
                            if (BuildConfig.DEBUG)
                                Log.e(getClass().getName(), response.body().getMessage());
                        }
                    }

                    String msg = response.body().getError();
                    if (!TextUtils.isEmpty(msg)) {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = (String) jsonObject.get(Constants.ERROR);
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
            }
        });
    }

    private void acceptFriend(UserRetro friend, int position) {
        try {
            friendsController.acceptFriend(friend, position, callBackAcceptFriend);
        } catch (Exception ignored) {

        }
    }

    public void handleSuccessSendRequest(UserRetro friend, int position) {
        try {
            putNotiSendRequest(friend);
            FriendInfo friendInfo = new FriendInfo();
            friendInfo.setRequestedBy(fileGet.getUserId());
            friendInfo.setIsAccepted(false);
            friendInfo.setIsRequested(true);
            friendInfo.setFollowByFriend(true);
            data.get(position).setFriendInfo(friendInfo);
            FindFriendsActivity.adapter.notifyDataSetChanged();
        } catch (Exception ignored) {

        }
    }

    public void handleSuccessAcceptFriend(UserRetro friend, int position){
        sqlFriends.insertAddFriend(friend);
        if (Utilities.lsFriendRequest.size() != 0) {
            for (int i = 0; i < Utilities.lsFriendRequest.size(); i++) {
                if (Utilities.lsFriendRequest.get(i).getFriendInfo().get(0).getUid().equals(data.get(position).getUid())) {
                    Utilities.lsFriendRequest.remove(i);
                    break;
                }
            }
        }

        PushNotifications pushNotifications = new PushNotifications(context);
        pushNotifications.putNotiAcceptFriend(friend);

        FriendInfo friendInfo = new FriendInfo();
        friendInfo.setRequestedBy(data.get(position).getUid());
        friendInfo.setIsAccepted(true);
        friendInfo.setIsRequested(false);
        friendInfo.setFollowByFriend(true);
        data.get(position).setFriendInfo(friendInfo);
        FindFriendsActivity.adapter.notifyDataSetChanged();

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
    }
}
