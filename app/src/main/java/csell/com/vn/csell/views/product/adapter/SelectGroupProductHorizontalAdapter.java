package csell.com.vn.csell.views.product.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.models.ItemCategory;
import csell.com.vn.csell.sqlites.SQLLanguage;


public class SelectGroupProductHorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ItemCategory> itemCategoryList;
    private int selected = 0;
    private FileSave fileGet;
    private FileSave filePut;
    private SQLLanguage sqlLanguage;
    private OnClickParentListener onClickParentListener;

    public SelectGroupProductHorizontalAdapter(Context context, List<ItemCategory> itemCategoryList, OnClickParentListener onClickParentListener) {
        this.context = context;
        this.itemCategoryList = itemCategoryList;
        this.onClickParentListener = onClickParentListener;
        sqlLanguage = new SQLLanguage(context);
        fileGet = new FileSave(context, Constants.GET);
        filePut = new FileSave(context, Constants.PUT);
    }

    public void addItemCategory(ItemCategory itemCategory) {
        itemCategoryList.add(itemCategory);
    }

    public List<ItemCategory> getItemCategoryList() {
        return itemCategoryList;
    }

    public void setItemCategoryList(List<ItemCategory> itemCategoryList) {
        this.itemCategoryList = itemCategoryList;
    }

    public ItemCategory getLastItemCategoryList() {
        return itemCategoryList.get(itemCategoryList.size() - 1);
    }

    public void removeItemCategorySaveLast(ItemCategory itemCategory) {
        int indexLast = itemCategoryList.indexOf(itemCategory);
        itemCategoryList = itemCategoryList.subList(0, indexLast + 1);
    }

    public void removeItemCategory(ItemCategory itemCategory) {
        int indexLast = itemCategoryList.indexOf(itemCategory);
        itemCategoryList = itemCategoryList.subList(0, indexLast);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gridview_category, parent, false);
        return new ViewHolderItem(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            ViewHolderItem holderItem = (ViewHolderItem) holder;
            holderItem.txtCategoryName.setText(itemCategoryList.get(position).getCategoryName());
//            if (!TextUtils.isEmpty(model.getCatid())) {
//
//                try {
//                    String idCateName = model.getCatid();
//
//                    String nameCate = "";
//                    if (!TextUtils.isEmpty(idCateName))
//                        nameCate = sqlLanguage.getDisplayNameByCatId(idCateName, fileGet.getLanguage());
//
//                    if (!TextUtils.isEmpty(nameCate))
//                        holderItem.txtCategoryName.setText(nameCate);
//                    else
//                        holderItem.txtCategoryName.setText(model.getCatName());
//
//                } catch (Exception e) {
//                    holderItem.txtCategoryName.setText(model.getCatName());
//                    filePut.putPrefixCateTemp(model.getCatName());
//                }
//
//            } else {
//                holderItem.txtCategoryName.setText(model.getProjectName());
//                filePut.putPrefixCateTemp(model.getProjectName());
//            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public int getItemCount() {
        return itemCategoryList.size();
    }

    public interface OnClickParentListener {
        void clickParent(ItemCategory itemCategory);
    }

    private class ViewHolderItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtCategoryName;

        public ViewHolderItem(View v) {
            super(v);
            txtCategoryName = v.findViewById(R.id.txtCategoryName);
            v.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            onClickParentListener.clickParent(itemCategoryList.get(getAdapterPosition()));
        }
    }
}
