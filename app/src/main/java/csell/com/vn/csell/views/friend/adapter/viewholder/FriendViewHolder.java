package csell.com.vn.csell.views.friend.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import csell.com.vn.csell.R;
import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by cuong.nv on 4/16/2018.
 */

public class FriendViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView imgAvatar;
    public TextView tvName, tvStatus;
    public ImageButton btnChat;
    public FancyButton btnAddFriend;
    public RelativeLayout layoutSendMessage;

    public FriendViewHolder(View itemView) {
        super(itemView);
        imgAvatar = itemView.findViewById(R.id.img_avatar_contact_friend);
        tvName = itemView.findViewById(R.id.tv_name_customer_friend);
        tvStatus = itemView.findViewById(R.id.tv_status_customer_friend);
        btnChat = itemView.findViewById(R.id.btn_chat_customer_friend);
        btnAddFriend = itemView.findViewById(R.id.btnAddFriend);
        btnAddFriend.setVisibility(View.GONE);
        layoutSendMessage = itemView.findViewById(R.id.layout_send_message);
    }
}
