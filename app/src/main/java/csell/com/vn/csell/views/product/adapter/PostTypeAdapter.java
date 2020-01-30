package csell.com.vn.csell.views.product.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.PostTypeV1;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.product.activity.EditProductActivity;
import csell.com.vn.csell.views.product.activity.SelectCategoryActivity;
import csell.com.vn.csell.views.product.activity.ShareNotePrivateActivity;
import csell.com.vn.csell.views.product.fragment.EndCreateFragment;

/**
 * Created by cuong.nv on 4/26/2018.
 */

public class PostTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PostTypeV1> data;
    private Context context;
    private int selectOld = 0;
    private boolean isReup;
    private BaseActivityTransition baseActivityTransition;

    public PostTypeAdapter(Context context, List<PostTypeV1> data, boolean isReup) {
        this.data = data;
        this.context = context;
        this.isReup = isReup;
        baseActivityTransition = new BaseActivityTransition(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_type, parent, false);
        return new ViewHolderItem(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ViewHolderItem holderItem = (ViewHolderItem) holder;

        try {
            if (selectOld == 0){
                if (isReup) {
                    EditProductActivity.paramsProduct.put(EntityAPI.FIELD_PRIVACY, data.get(selectOld).getName());
                } else {
                    SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_PRIVACY, data.get(selectOld).getName());
                }
            }

            if (position == selectOld) {
                holderItem.cardView_Type.setCardBackgroundColor(context.getResources().getColor(R.color.blue_main));
                holderItem.from_add_note_private.setVisibility(View.VISIBLE);
                holderItem.txtPostTypeName.setTextColor(context.getResources().getColor(R.color.white_100));
            } else {
                holderItem.cardView_Type.setCardBackgroundColor(context.getResources().getColor(R.color.white_100));
                holderItem.from_add_note_private.setVisibility(View.GONE);
                holderItem.txtPostTypeName.setTextColor(context.getResources().getColor(R.color.text_end_create_product));
            }

            holderItem.txtPostTypeName.setText(data.get(position).getDescription());

            holderItem.from_add_note_private.setOnClickListener(v -> {
                holderItem.from_add_note_private.setEnabled(false);
                if (EndCreateFragment.fromNotePrivateAndFriend != null) {
                    if (EndCreateFragment.fromNotePrivateAndFriend.getVisibility() == View.VISIBLE) {
                        Intent edit = new Intent(context, ShareNotePrivateActivity.class);
                        edit.putExtra(Constants.KEY_PASSINGDATA_NOTE_PRIVATE, Utilities.contentPrivateNote);
                        edit.putExtra(Constants.KEY_PASSINGDATA_LIST_FRIEND_SHARE_NOTE, Utilities.lsFriendsNotePrivate);
                        edit.putExtra(Constants.IS_EDIT_PRIVATE_NOTE_CREATE_PRODUCT, true);
                        edit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        baseActivityTransition.transitionTo(edit, 0);
                    } else {
                        Intent notePrivate = new Intent(context, ShareNotePrivateActivity.class);
                        notePrivate.putExtra(Constants.KEY_ADD_NOTE_PRIVATE, true);
                        notePrivate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        baseActivityTransition.transitionTo(notePrivate, 0);
                    }
                } else {
                    Intent notePrivate = new Intent(context, ShareNotePrivateActivity.class);
                    notePrivate.putExtra(Constants.KEY_ADD_NOTE_PRIVATE, true);
                    notePrivate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    baseActivityTransition.transitionTo(notePrivate, 0);
                }
                holderItem.from_add_note_private.setEnabled(true);
            });

            holderItem.itemView.setOnClickListener(v -> {
                if (selectOld != position) {
                    holderItem.cardView_Type.setCardBackgroundColor(context.getResources().getColor(R.color.blue_main));
                    holderItem.txtPostTypeName.setTextColor(context.getResources().getColor(R.color.white_100));
                    holderItem.from_add_note_private.setVisibility(View.VISIBLE);
                    notifyItemChanged(selectOld);
                    selectOld = position;
                }

                if (data.get(position).getId() != Utilities.TYPE_POST_ONLY_ME) {
                    holderItem.from_add_note_private.setVisibility(View.VISIBLE);
                } else {
                    holderItem.from_add_note_private.setVisibility(View.GONE);
                }

                if (SelectCategoryActivity.paramsProduct != null) {
                    SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_PRIVACY, data.get(position).getName());
                }

                if (EditProductActivity.paramsProduct != null) {
                    EditProductActivity.paramsProduct.put(EntityAPI.FIELD_PRIVACY, data.get(position).getName());
                }
            });
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d(getClass().getSimpleName(), e.getMessage());
            Crashlytics.logException(e);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private class ViewHolderItem extends RecyclerView.ViewHolder {
        TextView txtPostTypeName;
        CardView cardView_Type;
        LinearLayout from_add_note_private;
        TextView txtAddNotePrivate;

        public ViewHolderItem(View itemView) {
            super(itemView);
            txtPostTypeName = itemView.findViewById(R.id.txtTypeName);
            cardView_Type = itemView.findViewById(R.id.cardView_Type);
            from_add_note_private = itemView.findViewById(R.id.from_add_note_private);
            txtAddNotePrivate = itemView.findViewById(R.id.txtAddNotePrivate);
        }
    }
}
