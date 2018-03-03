package com.elkana.dslibrary.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Eric on 06-Nov-17.
 */

public class AdapterFragments extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public AdapterFragments(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    public List<Fragment> getList() {
        return fragments;
    }

}
