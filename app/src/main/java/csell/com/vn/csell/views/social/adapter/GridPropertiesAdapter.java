package csell.com.vn.csell.views.social.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.models.ProductResponseV1;
import csell.com.vn.csell.models.Properties;
import csell.com.vn.csell.sqlites.SQLLanguage;

//import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by cuong.nv on 3/30/2018.
 */

public class GridPropertiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Properties> properties;
    private ProductResponseV1 product;
    private FileSave fileGet;
    private SQLLanguage sqlLanguage;

    public GridPropertiesAdapter(Context context, ArrayList<Properties> properties, ProductResponseV1 product) {
        this.context = context;
        this.properties = properties;
        this.product = product;
        fileGet = new FileSave(context, Constants.GET);
        sqlLanguage = new SQLLanguage(context);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View convertView;

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.item_grid_property, null);

        return new ViewHolderItem(convertView);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {

        ViewHolderItem holder = (ViewHolderItem) holder1;

        if (properties.size() != 0) {
            try {
                try {
                    String nameCate = "";
                    if (!TextUtils.isEmpty(properties.get(position).property_name + ""))
                        nameCate = sqlLanguage.getDisplayNameByCatId(properties.get(position).property_name, fileGet.getLanguage());

                    if (!TextUtils.isEmpty(nameCate))
                        holder.txtDisplayName.setText(nameCate + ": ");
                    else
                        holder.txtDisplayName.setText(properties.get(position).display_name + ": ");
                } catch (NumberFormatException e) {
                    holder.txtDisplayName.setText(properties.get(position).display_name + ": ");
                }

                holder.txtPropertyName.setPadding(8, 0, 0, 0);
                holder.txtDisplayName.setVisibility(View.VISIBLE);
                holder.imgIconProp.setVisibility(View.GONE);
                GlideApp.with(context).load(properties.get(position).image)
                        .placeholder(R.drawable.noimage)
                        .error(R.drawable.noimage).into(holder.imgIconProp);

                if (product != null) {
                    HashMap<String, Object> propertyMap = new HashMap<>();
                    if (product.getAttributes() != null) {
                        propertyMap = product.getAttributes();
                    }
                    if (propertyMap.size() != 0) {
                        Object valueObj = propertyMap.get(properties.get(position).property_name);
                        String value = "";
                        if (valueObj != null) {
                            if (valueObj instanceof Double) {
                                int valuInt = ((Double) valueObj).intValue();
                                value = String.valueOf(valuInt);
                            } else {
                                value = String.valueOf(valueObj);
                            }
                        }
                        holder.txtPropertyName.setText(TextUtils.isEmpty(value) ? "-" : value);
                    } else {
                        holder.txtPropertyName.setText("-");
                    }
                }
            } catch (Exception e1) {
                if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e1.getMessage());
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    private class ViewHolderItem extends RecyclerView.ViewHolder {
        TextView txtPropertyName;
        TextView txtDisplayName;
        ImageView imgIconProp;

        public ViewHolderItem(View itemView) {
            super(itemView);

            txtPropertyName = itemView.findViewById(R.id.txtPropertyName);
            txtDisplayName = itemView.findViewById(R.id.txtDisplayName);
            imgIconProp = itemView.findViewById(R.id.imgIconProp);
        }
    }
}
