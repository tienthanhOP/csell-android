package csell.com.vn.csell.views.customer.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.GroupCustomerRetroV1;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.customer.activity.GroupCustomerDetailActivity;

////import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
////import com.firebase.ui.firestore.FirestoreRecyclerOptions;
//////import com.google.firebase.firestore.DocumentSnapshot;

public class GroupFirebaseAdapter extends RecyclerView.Adapter<GroupFirebaseAdapter.DataViewHolder> {
    private Context mContext;
    private ArrayList<GroupCustomerRetroV1> data;
    private BaseActivityTransition baseActivityTransition;
    private long mLastClickTime = 0;

    public GroupFirebaseAdapter(Context context, ArrayList<GroupCustomerRetroV1> list) {
        this.mContext = context;
        this.data = list;
        baseActivityTransition = new BaseActivityTransition(context);
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_customer, parent, false);
        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        try {
            holder.setValue(data.get(position));

            holder.itemView.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                Intent detail = new Intent(mContext, GroupCustomerDetailActivity.class);
                detail.putExtra(Constants.TEMP_GROUP_KEY, data.get(position).getId());
                detail.putExtra(Constants.KEY_PASSINGDATA_GROUP_OBJ, data.get(position));
                baseActivityTransition.transitionTo(detail, 0);
            });
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
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

    public class DataViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvAvatar;

        DataViewHolder(View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
        }

        public void setValue(GroupCustomerRetroV1 groupNew) {
            tvName.setText(groupNew.getName());
            tvAvatar.setText((groupNew.getName().charAt(0) + "").toUpperCase());
        }
    }
}