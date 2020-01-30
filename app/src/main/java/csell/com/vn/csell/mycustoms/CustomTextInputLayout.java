package csell.com.vn.csell.mycustoms;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by chuc.nq on 4/24/2018.
 */

public class CustomTextInputLayout extends TextInputLayout {

    public CustomTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setErrorEnabled(boolean enabled) {
        super.setErrorEnabled(enabled);

        if (!enabled) {
            return;
        }

        try {

            Field errorViewField = TextInputLayout.class.getDeclaredField("mErrorView");
            errorViewField.setAccessible(true);
            TextView errorView = (TextView) errorViewField.get(this);
            if (errorView != null) {
                errorView.setGravity(Gravity.END);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.END;
                errorView.setLayoutParams(params);
            }
        } catch (Exception e) {
            // At least log what went wrong
            e.printStackTrace();
        }
    }
}
