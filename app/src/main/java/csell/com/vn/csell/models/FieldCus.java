package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FieldCus implements Serializable {
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("group")
    @Expose
    private String group;
    @SerializedName("isDefault")
    @Expose
    private Boolean isDefault;


    public FieldCus(String address) {
        this.address = address;
    }

    public FieldCus(String address, String group, Boolean isDefault) {
        this.address = address;
        this.group = group;
        this.isDefault = isDefault;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}
