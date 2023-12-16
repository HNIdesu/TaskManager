package com.hnidesu.taskmanager.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hnidesu.taskmanager.R;
import com.hnidesu.taskmanager.adapter.ViewPagerAdapter;
import com.hnidesu.taskmanager.fragment.AllTaskFragment;
import com.hnidesu.taskmanager.fragment.SettingFragment;
import com.hnidesu.taskmanager.fragment.UnfinishedTaskFragment;

public class MainActivity extends AppCompatActivity {

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager=findViewById(R.id.viewpager_main);
        tabLayout=findViewById(R.id.tablayout);

        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPagerAdapter.addFragment(UnfinishedTaskFragment.getInstance(),getString(R.string.unfinished_tasks));
        viewPagerAdapter.addFragment(AllTaskFragment.getInstance(),getString(R.string.all_tasks));
        viewPagerAdapter.addFragment(new SettingFragment(),getString(R.string.setting));
        viewPagerAdapter.notifyDataSetChanged();
        tabLayout.setupWithViewPager(viewPager);
    }

}