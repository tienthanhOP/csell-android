package csell.com.vn.csell.views.product.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.ServerValue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.apis.ImageResponse;
import csell.com.vn.csell.apis.JSONProductV1;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.commons.PushNotifications;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.controllers.FriendsController;
import csell.com.vn.csell.controllers.NotiController;
import csell.com.vn.csell.models.ImageSuffix;
import csell.com.vn.csell.models.PostTypeV1;
import csell.com.vn.csell.models.ProductResponseModel;
import csell.com.vn.csell.models.ProductResponseV1;
import csell.com.vn.csell.models.ProjectV1;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.sqlites.SQLCategories;
import csell.com.vn.csell.sqlites.SQLPostTypesV1;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.product.activity.SelectCategoryActivity;
import csell.com.vn.csell.views.product.activity.ShareNotePrivateActivity;
import csell.com.vn.csell.views.product.adapter.PostTypeAdapter;
import io.fabric.sdk.android.Fabric;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.google.firebase.firestore.//DocumentReference;
//import com.google.firebase.firestore.FieldValue;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.//writeBatch;

/**
 * Created by cuong.nv on 4/12/2018.
 */

@SuppressLint("StaticFieldLeak")
public class EndCreateFragment extends Fragment implements FriendsController.OnGetFriendsListener, NotiController.OnSendNotiListener {

    public static LinearLayout fromNotePrivateAndFriend;
    public static ImageView img_persion_1;
    public static ImageView img_persion_2;
    public static ImageView img_persion_3;
    public static TextView txtListFriendName;
    private static Context context = null;
    private TextView txtPost;
    private ProgressBar progressLoading;
    private LinearLayout fromLoadding;
    private RecyclerView rvPostType;
    private SQLPostTypesV1 sqlPostTypes;
    private SQLCategories sqlCategories;
    private FileSave fileGet;
    private List<PostTypeV1> lsPostType;
    private PostTypeAdapter postTypeAdapter;
    private TextView tvEditNote;
    private boolean success = false;
    private int timeout = 0;
    //    private int sizeImage = -1;
    private ArrayList<ImageSuffix> imageSuffixes;
    private NotiController notiController;

    @SuppressLint("SetTextI18n")
    public static void updateViewNoteFriend(List<UserRetro> lsFriends) {
        try {
            fromNotePrivateAndFriend.setVisibility(View.VISIBLE);
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
                txtListFriendName.setText(lsFriends.get(0).getDisplayname() + " " + context.getString(R.string.text_and) + " " + (lsFriends.size() - 1)
                        + " " + context.getString(R.string.text_with_persons_orther));
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

                txtListFriendName.setText(lsFriends.get(0).getDisplayname() + " " + context.getString(R.string.text_and) + " " + (lsFriends.size() - 1)
                        + " " + context.getString(R.string.text_with_persons_orther));
            } else if (lsFriends.size() == 1) {
                img_persion_1.setVisibility(View.VISIBLE);
                img_persion_2.setVisibility(View.GONE);
                img_persion_3.setVisibility(View.GONE);
                GlideApp.with(context)
                        .load(lsFriends.get(0).getAvatar() == null ? R.drawable.ic_logo : lsFriends.get(0).getAvatar())
                        .error(R.drawable.ic_logo)
                        .placeholder(R.drawable.ic_logo)
                        .into(img_persion_1);

                txtListFriendName.setText(lsFriends.get(0).getDisplayname() + " ");
            } else {
                fromNotePrivateAndFriend.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getContext(), new Crashlytics());
        sqlPostTypes = new SQLPostTypesV1(getActivity());
        sqlCategories = new SQLCategories(getActivity());
        lsPostType = new ArrayList<>();
        fileGet = new FileSave(getActivity(), Constants.GET);
        imageSuffixes = new ArrayList<>();
        notiController = new NotiController(getActivity());
        try {
            FriendsController friendsController = new FriendsController(getActivity());
            friendsController.getFriends(0, 5000, this);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d(getClass().getSimpleName(), e.getMessage());
            Crashlytics.logException(new Exception(getClass().getSimpleName() + "/" + e));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_end_create_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        if (Utilities.lsFriendsNotePrivate != null) {
            Utilities.lsFriendsNotePrivate.clear();
        }

        funFindViewById(view);
        initView();
        initEvent();
    }

    private void funFindViewById(View view) {
        tvEditNote = view.findViewById(R.id.tv_edit_note_private);
        rvPostType = view.findViewById(R.id.rv_postType);
        progressLoading = view.findViewById(R.id.progress_loading);
        fromLoadding = view.findViewById(R.id.from_loadding);
        fromNotePrivateAndFriend = view.findViewById(R.id.fromNotePrivateAndFriend);
        txtPost = view.findViewById(R.id.txtPost);
        img_persion_1 = view.findViewById(R.id.img_persion_1);
        img_persion_2 = view.findViewById(R.id.img_persion_2);
        img_persion_3 = view.findViewById(R.id.img_persion_3);
        txtListFriendName = view.findViewById(R.id.txtListFriendName);
    }

    private void initView() {
        rvPostType.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());
        mLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        rvPostType.setLayoutManager(mLayoutManager2);
        lsPostType = sqlPostTypes.getAllPostType();
        if (lsPostType.size() == 0) {
            lsPostType = sqlPostTypes.getAllPostType();
        }
        postTypeAdapter = new PostTypeAdapter(getActivity(), lsPostType, false);
        rvPostType.setAdapter(postTypeAdapter);
    }

