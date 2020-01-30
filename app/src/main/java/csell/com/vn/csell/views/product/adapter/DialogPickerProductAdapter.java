package csell.com.vn.csell.views.product.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.models.Product;

/**
 * Created by cuong.nv on 2/23/2018.
 */

public class DialogPickerProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<Product> data;
    private ArrayList<Product> mListFilter;
    private final OnItemClickListener listener;

    public DialogPickerProductAdapter(Context mContext, ArrayList<Product> data, OnItemClickListener listener) {
        this.mContext = mContext;
        this.data = data;
        this.listener = listener;
        this.mListFilter = new ArrayList<>();
        mListFilter.addAll(data);
    }

    // tự tạo event item click cho recyclerview
    public interface OnItemClickListener {
        void onItemClick(Product item);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_picker_product, parent, false);

        return new ViewHolderItem(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderItem holderItem = (ViewHolderItem) holder;

        holderItem.bind(data.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {

        TextView productName;
        TextView productDescription;
        ImageView productImage;

        public ViewHolderItem(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.item_dialog_productImage);
            productName = itemView.findViewById(R.id.item_dialog_productName);
            productDescription = itemView.findViewById(R.id.item_dialog_productDescription);
        }

        //thay kieu String thanh object khi release
        void bind(final Product item, final OnItemClickListener listener) {

            try {
                productName.setText(item.getTitle());
                productDescription.setText(item.getDescription());
                itemView.setOnClickListener(v -> listener.onItemClick(item));

                if (item.getCatid().startsWith(Constants.KEY_CLASS_CAR)){
                    GlideApp.with(mContext)
                            .load(R.drawable.icon_car)
                            .placeholder(R.drawable.noimage)
                            .error(R.drawable.noimage)
                            .into(productImage);
                }else if (item.getCatid().startsWith(Constants.KEY_CLASS_LAND)){
                    GlideApp.with(mContext)
                            .load(R.drawable.icon_bds)
                            .placeholder(R.drawable.noimage)
                            .error(R.drawable.noimage)
                            .into(productImage);
                }else if (item.getCatid().startsWith(Constants.KEY_CLASS_SIM)){
                    GlideApp.with(mContext)
                            .load(R.drawable.icon_sim)
                            .placeholder(R.drawable.noimage)
                            .error(R.drawable.noimage)
                            .into(productImage);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
               if(BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
            }

        }
    }

    public void findProduct(String s) {

        data.clear();
        if (s.equals("")) {
            data.addAll(mListFilter);
        } else {
            for (Product local : mListFilter) {
                if (local.getTitle().toLowerCase().contains(s)) {
                    data.add(local);
                }
            }
        }
        notifyDataSetChanged();

    }

    public void updateList(ArrayList<Product> list) {
        mListFilter.clear();
        mListFilter.addAll(list);
    }

}
