package csell.com.vn.csell.views.customer.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.controllers.CustomersController;
import csell.com.vn.csell.models.CustomerRetroV1;
import csell.com.vn.csell.models.ResCustomers;
import csell.com.vn.csell.views.customer.activity.ContactCustomerDetailActivity;

/**
 * Created by chuc.nq on 3/16/2018.
 */

public class CustomerDetailsInformationFragment extends Fragment implements CustomersController.OnUpdateCustomerListener {

    public TextView tvHashtag, tvEmail, tvAddress, tvDob, tvJobs;
    public EditText tvNeed;
    public TextView tvPhone, tvPhone2, tvEmail2;
    public LinearLayout layoutHashtag, layoutEmail, layoutAddress, layoutNeed, layoutDob, layoutJobs, layoutPhone, layoutPhone2, layoutEmail2;
    public LinearLayout layoutAddNeed, layoutEditNeed;
    private Button btnEdit, btnCancel;
    //    private SQLCustomers sqlCustomers;
    private int timeoutEdit = 0;
    private ProgressBar progressBar;
    private LinearLayout layout;
    private TextView tvCount;
    private FileSave fileGet;
    private CustomersController customersController;

    public static void checkExistsData(LinearLayout layout, TextView tv, String value) {
        if (layout != null && tv != null) {
            if (value != null && !value.equals("")) {
                layout.setVisibility(View.VISIBLE);
                tv.setText(value);
            } else {
                layout.setVisibility(View.GONE);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_customer_infomation, container, false);

        initView(rootView);
        setupWindowAnimations();
        addEvent();
        return rootView;
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(getActivity()).inflateTransition(R.transition.fade);
        getActivity().getWindow().setEnterTransition(transition);
    }

    private void addEvent() {
        layoutAddNeed.setOnClickListener(v -> {
            layoutNeed.setVisibility(View.VISIBLE);
            layoutAddNeed.setVisibility(View.GONE);
            tvNeed.requestFocus();
        });

        tvNeed.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                layoutEditNeed.setVisibility(View.VISIBLE);
            } else {
                layoutEditNeed.setVisibility(View.GONE);
            }
        });

        btnCancel.setOnClickListener(v -> tvNeed.clearFocus());

        btnEdit.setOnClickListener(v -> {
            showProgress(true);
            HashMap<String, Object> map = new HashMap<>();
            map.put("note", tvNeed.getText().toString().trim());
            updateCustomer(ContactCustomerDetailActivity.customer.getId(), map);
        });

        tvNeed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    int length = tvNeed.getText().length();
                    tvCount.setText(length + "/200");
                } else {
                    tvCount.setText("0/200");
                }
            }
        });
    }

    private void initView(View rootView) {
        customersController = new CustomersController(getActivity());

        tvCount = rootView.findViewById(R.id.tv_count);
        tvHashtag = rootView.findViewById(R.id.tv_hashtag_friend_detail);
        tvEmail = rootView.findViewById(R.id.tv_email_friend_detail);
        tvEmail2 = rootView.findViewById(R.id.tv_email2_friend_detail);
        tvAddress = rootView.findViewById(R.id.tv_address_friend_detail);
        tvNeed = rootView.findViewById(R.id.tv_need_friend_detail);
        tvDob = rootView.findViewById(R.id.tv_dob_friend_detail);
        tvJobs = rootView.findViewById(R.id.tv_jobs_friend_detail);
        tvPhone = rootView.findViewById(R.id.tv_phone_customer_detail);
        tvPhone2 = rootView.findViewById(R.id.tv_phone2_customer_detail);
        layoutPhone = rootView.findViewById(R.id.layout_phone);
        layoutPhone2 = rootView.findViewById(R.id.layout_phone2);
        layoutAddress = rootView.findViewById(R.id.layout_address);
        layoutDob = rootView.findViewById(R.id.layout_dob);
        layoutEmail = rootView.findViewById(R.id.layout_email);
        layoutEmail2 = rootView.findViewById(R.id.layout_email2);
        layoutJobs = rootView.findViewById(R.id.layout_jobs);
        layoutNeed = rootView.findViewById(R.id.layout_need);
        layoutHashtag = rootView.findViewById(R.id.layout_hashtag);
        layoutAddNeed = rootView.findViewById(R.id.layout_add_need);
        layoutEditNeed = rootView.findViewById(R.id.layout_edit_need);
        btnEdit = rootView.findViewById(R.id.btn_edit);
        btnCancel = rootView.findViewById(R.id.btn_cancel);
//        sqlCustomers = new SQLCustomers(getActivity());
        layout = rootView.findViewById(R.id.layout);
        progressBar = rootView.findViewById(R.id.progress_bar);
        fileGet = new FileSave(getActivity(), Constants.GET);
    }

    private void updateCustomer(String id, HashMap<String, Object> map) {
        try {
            customersController.updateCustomer(id, map, this);
        } catch (Exception ignored) {
            ignored.getMessage();
        }
    }

    private void showProgress(boolean b) {
        if (b) {
            layout.setAlpha(0.5f);
            layout.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            layout.setAlpha(1);
            layout.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUpdateCustomerSuccess(ResCustomers response, CustomerRetroV1 customerNew) {
        try {
//            sqlCustomers.insertAddCustomer(customerNew);
            showProgress(false);
            ContactCustomerDetailActivity.isEdited = true;
            Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_LONG).show();
            tvNeed.clearFocus();
            layoutEditNeed.setVisibility(View.GONE);
            showProgress(false);
        } catch (Exception ignored) {
            ignored.getMessage();
        }
    }

    @Override
    public void onErrorUpdateCustomer() {
        showProgress(false);
    }

    @Override
    public void onUpdateCustomerFailure() {
        showProgress(false);
    }

    @Override
    public void onConnectUpdateCustomerFailure() {
        showProgress(false);
    }
}
