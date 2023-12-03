package com.hnidesu.taskmanager.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.hnidesu.taskmanager.R;
import com.hnidesu.taskmanager.adapter.ViewPagerAdapter;
import com.hnidesu.taskmanager.fragment.AllTaskFragment;
import com.hnidesu.taskmanager.fragment.UnfinishedTaskFragment;

public class MainActivity extends AppCompatActivity {

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPager=findViewById(R.id.viewpager_main);
        viewPager.setAdapter(viewPagerAdapter);
        viewPagerAdapter.addFragment(UnfinishedTaskFragment.getInstance());
        viewPagerAdapter.addFragment(AllTaskFragment.getInstance());
        viewPagerAdapter.notifyDataSetChanged();

    }

}