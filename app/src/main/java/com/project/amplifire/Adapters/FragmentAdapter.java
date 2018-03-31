package com.project.amplifire.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by aayush on 14-Mar-18.
 */

public class FragmentAdapter extends FragmentStatePagerAdapter{

    private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
    private ArrayList<String> mTitleFragmentList = new ArrayList<String>();
    private String tabTitles[] = new String[] {"All Songs", "Playlists", "Artists", "Albums"};

    public FragmentAdapter(FragmentManager fm)
    {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title)
    {
        mFragments.add(fragment);
        mTitleFragmentList.add(title);
    }

    @Override
    public Fragment getItem(int position)
    {
        return mFragments.get(position);
    }

    @Override
    public int getCount()
    {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
