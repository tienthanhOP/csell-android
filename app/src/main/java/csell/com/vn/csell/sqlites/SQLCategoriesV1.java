package csell.com.vn.csell.sqlites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.models.CategoryV1;
import csell.com.vn.csell.models.ItemCategory;


public class SQLCategoriesV1 {
    public static final String TABLE_CATEGORIES = "categories";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_PARENT_ID = "parent_id";
    public static final String COLUMN_PARENT_NAME = "parent_name";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_LOGO = "logo";
    public static final String COLUMN_POSITION = "position";
    public static final String COLUMN_CATEGORY_LEVEL = "group_level";
    public static final String COLUMN_IS_ACTIVE = "is_active";
    public static final String COLUMN_CATEGORY_NAME = "name";
    public static final String COLUMN_CREATED_ID = "created_by_id";
    public static final String COLUMN_CREATED_NAME = "created_by_name";

    public static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CATEGORY_ID + " TEXT , "
            + COLUMN_PARENT_ID + " TEXT , "
            + COLUMN_PARENT_NAME + " TEXT , "
            + COLUMN_NOTE + " TEXT , "
            + COLUMN_LOGO + " TEXT , "
            + COLUMN_POSITION + " INTEGER, "
            + COLUMN_CATEGORY_LEVEL + " INTEGER, "
            + COLUMN_IS_ACTIVE + " BOOLEAN, "
            + COLUMN_CATEGORY_NAME + " TEXT, "
            + COLUMN_CREATED_ID + " TEXT, "
            + COLUMN_CREATED_NAME + " TEXT)";

    private SQLDatabaseHelper helper;

    public SQLCategoriesV1(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    public int checkExistData() {
        int count = 0;
        String sql = "SELECT * FROM " + TABLE_CATEGORIES + " LIMIT 2";
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
            db.delete(TABLE_CATEGORIES, null, null);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        }
    }

