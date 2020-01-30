package csell.com.vn.csell.views.product.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.ProductController;
import csell.com.vn.csell.interfaces.GetProduct;
import csell.com.vn.csell.models.Location;
import csell.com.vn.csell.models.NoteV1;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.models.ProductResponseV1;
import csell.com.vn.csell.models.Properties;
import csell.com.vn.csell.models.PropertyValue;
import csell.com.vn.csell.models.UnitAdministrative;
import csell.com.vn.csell.sqlites.SQLCitys;
import csell.com.vn.csell.sqlites.SQLDistricts;
import csell.com.vn.csell.sqlites.SQLProperties;
import csell.com.vn.csell.sqlites.SQLStreets;
import csell.com.vn.csell.sqlites.SQLWards;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.csell.adapter.LocationSortAdapter;
import csell.com.vn.csell.views.product.adapter.CurrencyAdapter;
import csell.com.vn.csell.views.product.adapter.PropertiesDynamicAdapter;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;

public class EditProductActivityV1 extends AppCompatActivity implements GetProduct, ProductController.OnDeleteProductListener {
    public static HashMap<String, Object> paramsEditBasicProduct = new HashMap<>();
    private Bundle bundle;
    private TextView titleToolbar;
    private ProductResponseV1 productResponseV1;
    private FancyButton btnBackNavigation;
    private FancyButton btnSaveNavigation;
    private ViewStub viewStub;
    private View inflated;
    private int typeEdit; // 1: update basic infor; 2: update owner 3: update attributes
    private TextInputEditText edtProductName;
    private TextView txtCountCharacter;
    private TextInputEditText edtPrice;
    private Spinner spnTypePrice;
    private SQLCitys sqlCitys;
    private SQLDistricts sqlDistricts;
    private SQLWards sqlWards;
    private SQLStreets sqlStreets;
    private SQLProperties sqlProperties;
    private List<Location> lsCity = new ArrayList<>();
    private List<Location> lsDistrict = new ArrayList<>();
    private List<Location> lsWards = new ArrayList<>();
    private List<Location> lsStreets = new ArrayList<>();
    private Spinner spnCity;
    private Spinner spnDistrict;
    private Spinner spnWards;
    private Spinner spnStreets;
    private LocationSortAdapter adapterCity;
    private LocationSortAdapter adapterDistrict;
    private LocationSortAdapter adapterWards;
    private LocationSortAdapter adapterStreet;
    private TextInputEditText edtAddress;
    private TextInputEditText edtDes;
    private TextView txtCountCharacterDes;
    private RecyclerView rvProperties;
    private PropertiesDynamicAdapter propertyAdapter;
    private List<Properties> arrayProperties = new ArrayList<>();
    private ProductController productController;
    private FancyButton btnDelete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product_v1);
        Fabric.with(this, new Crashlytics());
        setupWindowAnimations();
        try {
            sqlCitys = new SQLCitys(this);
            sqlDistricts = new SQLDistricts(this);
            sqlWards = new SQLWards(this);
            sqlStreets = new SQLStreets(this);
            sqlProperties = new SQLProperties(this);

            Intent intent = getIntent();
            if (intent != null) {
                bundle = getIntent().getExtras();
                productResponseV1 = (ProductResponseV1) intent.getSerializableExtra(Constants.KEY_PASSINGDATA_PRODUCT_OBJ);
            }

            productController = new ProductController(this, this, this);
            initView();
            setEvent();
            getDataToEdit();

        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void initView() {
        titleToolbar = findViewById(R.id.custom_TitleToolbar);
        titleToolbar.setText(getString(R.string.update_information));

        btnBackNavigation = findViewById(R.id.btn_back_navigation);
        btnBackNavigation.setText(getString(R.string.title_back_vn));

        btnSaveNavigation = findViewById(R.id.btn_save_navigation);
        btnSaveNavigation.setText(getString(R.string.save));

        btnDelete = findViewById(R.id.btn_deleted_product);
    }

    private void setEvent() {
        btnDelete.setOnClickListener(view -> removeProduct());
        btnBackNavigation.setOnClickListener(view -> onBackPressed());
        btnSaveNavigation.setOnClickListener(view -> {
            EditProductActivityV1.paramsEditBasicProduct.put("attributes", PropertiesDynamicAdapter.mapAttributes);
            productController.updateProduct(productResponseV1.getId(), paramsEditBasicProduct);
            paramsEditBasicProduct.clear();
            PropertiesDynamicAdapter.mapAttributes.clear();
        });
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    private void getDataToEdit() {
        viewStub = findViewById(R.id.layout_stub);

        if (bundle != null) {
            typeEdit = bundle.getInt(Constants.KEY_PASSINGDATA_TYPE_EDIT_PRODUCT);
            switch (typeEdit) {
                case 1: {
                    editBasicInfor();
                    break;
                }
                case 2: {
                    editOwerInfor();
                    break;
                }
                case 3: {
                    editAttributesInfor();
                    break;
                }
            }
        }
    }

    private void editBasicInfor() {
        try {
            viewStub.setLayoutResource(R.layout.view_stub_edit_basic);
            inflated = viewStub.inflate();

            LinearLayout lnWards = inflated.findViewById(R.id.ln_wards);
            LinearLayout lnStreets = inflated.findViewById(R.id.ln_streets);
            LinearLayout lnProjects = inflated.findViewById(R.id.ln_projects);
            RelativeLayout rlDes = inflated.findViewById(R.id.rl_des);

            if (productResponseV1.getCategories().get(0).getId().contains("land")) {
                lnWards.setVisibility(View.VISIBLE);
                lnStreets.setVisibility(View.VISIBLE);
                lnProjects.setVisibility(View.VISIBLE);
            } else {
                lnWards.setVisibility(View.GONE);
                lnStreets.setVisibility(View.GONE);
                lnProjects.setVisibility(View.GONE);
            }

            if (productResponseV1.getCategories().get(0).getId().contains("sim")) {
                rlDes.setVisibility(View.GONE);
            } else {
                rlDes.setVisibility(View.VISIBLE);
            }

            edtProductName = inflated.findViewById(R.id.edtProductName);
            edtProductName.setText(productResponseV1.getName());

            txtCountCharacter = inflated.findViewById(R.id.txtCountCharacter);
            txtCountCharacter.setText(edtProductName.getText().length() + "/100");
            edtProductName.setSelection(edtProductName.getText().length());

            edtPrice = inflated.findViewById(R.id.edt_price);
            edtPrice.setText(productResponseV1.getPrice() + "");

            ArrayList<PropertyValue> lsCurrency = Utilities.CURRENCY(this);
            CurrencyAdapter currencyAdapter = new CurrencyAdapter(this, lsCurrency);
            spnTypePrice = inflated.findViewById(R.id.spnTypePrice);
            spnTypePrice.setAdapter(currencyAdapter);
            spnTypePrice.setSelection(currencyAdapter.getPositionByValue(productResponseV1.getCurrency()), true);

            lsCity.addAll(sqlCitys.getAllCity());
            lsDistrict.addAll(sqlDistricts.getDistrictsByCity(Integer.valueOf(productResponseV1.getProvince().getCode())));
            lsWards.addAll(sqlWards.getWardByDistrict(Integer.valueOf(productResponseV1.getDistrict().getCode())));
            lsStreets.addAll(sqlStreets.getStreetsByDistrict(Integer.valueOf(productResponseV1.getDistrict().getCode())));

            spnCity = inflated.findViewById(R.id.spn_city);
            spnDistrict = inflated.findViewById(R.id.spn_district);
            spnWards = inflated.findViewById(R.id.spn_wards);
            spnStreets = inflated.findViewById(R.id.spn_streets);

            adapterCity = new LocationSortAdapter(this, lsCity);
            adapterDistrict = new LocationSortAdapter(this, lsDistrict);
            adapterWards = new LocationSortAdapter(this, lsWards);
            adapterStreet = new LocationSortAdapter(this, lsStreets);

            spnCity.setAdapter(adapterCity);
            spnCity.setSelection(adapterCity.getPositionByValue(productResponseV1.getProvince().getName()));

            spnDistrict.setAdapter(adapterDistrict);
            spnDistrict.setSelection(adapterDistrict.getPositionByValue(productResponseV1.getProvince().getName()));

            spnWards.setAdapter(adapterWards);
            spnWards.setSelection(adapterWards.getPositionByValue(productResponseV1.getWard().getName()));

            spnStreets.setAdapter(adapterStreet);
            spnStreets.setSelection(0);

            edtAddress = inflated.findViewById(R.id.edt_address);
            edtAddress.setText(productResponseV1.getAddress());

            edtDes = inflated.findViewById(R.id.edtDes);
            edtDes.setText(productResponseV1.getContent());

            txtCountCharacterDes = inflated.findViewById(R.id.txtCountCharacterDes);
            txtCountCharacterDes.setText(productResponseV1.getContent().length() + "/4000");

//            set event

            edtAddress.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    paramsEditBasicProduct.put("address", editable.toString().trim());
                }
            });

            edtDes.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!TextUtils.isEmpty(editable)) {
                        txtCountCharacterDes.setText(editable.toString().length() + "/" + 4000);
                    } else {
                        txtCountCharacterDes.setText("0/4000");
                    }
                    paramsEditBasicProduct.put("content", editable.toString().trim());
                }
            });

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
                    paramsEditBasicProduct.put("name", s.toString().trim());
                }
            });

            edtPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    paramsEditBasicProduct.put("price", editable.toString().trim());
                }
            });

            spnTypePrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    paramsEditBasicProduct.put("currency", currencyAdapter.getItem(position).value);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    lsDistrict.clear();
                    lsDistrict.addAll(sqlDistricts.getDistrictsByCity(lsCity.get(i).getLocation_id()));
                    adapterDistrict.notifyDataSetChanged();
                    spnDistrict.setSelection(0);

                    lsWards.clear();
                    lsWards.addAll(sqlWards.getWardByDistrict(lsDistrict.get(0).getLocation_id()));
                    adapterWards.notifyDataSetChanged();
                    spnWards.setSelection(0);

                    lsStreets.clear();
                    lsStreets.addAll(sqlStreets.getStreetsByDistrict(lsDistrict.get(0).getLocation_id()));
                    adapterStreet.notifyDataSetChanged();
                    spnStreets.setSelection(0);

                    paramsEditBasicProduct.put("province", new UnitAdministrative(lsCity.get(i).getLocation_id(2), lsCity.get(i).getLocation_name()));
                    paramsEditBasicProduct.put("district", new UnitAdministrative(lsDistrict.get(0).getLocation_id(3), lsDistrict.get(0).getLocation_name()));

                    if (productResponseV1.getCategories().get(0).getId().contains("land")) {
                        paramsEditBasicProduct.put("ward", new UnitAdministrative(lsWards.get(0).getLocation_id(5), lsWards.get(0).getLocation_name()));
                        edtAddress.setText(
                                ((Location) spnStreets.getSelectedItem()).getLocation_name() + ", " +
                                        ((Location) spnWards.getSelectedItem()).getLocation_name() + ", " +
                                        ((Location) spnDistrict.getSelectedItem()).getLocation_name() + ", " +
                                        ((Location) spnCity.getSelectedItem()).getLocation_name()
                        );
                    } else {
                        edtAddress.setText(
                                ((Location) spnDistrict.getSelectedItem()).getLocation_name() + ", " +
                                        ((Location) spnCity.getSelectedItem()).getLocation_name()
                        );
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spnDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    lsWards.clear();
                    lsWards.addAll(sqlWards.getWardByDistrict(lsDistrict.get(i).getLocation_id()));
                    adapterWards.notifyDataSetChanged();
                    spnWards.setSelection(0);

                    lsStreets.clear();
                    lsStreets.addAll(sqlStreets.getStreetsByDistrict(lsDistrict.get(i).getLocation_id()));
                    adapterStreet.notifyDataSetChanged();
                    spnStreets.setSelection(0);

                    paramsEditBasicProduct.put("district", new UnitAdministrative(lsDistrict.get(i).getLocation_id(3), lsDistrict.get(i).getLocation_name()));

                    if (productResponseV1.getCategories().get(0).getId().contains("land")) {
                        paramsEditBasicProduct.put("ward", new UnitAdministrative(lsWards.get(0).getLocation_id(5), lsWards.get(0).getLocation_name()));
                        edtAddress.setText(
                                ((Location) spnStreets.getSelectedItem()).getLocation_name() + ", " +
                                        ((Location) spnWards.getSelectedItem()).getLocation_name() + ", " +
                                        ((Location) spnDistrict.getSelectedItem()).getLocation_name() + ", " +
                                        ((Location) spnCity.getSelectedItem()).getLocation_name()
                        );
                    } else {
                        edtAddress.setText(
                                ((Location) spnDistrict.getSelectedItem()).getLocation_name() + ", " +
                                        ((Location) spnCity.getSelectedItem()).getLocation_name()
                        );
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spnWards.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    paramsEditBasicProduct.put("ward", new UnitAdministrative(lsWards.get(i).getLocation_id(5), lsWards.get(i).getLocation_name()));

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
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void editOwerInfor() {

    }

    private void editAttributesInfor() {
        try {
            viewStub.setLayoutResource(R.layout.view_stub_edit_attributes);
            inflated = viewStub.inflate();

            rvProperties = inflated.findViewById(R.id.rv_properties);
            rvProperties.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.mainContext);
            rvProperties.setLayoutManager(layoutManager);

            String[] cate = productResponseV1.getCategories().get(0).getId().split("_");
            for (String aCate : cate) {
                arrayProperties.addAll(sqlProperties.getPropertiesByCate(aCate + ""));
            }

            HashMap<String, Object> map = productResponseV1.getAttributes();
            if (map != null && map.size() >= arrayProperties.size()) {
                for (int i = 0; i < arrayProperties.size(); i++) {
                    Object value = map.get(arrayProperties.get(i).property_name);
                    if (value != null) {
                        if (value instanceof Double) {
                            int valueInt = ((Double) value).intValue();

                            arrayProperties.get(i).pickedValue = String.valueOf(valueInt);
                        } else {
                            arrayProperties.get(i).pickedValue = String.valueOf(value);
                        }
                    }
                }
            }

            propertyAdapter = new PropertiesDynamicAdapter(this, arrayProperties);
            rvProperties.setAdapter(propertyAdapter);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void onGetDetail(ProductResponseV1 product) {
        setResult(Constants.EDIT_PRODUCT_RESULT);
        finish();
    }

    @Override
    public void onGetNoteProduct(List<NoteV1> lstNote) {

    }

    @Override
    public void onGetDetailNewfeed(Product product) {

    }

    @Override
    public void onDeleteProductSuccess() {
        setResult(Constants.RESULT_CODE_REMOVE_PRODUCT_V1);
        finish();
    }

    @Override
    public void onDeleteProductFailure() {

    }

    private void removeProduct() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        try {
            alertDialog.setTitle("Bạn chắc chắn muốn xóa sản phẩm ?");
            alertDialog.setPositiveButton("Đồng ý",
                    (dialog, which) -> {
                        productController.removeProduct(productResponseV1.getId());
                    });

            alertDialog.setNegativeButton("Hủy", (dialog1, which) -> {
                dialog1.dismiss();
            });
            alertDialog.setCancelable(false);
            alertDialog.show();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }
}
