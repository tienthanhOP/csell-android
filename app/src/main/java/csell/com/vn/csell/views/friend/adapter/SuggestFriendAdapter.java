package csell.com.vn.csell.views.friend.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.models.UserRetro;
import mehdi.sakout.fancybuttons.FancyButton;

public class SuggestFriendAdapter extends RecyclerView.Adapter<SuggestFriendAdapter.ViewHolder> {

    private Context context;
    private ArrayList<UserRetro> data;

    private FileSave fileSave;

    public SuggestFriendAdapter(Context context, ArrayList<UserRetro> data) {
        this.context = context;
        this.data = data;
        fileSave = new FileSave(context, Constants.GET);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflating recycle view item layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_list_suggest_friend, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.txtName.setText(data.get(position).getDisplayname());
        holder.txtPhone.setText(data.get(position).getPhone());
        // find myself
//        if (data.get(position).getUserId() == fileSave.getUserId()) {
//            holder.btnAddFriend.setVisibility(View.GONE);
//        } else {
//            holder.btnAddFriend.setVisibility(View.VISIBLE);
//        }

//        if (data.get(position).getIsFriend() == 1){
//            holder.btnAddFriend.setVisibility(View.GONE);
//        } else {
//            holder.btnAddFriend.setVisibility(View.VISIBLE);
//        }
//
//        if (data.get(position).getIsRequested() == 1) {
//            holder.btnAddFriend.setText(context.getString(R.string.text_requested_friend));
//            holder.btnAddFriend.setEnabled(false);
//        } else {
//            holder.btnAddFriend.setText(context.getString(R.string.text_add_friend));
//            holder.btnAddFriend.setEnabled(true);
//        }

//        ViewHolder finalHolder = holder;
//        holder.btnAddFriend.setOnClickListener(v -> {
////            addFriend(position, finalHolder);
//        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtName,txtPhone;
        ImageView imgAvatar;
//        ImageView imgChat;
        FancyButton btnAddFriend;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.tv_name_customer_friend);
            txtPhone = itemView.findViewById(R.id.tv_phone_customer_friend);
            imgAvatar = itemView.findViewById(R.id.img_avatar_contact_friend);
//            holder.imgChat = itemView.findViewById(R.id.btn_chat_customer_friend);
            btnAddFriend = itemView.findViewById(R.id.btnAddFriend);
//            holder.imgChat.setVisibility(View.GONE);
        }
    }

}
