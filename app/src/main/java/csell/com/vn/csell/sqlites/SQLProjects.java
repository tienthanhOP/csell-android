package csell.com.vn.csell.sqlites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.models.Project;

public class SQLProjects {

    private SQLDatabaseHelper helper;

    public static final String TABLE_PROJECT = "projects";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PROJECT_ID = "projectId";
    public static final String COLUMN_ADDRESS = "Address";
    public static final String COLUMN_CITY = "City";
    public static final String COLUMN_DATE_CREATED = "DateCreated";
    public static final String COLUMN_DESCRIPTION = "Description";
    public static final String COLUMN_DISTRICT = "District";
    public static final String COLUMN_IS_ACTIVED = "IsActived";
    public static final String COLUMN_DEAULT_IMAGE = "DefaultImage";
    public static final String COLUMN_PROJECT_NAME = "ProjectName";
    public static final String COLUMN_INVESTOR = "Investor";
    public static final String COLUMN_TOTAL_ACREAGE = "totalAcreage";
    public static final String COLUMN_TYPE_DEVELOPMENT = "typeDevelopment";
    public static final String COLUMN_PROJECT_SCALE = "projectScale";

    public static final String CREATE_TABLE_PROJECT = "CREATE TABLE " + TABLE_PROJECT + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_PROJECT_ID + " TEXT, "
            + COLUMN_ADDRESS + " TEXT, "
            + COLUMN_CITY + " TEXT, "
            + COLUMN_DATE_CREATED + " TEXT, "
            + COLUMN_DESCRIPTION + " TEXT, "
            + COLUMN_DISTRICT + " TEXT, "
            + COLUMN_DEAULT_IMAGE + " TEXT, "
            + COLUMN_IS_ACTIVED + " INTEGER, "
            + COLUMN_INVESTOR + " TEXT, "
            + COLUMN_TOTAL_ACREAGE + " TEXT, "
            + COLUMN_TYPE_DEVELOPMENT + " TEXT, "
            + COLUMN_PROJECT_SCALE + " TEXT, "
            + COLUMN_PROJECT_NAME + " TEXT)";

    public SQLProjects(Context context) {
        helper = SQLDatabaseHelper.getInstance(context);
    }

