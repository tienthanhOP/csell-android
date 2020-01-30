package csell.com.vn.csell.views.product.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import csell.com.vn.csell.R;

public class HeaderChooseImageViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgChooseImage;

    public HeaderChooseImageViewHolder(View itemView) {
        super(itemView);
        imgChooseImage = itemView.findViewById(R.id.img_choose);
    }
}
