package csell.com.vn.csell.views.product.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.models.Category;

/**
 * Created by cuong.nv on 3/8/2018.
 */

public class SpinnerCateProjectAdapter extends BaseAdapter {

    private Context context;
    private List<Category> data;

    public SpinnerCateProjectAdapter(Context context, List<Category> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Category getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        ViewHolder holder = new ViewHolder();
        if (itemView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = layoutInflater.inflate(R.layout.item_currency, parent, false);
            holder.textView = itemView.findViewById(R.id.txt_currencyName);
            itemView.setTag(holder);
        } else {
            holder = (ViewHolder) itemView.getTag();
        }

        holder.textView.setText(data.get(position).category_name);

        return itemView;
    }

    public class ViewHolder {
        TextView textView;
    }
}
