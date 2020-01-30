package csell.com.vn.csell.sqlites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.models.PostType;

/**
 * Created by cuong.nv on 4/20/2018.
 */

public class SQLPostTypes {

    private SQLDatabaseHelper helper;

    public static final String TABLE_POST_TYPES = "privacy_type";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE_ID = "privacy_id";
    public static final String COLUMN_TYPE_NAME = "privacy_name";
    public static final String COLUMN_DISPLAY_NAME = "display_name";
    public static final String COLUMN_PRIVACY_INDEX = "privacy_index";

    public SQLPostTypes(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    public static final String CREATE_TABLE_POST_TYPES = "CREATE TABLE " + TABLE_POST_TYPES + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TYPE_ID + " INTEGER, "
            + COLUMN_TYPE_NAME + " TEXT, "
            + COLUMN_DISPLAY_NAME + " TEXT, "
            + COLUMN_PRIVACY_INDEX + " INTEGER)";

    public void clearData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(TABLE_POST_TYPES, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public int checkExistData() {
        int count = 0;
        String sql = "SELECT * FROM " + TABLE_POST_TYPES;
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            count = cursor.getCount();
        }
        cursor.close();
        db.close();
        return count;
    }

    public void insertPostType(List<PostType> list) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (PostType item : list) {

                values.put(COLUMN_TYPE_ID, item.id);
                values.put(COLUMN_TYPE_NAME, item.name);
                values.put(COLUMN_PRIVACY_INDEX, item.index);
                values.put(COLUMN_DISPLAY_NAME, item.description);

                db.insert(TABLE_POST_TYPES, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
           if(BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<PostType> getAllPostType() {
        List<PostType> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select * from " + TABLE_POST_TYPES
                + " order by " + COLUMN_PRIVACY_INDEX + " asc";

        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                PostType p = new PostType();
                p.id = c.getInt(c.getColumnIndex(COLUMN_TYPE_ID));
                p.name = c.getString(c.getColumnIndex(COLUMN_TYPE_NAME));
                p.description = c.getString(c.getColumnIndex(COLUMN_DISPLAY_NAME));
                data.add(p);

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }

    public List<PostType> getAllPostType(String[] ids) {
        List<PostType> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String in = TextUtils.join(",", ids);
        String sql = "select * from " + TABLE_POST_TYPES + " where " + COLUMN_TYPE_ID + " in (1,3)"
                + " order by " + COLUMN_PRIVACY_INDEX + " asc";

        Cursor c = db.rawQuery(sql, null);
        try {

            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    PostType p = new PostType();
                    p.id = c.getInt(c.getColumnIndex(COLUMN_TYPE_ID));
                    p.name = c.getString(c.getColumnIndex(COLUMN_TYPE_NAME));
                    p.description = c.getString(c.getColumnIndex(COLUMN_DISPLAY_NAME));
                    data.add(p);

                } while (c.moveToNext());
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
        }finally {
            c.close();
            db.close();
        }
        return data;
    }
}
