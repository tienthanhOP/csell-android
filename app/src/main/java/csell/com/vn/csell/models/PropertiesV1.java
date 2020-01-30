package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PropertiesV1 {
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("index")
    @Expose
    private Integer index;
    @SerializedName("primary")
    @Expose
    private Integer primary;
    @SerializedName("property_default_values")
    @Expose
    private List<Value> propertyDefaultValues = new ArrayList<>();
    @SerializedName("property_name")
    @Expose
    private String propertyName;
    @SerializedName("type")
    @Expose
    private String type;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getPrimary() {
        return primary;
    }

    public void setPrimary(Integer primary) {
        this.primary = primary;
    }

    public List<Value> getPropertyDefaultValues() {
        return propertyDefaultValues;
    }

    public void setPropertyDefaultValues(List<Value> propertyDefaultValues) {
        this.propertyDefaultValues = propertyDefaultValues;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
