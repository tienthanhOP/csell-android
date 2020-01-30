package csell.com.vn.csell.views.product.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.ProductController;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FragmentTag;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.views.product.fragment.EndProductFragment;
import csell.com.vn.csell.views.product.fragment.ProductCollectionsFragment;
import csell.com.vn.csell.interfaces.RetryConnect;
import csell.com.vn.csell.models.Category;
import csell.com.vn.csell.models.ProductCountResponse;
import csell.com.vn.csell.mycustoms.DisconnectDialog;
import csell.com.vn.csell.sqlites.SQLCacheProduct;
import csell.com.vn.csell.sqlites.SQLCategories;
import csell.com.vn.csell.sqlites.SQLLanguage;
import csell.com.vn.csell.views.product.adapter.viewholder.GroupProductViewHolder;
import csell.com.vn.csell.views.product.adapter.viewholder.HeaderProductViewHolder;

/**
 * Created by cuong.nv on 3/15/2018.
 */

public class CollectionsProductRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RetryConnect {

    private Context context;
    private SQLCategories sqlCategories;
    public boolean isSubCate;
    private List<ProductCountResponse> data;
    private FileSave filePut;
    private FileSave fileGet;
    private Category category = null;
    private static String imgGray = "";
    private SQLLanguage sqlLanguage;
    private SQLCacheProduct sqlCacheProduct;
    private static final int TYPE_HEADER = -1;
    private ProductController productController;
    private ProductController.OnGetGroupProductsListener callBackProduct;
    private long mLastClickTime = 0;
    private ProgressBar progressBar;
    private int lastPosition = -1;

