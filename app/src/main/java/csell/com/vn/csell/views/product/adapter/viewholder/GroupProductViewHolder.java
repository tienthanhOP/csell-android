package csell.com.vn.csell.views.product.adapter.viewholder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import csell.com.vn.csell.R;

/**
 * Created by cuong.nv on 4/17/2018.
 */

public class GroupProductViewHolder extends RecyclerView.ViewHolder {

    public TextView txtTitle;
    public TextView txtNumberAll;
    public TextView txtLastupdate;
    public ImageView iconCategory;
    public ImageView layout;

    public GroupProductViewHolder(View itemView) {
        super(itemView);
        txtTitle = itemView.findViewById(R.id.txt_TitleAll);
        txtNumberAll = itemView.findViewById(R.id.txt_numberAll);
        iconCategory = itemView.findViewById(R.id.icon_category);
        txtLastupdate = itemView.findViewById(R.id.txt_lastupdate);
        layout = itemView.findViewById(R.id.layout_background);
    }
}
