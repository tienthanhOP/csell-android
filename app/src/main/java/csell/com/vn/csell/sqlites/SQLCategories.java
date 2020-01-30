package csell.com.vn.csell.sqlites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.models.Category;


public class SQLCategories {

    private SQLDatabaseHelper helper;

    public static final String TABLE_CATEGORY = "category";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CATEGORY_ID = "categoryid";
    public static final String COLUMN_CATEGORY_LEVEL = "categorylevel";
    public static final String COLUMN_CATEGORY_NAME = "categoryname";
    public static final String COLUMN_CATEGORY_IMAGE = "categoryimage";
    public static final String COLUMN_CATEGORY_IMAGE_WEB = "categoryimage_web";
    public static final String COLUMN_DATE_CREATED = "datecreated";
    public static final String COLUMN_MAX_LEVEL = "maxlevel";
    public static final String COLUMN_PRIORITY = "priority_car";
    public static final String COLUMN_BACKGROUND = "background";
    public static final String COLUMN_IMAGE_SUB = "image_sub";

    public static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORY + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CATEGORY_ID + " TEXT , "
            + COLUMN_CATEGORY_LEVEL + " INTEGER, "
            + COLUMN_MAX_LEVEL + " INTEGER, "
            + COLUMN_PRIORITY + " INTEGER, "
            + COLUMN_CATEGORY_NAME + " TEXT, "
            + COLUMN_DATE_CREATED + " TEXT, "
            + COLUMN_CATEGORY_IMAGE_WEB + " TEXT, "
            + COLUMN_CATEGORY_IMAGE + " TEXT,"
            + COLUMN_IMAGE_SUB + " TEXT,"
            + COLUMN_BACKGROUND + " TEXT)";

    public SQLCategories(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    public int checkExistData() {
        int count = 0;
        String sql = "SELECT * FROM " + TABLE_CATEGORY + " LIMIT 2";
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
            db.delete(TABLE_CATEGORY, null, null);
            db.close();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            db.close();
        }
    }

    public int getMaxLevel(String cate) {
        int max = 0;

        String sql = "SELECT " + COLUMN_MAX_LEVEL + " FROM " + TABLE_CATEGORY + " WHERE " + COLUMN_CATEGORY_ID + " like '" + cate.toLowerCase().toString() + "'";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            max = cursor.getInt(cursor.getColumnIndex(COLUMN_MAX_LEVEL));
        }
        cursor.close();

