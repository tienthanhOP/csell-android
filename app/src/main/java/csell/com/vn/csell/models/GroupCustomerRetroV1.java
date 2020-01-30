package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupCustomerRetroV1 implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("customers")
    @Expose
    private List<FieldCus> customers = new ArrayList<>();
    @SerializedName("lastInteract")
    @Expose
    private Long lastInteract;
    @SerializedName("interactCount")
    @Expose
    private Integer interactCount;
    @SerializedName("createdBy")
    @Expose
    private FieldV1 createdBy;
    @SerializedName("createdAt")
    @Expose
    private Long createdAt;
    @SerializedName("updatedAt")
    @Expose
    private Long updatedAt;

    public boolean isSelected = false;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<FieldCus> getCustomers() {
        return customers;
    }

    public void setCustomers(List<FieldCus> customers) {
        this.customers = customers;
    }

    public Long getLastInteract() {
        return lastInteract;
    }

    public void setLastInteract(Long lastInteract) {
        this.lastInteract = lastInteract;
    }

    public Integer getInteractCount() {
        return interactCount;
    }

    public void setInteractCount(Integer interactCount) {
        this.interactCount = interactCount;
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
