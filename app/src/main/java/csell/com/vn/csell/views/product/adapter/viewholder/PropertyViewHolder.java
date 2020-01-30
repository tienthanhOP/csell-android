package csell.com.vn.csell.views.product.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import csell.com.vn.csell.R;

public class PropertyViewHolder extends RecyclerView.ViewHolder {

    public TextView txtPropertyName;
    public TextView tvSeekMin;
    public TextView tvSeekMax;
    public EditText edtValue;
    public RecyclerView rvSelectedProperty;
    public SeekBar seekBar;
    public Spinner spinner;
    public Spinner spnTypeSim;
    public LinearLayout fromSelect;
    public LinearLayout fromSeekBar;
    public LinearLayout fromTypeSim;
    public LinearLayout fromInputSpinner;
    public Spinner spnValue;


    public PropertyViewHolder(View itemView) {
        super(itemView);
        edtValue = itemView.findViewById(R.id.edt_acreage);
        txtPropertyName = itemView.findViewById(R.id.txt_propertyName);
        seekBar = itemView.findViewById(R.id.seekBarKilomets);
        spinner = itemView.findViewById(R.id.spn_unit_acreage);
        rvSelectedProperty = itemView.findViewById(R.id.rvSelectProperty);
        rvSelectedProperty.setHasFixedSize(true);
        tvSeekMin = itemView.findViewById(R.id.txtSeekBarMin);
        tvSeekMax = itemView.findViewById(R.id.txtSeekBarMax);
        fromSelect = itemView.findViewById(R.id.from_select);
        fromSeekBar = itemView.findViewById(R.id.fromSeekBar);
        spnTypeSim = itemView.findViewById(R.id.spn_type_sim);
        fromTypeSim = itemView.findViewById(R.id.from_type_sim);
        fromInputSpinner = itemView.findViewById(R.id.from_input_spinner);
        spnValue = itemView.findViewById(R.id.spn_value);
    }
}
