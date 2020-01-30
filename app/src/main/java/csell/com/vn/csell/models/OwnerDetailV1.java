package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OwnerDetailV1 implements Serializable{
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("originPrice")
    @Expose
    private Long originPrice;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;

    public String getPhone() {
        return phone;
    }

    public String getNote() {
        return note;
    }

    public Long getOriginPrice() {
        return originPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
