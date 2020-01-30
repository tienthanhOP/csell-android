package csell.com.vn.csell.apis;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import csell.com.vn.csell.models.ErrorsResponse;
import csell.com.vn.csell.models.NoteV1;

public class JSONNote<T> {
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("notes")
    @Expose
    private T note;
    @SerializedName("errors")
    @Expose
    private List<ErrorsResponse> errors;

    public Integer getCount() {
        return count;
    }

    public T getNote() {
        return note;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<ErrorsResponse> getErrors() {
        return errors;
    }
}
