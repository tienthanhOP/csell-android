package csell.com.vn.csell.sqlites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.models.UserRetro;

/**
 * Created by chuc.nq on 3/20/2018.
 */

public class SQLFriends {


    private SQLDatabaseHelper helper;

    public static final String TABLE_FRIEND = "friend";

    private static final String COLUMN_ID = "id";

    private static final String COLUMN_USER_NAME = "UserName";
    private static final String COLUMN_DISPLAY_NAME = "DisplayName";
    private static final String COLUMN_EMAIL = "Email";
    private static final String COLUMN_PHONE = "Phone";
    private static final String COLUMN_AVATAR = "Avatar";
    private static final String COLUMN_IS_REQUESTED = "IsRequested";
    private static final String COLUMN_IS_FRIEND = "IsFriend";
    private static final String COLUMN_FRIEND_ID = "FriendId";
    private static final String COLUMN_IS_ACCEPTED = "IsAccepted";
    private static final String COLUMN_IS_FOLLOW = "IsFollow";
    private static final String COLUMN_IS_INVITED = "IsInvited";
    private static final String COLUMN_UNSEEN_QTY = "UnSeenQty";
    private static final String COLUMN_DATE_ACCEPTED = "DateAccepted";
    private static final String COLUMN_ROOM_CHAT_ID = "RoomChatId";

    public static final String CREATE_TABLE_FRIENDS = "create table " + TABLE_FRIEND + " ("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_FRIEND_ID + " text, "
            + COLUMN_USER_NAME + " text, "
            + COLUMN_DISPLAY_NAME + " text, "
            + COLUMN_EMAIL + " text, "
            + COLUMN_PHONE + " text, "
            + COLUMN_AVATAR + " text, "
            + COLUMN_DATE_ACCEPTED + " text, "
            + COLUMN_UNSEEN_QTY + " integer)";

    public SQLFriends(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }


    /* Kiem tra dữ liệu có không*/
    public boolean checkExistData() {

        boolean output = false;
        String sqlQuery = "select * from " + TABLE_FRIEND + " limit 5";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);

        if (cursor.getCount() >= 1) {
            output = true;
        }

