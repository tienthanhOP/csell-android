package csell.com.vn.csell.views.product.adapter.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import csell.com.vn.csell.R;

public class SubPropertyVH extends RecyclerView.ViewHolder {
    public TextView txtValue;
    public ImageView imgSelected;
    public CardView cardViewValue;

    public SubPropertyVH(View itemView) {
        super(itemView);
        txtValue = itemView.findViewById(R.id.txt_value);
        imgSelected = itemView.findViewById(R.id.img_selected);
        cardViewValue = itemView.findViewById(R.id.card_view_value);
    }
}