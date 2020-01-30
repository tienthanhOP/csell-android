package csell.com.vn.csell.views.social.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import csell.com.vn.csell.R;

public class SelectPriceInFilterAdapter extends RecyclerView.Adapter<SelectPriceInFilterAdapter.ViewHolder> {

    private List<String> data;

    public SelectPriceInFilterAdapter(List<String> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_price, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtPrice.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }
    }

}
