package csell.com.vn.csell.views.friend.fragment;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import csell.com.vn.csell.R;
import csell.com.vn.csell.views.csell.adapter.ViewPagerAdapter;
import csell.com.vn.csell.controllers.FriendsController;
import csell.com.vn.csell.models.UserRetro;
import csell.com.vn.csell.sqlites.SQLFriends;
import io.fabric.sdk.android.Fabric;

public class HomeFriendFragment extends Fragment implements FriendsController.OnGetFriendsListener {

    private TabLayout tabLayout;
    @SuppressLint("StaticFieldLeak")
    public static ViewPager viewPager;
    private ViewPagerAdapter adapter;

    private HistoryMessages historyMessages;
    private GroupMessageFriend groupMessageFriend;
    private SQLFriends sqlFriends;
    public static int currentTab = 0;
    private ProgressBar progressBar;
    private LinearLayout layoutContent;
    private boolean firstLoad;

    public HomeFriendFragment getInstance() {
        return new HomeFriendFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getContext(), new Crashlytics());
        sqlFriends = new SQLFriends(getActivity());
        if (!sqlFriends.checkExistData()) {
            FriendsController friendsController = new FriendsController(getActivity());
            friendsController.getFriends(0, 5000, this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View rootView) {
        progressBar = rootView.findViewById(R.id.progress_bar);
        layoutContent = rootView.findViewById(R.id.layout_content);
        firstLoad = true;
        hideProgressBar(false);

        adapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager = rootView.findViewById(R.id.view_pager_home_product);
        tabLayout = rootView.findViewById(R.id.tab_layout_home);
        setupViewPager();
    }

    public void hideProgressBar(boolean loading) {
        if (loading) {
            progressBar.setVisibility(View.GONE);
            layoutContent.setVisibility(View.VISIBLE);
            if (firstLoad) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                layoutContent.startAnimation(animation);
                firstLoad = false;
            }
        } else {
            progressBar.setVisibility(View.VISIBLE);
            layoutContent.setVisibility(View.GONE);
        }
    }

    private void setupViewPager() {
        try {
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(0);
            tabLayout.setupWithViewPager(viewPager);
            historyMessages = new HistoryMessages();
            groupMessageFriend = new GroupMessageFriend();
            FriendFragment friendFragment = new FriendFragment();
            adapter.addFrag(historyMessages, getResources().getString(R.string.title_tab_message));
            adapter.addFrag(groupMessageFriend, getString(R.string.group));
            adapter.addFrag(friendFragment, getResources().getString(R.string.friend));
            if (adapter.getCount() <= 4) {
                tabLayout.setTabMode(TabLayout.MODE_FIXED);
            } else {
                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            }
            adapter.notifyDataSetChanged();
            hideProgressBar(true);
        } catch (Exception ignored) {

        }

    }


    private void initEvent() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {

            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {

                TextView tabTextView = new TextView(getActivity());
                tab.setCustomView(tabTextView);

                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

                tabTextView.setText(tab.getText());

                // First tab is the selected tab, so if i==0 then set BOLD typeface
                if (i == 0) {
                    tabTextView.setTypeface(null, Typeface.BOLD);
                    tabTextView.setTextSize(24);
                    tabTextView.setTextColor(getResources().getColor(R.color.tab_selected_friend));
                } else {
                    tabTextView.setTypeface(null, Typeface.NORMAL);
                    tabTextView.setTextSize(16);
                    tabTextView.setTextColor(getResources().getColor(R.color.tab_unselected_friend));
                }

            }

        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                viewPager.setCurrentItem(tab.getPosition());

                TextView text = (TextView) tab.getCustomView();

                text.setTypeface(null, Typeface.BOLD);
                text.setTextSize(24);
                text.setTextColor(getResources().getColor(R.color.tab_selected_friend));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView text = (TextView) tab.getCustomView();

                text.setTypeface(null, Typeface.NORMAL);
                text.setTextSize(16);
                text.setTextColor(getResources().getColor(R.color.tab_unselected_friend));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onResume() {
        switch (currentTab) {
            case 0:
                viewPager.setCurrentItem(0);
                break;
            case 1:
                viewPager.setCurrentItem(1);
                break;
            case 2:
                viewPager.setCurrentItem(2);
                break;
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onGetFriendsSuccess(ArrayList<UserRetro> data) {
        try {
            if (data != null) {
                FriendFragment.friendArrayList.clear();
                FriendFragment.friendArrayList.addAll(sqlFriends.getAllFriend1());
                FriendFragment.friendsAdaper.notifyDataSetChanged();
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onGetFriendsFailure() {

    }

    @Override
    public void onConnectGetFriendsFailure() {

    }
}
