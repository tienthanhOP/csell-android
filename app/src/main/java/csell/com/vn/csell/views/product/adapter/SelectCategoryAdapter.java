package csell.com.vn.csell.views.product.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.models.Category;
import csell.com.vn.csell.models.ItemCategory;
import csell.com.vn.csell.models.Project;
import csell.com.vn.csell.sqlites.SQLCategories;
import csell.com.vn.csell.sqlites.SQLLanguage;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.project.activity.ProjectDetailsActivity;

/**
 * Created by cuong.nv on 3/12/2018.
 */

public class SelectCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static String imgGray = "";
    public boolean isChild = true;
    private Context context;
    private List<ItemCategory> itemCategoryList;
    private SQLCategories sqlCategories;
    private SQLLanguage sqlLanguage;
    private FileSave fileGet;
    private BaseActivityTransition baseActivityTransition;
    private OnClickCategoryListener onClickCategoryListener;

    public SelectCategoryAdapter(Context context, List<ItemCategory> data, OnClickCategoryListener onClickCategoryListener) {
        this.context = context;
        this.itemCategoryList = data;
        sqlCategories = new SQLCategories(context);
        this.onClickCategoryListener = onClickCategoryListener;
        sqlLanguage = new SQLLanguage(context);
        fileGet = new FileSave(context, Constants.GET);
        baseActivityTransition = new BaseActivityTransition(context);
    }

    public void setItemCategoryList(List<ItemCategory> itemCategoryList) {
        this.itemCategoryList = itemCategoryList;
    }

    private void loadViewProject(ViewHolderItem holderItem, Project project) {
        try {
            holderItem.txtCategoryName.setText(project.getProjectName());
            holderItem.imgInfoProject.setVisibility(View.VISIBLE);
            holderItem.imgArrowRight.setVisibility(View.GONE);
            holderItem.imgCategory.setPadding(0, 0, 0, 0);
            String img = project.getImage() + "";
            GlideApp.with(context)
                    .load(img)
                    .error(R.drawable.noimage)
                    .into(holderItem.imgCategory);

            holderItem.imgInfoProject.setOnClickListener(v -> {
//                Toast.makeText(context, "Project", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ProjectDetailsActivity.class);
                intent.putExtra("PROJECT_KEY", project);
                intent.putExtra("PROJECT_KEY_FROM_ADD", 1);
                baseActivityTransition.transitionTo(intent, 0);
            });

        } catch (Exception e) {
            Crashlytics.logException(e);
        }


    }

    private void loadViewCategory(ViewHolderItem holderItem, Category cate) {
        try {
            try {
                String nameCate = "";
                if (!TextUtils.isEmpty(cate.category_id + ""))
                    nameCate = sqlLanguage.getDisplayNameByCatId(cate.category_id, fileGet.getLanguage());

                if (!TextUtils.isEmpty(nameCate))
                    holderItem.txtCategoryName.setText(nameCate);
                else
                    holderItem.txtCategoryName.setText(cate.category_name);
            } catch (NumberFormatException e) {
                holderItem.txtCategoryName.setText(cate.category_name);
            }

            if (holderItem.imgInfoProject != null) {
                holderItem.imgInfoProject.setVisibility(View.GONE);
                holderItem.imgArrowRight.setVisibility(View.VISIBLE);
            }

            if (cate.level > 1) {
                holderItem.imgCategory.setPadding(15, 15, 15, 15);

                if (cate.level == 2) {
                    holderItem.imgCategory.setVisibility(View.VISIBLE);
                    if (cate.category_id.startsWith(Constants.KEY_CLASS_SIM)) {
                        GlideApp.with(context)
                                .load(cate.image)
                                .error(R.drawable.sim_default)
                                .into(holderItem.imgCategory);
                    } else {
                        if (TextUtils.isEmpty(cate.image) || cate.image.equals("empty")) {
                            GlideApp.with(context)
                                    .load(R.drawable.noimage)
                                    .into(holderItem.imgCategory);
                        } else {
                            GlideApp.with(context)
                                    .load(cate.image)
                                    .error(R.drawable.noimage)
                                    .into(holderItem.imgCategory);
                        }

                    }

                } else if (cate.level == 3 && cate.category_id.startsWith(Constants.KEY_CLASS_LAND)) {
                    if (TextUtils.isEmpty(cate.image) || cate.image.equals("empty")) {
                        GlideApp.with(context)
                                .load(R.drawable.noimage)
                                .into(holderItem.imgCategory);
                    } else {
                        GlideApp.with(context)
                                .load(cate.image)
                                .error(R.drawable.noimage)
                                .into(holderItem.imgCategory);
                    }
                } else {
                    ColorMatrix matrix = new ColorMatrix();
                    matrix.setSaturation(0);

                    if (TextUtils.isEmpty(imgGray)) {
                        if (cate.category_id.startsWith(Constants.KEY_CLASS_SIM)) {
                            GlideApp.with(context)
                                    .load(cate.image)
                                    .error(R.drawable.sim_default)
                                    .into(holderItem.imgCategory);
                        } else {
                            GlideApp.with(context)
                                    .load(cate.image)
                                    .error(R.drawable.noimage)
                                    .into(holderItem.imgCategory);
                        }
                    } else {
                        GlideApp.with(context)
                                .load(imgGray)
                                .error(R.drawable.noimage)
                                .into(holderItem.imgCategory);
                    }

                }

            } else {
                if (holderItem.imgCategory != null) {
                    holderItem.imgCategory.setVisibility(View.VISIBLE);
                    holderItem.imgCategory.setPadding(0, 0, 0, 0);
                }

                if (TextUtils.isEmpty(cate.image) || cate.image.equals("empty")) {
                    GlideApp.with(context)
                            .load(R.drawable.noimage)
                            .into(holderItem.imgCategory);
                } else {
                    GlideApp.with(context)
                            .load(cate.image)
                            .error(R.drawable.noimage)
                            .into(holderItem.imgCategory);
                }

//                if (holderItem.imgBackground != null) {
//                    String background = cate.background + "";
//                    if (!TextUtils.isEmpty(background)) {
//                        GlideApp.with(context)
//                                .load(background)
//                                .error(R.drawable.background_white)
//                                .into(holderItem.imgBackground);
//                    }
//                }

            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        ViewHolderItem viewHolder;
        switch (viewType) {
            case 1:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_root, parent, false);
                viewHolder = new ViewHolderItem(itemView);
                return viewHolder;
            case 2:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
                viewHolder = new ViewHolderItem(itemView);
                return viewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderItem holderItem = (ViewHolderItem) holder;
        holderItem.txtCategoryName.setText(itemCategoryList.get(position).getCategoryName());
        GlideApp.with(context)
                .load(itemCategoryList.get(position).getLogo())
                .error(R.drawable.noimage)
                .into(holderItem.imgCategory);
        setAnimation(holder.itemView);
    }

    private void setAnimation(View viewToAnimate) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        viewToAnimate.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return itemCategoryList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (itemCategoryList.get(position).getCategoryGroup() == 1) {
            return 1;
        } else {
            if (itemCategoryList.get(position).getCategoryId().startsWith("land")) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    public interface OnClickCategoryListener {
        void clickCategory(ItemCategory itemCategory);
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {
        ImageView imgCategory, imgArrowRight, imgBackground, imgInfoProject;
        TextView txtCategoryName;

        public ViewHolderItem(View itemView) {
            super(itemView);

            txtCategoryName = itemView.findViewById(R.id.txt_categoryName);
            imgBackground = itemView.findViewById(R.id.img_background);
            imgCategory = itemView.findViewById(R.id.img_category);
            imgArrowRight = itemView.findViewById(R.id.img_arrow_right);
            imgInfoProject = itemView.findViewById(R.id.img_info_project);

            itemView.setOnClickListener(v -> onClickCategoryListener.clickCategory(itemCategoryList.get(getAdapterPosition())));
        }
    }
}
