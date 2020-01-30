package csell.com.vn.csell.views.project.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.ProjectsController;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.adapter.ViewPagerContactAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.views.product.fragment.ProductAllCateProjectFragment;
import csell.com.vn.csell.views.project.fragment.ProjectDetailsFragment;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.models.ImageSuffix;
import csell.com.vn.csell.models.Project;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.models.ProductCountResponse;
import csell.com.vn.csell.views.product.activity.SelectCategoryActivity;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectDetailsActivity extends AppCompatActivity implements ProjectsController.OnGetProjectListener {

    private FileSave fileGet;
    private ProgressBar progressBar;
    private FancyButton btnBack;
    private TextView txtProjectName;
    //    private SQLProjects sqlProjects;
    public static Project project = null;
    private ProjectDetailsFragment projectDetailsFragment;
    private FloatingActionButton fab;
    private CarouselView carouselView;
    private ArrayList<ImageSuffix> listImagesProject;
    private Context mContext;
    private View layoutOverlay;
    private AppBarLayout appBarLayout;
    private BaseActivityTransition baseActivityTransition;
    private ProjectsController projectsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);
        Fabric.with(this, new Crashlytics());
        mContext = this;
//        sqlProjects = new SQLProjects(this);
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
        projectsController = new ProjectsController(this);
        baseActivityTransition = new BaseActivityTransition(this);

        layoutOverlay = findViewById(R.id.layout_overlay);
        appBarLayout = findViewById(R.id.app_bar);
        fileGet = new FileSave(this, Constants.GET);
        btnBack = findViewById(R.id.btn_back_navigation);
        btnBack.setText(getString(R.string.title_back_vn));

        carouselView = findViewById(R.id.carouselView);
        txtProjectName = findViewById(R.id.txtProjectName);
        TabLayout tabLayout = findViewById(R.id.tablayout_project);
        ViewPager viewPager = findViewById(R.id.viewpage_project);
        progressBar = findViewById(R.id.progress_bar);
        ViewPagerContactAdapter viewPagerAdapter = new ViewPagerContactAdapter(getSupportFragmentManager());
        projectDetailsFragment = new ProjectDetailsFragment();
        ProductAllCateProjectFragment productOfProjectFragment = new ProductAllCateProjectFragment();
        viewPagerAdapter.addFrag(projectDetailsFragment, getString(R.string.information));
        viewPagerAdapter.addFrag(productOfProjectFragment, getString(R.string.product));

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        fab = findViewById(R.id.fab_add_product);

        listImagesProject = new ArrayList<>();

        btnBack.setPadding(Utilities.dpToPx(mContext, 10), Utilities.getStatusBarHeight(mContext) + Utilities.dpToPx(mContext, 3),
                Utilities.dpToPx(mContext, 16), 0);

        Intent intent = getIntent();
        if (intent != null) {
            int checkDataIntent = intent.getIntExtra("PROJECT_KEY_FROM_ADD", 0);
            project = (Project) intent.getSerializableExtra("PROJECT_KEY");

            if (project != null) {
                if (checkDataIntent == 1) {
                    projectsController.getProjects(project.getProjectid(), this);
                }
                txtProjectName.setText(project.getProjectName() + "");
                Bundle bundle = new Bundle();
                bundle.putString("PROJECT_ID", project.getProjectid());
                productOfProjectFragment.setArguments(bundle);

                if (project.getImages() != null) {
                    if (project.getImages().size() > 0) {
                        carouselView.setImageListener(imageListener2);
                        carouselView.setPageCount(project.getImages().size());
                    }
                }
            }
        }
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            String url = Utilities.getLinkImageCDN(ProjectDetailsActivity.this, listImagesProject.get(position),
                    true);
            GlideApp.with(ProjectDetailsActivity.this).load(url).into(imageView);
        }
    };

    ImageListener imageListener2 = (position, imageView) -> GlideApp.with(ProjectDetailsActivity.this).load(project.getImages().get(position)).into(imageView);

    private void addEvent() {
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                //Collapsed
                layoutOverlay.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.GONE);
            } else {
                //Expanded
                layoutOverlay.setVisibility(View.GONE);
                btnBack.setVisibility(View.VISIBLE);
            }
        });

        btnBack.setOnClickListener(v -> finishAfterTransition());
        fab.setOnClickListener(v -> {
            Utilities.lsSelectGroupProduct.clear();
            Utilities.lsSelectGroupProduct.add(new ProductCountResponse("-1", getString(R.string.all), 0));
            Utilities.lsSelectGroupProduct.add(new ProductCountResponse("land", getString(R.string.real_estate), 0));
            Utilities.lsSelectGroupProduct.add(new ProductCountResponse("land_project", getString(R.string.project), 0));
            if (project != null)
                Utilities.lsSelectGroupProduct.add(new ProductCountResponse(project.getProjectid(), project.getProjectName(), 0));


            Intent intent = new Intent(this, SelectCategoryActivity.class);
            intent.putExtra(Constants.KEY_ADD_PRODUCT_FROM_PROJECT_DETAIL, true);
            baseActivityTransition.transitionTo(intent, Constants.ADD_PRODUCT_FROM_PROJECT_RESULT);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constants.ADD_PRODUCT_FROM_PROJECT_RESULT) {
            if (data != null) {
                Product product = (Product) data.getSerializableExtra(Constants.KEY_PASSINGDATA_ADD_PRODUCT);
                if (product != null) {
                    ProductAllCateProjectFragment.displayList(product);
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onGetProjectSuccess(Project data) {
        try {
            project = data;
            projectDetailsFragment.updateOnView();

            ArrayList<ImageSuffix> listImagesTemp = new ArrayList<>();
            for (int i = 0; i < project.getImages().size(); i++) {
                listImagesTemp.add(new ImageSuffix(project.getImages().get(i), null));
            }
            listImagesProject.addAll(listImagesTemp);

            if (listImagesProject != null) {
                if (listImagesProject.size() > 0) {
                    carouselView.setImageListener(imageListener);
                    carouselView.setPageCount(listImagesProject.size());
                }
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGetProjectFailure() {

    }

    @Override
    public void onConnectProjectFailure() {

    }
}