    private void initEvent() {
        BaseActivityTransition baseActivityTransition = new BaseActivityTransition(getActivity());
        txtPost.setOnClickListener(v -> createProduct());
        tvEditNote.setOnClickListener(v -> {
            Intent edit = new Intent(getActivity(), ShareNotePrivateActivity.class);
            edit.putExtra(Constants.KEY_PASSINGDATA_NOTE_PRIVATE, Utilities.contentPrivateNote);
            edit.putExtra(Constants.KEY_PASSINGDATA_LIST_FRIEND_SHARE_NOTE, Utilities.lsFriendsNotePrivate);
            edit.putExtra(Constants.IS_EDIT_PRIVATE_NOTE_CREATE_PRODUCT, true);
            baseActivityTransition.transitionTo(edit, 0);
        });
    }

    private void updateOnView(boolean successfull) {
        if (successfull) {
            fromLoadding.setVisibility(View.VISIBLE);
            fromLoadding.setEnabled(false);
            progressLoading.setVisibility(View.VISIBLE);
            txtPost.setEnabled(false);
            txtPost.setEnabled(false);
        } else {
            progressLoading.setVisibility(View.GONE);
            fromLoadding.setVisibility(View.GONE);
            fromLoadding.setEnabled(true);
            txtPost.setEnabled(true);
            txtPost.setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((SelectCategoryActivity) getActivity()).updateTitleToolbar("Hoàn tất");
        SelectCategoryActivity.currentFragment = "EndCreateFragment";
    }

    private void createProduct() {
        List<String> paramsImages = SelectCategoryActivity.paramsImages;
        if (paramsImages.size() > 0) {
            List<MultipartBody.Part> parts = new ArrayList<>();
            for (int i = 0; i < paramsImages.size(); i++) {
                File imgFile = new File(paramsImages.get(i));
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imgFile);
                MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", imgFile.getName(), requestFile);
                parts.add(multipartBody);
            }

            PostAPI postAPI = RetrofitClient.createServiceUploadImage(PostAPI.class, fileGet.getToken());

            Call<ImageResponse> imageResponseCall = postAPI.uploadImageInGroup(parts, "products");
            imageResponseCall.enqueue(new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_IMAGES, response.body().getUrls());
                            createProductAPI();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    Log.i("onFailure", "onFailure: " + t.getMessage());
                }
            });
        } else {
            createProductAPI();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void createProductAPI() {
        try {
            updateOnView(true);
            PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());

            if (postAPI != null) {
                Utilities.refreshToken(MainActivity.mainContext);
                if (SelectCategoryActivity.paramsProductPrivate != null & SelectCategoryActivity.paramsProductPrivate.size() > 0) {
                    SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_OWNER, SelectCategoryActivity.paramsProductPrivate);
                }

                ProjectV1 projectV1 = new ProjectV1(fileGet.getProjectId(), fileGet.getProjectName(), null);
                if (!projectV1.getName().equals("")) {
                    SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_PROJECT, projectV1);
                }
                SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_CATEGORIES, SelectCategoryActivity.paramsCategories);
                if (!SelectCategoryActivity.paramsProduct.get(EntityAPI.FIELD_PRIVACY).equals("onlyme")) {
                    SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_FRIENDS, new ArrayList<>());
                }

                SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_ATTRIBUTES, SelectCategoryActivity.paramsProperty);
                // thêm note private
