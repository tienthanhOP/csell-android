package csell.com.vn.csell.models;

import java.io.Serializable;
import java.util.List;

public class Street implements Serializable {
    private String code;
    private List<StreetInfo> streets;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<StreetInfo> getStreets() {
        return streets;
    }

    public void setStreets(List<StreetInfo> streets) {
        this.streets = streets;
    }
}
