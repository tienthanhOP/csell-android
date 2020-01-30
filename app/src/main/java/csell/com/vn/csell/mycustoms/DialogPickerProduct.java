package csell.com.vn.csell.mycustoms;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import csell.com.vn.csell.R;
import csell.com.vn.csell.views.note.activity.AddNoteProductActivity;
import csell.com.vn.csell.views.product.adapter.DialogPickerProductAdapter;
import csell.com.vn.csell.models.Product;

/**
 * Created by cuong.nv on 2/23/2018.
 */

public class DialogPickerProduct extends Dialog {

    private RecyclerView rvPickerProduct;
    private TextView txtNotFoundProduct;
    private RecyclerView.LayoutManager mLayoutManager;
    private DialogPickerProductAdapter adapter;
    private EditText edtSearch;

    public DialogPickerProduct(@NonNull Context context, final TextView tvProduct, final ArrayList<Product> listProduct) {
        super(context);
        Objects.requireNonNull(getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_picker_product);
        txtNotFoundProduct = findViewById(R.id.txtNotFoundProduct);
        rvPickerProduct = findViewById(R.id.dialog_rvPickerProduct);
        edtSearch = findViewById(R.id.edt_search);
        rvPickerProduct.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        rvPickerProduct.setLayoutManager(mLayoutManager);

        if (listProduct.size() == 0) {
            txtNotFoundProduct.setVisibility(View.VISIBLE);
        } else {
            txtNotFoundProduct.setVisibility(View.GONE);
        }

        adapter = new DialogPickerProductAdapter(getContext(), listProduct, item -> {
            tvProduct.setText(item.getTitle());
            AddNoteProductActivity.productId = item.getItemid();
            AddNoteProductActivity.productName = item.getTitle();
            DialogPickerProduct.this.dismiss();
        });
        rvPickerProduct.setAdapter(adapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.findProduct(s.toString());
            }
        });

    }
}
