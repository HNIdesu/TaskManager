package com.hnidesu.taskmanager.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hnidesu.taskmanager.R;
import com.hnidesu.taskmanager.adapter.FragmentPagerAdapter1;
import com.hnidesu.taskmanager.component.Observer;
import com.hnidesu.taskmanager.fragment.AllTaskFragment;
import com.hnidesu.taskmanager.fragment.SettingFragment;
import com.hnidesu.taskmanager.fragment.UnfinishedTaskFragment;
import com.hnidesu.taskmanager.utility.DBUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ViewHolder mViewHolder;
    private final AllTaskFragment mAllTaskFragment=new AllTaskFragment();
    private final UnfinishedTaskFragment mUnfinishedTaskFragment=new UnfinishedTaskFragment();
    private final SettingFragment mSettingFragment=new SettingFragment();

    public class ViewHolder{
        private FragmentPagerAdapter1 viewPagerAdapter;
        private ViewPager2 viewPager;
        private TabLayout tabLayout;
        public void bindViews(){
            viewPager=findViewById(R.id.viewpager_main);
            tabLayout=findViewById(R.id.tablayout);
            viewPagerAdapter=new FragmentPagerAdapter1(getSupportFragmentManager(),getLifecycle());
            viewPagerAdapter.addFragment(mUnfinishedTaskFragment);
            viewPagerAdapter.addFragment(mAllTaskFragment);
            viewPagerAdapter.addFragment(mSettingFragment);
            viewPager.setAdapter(viewPagerAdapter);
            final String[] headers=new String[]{
                    getString(R.string.unfinished_tasks),
                    getString(R.string.all_tasks),
                    getString(R.string.setting)};
            new TabLayoutMediator(tabLayout,viewPager,(tab,position)->{
                tab.setText(headers[position]);
            }).attach();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewHolder=new ViewHolder();
        mViewHolder.bindViews();
        ArrayList<Observer> list=new ArrayList<>();
        list.add(mAllTaskFragment);
        list.add(mUnfinishedTaskFragment);
        DBUtil.getInstance().setObserverList(list);
    }

}