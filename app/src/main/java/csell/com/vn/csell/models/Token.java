package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Token {
    @SerializedName("access")
    @Expose
    private Access access;
    @SerializedName("refresh")
    @Expose
    private Access refresh;
    @SerializedName("type")
    @Expose
    private String type;

    public Access getAccess() {
        return access;
    }

    public Access getRefresh() {
        return refresh;
    }

    public String getType() {
        return type;
    }

    public class Access {

        @SerializedName("expiredAt")
        @Expose
        private String expiredAt;
        @SerializedName("token")
        @Expose
        private String token;

        public String getExpiredAt() {
            return expiredAt;
        }

        public String getToken() {
            return token;
        }
    }
}
