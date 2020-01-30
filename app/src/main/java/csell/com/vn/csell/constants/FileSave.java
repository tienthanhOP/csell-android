package csell.com.vn.csell.constants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by chuc.nq on 3/2/2018.
 */

public class FileSave {

    private Context context;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;

    @SuppressLint("CommitPrefEdits")
    public FileSave(Context context, String type) {

        super();
        this.context = context;
        if (context == null) {
            return;
        } else if (type.equals(Constants.PUT)) {
            editor = context.getSharedPreferences(Constants.TEMP_CSELL_APP, Context.MODE_PRIVATE).edit();
        } else {
            if (type.equals(Constants.GET)) {
                preferences = context.getSharedPreferences(Constants.TEMP_CSELL_APP, Context.MODE_PRIVATE);
            }
        }
    }

    public void cleanFileSave() {
        editor.putBoolean(Constants.KEY_PREFERENCES_IS_FINGERPRINT, false);
        editor.putString(Constants.KEY_PREFERENCES_USER_ID_NEW, "");
        editor.putBoolean(Constants.KEY_PREFERENCES_NEW_VERSION, false);
        editor.putString(Constants.KEY_PREFERENCES_USER_ID_FIREBASE, "");
        editor.putString(Constants.KEY_PREFERENCES_USER_NAME, "");
        editor.putString(Constants.KEY_PREFERENCES_DISPLAYNAME, "");
        editor.putString(Constants.KEY_PREFERENCES_USER_EMAIL, "");
        editor.putString(Constants.KEY_PREFERENCES_USER_PHONE, "");
        editor.putString(Constants.KEY_PREFERENCES_USER_AVATAR, "");
        editor.putString(Constants.KEY_PREFERENCES_USER_STATUS, "");
        editor.putString(Constants.KEY_STRING_ROOT_CATEGORY_ID, "");
        editor.putString(Constants.KEY_STRING_ROOT_CATEGORY_DISPLAYNAME, "");
        editor.putString(Constants.KEY_JSON_SELECT_CATEGORY, "");
        editor.putInt(Constants.KEY_INT_LOCATION_CITY_ID, -1);
        editor.putString(Constants.KEY_STRING_LOCATION_CITY_NAME, "");
        editor.putInt(Constants.KEY_INT_LOCATION_DISTRICT_ID, -1);
        editor.putString(Constants.KEY_STRING_LOCATION_DISTRICT_NAME, "");
        editor.putInt(Constants.KEY_INT_CURRENT_CID, 0);
        editor.putInt(Constants.KEY_INT_MAX_LEVEL, 0);
        editor.putInt(Constants.KEY_CHOOSE_SUB1_CATEGORY, -1);
        editor.putInt(Constants.KEY_CHOOSE_SUB2_CATEGORY, -1);
        editor.apply();
    }

    public boolean isFingerprint() {
        if (context == null) return false;
        return preferences.getBoolean(Constants.KEY_PREFERENCES_IS_FINGERPRINT, false);
    }

    public void putFingerprint(boolean isFingerprint) {
        if (context == null) return;
        editor.putBoolean(Constants.KEY_PREFERENCES_IS_FINGERPRINT, isFingerprint);
        editor.commit();
    }

