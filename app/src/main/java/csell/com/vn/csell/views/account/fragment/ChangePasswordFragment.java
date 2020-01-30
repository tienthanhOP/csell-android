package csell.com.vn.csell.views.account.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
//import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import csell.com.vn.csell.R;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.csell.activity.SettingActivity;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.apis.PostAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends Fragment {

    private EditText txtCurrentPassword, txtNewPassword, txtConfirmNewPassword;
    private FileSave fileGet;
    private Button btnConfirm;
    private ProgressBar progressBar;
    private RelativeLayout layout;
    private int timeout = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_change_password, container, false);
        initView(rootView);
        addEvent();
        return rootView;
    }

    private void initView(View rootView) {
        SettingActivity.tvTitle.setText(MainActivity.mainContext.getResources().getString(R.string.title_change_password));
        txtCurrentPassword = rootView.findViewById(R.id.txt_current_password);
        txtNewPassword = rootView.findViewById(R.id.txt_new_password);
        txtConfirmNewPassword = rootView.findViewById(R.id.txt_confirm_new_password);
        fileGet = new FileSave(getActivity(), Constants.GET);
        btnConfirm = rootView.findViewById(R.id.btn_confirm);
        progressBar = rootView.findViewById(R.id.progress_bar);
        layout = rootView.findViewById(R.id.layout);
    }

    private void addEvent() {

//        txtCurrentPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
//                    checkPassword();
//                    return true;
//                } else if (actionId == EditorInfo.IME_ACTION_SEARCH
//                        || event == null
//                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                    checkPassword();
//                    return true;
//                }
//
//                return false;
//            }
//        });

//        txtCurrentPassword.setOnFocusChangeListener((v, hasFocus) -> {
//            if (!hasFocus) {
//                checkPassword();
//            }
//        });

        btnConfirm.setOnClickListener(v -> {

            String newPassword = txtNewPassword.getText().toString().trim();
            String confirmNewPassword = txtConfirmNewPassword.getText().toString().trim();
            String currentPassword = txtCurrentPassword.getText().toString().trim();

            if (TextUtils.isEmpty(currentPassword)) {
                txtCurrentPassword.requestFocus();
                txtCurrentPassword.setError(Objects.requireNonNull(getActivity()).getString(R.string.please_do_not_leave_it_blank));
                return;
            }

            if (TextUtils.isEmpty(newPassword)) {
                txtNewPassword.requestFocus();
                txtNewPassword.setError(Objects.requireNonNull(getActivity()).getString(R.string.please_do_not_leave_it_blank));
                return;
            }

            if (newPassword.length() < 6) {
                txtNewPassword.requestFocus();
                txtNewPassword.setError(Objects.requireNonNull(getActivity()).getString(R.string.password_contains_at_least_6_characters));
                return;
            }

            if (TextUtils.isEmpty(confirmNewPassword)) {
                txtConfirmNewPassword.requestFocus();
                txtConfirmNewPassword.setError(Objects.requireNonNull(getActivity()).getString(R.string.please_do_not_leave_it_blank));
                return;
            }

//            if (confirmNewPassword.length() < 6){
//                txtNewPassword.setError(getActivity().getString(R.string.error_invalid_password));
//                return;
//            }

            if (!newPassword.equals(confirmNewPassword)) {
                txtConfirmNewPassword.requestFocus();
                txtConfirmNewPassword.setError(MainActivity.mainContext.getResources().getString(R.string.error_password));
                return;
            }

            showProgress(true);
            updatePass(newPassword, currentPassword);
        });
    }

    private void updatePass(String newPassword, String currentPassword) {
        PostAPI api = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
        HashMap<String, Object> map = new HashMap<>();

        String encryptNew = Utilities.encryptString(newPassword);
        String encryptOld = Utilities.encryptString(currentPassword);

        map.put(EntityAPI.FIELD_NEW_PASSWORD, encryptNew);
        map.put(EntityAPI.FIELD_OLD_PASSWORD, encryptOld);
        Call<JSONResponse<Object>> updatePassword = api.updatePassword(map);
        updatePassword.enqueue(new Callback<JSONResponse<Object>>() {
            @Override
            public void onResponse(Call<JSONResponse<Object>> call, Response<JSONResponse<Object>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getSuccess() != null) {
                        if (response.body().getSuccess()) {
                            showProgress(false);
                            Toast.makeText(getActivity(), getString(R.string.text_success_update_password), Toast.LENGTH_LONG).show();
                            Objects.requireNonNull(getActivity()).onBackPressed();
                        } else {
                            showProgress(false);
                            String msg = response.body().getMessage();
                            if (msg.equals(getResources().getString(R.string.text_error_session_expired))) {
                                //refresh token
                            } else {
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                            }

                        }
                    }

                    String msg;
                    if (response.body() != null) {
                        msg = response.body().getError();
                        if (!TextUtils.isEmpty(msg)) {
                            if (msg.equals("Mật khẩu cũ không đúng. Xin vui lòng thử lại.!")) {
                                txtCurrentPassword.requestFocus();
                                txtCurrentPassword.setError(getActivity().getString(R.string.incorrect_password));
                            } else Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

                        }
                    }
                } else {
                    try {
                        showProgress(false);
                        JSONObject jsonObject;
                        if (response.errorBody() != null) {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String msg = (String) jsonObject.get(Constants.ERROR);

                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
                showProgress(false);
                updatePassword.cancel();
            }

            @Override
            public void onFailure(Call<JSONResponse<Object>> call, Throwable t) {
                updatePassword.cancel();
                if (timeout < 1) {
                    timeout++;
                    updatePass(newPassword, currentPassword);
                } else {
                    timeout = 0;
                    showProgress(false);
                    Toast.makeText(getActivity(), getString(R.string.text_error_unknown), Toast.LENGTH_LONG).show();
                }
                Crashlytics.logException(t);
            }
        });
    }


//    private void checkPassword() {
//        String password = txtCurrentPassword.getText().toString().trim();
//        if (TextUtils.isEmpty(password)) {
//            txtCurrentPassword.setError(MainActivity.mainContext.getResources().getString(R.string.error_incorrect_password));
//            wrongPassword = true;
//            return;
//        }
//        showProgress(true);
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        AuthCredential credential = EmailAuthProvider
//                .getCredential(fileGet.getUserEmail(), password);
//
//        user.reauthenticate(credential)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isComplete() && task.isSuccessful()) {
//                            wrongPassword = false;
//                            txtCurrentPassword.setCompoundDrawablesWithIntrinsicBounds(null, null,
//                                    getResources()
//                                            .getDrawable(R.drawable.ic_check_circle_green), null);
//                        } else {
//                            txtCurrentPassword.setError(MainActivity.mainContext.getResources().getString(R.string.error_incorrect_password));
//                            wrongPassword = true;
//                        }
//                        showProgress(false);
//                    }
//                });
//
//    }

    private void showProgress(boolean check) {
        if (check) {
            progressBar.setVisibility(View.VISIBLE);
            layout.setAlpha(0.5f);
            layout.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            layout.setAlpha(1);
            layout.setEnabled(true);
        }
    }

}
