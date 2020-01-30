package csell.com.vn.csell.views.csell.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.models.Location;

/**
 * Created by cuong.nv on 4/13/2018.
 */

public class LocationSortAdapter extends BaseAdapter {

    private Context context;
    private List<Location> locations;

    public LocationSortAdapter(Context context, List<Location> locations) {
        this.context = context;
        this.locations = locations;
    }

    public int getPositionByValue(String value) {
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).getLocation_name().equals(value)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public int getCount() {
        return locations.size();
    }

    @Override
    public Location getItem(int position) {
        return locations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem holder = new ViewHolderItem();
        View itemView = convertView;

        try {
            if (itemView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (layoutInflater != null) {
                    itemView = layoutInflater.inflate(R.layout.item_location, parent, false);
                    holder.txtLocationName = itemView.findViewById(R.id.txtLocationName);
                    itemView.setTag(holder);
                }
            } else {
                holder = (ViewHolderItem) itemView.getTag();
            }

            holder.txtLocationName.setText(locations.get(position).getLocation_name());
        } catch (Exception e) {
            Log.i("xxxx", "getView: " + e.getMessage());
        }

        return itemView;
    }

    private class ViewHolderItem {
        TextView txtLocationName;
    }
}
