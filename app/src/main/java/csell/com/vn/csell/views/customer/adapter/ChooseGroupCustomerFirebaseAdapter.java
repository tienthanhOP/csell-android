package csell.com.vn.csell.views.customer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.models.GroupCustomerRetroV1;
import csell.com.vn.csell.views.customer.activity.AddOrEditCustomerActivity;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.GroupCustomerRetro;

/**
 * Created by User on 4/14/2018.
 */

public class ChooseGroupCustomerFirebaseAdapter extends RecyclerView.Adapter<ChooseGroupCustomerFirebaseAdapter.DataViewHolder> {

    Context mContext;
    ArrayList<GroupCustomerRetroV1> data;

    public ChooseGroupCustomerFirebaseAdapter(Context context, ArrayList<GroupCustomerRetroV1> list) {
        this.mContext = context;
        this.data = list;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_item_add_friend, parent, false);
        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {

        try {
            holder.txtName.setText(data.get(position).getName());
            holder.avata.setText((data.get(position).getName().charAt(0) + "").toUpperCase());

            holder.txtEmail.setVisibility(View.GONE);

//            holder.txtEmail.setText(data.get(position).GroupUser.size() + " thành viên");

            if(data.get(position).isSelected){
                holder.itemView.setEnabled(false);
                holder.layout.setBackgroundColor(mContext.getResources().getColor(R.color.gray_50));
            }else {
                holder.itemView.setEnabled(true);
                holder.layout.setBackgroundColor(mContext.getResources().getColor(R.color.white_100));
            }

            holder.itemView.setOnClickListener(view -> {
                data.get(position).isSelected = true;
                holder.itemView.setEnabled(false);
                holder.layout.setBackgroundColor(mContext.getResources().getColor(R.color.gray_50));
                AddOrEditCustomerActivity.listIDGroupChoose.add(data.get(position));
                AddOrEditCustomerActivity.rvPickedGroup.setVisibility(View.VISIBLE);
                AddOrEditCustomerActivity.mAdapterPickedGroup.notifyDataSetChanged();

                if (AddOrEditCustomerActivity.listIDGroupRemove.size() != 0){
                    AddOrEditCustomerActivity.listIDGroupRemove.remove(data.get(position));
                }

            });
        } catch (Exception e) {
           if(BuildConfig.DEBUG) Log.d(""+getClass().getName(),e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class DataViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName, txtEmail;
        TextView avata;
        CheckBox checkBox;
        public LinearLayout layout;

        DataViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            avata = itemView.findViewById(R.id.icon_avatar_text);
            checkBox = itemView.findViewById(R.id.checkAddPeople);
            checkBox.setVisibility(View.GONE);
            layout = itemView.findViewById(R.id.layout);
        }
    }
}
