package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Product implements Serializable {
    @SerializedName("id")
    @Expose
    private String _id;
    @SerializedName("itemid")
    @Expose
    private String itemid;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("privacy_type")
    @Expose
    private Integer privacyType;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("price")
    @Expose
    private Long price;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("catid")
    @Expose
    private String catid;
    @SerializedName("cat_name")
    @Expose
    private String catName;
    @SerializedName("projectid")
    @Expose
    private String projectid;
    @SerializedName("project_name")
    @Expose
    private String projectName;
    @SerializedName("image_list")
    @Expose
    private List<ImageSuffix> imageList = null;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("district")
    @Expose
    private String district;
    @SerializedName("date_created")
    @Expose
    private String dateCreated;
    @SerializedName("owner_details")
    @Expose
    private List<OwnerDetail> ownerDetails = null;
    @SerializedName("owner_info")
    @Expose
    private ProductOwner ownerInfo;

    @SerializedName("note_private")
    @Expose
    private String notePrivate;

    @SerializedName("can_see_note_private")
    @Expose
    private List<UserRetro> canSeeNotePrivate = null;

    @SerializedName("properties")
    @Expose
    private HashMap<String, Object> properties;

    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("location")
    @Expose
    private String location;

    //social info
    @SerializedName("user_info")
    @Expose
    private UserRetro userInfo;
    @SerializedName("date_shared")
    @Expose
    private String dateShared;
    @SerializedName("is_friend")
    @Expose
    private Boolean isFriend;
    @SerializedName("follow_owner_by_viewer")
    @Expose
    private Boolean followOwnerByViewer;
    @SerializedName("is_follow_item")
    @Expose
    private Boolean isFollowItem;
    @SerializedName("is_reup")
    @Expose
    private Boolean isReup;
    @SerializedName("has_note_private")
    @Expose
    private Boolean hasNotePrivate;
    @SerializedName("image")
    @Expose
    private ImageSuffix image;
    @SerializedName("background")
    @Expose
    private String background;

    @SerializedName("images")
    @Expose
    private List<ImageSuffix> images = null;

    public List<ImageSuffix> getImages() {
        return images;
    }

    public void setImages(List<ImageSuffix> images) {
        this.images = images;
    }

    /*Variable using updateView in adapter*/

    public boolean itMeLikePost;
    public int totalLike;
    public long totalComments;
    public HashMap<String, Object> mapUserLikes = null;
    public ArrayList<String> lsUserId = new ArrayList<>();


    /*end*/

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPrivacyType() {
        return privacyType;
    }

    public void setPrivacyType(Integer privacyType) {
        this.privacyType = privacyType;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getCatid() {
        return catid;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<ImageSuffix> getImageList() {
        return imageList;
    }

    public void setImageList(List<ImageSuffix> imageList) {
        this.imageList = imageList;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public ProductOwner getOwnerInfo() {
        return ownerInfo;
    }

    public void setOwnerInfo(ProductOwner ownerInfo) {
        this.ownerInfo = ownerInfo;
    }

    public List<UserRetro> getCanSeeNotePrivate() {
        return canSeeNotePrivate;
    }

    public void setCanSeeNotePrivate(List<UserRetro> canSeeNotePrivate) {
        this.canSeeNotePrivate = canSeeNotePrivate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserRetro getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserRetro userInfo) {
        this.userInfo = userInfo;
    }

    public String getDateShared() {
        return dateShared;
    }

    public void setDateShared(String dateShared) {
        this.dateShared = dateShared;
    }

    public Boolean getFriend() {
        return isFriend;
    }

    public void setFriend(Boolean friend) {
        isFriend = friend;
    }


    public Boolean getHasNotePrivate() {
        return hasNotePrivate;
    }

    public void setHasNotePrivate(Boolean hasNotePrivate) {
        this.hasNotePrivate = hasNotePrivate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ImageSuffix getImage() {
        return image;
    }

    public void setImage(ImageSuffix image) {
        this.image = image;
    }

    public Boolean getFollowOwnerByViewer() {
        return followOwnerByViewer;
    }

    public void setFollowOwnerByViewer(Boolean followOwnerByViewer) {
        this.followOwnerByViewer = followOwnerByViewer;
    }

    public Boolean getFollowItem() {
        return isFollowItem;
    }

    public void setFollowItem(Boolean followItem) {
        isFollowItem = followItem;
    }

    public Boolean getReup() {
        return isReup;
    }

    public void setReup(Boolean reup) {
        isReup = reup;
    }

    public String getNotePrivate() {
        return notePrivate;
    }

    public void setNotePrivate(String notePrivate) {
        this.notePrivate = notePrivate;
    }

    public HashMap<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, Object> properties) {
        this.properties = properties;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<OwnerDetail> getOwnerDetails() {
        return ownerDetails;
    }

    public void setOwnerDetails(List<OwnerDetail> ownerDetails) {
        this.ownerDetails = ownerDetails;
    }

}