    public String getUserIdFingferprint() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_PREFERENCES_USER_ID_NEW, "");
    }

    public void putUserIFingerprint(String userId) {
        if (context == null) return;
        editor.putString(Constants.KEY_PREFERENCES_USER_ID_NEW, userId);
        editor.commit();
    }

    public boolean isNewVersion() {
        if (context == null) return false;
        return preferences.getBoolean(Constants.KEY_PREFERENCES_NEW_VERSION, false);
    }

    public void putNewVersion(boolean version) {
        if (context == null) return;
        editor.putBoolean(Constants.KEY_PREFERENCES_NEW_VERSION, version);
        editor.commit();
    }

    public boolean isAutoLogin() {
        if (context == null) return false;

        return preferences.getBoolean(Constants.KEY_PREFERENCES_AUTO_LOGIN, false);
    }

    public void putAutoLogin(boolean autoLogin) {
        if (context == null) return;
        editor.putBoolean(Constants.KEY_PREFERENCES_AUTO_LOGIN, autoLogin);
        editor.commit();
    }

    public String getUserId() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_PREFERENCES_USER_ID_FIREBASE, "");
    }

    public void putUserId(String userId) {
        if (context == null) return;
        editor.putString(Constants.KEY_PREFERENCES_USER_ID_FIREBASE, userId);
        editor.commit();
    }

    public String getUserName() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_PREFERENCES_USER_NAME, "");
    }

    public void putUserName(String userName) {
        if (context == null) return;
        editor.putString(Constants.KEY_PREFERENCES_USER_NAME, userName);
        editor.commit();
    }

    public String getDisplayName() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_PREFERENCES_DISPLAYNAME, "");
    }

    public void putDisplayName(String displayName) {
        if (context == null) return;
        editor.putString(Constants.KEY_PREFERENCES_DISPLAYNAME, displayName);
        editor.commit();
    }

    public String getUserEmail() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_PREFERENCES_USER_EMAIL, "");

    }

    public void putUserEmail(String userEmail) {
        if (context == null) return;
        editor.putString(Constants.KEY_PREFERENCES_USER_EMAIL, userEmail);
        editor.commit();
    }

    public String getUserPhone() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_PREFERENCES_USER_PHONE, "");

    }

    public void putUserPhone(String userPhone) {
        if (context == null) return;
        editor.putString(Constants.KEY_PREFERENCES_USER_PHONE, userPhone);
        editor.commit();
    }

    public String getUserAvatar() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_PREFERENCES_USER_AVATAR, "");

    }

    public void putUserAvatar(String userAvatar) {
        if (context == null) return;
        editor.putString(Constants.KEY_PREFERENCES_USER_AVATAR, userAvatar);
        editor.commit();
    }

    public String getUserStatus() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_PREFERENCES_USER_STATUS, "");

    }

    public void putUserStatus(String userStatus) {
        if (context == null) return;
        editor.putString(Constants.KEY_PREFERENCES_USER_STATUS, userStatus);
        editor.commit();
    }

    public String getRootCategoryId() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.KEY_STRING_ROOT_CATEGORY_ID, "");

    }

    public void putRootCategoryId(String rootCategoryId) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.KEY_STRING_ROOT_CATEGORY_ID, rootCategoryId);
        editor.commit();
    }

    public String getRootCategoryDisplayName() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.KEY_STRING_ROOT_CATEGORY_DISPLAYNAME, "");

    }

    public void putRootCategoryDisplayName(String rootCategoryDisplayName) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.KEY_STRING_ROOT_CATEGORY_DISPLAYNAME, rootCategoryDisplayName);
        editor.commit();
    }

    public String getSelectCategoryJSON() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.KEY_JSON_SELECT_CATEGORY, "");

    }

    public void putSelectCategoryJSON(String selectCategoryJSON) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.KEY_JSON_SELECT_CATEGORY, selectCategoryJSON);
        editor.commit();
    }

    public String getLocationCityId() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.KEY_INT_LOCATION_CITY_ID, "");
    }

    public void putLocationCityId(String locationCityId) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.KEY_INT_LOCATION_CITY_ID, locationCityId);
        editor.commit();
    }

    public String getLocationCityName() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.KEY_STRING_LOCATION_CITY_NAME, "");

    }

    public void putLocationCityName(String locationCityName) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.KEY_STRING_LOCATION_CITY_NAME, locationCityName);
        editor.commit();
    }

    public int getLocationDistrictId() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt(Constants.KEY_INT_LOCATION_DISTRICT_ID, -1);

    }

    public void putLocationDisTrictId(int locationDisTrictId) {
        if (context == null) {
            return;
        }
        editor.putInt(Constants.KEY_INT_LOCATION_DISTRICT_ID, locationDisTrictId);
        editor.commit();
    }

    public String getLocationDistrictName() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.KEY_STRING_LOCATION_DISTRICT_NAME, "");

    }

    public void putLocationDistrictName(String locationDistrictName) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.KEY_STRING_LOCATION_DISTRICT_NAME, locationDistrictName);
        editor.commit();
    }

    public int getCurrentCategoryId() {
        if (context == null) {
            return 0;
        }
        return preferences.getInt(Constants.KEY_INT_CURRENT_CID, 0);

    }

    public void putCurrentCategoryId(int currentId) {
        if (context == null) {
            return;
        }
        editor.putInt(Constants.KEY_INT_CURRENT_CID, currentId);
        editor.commit();
    }

    public int getMaxLevelCategory() {
        if (context == null) {
            return 0;
        }
        return preferences.getInt(Constants.KEY_INT_MAX_LEVEL, 0);
    }

    public void putMaxLevelCategory(int maxLevel) {
        if (context == null) {
            return;
        }
        editor.putInt(Constants.KEY_INT_MAX_LEVEL, maxLevel);
        editor.commit();
    }

    public int getPosChooseCate() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt(Constants.KEY_CHOOSE_CATEGORY, -1);

    }

    public void putPosChooseCate(int pos) {
        if (context == null) {
            return;
        }
        editor.putInt(Constants.KEY_CHOOSE_CATEGORY, pos);
        editor.commit();
    }

    public int getPosChooseSub1Cate() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt(Constants.KEY_CHOOSE_SUB1_CATEGORY, -1);

    }

    public void putPosChooseSub1Cate(int pos) {
        if (context == null) {
            return;
        }
        editor.putInt(Constants.KEY_CHOOSE_SUB1_CATEGORY, pos);
        editor.commit();
    }

    public int getPosChooseSub2Cate() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt(Constants.KEY_CHOOSE_SUB2_CATEGORY, -1);

    }

    public void putPosChooseSub2Cate(int pos) {
        if (context == null) {
            return;
        }
        editor.putInt(Constants.KEY_CHOOSE_SUB2_CATEGORY, pos);
        editor.commit();
    }

    public int getPosChooseCity() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt(Constants.KEY_CHOOSE_CITY, -1);

    }

    public void putPosChooseCity(int pos) {
        if (context == null) {
            return;
        }
        editor.putInt(Constants.KEY_CHOOSE_CITY, pos);
        editor.commit();
    }

    public int getPosChooseDistricts() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt(Constants.KEY_CHOOSE_DISTRICTS, -1);

    }

    public void putPosChooseDistricts(int pos) {
        if (context == null) {
            return;
        }
        editor.putInt(Constants.KEY_CHOOSE_DISTRICTS, pos);
        editor.commit();
    }

    public int getPosChoosePriceMin() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt(Constants.KEY_CHOOSE_PRICE_MIN, -1);

    }

    public void putPosChoosePriceMin(int pos) {
        if (context == null) {
            return;
        }
        editor.putInt(Constants.KEY_CHOOSE_PRICE_MIN, pos);
        editor.commit();
    }

    public int getPosChoosePriceMax() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt(Constants.KEY_CHOOSE_PRICE_MAX, -1);

    }

    public void putPosChoosePriceMax(int pos) {
        if (context == null) {
            return;
        }
        editor.putInt(Constants.KEY_CHOOSE_PRICE_MAX, pos);
        editor.commit();
    }

    public String getProductIdCurrentSelect() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.KEY_PRODUCT_KEY, "");

    }

    public void putProductIdCurrentSelect(String productKey) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.KEY_PRODUCT_KEY, productKey);
        editor.commit();
    }

    public String getProjectId() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.KEY_PROJECT_ID, "");

    }

    public void putProjectId(String projectId) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.KEY_PROJECT_ID, projectId);
        editor.commit();
    }

    public String getProjectName() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.KEY_PROJECT_NAME, "");

    }

    public void putProjectName(String projectName) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.KEY_PROJECT_NAME, projectName);
        editor.commit();
    }

