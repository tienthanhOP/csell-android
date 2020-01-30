package csell.com.vn.csell.sqlites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.models.NotificationType;

/**
 * Created by cuong.nv on 4/20/2018.
 */

public class SQLTypeNotifications {

    private SQLDatabaseHelper helper;

    public static final String TABLE_NOTIFICATION_TYPES = "notificationtype";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE_ID = "typeid";
    public static final String COLUMN_TYPE_NAME = "typeName";

    public SQLTypeNotifications(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    public SQLTypeNotifications(SQLDatabaseHelper helper) {
        this.helper = helper;
    }

    public static final String CREATE_TABLE_NOTIFICATION_TYPES = "CREATE TABLE " + TABLE_NOTIFICATION_TYPES + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TYPE_ID + " INTEGER, "
            + COLUMN_TYPE_NAME + " TEXT)";

    public void clearData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(TABLE_NOTIFICATION_TYPES, null, null);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (db != null) {
                db.close();
            }
        } finally {
            db.close();
        }
    }

    public int checkExistData() {
        int count = 0;
        String sql = "SELECT * FROM " + TABLE_NOTIFICATION_TYPES;
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            count = cursor.getCount();
        }
        cursor.close();
        db.close();
        return count;
    }

    public void insertNotificationTypes(List<NotificationType> list) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (NotificationType item : list) {

                values.put(COLUMN_TYPE_ID, item.type_id);
                values.put(COLUMN_TYPE_NAME, item.type_name);

                db.insert(TABLE_NOTIFICATION_TYPES, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
           if(BuildConfig.DEBUG) Log.d(""+getClass().getName(),e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<NotificationType> getAllType() {
        List<NotificationType> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select * from " + TABLE_NOTIFICATION_TYPES;

        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                NotificationType p = new NotificationType();
                p.type_id = c.getInt(c.getColumnIndex(COLUMN_TYPE_ID));
                p.type_name = c.getString(c.getColumnIndex(COLUMN_TYPE_NAME));

                data.add(p);

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }
}
