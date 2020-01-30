package csell.com.vn.csell.models;

import java.io.Serializable;
import java.util.List;

public class City implements Serializable {
    private String name;
    private String code;
    private List<List<List<LatLng>>> locations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<List<List<LatLng>>> getLists() {
        return locations;
    }

    public void setLists(List<List<List<LatLng>>> lists) {
        this.locations = lists;
    }
}
