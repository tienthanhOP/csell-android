package csell.com.vn.csell.views.social.adapter.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import csell.com.vn.csell.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cuong.nv on 4/27/2018.
 */

public class SocialViewHolder extends RecyclerView.ViewHolder {
    public TextView tvNameProduct, tvNameOwner, tvPrice, tvLastUpdated, tvDescription;
    public ImageView  imgProduct, imgTypePost;
    public CircleImageView imgUserComment, imgFriendAvatar, imgFriendAvatarComment;
    public LinearLayout btnLike, btnComment, btnShare;
//    public TextView edtComment;
    public TextView txtReadMore;

    public LinearLayout layoutFollow, layoutReup;//, layoutMenu;
    public RelativeLayout itemComment;
    public RelativeLayout layoutChangedPostType;
    public RelativeLayout itemFriendComment;
    public TextView tvFriendName, tvContentFriendComment, tvLastUpdatedFriendComment;

    public ImageView imgLike, imgCountLike;
    public TextView tvLike, tvCountLike, tvCountComments;
    public LinearLayout layoutCountLike;
    public LinearLayout fromTitle;

    public CardView layoutProduct;
    public CardView layoutSim;
    public LinearLayout layoutPriceSim;
    public LinearLayout from_show_like;

    public ImageView imgSimBG;
    public TextView tvSimTitle;
    public TextView tvSimDes;
    public TextView tvSimPrice;
    public  FrameLayout frameLayout;
    public TextView tvDescriptionHolder;

    public SocialViewHolder(View itemView) {
        super(itemView);
        frameLayout = itemView.findViewById(R.id.frame);
        fromTitle = itemView.findViewById(R.id.from_title);
        itemComment = itemView.findViewById(R.id.item_comment);
        layoutChangedPostType = itemView.findViewById(R.id.layout_changed_post_type);
        itemFriendComment = itemView.findViewById(R.id.item_friend_comment);

        tvNameProduct = itemView.findViewById(R.id.category_friend_tv_name_product);
        from_show_like = itemView.findViewById(R.id.from_show_like);
        tvNameOwner = itemView.findViewById(R.id.category_friend_tv_name_owner);
        tvPrice = itemView.findViewById(R.id.category_friend_tv_price_product);
        tvLastUpdated = itemView.findViewById(R.id.category_friend_tv_last_updated);
        tvDescription = itemView.findViewById(R.id.category_friend_tv_description);
//        tvStatus = itemView.findViewById(R.id.category_friend_status);
        tvFriendName = itemView.findViewById(R.id.tv_friend_name);
        tvContentFriendComment = itemView.findViewById(R.id.tv_content_friend_comment);
        tvLastUpdatedFriendComment = itemView.findViewById(R.id.tv_last_update_friend_comment);
        txtReadMore = itemView.findViewById(R.id.txtReadMore);

        imgProduct = itemView.findViewById(R.id.category_friend_img_product);
//        layoutMenu = itemView.findViewById(R.id.category_friend_img_save);
        imgTypePost = itemView.findViewById(R.id.img_type_post);
//        imgSendComment = itemView.findViewById(R.id.iconSendComment);
        imgFriendAvatar = itemView.findViewById(R.id.img_avatar_friend);
        imgFriendAvatarComment = itemView.findViewById(R.id.img_friend_comment);
        imgUserComment = itemView.findViewById(R.id.img_user_comment);
        btnLike = itemView.findViewById(R.id.btn_like_post);
        btnComment = itemView.findViewById(R.id.btn_comment_post);
        btnShare = itemView.findViewById(R.id.btn_share_post);

//        edtComment = itemView.findViewById(R.id.tv_content_comment);

        imgCountLike = itemView.findViewById(R.id.img_count_like);
        imgLike = itemView.findViewById(R.id.img_like);
        tvLike = itemView.findViewById(R.id.tv_like);
        tvCountLike = itemView.findViewById(R.id.tv_count_like);
        tvCountComments = itemView.findViewById(R.id.tv_count_comments);
        layoutCountLike = itemView.findViewById(R.id.layout_count_like);

        layoutFollow = itemView.findViewById(R.id.layout_follow);
        layoutReup = itemView.findViewById(R.id.layout_reup);
        layoutProduct = itemView.findViewById(R.id.layout_product);

        //sim
        imgSimBG = itemView.findViewById(R.id.item_end_product_img);
        layoutPriceSim = itemView.findViewById(R.id.layout_price_sim);
        tvSimTitle = itemView.findViewById(R.id.item_end_product_name);
        tvSimDes = itemView.findViewById(R.id.item_end_product_description);
        tvSimPrice = itemView.findViewById(R.id.item_end_product_price);
        layoutSim = itemView.findViewById(R.id.layout_sim);
        tvDescriptionHolder = itemView.findViewById(R.id.item_end_product_description);

    }
}
