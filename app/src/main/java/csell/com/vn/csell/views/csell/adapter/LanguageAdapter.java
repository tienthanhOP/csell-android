package csell.com.vn.csell.views.csell.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.Language;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

    private ArrayList<Language> data;
    private Context mContext;
    private FileSave fileGet;
    private FileSave filePut;
    private int indexCheck = 0;

    public LanguageAdapter(Context context, ArrayList<Language> list) {
        this.data = list;
        this.mContext = context;
        fileGet = new FileSave(context, Constants.GET);
        filePut = new FileSave(context, Constants.PUT);
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
        return new LanguageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvName.setText(data.get(position).getNameLanguage());

        if (fileGet.getLanguage() != null) {
            if (data.get(position).getIsoCodeCountry().equalsIgnoreCase(fileGet.getLanguage())) {
                data.get(position).isCheck = true;
                indexCheck = position;
            } else {
                data.get(position).isCheck = false;
            }
        }

        if (data.get(position).isCheck)
            holder.imgCheck.setVisibility(View.VISIBLE);
        else
            holder.imgCheck.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(v -> {

            Utilities.setLocale(mContext, data.get(position).getIsoCodeCountry());
            holder.imgCheck.setVisibility(View.VISIBLE);
            data.get(position).isCheck = true;
            filePut.putLanguage(data.get(position).getIsoCodeCountry());

            if (indexCheck != position) {
                data.get(indexCheck).isCheck = false;
                notifyItemChanged(indexCheck);
                notifyItemChanged(position);
                indexCheck = position;
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class LanguageViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        ImageView imgCheck;

        LanguageViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            imgCheck = itemView.findViewById(R.id.img_check);
        }
    }
}
