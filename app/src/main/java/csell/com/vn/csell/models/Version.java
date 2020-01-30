package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Version implements Serializable {
    @SerializedName("property_version")
    @Expose
    private Integer propertyVersion;
    @SerializedName("category_version")
    @Expose
    private Integer categoryVersion;
    @SerializedName("location_version")
    @Expose
    private Integer locationVersion;
    @SerializedName("privacy_version")
    @Expose
    private Integer privacyVersion;
    @SerializedName("project_version")
    @Expose
    private Integer projectVersion;
    @SerializedName("apk_version")
    @Expose
    private Integer apkVersion;
    @SerializedName("sqlite_version")
    @Expose
    private Integer sqliteVersion;
    @SerializedName("language_version")
    @Expose
    private Integer languageVersion;
    @SerializedName("filter_version")
    @Expose
    private Integer filterVersion;
    @SerializedName("apk_require")
    @Expose
    private boolean apkRequire;

    public boolean isApkRequire() {
        return apkRequire;
    }

    public void setApkRequire(boolean apkRequire) {
        this.apkRequire = apkRequire;
    }

    public Integer getLanguageVersion() {
        return languageVersion;
    }

    public void setLanguageVersion(Integer languageVersion) {
        this.languageVersion = languageVersion;
    }

    public Integer getFilterVersion() {
        return filterVersion;
    }

    public void setFilterVersion(Integer filterVersion) {
        this.filterVersion = filterVersion;
    }

    public Integer getPropertyVersion() {
        return propertyVersion;
    }

    public void setPropertyVersion(Integer propertyVersion) {
        this.propertyVersion = propertyVersion;
    }

    public Integer getCategoryVersion() {
        return categoryVersion;
    }

    public void setCategoryVersion(Integer categoryVersion) {
        this.categoryVersion = categoryVersion;
    }

    public Integer getLocationVersion() {
        return locationVersion;
    }

    public void setLocationVersion(Integer locationVersion) {
        this.locationVersion = locationVersion;
    }

    public Integer getPrivacyVersion() {
        return privacyVersion;
    }

    public void setPrivacyVersion(Integer privacyVersion) {
        this.privacyVersion = privacyVersion;
    }

    public Integer getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(Integer projectVersion) {
        this.projectVersion = projectVersion;
    }

    public Integer getApkVersion() {
        return apkVersion;
    }

    public void setApkVersion(Integer apkVersion) {
        this.apkVersion = apkVersion;
    }

    public Integer getSqliteVersion() {
        return sqliteVersion;
    }

    public void setSqliteVersion(Integer sqliteVersion) {
        this.sqliteVersion = sqliteVersion;
    }
}
