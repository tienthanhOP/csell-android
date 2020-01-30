package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductResponseModel {
    //get product response
    @SerializedName("info")
    @Expose
    private Product info = null;
    @SerializedName("items_note")
    @Expose
    private List<Note> itemsNote = null;
    //create product response
    @SerializedName("itemid")
    @Expose
    private String itemid;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("image_list")
    @Expose
    private List<ImageSuffix> imageList = null;

    @SerializedName("images")
    @Expose
    private List<ImageSuffix> images = null;

    public List<ImageSuffix> getImages() {
        return images;
    }

    public void setImages(List<ImageSuffix> images) {
        this.images = images;
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

    public List<ImageSuffix> getImageList() {
        return imageList;
    }

    public void setImageList(List<ImageSuffix> imageList) {
        this.imageList = imageList;
    }

    public Product getInfo() {
        return info;
    }

    public void setInfo(Product info) {
        this.info = info;
    }

    public List<Note> getItemsNote() {
        return itemsNote;
    }

    public void setItemsNote(List<Note> itemsNote) {
        this.itemsNote = itemsNote;
    }


}
