package csell.com.vn.csell.views.product.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.EntityFirebase;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.CustomersController;
import csell.com.vn.csell.models.CustomerRetro;
import csell.com.vn.csell.models.CustomerRetroV1;
import csell.com.vn.csell.models.ProductOwner;
import csell.com.vn.csell.models.ProductResponseModel;
import csell.com.vn.csell.models.PropertyValue;
import csell.com.vn.csell.sqlites.SQLCustomers;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.customer.adapter.CustomerAutoCompletedTextAdapter;
import csell.com.vn.csell.views.product.activity.SelectCategoryActivity;
import csell.com.vn.csell.views.product.adapter.CurrencyAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by cuong.nv on 4/3/2018.
 */

public class PrivateInfoProductFragment extends Fragment implements CustomersController.OnAddCustomerListener {

    //    private CountryListSpinner spinner_phone;
    public Spinner spnCurrency;
    public ArrayList<PropertyValue> lsCurrency;
    public boolean isUpdate;
    private EditText edtOwnerNote;
    private EditText edtPriceCapital;
    private AutoCompleteTextView edtOwnerName;
    private EditText edtOwnerPhone;
    private TextView txtHDSD;
    private TextView txtNext;
    private TextView txtCountCharacter;
    private TextView txtCountCharacterNote;
    private LinearLayout layoutClickDropdown;
    private ScrollView from_private;
    private CurrencyAdapter adapterCurrency;
    private FileSave fileGet;
    private boolean isAddOwnerInfoDetail = false;
    private int timeout = 0;
    private ProductOwner owner = null;
    private CustomerAutoCompletedTextAdapter customerAdapter;
    private SQLCustomers sqlCustomers;
    private ArrayList<CustomerRetro> listCutomers;
    private BaseActivityTransition baseActivityTransition;
    private CustomersController customersController;
    private String name;
    private String phone;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileGet = new FileSave(getActivity(), Constants.GET);
        customersController = new CustomersController(getActivity());
        baseActivityTransition = new BaseActivityTransition(getActivity());
        SelectCategoryActivity.paramsProductPrivate = new HashMap<>();
        owner = new ProductOwner();
        lsCurrency = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_private_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null)
            isUpdate = true;
        initView(view);
        initEvent();
    }

    private void initView(View view) {


        from_private = view.findViewById(R.id.from_private);
        layoutClickDropdown = view.findViewById(R.id.layout_click_dropdown);
        txtHDSD = view.findViewById(R.id.txt_HDSD);
        txtNext = view.findViewById(R.id.txtNext);
        txtCountCharacter = view.findViewById(R.id.txtCountCharacter);
        txtCountCharacterNote = view.findViewById(R.id.txtCountCharacterNote);
//        spinner_phone = view.findViewById(R.id.spinner_phone);
        edtOwnerName = view.findViewById(R.id.edt_owner);
        edtOwnerPhone = view.findViewById(R.id.edt_ownerPhone);
        edtPriceCapital = view.findViewById(R.id.edt_price_capital);
        spnCurrency = view.findViewById(R.id.spn_currency_info);
        edtOwnerNote = view.findViewById(R.id.edt_owner_note);
        lsCurrency = Utilities.CURRENCY(getContext());
        adapterCurrency = new CurrencyAdapter(getActivity(), lsCurrency);
        spnCurrency.setAdapter(adapterCurrency);
        spnCurrency.setSelection(0);

        sqlCustomers = new SQLCustomers(getActivity());
        listCutomers = new ArrayList<>();

        listCutomers.addAll(sqlCustomers.getAllCustomer());

        customerAdapter = new CustomerAutoCompletedTextAdapter(getActivity(), R.layout.item_customer_autocomplete, listCutomers);
        edtOwnerName.setAdapter(customerAdapter);

        String size = "0/" + getResources().getString(R.string.text_count_character_phone);
        txtCountCharacter.setText(size);
        String size2 = "0/" + getResources().getString(R.string.text_count_character_owner_note);
        txtCountCharacterNote.setText(size2);

    }

    private void initEvent() {

        layoutClickDropdown.setOnClickListener(v -> edtOwnerName.showDropDown());

        edtOwnerName.setOnItemClickListener((parent, view, position, id) -> {
            try {
                String phone = "";
                if (listCutomers != null) {
                    if (listCutomers.size() > 0) {
                        phone = listCutomers.get(position).getPhone().get(0) + "";
                        SelectCategoryActivity.paramsProductPrivate.put(EntityAPI.FIELD_OWNER_ID, listCutomers.get(position).getCustId());
                    } else {
                        phone = "";
                        SelectCategoryActivity.paramsProductPrivate.put(EntityAPI.FIELD_OWNER_ID, "");
                    }
                }
                edtOwnerPhone.setText(phone);
            } catch (Exception e) {
                Log.e("error: ", e.toString());
            }

        });

        txtNext.setOnClickListener(v -> {

            phone = edtOwnerPhone.getText().toString().trim();
            name = edtOwnerName.getText().toString().trim();
            String note = edtOwnerNote.getText().toString().trim();
            if (!TextUtils.isEmpty(note)) {
                SelectCategoryActivity.paramsProductPrivate.put(EntityAPI.FIELD_OWNER_NOTE, note);
            }

            if (TextUtils.isEmpty(name)) {
                edtOwnerName.requestFocus();
                edtOwnerName.setError(getString(R.string.text_error_empty));
                return;
            }

            if (TextUtils.isEmpty(phone)) {
                edtOwnerPhone.requestFocus();
                edtOwnerPhone.setError(getString(R.string.text_error_empty));
                return;
            }

            if (phone.length() < 10 || phone.length() > 11) {
                edtOwnerPhone.requestFocus();
                edtOwnerPhone.setError(getString(R.string.error_enter_phone));
                return;
            }

            owner.setNote(note);
            owner.setOwnerName(name);
            owner.setOwnerPhone(phone);
            String ownerid = (String) SelectCategoryActivity.paramsProductPrivate.get(EntityAPI.FIELD_OWNER_ID);

            if (TextUtils.isEmpty(ownerid)) {
                if (sqlCustomers.checkExistedPhone(phone)) {
                    edtOwnerPhone.requestFocus();
                    edtOwnerPhone.setError(getString(R.string.text_error_existed_phone));
                    return;
                }
                addCustomer(name, phone);
            } else {
                owner.setOwnerid(ownerid);
                if (!sqlCustomers.checkExistedPhone(phone)) {
                    addCustomer(name, phone);
                } else {

                    if (!name.equals(sqlCustomers.getCustomerById(ownerid).getName())) {
                        if (isAddOwnerInfoDetail) {
                            edtOwnerPhone.requestFocus();
                            edtOwnerPhone.setError(getString(R.string.text_error_existed_phone));
                            return;
                        }
                    } else {
                        updateOwnerInfo();
                    }
                }
            }

            if (!isAddOwnerInfoDetail) {
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
                    Utilities.hideSoftKeyboard(getActivity());
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

        edtOwnerPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    String size = s.toString().length() + "/" + getResources().getString(R.string.text_count_character_phone);
                    txtCountCharacter.setText(size);
                } else {
                    String size = "0/" + getResources().getString(R.string.text_count_character_phone);
                    txtCountCharacter.setText(size);
                }
            }
        });

        txtHDSD.setOnClickListener(v -> {
            //corner_top_white
            Toast.makeText(getActivity().getBaseContext(), getResources().getString(R.string.comming_soon), Toast.LENGTH_SHORT).show();

        });

        spnCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position > 0) {
                        owner.setCurrency(lsCurrency.get(position).value);
                        SelectCategoryActivity.paramsProductPrivate.put(EntityAPI.FIELD_CURRENCY, lsCurrency.get(position).value);
                    } else {
                        owner.setCurrency(lsCurrency.get(position).value);
                        SelectCategoryActivity.paramsProductPrivate.put(EntityAPI.FIELD_CURRENCY, lsCurrency.get(position).value);
                    }
                } catch (Exception e) {
                    Log.e("error: ", e.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        edtPriceCapital.addTextChangedListener(onTextChangedListener());

        edtOwnerNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    String size2 = s.toString().length() + "/" + getResources().getString(R.string.text_count_character_owner_note);
                    txtCountCharacterNote.setText(size2);
                } else {
                    String size2 = "0/" + getResources().getString(R.string.text_count_character_owner_note);
                    txtCountCharacterNote.setText(size2);
                }
            }
        });
    }

    private void addCustomer(String name, String phone) {
        try {
            CustomerRetroV1 customerNew = new CustomerRetroV1();
            String newName = Utilities.upperFirstLetter(name);
            customerNew.setName(newName);
            List<String> phones = new ArrayList<>();
            phones.add(phone);
//            customerNew.setPhone(phones);
//            customerNew.isAdded = null;
//            customerNew.isSelectedGroup = null;

//            customersController.addCustomer(customerNew, this);
        } catch (Exception ignored) {

        }
    }

    private void updateOwnerInfo() {
        try {
            if (TextUtils.isEmpty(owner.getOwnerName()) && TextUtils.isEmpty(owner.getOwnerPhone())) {
                getActivity().finishAfterTransition();
                return;
            }

            PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
            if (postAPI != null) {
                HashMap<String, Object> map = new HashMap<>();
                map.put(EntityFirebase.FieldItem_Id, fileGet.getProductIdCurrentSelect());
                map.put(EntityFirebase.FieldOwner_Info, SelectCategoryActivity.paramsProductPrivate);
                Call<JSONResponse<List<ProductResponseModel>>> jsonResponseCall = postAPI.updateOwnerInfo(map);
                jsonResponseCall.enqueue(new Callback<JSONResponse<List<ProductResponseModel>>>() {
                    @Override
                    public void onResponse(Call<JSONResponse<List<ProductResponseModel>>> call, Response<JSONResponse<List<ProductResponseModel>>> response) {
                        if (response.isSuccessful()) {
                            JSONResponse<List<ProductResponseModel>> result = response.body();
                            if (result.getSuccess() != null) {
                                if (result.getSuccess()) {
                                    SelectCategoryActivity.paramsProductPrivate = null;
                                    if (isUpdate) {
                                        Intent intent = new Intent();
                                        intent.putExtra(Constants.KEY_INTENT_PASSING_OWNER, owner);
                                        getActivity().setResult(Constants.RESULT_CODE_ADD_OWNER_INFO, intent);
                                        getActivity().onBackPressed();
                                    }
                                } else {
                                    Utilities.refreshToken(getActivity(), result.getMessage().toLowerCase() + "");
                                }
                            }
                        }
                        jsonResponseCall.cancel();
                    }

                    @Override
                    public void onFailure(Call<JSONResponse<List<ProductResponseModel>>> call, Throwable t) {
                        if (timeout < 2) {
                            timeout++;
                            updateOwnerInfo();
                        }

                        Crashlytics.logException(t);
                    }
                });
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d(getClass().getSimpleName(), e.getMessage());
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            Bundle bundle = getArguments();
            if (bundle != null) {
                isAddOwnerInfoDetail = bundle.getBoolean(Constants.KEY_PASSING_OWNER_INFO, false);
                owner = (ProductOwner) bundle.getSerializable(Constants.KEY_INTENT_PASSING_OWNER);
                if (owner != null) {

                    for (int i = 0; i < lsCurrency.size(); i++) {
                        if (owner.getCurrency() != null) {
                            if (owner.getCurrency().equals(lsCurrency.get(i).value)) {
                                spnCurrency.setSelection(i);
                                break;
                            }
                        }
                    }

                    SelectCategoryActivity.paramsProductPrivate.put(EntityAPI.FIELD_OWNER_ID, owner.getOwnerid());
                    SQLCustomers sqlCustomers = new SQLCustomers(getActivity());
                    CustomerRetro customerRetro = sqlCustomers.getCustomerById(owner.getOwnerid());

                    if (customerRetro != null) {
                        edtOwnerName.setText(TextUtils.isEmpty(customerRetro.getName()) ? "" : customerRetro.getName());
                        edtOwnerNote.setText(TextUtils.isEmpty(owner.getNote()) ? "" : owner.getNote());
                        edtPriceCapital.setText(owner.getOriginalPrice() == null ? "" : owner.getOriginalPrice() + "");

                        if (customerRetro.getPhone() != null) {
                            if (customerRetro.getPhone().size() > 0) {
                                edtOwnerPhone.setText(customerRetro.getPhone().get(0));
                            }
                        }
                    }

                } else {
                    owner = new ProductOwner();
                }
                txtNext.setText(MainActivity.mainContext.getResources().getString(R.string.text_update));
            } else {
                txtNext.setText(MainActivity.mainContext.getResources().getString(R.string.continue_));
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ((SelectCategoryActivity) getActivity()).updateTitleToolbar("Thông tin chủ sở hữu");
        SelectCategoryActivity.currentFragment = "InputDescriptionFragment";
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
                edtPriceCapital.removeTextChangedListener(this);
                Long longval;
                try {
                    String originalString = s.toString();


                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }

                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);
                    edtPriceCapital.setText(formattedString);
                    edtPriceCapital.setSelection(edtPriceCapital.getText().length());
                    owner.setOriginalPrice(longval);
                    SelectCategoryActivity.paramsProductPrivate.put(EntityAPI.FIELD_OWNER_ORIGINAL_PRICE, longval);
                } catch (NumberFormatException nfe) {
                    edtPriceCapital.setText(s.toString());
                    edtPriceCapital.setSelection(edtPriceCapital.getText().length());
                    longval = Long.parseLong(edtPriceCapital.getText().toString());
                    owner.setOriginalPrice(longval);
                    SelectCategoryActivity.paramsProductPrivate.put(EntityAPI.FIELD_OWNER_ORIGINAL_PRICE, longval);
                }

                edtPriceCapital.addTextChangedListener(this);
            }
        };
    }

    @Override
    public void onAddCustomerSuccess(CustomerRetroV1 customerNew) {
//        String custId = (String) response.body().getData();
//        owner.setOwnerid(custId);
//        SelectCategoryActivity.paramsProductPrivate.put(EntityAPI.FIELD_OWNER_ID, custId);
//        customerNew.setCustId(custId);
//        sqlCustomers.insertAddCustomer(customerNew);
//        new AlertDialog.Builder(getContext())
//                .setTitle("Thêm liên hệ")
//                .setMessage("Bạn có muốn thêm khách hàng: '" + name + "' vào trong danh bạ điện thoại không?")
//                .setPositiveButton("Đồng ý", (dialog, which) -> {
//                    Intent intent = new Intent(Intent.ACTION_INSERT);
//                    intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
//
//                    intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
//                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
//                    baseActivityTransition.transitionTo(intent, 0);
//                })
//                .setNegativeButton("Hủy", (dialog, which) ->
//                {
//                    dialog.dismiss();
//                    if (isAddOwnerInfoDetail) {
//                        updateOwnerInfo();
//                    }
//                })
//                .setCancelable(false)
//                .show();
    }

    @Override
    public void onErrorAddCustomer() {

    }

    @Override
    public void onAddCustomerFailure() {

    }

    @Override
    public void onConnectAddCustomerFailure() {

    }
}
