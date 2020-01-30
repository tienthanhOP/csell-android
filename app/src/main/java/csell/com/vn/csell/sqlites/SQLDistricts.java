package csell.com.vn.csell.sqlites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.models.District;
import csell.com.vn.csell.models.Location;

public class SQLDistricts {
    public static final String TABLE_DISTRICTS = "districts";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_LOCATIONS = "locations";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PARENT_CODE = "parent_code";

    public static final String CREATE_TABLE_DISTRICTS = "CREATE TABLE " + TABLE_DISTRICTS + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CODE + " TEXT, "
            + COLUMN_LOCATIONS + " TEXT, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_PARENT_CODE + " TEXT)";

    private SQLDatabaseHelper helper;

    public SQLDistricts(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    public void clearData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(TABLE_DISTRICTS, null, null);
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
        String sql = "SELECT * FROM " + TABLE_DISTRICTS + " LIMIT 3";
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            count = cursor.getCount();
        }
        cursor.close();
        db.close();
        return count;
    }

    public void insertDistricts(List<District> list) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (District item : list) {

                values.put(COLUMN_CODE, item.getCode());
                values.put(COLUMN_NAME, item.getName());
                values.put(COLUMN_PARENT_CODE, item.getParentCode());
                values.put(COLUMN_LOCATIONS, new Gson().toJson(item.getLocations()));
                db.insert(TABLE_DISTRICTS, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Location> getDistrictsByCity(int parentCode) {
        List<Location> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String parentId = parentCode + "";
        if (parentCode < 10) {
            parentId = "0" + parentId;
        }
        String sql = "select * from " + TABLE_DISTRICTS + " where " + COLUMN_PARENT_CODE + " = " + "'" + parentId + "'";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Location p = new Location();
                p.setLocation_id(Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_CODE))));
                p.setLocation_name(c.getString(c.getColumnIndex(COLUMN_NAME)));
                p.setParent_id(Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_PARENT_CODE))));
                data.add(p);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }
}
