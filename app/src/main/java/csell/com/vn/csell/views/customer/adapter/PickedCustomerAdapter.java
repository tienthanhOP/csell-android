package csell.com.vn.csell.views.customer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import csell.com.vn.csell.R;
import csell.com.vn.csell.models.CustomerRetroV1;

public class PickedCustomerAdapter extends RecyclerView.Adapter<PickedCustomerAdapter.DataViewHolder> {

    private Context mContext;
    private ArrayList<CustomerRetroV1> data;

    public PickedCustomerAdapter(Context context, ArrayList<CustomerRetroV1> list) {
        this.mContext = context;
        this.data = list;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_picked_customer, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {

        holder.tvName.setText(data.get(position).getName());
        holder.tvAvatar.setText((data.get(position).getName().charAt(0) + "").toUpperCase());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class DataViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvAvatar;

        DataViewHolder(View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }

}
