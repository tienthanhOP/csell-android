package csell.com.vn.csell.views.product.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.ActionProperty;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.models.Properties;
import csell.com.vn.csell.models.PropertyValue;
import csell.com.vn.csell.sqlites.SQLLanguage;
import csell.com.vn.csell.sqlites.SQLPropertyValue;
import csell.com.vn.csell.views.product.adapter.viewholder.PropertyViewHolder;

public class PropertiesDynamicAdapter extends RecyclerView.Adapter<PropertyViewHolder> {

    public static HashMap<String, Object> mapAttributes = new HashMap<>();
    public List<Properties> properties;
    private Context context;
    private FileSave fileGet;
    private SQLPropertyValue sqlPropertyValue;
    private SQLLanguage sqlLanguage;

    public PropertiesDynamicAdapter(Context context, List<Properties> properties) {
        this.context = context;
        this.properties = properties;
        this.fileGet = new FileSave(context, Constants.GET);
        sqlPropertyValue = new SQLPropertyValue(context);
        sqlLanguage = new SQLLanguage(context);
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_property, parent, false);
        return new PropertyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            String nameCate = "";
            if (!TextUtils.isEmpty(properties.get(position).property_name + ""))
                nameCate = sqlLanguage.getDisplayNameByCatId(properties.get(position).property_name, fileGet.getLanguage());

