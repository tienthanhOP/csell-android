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

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.models.ProductCountResponse;

/**
 * Created by cuong.nv on 4/17/2018.
 */

public class SQLCacheProduct {

    private SQLDatabaseHelper helper;

    public static final String TABLE_CACHE_PRODUCT = "cacheproduct";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CATEGORY_ID = "categoryid";
    public static final String COLUMN_DATA_JSON = "datajson";

    public static final String CREATE_TABLE_PRODUCT_COUNT = "CREATE TABLE " + TABLE_CACHE_PRODUCT + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CATEGORY_ID + " TEXT , "
            + COLUMN_DATA_JSON + " TEXT)";

    public SQLCacheProduct(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    public int checkExistData() {
        int count = 0;
        String sql = "SELECT * FROM " + TABLE_CACHE_PRODUCT;
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
            db.delete(TABLE_CACHE_PRODUCT, null, null);
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

    public void insertCache(String catId, String data) {
        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransaction();
        Cursor cursor = null;
        String sqlQuery = "select * from " + TABLE_CACHE_PRODUCT + "  WHERE " + COLUMN_CATEGORY_ID + " like '" + catId + "' ";
        cursor = db.rawQuery(sqlQuery, null);
        try {

            if (cursor.getCount() > 0) { //check data exist, if exist update then update
                cursor.moveToFirst();

                ContentValues values = new ContentValues();
                values.put(COLUMN_CATEGORY_ID, catId);
                values.put(COLUMN_DATA_JSON, data);

                db.update(TABLE_CACHE_PRODUCT, values, COLUMN_CATEGORY_ID + " like '" + catId + "'", null);

            } else {
                ContentValues values = new ContentValues();

                values.put(COLUMN_CATEGORY_ID, catId);
                values.put(COLUMN_DATA_JSON, data);

                db.insert(TABLE_CACHE_PRODUCT, null, values);

                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            cursor.close();
            db.endTransaction();
            db.close();
        }
    }

    public void deleteCacheByCatId(String catId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE_CACHE_PRODUCT, COLUMN_CATEGORY_ID + " like '" + catId + "'", null);

        db.close();
    }


    public String getDataByCatId(String catId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        String data = "";

        String selectQuery = "select " + COLUMN_DATA_JSON
                + " from " + TABLE_CACHE_PRODUCT
                + " where " + COLUMN_CATEGORY_ID + " like '" + catId + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    data = cursor.getString(cursor.getColumnIndex(COLUMN_DATA_JSON));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("error: ", "cache_product: " + e.toString());
        } finally {
            cursor.close();
            db.close();
        }

        return data;
    }

    public List<ProductCountResponse> getDataByCat(String catId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        List<ProductCountResponse> data = new ArrayList<>();

        String selectQuery = "select " + COLUMN_DATA_JSON
                + " from " + TABLE_CACHE_PRODUCT
                + " where " + COLUMN_CATEGORY_ID + " like '" + catId + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    data.addAll(new Gson().fromJson(cursor.getString(cursor.getColumnIndex(COLUMN_DATA_JSON)),
                            new TypeToken<List<ProductCountResponse>>() {
                            }.getType()));

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("error: ", "cache_product: " + e.toString());
        } finally {
            cursor.close();
            db.close();
        }

        return data;

    }
}
