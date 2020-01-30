package csell.com.vn.csell.mycustoms;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;

import csell.com.vn.csell.R;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.interfaces.RetryConnect;

public class DisconnectDialog extends Dialog {

    private Button btnRetry;
    private Context mContext;
    private RetryConnect mListener;

    public DisconnectDialog(@NonNull Context context, RetryConnect retryConnect) {
        super(context, android.R.style.Theme_NoTitleBar);
        mContext = context;
        this.mListener = retryConnect;

        WindowManager.LayoutParams lp = Objects.requireNonNull(getWindow()).getAttributes();

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // Width
        lp.height = WindowManager.LayoutParams.MATCH_PARENT; // Height
        getWindow().setAttributes(lp);

        setContentView(R.layout.disconnect_dialog);

        btnRetry = findViewById(R.id.retry_connect);

        btnRetry.setOnClickListener(v -> {
            if (Utilities.isNetworkConnected(mContext)) {
                if (Utilities.getInetAddressByName() != null) {
                    dismiss();
                    mListener.onRetryConnect();
                } else {
                    Toast.makeText(context, mContext.getResources().getString(R.string.Please_check_your_network_connection), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, mContext.getResources().getString(R.string.Please_check_your_network_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        dismiss();
        mListener.onBackRetryConnect();
    }
}
