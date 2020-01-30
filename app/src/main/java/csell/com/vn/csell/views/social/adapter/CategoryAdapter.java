package csell.com.vn.csell.views.social.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.models.CategoryRequest;

public class CategoryAdapter extends RecyclerView.Adapter {
    private List<CategoryRequest> categoryRequestList;

    public void setCategoryRequestList(List<CategoryRequest> categoryRequestList) {
        this.categoryRequestList = categoryRequestList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cat, parent, false);
        return new CategoryVH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CategoryVH categoryVH = (CategoryVH) holder;
        categoryVH.tvCat.setText(categoryRequestList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (categoryRequestList != null) {
            return categoryRequestList.size();
        } else {
            return 0;
        }
    }

    class CategoryVH extends RecyclerView.ViewHolder {
        public TextView tvCat;

        public CategoryVH(View itemView) {
            super(itemView);
            tvCat = itemView.findViewById(R.id.tv_tag_cat_one);
        }
    }
}
