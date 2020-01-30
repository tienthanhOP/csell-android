package csell.com.vn.csell.views.friend.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.crashlytics.android.Crashlytics;

import java.util.Calendar;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.social.activity.DetailProductActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.TimeAgo;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.views.product.adapter.viewholder.EndProductViewHolder;

public class EndProductsAdapter extends RecyclerView.Adapter<EndProductViewHolder> {

    private Context mContext;
    private FileSave filePut;
    private List<Product> data;
    private boolean isSim;
    private BaseActivityTransition baseActivityTransition;
    private long mLastClickTime = 0;

    public EndProductsAdapter(Context mContext, List<Product> data, boolean isSim) {
        this.mContext = mContext;
        this.filePut = new FileSave(mContext, Constants.PUT);
        this.data = data;
        this.isSim = isSim;
        baseActivityTransition = new BaseActivityTransition(mContext);
    }

    @NonNull
    @Override
    public EndProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (isSim) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_end_sim, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_end_product, parent, false);
        }
        return new EndProductViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull EndProductViewHolder holder, int position) {
        try {

            Product model = data.get(position);

            if (model != null) {
                holder.txtProductName.setText(model.getTitle());
                holder.txtProductDescription.setText(model.getDescription());

                String url = model.getImage().getPath() + "";
                if (model.getCatid().startsWith(Constants.KEY_CLASS_CAR)) {
                    GlideApp.with(mContext)
                            .load(url)
                            .placeholder(R.drawable.ic_car)
                            .error(R.drawable.ic_car)
                            .into(holder.imgProductImage);
                } else if (model.getCatid().startsWith(Constants.KEY_CLASS_LAND)) {
                    GlideApp.with(mContext)
                            .load(url)
                            .placeholder(R.drawable.ic_bds)
                            .error(R.drawable.ic_bds)
                            .into(holder.imgProductImage);
                } else if (model.getCatid().startsWith(Constants.KEY_CLASS_SIM)) {
                    GlideApp.with(mContext)
                            .load(url)
                            .error(R.drawable.bg_3)
                            .into(holder.imgProductImage);
                } else {
                    GlideApp.with(mContext)
                            .load(url)
                            .error(R.drawable.noimage)
                            .into(holder.imgProductImage);
                }

                if (model.getCatid().startsWith(Utilities.SIM_LIST_MONTH)) {
                    holder.layout_price_sim.setVisibility(View.GONE);
                } else {
                    holder.layout_price_sim.setVisibility(View.VISIBLE);
                }

                if (model.getPrice() == null || model.getPrice() == 0) {
                    holder.txtProductPrice.setText(mContext.getString(R.string.contact) + "");
                } else {
                    holder.txtProductPrice.setText(Utilities.formatMoney(model.getPrice(), model.getCurrency()));
                }
                if (model.getDateCreated() != null) {
                    holder.txtProductDate.setText(TimeAgo.toRelative(Utilities.convertDateStringToMilisAllType(model.getDateCreated())
                            , Calendar.getInstance().getTime().getTime(), 1));
                } else {
                    if (model.getDateShared() != null) {
                        holder.txtProductDate.setText(TimeAgo.toRelative(Utilities.convertDateStringToMilisAllType(model.getDateShared())
                                , Calendar.getInstance().getTime().getTime(), 1));
                    }
                }

                holder.itemView.setOnClickListener(v -> {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    filePut.putProductIdCurrentSelect(model.getItemid());
                    filePut.putProductNameCurrentSelect(model.getTitle());
                    if (mContext instanceof MainActivity) {
                        Intent detail = new Intent(mContext, DetailProductActivity.class);
                        detail.putExtra(Constants.TEMP_PRODUCT_KEY, model.getItemid());
                        detail.putExtra(Constants.TEMP_POSITION, holder.getAdapterPosition());
                        detail.putExtra(Constants.IS_MY_PRODUCT, true);
                        detail.putExtra(Constants.TEMP_PRODUCT, model);
                        baseActivityTransition.transitionTo(detail, Constants.RESULT_CODE_REMOVE_PRODUCT);
                    } else {
                        Intent detail = new Intent(mContext, DetailProductActivity.class);
                        detail.putExtra(Constants.TEMP_PRODUCT_KEY, model.getItemid());
                        detail.putExtra(Constants.TEMP_PRODUCT, model);
                        baseActivityTransition.transitionTo(detail, 0);
                    }
                });
            }
            setAnimation(holder.itemView);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void setAnimation(View viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.left_in);
        viewToAnimate.startAnimation(animation);
    }
}
