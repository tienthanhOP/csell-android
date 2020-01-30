package csell.com.vn.csell.views.product.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.TimeAgo;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.Product;

/**
 * Created by User on 4/29/2018.
 */

public class FavoriteProductViewHolder extends RecyclerView.ViewHolder {

    TextView txtProductName;
    TextView txtProductPrice;
    TextView txtProductDate;
    TextView txtProductDescription;
    ImageView imgProductImage;
    LinearLayout layout;


    public FavoriteProductViewHolder(View itemView) {
        super(itemView);
        txtProductName = itemView.findViewById(R.id.item_end_product_name);
        txtProductPrice = itemView.findViewById(R.id.item_end_product_price);
        txtProductDate = itemView.findViewById(R.id.item_end_product_date);
        txtProductDescription = itemView.findViewById(R.id.item_end_product_description);
        imgProductImage = itemView.findViewById(R.id.item_end_product_img);
        layout = itemView.findViewById(R.id.layout);

    }

    public void setValue(Context mContext, Product product) {

        try {
            txtProductName.setText(product.getTitle());
            txtProductDescription.setText(product.getDescription());

            if (product.getPrice() != null) {
                if (product.getPrice() == 0) {
                    txtProductPrice.setText(mContext.getString(R.string.contact));
                } else {
                    String price = Utilities.formatMoney(product.getPrice(), product.getCurrency());
                    txtProductPrice.setText(price);
                }
            } else {
                txtProductPrice.setText(mContext.getString(R.string.contact));
            }

            if (product.getDateShared() != null) {
                txtProductDate.setText(TimeAgo.toRelative(Utilities.convertDateStringToMilisAllType(product.getDateShared()), Calendar.getInstance().getTime().getTime(), 1));
            } else {
                txtProductDate.setVisibility(View.GONE);
            }

            if (product.getImage() != null) {
                GlideApp.with(mContext)
                        .load(product.getImage().getPath())
                        .error(R.drawable.noimage)
                        .into(imgProductImage);
            } else {
                GlideApp.with(mContext)
                        .load(R.drawable.noimage)
                        .into(imgProductImage);

            }

        } catch (Exception e) {
           if(BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
        }

    }

}
