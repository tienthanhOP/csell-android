package csell.com.vn.csell.commons;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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
import java.util.Random;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.controllers.NotiController;
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
import csell.com.vn.csell.models.UserRetro;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PushNotifications {

    private Context context;
    private DatabaseReference dbReference;
    private FileSave fileGet;
    private NotiController notiController;

    public PushNotifications(Context context) {
        this.context = context;
        dbReference = FirebaseDBUtil.getDatebase().getReference();
        fileGet = new FileSave(context, Constants.GET);
    }


    public boolean pushAcceptFriend() {
        return false;
    }

    public boolean pushAddFriend() {
        return false;
    }

    public void pushAddNotePrivate(HashMap<String, Object> data, String userId) {
        String id = dbReference.child(EntityFirebase.TableNotifications).child(userId).push().getKey();
        data.put(EntityAPI.FIELD_NOTIFICATION_NOTIFICATION_TYPE, 2);
        data.put(EntityAPI.FIELD_NOTIFICATION_ID_NOTI, id);

        if (!TextUtils.isEmpty(userId)) {
            dbReference.child(EntityFirebase.TableNotifications).child(userId).child(id).setValue(data);
        }

        dbReference.child(EntityFirebase.TableCountNotifications).child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Long countTemp = (Long) dataSnapshot.getValue();
                            countTemp++;
                            dbReference.child(EntityFirebase.TableCountNotifications).child(userId).setValue(countTemp);
                        } else {
                            dbReference.child(EntityFirebase.TableCountNotifications).child(userId).setValue(1);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void pushComment(HashMap<String, Object> data, String userId) {
        String id = dbReference.child(EntityFirebase.TableNotifications).child(userId).push().getKey();
        data.put(EntityAPI.FIELD_NOTIFICATION_NOTIFICATION_TYPE, 2);
        data.put(EntityAPI.FIELD_NOTIFICATION_ID_NOTI, id);
        dbReference.child(EntityFirebase.TableNotifications).child(userId).child(id).setValue(data);
        dbReference.child(EntityFirebase.TableCountNotifications).child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Long countTemp = (Long) dataSnapshot.getValue();
                            countTemp++;
                            dbReference.child(EntityFirebase.TableCountNotifications).child(userId).setValue(countTemp);
                        } else {
                            dbReference.child(EntityFirebase.TableCountNotifications).child(userId).setValue(1);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void pushLike(HashMap<String, Object> data, String userId) {
        try {
            String id = dbReference.child(EntityFirebase.TableNotifications).child(userId).push().getKey();
            data.put(EntityAPI.FIELD_NOTIFICATION_NOTIFICATION_TYPE, 2);
            data.put(EntityAPI.FIELD_NOTIFICATION_ID_NOTI, id);
            dbReference.child(EntityFirebase.TableCountNotifications).keepSynced(true);
            dbReference.child(EntityFirebase.TableNotifications).child(userId).child(id).setValue(data);
            dbReference.child(EntityFirebase.TableCountNotifications).child(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                Long countTemp = (Long) dataSnapshot.getValue();
                                countTemp++;
                                dbReference.child(EntityFirebase.TableCountNotifications).child(userId).setValue(countTemp);
                            } else {
                                dbReference.child(EntityFirebase.TableCountNotifications).child(userId).setValue(1);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        } catch (Exception ignored) {

        }
    }

    public boolean pushShared() {
        return false;
    }


    public void putNotiAcceptFriend(UserRetro friend) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put("type", 3);
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
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getSuccess()) {
                                    if (BuildConfig.DEBUG)
                                        Log.e(getClass().getName(), response.body().getMessage());
                                } else {
                                    Utilities.refreshToken(MainActivity.mainContext, response.body().getMessage().toLowerCase() + "");
                                    if (BuildConfig.DEBUG)
                                        Log.e(getClass().getName(), response.body().getMessage());
                                }

                                String msg = response.body().getError();
                                if (!TextUtils.isEmpty(msg)) {
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                }
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
                    } catch (Exception ignored) {

                    }
                }

                @Override
                public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                    if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
                }
            });
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(), e.getMessage());
        }
    }

}
