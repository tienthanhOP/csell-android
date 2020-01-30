package csell.com.vn.csell.views.product.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.BuildConfig;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.views.product.fragment.InputEmptyPostFragment;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;

@SuppressLint("StaticFieldLeak")
public class EmptyPostActivity extends AppCompatActivity {

    private FancyButton btnBack;

    public static FancyButton btnSave;
    public static TextView tvTitle;
    public static int level = 1;
    public static boolean selectProject = false;
    public static ArrayList<String> lsCate;

    private FileSave fileGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_post);
        Fabric.with(this, new Crashlytics());
        lsCate = new ArrayList<>();
        initView();
        addEvent();

    }

    private void addEvent() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnSave.setOnClickListener(v -> {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            InputEmptyPostFragment fragment1 = new InputEmptyPostFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_frame_created, fragment1, fragment1.getClass().getName())
                    .commit();
        });
    }

    private void initView() {

        tvTitle = findViewById(R.id.custom_TitleToolbar);
        tvTitle.setText(getString(R.string.choose_category));
        btnBack = findViewById(R.id.btn_back_navigation);
        btnBack.setText(getString(R.string.title_back_vn));
        btnSave = findViewById(R.id.btn_save_navigation);
        btnSave.setText(getString(R.string.skip));
        btnSave.setTextSize(12);
        btnSave.setVisibility(View.GONE);
        fileGet = new FileSave(this, Constants.GET);


    }

    @Override
    public void onBackPressed() {
        try {

            if (fileGet.getCategoryIdPostEmpty() == null || fileGet.getCategoryIdPostEmpty().isEmpty()) {
                finishAfterTransition();
            } else {
                if (selectProject) {
                    selectProject = false;

                    btnSave.setVisibility(View.VISIBLE);
                    super.onBackPressed();
                } else {
                    if (lsCate.size() > 0) {
                        lsCate.remove(lsCate.size() - 1);
                    }
                    level--;
                    btnSave.setVisibility(View.VISIBLE);
                    super.onBackPressed();
                }

                if (level == 1) {
                    btnSave.setVisibility(View.GONE);
                }
            }


        } catch (Exception e) {
           if(BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        level = 1;
    }

//    public void backProject() {
//        if (selectProject) {
//            selectProject = false;
//            super.onBackPressed();
//        }
//    }

}
