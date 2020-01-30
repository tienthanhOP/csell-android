package csell.com.vn.csell.apis;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import csell.com.vn.csell.models.ErrorsResponse;

public class JSONProductV1<T> {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("product")
    @Expose
    private T product;
    @SerializedName("errors")
    @Expose
    private List<ErrorsResponse> errors;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getProduct() {
        return product;
    }

    public List<ErrorsResponse> getErrors() {
        return errors;
    }
}
