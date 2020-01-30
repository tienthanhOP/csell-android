package csell.com.vn.csell.sqlites;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by cuong.nv on 3/15/2018.
 */

public class SQLProducts {

    private SQLDatabaseHelper helper;

    public static final String TABLE_PRODUCTS = "products";

    public static final String COLUMN_PRODUCT_ID = "productid";
    public static final String COLUMN_USER_ID = "userid";
    public static final String COLUMN_PRODUCT_NAME = "productname";
    public static final String COLUMN_PRODUCT_DESCRIPTION = "productdescription";
    public static final String COLUMN_CATEGORY_ID = "categoryid";
    public static final String COLUMN_PARENT_CATEGORY_ID = "parentcategoryid";
    public static final String COLUMN_ROOT_CATEGORY_ID = "rootcategoryid";
    public static final String COLUMN_PRODUCT_DATECREATED = "datecreated";
    public static final String COLUMN_PRODUCT_PRICE = "price";
    public static final String COLUMN_PRODUCT_LOCATION_ID = "locationid";
    public static final String COLUMN_PRODUCT_LOCATION_NAME = "locationname";
    public static final String COLUMN_PRODUCT_LOCATION2 = "locationid2";
    public static final String COLUMN_PRODUCT_LOCATIONNAME2 = "locationname2";
    public static final String COLUMN_TYPE_PRICE_ID = "typepriceid";

    public static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS + " ("
            + COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_USER_ID + " TEXT, "
            + COLUMN_PRODUCT_NAME + " TEXT, "
            + COLUMN_PRODUCT_DESCRIPTION + " TEXT, "
            + COLUMN_CATEGORY_ID + " INTEGER, "
            + COLUMN_PARENT_CATEGORY_ID + " INTEGER, "
            + COLUMN_ROOT_CATEGORY_ID + " INTEGER, "
            + COLUMN_PRODUCT_DATECREATED + " TEXT, "
            + COLUMN_PRODUCT_PRICE + " TEXT, "
            + COLUMN_PRODUCT_LOCATION_ID + " INTEGER, "
            + COLUMN_PRODUCT_LOCATION_NAME + " TEXT, "
            + COLUMN_PRODUCT_LOCATION2 + " INTEGER, "
            + COLUMN_PRODUCT_LOCATIONNAME2 + " TEXT, "
            + COLUMN_TYPE_PRICE_ID + " INTEGER)";

    public SQLProducts(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

//    public void insertProducts(List<DataProduct> listCustomer) {
//        SQLiteDatabase db = helper.getWritableDatabase();
//        db.beginTransaction();
//        try {
//            ContentValues values = new ContentValues();
//            for (DataProduct item : listCustomer) {
//                values.put(COLUMN_USER_ID, item.getUserId());
//                values.put(COLUMN_PRODUCT_ID, item.getProductId());
//                values.put(COLUMN_PRODUCT_NAME, item.getProductName());
//                values.put(COLUMN_PRODUCT_DESCRIPTION, item.getProductDescription());
//                values.put(COLUMN_CATEGORY_ID, item.CategoryId); //category con
//                values.put(COLUMN_PARENT_CATEGORY_ID, item.getParentCategoryId()); // category cha
//                values.put(COLUMN_ROOT_CATEGORY_ID, item.getRootCategoryLevel2()); // category goc
//                values.put(COLUMN_PRODUCT_DATECREATED, item.getDateCreated());
//                values.put(COLUMN_PRODUCT_PRICE, item.getPrice());
//                values.put(COLUMN_PRODUCT_LOCATION_ID, item.getLocationId());
//                values.put(COLUMN_PRODUCT_LOCATION_NAME, item.getLocationName());
//                values.put(COLUMN_PRODUCT_LOCATION2, item.getLocationIdLv2());
//                values.put(COLUMN_PRODUCT_LOCATIONNAME2, item.getLocationNameLv2());
//                db.insert(TABLE_PRODUCTS, null, values);
//            }
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//
//        } finally {
//            db.endTransaction();
//        }
//    }


    public int checkExistData() {
        int count = 0;
        String sql = "SELECT * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            count = cursor.getCount();
        }

        cursor.close();
        db.close();
        return count;
    }

//    public ArrayList<String> getProductByCategoryId(int categoryId) {
//        ArrayList<String> data = new ArrayList<>();
//        SQLiteDatabase db = helper.getReadableDatabase();
//        String where = COLUMN_CATEGORY_ID + "=?";
//        String[] whereAgrs = {String.valueOf(categoryId)};
//        Cursor c = db.queryDefault(TABLE_PRODUCTS, null, where, whereAgrs, null, null, null, null);
//        if (c.getCount() > 0) {
//            c.moveToFirst();
//            do {
//
//                data.add(c.getString(c.getColumnIndex(COLUMN_PRODUCT_NAME)));
//
//            } while (c.moveToNext());
//        }
//        db.close();
//        c.close();
//        return data;
//    }
//
//    public int countProductByRootCategoryId(int categoryId) {
//        int count = 0;
//        SQLiteDatabase db = helper.getReadableDatabase();
//        String where = COLUMN_ROOT_CATEGORY_ID + "=?";
//        String[] whereAgrs = {String.valueOf(categoryId)};
//        String[] columns = {COLUMN_PRODUCT_ID};
//        Cursor c = db.queryDefault(TABLE_PRODUCTS, columns, where, whereAgrs, COLUMN_ROOT_CATEGORY_ID, null, null, null);
//        if (c.getCount() > 0) {
//            count = c.getCount();
//        }
//        db.close();
//        c.close();
//        return count;
//    }

