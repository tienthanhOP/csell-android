package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ImageSuffix implements Serializable {

    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("suffixes")
    @Expose
    private List<String> suffixes = null;

    private boolean isSuccessResize = false;

    public ImageSuffix(String path) {
        this.path = path;
    }

    public ImageSuffix(String path, List<String> suffixes) {
        this.path = path;
        this.suffixes = suffixes;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSuccessResize() {
        return isSuccessResize;
    }

    public void setSuccessResize(boolean successResize) {
        isSuccessResize = successResize;
    }

    public List<String> getSuffixes() {
        return suffixes;
    }

    public void setSuffixes(List<String> suffixes) {
        this.suffixes = suffixes;
    }

}
