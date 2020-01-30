package csell.com.vn.csell.views.product.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.interfaces.OnItemClick;
import csell.com.vn.csell.models.PropertyValue;
import csell.com.vn.csell.views.product.adapter.viewholder.SubPropertyVH;

public class SubPropertyAdapter extends RecyclerView.Adapter<SubPropertyVH> {

    private List<PropertyValue> data;
    private int selectOld;
    private Context context;
    private OnItemClick listener;
    private String indexing;

    SubPropertyAdapter(Context context, List<PropertyValue> data, String pickedIndex, OnItemClick listenerClick) {
        this.data = data;
        this.context = context;
        this.listener = listenerClick;
        this.indexing = pickedIndex;
        selectOld = Integer.parseInt(indexing);
        if (selectOld >= 0 && selectOld < data.size()) {
            data.get(selectOld).isSelected = true;
        }
    }

    @NonNull
    @Override
    public SubPropertyVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_item_select_property, parent, false);
        return new SubPropertyVH(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SubPropertyVH holder, @SuppressLint("RecyclerView") int position) {
        try {
            holder.txtValue.setText(data.get(position).value + "");

            if (data.get(position).isSelected) {
                holder.imgSelected.setVisibility(View.VISIBLE);
            } else {
                holder.imgSelected.setVisibility(View.GONE);
            }

            String key = data.get(position).key + "";

            if (key.startsWith("#")) {
                if (data.get(position).value.toLowerCase().equals(Utilities.COLOR_WHITE) ||
                        data.get(position).value.toLowerCase().equals(Utilities.COLOR_SILVER)) {
                    holder.txtValue.setTextColor(context.getResources().getColor(R.color.text_end_create_product));
                    holder.cardViewValue.setCardBackgroundColor(Color.parseColor(key));
                } else {
                    holder.cardViewValue.setCardBackgroundColor(Color.parseColor(key));
                    holder.txtValue.setTextColor(Color.WHITE);
                }
            } else {
                holder.txtValue.setTextColor(context.getResources().getColor(R.color.text_end_create_product));
                holder.cardViewValue.setCardBackgroundColor(Color.WHITE);
            }

            holder.itemView.setOnClickListener(v -> {

                listener.onClick(data.get(position), position);

                data.get(position).isSelected = true;
                holder.imgSelected.setVisibility(View.VISIBLE);
                if (selectOld == -1) {
                    selectOld = position;
                } else {
                    if (selectOld == position) return;
                    data.get(selectOld).isSelected = false;
                    notifyItemChanged(position);
                    notifyItemChanged(selectOld);
                    selectOld = position;
                }
            });
//
//            if (!TextUtils.isEmpty(indexing) && TextUtils.isDigitsOnly(indexing)) {
//                if (Integer.parseInt(indexing) == position) {
//                    data.get(position).isSelected = true;
////                    holder.txtValue.setBackground(context.getDrawable(R.drawable.bg_select_property));
//                    holder.imgSelected.setVisibility(View.VISIBLE);
//                } else {
//                    data.get(position).isSelected = false;
////                    holder.txtValue.setBackground(context.getDrawable(R.drawable.bg_unselect_property));
//                    holder.imgSelected.setVisibility(View.GONE);
//                }
//            }

        } catch (Exception e) {
            Log.e(getClass().getName(), e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
