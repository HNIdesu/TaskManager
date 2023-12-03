package com.hnidesu.taskmanager.component;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Item {
    public String content;
    public String title;
    public boolean isFinished;
    public Date deadLine;
    public Date createTime;
    public Date lastModifiedTime;

    public String toJson() throws JSONException {
        JSONObject obj=new JSONObject();
        obj.put("content",content==null?"":content);
        obj.put("title",title==null?"":title);
        obj.put("is_finished",isFinished);
        obj.put("deadline",deadLine.getTime());
        obj.put("create_time",createTime.getTime());
        obj.put("last_modified_time",lastModifiedTime.getTime());
        return obj.toString();
    }


}
