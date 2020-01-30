package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DataCustomerV1 {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("interacts")
    @Expose
    private List<CustomerRetroV1> interacts = new ArrayList<>();
    @SerializedName("customers")
    @Expose
    private List<CustomerRetroV1> customers = new ArrayList<>();

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<CustomerRetroV1> getInteracts() {
        return interacts;
    }

    public void setInteracts(List<CustomerRetroV1> interacts) {
        this.interacts = interacts;
    }

    public List<CustomerRetroV1> getCustomers() {
        return customers;
    }

    public void setCustomers(List<CustomerRetroV1> customers) {
        this.customers = customers;
    }
}
