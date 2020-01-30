package csell.com.vn.csell.views.product.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import csell.com.vn.csell.R;

/**
 * Created by Thang Nguyen on 4/24/2018.
 */

public class EndProductViewHolder extends RecyclerView.ViewHolder {

    public TextView txtProductName;
    public TextView txtProductPrice;
    public TextView txtProductDate;
    public TextView txtProductDescription;
    public ImageView imgProductImage;
    public LinearLayout layout_price_sim;

    public EndProductViewHolder(View itemView) {
        super(itemView);
        txtProductName = itemView.findViewById(R.id.item_end_product_name);
        txtProductPrice = itemView.findViewById(R.id.item_end_product_price);
        txtProductDate = itemView.findViewById(R.id.item_end_product_date);
        txtProductDescription = itemView.findViewById(R.id.item_end_product_description);
        imgProductImage = itemView.findViewById(R.id.item_end_product_img);
        layout_price_sim = itemView.findViewById(R.id.layout_price_sim);

    }
}