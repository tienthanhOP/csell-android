package csell.com.vn.csell.views.customer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
//import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.customer.activity.AddCustomerFromContactActivity;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.ContactLocal;

public class AddCustomerFromContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<ContactLocal> data;
    private ArrayList<ContactLocal> mListFilter;
    public static boolean isSearching = false;

    public AddCustomerFromContactAdapter(Context context, ArrayList<ContactLocal> data) {
        this.data = data;
        this.mContext = context;
        mListFilter = new ArrayList<>();
        this.mListFilter.addAll(data);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggest_contact, parent, false);
        return new ItemViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            ItemViewHolder holderItem = (ItemViewHolder) holder;
            holderItem.tvName.setText(data.get(position).getContactName());

//            if (data.get(position).isAdded()) {
//                holderItem.tv_add.setVisibility(View.GONE);
//            } else {
//                holderItem.tv_add.setVisibility(View.VISIBLE);
//            }

            holderItem.tv_add.setOnCheckedChangeListener(null);

            if (!isSearching) {
                holderItem.clearView();
            }

            if (data.get(position).isSelectedGroup) {
                holderItem.tv_add.setSelected(true);
            } else {
                holderItem.tv_add.setSelected(false);
            }

            holderItem.tv_add.setOnCheckedChangeListener((buttonView, isChecked) -> {
                try {
                    if (isChecked) {
                        if (AddCustomerFromContactActivity.listIDChoose.size() != 0) {
                            int index = AddCustomerFromContactActivity.listIDChoose.indexOf(data.get(position));
                            if (index != -1) return;
                        }

                        AddCustomerFromContactActivity.listIDChoose.add(data.get(position));
                        data.get(holder.getAdapterPosition()).isSelectedGroup = true;

                        AddCustomerFromContactActivity.tvAddMultiple.setText(mContext.getString(R.string.add) + " "
                                + AddCustomerFromContactActivity.listIDChoose.size() + "/" + mListFilter.size());
                        AddCustomerFromContactActivity.tvAddMultiple.setTextColor(mContext.getResources().getColor(R.color.white_100));
                        AddCustomerFromContactActivity.tvAddMultiple.setBackgroundColor(mContext.getResources().getColor(R.color.red_100));

                    } else {
                        int pos = AddCustomerFromContactActivity.listIDChoose.indexOf(data.get(position));
                        if (pos != -1) {
                            AddCustomerFromContactActivity.listIDChoose.remove(pos);
                            data.get(holder.getAdapterPosition()).isSelectedGroup = false;
                        }

                        if (AddCustomerFromContactActivity.listIDChoose.size() == 0) {
                            AddCustomerFromContactActivity.tvAddMultiple.setText(mContext.getString(R.string.add));
                            AddCustomerFromContactActivity.tvAddMultiple.setTextColor(mContext.getResources().getColor(R.color.dark_blue_100));
                            AddCustomerFromContactActivity.tvAddMultiple.setBackgroundColor(mContext.getResources().getColor(R.color.white_background_100));
                        } else {
                            AddCustomerFromContactActivity.tvAddMultiple.setText(mContext.getString(R.string.add) + " "
                                    + AddCustomerFromContactActivity.listIDChoose.size() + "/" + mListFilter.size());
                        }
                    }

                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                    Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                }

            });

            if (data.get(position).isSelectedGroup) {
                holderItem.tv_add.setChecked(true);
            } else {
                holderItem.tv_add.setChecked(false);
            }

            holder.itemView.setOnClickListener(view -> {
                if (holderItem.tv_add.isChecked()) {
                    holderItem.tv_add.setChecked(false);
                } else {
                    holderItem.tv_add.setChecked(true);
                }
            });

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }


    }

//    private void hideView(ItemViewHolder holderItem, int position) {
//        holderItem.tv_add.setVisibility(View.GONE);
//        data.get(position).setAdded(true);
//        // update local isAdded
//        dbLocal.updateAddedbyContactLocalId(data.get(position).getId());
//    }

    public void findContact(String s) {

        data.clear();
        if (s.equals("")) {
            data.addAll(mListFilter);
            AddCustomerFromContactActivity.layoutAdd.setVisibility(View.VISIBLE);
        } else {
            AddCustomerFromContactActivity.layoutAdd.setVisibility(View.GONE);
            for (ContactLocal local : mListFilter) {
                if (local.getContactName().toLowerCase().contains(s)) {
                    data.add(local);
                    isSearching = true;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateList(ArrayList<ContactLocal> list) {
        mListFilter.clear();
        mListFilter.addAll(list);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView imgAvatar;
        CheckBox tv_add;

        ItemViewHolder(View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.item_img_avatar_contact);
            tv_add = itemView.findViewById(R.id.item_tv_add);
            tvName = itemView.findViewById(R.id.item_tv_name_contact);
            setIsRecyclable(false);
        }

        void clearView() {
            tv_add.setSelected(false);
        }
    }
}
