package csell.com.vn.csell.views.social.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.account.activity.PersonalPageActivity;
import csell.com.vn.csell.views.friend.activity.FriendDetailsActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.controllers.FriendsController;
import csell.com.vn.csell.models.FriendInfo;
import csell.com.vn.csell.models.UserRetro;
import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by User on 4/5/2018.
 */

public class ListLikesFirebaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<UserRetro> data;
    private FileSave fileSave;
    private BaseActivityTransition baseActivityTransition;

    public ListLikesFirebaseAdapter(Context mContext, ArrayList<UserRetro> data) {
        this.mContext = mContext;
        this.data = data;
        fileSave = new FileSave(mContext, Constants.GET);
        baseActivityTransition = new BaseActivityTransition(mContext);
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new DataViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            DataViewHolder viewHolder = (DataViewHolder) holder;
            UserRetro model = data.get(position);

            if (model.getUid().equals(fileSave.getUserId())) {
                viewHolder.btnAddFriend.setVisibility(View.GONE);
                viewHolder.itemView.setOnClickListener(v -> {
                    Intent detail = new Intent(mContext, PersonalPageActivity.class);
                    detail.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, data.get(position));
                    baseActivityTransition.transitionTo(detail, 0);
                });
            } else {
                viewHolder.btnAddFriend.setVisibility(View.VISIBLE);
                if (model.getFriendInfo() == null) {
                    viewHolder.btnAddFriend.setText(mContext.getString(R.string.add_friend));
                } else {
                    if (model.getFriendInfo().getIsAccepted()) {

                        viewHolder.btnAddFriend.setText(mContext.getString(R.string.friend));
                        viewHolder.btnAddFriend.setTextColor(Color.BLACK);
                        viewHolder.btnAddFriend.setBackgroundColor(Color.WHITE);
                        viewHolder.btnAddFriend.setBorderWidth(1);
                        viewHolder.btnAddFriend.setBorderColor(Color.GRAY);
                    } else {
                        viewHolder.btnAddFriend.setTextColor(Color.WHITE);
                        viewHolder.btnAddFriend.setBackgroundColor(mContext.getResources().getColor(R.color.blue_main));
                        viewHolder.btnAddFriend.setBorderWidth(0);
                        if (model.getFriendInfo().getIsRequested()) {
                            viewHolder.btnAddFriend.setText(mContext.getString(R.string.already_requested));
                        } else if (!model.getFriendInfo().getIsAccepted() && !model.getFriendInfo().getIsRequested()) {
                            viewHolder.btnAddFriend.setText(mContext.getString(R.string.add_friend));
                        }
                    }

                }
            }

            viewHolder.itemView.setOnClickListener(v -> {

                Intent detail = new Intent(mContext, FriendDetailsActivity.class);
                detail.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, data.get(position));
                baseActivityTransition.transitionTo(detail, 0);
            });

            viewHolder.tvName.setText(model.getDisplayname() + "");

            GlideApp.with(mContext)
                    .load(model.getAvatar() + "")
                    .error(R.drawable.ic_logo)
                    .into(viewHolder.imgAvatar);

            viewHolder.btnAddFriend.setOnClickListener(v -> {
                FriendsController friendsController = new FriendsController(mContext);
                if (model.getFriendInfo() == null) {
                    friendsController.addFriendRequest(model);
                    model.setFriendInfo(new FriendInfo(false, true));
                    notifyItemChanged(position);
                } else {
                    if (model.getFriendInfo().getIsAccepted()) {
                        return;
                    }

                    if (model.getFriendInfo().getIsRequested()) {
                        friendsController.denyFriendRequest(model);
                        model.setFriendInfo(new FriendInfo(false, false));
                    } else if (!model.getFriendInfo().getIsAccepted() && !model.getFriendInfo().getIsRequested()) {
                        friendsController.addFriendRequest(model);
                        model.setFriendInfo(new FriendInfo(false, true));
                    }
                    notifyItemChanged(position);
                }
            });

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imgAvatar;
        TextView tvName, tvStatus;
        ImageButton btnChat;
        public FancyButton btnAddFriend;
        RelativeLayout layoutSendMessage;

        DataViewHolder(View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_avatar_contact_friend);
            tvName = itemView.findViewById(R.id.tv_name_customer_friend);
            tvStatus = itemView.findViewById(R.id.tv_status_customer_friend);
            btnChat = itemView.findViewById(R.id.btn_chat_customer_friend);
            btnAddFriend = itemView.findViewById(R.id.btnAddFriend);
            layoutSendMessage = itemView.findViewById(R.id.layout_send_message);

            tvStatus.setVisibility(View.GONE);
            btnChat.setVisibility(View.GONE);
            btnAddFriend.setVisibility(View.VISIBLE);
            layoutSendMessage.setVisibility(View.GONE);
        }

    }
}
