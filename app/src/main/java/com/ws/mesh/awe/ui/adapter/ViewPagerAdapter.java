package com.ws.mesh.awe.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ws.mesh.awe.base.BaseFragment;

import java.util.List;

/**
 * Created by zhaol on 2018/1/26.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> mListFragments;
    private List<String> mTitles;

    public ViewPagerAdapter(FragmentManager fm, List<BaseFragment> listFragment, List<String> titles) {
        super(fm);
        this.mListFragments = listFragment;
        this.mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mListFragments.get(position);
    }

    @Override
    public int getCount() {
        return mListFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position % mTitles.size());
    }
}
