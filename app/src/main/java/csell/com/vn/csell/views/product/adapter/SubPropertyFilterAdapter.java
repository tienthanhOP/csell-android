package csell.com.vn.csell.views.product.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.models.PropertyFilterValue;


public class SubPropertyFilterAdapter extends RecyclerView.Adapter<SubPropertyFilterAdapter.ViewHoler> {
    private List<PropertyFilterValue> data;
    private int selectOld = -1;
    private Context mContext;
    private ItemClickProperty itemClickProperty;
    private int indexing;

    public SubPropertyFilterAdapter(Context context, List<PropertyFilterValue> data, int pickedIndex,
                                    ItemClickProperty itemClickProperty) {
        this.data = data;
        this.mContext = context;
        this.itemClickProperty = itemClickProperty;
        this.indexing = pickedIndex;
    }

    public interface ItemClickProperty {
        void onItemClick(PropertyFilterValue propertyFilterValue, int position);
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_filter, parent, false);
        return new ViewHoler(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHoler holder, @SuppressLint("RecyclerView") int position) {
        try {
            holder.txtValue.setText(data.get(position).value + "");

            if (indexing != -1) {
                data.get(position).isSelected = indexing == position;
            }

            if (data.get(position).isSelected) {
                holder.layoutValue.setCardBackgroundColor(mContext.getResources().getColor(R.color.blue_main));
                holder.txtValue.setTextColor(mContext.getResources().getColor(R.color.white_100));
            } else {
                holder.layoutValue.setCardBackgroundColor(mContext.getResources().getColor(R.color.white_100));
                holder.txtValue.setTextColor(mContext.getResources().getColor(R.color.text_item_job_filter_social));
            }

            holder.itemView.setOnClickListener(v -> {

                itemClickProperty.onItemClick(data.get(position), position);

                data.get(position).isSelected = true;
                if (selectOld == -1) {
                    selectOld = position;
                    notifyItemChanged(position);
                } else {
                    if (selectOld == position) return;
                    data.get(selectOld).isSelected = false;
                    notifyItemChanged(position);
                    notifyItemChanged(selectOld);
                    selectOld = position;
                }
            });

        } catch (Exception e) {
            Log.e(getClass().getName(), e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    class ViewHoler extends RecyclerView.ViewHolder {
        CardView layoutValue;
        TextView txtValue;

        ViewHoler(View itemView) {
            super(itemView);
            txtValue = itemView.findViewById(R.id.txt_job);
            layoutValue = itemView.findViewById(R.id.layout_job);
        }
    }
}
