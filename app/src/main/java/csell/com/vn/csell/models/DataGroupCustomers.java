package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataGroupCustomers implements Serializable {
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("groups")
    @Expose
    private List<GroupCustomerRetroV1> groups = new ArrayList<>();

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<GroupCustomerRetroV1> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupCustomerRetroV1> groups) {
        this.groups = groups;
    }
}
