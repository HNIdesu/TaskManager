package com.hnidesu.taskmanager.menu;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.hnidesu.taskmanager.R;

public class EditTaskPopupMenu extends PopupMenu {
    public EditTaskPopupMenu(@NonNull Context context, @NonNull View anchor,OnMenuItemClickListener listener) {
        super(context, anchor);
        this.inflate(R.menu.menu_edit_task_activity);
        setOnMenuItemClickListener(listener);
    }


}
