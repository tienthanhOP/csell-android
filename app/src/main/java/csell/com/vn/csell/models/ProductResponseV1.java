package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductResponseV1 implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("images")
    @Expose
    private List<String> images = new ArrayList<>();
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("attributes")
    @Expose
    private HashMap<String, Object> attributes;
    @SerializedName("categories")
    @Expose
    private List<CategoryRequest> categories;
    @SerializedName("project")
    @Expose
    private ProjectV1 project;
    @SerializedName("owner")
    @Expose
    private OwnerDetailV1 owner;
    @SerializedName("location")
    @Expose
    private LatLng location;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("province")
    @Expose
    private UnitAdministrative province;
    @SerializedName("district")
    @Expose
    private UnitAdministrative district;
    @SerializedName("ward")
    @Expose
    private UnitAdministrative ward;
    @SerializedName("price")
    @Expose
    private Long price;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("originPrice")
    @Expose
    private Long originPrice;
    @SerializedName("publicPrice")
    @Expose
    private Long publicPrice;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("privacy")
    @Expose
    private String privacy;
    @SerializedName("friends")
    @Expose
    private List<FriendV1> friends;
    @SerializedName("link")
    @Expose
    private Link link;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("createdBy")
    @Expose
    private FieldV1 createdBy;
    @SerializedName("updatedAt")
    @Expose
    private Long updatedAt;
    @SerializedName("createdAt")
    @Expose
    private Long createdAt;

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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public HashMap<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, Object> attributes) {
        this.attributes = attributes;
    }

    public List<CategoryRequest> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryRequest> categories) {
        this.categories = categories;
    }

    public ProjectV1 getProject() {
        return project;
    }

    public void setProject(ProjectV1 project) {
        this.project = project;
    }

    public OwnerDetailV1 getOwner() {
        return owner;
    }

    public void setOwner(OwnerDetailV1 owner) {
        this.owner = owner;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public UnitAdministrative getProvince() {
        return province;
    }

    public void setProvince(UnitAdministrative province) {
        this.province = province;
    }

    public UnitAdministrative getDistrict() {
        return district;
    }

    public void setDistrict(UnitAdministrative district) {
        this.district = district;
    }

    public UnitAdministrative getWard() {
        return ward;
    }

    public void setWard(UnitAdministrative ward) {
        this.ward = ward;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(long originPrice) {
        this.originPrice = originPrice;
    }

    public Long getPublicPrice() {
        return publicPrice;
    }

    public void setPublicPrice(long publicPrice) {
        this.publicPrice = publicPrice;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public List<FriendV1> getFriends() {
        return friends;
    }

    public void setFriends(List<FriendV1> friends) {
        this.friends = friends;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Object getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(FieldV1 createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