//    public String getProjectImage() {
//        if (context == null) {
//            return "";
//        }
//        return preferences.getString(Constants.KEY_PROJECT_IMAGE, "");
//
//    }
//
//    public void putProjectImage(List<String> projectImage) {
//        if (context == null) {
//            return;
//        }
//        editor.putString(Constants.KEY_PROJECT_IMAGE, projectImage);
//        editor.commit();
//    }

    public String getProjectCity() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.KEY_PROJECT_CITY, "");
    }

    public void putProjectCity(String city) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.KEY_PROJECT_CITY, city);
        editor.commit();
    }

    public String getProjectDistrict() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.KEY_PROJECT_DISTRICT, "");
    }

    public void putProjectDistrict(String district) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.KEY_PROJECT_DISTRICT, district);
        editor.commit();
    }

    public int getPosChoosePublicLevel() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt(Constants.KEY_CHOOSE_PUBLIC_LEVEL, -1);

    }

    public void putPosChoosePublicLevel(int pos) {
        if (context == null) {
            return;
        }
        editor.putInt(Constants.KEY_CHOOSE_PUBLIC_LEVEL, pos);
        editor.commit();
    }

    public String getContactKey() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.KEY_CONTACT_ID, "");

    }

    public void putContactKey(String key) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.KEY_CONTACT_ID, key);
        editor.commit();
    }


    public void putDob(String dateofbirth) {
        if (context == null) {
            return;
        }
        editor.putString("DOB", dateofbirth);
        editor.commit();
    }

    public String getDateOfBirth() {
        if (context == null) {
            return "";
        }
        return preferences.getString("DOB", "");

    }

    public String getUserAccount() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_PREFERENCES_USER_ACCOUNT, "");

    }

    public void putUserAccount(String userAccount) {
        if (context == null) return;
        editor.putString(Constants.KEY_PREFERENCES_USER_ACCOUNT, userAccount);
        editor.commit();
    }

    public int getTypePostId() {
        if (context == null) return -1;
        return preferences.getInt(Constants.KEY_PREFERENCES_TYPE_POST, -1);

    }

    public String getUserAddress() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_PREFERENCES_USER_ADDRESS, "");

    }

    public void putTypePostId(int type) {
        if (context == null) return;
        editor.putInt(Constants.KEY_PREFERENCES_TYPE_POST, type);
        editor.commit();
    }

    public void putUserAddress(String userAddress) {
        if (context == null) return;
        editor.putString(Constants.KEY_PREFERENCES_USER_ADDRESS, userAddress);
        editor.commit();
    }

    public String getOwnerName() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_PREFERENCES_OWNER_NAME, "");

    }

    public void putOwnerName(String ownerName) {
        if (context == null) return;
        editor.putString(Constants.KEY_PREFERENCES_OWNER_NAME, ownerName);
        editor.commit();
    }

    public String getOwnerNote() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_PREFERENCES_OWNER_NOTE, "");

    }

    public void putOwnerNote(String ownerNote) {
        if (context == null) return;
        editor.putString(Constants.KEY_PREFERENCES_OWNER_NOTE, ownerNote);
        editor.commit();
    }

    public String getOwnerPhone() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_PREFERENCES_OWNER_NOTE, "");

    }

    public void putOwnerPhone(String ownerPhone) {
        if (context == null) return;
        editor.putString(Constants.KEY_PREFERENCES_OWNER_NOTE, ownerPhone);
        editor.commit();
    }

    public String getPriceCaptial() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_PRICE_CAPITAL, "");

    }

    public void putPriceCaptial(String priceCaptital) {
        if (context == null) return;
        editor.putString(Constants.KEY_PRICE_CAPITAL, priceCaptital);
        editor.commit();
    }

    public int getPosChooseLocation() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt(Constants.KEY_CHOOSE_LOCATION, -1);

    }

    public void putPosChooseLocation(int pos) {
        if (context == null) {
            return;
        }
        editor.putInt(Constants.KEY_CHOOSE_LOCATION, pos);
        editor.commit();
    }

    public String getProductUserId() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.KEY_PRODUCT_UID, "");

    }

    public void putProductUserId(String uid) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.KEY_PRODUCT_UID, uid);
        editor.commit();
    }

    public String getUserCityName() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.KEY_CITY_NAME, "");

    }

    public void putUserCityName(String userCityName) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.KEY_CITY_NAME, userCityName);
        editor.commit();
    }

    public Integer getUserCityId() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt(Constants.KEY_CITY_ID, -1);

    }

    public void putUserCityId(Integer userCityId) {
        if (context == null) {
            return;
        }
        editor.putInt(Constants.KEY_CITY_ID, userCityId);
        editor.commit();
    }

    public String getCategoryIdPostEmpty() {
        if (context == null) {
            return "";
        }
        return preferences.getString("CATE_POST_EMPTY", "");

    }

    public void putCategoryIdPostEmpty(String cateId) {
        if (context == null) {
            return;
        }
        editor.putString("CATE_POST_EMPTY", cateId);
        editor.commit();
    }

    public void putCategoryNamePostEmpty(String cateName) {
        if (context == null) {
            return;
        }
        editor.putString("CATE_NAME_POST_EMPTY", cateName);
        editor.commit();
    }

    public String getCategoryNamePostEmpty() {
        if (context == null) {
            return "";
        }
        return preferences.getString("CATE_NAME_POST_EMPTY", "");

    }

    public String getProjectIdPostEmpty() {
        if (context == null) {
            return "";
        }
        return preferences.getString("PROJECT_ID_POST_EMPTY", "");

    }

    public void putProjectIdPostEmpty(String projectId) {
        if (context == null) {
            return;
        }
        editor.putString("PROJECT_ID_POST_EMPTY", projectId);
        editor.commit();
    }

    public String getProjectNamePostEmpty() {
        if (context == null) {
            return "";
        }
        return preferences.getString("PROJECT_NAME_POST_EMPTY", "");

    }

    public void putProjectNamePostEmpty(String projectName) {
        if (context == null) {
            return;
        }
        editor.putString("PROJECT_NAME_POST_EMPTY", projectName);
        editor.commit();
    }


    public Set<String> getUserJob() {
        if (context == null) return null;
        return preferences.getStringSet(Constants.KEY_PREFERENCES_USER_JOBS, null);

    }

    public void putUserJob(Set<String> values) {
        if (context == null) return;
        editor.putStringSet(Constants.KEY_PREFERENCES_USER_JOBS, values);
        editor.commit();
    }

    public String getUserCover() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_PREFERENCES_USER_COVER, "");

    }

    public void putUserCover(String userCover) {
        if (context == null) return;
        editor.putString(Constants.KEY_PREFERENCES_USER_COVER, userCover);
        editor.commit();
    }


    public String getProjectCurrent() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_PROJECT_CURRENT, "");
    }

    public String getProjectCurrentFriend() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_PROJECT_CURRENT_FRIEND, "");
    }

    public void putProjectCurrent(String project) {
        if (context == null) return;
        editor.putString(Constants.KEY_PROJECT_CURRENT, project);
        editor.commit();
    }

    public void putProjectCurrentFriend(String project) {
        if (context == null) return;
        editor.putString(Constants.KEY_PROJECT_CURRENT_FRIEND, project);
        editor.commit();
    }

    public long getTimeRefreshToken() {
        if (context == null) return 0;
        return preferences.getLong(Constants.KEY_REFRESH_TOKEN, 0);
    }

    public void putTimeRefreshToken(long time) {
        if (context == null) return;
        editor.putLong(Constants.KEY_REFRESH_TOKEN, time);
        editor.commit();
    }


    public String getSelectCateCurrent() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_SELECT_CURRENT_CATE, "");
    }

    public String getSelectCateCurrentFriend() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_SELECT_CURRENT_CATE_FRIEND, "");
    }

    public void putSelectCateCurrent(String cat) {
        if (context == null) return;
        editor.putString(Constants.KEY_SELECT_CURRENT_CATE, cat);
        editor.commit();
    }

    public void putSelectCateCurrentFriend(String cat) {
        if (context == null) return;
        editor.putString(Constants.KEY_SELECT_CURRENT_CATE_FRIEND, cat);
        editor.commit();
    }

    public String getSelectProjectCurrent() {
        if (context == null) return "";
        return preferences.getString(Constants.KEY_SELECT_CURRENT_PROJECT, "");
    }

    public void putSelectProjectCurrent(String project) {
        if (context == null) return;
        editor.putString(Constants.KEY_SELECT_CURRENT_PROJECT, project);
        editor.commit();
    }

    public int getPosChooseSubCate() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt(Constants.KEY_CHOOSE_SUB_CATEGORY, -1);
    }

    public void putPosChooseSubCate(int pos) {
        if (context == null) {
            return;
        }
        editor.putInt(Constants.KEY_CHOOSE_SUB_CATEGORY, pos);
        editor.commit();
    }

    public String getToken() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.KEY_TOKEN, "");
    }

    public void putToken(String token) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.KEY_TOKEN, token);
        editor.commit();
    }

    public boolean getActiveMain() {
        if (context == null) {
            return false;
        }
        return preferences.getBoolean(Constants.KEY_ACTIVE_MAIN, false);
    }

    public void putActiveMain(boolean isActive) {
        if (context == null) {
            return;
        }
        editor.putBoolean(Constants.KEY_ACTIVE_MAIN, isActive);
        editor.commit();
    }

    public boolean getIsCreateProduct() {
        if (context == null) {
            return false;
        }
        return preferences.getBoolean(Constants.KEY_IS_CREATE_PRODUCT, false);
    }

    public void putIsCreateProduct(boolean isActive) {
        if (context == null) {
            return;
        }
        editor.putBoolean(Constants.KEY_IS_CREATE_PRODUCT, isActive);
        editor.commit();
    }

    public String getDeviceId() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.KEY_DEVICE_ID, "");
    }

    public void putDeviceId(String deviceId) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.KEY_DEVICE_ID, deviceId);
        editor.commit();
    }

    public int getCountCheckAllTab() {
        if (context == null) {
            return 0;
        }
        return preferences.getInt(Constants.KEY_COUNT_ALL_TAB, 0);
    }

    public void putCountCheckAllTab(int count) {
        if (context == null) {
            return;
        }
        editor.putInt(Constants.KEY_COUNT_ALL_TAB, count);
        editor.commit();
    }

    public boolean getCurrentCreateAtEndProduct() {
        if (context == null) {
            return false;
        }
        return preferences.getBoolean("TEMP_AT_END_PRODUCT", false);
    }

    public boolean getCurrentCreateAtFriendEndProduct() {
        if (context == null) {
            return false;
        }
        return preferences.getBoolean("TEMP_AT_FRIEND_END_PRODUCT", false);
    }

    public void putCurrentCreateAtEndProduct(boolean ok) {
        if (context == null) {
            return;
        }
        editor.putBoolean("TEMP_AT_END_PRODUCT", ok);
        editor.commit();
    }

    public void putCurrentCreateAtFriendEndProduct(boolean ok) {
        if (context == null) {
            return;
        }
        editor.putBoolean("TEMP_AT_FRIEND_END_PRODUCT", ok);
        editor.commit();
    }

    public boolean getCurrentCreateAtAllTab() {
        if (context == null) {
            return false;
        }
        return preferences.getBoolean("TEMP_AT_ALL", false);
    }

    public void putCurrentCreateAtAllTab(boolean ok) {
        if (context == null) {
            return;
        }
        editor.putBoolean("TEMP_AT_ALL", ok);
        editor.commit();
    }

    public int getCurrentPositionSocial() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt("TEMP_POSITIOB_SOCIAL", -1);
    }

    public void putCurrentPositionSocial(int pos) {
        if (context == null) {
            return;
        }
        editor.putInt("TEMP_POSITIOB_SOCIAL", pos);
        editor.commit();
    }

    public int getCategoryVersion() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt("VERSION_CATE", -1);
    }

    public void putCategoryVersion(int version) {
        if (context == null) {
            return;
        }
        editor.putInt("VERSION_CATE", version);
        editor.commit();
    }

    public int getPropertyVersion() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt("VERSION_PROP", -1);
    }

    public void putPropertyVersion(int version) {
        if (context == null) {
            return;
        }
        editor.putInt("VERSION_PROP", version);
        editor.commit();
    }

    public int getLocationVersion() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt("VERSION_LOCATION", -1);
    }

    public void putLocationVersion(int version) {
        if (context == null) {
            return;
        }
        editor.putInt("VERSION_LOCATION", version);
        editor.commit();
    }

    public int getPrivacyVersion() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt("VERSION_PRIVACY", -1);
    }

    public void putPrivacyVersion(int version) {
        if (context == null) {
            return;
        }
        editor.putInt("VERSION_PRIVACY", version);
        editor.commit();
    }

    public int getLanguageVersion() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt("VERSION_LANGUAGE", -1);
    }

    public void putLanguageVersion(int version) {
        if (context == null) {
            return;
        }
        editor.putInt("VERSION_LANGUAGE", version);
        editor.commit();
    }

    public int getFilterVersion() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt("VERSION_FILTER", -1);
    }

    public void putFilterVersion(int version) {
        if (context == null) {
            return;
        }
        editor.putInt("VERSION_FILTER", version);
        editor.commit();
    }

    public int getProjectVersion() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt("VERSION_PROJECT", -1);
    }

    public void putProjectVersion(int version) {
        if (context == null) {
            return;
        }
        editor.putInt("VERSION_PROJECT", version);
        editor.commit();
    }

    public int getAPKVersion() {
        if (context == null) {
            return -1;
        }
        return preferences.getInt("VERSION_APK", -1);
    }

    public void putAPKVersion(int version) {
        if (context == null) {
            return;
        }
        editor.putInt("VERSION_APK", version);
        editor.commit();
    }

    public void putFriendId(String uid) {
        if (context == null) {
            return;
        }
        editor.putString("FRIEND_ID", uid);
        editor.commit();
    }

    public String getFriendId() {
        if (context == null) {
            return "";
        }
        return preferences.getString("FRIEND_ID", "");
    }

    public void putSQLOldVersion(int uid) {
        if (context == null) {
            return;
        }
        editor.putInt("OLD_VERSION_SQL", uid);
        editor.commit();
    }

    public int getSQLOldVersion() {
        if (context == null) {
            return 0;
        }
        return preferences.getInt("OLD_VERSION_SQL", 0);
    }

    public void putSQLNewVersion(int uid) {
        if (context == null) {
            return;
        }
        editor.putInt("OLD_VERSION_SQL", uid);
        editor.commit();
    }

    public int getSQLNewVersion() {
        if (context == null) {
            return 0;
        }
        return preferences.getInt("OLD_VERSION_SQL", 0);
    }

    public Long getExpirationTime() {
        if (context == null) return null;
        return preferences.getLong(Constants.KEY_SELECT_CURRENT_PROJECT, -1);
    }

    public Long getSystemTime() {
        if (context == null) return null;
        return preferences.getLong(Constants.KEY_SELECT_CURRENT_PROJECT, -1);
    }

    public void setSystemTime(Long expTime) {
        if (context == null) return;
        editor.putLong(Constants.KEY_SELECT_CURRENT_PROJECT, expTime);
        editor.commit();
    }

    public void putExpirationTime(Long expTime) {
        if (context == null) return;
        editor.putLong(Constants.KEY_SELECT_CURRENT_PROJECT, expTime);
        editor.commit();
    }


    public String getProductNameCurrentSelect() {
        if (context == null) {
            return "";
        }
        return preferences.getString("PRODUCT_NAME_CURRENT", "");

    }

    public void putProductNameCurrentSelect(String name) {
        if (context == null) {
            return;
        }
        editor.putString("PRODUCT_NAME_CURRENT", name);
        editor.commit();
    }


    public String getPrefixCateTemp() {
        if (context == null) {
            return "";
        }
        return preferences.getString("PREFIX_CATE_TEMP", "");

    }

    public void putPrefixCateTemp(String name) {
        if (context == null) {
            return;
        }
        editor.putString("PREFIX_CATE_TEMP", name);
        editor.commit();
    }

    public boolean getExpired() {
        if (context == null) {
            return false;
        }
        return preferences.getBoolean("EXPIRED", false);

    }

    public void putExpired(boolean expried) {
        if (context == null) {
            return;
        }
        editor.putBoolean("EXPIRED", expried);
        editor.commit();
    }

    public String getTokenFirebase() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.KEY_TOKEN_FIREBASE, "");
    }

    public void putTokenFirebase(String token) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.KEY_TOKEN_FIREBASE, token);
        editor.commit();
    }

    public String getLanguage() {
        if (context == null) {
            return Constants.ISO_CODE_VN;
        }
        return preferences.getString(Constants.KEY_LANGUAGE, Constants.ISO_CODE_VN);
    }

    public void putLanguage(String isoCountryCode) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.KEY_LANGUAGE, Constants.ISO_CODE_VN);
        editor.commit();
    }

    public void putCacheSocial(String dataJsonSocial) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.CACHE_SOCIAL, dataJsonSocial);
        editor.commit();
    }

    public String getCacheSocial() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.CACHE_SOCIAL, "");
    }

    public void putCacheHistoryMessage(String dataJsonHistoryMessage) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.CACHE_HISTORY_MESSAGE, dataJsonHistoryMessage);
        editor.commit();
    }

    public String getCacheHistoryMessage() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.CACHE_HISTORY_MESSAGE, "");
    }

    public void putCacheNotiRequest(String dataJsonNotiRequest) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.CACHE_NOTI_REQUEST, dataJsonNotiRequest);
        editor.commit();
    }

    public String getCacheNotiRequest() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.CACHE_NOTI_REQUEST, "");
    }

    public void putCacheCustomerRecent(String dataJsonCustomerRecent) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.CACHE_CUSTOMER_RECENT, dataJsonCustomerRecent);
        editor.commit();
    }

    public String getCacheCustomerRecent() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.CACHE_CUSTOMER_RECENT, "");
    }

    public void putCacheGroupCustomer(String dataJsonGroupCustomer) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.CACHE_GROUP_CUSTOMER, dataJsonGroupCustomer);
        editor.commit();
    }

    public String getCacheGroupCustomer() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.CACHE_GROUP_CUSTOMER, "");
    }

    public void putCacheFavoriteProduct(String dataJsonFavoriteProduct) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.CACHE_FAVORITE_PRODUCT, dataJsonFavoriteProduct);
        editor.commit();
    }

    public String getCacheFavoriteProduct() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.CACHE_FAVORITE_PRODUCT, "");
    }

    public void putCacheNoti(String dataJsonNoti) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.CACHE_NOTI, dataJsonNoti);
        editor.commit();
    }

    public String getCacheNoti() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.CACHE_NOTI, "");
    }

    public void putLinkBgVuVo(String linkBgVuVo) {
        if (context == null) {
            return;
        }
        editor.putString(Constants.TIN_VU_VO, linkBgVuVo);
        editor.commit();
    }

    public String getLinkBgVuVo() {
        if (context == null) {
            return "";
        }
        return preferences.getString(Constants.TIN_VU_VO, "");
    }

}
