package csell.com.vn.csell.models;

import java.io.Serializable;

public class CategoryRequest implements Serializable {
    private String id;
    private String name;
    private Integer group;
    private String logo;

    public CategoryRequest(String id, String name, Integer group) {
        this.id = id;
        this.name = name;
        this.group = group;
    }

    public String getLogo() {
        return logo;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getGroup() {
        return group;
    }
}
