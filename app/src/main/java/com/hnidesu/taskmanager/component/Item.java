package com.hnidesu.taskmanager.component;

import android.os.Bundle;

import java.util.Date;

public class Item {
    public String content;
    public String title;
    public boolean isFinished;
    public Date deadLine;
    public Date createTime;
    public Date lastModifiedTime;

    public Bundle toBundle() {
        Bundle bundle=new Bundle();
        bundle.putString("content",content==null?"":content);
        bundle.putString("title",title==null?"":title);
        bundle.putBoolean("is_finished",isFinished);
        bundle.putLong("deadline",deadLine.getTime());
        bundle.putLong("create_time",createTime.getTime());
        bundle.putLong("last_modified_time",lastModifiedTime.getTime());
        return bundle;
    }


}
