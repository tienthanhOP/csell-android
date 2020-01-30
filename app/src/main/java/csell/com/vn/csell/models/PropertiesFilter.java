package csell.com.vn.csell.models;

import java.util.List;

public class PropertiesFilter {
    public String cate_id;
    public Integer index;
    public String filter_id;
    public String displayname;
    public String view_type;
    public boolean get_by_key;
    public boolean get_by_child;
    public List<PropertyFilterValue> values;

    public String pickedIndex = "";
    public int choose_value_index = -1;
    public String choose_value = "";
}
