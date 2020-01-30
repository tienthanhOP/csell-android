package csell.com.vn.csell.views.social.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import csell.com.vn.csell.R;

public class HashtagViewHolder extends RecyclerView.ViewHolder {

    public TextView tvName;
    public ImageView imgDelete;

    public HashtagViewHolder(View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tv_hashtag_name);
        imgDelete = itemView.findViewById(R.id.img_delete);
    }
}
