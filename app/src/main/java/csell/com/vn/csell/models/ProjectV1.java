package csell.com.vn.csell.models;

import java.io.Serializable;

public class ProjectV1 implements Serializable {
    private String id;
    private String name;
    private String image;

    public ProjectV1(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
