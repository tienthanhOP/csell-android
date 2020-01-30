package csell.com.vn.csell.views.friend.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.UserRetro;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by chuc.nq on 2/8/2018.
 */

public class FriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<UserRetro> data;
    private ArrayList<UserRetro> mListFilter;

    public FriendAdapter(Context context, ArrayList<UserRetro> arrayList) {
        this.mContext = context;
        this.data = arrayList;
        mListFilter = new ArrayList<>();
        this.mListFilter.addAll(data);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_item_add_friend, parent, false);
        return new ViewHolderItem(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        try {
            ViewHolderItem holderItem = (ViewHolderItem) holder;
            holderItem.txtName.setText(data.get(position).getDisplayname());

            GlideApp.with(mContext).load(data.get(position).getAvatar())
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo).into(holderItem.imgAvatar);

            if (!TextUtils.isEmpty(data.get(position).getEmail())) {
                holderItem.tvEmail.setVisibility(View.VISIBLE);
            } else {
                holderItem.tvEmail.setVisibility(View.GONE);
            }

            if (data.get(position).IsSelected) {
                holderItem.checkBox.setChecked(true);
            } else {
                holderItem.checkBox.setChecked(false);
            }

            holderItem.itemView.setOnClickListener(v -> {
                if (holderItem.checkBox.isChecked()) {
                    //remove to list
                    //                Utilities.lsFriendsShareNotePrivate.remove(data.get(position));
                    holderItem.checkBox.setChecked(false);
                } else {
                    holderItem.checkBox.setChecked(true);
                    //                Utilities.lsFriendsShareNotePrivate.add(data.get(position).addToMeMap());
                }
            });

            holderItem.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked) {
                    //remove to list
                    data.get(position).IsSelected = false;
                    int pos = Utilities.lsFriendsNotePrivate.indexOf(data.get(position));
                    if (pos != -1) {
                        Utilities.lsFriendsNotePrivate.remove(pos);
                    }
                    holderItem.checkBox.setChecked(false);
                } else {
                    data.get(position).IsSelected = true;
                    holderItem.checkBox.setChecked(true);
                    Utilities.lsFriendsNotePrivate.add(data.get(position));
                }
            });
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

//    public void updateList(ArrayList<UserRetro> list) {
//
//        data.addAll(list);
//    }

    class ViewHolderItem extends RecyclerView.ViewHolder {

        CircleImageView imgAvatar;
        TextView txtName;
        CheckBox checkBox;
        TextView txtAvatar;
        TextView tvEmail;

        public ViewHolderItem(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtName = itemView.findViewById(R.id.txtName);
            checkBox = itemView.findViewById(R.id.checkAddPeople);
            imgAvatar = itemView.findViewById(R.id.img_avatar);
            txtAvatar = itemView.findViewById(R.id.icon_avatar_text);
            txtAvatar.setVisibility(View.GONE);
            imgAvatar.setVisibility(View.VISIBLE);
            tvEmail = itemView.findViewById(R.id.txtEmail);
        }
    }

    public void findFriend(String s) {
        data.clear();
        if (TextUtils.isEmpty(s)) {
            data.addAll(mListFilter);
        } else {

            for (UserRetro friend : mListFilter) {

                if (friend.getDisplayname().toLowerCase().contains(s) || friend.getPhone().contains(s)) {
                    data.add(friend);
                }
            }
        }
        notifyDataSetChanged();

    }


}
