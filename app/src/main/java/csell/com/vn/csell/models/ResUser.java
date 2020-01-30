package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResUser {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("group")
    @Expose
    private String group;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("cover")
    @Expose
    private String cover;
    @SerializedName("birthday")
    @Expose
    private Long birthday;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("fields")
    @Expose
    private List<FieldV1> fields = null;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("province")
    @Expose
    private UnitAdministrative province;
    @SerializedName("district")
    @Expose
    private UnitAdministrative district;
    @SerializedName("ward")
    @Expose
    private UnitAdministrative ward;
    @SerializedName("setting")
    @Expose
    private ResSetting setting;
    @SerializedName("totalPoint")
    @Expose
    private Integer totalPoint;
    @SerializedName("lastPurchase")
    @Expose
    private Long lastPurchase;
    @SerializedName("expiredAt")
    @Expose
    private Long expiredAt;
    @SerializedName("createdAt")
    @Expose
    private Long createdAt;
    @SerializedName("updatedAt")
    @Expose
    private Long updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<FieldV1> getFields() {
        return fields;
    }

    public void setFields(List<FieldV1> fields) {
        this.fields = fields;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public UnitAdministrative getProvince() {
        return province;
    }

    public void setProvince(UnitAdministrative province) {
        this.province = province;
    }

    public UnitAdministrative getDistrict() {
        return district;
    }

    public void setDistrict(UnitAdministrative district) {
        this.district = district;
    }

    public UnitAdministrative getWard() {
        return ward;
    }

    public void setWard(UnitAdministrative ward) {
        this.ward = ward;
    }

    public ResSetting getSetting() {
        return setting;
    }

    public void setSetting(ResSetting setting) {
        this.setting = setting;
    }

    public Integer getTotalPoint() {
        return totalPoint;
    }

    public void setTotalPoint(Integer totalPoint) {
        this.totalPoint = totalPoint;
    }

    public Long getLastPurchase() {
        return lastPurchase;
    }

    public void setLastPurchase(Long lastPurchase) {
        this.lastPurchase = lastPurchase;
    }

    public Long getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Long expiredAt) {
        this.expiredAt = expiredAt;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
