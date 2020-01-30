package csell.com.vn.csell.sqlites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.models.CustomerRetro;

/**
 * Created by chuc.nq on 3/20/2018.
 */

public class SQLCustomers {

    private SQLDatabaseHelper helper;

    public static final String TABLE_CUSTOMER = "Customers";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CUSTOMER_ID = "custid";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_CONTACT_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_HASH_TAG = "tags";
    private static final String COLUMN_NEED = "need";
    private static final String COLUMN_DOB = "dob";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_JOBS = "jobs";

    public static final String CREATE_TABLE_CUSTOMERS = "create table " + TABLE_CUSTOMER + "("
            + COLUMN_ID + " integer primary key, "
            + COLUMN_CUSTOMER_ID + " text, "
            + COLUMN_CONTACT_NAME + " text, "
            + COLUMN_PHONE + " text, "
            + COLUMN_EMAIL + " text, "
            + COLUMN_NEED + " text, "
            + COLUMN_DOB + " text, "
            + COLUMN_ADDRESS + " text, "
            + COLUMN_JOBS + " text, "
            + COLUMN_HASH_TAG + " text)";

    public SQLCustomers(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    /* Kiem tra dữ liệu có không*/
    public boolean checkExistData() {

        boolean output = false;
        String sqlQuery = "select count(*) from " + TABLE_CUSTOMER;
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

    /* Kiem tra xem co bao nhieu ban ghi*/
    public int getCountAllCustomer() {

        int c = 0;
        String allQuery = "select * from " + TABLE_CUSTOMER;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(allQuery, null);
        c = cursor.getCount();
        cursor.close();
        db.close();

        return c;

    }

    /*Clear du lieu*/
    public void clearData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(TABLE_CUSTOMER, null, null);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
        }
    }

