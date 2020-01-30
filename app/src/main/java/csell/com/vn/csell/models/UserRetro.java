package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.constants.EntityAPI;

public class UserRetro implements Serializable {
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("fields")
    @Expose
    private List<Field> fields = null;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("display_name")
    @Expose
    private String displayname;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("cover")
    @Expose
    private String cover;

    @SerializedName("info")
    @Expose
    private HashMap info = null;

    @SerializedName("friend_info")
    @Expose
    private FriendInfo friendInfo = null;

    @SerializedName("friend_general")
    @Expose
    private Integer friendGeneral;

    @SerializedName("expiration_time")
    @Expose
    private Long expirationTime;
    @SerializedName("date_system")
    @Expose
    private Long dateSystem;

    public Long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Long getDateSystem() {
        return dateSystem;
    }

    public void setDateSystem(Long dateSystem) {
        this.dateSystem = dateSystem;
    }
    public String RoomChatId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public HashMap getInfo() {
        return info;
    }

    public void setInfo(HashMap info) {
        this.info = info;
    }

    public FriendInfo getFriendInfo() {
        return friendInfo;
    }

    public void setFriendInfo(FriendInfo friendInfo) {
        this.friendInfo = friendInfo;
    }

    public Integer getFriendGeneral() {
        return friendGeneral;
    }

    public void setFriendGeneral(Integer friendGeneral) {
        this.friendGeneral = friendGeneral;
    }

    public Boolean IsSelected = false;

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(EntityAPI.FIELD_DISPLAY_NAME, displayname);
        map.put(EntityAPI.FIELD_DOB, dob);
        map.put(EntityAPI.FIELD_FIELDS, fields);
        map.put(EntityAPI.FIELD_EMAIL, email);
        map.put(EntityAPI.FIELD_PHONE, phone);
        map.put(EntityAPI.FIELD_USERNAME, username);
        map.put(EntityAPI.FIELD_CITY, city);
        map.put(EntityAPI.FIELD_PASSWORD, password);
        map.put(EntityAPI.FIELD_AVATAR, avatar);

        return map;
    }

    public UserRetro() {
    }

    public UserRetro(String uid) {
        this.uid = uid;
    }

    public UserRetro(String uid, String displayname, String avatar) {
        this.uid = uid;
        this.avatar = avatar;
        this.displayname = displayname;
    }
}
