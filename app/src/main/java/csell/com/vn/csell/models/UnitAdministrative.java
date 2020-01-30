package csell.com.vn.csell.models;

import java.io.Serializable;

public class UnitAdministrative implements Serializable {
    private String code;
    private String name;

    public UnitAdministrative(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
