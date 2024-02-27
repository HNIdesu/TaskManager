package com.hnidesu.taskmanager.menu;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.hnidesu.taskmanager.R;
import com.hnidesu.taskmanager.utility.DBUtil;

public class SortTypePopUpMenu extends PopupMenu {
    public SortTypePopUpMenu(@NonNull Context context, @NonNull View anchor) {
        super(context, anchor);
        inflate(R.menu.menu_sort_type);
        setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.item_sort_creation:{
                    DBUtil.getInstance().setSortType(DBUtil.SortType.Creation);
                    break;
                }
                case R.id.item_sort_modified:{
                    DBUtil.getInstance().setSortType(DBUtil.SortType.Modified);
                    break;
                }
                case R.id.item_sort_deadline:{
                    DBUtil.getInstance().setSortType(DBUtil.SortType.Deadline);
                    break;
                }
                default:
                    return false;
            }
            DBUtil.getInstance().notifyObservers();
            return true;
        });
    }


}
