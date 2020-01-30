package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductOwner implements Serializable {

    @SerializedName("ownerid")
    @Expose
    private String ownerid;
    @SerializedName("owner_name")
    @Expose
    private String ownerName;
    @SerializedName("owner_phone")
    @Expose
    private String ownerPhone;
    @SerializedName("original_price")
    @Expose
    private Long originalPrice;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("note")
    @Expose
    private String note;

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public Long getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Long originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
