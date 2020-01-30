package csell.com.vn.csell.views.product.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//import com.google.firebase.firestore.FirebaseFirestore;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.models.GroupCustomerRetroV1;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.customer.activity.AddGroupCustomerActivity;
import csell.com.vn.csell.views.customer.activity.GroupCustomerDetailActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.interfaces.OnStartDragListener;
import csell.com.vn.csell.interfaces.ItemTouchHelperViewHolder;
import csell.com.vn.csell.models.GroupCustomerRetro;

public class ManageGroupViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
    public TextView txtName, txtEmail;
    public TextView avata;
    public Button btnEdit, btnDelete;
    public ImageView handleView;
    private BaseActivityTransition baseActivityTransition;

    public ManageGroupViewHolder(View itemView) {
        super(itemView);
        txtName = itemView.findViewById(R.id.txtName);
        txtEmail = itemView.findViewById(R.id.txtEmail);
        avata = itemView.findViewById(R.id.icon_avatar_text);
        btnEdit = itemView.findViewById(R.id.btn_edit);
        btnDelete = itemView.findViewById(R.id.btn_delete);
        handleView = itemView.findViewById(R.id.handle);
    }

    public void setValue(Context mContext, GroupCustomerRetroV1 groupNew, String groupId, FileSave fileSave, int position, OnStartDragListener mDragStartListener) {

        try {
            baseActivityTransition = new BaseActivityTransition(mContext);

            txtName.setText(groupNew.getName());
            avata.setText((groupNew.getName().charAt(0) + "").toUpperCase());

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, AddGroupCustomerActivity.class);
                intent.putExtra(Constants.TEMP_GROUP_KEY, groupId);
                intent.putExtra(Constants.KEY_PASSINGDATA_GROUP_OBJ, groupNew);
                baseActivityTransition.transitionTo(intent, 0);
            });

            itemView.setOnClickListener(v -> {
                mDragStartListener.onStartDrag(null, position);
                Intent detail = new Intent(mContext, GroupCustomerDetailActivity.class);
                detail.putExtra(Constants.TEMP_GROUP_KEY, groupId);
                detail.putExtra(Constants.KEY_PASSINGDATA_GROUP_OBJ, groupNew);
                baseActivityTransition.transitionTo(detail, 0);
            });

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        }

    }

    @Override
    public void onItemSelected(Context mContext) {
        itemView.setBackgroundColor(mContext.getResources().getColor(R.color.gray_line_alpha_10));
    }

    @Override
    public void onItemClear(Context mContext) {
        itemView.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
    }

}
