package csell.com.vn.csell.mycustoms;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import csell.com.vn.csell.R;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.social.adapter.JobFilterAdapter;
import csell.com.vn.csell.views.csell.adapter.LocationSortAdapter;
import csell.com.vn.csell.views.social.adapter.PropertiesFilterDynamicAdapter;
import csell.com.vn.csell.views.social.adapter.SelectPriceInFilterAdapter;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.models.Category;
import csell.com.vn.csell.models.Location;
import csell.com.vn.csell.models.PropertiesFilter;
import csell.com.vn.csell.sqlites.SQLCategories;
import csell.com.vn.csell.sqlites.SQLLocations;
import csell.com.vn.csell.sqlites.SQLPropertiesFilter;

public class FilterSocialDialog extends BottomSheetDialog {

    private TextView txtSelectPrice;
    private RecyclerView recyclerViewJob;
    private RecyclerView recyclerViewProperties;
    private AlertDialog dialogSelectPrice;
    private Context mContext;
    private int pos = -1;
    private List<String> lstPrice;
    private JobFilterAdapter adapterJob;
    private List<Location> lsCity;
    private List<Location> lsDistrict;
    private Spinner spnCity;
    private Spinner spnDistrict;
    private Location city;
    private Location district;
    private SQLLocations sqlLocations;
    private LocationSortAdapter adapterCity;
    private LocationSortAdapter adapterDistrict;
    private FileSave fileGet;
    private String districtSelect = "";
    private ArrayList<Category> lstJob;
    private Button btnSkipFilter;
    private Button btnApplyFilter;
    private PropertiesFilterDynamicAdapter adapterPropertiesDynamic;
    private static List<PropertiesFilter> dataProperties = new ArrayList<>();
    private static String filterCity;
    private static String filterDistrict;
    private static String filterJob;
    private static String filterPrice;
    private SQLPropertiesFilter sqlPropertiesFilter;