        cursor.close();
        db.close();
        return output;

    }

    /*Clear du lieu*/
    public void clearData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(TABLE_FRIEND, null, null);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (db != null) {
                db.close();
            }
        }
    }

    /* Kiem tra xem co bao nhieu ban ghi*/
    public int getCountAllFriends() {

        int c = 0;
        String allQuery = "select * from " + TABLE_FRIEND;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(allQuery, null);
        c = cursor.getCount();
        cursor.close();
        db.close();

        return c;

    }


    public void insertFriends1(List<UserRetro> dataFriends) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (UserRetro item : dataFriends) {
                values.put(COLUMN_AVATAR, !TextUtils.isEmpty(item.getAvatar()) ? item.getAvatar() : "");
                values.put(COLUMN_EMAIL, !TextUtils.isEmpty(item.getEmail()) ? item.getEmail() : "");
                values.put(COLUMN_DISPLAY_NAME, !TextUtils.isEmpty(item.getDisplayname()) ? item.getDisplayname() : "");
                values.put(COLUMN_USER_NAME, !TextUtils.isEmpty(item.getUsername()) ? item.getUsername() : "");
                values.put(COLUMN_PHONE, !TextUtils.isEmpty(item.getPhone()) ? item.getPhone() : "");
                values.put(COLUMN_FRIEND_ID, !TextUtils.isEmpty(item.getUid()) ? item.getUid() : "");

                db.insert(TABLE_FRIEND, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
           if(BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void insertAddFriend(UserRetro dataFriends) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        Cursor cursor = null;
        try {
            String sqlQuery = "select * from " + TABLE_FRIEND + "  WHERE " + COLUMN_FRIEND_ID + " ='" + dataFriends.getUid() + "' ";
            cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                ContentValues values = new ContentValues();

                values.put(COLUMN_AVATAR, !TextUtils.isEmpty(dataFriends.getAvatar()) ? dataFriends.getAvatar() : "");
                values.put(COLUMN_EMAIL, !TextUtils.isEmpty(dataFriends.getEmail()) ? dataFriends.getEmail() : "");
                values.put(COLUMN_DISPLAY_NAME, !TextUtils.isEmpty(dataFriends.getDisplayname()) ? dataFriends.getDisplayname() : "");
                values.put(COLUMN_USER_NAME, !TextUtils.isEmpty(dataFriends.getUsername()) ? dataFriends.getUsername() : "");
                values.put(COLUMN_PHONE, !TextUtils.isEmpty(dataFriends.getPhone()) ? dataFriends.getPhone() : "");
                values.put(COLUMN_FRIEND_ID, !TextUtils.isEmpty(dataFriends.getUid()) ? dataFriends.getUid() : "");

                db.update(TABLE_FRIEND, values, COLUMN_FRIEND_ID + "='" + dataFriends.getUid() + "'", null);

            } else {

                ContentValues values = new ContentValues();

                values.put(COLUMN_AVATAR, !TextUtils.isEmpty(dataFriends.getAvatar()) ? dataFriends.getAvatar() : "");
                values.put(COLUMN_EMAIL, !TextUtils.isEmpty(dataFriends.getEmail()) ? dataFriends.getEmail() : "");
                values.put(COLUMN_DISPLAY_NAME, !TextUtils.isEmpty(dataFriends.getDisplayname()) ? dataFriends.getDisplayname() : "");
                values.put(COLUMN_USER_NAME, !TextUtils.isEmpty(dataFriends.getUsername()) ? dataFriends.getUsername() : "");
                values.put(COLUMN_PHONE, !TextUtils.isEmpty(dataFriends.getPhone()) ? dataFriends.getPhone() : "");
                values.put(COLUMN_FRIEND_ID, !TextUtils.isEmpty(dataFriends.getUid()) ? dataFriends.getUid() : "");

                db.insert(TABLE_FRIEND, null, values);
            }


            db.setTransactionSuccessful();
        } catch (Exception e) {
           if(BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            cursor.close();
            db.endTransaction();
            db.close();
        }
    }

    public UserRetro checkIsFriendById(String friendId) {

        UserRetro friend = new UserRetro();
        String sqlQuery = "select * from " + TABLE_FRIEND + "  WHERE " + COLUMN_FRIEND_ID + " ='" + friendId + "' ";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            friend.setUid(cursor.getString(cursor.getColumnIndex(COLUMN_FRIEND_ID)));
            friend.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
            friend.setDisplayname(cursor.getString(cursor.getColumnIndex(COLUMN_DISPLAY_NAME)));
            friend.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
            friend.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
            friend.setAvatar(cursor.getString(cursor.getColumnIndex(COLUMN_AVATAR)));
//            friend.RoomChatId = cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_CHAT_ID));

            cursor.close();
            db.close();
            return friend;
        } else {
            cursor.close();
            db.close();
            return null;
        }


    }


    public UserRetro checkIsFriendByUserName(String uname) {

        UserRetro friend = new UserRetro();
        String sqlQuery = "select * from " + TABLE_FRIEND + "  WHERE " + COLUMN_USER_NAME + " ='" + uname + "' ";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            friend.setUid(cursor.getString(cursor.getColumnIndex(COLUMN_FRIEND_ID)));
            friend.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
            friend.setDisplayname(cursor.getString(cursor.getColumnIndex(COLUMN_DISPLAY_NAME)));
            friend.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
            friend.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
            friend.setAvatar(cursor.getString(cursor.getColumnIndex(COLUMN_AVATAR)));
//            friend.RoomChatId = cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_CHAT_ID));

            cursor.close();
            db.close();
            return friend;
        } else {
            cursor.close();
            db.close();
            return null;
        }


    }

    public ArrayList<UserRetro> getAllFriend1() {

        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<UserRetro> listFriend = new ArrayList<>();

        String selectQuery = "select * from " + TABLE_FRIEND;

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {

                    UserRetro friend = new UserRetro();
                    friend.setUid(cursor.getString(cursor.getColumnIndex(COLUMN_FRIEND_ID)));
                    friend.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                    friend.setDisplayname(cursor.getString(cursor.getColumnIndex(COLUMN_DISPLAY_NAME)));
                    friend.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
                    friend.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
                    friend.setAvatar(cursor.getString(cursor.getColumnIndex(COLUMN_AVATAR)));

//                    friend.setRoomId(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_CHAT_ID)));

                    listFriend.add(friend);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {

        } finally {
            cursor.close();
            db.close();
        }

        return listFriend;
    }

    public ArrayList<UserRetro> getAllFriendLimit(int limit) {

        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<UserRetro> listFriend = new ArrayList<>();

        String selectQuery = "select * from " + TABLE_FRIEND + " limit " + limit;

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {

                    UserRetro friend = new UserRetro();
                    friend.setUid(cursor.getString(cursor.getColumnIndex(COLUMN_FRIEND_ID)));
                    friend.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                    friend.setDisplayname(cursor.getString(cursor.getColumnIndex(COLUMN_DISPLAY_NAME)));
                    friend.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
                    friend.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
                    friend.setAvatar(cursor.getString(cursor.getColumnIndex(COLUMN_AVATAR)));

//                    friend.setRoomId(cursor.getString(cursor.getColumnIndex(COLUMN_ROOM_CHAT_ID)));

                    listFriend.add(friend);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {

        } finally {
            cursor.close();
            db.close();
        }

        return listFriend;
    }


    public void deleteFriend(String friendId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE_FRIEND, COLUMN_FRIEND_ID + " like '" + friendId + "'", null);

        db.close();
    }

}
