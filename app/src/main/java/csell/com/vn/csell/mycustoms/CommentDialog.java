package csell.com.vn.csell.mycustoms;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.NotiController;
import csell.com.vn.csell.views.social.adapter.CommentsAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.EntityFirebase;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FirebaseDBUtil;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.commons.PushNotifications;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.models.Comment;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.sqlites.SQLFriends;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentDialog extends BottomSheetDialog implements NotiController.OnSendNotiListener {

    private LinearLayout from_replyComment;
    private TextView txtReplyComment;
    private ImageView img_close_reply;
    public EditText edtContent;
    private ImageButton btnSendComment;
    private FileSave fileGet;
    private RecyclerView rvComments;
    private SwipeRefreshLayout swipe_loadmore;
    private ArrayList<Comment> dataComments;
    private DatabaseReference dbReference;
    private CommentsAdapter commentsAdapter;
    private DatabaseReference db;
    private boolean isReplyComment = false;
    private String productId;
    private String productName;
    private UserRetro userReply = null;
    private String lastKeyComment = "";
    private SQLFriends sqlFriends;
    private boolean isClickSend = false;
    private String uidRepCmt = "";
    private NotiController notiController;
    private Context context;

    public CommentDialog(@NonNull Context context, String productId, String productName) {
        super(context, R.style.full_screen_dialog_no_status_bar);

        setContentView(R.layout.activity_detail_comments);

        Fabric.with(context, new Crashlytics());
        this.context = context;
        //custom position dialog
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp;
        if (dialogWindow != null) {
            lp = dialogWindow.getAttributes();
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            lp.windowAnimations = R.style.DialogCreateAnimation;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT; // Width
            lp.height = WindowManager.LayoutParams.MATCH_PARENT; // Height
            dialogWindow.setAttributes(lp);
        }
        dbReference = FirebaseDBUtil.getDatebase().getReference();
        dbReference.child(EntityFirebase.TableOnlineComment).keepSynced(false);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        fileGet = new FileSave(context, Constants.GET);

        notiController = new NotiController(context);
        db = FirebaseDBUtil.getDatebase().getReference();

        this.productId = productId;
        this.productName = productName;

        initView();
        addEvent();
        getListComments();
    }

    private void initView() {
        btnSendComment = findViewById(R.id.btn_send_comment);
        rvComments = findViewById(R.id.rv_comments);
        swipe_loadmore = findViewById(R.id.swipe_loadmore);
        edtContent = findViewById(R.id.edt_comment_content);
        from_replyComment = findViewById(R.id.from_replyComment);
        txtReplyComment = findViewById(R.id.txtReplyComment);
        img_close_reply = findViewById(R.id.img_close_reply);
        dataComments = new ArrayList<>();
        rvComments.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvComments.setLayoutManager(mLayoutManager);
        commentsAdapter = new CommentsAdapter(this.context, dataComments, R.layout.item_comment, this);
        rvComments.setAdapter(commentsAdapter);
        sqlFriends = new SQLFriends(getContext());

        dbReference.child(EntityFirebase.TableOnlineComment).child(fileGet.getUserId()).setValue(productId);
        dbReference.child(EntityFirebase.TableOnlineComment).keepSynced(false);
    }

    @Override
    protected void onStop() {
        dbReference.child(EntityFirebase.TableOnlineComment).child(fileGet.getUserId()).removeValue();
        super.onStop();
    }

    private void getListComments() {
        try {

            db.child(EntityFirebase.TableComments)
                    .child(productId).orderByKey()
                    .limitToLast(15).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            Comment comment = dataSnapshot.getValue(Comment.class);
                            if (comment != null) {
                                comment.comment_id = dataSnapshot.getKey();
                                if (fileGet.getProductUserId().equals(fileGet.getUserId())) {
                                    dataComments.add(comment);
                                } else {

                                    if (!comment.uid.equals(fileGet.getUserId())) {

                                        UserRetro f = sqlFriends.checkIsFriendById(comment.uid);
                                        if (f != null) {
                                            dataComments.add(comment);
                                        }
                                    } else {
                                        dataComments.add(comment);
                                    }

                                    if (dataComments.get(0) != null) {
                                        lastKeyComment = dataComments.get(0).comment_id;
                                    }

                                }
                            }
                            commentsAdapter.notifyDataSetChanged();
                            scrollToBottom();
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.toString());
                        Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {

            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void loadMoreComment() {

        db.child(EntityFirebase.TableComments)
                .child(productId).orderByKey().endAt(lastKeyComment)
                .limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        ArrayList<Comment> temp = new ArrayList<>();
                        for (DataSnapshot s : dataSnapshot.getChildren()) {
                            if (lastKeyComment.equals(s.getKey())) continue;
                            Comment comment = s.getValue(Comment.class);
                            if (comment != null) {
                                comment.comment_id = s.getKey();
                                if (fileGet.getProductUserId().equals(fileGet.getUserId())) {
                                    temp.add(comment);
                                } else {
                                    if (!comment.uid.equals(fileGet.getUserId())) {
                                        UserRetro f = sqlFriends.checkIsFriendById(comment.uid);
                                        if (f != null) {
                                            temp.add(comment);
                                        }
                                    } else {
                                        temp.add(comment);
                                    }
                                }
                            }
                        }
                        swipe_loadmore.setRefreshing(false);
                        dataComments.addAll(0, temp);
                        lastKeyComment = dataComments.get(0).comment_id;
                        commentsAdapter.notifyDataSetChanged();
                    } else {
                        lastKeyComment = "";
                        swipe_loadmore.setRefreshing(false);
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.toString());
                    Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                    swipe_loadmore.setRefreshing(false);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendComment() {
        try {

            String keyComment = db.child(EntityFirebase.TableComments).child(productId).push().getKey();
            Comment comment = new Comment();
            comment.comment_id = keyComment;

            comment.is_deleted = false;
            comment.uid = fileGet.getUserId();
//            comment.UserName = fileGet.getUserName();
            comment.content = edtContent.getText().toString();
            comment.display_name = fileGet.getDisplayName();
            comment.product_id = productId;
            comment.avatar = fileGet.getUserAvatar();

            if (isReplyComment) {
                comment.is_reply = true;
                comment.reply_uid = userReply.getUid();
                comment.reply_display_name = userReply.getDisplayname();

                String content = "đã trả lời bài viết của bạn trong " + productName;

                db.child(EntityFirebase.TableComments).child(productId)
                        .child(keyComment).setValue(comment.toMapReply())
                        .addOnCompleteListener(task -> {
                            if (task.isComplete() && task.isSuccessful()) {
                                from_replyComment.setVisibility(View.GONE);
                                isReplyComment = false;
                                if (fileGet.getUserId().equals(fileGet.getProductUserId())) {
                                    return;
                                }

                                PushNotifications pushNotifications = new PushNotifications(getContext());
                                HashMap<String, Object> data = new HashMap<>();
                                data.put(EntityAPI.FIELD_NOTIFICATION_TITLE, "ReplyComment");
                                data.put(EntityAPI.FIELD_NOTIFICATION_BODY, fileGet.getDisplayName() + " "
                                        + getContext().getResources().getString(R.string.text_body_reply_comment)
                                        + " " + productName);
                                data.put(EntityAPI.FIELD_NOTIFICATION_TARGET_NAME, productName);
                                data.put(EntityAPI.FIELD_NOTIFICATION_ACTION, Utilities.ACTION_COMMENT);
                                data.put(EntityAPI.FIELD_DATE_CREATED, ServerValue.TIMESTAMP);
                                data.put(EntityAPI.FIELD_NOTIFICATION_DATA, productId);
                                data.put(EntityAPI.FIELD_NOTIFICATION_SENDER, fileGet.getUserId());
                                data.put(EntityAPI.FIELD_NOTIFICATION_SENDER_NAME, fileGet.getDisplayName());
                                data.put(EntityAPI.FIELD_NOTIFICATION_SENDER_AVATAR, fileGet.getUserAvatar());
                                data.put(EntityAPI.FIELD_NOTIFICATION_IS_SEEN, false);
                                pushNotifications.pushComment(data, userReply.getUid());
                            }
                        });

                dbReference.child(EntityFirebase.TableOnlineComment).child(uidRepCmt)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (isClickSend) {
                                    if (dataSnapshot.getValue() == null) {
                                        putNotiComment(fileGet.getDisplayName(), productId, fileGet.getProductUserId(), uidRepCmt, content);
                                    } else {
                                        if (!dataSnapshot.getValue().equals(productId)) {
                                            putNotiComment(fileGet.getDisplayName(), productId, fileGet.getProductUserId(), uidRepCmt, content);
                                        }
                                    }
                                    isClickSend = false;
                                    isReplyComment = false;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            } else {

                String content = "đã bình luận trong bài viết " + productName + " của bạn";

                db.child(EntityFirebase.TableComments).child(productId)
                        .child(keyComment).setValue(comment.toMap()).addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.isComplete()) {

                        isReplyComment = false;
                        if (fileGet.getUserId().equals(fileGet.getProductUserId())) {
                            return;
                        }

                        PushNotifications pushNotifications = new PushNotifications(getContext());
                        HashMap<String, Object> data = new HashMap<>();
                        data.put(EntityAPI.FIELD_NOTIFICATION_TITLE, "Comment");
                        data.put(EntityAPI.FIELD_NOTIFICATION_BODY, fileGet.getDisplayName() + " "
                                + getContext().getResources().getString(R.string.text_body_comment)
                                + " " + productName
                                + " " + getContext().getResources().getString(R.string.text_body_of_you));
                        data.put(EntityAPI.FIELD_NOTIFICATION_TARGET_NAME, productName);
                        data.put(EntityAPI.FIELD_NOTIFICATION_ACTION, Utilities.ACTION_COMMENT);
                        data.put(EntityAPI.FIELD_DATE_CREATED, ServerValue.TIMESTAMP);
                        data.put(EntityAPI.FIELD_NOTIFICATION_DATA, productId);
                        data.put(EntityAPI.FIELD_NOTIFICATION_SENDER, fileGet.getUserId());
                        data.put(EntityAPI.FIELD_NOTIFICATION_SENDER_NAME, fileGet.getDisplayName());
                        data.put(EntityAPI.FIELD_NOTIFICATION_SENDER_AVATAR, fileGet.getUserAvatar());
                        data.put(EntityAPI.FIELD_NOTIFICATION_IS_SEEN, false);
                        pushNotifications.pushComment(data, fileGet.getProductUserId());
                    }
                });

                dbReference.child(EntityFirebase.TableOnlineComment).child(fileGet.getProductUserId())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (isClickSend) {
                                    if (dataSnapshot.getValue() == null) {
                                        putNotiComment(fileGet.getDisplayName(), productId, fileGet.getProductUserId(), fileGet.getProductUserId(), content);
                                    } else {
                                        if (!dataSnapshot.getValue().equals(productId)) {
                                            putNotiComment(fileGet.getDisplayName(), productId, fileGet.getProductUserId(), fileGet.getProductUserId(), content);
                                        }
                                    }
                                    isClickSend = false;
                                    isReplyComment = false;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            edtContent.setText("");

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(fileGet.getUserId(), true);
            db.child(EntityFirebase.Field_User_Comments).child(productId)
                    .updateChildren(hashMap);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void addEvent() {

        img_close_reply.setOnClickListener(v -> {
            from_replyComment.setVisibility(View.GONE);
            isReplyComment = false;
        });

        btnSendComment.setOnClickListener(v -> {
            String content = edtContent.getText().toString();
            if (TextUtils.isEmpty(content)) return;
            isClickSend = true;
            sendComment();
        });

        swipe_loadmore.setOnRefreshListener(this::loadMoreComment);


    }

    private void scrollToBottom() {
        if (dataComments.size() != 0) {
//            rvComments.scrollToPosition(dataComments.size() - 1);
            rvComments.smoothScrollToPosition(dataComments.size() - 1);
        }

    }

    public void setReplyComment(Comment replyComment) {
        try {
            from_replyComment.setVisibility(View.VISIBLE);

            userReply = new UserRetro();
            userReply.setDisplayname(replyComment.display_name);
            userReply.setUid(replyComment.uid);

            isReplyComment = true;
            uidRepCmt = replyComment.uid;

            try {
                String text = getContext().getResources().getString(R.string.text_reply_to) + " " + replyComment.display_name;
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                stringBuilder.append(text);
                stringBuilder.setSpan(new StyleSpan(Typeface.BOLD), text.length() - replyComment.display_name.length(),
                        text.length(), 0);
                txtReplyComment.setText(stringBuilder);
            } catch (Exception e) {
                from_replyComment.setVisibility(View.GONE);
            }

            edtContent.setText("");
            edtContent.setSelection(edtContent.getText().length());

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d(getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }

    }

    private void putNotiComment(String senderName, String productID, String uidProduct, String uid, String content) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put(EntityAPI.FIELD_TYPE_SEND_EMAIL, 4);
            map.put(EntityAPI.FIELD_KEY, productID + "," + senderName + "," + uidProduct);
            map.put(EntityAPI.FIELD_CONTENT, content);
            ArrayList<String> list = new ArrayList<>();
            list.add(uid);
            map.put(EntityAPI.FIELD_LIST_USERS, list);

            notiController.sendNoti(map, this);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onSendNotiSuccess() {

    }

    @Override
    public void onErrorSendNoti() {

    }

    @Override
    public void onSendNotiFailure() {

    }

    @Override
    public void onConnectSendNotiFailure() {

    }
}
