package csell.com.vn.csell.sqlites;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;

import csell.com.vn.csell.BuildConfig;

/**
 * Created by chuc.nq on 3/20/2018.
 */

public class SQLCallLog {
    private SQLDatabaseHelper helper;

    public static final String TABLE_CALL_LOG = "CallLog";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PHONE_NUMBER = "phoneNumber";
    public static final String COLUMN_IS_ADDED = "isAdded";

    public static final String CREATE_TABLE_CALL_LOG = "create table " + TABLE_CALL_LOG + " ("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PHONE_NUMBER + " text not null, "
            + COLUMN_IS_ADDED + " integer default 0)";

    public SQLCallLog(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    /* Kiem tra dữ liệu có không*/
    public boolean checkExistData() {

        boolean output = false;
        String sqlQuery = "select count(*) from " + TABLE_CALL_LOG;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.getInt(0) > 0) {
                output = true;
            }
        }

        cursor.close();
//        db.close();
        return output;

    }

    /* Kiem tra xem co bao nhieu ban ghi*/
    public int getCountAllCallLog() {

        int c = 0;
        String allQuery = "select * from " + TABLE_CALL_LOG;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(allQuery, null);
        c = cursor.getCount();
        cursor.close();
//        db.close();

        return c;

    }

    /*Clear du lieu*/
    public void clearData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(TABLE_CALL_LOG, null, null);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (db != null) {
                db.close();
            }
        }
    }

    /* insert dữ liệu bằng transaction */
    public void insertAllCallLogTransaction(ArrayList<String> mData) {

        String sqlInsert = "insert into " + TABLE_CALL_LOG + " ("
                + COLUMN_PHONE_NUMBER + ", "
                + COLUMN_IS_ADDED
                + ") values (?, ?)";

        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();



        try {

            SQLiteStatement sqlState = db.compileStatement(sqlInsert);
            for (int i = 0; i < mData.size(); i++) {

                sqlState.bindString(1, mData.get(i));
                sqlState.bindLong(2, 0);

                sqlState.execute();
                sqlState.clearBindings();

            }

            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {
           if(BuildConfig.DEBUG) Log.d(""+getClass().getName(),e.getMessage());
        }

        db.close();


    }

    public ArrayList<String> getItems(int offset) {

        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<String> list = new ArrayList<>();


        String selectQuery = "select * from " + TABLE_CALL_LOG
                + " limit " + 10
                + " offset " + offset;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {

            do {

                String callLog = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER));
                list.add(callLog);

            } while (cursor.moveToNext());

        }

        cursor.close();

//            String selectQuery = "select * from " + TABLE_PRODUCTS
        return list;

    }

}
