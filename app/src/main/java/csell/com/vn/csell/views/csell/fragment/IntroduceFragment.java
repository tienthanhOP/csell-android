package csell.com.vn.csell.views.csell.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.csell.activity.SettingActivity;

public class IntroduceFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_introduce, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        TextView tvVer = rootView.findViewById(R.id.tv_ver);
        SettingActivity.tvTitle.setText(MainActivity.mainContext.getResources().getString(R.string.title_introduce));
        ImageView img = rootView.findViewById(R.id.img);
        GlideApp.with(Objects.requireNonNull(getActivity()))
                .load("http://www.businessnewsdaily.com/images/i/000/007/874/original/free-business-plan-templates.jpg?1421856207")
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)
                .into(img);
        try {
            String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            tvVer.setText(versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
