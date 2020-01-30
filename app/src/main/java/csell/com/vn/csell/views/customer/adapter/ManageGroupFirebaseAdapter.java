package csell.com.vn.csell.views.customer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

//import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.interfaces.OnStartDragListener;
import csell.com.vn.csell.interfaces.ItemTouchHelperAdapter;
import csell.com.vn.csell.models.GroupCustomerRetro;
import csell.com.vn.csell.models.GroupCustomerRetroV1;
import csell.com.vn.csell.views.product.adapter.viewholder.ManageGroupViewHolder;

/**
 * Created by chuc.nq on 4/13/2018.
 */

public class ManageGroupFirebaseAdapter extends RecyclerView.Adapter<ManageGroupViewHolder> implements ItemTouchHelperAdapter {

    Context mContext;
    //    FirebaseFirestore mFireStoreRef;
    private FileSave fileSave;
    ArrayList<GroupCustomerRetroV1> data;
    private final OnStartDragListener mDragStartListener;

    public ManageGroupFirebaseAdapter(Context context, ArrayList<GroupCustomerRetroV1> list, OnStartDragListener dragStartListener) {
        this.mContext = context;
        //mFireStoreRef = FirebaseDBUtil.getFirestore();
        fileSave = new FileSave(context, Constants.GET);
        data = list;
        mDragStartListener = dragStartListener;
    }

    @NonNull
    @Override
    public ManageGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_group, parent, false);
        return new ManageGroupViewHolder(itemView);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ManageGroupViewHolder holder, int position) {
        holder.setValue(mContext, data.get(position), data.get(position).getId(), fileSave, position, mDragStartListener);

        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener((v, event) -> {
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                mDragStartListener.onStartDrag(holder, position);
            }
            return false;
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
        return 0;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

        try {
            notifyItemMoved(fromPosition, toPosition);
//            Collections.swap(ManageGroupCustomerActivity.list, fromPosition, toPosition);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        }

        return true;
    }


}
