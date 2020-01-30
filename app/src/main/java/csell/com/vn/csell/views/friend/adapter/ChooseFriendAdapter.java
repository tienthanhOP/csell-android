package csell.com.vn.csell.views.friend.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import csell.com.vn.csell.R;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.views.friend.adapter.viewholder.FriendViewHolder;

public class ChooseFriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<UserRetro> data;

    public ChooseFriendAdapter(Context context, ArrayList<UserRetro> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ItemFriendHolder extends RecyclerView.ViewHolder {
        public TextView txtName, txtEmail;
        public TextView avata;
        public CheckBox checkBox;

        public ItemFriendHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            avata = itemView.findViewById(R.id.icon_avatar_text);
            checkBox = itemView.findViewById(R.id.checkAddPeople);
        }
    }
}
