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
import androidx.appcompat.app.AlertDialog;
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
import com.hnidesu.taskmanager.utility.ToastUtil;

import java.util.Date;

public class AllTaskFragment extends Fragment implements Observer {

    private AllTaskListAdapter mRecyclerViewAdapter;
    private View itemView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        itemView =inflater.inflate(R.layout.fragment_all_task, container, false);
        mRecyclerViewAdapter=new AllTaskListAdapter(getContext());
        RecyclerView rv= itemView.findViewById(R.id.recyclerview);
        rv.setAdapter(mRecyclerViewAdapter);
        itemView.findViewById(R.id.button_add_task).setOnClickListener(v -> {
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
                    ToastUtil.ToastShort(getString(R.string.add_success));
                    Intent intent=new Intent(AllTaskFragment.this.getContext(), EditTaskActivity.class);
                    intent.putExtra("task",item.toBundle());
                    startActivity(intent);
                }

                @Override
                public void onCancel(SetTaskDialog dialog) {
                    ToastUtil.ToastShort(R.string.cancelled);
                }

            });
            dialog.setDate(new Date(System.currentTimeMillis()));
            dialog.popup(itemView);

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
            {
                new AlertDialog.Builder(getContext())
                    .setMessage(R.string.if_sure_to_delete)
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> ToastUtil.ToastShort(R.string.cancelled))
                    .setPositiveButton(R.string.ok, (dialogInterface, i) -> DBUtil.getInstance().deleteTask(mRecyclerViewAdapter.getSelectedItem()))
                    .setOnCancelListener(dialogInterface -> ToastUtil.ToastShort(R.string.cancelled)).create().show();
                return true;
            }
            case R.id.option_edit:
            {
                SetTaskDialog dialog=new SetTaskDialog(AllTaskFragment.this.getContext(), R.layout.window_set_task, new SetTaskDialog.OnFinishListener() {
                    @Override
                    public void onSet(SetTaskDialog dialog) {
                        Item selectedItem= mRecyclerViewAdapter.getSelectedItem();
                        selectedItem.deadLine=dialog.getDate();
                        selectedItem.title=dialog.getTitle();
                        DBUtil.getInstance().updateTask(selectedItem);
                    }

                    @Override
                    public void onCancel(SetTaskDialog dialog) {
                        ToastUtil.ToastShort(R.string.cancelled);
                    }
                });
                Date date= mRecyclerViewAdapter.getSelectedItem().deadLine;
                dialog.setDate(date);
                dialog.setTitle(mRecyclerViewAdapter.getSelectedItem().title);
                dialog.popup(itemView);
                return true;
            }
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void update(Observable target) {
        if(mRecyclerViewAdapter!=null)
            mRecyclerViewAdapter.setItemList(DBUtil.getInstance().getAllTasks());
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerViewAdapter.setItemList(DBUtil.getInstance().getAllTasks());
    }
}