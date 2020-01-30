package csell.com.vn.csell.views.product.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import java.util.Timer;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.commons.PushNotifications;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.EntityFirebase;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FirebaseDBUtil;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.ProjectsController;
import csell.com.vn.csell.controllers.UserController;
import csell.com.vn.csell.interfaces.ShowNotePrivate;
import csell.com.vn.csell.models.Comment;
import csell.com.vn.csell.models.CustomerRetro;
import csell.com.vn.csell.models.CustomerRetroV1;
import csell.com.vn.csell.models.FieldCus;
import csell.com.vn.csell.models.OwnerDetailV1;
import csell.com.vn.csell.models.ProductResponseModel;
import csell.com.vn.csell.models.ProductResponseV1;
import csell.com.vn.csell.models.Project;
import csell.com.vn.csell.models.Properties;
import csell.com.vn.csell.models.ResLogin;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.mycustoms.InputPasswordPrivateMode;
import csell.com.vn.csell.mycustoms.ListUserLikeCommentsDialog;
import csell.com.vn.csell.sqlites.SQLCustomers;
import csell.com.vn.csell.sqlites.SQLFriends;
import csell.com.vn.csell.sqlites.SQLProperties;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.customer.activity.ContactCustomerDetailActivity;
import csell.com.vn.csell.views.product.activity.EditProductActivityV1;
import csell.com.vn.csell.views.product.activity.SelectCategoryActivity;
import csell.com.vn.csell.views.product.activity.ShareNotePrivateActivity;
import csell.com.vn.csell.views.project.activity.ProjectDetailsActivity;
import csell.com.vn.csell.views.social.activity.DetailProductActivity;
import csell.com.vn.csell.views.social.adapter.CommentsAdapter;
import csell.com.vn.csell.views.social.adapter.GridPropertiesAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by cuong.nv on 3/19/2018.
 */
@SuppressLint("StaticFieldLeak")
public class TabDetailProductFragment extends Fragment implements ShowNotePrivate, ProjectsController.OnGetProjectListener, UserController.OnLoginListener {
    public static ArrayList<UserRetro> finalLst = new ArrayList<>();
    public static FileSave fileGet;
    public static ImageView img_persion_1;
    public static ImageView img_persion_2;
    public static ImageView img_persion_3;
    public static TextView txtListFriendName;
    public static LinearLayout fromNotePrivateAndFriend;
    private static TextView tvContentNote;
    private static TextView tvContentNoteMyProduct;
    private static LinearLayout layoutAddNotePrivate;
    private static Context context = null;
    private static int timeout = 0;
    //    private static Product product;
    private static ProductResponseV1 productResponseV1;
    private final long DELAY = 100;
    private FileSave filePut;
    private ArrayList<String> listImagesProduct;
    private Timer timer1 = new Timer();
    private Timer timer2 = new Timer();
    private TextView edtContent;
    private TextView txtProductDescription;
    private RecyclerView rvComments;
    private RelativeLayout layoutProperty;
    private LinearLayout layoutDescription;
    private CommentsAdapter adapterComment;
    private ArrayList<Properties> arrayProperties;
    private ArrayList<Comment> dataComments;
    private LinearLayout layoutPrivateOwnerInfo;
    private LinearLayout from_info_owner;
    private String ownerPhone;
    private RelativeLayout layoutOwnerName, layoutPriceCapital;
    private TextView tvOwnerNote, tvOwnerName, tvOwnerPrice;
    private FancyButton btnCall, btnSms;
    private LinearLayout fromShowUserLikes;
    private ImageView imgCountLike, imgLike;
    private CircleImageView imgUserComment;
    private TextView tvCountLike;
    private TextView tvCountComments;
    private TextView tvLike;
    private LinearLayout btnLike, btnComment, btnShare;
    private LinearLayout layoutCountLike;
    private LinearLayout layoutProjectInfo;
    private TextView txtProjectName;
    private TextView txtProjectInvestor;
    private TextView txtProjectAddress;
    private long countComments;
    private TextView txtEditOwner;
    private List<UserRetro> listFriendShareNote;
    private TextView tvEditNote;
    private TextView txtTitleDes;
    private ValueEventListener registrationLastComments;
    private ValueEventListener registrationCountComments;
    private LinearLayout layout_loading;
    private ProgressBar progress_loading;
    private LinearLayout layoutSocial, layoutComment;
    private TextView txtNoComment;
    private DatabaseReference mDatabaseRef;
    private SQLFriends sqlFriends;
    private ArrayList<String> lsUserIds;
    private Context mContext;
    private Project project;
    private ProjectsController projectsController;
    private RecyclerView gridViewCustomize1;
    private LinearLayout layoutNameOwner;
    private LinearLayout layoutNotePrivate;
    private BaseActivityTransition baseActivityTransition;
    private InputPasswordPrivateMode dialog;
    private long mLastClickTime = 0;
    private TextView tvEditProperties;

    public static void updateViewNoteFriend(List<UserRetro> lsFriends) {
        layoutAddNotePrivate.setVisibility(View.GONE);
        fromNotePrivateAndFriend.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(Utilities.contentPrivateNote)) {
            tvContentNote.setText(Utilities.contentPrivateNote);
            tvContentNoteMyProduct.setText(Utilities.contentPrivateNote);
        }