//                addNotePrivate();
                Call<JSONProductV1<ProductResponseV1>> jsonResponseCall = postAPI.addProduct(SelectCategoryActivity.paramsProduct);
                jsonResponseCall.enqueue(new Callback<JSONProductV1<ProductResponseV1>>() {
                    @Override
                    public void onResponse(Call<JSONProductV1<ProductResponseV1>> call, Response<JSONProductV1<ProductResponseV1>> response) {
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    if (response.body().getCode() == 0) {
                                        getActivity().setResult(Constants.ADD_PRODUCT_RESULT);
                                        getActivity().finish();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.mainContext, MainActivity.mainContext.getResources().getString(R.string.pls_try_again),
                                    Toast.LENGTH_SHORT).show();
                            Crashlytics.logException(e);
                        }

                        updateOnView(false);
                        jsonResponseCall.cancel();
                    }

                    @Override
                    public void onFailure(Call<JSONProductV1<ProductResponseV1>> call, Throwable t) {
                        if (BuildConfig.DEBUG)
                            Log.d(getClass().getSimpleName(), t.getMessage() + "");
                        success = false;
                        updateOnView(false);
                        Crashlytics.logException(t);
                        jsonResponseCall.cancel();
                        createProductAPI();
                        Toast.makeText(MainActivity.mainContext, MainActivity.mainContext.getResources().getString(R.string.pls_try_again),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // add lai header
                updateOnView(false);
            }
        } catch (Exception e) {
            updateOnView(false);
            if (BuildConfig.DEBUG) Log.d(getClass().getSimpleName(), e.getMessage());
            Crashlytics.logException(e);
        }
    }

    private void addNotePrivate() {
        if (Utilities.lsFriendsNotePrivate.size() > 0) {

            SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_NOTE_PRIVATE, Utilities.contentPrivateNote);

            ArrayList<UserRetro> lst = new ArrayList<>();
            for (UserRetro user : Utilities.lsFriendsNotePrivate) {
                user.IsSelected = null;
                user.setEmail(null);
                user.setPhone(null);
                user.setUsername(null);
                lst.add(user);
            }

            SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_CAN_SEE_NOTE_PRIVATE, lst);

        }
    }

    private void sendNotiShareNotePrivate(ProductResponseModel data) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put(EntityAPI.FIELD_TYPE_SEND_EMAIL, 1);
            map.put(EntityAPI.FIELD_CONTENT, "đã chia sẻ nội dung riêng tư cho bạn từ " + data.getTitle());
            String keyNoti = data.getItemid() + ":" + fileGet.getUserId();
            map.put(EntityAPI.FIELD_KEY, keyNoti);
            List<String> lstUsers = new ArrayList<>();
            for (UserRetro userRetro : Utilities.lsFriendsNotePrivate) {
                lstUsers.add(userRetro.getUid());
            }
            map.put(EntityAPI.FIELD_LIST_USERS, lstUsers);

            notiController.sendNoti(map, this);
        } catch (Exception ignored) {

        }
    }

    public void checkAddNotePrivate(String productId, String productName) {
        if (Utilities.lsFriendsNotePrivate.size() != 0) {
            PushNotifications push = new PushNotifications(context);
            for (UserRetro item : Utilities.lsFriendsNotePrivate) {
                HashMap<String, Object> data = new HashMap<>();
                data.put(EntityAPI.FIELD_NOTIFICATION_TITLE, "Share Note Private");
                data.put(EntityAPI.FIELD_NOTIFICATION_BODY, fileGet.getDisplayName() + " "
                        + context.getString(R.string.text_body_shared_note_private) + " "
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
    public void onDestroyView() {
        super.onDestroyView();
        fromNotePrivateAndFriend = null;
    }

    @Override
    public void onGetFriendsSuccess(ArrayList<UserRetro> data) {

    }

    @Override
    public void onGetFriendsFailure() {

    }

    @Override
    public void onConnectGetFriendsFailure() {

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
