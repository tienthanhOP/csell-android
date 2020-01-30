package csell.com.vn.csell.views.product.adapter.viewholder;

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import csell.com.vn.csell.R;
import csell.com.vn.csell.mycustoms.RoundedImageView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChooseImageViewHolder extends RecyclerView.ViewHolder {
    public RoundedImageView imgSelected;
    public CircleImageView imgRemove;
    public CoordinatorLayout layoutLoading;

    public ChooseImageViewHolder(View itemView) {
        super(itemView);
        imgRemove = itemView.findViewById(R.id.img_remove);
        imgSelected = itemView.findViewById(R.id.img_selected);
        layoutLoading = itemView.findViewById(R.id.layout_loading);
    }
}
