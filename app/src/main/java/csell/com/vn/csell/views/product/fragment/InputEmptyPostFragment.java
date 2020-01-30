package csell.com.vn.csell.views.product.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.product.activity.SelectCategoryActivity;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.models.UserRetro;
import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InputEmptyPostFragment extends Fragment {

    private Button btnSharePost;
    private CircleImageView imgBackground1;
    private CircleImageView imgBackground2;
    private CircleImageView imgBackground3;
    private CircleImageView imgBackground4;
    private CircleImageView imgBackground5;
    private ImageView background;
    //    private EditText edt_sim_title;
    private EditText edt_description;

    private FileSave fileGet;
    private FileSave filePut;

    //private FirebaseFirestore mFireStoreRef;

    private ProgressBar progressBar;
    private LinearLayout layoutContainer;
    HashMap<String, Object> empty = new HashMap<>();
    private FancyButton btnSaveToolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_input_empty_post, container, false);
        initView(rootView);
        addEvent();
        return rootView;
    }


    @SuppressLint("SetTextI18n")
    private void initView(View rootView) {
        try {
            btnSaveToolbar = getActivity().findViewById(R.id.btn_save_navigation);
            btnSaveToolbar.setVisibility(View.GONE);
            layoutContainer = rootView.findViewById(R.id.layout_container);
            progressBar = rootView.findViewById(R.id.progress_bar);
            //mFireStoreRef = FirebaseDBUtil.getFirestore();
            fileGet = new FileSave(getActivity(), Constants.GET);
            filePut = new FileSave(getActivity(), Constants.PUT);
            btnSharePost = rootView.findViewById(R.id.btn_share_post);
//            edt_sim_title = rootView.findViewById(R.id.edt_sim_title);
            edt_description = rootView.findViewById(R.id.edt_description);
            imgBackground1 = rootView.findViewById(R.id.img_color_1);
            imgBackground2 = rootView.findViewById(R.id.img_color_2);
            imgBackground3 = rootView.findViewById(R.id.img_color_3);
            imgBackground4 = rootView.findViewById(R.id.img_color_4);
            imgBackground5 = rootView.findViewById(R.id.img_color_5);
            background = rootView.findViewById(R.id.image_bg);
            background.setClipToOutline(true);
            defaulSimImage(MainActivity.mainContext.getResources().getString(R.string.bg_sim_url_1));
            SelectCategoryActivity.currentFragment = "InputEmptyPostFragment";

            edt_description.setText("[" + fileGet.getPrefixCateTemp() + "] \n");

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
        }
    }

    private void addEvent() {

        btnSharePost.setOnClickListener(v -> {
//
            try {
                //description
                String description = edt_description.getText().toString();

                if (TextUtils.isEmpty(description)) {
                    Toast.makeText(getActivity(), getString(R.string.description_can_not_empty), Toast.LENGTH_LONG).show();
                    return;
                } else {
                    empty.put(EntityAPI.FIELD_DESCRIPTION, description);
                }
                showProgress(true);
                //root cat name
                if (TextUtils.isEmpty(fileGet.getRootCategoryId())) {
                    empty.put(EntityAPI.FIELD_CAT_ID, fileGet.getCategoryIdPostEmpty());
                } else {
                    empty.put(EntityAPI.FIELD_CAT_ID, fileGet.getRootCategoryId());
                }
                empty.put(EntityAPI.FIELD_CAT_NAME, fileGet.getCategoryNamePostEmpty());
                empty.put(EntityAPI.FIELD_ROOT_CAT_NAME, fileGet.getCategoryNamePostEmpty());
                empty.put(EntityAPI.FIELD_SUB_CAT_NAME, fileGet.getCategoryNamePostEmpty());
                empty.put(EntityAPI.FIELD_TITLE, "Tin đăng nhanh " + fileGet.getCategoryNamePostEmpty());
                if (fileGet.getRootCategoryId().startsWith(Utilities.LAND_PROJECT)) {
                    empty.put(EntityAPI.FIELD_EMPTY_POST_PROJECT_ID, fileGet.getProjectId());
                    empty.put(EntityAPI.FIELD_EMPTY_POST_PROJECT_NAME, fileGet.getProjectName());
                } else {
                    empty.put(EntityAPI.FIELD_EMPTY_POST_PROJECT_ID, fileGet.getProjectIdPostEmpty());
                    empty.put(EntityAPI.FIELD_EMPTY_POST_PROJECT_NAME, fileGet.getProjectNamePostEmpty());
                }
                postEmpty(empty);


            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.d("" + getClass().getName(), e.getMessage());
                Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                showProgress(false);
            }

        });

        imgBackground1.setOnClickListener(v -> {
            background.setImageResource(R.drawable.bg_3);
            defaulSimImage(MainActivity.mainContext.getResources().getString(R.string.bg_sim_url_1));

        });
        imgBackground2.setOnClickListener(v -> {
            background.setImageResource(R.drawable.bg_4);
            defaulSimImage(MainActivity.mainContext.getResources().getString(R.string.bg_sim_url_2));
        });
        imgBackground3.setOnClickListener(v -> {
            background.setImageResource(R.drawable.bg_5);
            defaulSimImage(MainActivity.mainContext.getResources().getString(R.string.bg_sim_url_3));
        });
        imgBackground4.setOnClickListener(v -> {
            background.setImageResource(R.drawable.bg_2);
            defaulSimImage(MainActivity.mainContext.getResources().getString(R.string.bg_sim_url_4));
        });
        imgBackground5.setOnClickListener(v -> {
            background.setImageResource(R.drawable.bg_1);
            defaulSimImage(MainActivity.mainContext.getResources().getString(R.string.bg_sim_url_5));
        });

    }

    private void postEmpty(HashMap<String, Object> params) {
        try {
            PostAPI postAPI = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
            if (postAPI != null) {
                Call<JSONResponse<List<Product>>> jsonResponseCall = postAPI.postEmpty(params);
                jsonResponseCall.enqueue(new Callback<JSONResponse<List<Product>>>() {
                    @Override
                    public void onResponse(Call<JSONResponse<List<Product>>> call, Response<JSONResponse<List<Product>>> response) {
                        if (response.isSuccessful()) {
                            JSONResponse<List<Product>> result = response.body();
                            if (result.getSuccess() != null) {
                                if (result.getSuccess()) {
                                    if (result.getData().size() > 0) {
                                        Product product = result.getData().get(0);
                                        try {
                                            product.setCatid(empty.get(EntityAPI.FIELD_CAT_ID).toString() + "");
                                            product.setUserInfo(new UserRetro(fileGet.getUserId(), fileGet.getDisplayName(), fileGet.getUserAvatar()));
                                            product.setType(2);
                                            Calendar calendar = Calendar.getInstance();
//                                    yyyy-MM-dd HH:mm:ss
                                            String date = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
                                            String time = calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
                                            product.setDateShared(date + " " + time);
                                            product.setBackground(empty.get(EntityAPI.FIELD_BACKGROUND).toString());
                                        } catch (Exception ignored) {

                                        }

                                        Intent intent = new Intent();
                                        intent.putExtra(Constants.KEY_PASSINGDATA_EMPTY_POST, product);
                                        getActivity().setResult(Constants.POST_EMPTY, intent);
                                        getActivity().finishAfterTransition();
                                    } else {
                                        Toast.makeText(getActivity(), "" + result.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Utilities.refreshToken(getActivity(), result.getMessage().toLowerCase() + "");
                                    Toast.makeText(getActivity(), "" + result.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            String msg = response.body().getError();
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (response.errorBody() != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = (String) jsonObject.get(Constants.ERROR);
                                    showProgress(false);
                                    if (!TextUtils.isEmpty(msg)) {
                                        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        showProgress(false);
                        jsonResponseCall.cancel();
                    }

                    @Override
                    public void onFailure(Call<JSONResponse<List<Product>>> call, Throwable t) {
                        jsonResponseCall.cancel();
                        Toast.makeText(getActivity(), "" + MainActivity.mainContext.getResources().getString(R.string.text_error_unknown), Toast.LENGTH_SHORT).show();
                        showProgress(false);
                        Crashlytics.logException(t);
                    }
                });
            }
        } catch (Exception e) {
            showProgress(false);
            Crashlytics.logException(e);
        }
    }

    private void defaulSimImage(String url) {
        empty.put(EntityAPI.FIELD_BACKGROUND, url);
    }

    private void showProgress(boolean check) {
        if (check) {
            progressBar.setVisibility(View.VISIBLE);
            layoutContainer.setEnabled(false);
            layoutContainer.setAlpha(0.5f);
            imgBackground1.setEnabled(false);
            imgBackground2.setEnabled(false);
            imgBackground3.setEnabled(false);
            imgBackground4.setEnabled(false);
            imgBackground5.setEnabled(false);
//            edt_sim_title.setEnabled(false);
            edt_description.setEnabled(false);
            btnSharePost.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            layoutContainer.setEnabled(true);
            layoutContainer.setAlpha(1);
            imgBackground1.setEnabled(true);
            imgBackground2.setEnabled(true);
            imgBackground3.setEnabled(true);
            imgBackground4.setEnabled(true);
            imgBackground5.setEnabled(true);
//            edt_sim_title.setEnabled(true);
            edt_description.setEnabled(true);
            btnSharePost.setEnabled(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        filePut.putProjectIdPostEmpty("");
        filePut.putProjectNamePostEmpty("");
    }
}

