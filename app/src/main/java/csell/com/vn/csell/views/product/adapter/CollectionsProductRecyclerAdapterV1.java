package csell.com.vn.csell.views.product.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.TimeAgo;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.ProductResponseV1;
import csell.com.vn.csell.views.social.activity.DetailProductActivity;

public class CollectionsProductRecyclerAdapterV1 extends RecyclerView.Adapter {
    private static final int TYPE_BDS_CAR = 0;
    private static final int TYPE_SIM = 1;

    private Fragment fragment;
    private List<ProductResponseV1> productResponseV1List;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isMoreDataAvailable = true;

    public CollectionsProductRecyclerAdapterV1(Fragment fragment) {
        this.fragment = fragment;
    }

    public List<ProductResponseV1> getProductResponseV1List() {
        return productResponseV1List;
    }

    public void setProductResponseV1List(List<ProductResponseV1> productResponseV1List) {
        this.productResponseV1List = productResponseV1List;
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMore) {
        this.mOnLoadMoreListener = loadMore;
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case TYPE_BDS_CAR: {
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_bds_oto, parent, false);
                return new BdsCarVH(itemView);
            }
            case TYPE_SIM: {
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_sim, parent, false);
                return new SimVH(itemView);
            }
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof BdsCarVH) {
                BdsCarVH bdsCarVH = (BdsCarVH) holder;
                bdsCarVH.bindView(position);
            }

            if (holder instanceof SimVH) {
                SimVH simVH = (SimVH) holder;
                simVH.bindView(position);
            }
            setAnimation(holder.itemView);

            if (position >= getItemCount() - 1
                    && isMoreDataAvailable
                    && mOnLoadMoreListener != null) {
                mOnLoadMoreListener.onLoadMore();
            }

            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(fragment.getContext(), DetailProductActivity.class);
                intent.putExtra(Constants.TEMP_PRODUCT_KEY, productResponseV1List.get(position).getId());
                intent.putExtra(Constants.TEMP_PRODUCT, productResponseV1List.get(position));
                intent.putExtra(Constants.TEMP_POSITION, position);
                intent.putExtra(Constants.IS_MY_PRODUCT, true);
                fragment.startActivityForResult(intent, Constants.REQUEST_CODE_EDIT_PRODUCT);
            });
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public int getItemCount() {
        if (productResponseV1List != null) {
            return productResponseV1List.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (productResponseV1List.get(position).getCategories().get(0).getId().startsWith("sim")) {
            return TYPE_SIM;
        } else {
            return TYPE_BDS_CAR;
        }
    }

    private void setAnimation(View viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(fragment.getContext(), R.anim.fade_in);
        viewToAnimate.startAnimation(animation);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    class BdsCarVH extends RecyclerView.ViewHolder {
        private ImageView imgProduct;
        private TextView tvNameProduct;
        private TextView tvDescription;
        private TextView tvPrice;
        private TextView tvTimeAgo;

        public BdsCarVH(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvNameProduct = itemView.findViewById(R.id.tv_name_product);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvTimeAgo = itemView.findViewById(R.id.tv_access_time);
        }

        void bindView(int position) {
            List<String> listAvatar = productResponseV1List.get(position).getImages();
            if (listAvatar != null && listAvatar.size() > 0) {
                GlideApp.with(fragment).load(listAvatar.get(0))
                        .placeholder(R.drawable.noimage)
                        .error(R.drawable.noimage)
                        .into(imgProduct);
            } else {
                GlideApp.with(fragment).load(R.drawable.noimage)
                        .placeholder(R.drawable.noimage)
                        .error(R.drawable.noimage)
                        .into(imgProduct);
            }

            tvNameProduct.setText(productResponseV1List.get(position).getName());
            tvDescription.setText(productResponseV1List.get(position).getContent());
            if (productResponseV1List.get(position).getPrice() == 0) {
                tvPrice.setText("Liên hệ");
            } else {
                tvPrice.setText(Utilities.formatMoney(productResponseV1List.get(position).getPrice(), productResponseV1List.get(position).getCurrency()));
            }

            String timeAgo = TimeAgo.toRelative(
                    productResponseV1List.get(position).getCreatedAt() * 1000,
                    System.currentTimeMillis() / 1000 * 1000, 1);
            tvTimeAgo.setText(timeAgo);
        }
    }

    class SimVH extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvDes;
        private TextView tvPrice;
        private ImageView itemEndProductImg;
        private TextView tvTimeAgo;

        public SimVH(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.item_end_product_name);
            tvDes = itemView.findViewById(R.id.tv_description);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            itemEndProductImg = itemView.findViewById(R.id.item_end_product_img);
            tvTimeAgo = itemView.findViewById(R.id.tv_access_time);
        }

        void bindView(int po) {
            List<String> listAvatar = productResponseV1List.get(po).getImages();
            if (listAvatar != null && listAvatar.size() > 0) {
                GlideApp.with(fragment).load(listAvatar.get(0))
                        .placeholder(R.drawable.noimage)
                        .error(R.drawable.noimage)
                        .into(itemEndProductImg);
            } else {
                GlideApp.with(fragment).load(R.drawable.noimage)
                        .placeholder(R.drawable.noimage)
                        .error(R.drawable.noimage)
                        .into(itemEndProductImg);
            }

            tvName.setText(productResponseV1List.get(po).getName());
            tvDes.setText(productResponseV1List.get(po).getContent());
            if (productResponseV1List.get(po).getPrice() == 0) {
                tvPrice.setText("Liên hệ");
            } else {
                tvPrice.setText(Utilities.formatMoney(productResponseV1List.get(po).getPrice(), productResponseV1List.get(po).getCurrency()));
            }

            String timeAgo = TimeAgo.toRelative(
                    productResponseV1List.get(po).getCreatedAt() * 1000,
                    System.currentTimeMillis() / 1000 * 1000, 1);
            tvTimeAgo.setText(timeAgo);
        }
    }
}
