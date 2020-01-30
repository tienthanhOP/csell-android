package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginResponse implements Serializable{
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("user_info")
    @Expose
    private UserRetro userInfo;
    @SerializedName("token_firebase")
    @Expose
    private String tokenFirebase;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserRetro getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserRetro userInfo) {
        this.userInfo = userInfo;
    }

    public String getTokenFirebase() {
        return tokenFirebase;
    }

    public void setTokenFirebase(String tokenFirebase) {
        this.tokenFirebase = tokenFirebase;
    }

}