    public int checkExistData() {
        int count = 0;
        String sql = "SELECT * FROM " + TABLE_PROJECT + " LIMIT 3";
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
            db.delete(TABLE_PROJECT, null, null);
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

    public void insertProjects(List<Project> list) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            for (Project item : list) {
                values.put(COLUMN_PROJECT_ID, item.getProjectid());
                values.put(COLUMN_ADDRESS, item.getAddress());
                values.put(COLUMN_CITY, item.getCity());
                values.put(COLUMN_DESCRIPTION, item.getDescription());
                values.put(COLUMN_DISTRICT, item.getDistrict());
                values.put(COLUMN_PROJECT_NAME, item.getProjectName());
//                values.put(COLUMN_DEAULT_IMAGE, item.getImage());
                values.put(COLUMN_INVESTOR, item.getInvestor());
                values.put(COLUMN_TOTAL_ACREAGE, item.getTotalAcreage());
                values.put(COLUMN_TYPE_DEVELOPMENT, item.getTypeDevelopment());
                values.put(COLUMN_PROJECT_SCALE, item.getProjectScale());

                db.insert(TABLE_PROJECT, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
           if(BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void insertProject(Project project) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();

            values.put(COLUMN_PROJECT_ID, project.getProjectid());
            values.put(COLUMN_ADDRESS, project.getAddress());
            values.put(COLUMN_CITY, project.getCity());
//            values.put(COLUMN_DATE_CREATED, project.DateCreated + "");
            values.put(COLUMN_DESCRIPTION, project.getDescription());
            values.put(COLUMN_DISTRICT, project.getDistrict());
//            if (project.is_actived) {
//                values.put(COLUMN_IS_ACTIVED, 1);
//            } else {
//                values.put(COLUMN_IS_ACTIVED, 0);
//            }

            values.put(COLUMN_PROJECT_NAME, project.getProjectName());
//            values.put(COLUMN_DEAULT_IMAGE, project.getImage());

            db.insert(TABLE_PROJECT, null, values);

            db.setTransactionSuccessful();
        } catch (Exception e) {
           if(BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Project> getAllLimit() {
        List<Project> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select * from " + TABLE_PROJECT + " order by " + COLUMN_PROJECT_NAME + " asc" + " limit 100";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Project p = new Project();
                p.setProjectid(c.getString(c.getColumnIndex(COLUMN_PROJECT_ID)));
                p.setAddress(c.getString(c.getColumnIndex(COLUMN_ADDRESS)));
                p.setCity(c.getString(c.getColumnIndex(COLUMN_CITY)));
                p.setDescription(c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)));
                p.setDistrict(c.getString(c.getColumnIndex(COLUMN_DISTRICT)));
                p.setProjectName(c.getString(c.getColumnIndex(COLUMN_PROJECT_NAME)));
//                p.setImage(c.getString(c.getColumnIndex(COLUMN_DEAULT_IMAGE)));
                p.setInvestor(c.getString(c.getColumnIndex(COLUMN_INVESTOR)));
                p.setTotalAcreage(c.getString(c.getColumnIndex(COLUMN_TOTAL_ACREAGE)));
                p.setTypeDevelopment(c.getString(c.getColumnIndex(COLUMN_TYPE_DEVELOPMENT)));
                p.setProjectScale(c.getString(c.getColumnIndex(COLUMN_PROJECT_SCALE)));

                data.add(p);

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }

    public List<Project> getAll() {
        List<Project> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "select * from " + TABLE_PROJECT + " order by " + COLUMN_PROJECT_NAME + " asc";
        ;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Project p = new Project();
                p.setProjectid(c.getString(c.getColumnIndex(COLUMN_PROJECT_ID)));
                p.setAddress(c.getString(c.getColumnIndex(COLUMN_ADDRESS)));
                p.setCity(c.getString(c.getColumnIndex(COLUMN_CITY)));
                p.setDescription(c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)));
                p.setDistrict(c.getString(c.getColumnIndex(COLUMN_DISTRICT)));
                p.setProjectName(c.getString(c.getColumnIndex(COLUMN_PROJECT_NAME)));
//                p.setImage(c.getString(c.getColumnIndex(COLUMN_DEAULT_IMAGE)));
                p.setInvestor(c.getString(c.getColumnIndex(COLUMN_INVESTOR)));
                p.setTotalAcreage(c.getString(c.getColumnIndex(COLUMN_TOTAL_ACREAGE)));
                p.setTypeDevelopment(c.getString(c.getColumnIndex(COLUMN_TYPE_DEVELOPMENT)));
                p.setProjectScale(c.getString(c.getColumnIndex(COLUMN_PROJECT_SCALE)));
                data.add(p);

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }


    public List<Project> getProjectByLocation(String city, String district) {
        List<Project> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select * from " + TABLE_PROJECT + " where " + COLUMN_CITY + " like '%" + city.toLowerCase() + "%'"
                + " and " + COLUMN_DISTRICT + " like '%" + district.toLowerCase() + "%' "
                + " order by " + COLUMN_PROJECT_NAME + " asc";
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Project p = new Project();
                p.setProjectid(c.getString(c.getColumnIndex(COLUMN_PROJECT_ID)));
                p.setAddress(c.getString(c.getColumnIndex(COLUMN_ADDRESS)));
                p.setCity(c.getString(c.getColumnIndex(COLUMN_CITY)));
                p.setDescription(c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)));
                p.setDistrict(c.getString(c.getColumnIndex(COLUMN_DISTRICT)));
                p.setProjectName(c.getString(c.getColumnIndex(COLUMN_PROJECT_NAME)));
//                p.setImage(c.getString(c.getColumnIndex(COLUMN_DEAULT_IMAGE)));
                p.setInvestor(c.getString(c.getColumnIndex(COLUMN_INVESTOR)));
                p.setTotalAcreage(c.getString(c.getColumnIndex(COLUMN_TOTAL_ACREAGE)));
                p.setTypeDevelopment(c.getString(c.getColumnIndex(COLUMN_TYPE_DEVELOPMENT)));
                p.setProjectScale(c.getString(c.getColumnIndex(COLUMN_PROJECT_SCALE)));

