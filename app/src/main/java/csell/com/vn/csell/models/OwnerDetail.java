package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OwnerDetail implements Serializable{
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private List<String> phone = null;
    @SerializedName("avatar")
    @Expose
    private Object avatar;
    @SerializedName("custid")
    @Expose
    private String custid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhone() {
        return phone;
    }

    public void setPhone(List<String> phone) {
        this.phone = phone;
    }

    public Object getAvatar() {
        return avatar;
    }

    public void setAvatar(Object avatar) {
        this.avatar = avatar;
    }

    public String getCustid() {
        return custid;
    }

    public void setCustid(String custid) {
        this.custid = custid;
    }
}
