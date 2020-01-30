package csell.com.vn.csell.views.friend.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.friend.activity.ChatActivity;
import csell.com.vn.csell.views.friend.adapter.viewholder.FindFriendViewHolder;
import csell.com.vn.csell.views.social.activity.DetailProductActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityFirebase;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.Message;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.sqlites.SQLFriends;

/**
 * Created by cuong.nv on 3/4/2018.
 */

public class MessageAdapter extends FirebaseRecyclerAdapter<Message, FindFriendViewHolder.MessageViewHolder> {

    private Context mContext;
    private FileSave fileSave;
    FirebaseRecyclerOptions<Message> data;
    private String roomId;
    private boolean check = false;
    private int positionSeen = 0;
    private int maxIndex = -1;
    private BaseActivityTransition baseActivityTransition;

    public MessageAdapter(Context context, @NonNull FirebaseRecyclerOptions<Message> options, String id) {
        super(options);
        this.mContext = context;
        fileSave = new FileSave(mContext, Constants.GET);
        data = options;
        roomId = id;
        baseActivityTransition = new BaseActivityTransition(context);
    }

    @NonNull
    @Override
    public FindFriendViewHolder.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new FindFriendViewHolder.MessageViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull FindFriendViewHolder.MessageViewHolder holder, int position, @NonNull Message model) {

        try {
            if (model != null) {

                holder.txtContentMessage.setText(model.message_content);
//                ViewGroup.MarginLayoutParams margins = new ViewGroup.MarginLayoutParams(holder.txtContentMessage.getLayoutParams());

                if (model.sender.equals(fileSave.getUserId())) {
                    if (position == maxIndex) {
                        if (model.is_sent) {
                            holder.img_avatar_sender.setImageResource(R.drawable.ic_send_mess);
                            holder.img_avatar_sender.setVisibility(View.VISIBLE);
                        } else {
                            holder.img_avatar_sender.setVisibility(View.GONE);
                        }

                        if (model.is_seen) {
                            holder.img_avatar_sender.setVisibility(View.VISIBLE);
                            GlideApp.with(mContext)
                                    .load(!TextUtils.isEmpty(model.receiver_avatar) ? model.receiver_avatar : R.drawable.ic_logo)
                                    .placeholder(R.drawable.ic_logo)
                                    .error(R.drawable.ic_logo)
                                    .into(holder.img_avatar_sender);
                        }
                    } else {
                        holder.img_avatar_sender.setVisibility(View.GONE);
                    }

                    if (positionSeen != 0) {
                        if (holder.getAdapterPosition() == positionSeen) {
                            if (model.is_seen) {
                                holder.img_avatar_sender.setVisibility(View.VISIBLE);
                                GlideApp.with(mContext)
                                        .load(!TextUtils.isEmpty(model.receiver_avatar) ? model.receiver_avatar : R.drawable.ic_logo)
                                        .placeholder(R.drawable.ic_logo)
                                        .error(R.drawable.ic_logo)
                                        .into(holder.img_avatar_sender);
                            }
                        }
                    }

                    holder.layoutControlItemChat.setGravity(Gravity.END);
                    holder.txtContentMessage.setBackgroundColor(mContext.getResources().getColor(R.color.bg_message_send));
                    holder.txtContentMessage.setTextColor(Color.WHITE);
//                    margins.setMargins(150, 4, 0, 4);
//                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(margins);
//                    holder.txtContentMessage.setLayoutParams(layoutParams);

                    if (model.product != null) {

                        holder.cardView.setVisibility(View.VISIBLE);

//                        ViewGroup.MarginLayoutParams layoutParamsCardView =
//                                (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
//                        layoutParamsCardView.setMargins(8, 4, 4, 4);
                        holder.cardView.requestLayout();

                        holder.tvProductName.setText(model.product.getTitle());
                        holder.tvProductDescription.setText(model.product.getDescription());
                        if (model.product.getPrice() == null || model.product.getPrice() == 0) {
                            holder.tvPrice.setText(mContext.getString(R.string.contact));
                        } else {
                            String price = Utilities.formatMoney(model.product.getPrice(), model.product.getCurrency());
                            holder.tvPrice.setText(price);
                        }
                        GlideApp.with(mContext)
                                .load(!TextUtils.isEmpty(model.product.getImages().get(0).getPath()) ?
                                        model.product.getImages().get(0).getPath() : R.drawable.noimage)
                                .placeholder(R.drawable.noimage)
                                .error(R.drawable.noimage)
                                .into(holder.imgProduct);

                    } else {
                        holder.cardView.setVisibility(View.GONE);
                    }

                } else {
                    if (position == maxIndex) {
                        if (model.is_pending != null) {
                            if (model.is_pending) {
                                ((ChatActivity) mContext).layoutAccept.setVisibility(View.VISIBLE);
                                ((ChatActivity) mContext).layoutType.setVisibility(View.GONE);
                            } else {
                                ((ChatActivity) mContext).updatePending();
                            }
                        }
                    }

                    holder.img_avatar_receiver.setVisibility(View.VISIBLE);
                    if (model.is_seen) {
                        holder.img_avatar_sender.setVisibility(View.GONE);
                    }

                    GlideApp.with(mContext)
                            .load(!TextUtils.isEmpty(model.sender_avatar) ? model.sender_avatar : R.drawable.ic_logo)
                            .placeholder(R.drawable.ic_logo)
                            .error(R.drawable.ic_logo)
                            .into(holder.img_avatar_receiver);

                    holder.layoutControlItemChat.setGravity(Gravity.START);
                    holder.txtContentMessage.setBackgroundColor(mContext.getResources().getColor(R.color.white_100));
                    holder.txtContentMessage.setTextColor(mContext.getResources().getColor(R.color.text_friend_send));

                    if (model.product != null) {

                        holder.cardView.setVisibility(View.VISIBLE);

                        holder.tvProductName.setText(model.product.getTitle());
                        holder.tvProductDescription.setText(model.product.getDescription());

                        if (model.product.getPrice() == null || model.product.getPrice() == 0) {
                            holder.tvPrice.setText(mContext.getString(R.string.contact));
                        } else {
                            String price = Utilities.formatMoney(model.product.getPrice(), model.product.getCurrency());
                            holder.tvPrice.setText(price);
                        }

                        GlideApp.with(mContext)
                                .load(!TextUtils.isEmpty(model.product.getImages().get(0).getPath())
                                        ? model.product.getImages().get(0).getPath() : R.drawable.noimage)
                                .placeholder(R.drawable.noimage)
                                .error(R.drawable.noimage)
                                .into(holder.imgProduct);

                    } else {
                        holder.cardView.setVisibility(View.GONE);
                    }
                }

                if (position > 0) {
                    Message old = data.getSnapshots().get(position - 1);
                    if (old.sender.equals(model.sender)) {
                        holder.img_avatar_receiver.setVisibility(View.GONE);
                    } else {
                        if (model.sender.equals(fileSave.getUserId())) {
                            holder.img_avatar_receiver.setVisibility(View.GONE);
                        } else {
                            holder.img_avatar_receiver.setVisibility(View.VISIBLE);
                        }
                    }
                }

                if (!model.sender.equals(fileSave.getUserId())) {
                    String key = getRef(position).getKey();
                    model.is_seen = true;

                    FirebaseDatabase.getInstance().getReference().child(EntityFirebase.TableDirectMessages).child(roomId)
                            .child(key).setValue(model);

                }

                if (!model.sender.equals(fileSave.getUserId())) {
                    if (position == getItemCount() - 1) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(EntityFirebase.FieldIsSeen, true);
                        map.put(EntityFirebase.FieldCountUnread, 0);

                        FirebaseDatabase.getInstance().getReference().child(EntityFirebase.TableLastMessages)
                                .child(fileSave.getUserId()).child(roomId).updateChildren(map);

                        FirebaseDatabase.getInstance().getReference().child(EntityFirebase.TableLastMessages)
                                .child(model.sender).child(roomId).updateChildren(map);
                    }
                }

                holder.cardView.setOnClickListener(v -> showDetail(model.product));

            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    @Override
    public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {
        super.onChildChanged(type, snapshot, newIndex, oldIndex);

        switch (type) {
            case ADDED:
                try {
                    Message message = snapshot.getValue(Message.class);
                    if (message.is_seen != null) {
                        if (!message.is_seen) {
                            check = false;
                        }
                        maxIndex = newIndex;
                        notifyItemInserted(newIndex);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CHANGED:
                try {
                    Message message1 = snapshot.getValue(Message.class);
                    if (message1.is_seen != null) {
                        if (message1.is_seen) {
                            check = false;
                        }
                        maxIndex = newIndex;
                    }

                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    private void showDetail(Product item) {
        if (item.getUserInfo().getUid().equalsIgnoreCase(fileSave.getUserId())) {
            Intent detail = new Intent(mContext, DetailProductActivity.class);
            detail.putExtra(Constants.TEMP_PRODUCT_KEY, item.getItemid());
            baseActivityTransition.transitionTo(detail, 0);
        } else {
            Intent detail = new Intent(mContext, DetailProductActivity.class);
            detail.putExtra(Constants.TEMP_PRODUCT, item);
            detail.putExtra(Constants.TEMP_PRODUCT_KEY, item.getItemid());
            baseActivityTransition.transitionTo(detail, 0);
        }
    }

    @Override
    public int getItemCount() {
        return data.getSnapshots().size();
    }

}
