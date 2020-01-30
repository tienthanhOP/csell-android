package csell.com.vn.csell.apis;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import csell.com.vn.csell.models.ProductResponseV1;

public class ProductResponse {
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("products")
    @Expose
    private List<ProductResponseV1> productResponseV1List;

    public Integer getCount() {
        return count;
    }

    public List<ProductResponseV1> getProductResponseV1List() {
        return productResponseV1List;
    }
}
