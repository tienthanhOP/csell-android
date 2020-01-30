package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by cuong.nv on 4/17/2018.
 */

public class ProductCountResponse implements Serializable {

    @SerializedName("catid")
    @Expose
    private String catid;
    @SerializedName("projectid")
    @Expose
    private String projectid;
    @SerializedName("cat_name")
    @Expose
    private String catName;
    @SerializedName("project_name")
    @Expose
    private String projectName;
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("image")
    @Expose
    private String image;

    public String city;
    public String district;

    public String getCatid() {
        return catid;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ProductCountResponse(String id, String name, Integer total) {
        this.catid = id;
        this.catName = name;
        this.total = total;
    }

    public ProductCountResponse() {
    }

}
