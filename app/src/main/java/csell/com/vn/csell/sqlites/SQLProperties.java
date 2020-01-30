package csell.com.vn.csell.sqlites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.models.Properties;

/**
 * Created by cuong.nv on 4/11/2018.
 */

public class SQLProperties {

    private SQLDatabaseHelper helper;

    public static final String TABLE_PROPERTY = "property";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CATEGORY_ID = "categoryid";
    public static final String COLUMN_DISPLAYNAME = "displayname";
    public static final String COLUMN_PROPERTY_PRIMARY = "primaryprop";
    public static final String COLUMN_PROPERTY_NAME = "propname";
    public static final String COLUMN_PROPERTY_TYPE = "type";
    public static final String COLUMN_ACTION = "action";
    public static final String COLUMN_IMAGE = "propImage";
    public static final String COLUMN_INDEX = "indexproperty";

    public static final String CREATE_TABLE_PROPERTY = "CREATE TABLE " + TABLE_PROPERTY + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CATEGORY_ID + " TEXT, "
            + COLUMN_DISPLAYNAME + " INTEGER, "
            + COLUMN_PROPERTY_PRIMARY + " INTEGER, "
            + COLUMN_PROPERTY_NAME + " TEXT, "
            + COLUMN_ACTION + " TEXT, "
            + COLUMN_IMAGE + " TEXT, "
            + COLUMN_INDEX + " INTEGER, "
            + COLUMN_PROPERTY_TYPE + " TEXT)";

    public SQLProperties(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    public int checkExistData() {
        int count = 0;
        String sql = "SELECT * FROM " + TABLE_PROPERTY + " LIMIT 3";
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            count = cursor.getCount();
        }
        cursor.close();
        db.close();
        return count;
    }


