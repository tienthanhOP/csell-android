package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListCustomer {
    @SerializedName("recent_interactions")
    @Expose
    private List<CustomerRetro> interactiveRecent = null;
    @SerializedName("customers")
    @Expose
    private List<CustomerRetro> customers = null;

    public List<CustomerRetro> getInteractiveRecent() {
        return interactiveRecent;
    }

    public void setInteractiveRecent(List<CustomerRetro> interactiveRecent) {
        this.interactiveRecent = interactiveRecent;
    }

    public List<CustomerRetro> getCustomers() {
        return customers;
    }

    public void setCustomers(List<CustomerRetro> customers) {
        this.customers = customers;
    }
}
