package csell.com.vn.csell.views.customer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.models.CustomerRetro;

public class CustomerAutoCompletedTextAdapter extends ArrayAdapter<CustomerRetro> {

    private Context context;
    private int resource;
    private List<CustomerRetro> locations;
    private List<CustomerRetro> suggestions;
    private List<CustomerRetro> temp;

    public CustomerAutoCompletedTextAdapter(Context context, int resource, List<CustomerRetro> locations) {
        super(context, resource, locations);
        this.context = context;
        this.resource = resource;
        this.locations = locations;
        this.suggestions = new ArrayList<>(locations);
        this.temp = new ArrayList<>(locations);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        CustomerAutoCompletedTextAdapter.ViewHolderItem holder = new CustomerAutoCompletedTextAdapter.ViewHolderItem();
        View itemView = convertView;
        if (itemView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = layoutInflater.inflate(resource, parent, false);
            holder.txtLocationName = itemView.findViewById(R.id.txtLocationName);
            itemView.setTag(holder);
        } else {
            holder = (CustomerAutoCompletedTextAdapter.ViewHolderItem) itemView.getTag();
        }

        holder.txtLocationName.setText(locations.get(position).getName());

        return itemView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return myFilter;
    }

    private Filter myFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            CustomerRetro location = (CustomerRetro) resultValue;
            return location.getName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (CustomerRetro item : temp) {
                    if (item.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(item);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<CustomerRetro> c = (ArrayList<CustomerRetro>) results.values;
            if (results.count > 0) {
                clear();
                for (CustomerRetro cust : c) {
                    add(cust);
                    notifyDataSetChanged();
                }
            } else {
                clear();
                notifyDataSetChanged();
            }
        }
    };

    private class ViewHolderItem {
        TextView txtLocationName;
    }
}
