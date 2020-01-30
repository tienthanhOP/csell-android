package csell.com.vn.csell.models;

import java.io.Serializable;

public class NameLanguage implements Serializable {
    Integer title_code;
    String title_name;
    String display_name_vi;
    String display_name_en;
    String display_name_ko;
    String display_name_fr;

    public Integer getTitle_code() {
        return title_code;
    }

    public void setTitle_code(Integer title_code) {
        this.title_code = title_code;
    }

    public String getTitle_name() {
        return title_name;
    }

    public void setTitle_name(String title_name) {
        this.title_name = title_name;
    }

    public String getDisplay_name_vi() {
        return display_name_vi;
    }

    public void setDisplay_name_vi(String display_name_vi) {
        this.display_name_vi = display_name_vi;
    }

    public String getDisplay_name_en() {
        return display_name_en;
    }

    public void setDisplay_name_en(String display_name_en) {
        this.display_name_en = display_name_en;
    }

    public String getDisplay_name_ko() {
        return display_name_ko;
    }

    public void setDisplay_name_ko(String display_name_ko) {
        this.display_name_ko = display_name_ko;
    }

    public String getDisplay_name_fr() {
        return display_name_fr;
    }

    public void setDisplay_name_fr(String display_name_fr) {
        this.display_name_fr = display_name_fr;
    }
}
