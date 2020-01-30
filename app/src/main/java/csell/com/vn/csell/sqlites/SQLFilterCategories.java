package csell.com.vn.csell.sqlites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.models.FilterCategories;

public class SQLFilterCategories {
    public static final String TABLE_FILTER_CATEGORIES = "filter_categories";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_CATEGORY_NAME = "category_name";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_IMAGE_WEB = "image_web";
    public static final String COLUMN_BACKGROUND = "background";
    public static final String COLUMN_LEVEL = "level";
    public static final String COLUMN_LEVEL_MAX = "max_level";
    public static final String COLUMN_PROPERTY = "priority";

    public static final String CREATE_TABLE_FILTER_CATEGORIES = "CREATE TABLE " + TABLE_FILTER_CATEGORIES + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CATEGORY_ID + " TEXT, "
            + COLUMN_CATEGORY_NAME + " TEXT, "
            + COLUMN_IMAGE + " TEXT, "
            + COLUMN_IMAGE_WEB + " TEXT, "
            + COLUMN_BACKGROUND + " TEXT, "
            + COLUMN_LEVEL + " INTEGER, "
            + COLUMN_LEVEL_MAX + " INTEGER, "
            + COLUMN_PROPERTY + " INTEGER)";

    private SQLDatabaseHelper helper;

    public SQLFilterCategories(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    public int checkExistData() {
        int count = 0;
        String sql = "SELECT * FROM " + TABLE_FILTER_CATEGORIES + " LIMIT 3";
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
            db.delete(TABLE_FILTER_CATEGORIES, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertFilterCategories(List<FilterCategories> list) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (FilterCategories item : list) {

                values.put(COLUMN_CATEGORY_ID, item.getCategoryId());
                values.put(COLUMN_CATEGORY_NAME, item.getCategoryName());
                values.put(COLUMN_IMAGE, item.getImage());
                values.put(COLUMN_IMAGE_WEB, item.getImageWeb());
                values.put(COLUMN_BACKGROUND, item.getBackground());
                values.put(COLUMN_LEVEL, item.getLevel());
                values.put(COLUMN_LEVEL_MAX, item.getMaxLevel());
                values.put(COLUMN_PROPERTY, item.getPriority());

                db.insert(TABLE_FILTER_CATEGORIES, null, values);
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
