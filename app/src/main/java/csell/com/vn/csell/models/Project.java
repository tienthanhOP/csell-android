package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cuong.nv on 4/5/2018.
 */

public class Project implements Serializable {

    @SerializedName("acreage_build")
    @Expose
    private String acreageBuild;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("district")
    @Expose
    private String district;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("investor")
    @Expose
    private String investor;
    @SerializedName("is_actived")
    @Expose
    private Boolean isActived;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("projectid")
    @Expose
    private String projectId;
    @SerializedName("project_scale")
    @Expose
    private String projectScale;
    @SerializedName("project_name")
    @Expose
    private String projectName;
    @SerializedName("total_acreage")
    @Expose
    private String totalAcreage;
    @SerializedName("type_development")
    @Expose
    private String typeDevelopment;
    @SerializedName("images")
    @Expose
    private List<String> images = null;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getAcreageBuild() {
        return acreageBuild;
    }

    public void setAcreageBuild(String acreageBuild) {
        this.acreageBuild = acreageBuild;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInvestor() {
        return investor;
    }

    public void setInvestor(String investor) {
        this.investor = investor;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProjectScale() {
        return projectScale;
    }

    public void setProjectScale(String projectScale) {
        this.projectScale = projectScale;
    }

    public String getTotalAcreage() {
        return totalAcreage;
    }

    public void setTotalAcreage(String totalAcreage) {
        this.totalAcreage = totalAcreage;
    }

    public String getTypeDevelopment() {
        return typeDevelopment;
    }

    public void setTypeDevelopment(String typeDevelopment) {
        this.typeDevelopment = typeDevelopment;
    }

    public String getProjectid() {
        return projectId;
    }

    public void setProjectid(String projectid) {
        this.projectId = projectid;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

//    public HashMap<String, Object> toMap(FileSave fileSave) {
//        HashMap<String, Object> map = new HashMap<>();
//
//        map.put(EntityAPI.FIELD_PROJECT_NAME, projectName);
//        map.put(EntityAPI.FIELD_DESCRIPTION, description);
//        map.put(EntityAPI.FIELD_DATE_CREATED, ServerValue.TIMESTAMP);
//        map.put(EntityAPI.FIELD_DISTRICT, district);
//        map.put(EntityAPI.FIELD_CITY, city);
//        map.put(EntityAPI.FIELD_IS_ACTIVED, act);
//        map.put(EntityAPI.FIELD_IS_PENDING, isPending);
//        map.put("uid", fileSave.getUserId());
//
//        return map;
//    }
}
