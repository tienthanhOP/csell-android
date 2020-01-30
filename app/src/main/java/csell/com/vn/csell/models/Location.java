package csell.com.vn.csell.models;

import java.io.Serializable;

/**
 * Created by cuong.nv on 3/20/2018.
 */

public class Location implements Serializable {

    private Integer location_id;
    private Integer location_level;
    private Integer priority;
    private String location_name;
    private Integer parent_id;

    public Integer getLocation_id() {
        return location_id;
    }

    public void setLocation_id(Integer location_id) {
        this.location_id = location_id;
    }

    public String getLocation_id(int max) {
        StringBuilder location = new StringBuilder(String.valueOf(location_id));
        while (location.length() < max) {
            location.insert(0, "0");
        }
        return location.toString();
    }

    public Integer getLocation_level() {
        return location_level;
    }

    public void setLocation_level(Integer location_level) {
        this.location_level = location_level;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
