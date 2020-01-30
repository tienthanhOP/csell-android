package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

import csell.com.vn.csell.constants.EntityAPI;

public class Note implements Serializable {
    @SerializedName("noteid")
    @Expose
    private String noteid;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("custid")
    @Expose
    private String custid;
    @SerializedName("cust_name")
    @Expose
    private String custname;
    @SerializedName("productid")
    @Expose
    private String productid;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("itemid")
    @Expose
    private String itemid;
    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("date_created")
    @Expose
    private String datecreated;
    @SerializedName("date_remindered")
    @Expose
    private String datereminder;
    @SerializedName("is_reminder")
    @Expose
    private Boolean isreminder;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("last_updated")
    @Expose
    private String lastupdated;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("prompt_before")
    @Expose
    private String promptbefore;

    public String getPromptbefore() {
        return promptbefore;
    }

    public void setPromptbefore(String promptbefore) {
        this.promptbefore = promptbefore;
    }

    public String getNoteid() {
        return noteid;
    }

    public void setNoteid(String noteid) {
        this.noteid = noteid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCustid() {
        return custid;
    }

    public void setCustid(String custid) {
        this.custid = custid;
    }

    public String getCustname() {
        return custname;
    }

    public void setCustname(String custname) {
        this.custname = custname;
    }

    public String getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(String datecreated) {
        this.datecreated = datecreated;
    }

    public String getDatereminder() {
        return datereminder;
    }

    public void setDatereminder(String datereminder) {
        this.datereminder = datereminder;
    }

    public Boolean getIsreminder() {
        return isreminder;
    }

    public void setIsreminder(Boolean isreminder) {
        this.isreminder = isreminder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(String lastupdated) {
        this.lastupdated = lastupdated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(EntityAPI.FIELD_NOTE_ID, noteid);
        map.put(EntityAPI.FIELD_CUSTOMER_ID, custid);
        map.put(EntityAPI.FIELD_DESCRIPTION, content);
        map.put(EntityAPI.FIELD_TITLE, title);
        map.put(EntityAPI.FIELD_DATE_REMINDER, datereminder);
        map.put(EntityAPI.FIELD_IS_REMINDER, isreminder);
        map.put(EntityAPI.FIELD_STATUS, status);
        map.put(EntityAPI.FIELD_NOTE_ITEMID, itemid);

        return map;

    }

}
