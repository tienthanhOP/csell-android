package csell.com.vn.csell.views.social.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import java.util.ArrayList;

import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import io.fabric.sdk.android.Fabric;

public class PreviewImageActivity extends AppCompatActivity {

    private ArrayList<String> imgs;
    private int position;
    private String description;
    private CarouselView carouselView;
    ViewListener viewListener = new ViewListener() {
        @Override
        public View setViewForPosition(int position) {

            @SuppressLint("InflateParams") View customView = getLayoutInflater().inflate(R.layout.view_image_preview, null);

            ImageView imagePreview = customView.findViewById(R.id.image_preview);

            GlideApp.with(PreviewImageActivity.this)
                    .load(imgs.get(position))
                    .into(imagePreview);
            carouselView.setIndicatorGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);

            return customView;
        }
    };
    private ImageView imgClose;
    private boolean isProductSim;
    private RelativeLayout layoutSim;
    private ImageView imgSim;
    private TextView txtDes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);

        Fabric.with(this, new Crashlytics());

        initView();
        setupWindowAnimations();
        getData();
        initEvent();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.preview_image);
        getWindow().setEnterTransition(transition);
    }

    private void initView() {
        carouselView = findViewById(R.id.images_preview);
        imgClose = findViewById(R.id.close_preview);
        layoutSim = findViewById(R.id.layout_sim);
        imgSim = findViewById(R.id.img_sim);
        txtDes = findViewById(R.id.txtDetailSim);

        imgs = new ArrayList<>();

        carouselView.setRadius(10);
    }

    private void initEvent() {
        imgClose.setOnClickListener(v -> onBackPressed());
    }

    private void getData() {
        try {
            Intent intent = getIntent();
            if (intent != null) {
                imgs = (ArrayList<String>) intent.getSerializableExtra(Constants.PREVIEW);
                position = intent.getIntExtra(Constants.PREVIEW_POSITION, 0);
                description = intent.getStringExtra(Constants.PREVIEW_DES);
                isProductSim = intent.getBooleanExtra(Constants.PREVIEW_IS_SIM, false);
            }

            if (isProductSim) {
                carouselView.setVisibility(View.GONE);
                layoutSim.setVisibility(View.VISIBLE);

                GlideApp.with(PreviewImageActivity.this)
                        .load(imgs.get(0))
                        .error(R.drawable.bg_3)
                        .into(imgSim);

                txtDes.setText(description.trim());
                txtDes.post(() -> {
                    int lineCount = txtDes.getLineCount();
                    if (lineCount > 20) {
                        txtDes.setGravity(Gravity.CENTER_VERTICAL);
                    } else {
                        txtDes.setGravity(Gravity.CENTER);
                    }
                });
            } else {
                carouselView.setVisibility(View.VISIBLE);
                layoutSim.setVisibility(View.GONE);

                carouselView.setViewListener(viewListener);
                carouselView.setPageCount(imgs.size());
                carouselView.setCurrentItem(position);
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }
}
