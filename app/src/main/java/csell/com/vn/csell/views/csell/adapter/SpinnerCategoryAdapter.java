package csell.com.vn.csell.views.csell.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.models.Category;
import csell.com.vn.csell.sqlites.SQLLanguage;

/**
 * Created by cuong.nv on 4/13/2018.
 */

public class SpinnerCategoryAdapter extends BaseAdapter {

    private Context context;
    private List<Category> locations;
    private SQLLanguage sqlLanguage;
    private FileSave fileGet;

    public SpinnerCategoryAdapter(Context context, List<Category> locations) {
        this.context = context;
        this.locations = locations;
        sqlLanguage = new SQLLanguage(context);
        fileGet = new FileSave(context, Constants.GET);
    }

    @Override
    public int getCount() {
        return locations.size();
    }

    @Override
    public Object getItem(int position) {
        return locations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem holder = new ViewHolderItem();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater != null) {
                convertView = layoutInflater.inflate(R.layout.item_category_spinner, parent, false);
                holder.tvLocationName = convertView.findViewById(R.id.txtLocationName);
                convertView.setTag(holder);
            }
        } else {
            holder = (ViewHolderItem) convertView.getTag();
        }

        try {

            String nameCate = "";
            if (!TextUtils.isEmpty(locations.get(position).category_id + ""))
                nameCate = sqlLanguage.getDisplayNameByCatId(locations.get(position).category_id, fileGet.getLanguage());

            if (!TextUtils.isEmpty(nameCate))
                holder.tvLocationName.setText(nameCate);
            else
                holder.tvLocationName.setText(locations.get(position).category_name);
        } catch (NumberFormatException e) {
            holder.tvLocationName.setText(locations.get(position).category_name);
        }

        return convertView;
    }


    class ViewHolderItem {
        TextView tvLocationName;
    }

}
