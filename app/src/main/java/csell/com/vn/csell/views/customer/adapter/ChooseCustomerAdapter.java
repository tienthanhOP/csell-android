package csell.com.vn.csell.views.customer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.CustomerRetro;
import csell.com.vn.csell.views.customer.activity.AddGroupCustomerActivity;

public class ChooseCustomerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private ArrayList<CustomerRetro> data;
    private ArrayList<CustomerRetro> mListFilter;

    public ChooseCustomerAdapter(Context context, ArrayList<CustomerRetro> data) {
        this.mContext = context;
        this.data = data;
        mListFilter = new ArrayList<>();
        mListFilter.addAll(data);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rc_item_add_friend, parent, false);
        return new ItemFriendHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ItemFriendHolder viewHolder = (ItemFriendHolder) holder;

        viewHolder.txtName.setText(data.get(position).getName());
        viewHolder.txtEmail.setVisibility(View.GONE);
//        if (data.get(position).getEmail() != null) {
//            viewHolder.txtEmail.setText(data.get(position).getEmail().get(0));
//        } else {
//            viewHolder.txtEmail.setText(data.get(position).getPhone().get(0));
//        }

        viewHolder.avata.setText((data.get(position).getName().charAt(0) + "").toUpperCase());

        viewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            try {
                if (isChecked) {

                    if(AddGroupCustomerActivity.listIDChoose.size() != 0){
                        int index =  AddGroupCustomerActivity.listIDChoose.indexOf(data.get(position));
                        if (index != -1) return;
                    }

//                    AddGroupCustomerActivity.listIDChoose.add(data.get(position));
                    if (AddGroupCustomerActivity.listIDRemove.size() != 0){
                        int pos = AddGroupCustomerActivity.listIDRemove.indexOf(data.get(position));
                        if (pos != -1){
                            AddGroupCustomerActivity.listIDRemove.remove(pos);
                        }
                    }
                    AddGroupCustomerActivity.rvChoose.setVisibility(View.VISIBLE);
                    AddGroupCustomerActivity.mAdapterPicked.notifyDataSetChanged();

                    data.get(position).isSelectedGroup = true;
                } else {
//                    AddGroupCustomerActivity.listIDRemove.add(data.get(position));
                    AddGroupCustomerActivity.listIDChoose.remove(data.get(position));
                    AddGroupCustomerActivity.mAdapterPicked.notifyDataSetChanged();
                    data.get(position).isSelectedGroup = false;

                    if (AddGroupCustomerActivity.listIDChoose.size() == 0){
                        AddGroupCustomerActivity.rvChoose.setVisibility(View.GONE);
                    }else {
                        AddGroupCustomerActivity.rvChoose.setVisibility(View.VISIBLE);
                    }
                }

            } catch (Exception e) {
               if(BuildConfig.DEBUG) Log.d(""+getClass().getName(),e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }

        });

        if(data.get(position).isSelectedGroup){
            viewHolder.checkBox.setChecked(true);
        }else {
            viewHolder.checkBox.setChecked(false);
        }

        viewHolder.itemView.setOnClickListener(view -> {
            if (viewHolder.checkBox.isChecked()){
                viewHolder.checkBox.setChecked(false);
            }else {
                viewHolder.checkBox.setChecked(true);
            }
        });
    }



    public void updateList(ArrayList<CustomerRetro> list){
        mListFilter.clear();
        mListFilter.addAll(list);
    }

    public void findContact(String s){
        data.clear();
        if (s.equals("")){data.addAll(mListFilter);}
        else {
            for (CustomerRetro customer: mListFilter){
                if (customer.getName().toLowerCase().contains(s)){
                    data.add(customer);
                }
            }
        }
        notifyDataSetChanged();
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

    class ItemFriendHolder extends RecyclerView.ViewHolder{
        public TextView txtName, txtEmail;
        TextView avata;
        CheckBox checkBox;

        ItemFriendHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            avata = itemView.findViewById(R.id.icon_avatar_text);
            checkBox = itemView.findViewById(R.id.checkAddPeople);
        }

    }

}
