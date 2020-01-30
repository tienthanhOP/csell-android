package csell.com.vn.csell.models;

import java.io.Serializable;

public class CreatedBy implements Serializable {
    private String avatar;
    private String id;
    private String name;

    public String getAvatar() {
        return avatar;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
