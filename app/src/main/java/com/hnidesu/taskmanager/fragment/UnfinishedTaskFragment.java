package com.hnidesu.taskmanager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.hnidesu.taskmanager.R;
import com.hnidesu.taskmanager.adapter.UnfinishedListAdapter;
import com.hnidesu.taskmanager.component.Observable;
import com.hnidesu.taskmanager.component.Observer;
import com.hnidesu.taskmanager.menu.SortTypePopUpMenu;
import com.hnidesu.taskmanager.utility.DBUtil;


public class UnfinishedTaskFragment extends Fragment implements Observer {
    private UnfinishedListAdapter mRecyclerViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerViewAdapter.setItemList(DBUtil.getInstance().getUnfinishedTasks(false));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRecyclerViewAdapter =new UnfinishedListAdapter(getContext());
        View root= inflater.inflate(R.layout.fragment_unfinished_task, container, false);
        root.findViewById(R.id.button_sort).setOnClickListener(view -> new SortTypePopUpMenu(UnfinishedTaskFragment.this.getContext(), view).show());
        RecyclerView recyclerView= root.findViewById(R.id.recyclerview);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        return root;
    }

    @Override
    public void update(Observable target) {
        if(mRecyclerViewAdapter!=null)
            mRecyclerViewAdapter.setItemList(DBUtil.getInstance().getUnfinishedTasks(false));
    }
}