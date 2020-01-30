package csell.com.vn.csell.views.product.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import csell.com.vn.csell.R;

public class HeaderProductViewHolder extends RecyclerView.ViewHolder {
    public TextView txtTitle;
    public HeaderProductViewHolder(View itemView) {
        super(itemView);
        this.txtTitle = (TextView) itemView.findViewById(R.id.txtHeader);
    }
}