    public CollectionsProductRecyclerAdapter(Context context, List<ProductCountResponse> data, boolean isSubCate,
                                             ProgressBar progressBar,
                                             ProductController productController, ProductController.OnGetGroupProductsListener callBackProduct) {
        this.context = context;
        this.sqlCategories = new SQLCategories(context);
        this.sqlLanguage = new SQLLanguage(context);
        this.sqlCacheProduct = new SQLCacheProduct(context);
        this.isSubCate = isSubCate;
        this.data = data;
        this.filePut = new FileSave(context, Constants.PUT);
        this.fileGet = new FileSave(context, Constants.GET);
        this.productController = productController;
        this.callBackProduct = callBackProduct;
        this.progressBar = progressBar;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_HEADER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_item, parent, false);
            return new HeaderProductViewHolder(itemView);
        } else {
            switch (viewType) {
                case 0:
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_tab_all, parent, false);
                    break;
                case 1:
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_tab_all_sub, parent, false);
                    break;
                default:
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_tab_all_sub, parent, false);
                    break;
            }
            return new GroupProductViewHolder(itemView);
        }
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderAll, int position) {
        try {
            if (holderAll instanceof HeaderProductViewHolder) {
                HeaderProductViewHolder header = (HeaderProductViewHolder) holderAll;
                String catName = Utilities.lsSelectGroupProduct.get(Utilities.lsSelectGroupProduct.size() - 1).getCatName();
                if (TextUtils.isEmpty(catName)) {
                    catName = Utilities.lsSelectGroupProduct.get(Utilities.lsSelectGroupProduct.size() - 1).getProjectName();
                }
                header.txtTitle.setText(catName);
            } else if (holderAll instanceof GroupProductViewHolder) {
                GroupProductViewHolder holder = (GroupProductViewHolder) holderAll;

                ProductCountResponse model = data.get(position - 1);
                if (model != null) {
                    Category cat = null;
                    if (!TextUtils.isEmpty(model.getCatid())) {
                        cat = sqlCategories.getCategoryById(model.getCatid());

                        try {
                            String idCateName = model.getCatid();

                            String nameCate = "";
                            if (!TextUtils.isEmpty(idCateName))
                                nameCate = sqlLanguage.getDisplayNameByCatId(idCateName, fileGet.getLanguage());

                            if (!TextUtils.isEmpty(nameCate))
                                holder.txtTitle.setText(nameCate);
                            else
                                holder.txtTitle.setText(model.getCatName());

                        } catch (Exception e) {
                            holder.txtTitle.setText(model.getCatName());
                        }

                    } else {
                        holder.txtTitle.setText(model.getProjectName());
                    }


                    if (cat == null) {
                        Category cate = sqlCategories.getCategoryById(Utilities.LAND_PROJECT);
                        String img = cate.image;
                        GlideApp.with(context).load(img)
                                .error(R.drawable.noimage).into(holder.iconCategory);

                        try {
                            if (holder.layout != null)
                                GlideApp.with(context)
                                        .load(context.getResources().getString(R.string.default_sub_project))
                                        .into(new SimpleTarget<Drawable>() {
                                            @Override
                                            public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
                                                holder.layout.setBackground(resource);
                                            }
                                        });
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                        }
                    } else {

                        String image = cat.image;
                        if (cat.category_id.startsWith(Utilities.SIM)) {

                            GlideApp.with(context).load(image)
                                    .error(R.drawable.sim_default)
                                    .into(holder.iconCategory);
                            showBackground(holder, cat);

                        } else if (cat.category_id.startsWith(Constants.KEY_CLASS_CAR)) {

                            if (cat.level == 3)
                                CollectionsProductRecyclerAdapter.imgGray = category.image;

                            if (cat.level >= cat.max_level) {
                                ColorMatrix matrix = new ColorMatrix();
                                matrix.setSaturation(0);
                                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                                holder.iconCategory.setColorFilter(filter);
                                if (TextUtils.isEmpty(imgGray)) {
                                    GlideApp.with(context)
                                            .load(cat.image)
                                            .error(R.drawable.noimage)
                                            .into(holder.iconCategory);
                                } else {
                                    GlideApp.with(context)
                                            .load(imgGray)
                                            .error(R.drawable.noimage)
                                            .into(holder.iconCategory);
                                }

                            } else {
                                holder.iconCategory.setColorFilter(null);
                                GlideApp.with(context).load(cat.image)
                                        .error(R.drawable.ic_car)
                                        .into(holder.iconCategory);
                            }
                            showBackground(holder, cat);

                        } else if (cat.category_id.startsWith(Constants.KEY_CLASS_LAND)) {

                            GlideApp.with(context).load(image)
                                    .error(R.drawable.ic_land)
                                    .into(holder.iconCategory);
                            showBackground(holder, cat);

                        } else {
                            GlideApp.with(context).load(image)
                                    .error(R.drawable.noimage)
                                    .into(holder.iconCategory);
                        }
                    }


                    holder.txtNumberAll.setText(model.getTotal() + " " + context.getString(R.string.product));

                    try {
                        if (position > lastPosition) {
                            setAnimation(holder.itemView);
                            lastPosition = position;
                        }
                    } catch (Exception ignored) {

                    }

                    holder.itemView.setOnClickListener(v -> {
                        try {
                            lastPosition = -1;
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            progressBar.setVisibility(View.VISIBLE);

                            isSubCate = true;
                            data.clear();
                            Utilities.lsSelectGroupProduct.add(model);
//                            MainActivity.groupProductAdapter.notifyDataSetChanged();
                            if (TextUtils.isEmpty(model.getCatid())) {
                                Utilities.IS_SELECT_PROJECT = true;
                                MainActivity.Level = 3;
                                MainActivity.MaxLevel = 3;
                                filePut.putProjectCurrent(model.getProjectid());
                                filePut.putSelectProjectCurrent(model.getProjectid());
                                loadDataProduct(MainActivity.Level, Utilities.LAND_PROJECT);
                            } else {
                                if (model.getCatid().toLowerCase().equals(Utilities.LAND_PROJECT)) {
                                    filePut.putProjectCurrent("");
                                    filePut.putSelectProjectCurrent("");
                                }
                                category = sqlCategories.getCategoryById(model.getCatid());
                                MainActivity.MaxLevel = category.max_level;
                                Utilities.IS_SELECT_PROJECT = false;
                                MainActivity.Level = category.level + 1;
                                filePut.putSelectCateCurrent(category.category_id);//land_project_shophouse
                                if (category.level.equals(category.max_level))
                                    category.image = imgGray;
                                if (category.category_id.startsWith(Utilities.LAND_PROJECT)) {

                                    if (MainActivity.Level <= MainActivity.MaxLevel) {
                                        loadDataProduct(MainActivity.Level, model.getCatid());
                                    } else {
                                        MainActivity.Level = category.level;
                                        EndProductFragment endProductFragment = new EndProductFragment();
                                        MainActivity.fab.show();
                                        MainActivity.fab.setOnClickListener(endProductFragment.onClickFloatButton.getInstance(context));
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable(Constants.KEY_PASSINGDATA_PRODUCT_COUNT, model);
                                        endProductFragment.setArguments(bundle);
                                        ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                                                .add(R.id.container_all_tab, endProductFragment, FragmentTag.TAG_END_PRODUCT)
                                                .addToBackStack(FragmentTag.TAG_END_PRODUCT)
                                                .commit();
                                        progressBar.setVisibility(View.GONE);
                                    }

                                } else {
                                    filePut.putProjectCurrent("");
                                    if (MainActivity.Level <= MainActivity.MaxLevel) {
                                        loadDataProduct(MainActivity.Level, model.getCatid());
                                    } else {
                                        MainActivity.Level = category.level;
                                        EndProductFragment endProductFragment = new EndProductFragment();
                                        MainActivity.fab.show();
                                        MainActivity.fab.setOnClickListener(endProductFragment.onClickFloatButton.getInstance(context));
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable(Constants.KEY_PASSINGDATA_PRODUCT_COUNT, model);
                                        endProductFragment.setArguments(bundle);
                                        ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                                                .add(R.id.container_all_tab, endProductFragment, FragmentTag.TAG_END_PRODUCT)
                                                .addToBackStack(FragmentTag.TAG_END_PRODUCT)
                                                .commit();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            }


                        } catch (Exception e) {
                            if (BuildConfig.DEBUG)
                                Log.d(getClass().getSimpleName(), e.getMessage());
                            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + "" + e));
                        }

                    });
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d(getClass().getSimpleName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + "" + e));
        }
    }

    private void setAnimation(View viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.left_in);
        viewToAnimate.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        else {
            if (!isSubCate) {
                return 0;
            } else {
                ProductCountResponse model = data.get(position - 1);
                if (TextUtils.isEmpty(model.getCatid())) {
                    return 1;
                } else {
                    if (model.getCatid().startsWith(Utilities.LAND)) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            }
        }
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private void loadDataProduct(int level, String catId) {
        try {
            if (Utilities.isNetworkConnected(MainActivity.mainContext)) {
                if (Utilities.getInetAddressByName() != null) {
                    loadData(level, catId);
                } else {
                    loadDataCache(catId);
                }
            } else {
                loadDataCache(catId);
            }
        } catch (Exception e) {
            Crashlytics.logException(new Exception("LOG_" + e));
        }
    }

    private void loadData(int level, String catId) {
        try {
            if (level == 1) {
                productController.getGroupProducts(3, level, catId, "", true, callBackProduct);
            } else {
                if (TextUtils.isEmpty(fileGet.getProjectCurrent())) {
                    productController.getGroupProducts(3, level, catId, "", true, callBackProduct);
                } else {
                    productController.getGroupProducts(3, level, catId, fileGet.getProjectCurrent(), true, callBackProduct);
                }
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    public void reloadData(List<ProductCountResponse> data, String catId) {
        try {
            if (data.size() > 0) {
                this.data.addAll(data);
//                ProductCollectionsFragment.dataLists.add(data);
                notifyDataSetChanged();
                if (Utilities.IS_SELECT_PROJECT) {
                    sqlCacheProduct.insertCache(fileGet.getProjectCurrent(),
                            new Gson().toJson(data));
                } else {
                    sqlCacheProduct.insertCache(catId,
                            new Gson().toJson(data));
                }
            }
        } catch (Exception ignored) {

        }
    }

    private void loadDataCache(String catId) {
        List<ProductCountResponse> temp;
        if (Utilities.IS_SELECT_PROJECT) {
            temp = sqlCacheProduct.getDataByCat(fileGet.getProjectCurrent());

        } else {
            temp = sqlCacheProduct.getDataByCat(catId);

        }
        data.addAll(temp);
//        ProductCollectionsFragment.dataLists.add(temp);
        notifyDataSetChanged();
        if (temp.size() == 0) {
            DisconnectDialog disconnectDialog = new DisconnectDialog(context, this);
            disconnectDialog.setCancelable(false);
            disconnectDialog.show();
        }
    }

    private void showBackground(GroupProductViewHolder holder, Category cat) {
        if (holder.layout != null) {
            if (!TextUtils.isEmpty(cat.background)) {
                if (!cat.background.equals("empty")) {
                    GlideApp.with(context)
                            .load(cat.background)
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
                                    holder.layout.setBackground(resource);
                                }
                            });

                } else {
                    if (cat.category_id.startsWith(Constants.KEY_CLASS_LAND)) {
                        Category catTemp = sqlCategories.getCategoryById(Constants.KEY_CLASS_LAND);
                        if (!catTemp.background.equals("empty"))
                            GlideApp.with(context)
                                    .load(catTemp.background)
                                    .into(new SimpleTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
                                            holder.layout.setBackground(resource);
                                        }
                                    });

                    }
                }
            }
        }
    }

    public void backData() {
        lastPosition = -1;
//        if (ProductCollectionsFragment.dataLists.size() == 1) {
//            isSubCate = false;
//            data.clear();
//            data.addAll(ProductCollectionsFragment.dataLists.get(0));
//            notifyDataSetChanged();
//        } else {
//            isSubCate = true;
//            ProductCollectionsFragment.dataLists.remove(ProductCollectionsFragment.dataLists.size() - 1);
//            data.clear();
//            data.addAll(ProductCollectionsFragment.dataLists.get(ProductCollectionsFragment.dataLists.size() - 1));
//            if (ProductCollectionsFragment.dataLists.size() == 1) {
//                isSubCate = false;
//            }
//            notifyDataSetChanged();
//        }
    }

    public void updateCollection(int position, int sizeCollection, boolean sub) {
//        isSubCate = sub;
//        for (int i = position + 1; i < sizeCollection; i++) {
//            ProductCollectionsFragment.dataLists.remove(ProductCollectionsFragment.dataLists.size() - 1);
//        }
//        data.clear();
//        data.addAll(ProductCollectionsFragment.dataLists.get(ProductCollectionsFragment.dataLists.size() - 1));
//        notifyDataSetChanged();
    }

    @Override
    public void onRetryConnect() {
        try {
            loadData(MainActivity.Level, fileGet.getSelectCateCurrent());
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                Log.d(getClass().getSimpleName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + "" + e));
        }
    }

    @Override
    public void onBackRetryConnect() {

    }
}
