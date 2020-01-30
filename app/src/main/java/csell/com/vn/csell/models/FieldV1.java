package csell.com.vn.csell.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FieldV1 implements Serializable {
    @SerializedName("id")
    @Expose
    private String fieldId;
    @SerializedName("name")
    @Expose
    private String fieldName;
    @SerializedName("avatar")
    @Expose
    private String fieldAvatar;

    public FieldV1() {
    }

    public FieldV1(String fieldId, String fieldName) {
        this.fieldId = fieldId;
        this.fieldName = fieldName;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldAvatar() {
        return fieldAvatar;
    }

    public void setFieldAvatar(String fieldAvatar) {
        this.fieldAvatar = fieldAvatar;
    }
}
