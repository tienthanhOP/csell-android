package csell.com.vn.csell.views.social.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import csell.com.vn.csell.R;
import csell.com.vn.csell.views.customer.activity.AddOrEditCustomerActivity;
import csell.com.vn.csell.views.social.adapter.viewholder.HashtagViewHolder;

public class HashtagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> data;

    public HashtagAdapter(ArrayList<String> list){
        this.data = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hash_tag, parent, false);
        return new HashtagViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        HashtagViewHolder holderItem = (HashtagViewHolder) holder;
        holderItem.tvName.setText(data.get(position));
        holderItem.imgDelete.setOnClickListener(v -> {
            data.remove(position);
            notifyDataSetChanged();

            if (data.size() == 0){
                AddOrEditCustomerActivity.rvHashtag.setVisibility(View.GONE);
            }

        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
