package csell.com.vn.csell.views.customer.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.models.CustomerRetroV1;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.customer.activity.ContactCustomerDetailActivity;

public class CustomerRecentAdapter extends RecyclerView.Adapter<CustomerRecentAdapter.DataViewHolder> {
    private Context mContext;
    private ArrayList<CustomerRetroV1> data;
    private BaseActivityTransition baseActivityTransition;
    private long mLastClickTime = 0;

    public CustomerRecentAdapter(Context context, ArrayList<CustomerRetroV1> list) {
        this.mContext = context;
        data = list;
        baseActivityTransition = new BaseActivityTransition(context);
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        holder.setValue(data.get(position));

        holder.itemView.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            Intent detail = new Intent(mContext, ContactCustomerDetailActivity.class);
            detail.putExtra(Constants.TEMP_CUSTOMER_KEY, data.get(position).getId());
            detail.putExtra(Constants.KEY_PASSINGDATA_CUSTOMER_OBJ, data.get(position));
            detail.putExtra(Constants.KEY_POSITION_TAB_SELECT_CUSTOMER_DETAIL, 0);
            baseActivityTransition.transitionTo(detail, Constants.RESULT_CODE_DELETE_EDIT);
        });

        holder.cardView.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            Intent detail = new Intent(mContext, ContactCustomerDetailActivity.class);
            detail.putExtra(Constants.TEMP_CUSTOMER_KEY, data.get(position).getId());
            detail.putExtra(Constants.KEY_PASSINGDATA_CUSTOMER_OBJ, data.get(position));
            detail.putExtra(Constants.KEY_POSITION_TAB_SELECT_CUSTOMER_DETAIL, 1);
            baseActivityTransition.transitionTo(detail, Constants.RESULT_CODE_DELETE_EDIT);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {

        final LinearLayout.LayoutParams params;
        TextView tvName;
        TextView tvAvatar, tvNeed;
        LinearLayout layout;
        private CardView cardView;

        DataViewHolder(View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tv_avatar_item);
            tvName = itemView.findViewById(R.id.tv_item_customer);
            tvNeed = itemView.findViewById(R.id.tv_need_item);
            layout = itemView.findViewById(R.id.item_layout_rela);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        public void setValue(CustomerRetroV1 customer) {

            if (!TextUtils.isEmpty(customer.getNote())) {
                tvNeed.setVisibility(View.VISIBLE);
                tvNeed.setText(customer.getNote());
            } else {
                tvNeed.setVisibility(View.GONE);
            }
            tvName.setText(customer.getName());
            tvAvatar.setText((customer.getName().charAt(0) + "").toUpperCase());
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
