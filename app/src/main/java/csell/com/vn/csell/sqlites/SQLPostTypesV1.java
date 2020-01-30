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
import csell.com.vn.csell.models.PostTypeV1;

/**
 * Created by cuong.nv on 4/20/2018.
 */

public class SQLPostTypesV1 {

    public static final String TABLE_POST_TYPES = "privacy_types";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE_ID = "privacy_id";
    public static final String COLUMN_TYPE_NAME = "privacy_name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_PRIVACY_INDEX = "privacy_index";
    public static final String CREATE_TABLE_POST_TYPES = "CREATE TABLE " + TABLE_POST_TYPES + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TYPE_ID + " INTEGER, "
            + COLUMN_TYPE_NAME + " TEXT, "
            + COLUMN_DESCRIPTION + " TEXT, "
            + COLUMN_PRIVACY_INDEX + " INTEGER)";
    private SQLDatabaseHelper helper;

    public SQLPostTypesV1(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    public void clearData() {
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            db.delete(TABLE_POST_TYPES, null, null);
        } catch (Exception e) {
            e.printStackTrace();
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

    public void insertPostType(List<PostTypeV1> list) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (PostTypeV1 item : list) {

                values.put(COLUMN_TYPE_ID, item.getId());
                values.put(COLUMN_TYPE_NAME, item.getName());
                values.put(COLUMN_PRIVACY_INDEX, item.getIndex());
                values.put(COLUMN_DESCRIPTION, item.getDescription());

                db.insert(TABLE_POST_TYPES, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            db.endTransaction();
//            db.close();
        }
    }

    public List<PostTypeV1> getAllPostType() {
        List<PostTypeV1> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select * from " + TABLE_POST_TYPES
                + " order by " + COLUMN_PRIVACY_INDEX + " asc";

        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                PostTypeV1 p = new PostTypeV1();
                p.setId(c.getInt(c.getColumnIndex(COLUMN_TYPE_ID)));
                p.setName(c.getString(c.getColumnIndex(COLUMN_TYPE_NAME)));
                p.setDescription(c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)));
                p.setIndex(c.getInt(c.getColumnIndex(COLUMN_PRIVACY_INDEX)));
                data.add(p);

            } while (c.moveToNext());
        }
        c.close();
//        db.close();
        return data;
    }

    public List<PostType> getAllPostTypeIn(String[] ids) {
        List<PostType> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String in = TextUtils.join(",", ids);
        String sql = "select * from " + TABLE_POST_TYPES + " where " + COLUMN_TYPE_ID + " in (" + in + ")"
                + " order by " + COLUMN_PRIVACY_INDEX + " asc";

        Cursor c = db.rawQuery(sql, null);
        try {

            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    PostType p = new PostType();
                    p.id = c.getInt(c.getColumnIndex(COLUMN_TYPE_ID));
                    p.name = c.getString(c.getColumnIndex(COLUMN_TYPE_NAME));
                    p.description = c.getString(c.getColumnIndex(COLUMN_DESCRIPTION));
                    data.add(p);

                } while (c.moveToNext());
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
        } finally {
            c.close();
            db.close();
        }
        return data;
    }
}
