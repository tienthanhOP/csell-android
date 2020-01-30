package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.constants.EntityAPI;

public class UploadImageRetro {
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("images")
    @Expose
    private List<ImageUpload> images = null;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<ImageUpload> getImages() {
        return images;
    }

    public void setImages(List<ImageUpload> images) {
        this.images = images;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(EntityAPI.FIELD_TYPE_UPLOAD, type);
        map.put(EntityAPI.FIELD_IMAGES_UPLOAD, images);

        return map;
    }
}
