package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

public class NoteV1 implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("customer")
    @Expose
    private FieldV1 customer;
    @SerializedName("product")
    @Expose
    private FieldV1 product;
    @SerializedName("appointmentAt")
    @Expose
    private Long appointmentAt;
    @SerializedName("noticeAt")
    @Expose
    private Long noticeAt;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("createdBy")
    @Expose
    private FieldV1 createdBy;
    @SerializedName("createdAt")
    @Expose
    private Long createdAt;
    @SerializedName("updatedAt")
    @Expose
    private Long updatedAt;

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> customerMap = new HashMap<>();
        customerMap.put("id", customer.getFieldId());
        customerMap.put("name", customer.getFieldName());

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("id", product.getFieldId());
        productMap.put("name", product.getFieldName());

        HashMap<String, Object> createByMap = new HashMap<>();
        createByMap.put("id", createdBy.getFieldId());
        createByMap.put("name", createdBy.getFieldName());

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("content", content);
        map.put("customer", customerMap);
        map.put("product", productMap);
        map.put("appointmentAt", appointmentAt);
        map.put("noticeAt", noticeAt);
        map.put("note", note);
        map.put("createdBy", createByMap);
        map.put("createdAt", createdAt);
        map.put("updatedAt", updatedAt);

        return map;

    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public FieldV1 getCustomer() {
        return customer;
    }

    public void setCustomer(FieldV1 customer) {
        this.customer = customer;
    }

    public FieldV1 getProduct() {
        return product;
    }

    public void setProduct(FieldV1 product) {
        this.product = product;
    }

    public Long getAppointmentAt() {
        return appointmentAt;
    }

    public void setAppointmentAt(Long appointmentAt) {
        this.appointmentAt = appointmentAt;
    }

    public Long getNoticeAt() {
        return noticeAt;
    }

    public void setNoticeAt(Long noticeAt) {
        this.noticeAt = noticeAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public FieldV1 getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(FieldV1 createdBy) {
        this.createdBy = createdBy;
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
