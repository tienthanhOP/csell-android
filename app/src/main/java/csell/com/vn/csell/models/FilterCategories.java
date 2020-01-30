package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FilterCategories {
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("image_web")
    @Expose
    private String imageWeb;
    @SerializedName("background")
    @Expose
    private String background;
    @SerializedName("level")
    @Expose
    private Integer level;
    @SerializedName("max_level")
    @Expose
    private Integer maxLevel;
    @SerializedName("priority")
    @Expose
    private Integer priority;

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getImage() {
        return image;
    }

    public String getImageWeb() {
        return imageWeb;
    }

    public String getBackground() {
        return background;
    }

    public Integer getLevel() {
        return level;
    }

    public Integer getMaxLevel() {
        return maxLevel;
    }

    public Integer getPriority() {
        return priority;
    }
}
