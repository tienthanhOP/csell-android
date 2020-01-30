package csell.com.vn.csell.views.friend.adapter.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import csell.com.vn.csell.R;
import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class FindFriendViewHolder extends RecyclerView.ViewHolder {
    public TextView txtName;
    public TextView tvtStatus;
    public ImageView imgAvatar;
    public ImageView iconCategoryMain;
    public ImageView iconCategorySub;
    public ImageButton imgChat;
    public FancyButton btnAddFriend;
    public ProgressBar progressLoading;

    public FindFriendViewHolder(View itemView) {
        super(itemView);
        txtName = itemView.findViewById(R.id.tv_name_customer_friend);
        tvtStatus = itemView.findViewById(R.id.tv_status_customer_friend);
        progressLoading = itemView.findViewById(R.id.progress_loading);
        imgAvatar = itemView.findViewById(R.id.img_avatar_contact_friend);
        iconCategoryMain = itemView.findViewById(R.id.icon_category_main);
        iconCategorySub = itemView.findViewById(R.id.icon_category_sub);
        imgChat = itemView.findViewById(R.id.btn_chat_customer_friend);
        btnAddFriend = itemView.findViewById(R.id.btnAddFriend);
        imgChat.setVisibility(View.GONE);
    }

    public static class PendingMessageViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView img_avatar_receiver;
        public CircleImageView icon_category;
        public TextView txtReceiverName;
        public TextView txtLastTime;
        public TextView txtLastMessage;
        public ImageButton btnAccept, btnDeny;

        public PendingMessageViewHolder(View itemView) {
            super(itemView);
            img_avatar_receiver = itemView.findViewById(R.id.img_avatar_receiver);
            icon_category = itemView.findViewById(R.id.icon_category);
            txtReceiverName = itemView.findViewById(R.id.txtReceiverName);
            txtLastTime = itemView.findViewById(R.id.txtLastTime);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnDeny = itemView.findViewById(R.id.btn_deny);
        }
    }

    /**
     * Created by cuong.nv on 4/23/2018.
     */

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layoutControlItemChat;
        public TextView txtContentMessage;
        public CircleImageView img_avatar_sender;
        public CircleImageView img_avatar_receiver;
        public CardView cardView;
        public TextView tvProductName;
        public TextView tvProductDescription;
        public TextView tvPrice;
        public ImageView imgProduct;

        public MessageViewHolder(View itemView) {
            super(itemView);
            layoutControlItemChat = itemView.findViewById(R.id.layout_control_item_chat);
            txtContentMessage = itemView.findViewById(R.id.txt_content_message);
            img_avatar_sender = itemView.findViewById(R.id.img_avatar_sender);
            img_avatar_receiver = itemView.findViewById(R.id.img_avatar_receiver);
            cardView = itemView.findViewById(R.id.card_view);
            imgProduct = itemView.findViewById(R.id.item_end_product_img);
            tvProductName = itemView.findViewById(R.id.item_end_product_name);
            tvProductDescription = itemView.findViewById(R.id.item_end_product_description);
            tvPrice = itemView.findViewById(R.id.item_end_product_price);
        }
    }
}