    public FilterSocialDialog(@NonNull Context context) {
        super(context, R.style.full_screen_dialog_no_status_bar);
        this.mContext = context;

        setContentView(R.layout.dialog_filter_social);

        //custom position dialog
        Window dialogWindow = getWindow();
        assert dialogWindow != null;
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        lp.windowAnimations = R.style.DialogCreateAnimation;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // Width
        lp.height = WindowManager.LayoutParams.MATCH_PARENT; // Height
        dialogWindow.setAttributes(lp);

        Objects.requireNonNull(this.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        initView();
        initEvent();
    }

    @SuppressLint("SetTextI18n")
    private void initEvent() {
        txtSelectPrice.setOnClickListener(v -> {
            dialogSelectPrice();
        });

        btnSkipFilter.setOnClickListener(v -> {
            filterCity = "";
            filterDistrict = "";
            filterJob = "";
            filterPrice = "";
            dismiss();
        });

        btnApplyFilter.setOnClickListener(v -> {

        });

        spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    spnDistrict.setEnabled(true);
                    lsDistrict.clear();
                    district = new Location();
                    district.setLocation_name(MainActivity.mainContext.getResources().getString(R.string.district));
                    district.setLocation_id(-1);
                    district.setParent_id(-1);
                    district.setLocation_level(-1);
                    lsDistrict.add(0, district);
                    lsDistrict.addAll(sqlLocations.getAllDistrictByCity(lsCity.get(position).getLocation_id(), 2));
                    adapterDistrict.notifyDataSetChanged();
                    filterCity = lsCity.get(position).getLocation_name();
                    filterDistrict = "";
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
                    districtSelect = lsDistrict.get(position).getLocation_name();
                    filterDistrict = districtSelect;
                } else {
                    filterDistrict = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initView() {
        fileGet = new FileSave(getContext(), Constants.GET);

        sqlPropertiesFilter = new SQLPropertiesFilter(getContext());
        sqlLocations = new SQLLocations(getContext());
        SQLCategories sqlCategories = new SQLCategories(getContext());

        btnSkipFilter = findViewById(R.id.btn_skip_filter);
        btnApplyFilter = findViewById(R.id.btn_apply_filter);
        spnCity = findViewById(R.id.spn_city);
        spnDistrict = findViewById(R.id.spn_district);
        txtSelectPrice = findViewById(R.id.txt_select_price);
        recyclerViewJob = findViewById(R.id.lv_job);
        recyclerViewProperties = findViewById(R.id.lv_properties_filter);

        lsCity = new ArrayList<>();
        lsDistrict = new ArrayList<>();
        lstPrice = new ArrayList<>();

        getDataCityDistrict();

        lstJob = new ArrayList<>();
        lstJob = sqlCategories.getListCategoryByLevel(null, 1);

        recyclerViewJob.setHasFixedSize(true);
        recyclerViewJob.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        adapterJob = new JobFilterAdapter(getContext(), lstJob, this::clickJob);
        recyclerViewJob.setAdapter(adapterJob);

        recyclerViewProperties.setHasFixedSize(true);
        recyclerViewProperties.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        if (!TextUtils.isEmpty(filterJob)) {
            for (int i = 0; i < lstJob.size(); i++) {
                if (filterJob.equalsIgnoreCase(lstJob.get(i).category_name)) {
                    lstJob.get(i).is_select = true;
//                    dataProperties = sqlPropertiesFilter.getPropertiesByCate(lstJob.get(i).category_id);

                    adapterPropertiesDynamic = new PropertiesFilterDynamicAdapter(getContext(), dataProperties);
                    recyclerViewProperties.setAdapter(adapterPropertiesDynamic);
                    adapterPropertiesDynamic.notifyDataSetChanged();
                    break;
                }
            }
        }

        if (!TextUtils.isEmpty(filterPrice)) {
            txtSelectPrice.setText(filterPrice);
        }
    }

    private void clickJob(View v, int pos) {
        if (lstJob.get(pos).is_select) {
            filterJob = lstJob.get(pos).category_name;

            dataProperties = sqlPropertiesFilter.getPropertiesByCate(lstJob.get(pos).category_id);

            adapterPropertiesDynamic = new PropertiesFilterDynamicAdapter(getContext(), dataProperties);
            recyclerViewProperties.setAdapter(adapterPropertiesDynamic);
            adapterPropertiesDynamic.notifyDataSetChanged();
            recyclerViewProperties.setVisibility(View.VISIBLE);
        } else {
            filterJob = "";
            dataProperties.clear();
            recyclerViewProperties.setVisibility(View.GONE);
            adapterPropertiesDynamic.notifyDataSetChanged();
        }

    }

    private void getDataCityDistrict() {
        lsCity.clear();

        city = new Location();
        district = new Location();
        city.setLocation_name(MainActivity.mainContext.getResources().getString(R.string.city));
        city.setLocation_id(-1);
        city.setParent_id(-1);
        city.setLocation_level(-1);

        lsCity.add(0, city);
        lsCity.addAll(sqlLocations.getAllCity(1));
        adapterCity = new LocationSortAdapter(getContext(), lsCity);
        spnCity.setAdapter(adapterCity);
        spnCity.setSelection(0);

        district = new Location();
        district.setLocation_name(MainActivity.mainContext.getResources().getString(R.string.district));
        district.setLocation_id(-1);
        district.setParent_id(-1);
        district.setLocation_level(-1);

        lsDistrict.add(0, district);
        adapterDistrict = new LocationSortAdapter(getContext(), lsDistrict);
        spnDistrict.setAdapter(adapterDistrict);
        spnDistrict.setEnabled(false);

        try {
            if (!TextUtils.isEmpty(filterCity)) {
                for (int i = 0; i < lsCity.size(); i++) {
                    if (filterCity.equalsIgnoreCase(lsCity.get(i).getLocation_name())) {
                        district = new Location();
                        district.setLocation_name(MainActivity.mainContext.getResources().getString(R.string.district));
                        district.setLocation_id(-1);
                        district.setParent_id(-1);
                        district.setLocation_level(-1);
                        lsDistrict.add(0, district);
                        lsDistrict.addAll(sqlLocations.getAllDistrictByCity(lsCity.get(i).getLocation_id(), 2));
                        adapterDistrict.notifyDataSetChanged();

                        //lech pos
                        spnCity.setSelection(i);
                        spnDistrict.setSelection(0);
                        if (!TextUtils.isEmpty(filterDistrict)) {
                            for (int j = 0; j < lsDistrict.size(); j++) {
                                if (filterDistrict.equalsIgnoreCase(lsDistrict.get(j).getLocation_name())) {
                                    spnDistrict.setSelection(j - 1);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            } else {
                String city = fileGet.getUserCityName();
                for (int i = 0; i < lsCity.size(); i++) {
                    if (city.equalsIgnoreCase(lsCity.get(i).getLocation_name())) {
                        spnCity.setSelection(i);
                        filterCity = lstJob.get(i).category_name;
                        filterDistrict = "";
                        break;
                    }

                    spnDistrict.setSelection(0);
                }
            }
        } catch (Exception ignored) {

        }

    }

    @SuppressLint("SetTextI18n")
    private void dialogSelectPrice() {
        dialogSelectPrice = new AlertDialog.Builder(getContext())
                .setView(R.layout.dialog_select_price_filter_social)
                .create();

        dialogSelectPrice.show();

        Button btnAccept = dialogSelectPrice.findViewById(R.id.btn_accept);
        EditText edtMinPrice = dialogSelectPrice.findViewById(R.id.edt_min_price);
        EditText edtMaxPrice = dialogSelectPrice.findViewById(R.id.edt_max_price);
        TextView txt_price = dialogSelectPrice.findViewById(R.id.txt_price);
        RecyclerView recyclerView = dialogSelectPrice.findViewById(R.id.list_price);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        initListPrice();

        SelectPriceInFilterAdapter adapter = new SelectPriceInFilterAdapter(lstPrice);
        recyclerView.setAdapter(adapter);

        final SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    try {
                        View centerView = snapHelper.findSnapView(mLayoutManager);
                        if (centerView != null) {
                            pos = mLayoutManager.getPosition(centerView);
                            String[] strPrice = lstPrice.get(pos).split(" - ");

                            if (lstPrice.get(pos).startsWith("Thỏa thuận")) {
                                edtMinPrice.setText("");
                                edtMaxPrice.setText("");
                                txt_price.setText("Thỏa thuận");
                            }

                            if (lstPrice.get(pos).startsWith("<")) {
                                edtMinPrice.setText(lstPrice.get(pos).trim().split(" ")[1]);
                                edtMaxPrice.setText("");
                            }

                            if (lstPrice.get(pos).startsWith(">")) {
                                edtMinPrice.setText("");

                                if (lstPrice.get(pos).trim().split(" ")[2].equals("tỷ")) {
                                    edtMaxPrice.setText(lstPrice.get(pos).trim().split(" ")[1] + "000");
                                } else {
                                    edtMaxPrice.setText(lstPrice.get(pos).trim().split(" ")[1]);
                                }
                            }

                            //500 - 1 tỷ
                            if (strPrice.length > 1) {
                                if (lstPrice.get(pos).trim().split(" ")[1].equals("tỷ")) {
                                    edtMinPrice.setText(lstPrice.get(pos).trim().split(" ")[0] + "000");
                                } else {
                                    edtMinPrice.setText(lstPrice.get(pos).trim().split(" ")[0]);
                                }

                                if (lstPrice.get(pos).trim().split(" ")[4].equals("tỷ")) {
                                    edtMaxPrice.setText(lstPrice.get(pos).trim().split(" ")[3] + "000");
                                } else {
                                    edtMaxPrice.setText(lstPrice.get(pos).trim().split(" ")[3]);
                                }

                            }
                        }
                    } catch (Exception e) {
                        Log.e("error select price: ", e.toString());
                    }
                }
            }
        });

        edtMinPrice.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {

                try {
                    if (TextUtils.isEmpty(edtMaxPrice.getText())) {
                        if (TextUtils.isEmpty(edtMinPrice.getText())) {
                            txt_price.setText("");
                        } else {
                            if (edtMinPrice.getText().length() <= 3) {
                                txt_price.setText("< " + edtMinPrice.getText() + " " + "triệu");
                            } else {
                                String priceTemp1 = edtMinPrice.getText().toString().substring(0, edtMinPrice.getText().length() - 3);
                                String priceTemp2 = edtMinPrice.getText().toString().substring(edtMinPrice.getText().length() - 3);
                                String priceTemp = priceTemp1 + "." + priceTemp2;
                                txt_price.setText("< " + priceTemp + " " + "tỷ");
                            }
                        }
                    } else {
                        String minPrice;

                        if (TextUtils.isEmpty(edtMinPrice.getText())) {
                            minPrice = "";
                        } else {
                            if (edtMinPrice.getText().length() <= 3) {
                                minPrice = edtMinPrice.getText() + " " + "triệu";
                            } else {
                                String priceTemp1 = edtMinPrice.getText().toString().substring(0, edtMinPrice.getText().length() - 3);
                                String priceTemp2 = edtMinPrice.getText().toString().substring(edtMinPrice.getText().length() - 3);
                                String priceTemp = priceTemp1 + "." + priceTemp2;
                                minPrice = priceTemp + " " + "tỷ";
                            }
                        }

                        String maxPrice;
                        if (edtMaxPrice.getText().length() <= 3) {
                            maxPrice = edtMaxPrice.getText() + " " + "triệu";
                        } else {
                            String priceTemp1 = edtMaxPrice.getText().toString().substring(0, edtMaxPrice.getText().length() - 3);
                            String priceTemp2 = edtMaxPrice.getText().toString().substring(edtMaxPrice.getText().length() - 3);
                            String priceTemp = priceTemp1 + "." + priceTemp2;
                            maxPrice = priceTemp + " " + "tỷ";
                        }

                        if (TextUtils.isEmpty(minPrice)) {
                            txt_price.setText("> " + maxPrice);
                        } else {
                            txt_price.setText(minPrice + " - " + maxPrice);
                        }
                    }
                } catch (Exception e) {
                    Log.e("test: ", e.toString());
                }

            }
        });

        edtMaxPrice.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {

                try {
                    if (TextUtils.isEmpty(edtMinPrice.getText())) {
                        if (TextUtils.isEmpty(edtMaxPrice.getText())) {
                            txt_price.setText("");
                        } else {
                            if (edtMaxPrice.getText().length() <= 3) {
                                txt_price.setText("< " + edtMaxPrice.getText() + " triệu");
                            } else {
                                String priceTemp1 = edtMaxPrice.getText().toString().substring(0, edtMaxPrice.getText().length() - 3);
                                String priceTemp2 = edtMaxPrice.getText().toString().substring(edtMaxPrice.getText().length() - 3);
                                String priceTemp = priceTemp1 + "." + priceTemp2;
                                txt_price.setText("> " + priceTemp + " tỷ");
                            }
                        }
                    } else {
                        String minPrice;

                        if (edtMinPrice.getText().length() <= 3) {
                            minPrice = edtMinPrice.getText() + " triệu";
                        } else {
                            String priceTemp1 = edtMinPrice.getText().toString().substring(0, edtMinPrice.getText().length() - 3);
                            String priceTemp2 = edtMinPrice.getText().toString().substring(edtMinPrice.getText().length() - 3);
                            String priceTemp = priceTemp1 + "." + priceTemp2;
                            minPrice = priceTemp + " tỷ";
                        }

                        String maxPrice;
                        if (TextUtils.isEmpty(edtMaxPrice.getText())) {
                            maxPrice = "";
                        } else {
                            if (edtMaxPrice.getText().length() <= 3) {
                                maxPrice = edtMaxPrice.getText() + " " + "triệu";
                            } else {
                                String priceTemp1 = edtMaxPrice.getText().toString().substring(0, edtMaxPrice.getText().length() - 3);
                                String priceTemp2 = edtMaxPrice.getText().toString().substring(edtMaxPrice.getText().length() - 3);
                                String priceTemp = priceTemp1 + "." + priceTemp2;
                                maxPrice = priceTemp + " " + "tỷ";
                            }
                        }

                        if (TextUtils.isEmpty(maxPrice)) {
                            txt_price.setText("< " + minPrice);
                        } else {
                            txt_price.setText(minPrice + " - " + maxPrice);
                        }
                    }
                } catch (Exception e) {
                    Log.e("test: ", e.toString());
                }

            }
        });

        btnAccept.setOnClickListener(v1 -> {
            if (pos != -1) {
                filterPrice = txt_price.getText() + "";
                txtSelectPrice.setText(txt_price.getText() + "");
            }
            dialogSelectPrice.dismiss();
        });
    }

    private void initListPrice() {
        lstPrice.clear();
        lstPrice.add("");
        lstPrice.add("");
        lstPrice.add("");
        lstPrice.add("Thỏa thuận");
        lstPrice.add("< 1 triệu");
        lstPrice.add("1 triệu - 5 triệu");
        lstPrice.add("5 triệu - 10 triệu");
        lstPrice.add("10 triệu- 50 triệu");
        lstPrice.add("50 triệu - 100 triệu");
        lstPrice.add("100 triệu - 300 triệu");
        lstPrice.add("300 triệu - 500 triệu");
        lstPrice.add("500 triệu - 1 tỷ");
        lstPrice.add("1 tỷ - 3 tỷ");
        lstPrice.add("3 tỷ - 5 tỷ");
        lstPrice.add("5 tỷ - 10 tỷ");
        lstPrice.add("> 10 tỷ");
        lstPrice.add("");
        lstPrice.add("");
        lstPrice.add("");
    }

}