    public void insertListCustomers(List<CustomerRetro> dataCustomers) {

        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (CustomerRetro item : dataCustomers) {
                values.put(COLUMN_CUSTOMER_ID, item.getCustId());
                values.put(COLUMN_CONTACT_NAME, item.getName());
                if (item.getPhone() != null) {
                    if (item.getPhone().size() != 0) {
                        String phone = "";
                        for (int i = 0; i < item.getPhone().size(); i++) {
                            if (i == 0) {
                                phone += item.getPhone().get(i);
                            } else {
                                phone += "," + item.getPhone().get(i);
                            }
                        }
                        values.put(COLUMN_PHONE, phone);
                    }
                }

                if (item.getEmail() != null) {
                    if (item.getEmail().size() != 0) {
                        String email = "";
                        for (int i = 0; i < item.getEmail().size(); i++) {
                            if (i == 0) {
                                email += item.getEmail().get(i);
                            } else {
                                email += "," + item.getEmail().get(i);
                            }
                        }
                        values.put(COLUMN_EMAIL, email);
                    }
                }


                if (item.getTags() != null) {
                    if (item.getTags().size() != 0) {
                        String hashTag = "";
                        for (int i = 0; i < item.getTags().size(); i++) {
                            if (i == 0) {
                                hashTag += item.getTags().get(i);
                            } else {
                                hashTag += "," + item.getTags().get(i);
                            }
                        }
                        values.put(COLUMN_HASH_TAG, hashTag);
                    }
                }
                values.put(COLUMN_NEED, !TextUtils.isEmpty(item.getNeed()) ? item.getNeed() : "");
                values.put(COLUMN_DOB, !TextUtils.isEmpty(item.getDob()) ? item.getDob() : "");
                values.put(COLUMN_ADDRESS, !TextUtils.isEmpty(item.getAddress()) ? item.getAddress() : "");
                values.put(COLUMN_JOBS, !TextUtils.isEmpty(item.getJobs()) ? item.getJobs() : "");

                db.insert(TABLE_CUSTOMER, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void insertAddCustomer(CustomerRetro item) {
        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransaction();
        Cursor cursor = null;
        try {
            String sqlQuery = "select * from " + TABLE_CUSTOMER + "  WHERE " + COLUMN_CUSTOMER_ID + " ='" + item.getCustId() + "' ";
            cursor = db.rawQuery(sqlQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                ContentValues values = new ContentValues();
                values.put(COLUMN_CUSTOMER_ID, item.getCustId());
                values.put(COLUMN_CONTACT_NAME, item.getName());

                if (item.getPhone() != null) {
                    if (item.getPhone().size() != 0) {
                        String phone = "";
                        for (int i = 0; i < item.getPhone().size(); i++) {
                            if (i == 0) {
                                phone += item.getPhone().get(i);
                            } else {
                                phone += "," + item.getPhone().get(i);
                            }
                        }
                        values.put(COLUMN_PHONE, phone);
                    }
                }

                if (item.getEmail() != null) {
                    if (item.getEmail().size() != 0) {
                        String email = "";
                        for (int i = 0; i < item.getEmail().size(); i++) {
                            if (i == 0) {
                                email += item.getEmail().get(i);
                            } else {
                                email += "," + item.getEmail().get(i);
                            }
                        }
                        values.put(COLUMN_EMAIL, email);
                    }
                }


                if (item.getTags() != null) {
                    if (item.getTags().size() != 0) {
                        String hashTag = "";
                        for (int i = 0; i < item.getTags().size(); i++) {
                            if (i == 0) {
                                hashTag += item.getTags().get(i);
                            } else {
                                hashTag += "," + item.getTags().get(i);
                            }
                        }
                        values.put(COLUMN_HASH_TAG, hashTag);
                    }
                }

                values.put(COLUMN_NEED, !TextUtils.isEmpty(item.getNeed()) ? item.getNeed() : "");
                values.put(COLUMN_DOB, !TextUtils.isEmpty(item.getDob()) ? item.getDob() : "");
                values.put(COLUMN_ADDRESS, !TextUtils.isEmpty(item.getAddress()) ? item.getAddress() : "");
                values.put(COLUMN_JOBS, !TextUtils.isEmpty(item.getJobs()) ? item.getJobs() : "");

                db.update(TABLE_CUSTOMER, values, COLUMN_CUSTOMER_ID + "='" + item.getCustId() + "'", null);
            } else {

                ContentValues values = new ContentValues();
                values.put(COLUMN_CUSTOMER_ID, item.getCustId());
                values.put(COLUMN_CONTACT_NAME, item.getName());
                if (item.getPhone() != null) {
                    if (item.getPhone().size() != 0) {
                        String phone = "";
                        for (int i = 0; i < item.getPhone().size(); i++) {
                            if (i == 0) {
                                phone += item.getPhone().get(i);
                            } else {
                                phone += "," + item.getPhone().get(i);
                            }
                        }
                        values.put(COLUMN_PHONE, phone);
                    }
                }

                if (item.getEmail() != null) {
                    if (item.getEmail().size() != 0) {
                        String email = "";
                        for (int i = 0; i < item.getEmail().size(); i++) {
                            if (i == 0) {
                                email += item.getEmail().get(i);
                            } else {
                                email += "," + item.getEmail().get(i);
                            }
                        }
                        values.put(COLUMN_EMAIL, email);
                    }
                }

                if (item.getTags() != null) {
                    if (item.getTags().size() != 0) {
                        String hashTag = "";
                        for (int i = 0; i < item.getTags().size(); i++) {
                            if (i == 0) {
                                hashTag += item.getTags().get(i);
                            } else {
                                hashTag += "," + item.getTags().get(i);
                            }
                        }
                        values.put(COLUMN_HASH_TAG, hashTag);
                    }
                }

                values.put(COLUMN_NEED, !TextUtils.isEmpty(item.getNeed()) ? item.getNeed() : "");
                values.put(COLUMN_DOB, !TextUtils.isEmpty(item.getDob()) ? item.getDob() : "");
                values.put(COLUMN_ADDRESS, !TextUtils.isEmpty(item.getAddress()) ? item.getAddress() : "");
                values.put(COLUMN_JOBS, !TextUtils.isEmpty(item.getJobs()) ? item.getJobs() : "");

                db.insert(TABLE_CUSTOMER, null, values);
            }


            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            try {
                cursor.close();
                db.endTransaction();
                db.close();
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
            }
        }
    }

    public void deleteCustomer(String customerId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TABLE_CUSTOMER, COLUMN_CUSTOMER_ID + " like '" + customerId + "'", null);
        db.close();
    }

    public ArrayList<CustomerRetro> getAllCustomer() {

        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<CustomerRetro> data = new ArrayList<>();
//        if (!db.isOpen()){
//            SQLCustomers
//        }

        String selectQuery = "select * from " + TABLE_CUSTOMER;
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {

                do {

                    CustomerRetro customerRetro = new CustomerRetro();
                    customerRetro.setCustId(cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMER_ID)));
                    customerRetro.setName(cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_NAME)));
                    customerRetro.setAddress(cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)));
                    customerRetro.setNeed(cursor.getString(cursor.getColumnIndex(COLUMN_NEED)));
                    customerRetro.setJobs(cursor.getString(cursor.getColumnIndex(COLUMN_JOBS)));
                    customerRetro.setDob(cursor.getString(cursor.getColumnIndex(COLUMN_DOB)));

                    String phone = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE));
                    ArrayList<String> listPhone = new ArrayList<>();
                    if (!TextUtils.isEmpty(phone)) {
                        if (phone.contains(",")) {
                            String[] phones = phone.split(",");
                            for (String p : phones) {
                                listPhone.add(p);
                            }
                        } else {
                            listPhone.add(phone);
                        }

                        customerRetro.setPhone(listPhone);
                    }

                    String email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));
                    ArrayList<String> listEmail = new ArrayList<>();
                    if (!TextUtils.isEmpty(email)) {
                        if (email.contains(",")) {
                            String[] emails = email.split(",");
                            for (String p : emails) {
                                listEmail.add(p);
                            }
                        } else {
                            listEmail.add(email);
                        }

                        customerRetro.setEmail(listEmail);
                    }

                    String tag = cursor.getString(cursor.getColumnIndex(COLUMN_HASH_TAG));
                    ArrayList<String> listTag = new ArrayList<>();
                    if (!TextUtils.isEmpty(tag)) {
                        if (tag.contains(",")) {
                            String[] tags = tag.split(",");
                            for (String p : tags) {
                                listTag.add(p);
                            }
                        } else {
                            listTag.add(tag);
                        }

                        customerRetro.setTags(listTag);
                    }

                    data.add(customerRetro);

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

        return data;
    }

    public boolean checkExistedPhone(String phone) {

        SQLiteDatabase db = helper.getWritableDatabase();
        boolean check = false;

        String selectQuery = "select * from " + TABLE_CUSTOMER + " where " + COLUMN_PHONE + " like '%" + phone + "%'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.getCount() > 0) {
            check = true;
        } else {
            check = false;
        }

        cursor.close();
        db.close();

        return check;

    }

    public boolean checkExistedEmail(String email) {
        SQLiteDatabase db = helper.getWritableDatabase();
        boolean check = false;

        String selectQuery = "select * from " + TABLE_CUSTOMER + " where " + COLUMN_EMAIL + " like '%" + email + "%'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.getCount() > 0) {
            check = true;
        } else {
            check = false;
        }

        cursor.close();
        db.close();

        return check;
    }

    public ArrayList<CustomerRetro> getItems(int offset) {

        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<CustomerRetro> list = new ArrayList<>();


        String selectQuery = "select * from " + TABLE_CUSTOMER
                + " limit " + 10
                + " offset " + offset;
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {

                do {

                    CustomerRetro customerRetro = new CustomerRetro();
                    customerRetro.setCustId(cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMER_ID)));
                    customerRetro.setName(cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_NAME)));
                    String address = cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS));
                    customerRetro.setAddress(!TextUtils.isEmpty(address) ? address : "");
                    String need = cursor.getString(cursor.getColumnIndex(COLUMN_NEED));
                    customerRetro.setNeed(!TextUtils.isEmpty(need) ? need : "");
                    String job = cursor.getString(cursor.getColumnIndex(COLUMN_JOBS));
                    customerRetro.setJobs(!TextUtils.isEmpty(job) ? job : "");
                    String dob = cursor.getString(cursor.getColumnIndex(COLUMN_DOB));
                    customerRetro.setDob(!TextUtils.isEmpty(dob) ? dob : "");

                    String phone = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE));
                    ArrayList<String> listPhone = new ArrayList<>();
                    if (!TextUtils.isEmpty(phone)) {
                        if (phone.contains(",")) {
                            String[] phones = phone.split(",");
                            for (String p : phones) {
                                listPhone.add(p);
                            }
                        } else {
                            listPhone.add(phone);
                        }

                        customerRetro.setPhone(listPhone);
                    }

                    String email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));
                    ArrayList<String> listEmail = new ArrayList<>();
                    if (!TextUtils.isEmpty(email)) {
                        if (email.contains(",")) {
                            String[] emails = email.split(",");
                            for (String p : emails) {
                                listEmail.add(p);
                            }
                        } else {
                            listEmail.add(email);
                        }

                        customerRetro.setEmail(listEmail);
                    }

                    String tag = cursor.getString(cursor.getColumnIndex(COLUMN_HASH_TAG));
                    ArrayList<String> listTag = new ArrayList<>();
                    if (!TextUtils.isEmpty(tag)) {
                        if (tag.contains(",")) {
                            String[] tags = tag.split(",");
                            for (String p : tags) {
                                listTag.add(p);
                            }
                        } else {
                            listTag.add(tag);
                        }

                        customerRetro.setTags(listTag);
                    }

                    list.add(customerRetro);

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

    public CustomerRetro getCustomerById(String customerId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        String selectQuery = "select * from " + TABLE_CUSTOMER + " where " + COLUMN_CUSTOMER_ID + " ='" + customerId + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        CustomerRetro customerRetro = null;
        try {
            if (cursor != null && cursor.moveToFirst()) {
                customerRetro = new CustomerRetro();
                customerRetro.setCustId(cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMER_ID)));
                customerRetro.setName(cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_NAME)));
                customerRetro.setAddress(cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)));
                customerRetro.setNeed(cursor.getString(cursor.getColumnIndex(COLUMN_NEED)));
                customerRetro.setJobs(cursor.getString(cursor.getColumnIndex(COLUMN_JOBS)));
                customerRetro.setDob(cursor.getString(cursor.getColumnIndex(COLUMN_DOB)));

                String phone = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE));
                ArrayList<String> listPhone = new ArrayList<>();
                if (!TextUtils.isEmpty(phone)) {
                    if (phone.contains(",")) {
                        String[] phones = phone.split(",");
                        for (String p : phones) {
                            listPhone.add(p);
                        }
                    } else {
                        listPhone.add(phone);
                    }

                    customerRetro.setPhone(listPhone);
                }

                String email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));
                ArrayList<String> listEmail = new ArrayList<>();
                if (!TextUtils.isEmpty(email)) {
                    if (email.contains(",")) {
                        String[] emails = email.split(",");
                        for (String p : emails) {
                            listEmail.add(p);
                        }
                    } else {
                        listEmail.add(email);
                    }

                    customerRetro.setEmail(listEmail);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return customerRetro;
    }

    public void closeDB() {
//        SQLiteDatabase db = helper.getReadableDatabase();
//        if (db != null && db.isOpen())
//            db.close();
    }
}
