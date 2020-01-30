package csell.com.vn.csell.views.friend.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.friend.activity.ListFriendRequestActivity;
import csell.com.vn.csell.views.friend.activity.FindFriendsActivity;
import csell.com.vn.csell.views.friend.adapter.SuggestFriendAdapter;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.models.FriendResponse;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.sqlites.SQLCallLog;
import io.fabric.sdk.android.Fabric;

/**
 * Created by cuongnv on 3/2/2018.
 */

public class FriendManagementFragment extends Fragment {

    ConstraintLayout ct_next_list_friend_request;
    ConstraintLayout ct_next_find_friend;
    @SuppressLint("StaticFieldLeak")
    public static TextView txtCountRequest;
    public static ArrayList<FriendResponse> listFriendRequest;
    private FileSave fileGet;
    private RecyclerView rvListSuggest;
    private ArrayList<String> callLogList;
    private SQLCallLog db;
    private ProgressBar progressLoadmoreFriend;
    public static FragmentManager fragmentManager;
    private BaseActivityTransition baseActivityTransition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getContext(), new Crashlytics());
        fileGet = new FileSave(getActivity(), Constants.GET);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friendmanagement, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentManager = getFragmentManager();
    }

    public static boolean back() {
        try {
            if (fragmentManager.getBackStackEntryCount() == 0) {
                return false;
            }
            fragmentManager.popBackStack();
            return true;
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.d("FriendManager.Class", e.getMessage());
            Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
            return false;
        }
    }

    private void initEvent() {
        ct_next_list_friend_request.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListFriendRequestActivity.class);
            baseActivityTransition.transitionTo(intent, 0);
        });

        ct_next_find_friend.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FindFriendsActivity.class);
            baseActivityTransition.transitionTo(intent, 0);
        });
    }

    private void initView(View rootView) {
        baseActivityTransition = new BaseActivityTransition(getActivity());
        db = new SQLCallLog(getActivity());
        ct_next_list_friend_request = rootView.findViewById(R.id.ct_next_list_friend_request);
        ct_next_find_friend = rootView.findViewById(R.id.ct_next_find_friend);
        txtCountRequest = rootView.findViewById(R.id.txt_countRequestFriend);
        progressLoadmoreFriend = rootView.findViewById(R.id.progress_loadmore_suggest);
        rvListSuggest = rootView.findViewById(R.id.rv_suggestFriend);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvListSuggest.setLayoutManager(mLayoutManager);
        ArrayList<UserRetro> friendSuggestArrayList = new ArrayList<>();
        SuggestFriendAdapter adapter = new SuggestFriendAdapter(getActivity(), friendSuggestArrayList);
        rvListSuggest.setAdapter(adapter);

        callLogList = new ArrayList<>();
        //    private TextView txtLoadMore;
        //    private ProgressBar progressBarLoadMore;
        int offSet = 0;
        requestServerCallLog(offSet);

        if (friendSuggestArrayList.size() != 0) {
            progressLoadmoreFriend.setVisibility(View.VISIBLE);
        }
        listFriendRequest = new ArrayList<>();
        GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
    }


    private void requestServerCallLog(int offSet) {
        callLogList.clear();
        callLogList = db.getItems(offSet);

//        findFriendfromCallLog(callLogList);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        if (Utilities.lsFriendRequest.size() != 0) {
            if (Utilities.lsFriendRequest.size() > 9) {
                txtCountRequest.setText("9+");
            } else {
                txtCountRequest.setText(Utilities.lsFriendRequest.size() + "");
            }
            txtCountRequest.setVisibility(View.VISIBLE);
        } else {
            txtCountRequest.setText("");
            txtCountRequest.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
