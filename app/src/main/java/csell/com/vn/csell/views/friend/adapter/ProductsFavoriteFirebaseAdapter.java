package csell.com.vn.csell.views.friend.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.social.activity.DetailProductActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.views.product.adapter.viewholder.FavoriteProductViewHolder;


public class ProductsFavoriteFirebaseAdapter extends RecyclerView.Adapter<FavoriteProductViewHolder> {

    private Context mContext;
    private ArrayList<Product> data;
    private BaseActivityTransition baseActivityTransition;

    public ProductsFavoriteFirebaseAdapter(Context context, ArrayList<Product> list) {
        this.mContext = context;
        this.data = list;
        baseActivityTransition = new BaseActivityTransition(context);
    }

    @NonNull
    @Override
    public FavoriteProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_end_product, parent, false);
        return new FavoriteProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteProductViewHolder holder, int position) {

        try {
            holder.itemView.setOnClickListener(v -> {
                Intent detail = new Intent(mContext, DetailProductActivity.class);
                data.get(position).setFollowItem(true);
                detail.putExtra(Constants.TEMP_PRODUCT, data.get(position));
                detail.putExtra(Constants.TEMP_PRODUCT_KEY, data.get(position).getItemid());
                baseActivityTransition.transitionTo(detail, 0);
            });

            holder.setValue(mContext, data.get(position));

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}
