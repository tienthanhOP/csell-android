package csell.com.vn.csell.views.product.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import csell.com.vn.csell.R;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.product.activity.SelectCategoryActivity;
import csell.com.vn.csell.constants.EntityAPI;
import csell.com.vn.csell.constants.Utilities;
import io.fabric.sdk.android.Fabric;

/**
 * Created by cuong.nv on 4/3/2018.
 */

public class InputDescriptionFragment extends Fragment {


    private TextInputEditText edtDescription;
    private TextView txtNext;
    private TextView txtCountCharacter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getActivity(), new Crashlytics());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_input_description, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();
    }

    private void initView(View view) {
        edtDescription = view.findViewById(R.id.edt_description);
        txtNext = view.findViewById(R.id.txtNext);
        txtCountCharacter = view.findViewById(R.id.txtCountCharacter);
        String text = "0/" + MainActivity.mainContext.getString(R.string.text_count_character_description_4000);
        txtCountCharacter.setText(text);
    }

    private void initEvent() {

        edtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    int size = s.toString().length();
                    String text = size + "/" + MainActivity.mainContext.getString(R.string.text_count_character_description_4000);
                    txtCountCharacter.setText(text);
                } else {
                    String text = "0/" + MainActivity.mainContext.getString(R.string.text_count_character_description_4000);
                    txtCountCharacter.setText(text);
                }

            }
        });

        txtNext.setOnClickListener(v -> {

            String textDesTemp = edtDescription.getText().toString().trim();
            int countWord = Utilities.countWords(textDesTemp);
            if (countWord < 10) {
                edtDescription.setError(MainActivity.mainContext.getResources().
                        getString(R.string.Description_at_least_10_words));
                edtDescription.requestFocus();
                return;
            }

            edtDescription.setCursorVisible(false);
            SelectCategoryActivity.paramsProduct.put(EntityAPI.FIELD_DESCRIPTION, edtDescription.getText().toString());

            Utilities.hideSoftKeyboard(getActivity());
            ChooseImageUploadFragment fragment = new ChooseImageUploadFragment();
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            manager.popBackStack();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            transaction.add(R.id.container_frame_created, fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commit();
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((SelectCategoryActivity) getActivity()).updateTitleToolbar("Nhập mô tả");
        SelectCategoryActivity.currentFragment = "InputDescriptionFragment";
    }
}
