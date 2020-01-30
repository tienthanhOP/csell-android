package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResCustomers {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("customer")
    @Expose
    private CustomerRetroV1 customer;
    @SerializedName("errors")
    @Expose
    private List<ErrorsResponse> errors;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CustomerRetroV1 getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerRetroV1 customer) {
        this.customer = customer;
    }

    public List<ErrorsResponse> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorsResponse> errors) {
        this.errors = errors;
    }
}
