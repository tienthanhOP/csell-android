package csell.com.vn.csell.views.social.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.interfaces.OnLoadmoreListener;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.friend.activity.FriendDetailsActivity;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.account.activity.PersonalPageActivity;
import csell.com.vn.csell.views.social.activity.DetailProductActivity;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.EntityFirebase;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FirebaseDBUtil;
import csell.com.vn.csell.constants.TimeAgo;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.commons.PushNotifications;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.models.Comment;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.mycustoms.CommentDialog;
import csell.com.vn.csell.mycustoms.ListUserLikeCommentsDialog;
import csell.com.vn.csell.sqlites.SQLFriends;
import csell.com.vn.csell.views.social.adapter.viewholder.SocialViewHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

/**
 * Created by chuc.nq on 4/4/2018.
 */

public class SocialNewFeedsFirebaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private OnLoadmoreListener onLoadmoreListener;

    private Context mContext;
    private FileSave fileGet;
    private FileSave filePut;
    private List<Product> data;
    private DatabaseReference dbReference;
    private SQLFriends sqlFriends;
    private boolean isFromSocial;
    private BaseActivityTransition baseActivityTransition;
    private long mLastClickTime = 0;
    private boolean loading = false;

    public SocialNewFeedsFirebaseAdapter(Context mContext, List<Product> data, boolean isFromSocial, OnLoadmoreListener onLoadmoreListener) {
        this.mContext = mContext;
        this.data = data;
        this.isFromSocial = isFromSocial;
        fileGet = new FileSave(mContext, Constants.GET);
        filePut = new FileSave(mContext, Constants.PUT);
        dbReference = FirebaseDBUtil.getDatebase().getReference();
        sqlFriends = new SQLFriends(mContext);
        baseActivityTransition = new BaseActivityTransition(mContext);
        this.onLoadmoreListener = onLoadmoreListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_social, parent, false);
            return new SocialViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_bottom_layout, parent, false);
            return new ProgressLoadmoreViewHolder(itemView);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        try {
            if (viewHolder instanceof SocialViewHolder) {
                SocialViewHolder holder = (SocialViewHolder) viewHolder;

                Product model = data.get(position);
                if (model != null) {
                    getLikes(holder, model.getItemid());
                    getComments(holder, model.getItemid());
                    getLastComment(holder, model.getItemid());
                    initEvent(holder, model);
                    if (model.getType() != null) {
                        if (model.getCatid().startsWith(Utilities.SIM) || model.getType() == 2) {
                            holder.layoutSim.setVisibility(View.VISIBLE);
                            holder.layoutProduct.setVisibility(View.GONE);

                            holder.tvSimDes.setText(model.getDescription().trim());
                            holder.tvSimDes.post(() -> {
                                int lineCount = holder.tvSimDes.getLineCount();
                                if (lineCount == 0 || lineCount > 4) {
                                    holder.tvSimDes.setMaxLines(4);
                                    holder.txtReadMore.setVisibility(View.VISIBLE);
                                } else {
                                    holder.txtReadMore.setVisibility(View.GONE);
                                }
                            });

                            if (model.getType() == 2) {
                                holder.fromTitle.setVisibility(View.GONE);
                                holder.layoutPriceSim.setVisibility(View.GONE);
                                GlideApp.with(mContext)
                                        .load(model.getBackground() + "")
                                        .placeholder(R.drawable.bg_3)
                                        .error(R.drawable.bg_3)
                                        .into(holder.imgSimBG);
                            } else {
                                GlideApp.with(mContext)
                                        .load(model.getImage().getPath() + "")
                                        .placeholder(R.drawable.bg_3)
                                        .error(R.drawable.bg_3)
                                        .into(holder.imgSimBG);
                                holder.fromTitle.setVisibility(View.VISIBLE);
                                holder.layoutPriceSim.setVisibility(View.VISIBLE);
                                if (model.getPrice() == null || model.getPrice() == 0) {
                                    holder.tvSimPrice.setText(mContext.getString(R.string.contact));
                                } else {
                                    String price = Utilities.formatMoney(model.getPrice(), model.getCurrency());
                                    holder.tvSimPrice.setText(price);
                                }
                            }

                            holder.tvSimTitle.setText(model.getTitle());
                            //                    if (model.getDescription().length() > 130){
                            //                        holder.tvSimDes.setText(model.getDescription().substring(0,130));
                            //                    }else {
                            //                        holder.tvSimDes.setText(model.getDescription());
                            //                    }

                        } else {
                            holder.layoutProduct.setVisibility(View.VISIBLE);
                            holder.layoutSim.setVisibility(View.GONE);
                            holder.tvNameProduct.setText(model.getTitle());
                            holder.tvDescription.setText(model.getDescription());

                            if (model.getPrice() == null || model.getPrice() == 0) {
                                holder.tvPrice.setText(mContext.getString(R.string.contact));
                            } else {
                                String price = Utilities.formatMoney(model.getPrice(), model.getCurrency());
                                holder.tvPrice.setText(price);
                            }
                            if (model.getImage() != null) {
                                if (model.getImage().getPath() != null) {
                                    String linkImage = Utilities.subStringForGetImages(model.getImage().getPath());
                                    int getSuffix = 0;
                                    if (model.getImage().getSuffixes() != null)
                                        getSuffix = model.getImage().getSuffixes().size();
                                    if (getSuffix > 1)
                                        linkImage = linkImage + "-" + Collections.min(Utilities.SUFFIXES) + Constants.JPG;
                                    else
                                        linkImage = linkImage + Constants.JPG;

                                    GlideApp.with(mContext)
                                            .load(linkImage)
                                            .placeholder(R.drawable.noimage)
                                            .error(R.drawable.noimage)
                                            .into(holder.imgProduct);
                                }
                            } else {

                                GlideApp.with(mContext)
                                        .load(Constants.LINK_DEFAUT_NOT_CHOOSE)
                                        .placeholder(R.drawable.noimage)
                                        .error(R.drawable.noimage)
                                        .into(holder.imgProduct);
                            }
                        }
                    }

                    if (model.getReup() != null) {
                        if (model.getReup()) {
                            holder.layoutReup.setVisibility(View.VISIBLE);
                        } else {
                            holder.layoutReup.setVisibility(View.GONE);
                        }
                    } else {
                        holder.layoutReup.setVisibility(View.GONE);
                    }

                    if (model.getFollowItem() != null) {
                        if (model.getFollowItem()) {
                            holder.layoutFollow.setVisibility(View.VISIBLE);
                        } else {
                            holder.layoutFollow.setVisibility(View.GONE);
                        }
                    } else {
                        holder.layoutFollow.setVisibility(View.GONE);
                    }


                    if (model.getPrivacyType() != null) {
                        if (model.getPrivacyType() == 1) {
                            holder.imgTypePost.setImageResource(R.drawable.ic_public_black);
                        } else if (model.getPrivacyType() == 3) {
                            holder.imgTypePost.setImageResource(R.drawable.ic_only_me);
                        }
                    }

                    try {
                        if (model.getUserInfo() != null) {
                            if (!TextUtils.isEmpty(model.getUserInfo().getDisplayname())) {
                                holder.tvNameOwner.setText(model.getUserInfo().getDisplayname());
                            } else {
                                holder.tvNameOwner.setText("Người mới");
                            }
                        }


                        GlideApp.with(mContext)
                                .load(!TextUtils.isEmpty(model.getUserInfo().getAvatar()) ? model.getUserInfo().getAvatar() + "" : R.drawable.ic_logo)
                                .placeholder(R.drawable.ic_logo)
                                .error(R.drawable.ic_logo)
                                .into(holder.imgFriendAvatar);

                        GlideApp.with(mContext)
                                .load(fileGet.getUserAvatar() + "")
                                .placeholder(R.drawable.ic_logo)
                                .error(R.drawable.ic_logo)
                                .into(holder.imgUserComment);


                        String timeAgo = TimeAgo.toRelative(Utilities.convertDateStringToMilisAllType(model.getDateShared()),
                                System.currentTimeMillis(), 1);


                        holder.tvLastUpdated.setText(timeAgo);


                    } catch (Exception e1) {
                        if (BuildConfig.DEBUG) Log.d(getClass().getSimpleName(), e1.getMessage());
                    }
                }
            } else {
                try {
                    ProgressLoadmoreViewHolder holder = (ProgressLoadmoreViewHolder) viewHolder;
                    holder.progressBar.setIndeterminate(true);
                    if (!loading) {
                        if (onLoadmoreListener != null) {
                            onLoadmoreListener.onLoadMore(data.size());
                        }
                    } else {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                } catch (Exception ignored) {

                }
            }
        } catch (Exception ignored) {
            Log.d("1111", ignored.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        try {
            return data.get(position) != null ? VIEW_ITEM : VIEW_PROG;
        } catch (Exception e) {
            return VIEW_PROG;
        }
    }

    public void setLoaded(boolean isRun) {
        loading = isRun;
    }

    public void hideLoadmore() {
        loading = true;
        notifyDataSetChanged();
    }

    public void update(List<Product> newItems) {
        loading = true;
        data.addAll(newItems);
        notifyDataSetChanged();
    }

    public void setData(List<Product> products) {
        data.clear();
        data.addAll(products);
        notifyDataSetChanged();
    }

    public void removeLastItem() {
        notifyDataSetChanged();
    }

    @SuppressLint("RestrictedApi")
    private void showMenu(SocialViewHolder holder) {
        try {
            PopupMenu popupMenu = new PopupMenu(mContext, holder.layoutChangedPostType);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_type_post, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                switch (id) {
                    case R.id.menu_public:
                        holder.imgTypePost.setImageResource(R.drawable.ic_public_black);
                        updatePostType(data.get(holder.getAdapterPosition()).getItemid(), 1);
                        break;
                    case R.id.menu_only_me:
                        holder.imgTypePost.setImageResource(R.drawable.ic_only_me);
                        updatePostType(data.get(holder.getAdapterPosition()).getItemid(), 3);
                        break;

                }
                return false;
            });

            MenuPopupHelper menuHelper = new MenuPopupHelper(mContext, (MenuBuilder) popupMenu.getMenu(), holder.layoutChangedPostType);
            menuHelper.setForceShowIcon(true);
            menuHelper.show();
//            popupMenu.show();

        } catch (Exception e) {

            PopupMenu popupMenu = new PopupMenu(mContext, holder.layoutChangedPostType);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu_type_post, popupMenu.getMenu());


            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                switch (id) {
                    case R.id.menu_public:
                        holder.imgTypePost.setImageResource(R.drawable.ic_public_black);
                        updatePostType(data.get(holder.getAdapterPosition()).getItemid(), 1);
                        break;
                    case R.id.menu_only_me:
                        holder.imgTypePost.setImageResource(R.drawable.ic_only_me);
                        updatePostType(data.get(holder.getAdapterPosition()).getItemid(), 3);
                        break;

                }
                return false;
            });

            popupMenu.show();
        }
    }


    private void updatePostType(String productKey, int type) {
        try {
            PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
            if (postAPI != null) {
                HashMap<String, Object> map = new HashMap<>();
                map.put(EntityAPI.FIELD_PRIVACY_TYPE, type);
                map.put(EntityAPI.FIELD_ITEM_ID_2, productKey);
                Call<JSONResponse<Object>> jsonResponseCall = postAPI.updatePostType(map);
                jsonResponseCall.enqueue(new Callback<JSONResponse<Object>>() {
                    @Override
                    public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                        if (response.isSuccessful()) {
                            JSONResponse<Object> result = response.body();
                            if (result.getSuccess() != null) {
                                if (result.getSuccess()) {
                                    Toast.makeText(MainActivity.mainContext, "Cập nhật", Toast.LENGTH_SHORT).show();
                                } else {
                                    Utilities.refreshToken(MainActivity.mainContext, result.getMessage() + "");
                                }
                            }
                            String msg = response.body().getError();
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (response.errorBody() != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = (String) jsonObject.get(Constants.ERROR);
                                    if (!TextUtils.isEmpty(msg)) {
                                        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        jsonResponseCall.cancel();
                    }

                    @Override
                    public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                        jsonResponseCall.cancel();
                        Toast.makeText(MainActivity.mainContext, "Thử lại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void initEvent(SocialViewHolder holder, Product product) {

        holder.from_show_like.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            filePut.putProductIdCurrentSelect(data.get(holder.getAdapterPosition()).getItemid());
            filePut.putProductNameCurrentSelect(data.get(holder.getAdapterPosition()).getTitle());
            ListUserLikeCommentsDialog likeCommentsDialog = new ListUserLikeCommentsDialog(mContext,
                    data.get(holder.getAdapterPosition()).lsUserId, false);
            likeCommentsDialog.show();
        });

        holder.tvCountComments.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            nextCommentActivity(product);
        });

        holder.layoutChangedPostType.setOnClickListener(v -> {
            try {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (product.getUserInfo().getUid().equals(fileGet.getUserId())) {
                    showMenu(holder);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        });

        holder.btnLike.setOnClickListener(v ->
        {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            actionLike(holder);
        });

        holder.layoutSim.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            filePut.putCurrentPositionSocial(holder.getAdapterPosition());
            showDetail(product, holder.getAdapterPosition());
        });
        holder.frameLayout.setOnClickListener(v -> holder.layoutSim.performClick());
        holder.tvDescriptionHolder.setOnClickListener(v -> holder.layoutSim.performClick());

        holder.layoutProduct.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            filePut.putCurrentPositionSocial(holder.getAdapterPosition());
            showDetail(product, holder.getAdapterPosition());
        });

        holder.btnShare.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Welcome to CSELL");
            sendIntent.setType("text/plain");
            transitionTo(sendIntent, 0);
        });


        /*Comment click 3 view*/
        holder.btnComment.setOnClickListener(v -> nextCommentActivity(product));
        holder.itemFriendComment.setOnClickListener(v -> nextCommentActivity(product));
        holder.itemComment.setOnClickListener(v -> nextCommentActivity(product));
        holder.imgFriendAvatar.setOnClickListener(v -> nextFriendDetailActivity(product));
        holder.tvNameOwner.setOnClickListener(v -> nextFriendDetailActivity(product));
    }


    private void actionLike(SocialViewHolder holder) {
        try {
            if (data.get(holder.getAdapterPosition()).itMeLikePost) {
                disLike(holder);
            } else {
                setLike(holder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void disLike(SocialViewHolder holder) {
        data.get(holder.getAdapterPosition()).itMeLikePost = false;
        data.get(holder.getAdapterPosition()).totalLike--;
        if (data.get(holder.getAdapterPosition()).totalLike > 0) {
            holder.from_show_like.setVisibility(View.VISIBLE);
            holder.tvCountLike.setText(data.get(holder.getAdapterPosition()).totalLike + "");
        } else {
            holder.from_show_like.setVisibility(View.INVISIBLE);
            holder.tvCountLike.setText("");
        }
        updateViewLike(holder, false);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(fileGet.getUserId(), null);
        dbReference.child(EntityFirebase.Field_User_Likes)
                .child(data.get(holder.getAdapterPosition()).getItemid())
                .updateChildren(hashMap);
    }

    @SuppressLint("SetTextI18n")
    private void setLike(SocialViewHolder holder) {
        data.get(holder.getAdapterPosition()).itMeLikePost = true;
        data.get(holder.getAdapterPosition()).totalLike++;
        if (data.get(holder.getAdapterPosition()).totalLike > 0) {
            holder.from_show_like.setVisibility(View.VISIBLE);
            holder.tvCountLike.setText(data.get(holder.getAdapterPosition()).totalLike + "");
        } else {
            holder.from_show_like.setVisibility(View.INVISIBLE);
            holder.tvCountLike.setText("");
        }
        updateViewLike(holder, true);
        HashMap<String, Object> maplikes = data.get(holder.getAdapterPosition()).mapUserLikes;
        HashMap<String, Object> hashMap = new HashMap<>();

        if (maplikes == null) {
            hashMap.put(fileGet.getUserId(), true);
            dbReference.child(EntityFirebase.Field_User_Likes)
                    .child(data.get(holder.getAdapterPosition()).getItemid())
                    .updateChildren(hashMap);
        } else {
            hashMap.put(fileGet.getUserId(), true);
            dbReference.child(EntityFirebase.Field_User_Likes)
                    .child(data.get(holder.getAdapterPosition()).getItemid())
                    .updateChildren(hashMap);
        }

        PushNotifications pushNotifications = new PushNotifications(mContext);
        HashMap<String, Object> mapNotificationLike = new HashMap<>();
        mapNotificationLike.put(EntityAPI.FIELD_NOTIFICATION_TITLE, "Like");
        mapNotificationLike.put(EntityAPI.FIELD_NOTIFICATION_BODY, fileGet.getDisplayName() + " "
                + mContext.getString(R.string.text_body_liked) + " "
                + data.get(holder.getAdapterPosition()).getTitle() + " "
                + mContext.getString(R.string.text_body_of_you));
        mapNotificationLike.put(EntityAPI.FIELD_NOTIFICATION_TARGET_NAME, data.get(holder.getAdapterPosition()).getTitle());
        mapNotificationLike.put(EntityAPI.FIELD_NOTIFICATION_ACTION, Utilities.ACTION_LIKE);
        mapNotificationLike.put(EntityAPI.FIELD_DATE_CREATED, ServerValue.TIMESTAMP);
        mapNotificationLike.put(EntityAPI.FIELD_NOTIFICATION_DATA, data.get(holder.getAdapterPosition()).getItemid());
        mapNotificationLike.put(EntityAPI.FIELD_NOTIFICATION_SENDER, fileGet.getUserId());
        mapNotificationLike.put(EntityAPI.FIELD_NOTIFICATION_SENDER_NAME, fileGet.getDisplayName());
        mapNotificationLike.put(EntityAPI.FIELD_NOTIFICATION_SENDER_AVATAR, fileGet.getUserAvatar());
        mapNotificationLike.put(EntityAPI.FIELD_NOTIFICATION_IS_SEEN, false);

        if (data.get(holder.getAdapterPosition()).getUserInfo() != null) {
            if (!fileGet.getUserId().equals(data.get(holder.getAdapterPosition()).getUserInfo().getUid())) {
                if (!TextUtils.isEmpty(data.get(holder.getAdapterPosition()).getUserInfo().getUid())) {
                    pushNotifications.pushLike(mapNotificationLike, data.get(holder.getAdapterPosition()).getUserInfo().getUid());
                } else {
                    if (FriendDetailsActivity.friend != null) {
                        pushNotifications.pushLike(mapNotificationLike, FriendDetailsActivity.friend.getUid());
                    }
                }
            }

        }

    }

    @SuppressLint("StaticFieldLeak")
    private void getLikes(SocialViewHolder holder, String pid) {

        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    dbReference.child(EntityFirebase.Field_User_Likes).child(pid)
                            .addValueEventListener(new ValueEventListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        try {
                                            Object o = dataSnapshot.getValue();
                                            HashMap<String, Object> mapLikes = (HashMap<String, Object>) o;

                                            final int position = holder.getAdapterPosition();
                                            if (position != RecyclerView.NO_POSITION) {

                                                data.get(position).lsUserId = new ArrayList<>();
                                                try {
                                                    assert mapLikes != null;
                                                    data.get(position).lsUserId.addAll(mapLikes.keySet());
                                                } catch (Exception e) {
                                                    if (BuildConfig.DEBUG)
                                                        Log.d(getClass().getSimpleName(), e.getMessage());
                                                }

                                                int i = mapLikes.size();

                                                holder.tvCountLike.setText(i + "");
                                                if (mapLikes.get(fileGet.getUserId()) == null) {
                                                    data.get(position).totalLike = mapLikes.size();
                                                    data.get(position).itMeLikePost = false;
                                                    updateViewLike(holder, false);
                                                } else {
                                                    data.get(position).totalLike = i;
                                                    if ((boolean) mapLikes.get(fileGet.getUserId())) {
                                                        data.get(position).itMeLikePost = true;
                                                        updateViewLike(holder, true);
                                                    } else {
                                                        data.get(position).itMeLikePost = false;
                                                        updateViewLike(holder, false);
                                                    }
                                                }

                                                data.get(position).mapUserLikes = mapLikes;
                                                if (i > 0) {
                                                    holder.from_show_like.setVisibility(View.VISIBLE);
                                                    holder.tvCountLike.setText(data.get(position).totalLike + "");
                                                } else {
                                                    holder.from_show_like.setVisibility(View.INVISIBLE);
                                                    holder.tvCountLike.setText("");
                                                }
                                            }
                                        } catch (Exception e) {
                                            Crashlytics.logException(e);
                                        }

                                    } else {
                                        try {
                                            final int position = holder.getAdapterPosition();
                                            if (position != RecyclerView.NO_POSITION) {
                                                data.get(position).totalLike = 0;
                                                data.get(position).itMeLikePost = false;
                                                holder.tvCountLike.setText("");
                                                updateViewLike(holder, false);
                                            }
                                        } catch (Exception e) {
                                            Crashlytics.logException(e);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Crashlytics.logException(databaseError.toException());
                                }
                            });
                    return null;
                }
            }.executeOnExecutor(THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            Crashlytics.logException(e);
        }

    }

    private void updateViewLike(SocialViewHolder holder, boolean hasLike) {
        if (data.get(holder.getAdapterPosition()).totalLike > 0 || data.get(holder.getAdapterPosition()).totalComments > 0) {
            holder.layoutCountLike.setVisibility(View.VISIBLE);
        } else {
            holder.layoutCountLike.setVisibility(View.GONE);
        }

        if (data.get(holder.getAdapterPosition()).totalLike > 0) {
            holder.imgCountLike.setVisibility(View.VISIBLE);
            holder.imgCountLike.setImageResource(R.drawable.icon_love);
        } else {
            holder.imgCountLike.setVisibility(View.GONE);
            holder.imgCountLike.setImageDrawable(null);
        }

        if (hasLike) {
            holder.imgLike.setImageResource(R.drawable.icon_love);
            holder.tvLike.setTextColor(mContext.getResources().getColor(R.color.red_100));
        } else {
            holder.imgLike.setImageResource(R.drawable.icon_button_like);
            holder.tvLike.setTextColor(mContext.getResources().getColor(R.color.dark_blue_100));
        }
    }

    private void getComments(SocialViewHolder holder, String pid) {

        dbReference.child(EntityFirebase.TableComments).child(pid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                data.get(holder.getAdapterPosition()).totalComments = dataSnapshot.getChildrenCount();
                                holder.tvCountComments.setVisibility(View.VISIBLE);
                                String text;
                                try {
                                    text = data.get(holder.getAdapterPosition()).totalComments + " "
                                            + mContext.getString(R.string.comment).toLowerCase();
                                } catch (Exception e) {
                                    text = data.get(holder.getAdapterPosition()).totalComments + " " + mContext.getResources().getString(R.string.comment);
                                }

                                holder.tvCountComments.setText(text);
                                holder.tvCountComments.setVisibility(View.VISIBLE);
                                holder.layoutCountLike.setVisibility(View.VISIBLE);
                            } else {
                                if (data.get(holder.getAdapterPosition()).totalLike == 0) {
                                    holder.layoutCountLike.setVisibility(View.GONE);
                                }
                                data.get(holder.getAdapterPosition()).totalComments = 0;
                                holder.tvCountComments.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                            Crashlytics.logException(e);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Crashlytics.logException(databaseError.toException());
                    }
                });
    }

    private void getLastComment(SocialViewHolder holder, String pid) {
        dbReference.child(EntityFirebase.TableComments)
                .child(data.get(holder.getAdapterPosition()).getItemid())
                .orderByChild(EntityFirebase.FieldDateCreated)
                .limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.getChildrenCount() == 0) {
                                holder.itemFriendComment.setVisibility(View.GONE);
                                return;
                            }
                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                // hiển thị comment cuối cùng
                                if (snapshot1.exists()) {
                                    Comment comment = snapshot1.getValue(Comment.class);
                                    if (comment.product_id.equals(pid)) {
                                        if (comment.uid.equals(fileGet.getUserId())) {

                                            holder.itemFriendComment.setVisibility(View.VISIBLE);

                                            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                                            stringBuilder.append(comment.display_name.trim());
                                            stringBuilder.setSpan(new ClickableSpan() {
                                                @Override
                                                public void onClick(@NonNull View widget) {
                                                    if (!comment.uid.equals(fileGet.getUserId())) {

                                                        UserRetro friend = new UserRetro();
                                                        friend.setUid(comment.uid);
                                                        friend.setDisplayname(comment.display_name);
                                                        Intent detail = new Intent(mContext, FriendDetailsActivity.class);
                                                        detail.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, friend);
                                                        detail.putExtra(Constants.KEY_PASSINGDATA_SELECT_REPLY_COMMENT_USER, true);
                                                        transitionTo(detail, 0);
                                                    } else {
                                                        Intent detail = new Intent(mContext, PersonalPageActivity.class);
                                                        transitionTo(detail, 0);
                                                    }

                                                }

                                                @Override
                                                public void updateDrawState(@NonNull TextPaint ds) {
                                                    super.updateDrawState(ds);
                                                    ds.setColor(mContext.getResources().getColor(R.color.dark_blue_100));
                                                    ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                                    ds.setUnderlineText(false);
                                                }
                                            }, 0, comment.display_name.trim().length(), 0);

                                            stringBuilder.append(" ");
                                            if (!TextUtils.isEmpty(comment.reply_display_name)) {
                                                stringBuilder.append(comment.reply_display_name).append(" ").append(comment.content);
                                            } else {
                                                stringBuilder.append(comment.content);
                                            }

                                            holder.tvContentFriendComment.setMovementMethod(LinkMovementMethod.getInstance());
                                            holder.tvContentFriendComment.setText(stringBuilder);

                                        } else {

                                            UserRetro f = sqlFriends.checkIsFriendById(comment.uid);

                                            if (f != null) {


                                                holder.itemFriendComment.setVisibility(View.VISIBLE);

                                                SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                                                stringBuilder.append(comment.display_name.trim());
                                                stringBuilder.setSpan(new ClickableSpan() {
                                                    @Override
                                                    public void onClick(@NonNull View widget) {
                                                        if (!comment.uid.equals(fileGet.getUserId())) {

                                                            UserRetro friend = new UserRetro();
                                                            friend.setUid(comment.uid);
                                                            friend.setDisplayname(comment.display_name);
                                                            Intent detail = new Intent(mContext, FriendDetailsActivity.class);
                                                            detail.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, friend);
                                                            detail.putExtra(Constants.KEY_PASSINGDATA_SELECT_REPLY_COMMENT_USER, true);
                                                            transitionTo(detail, 0);
                                                        } else {
                                                            Intent detail = new Intent(mContext, PersonalPageActivity.class);
                                                            transitionTo(detail, 0);
                                                        }
                                                    }

                                                    @Override
                                                    public void updateDrawState(@NonNull TextPaint ds) {
                                                        super.updateDrawState(ds);
                                                        ds.setColor(mContext.getResources().getColor(R.color.dark_blue_100));
                                                        ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                                        ds.setUnderlineText(false);
                                                    }
                                                }, 0, comment.display_name.trim().length(), 0);

                                                stringBuilder.append(" ");
                                                if (!TextUtils.isEmpty(comment.reply_display_name)) {
                                                    stringBuilder.append(comment.reply_display_name).append(" ").append(comment.content);
                                                } else {
                                                    stringBuilder.append(comment.content);
                                                }

                                                holder.tvContentFriendComment.setMovementMethod(LinkMovementMethod.getInstance());
                                                holder.tvContentFriendComment.setText(stringBuilder);

                                            } else {
                                                holder.itemFriendComment.setVisibility(View.GONE);
                                            }
                                        }

                                    } else {
                                        holder.itemFriendComment.setVisibility(View.GONE);
                                    }
                                } else {
                                    holder.itemFriendComment.setVisibility(View.GONE);
                                }
                            }

                        } catch (Exception e) {
                            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.toString());
                            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void nextFriendDetailActivity(Product model) {
        if (isFromSocial) {
            if (!fileGet.getUserId().equals(model.getUserInfo().getUid())) {
                UserRetro friend = new UserRetro();
                friend.setUid(model.getUserInfo().getUid());
                friend.setAvatar(model.getUserInfo().getAvatar());
                friend.setDisplayname(model.getUserInfo().getDisplayname());

                Intent detail = new Intent(mContext, FriendDetailsActivity.class);
                detail.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, friend);
                transitionTo(detail, 0);
            } else {
                FriendDetailsActivity.friend = null;
                Intent intent = new Intent(mContext, PersonalPageActivity.class);
                transitionTo(intent, 0);
            }
        }
    }


    private void nextCommentActivity(Product mProduct) {
        try {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            CommentDialog likeCommentsDialog = new CommentDialog(mContext, mProduct.getItemid(), mProduct.getTitle());
            likeCommentsDialog.show();
            filePut.putProductUserId(mProduct.getUserInfo().getUid());
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void showDetail(Product item, int position) {
        try {
            String uid;
            if (null != item.getUserInfo()) {
                uid = item.getUserInfo().getUid();
            } else {
                uid = fileGet.getUserId();
//                    filePut.putProductIdCurrentSelect();
            }
            if (!isFromSocial && TextUtils.isEmpty(uid)) {
                uid = FriendDetailsActivity.friend != null ? FriendDetailsActivity.friend.getUid() : fileGet.getUserId();
            }
            // type 1 : san pham , 2 : tin vu vo , 3 : sim so
            if (item.getType() == 2) {
                if (item.getBackground() != null)
                    filePut.putLinkBgVuVo(item.getBackground());
                Intent detail = new Intent(mContext, DetailProductActivity.class);
                detail.putExtra(Constants.TEMP_PRODUCT, item);
                detail.putExtra(Constants.TEMP_POSITION, position);
                detail.putExtra(Constants.TEMP_PRODUCT_KEY, item.getItemid());
                UserRetro userInfo = item.getUserInfo();
                detail.putExtra(Constants.TEMP_PRODUCT_UID, userInfo.getUid());
                if (uid.equals(fileGet.getUserId()))
                    detail.putExtra(Constants.IS_MY_PRODUCT, true);
                else
                    detail.putExtra(Constants.IS_MY_PRODUCT, false);
                if (mContext instanceof MainActivity) {
                    transitionTo(detail, Constants.RESULT_CODE_FOLLOW_PRODUCT);
                } else if (mContext instanceof FriendDetailsActivity) {
                    transitionTo(detail, Constants.RESULT_CODE_FOLLOW_PRODUCT);

                } else {
                    transitionTo(detail, 0);
                }
            } else {

                if (uid.equals(fileGet.getUserId())) { //san pham cua minh
                    Intent detail = new Intent(mContext, DetailProductActivity.class);
                    detail.putExtra(Constants.TEMP_POSITION, position);
                    detail.putExtra(Constants.TEMP_PRODUCT_KEY, item.getItemid());
                    detail.putExtra(Constants.IS_MY_PRODUCT, true);
                    if (mContext instanceof MainActivity) {
                        transitionTo(detail, Constants.RESULT_CODE_FOLLOW_PRODUCT);
                    } else if (mContext instanceof FriendDetailsActivity) {
                        transitionTo(detail, Constants.RESULT_CODE_FOLLOW_PRODUCT);
                    } else {
                        transitionTo(detail, 0);
                    }

                } else { //san pham cua nguoi khac
                    Intent detail = new Intent(mContext, DetailProductActivity.class);
                    detail.putExtra(Constants.TEMP_PRODUCT, item);
                    detail.putExtra(Constants.TEMP_POSITION, position);
                    detail.putExtra(Constants.TEMP_PRODUCT_KEY, item.getItemid());
                    detail.putExtra(Constants.IS_MY_PRODUCT, false);
                    if (mContext instanceof MainActivity) {
                        transitionTo(detail, Constants.RESULT_CODE_FOLLOW_PRODUCT);
                    } else if (mContext instanceof FriendDetailsActivity) {
                        transitionTo(detail, Constants.RESULT_CODE_FOLLOW_PRODUCT);
                    } else {
                        transitionTo(detail, 0);
                    }
                }
            }

        } catch (Exception e) {
            Toast.makeText(mContext, "" + mContext.getString(R.string.text_product_invalid), Toast.LENGTH_SHORT).show();
            Crashlytics.logException(e);
        }
    }

    private void transitionTo(Intent i, int result_code) {
        baseActivityTransition.transitionTo(i, result_code);
    }
}
