package csell.com.vn.csell.views.project.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.csell.adapter.LocationSortAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.models.Location;
import csell.com.vn.csell.models.Project;
import csell.com.vn.csell.sqlites.SQLLocations;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProjectActivity extends AppCompatActivity {

    private FancyButton btnBackNavigation;
    private FancyButton btnSaveNavigation;
    private TextView txt_input_2000;
    private EditText edtProjectName;
    private EditText edtDescription;
    private Spinner spnCity;
    private Spinner spnDistrict;
    private LocationSortAdapter adapterDistrict;
    private SQLLocations sqlLocations;
    private List<Location> lsCity;
    private List<Location> lsDistrict;
    private Location district;
    private String _city = "";
    private String _district = "";
    private int _posDistrict = 0;
    private FileSave fileGet;
    private String characters = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        Fabric.with(this, new Crashlytics());
        sqlLocations = new SQLLocations(this);
        fileGet = new FileSave(this, Constants.GET);
        lsCity = new ArrayList<>();
        lsDistrict = new ArrayList<>();
        initView();
        setupWindowAnimations();
        initEvent();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        btnBackNavigation = findViewById(R.id.btn_back_navigation);
        btnBackNavigation.setText(getString(R.string.title_back_vn));
        btnSaveNavigation = findViewById(R.id.btn_save_navigation);
        btnSaveNavigation.setVisibility(View.VISIBLE);
        btnSaveNavigation.setText(getString(R.string.done));
        TextView titleToolbar = findViewById(R.id.custom_TitleToolbar);
        edtProjectName = findViewById(R.id.edtProjectName);
        edtDescription = findViewById(R.id.edtDescription);
        titleToolbar.setText(MainActivity.mainContext.getResources().getString(R.string.text_toolbar_title_add_project));
        spnCity = findViewById(R.id.spn_city);
        spnDistrict = findViewById(R.id.spn_district);
        txt_input_2000 = findViewById(R.id.txt_input_2000);

        characters = getString(R.string.text_input_lenght_characters);
        txt_input_2000.setText("0/2000 " + characters);

        Location city = new Location();
        city.setLocation_name(getString(R.string.city));
        city.setLocation_id(-1);
        city.setParent_id(-1);
        city.setLocation_level(-1);
        lsCity.add(0, city);
        lsCity.addAll(sqlLocations.getAllCity(1));
        LocationSortAdapter adapterCity = new LocationSortAdapter(this, lsCity);
        spnCity.setAdapter(adapterCity);
        spnCity.setSelection(0);

        district = new Location();
        district.setLocation_name(getString(R.string.district));
        district.setLocation_id(-1);
        district.setParent_id(-1);
        district.setLocation_level(-1);
        lsDistrict.add(0, district);
        adapterDistrict = new LocationSortAdapter(this, lsDistrict);
        spnDistrict.setAdapter(adapterDistrict);
        spnDistrict.setSelection(0);

        Intent intent = getIntent();
        if (intent != null) {
            String projectname = intent.getStringExtra("projectName");
            int _posCity = intent.getIntExtra("positionCity", 0);
            _posDistrict = intent.getIntExtra("positionDistrict", 0);
            edtProjectName.setText(TextUtils.isEmpty(projectname) ? "" : projectname);
            spnCity.setSelection(_posCity);
        }

    }

    private void initEvent() {

        btnBackNavigation.setOnClickListener(v -> finishAfterTransition());

        btnSaveNavigation.setOnClickListener(v -> {
            if (TextUtils.isEmpty(edtProjectName.getText().toString())) {
                edtProjectName.setError(getString(R.string.text_error_input_project));
                edtProjectName.requestFocus();
                return;
            }

            edtProjectName.setError(null);
//            DatabaseReference dbReference = FirebaseDBUtil.getDatebase().getReference();
//            String key = dbReference.child(EntityFirebase.TableProjects).push().getKey();

            Project project = new Project();

            project.setProjectName(edtProjectName.getText().toString().trim());
            project.setDescription(edtDescription.getText().toString().trim() + "");
            project.setCity(_city);
            project.setDistrict(_district);

//            project.project_name = ;
//            project.description =  + "";
//            project.city = _city;
//            project.district = _district;
//            project.isPending = true;
//            project.isDeleted = false;
//            project.is_actived = true;
//            project.project_id = key;

            addProject(project);
        });

        spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
//                    _cityId = ;
                    _city = lsCity.get(position).getLocation_name();
                    lsDistrict.clear();
                    lsDistrict.add(0, district);
                    lsDistrict.addAll(sqlLocations.getAllDistrictByCity(lsCity.get(position).getLocation_id(), 2));
                    adapterDistrict = new LocationSortAdapter(AddProjectActivity.this, lsDistrict);
                    spnDistrict.setAdapter(adapterDistrict);
                    spnDistrict.setSelection(_posDistrict);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
//                    _districtId = lsDistrict.get(position).getLocationId();
                    _district = lsDistrict.get(position).getLocation_name();
                    _posDistrict = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    int size = s.toString().length();
                    txt_input_2000.setText(size + "/2000 " + characters);
                }
            }
        });
    }

    private void addProject(Project project) {
        PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        Call<JSONResponse<Project>> addProject = postAPI.addProject(project);
        addProject.enqueue(new Callback<JSONResponse<Project>>() {
            @Override
            public void onResponse(Call<JSONResponse<Project>> call, Response<JSONResponse<Project>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getSuccess() != null) {
                            if (response.body().getSuccess()) {
                                Project newProject = response.body().getData();
                                project.setProjectid(newProject.getProjectid());
                                Intent intent = new Intent();
                                intent.putExtra("projectadd", project);
                                setResult(Constants.RESULT_CODE_ADD_PROJECT, intent);
                                finishAfterTransition();
                            }
                        }

                        String msg = response.body().getError();
                        if (!TextUtils.isEmpty(msg)) {
                            Toast.makeText(AddProjectActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String msg = (String) jsonObject.get(Constants.ERROR);
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(AddProjectActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONResponse<Project>> call, Throwable t) {
                addProject(project);
            }
        });
    }


}