    //lay ra danh sach parent category trong danh sach sp co bao nhieu loai
//    public ArrayList<DataCategory> getParentIdByRootId(int userId, int categoryId) {
//        ArrayList<DataCategory> data = new ArrayList<>();
//        SQLiteDatabase db = helper.getReadableDatabase();
//        String sqlLocations = "SELECT "
//                + COLUMN_PARENT_CATEGORY_ID
//                + " FROM "
//                + TABLE_PRODUCTS
//                + " WHERE "
//                + COLUMN_USER_ID + " = " + userId
//                + " AND "
//                + COLUMN_ROOT_CATEGORY_ID + " = " + categoryId
//                + " GROUP BY " + COLUMN_PARENT_CATEGORY_ID;
//
//        Cursor c = db.rawQuery(sqlLocations, null);
//        if (c.getCount() > 0) {
//            c.moveToFirst();
//            do {
//
//                DataCategory category = new DataCategory();
//                category.CategoryId=c.getInt(c.getColumnIndex(COLUMN_PARENT_CATEGORY_ID)));
//                data.add(category);
//
//            } while (c.moveToNext());
//        }
//        db.close();
//        c.close();
//        return data;
//    }

    //lay ra danh sach parent category trong danh sach sp co bao nhieu loai
//    public ArrayList<DataCategory> getRootCategoryIdProduct() {
//        ArrayList<DataCategory> data = new ArrayList<>();
//        SQLiteDatabase db = helper.getReadableDatabase();
//        String sqlLocations = "SELECT "
//                + COLUMN_ROOT_CATEGORY_ID
//                + " FROM "
//                + TABLE_PRODUCTS
//                + " GROUP BY " + COLUMN_ROOT_CATEGORY_ID;
//
//        Cursor c = db.rawQuery(sqlLocations, null);
//        if (c.getCount() > 0) {
//            c.moveToFirst();
//            do {
//                DataCategory category = new DataCategory();
//                category.CategoryId=c.getInt(c.getColumnIndex(COLUMN_ROOT_CATEGORY_ID)));
//                data.add(category);
//
//            } while (c.moveToNext());
//        }
//        db.close();
//        c.close();
//        return data;
//    }



