package csell.com.vn.csell.views.customer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.GroupCustomerRetroV1;
import csell.com.vn.csell.views.customer.activity.AddOrEditCustomerActivity;
import csell.com.vn.csell.views.customer.fragment.CustomersFragment;

/**
 * Created by User on 4/14/2018.
 */

public class PickedGroupAdapter extends RecyclerView.Adapter<PickedGroupAdapter.DataViewHolder> {

    private Context mContext;
    private ArrayList<GroupCustomerRetroV1> data;

    public PickedGroupAdapter(Context context, ArrayList<GroupCustomerRetroV1> list) {
        this.mContext = context;
        this.data = list;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item_add_group, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {

        try {
            holder.tvName.setText(data.get(position).getName());
            holder.tvAvatar.setText((data.get(position).getName().charAt(0) + "").toUpperCase());

            holder.imgRemove.setOnClickListener(view -> {

                int index = CustomersFragment.listGroup.indexOf(data.get(position));
                CustomersFragment.listGroup.get(index).isSelected = false;

                AddOrEditCustomerActivity.listIDGroupRemove.add(data.get(position));

                data.remove(position);

                if (data.size() == 0) {
                    AddOrEditCustomerActivity.rvPickedGroup.setVisibility(View.GONE);
                }
                AddOrEditCustomerActivity.mAdapterPickedGroup.notifyDataSetChanged();

            });
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class DataViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvAvatar;
        ImageButton imgRemove;

        DataViewHolder(View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.icon_avata);
            tvName = itemView.findViewById(R.id.txtName);
            imgRemove = itemView.findViewById(R.id.btn_remove_group);
        }
    }
}