        if (lsFriends.size() >= 3) {
            img_persion_1.setVisibility(View.VISIBLE);
            img_persion_2.setVisibility(View.VISIBLE);
            img_persion_3.setVisibility(View.VISIBLE);
            GlideApp.with(context)
                    .load(lsFriends.get(0).getAvatar() == null ? R.drawable.ic_logo : lsFriends.get(0).getAvatar())
                    .error(R.drawable.ic_logo)
                    .placeholder(R.drawable.ic_logo)
                    .into(img_persion_1);
            GlideApp.with(context)
                    .load(lsFriends.get(1).getAvatar() == null ? R.drawable.ic_logo : lsFriends.get(1).getAvatar())
                    .error(R.drawable.ic_logo)
                    .placeholder(R.drawable.ic_logo)
                    .into(img_persion_2);
            GlideApp.with(context)
                    .load(lsFriends.get(2).getAvatar() == null ? R.drawable.ic_logo : lsFriends.get(2).getAvatar())
                    .error(R.drawable.ic_logo)
                    .placeholder(R.drawable.ic_logo)
                    .into(img_persion_3);
            String text = lsFriends.get(0).getDisplayname() + " " + context.getString(R.string.text_and) + " " + (lsFriends.size() - 1)
                    + " " + context.getString(R.string.text_with_persons_orther);
            txtListFriendName.setText(text);
        } else if (lsFriends.size() == 2) {
            img_persion_1.setVisibility(View.VISIBLE);
            img_persion_2.setVisibility(View.VISIBLE);
            img_persion_3.setVisibility(View.GONE);
            GlideApp.with(context)
                    .load(lsFriends.get(0).getAvatar() == null ? R.drawable.ic_logo : lsFriends.get(0).getAvatar())
                    .error(R.drawable.ic_logo)
                    .placeholder(R.drawable.ic_logo)
                    .into(img_persion_1);
            GlideApp.with(context)
                    .load(lsFriends.get(1).getAvatar() == null ? R.drawable.ic_logo : lsFriends.get(1).getAvatar())
                    .error(R.drawable.ic_logo)
                    .placeholder(R.drawable.ic_logo)
                    .into(img_persion_2);
            String text = lsFriends.get(0).getDisplayname() + " " + context.getString(R.string.text_and) + " " + (lsFriends.size() - 1)
                    + " " + context.getString(R.string.text_with_persons_orther);
            txtListFriendName.setText(text);
        } else if (lsFriends.size() == 1) {
            img_persion_1.setVisibility(View.VISIBLE);
            img_persion_2.setVisibility(View.GONE);
            img_persion_3.setVisibility(View.GONE);
            GlideApp.with(context)
                    .load(lsFriends.get(0).getAvatar() == null ? R.drawable.ic_logo : lsFriends.get(0).getAvatar())
                    .error(R.drawable.ic_logo)
                    .placeholder(R.drawable.ic_logo)
                    .into(img_persion_1);
            String text = lsFriends.get(0).getDisplayname() + " ";
            txtListFriendName.setText(text);
        } else {
            layoutAddNotePrivate.setVisibility(View.GONE); // Hien thi khi can them noi dung rieng tu
            fromNotePrivateAndFriend.setVisibility(View.GONE);
        }
    }

    public static void updatePrivateNote() {
        PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        HashMap<String, Object> hashMap = new HashMap();
        hashMap.put(EntityAPI.FIELD_ITEM_ID, DetailProductActivity.productKey);
        String content = Utilities.contentPrivateNote;
        hashMap.put(EntityAPI.FIELD_NOTE, Utilities.contentPrivateNote);
        ArrayList<UserRetro> lst = new ArrayList<>();
        if (Utilities.lsFriendsNotePrivate.size() > 0) {
            for (UserRetro user : Utilities.lsFriendsNotePrivate) {
                user.IsSelected = null;
                user.setEmail(null);
                user.setPhone(null);
                user.setUsername(null);
                lst.add(user);
            }
        } else {
            lst = null;
        }

        hashMap.put(EntityAPI.FIELD_CAN_SEE_NOTE, lst);
        finalLst.clear();
        if (lst != null) {
            finalLst = lst;
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Call<JSONResponse<List<ProductResponseModel>>> jsonResponseCall = postAPI.updatePrivateNote(hashMap);
                jsonResponseCall.enqueue(new Callback<JSONResponse<List<ProductResponseModel>>>() {
                    @Override
                    public void onResponse(Call<JSONResponse<List<ProductResponseModel>>> call, Response<JSONResponse<List<ProductResponseModel>>> response) {
                        if (response.isSuccessful()) {
                            JSONResponse<List<ProductResponseModel>> result = response.body();
                            if (result.getSuccess() != null) {
                                if (result.getSuccess()) {

                                    sendNotiShareNotePrivate(DetailProductActivity.productKey);

                                    checkAddNotePrivate(productResponseV1.getId(), productResponseV1.getName());
                                    Utilities.lsFriendsNotePrivate.clear();
                                    Utilities.lsFriendsNotePrivate.addAll(finalLst);
                                    Utilities.contentPrivateNote = content;
                                } else {
                                    Utilities.refreshToken(MainActivity.mainContext, result.getMessage().toLowerCase() + "");
                                    Toast.makeText(MainActivity.mainContext, MainActivity.mainContext.getResources().getString(R.string.pls_try_again),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            String msg = response.body().getError();
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(MainActivity.mainContext, msg, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (response.errorBody() != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = (String) jsonObject.get(Constants.ERROR);
                                    if (!TextUtils.isEmpty(msg)) {
                                        Toast.makeText(MainActivity.mainContext, msg, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JSONResponse<List<ProductResponseModel>>> call, Throwable t) {
                        updatePrivateNote();
                        Crashlytics.logException(t);
                    }
                });
                return null;
            }
        }.execute();
    }

    public static void sendNotiShareNotePrivate(String itemId) {
        PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());

        HashMap<String, Object> map = new HashMap<>();
        map.put(EntityAPI.FIELD_TYPE_SEND_EMAIL, 1);
        map.put(EntityAPI.FIELD_CONTENT, "đã chia sẻ nội dung riêng tư cho bạn");
        String keyNoti = itemId + ":" + fileGet.getUserId();
        map.put(EntityAPI.FIELD_KEY, keyNoti);
        List<String> lstUsers = new ArrayList<>();
        for (UserRetro userRetro : Utilities.lsFriendsNotePrivate) {
            lstUsers.add(userRetro.getUid());
        }
        map.put(EntityAPI.FIELD_LIST_USERS, lstUsers);
        Call<JSONResponse<Object>> sendNoti = postAPI.sendNoti(map);
        sendNoti.enqueue(new Callback<JSONResponse<Object>>() {
            @Override
            public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess() != null) {
                            if (response.body().getSuccess()) {
                                if (BuildConfig.DEBUG)
                                    Log.e(getClass().getName(), response.body().getMessage());
                            } else {
                                if (BuildConfig.DEBUG)
                                    Log.e(getClass().getName(), response.body().getMessage());
                            }
                        }
                    }
//                else {
//                    if (response.errorBody() != null) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
//                            String msg = (String) jsonObject.get(Constants.ERROR);
//                            TextUtils.isEmpty(msg);
//                        } catch (JSONException | IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
                    sendNoti.cancel();
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                sendNoti.cancel();
                if (timeout < 1) {
                    timeout++;
                    sendNotiShareNotePrivate(itemId);
                } else {
                    timeout = 0;
                }
                if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
                Crashlytics.logException(t);
            }
        });
    }

    private static void checkAddNotePrivate(String productId, String productName) {

        if (Utilities.lsFriendsNotePrivate.size() != 0) {
            FileSave fileGet = new FileSave(MainActivity.mainContext, Constants.GET);
            PushNotifications push = new PushNotifications(MainActivity.mainContext);
            for (UserRetro item : Utilities.lsFriendsNotePrivate) {
                HashMap<String, Object> data = new HashMap<>();
                data.put(EntityAPI.FIELD_NOTIFICATION_TITLE, "Share Note Private");
                data.put(EntityAPI.FIELD_NOTIFICATION_BODY, fileGet.getDisplayName() + " "
                        + MainActivity.mainContext.getString(R.string.text_body_shared_note_private) + " "
                        + productName);
                data.put(EntityAPI.FIELD_NOTIFICATION_TARGET_NAME, productName);
                data.put(EntityAPI.FIELD_NOTIFICATION_ACTION, Utilities.ACTION_NOTE_PRIVATE);
                data.put(EntityAPI.FIELD_DATE_CREATED, ServerValue.TIMESTAMP);
                data.put(EntityAPI.FIELD_NOTIFICATION_DATA, productId);
                data.put(EntityAPI.FIELD_NOTIFICATION_SENDER, fileGet.getUserId());
                data.put(EntityAPI.FIELD_NOTIFICATION_SENDER_NAME, fileGet.getDisplayName());
                data.put(EntityAPI.FIELD_NOTIFICATION_SENDER_AVATAR, fileGet.getUserAvatar());
                data.put(EntityAPI.FIELD_NOTIFICATION_IS_SEEN, false);
                if (!TextUtils.isEmpty(item.getUid()))
                    push.pushAddNotePrivate(data, item.getUid());
            }
            Utilities.contentPrivateNote = "";
            Utilities.lsFriendsNotePrivate.clear();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getContext(), new Crashlytics());
        fileGet = new FileSave(getActivity(), Constants.GET);
        filePut = new FileSave(getActivity(), Constants.PUT);
        arrayProperties = new ArrayList<>();
        dataComments = new ArrayList<>();
        lsUserIds = new ArrayList<>();
        context = getActivity();
        projectsController = new ProjectsController(getActivity());
        baseActivityTransition = new BaseActivityTransition(getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_UPDATE_IMAGE);
        return inflater.inflate(R.layout.fragment_tab_detail_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        funFindViewById(view);
        initView();
        initEvent();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void funFindViewById(View root) {
        layoutNotePrivate = root.findViewById(R.id.layout_note_info);
        layoutCountLike = root.findViewById(R.id.layout_count_like);
        progress_loading = root.findViewById(R.id.progress_loading);
        layout_loading = root.findViewById(R.id.layout_loading);
        txtEditOwner = root.findViewById(R.id.txtEditOwner);
        img_persion_1 = root.findViewById(R.id.img_persion_1);
        img_persion_2 = root.findViewById(R.id.img_persion_2);
        img_persion_3 = root.findViewById(R.id.img_persion_3);
        txtListFriendName = root.findViewById(R.id.txtListFriendName);
        fromNotePrivateAndFriend = root.findViewById(R.id.fromNotePrivateAndFriend);
        layoutAddNotePrivate = root.findViewById(R.id.from_add_note_private);
        txtNoComment = root.findViewById(R.id.txtNoComment);
        imgUserComment = root.findViewById(R.id.img_user_comment);
        imgCountLike = root.findViewById(R.id.img_count_like);
        tvCountLike = root.findViewById(R.id.tv_count_like);
        tvCountComments = root.findViewById(R.id.tv_count_comments);
        btnLike = root.findViewById(R.id.btn_like_post);
        imgLike = root.findViewById(R.id.img_like);
        tvLike = root.findViewById(R.id.tv_like);
        btnComment = root.findViewById(R.id.btn_comment_post);
        btnShare = root.findViewById(R.id.btn_share_post);
        layoutProperty = root.findViewById(R.id.layout_property);
        layoutDescription = root.findViewById(R.id.layout_description);
        txtTitleDes = root.findViewById(R.id.txtTitleDes);
        fromShowUserLikes = root.findViewById(R.id.from_show_like);
        layoutProjectInfo = root.findViewById(R.id.layout_project_info);
        txtProjectName = root.findViewById(R.id.txtProjectName);
        txtProjectInvestor = root.findViewById(R.id.txtProjectInvestor);
        txtProjectAddress = root.findViewById(R.id.txtProjectAddress);
        btnCall = root.findViewById(R.id.btn_call_owner);
        btnSms = root.findViewById(R.id.btn_mess_owner);
        layoutOwnerName = root.findViewById(R.id.layout_owner_name);
        layoutPriceCapital = root.findViewById(R.id.layout_owner_price);
        tvOwnerNote = root.findViewById(R.id.tv_note_owner);
        tvOwnerName = root.findViewById(R.id.tv_name_owner);
        tvOwnerPrice = root.findViewById(R.id.tv_price_capital);
        edtContent = root.findViewById(R.id.edtComment);
        rvComments = root.findViewById(R.id.rv_comments);
        rvComments.setHasFixedSize(true);
        gridViewCustomize1 = root.findViewById(R.id.gridView_Properties1);
        txtProductDescription = root.findViewById(R.id.txt_product_description);
        layoutPrivateOwnerInfo = root.findViewById(R.id.layout_private_info);
        from_info_owner = root.findViewById(R.id.from_info_owner);
        tvContentNote = root.findViewById(R.id.tv_content_note);
        tvContentNoteMyProduct = root.findViewById(R.id.tv_content_note_my_product);
        tvEditNote = root.findViewById(R.id.tv_edit_note_private);
        layoutSocial = root.findViewById(R.id.layout_social);
        layoutComment = root.findViewById(R.id.layout_comment);
        layoutNameOwner = root.findViewById(R.id.form_name_owner);

        tvEditProperties = root.findViewById(R.id.tv_edit_properties);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView() {
        try {
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            rvComments.setLayoutManager(mLayoutManager);
            dataComments = new ArrayList<>();
            adapterComment = new CommentsAdapter(getActivity(), dataComments, R.layout.item_comment_simple, null);
            rvComments.setAdapter(adapterComment);
            rvComments.setNestedScrollingEnabled(false);
            listImagesProduct = new ArrayList<>();
            listFriendShareNote = new ArrayList<>();

            mDatabaseRef = FirebaseDBUtil.getDatebase().getReference();
            sqlFriends = new SQLFriends(getActivity());

//            getListComments();

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void getListComments() {
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    registrationLastComments = mDatabaseRef.child(EntityFirebase.TableComments)
                            .child(DetailProductActivity.productKey)
                            .orderByChild(EntityFirebase.FieldDateCreated)
                            .limitToLast(5)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    try {
                                        dataComments.clear();
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            Comment comment = snapshot.getValue(Comment.class);

                                            if (!comment.uid.equals(fileGet.getUserId())) {
                                                UserRetro f = sqlFriends.checkIsFriendById(comment.uid);
                                                if (f != null) {
                                                    dataComments.add(comment);
                                                }
                                            } else {
                                                dataComments.add(comment);
                                            }
                                        }
                                        adapterComment.notifyDataSetChanged();
                                    } catch (Exception e1) {
                                        if (BuildConfig.DEBUG)
                                            Log.e("" + getClass().getName(), e1.getMessage());
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
        }
    }

    private void initEvent() {

        layoutNameOwner.setOnClickListener(v -> {
            try {
                if (productResponseV1.getOwner() != null) {
                    OwnerDetailV1 owner = productResponseV1.getOwner();
                    CustomerRetroV1 customerRetroV1 = new CustomerRetroV1();
                    customerRetroV1.setName(owner.getName());

                    if (owner.getPhone() != null) {
                        List<FieldCus> fieldCuses = new ArrayList<>();
                        fieldCuses.add(new FieldCus(owner.getPhone()));
                        customerRetroV1.setPhones(fieldCuses);
                    }

                    Intent detail = new Intent(getActivity(), ContactCustomerDetailActivity.class);
                    detail.putExtra(Constants.TEMP_CUSTOMER_KEY, owner.getId());
                    detail.putExtra(Constants.KEY_PASSINGDATA_CUSTOMER_OBJ, customerRetroV1);
                    detail.putExtra(Constants.KEY_POSITION_TAB_SELECT_CUSTOMER_DETAIL, 1);
                    baseActivityTransition.transitionTo(detail, 0);

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        });

        fromShowUserLikes.setOnClickListener(v -> {
            ListUserLikeCommentsDialog likeCommentsDialog = new ListUserLikeCommentsDialog(getActivity(), lsUserIds, false);
            likeCommentsDialog.show();
        });

        tvCountComments.setOnClickListener(v -> nextCommentActivity());

        txtEditOwner.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SelectCategoryActivity.class);
            intent.putExtra(Constants.KEY_PASSING_OWNER_INFO, true);
            intent.putExtra(Constants.KEY_INTENT_PASSING_OWNER, productResponseV1.getOwner());
            startActivityForResult(intent, Constants.RESULT_CODE_ADD_OWNER_INFO);
//            baseActivityTransition.transitionTo(intent, Constants.RESULT_CODE_ADD_OWNER_INFO);
        });

        layoutProjectInfo.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            Intent intent = new Intent(getActivity(), ProjectDetailsActivity.class);
            intent.putExtra("PROJECT_KEY", project);
            baseActivityTransition.transitionTo(intent, 0);
        });

        btnShare.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Welcome to CSELL");
            sendIntent.setType("text/plain");
            baseActivityTransition.transitionTo(sendIntent, 0);
        });


        edtContent.setOnClickListener(v -> nextCommentActivity());

        btnComment.setOnClickListener(v -> nextCommentActivity());

        tvEditNote.setOnClickListener(v -> {
            Intent edit = new Intent(getActivity(), ShareNotePrivateActivity.class);
            edit.putExtra(Constants.KEY_PASSINGDATA_NOTE_PRIVATE, Utilities.contentPrivateNote);
            edit.putExtra(Constants.KEY_PASSINGDATA_LIST_FRIEND_SHARE_NOTE, Utilities.lsFriendsNotePrivate);
            edit.putExtra(Constants.IS_EDIT_PRIVATE_NOTE_DETAIL_PRODUCT, true);
            baseActivityTransition.transitionTo(edit, 0);
        });

        layoutAddNotePrivate.setOnClickListener(v -> {
            Intent notePrivate = new Intent(getActivity(), ShareNotePrivateActivity.class);
            notePrivate.putExtra(Constants.KEY_ADD_NOTE_PRIVATE, true);
            notePrivate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            baseActivityTransition.transitionTo(notePrivate, 0);
        });

        btnLike.setOnClickListener(v -> actionLike());

        tvEditProperties.setOnClickListener(view -> {
            if (productResponseV1 == null) {
                Toast.makeText(getContext(), "" + getString(R.string.information_does_not_exist), Toast.LENGTH_SHORT).show();
                return;
            }

            Intent edit = new Intent(getContext(), EditProductActivityV1.class);
            edit.putExtra(Constants.KEY_PASSINGDATA_TYPE_EDIT_PRODUCT, 3);
            edit.putExtra(Constants.KEY_PASSINGDATA_EDIT_PRODUCT, false);
            edit.putExtra(Constants.KEY_PASSINGDATA_PRODUCT_OBJ, productResponseV1);
            baseActivityTransition.transitionTo(edit, Constants.EDIT_PROPERTIES_PRODUCT_RESULT);
        });
    }

    private void actionLike() {
//        try {
//            if (product.itMeLikePost) {
//                disLike();
//            } else {
//                setLike();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @SuppressLint("SetTextI18n")
    private void disLike() {
//        product.itMeLikePost = false;
//        product.totalLike--;
//        if (product.totalLike > 0) {
//            tvCountLike.setText(product.totalLike + "");
//        } else {
//            tvCountLike.setText("");
//        }
//        updateViewLike(false);
//        timer2.cancel();
//        timer1.cancel();
//        timer1 = new Timer();
//        timer1.schedule(
//                new TimerTask() {
//                    @Override
//                    public void run() {
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put(fileGet.getUserId(), null);
//                        mDatabaseRef.child(EntityFirebase.Field_User_Likes)
//                                .child(product.getItemid())
//                                .updateChildren(hashMap);
//                    }
//                }, DELAY);
    }

    @SuppressLint("SetTextI18n")
    private void setLike() {
//        product.itMeLikePost = true;
//        product.totalLike++;
//        tvCountLike.setText(product.totalLike + "");
//        updateViewLike(true);
//        timer1.cancel();
//        timer2.cancel();
//        timer2 = new Timer();
//        timer2.schedule(
//                new TimerTask() {
//                    @Override
//                    public void run() {
//
//                        HashMap<String, Object> maplikes = product.mapUserLikes;
//                        HashMap<String, Object> hashMap = new HashMap<>();
//
//                        if (maplikes == null) {
//                            hashMap.put(fileGet.getUserId(), true);
//                            mDatabaseRef.child(EntityFirebase.Field_User_Likes)
//                                    .child(product.getItemid())
//                                    .updateChildren(hashMap);
//                        } else if (maplikes.get(fileGet.getUserId()) == null) {
//                            hashMap.put(fileGet.getUserId(), true);
//                            mDatabaseRef.child(EntityFirebase.Field_User_Likes)
//                                    .child(product.getItemid())
//                                    .updateChildren(hashMap);
//                        }
//
//                    }
//                }, DELAY);
    }

    private void updateViewLike(boolean hasLike) {
//        if (product.totalLike > 0 || product.totalComments > 0) {
//            layoutCountLike.setVisibility(View.VISIBLE);
//        } else {
//            layoutCountLike.setVisibility(View.GONE);
//        }
//
//        if (product.totalLike > 0) {
//            imgCountLike.setVisibility(View.VISIBLE);
//            imgCountLike.setImageResource(R.drawable.icon_love);
//        } else {
//            imgCountLike.setVisibility(View.GONE);
//            imgCountLike.setImageDrawable(null);
//        }
//
//        if (hasLike) {
//            imgLike.setImageResource(R.drawable.icon_love);
//            tvLike.setTextColor(MainActivity.mainContext.getResources().getColor(R.color.red_100));
//        } else {
//            imgLike.setImageResource(R.drawable.icon_button_like);
//            tvLike.setTextColor(MainActivity.mainContext.getResources().getColor(R.color.text_button_like_share_comment));
//        }
    }

    private void nextCommentActivity() {
//        CommentDialog likeCommentsDialog = new CommentDialog(getActivity(), productResponseV1.getId(), productResponseV1.getName());
//        likeCommentsDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void getDataProductOnView(ProductResponseV1 productResponseV1) {
        try {
            try {
                if (productResponseV1 == null) {
                    Toast.makeText(getActivity(), "" + MainActivity.mainContext.getResources().getString(R.string.information_does_not_exist), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextUtils.isEmpty(productResponseV1.getName())) {
                    filePut.putProductNameCurrentSelect(productResponseV1.getName());
                }
                if (!TextUtils.isEmpty(productResponseV1.getId())) {
                    filePut.putProductIdCurrentSelect(productResponseV1.getId());
                }

                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
                gridViewCustomize1.setLayoutManager(gridLayoutManager);

                SQLProperties sqlProperties = new SQLProperties(getActivity());
                GridPropertiesAdapter adapterProperty = new GridPropertiesAdapter(getActivity(), arrayProperties, productResponseV1);
                gridViewCustomize1.setHasFixedSize(true);
                gridViewCustomize1.setAdapter(adapterProperty);

                if (productResponseV1.getCategories().get(0).getId().startsWith(Utilities.SIM)) {
                    layoutProjectInfo.setVisibility(View.GONE);
                    layoutProjectInfo.removeAllViews();
                    txtTitleDes.setVisibility(View.GONE);
                    txtProductDescription.setVisibility(View.GONE);

                    if (productResponseV1.getCategories().get(0).getId().startsWith(Utilities.SIM_LIST_MONTH)) {

                        layoutProperty.setVisibility(View.GONE);
                        layoutDescription.setVisibility(View.GONE);

                    } else {
                        layoutProperty.setVisibility(View.VISIBLE);
                        layoutDescription.setVisibility(View.VISIBLE);
                    }
                    if (productResponseV1.getImages() != null) {
                        for (String item : productResponseV1.getImages()) {
                            if (!TextUtils.isEmpty(item)) {
                                break;
                            }
                        }
                    }

                } else {
                    if (productResponseV1.getCategories().get(0).getId().startsWith(Utilities.LAND_PROJECT)) {
                        projectsController.getProjects(productResponseV1.getProject().getId(), this);
                        layoutProjectInfo.setVisibility(View.VISIBLE);
                        txtProjectName.setText(productResponseV1.getProject().getName() + "");
                    } else {
                        layoutProjectInfo.setVisibility(View.GONE);
                    }

                    layoutProperty.setVisibility(View.VISIBLE);
                    layoutDescription.setVisibility(View.VISIBLE);
                    txtTitleDes.setVisibility(View.VISIBLE);
                    txtProductDescription.setVisibility(View.VISIBLE);
                    txtProductDescription.setText(productResponseV1.getContent() + "");
                    listImagesProduct.clear();
                    if (productResponseV1.getImages() != null) {
                        if (productResponseV1.getImages().size() > 0) {

                            listImagesProduct.addAll(productResponseV1.getImages());
                        } else {
                            listImagesProduct.add(Constants.LINK_DEFAUT_NOT_CHOOSE);
                        }
                    } else {
                        listImagesProduct.add(Constants.LINK_DEFAUT_NOT_CHOOSE);
                    }
                }

                getPrivateInfo();

                if (!productResponseV1.getCategories().get(0).getId().startsWith(Utilities.SIM_LIST_MONTH)) {
                    arrayProperties.clear();
                    arrayProperties.addAll(sqlProperties.getAllPropertiesDetail(productResponseV1.getCategories().get(0).getId(), 3));

                    Collections.sort(arrayProperties, (o1, o2) -> o1.index.compareTo(o2.index));
                    Collections.sort(arrayProperties, (o1, o2) -> o1.primary.compareTo(o2.primary));
                    adapterProperty.notifyDataSetChanged();
                }

            } catch (Exception e1) {

                if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e1.getMessage());
                Crashlytics.logException(e1);
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(e);
        }
    }

    private void hideSocialInfo() {
        layoutSocial.setVisibility(View.GONE);
        layoutComment.setVisibility(View.GONE);
        rvComments.setVisibility(View.GONE);
        layoutSocial.removeAllViews();
        layoutComment.removeAllViews();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RESULT_CODE_ADD_OWNER_INFO) {
//            Toast.makeText(getActivity(), "" + MainActivity.mainContext.getResources().getString(R.string.text_update_success), Toast.LENGTH_SHORT).show();
            if (data != null) {
                try {
                    OwnerDetailV1 owner = (OwnerDetailV1) data.getSerializableExtra(Constants.KEY_INTENT_PASSING_OWNER);
                    productResponseV1.setOwner(owner);
                    getPrivateInfo();

                    layoutNameOwner.setOnClickListener(v -> {
                        Intent detail = new Intent(getActivity(), ContactCustomerDetailActivity.class);
                        detail.putExtra(Constants.TEMP_CUSTOMER_KEY, owner.getId());

                        CustomerRetro customerRetro = new CustomerRetro();
                        customerRetro.setName(owner.getName());

                        if (owner.getPhone() != null) {
                            List<String> temp = new ArrayList<>();
                            temp.add(owner.getPhone());
                            customerRetro.setPhone(temp);
                        }

                        detail.putExtra(Constants.KEY_PASSINGDATA_CUSTOMER_OBJ, customerRetro);
                        detail.putExtra(Constants.KEY_POSITION_TAB_SELECT_CUSTOMER_DETAIL, 1);
                        baseActivityTransition.transitionTo(detail, 0);
                    });

                } catch (Exception ignored) {

                }
            }
        }
        if (resultCode == Constants.EDIT_PRODUCT_RESULT) {
////uP DATE
//            SSSSS
        }
    }

    private void getPrivateInfo() {
        if (DetailProductActivity.isMyProduct) {
            try {
                if (Utilities.isPrivateMode) {
                    try {
                        if (productResponseV1.getOwner() != null && !TextUtils.isEmpty(productResponseV1.getOwner().getId())) {
                            from_info_owner.setVisibility(View.VISIBLE);
                            layoutPrivateOwnerInfo.setVisibility(View.VISIBLE);
                            txtEditOwner.setText(MainActivity.mainContext.getResources().getString(R.string.text_edit_info_owner));

                            SQLCustomers sqlCustomers = new SQLCustomers(getActivity());
                            CustomerRetro customerRetro = sqlCustomers.getCustomerById(productResponseV1.getOwner().getId());

                            String ownerName = customerRetro.getName();

                            String ownerNote = TextUtils.isEmpty(productResponseV1.getOwner().getNote()) ? "" : productResponseV1.getOwner().getNote();
                            if (customerRetro.getPhone() != null) {
                                if (customerRetro.getPhone().size() > 0) {
                                    ownerPhone = customerRetro.getPhone().get(0);
                                } else {
                                    ownerPhone = "";
                                }
                            } else {
                                ownerPhone = "";
                            }
                            Long ownerPrice = productResponseV1.getOwner().getOriginPrice() == null ? 0 : productResponseV1.getOwner().getOriginPrice();

                            if (TextUtils.isEmpty(ownerPhone)) {
                                btnCall.setVisibility(View.GONE);
                                btnSms.setVisibility(View.GONE);
                            } else {
                                btnCall.setVisibility(View.VISIBLE);
                                btnSms.setVisibility(View.VISIBLE);
                            }

                            layoutOwnerName.setVisibility(View.VISIBLE);
                            tvOwnerName.setText(ownerName);

                            if (!TextUtils.isEmpty(ownerPrice + "")) {

                                layoutPriceCapital.setVisibility(View.VISIBLE);
                                tvOwnerPrice.setText(Utilities.formatMoney(ownerPrice, productResponseV1.getOwner().getCurrency()));

                            } else {
                                layoutPriceCapital.setVisibility(View.GONE);
                            }

                            if (!TextUtils.isEmpty(ownerNote)) {
                                tvOwnerNote.setTypeface(tvOwnerNote.getTypeface());
                                tvOwnerNote.setVisibility(View.VISIBLE);
                                tvOwnerNote.setText(ownerNote);
                            } else {
                                tvOwnerNote.setVisibility(View.GONE);
                            }

                            btnSms.setOnClickListener(v -> {
                                if (TextUtils.isEmpty(ownerPhone)) {
                                    Snackbar.make(v, MainActivity.mainContext.getResources().getString(R.string.customer_not_phone_number), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                    return;
                                }
                                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", ownerPhone.trim(), null));
                                baseActivityTransition.transitionTo(intent, 0);
                            });

                            btnCall.setOnClickListener(v -> {
                                if (TextUtils.isEmpty(ownerPhone)) {
                                    Snackbar.make(v, MainActivity.mainContext.getResources().getString(R.string.customer_not_phone_number), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                    return;
                                }
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", ownerPhone.trim(), null));
                                baseActivityTransition.transitionTo(intent, 0);
                            });

                        } else {
                            txtEditOwner.setText(MainActivity.mainContext.getResources().getString(R.string.text_add_owner_info));
                            layoutOwnerName.setVisibility(View.GONE);
                            layoutPrivateOwnerInfo.setVisibility(View.VISIBLE);
                            tvOwnerNote.setVisibility(View.VISIBLE);
                            tvOwnerNote.setText(MainActivity.mainContext.getResources().getString(R.string.text_add_owner_info_not_exist));
                            tvOwnerNote.setTypeface(tvOwnerNote.getTypeface(), Typeface.ITALIC);
                            from_info_owner.setVisibility(View.GONE);
                        }

                    } catch (Exception e1) {
                        if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e1.getMessage());
                        Crashlytics.logException(e1);
                        layoutPrivateOwnerInfo.setVisibility(View.GONE);
                    }

                    if (!TextUtils.isEmpty(productResponseV1.getNote())) {
                        tvContentNote.setText(productResponseV1.getNote());
                        tvContentNoteMyProduct.setText(productResponseV1.getNote());
                        fromNotePrivateAndFriend.setVisibility(View.VISIBLE);
                        layoutAddNotePrivate.setVisibility(View.GONE);
                        Utilities.lsFriendsNotePrivate.clear();
                        listFriendShareNote.clear();
//                        listFriendShareNote.addAll(product.getCanSeeNotePrivate());
                        Utilities.lsFriendsNotePrivate.addAll(listFriendShareNote);
                        Utilities.contentPrivateNote = productResponseV1.getNote();
                        updateViewNoteFriend(Utilities.lsFriendsNotePrivate);


                    } else {
                        fromNotePrivateAndFriend.setVisibility(View.GONE);
                        layoutAddNotePrivate.setVisibility(View.VISIBLE);
                    }

                } else {
                    layoutPrivateOwnerInfo.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(productResponseV1.getNote())) {
                        layoutAddNotePrivate.setVisibility(View.GONE);
                    } else {
                        layoutAddNotePrivate.setVisibility(View.GONE); // Hien thi khi can them noi dung rieng tu
                    }

                }
            } catch (Exception e) {
                layoutPrivateOwnerInfo.setVisibility(View.GONE);
                layoutAddNotePrivate.setVisibility(View.GONE);
            }
        } else {
            try {
                if (Utilities.isPrivateMode) {
//                    if (!fileGet.getUserId().equals(product.getUserInfo().getUid())) {
//
//                        if (product.getHasNotePrivate()) {
//                            if (TextUtils.isEmpty(product.getNotePrivate())) {
//                                layoutNotePrivate.setVisibility(View.GONE);
//                            } else {
//                                layoutNotePrivate.setVisibility(View.VISIBLE);
//                                tvContentNote.setText(product.getNotePrivate());
//                                tvContentNoteMyProduct.setText(product.getNotePrivate());
//                            }
//                        } else {
//                            layoutNotePrivate.setVisibility(View.GONE);
//                        }
//                    }
                } else {

                    if (DetailProductActivity.turnPrivateMode) {
                        showDialogInputPass();
                    }
                    layoutNotePrivate.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
    }

    private void getLikeComments() {

        GlideApp.with(this)
                .load(!TextUtils.isEmpty(fileGet.getUserAvatar()) ? fileGet.getUserAvatar() : R.drawable.ic_logo)
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(imgUserComment);

        getCountLikes();
        getCountComments();

    }

    @SuppressLint("StaticFieldLeak")
    private void getCountLikes() {

//        try {
//            new AsyncTask<Void, Void, Void>() {
//                @Override
//                protected Void doInBackground(Void... voids) {
//                    mDatabaseRef.child(EntityFirebase.Field_User_Likes).child(DetailProductActivity.productKey)
//                            .addValueEventListener(new ValueEventListener() {
//                                @SuppressLint("SetTextI18n")
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.getChildrenCount() > 0) {
//
//                                        Object o = dataSnapshot.getValue();
//
//                                        HashMap<String, Object> mapLikes = (HashMap<String, Object>) o;
//                                        try {
//                                            assert mapLikes != null;
//                                            lsUserIds.addAll(mapLikes.keySet());
//                                            int i = mapLikes.size();
//                                            tvCountLike.setText(i + "");
//
//                                            if (i > 0) {
//                                                imgCountLike.setImageResource(R.drawable.icon_love);
//                                            }
//
//                                            if (mapLikes.get(fileGet.getUserId()) == null) {
//                                                product.totalLike = mapLikes.size();
//                                                product.itMeLikePost = false;
//                                                updateViewLike(false);
//                                            } else {
//                                                product.totalLike = i;
//                                                if ((boolean) mapLikes.get(fileGet.getUserId())) {
//                                                    product.itMeLikePost = true;
//                                                    updateViewLike(true);
//                                                } else {
//                                                    product.itMeLikePost = false;
//                                                    updateViewLike(false);
//                                                }
//                                            }
//                                        } catch (Exception ignored) {
//                                        }
//
//                                        product.mapUserLikes = mapLikes;
//
//
//                                    } else {
//                                        if (product.totalComments == 0) {
//                                            layoutCountLike.setVisibility(View.GONE);
//                                        }
//                                        product.totalLike = 0;
//                                        product.itMeLikePost = false;
//                                        tvCountLike.setText("");
//                                        updateViewLike(false);
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//                                    Crashlytics.logException(databaseError.toException());
//                                }
//                            });
//                    return null;
//                }
//            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//        } catch (Exception e) {
//            Crashlytics.logException(e);
//        }

    }

    private void getCountComments() {

//        try {
//            new AsyncTask<Void, Void, Void>() {
//
//                @Override
//                protected Void doInBackground(Void... voids) {
//                    registrationCountComments = mDatabaseRef.child(EntityFirebase.TableComments).child(DetailProductActivity.productKey)
//                            .addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.getChildrenCount() > 0) {
//                                        product.totalComments = dataSnapshot.getChildrenCount();
//                                        countComments = dataSnapshot.getChildrenCount();
//                                        String text;
//                                        txtNoComment.setVisibility(View.GONE);
//                                        rvComments.setVisibility(View.VISIBLE);
//                                        try {
//                                            text = countComments + " "
//                                                    + getString(R.string.comment).toLowerCase();
//                                        } catch (Exception e) {
//                                            text = countComments + " " + MainActivity.mainContext.getResources().getString(R.string.comment);
//                                        }
//                                        tvCountComments.setText(text);
//                                        tvCountComments.setVisibility(View.VISIBLE);
//                                        layoutCountLike.setVisibility(View.VISIBLE);
//                                    } else {
//                                        if (product.totalLike == 0) {
//                                            layoutCountLike.setVisibility(View.GONE);
//                                        }
//                                        txtNoComment.setVisibility(View.VISIBLE);
//                                        rvComments.setVisibility(View.GONE);
//                                        product.totalComments = 0;
//                                        tvCountComments.setVisibility(View.GONE);
//                                    }
//
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//                                    product.totalComments = 0;
//                                    txtNoComment.setVisibility(View.VISIBLE);
//                                    rvComments.setVisibility(View.GONE);
//                                    Crashlytics.logException(databaseError.toException());
//                                }
//                            });
//                    return null;
//                }
//            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//        } catch (Exception e) {
//            Crashlytics.logException(e);
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (registrationCountComments != null) {
                mDatabaseRef.removeEventListener(registrationCountComments);
            }
            if (registrationLastComments != null) {
                mDatabaseRef.removeEventListener(registrationLastComments);
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (layoutPrivateOwnerInfo != null)
            layoutPrivateOwnerInfo.setVisibility(View.GONE);
    }

    private void showDialogInputPass() {
        try {
            dialog = new InputPasswordPrivateMode(mContext, this, Constants.PRIVATE_MODE, this);
            dialog.show();
        } catch (Exception ignored) {
            ignored.getMessage();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fromNotePrivateAndFriend = null;
    }

    public void loadingView(boolean loading) {
        if (loading) {
            progress_loading.setVisibility(View.VISIBLE);
            layout_loading.setVisibility(View.VISIBLE);
        } else {
            progress_loading.setVisibility(View.GONE);
            layout_loading.setVisibility(View.GONE);
        }
    }

    public void reloadViewTabInfo(ProductResponseV1 productResponse) {
        Bundle bundle = getArguments();
        if (bundle != null) {
//            product = (Product) getArguments().getSerializable(Constants.KEY_DETAIL_PRODUCT);
            productResponseV1 = productResponse;
        }
        getDataProductOnView(productResponse);
    }

    @Override
    public void showNote() {
        getPrivateInfo();
    }

    @Override
    public void onGetProjectSuccess(Project data) {
        try {
            project = data;
            txtProjectName.setText(project.getProjectName());
            if (TextUtils.isEmpty(DetailProductActivity.productKey)) {
                DetailProductActivity.productKey = project.getProjectid() + "";
            }
            if (TextUtils.isEmpty(DetailProductActivity.productName)) {
                DetailProductActivity.productName = project.getProjectName() + "";
            }
            txtProjectName.setTextColor(MainActivity.mainContext.getResources().getColor(R.color.orange));
            txtProjectInvestor.setText(project.getInvestor());
            txtProjectAddress.setText(project.getAddress());
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onGetProjectFailure() {

    }

    @Override
    public void onConnectProjectFailure() {

    }

    @Override
    public void onLoginSuccess(ResLogin response) {
        try {
//            dialog.handeResponseSuccess(response);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onAcountActive() {

    }

    @Override
    public void onErrorLogin() {

    }

    @Override
    public void onLoginFailure(String account) {
        try {
            dialog.handleResponseFailure();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onConnectLoginFailure() {
        dialog.handleResponseConnectFailure();
    }
}
