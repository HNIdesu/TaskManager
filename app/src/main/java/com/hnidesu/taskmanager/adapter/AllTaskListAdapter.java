package com.hnidesu.taskmanager.adapter;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hnidesu.taskmanager.R;
import com.hnidesu.taskmanager.activity.EditTaskActivity;
import com.hnidesu.taskmanager.component.Item;
import com.hnidesu.taskmanager.fragment.AllTaskFragment;
import com.hnidesu.taskmanager.ui.CheckBoxEx;
import com.hnidesu.taskmanager.utility.DBUtil;
import com.hnidesu.taskmanager.utility.LogUtil;
import com.hnidesu.taskmanager.viewholder.SingleTaskViewHolder;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.List;

public class AllTaskListAdapter extends RecyclerView.Adapter<SingleTaskViewHolder> {

    private SingleTaskViewHolder selectedVH;
    public Item getSelectedItem(){
        return selectedVH.item;
    }

    private List<Item> itemList;
    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }


    public AllTaskListAdapter() {


    }

    @NonNull
    @Override
    public SingleTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(AllTaskFragment.getInstance().getContext()).inflate(R.layout.single_task, null);
        return new SingleTaskViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleTaskViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.item=item;
        holder.finishCheckBox.setPopupWindiwContainer(AllTaskFragment.getInstance().getView());
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(AllTaskFragment.getInstance().getContext(), EditTaskActivity.class);
                    intent.putExtra("task", item.toJson());
                    AllTaskFragment.getInstance().startActivity(intent);
                } catch (JSONException e) {
                    LogUtil.Error("打开编辑器失败", e);
                }
            }
        });
        holder.titleView.setText(item.title);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        holder.deadlineView.setText("截止日期:" + format.format(item.deadLine));
        holder.finishCheckBox.setChecked(item.isFinished);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectedVH=holder;
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }
}
