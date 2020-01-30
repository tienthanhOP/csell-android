package csell.com.vn.csell.views.project.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.project.activity.ProjectDetailsActivity;
import csell.com.vn.csell.views.product.activity.EmptyPostActivity;
import csell.com.vn.csell.views.product.activity.SelectCategoryActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.views.product.fragment.SelectCategoryEmptyPostFragment;
import csell.com.vn.csell.views.product.fragment.SelectRootCategoryFragment;
import csell.com.vn.csell.models.Project;
import csell.com.vn.csell.sqlites.SQLProjects;

/**
 * Created by cuong.nv on 4/5/2018.
 */

public class ProjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Project> data;
    private FileSave fileGet;
    private FileSave filePut;
    private SQLProjects sqlProjects;
    private BaseActivityTransition baseActivityTransition;

    public ProjectAdapter(Context mContext, List<Project> data) {
        this.mContext = mContext;
        this.data = data;
        fileGet = new FileSave(mContext, Constants.GET);
        filePut = new FileSave(mContext, Constants.PUT);
        sqlProjects = new SQLProjects(mContext);
        baseActivityTransition = new BaseActivityTransition(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
        return new ViewHolderItem(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            ViewHolderItem holderItem = (ViewHolderItem) holder;

            holderItem.txtProjectName.setText(data.get(position).getProjectName());
            holderItem.txtProjectDescription.setText(data.get(position).getDescription());
            if (TextUtils.isEmpty(data.get(position).getDistrict()))
                holderItem.txtAddress.setText(data.get(position).getCity());
            holderItem.txtAddress.setText(data.get(position).getDistrict() + ", " + data.get(position).getCity());

            holderItem.itemView.setOnClickListener(v -> {

                if (mContext instanceof SelectCategoryActivity) {
                    sqlProjects.insertProject(data.get(position));

                    filePut.putProjectId(data.get(position).getProjectid());
                    filePut.putProjectName(data.get(position).getProjectName());
//                    filePut.putProjectImage(data.get(position).getImage());
                    filePut.putProjectCity(data.get(position).getCity().toLowerCase());
                    filePut.putProjectDistrict(data.get(position).getDistrict().toLowerCase());
                    SelectCategoryActivity.selectProject = true;
//                    SelectCategoryActivity.categories.add();
                    SelectRootCategoryFragment fragment = new SelectRootCategoryFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.KEY_CATEGORY, fileGet.getRootCategoryId());
                    fragment.setArguments(bundle);
                    ((SelectCategoryActivity) mContext).getSupportFragmentManager().beginTransaction()
                            .add(R.id.container_frame_created, fragment, SelectCategoryActivity.level + "")
                            .addToBackStack(SelectCategoryActivity.level + "")
                            .commit();

                } else if (mContext instanceof EmptyPostActivity) {
//                    ((EmptyPostActivity) mContext).backProject();
                    SelectCategoryActivity.selectProject = true;
                    filePut.putProjectIdPostEmpty(data.get(position).getProjectid());
                    filePut.putProjectNamePostEmpty(data.get(position).getProjectName());
                    SelectCategoryEmptyPostFragment fragment = new SelectCategoryEmptyPostFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.KEY_CATEGORY, fileGet.getCategoryIdPostEmpty());
                    fragment.setArguments(bundle);
//                    ((EmptyPostActivity) mContext).getSupportFragmentManager().beginTransaction()
//                            .add(R.id.container_frame_created, fragment, EmptyPostActivity.level + "")
//                            .addToBackStack(EmptyPostActivity.level + "")
//                            .commit();

                }


            });

            holderItem.imgInfoProject.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, ProjectDetailsActivity.class);
                intent.putExtra("PROJECT_KEY", data.get(position));
                baseActivityTransition.transitionTo(intent, 0);
            });

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private class ViewHolderItem extends RecyclerView.ViewHolder {

        TextView txtProjectName;
        TextView txtProjectDescription;
        TextView txtAddress;
        RelativeLayout imgInfoProject;


        public ViewHolderItem(View itemView) {
            super(itemView);
            txtProjectName = itemView.findViewById(R.id.txtProjectName);
            txtProjectDescription = itemView.findViewById(R.id.txtProjectDescription);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            imgInfoProject = itemView.findViewById(R.id.img_info_project);

        }
    }

}
