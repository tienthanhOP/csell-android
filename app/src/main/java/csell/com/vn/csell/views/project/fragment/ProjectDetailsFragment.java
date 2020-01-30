package csell.com.vn.csell.views.project.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.project.activity.ProjectDetailsActivity;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.ImageSuffix;
import csell.com.vn.csell.models.Project;
import io.fabric.sdk.android.Fabric;

import static android.graphics.Typeface.BOLD;

public class ProjectDetailsFragment extends Fragment {

    private TextView txt_project_description;
    private TextView text_field_project_investor;
    private TextView title_address;
    private TextView text_project_total_acreage_build;
    private TextView text_project_acreage_build;
    private TextView text_project_price;
    private TextView text_project_scale;
    private TextView text_project_type_development;
    private Project project;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getContext(), new Crashlytics());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        txt_project_description = view.findViewById(R.id.txt_project_description);
        text_field_project_investor = view.findViewById(R.id.text_field_project_investor);
        title_address = view.findViewById(R.id.title_address);
        text_project_total_acreage_build = view.findViewById(R.id.text_project_total_acreage_build);
        text_project_acreage_build = view.findViewById(R.id.text_project_acreage_build);
        text_project_price = view.findViewById(R.id.text_project_price);
        text_project_scale = view.findViewById(R.id.text_project_scale);
        text_project_type_development = view.findViewById(R.id.text_project_type_development);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            project = new Project();
            updateOnView();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    public void updateOnView() {

        try {
            project = ProjectDetailsActivity.project;
            ArrayList<ImageSuffix> listImagesTemp = new ArrayList<>();
            for (int i = 0; i < project.getImages().size(); i++) {
                listImagesTemp.add(new ImageSuffix(project.getImages().get(i), null));
            }
            txt_project_description.setText(project.getDescription() == null ? "" : project.getDescription());

            String address1 = project.getAddress() == null ? "-" : project.getAddress();
            String address2 = project.getInvestor() == null ? "-" : project.getInvestor();
            String address3 = project.getTotalAcreage() == null ? "-" : project.getTotalAcreage();
            String address4 = project.getAcreageBuild() == null ? "-" : project.getAcreageBuild();
            String address5 = project.getPrice() == null ? "-" : project.getPrice();
            String address6 = project.getProjectScale() == null ? "-" : project.getProjectScale();
            String address7 = project.getTypeDevelopment() == null ? "-" : project.getTypeDevelopment();

            String address = getResources().getString(R.string.Address) + " " + address1;
            String investor = getResources().getString(R.string.text_field_project_investor) + " " + address2;
            String totalAcreageBuild = getResources().getString(R.string.text_project_total_acreage_build) + " " + address3;
            String acreageBuild = getResources().getString(R.string.text_project_acreage_build) + " " + address4;
            String price = getResources().getString(R.string.text_project_price) + " " + address5;
            String projectScale = getResources().getString(R.string.text_project_scale) + " " + address6;
            String typeDelelopment = getResources().getString(R.string.text_project_type_development) + " " + address7;


            SpannableStringBuilder stringBuilder1 = new SpannableStringBuilder(address);
            stringBuilder1.setSpan(new StyleSpan(BOLD), 0,
                    getResources().getString(R.string.Address).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            title_address.setText(stringBuilder1);

            SpannableStringBuilder stringBuilder2 = new SpannableStringBuilder(investor);
            stringBuilder2.setSpan(new StyleSpan(BOLD), 0,
                    getResources().getString(R.string.text_field_project_investor).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            text_field_project_investor.setText(stringBuilder2);

            SpannableStringBuilder stringBuilder3 = new SpannableStringBuilder(totalAcreageBuild);
            stringBuilder3.setSpan(new StyleSpan(BOLD), 0,
                    getResources().getString(R.string.text_project_total_acreage_build).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            text_project_total_acreage_build.setText(stringBuilder3);

            SpannableStringBuilder stringBuilder4 = new SpannableStringBuilder(acreageBuild);
            stringBuilder4.setSpan(new StyleSpan(BOLD), 0,
                    getResources().getString(R.string.text_project_acreage_build).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            text_project_acreage_build.setText(stringBuilder4);

            SpannableStringBuilder stringBuilder5 = new SpannableStringBuilder(price);
            stringBuilder5.setSpan(new StyleSpan(BOLD), 0,
                    getResources().getString(R.string.text_project_price).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            text_project_price.setText(stringBuilder5);

            SpannableStringBuilder stringBuilder6 = new SpannableStringBuilder(projectScale);
            stringBuilder6.setSpan(new StyleSpan(BOLD), 0,
                    getResources().getString(R.string.text_project_scale).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            text_project_scale.setText(stringBuilder6);

            SpannableStringBuilder stringBuilder7 = new SpannableStringBuilder(typeDelelopment);
            stringBuilder7.setSpan(new StyleSpan(BOLD), 0,
                    getResources().getString(R.string.text_project_type_development).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            text_project_type_development.setText(stringBuilder7);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }

    }
}
