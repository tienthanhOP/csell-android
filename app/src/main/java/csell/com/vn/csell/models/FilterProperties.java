package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FilterProperties {
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("category_ids")
    @Expose
    private List<String> categoryIds = null;
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
}
