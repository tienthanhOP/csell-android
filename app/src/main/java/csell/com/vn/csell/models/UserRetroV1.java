package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserRetroV1 {
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
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("group")
    @Expose
    private String group;
    @SerializedName("avatar")
    @Expose
    private Object avatar;
    @SerializedName("cover")
    @Expose
    private Object cover;
    @SerializedName("birthday")
    @Expose
    private Object birthday;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("fields")
    @Expose
    private List<Object> fields;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("province")
    @Expose
    private Object province;
    @SerializedName("district")
    @Expose
    private Object district;
    @SerializedName("ward")
    @Expose
    private Object ward;
    @SerializedName("setting")
    @Expose
    private Object setting;
    @SerializedName("totalPoint")
    @Expose
    private Integer totalPoint;
    @SerializedName("lastPurchase")
    @Expose
    private Object lastPurchase;
    @SerializedName("expiredAt")
    @Expose
    private Integer expiredAt;
    @SerializedName("createdAt")
    @Expose
    private Integer createdAt;
    @SerializedName("updatedAt")
    @Expose
    private Integer updatedAt;


}
