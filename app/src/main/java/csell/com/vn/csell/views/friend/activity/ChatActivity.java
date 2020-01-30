package csell.com.vn.csell.views.friend.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.controllers.FriendsController;
import csell.com.vn.csell.controllers.NotiController;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.friend.adapter.MessageAdapter;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.EntityFirebase;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FirebaseDBUtil;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.models.LastMessage;
import csell.com.vn.csell.models.Message;
import csell.com.vn.csell.models.FriendInfo;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.models.UserRetro;
import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements FriendsController.OnGetFriendDetailListener, NotiController.OnSendNotiListener {
    private FancyButton btnBackNavigation;
    private TextView titleToolbar;
    //    private ProgressBar progressLoadmoreMessage;
    public RecyclerView rvChat;
    private EditText edtContent;
    private ImageView btnSend;
    private MessageAdapter adapter;
    public static UserRetro friend;
    private FileSave fileSave;
    private LinearLayoutManager mLayoutManager;
    private DatabaseReference dbReference = null;
    private Message message;
    private String roomId = null;
    public static Product product = null;
    private String check1 = "";
    private CircleImageView imgRemove;
    private LinearLayout layoutProduct;
    private TextView tvProductName, tvProductDescription;
    private ImageView imgProduct;
    private boolean isDeleted = false;
    public LinearLayout layoutAccept;
    public LinearLayout layoutType;
    private RelativeLayout layoutWrapper;
    private TextView tvAccept, tvDeny;
    private boolean isAccepted;
    //    private ProgressBar progressBarSend;
    String url = "";
    private RecyclerView.AdapterDataObserver mAdapterDataObserver;
    private boolean isClickSend;
    private boolean isAddMess;
    private boolean isSuccessDetail;
    private boolean isFriend;
    private int countUnread = 0;
    private Query queryCountUnread;
    private ValueEventListener valueEventListenerCountUnread;
    private ImageView imgInfo;
    private BaseActivityTransition baseActivityTransition;
    private FriendsController friendsController;
    private NotiController notiController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Fabric.with(this, new Crashlytics());

        friendsController = new FriendsController(this);
        notiController = new NotiController(this);
        dbReference = FirebaseDBUtil.getDatebase().getReference();
        message = new Message();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        fileSave = new FileSave(ChatActivity.this, Constants.GET);
        initView();
        setupWindowAnimations();
        initEvent();
        checkIntentData();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    private void checkIntentData() {
        try {
            Intent intent = getIntent();
            if (intent != null) {
                friend = (UserRetro) intent.getSerializableExtra(Constants.KEY_PASSINGDATA_FRIEND_ID);
                product = (Product) intent.getSerializableExtra(Constants.KEY_PASSINGDATA_PRODUCT_OBJ);
                roomId = intent.getStringExtra(Constants.KEY_PASSINGDATA_ROOM_ID);
                try {
                    imgInfo.setOnClickListener(v -> {
                        Intent detail = new Intent(ChatActivity.this, FriendDetailsActivity.class);
                        detail.putExtra(Constants.KEY_PASSINGDATA_FRIEND_ID, friend);
                        baseActivityTransition.transitionTo(detail, 0);
                    });
                } catch (Exception ignored) {

                }

                queryCountUnread = dbReference.child(EntityFirebase.TableLastMessages)
                        .child(fileSave.getUserId()).child(roomId).orderByChild(EntityFirebase.FieldIsSeen);

                valueEventListenerCountUnread = queryCountUnread.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            LastMessage lastMessage = dataSnapshot.getValue(LastMessage.class);
                            if (lastMessage != null) {
                                try {
                                    isAccepted = lastMessage.is_accepted;
                                    countUnread = lastMessage.count_unread;
                                } catch (Exception ignored) {

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if (friend != null) {

                    titleToolbar.setText(friend.getDisplayname());
                    layoutProduct.setVisibility(View.GONE);
                    showProgress(true);

                    if (roomId != null) {
                        dbReference.child(EntityFirebase.TableOnlineChat).child(fileSave.getUserId()).setValue(roomId);
                        dbReference.child(EntityFirebase.TableOnlineChat).keepSynced(false);
                        getMessages();
                    }


                    //api get detai
                    // isSuccessDetails - isFriend
                    CheckStatusFriend();
                }

                if (product != null) {
                    showProgress(true);
                    titleToolbar.setText(product.getUserInfo().getDisplayname());
                    layoutProduct.setVisibility(View.VISIBLE);

                    tvProductName.setText(product.getTitle());
                    tvProductDescription.setText(product.getDescription());

                    if (product.getImages() != null) {
                        if (product.getImages().size() != 0) {
                            url = TextUtils.isEmpty(product.getImages().get(0).getPath()) ? "" : product.getImages().get(0).getPath();
                        }
                    }

                    GlideApp.with(this)
                            .load(!TextUtils.isEmpty(url) ? url : R.drawable.ic_logo)
                            .placeholder(R.drawable.ic_logo)
                            .error(R.drawable.ic_logo)
                            .into(imgProduct);

                    getMessages();


                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void initView() {
        try {
            baseActivityTransition = new BaseActivityTransition(this);

            imgInfo = findViewById(R.id.icon_info);
            rvChat = findViewById(R.id.rvChat);
            edtContent = findViewById(R.id.edtContent);
            btnSend = findViewById(R.id.btnSend);
            btnBackNavigation = findViewById(R.id.btn_back_navigation);
            btnBackNavigation.setText(getString(R.string.title_back_vn));
            titleToolbar = findViewById(R.id.custom_TitleToolbar);
            TextView subTitleToolbar = findViewById(R.id.custom_SubTitleToolbar);
            subTitleToolbar.setVisibility(View.GONE);

            rvChat.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(ChatActivity.this);
//            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
            imgRemove = findViewById(R.id.img_remove);
            layoutProduct = findViewById(R.id.layout_product);
            tvProductName = findViewById(R.id.tv_product_name);
            tvProductDescription = findViewById(R.id.tv_product_description);
            imgProduct = findViewById(R.id.img_product);
            layoutAccept = findViewById(R.id.linear_layout_accept_deny);
            layoutType = findViewById(R.id.layout_type);
            tvAccept = findViewById(R.id.tv_accept);
            tvDeny = findViewById(R.id.tv_deny);
            layoutWrapper = findViewById(R.id.layout_wrapper);
            //            progressBarSend = findViewById(R.id.progress_bar_send);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }

    }

    private void initEvent() {
        btnBackNavigation.setOnClickListener(v -> onBackPressed());

        edtContent.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                TextUtils.isEmpty(edtContent.getText().toString());
                return true;
            } else {
                return false;
            }
        });

        edtContent.setOnFocusChangeListener((v, hasFocus) -> {
        });

        btnSend.setOnClickListener(v -> {
            if (TextUtils.isEmpty(edtContent.getText().toString())) {
                return;
            }

            if (isSuccessDetail) {
                isClickSend = true;
                isAddMess = true;
                addMessageExecute();
            } else {
                Toast.makeText(this, "Hệ thống đang bận", Toast.LENGTH_SHORT).show();
            }

        });

        imgRemove.setOnClickListener(v -> {
            layoutProduct.setVisibility(View.GONE);
            isDeleted = true;
        });

        tvAccept.setOnClickListener(view -> {
            showProgress(true);
            HashMap<String, Object> map = new HashMap<>();
            map.put(EntityFirebase.FieldIsPending, false);
            map.put(EntityFirebase.FieldIsAccepted, true);
            map.put(EntityFirebase.FieldCountUnread, 0);
            if (friend != null) {

                dbReference.child(EntityFirebase.TableLastMessages)
                        .child(fileSave.getUserId())
                        .child(roomId)
                        .updateChildren(map);

                dbReference.child(EntityFirebase.TableLastMessages)
                        .child(fileSave.getUserId())
                        .child(roomId)
                        .child(EntityFirebase.FieldMessageId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String messageId = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                                dbReference.child(EntityFirebase.TableDirectMessages)
                                        .child(roomId)
                                        .child(messageId)
                                        .updateChildren(map)
                                        .addOnCompleteListener(task -> {
                                            if (task.isComplete() && task.isSuccessful()) {
                                                layoutAccept.setVisibility(View.GONE);
                                                layoutType.setVisibility(View.VISIBLE);
                                                isAccepted = true;
                                                showProgress(false);
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }

            if (product != null) {

                dbReference.child(EntityFirebase.TableLastMessages)
                        .child(fileSave.getUserId())
                        .child(roomId)
                        .updateChildren(map);

                dbReference.child(EntityFirebase.TableLastMessages)
                        .child(fileSave.getUserId())
                        .child(roomId)
                        .child(EntityFirebase.FieldMessageId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String messageId = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                                dbReference.child(EntityFirebase.TableDirectMessages)
                                        .child(roomId)
                                        .child(messageId)
                                        .updateChildren(map)
                                        .addOnCompleteListener(task -> {
                                            if (task.isComplete() && task.isSuccessful()) {
                                                layoutAccept.setVisibility(View.GONE);
                                                layoutType.setVisibility(View.VISIBLE);
                                                isAccepted = true;
                                                showProgress(false);
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }

        });

        tvDeny.setOnClickListener(view -> {
            showProgress(true);
            if (friend != null) {

                dbReference.child(EntityFirebase.TableLastMessages)
                        .child(fileSave.getUserId())
                        .child(roomId)
                        .removeValue();

                dbReference.child(EntityFirebase.TableLastMessages)
                        .child(friend.getUid())
                        .child(roomId)
                        .removeValue();

                dbReference.child(EntityFirebase.TableDirectMessages)
                        .child(roomId)
                        .removeValue()
                        .addOnCompleteListener(task -> {
                            if (task.isComplete() && task.isSuccessful()) {
                                onBackPressed();
                                showProgress(false);
                            }
                        });

            }

            if (product != null) {

                dbReference.child(EntityFirebase.TableLastMessages)
                        .child(fileSave.getUserId())
                        .child(roomId)
                        .removeValue();

                dbReference.child(EntityFirebase.TableLastMessages)
                        .child(product.getUserInfo().getUid())
                        .child(roomId)
                        .removeValue();

                dbReference.child(EntityFirebase.TableDirectMessages)
                        .child(roomId)
                        .removeValue()
                        .addOnCompleteListener(task -> {
                            if (task.isComplete() && task.isSuccessful()) {
                                onBackPressed();
                                showProgress(false);
                            }
                        });

            }

        });

    }

    private void getMessages() {
        try {
            if (friend != null) {
                if (TextUtils.isEmpty(roomId)) {
                    showProgress(false);
                    return;
                }

                displayList(roomId);
                updateSeenLastMessage(roomId, friend.getUid());
            } else if (product != null) {

                if (TextUtils.isEmpty(roomId)) {
                    showProgress(false);
                    return;
                }

                displayList(roomId);
                updateSeenLastMessage(roomId, product.getUserInfo().getUid());
            }

            showProgress(false);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            showProgress(false);
        }
    }

    private void addMessageExecute() {
        try {
            if (product == null) {
                if (roomId != null)
                    addMessageFriend();
                else
                    Toast.makeText(this, getResources().getString(R.string.text_error_occurred), Toast.LENGTH_SHORT).show();
            } else {
                addMessageProduct();
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("CSELL chat", e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void addMessageProduct() {

        if (TextUtils.isEmpty(roomId)) {

            roomId = fileSave.getUserId() + product.getUserInfo().getUid();

            dbReference.child(EntityFirebase.FieldRooms).child(fileSave.getUserId()).child(product.getUserInfo().getUid()).setValue(roomId);
            dbReference.child(EntityFirebase.FieldRooms).child(product.getUserInfo().getUid())
                    .child(fileSave.getUserId())
                    .setValue(roomId);

        }

        message.sender = fileSave.getUserId();
        message.sender_name = fileSave.getDisplayName();
        message.sender_avatar = fileSave.getUserAvatar();
        message.receiver = product.getUserInfo().getUid();
        message.receiver_name = product.getUserInfo().getDisplayname();
        message.receiver_avatar = product.getUserInfo().getAvatar();
        message.message_content = edtContent.getText().toString() + "";
//        message.is_pending = isPending;
        message.is_sent = true;
        message.is_seen = false;
        if (!isDeleted) {
            message.product = product;
        } else {
            message.product = null;
        }

        if (isAddMess) {
            addMessProduct();
            isAddMess = false;
        }
    }

    private void addMessageFriend() {

        message.sender = fileSave.getUserId();
        message.sender_name = fileSave.getDisplayName();
        message.sender_avatar = fileSave.getUserAvatar();
        message.receiver = friend.getUid();
        message.receiver_name = friend.getDisplayname();
        message.receiver_avatar = friend.getAvatar();
        message.message_content = edtContent.getText().toString() + "";

        if (isAddMess) {
            addMessFriend();
            isAddMess = false;
        }

    }

    private void addMessProduct() {
        if (!isFriend) {
            if (isAccepted) {
                message.is_pending = false;
                message.is_accepted = true;
            } else {
                message.is_pending = true;
                message.is_accepted = false;
            }
        } else {
            message.is_pending = false;
            message.is_accepted = true;
        }

        countUnread++;

        message.is_sent = true;
        message.is_seen = false;
        message.count_unread = countUnread;

        String key = dbReference.child(EntityFirebase.TableDirectMessages).child(roomId).push().getKey();
        message.message_id = key;
        dbReference.child(EntityFirebase.TableDirectMessages).child(roomId).child(key).setValue(message.addMessage());

        edtContent.setText("");

        //add history last message

        dbReference.child(EntityFirebase.TableLastMessages)
                .child(fileSave.getUserId())
                .child(roomId)
                .setValue(message.toLastMessageMeMap(false));

        dbReference.child(EntityFirebase.TableLastMessages)
                .child(product.getUserInfo().getUid())
                .child(roomId)
                .setValue(message.toLastMessageMeMap(message.is_pending))
                .addOnSuccessListener(aVoid -> {
                    isDeleted = true;
                    layoutProduct.setVisibility(View.GONE);
                    showProgress(false);
                    if (TextUtils.isEmpty(check1)) {
                        getMessages();
                    }
                });

        dbReference.child(EntityFirebase.TableOnlineChat).child(product.getUserInfo().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (isClickSend) {
                            if (dataSnapshot.getValue() == null) {
                                UserRetro friend = new UserRetro();
                                friend.setUid(product.getUserInfo().getUid());
                                friend.setDisplayname(product.getUserInfo().getDisplayname());
                                friend.setAvatar(product.getUserInfo().getAvatar());
                                putNotiMessageFriend(friend);
                            } else {
                                if (!dataSnapshot.getValue().equals(roomId + "")) {
                                    UserRetro friend = new UserRetro();
                                    friend.setUid(product.getUserInfo().getUid());
                                    friend.setDisplayname(product.getUserInfo().getDisplayname());
                                    friend.setAvatar(product.getUserInfo().getAvatar());
                                    putNotiMessageFriend(friend);
                                }
                            }
                            isClickSend = false;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addMessFriend() {
        if (!isFriend) {
            if (isAccepted) {
                message.is_pending = false;
                message.is_accepted = true;
            } else {
                message.is_pending = true;
                message.is_accepted = false;
            }
        } else {
            message.is_pending = false;
            message.is_accepted = true;
        }

        countUnread++;

        message.is_sent = true;
        message.is_seen = false;
        message.count_unread = countUnread;

        if (product != null) {
            message.product = product;
            product = null;
        }

        String key = dbReference.child(EntityFirebase.TableDirectMessages).child(roomId).push().getKey();
        message.message_id = key;
        dbReference.child(EntityFirebase.TableDirectMessages).child(roomId).child(key).setValue(message.addMessage());

        edtContent.setText("");

        dbReference.child(EntityFirebase.TableLastMessages)
                .child(fileSave.getUserId())
                .child(roomId)
                .setValue(message.toLastMessageMeMap(false));

        dbReference.child(EntityFirebase.TableLastMessages)
                .child(friend.getUid())
                .child(roomId)
                .setValue(message.toLastMessageMeMap(message.is_pending))
                .addOnCompleteListener(task -> {
                    if (task.isComplete() && task.isSuccessful()) {
                        showProgress(false);
                    }
                });

        dbReference.child(EntityFirebase.FieldRooms).child(fileSave.getUserId()).child(friend.getUid()).setValue(roomId);
        dbReference.child(EntityFirebase.FieldRooms).child(friend.getUid())
                .child(fileSave.getUserId())
                .setValue(roomId)
                .addOnSuccessListener(aVoid -> {
                    showProgress(false);
                    getMessages();
                });

        dbReference.child(EntityFirebase.TableOnlineChat).child(friend.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (isClickSend) {
                            if (dataSnapshot.getValue() == null) {
                                putNotiMessageFriend(friend);
                            } else {
                                if (!dataSnapshot.getValue().equals(roomId + "")) {
                                    putNotiMessageFriend(friend);
                                }
                            }
                            isClickSend = false;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        try {
            if (adapter != null) {
                if (mAdapterDataObserver != null)
                    adapter.unregisterAdapterDataObserver(mAdapterDataObserver);
                adapter.stopListening();
            }

            queryCountUnread.removeEventListener(valueEventListenerCountUnread);
        } catch (Exception ignored) {

        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        dbReference.child(EntityFirebase.TableOnlineChat).child(fileSave.getUserId()).removeValue();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void displayList(String id) {

        if (adapter == null) {

            SnapshotParser<Message> parser = snapshot -> snapshot.getValue(Message.class);

            DatabaseReference messagesRef = dbReference.child(EntityFirebase.TableDirectMessages).child(id);
            FirebaseRecyclerOptions<Message> options =
                    new FirebaseRecyclerOptions.Builder<Message>()
                            .setQuery(messagesRef, parser)
                            .build();

            try {
                adapter = new MessageAdapter(ChatActivity.this, options, id);
                adapter.startListening();

                mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        int friendlyMessageCount = adapter.getItemCount();
                        int lastVisiblePosition =
                                mLayoutManager.findLastCompletelyVisibleItemPosition();
                        // If the recycler view is initially being loaded or the
                        // user is at the bottom of the list, scroll to the bottom
                        // of the list to show the newly added message.
                        try {
                            if (lastVisiblePosition == -1 ||
                                    (positionStart >= (friendlyMessageCount - 1) &&
                                            lastVisiblePosition == (positionStart - 1))) {
                                rvChat.scrollToPosition(positionStart);
                            }
                        } catch (Exception e) {
                            Log.w("TEST_", e.toString());
                        }

                    }
                };

                rvChat.setLayoutManager(mLayoutManager);
                rvChat.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
            showProgress(false);

            adapter.registerAdapterDataObserver(mAdapterDataObserver);
        } else {
            showProgress(false);
            //scrollToBottom();
            adapter.registerAdapterDataObserver(mAdapterDataObserver);
        }
    }

    private void checkStatusPending(DataSnapshot dataSnapshot) {
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            Message message = data.getValue(Message.class);
            assert message != null;
            if (!message.is_pending) {
                layoutType.setVisibility(View.VISIBLE);
                layoutAccept.setVisibility(View.GONE);
            } else {
                if (message.sender.equalsIgnoreCase(fileSave.getUserId())) {
                    layoutType.setVisibility(View.VISIBLE);
                    layoutAccept.setVisibility(View.GONE);
                } else {
                    layoutType.setVisibility(View.GONE);
                    layoutAccept.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void showProgress(boolean check) {
        if (check) {
//            progressLoadmoreMessage.setVisibility(View.VISIBLE);
            layoutWrapper.setAlpha(0.5f);
            layoutWrapper.setEnabled(false);
        } else {
//            progressBarSend.setVisibility(View.GONE);
//            progressLoadmoreMessage.setVisibility(View.GONE);
            layoutWrapper.setAlpha(1);
            layoutWrapper.setEnabled(true);
        }
    }

    public void CheckStatusFriend() {
        try {
            friendsController.getFriendDetail(friend.getUid(), this);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private void putNotiMessageFriend(UserRetro friend) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put(EntityAPI.FIELD_TYPE_SEND_EMAIL, 5);
            map.put(EntityAPI.FIELD_CONTENT, "đã gửi cho bạn 1 tin nhắn");
            map.put(EntityAPI.FIELD_KEY, roomId + "," + fileSave.getUserId()
                    + "," + fileSave.getDisplayName());
            List<String> lstUser = new ArrayList<>();
            lstUser.add(friend.getUid());
            map.put(EntityAPI.FIELD_LIST_USERS, lstUser);

            notiController.sendNoti(map, this);

        } catch (Exception e) {
            showProgress(false);
        }
    }

    private void updateSeenLastMessage(String id, String friendId) {
        try {
            dbReference.child(EntityFirebase.TableDirectMessages).child(id)
                    .orderByChild(EntityFirebase.FieldSender).equalTo(friendId)
                    .limitToLast(10)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String key = snapshot.getKey();
                                    Message message = snapshot.getValue(Message.class);
                                    if (friend != null) {
                                        assert message != null;
                                        if (message.sender.equals(friend.getUid())) {
                                            message.is_seen = true;
                                            message.is_seen = true;
                                        }
                                    }

                                    if (product != null) {
                                        assert message != null;
                                        if (message.sender.equals(product.getUserInfo().getUid())) {
                                            message.is_seen = true;
                                            message.is_seen = true;
                                        }
                                    }

                                    dbReference.child(EntityFirebase.TableDirectMessages).child(id)
                                            .child(key).setValue(message);

                                }

                            } catch (Exception e) {
                                if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                                Crashlytics.logException(e);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
            Crashlytics.logException(e);
        }
    }

    public void updatePending() {
        try {
            if (layoutAccept != null) {
                layoutAccept.setVisibility(View.GONE);
                layoutType.setVisibility(View.VISIBLE);
                layoutAccept.removeAllViewsInLayout();
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGetFriendDetailSuccess(UserRetro data) {
        try {
            friend = data;
            FriendInfo friendInfo = data.getFriendInfo();

            if (friendInfo != null) {
                isFriend = friendInfo.getIsAccepted();
            } else {
                isFriend = false;
            }

            isSuccessDetail = true;
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGetFriendDetailFailure() {
        showProgress(false);
    }

    @Override
    public void onConnectGetFriendDetailFailure() {
        showProgress(false);
    }

    @Override
    public void onSendNotiSuccess() {
        showProgress(false);
    }

    @Override
    public void onErrorSendNoti() {
        showProgress(false);
    }

    @Override
    public void onSendNotiFailure() {
        showProgress(false);
    }

    @Override
    public void onConnectSendNotiFailure() {
        showProgress(false);
    }
}
