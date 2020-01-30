package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FriendInfo implements Serializable{

    @SerializedName("requested_by")
    @Expose
    private String requestedBy;
    @SerializedName("is_accepted")
    @Expose
    private Boolean isAccepted;
    @SerializedName("is_requested")
    @Expose
    private Boolean isRequested;
    @SerializedName("follow_by_me")
    @Expose
    private Boolean followByMe;
    @SerializedName("follow_by_friend")
    @Expose
    private Boolean followByFriend;

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(Boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    public Boolean getIsRequested() {
        return isRequested;
    }

    public void setIsRequested(Boolean isRequested) {
        this.isRequested = isRequested;
    }

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

    public FriendInfo() {
    }

    public FriendInfo(Boolean isAccepted, Boolean isRequested) {
        this.isAccepted = isAccepted;
        this.isRequested = isRequested;
    }

    public FriendInfo(Boolean isAccepted, Boolean isRequested, Boolean followByMe) {
        this.isAccepted = isAccepted;
        this.isRequested = isRequested;
        this.followByMe = followByMe;
    }
}
