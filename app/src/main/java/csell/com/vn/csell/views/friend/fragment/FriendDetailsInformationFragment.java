package csell.com.vn.csell.views.friend.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.Calendar;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.friend.activity.FriendDetailsActivity;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;

public class FriendDetailsInformationFragment extends Fragment {

    public TextView tvDOB, tvEmail, tvAddress, tvPhone;
    public LinearLayout layoutDOB, layoutEmail, layoutAddress, layoutPhone;
    private FileSave fileGet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_friend_detail_infomation, container, false);

        initView(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (FriendDetailsActivity.friend == null) {
            setValue(fileGet.getDateOfBirth(), fileGet.getUserEmail(),
                    fileGet.getUserPhone(), fileGet.getUserAddress());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            setValue(FriendDetailsActivity.friend.getDob(), FriendDetailsActivity.friend.getEmail(),
                    FriendDetailsActivity.friend.getPhone(), FriendDetailsActivity.friend.getAddress());
        } catch (Exception e) {
            Crashlytics.logException(e);
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
        }
    }

    private void setValue(String dob, String email, String phone, String address) {
        try {
            if (!TextUtils.isEmpty(dob)) {
                layoutDOB.setVisibility(View.VISIBLE);
                Calendar reminder = Utilities.convertDateStringToCalendarAllType(dob);
                if (reminder != null) {
                    String date = reminder.get(Calendar.DAY_OF_MONTH) + "-" + (reminder.get(Calendar.MONTH) + 1) + "-" + reminder.get(Calendar.YEAR);
                    tvDOB.setText(date);
                } else {
                    tvDOB.setText("");
                }
            } else {
                layoutDOB.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(email)) {
                layoutEmail.setVisibility(View.VISIBLE);
                tvEmail.setText(email);
            } else {
                layoutEmail.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(phone)) {
                layoutPhone.setVisibility(View.VISIBLE);
                tvPhone.setText(phone);
            } else {
                layoutPhone.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(address)) {
                layoutAddress.setVisibility(View.VISIBLE);
                tvAddress.setText(address);
            } else {
                layoutAddress.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
            Crashlytics.logException(e);
        }

    }

    private void initView(View rootView) {
        tvDOB = rootView.findViewById(R.id.tv_dob_friend_detail);
        tvEmail = rootView.findViewById(R.id.tv_email_friend_detail);
        tvAddress = rootView.findViewById(R.id.tv_address_friend_detail);
        tvPhone = rootView.findViewById(R.id.tv_phone_friend_detail);
        fileGet = new FileSave(getActivity(), Constants.GET);
        layoutAddress = rootView.findViewById(R.id.layout_address);
        layoutPhone = rootView.findViewById(R.id.layout_phone);
        layoutEmail = rootView.findViewById(R.id.layout_email);
        layoutDOB = rootView.findViewById(R.id.layout_dob);
    }

}
