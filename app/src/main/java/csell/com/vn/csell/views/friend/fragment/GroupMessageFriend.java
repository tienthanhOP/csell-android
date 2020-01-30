package csell.com.vn.csell.views.friend.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import csell.com.vn.csell.R;

public class GroupMessageFriend extends Fragment {

    private Context mContext;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_group_message_friend, container, false);
        mContext = getActivity();

        initView(rootView);
        initEvent();

        return rootView;
    }

    private void initEvent() {
        fab.setOnClickListener((View v) -> {
            Toast.makeText(getActivity(), R.string.comming_soon, Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(mContext, AddOrEditGroupMessage.class);
//            startActivity(intent);
        });
    }

    private void initView(View v) {
        fab = v.findViewById(R.id.fab);
    }


}