    //lay ra danh sach category cua sp theo parentid trong danh sach sp co bao nhieu loai
//    public ArrayList<DataCategory> getCategoryIdIdByParentId(int userId, int categoryId) {
//        ArrayList<DataCategory> data = new ArrayList<>();
//        SQLiteDatabase db = helper.getReadableDatabase();
//        String sqlLocations = "SELECT "
//                + COLUMN_CATEGORY_ID
//                + " FROM "
//                + TABLE_PRODUCTS
//                + " WHERE "
//                + COLUMN_USER_ID + " = " + userId
//                + " AND "
//                + COLUMN_ROOT_CATEGORY_ID + " = " + categoryId
//                + " GROUP BY " + COLUMN_CATEGORY_ID;
//
//        Cursor c = db.rawQuery(sqlLocations, null);
//        if (c.getCount() > 0) {
//            c.moveToFirst();
//            do {
//                DataCategory category = new DataCategory();
//                category.CategoryId=c.getInt(c.getColumnIndex(COLUMN_CATEGORY_ID)));
//                data.add(category);
//
//            } while (c.moveToNext());
//        }
//        db.close();
//        c.close();
//        return data;
//    }

//    public int countProductByCategoryId(int userId, int categoryId) {
//        int count = 0;
//        SQLiteDatabase db = helper.getReadableDatabase();
//        String where = COLUMN_USER_ID + " = ? and " + COLUMN_PARENT_CATEGORY_ID + " = ?";
//        String[] whereAgrs = {String.valueOf(userId), String.valueOf(categoryId)};
//        String[] columns = {COLUMN_PRODUCT_ID};
//        Cursor c = db.queryDefault(TABLE_PRODUCTS, columns, where, whereAgrs, null, null, null, null);
//        if (c.getCount() > 0) {
//            count = c.getCount();
//        }
//        db.close();
//        c.close();
//        return count;
//    }

//    public int countProductByParentId(int userId, int parentCategoryId) {
//        int count = 0;
//        SQLiteDatabase db = helper.getReadableDatabase();
//        String where = COLUMN_USER_ID + " = ? and " + COLUMN_PARENT_CATEGORY_ID + " = ?";
//        String[] whereAgrs = {String.valueOf(userId), String.valueOf(parentCategoryId)};
//        String[] columns = {COLUMN_PRODUCT_ID};
//        Cursor c = db.queryDefault(TABLE_PRODUCTS, columns, where, whereAgrs, null, null, null, null);
//        if (c.getCount() > 0) {
//            count = c.getCount();
//        }
//        db.close();
//        c.close();
//        return count;
//    }

//    public ArrayList<DataProduct> getProductByCategoryId(int userId, int categoryId) {
//        ArrayList<DataProduct> data = new ArrayList<>();
//        SQLiteDatabase db = helper.getReadableDatabase();
//        String where = COLUMN_USER_ID + " = ? and " + COLUMN_CATEGORY_ID + " = ?";
//        String[] whereAgrs = {String.valueOf(userId), String.valueOf(categoryId)};
//        Cursor c = db.queryDefault(TABLE_PRODUCTS, null, where, whereAgrs, null, null, null, null);
//        if (c.getCount() > 0) {
//
//            c.moveToFirst();
//            do{
//                DataProduct product = new DataProduct();
//                product.setUserId(c.getInt(c.getColumnIndex(COLUMN_USER_ID)));
//                product.setProductId(c.getInt(c.getColumnIndex(COLUMN_PRODUCT_ID)));
//                product.setProductName(c.getString(c.getColumnIndex(COLUMN_PRODUCT_NAME)));
//                product.setProductDescription(c.getString(c.getColumnIndex(COLUMN_PRODUCT_DESCRIPTION)));
//                product.setDateCreated(c.getString(c.getColumnIndex(COLUMN_PRODUCT_DATECREATED)));
//                product.CategoryId=c.getInt(c.getColumnIndex(COLUMN_CATEGORY_ID)));
//                product.setParentCategoryId(c.getInt(c.getColumnIndex(COLUMN_PARENT_CATEGORY_ID)));
//                product.setRootCategoryLevel2(c.getInt(c.getColumnIndex(COLUMN_ROOT_CATEGORY_ID)));
//                product.setPrice(c.getLong(c.getColumnIndex(COLUMN_PRODUCT_PRICE)));
////                product.setTypePriceId(c.getInt(c.getColumnIndex(COLUMN_USER_ID)));
//                product.setLocationId(c.getInt(c.getColumnIndex(COLUMN_PRODUCT_LOCATION_ID)));
//                product.setLocationName(c.getString(c.getColumnIndex(COLUMN_PRODUCT_LOCATION_NAME)));
//                product.setLocationIdLv2(c.getInt(c.getColumnIndex(COLUMN_PRODUCT_LOCATION2)));
//                product.setLocationNameLv2(c.getString(c.getColumnIndex(COLUMN_PRODUCT_LOCATIONNAME2)));
//                data.add(product);
//            }while (c.moveToNext());
//        }
//        db.close();
//        c.close();
//        return data;
//    }

