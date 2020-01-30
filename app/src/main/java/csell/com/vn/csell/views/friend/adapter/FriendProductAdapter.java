package csell.com.vn.csell.views.friend.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.crashlytics.android.Crashlytics;

import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FragmentTag;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.FriendsController;
import csell.com.vn.csell.models.Category;
import csell.com.vn.csell.models.ProductCountResponse;
import csell.com.vn.csell.sqlites.SQLCategories;
import csell.com.vn.csell.sqlites.SQLLanguage;
import csell.com.vn.csell.views.friend.activity.FriendDetailsActivity;
import csell.com.vn.csell.views.friend.fragment.FriendDetailsProductFragment;
import csell.com.vn.csell.views.friend.fragment.FriendEndProductFragment;
import csell.com.vn.csell.views.product.adapter.viewholder.GroupProductViewHolder;

/**
 * Created by User on 5/2/2018.
 */

public class FriendProductAdapter extends RecyclerView.Adapter<GroupProductViewHolder> {

    private static String imgGray = "";
    public boolean isSubCate;
    private Context context;
    private SQLCategories sqlCategories;
    private List<ProductCountResponse> data;
    private FileSave filePut;
    private FileSave fileGet;
    private Category category = null;
    private FriendsController.OnGetFriendGroupProductListener callBackFriendProduct;
    private FriendsController friendsController;
    private SQLLanguage sqlLanguage;

    public FriendProductAdapter(Context context, FriendsController friendsController, FriendsController.OnGetFriendGroupProductListener callBackFriendProduct, List<ProductCountResponse> data, boolean isSubCate) {
        this.context = context;
        this.sqlCategories = new SQLCategories(context);
        this.sqlLanguage = new SQLLanguage(context);
        this.isSubCate = isSubCate;
        this.data = data;
        this.filePut = new FileSave(context, Constants.PUT);
        this.fileGet = new FileSave(context, Constants.GET);
        this.callBackFriendProduct = callBackFriendProduct;
        this.friendsController = friendsController;
    }

    @NonNull
    @Override
    public GroupProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        GroupProductViewHolder viewHolder;
        switch (viewType) {
            case 0:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_tab_all, parent, false);
                viewHolder = new GroupProductViewHolder(itemView);
                break;
            case 1:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_tab_all_sub, parent, false);
                viewHolder = new GroupProductViewHolder(itemView);
                break;

            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_tab_all_sub, parent, false);
                viewHolder = new GroupProductViewHolder(itemView);
                break;
        }
        return viewHolder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GroupProductViewHolder holder, int position) {
        try {

            ProductCountResponse model = data.get(position);
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
                        GlideApp.with(context)
                                .load(cat.background)
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
                            FriendProductAdapter.imgGray = category.image;

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


                holder.itemView.setOnClickListener(v -> {
                    try {
                        isSubCate = true;
                        data.clear();
                        Utilities.lsSelectGroupProductFriend.add(model);
//                        MainActivity.groupProductAdapter.notifyDataSetChanged();
                        if (TextUtils.isEmpty(model.getCatid())) {
                            FriendDetailsActivity.Level = 3;
                            FriendDetailsActivity.MaxLevel = 3;
                            filePut.putProjectCurrentFriend(model.getProjectid());
                            loadDataProduct(FriendDetailsActivity.Level, fileGet.getSelectCateCurrentFriend());

                        } else {
                            category = sqlCategories.getCategoryById(model.getCatid());
                            filePut.putProjectCurrentFriend("");
                            FriendDetailsActivity.MaxLevel = category.max_level;
                            FriendDetailsActivity.Level = category.level + 1;
                            filePut.putSelectCateCurrentFriend(category.category_id);
                            if (category.level.equals(category.max_level))
                                category.image = imgGray;
                            if (category.category_id.startsWith(Utilities.LAND_PROJECT)) {

                                if (FriendDetailsActivity.Level <= FriendDetailsActivity.MaxLevel) {
                                    loadDataProduct(FriendDetailsActivity.Level, model.getCatid());
                                } else {
                                    FriendDetailsActivity.Level = category.level;
                                    FriendEndProductFragment endProductFragment = new FriendEndProductFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(Constants.KEY_PASSINGDATA_PRODUCT_COUNT, model);
                                    endProductFragment.setArguments(bundle);
                                    ((FriendDetailsActivity) context).getSupportFragmentManager().beginTransaction()
                                            .add(R.id.container_all_tab, endProductFragment, FragmentTag.TAG_END_PRODUCT)
                                            .addToBackStack(FragmentTag.TAG_END_PRODUCT)
                                            .commit();
                                }

                            } else {
                                filePut.putProjectCurrentFriend("");
                                if (FriendDetailsActivity.Level <= FriendDetailsActivity.MaxLevel) {
                                    loadDataProduct(FriendDetailsActivity.Level, model.getCatid());
                                } else {

                                    FriendDetailsActivity.Level = category.level;
                                    FriendEndProductFragment endProductFragment = new FriendEndProductFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(Constants.KEY_PASSINGDATA_PRODUCT_COUNT, model);
                                    endProductFragment.setArguments(bundle);
                                    ((FriendDetailsActivity) context).getSupportFragmentManager().beginTransaction()
                                            .add(R.id.container_all_tab, endProductFragment, FragmentTag.TAG_END_PRODUCT)
                                            .addToBackStack(FragmentTag.TAG_END_PRODUCT)
                                            .commit();
                                }
                            }
                        }


                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.d(getClass().getSimpleName(), e.getMessage());
                        Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + "" + e));
                    }

                });
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d(getClass().getSimpleName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + "" + e));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        try {
            if (!isSubCate) {
                return 0;
            } else {
                ProductCountResponse model = data.get(position);
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
        } catch (Exception ignored) {
            return 0;
        }
    }

    private void loadDataProduct(int level, String catId) {
        try {
            if (TextUtils.isEmpty(fileGet.getProjectCurrentFriend())) {
                friendsController.getFriendGroupProduct(fileGet.getFriendId(), level, catId, "", callBackFriendProduct);
            } else {
                friendsController.getFriendGroupProduct(fileGet.getFriendId(), level, catId, fileGet.getProjectCurrentFriend(), callBackFriendProduct);
            }
        } catch (Exception e) {

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

    public void refeshAdapter(List<ProductCountResponse> list) {
        data.clear();
        data = list;
        notifyDataSetChanged();
    }

    public void reloadData(List<ProductCountResponse> result) {
        data.clear();
        data.addAll(result);
        FriendDetailsProductFragment.dataLists.add(result);
        notifyDataSetChanged();
    }
}