                data.add(p);

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }

    public List<Project> getProjectByLocation(String search, String city, String district) {
        List<Project> data = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select * from " + TABLE_PROJECT + " where "
                + COLUMN_PROJECT_NAME + " like '&" + search + "%' and "
                + COLUMN_CITY + " like '%" + city.toLowerCase() + "%'"
                + " and " + COLUMN_DISTRICT + " like '%" + district.toLowerCase() + "%'"
                + " order by " + COLUMN_PROJECT_NAME + " asc";

        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Project p = new Project();
                p.setProjectid(c.getString(c.getColumnIndex(COLUMN_PROJECT_ID)));
                p.setAddress(c.getString(c.getColumnIndex(COLUMN_ADDRESS)));
                p.setCity(c.getString(c.getColumnIndex(COLUMN_CITY)));
                p.setDescription(c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)));
                p.setDistrict(c.getString(c.getColumnIndex(COLUMN_DISTRICT)));
//                p.set = c.getInt(c.getColumnIndex(COLUMN_IS_ACTIVED)) == 1 ? true : false;
                p.setProjectName(c.getString(c.getColumnIndex(COLUMN_PROJECT_NAME)));
//                p.setImage(c.getString(c.getColumnIndex(COLUMN_DEAULT_IMAGE)));

                data.add(p);

            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return data;
    }

    public Project getProjectById(String projectId) {
        Project p = new Project();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = null;
        try {
            String sql = "select * from " + TABLE_PROJECT + " where " + COLUMN_PROJECT_ID + " like '" + projectId + "'";
            c = db.rawQuery(sql, null);
            if (c.getCount() > 0) {
                c.moveToFirst();

                p.setProjectid(c.getString(c.getColumnIndex(COLUMN_PROJECT_ID)));
                p.setAddress(c.getString(c.getColumnIndex(COLUMN_ADDRESS)));
                p.setCity(c.getString(c.getColumnIndex(COLUMN_CITY)));
                p.setDescription(c.getString(c.getColumnIndex(COLUMN_DESCRIPTION)));
                p.setDistrict(c.getString(c.getColumnIndex(COLUMN_DISTRICT)));
                p.setProjectName(c.getString(c.getColumnIndex(COLUMN_PROJECT_NAME)));
//                p.setImage(c.getString(c.getColumnIndex(COLUMN_DEAULT_IMAGE)));
                p.setInvestor(c.getString(c.getColumnIndex(COLUMN_INVESTOR)));
                p.setTotalAcreage(c.getString(c.getColumnIndex(COLUMN_TOTAL_ACREAGE)));
                p.setTypeDevelopment(c.getString(c.getColumnIndex(COLUMN_TYPE_DEVELOPMENT)));
                p.setProjectScale(c.getString(c.getColumnIndex(COLUMN_PROJECT_SCALE)));

            }
        } catch (Exception e) {

        } finally {
            if (c != null) {
                c.close();
            }
            db.close();
        }

        return p;
    }


    public String getImageByProjectId(String projectId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = null;
        String url = "";
        try {
            String sql = "select " + COLUMN_DEAULT_IMAGE + " from " + TABLE_PROJECT + " where " + COLUMN_PROJECT_ID + " like '" + projectId + "'";
            c = db.rawQuery(sql, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                url = c.getString(c.getColumnIndex(COLUMN_DEAULT_IMAGE));
            }
        } catch (Exception e) {
           if(BuildConfig.DEBUG) Log.d(getClass().getSimpleName(), e.getMessage());
        } finally {
            if (c != null) {
                c.close();
            }
            db.close();
        }

        return url;
    }

    ;

}