        return max;
    }

    public ArrayList<Category> getAll() {
        ArrayList<Category> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = null;
        String sql = "select * from " + TABLE_CATEGORY;
        c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Category category = new Category();
                category.category_id = c.getString(c.getColumnIndex(COLUMN_CATEGORY_ID));
                category.category_name = c.getString(c.getColumnIndex(COLUMN_CATEGORY_NAME));
                category.level = c.getInt(c.getColumnIndex(COLUMN_CATEGORY_LEVEL));
                category.image = c.getString(c.getColumnIndex(COLUMN_CATEGORY_IMAGE));
                category.image_web = c.getString(c.getColumnIndex(COLUMN_CATEGORY_IMAGE_WEB));
                category.max_level = c.getInt(c.getColumnIndex(COLUMN_MAX_LEVEL));
                category.priority = c.getInt(c.getColumnIndex(COLUMN_PRIORITY));
                category.background = c.getString(c.getColumnIndex(COLUMN_BACKGROUND));
                category.image_sub = c.getString(c.getColumnIndex(COLUMN_IMAGE_SUB));
                data.add(category);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }

    public ArrayList<Category> getListCategoryByCat(String categoryId) {
        ArrayList<Category> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = null;
        try {

            if (!TextUtils.isEmpty(categoryId)) {

                String sql = "SELECT * FROM " + TABLE_CATEGORY
                        + " WHERE "
                        + COLUMN_CATEGORY_ID + " like '" + categoryId + "_" + "%'"
                        + " ORDER BY " + COLUMN_CATEGORY_NAME + " asc";
                c = db.rawQuery(sql, null);
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    do {
                        Category category = new Category();
                        category.category_id = c.getString(c.getColumnIndex(COLUMN_CATEGORY_ID));
                        category.category_name = c.getString(c.getColumnIndex(COLUMN_CATEGORY_NAME));
                        category.level = c.getInt(c.getColumnIndex(COLUMN_CATEGORY_LEVEL));
                        category.image = c.getString(c.getColumnIndex(COLUMN_CATEGORY_IMAGE));
                        category.image_sub = c.getString(c.getColumnIndex(COLUMN_IMAGE_SUB));
                        category.image_web = c.getString(c.getColumnIndex(COLUMN_CATEGORY_IMAGE_WEB));
                        category.max_level = c.getInt(c.getColumnIndex(COLUMN_MAX_LEVEL));
                        category.background = c.getString(c.getColumnIndex(COLUMN_BACKGROUND));
                        data.add(category);
                    } while (c.moveToNext());
                }
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("XXX" + getClass().getName(), e.getMessage());
        } finally {
            c.close();
            db.close();
        }
        return data;
    }

    public ArrayList<Category> getListCategoryByLevel(String categoryId, int level) {
        ArrayList<Category> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = null;
        try {

            if (TextUtils.isEmpty(categoryId)) {

                String sql = "SELECT * FROM " + TABLE_CATEGORY
                        + " WHERE "
                        + COLUMN_CATEGORY_LEVEL + "=" + level
                        + " order by " + COLUMN_PRIORITY + " desc, " + COLUMN_CATEGORY_NAME + " asc";
                c = db.rawQuery(sql, null);
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    do {
                        Category category = new Category();
                        category.category_id = c.getString(c.getColumnIndex(COLUMN_CATEGORY_ID));
                        category.category_name = c.getString(c.getColumnIndex(COLUMN_CATEGORY_NAME));
                        category.level = c.getInt(c.getColumnIndex(COLUMN_CATEGORY_LEVEL));
                        category.image = c.getString(c.getColumnIndex(COLUMN_CATEGORY_IMAGE));
                        category.image_sub = c.getString(c.getColumnIndex(COLUMN_IMAGE_SUB));
                        category.image_web = c.getString(c.getColumnIndex(COLUMN_CATEGORY_IMAGE_WEB));
                        category.max_level = c.getInt(c.getColumnIndex(COLUMN_MAX_LEVEL));
                        category.background = c.getString(c.getColumnIndex(COLUMN_BACKGROUND));
                        data.add(category);
                    } while (c.moveToNext());
                }
            } else {

                String sql = "SELECT * FROM " + TABLE_CATEGORY
                        + " WHERE "
                        + COLUMN_CATEGORY_ID + " like '" + categoryId + "%' "
                        + " and "
                        + COLUMN_CATEGORY_LEVEL + "=" + level
                        + " order by " + COLUMN_PRIORITY + " desc, " + COLUMN_CATEGORY_NAME + " asc";

                c = db.rawQuery(sql, null);

                if (c.getCount() > 0) {
                    c.moveToFirst();
                    do {
                        Category category = new Category();
                        category.category_id = c.getString(c.getColumnIndex(COLUMN_CATEGORY_ID));
                        category.category_name = c.getString(c.getColumnIndex(COLUMN_CATEGORY_NAME));
                        category.level = c.getInt(c.getColumnIndex(COLUMN_CATEGORY_LEVEL));
                        category.image = c.getString(c.getColumnIndex(COLUMN_CATEGORY_IMAGE));
                        category.image_sub = c.getString(c.getColumnIndex(COLUMN_IMAGE_SUB));
                        category.image_web = c.getString(c.getColumnIndex(COLUMN_CATEGORY_IMAGE_WEB));
                        category.max_level = c.getInt(c.getColumnIndex(COLUMN_MAX_LEVEL));
                        category.background = c.getString(c.getColumnIndex(COLUMN_BACKGROUND));
                        data.add(category);
                    } while (c.moveToNext());
                }
            }


        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            c.close();
            db.close();
        }
        return data;
    }

    public Category getCategoryById(String categoryId) {
        Category category = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        String where = COLUMN_CATEGORY_ID + "=?";
        String[] whereAgrs = {String.valueOf(categoryId)};
        Cursor c = null;
        try {
            c = db.query(TABLE_CATEGORY, null, where, whereAgrs, null, null, null, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                category = new Category();
                category.category_id = c.getString(c.getColumnIndex(COLUMN_CATEGORY_ID));
                category.category_name = c.getString(c.getColumnIndex(COLUMN_CATEGORY_NAME));
                category.level = c.getInt(c.getColumnIndex(COLUMN_CATEGORY_LEVEL));
                category.image = c.getString(c.getColumnIndex(COLUMN_CATEGORY_IMAGE));
                category.image_sub = c.getString(c.getColumnIndex(COLUMN_IMAGE_SUB));
                category.image_web = c.getString(c.getColumnIndex(COLUMN_CATEGORY_IMAGE_WEB));
                category.max_level = c.getInt(c.getColumnIndex(COLUMN_MAX_LEVEL));
                category.background = c.getString(c.getColumnIndex(COLUMN_BACKGROUND));
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            assert c != null;
            c.close();
            db.close();
        }

        return category;
    }

    public void insertCategories(List<Category> list) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Category item : list) {

                values.put(COLUMN_CATEGORY_ID, item.category_id);
                values.put(COLUMN_CATEGORY_NAME, item.category_name);
                values.put(COLUMN_CATEGORY_LEVEL, item.level);
                values.put(COLUMN_CATEGORY_IMAGE, item.image);
                values.put(COLUMN_IMAGE_SUB, item.image_sub);
                values.put(COLUMN_CATEGORY_IMAGE_WEB, item.image_web);
                values.put(COLUMN_MAX_LEVEL, item.max_level);
                values.put(COLUMN_PRIORITY, item.priority);
                values.put(COLUMN_BACKGROUND, item.background);
                db.insert(TABLE_CATEGORY, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public Category getCategoryByName(String categoryName) {
        Category category = new Category();
        SQLiteDatabase db = helper.getReadableDatabase();
        String where = COLUMN_CATEGORY_NAME + "=?";
        String[] whereAgrs = {String.valueOf(categoryName)};
        Cursor c = null;
        try {
            c = db.query(TABLE_CATEGORY, null, where, whereAgrs, null, null, null, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                category = new Category();
                category.category_id = c.getString(c.getColumnIndex(COLUMN_CATEGORY_ID));
                category.category_name = c.getString(c.getColumnIndex(COLUMN_CATEGORY_NAME));
                category.level = c.getInt(c.getColumnIndex(COLUMN_CATEGORY_LEVEL));
                category.image = c.getString(c.getColumnIndex(COLUMN_CATEGORY_IMAGE));
                category.image_sub = c.getString(c.getColumnIndex(COLUMN_IMAGE_SUB));
                category.image_web = c.getString(c.getColumnIndex(COLUMN_CATEGORY_IMAGE_WEB));
                category.max_level = c.getInt(c.getColumnIndex(COLUMN_MAX_LEVEL));
                category.background = c.getString(c.getColumnIndex(COLUMN_BACKGROUND));
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            assert c != null;
            c.close();
            db.close();
        }

        return category;
    }

    public String getCategoryName(String root, String subCat) {
        Category category = new Category();
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select * from " + TABLE_CATEGORY + " where "
                + COLUMN_CATEGORY_ID + " like '%"
                + subCat + "%'";
        Cursor c = null;
        try {
            c = db.rawQuery(sql, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {

                    category.category_id = c.getString(c.getColumnIndex(COLUMN_CATEGORY_ID));
                    category.category_name = c.getString(c.getColumnIndex(COLUMN_CATEGORY_NAME));
                    category.level = c.getInt(c.getColumnIndex(COLUMN_CATEGORY_LEVEL));
                    category.image = c.getString(c.getColumnIndex(COLUMN_CATEGORY_IMAGE));
                    category.image_sub = c.getString(c.getColumnIndex(COLUMN_IMAGE_SUB));
                    category.max_level = c.getInt(c.getColumnIndex(COLUMN_MAX_LEVEL));
                    category.image_web = c.getString(c.getColumnIndex(COLUMN_CATEGORY_IMAGE_WEB));
                    category.background = c.getString(c.getColumnIndex(COLUMN_BACKGROUND));
                    if (category.category_id.startsWith(root)) {
                        break;
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            assert c != null;
            c.close();
            db.close();
        }

        return category.category_name;
    }

    public Category getCategoryByIdAndLevel(String categoryId, int level) {
        Category category = null;
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = null;

        String sql = "SELECT * FROM " + TABLE_CATEGORY
                + " WHERE "
                + COLUMN_CATEGORY_ID + " like '%" + categoryId + "%' "
                + " and "
                + COLUMN_CATEGORY_LEVEL + "=" + level
                + " order by " + COLUMN_PRIORITY + " desc, " + COLUMN_CATEGORY_NAME + " asc";

        c = db.rawQuery(sql, null);

        try {
            if (c.getCount() > 0) {
                c.moveToFirst();
                category = new Category();
                category.category_id = c.getString(c.getColumnIndex(COLUMN_CATEGORY_ID));
                category.category_name = c.getString(c.getColumnIndex(COLUMN_CATEGORY_NAME));
                category.level = c.getInt(c.getColumnIndex(COLUMN_CATEGORY_LEVEL));
                category.image = c.getString(c.getColumnIndex(COLUMN_CATEGORY_IMAGE));
                category.image_sub = c.getString(c.getColumnIndex(COLUMN_IMAGE_SUB));
                category.image_web = c.getString(c.getColumnIndex(COLUMN_CATEGORY_IMAGE_WEB));
                category.max_level = c.getInt(c.getColumnIndex(COLUMN_MAX_LEVEL));
                category.background = c.getString(c.getColumnIndex(COLUMN_BACKGROUND));
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            assert c != null;
            c.close();
            db.close();
        }

        return category;
    }

}
