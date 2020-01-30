package csell.com.vn.csell.views.csell.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import csell.com.vn.csell.R;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.csell.adapter.LanguageAdapter;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.models.Language;

public class ChangeLanguageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_change_language, container, false);
        initView(root);
        return root;
    }

    private void initView(View root) {
        RecyclerView rvList = root.findViewById(R.id.rv_list_language);
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<Language> data = new ArrayList<>();
        data.add(new Language(MainActivity.mainContext.getResources().getString(R.string.language_vn), Constants.ISO_CODE_VN));
        data.add(new Language(MainActivity.mainContext.getResources().getString(R.string.language_en), Constants.ISO_CODE_ENGLAND));
        data.add(new Language(MainActivity.mainContext.getResources().getString(R.string.language_fr), Constants.ISO_CODE_FRANCE));
        data.add(new Language(MainActivity.mainContext.getResources().getString(R.string.language_ko), Constants.ISO_CODE_KOREAN));

        LanguageAdapter mAdapter = new LanguageAdapter(getActivity(), data);
        rvList.setAdapter(mAdapter);

    }
}
