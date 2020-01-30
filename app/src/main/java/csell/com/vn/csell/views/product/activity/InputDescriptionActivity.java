package csell.com.vn.csell.views.product.activity;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;

public class InputDescriptionActivity extends AppCompatActivity {

    private FancyButton btnBack, btnSave;
    private TextView tvTitle;
    private TextInputEditText edtDescription;
    private TextView txtCountCharacter;

    String description = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_description);
        Fabric.with(this, new Crashlytics());

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initView();
        addEvent();
    }


    private void initView() {
        btnBack = findViewById(R.id.btn_back_navigation);
        btnBack.setText(getString(R.string.title_back_vn));
        btnSave = findViewById(R.id.btn_save_navigation);
        tvTitle = findViewById(R.id.custom_TitleToolbar);
        edtDescription = findViewById(R.id.edt_description);
        txtCountCharacter = findViewById(R.id.txtCountCharacter);

        tvTitle.setText(getString(R.string.enter_description));
        btnSave.setText(getString(R.string.done));

        Intent intent = getIntent();
        if (intent != null) {
            description = getIntent().getStringExtra(Constants.KEY_DESCRIPTION_VALUE);
            edtDescription.setText(description + "");
            edtDescription.setSelection(description.length());
            String text = description.length() + "/" + MainActivity.mainContext.getString(R.string.text_count_character_description_4000);
            txtCountCharacter.setText(text);
        } else {
            edtDescription.setText("");
            String text = "0/" + getString(R.string.text_count_character_description_4000);
            txtCountCharacter.setText(text);
        }
    }

    private void addEvent() {
        btnBack.setOnClickListener(view -> finishAfterTransition());

        btnSave.setOnClickListener(view -> {
            if (Utilities.countWords(edtDescription.getText().toString().trim()) > 9) {
                Utilities.hideSoftKeyboard(this);
                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_DESCRIPTION_VALUE, edtDescription.getText().toString());
                InputDescriptionActivity.this.setResult(Constants.KEY_DESCRIPTION, intent);
                finishAfterTransition();
            } else {
                edtDescription.setError(MainActivity.mainContext.getResources().getString(R.string.Description_at_least_10_words));
                edtDescription.requestFocus();
            }
        });

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
                    String text = size + "/" + getString(R.string.text_count_character_description_4000);
                    txtCountCharacter.setText(text);
                } else {
                    String text = "0/" + getString(R.string.text_count_character_description_4000);
                    txtCountCharacter.setText(text);
                }

            }
        });

    }

}
