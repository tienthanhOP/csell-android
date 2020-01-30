package csell.com.vn.csell.models;

import java.io.Serializable;

public class FriendV1 implements Serializable {
    private String avatar;
    private boolean isSeenNote;
    private String id;
    private String name;

    public String getAvatar() {
        return avatar;
    }

    public boolean isSeenNote() {
        return isSeenNote;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
