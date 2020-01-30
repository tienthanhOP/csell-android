package csell.com.vn.csell.views.project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.ProjectsController;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.product.fragment.SelectRootCategoryFragment;
import csell.com.vn.csell.views.project.activity.AddProjectActivity;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.csell.adapter.LocationSortAdapter;
import csell.com.vn.csell.views.project.adapter.ProjectAdapter;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.models.Location;
import csell.com.vn.csell.models.Project;
import csell.com.vn.csell.sqlites.SQLLocations;
import io.fabric.sdk.android.Fabric;

/**
 * Created by cuong.nv on 4/5/2018.
 */

public class ProjectFragment extends Fragment implements ProjectsController.OnGetListProjectsListener {

    private RecyclerView rvProjects;
    private EditText edtSearchProject;
    private LinearLayout from_loading;
    private Button btnAddProject;
    private LinearLayout fromCreateProject;
    private List<Project> listProject;
    private List<Project> listProjectReadOnly;
    private List<Location> lsCity;
    private List<Location> lsDistrict;
    private ProjectAdapter projectAdapter;
    private Spinner spnCity;
    private Spinner spnDistrict;
    private LocationSortAdapter adapterDistrict;
    private SQLLocations sqlLocations;
    private String _city = "";
    private String _district = "";
    private int posCity = 0;
    private int posDistrict = 0;
    private Location location;
    private FileSave fileGet;
    private Timer timer = new Timer();
    private final long DELAY = 500;
    private String search = "";
    private BaseActivityTransition baseActivityTransition;
    private ProjectsController projectsController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getContext(), new Crashlytics());
        listProject = new ArrayList<>();
        lsCity = new ArrayList<>();
        lsDistrict = new ArrayList<>();
        fileGet = new FileSave(getActivity(), Constants.GET);
        listProjectReadOnly = new ArrayList<>();
        sqlLocations = new SQLLocations(getActivity());
        projectsController = new ProjectsController(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_project, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return root;
    }

    private void initView(View root) {
        baseActivityTransition = new BaseActivityTransition(getActivity());

        edtSearchProject = root.findViewById(R.id.edt_input_project);
        spnCity = root.findViewById(R.id.spn_city);
        spnDistrict = root.findViewById(R.id.spn_district);
        fromCreateProject = root.findViewById(R.id.from_create_project);
        from_loading = root.findViewById(R.id.from_loading);
        from_loading.setVisibility(View.GONE);
        btnAddProject = root.findViewById(R.id.btnAddProject);
        rvProjects = root.findViewById(R.id.rvProjects);
        fromCreateProject.setVisibility(View.GONE);
        rvProjects.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvProjects.setLayoutManager(mLayoutManager);
        listProjectReadOnly.clear();

        projectAdapter = new ProjectAdapter(getActivity(), listProject);
        rvProjects.setAdapter(projectAdapter);

        lsCity.clear();

        location = new Location();
        location.setLocation_name(MainActivity.mainContext.getResources().getString(R.string.all));
        location.setLocation_id(-1);
        location.setParent_id(-1);
        location.setLocation_level(-1);

        lsCity.add(0, location);
        lsCity.addAll(sqlLocations.getAllCity(1));
        LocationSortAdapter adapterCity = new LocationSortAdapter(getActivity(), lsCity);
        String city = fileGet.getUserCityName();
        _city = city;
        int index = 0;
        for (int i = 0; i < lsCity.size(); i++) {
            if (city.toLowerCase().equals(lsCity.get(i).getLocation_name().toLowerCase())) {
                index = i;
                break;
            }
        }
        spnCity.setAdapter(adapterCity);
        spnCity.setSelection(index);

        lsDistrict.add(0, location);
        adapterDistrict = new LocationSortAdapter(getActivity(), lsDistrict);
        spnDistrict.setAdapter(adapterDistrict);
        spnDistrict.setEnabled(false);

        getProject();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SelectRootCategoryFragment.isProject = false;
        initView(view);
        initEvent();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initEvent() {

        spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int _id = lsCity.get(position).getLocation_id();
                if (position > 0) {
                    posCity = position;
                    spnDistrict.setEnabled(true);
                    _city = lsCity.get(position).getLocation_name();
                    _district = "";
                    lsDistrict.clear();
                    lsDistrict.add(0, location);
                    lsDistrict.addAll(sqlLocations.getAllDistrictByCity(lsCity.get(position).getLocation_id(), 2));
                    adapterDistrict = new LocationSortAdapter(getActivity(), lsDistrict);
                    spnDistrict.setAdapter(adapterDistrict);
                    from_loading.setVisibility(View.VISIBLE);
                    findProjectOnList(0, search);

                } else {
                    if (_id == -1) {
                        lsDistrict.clear();
                        lsDistrict.add(0, location);
                        adapterDistrict.notifyDataSetChanged();
                        spnDistrict.setEnabled(false);
                        _district = "";
                        from_loading.setVisibility(View.VISIBLE);
                        findProjectOnList(0, search);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int _id = lsCity.get(position).getLocation_id();
                if (position > 0) {
                    posDistrict = position;
                    _district = lsDistrict.get(position).getLocation_name();
                    from_loading.setVisibility(View.VISIBLE);
                    findProjectOnList(0, search);
                } else {
                    if (_id == -1) {
                        if (!spnDistrict.isEnabled()) return;
                        from_loading.setVisibility(View.VISIBLE);
                        findProjectOnList(0, search);
                        _district = "";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edtSearchProject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                from_loading.setVisibility(View.VISIBLE);
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        findProjectOnList(0, s.toString());
                        search = s.toString();
                    }
                }, DELAY);
            }
        });

        btnAddProject.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddProjectActivity.class);
            intent.putExtra("projectName", edtSearchProject.getText().toString());
            intent.putExtra("positionCity", posCity);
            intent.putExtra("positionDistrict", posDistrict);
            baseActivityTransition.transitionTo(intent, Constants.RESULT_CODE_ADD_PROJECT);
        });

        rvProjects.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                if (mLayoutManager.findLastCompletelyVisibleItemPosition() == listProject.size() - 1) {
//                    if (listProject.size() > 15){
//                        findProjectOnList(listProject.size(), search);
//                    }

//                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.RESULT_CODE_ADD_PROJECT) {

            if (data != null) {
                Project project = (Project) data.getSerializableExtra("projectadd");
                if (project != null) {
                    listProject.add(0, project);
                    projectAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void getProject() {
        try {
            from_loading.setVisibility(View.VISIBLE);
            findProjectOnList(0, "");
        } catch (Exception e) {
            from_loading.setVisibility(View.GONE);
        }
    }

    private void findProjectOnList(int skip, String search) {
        try {
            listProject.clear();
            projectsController.getListProject(skip, search.isEmpty() ? "" : search, _city, _district, false, this);
        } catch (Exception e) {
            Crashlytics.logException(e);
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
        }
    }

    @Override
    public void onGetListProjectsSuccess(List<Project> projects, int skip, boolean isFilter) {
        try {
            listProject.clear();
            if (projects.size() == 0) {
                fromCreateProject.setVisibility(View.VISIBLE);
            } else {
                fromCreateProject.setVisibility(View.GONE);
            }

            listProject.addAll(projects);
            projectAdapter.notifyDataSetChanged();
            from_loading.setVisibility(View.GONE);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGetListProjectsFailure() {
        from_loading.setVisibility(View.GONE);
    }

    @Override
    public void onConnectListProjectsFailure() {
        from_loading.setVisibility(View.GONE);
    }
}