    public void insertCategories(List<CategoryV1> list) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (CategoryV1 item : list) {
                values.put(COLUMN_CATEGORY_ID, item.getId());
                values.put(COLUMN_PARENT_ID, item.getParent().getFieldId());
                values.put(COLUMN_PARENT_NAME, item.getParent().getFieldName());
                values.put(COLUMN_NOTE, item.getNote());
                values.put(COLUMN_LOGO, item.getLogo());
                values.put(COLUMN_POSITION, item.getPosition());
                values.put(COLUMN_CATEGORY_LEVEL, item.getGroup());
                values.put(COLUMN_IS_ACTIVE, item.getActive());
                values.put(COLUMN_CATEGORY_NAME, item.getName());
                values.put(COLUMN_CREATED_ID, item.getCreatedBy().getFieldId());
                values.put(COLUMN_CREATED_NAME, item.getCreatedBy().getFieldName());
                db.insert(TABLE_CATEGORIES, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<ItemCategory> getItemCategoryListByParentIdAndGroup(String parentId, int group) {
        List<ItemCategory> itemCategoryList = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();

        String sqlQuery;
        Cursor cursor;

        if (parentId == null) {
            sqlQuery = "SELECT "
                    + COLUMN_CATEGORY_ID + ", "
                    + COLUMN_CATEGORY_NAME + ", "
                    + COLUMN_CATEGORY_LEVEL + ", "
                    + COLUMN_PARENT_ID + ", "
                    + COLUMN_PARENT_NAME + ","
                    + COLUMN_LOGO
                    + " FROM " + TABLE_CATEGORIES
                    + " WHERE " + COLUMN_PARENT_ID + " IS NULL"
                    + " AND " + COLUMN_CATEGORY_LEVEL + " = " + group;
        } else {
            sqlQuery = "SELECT "
                    + COLUMN_CATEGORY_ID + ", "
                    + COLUMN_CATEGORY_NAME + ", "
                    + COLUMN_CATEGORY_LEVEL + ", "
                    + COLUMN_PARENT_ID + ", "
                    + COLUMN_PARENT_NAME + ","
                    + COLUMN_LOGO
                    + " FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_PARENT_ID + " LIKE '" + parentId + "%'"
                    + " AND " + COLUMN_CATEGORY_LEVEL + " = " + group;
        }

        cursor = db.rawQuery(sqlQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ItemCategory itemCategory = new ItemCategory();
                itemCategory.setCategoryId(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                itemCategory.setCategoryName(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
                itemCategory.setCategoryGroup(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_LEVEL)));
                itemCategory.setRankParentId(cursor.getString(cursor.getColumnIndex(COLUMN_PARENT_ID)));
                itemCategory.setRankParentName(cursor.getString(cursor.getColumnIndex(COLUMN_PARENT_NAME)));
                itemCategory.setLogo(cursor.getString(cursor.getColumnIndex(COLUMN_LOGO)));
                itemCategoryList.add(itemCategory);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemCategoryList;
    }

    public List<ItemCategory> getItemCategoryListByParentId(String parentId) {
        List<ItemCategory> itemCategoryList = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();

        String sqlQuery;
        Cursor cursor;

        if (parentId == null) {
            sqlQuery = "SELECT "
                    + COLUMN_CATEGORY_ID + ", "
                    + COLUMN_CATEGORY_NAME + ", "
                    + COLUMN_CATEGORY_LEVEL + ", "
                    + COLUMN_PARENT_ID + ", "
                    + COLUMN_PARENT_NAME + ","
                    + COLUMN_LOGO
                    + " FROM " + TABLE_CATEGORIES
                    + " WHERE " + COLUMN_PARENT_ID + " IS NULL";
        } else {
            sqlQuery = "SELECT "
                    + COLUMN_CATEGORY_ID + ", "
                    + COLUMN_CATEGORY_NAME + ", "
                    + COLUMN_CATEGORY_LEVEL + ", "
                    + COLUMN_PARENT_ID + ", "
                    + COLUMN_PARENT_NAME + ","
                    + COLUMN_LOGO
                    + " FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_PARENT_ID + " = '" + parentId + "'";
        }

        cursor = db.rawQuery(sqlQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ItemCategory itemCategory = new ItemCategory();
                itemCategory.setCategoryId(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                itemCategory.setCategoryName(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
                itemCategory.setCategoryGroup(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_LEVEL)));
                itemCategory.setRankParentId(cursor.getString(cursor.getColumnIndex(COLUMN_PARENT_ID)));
                itemCategory.setRankParentName(cursor.getString(cursor.getColumnIndex(COLUMN_PARENT_NAME)));
                itemCategory.setLogo(cursor.getString(cursor.getColumnIndex(COLUMN_LOGO)));
                itemCategoryList.add(itemCategory);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemCategoryList;
    }

    public List<ItemCategory> searchItemCategoryListByNameAndParentId(String name, String parentId) {
        List<ItemCategory> itemCategoryList = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        String sqlQuery;
        if (parentId == null) {
            sqlQuery = "SELECT "
                    + COLUMN_CATEGORY_ID + ", "
                    + COLUMN_PARENT_NAME + ", "
                    + COLUMN_CATEGORY_LEVEL + ", "
                    + COLUMN_PARENT_ID + ", "
                    + COLUMN_PARENT_NAME + ","
                    + COLUMN_LOGO
                    + " FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_PARENT_NAME + " LIKE '" + name + "%'"
                    + " AND " + COLUMN_PARENT_ID + " IS NULL";
        } else {
            sqlQuery = "SELECT "
                    + COLUMN_CATEGORY_ID + ", "
                    + COLUMN_PARENT_NAME + ", "
                    + COLUMN_CATEGORY_LEVEL + ", "
                    + COLUMN_PARENT_ID + ", "
                    + COLUMN_PARENT_NAME + ","
                    + COLUMN_LOGO
                    + " FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_PARENT_NAME + " LIKE '" + name + "%'"
                    + " AND " + COLUMN_PARENT_ID + " = '" + parentId + "'";
        }

        Cursor cursor = db.rawQuery(sqlQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ItemCategory itemCategory = new ItemCategory();
                itemCategory.setCategoryId(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                itemCategory.setCategoryName(cursor.getString(cursor.getColumnIndex(COLUMN_PARENT_NAME)));
                itemCategory.setCategoryGroup(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_LEVEL)));
                itemCategory.setRankParentId(cursor.getString(cursor.getColumnIndex(COLUMN_PARENT_ID)));
                itemCategory.setRankParentName(cursor.getString(cursor.getColumnIndex(COLUMN_PARENT_NAME)));
                itemCategory.setLogo(cursor.getString(cursor.getColumnIndex(COLUMN_LOGO)));
                itemCategoryList.add(itemCategory);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return itemCategoryList;
    }

    public List<ItemCategory> getListParentCategoryByParentId(String parentId) {
        List<ItemCategory> itemCategoryList = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();

        ItemCategory itemCategory = getParentCategory(parentId, itemCategoryList, db);
        if (itemCategory != null) {
            while (itemCategory != null && itemCategory.getCategoryName() != null) {
                itemCategory = getParentCategory(itemCategory.getRankParentName(), itemCategoryList, db);
            }
        }
        return itemCategoryList;
    }

    private ItemCategory getParentCategory(String parentId, List<ItemCategory> itemCategoryList, SQLiteDatabase db) {
        ItemCategory itemCategory = new ItemCategory();

        String sqlQuery;
        Cursor cursor;
        if (parentId != null) {
            sqlQuery = "SELECT "
                    + COLUMN_CATEGORY_ID + ", "
                    + COLUMN_CATEGORY_NAME + ", "
                    + COLUMN_CATEGORY_LEVEL + ", "
                    + COLUMN_PARENT_ID + ", "
                    + COLUMN_PARENT_NAME + ","
                    + COLUMN_LOGO
                    + " FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_CATEGORY_NAME + " = '" + parentId + "'";

            cursor = db.rawQuery(sqlQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    itemCategory.setCategoryId(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                    itemCategory.setCategoryName(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
                    itemCategory.setCategoryGroup(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_LEVEL)));
                    itemCategory.setRankParentId(cursor.getString(cursor.getColumnIndex(COLUMN_PARENT_ID)));
                    itemCategory.setRankParentName(cursor.getString(cursor.getColumnIndex(COLUMN_PARENT_NAME)));
                    itemCategory.setLogo(cursor.getString(cursor.getColumnIndex(COLUMN_LOGO)));
                    itemCategoryList.add(itemCategory);
                } while (cursor.moveToNext());
            }
            return itemCategory;
        } else {
            return null;
        }
    }
}
