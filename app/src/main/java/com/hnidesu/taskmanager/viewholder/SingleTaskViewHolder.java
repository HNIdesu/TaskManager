package com.hnidesu.taskmanager.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hnidesu.taskmanager.R;
import com.hnidesu.taskmanager.component.Item;
import com.hnidesu.taskmanager.ui.CheckBoxEx;

public class SingleTaskViewHolder extends RecyclerView.ViewHolder {
    public TextView titleView;
    public TextView deadlineView;
    public CheckBoxEx finishCheckBox;
    public Item item;

    public SingleTaskViewHolder(@NonNull View itemView) {
        super(itemView);
        this.titleView = itemView.findViewById(R.id.textview_title);
        this.deadlineView = itemView.findViewById(R.id.textview_deadline);
        this.finishCheckBox = itemView.findViewById(R.id.checkbox_isfinish);
    }
}
