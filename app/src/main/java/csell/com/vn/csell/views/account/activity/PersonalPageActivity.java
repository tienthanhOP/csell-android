package csell.com.vn.csell.views.account.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.esafirm.imagepicker.features.ImagePicker;
//import com.google.firebase.firestore.FirebaseFirestore;
import com.esafirm.imagepicker.model.Image;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.UploadImageController;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.csell.adapter.ViewPagerContactAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.libraries.compressors.Luban;
import csell.com.vn.csell.libraries.compressors.OnCompressListener;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.models.ImageSuffix;
import csell.com.vn.csell.models.ImageUpload;
import csell.com.vn.csell.models.UploadImageRetro;
import csell.com.vn.csell.views.product.activity.SelectCategoryActivity;
import csell.com.vn.csell.views.note.activity.AddNoteProductActivity;
import csell.com.vn.csell.views.social.activity.PreviewImageActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalPageActivity extends AppCompatActivity implements UploadImageController.OnUploadImagesListener {

    public static CircleImageView imgAvatar;
    private FileSave fileGet;
    private FileSave filePut;
    private FancyButton btnBack;
    private TextView tvFullname;
    private LinearLayout btnPostShare, btnAddNote, btnEditInfo;
    Button btnNewAvatar;
    Button btnChooseGallery;
    Button btnShowAvatar;
    Button btnRemoveAvatar;
    private Dialog dialogAvatar;
    private ArrayList<String> listPathImageResize = new ArrayList<>();
    public static String avatarUrl = "";
    private String coverUrl = "";
    private boolean isCover = false;
    private RelativeLayout tvUpdateCover;
    private ImageView imgCover;
    private int timeoutUpdate = 0;
    private int timeoutUpdateCover = 0;
    private Context mContext;
    private ArrayList<String> listImageBase64;
    private List<String> listURLImagesAPI;
    private AppBarLayout appBarLayout;
    private RelativeLayout layoutAvatar;
    private RelativeLayout layoutEditAvatar;
    private LinearLayout layoutOption;
    private View layoutOverlay;
    private BaseActivityTransition baseActivityTransition;
    private UploadImageController uploadImageController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);
        mContext = this;
        Fabric.with(mContext, new Crashlytics());
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        initView();
        setupWindowAnimations();
        addEvent();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    private void initView() {
        uploadImageController = new UploadImageController(this);
        baseActivityTransition = new BaseActivityTransition(this);

        fileGet = new FileSave(this, Constants.GET);
        filePut = new FileSave(this, Constants.PUT);
        btnBack = findViewById(R.id.btn_back_navigation);
        btnBack.setText(getString(R.string.title_back_vn));
        btnBack.setIconColor(getResources().getColor(R.color.white_100));
        btnBack.setTextColor(getResources().getColor(R.color.white_100));

        btnBack.setPadding(Utilities.dpToPx(mContext, 10), Utilities.getStatusBarHeight(mContext) + Utilities.dpToPx(mContext, 3),
                Utilities.dpToPx(mContext, 16), 0);

        layoutOverlay = findViewById(R.id.layout_overlay);
        layoutOption = findViewById(R.id.layout_option_personal);
        layoutEditAvatar = findViewById(R.id.layout_edit_avatar);
        layoutAvatar = findViewById(R.id.layout_avatar);
        imgAvatar = findViewById(R.id.img_avatar_personal_page);
        appBarLayout = findViewById(R.id.app_bar);
        imgCover = findViewById(R.id.img_cover);
        listImageBase64 = new ArrayList<>();
        listURLImagesAPI = new ArrayList<>();

        GlideApp.with(this)
                .load(!TextUtils.isEmpty(fileGet.getUserAvatar()) ? fileGet.getUserAvatar() : R.drawable.ic_logo)
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(imgAvatar);

        GlideApp.with(this)
                .load(!TextUtils.isEmpty(fileGet.getUserCover()) ? fileGet.getUserCover() : R.drawable.background_gradient)
                .placeholder(R.drawable.noimage)
                .error(R.drawable.background_gradient)
                .into(imgCover);

        tvFullname = findViewById(R.id.tv_fullname);
        tvFullname.setText(fileGet.getDisplayName());
        btnPostShare = findViewById(R.id.btn_post_share);
        btnAddNote = findViewById(R.id.btn_add_note);
        btnEditInfo = findViewById(R.id.btn_edit_info);
        tvUpdateCover = findViewById(R.id.tv_update_cover);

    }

    private void addEvent() {

        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {

            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                //  Collapsed
                layoutOverlay.setVisibility(View.VISIBLE);
                layoutEditAvatar.setVisibility(View.GONE);
                btnBack.setVisibility(View.GONE);
                tvUpdateCover.setVisibility(View.GONE);
                layoutAvatar.setLayoutParams(new RelativeLayout.LayoutParams(Utilities.dpToPx(mContext, 46),
                        Utilities.dpToPx(mContext, 46)));
            } else {
                //Expanded
                layoutOverlay.setVisibility(View.GONE);
                layoutEditAvatar.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.VISIBLE);
                tvUpdateCover.setVisibility(View.VISIBLE);
                layoutAvatar.setLayoutParams(new RelativeLayout.LayoutParams(Utilities.dpToPx(mContext, 116),
                        Utilities.dpToPx(mContext, 116)));
            }
        });

        btnEditInfo.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditUserActivity.class);
            baseActivityTransition.transitionTo(intent, 0);
        });

        btnAddNote.setOnClickListener(view -> {
            // sang màn hình add note
            requestPermissionsAddNode();
        });

        imgAvatar.setOnClickListener(v -> {
            isCover = false;
            displayDialogSharePost();
        });

        btnBack.setOnClickListener(v -> onBackPressed());

        btnPostShare.setOnClickListener(v -> {
            Intent intent = new Intent(PersonalPageActivity.this, SelectCategoryActivity.class);
            intent.putExtra(Constants.POST_EMPTY_KEY, true);
            baseActivityTransition.transitionTo(intent, 0);
        });

        tvUpdateCover.setOnClickListener(view -> {
            isCover = true;
            ImagePicker.create(this)
                    .folderMode(true) // folder mode (false by default)
                    .toolbarFolderTitle("Folder") // folder selection title
                    .toolbarImageTitle("Tap to select") // image selection title
                    .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
//                    .multi() // multi mode (default mode)
                    .limit(1) // max images can be selected (99 by default)
                    .showCamera(true) // show camera or not (true by default)
                    .imageDirectory("csell") // directory name for captured image  ("Camera" folder by default)
                    .theme(R.style.ImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
                    .enableLog(false) // disabling log
                    .start(); // start image picker activity with request code
        });

    }

    private void requestPermissionsAddNode() {
        int checkReadCal = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        int checkWriteCal = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        int permissionGranted = PackageManager.PERMISSION_GRANTED;

        String[] permissions1 = new String[]{
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR};
        //check Android 6+
        if (Build.VERSION.SDK_INT >= 23) {
            //check permission granted
            if (checkReadCal != permissionGranted || checkWriteCal != permissionGranted) {
                //request Permissions
                ActivityCompat.requestPermissions(PersonalPageActivity.this, permissions1, 1);
            } else {
                Intent note = new Intent(PersonalPageActivity.this, AddNoteProductActivity.class);
                baseActivityTransition.transitionTo(note, 0);
            }
        } else {
            Intent note = new Intent(PersonalPageActivity.this, AddNoteProductActivity.class);
            baseActivityTransition.transitionTo(note, 0);
        }
    }

    private void displayDialogSharePost() {

        dialogAvatar = new Dialog(this);
        dialogAvatar.setContentView(R.layout.dialog_take_image);

        btnNewAvatar = dialogAvatar.findViewById(R.id.btn_new_avatar);
        btnChooseGallery = dialogAvatar.findViewById(R.id.btn_choose_gallery);
        btnShowAvatar = dialogAvatar.findViewById(R.id.btn_show_avatar);
        btnRemoveAvatar = dialogAvatar.findViewById(R.id.btn_remove_avatar);

        //custom position dialog
        Window dialogWindow = dialogAvatar.getWindow();
        WindowManager.LayoutParams lp;
        if (dialogWindow != null) {
            lp = dialogWindow.getAttributes();
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            lp.windowAnimations = R.style.DialogCreateAnimation;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT; // Width
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // Height
            dialogWindow.setAttributes(lp);
        }

        addEventDialog();

        dialogAvatar.show();

    }

    private void addEventDialog() {

        btnNewAvatar.setOnClickListener(v -> {
            openCamera();
            dialogAvatar.dismiss();
        });

        btnChooseGallery.setOnClickListener(v -> {
            openGallery();
            dialogAvatar.dismiss();
        });

        btnShowAvatar.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(fileGet.getUserAvatar())) {
                ArrayList<ImageSuffix> list = new ArrayList<>();
                list.add(new ImageSuffix(fileGet.getUserAvatar(), null));
                Intent preview = new Intent(this, PreviewImageActivity.class);
                preview.putExtra("preview", list);
                baseActivityTransition.transitionTo(preview, 0);
                dialogAvatar.dismiss();
            }
        });

        btnRemoveAvatar.setOnClickListener(v -> {

            try {
                final AlertDialog.Builder builder = new AlertDialog.Builder(PersonalPageActivity.this);
                final String message = getString(R.string.alert_message_deleted_avatar);

                builder.setMessage(message).setTitle(getString(R.string.alert_title_deleted_avatar))
                        .setNegativeButton(getString(R.string.revoke),
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    dialogAvatar.dismiss();
                                })
                        .setPositiveButton(getString(R.string.agree),
                                (dialog, which) -> {
                                    if (!TextUtils.isEmpty(fileGet.getUserAvatar())) {
                                        updateAvatar("");
                                    } else dialog.dismiss();
                                });
                builder.create().show();

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }


        });

    }

    private void openCamera() {
        requestPermissions();
    }

    public void requestPermissions() {
        int checkReadCal = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int checkWriteCal = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionGranted = PackageManager.PERMISSION_GRANTED;

        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //check Android 6+
        if (Build.VERSION.SDK_INT >= 23) {
            //check permission granted
            if (checkReadCal != permissionGranted
                    || checkWriteCal != permissionGranted) {
                //request Permissions
                ActivityCompat.requestPermissions(this, permissions, 2);
            } else {
                captureImage();
            }
        } else {
            captureImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    captureImage();
                }
                break;
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    Intent note = new Intent(PersonalPageActivity.this, AddNoteProductActivity.class);
                    baseActivityTransition.transitionTo(note, 0);
                } else {
                    // permission denied, boo! Disable the
                    showDialogMessage();
                }
                break;
        }
    }

    private void showDialogMessage() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    Intent note = new Intent(PersonalPageActivity.this, AddNoteProductActivity.class);
                    baseActivityTransition.transitionTo(note, 0);
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalPageActivity.this);
        builder.setMessage("Thông báo từ lịch đã bị từ chối ").setPositiveButton("OK", dialogClickListener)
                .show();
    }

    private void openGallery() {
        ImagePicker.create(this)
                .folderMode(true) // folder mode (false by default)
                .toolbarFolderTitle("Folder") // folder selection title
                .toolbarImageTitle("Tap to select") // image selection title
                .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
//                    .multi() // multi mode (default mode)
                .limit(1) // max images can be selected (99 by default)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("csell") // directory name for captured image  ("Camera" folder by default)
                .theme(R.style.ImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
                .enableLog(false) // disabling log
                .start(); // start image picker activity with request code
    }

    private void captureImage() {
        ImagePicker.cameraOnly().start(this);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            try {
                ArrayList<Image> images = (ArrayList<Image>) ImagePicker.getImages(data);
                listImageBase64.clear();
                new resizeImage(images).execute();
                for (Image img : images) {
                    if (!isCover) {
                        GlideApp.with(this)
                                .load(new File(img.getPath()))
                                .error(R.drawable.ic_logo)
                                .into(imgAvatar);
                    } else {
                        GlideApp.with(this)
                                .load(new File(img.getPath()))
                                .error(R.drawable.background_gradient)
                                .into(imgCover);
                    }
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onUploadImagesSuccess(List<String> data) {
        listURLImagesAPI = data;

        if (!isCover) {
            avatarUrl = listURLImagesAPI.get(0);
            filePut.putUserAvatar(avatarUrl);
            updateAvatar(avatarUrl);
        } else {
            coverUrl = listURLImagesAPI.get(0);
            filePut.putUserCover(coverUrl);
            updateCover(coverUrl);
        }
    }

    @Override
    public void onErrorUploadImages() {

    }

    @Override
    public void onUploadImagesFailure() {

    }

    @Override
    public void onConnectUploadImagesFailure() {

    }

    @SuppressLint("StaticFieldLeak")
    class resizeImage extends AsyncTask<Void, Void, Void> {

        private ArrayList<Image> lstImage;

        resizeImage(ArrayList<Image> lst) {
            this.lstImage = lst;
        }

        @Override
        protected void onPreExecute() {
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

                                    lstImage.remove(0);
                                    Bitmap bm = BitmapFactory.decodeFile(file.getPath());
                                    String base64 = Utilities.getEncoded64ImageStringFromBitmap(bm);
                                    listImageBase64.add(base64); //1,
                                    if (lstImage.size() > 0) {
                                        new resizeImage(lstImage).execute();
                                    } else {
                                        uploadImageGetURL(listImageBase64);
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

    private void uploadImageGetURL(ArrayList<String> listImageBase) {

        try {

            List<ImageUpload> uploadList = new ArrayList<>();
            for (int i = 0; i < listImageBase.size(); i++) {
                uploadList.add(new ImageUpload(fileGet.getUserId() + System.currentTimeMillis() + i + "", listImageBase.get(i)));
            }

            UploadImageRetro imageRetro = new UploadImageRetro();
            imageRetro.setType(1);
            imageRetro.setImages(uploadList);

            uploadImage(imageRetro);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void uploadImage(UploadImageRetro imageRetro) {
        try {
            uploadImageController.uploadImages(imageRetro, this);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void updateAvatar(String ava) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(EntityAPI.FIELD_AVATAR, ava);
        PostAPI api = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        Call<JSONResponse<Object>> updateAvatar = api.updateAvatar(map);
        updateAvatar.enqueue(new Callback<JSONResponse<Object>>() {
            @Override
            public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                try {
                    if (response.isSuccessful()) {
                        dialogAvatar.dismiss();
                        if (response.body() != null) {
                            if (response.body().getSuccess() != null) {
                                if (response.body().getSuccess()) {
                                    listPathImageResize.clear();
                                    avatarUrl = "";
                                    if (TextUtils.isEmpty(ava)) {
                                        GlideApp.with(PersonalPageActivity.this)
                                                .load(R.drawable.ic_logo)
                                                .into(imgAvatar);
                                        filePut.putUserAvatar("");
                                    }
                                    Toast.makeText(PersonalPageActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                } else {
                                    Utilities.refreshToken(MainActivity.mainContext, response.body().getMessage().toLowerCase() + "");
                                    Toast.makeText(PersonalPageActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }

                            String msg = response.body().getError();
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(PersonalPageActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        }

                    } else {
                        try {
                            JSONObject jsonObject;
                            if (response.errorBody() != null) {
                                jsonObject = new JSONObject(response.errorBody().string());
                                String msg = (String) jsonObject.get(Constants.ERROR);

                                if (!TextUtils.isEmpty(msg)) {
                                    Toast.makeText(PersonalPageActivity.this, msg, Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                    updateAvatar.cancel();
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                updateAvatar.cancel();
                if (timeoutUpdate < 1) {
                    timeoutUpdate++;
                    updateAvatar(ava);
                } else {
                    timeoutUpdate = 0;
                    Toast.makeText(PersonalPageActivity.this, getString(R.string.text_error_unknown), Toast.LENGTH_LONG).show();
                }
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + t.getMessage()));
            }

        });
    }

    private void updateCover(String cover) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(EntityAPI.FIELD_COVER, cover);
        PostAPI api = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        Call<JSONResponse<Object>> updateCover = api.updateCover(map);
        updateCover.enqueue(new Callback<JSONResponse<Object>>() {
            @Override
            public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getSuccess() != null) {
                                if (response.body().getSuccess()) {
                                    listPathImageResize.clear();
                                    coverUrl = "";
                                    Toast.makeText(PersonalPageActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                } else {
                                    Utilities.refreshToken(MainActivity.mainContext, response.body().getMessage().toLowerCase() + "");
                                    Toast.makeText(PersonalPageActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }

                            String msg = response.body().getError();
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(PersonalPageActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        }

                    } else {
                        try {
                            JSONObject jsonObject;
                            if (response.errorBody() != null) {
                                jsonObject = new JSONObject(response.errorBody().string());
                                String msg = (String) jsonObject.get(Constants.ERROR);

                                if (!TextUtils.isEmpty(msg)) {
                                    Toast.makeText(PersonalPageActivity.this, msg, Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                    updateCover.cancel();
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                updateCover.cancel();
                if (timeoutUpdateCover < 1) {
                    timeoutUpdateCover++;
                    updateCover(cover);
                } else {
                    timeoutUpdateCover = 0;
                    Toast.makeText(PersonalPageActivity.this, getString(R.string.text_error_unknown), Toast.LENGTH_LONG).show();
                }
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + t.getMessage()));
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvFullname.setText(fileGet.getDisplayName());
    }
}