    public void clearData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(TABLE_PROPERTY, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void insertProperty(List<Properties> list) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Properties item : list) {

                values.put(COLUMN_CATEGORY_ID, item.category_id);
                values.put(COLUMN_DISPLAYNAME, item.display_name);
                values.put(COLUMN_PROPERTY_NAME, item.property_name);
                values.put(COLUMN_PROPERTY_TYPE, item.type);
                values.put(COLUMN_ACTION, item.action);
                values.put(COLUMN_PROPERTY_PRIMARY, item.primary);
                values.put(COLUMN_INDEX, item.index);
                values.put(COLUMN_IMAGE, item.image);

                db.insert(TABLE_PROPERTY, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
           if(BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

// get full properties for edit product
    public List<Properties> getAllPropertyById(String categoryId) {
        List<Properties> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select * from " + TABLE_PROPERTY + " where " + COLUMN_CATEGORY_ID + " like '%" + categoryId + "%'"
                + " order by " + COLUMN_INDEX + " asc";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Properties p = new Properties();
                p.category_id = c.getString(c.getColumnIndex(COLUMN_CATEGORY_ID));
                p.display_name = c.getString(c.getColumnIndex(COLUMN_DISPLAYNAME));
                p.property_name = c.getString(c.getColumnIndex(COLUMN_PROPERTY_NAME));
                p.type = c.getString(c.getColumnIndex(COLUMN_PROPERTY_TYPE));
                p.action = c.getString(c.getColumnIndex(COLUMN_ACTION));
                p.index = c.getInt(c.getColumnIndex(COLUMN_INDEX));
                p.primary = c.getInt(c.getColumnIndex(COLUMN_PROPERTY_PRIMARY));
                p.image = c.getString(c.getColumnIndex(COLUMN_IMAGE));
                data.add(p);

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }

//    public List<Properties> getAllPropertyById(String categoryId) {
//        List<Properties> data = new ArrayList<>();
//        SQLiteDatabase db = helper.getReadableDatabase();
//
//        String sql = "select * from " + TABLE_PROPERTY + " where " + COLUMN_CATEGORY_ID + " like '%" + categoryId + "%'"
//                + " order by " + COLUMN_INDEX + " asc";
//        Cursor c = db.rawQuery(sql, null);
//        if (c.getCount() > 0) {
//            c.moveToFirst();
//            do {
//                Properties p = new Properties();
//                p.category_id = c.getString(c.getColumnIndex(COLUMN_CATEGORY_ID));
//                p.display_name = c.getString(c.getColumnIndex(COLUMN_DISPLAY_NAME));
//                p.property_name = c.getString(c.getColumnIndex(COLUMN_PROPERTY_NAME));
//                p.type = c.getString(c.getColumnIndex(COLUMN_PROPERTY_TYPE));
//                p.action = c.getString(c.getColumnIndex(COLUMN_ACTION));
//                p.index = c.getInt(c.getColumnIndex(COLUMN_INDEX));
//                p.primary = c.getInt(c.getColumnIndex(COLUMN_PROPERTY_PRIMARY));
//                p.image = c.getString(c.getColumnIndex(COLUMN_IMAGE));
//                data.add(p);
//
//            } while (c.moveToNext());
//        }
//        c.close();
//        db.close();
//        return data;
//    }

    public List<Properties> getAll() {
        List<Properties> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select * from " + TABLE_PROPERTY;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Properties p = new Properties();
                p.category_id = c.getString(c.getColumnIndex(COLUMN_CATEGORY_ID));
                p.display_name = c.getString(c.getColumnIndex(COLUMN_DISPLAYNAME));
                p.property_name = c.getString(c.getColumnIndex(COLUMN_PROPERTY_NAME));
                p.type = c.getString(c.getColumnIndex(COLUMN_PROPERTY_TYPE));
                p.action = c.getString(c.getColumnIndex(COLUMN_ACTION));
                p.index = c.getInt(c.getColumnIndex(COLUMN_INDEX));
                p.primary = c.getInt(c.getColumnIndex(COLUMN_PROPERTY_PRIMARY));
                p.image = c.getString(c.getColumnIndex(COLUMN_IMAGE));
                data.add(p);

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }

    public List<Properties> getPropertiesByCate(String cate) {
        List<Properties> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select * from " + TABLE_PROPERTY + " where " + COLUMN_CATEGORY_ID + " like '%" + cate + "%'"
                + " order by " + COLUMN_PROPERTY_PRIMARY + " asc," + COLUMN_INDEX + " asc";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Properties p = new Properties();
                p.category_id = c.getString(c.getColumnIndex(COLUMN_CATEGORY_ID));
                p.display_name = c.getString(c.getColumnIndex(COLUMN_DISPLAYNAME));
                p.property_name = c.getString(c.getColumnIndex(COLUMN_PROPERTY_NAME));
                p.type = c.getString(c.getColumnIndex(COLUMN_PROPERTY_TYPE));
                p.action = c.getString(c.getColumnIndex(COLUMN_ACTION));
                p.index = c.getInt(c.getColumnIndex(COLUMN_INDEX));
                p.primary = c.getInt(c.getColumnIndex(COLUMN_PROPERTY_PRIMARY));
                p.image = c.getString(c.getColumnIndex(COLUMN_IMAGE));
                data.add(p);

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }

//    get all properties for moreinfo fragment primary == 3
    public List<Properties> getPropertiesByCate(String cate, int primary) {
        List<Properties> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select * from " + TABLE_PROPERTY + " where " + COLUMN_CATEGORY_ID + " like '%" + cate + "%'"
                + " and " + COLUMN_PROPERTY_PRIMARY + "=" + primary
                + " order by " + COLUMN_INDEX + " asc";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Properties p = new Properties();
                p.category_id = c.getString(c.getColumnIndex(COLUMN_CATEGORY_ID));
                p.display_name = c.getString(c.getColumnIndex(COLUMN_DISPLAYNAME));
                p.property_name = c.getString(c.getColumnIndex(COLUMN_PROPERTY_NAME));
                p.type = c.getString(c.getColumnIndex(COLUMN_PROPERTY_TYPE));
                p.action = c.getString(c.getColumnIndex(COLUMN_ACTION));
                p.index = c.getInt(c.getColumnIndex(COLUMN_INDEX));
                p.primary = c.getInt(c.getColumnIndex(COLUMN_PROPERTY_PRIMARY));
                p.image = c.getString(c.getColumnIndex(COLUMN_IMAGE));
                data.add(p);

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }

    public List<Properties> getAllPropertiesDetail(String cate, int primary) {
        List<Properties> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select * from " + TABLE_PROPERTY + " where " + COLUMN_CATEGORY_ID + " like '%" + cate + "%'"
                + " and " + COLUMN_PROPERTY_PRIMARY + "<=" + primary
                + " order by " + COLUMN_INDEX + " asc";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Properties p = new Properties();
                p.category_id = c.getString(c.getColumnIndex(COLUMN_CATEGORY_ID));
                p.display_name = c.getString(c.getColumnIndex(COLUMN_DISPLAYNAME));
                p.property_name = c.getString(c.getColumnIndex(COLUMN_PROPERTY_NAME));
                p.type = c.getString(c.getColumnIndex(COLUMN_PROPERTY_TYPE));
                p.action = c.getString(c.getColumnIndex(COLUMN_ACTION));
                p.index = c.getInt(c.getColumnIndex(COLUMN_INDEX));
                p.primary = c.getInt(c.getColumnIndex(COLUMN_PROPERTY_PRIMARY));
                p.image = c.getString(c.getColumnIndex(COLUMN_IMAGE));
                data.add(p);

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }
}