    //lay danh sach san pham theo category
//    public ArrayList<DataProduct> getProductByParentId(int userId, int categoryId) {
//        ArrayList<DataProduct> data = new ArrayList<>();
//        SQLiteDatabase db = helper.getReadableDatabase();
//        String where = COLUMN_USER_ID + " = ? and " + COLUMN_CATEGORY_ID + " = ?";
//        String[] whereAgrs = {String.valueOf(userId), String.valueOf(categoryId)};
//
//        Cursor c = db.queryDefault(TABLE_PRODUCTS, null, where, whereAgrs, null, null, null, null);
//        if (c.getCount() > 0) {
//            c.moveToFirst();
//            do {
//
//                DataProduct product = new DataProduct();
//                product.setUserId(c.getInt(c.getColumnIndex(COLUMN_USER_ID)));
//                product.setProductId(c.getInt(c.getColumnIndex(COLUMN_PRODUCT_ID)));
//                product.setProductName(c.getString(c.getColumnIndex(COLUMN_PRODUCT_NAME)));
//                product.setProductDescription(c.getString(c.getColumnIndex(COLUMN_PRODUCT_DESCRIPTION)));
//                product.setDateCreated(c.getString(c.getColumnIndex(COLUMN_PRODUCT_DATECREATED)));
//                product.CategoryId=c.getInt(c.getColumnIndex(COLUMN_CATEGORY_ID)));
//                product.setParentCategoryId(c.getInt(c.getColumnIndex(COLUMN_PARENT_CATEGORY_ID)));
//                product.setRootCategoryLevel2(c.getInt(c.getColumnIndex(COLUMN_ROOT_CATEGORY_ID)));
//                product.setPrice(c.getLong(c.getColumnIndex(COLUMN_PRODUCT_PRICE)));
////                product.setTypePriceId(c.getInt(c.getColumnIndex(COLUMN_USER_ID)));
//                product.setLocationId(c.getInt(c.getColumnIndex(COLUMN_PRODUCT_LOCATION_ID)));
//                product.setLocationName(c.getString(c.getColumnIndex(COLUMN_PRODUCT_LOCATION_NAME)));
//                product.setLocationIdLv2(c.getInt(c.getColumnIndex(COLUMN_PRODUCT_LOCATION2)));
//                product.setLocationNameLv2(c.getString(c.getColumnIndex(COLUMN_PRODUCT_LOCATIONNAME2)));
//                data.add(product);
//
//            } while (c.moveToNext());
//        }
//        db.close();
//        c.close();
//        return data;
//    }

//    public DataProduct getProductbyProductId(String userId, int productId){
//
//        SQLiteDatabase db = helper.getWritableDatabase();
//        String selectQuery = "SELECT * FROM "
//                + TABLE_PRODUCTS
//                + " WHERE "
//                + COLUMN_USER_ID
//                + " ='" + userId + "'"
//                + " AND "
//                + COLUMN_PRODUCT_ID
//                + " ='" + productId +"'";
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        DataProduct product = new DataProduct();
//        if (cursor != null && cursor.moveToFirst()){
//
//            product.setProductName(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME)));
//            product.setProductDescription(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_DESCRIPTION)));
//
//        }
//
//        cursor.close();
//        return product;
//    }

//    public ArrayList<DataProduct> getAllProductbyUserId(String userId){
//
//        SQLiteDatabase db = helper.getWritableDatabase();
//        String selectQuery = "SELECT * FROM "
//                + TABLE_PRODUCTS
//                + " WHERE "
//                + COLUMN_USER_ID
//                + " ='" + userId + "'";
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        ArrayList<DataProduct> list = new ArrayList<>();
//        if (cursor.moveToFirst() && cursor != null){
//
//            do {
//
//                DataProduct product = new DataProduct();
//                product.setProductName(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME)));
//                product.setProductDescription(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_DESCRIPTION)));
//                product.setProductId(cursor.getInt(cursor.getColumnIndex(COLUMN_PRODUCT_ID)));
//                list.add(product);
//
//            }while (cursor.moveToNext());
//
//        }
//
//        cursor.close();
//        return list;
//
//    }

    public void clearData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(TABLE_PRODUCTS, null, null);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (db != null) {
                db.close();
            }
        }
    }

}
