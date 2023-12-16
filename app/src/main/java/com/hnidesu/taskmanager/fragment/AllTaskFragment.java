package com.hnidesu.taskmanager.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.hnidesu.taskmanager.R;
import com.hnidesu.taskmanager.activity.EditTaskActivity;
import com.hnidesu.taskmanager.adapter.AllTaskListAdapter;
import com.hnidesu.taskmanager.component.Item;
import com.hnidesu.taskmanager.component.Observable;
import com.hnidesu.taskmanager.component.Observer;
import com.hnidesu.taskmanager.dialog.SetTaskDialog;
import com.hnidesu.taskmanager.utility.DBUtil;
import com.hnidesu.taskmanager.utility.LogUtil;
import com.hnidesu.taskmanager.utility.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;



public class AllTaskFragment extends Fragment implements Observer {

    private AllTaskListAdapter recyclerViewAdapter;
    private View itemView;


    private AllTaskFragment() {

    }

    private static AllTaskFragment instance;

    public static AllTaskFragment getInstance() {
        if(instance==null)
            instance=new AllTaskFragment();
        return instance;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        itemView =inflater.inflate(R.layout.fragment_all_task, container, false);
        recyclerViewAdapter=new AllTaskListAdapter();
        RecyclerView rv= itemView.findViewById(R.id.recyclerview);
        rv.setAdapter(recyclerViewAdapter);
        itemView.findViewById(R.id.button_add_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetTaskDialog dialog=new SetTaskDialog(getContext(), R.layout.window_set_task, new SetTaskDialog.OnFinishListener() {
                    @Override
                    public void onSet(SetTaskDialog dialog) {
                        Item item=new Item();
                        item.deadLine=dialog.getDate();
                        item.title=dialog.getTitle();
                        Date curDate=new Date(System.currentTimeMillis());
                        item.createTime=curDate;
                        item.lastModifiedTime=curDate;
                        item.content="";
                        item.isFinished=false;
                        DBUtil.getInstance().createTask(item);
                        ToastUtil.ToastShort("添加成功");

                        try {
                            Intent intent=new Intent(AllTaskFragment.this.getContext(), EditTaskActivity.class);
                            intent.putExtra("task",item.toJson());
                            startActivity(intent);
                        } catch (Exception e) {
                            LogUtil.Error("打开编辑器失败",e);
                        }


                    }

                    @Override
                    public void onCancel(SetTaskDialog dialog) {
                        ToastUtil.ToastShort("已取消");
                    }

                });
                dialog.setDate(new Date(System.currentTimeMillis()));
                dialog.popuop(itemView);

            }
        });
        registerForContextMenu(rv);
        return itemView;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater=new MenuInflater(getContext());
        inflater.inflate(R.menu.menu_context_all_tesk_single,menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.option_delete:
                DBUtil.getInstance().deleteTask(recyclerViewAdapter.getSelectedItem());
                return true;
            case R.id.option_edit:
                SetTaskDialog dialog=new SetTaskDialog(AllTaskFragment.this.getContext(), R.layout.window_set_task, new SetTaskDialog.OnFinishListener() {
                    @Override
                    public void onSet(SetTaskDialog dialog) {
                        Item selectedItem=recyclerViewAdapter.getSelectedItem();
                        selectedItem.deadLine=dialog.getDate();
                        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                        selectedItem.title=dialog.getTitle();
                        DBUtil.getInstance().updateTask(selectedItem);
                    }

                    @Override
                    public void onCancel(SetTaskDialog dialog) {
                        ToastUtil.ToastShort("已取消");
                    }
                });
                Date date= recyclerViewAdapter.getSelectedItem().deadLine;
                dialog.setDate(date);
                dialog.setTitle(recyclerViewAdapter.getSelectedItem().title);
                dialog.popuop(itemView);
                return true;

        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void update(Observable target) {
        recyclerViewAdapter.setItemList(DBUtil.getInstance().getAllTasks());
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerViewAdapter.setItemList(DBUtil.getInstance().getAllTasks());
    }
}