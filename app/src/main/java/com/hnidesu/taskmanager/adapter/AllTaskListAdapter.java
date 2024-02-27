package com.hnidesu.taskmanager.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hnidesu.taskmanager.R;
import com.hnidesu.taskmanager.activity.EditTaskActivity;
import com.hnidesu.taskmanager.component.Item;
import com.hnidesu.taskmanager.ui.CheckBoxEx;
import com.hnidesu.taskmanager.utility.DBUtil;
import com.hnidesu.taskmanager.viewholder.SingleTaskViewHolder;

import java.text.SimpleDateFormat;
import java.util.List;

public class AllTaskListAdapter extends RecyclerView.Adapter<SingleTaskViewHolder> {
    private Context mContext;
    private SingleTaskViewHolder mSelectedViewHolder;

    private List<Item> itemList;

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }
    public Item getSelectedItem(){
        return mSelectedViewHolder.item;
    }
    public AllTaskListAdapter(@NonNull Context ctx){
        mContext=ctx;
    }
    @NonNull
    @Override
    public SingleTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(mContext).inflate(R.layout.single_task, parent,false);
        return new SingleTaskViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleTaskViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.item=item;
        holder.finishCheckBox.setOnCheckChangeListener(new CheckBoxEx.OnCheckChangeListener() {
            @Override
            public void onChecked() {
                item.isFinished=true;
                DBUtil.getInstance().updateTask(item);
            }

            @Override
            public void onNotChecked() {
                item.isFinished=false;
                DBUtil.getInstance().updateTask(item);
            }
        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, EditTaskActivity.class);
            intent.putExtra("task", item.toBundle());
            mContext.startActivity(intent);
        });
        holder.titleView.setText(item.title);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        holder.deadlineView.setText(mContext.getString(R.string.deadline) +":" + format.format(item.deadLine));
        holder.finishCheckBox.setChecked(item.isFinished);
        holder.itemView.setOnLongClickListener(v -> {
            mSelectedViewHolder =holder;
            return false;
        });
    }


    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }
}
