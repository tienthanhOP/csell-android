package csell.com.vn.csell.sqlites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.models.PropertiesV1;

public class SQLPropertiesV1 {
    public static final String TABLE_PROPERTIES = "properties";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ACTION = "action";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_DISPLAY_NAME = "display_name";
    public static final String COLUMN_PROPERTY_PRIMARY = "primary";
    public static final String COLUMN_PROPERTY_NAME = "property_name";
    public static final String COLUMN_PROPERTY_TYPE = "type";
    public static final String COLUMN_PROPERTY_VALUES = "property_default_values";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_INDEX = "index";

    public static final String CREATE_TABLE_PROPERTIES = "CREATE TABLE " + TABLE_PROPERTIES + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_ACTION + " TEXT, "
            + COLUMN_CATEGORY_ID + " TEXT, "
            + COLUMN_DISPLAY_NAME + " TEXT, "
            + COLUMN_PROPERTY_PRIMARY + " INTEGER, "
            + COLUMN_PROPERTY_NAME + " TEXT, "
            + COLUMN_PROPERTY_VALUES + " TEXT, "
            + COLUMN_IMAGE + " TEXT, "
            + COLUMN_INDEX + " INTEGER, "
            + COLUMN_PROPERTY_TYPE + " TEXT)";

    private SQLDatabaseHelper helper;

    public SQLPropertiesV1(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    public int checkExistData() {
        int count = 0;
        String sql = "SELECT * FROM " + TABLE_PROPERTIES + " LIMIT 3";
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
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            db.delete(TABLE_PROPERTIES, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertProperties(List<PropertiesV1> list) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (PropertiesV1 item : list) {

                values.put(COLUMN_ACTION, item.getAction());
                values.put(COLUMN_CATEGORY_ID, item.getCategoryId());
                values.put(COLUMN_DISPLAY_NAME, item.getDisplayName());
                values.put(COLUMN_PROPERTY_NAME, item.getPropertyName());
                values.put(COLUMN_PROPERTY_TYPE, item.getType());
                values.put(COLUMN_PROPERTY_PRIMARY, item.getPrimary());
                values.put(COLUMN_PROPERTY_VALUES, new Gson().toJson(item.getPropertyDefaultValues()));
                values.put(COLUMN_INDEX, item.getIndex());
                values.put(COLUMN_IMAGE, item.getImage());

                db.insert(TABLE_PROPERTIES, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}
