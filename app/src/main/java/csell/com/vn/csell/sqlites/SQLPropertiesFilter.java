package csell.com.vn.csell.sqlites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.models.PropertiesFilter;
import csell.com.vn.csell.models.PropertyFilterValue;

public class SQLPropertiesFilter {

    private SQLDatabaseHelper helper;

    public static final String TABLE_PROPERTY_FILTER = "propertyFilter";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CATEGORY_ID = "cate_id";
    public static final String COLUMN_INDEX = "index_filter";
    public static final String COLUMN_FILTER_ID = "formality";
    public static final String COLUMN_DISPLAY_NAME = "displayname";
    public static final String COLUMN_GET_BY_KEY = "get_by_key";
    public static final String COLUMN_GET_BY_CHILD = "get_by_child";
    public static final String COLUMN_VIEW_TYPE = "view_type";
    public static final String COLUMN_VALUES = "valuesFilter";

    public static final String CREATE_TABLE_PROPERTY = "CREATE TABLE " + TABLE_PROPERTY_FILTER + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CATEGORY_ID + " TEXT, "
            + COLUMN_INDEX + " INTEGER, "
            + COLUMN_FILTER_ID + " TEXT, "
            + COLUMN_DISPLAY_NAME + " TEXT, "
            + COLUMN_GET_BY_KEY + " TEXT, "
            + COLUMN_GET_BY_CHILD + " TEXT, "
            + COLUMN_VALUES + " TEXT, "
            + COLUMN_VIEW_TYPE + " TEXT)";

    public SQLPropertiesFilter(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    public int checkExistData() {
        int count = 0;
        String sql = "SELECT * FROM " + TABLE_PROPERTY_FILTER + " LIMIT 3";
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
            db.delete(TABLE_PROPERTY_FILTER, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void insertPropertyFilter(List<PropertiesFilter> list) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (PropertiesFilter item : list) {

                values.put(COLUMN_CATEGORY_ID, item.cate_id);
                values.put(COLUMN_INDEX, item.index);
                values.put(COLUMN_FILTER_ID, item.filter_id);
                values.put(COLUMN_DISPLAY_NAME, item.displayname);
                String check = "";
                if (!item.get_by_key) {
                    check = "false";
                } else {
                    check = "true";
                }
                values.put(COLUMN_GET_BY_KEY, check);
                String checkChild = "";
                if (!item.get_by_child) {
                    checkChild = "false";
                } else {
                    checkChild = "true";
                }
                values.put(COLUMN_GET_BY_CHILD, checkChild);
                values.put(COLUMN_VIEW_TYPE, item.view_type);
                String jons = new Gson().toJson(item.values);
                values.put(COLUMN_VALUES, jons);
                db.insert(TABLE_PROPERTY_FILTER, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<PropertiesFilter> getPropertiesByCate(String cate) {
        List<PropertiesFilter> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select * from " + TABLE_PROPERTY_FILTER + " where " + COLUMN_CATEGORY_ID + " like '" + cate + "'"
                + " order by " + COLUMN_INDEX + " asc";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                PropertiesFilter p = new PropertiesFilter();
                p.cate_id = c.getString(c.getColumnIndex(COLUMN_CATEGORY_ID));
                p.index = c.getInt(c.getColumnIndex(COLUMN_INDEX));
                p.filter_id = c.getString(c.getColumnIndex(COLUMN_FILTER_ID));
                p.displayname = c.getString(c.getColumnIndex(COLUMN_DISPLAY_NAME));
                String temp = c.getString(c.getColumnIndex(COLUMN_GET_BY_KEY));
                if (temp.equals("true")) {
                    p.get_by_key = true;
                }
                String temp2 = c.getString(c.getColumnIndex(COLUMN_GET_BY_CHILD));
                if (temp2.equals("true")) {
                    p.get_by_child = true;
                }

                p.view_type = c.getString(c.getColumnIndex(COLUMN_VIEW_TYPE));
                p.values = new Gson().fromJson(c.getString(c.getColumnIndex(COLUMN_VALUES)),
                        new TypeToken<List<PropertyFilterValue>>() {
                        }.getType());

                data.add(p);

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }
}
