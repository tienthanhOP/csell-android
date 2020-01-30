package csell.com.vn.csell.views.csell.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chuc.nq on 2/7/2018.
 */

public class ViewPagerContactAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private ArrayList<String> mTitlPagers = new ArrayList<>();

    public ViewPagerContactAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {

//        switch (position){
//            case 0:
//                ProductUserFragment fragment1 = new ProductUserFragment();
//                return fragment1;
//            case 1:
//                GroupFragment groupFragment = new GroupFragment();
//                return groupFragment;
//            case 2:
//                InfoFragment infoFragment = new InfoFragment();
//                return infoFragment;
//            default:
//                return null;
//        }

        return mFragmentList.get(position);
    }

    public void addFrag(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mTitlPagers.add(title);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mTitlPagers.get(0);
            case 1:
                return mTitlPagers.get(1);
            case 2:
                return mTitlPagers.get(2);
            default:
                return null;
        }
    }
}
