package csell.com.vn.csell.models;

import java.io.Serializable;

/**
 * Created by chuc.nq on 3/27/2018.
 */

public class Category implements Serializable {

    public String category_id;
    public Integer level;
    public String category_name;
    public String image;
    public String image_web;
    public Integer max_level;
    public Integer priority;
    public String background;
    public String image_sub;
    public boolean is_select;

}
