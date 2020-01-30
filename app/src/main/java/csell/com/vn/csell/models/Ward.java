package csell.com.vn.csell.models;

import java.io.Serializable;

public class Ward implements Serializable {
    private String code;
    private LatLng locations;
    private String parent_code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LatLng getLocations() {
        return locations;
    }

    public void setLocations(LatLng locations) {
        this.locations = locations;
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
}
