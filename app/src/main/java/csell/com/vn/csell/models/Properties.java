package csell.com.vn.csell.models;

import java.util.List;

/**
 * Created by cuong.nv on 3/28/2018.
 */

public class Properties {

    public String category_id;
    public String display_name;
    public String property_name;
    public String type;
    public Integer index;
    public String action;
    public String image;
    public Integer primary;

    public List<PropertyValue> property_default_values;

    public String pickedValue = "";

}
