package csell.com.vn.csell.sqlites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.models.Location;
import csell.com.vn.csell.models.Street;
import csell.com.vn.csell.models.StreetInfo;

public class SQLStreets {
    public static final String TABLE_STREETS = "streets";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_LOCATIONS = "streets";

    public static final String CREATE_TABLE_STREETS = "CREATE TABLE " + TABLE_STREETS + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CODE + " TEXT, "
            + COLUMN_LOCATIONS + " TEXT)";

    private SQLDatabaseHelper helper;

    public SQLStreets(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    public void clearData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(TABLE_STREETS, null, null);
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
        String sql = "SELECT * FROM " + TABLE_STREETS + " LIMIT 3";
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            count = cursor.getCount();
        }
        cursor.close();
        db.close();
        return count;
    }

    public void insertStreets(List<Street> list) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Street item : list) {

                values.put(COLUMN_CODE, item.getCode());
                values.put(COLUMN_LOCATIONS, new Gson().toJson(item.getStreets()));
                db.insert(TABLE_STREETS, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Location> getStreetsByDistrict(int parentCode) {
        String json = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        String parentId = parentCode + "";
        if (parentCode < 10) {
            parentId = "00" + parentId;
        } else if (parentCode < 100) {
            parentId = "0" + parentId;
        }
        String sql = "select * from " + TABLE_STREETS + " where " + COLUMN_CODE + " = " + "'" + parentId + "'";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            json = c.getString(c.getColumnIndex(COLUMN_LOCATIONS));
        }
        c.close();
        db.close();

        List<StreetInfo> data;
        List<Location> locations = new ArrayList<>();

        try {
            Gson gson2 = new Gson();
            Type listType = new TypeToken<List<StreetInfo>>() {
            }.getType();

            data = gson2.fromJson(json, listType);
            for (int i = 0;i<data.size();i++){
                Location location = new Location();
                location.setLocation_name(data.get(i).getName());
                location.setLocation_id(Integer.parseInt(data.get(i).getCode()));
                locations.add(location);
            }
        } catch (JsonSyntaxException e) {
            if (BuildConfig.DEBUG) Log.d("ABCD", "" + e.getMessage());
        }
        return locations;
    }
}
