package csell.com.vn.csell.views.product.fragment;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.libraries.compressors.Luban;
import csell.com.vn.csell.libraries.compressors.OnCompressListener;
import csell.com.vn.csell.models.ImageSuffix;
import csell.com.vn.csell.mycustoms.SpacesItemDecoration;
import csell.com.vn.csell.views.product.activity.SelectCategoryActivity;
import csell.com.vn.csell.views.product.adapter.ChooseImageAdapter;
import io.fabric.sdk.android.Fabric;

/**
 * Created by cuong.nv on 4/9/2018.
 */
@SuppressLint("StaticFieldLeak")
public class ChooseImageUploadFragment extends Fragment {

    public static TextView txtCountSelectImage;
    public static ArrayList<ImageSuffix> imageSuffixes;
    public static ArrayList<String> imagesFake;
    public static int sizeListImage = 0;
    public static boolean isSuccessResizeImage;
    public static boolean isSelectImages;
    public LinearLayout fromChoosePhoto;
    public ProgressBar progressLoading;
    public ChooseImageAdapter chooseImageAdapter;
    public ArrayList<String> lsImageUrl;
    public List<String> listURLImagesAPI;
    public Activity activity = null;
    private TextView txtNext;
    private Context mContext;

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimator(transit, enter, nextAnim);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSuccessResizeImage = true;
        mContext = getActivity();
        Fabric.with(mContext, new Crashlytics());
        lsImageUrl = new ArrayList<>();
        listURLImagesAPI = new ArrayList<>();
        imageSuffixes = new ArrayList<>();
        imagesFake = new ArrayList<>();
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_choose_image, container, false);
        sizeListImage = 0;
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestPermission();
    }

    @SuppressLint("SetTextI18n")
    private void initView(View view) {
        txtNext = view.findViewById(R.id.txtNext);
        progressLoading = view.findViewById(R.id.progress_loading);
        fromChoosePhoto = view.findViewById(R.id.from_choosePhoto);
        txtCountSelectImage = view.findViewById(R.id.txtCountSelectImage);
        RecyclerView recyclerViewImageUpload = view.findViewById(R.id.recyclerViewImageUpload);
        txtCountSelectImage.setText(getResources().getString(R.string.image_number) + " " + lsImageUrl.size() + "/10");
        chooseImageAdapter = new ChooseImageAdapter(getActivity(), imageSuffixes, imagesFake, true);
        recyclerViewImageUpload.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerViewImageUpload.addItemDecoration(new SpacesItemDecoration(3, 0, false));
        recyclerViewImageUpload.setAdapter(chooseImageAdapter);
        txtCountSelectImage.setVisibility(View.GONE);

        SelectCategoryActivity.paramsImages.clear();
    }

    private void initEvent() {
        txtNext.setOnClickListener(v -> {
            if (isSuccessResizeImage) {
                EndCreateFragment fragment = new EndCreateFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.SIZE_LIST_IMAGE_KEY, imageSuffixes.size());
                fragment.setArguments(bundle);
                if (getActivity() != null) {
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    manager.popBackStack();
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    transaction.add(R.id.container_frame_created, fragment, fragment.getClass().getName())
                            .addToBackStack(fragment.getClass().getName())
                            .commit();
                }
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.loading_image), Toast.LENGTH_SHORT).show();
            }
        });

        fromChoosePhoto.setOnClickListener(v -> selectImage());
    }

    private void selectImage() {
        try {
            if (!isSelectImages) {
                selectImagePicker();
            } else {
                if (isSuccessResizeImage) {

                    selectImagePicker();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.loading_image), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void selectImagePicker() {
        try {
            if (sizeListImage < 10) {
                ImagePicker.create(activity)
                        .folderMode(true) // folder mode (false by default)
                        .toolbarFolderTitle("Thư mục") // folder selection title
                        .toolbarImageTitle("Chạm để chọn ảnh") // image selection title
                        .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
                        .multi() // multi mode (default mode)
                        .limit(10 - imagesFake.size()) // max images can be selected (99 by default)
                        .showCamera(true) // show camera or not (true by default)
                        .imageDirectory("csell") // directory name for captured image  ("Camera" folder by default)
                        .theme(R.style.ImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
                        .enableLog(false) // disabling log
                        .start(); // start image picker activity with request code
            } else {
                Toast.makeText(activity, activity.getString(R.string.text_error_enogh_image), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
                txtCountSelectImage.setVisibility(View.VISIBLE);
                fromChoosePhoto.setVisibility(View.GONE);

                isSuccessResizeImage = false;
                isSelectImages = true;
                ArrayList<Image> images = new ArrayList<>();
                images.clear();
                images = (ArrayList<Image>) ImagePicker.getImages(data);

                for (int i = 0; i < images.size(); i++) {
                    imagesFake.add(images.get(i).getPath());
                    imageSuffixes.add(new ImageSuffix(images.get(i).getPath()));
                }

                txtCountSelectImage.setText(getResources().getString(R.string.image_number) + " " + imagesFake.size() + "/10");

                chooseImageAdapter.notifyItemRangeInserted(imagesFake.size(), images.size());
                chooseImageAdapter.notifyItemRangeInserted(imageSuffixes.size(), images.size());

                new ResizeImage(images).execute();
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void requestPermission() {
        int checkCameraPer = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA);
        int checkWiteExPer = ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int checkReadExPer = ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionGranted = PackageManager.PERMISSION_GRANTED;

        String[] permissions = new String[]{
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE};
        //check Android 6+
        if (Build.VERSION.SDK_INT >= 23) {
            //check permission granted
            //check permission granted
            if (checkCameraPer != permissionGranted
                    || checkWiteExPer != permissionGranted
                    || checkReadExPer != permissionGranted
            ) {
                //request Permissions
                ActivityCompat.requestPermissions(getActivity(), permissions, Constants.PERMISSION_CONSTANT);

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((SelectCategoryActivity) getActivity()).updateTitleToolbar(getString(R.string.upload_image));
        SelectCategoryActivity.currentFragment = "ChooseImageUploadFragment";
    }

    class ResizeImage extends AsyncTask<Void, Void, Void> {

        private ArrayList<Image> lstImage;

        ResizeImage(ArrayList<Image> lst) {
            this.lstImage = lst;
        }

        @Override
        protected void onPreExecute() {
//            progressLoading.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @SuppressLint("CheckResult")
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Image img = lstImage.get(0);

                Luban.compress(mContext, new File(img.getPath()))
                        .setMaxSize(1024)                // limit the final image size（unit：Kb）
                        .setMaxHeight(1920)             // limit image height
                        .setMaxWidth(1080)              // limit image width
                        .putGear(Luban.CUSTOM_GEAR)     // use CUSTOM GEAR compression mode
                        .launch(new OnCompressListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess(File file) {
                                try {
                                    SelectCategoryActivity.paramsImages.add(file.getPath());

                                    int index = imagesFake.indexOf(lstImage.get(0).getPath());
                                    chooseImageAdapter.getLsImages().get(index).setSuccessResize(true);
                                    chooseImageAdapter.notifyDataSetChanged();

                                    lstImage.remove(0);
                                    try {
                                        if (file.getPath().contains(Luban.DEFAULT_DISK_CACHE_DIR)) {
                                            File fileDelete = new File(file.getPath());
                                            boolean deleted = fileDelete.delete();
                                            if (deleted) {
                                                Log.w("TEST_", "1");
                                            } else {
                                                Log.w("TEST_", "2");
                                            }
                                        }
                                    } catch (Exception e) {
                                        Crashlytics.logException(e);
                                    }

                                    if (lstImage.size() > 0) {
                                        new ResizeImage(lstImage).execute();
                                    } else {
                                        isSuccessResizeImage = true;
                                    }
                                } catch (Exception e) {
                                    Crashlytics.logException(e);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Crashlytics.logException(e);
                            }
                        });

            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    Log.d("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }

            return null;
        }
    }
}
