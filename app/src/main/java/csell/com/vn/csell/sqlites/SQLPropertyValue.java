package csell.com.vn.csell.sqlites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.models.PropertyValue;

public class SQLPropertyValue {
    public static final String TABLE_PROPERTY_VALUES = "property_values";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_PROPERTY_NAME = "property_name";
    public static final String COLUMN_KEY = "key";
    public static final String COLUMN_VALUE = "value";

    public static final String CREATE_TABLE_PROPERTY_VALUE = "CREATE TABLE " + TABLE_PROPERTY_VALUES + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CATEGORY_ID + " TEXT, "
            + COLUMN_PROPERTY_NAME + " TEXT, "
            + COLUMN_KEY + " TEXT, "
            + COLUMN_VALUE + " TEXT)";

    private SQLDatabaseHelper helper;

    public SQLPropertyValue(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    public int checkExistData() {
        int count = 0;
        String sql = "SELECT * FROM " + TABLE_PROPERTY_VALUES + " LIMIT 3";
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
            db.delete(TABLE_PROPERTY_VALUES, null, null);
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

    public void insertProperty(List<PropertyValue> list) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (PropertyValue item : list) {
                values.put(COLUMN_CATEGORY_ID, item.category_id);
                values.put(COLUMN_PROPERTY_NAME, item.property_name);
                values.put(COLUMN_KEY, item.key);
                values.put(COLUMN_VALUE, item.value);

                db.insert(TABLE_PROPERTY_VALUES, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<PropertyValue> getPropertiesByCatePropName(String cate, String propName) {
        List<PropertyValue> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = null;
        cate = cate.toLowerCase().toString();
        propName = propName.toLowerCase().toString();
        String sql = "select * from " + TABLE_PROPERTY_VALUES + " where " + COLUMN_CATEGORY_ID + " like '%" + cate + "%'"
                + " and " + COLUMN_PROPERTY_NAME + " like '" + propName + "'";
        c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                PropertyValue p = new PropertyValue();
                p.category_id = c.getString(c.getColumnIndex(COLUMN_CATEGORY_ID));
                p.key = c.getString(c.getColumnIndex(COLUMN_KEY));
                p.property_name = c.getString(c.getColumnIndex(COLUMN_PROPERTY_NAME));
                p.value = c.getString(c.getColumnIndex(COLUMN_VALUE));
                data.add(p);

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }
}
