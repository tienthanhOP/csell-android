package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CustomerRetroV1 implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("phones")
    @Expose
    private List<FieldCus> phones = new ArrayList<>();
    @SerializedName("emails")
    @Expose
    private List<FieldCus> emails = new ArrayList<>();
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("hashtag")
    @Expose
    private List<String> hashtag = new ArrayList<>();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<FieldCus> getPhones() {
        return phones;
    }

    public void setPhones(List<FieldCus> phones) {
        this.phones = phones;
    }

    public List<FieldCus> getEmails() {
        return emails;
    }

    public void setEmails(List<FieldCus> emails) {
        this.emails = emails;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public List<String> getHashtag() {
        return hashtag;
    }

    public void setHashtag(List<String> hashtag) {
        this.hashtag = hashtag;
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
