package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CategoryV1 implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("parent")
    @Expose
    private FieldV1 parent;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("position")
    @Expose
    private Integer position;
    @SerializedName("group")
    @Expose
    private Integer group;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("createdBy")
    @Expose
    private FieldV1 createdBy;

    public CategoryV1(String id, Integer group, String name) {
        this.id = id;
        this.group = group;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public FieldV1 getParent() {
        return parent;
    }

    public String getNote() {
        return note;
    }

    public String getLogo() {
        return logo;
    }

    public Integer getPosition() {
        return position;
    }

    public Integer getGroup() {
        return group;
    }

    public Boolean getActive() {
        return isActive;
    }

    public String getName() {
        return name;
    }

    public FieldV1 getCreatedBy() {
        return createdBy;
    }
}
