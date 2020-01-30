package csell.com.vn.csell.models;

public class LoginResponseV1 {
    private Integer code;
    private String message;
    private Token token;
    private UserRetroV1 user;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Token getToken() {
        return token;
    }

    public UserRetroV1 getUser() {
        return user;
    }
}
