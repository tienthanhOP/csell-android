package csell.com.vn.csell.views.product.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.Category;
import csell.com.vn.csell.models.ImageSuffix;
import csell.com.vn.csell.models.Location;
import csell.com.vn.csell.models.Properties;
import csell.com.vn.csell.models.PropertyValue;
import csell.com.vn.csell.sqlites.SQLCitys;
import csell.com.vn.csell.sqlites.SQLDistricts;
import csell.com.vn.csell.sqlites.SQLLocations;
import csell.com.vn.csell.sqlites.SQLProjects;
import csell.com.vn.csell.sqlites.SQLProperties;
import csell.com.vn.csell.sqlites.SQLStreets;
import csell.com.vn.csell.sqlites.SQLWards;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.csell.adapter.LocationSortAdapter;
import csell.com.vn.csell.views.product.activity.SelectCategoryActivity;
import csell.com.vn.csell.views.product.adapter.CurrencyAdapter;
import csell.com.vn.csell.views.product.adapter.PropertiesDynamicAdapter;
import io.fabric.sdk.android.Fabric;

/**
 * Created by cuong.nv on 4/11/2018.
 */

public class BasicInfoFragment extends Fragment {
    public List<Properties> arrayProperties;
    public PropertiesDynamicAdapter propertiesDynamicAdapter;
    public ArrayList<Category> categories;
    public ArrayList<PropertyValue> lsCurrency;
    View inflated;
    HashMap<String, String> hashMap = new HashMap<>();
    private RecyclerView rvProperties;
    private TextView txtNext;
    private ScrollView scrollView;
    private ImageView imgBackground1;
    private ImageView imgBackground2;
    private ImageView imgBackground3;
    private ImageView imgBackground4;
    private ImageView imgBackground5;
    private ImageView background;
    private TextView txtCountCharacter;
    private EditText edtProductName;
    private EditText edtPrice;
    private EditText edtSimDescription;
    private Spinner spnTypePrice;
    private LinearLayout fromInputSim;
    private LinearLayout fromInputPrice;
    private TextView txtShowGuidePrivateInfo;
    private LinearLayout layout_property;
    //    private ImageView img_arrow_down;
    private LinearLayout fromLocation;
    private List<Location> lsCity;
    private List<Location> lsDistrict;
    private List<Location> lsWards;
    private List<Location> lsStreets;
    private List<Location> lsProjects;
    private Spinner spnCity;
    private Spinner spnDistrict;
    private LocationSortAdapter adapterCity;
    private LocationSortAdapter adapterDistrict;
    private LocationSortAdapter adapterWards;
    private LocationSortAdapter adapterStreet;
    private LocationSortAdapter adapterProject;
    private SQLLocations sqlLocations;
    private SQLCitys sqlCitys;
    private SQLDistricts sqlDistricts;
    private SQLWards sqlWards;
    private SQLStreets sqlStreets;
    private SQLProjects sqlProjects;
    private Location district0;
    private FileSave fileGet;
    private String category = "";
    private String districtSelect = "";
    private SQLProperties sqlProperties;
    private ViewStub stub;
    private Spinner spnWards;
    private Spinner spnStreets;
    private Spinner spnProjects;
    private TextInputEditText edtAddress;
    private RadioGroup radioGroupForm;
    private EditText edtManufactureYear;
    private RadioGroup radioGroupOrigin;
    private RadioGroup radioGroupStatus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getContext(), new Crashlytics());
        arrayProperties = new ArrayList<>();
        sqlLocations = new SQLLocations(getActivity());
        sqlCitys = new SQLCitys(getActivity());
        sqlDistricts = new SQLDistricts(getActivity());
        sqlWards = new SQLWards(getActivity());
        sqlStreets = new SQLStreets(getActivity());
        sqlProjects = new SQLProjects(getActivity());
        categories = new ArrayList<>();
        lsCurrency = new ArrayList<>();
        lsCity = new ArrayList<>();
        lsDistrict = new ArrayList<>();
        lsWards = new ArrayList<>();
        lsStreets = new ArrayList<>();
        lsProjects = new ArrayList<>();
        fileGet = new FileSave(getActivity(), Constants.GET);
        sqlProperties = new SQLProperties(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.v2_fragment_input_basic_infomations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View view) {
        try {
            stub = view.findViewById(R.id.layout_stub);
            rvProperties = view.findViewById(R.id.gridProperties);
            scrollView = view.findViewById(R.id.scrollView);
            rvProperties.setHasFixedSize(true);
            rvProperties.setLayoutManager(new LinearLayoutManager(getActivity()));

            txtShowGuidePrivateInfo = view.findViewById(R.id.txtShowGuidePrivateInfo);
            layout_property = view.findViewById(R.id.layout_property);

            txtNext = view.findViewById(R.id.txtNext);
            txtCountCharacter = view.findViewById(R.id.txtCountCharacter);
            fromLocation = view.findViewById(R.id.from_location);
            imgBackground1 = view.findViewById(R.id.img_color_1);
            imgBackground2 = view.findViewById(R.id.img_color_2);
            imgBackground3 = view.findViewById(R.id.img_color_3);
            imgBackground4 = view.findViewById(R.id.img_color_4);
            imgBackground5 = view.findViewById(R.id.img_color_5);
            background = view.findViewById(R.id.image_bg);

            edtProductName = view.findViewById(R.id.edtProductName);
            edtPrice = view.findViewById(R.id.edt_price);
            spnTypePrice = view.findViewById(R.id.spnTypePrice);
            fromInputSim = view.findViewById(R.id.from_input_sim);
            fromInputPrice = view.findViewById(R.id.from_input_price);
            edtSimDescription = view.findViewById(R.id.edt_sim_title);
            checkOnView();

        } catch (Exception e) {
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void checkOnView() {
        lsCity.addAll(sqlCitys.getAllCity());
        lsDistrict.addAll(sqlDistricts.getDistrictsByCity(lsCity.get(0).getLocation_id()));
        lsWards.addAll(sqlWards.getWardByDistrict(lsDistrict.get(0).getLocation_id()));
        lsStreets.addAll(sqlStreets.getStreetsByDistrict(lsDistrict.get(0).getLocation_id()));

        adapterCity = new LocationSortAdapter(getActivity(), lsCity);
        adapterDistrict = new LocationSortAdapter(getActivity(), lsDistrict);
        adapterWards = new LocationSortAdapter(getActivity(), lsWards);
        adapterStreet = new LocationSortAdapter(getActivity(), lsStreets);

        lsCurrency = Utilities.CURRENCY(getContext());
        CurrencyAdapter adapter = new CurrencyAdapter(getActivity(), lsCurrency);
        spnTypePrice.setSelection(0, true);
        spnTypePrice.setAdapter(adapter);

        if (!TextUtils.isEmpty(fileGet.getRootCategoryId())) {
            String cateRoot = fileGet.getRootCategoryId().split("_")[0] + "";
            switch (cateRoot) {
                case "land": {
                    stub.setLayoutResource(R.layout.view_stub_land);
                    inflated = stub.inflate();

                    radioGroupForm = inflated.findViewById(R.id.radio_form);

                    edtAddress = inflated.findViewById(R.id.edt_address);

                    spnCity = inflated.findViewById(R.id.spn_city);
                    spnDistrict = inflated.findViewById(R.id.spn_district);
                    spnWards = inflated.findViewById(R.id.spn_wards);
                    spnStreets = inflated.findViewById(R.id.spn_streets);
                    spnProjects = inflated.findViewById(R.id.spn_projects);

                    spnCity.setAdapter(adapterCity);
                    spnCity.setSelection(0);

                    spnDistrict.setAdapter(adapterDistrict);
                    spnDistrict.setSelection(0);

                    spnWards.setAdapter(adapterWards);
                    spnWards.setSelection(0);

                    spnStreets.setAdapter(adapterStreet);
                    spnStreets.setSelection(0);

                    edtAddress.setText(
                            ((Location) spnStreets.getSelectedItem()).getLocation_name() + ", " +
                                    ((Location) spnWards.getSelectedItem()).getLocation_name() + ", " +
                                    ((Location) spnDistrict.getSelectedItem()).getLocation_name() + ", " +
                                    ((Location) spnCity.getSelectedItem()).getLocation_name()
                    );
                    hashMap.put("code", lsCity.get(0).getLocation_id(2));
                    hashMap.put("name", lsCity.get(0).getLocation_name());
                    SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_PROVINCE, hashMap);

                    hashMap.put("code", lsDistrict.get(0).getLocation_id(3));
                    hashMap.put("name", lsDistrict.get(0).getLocation_name());
                    SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_DISTRICT, hashMap);
                    hashMap.put("code", lsWards.get(0).getLocation_id(5));
                    hashMap.put("name", lsWards.get(0).getLocation_name());
                    SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_WARD, hashMap);
                    hashMap.put("code", lsStreets.get(0).getLocation_id(2));
                    hashMap.put("name", lsStreets.get(0).getLocation_name());
                    SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_STREET, hashMap);
                    spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            hashMap.put("code", lsCity.get(0).getLocation_id(2));
                            hashMap.put("name", lsCity.get(0).getLocation_name());
                            SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_PROVINCE, hashMap);
                            spnDistrict.setEnabled(true);
                            lsDistrict.clear();
                            lsDistrict.addAll(sqlDistricts.getDistrictsByCity(lsCity.get(position).getLocation_id()));
                            adapterDistrict.notifyDataSetChanged();
                            spnDistrict.setSelection(0);

                            spnWards.setEnabled(true);
                            lsWards.clear();
                            lsWards.addAll(sqlWards.getWardByDistrict(lsDistrict.get(0).getLocation_id()));
                            adapterWards.notifyDataSetChanged();
                            spnWards.setSelection(0);

                            spnStreets.setEnabled(true);
                            lsStreets.clear();
                            lsStreets.addAll(sqlStreets.getStreetsByDistrict(lsDistrict.get(0).getLocation_id()));
                            adapterStreet.notifyDataSetChanged();
                            spnStreets.setSelection(0);

                            edtAddress.setText(
                                    ((Location) spnStreets.getSelectedItem()).getLocation_name() + ", " +
                                            ((Location) spnWards.getSelectedItem()).getLocation_name() + ", " +
                                            ((Location) spnDistrict.getSelectedItem()).getLocation_name() + ", " +
                                            ((Location) spnCity.getSelectedItem()).getLocation_name()
                            );
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    spnDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            hashMap.put("code", lsDistrict.get(0).getLocation_id(3));
                            hashMap.put("name", lsDistrict.get(0).getLocation_name());
                            SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_DISTRICT, hashMap);

                            spnWards.setEnabled(true);
                            lsWards.clear();
                            lsWards.addAll(sqlWards.getWardByDistrict(lsDistrict.get(i).getLocation_id()));
                            adapterWards.notifyDataSetChanged();
                            spnWards.setSelection(0);

                            spnStreets.setEnabled(true);
                            lsStreets.clear();
                            lsStreets.addAll(sqlStreets.getStreetsByDistrict(lsDistrict.get(i).getLocation_id()));
                            adapterStreet.notifyDataSetChanged();
                            spnStreets.setSelection(0);

                            edtAddress.setText(
                                    ((Location) spnStreets.getSelectedItem()).getLocation_name() + ", " +
                                            ((Location) spnWards.getSelectedItem()).getLocation_name() + ", " +
                                            ((Location) spnDistrict.getSelectedItem()).getLocation_name() + ", " +
                                            ((Location) spnCity.getSelectedItem()).getLocation_name()
                            );
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    spnWards.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            hashMap.put("code", lsWards.get(0).getLocation_id(5));
                            hashMap.put("name", lsWards.get(0).getLocation_name());
                            SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_WARD, hashMap);
                            edtAddress.setText(
                                    ((Location) spnStreets.getSelectedItem()).getLocation_name() + ", " +
                                            ((Location) spnWards.getSelectedItem()).getLocation_name() + ", " +
                                            ((Location) spnDistrict.getSelectedItem()).getLocation_name() + ", " +
                                            ((Location) spnCity.getSelectedItem()).getLocation_name()
                            );
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    spnStreets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            hashMap.put("code", lsStreets.get(0).getLocation_id(2));
                            hashMap.put("name", lsStreets.get(0).getLocation_name());
                            SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_STREET, hashMap);
                            edtAddress.setText(
                                    ((Location) spnStreets.getSelectedItem()).getLocation_name() + ", " +
                                            ((Location) spnWards.getSelectedItem()).getLocation_name() + ", " +
                                            ((Location) spnDistrict.getSelectedItem()).getLocation_name() + ", " +
                                            ((Location) spnCity.getSelectedItem()).getLocation_name()
                            );
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    break;
                }
                case "car": {
                    stub.setLayoutResource(R.layout.view_stub_car);
                    inflated = stub.inflate();

                    edtAddress = inflated.findViewById(R.id.edt_address);

                    spnCity = inflated.findViewById(R.id.spn_city);
                    spnDistrict = inflated.findViewById(R.id.spn_district);

                    edtManufactureYear = inflated.findViewById(R.id.edt_manufacture_year);
                    radioGroupOrigin = inflated.findViewById(R.id.radio_origin);
                    radioGroupStatus = inflated.findViewById(R.id.radio_status);

                    spnCity.setAdapter(adapterCity);
                    spnCity.setSelection(0);

                    spnDistrict.setAdapter(adapterDistrict);
                    spnDistrict.setSelection(0);

                    edtAddress.setText(
                            ((Location) spnDistrict.getSelectedItem()).getLocation_name() + ", " +
                                    ((Location) spnCity.getSelectedItem()).getLocation_name()
                    );

                    hashMap.put("code", lsCity.get(0).getLocation_id(2));
                    hashMap.put("name", lsCity.get(0).getLocation_name());
                    SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_PROVINCE, hashMap);

                    hashMap.put("code", lsDistrict.get(0).getLocation_id(3));
                    hashMap.put("name", lsDistrict.get(0).getLocation_name());
                    SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_DISTRICT, hashMap);
                    spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            hashMap.put("code", lsCity.get(0).getLocation_id(2));
                            hashMap.put("name", lsCity.get(0).getLocation_name());
                            SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_PROVINCE, hashMap);

                            spnDistrict.setEnabled(true);
                            lsDistrict.clear();
                            lsDistrict.addAll(sqlDistricts.getDistrictsByCity(lsCity.get(position).getLocation_id()));
                            adapterDistrict.notifyDataSetChanged();
                            spnDistrict.setSelection(0);

                            edtAddress.setText(
                                    ((Location) spnDistrict.getSelectedItem()).getLocation_name() + ", " +
                                            ((Location) spnCity.getSelectedItem()).getLocation_name()
                            );
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    spnDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            hashMap.put("code", lsDistrict.get(0).getLocation_id(3));
                            hashMap.put("name", lsDistrict.get(0).getLocation_name());
                            SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_DISTRICT, hashMap);

                            edtAddress.setText(
                                    ((Location) spnDistrict.getSelectedItem()).getLocation_name() + ", " +
                                            ((Location) spnCity.getSelectedItem()).getLocation_name()
                            );
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    break;
                }
                case "sim": {
                    stub.setLayoutResource(R.layout.view_stub_sim);
                    View inflated = stub.inflate();

                    edtAddress = inflated.findViewById(R.id.edt_address);

                    spnCity = inflated.findViewById(R.id.spn_city);
                    spnDistrict = inflated.findViewById(R.id.spn_district);

                    spnCity.setAdapter(adapterCity);
                    spnCity.setSelection(0);

                    spnDistrict.setAdapter(adapterDistrict);
                    spnDistrict.setSelection(0);

                    edtAddress.setText(
                            ((Location) spnDistrict.getSelectedItem()).getLocation_name() + ", " +
                                    ((Location) spnCity.getSelectedItem()).getLocation_name()
                    );

                    hashMap.put("code", lsCity.get(0).getLocation_id(2));
                    hashMap.put("name", lsCity.get(0).getLocation_name());
                    SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_PROVINCE, hashMap);

                    hashMap.put("code", lsDistrict.get(0).getLocation_id(3));
                    hashMap.put("name", lsDistrict.get(0).getLocation_name());
                    SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_DISTRICT, hashMap);

                    spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            hashMap.put("code", lsCity.get(0).getLocation_id(2));
                            hashMap.put("name", lsCity.get(0).getLocation_name());
                            SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_PROVINCE, hashMap);

                            spnDistrict.setEnabled(true);
                            lsDistrict.clear();
                            lsDistrict.addAll(sqlDistricts.getDistrictsByCity(lsCity.get(position).getLocation_id()));
                            adapterDistrict.notifyDataSetChanged();
                            spnDistrict.setSelection(0);

                            edtAddress.setText(
                                    ((Location) spnDistrict.getSelectedItem()).getLocation_name() + ", " +
                                            ((Location) spnCity.getSelectedItem()).getLocation_name()
                            );
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    spnDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            hashMap.put("code", lsDistrict.get(0).getLocation_id(3));
                            hashMap.put("name", lsDistrict.get(0).getLocation_name());
                            SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_DISTRICT, hashMap);

                            edtAddress.setText(
                                    ((Location) spnDistrict.getSelectedItem()).getLocation_name() + ", " +
                                            ((Location) spnCity.getSelectedItem()).getLocation_name()
                            );
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    break;
                }
            }
        }

        category = fileGet.getRootCategoryId();
        if (category.contains(Utilities.SIM)) {
            defaultSimImage(MainActivity.mainContext.getResources().getString(R.string.bg_sim_url_1));

            if (category.contains(Utilities.SIM_LIST_MONTH)) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH) + 1;
                int year = calendar.get(Calendar.YEAR);
                String nameDateListSim = "[" + day + "-" + month + "-" + year + "] ";
                edtProductName.setText(nameDateListSim);
                edtSimDescription.setEnabled(true);
                fromInputSim.setVisibility(View.VISIBLE);
                fromInputPrice.setVisibility(View.GONE);
                layout_property.setVisibility(View.GONE);
