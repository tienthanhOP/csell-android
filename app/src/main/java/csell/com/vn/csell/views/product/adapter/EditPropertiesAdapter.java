package csell.com.vn.csell.views.product.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.product.activity.EditProductActivity;
import csell.com.vn.csell.constants.ActionProperty;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.models.Properties;
import csell.com.vn.csell.models.PropertyValue;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.sqlites.SQLLanguage;
import csell.com.vn.csell.sqlites.SQLPropertyValue;

//import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by cuong.nv on 4/27/2018.
 */

public class EditPropertiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Properties> properties;
    //private FirebaseFirestore firestore;
    private FileSave fileGet;
    private boolean isEdit;
    private SQLPropertyValue sqlPropertyValue;
    private Product product;
    private SQLLanguage sqlLanguage;

    public EditPropertiesAdapter(Context context, List<Properties> properties, Product product, boolean IsEdit) {
        this.context = context;
        this.properties = properties;
        fileGet = new FileSave(context, Constants.GET);
        this.product = product;
        this.isEdit = IsEdit;
        sqlPropertyValue = new SQLPropertyValue(context);
        sqlLanguage = new SQLLanguage(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_propery_product, parent, false);
        return new EditPropertyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        EditPropertyViewHolder holderItem = (EditPropertyViewHolder) holder;
        holderItem.edtPropertyValues.setEnabled(isEdit);
        holderItem.edtPropertyValues.setHint(context.getString(R.string.input));

        try {

            String nameCate = "";
            if (!TextUtils.isEmpty(properties.get(position).category_id + ""))
                nameCate = sqlLanguage.getDisplayNameByCatId(properties.get(position).property_name, fileGet.getLanguage());

            if (!TextUtils.isEmpty(nameCate))
                holderItem.txtPropertyName.setText(nameCate + ": ");
            else
                holderItem.txtPropertyName.setText(properties.get(position).display_name + ":");
        } catch (NumberFormatException e) {
            holderItem.txtPropertyName.setText(properties.get(position).display_name + ":");
        }

        holderItem.edtPropertyValues.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {

                    try {
                        switch (properties.get(position).type) {
                            case ActionProperty.TYPE_STRING:
                                EditProductActivity.paramsProperty.put(properties.get(position).property_name, s.toString());
                                break;
                            case ActionProperty.TYPE_INT:
                                if (TextUtils.isDigitsOnly(s.toString())) {
                                    EditProductActivity.paramsProperty.put(properties.get(position).property_name, Integer.valueOf(s.toString()));
                                } else {
                                    Toast.makeText(context, "Vui lòng nhập số", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case ActionProperty.TYPE_FLOAT:
                                try {
                                    if (s.toString().contains(".")) {
                                        EditProductActivity.paramsProperty.put(properties.get(position).property_name, Double.valueOf(s.toString()));
                                    } else {
                                        if (TextUtils.isDigitsOnly(s.toString())) {
                                            EditProductActivity.paramsProperty.put(properties.get(position).property_name, Double.valueOf(s.toString()));
                                        } else {
                                            Toast.makeText(context, "Vui lòng nhập số", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    Toast.makeText(context, "Vui lòng nhập số", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }

//                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.d(getClass().getName(), e.getMessage() + "");
                    }
                } else {
                    EditProductActivity.paramsProperty.put(properties.get(position).property_name, null);
                }
            }
        });

        if (properties.size() != 0) {
            if (product != null) {
                HashMap<String, Object> propertyMap = new HashMap<>();
                if (product.getProperties() != null) {
                    propertyMap = product.getProperties();
                }

                if (propertyMap.size() > 0) {
                    for (int i = 0; i < propertyMap.size(); i++) {
                        String action = properties.get(position).action.toLowerCase();
                        String type = properties.get(position).type.toLowerCase();
                        String value;
                        String result;
                        if (action.equals(ActionProperty.SEEKER) || action.equals(ActionProperty.INPUT)) {
                            switch (type) {
                                case ActionProperty.TYPE_INT:
                                case ActionProperty.TYPE_LONG:
                                    holderItem.edtPropertyValues.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    value = propertyMap.get(properties.get(position).property_name) == null ? "" : propertyMap.get(properties.get(position).property_name) + "";

                                    break;
                                case ActionProperty.TYPE_FLOAT:
                                    holderItem.edtPropertyValues.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
                                    value = propertyMap.get(properties.get(position).property_name) == null ? "" : (String) propertyMap.get(properties.get(position).property_name);
                                    break;
                                default:
                                    holderItem.edtPropertyValues.setInputType(InputType.TYPE_CLASS_TEXT);
                                    value = propertyMap.get(properties.get(position).property_name) == null ? "" : (String) propertyMap.get(properties.get(position).property_name);
                                    break;
                            }

                            holderItem.edtPropertyValues.setEnabled(true);
                            holderItem.edtPropertyValues.setVisibility(View.VISIBLE);
                            holderItem.from_spn.setVisibility(View.GONE);


                            holderItem.edtPropertyValues.setText(TextUtils.isEmpty(value) ? "" : value);

                        } else {
                            holderItem.edtPropertyValues.setEnabled(false);
                            holderItem.edtPropertyValues.setVisibility(View.GONE);
                            holderItem.from_spn.setVisibility(View.VISIBLE);
                            switch (type) {
                                case ActionProperty.TYPE_INT:
                                case ActionProperty.TYPE_LONG:
                                    result = propertyMap.get(properties.get(position).property_name) == null ? "" : (String) propertyMap.get(properties.get(position).property_name);
                                    break;
                                case ActionProperty.TYPE_FLOAT:
                                    result = propertyMap.get(properties.get(position).property_name) == null ? "" : (String) propertyMap.get(properties.get(position).property_name);
                                    break;
                                default:
                                    result = propertyMap.get(properties.get(position).property_name) == null ? "" : (String) propertyMap.get(properties.get(position).property_name);
                                    break;
                            }

                            List<PropertyValue> data = new ArrayList<>();
                            PropertyValue select = new PropertyValue();
                            select.property_name = "-1";
                            select.value = "Chọn";
                            data.add(select);
                            data.addAll(sqlPropertyValue.getPropertiesByCatePropName(properties.get(position).category_id, properties.get(position).property_name));

                            int pos = position;
                            SpinnerAdapter dropdowValueAdapter = new SpinnerAdapter(context, data, true);
                            holderItem.spnPropValue.setAdapter(dropdowValueAdapter);
                            int j = 0;
                            for (int t = 0; t < data.size(); t++) {
                                if (data.get(t).value.equalsIgnoreCase(String.valueOf(result))) {
                                    j = t;
                                    break;
                                }
                            }

                            holderItem.spnPropValue.setSelection(j);
                            holderItem.spnPropValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    if (!data.get(position).property_name.equals("-1")) {
                                        if (properties.get(pos).type.toLowerCase().equals(ActionProperty.TYPE_INT)) {
                                            EditProductActivity.paramsProperty.put(properties.get(pos).property_name, Long.parseLong(data.get(position).value));
                                        } else {
                                            EditProductActivity.paramsProperty.put(properties.get(pos).property_name, data.get(position).value);
                                        }
                                    }


                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }

                    }
                } else {
                    for (int i = 0; i < properties.size(); i++) {
                        String action = properties.get(position).action.toLowerCase();
                        String type = properties.get(position).type.toLowerCase();
                        String value;
                        if (action.equals(ActionProperty.SEEKER) || action.equals(ActionProperty.INPUT)) {
                            switch (type) {
                                case ActionProperty.TYPE_INT:
                                case ActionProperty.TYPE_LONG:
                                    holderItem.edtPropertyValues.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    value = "";

                                    break;
                                case ActionProperty.TYPE_FLOAT:
                                    holderItem.edtPropertyValues.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
                                    value = "";
                                    break;
                                default:
                                    holderItem.edtPropertyValues.setInputType(InputType.TYPE_CLASS_TEXT);
                                    value = "";
                                    break;
                            }

                            holderItem.edtPropertyValues.setEnabled(true);
                            holderItem.edtPropertyValues.setVisibility(View.VISIBLE);
                            holderItem.from_spn.setVisibility(View.GONE);


                            holderItem.edtPropertyValues.setText(TextUtils.isEmpty(value) ? "" : value);

                        } else {
                            holderItem.edtPropertyValues.setEnabled(false);
                            holderItem.edtPropertyValues.setVisibility(View.GONE);
                            holderItem.from_spn.setVisibility(View.VISIBLE);

                            List<PropertyValue> data = new ArrayList<>();
                            PropertyValue select = new PropertyValue();
                            select.property_name = "-1";
                            select.value = "Chọn";
                            data.add(select);
                            data.addAll(sqlPropertyValue.getPropertiesByCatePropName(properties.get(position).category_id, properties.get(position).property_name));

                            int pos = position;
                            SpinnerAdapter dropdowValueAdapter = new SpinnerAdapter(context, data, true);
                            holderItem.spnPropValue.setAdapter(dropdowValueAdapter);

                            holderItem.spnPropValue.setSelection(0);
                            holderItem.spnPropValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    if (!data.get(position).property_name.equals("-1")) {
                                        if (properties.get(pos).type.toLowerCase().equals(ActionProperty.TYPE_INT)) {
                                            EditProductActivity.paramsProperty.put(properties.get(pos).property_name, Long.parseLong(data.get(position).value));
                                        } else {
                                            EditProductActivity.paramsProperty.put(properties.get(pos).property_name, data.get(position).value);
                                        }
                                    }


                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }

                    }
                }

            }
        }

    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    private class EditPropertyViewHolder extends RecyclerView.ViewHolder {

        TextView txtPropertyName;
        EditText edtPropertyValues;
        Spinner spnPropValue;
        RelativeLayout from_spn;

        EditPropertyViewHolder(View itemView) {
            super(itemView);
            txtPropertyName = itemView.findViewById(R.id.txtPropertyName);
            edtPropertyValues = itemView.findViewById(R.id.edtPropertyValue);
            spnPropValue = itemView.findViewById(R.id.spnPropValue);
            from_spn = itemView.findViewById(R.id.from_spn);

        }
    }
}
