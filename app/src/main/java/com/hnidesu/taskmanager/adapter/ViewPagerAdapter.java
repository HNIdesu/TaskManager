package com.hnidesu.taskmanager.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.LinkedList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private LinkedList<Fragment> mFragmentList =new LinkedList<>();
    private LinkedList<String> mFragmentTitles =new LinkedList<>();
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }
    public void addFragment(Fragment fragment,String title){
        mFragmentTitles.addLast(title);
        mFragmentList.addLast(fragment);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
