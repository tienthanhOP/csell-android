package csell.com.vn.csell.views.friend.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.social.activity.DetailProductActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.TimeAgo;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.views.product.adapter.viewholder.EndProductViewHolder;


public class ProductAllProjectAdapter extends RecyclerView.Adapter<EndProductViewHolder> {

    private List<Product> data;
    private Context context;
    private FileSave fileGet;
    private BaseActivityTransition baseActivityTransition;

    public ProductAllProjectAdapter(Context context, List<Product> data) {
        this.data = data;
        this.context = context;
        fileGet = new FileSave(context, Constants.GET);
        baseActivityTransition = new BaseActivityTransition(context);
    }

    @NonNull
    @Override
    public EndProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_end_product, parent, false);
        return new EndProductViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull EndProductViewHolder holder, int position) {
        try {

            Product model = data.get(position);

            holder.txtProductName.setText(model.getTitle());
            holder.txtProductDescription.setText(model.getDescription());
            GlideApp.with(context)
                    .load(model.getImage().getPath())
                    .centerCrop()
                    .placeholder(R.drawable.noimage)
                    .into(holder.imgProductImage);
            if (model.getPrice() == null || model.getPrice() == 0) {
                holder.txtProductPrice.setText(context.getString(R.string.contact) + "");
            } else {
                holder.txtProductPrice.setText(Utilities.formatMoney(model.getPrice(), model.getCurrency()));
            }

            if (model.getDateShared() != null) {
                holder.txtProductDate.setText(TimeAgo.toRelative(Utilities.convertDateStringToMilisAllType(model.getDateShared()), System.currentTimeMillis(), 1));
            }

            holder.itemView.setOnClickListener(v -> {
                if (model.getUserInfo() != null)
                    if (model.getUserInfo().getUid() != null) {
                        if (model.getUserInfo().getUid().equals(fileGet.getUserId())) {
                            Intent detail = new Intent(context, DetailProductActivity.class);
                            detail.putExtra(Constants.TEMP_PRODUCT_KEY, model.getItemid());
                            detail.putExtra(Constants.IS_MY_PRODUCT, true);
                            baseActivityTransition.transitionTo(detail, 0);
                        } else {
                            Intent detail = new Intent(context, DetailProductActivity.class);
                            detail.putExtra(Constants.TEMP_PRODUCT, model);
                            detail.putExtra(Constants.TEMP_PRODUCT_KEY, model.getItemid());
                            baseActivityTransition.transitionTo(detail, 0);
                        }
                    } else {
                        Intent detail = new Intent(context, DetailProductActivity.class);
                        detail.putExtra(Constants.TEMP_PRODUCT, model);
                        detail.putExtra(Constants.TEMP_PRODUCT_KEY, model.getItemid());
                        baseActivityTransition.transitionTo(detail, 0);
                    }
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
}
