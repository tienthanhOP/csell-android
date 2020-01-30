package csell.com.vn.csell.commons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.StringCompare;
import csell.com.vn.csell.interfaces.CheckDownload;
import csell.com.vn.csell.models.Category;
import csell.com.vn.csell.models.CategoryV1;
import csell.com.vn.csell.models.City;
import csell.com.vn.csell.models.District;
import csell.com.vn.csell.models.NameLanguage;
import csell.com.vn.csell.models.PostTypeV1;
import csell.com.vn.csell.models.Project;
import csell.com.vn.csell.models.Properties;
import csell.com.vn.csell.models.PropertiesFilter;
import csell.com.vn.csell.models.PropertyValue;
import csell.com.vn.csell.models.Street;
import csell.com.vn.csell.models.Version;
import csell.com.vn.csell.models.Ward;
import csell.com.vn.csell.sqlites.SQLCategories;
import csell.com.vn.csell.sqlites.SQLCategoriesV1;
import csell.com.vn.csell.sqlites.SQLCitys;
import csell.com.vn.csell.sqlites.SQLDistricts;
import csell.com.vn.csell.sqlites.SQLLanguage;
import csell.com.vn.csell.sqlites.SQLPostTypesV1;
import csell.com.vn.csell.sqlites.SQLProjects;
import csell.com.vn.csell.sqlites.SQLProperties;
import csell.com.vn.csell.sqlites.SQLPropertiesFilter;
import csell.com.vn.csell.sqlites.SQLPropertyValue;
import csell.com.vn.csell.sqlites.SQLStreets;
import csell.com.vn.csell.sqlites.SQLWards;

@SuppressLint("StaticFieldLeak")
public class DownloadFile extends AsyncTask<String, Integer, String> {

    private CheckDownload mListener;

    private Context context;
    private String fileName;
    private String result = "";
    private long start = 0;
    private boolean isDownLoaded = false;

    public DownloadFile(Context context, String fileName, CheckDownload mListener) {
        this.context = context;
        this.fileName = fileName;
        this.mListener = mListener;
    }

    @Override
    protected String doInBackground(String... strings) {
        start = System.currentTimeMillis();
        InputStream input;
        HttpURLConnection connection;
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(true);
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP "
                        + connection.getResponseCode() + " "
                        + connection.getResponseMessage();
            }

