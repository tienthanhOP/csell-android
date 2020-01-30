package csell.com.vn.csell.models;

import java.io.Serializable;
import java.util.List;

public class District implements Serializable {
    private String code;
    private String parent_code;
    private String name;
    private List<List<List<LatLng>>> locations;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParentCode() {
        return parent_code;
    }

    public void setParentCode(String parent_code) {
        this.parent_code = parent_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<List<List<LatLng>>> getLocations() {
        return locations;
    }

    public void setLocations(List<List<List<LatLng>>> locations) {
        this.locations = locations;
    }
}
