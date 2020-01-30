package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class FriendResponse implements Serializable{
    @SerializedName("follow_by_me")
    @Expose
    private Boolean followByMe;
    @SerializedName("follow_by_friend")
    @Expose
    private Boolean followByFriend;
    @SerializedName("friend_general")
    @Expose
    private Integer friendGeneral;
    @SerializedName("friend_info")
    @Expose
    private List<UserRetro> friendInfo = null;
    public boolean is_show; //for noti

    public Boolean getFollowByMe() {
        return followByMe;
    }

    public void setFollowByMe(Boolean followByMe) {
        this.followByMe = followByMe;
    }

    public Boolean getFollowByFriend() {
        return followByFriend;
    }

    public void setFollowByFriend(Boolean followByFriend) {
        this.followByFriend = followByFriend;
    }

    public Integer getFriendGeneral() {
        return friendGeneral;
    }

    public void setFriendGeneral(Integer friendGeneral) {
        this.friendGeneral = friendGeneral;
    }

    public List<UserRetro> getFriendInfo() {
        return friendInfo;
    }

    public void setFriendInfo(List<UserRetro> friendInfo) {
        this.friendInfo = friendInfo;
    }
}