            // download the file
            input = connection.getInputStream();

            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(input, "utf-8"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line).append("\n");
                }

                input.close();
                result = sBuilder.toString();
                getVersions();

            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    Log.e("StringBuilding", "Error converting result " + e.toString());
            }
        } catch (IOException e) {
            Crashlytics.logException(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mListener.onGetStatus(true, fileName, isDownLoaded);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    private void checkDownload(String data, String name) {
        switch (name) {
            case StringCompare.cate:
                getCategoriesV1(data);
                break;
            case StringCompare.property:
                getProperties(data);
                break;
            case StringCompare.citys:
                getCitys(data);
                break;
            case StringCompare.districts:
                getDistricts(data);
                break;
            case StringCompare.wards:
                getWards(data);
                break;
            case StringCompare.streets:
                getStreets(data);
                break;
            case StringCompare.privacy:
                getPrivacyType(data);
                break;
            case StringCompare.project:
                getProjects(data);
                break;
            case StringCompare.language:
                getLanguage(data);
                break;
            case StringCompare.filter:
                getFilter(data);
                break;
            case StringCompare.filter_categories:
                getFilterCategories(data);
                break;
            case StringCompare.filter_properties:
                getFilterProperties(data);
                break;
        }
    }

    private void checkDownloadData(String link, String name) {
        start = System.currentTimeMillis();
        InputStream input;
        HttpURLConnection connection;
        try {
            URL url = new URL(link);
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(true);
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return;
            }

            // download the file
            input = connection.getInputStream();

            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(input, "utf-8"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line).append("\n");
                }

                input.close();
                String result = sBuilder.toString();

                checkDownload(result, name);

            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    Log.e("StringBuilding", "Error converting result " + e.toString());
            }
        } catch (IOException e) {
            Crashlytics.logException(e);
        }

    }

    @SuppressLint("StaticFieldLeak")
    private void getVersions() {
        try {

            Version version = null;
            try {
                Gson gson2 = new Gson();
                Type listType = new TypeToken<Version>() {
                }.getType();

                version = gson2.fromJson(result, listType);
            } catch (JsonSyntaxException e) {
                if (BuildConfig.DEBUG) Log.d("ABCD", "" + e.getMessage());
            }

            if (version != null) {
                FileSave fileSave = new FileSave(context, Constants.GET);
                FileSave filePut = new FileSave(context, Constants.PUT);

                if (fileSave.getCategoryVersion() < version.getCategoryVersion()) {
                    filePut.putCategoryVersion(version.getCategoryVersion());

                    checkDownloadData(Constants.JSON_URL_CATEGORY, StringCompare.cate);
                    isDownLoaded = true;
                } else {
                    isDownLoaded = false;
                }

                if (fileSave.getPropertyVersion() < version.getPropertyVersion()) {
                    filePut.putPropertyVersion(version.getPropertyVersion());

                    checkDownloadData(Constants.JSON_URL_PROPERTY, StringCompare.property);
                    isDownLoaded = true;
                } else {
                    isDownLoaded = false;
                }

                if (fileSave.getLocationVersion() < version.getLocationVersion()) {
                    filePut.putLocationVersion(version.getLocationVersion());

                    checkDownloadData(Constants.JSON_URL_CITYS, StringCompare.citys);
                    checkDownloadData(Constants.JSON_URL_DISTRICTS, StringCompare.districts);
                    checkDownloadData(Constants.JSON_URL_WARDS, StringCompare.wards);
                    checkDownloadData(Constants.JSON_URL_STREETS, StringCompare.streets);
                    isDownLoaded = true;
                } else {
                    isDownLoaded = false;
                }

                if (fileSave.getPrivacyVersion() < version.getPrivacyVersion()) {
                    filePut.putPrivacyVersion(version.getPrivacyVersion());

                    checkDownloadData(Constants.JSON_URL_PRIVACY, StringCompare.privacy);
                    isDownLoaded = true;
                } else {
                    isDownLoaded = false;
                }

                if (fileSave.getLanguageVersion() < version.getLanguageVersion()) {
                    filePut.putLanguageVersion(version.getLanguageVersion());

                    checkDownloadData(Constants.JSON_URL_LANGUAGE, StringCompare.language);
                    isDownLoaded = true;
                } else {
                    isDownLoaded = false;
                }

                filePut.putAPKVersion(version.getApkVersion() == null ? -1 : version.getApkVersion());

                if (fileSave.getFilterVersion() < version.getFilterVersion()) {
                    filePut.putFilterVersion(version.getFilterVersion());

                    checkDownloadData(Constants.JSON_URL_FILTER, StringCompare.filter);
                    checkDownloadData(Constants.JSON_URL_FILTER_CATEGORIES, StringCompare.filter_categories);
                    checkDownloadData(Constants.JSON_URL_FILTER_PROPERTIES, StringCompare.filter_properties);
                    isDownLoaded = true;
                } else {
                    isDownLoaded = false;
                }

                if (BuildConfig.DEBUG)
                    Log.w("TEST_VERSION_CATE", version.getCategoryVersion() + "");
                if (BuildConfig.DEBUG)
                    Log.w("TEST_VERSION_PROP", version.getPropertyVersion() + "");
                if (BuildConfig.DEBUG)
                    Log.w("TEST_VERSION_LOCATION", version.getLocationVersion() + "");
                if (BuildConfig.DEBUG)
                    Log.w("TEST_VERSION_PRIVACY", version.getPrivacyVersion() + "");
                if (BuildConfig.DEBUG)
                    Log.w("TEST_VERSION_LANGUAGE", version.getLanguageVersion() + "");
                if (BuildConfig.DEBUG)
                    Log.w("TEST_VERSION_FILTER", version.getFilterVersion() + "");
            }

            long end = System.currentTimeMillis() - start;
            if (BuildConfig.DEBUG) Log.d("JSON_VERSION_TIME", end + "");

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("JSON_EXCEPTION1", e + "");
            Crashlytics.logException(e);
        }
    }

    private void getCitys(String result) {
        try {
            SQLCitys sqlCitys = new SQLCitys(context);

            List<City> JSON = null;
            try {
                Gson gson2 = new Gson();
                Type listType = new TypeToken<List<City>>() {
                }.getType();

                JSON = gson2.fromJson(result, listType);
            } catch (JsonSyntaxException e) {
                if (BuildConfig.DEBUG) Log.d("ABCD", "" + e.getMessage());
            }

            if (JSON != null) {

                sqlCitys.clearData();
                sqlCitys.insertCitys(JSON);

                mListener.onGetStatus(true, "citys", true);
            } else {
                mListener.onGetStatus(false, "citys", false);
            }

            long end = System.currentTimeMillis() - start;
            if (BuildConfig.DEBUG) Log.d("JSON_CITYS_TIME", end + "");

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("JSON_EXCEPTION1", e + "");
            Crashlytics.logException(e);
        }
    }

    private void getDistricts(String result) {
        try {
            SQLDistricts sqlDistricts = new SQLDistricts(context);

            List<District> JSON = null;
            try {
                Gson gson2 = new Gson();
                Type listType = new TypeToken<List<District>>() {
                }.getType();

                JSON = gson2.fromJson(result, listType);
            } catch (JsonSyntaxException e) {
                if (BuildConfig.DEBUG) Log.d("ABCD", "" + e.getMessage());
            }

            if (JSON != null) {

                sqlDistricts.clearData();
                sqlDistricts.insertDistricts(JSON);

                mListener.onGetStatus(true, "districts", true);
            } else {
                mListener.onGetStatus(false, "districts", false);
            }

            long end = System.currentTimeMillis() - start;
            if (BuildConfig.DEBUG) Log.d("JSON_DISTRICTS_TIME", end + "");

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("JSON_EXCEPTION1", e + "");
            Crashlytics.logException(e);
        }
    }

    private void getWards(String result) {
        try {
            SQLWards sqlWards = new SQLWards(context);

            List<Ward> JSON = null;
            try {
                Gson gson2 = new Gson();
                Type listType = new TypeToken<List<Ward>>() {
                }.getType();

                JSON = gson2.fromJson(result, listType);
            } catch (JsonSyntaxException e) {
                if (BuildConfig.DEBUG) Log.d("ABCD", "" + e.getMessage());
            }

            if (JSON != null) {

                sqlWards.clearData();
                sqlWards.insertWards(JSON);

                mListener.onGetStatus(true, "wards", true);
            } else {
                mListener.onGetStatus(false, "wards", false);
            }

            long end = System.currentTimeMillis() - start;
            if (BuildConfig.DEBUG) Log.d("JSON_STREETS_TIME", end + "");

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("JSON_EXCEPTION1", e + "");
            Crashlytics.logException(e);
        }
    }

    private void getStreets(String result) {
        try {
            SQLStreets sqlStreets = new SQLStreets(context);

            List<Street> JSON = null;
            try {
                Gson gson2 = new Gson();
                Type listType = new TypeToken<List<Street>>() {
                }.getType();

                JSON = gson2.fromJson(result, listType);
            } catch (JsonSyntaxException e) {
                if (BuildConfig.DEBUG) Log.d("ABCD", "" + e.getMessage());
            }

            if (JSON != null) {

                sqlStreets.clearData();
                sqlStreets.insertStreets(JSON);

                mListener.onGetStatus(true, "streets", true);
            } else {
                mListener.onGetStatus(false, "streets", false);
            }

            long end = System.currentTimeMillis() - start;
            if (BuildConfig.DEBUG) Log.d("JSON_STREETS_TIME", end + "");

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("JSON_EXCEPTION1", e + "");
            Crashlytics.logException(e);
        }
    }

    private void getCategories(String result) {
        try {
            SQLCategories sqlCategories = new SQLCategories(context);
            List<Category> JSON = null;
            try {
                Gson gson2 = new Gson();
                Type listType = new TypeToken<List<Category>>() {
                }.getType();

                JSON = gson2.fromJson(result, listType);
            } catch (JsonSyntaxException e) {
                if (BuildConfig.DEBUG) Log.d("ABCD", "" + e.getMessage());
            }

            if (JSON != null) {
                sqlCategories.clearData();
                sqlCategories.insertCategories(JSON);
                mListener.onGetStatus(true, "cate", true);
            } else {
                mListener.onGetStatus(false, "cate", false);
            }

            long end = System.currentTimeMillis() - start;
            if (BuildConfig.DEBUG) Log.d("JSON_CATEOGRY_TIME", end + "");

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("JSON_EXCEPTION1", e + "");
            Crashlytics.logException(e);
        }
    }

    private void getCategoriesV1(String result) {
        try {
            SQLCategoriesV1 sqlCategoriesV1 = new SQLCategoriesV1(context);
            List<CategoryV1> JSON = null;
            try {
                Gson gson2 = new Gson();
                Type listType = new TypeToken<List<CategoryV1>>() {
                }.getType();

                JSON = gson2.fromJson(result, listType);
            } catch (JsonSyntaxException e) {
                if (BuildConfig.DEBUG) Log.d("ABCD", "" + e.getMessage());
            }

            if (JSON != null) {
                sqlCategoriesV1.clearData();
                sqlCategoriesV1.insertCategories(JSON);
                mListener.onGetStatus(true, "cate", true);
            } else {
                mListener.onGetStatus(false, "cate", false);
            }

            long end = System.currentTimeMillis() - start;
            if (BuildConfig.DEBUG) Log.d("JSON_CATEOGRY_TIME", end + "");
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("JSON_EXCEPTION2", e + "");
            Crashlytics.logException(e);
        }
    }

    private void getProperties(String result) {
        try {
            SQLProperties sqlProperties = new SQLProperties(context);
            SQLPropertyValue sqlPropertyValue = new SQLPropertyValue(context);

            List<PropertyValue> lsPropertyValues = new ArrayList<>();

            List<Properties> JSON = null;
            try {
                Gson gson2 = new Gson();
                Type listType = new TypeToken<List<Properties>>() {
                }.getType();

                JSON = gson2.fromJson(result, listType);

            } catch (JsonSyntaxException e) {
                if (BuildConfig.DEBUG) Log.d("ABCD", "" + e.getMessage());
            }


            if (JSON != null) {
                for (Properties item : JSON) {
                    for (int i = 0; i < item.property_default_values.size(); i++) {
                        PropertyValue propertyValue = new PropertyValue();
                        propertyValue.property_name = item.property_name;
                        propertyValue.category_id = item.category_id;
                        propertyValue.key = item.property_default_values.get(i).key;
                        propertyValue.value = item.property_default_values.get(i).value + "";
                        lsPropertyValues.add(propertyValue);
                    }
                }


                sqlProperties.clearData();
                sqlProperties.insertProperty(JSON);

                sqlPropertyValue.clearData();
                sqlPropertyValue.insertProperty(lsPropertyValues);
                mListener.onGetStatus(true, StringCompare.property, true);
            } else {
                mListener.onGetStatus(false, StringCompare.property, false);
            }

            long end = System.currentTimeMillis() - start;
            if (BuildConfig.DEBUG) Log.d("JSON_PROPERTY_TIME", end + "");
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("JSON_EXCEPTION2", e + "");
            Crashlytics.logException(e);
        }
    }

    private void getProjects(String result) {
        try {
            SQLProjects sqlProjects = new SQLProjects(context);
            List<Project> JSON = null;
            try {
                Gson gson2 = new Gson();
                Type listType = new TypeToken<List<Project>>() {
                }.getType();

                JSON = gson2.fromJson(result, listType);
            } catch (JsonSyntaxException e) {
                if (BuildConfig.DEBUG) Log.d("ABCD", "" + e.getMessage());
            }

            if (JSON != null) {

                sqlProjects.clearData();
                sqlProjects.insertProjects(JSON);

                mListener.onGetStatus(true, StringCompare.project, true);
            } else {
                mListener.onGetStatus(false, StringCompare.project, false);
            }
            long end = System.currentTimeMillis() - start;
            if (BuildConfig.DEBUG) Log.d("JSON_PROJECT_TIME", end + "");
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("JSON_EXCEPTION3", e + "");
            Crashlytics.logException(e);
        }
    }

    private void getPrivacyType(String result) {
        try {
            SQLPostTypesV1 postTypes = new SQLPostTypesV1(context);
            List<PostTypeV1> JSON = null;
            try {
                Gson gson2 = new Gson();
                Type listType = new TypeToken<List<PostTypeV1>>() {
                }.getType();

                JSON = gson2.fromJson(result, listType);
            } catch (JsonSyntaxException e) {
                if (BuildConfig.DEBUG) Log.d("ABCD", "" + e.getMessage());
            }

            if (JSON != null) {

                postTypes.clearData();
                postTypes.insertPostType(JSON);
                mListener.onGetStatus(true, StringCompare.privacy, true);
            } else {
                mListener.onGetStatus(false, StringCompare.privacy, false);
            }

            long end = System.currentTimeMillis() - start;
            if (BuildConfig.DEBUG) Log.d("JSON_PRIVACY_TIME", end + "");
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("JSON_EXCEPTION3", e + "");
            Crashlytics.logException(e);
        }
    }

    private void getLanguage(String result) {
        try {
            SQLLanguage sqlLanguage = new SQLLanguage(context);

            List<NameLanguage> JSON = null;
            try {
                Gson gson2 = new Gson();
                Type listType = new TypeToken<List<NameLanguage>>() {
                }.getType();

                JSON = gson2.fromJson(result, listType);
            } catch (JsonSyntaxException e) {
                if (BuildConfig.DEBUG) Log.d("ABCD", "" + e.getMessage());
            }

            if (JSON != null) {

                sqlLanguage.clearData();
                sqlLanguage.insertLanguage(JSON);

                mListener.onGetStatus(true, StringCompare.language, true);
            } else {
                mListener.onGetStatus(false, StringCompare.language, false);
            }

            long end = System.currentTimeMillis() - start;
            if (BuildConfig.DEBUG) Log.d("JSON_LANGUAGE_TIME", end + "");

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("JSON_EXCEPTION1", e + "");
            Crashlytics.logException(e);
        }
    }

    private void getFilter(String result) {
        try {
            SQLPropertiesFilter sqlPropertiesFilter = new SQLPropertiesFilter(context);

            List<PropertiesFilter> JSON = null;
            try {
                Gson gson2 = new Gson();
                Type listType = new TypeToken<List<PropertiesFilter>>() {
                }.getType();

                JSON = gson2.fromJson(result, listType);
            } catch (JsonSyntaxException e) {
                if (BuildConfig.DEBUG) Log.d("ABCD", "" + e.getMessage());
            }

            if (JSON != null) {

                sqlPropertiesFilter.clearData();
                sqlPropertiesFilter.insertPropertyFilter(JSON);

                mListener.onGetStatus(true, StringCompare.filter, true);
            } else {
                mListener.onGetStatus(false, StringCompare.filter, false);
            }

            long end = System.currentTimeMillis() - start;
            if (BuildConfig.DEBUG) Log.d("JSON_FILTER_TIME", end + "");

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("JSON_EXCEPTION1", e + "");
            Crashlytics.logException(e);
        }
    }

    private void getFilterCategories(String result) {

    }

    private void getFilterProperties(String result) {

    }
}
