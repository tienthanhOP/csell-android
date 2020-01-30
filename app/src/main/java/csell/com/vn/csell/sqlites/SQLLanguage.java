package csell.com.vn.csell.sqlites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.models.NameLanguage;

public class SQLLanguage {
    private SQLDatabaseHelper helper;

    public static final String TABLE_LANGUAGE = "language";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CODE_ID = "title_code";
    public static final String COLUMN_CODE_NAME = "title_name";
    public static final String COLUMN_DISPLAY_NAME_VI = "display_name_vi";
    public static final String COLUMN_DISPLAY_NAME_EN = "display_name_en";
    public static final String COLUMN_DISPLAY_NAME_KO = "display_name_ko";
    public static final String COLUMN_DISPLAY_NAME_FR = "display_name_fr";

    public SQLLanguage(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    public static final String CREATE_TABLE_LANGUAGE = "CREATE TABLE " + TABLE_LANGUAGE + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CODE_ID + " INTEGER, "
            + COLUMN_CODE_NAME + " TEXT, "
            + COLUMN_DISPLAY_NAME_VI + " TEXT, "
            + COLUMN_DISPLAY_NAME_EN + " TEXT, "
            + COLUMN_DISPLAY_NAME_FR + " TEXT, "
            + COLUMN_DISPLAY_NAME_KO + " TEXT)";

    public void clearData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(TABLE_LANGUAGE, null, null);
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
        String sql = "SELECT * FROM " + TABLE_LANGUAGE + " LIMIT 3";
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            count = cursor.getCount();
        }
        cursor.close();
        db.close();
        return count;
    }

    public void insertLanguage(List<NameLanguage> list) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (NameLanguage item : list) {

                values.put(COLUMN_CODE_ID, item.getTitle_code());
                values.put(COLUMN_CODE_NAME, item.getTitle_name());
                values.put(COLUMN_DISPLAY_NAME_VI, item.getDisplay_name_vi());
                values.put(COLUMN_DISPLAY_NAME_EN, item.getDisplay_name_en());
                values.put(COLUMN_DISPLAY_NAME_KO, item.getDisplay_name_ko());
                values.put(COLUMN_DISPLAY_NAME_FR, item.getDisplay_name_fr());
                db.insert(TABLE_LANGUAGE, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public String getDisplayNameByCode(int titleCode, String isoCode) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql;
        String displayName = "";
        if (isoCode.equalsIgnoreCase(Constants.ISO_CODE_ENGLAND)) {
            sql = "select " + COLUMN_DISPLAY_NAME_EN +
                    " from " + TABLE_LANGUAGE +
                    " where " + COLUMN_CODE_ID + "=" + titleCode;

            Cursor c = db.rawQuery(sql, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    displayName = c.getString(c.getColumnIndex(COLUMN_DISPLAY_NAME_EN));
                } while (c.moveToNext());
            }
            c.close();
        } else if (isoCode.equalsIgnoreCase(Constants.ISO_CODE_FRANCE)) {
            sql = "select " + COLUMN_DISPLAY_NAME_FR +
                    " from " + TABLE_LANGUAGE +
                    " where " + COLUMN_CODE_ID + "=" + titleCode;

            Cursor c = db.rawQuery(sql, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    displayName = c.getString(c.getColumnIndex(COLUMN_DISPLAY_NAME_FR));
                } while (c.moveToNext());
            }
            c.close();
        } else if (isoCode.equalsIgnoreCase(Constants.ISO_CODE_KOREAN)) {
            sql = "select " + COLUMN_DISPLAY_NAME_KO +
                    " from " + TABLE_LANGUAGE +
                    " where " + COLUMN_CODE_ID + "=" + titleCode;

            Cursor c = db.rawQuery(sql, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    displayName = c.getString(c.getColumnIndex(COLUMN_DISPLAY_NAME_KO));
                } while (c.moveToNext());
            }
            c.close();
        } else {
            sql = "select " + COLUMN_DISPLAY_NAME_VI +
                    " from " + TABLE_LANGUAGE +
                    " where " + COLUMN_CODE_ID + "=" + titleCode;

            Cursor c = db.rawQuery(sql, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    displayName = c.getString(c.getColumnIndex(COLUMN_DISPLAY_NAME_VI));
                } while (c.moveToNext());
            }
            c.close();
        }

        db.close();
        return displayName;
    }

    public String getDisplayNameByCatId(String catId, String isoCode) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql;
        String displayName = "";
        if (isoCode.equalsIgnoreCase(Constants.ISO_CODE_ENGLAND)) {
            sql = "select " + COLUMN_DISPLAY_NAME_EN +
                    " from " + TABLE_LANGUAGE +
                    " where " + COLUMN_CODE_NAME + " like '" + catId + "'";

            Cursor c = db.rawQuery(sql, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    displayName = c.getString(c.getColumnIndex(COLUMN_DISPLAY_NAME_EN));
                } while (c.moveToNext());
            }
            c.close();
        } else if (isoCode.equalsIgnoreCase(Constants.ISO_CODE_FRANCE)) {
            sql = "select " + COLUMN_DISPLAY_NAME_FR +
                    " from " + TABLE_LANGUAGE +
                    " where " + COLUMN_CODE_NAME + " like '" + catId +"'";

            Cursor c = db.rawQuery(sql, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    displayName = c.getString(c.getColumnIndex(COLUMN_DISPLAY_NAME_FR));
                } while (c.moveToNext());
            }
            c.close();
        } else if (isoCode.equalsIgnoreCase(Constants.ISO_CODE_KOREAN)) {
            sql = "select " + COLUMN_DISPLAY_NAME_KO +
                    " from " + TABLE_LANGUAGE +
                    " where " + COLUMN_CODE_NAME + " like '" + catId +"'";

            Cursor c = db.rawQuery(sql, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    displayName = c.getString(c.getColumnIndex(COLUMN_DISPLAY_NAME_KO));
                } while (c.moveToNext());
            }
            c.close();
        } else {
            sql = "select " + COLUMN_DISPLAY_NAME_VI +
                    " from " + TABLE_LANGUAGE +
                    " where " + COLUMN_CODE_NAME + " like '" + catId +"'";
//                    " where " + COLUMN_CODE_NAME + " ='" + catId + "' ";

            Cursor c = db.rawQuery(sql, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    displayName = c.getString(c.getColumnIndex(COLUMN_DISPLAY_NAME_VI));
                } while (c.moveToNext());
            }
            c.close();

        }

        db.close();
        return displayName;
    }
}