            if (!TextUtils.isEmpty(nameCate))
                holder.txtPropertyName.setText(nameCate + ": ");
            else
                holder.txtPropertyName.setText(properties.get(position).display_name + ":");
        } catch (NumberFormatException e) {
            holder.txtPropertyName.setText(properties.get(position).display_name + ":");
        }

        String action = properties.get(position).action.toLowerCase();
        int index = position;
        switch (action) {
            case ActionProperty.INPUT: {
                holder.spnValue.setVisibility(View.GONE);
                holder.fromInputSpinner.setVisibility(View.VISIBLE);
                holder.fromSelect.setVisibility(View.GONE);
                holder.fromTypeSim.setVisibility(View.GONE);
                holder.txtPropertyName.setVisibility(View.VISIBLE);
                holder.fromSeekBar.setVisibility(View.GONE);
                holder.edtValue.setEnabled(true);
                holder.edtValue.setVisibility(View.VISIBLE);

                if (properties.get(index).type.equals(ActionProperty.TYPE_STRING)) {
                    holder.edtValue.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    holder.edtValue.setInputType(InputType.TYPE_CLASS_NUMBER);
                }

                List<PropertyValue> arrayList = new ArrayList<>(sqlPropertyValue.getPropertiesByCatePropName(properties.get(index).category_id, properties.get(index).property_name));

                String hint;
                if (arrayList.size() > 0) {
                    if (!TextUtils.isEmpty(arrayList.get(0).value)) {
                        hint = arrayList.get(0).value;
                        holder.edtValue.setHint(context.getString(R.string.input) + " (" + hint + ")");
                    } else {
                        holder.edtValue.setHint(context.getString(R.string.input));
                    }
                } else {
                    holder.edtValue.setHint(context.getString(R.string.input));
                }

                holder.spinner.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(properties.get(position).pickedValue)) {
                    holder.edtValue.setText(properties.get(position).pickedValue);
                } else {
                    holder.edtValue.setText(null);
                }
            }
            break;
            case ActionProperty.SELECT: {
                holder.edtValue.setVisibility(View.GONE);
                holder.fromInputSpinner.setVisibility(View.GONE);
                holder.fromSelect.setVisibility(View.VISIBLE);
                holder.fromTypeSim.setVisibility(View.GONE);
                holder.txtPropertyName.setVisibility(View.VISIBLE);
                holder.fromSeekBar.setVisibility(View.GONE);
                holder.spnValue.setVisibility(View.GONE);
                holder.edtValue.setEnabled(false);
                holder.edtValue.setVisibility(View.GONE);

                List<PropertyValue> data = new ArrayList<>(sqlPropertyValue.getPropertiesByCatePropName(properties.get(index).category_id, properties.get(index).property_name));
                holder.rvSelectedProperty.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

                int pickerIndex = -1;
                for (int i = 0; i < data.size(); i++) {
                    if (properties.get(position).pickedValue.equals(data.get(i).value)) {
                        pickerIndex = i;
                    }
                }

                holder.rvSelectedProperty.setAdapter(new SubPropertyAdapter(context, data, pickerIndex + "", (item, position1) -> {
                    //put params
                    mapAttributes.put(item.property_name, item.value);

                    properties.get(position).pickedValue = item.value;

                }));
            }
            break;
            case ActionProperty.SEEKER: {
//                MUC O TO CHUYEN THANH NHAP
                holder.spnValue.setVisibility(View.GONE);
                holder.fromInputSpinner.setVisibility(View.GONE);
                holder.fromSelect.setVisibility(View.GONE);
                holder.fromTypeSim.setVisibility(View.GONE);
                holder.txtPropertyName.setVisibility(View.VISIBLE);
                holder.fromSeekBar.setVisibility(View.VISIBLE);
                holder.edtValue.setVisibility(View.GONE);
                holder.edtValue.setEnabled(false);
                holder.tvSeekMin.setText("0");
                holder.seekBar.setMax(150);
                holder.tvSeekMax.setText(context.getString(R.string.text_max_speed_ometer));
            }
            break;
            case ActionProperty.DROP_DOWN: {
//                holder.fromInputSpinner.setVisibility(View.GONE);
//                holder.fromSelect.setVisibility(View.GONE);
//                holder.txtPropertyName.setVisibility(View.VISIBLE);
//                holder.fromSeekBar.setVisibility(View.GONE);
//                holder.spnValue.setVisibility(View.GONE);
//                holder.edtValue.setEnabled(false);
//                holder.edtValue.setVisibility(View.GONE);
//                String cate = fileGet.getRootCategoryId() + "";
//                if (cate.contains(Utilities.SIM_VIP) || cate.contains(Utilities.SIM_EASY_SPECIAL)) {
//                    holder.fromTypeSim.setVisibility(View.VISIBLE);
//                    holder.spnValue.setVisibility(View.GONE);
//                    List<PropertyValue> typeSim = new ArrayList<>();
//                    PropertyValue select = new PropertyValue();
//                    select.property_name = "-1";
//                    select.value = "Chọn";
//                    typeSim.add(select);
//                    typeSim.addAll(sqlPropertyValue.getPropertiesByCatePropName(properties.get(index).category_id, properties.get(index).property_name));
//                    SpinnerAdapter adapterTypeSim = new SpinnerAdapter(context, typeSim);
//                    holder.spnTypeSim.setAdapter(adapterTypeSim);
//
//                    try {
//                        if (TextUtils.isDigitsOnly(properties.get(position).pickedValue) && !TextUtils.isEmpty(properties.get(position).pickedValue)) {
//                            holder.spnTypeSim.setSelection(Integer.parseInt(properties.get(position).pickedValue));
//                        }
//                    } catch (NumberFormatException ignored) {
//
//                    }
//
//                    holder.spnTypeSim.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
////                                Toast.makeText(context, properties.get(index).Prop_Name + "/" + finalTypeSim.get(position), Toast.LENGTH_SHORT).show();
//                            if (!typeSim.get(position).property_name.equals("-1")) {
//                                properties.get(holder.getAdapterPosition()).pickedValue = position + "";
//                                SelectCategoryActivity.paramsProperty.put(properties.get(index).property_name, typeSim.get(position).value);
//                            }
//
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//
//                        }
//                    });
//                } else {
//                    holder.fromTypeSim.setVisibility(View.GONE);
//                    holder.spnValue.setVisibility(View.VISIBLE);
//                    List<PropertyValue> data1 = new ArrayList<>();
//                    PropertyValue select = new PropertyValue();
//                    select.property_name = "-1";
//                    select.value = "Chọn";
//                    data1.add(select);
//                    data1.addAll(sqlPropertyValue.getPropertiesByCatePropName(properties.get(position).category_id, properties.get(position).property_name));
//                    SpinnerAdapter adapter = new SpinnerAdapter(context, data1);
//                    holder.spnValue.setAdapter(adapter);
//
//                    try {
//                        if (TextUtils.isDigitsOnly(properties.get(position).pickedValue) && !TextUtils.isEmpty(properties.get(position).pickedValue)) {
//                            holder.spnValue.setSelection(Integer.parseInt(properties.get(position).pickedValue));
//                        }
//                    } catch (NumberFormatException ignored) {
//
//                    }
//
//                    holder.spnValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            if (!data1.get(position).property_name.equals("-1")) {
//                                if (TextUtils.isDigitsOnly(data1.get(position).value)) {
//                                    properties.get(holder.getAdapterPosition()).pickedValue = position + "";
//                                    int result = Integer.parseInt(data1.get(position).value);
//                                    SelectCategoryActivity.paramsProperty.put(properties.get(index).property_name, result);
//                                } else {
//                                    properties.get(holder.getAdapterPosition()).pickedValue = position + "";
//                                    SelectCategoryActivity.paramsProperty.put(properties.get(index).property_name, data1.get(position).value);
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//
//                        }
//                    });
//                }
            }
            break;
            default: {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                break;
            }
        }

        holder.edtValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    if (!TextUtils.isEmpty(s.toString())) {
                        properties.get(holder.getAdapterPosition()).pickedValue = s.toString();
//                        if (TextUtils.isDigitsOnly(s.toString())) {
//                            Double a = Double.parseDouble(s.toString());
//                            SelectCategoryActivity.paramsProperty.put(properties.get(position).property_name, a);
//                        } else {
//                            SelectCategoryActivity.paramsProperty.put(properties.get(position).property_name, s.toString());
//                        }
                        mapAttributes.put(properties.get(position).property_name, s.toString());
                    }
                } catch (Exception e) {
                    mapAttributes.put(properties.get(position).property_name, s.toString());
                }
            }
        });

        String interval = context.getString(R.string.about) + "";
        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    long updateValue;
                    if (progress <= 100) {
                        updateValue = (progress * 1000);
                    } else if (progress <= 125) {
                        updateValue = (progress * 1000) + 10000;
                    } else {
                        updateValue = (progress * 1500);
                    }

                    try {
                        String nameCate = "";
                        if (!TextUtils.isEmpty(properties.get(position).category_id + ""))
                            nameCate = sqlLanguage.getDisplayNameByCatId(properties.get(position).category_id, fileGet.getLanguage());

                        if (!TextUtils.isEmpty(nameCate))
                            holder.txtPropertyName.setText(nameCate + " "
                                    + interval + " "
                                    + updateValue + " Km");
                        else holder.txtPropertyName.setText(properties.get(index).display_name + " "
                                + interval + " "
                                + updateValue + " Km");
                    } catch (NumberFormatException e) {
                        holder.txtPropertyName.setText(properties.get(index).display_name + " "
                                + interval + " "
                                + updateValue + " Km");
                    }

//                        Toast.makeText(context, "" + properties.get(index).Prop_Name + "=" + updateValue, Toast.LENGTH_SHORT).show();
                    mapAttributes.put(properties.get(index).property_name, updateValue);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }
}