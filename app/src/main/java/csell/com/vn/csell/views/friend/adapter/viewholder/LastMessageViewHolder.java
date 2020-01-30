package csell.com.vn.csell.views.friend.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import csell.com.vn.csell.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cuong.nv on 4/23/2018.
 */

public class LastMessageViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView img_avatar_receiver;
    public CircleImageView icon_category;
    public TextView txtReceiverName;
    public TextView txtLastTime;
    public TextView txtLastMessage;
    public TextView txtCountUnread;
    public RelativeLayout layout;
    public LinearLayout.LayoutParams params;

    public LastMessageViewHolder(View itemView) {
        super(itemView);

        img_avatar_receiver = itemView.findViewById(R.id.img_avatar_receiver);
        icon_category = itemView.findViewById(R.id.icon_category);
        txtReceiverName = itemView.findViewById(R.id.txtReceiverName);
        txtLastTime = itemView.findViewById(R.id.txtLastTime);
        txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
        layout = itemView.findViewById(R.id.layout);
        txtCountUnread = itemView.findViewById(R.id.count_unread);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void hideLayout() {
        params.height = 0;
        layout.setLayoutParams(params);
    }

    public void showLayout() {
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layout.setLayoutParams(params);
    }

}
