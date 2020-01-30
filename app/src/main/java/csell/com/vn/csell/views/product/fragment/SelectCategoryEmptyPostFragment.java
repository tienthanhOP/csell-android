package csell.com.vn.csell.views.product.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.Objects;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.product.activity.SelectCategoryActivity;
import csell.com.vn.csell.views.product.adapter.SelectCategoryAdapter;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.Category;
import csell.com.vn.csell.sqlites.SQLCategories;
import csell.com.vn.csell.views.project.fragment.ProjectFragment;

public class SelectCategoryEmptyPostFragment extends Fragment {


    private GridView lvRoot;
    ProgressBar progressLoading;
    private SelectCategoryAdapter adapter;
    private ArrayList<Category> dataCategories = new ArrayList<>();
    private SQLCategories sqlCategories;
    private FileSave filePut;
    private FileSave fileGet;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //        initView(rootView);
//        initEvent();
        return inflater.inflate(R.layout.fragment_select_root_category_empty_post, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        getListCategory();
    }

    private void initView(View root) {
        filePut = new FileSave(getActivity(), Constants.PUT);
        fileGet = new FileSave(getActivity(), Constants.GET);
        sqlCategories = new SQLCategories(getActivity());
    }

    private void initEvent() {
        lvRoot.setOnItemClickListener((parent, view, position, id) -> {
            try {
                filePut = new FileSave(getActivity(), Constants.PUT);
                filePut.putCategoryIdPostEmpty(dataCategories.get(position).category_id);

                if (dataCategories.get(position).max_level == null) {
                    SelectCategoryActivity.level = dataCategories.get(position).level + 1;
                    nextFragment(dataCategories.get(position));
                } else {
                    if (dataCategories.get(position).max_level > 0) {
                        filePut.putMaxLevelCategory(dataCategories.get(position).max_level);
                    }
                    SelectCategoryActivity.level = dataCategories.get(position).level + 1;
                    nextFragment(dataCategories.get(position));
                }
            } catch (Exception e) {
               if(BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }
        });
    }

    private void nextFragment(Category cat) {
        try {

            if (SelectCategoryActivity.level > fileGet.getMaxLevelCategory()) {
                SelectCategoryActivity.lsCate.add(cat.category_name);
                InputEmptyPostFragment fragment = new InputEmptyPostFragment();
                getFragmentManager().beginTransaction()
                        .add(R.id.container_frame_created, fragment, fragment.getClass().getName())
                        .addToBackStack(fragment.getClass().getName())
                        .commit();

            } else {
                checkFragmentProject(cat);
            }

        } catch (Exception e) {
           if(BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void checkFragmentProject(Category cat) {
        try {

            if (cat.category_id.toLowerCase().equals(Utilities.LAND_PROJECT)) {
                SelectCategoryActivity.selectProject = true;
                ProjectFragment fragment = new ProjectFragment();

                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .add(R.id.container_frame_created, fragment, fragment.getClass().getName() + "")
                        .addToBackStack(fragment.getClass().getName() + "")
                        .commit();
            } else {
                SelectCategoryActivity.lsCate.add(cat.category_name);
                SelectCategoryEmptyPostFragment fragment = new SelectCategoryEmptyPostFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.KEY_CATEGORY, cat.category_id);
                fragment.setArguments(bundle);
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .add(R.id.container_frame_created, fragment, SelectCategoryActivity.level + "")
                        .addToBackStack(SelectCategoryActivity.level + "")
                        .commit();
            }

        } catch (Exception e) {
           if(BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void getListCategory() {
        try {
            Bundle bundle = getArguments();
            if (bundle == null) {
                dataCategories.clear();
                dataCategories.addAll(sqlCategories.getListCategoryByLevel(null, SelectCategoryActivity.level));
                if (dataCategories != null && dataCategories.size() != 0) {
                    progressLoading.setVisibility(View.GONE);
                    adapter.isChild = false;
                    adapter.notifyDataSetChanged();
                }
            } else {
                try {
                    String categoryId = bundle.getString(Constants.KEY_CATEGORY);
                    dataCategories.clear();
                    dataCategories.addAll(sqlCategories.getListCategoryByLevel(categoryId, SelectCategoryActivity.level));
                    if (dataCategories != null && dataCategories.size() != 0) {
                        progressLoading.setVisibility(View.GONE);
                        adapter.isChild = true;
                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    progressLoading.setVisibility(View.GONE);
                   if(BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                    Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                }
            }

        } catch (Exception e) {
            progressLoading.setVisibility(View.GONE);
           if(BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

}
