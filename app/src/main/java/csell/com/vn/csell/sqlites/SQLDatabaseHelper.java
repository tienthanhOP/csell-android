package csell.com.vn.csell.sqlites;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cuong.nv on 3/15/2018.
 */

public class SQLDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 25;
    private static final String DATABASE_NAME = "cselldatabase1";
    private static SQLDatabaseHelper sInstance;

    public SQLDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized SQLDatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new SQLDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.beginTransaction();
        db.execSQL(SQLCategoriesV1.CREATE_TABLE_CATEGORIES);
        db.execSQL(SQLPostTypesV1.CREATE_TABLE_POST_TYPES);

        db.execSQL(SQLProducts.CREATE_TABLE_PRODUCTS);
        db.execSQL(SQLCategories.CREATE_TABLE_CATEGORIES);//
        db.execSQL(SQLFriends.CREATE_TABLE_FRIENDS);
        db.execSQL(SQLCustomers.CREATE_TABLE_CUSTOMERS);
        db.execSQL(SQLContactLocal.CREATE_TABLE_CONTACT_LOCAL);
        db.execSQL(SQLCallLog.CREATE_TABLE_CALL_LOG);
        db.execSQL(SQLLocations.CREATE_TABLE_LOCATIONS);//
        db.execSQL(SQLCitys.CREATE_TABLE_CITYS);
        db.execSQL(SQLDistricts.CREATE_TABLE_DISTRICTS);
        db.execSQL(SQLWards.CREATE_TABLE_WARDS);
        db.execSQL(SQLStreets.CREATE_TABLE_STREETS);
        db.execSQL(SQLProperties.CREATE_TABLE_PROPERTY);
        db.execSQL(SQLCacheProduct.CREATE_TABLE_PRODUCT_COUNT);
        db.execSQL(SQLPostTypes.CREATE_TABLE_POST_TYPES);
        db.execSQL(SQLProjects.CREATE_TABLE_PROJECT);
        db.execSQL(SQLPropertyValue.CREATE_TABLE_PROPERTY_VALUE);
        db.execSQL(SQLTypeNotifications.CREATE_TABLE_NOTIFICATION_TYPES);
        db.execSQL(SQLLanguage.CREATE_TABLE_LANGUAGE);
        db.execSQL(SQLPropertiesFilter.CREATE_TABLE_PROPERTY);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        db.execSQL("DROP TABLE IF EXISTS " + SQLCategoriesV1.TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + SQLPostTypesV1.TABLE_POST_TYPES);

        db.execSQL("DROP TABLE IF EXISTS " + SQLProducts.TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + SQLCategories.TABLE_CATEGORY);//
        db.execSQL("DROP TABLE IF EXISTS " + SQLFriends.TABLE_FRIEND);
        db.execSQL("DROP TABLE IF EXISTS " + SQLCustomers.TABLE_CUSTOMER);
        db.execSQL("DROP TABLE IF EXISTS " + SQLContactLocal.TABLE_CONTACT_LOCAL);
        db.execSQL("DROP TABLE IF EXISTS " + SQLCallLog.TABLE_CALL_LOG);
        db.execSQL("DROP TABLE IF EXISTS " + SQLLocations.TABLE_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + SQLCitys.TABLE_CITYS);
        db.execSQL("DROP TABLE IF EXISTS " + SQLDistricts.TABLE_DISTRICTS);
        db.execSQL("DROP TABLE IF EXISTS " + SQLWards.TABLE_WARDS);
        db.execSQL("DROP TABLE IF EXISTS " + SQLStreets.TABLE_STREETS);
        db.execSQL("DROP TABLE IF EXISTS " + SQLProperties.TABLE_PROPERTY);
        db.execSQL("DROP TABLE IF EXISTS " + SQLCacheProduct.TABLE_CACHE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + SQLPostTypes.TABLE_POST_TYPES);
        db.execSQL("DROP TABLE IF EXISTS " + SQLProjects.TABLE_PROJECT);
        db.execSQL("DROP TABLE IF EXISTS " + SQLPropertyValue.TABLE_PROPERTY_VALUES);
        db.execSQL("DROP TABLE IF EXISTS " + SQLTypeNotifications.TABLE_NOTIFICATION_TYPES);
        db.execSQL("DROP TABLE IF EXISTS " + SQLLanguage.TABLE_LANGUAGE);
        db.execSQL("DROP TABLE IF EXISTS " + SQLPropertiesFilter.TABLE_PROPERTY_FILTER);
        onCreate(db);
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
