package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataCustomer {
    @SerializedName("info")
    @Expose
    private CustomerRetro info;
    @SerializedName("group_info")
    @Expose
    private List<Object> groupInfo = null;
    @SerializedName("items_note")
    @Expose
    private List<Note> itemsNote = null;

    public CustomerRetro getInfo() {
        return info;
    }

    public void setInfo(CustomerRetro info) {
        this.info = info;
    }

    public List<Object> getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(List<Object> groupInfo) {
        this.groupInfo = groupInfo;
    }

    public List<Note> getItemsNote() {
        return itemsNote;
    }

    public void setItemsNote(List<Note> itemsNote) {
        this.itemsNote = itemsNote;
    }
}
