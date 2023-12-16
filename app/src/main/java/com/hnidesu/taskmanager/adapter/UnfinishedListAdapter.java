package com.hnidesu.taskmanager.adapter;

import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hnidesu.taskmanager.R;
import com.hnidesu.taskmanager.activity.EditTaskActivity;
import com.hnidesu.taskmanager.component.Item;
import com.hnidesu.taskmanager.fragment.AllTaskFragment;
import com.hnidesu.taskmanager.fragment.UnfinishedTaskFragment;
import com.hnidesu.taskmanager.ui.CheckBoxEx;
import com.hnidesu.taskmanager.utility.DBUtil;
import com.hnidesu.taskmanager.utility.LogUtil;
import com.hnidesu.taskmanager.viewholder.SingleTaskViewHolder;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UnfinishedListAdapter extends RecyclerView.Adapter<SingleTaskViewHolder>{
    private List<Item> itemList;
    private SingleTaskViewHolder selectedVH;
    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SingleTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root= LayoutInflater.from(UnfinishedTaskFragment.getInstance().getContext()).inflate(R.layout.single_task,null);
        return new SingleTaskViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleTaskViewHolder holder, int position) {
        Item item=itemList.get(position);
        holder.item=item;
        holder.finishCheckBox.setPopupWindiwContainer(UnfinishedTaskFragment.getInstance().getView());
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
        Date deadline=item.deadLine;
        SpannableStringBuilder toShow=new SpannableStringBuilder();
        if(deadline.getTime()<System.currentTimeMillis()){
            toShow.append("已超时");
            toShow.setSpan(new ForegroundColorSpan(0xFFFF0000),0,3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else if((deadline.getTime()-System.currentTimeMillis())<1000*60*60){//剩余一小时，按分钟显示
            String fmt=String.format("剩余%d分钟",(deadline.getTime()-System.currentTimeMillis())/1000/60);
            toShow.append(fmt);
            toShow.setSpan(new ForegroundColorSpan(0xDFFF0000),0,fmt.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else if((deadline.getTime()-System.currentTimeMillis())<1000*60*60*24) {//剩余一天，按小时显示
            String fmt=String.format("剩余%d小时",(deadline.getTime()-System.currentTimeMillis())/1000/60/60);
            toShow.append(fmt);
            toShow.setSpan(new ForegroundColorSpan(0xBFFF0000),0,fmt.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else if((deadline.getTime()-System.currentTimeMillis())<1000*60*60*24*10){//剩余十天，按天表示
            String fmt=String.format("剩余%d天",(deadline.getTime()-System.currentTimeMillis())/1000/60/60/24);
            toShow.append(fmt);
            toShow.setSpan(new ForegroundColorSpan(0x8FFF0000),0,fmt.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            toShow.append("截止日期:").append(format.format(deadline));
        }
        holder.deadlineView.setText(toShow);
        holder.finishCheckBox.setChecked(item.isFinished);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectedVH = holder;
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList==null?0:itemList.size();
    }


}
