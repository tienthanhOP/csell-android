package csell.com.vn.csell.views.social.adapter;

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

import java.util.ArrayList;

import csell.com.vn.csell.R;
import csell.com.vn.csell.models.Category;

public class JobFilterAdapter extends RecyclerView.Adapter<JobFilterAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Category> data;
    private int posOld = -1;

    private ItemClickJob itemClickJob;

    public interface ItemClickJob {
        void onItemClick(View v, int position);
    }

    public JobFilterAdapter(Context mContext, ArrayList<Category> data, ItemClickJob itemClickJob) {
        this.mContext = mContext;
        this.data = data;
        this.itemClickJob = itemClickJob;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_filter, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            if (data.get(position).is_select) {
                holder.layoutJob.setCardBackgroundColor(mContext.getResources().getColor(R.color.blue_main));
                holder.txtJob.setTextColor(mContext.getResources().getColor(R.color.white_100));
                posOld = position;
            } else {
                holder.layoutJob.setCardBackgroundColor(mContext.getResources().getColor(R.color.white_100));
                holder.txtJob.setTextColor(mContext.getResources().getColor(R.color.text_item_job_filter_social));
            }

            holder.txtJob.setText(data.get(position).category_name + "");

            holder.itemView.setOnClickListener(v -> {
                if (data.get(position).is_select) {
                    data.get(position).is_select = false;
                    posOld = -1;
                    notifyItemChanged(position);
                } else {
                    data.get(position).is_select = true;

                    if (posOld == -1) {
                        notifyItemChanged(position);
                    } else {
                        data.get(posOld).is_select = false;
                        notifyItemChanged(posOld);
                        notifyItemChanged(position);
                    }
                    itemClickJob.onItemClick(v, position);
                }
            });
        } catch (Exception e) {
            Log.e("test: ", e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return (data == null) ? 0 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView layoutJob;
        TextView txtJob;

        public ViewHolder(View itemView) {
            super(itemView);
            txtJob = itemView.findViewById(R.id.txt_job);
            layoutJob = itemView.findViewById(R.id.layout_job);
        }
    }

}
