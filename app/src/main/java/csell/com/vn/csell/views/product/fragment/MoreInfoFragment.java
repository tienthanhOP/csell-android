package csell.com.vn.csell.views.product.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.Category;
import csell.com.vn.csell.models.Properties;
import csell.com.vn.csell.sqlites.SQLProperties;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.product.activity.SelectCategoryActivity;
import csell.com.vn.csell.views.product.adapter.PropertiesDynamicAdapter;

/**
 * Created by cuong.nv on 4/11/2018.
 */

public class MoreInfoFragment extends Fragment {

    public ArrayList<Category> categories;
    private TextView txtNext;
    //    private GridView gridProperties;
    private RecyclerView rvProperties;
    private CheckBox ckbInputOwnerInfo;
    private TextView txtShowGuidePrivateInfo;
    private PropertiesDynamicAdapter propertyAdapter;
    private List<Properties> arrayProperties;
    private FileSave fileGet;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayProperties = new ArrayList<>();
        categories = new ArrayList<>();
        fileGet = new FileSave(MainActivity.mainContext, Constants.GET);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_input_more_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();
    }


    private void initView(View view) {
        txtNext = view.findViewById(R.id.txtNext);
        rvProperties = view.findViewById(R.id.rv_properties);
        ckbInputOwnerInfo = view.findViewById(R.id.ckbInputOwnerInfo);
        txtShowGuidePrivateInfo = view.findViewById(R.id.txtShowGuidePrivateInfo);
//        gridProperties = view.findViewById(R.id.gridProperties);
        rvProperties.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.mainContext);
        rvProperties.setLayoutManager(layoutManager);
        SQLProperties sqlProperties = new SQLProperties(getActivity());
        arrayProperties.clear();

//        ArrayList<Properties> temp = new ArrayList<>();

        String[] cate = fileGet.getRootCategoryId().split("_");
        for (String aCate : cate) {
            if (aCate.toLowerCase().contains(Utilities.HOUSE)) {
                arrayProperties.addAll(sqlProperties.getPropertiesByCate(Utilities.HOUSE, 3));
            } else {
                arrayProperties.addAll(sqlProperties.getPropertiesByCate(aCate + "", 3));
            }

        }
        for (int j = 0; j < arrayProperties.size(); j++) {
            if (Objects.equals(arrayProperties.get(j).property_name, "projectid")) {
                arrayProperties.remove(j);
            }
        }
        Collections.sort(arrayProperties, (o1, o2) -> o1.index.compareTo(o2.index));

        propertyAdapter = new PropertiesDynamicAdapter(getActivity(), arrayProperties);
        rvProperties.setAdapter(propertyAdapter);
    }

    private void initEvent() {

        txtNext.setOnClickListener(v -> {
            if (ckbInputOwnerInfo.isChecked()) {

                PrivateInfoProductFragment fragment = new PrivateInfoProductFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                manager.popBackStack();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                transaction.add(R.id.container_frame_created, fragment, fragment.getClass().getName())
                        .addToBackStack(fragment.getClass().getName())
                        .commit();
            } else {
                if (fileGet.getRootCategoryId().contains(Utilities.SIM)) {
                    EndCreateFragment fragment = new EndCreateFragment();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    manager.popBackStack();
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    transaction.add(R.id.container_frame_created, fragment, fragment.getClass().getName())
                            .addToBackStack(fragment.getClass().getName())
                            .commit();
                } else {
                    InputDescriptionFragment fragment = new InputDescriptionFragment();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    manager.popBackStack();
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    transaction.add(R.id.container_frame_created, fragment, fragment.getClass().getName())
                            .addToBackStack(fragment.getClass().getName())
                            .commit();
                }
            }
        });

        txtShowGuidePrivateInfo.setOnClickListener(v -> Toast.makeText(getActivity(), getResources().getString(R.string.comming_soon), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((SelectCategoryActivity) getActivity()).updateTitleToolbar(getString(R.string.additional_information));
        SelectCategoryActivity.currentFragment = "MoreInfoFragment";
    }
}
