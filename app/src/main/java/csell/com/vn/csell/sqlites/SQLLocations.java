package csell.com.vn.csell.sqlites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.models.Location;

/**
 * Created by cuong.nv on 3/20/2018.
 */

public class SQLLocations {
    private SQLDatabaseHelper helper;

    public static final String TABLE_LOCATIONS = "locations";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LOCATION_ID = "locationid";
    public static final String COLUMN_LOCATION_NAME = "locationname";
    public static final String COLUMN_LOCATION_LEVEL = "level";
    public static final String COLUMN_PARENT_ID = "parentId";
    public static final String COLUMN_LOCATION_PRIORITY = "locationpriority";

    public SQLLocations(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    public static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE " + TABLE_LOCATIONS + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_LOCATION_ID + " INTEGER, "
            + COLUMN_LOCATION_NAME + " TEXT, "
            + COLUMN_LOCATION_LEVEL + " INTEGER, "
            + COLUMN_LOCATION_PRIORITY + " INTEGER, "
            + COLUMN_PARENT_ID + " INTEGER)";

    public void clearData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(TABLE_LOCATIONS, null, null);
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
        String sql = "SELECT * FROM " + TABLE_LOCATIONS + " LIMIT 3";
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            count = cursor.getCount();
        }
        cursor.close();
        db.close();
        return count;
    }


    public void insertLocations(List<Location> list) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Location item : list) {

                values.put(COLUMN_LOCATION_ID, item.getLocation_id());
                values.put(COLUMN_LOCATION_NAME, item.getLocation_name());
                values.put(COLUMN_PARENT_ID, item.getParent_id());
                values.put(COLUMN_LOCATION_LEVEL, item.getLocation_level());
                values.put(COLUMN_LOCATION_PRIORITY, item.getPriority());
                db.insert(TABLE_LOCATIONS, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
           if(BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Location> getAllCity(int level) {
        List<Location> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select * from " + TABLE_LOCATIONS + " where " + COLUMN_LOCATION_LEVEL + " =" + level
                + " order by " + COLUMN_LOCATION_PRIORITY + " desc, " + COLUMN_LOCATION_NAME + " asc";

        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Location p = new Location();
                p.setLocation_id(c.getInt(c.getColumnIndex(COLUMN_LOCATION_ID)));
                p.setLocation_name(c.getString(c.getColumnIndex(COLUMN_LOCATION_NAME)));
                p.setParent_id(c.getInt(c.getColumnIndex(COLUMN_PARENT_ID)));
                p.setLocation_level(c.getInt(c.getColumnIndex(COLUMN_LOCATION_LEVEL)));
                p.setPriority(c.getInt(c.getColumnIndex(COLUMN_LOCATION_PRIORITY)));
                data.add(p);

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }

    public List<Location> getAll() {
        List<Location> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select * from " + TABLE_LOCATIONS
                + " order by " + COLUMN_LOCATION_PRIORITY + " desc, " + COLUMN_LOCATION_NAME + " asc";

        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Location p = new Location();
                p.setLocation_id(c.getInt(c.getColumnIndex(COLUMN_LOCATION_ID)));
                p.setLocation_name(c.getString(c.getColumnIndex(COLUMN_LOCATION_NAME)));
                p.setParent_id(c.getInt(c.getColumnIndex(COLUMN_PARENT_ID)));
                p.setLocation_level(c.getInt(c.getColumnIndex(COLUMN_LOCATION_LEVEL)));
                data.add(p);

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }


    public List<Location> getAllDistrictByCity(int parentId, int level) {
        List<Location> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select * from " + TABLE_LOCATIONS + " where " + COLUMN_PARENT_ID + " =" + parentId
                + " and " + COLUMN_LOCATION_LEVEL + " =" + level
                + " order by " + COLUMN_LOCATION_PRIORITY + " desc, " + COLUMN_LOCATION_NAME + " asc";

        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {

                Location p = new Location();
                p.setLocation_id(c.getInt(c.getColumnIndex(COLUMN_LOCATION_ID)));
                p.setLocation_name(c.getString(c.getColumnIndex(COLUMN_LOCATION_NAME)));
                p.setParent_id(c.getInt(c.getColumnIndex(COLUMN_PARENT_ID)));
                p.setLocation_level(c.getInt(c.getColumnIndex(COLUMN_LOCATION_LEVEL)));
                data.add(p);

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }
}