//                edtSimDescription.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
            } else {
                edtSimDescription.setEnabled(true);
                fromInputSim.setVisibility(View.VISIBLE);
                fromInputPrice.setVisibility(View.VISIBLE);
            }
        } else {
            layout_property.setVisibility(View.VISIBLE);
            edtSimDescription.setEnabled(false);
            fromInputSim.setVisibility(View.GONE);
            fromInputPrice.setVisibility(View.VISIBLE);
        }
    }

    private void initEvent() {
        edtProductName.addTextChangedListener(new TextWatcher() {
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
                    txtCountCharacter.setText(s.toString().length() + "/" + 100);
                } else {
                    txtCountCharacter.setText("0/100");
                }
            }
        });

        txtNext.setOnClickListener(v -> {
            try {
                View lastChild = scrollView.getChildAt(scrollView.getChildCount() - 1);
                int bottom = lastChild.getBottom() + scrollView.getPaddingBottom();
                int sy = scrollView.getScrollY();
                int sh = scrollView.getHeight();
                int delta = bottom - (sy + sh);
                if (delta < 170) {
                    if (edtProductName.getText().toString().trim().isEmpty()) {
                        edtProductName.setError(MainActivity.mainContext.getResources().getString(R.string.please_enter_itle));
                        edtProductName.requestFocus();
                        return;
                    } else {
                        SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_NAME, edtProductName.getText().toString());
                    }

                    if (TextUtils.isEmpty(edtPrice.getText().toString())) {
                        SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_PRICE, 0);
                    } else {
                        long price = Utilities.formatCurrencySplit(edtPrice.getText().toString(), ",");
                        SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_PRICE, price);
                    }

                    SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_CURRENCY, ((PropertyValue) spnTypePrice.getSelectedItem()).value);

                    String address = edtAddress.getText().toString().trim();
                    if (!TextUtils.isEmpty(address)) {
                        SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_ADDRESS, address);
                        SelectCategoryActivity.paramsProperty.put(EntityAPI.FIELD_ADDRESS, address);
                    } else {
                        Toast.makeText(getContext(), "" + getString(R.string.text_warning_address), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    edtProductName.setError(null);
                    Utilities.hideSoftKeyboard(Objects.requireNonNull(getActivity()));

                    if (category.startsWith(Utilities.SIM)) {
                        if (edtSimDescription.getText().toString().isEmpty()) {
                            edtSimDescription.setError(MainActivity.mainContext.getResources().getString(R.string.description_can_not_empty));
                            edtSimDescription.requestFocus();
                            return;
                        } else {
                            int countWord = Utilities.countWords(edtSimDescription.getText().toString().trim());
                            if (countWord < 10) {
                                edtSimDescription.setError(MainActivity.mainContext.getResources().
                                        getString(R.string.Description_at_least_10_words));
                                edtSimDescription.requestFocus();
                                return;
                            }
                        }
                        edtSimDescription.setError(null);
                        SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_DESCRIPTION, edtSimDescription.getText().toString());

                        if (category.startsWith(Utilities.SIM_LIST_MONTH)) {
//                            SIM trong thang den thang man hinh cuoi
                            EndCreateFragment fragment = new EndCreateFragment();
                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            manager.popBackStack();
                            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                            transaction.add(R.id.container_frame_created, fragment, fragment.getClass().getName())
                                    .addToBackStack(fragment.getClass().getName())
                                    .commit();
                        } else {
                            MoreInfoFragment moreInfoFragment = new MoreInfoFragment();
                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            manager.popBackStack();
                            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                            transaction.add(R.id.container_frame_created, moreInfoFragment)
                                    .addToBackStack(moreInfoFragment.getClass().getSimpleName())
                                    .commit();
                        }
                    } else if (category.startsWith(Utilities.LAND)) {
                        int checkedRadioId = radioGroupForm.getCheckedRadioButtonId();
                        if (checkedRadioId != -1) {
                            SelectCategoryActivity.paramsProperty.put(EntityAPI.FIELD_FORMALITY,
                                    ((RadioButton) inflated.findViewById(checkedRadioId)).getText().toString());
                        }

                        SelectCategoryActivity.paramsProperty.put(EntityAPI.FIELD_ACREAGE_TYPE, "m2");

                        String acreageString = ((TextInputEditText) inflated.findViewById(R.id.edt_acreage)).getText().toString();
                        Long acreage = 0l;
                        if (!acreageString.equals("")) {
                            acreage = Long.parseLong(acreageString);
                        }
                        SelectCategoryActivity.paramsProperty.put(EntityAPI.FIELD_ACREAGE, acreage);

                        MoreInfoFragment moreInfoFragment = new MoreInfoFragment();
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        manager.popBackStack();
                        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        transaction.add(R.id.container_frame_created, moreInfoFragment)
                                .addToBackStack(moreInfoFragment.getClass().getSimpleName())
                                .commit();
                    } else if (category.startsWith(Utilities.CAR)) {
                        int checkedRadioId1 = radioGroupOrigin.getCheckedRadioButtonId();
                        if (checkedRadioId1 != -1) {
                            SelectCategoryActivity.paramsProperty.put(EntityAPI.FIELD_ORIGIN,
                                    ((RadioButton) inflated.findViewById(checkedRadioId1)).getText().toString());
                        }

                        int checkedRadioId2 = radioGroupStatus.getCheckedRadioButtonId();
                        if (checkedRadioId2 != -1) {
                            SelectCategoryActivity.paramsProperty.put(EntityAPI.FIELD_STATUS,
                                    ((RadioButton) inflated.findViewById(checkedRadioId2)).getText().toString());
                        }

                        String manufactureYear = ((EditText) inflated.findViewById(R.id.edt_manufacture_year)).getText().toString();
                        if (!manufactureYear.equals("")) {
                            Long year = Long.parseLong(manufactureYear);
                            SelectCategoryActivity.paramsProperty.put(EntityAPI.FIELD_MANUFACTURE_YEAR, year);
                        }

                        MoreInfoFragment moreInfoFragment = new MoreInfoFragment();
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        manager.popBackStack();
                        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        transaction.add(R.id.container_frame_created, moreInfoFragment)
                                .addToBackStack(moreInfoFragment.getClass().getSimpleName())
                                .commit();
                    }
                } else {
                    scrollView.smoothScrollBy(0, delta);
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            }
        });

        spnTypePrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!TextUtils.isEmpty(edtPrice.getText().toString()) || !edtPrice.equals("0")) {
                    if (position > 0) {
                        SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_CURRENCY, lsCurrency.get(position).value);
                    } else {
                        SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_CURRENCY, lsCurrency.get(position).value);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edtPrice.addTextChangedListener(onTextChangedListener());

        imgBackground1.setOnClickListener(v -> {
            background.setImageResource(R.drawable.bg_3);
            defaultSimImage(MainActivity.mainContext.getResources().getString(R.string.bg_sim_url_1));

        });
        imgBackground2.setOnClickListener(v -> {
            background.setImageResource(R.drawable.bg_4);
            defaultSimImage(MainActivity.mainContext.getResources().getString(R.string.bg_sim_url_2));
        });
        imgBackground3.setOnClickListener(v -> {
            background.setImageResource(R.drawable.bg_5);
            defaultSimImage(MainActivity.mainContext.getResources().getString(R.string.bg_sim_url_3));
        });
        imgBackground4.setOnClickListener(v -> {
            background.setImageResource(R.drawable.bg_2);
            defaultSimImage(MainActivity.mainContext.getResources().getString(R.string.bg_sim_url_4));
        });
        imgBackground5.setOnClickListener(v -> {
            background.setImageResource(R.drawable.bg_1);
            defaultSimImage(MainActivity.mainContext.getResources().getString(R.string.bg_sim_url_5));
        });
    }

    private void defaultSimImage(String url) {
        ArrayList<ImageSuffix> lsImageUrl = new ArrayList<>();
        lsImageUrl.add(new ImageSuffix(url, null));
        SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_IMAGES, lsImageUrl);
    }

    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) return;
                edtPrice.removeTextChangedListener(this);
                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }

                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);
                    edtPrice.setText(formattedString);
                    edtPrice.setSelection(edtPrice.getText().length());

                } catch (NumberFormatException nfe) {
                    edtPrice.setText(s.toString());
                    edtPrice.setSelection(edtPrice.getText().length());
                }

                edtPrice.addTextChangedListener(this);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        ((SelectCategoryActivity) getActivity()).updateTitleToolbar(getString(R.string.basic_information));
        SelectCategoryActivity.currentFragment = "BasicInfoFragment";
    }
}
