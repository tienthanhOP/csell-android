package csell.com.vn.csell.sqlites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.models.ContactLocal;

public class SQLContactLocal {

    private SQLDatabaseHelper helper;

    public static final String TABLE_CONTACT_LOCAL = "ContactLocal";
    public static final String COLUMN_CONTACT_ID_LOCAL = "localId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    private static String COLUMN_PHONE1 = "phone1";
    private static String COLUMN_PHONE2 = "phone2";
    public static final String COLUMN_IS_DELETED = "isDeleted";
    public static final String COLUMN_ID = "id";
    private static String COLUMN_IS_ADDED = "isAdded";


    public static final String CREATE_TABLE_CONTACT_LOCAL = "create table " + TABLE_CONTACT_LOCAL + "("
//                + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_CONTACT_ID_LOCAL + " integer primary key not null, "
            + COLUMN_NAME + " text, "
            + COLUMN_EMAIL + " text, "
            + COLUMN_PHONE1 + " text, "
            + COLUMN_PHONE2 + " text, "
            + COLUMN_IS_ADDED + " integer default 0, "
            + COLUMN_IS_DELETED + " integer default 0)";

    public SQLContactLocal(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    /* Kiem tra dữ liệu có không*/
    public boolean checkExistData() {

        boolean output = false;
        String sqlQuery = "select count(*) from " + TABLE_CONTACT_LOCAL;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getInt(0) > 0) {
                output = true;
            }
        }

        cursor.close();
        db.close();
        return output;

    }

    /*Clear du lieu*/
    public void clearData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(TABLE_CONTACT_LOCAL, null, null);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (db != null) {
                db.close();
            }
        }
    }

    /* Kiem tra xem co bao nhieu ban ghi*/
    public int getCountAllContactLocals() {

        int c = 0;
        String allQuery = "select * from " + TABLE_CONTACT_LOCAL;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(allQuery, null);
        c = cursor.getCount();
        cursor.close();
        db.close();

        return c;

    }

    /* insert dữ liệu bằng transaction */
    public void insertAllContactLocalTransaction(ArrayList<ContactLocal> mDataContact) {

        String sqlInsert = "insert into " + TABLE_CONTACT_LOCAL + " ("
                + COLUMN_CONTACT_ID_LOCAL + ", "
                + COLUMN_PHONE1 + ", "
                + COLUMN_PHONE2 + ", "
                + COLUMN_NAME + ", "
                + COLUMN_EMAIL + ", "
                + COLUMN_IS_ADDED + ", "
                + COLUMN_IS_DELETED
                + ") values (?, ?, ?, ?, ?, ?, ?)";

        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();


        try {

            SQLiteStatement sqlState = db.compileStatement(sqlInsert);
            for (int i = 0; i < mDataContact.size(); i++) {

                sqlState.bindLong(1, mDataContact.get(i).getId());

                if (mDataContact.get(i).getPhone1() == null) {
                    mDataContact.get(i).setPhone1("");
                    sqlState.bindString(2, mDataContact.get(i).getPhone1());
                } else {
                    sqlState.bindString(2, mDataContact.get(i).getPhone1());
                }

                if (mDataContact.get(i).getPhone2() == null) {
                    mDataContact.get(i).setPhone2("");
                    sqlState.bindString(3, mDataContact.get(i).getPhone2());
                } else {
                    sqlState.bindString(3, mDataContact.get(i).getPhone2());
                }

                if (mDataContact.get(i).getContactName() == null) {
                    mDataContact.get(i).setContactName("");
                    sqlState.bindString(4, mDataContact.get(i).getContactName());
                } else {
                    sqlState.bindString(4, mDataContact.get(i).getContactName());
                }

                if (mDataContact.get(i).getEmail() == null) {
                    mDataContact.get(i).setEmail("");
                    sqlState.bindString(5, mDataContact.get(i).getEmail());
                } else {
                    sqlState.bindString(5, mDataContact.get(i).getEmail());
                }

                sqlState.bindLong(6, mDataContact.get(i).isAdded() == true ? 1 : 0);
                sqlState.bindLong(7, mDataContact.get(i).isDeleted() == true ? 1 : 0);

                sqlState.execute();
                sqlState.clearBindings();

            }

            db.setTransactionSuccessful();

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            db.endTransaction();
        }

        db.close();
    }

    public void insertContactLocal(ContactLocal local) {

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACT_ID_LOCAL, local.getId());
        values.put(COLUMN_NAME, local.getContactName());
        values.put(COLUMN_PHONE1, local.getPhone1());
        values.put(COLUMN_PHONE2, local.getPhone2());
        values.put(COLUMN_EMAIL, local.getEmail());
        values.put(COLUMN_IS_ADDED, local.isAdded() == true ? 1 : 0);
        values.put(COLUMN_IS_DELETED, local.isDeleted() == true ? 1 : 0);

        db.insert(TABLE_CONTACT_LOCAL, null, values);

        db.close();

    }

    public void updateAddedbyContactLocalId(int contactLocalId) {

        SQLiteDatabase db = helper.getWritableDatabase();

        String selectQuery = "update " + TABLE_CONTACT_LOCAL + " set " + COLUMN_IS_ADDED + " = 1"
                + " where " + COLUMN_CONTACT_ID_LOCAL + " ='" + contactLocalId + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        cursor.close();
        db.close();
    }

    public boolean checkIsAdded(int contactLocalId) {

        SQLiteDatabase db = helper.getWritableDatabase();
        String selectQuery = "select * from " + TABLE_CONTACT_LOCAL + " where "
                + COLUMN_CONTACT_ID_LOCAL + " ='" + contactLocalId + "' and " + COLUMN_IS_ADDED + " =1";
        boolean kq;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            kq = true;
        } else {
            kq = false;
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return kq;

    }

    public ArrayList<ContactLocal> getAllContactLocal() {

        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<ContactLocal> list = new ArrayList<>();

        String selectQuery = "select * from " + TABLE_CONTACT_LOCAL + " where " + COLUMN_IS_ADDED + " = '0'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {

                do {

                    ContactLocal local = new ContactLocal();
                    local.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_CONTACT_ID_LOCAL)));
                    local.setContactName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                    local.setPhone1(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE1)));
                    local.setPhone2(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE2)));
                    local.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
                    local.setAdded(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_ADDED)) == 1 ? true : false);
                    local.setDeleted(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_DELETED)) == 1 ? true : false);

                    list.add(local);

                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return list;
    }

    public String getMaxId() {

        SQLiteDatabase db = helper.getWritableDatabase();
        ContactLocal contact = new ContactLocal();

        String selectQuery = "SELECT * FROM " + TABLE_CONTACT_LOCAL + " ORDER BY " + COLUMN_CONTACT_ID_LOCAL + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            contact.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_CONTACT_ID_LOCAL)));
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return String.valueOf(contact.getId());

    }
}
