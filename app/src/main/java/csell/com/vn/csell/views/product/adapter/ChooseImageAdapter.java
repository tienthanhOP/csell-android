package csell.com.vn.csell.views.product.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.esafirm.imagepicker.features.ImagePicker;

import java.io.File;
import java.util.List;

import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.models.ImageSuffix;
import csell.com.vn.csell.views.product.activity.EditProductActivity;
import csell.com.vn.csell.views.product.activity.SelectCategoryActivity;
import csell.com.vn.csell.views.product.adapter.viewholder.ChooseImageViewHolder;
import csell.com.vn.csell.views.product.adapter.viewholder.HeaderChooseImageViewHolder;
import csell.com.vn.csell.views.product.fragment.ChooseImageUploadFragment;

/**
 * Created by cuong.nv on 4/13/2018.
 */

public class ChooseImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TYPE_HEADER = -1;
    private final static int TYPE_ITEM = -2;
    private Context context;
    private List<ImageSuffix> lsImages;
    private List<String> lsImagesFake;
    private boolean isFake;
    private boolean isSelectedImage;

    public ChooseImageAdapter(Context context, List<ImageSuffix> lsImages, List<String> lsImagesFake, boolean isFake) {
        this.context = context;
        this.lsImages = lsImages;
        this.lsImagesFake = lsImagesFake;
        this.isFake = isFake;
    }

    public List<ImageSuffix> getLsImages() {
        return lsImages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == TYPE_HEADER) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_choose_image, parent, false);
            return new HeaderChooseImageViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_select, parent, false);
            return new ChooseImageViewHolder(itemView);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mainHolder, int position) {
        try {
            isSelectedImage = true;
            if (mainHolder instanceof HeaderChooseImageViewHolder) {
                HeaderChooseImageViewHolder headerHolder = (HeaderChooseImageViewHolder) mainHolder;
                headerHolder.imgChooseImage.setOnClickListener(v -> selectImage());
            } else {
                ChooseImageViewHolder holder = (ChooseImageViewHolder) mainHolder;

                if (!isFake) {
                    GlideApp.with(context)
                            .load(lsImages.get(position - 1).getPath())
                            .placeholder(R.drawable.noimage)
                            .error(R.drawable.ic_logo)
                            .into(holder.imgSelected);

                    holder.imgRemove.setOnClickListener(v -> {
                        lsImages.remove(position - 1);
                        ChooseImageUploadFragment.sizeListImage--;

                        notifyDataSetChanged();
                        try {
                            if (context instanceof EditProductActivity) {
                                try {
                                    ((EditProductActivity) context).product.setImages(lsImages);
                                } catch (Exception e) {
                                    Toast.makeText(context, context.getResources().getString(R.string.text_error_occurred), Toast.LENGTH_SHORT).show();
                                }
                                ((EditProductActivity) context).txtCountSelectImage
                                        .setText(context.getResources().getString(R.string.image_number) + " " + lsImages.size() + "/10");
                            } else if (context instanceof SelectCategoryActivity) {
                                ChooseImageUploadFragment.txtCountSelectImage
                                        .setText(context.getResources().getString(R.string.image_number) + " " + lsImages.size() + "/10");
                            }
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                            Log.e(getClass().getName(), e.toString());
                        }
                    });
                } else {
                    if (lsImages.get(position - 1).isSuccessResize()) {
                        holder.layoutLoading.setVisibility(View.GONE);
                    } else {
                        holder.layoutLoading.setVisibility(View.VISIBLE);
                    }

                    File imgFile = new File(lsImagesFake.get(position - 1));
                    if (imgFile.exists()) {
                        GlideApp.with(context)
                                .load(imgFile.getPath())
                                .placeholder(R.drawable.noimage)
                                .error(R.drawable.ic_logo)
                                .into(holder.imgSelected);
                    }

                    holder.imgRemove.setOnClickListener(v -> {
                        if (ChooseImageUploadFragment.isSuccessResizeImage) {
                            try {
                                lsImagesFake.remove(position - 1);
                                ChooseImageUploadFragment.imageSuffixes.remove(position - 1);
                            } catch (Exception ignored) {

                            }

                            notifyDataSetChanged();
                            try {
                                if (context instanceof EditProductActivity) {
                                    ((EditProductActivity) context).txtCountSelectImage.setText(context.getResources().getString(R.string.image_number) + " " + lsImagesFake.size() + "/10");
                                } else if (context instanceof SelectCategoryActivity) {
                                    ChooseImageUploadFragment.txtCountSelectImage.setText(context.getResources().getString(R.string.image_number) + " " + lsImagesFake.size() + " " + "/10");
                                }
                            } catch (Exception e) {
                                Crashlytics.logException(e);
                            }
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.loading_image),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public int getItemCount() {
        if (!isFake) {
            return lsImages.size() + 1;
        } else {
            if (lsImagesFake.size() == 0) {
                if (!isSelectedImage)
                    return 0;
                else
                    return 1;
            } else
                return lsImagesFake.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private void selectImage() {
        if (isFake) {
            if (!ChooseImageUploadFragment.isSelectImages) {
                selectImagePicker();
            } else {
                if (ChooseImageUploadFragment.isSuccessResizeImage) {

                    selectImagePicker();
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.loading_image), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (!EditProductActivity.isSelectImages) {
                selectImagePicker();
            } else {
                if (EditProductActivity.isSuccessUploadImage) {

                    selectImagePicker();
                    EditProductActivity.isSuccessUploadImage = false;
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.loading_image), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void selectImagePicker() {
        if (isFake) {
            if (ChooseImageUploadFragment.sizeListImage < 10) {
                ImagePicker.create((Activity) context)
                        .folderMode(true) // folder mode (false by default)
                        .toolbarFolderTitle("Thư mục") // folder selection title
                        .toolbarImageTitle("Chạm để chọn ảnh") // image selection title
                        .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
                        .multi() // multi mode (default mode)
                        .limit(10 - ChooseImageUploadFragment.imagesFake.size()) // max images can be selected (99 by default)
                        .showCamera(true) // show camera or not (true by default)
                        .imageDirectory("csell") // directory name for captured image  ("Camera" folder by default)
                        .theme(R.style.ImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
                        .enableLog(false) // disabling log
                        .start(); // start image picker activity with request code
            } else {
                Toast.makeText(context, context.getString(R.string.text_error_enogh_image), Toast.LENGTH_LONG).show();
            }
        } else {
            if (EditProductActivity.lsImagesDefault.size() < 10) {
                ImagePicker.create((Activity) context)
                        .folderMode(true) // folder mode (false by default)
                        .toolbarFolderTitle("Thư mục") // folder selection title
                        .toolbarImageTitle("Chạm để chọn ảnh") // image selection title
                        .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
                        .multi() // multi mode (default mode)
                        .limit(10 - EditProductActivity.lsImagesDefault.size()) // max images can be selected (99 by default)
                        .showCamera(true) // show camera or not (true by default)
                        .imageDirectory("csell") // directory name for captured image  ("Camera" folder by default)
                        .theme(R.style.ImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
                        .enableLog(false) // disabling log
                        .start(); // start image picker activity with request code
            } else {
                EditProductActivity.lsImagesDefault.size();
                Toast.makeText(context, context.getString(R.string.text_error_enogh_image), Toast.LENGTH_LONG).show();
            }
        }
    }
}
