package csell.com.vn.csell.views.customer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.customer.activity.ContactCustomerDetailActivity;
import csell.com.vn.csell.views.customer.activity.GroupCustomerDetailActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.CustomerRetro;

/**
 * Created by chuc.nq on 4/13/2018.
 */

public class UserGroupFirebaseAdapter extends RecyclerView.Adapter<UserGroupFirebaseAdapter.DataViewHolder> {

    private Context mContext;
    private ArrayList<Object> data;
    private String custId = "";
    private BaseActivityTransition baseActivityTransition;

    public UserGroupFirebaseAdapter(Context context, ArrayList<Object> list) {
        this.mContext = context;
        this.data = list;
        baseActivityTransition = new BaseActivityTransition(context);
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_item_add_friend, parent, false);
        return new DataViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {

        if (position == 0)
            GroupCustomerDetailActivity.emailList.clear();

        GroupCustomerDetailActivity.tvNumberCustomers.setText(getItemCount() + " " + mContext.getString(R.string.text_count_people));
        holder.setValue(data.get(position));

        holder.itemView.setOnClickListener(v -> {
            if (data.get(position) instanceof LinkedTreeMap) {
                LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) data.get(position);
                custId = map.get("custid").toString();
            } else {
                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) data.get(position);
                custId = map.get("custid").toString();
            }

            CustomerRetro customerRetro = GroupCustomerDetailActivity.sqlCustomers.getCustomerById(custId);

            Intent detail = new Intent(mContext, ContactCustomerDetailActivity.class);
            detail.putExtra(Constants.TEMP_CUSTOMER_KEY, custId);
            detail.putExtra(Constants.KEY_PASSINGDATA_CUSTOMER_OBJ, customerRetro);
            detail.putExtra(Constants.KEY_POSITION_TAB_SELECT_CUSTOMER_DETAIL, 1);
            baseActivityTransition.transitionTo(detail, 0);
        });

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
        RelativeLayout layoutNeed;
        TextView tvNeed;

        DataViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            avata = itemView.findViewById(R.id.icon_avatar_text);
            checkBox = itemView.findViewById(R.id.checkAddPeople);
            checkBox.setVisibility(View.GONE);
            layoutNeed = itemView.findViewById(R.id.layout_need);
            tvNeed = itemView.findViewById(R.id.tv_need_item);
        }

        public void setValue(Object object) {

            try {
                String custId;
                if (object instanceof LinkedTreeMap) {
                    LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) object;
                    txtName.setText(map.get(EntityAPI.FIELD_DISPLAY_NAME).toString());

                    txtEmail.setVisibility(View.GONE);

                    avata.setText((map.get(EntityAPI.FIELD_DISPLAY_NAME).toString().charAt(0) + "").toUpperCase());
                    custId = map.get("custid").toString();
                } else {
                    LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) object;
                    txtName.setText(map.get(EntityAPI.FIELD_DISPLAY_NAME).toString());

                    txtEmail.setVisibility(View.GONE);

                    avata.setText((map.get(EntityAPI.FIELD_DISPLAY_NAME).toString().charAt(0) + "").toUpperCase());
                    custId = map.get("custid").toString();
                }

                CustomerRetro customerRetro = GroupCustomerDetailActivity.sqlCustomers.getCustomerById(custId);

                if (customerRetro.getEmail() != null) {
                    if (customerRetro.getEmail().size() != 0) {
                        GroupCustomerDetailActivity.emailList.add(customerRetro.getEmail().get(0));
                    }
                }

                if (customerRetro.getPhone() != null) {
                    if (customerRetro.getPhone().size() != 0) {
                        GroupCustomerDetailActivity.phoneList += customerRetro.getPhone().get(0) + ";";
                    }
                }

                if (!TextUtils.isEmpty(customerRetro.getNeed())) {
                    layoutNeed.setVisibility(View.VISIBLE);
                    tvNeed.setText(customerRetro.getNeed());
                } else {
                    layoutNeed.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }

        }
    }
}
