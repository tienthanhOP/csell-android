package csell.com.vn.csell.views.social.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.ActionProperty;
import csell.com.vn.csell.models.Category;
import csell.com.vn.csell.models.PropertiesFilter;
import csell.com.vn.csell.models.PropertyFilterValue;
import csell.com.vn.csell.sqlites.SQLCategories;
import csell.com.vn.csell.views.product.adapter.SubPropertyFilterAdapter;

public class PropertiesFilterDynamicAdapter extends RecyclerView.Adapter<PropertiesFilterDynamicAdapter.ViewHolder> {

    private Context mContext;
    private List<PropertiesFilter> data;
    private SQLCategories sqlCategories;
    private String keyGetSub = "";
    private int indexAddValue = -1;

    public PropertiesFilterDynamicAdapter(Context mContext, List<PropertiesFilter> data) {
        this.mContext = mContext;
        this.data = data;
        sqlCategories = new SQLCategories(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_properties_dynamic_filter, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PropertiesFilter propertiesFilter = data.get(position);

        holder.txtTitle.setText(propertiesFilter.displayname + "");

        List<PropertyFilterValue> dataValue = data.get(position).values;

        switch (propertiesFilter.view_type) {
            case ActionProperty.SELECT:
                holder.rvProperties.setVisibility(View.VISIBLE);
                holder.edtInput.setVisibility(View.GONE);
                holder.spnProperties.setVisibility(View.GONE);

                holder.rvProperties.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                holder.rvProperties.setAdapter(new SubPropertyFilterAdapter(mContext, dataValue, propertiesFilter.choose_value_index, (property, posSub) -> {
                    data.get(position).choose_value = property.value;
                    data.get(position).choose_value_index = posSub;
                }));
                break;
            case ActionProperty.DROP_DOWN:
                holder.spnProperties.setVisibility(View.VISIBLE);
                holder.rvProperties.setVisibility(View.GONE);
                holder.edtInput.setVisibility(View.GONE);

                SpinnerFilterAdapter adapter = new SpinnerFilterAdapter(mContext, dataValue);
                holder.spnProperties.setAdapter(adapter);
                if (propertiesFilter.choose_value_index == -1) {
                    holder.spnProperties.setSelection(0);
                } else {
                    holder.spnProperties.setSelection(propertiesFilter.choose_value_index);
                }
                holder.spnProperties.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        if (pos > 0) {
                            data.get(position).choose_value = dataValue.get(pos).value;
                            data.get(position).choose_value_index = pos;

                            if (propertiesFilter.get_by_key && propertiesFilter.get_by_child) {
                                keyGetSub = dataValue.get(pos).key;
                                indexAddValue = position + 1;
                                if (!TextUtils.isEmpty(keyGetSub)) {
                                    List<Category> lstCategories = sqlCategories.getListCategoryByCat(keyGetSub);
                                    List<PropertyFilterValue> temp = new ArrayList<>();
                                    for (int i = 0; i < lstCategories.size(); i++) {
                                        Category category = lstCategories.get(i);

                                        PropertyFilterValue property = new PropertyFilterValue();
                                        property.key = category.category_id;
                                        property.value = category.category_name;
                                        temp.add(property);
                                    }
                                    data.get(indexAddValue).values.addAll(temp);
                                    notifyItemChanged(indexAddValue);
                                }
                            }
                        } else {
                            data.get(position).choose_value = "";
                            data.get(position).choose_value_index = pos;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                break;
            case ActionProperty.INPUT:
                holder.edtInput.setVisibility(View.VISIBLE);
                holder.rvProperties.setVisibility(View.GONE);
                holder.spnProperties.setVisibility(View.GONE);
                break;
            case ActionProperty.DIALOG:
                holder.edtInput.setVisibility(View.GONE);
                holder.rvProperties.setVisibility(View.GONE);
                holder.spnProperties.setVisibility(View.GONE);
                Toast.makeText(mContext, "Dialog", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        EditText edtInput;
        RecyclerView rvProperties;
        Spinner spnProperties;

        public ViewHolder(View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txt_propertyName);
            rvProperties = itemView.findViewById(R.id.rvSelectProperty);
            edtInput = itemView.findViewById(R.id.edt_input);
            spnProperties = itemView.findViewById(R.id.spn_value);
        }
    }
}
