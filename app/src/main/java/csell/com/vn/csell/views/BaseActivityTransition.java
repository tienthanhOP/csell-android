package csell.com.vn.csell.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.Toast;

import csell.com.vn.csell.R;

@SuppressLint("Registered")
public class BaseActivityTransition extends AppCompatActivity {
    Context mContext;

    public BaseActivityTransition(Context mContext) {
        this.mContext = mContext;
    }

    @SuppressWarnings("unchecked")
    public void transitionTo(Intent i, int result_code) {

        try {
            final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants((Activity) mContext, false);
            ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, pairs);
            Activity activity = (Activity) mContext;
            if (result_code == 0) {

                try {
                    activity.startActivity(i, transitionActivityOptions.toBundle());
                } catch (Exception e) {
                    startActivity(i);
                }
            } else {
                activity.startActivityForResult(i, result_code, transitionActivityOptions.toBundle());
            }
        } catch (Exception e) {

        }
    }
}


