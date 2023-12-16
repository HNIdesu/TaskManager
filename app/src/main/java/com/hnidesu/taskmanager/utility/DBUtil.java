package com.hnidesu.taskmanager.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.hnidesu.taskmanager.MyApplication;
import com.hnidesu.taskmanager.component.Filter;
import com.hnidesu.taskmanager.component.Item;
import com.hnidesu.taskmanager.component.Observable;
import com.hnidesu.taskmanager.component.Observer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class DBUtil extends Observable {
    public enum SortType{
        Creation,Modified,Deadline
    }
    private SortType mSortType;
    public void setSortType(SortType type){
        mSortType=type;
        SharedPreferences.Editor editor= MyApplication.getInstance().getSharedPreferences("setting",Context.MODE_PRIVATE).edit();
        editor.putString("sort_type",type.toString());
        editor.commit();
    }
    public final static String dbName="database.db";
    private static DBUtil instance;
    public SQLiteOpenHelper1 mSQLiteOpenHelper;
    private List<Observer> observerList;
    private DBUtil(){
        mSQLiteOpenHelper =new SQLiteOpenHelper1(MyApplication.getInstance(),dbName,null,1);
    }


    public static DBUtil getInstance(){
        if(instance==null){
            instance=new DBUtil();
            instance.setSortType(SortType.valueOf(MyApplication.getInstance().getSharedPreferences("setting", Context.MODE_PRIVATE).getString("sort_type","Creation")));
        }
        return instance;
    }

    public String getAdditionalSql(){
        switch (mSortType){
            case Creation:
            {
                return " ORDER BY create_time DESC";
            }
            case Modified:{
                return " ORDER BY last_modified_time DESC";
            }
            case Deadline:{
                return " ORDER BY deadline ASC";
            }

        }
        return "";
    }

    public void clear(){
        SQLiteDatabase db= mSQLiteOpenHelper.getWritableDatabase();
        db.execSQL("DELETE from tasks");
    }
    public List<Item> getAllTasks(){
        SQLiteDatabase db= mSQLiteOpenHelper.getReadableDatabase();
        List<Item> list;
        try(Cursor cursor= db.rawQuery("SELECT * FROM tasks"+getAdditionalSql(),new String[]{})) {
            list=getItemsFromCursor(cursor,null);
        }
        return list;
    }


    public static List<Item> getItemsFromCursor(Cursor cursor,@Nullable Filter<Item> filter){
        HashMap<String,Integer> map=new HashMap<>();
        for(int i=0,length=cursor.getColumnCount();i<length;i++)
           map.put(cursor.getColumnName(i),i);
        ArrayList<Item> list=new ArrayList<>();
        while (cursor.moveToNext()){
            Item item = new Item();
            item.content = cursor.getString(map.get("content"));
            item.deadLine = new Date(cursor.getLong(map.get("deadline")));
            item.createTime = new Date(cursor.getLong(map.get("create_time")));
            item.lastModifiedTime = new Date(cursor.getLong(map.get("last_modified_time")));
            item.isFinished = cursor.getInt(map.get("is_finished")) == 1;
            item.title = cursor.getString(map.get("title"));
            if(filter==null|| filter.match(item))
                list.add(item);
        }
        return list;
    }
    public List<Item> getUnfinishedTasks(boolean showObsolete){
        SQLiteDatabase db= mSQLiteOpenHelper.getReadableDatabase();
        List<Item> list=null;

        try(Cursor cursor= db.rawQuery("SELECT * FROM tasks where is_finished = 0"+getAdditionalSql(),
                new String[]{})) {
            list=getItemsFromCursor(cursor, new Filter<Item>() {
                @Override
                public boolean match(Item item) {
                    return  showObsolete || (item.deadLine.getTime()-System.currentTimeMillis() > 5000);//过滤截止的任务
                }
            });
        }
        return list;
    }

    public List<String> getEndingTasks(int ms){
        SQLiteDatabase db= mSQLiteOpenHelper.getReadableDatabase();

        List<String> list=new ArrayList<>();
        Cursor cursor= db.rawQuery("SELECT title,deadline FROM tasks WHERE is_finished = 0 AND deadline <= ?",new String[]{String.valueOf(System.currentTimeMillis()+ms) });
        while (cursor.moveToNext()){
            if(cursor.getLong(1)-System.currentTimeMillis()<5000)//排除截止任务
                continue;
            list.add(cursor.getString(0));
        }
        cursor.close();
        return list;
    }

    public void updateTask(Item newItem){
        if(findTask(newItem.createTime.getTime())==null)
            throw new RuntimeException();

        SQLiteDatabase db= mSQLiteOpenHelper.getWritableDatabase();
        db.execSQL("UPDATE tasks SET content=?,is_finished=?,last_modified_time=?,deadline=?,title=? where create_time=?",new Object[]{
                newItem.content,
                newItem.isFinished?1:0,
                System.currentTimeMillis(),
                newItem.deadLine.getTime(),
                newItem.title,
                newItem.createTime.getTime()
        });
        notifyObservers();

    }

    public Item findTask(long create_time){
        SQLiteDatabase db= mSQLiteOpenHelper.getReadableDatabase();
        Item item=null;
        try(Cursor cursor= db.rawQuery("SELECT * FROM tasks WHERE create_time=?",new String[]{
                Long.toString(create_time)
        })){
            List<Item> items=getItemsFromCursor(cursor,null);
            if(items.size()!=0)
                item=items.get(0);
        }
        return item;
    }

    public void updateContent(long create_time,String content){
        SQLiteDatabase db= mSQLiteOpenHelper.getWritableDatabase();
        db.execSQL("UPDATE tasks SET content=?,last_modified_time=? WHERE create_time=?",new Object[]{
                content,
                System.currentTimeMillis(),
                create_time
        });
    }

    public void deleteTask(Item item){
        deleteTask(item.createTime.getTime());
    }

    public void deleteTask(long create_time){
        SQLiteDatabase db= mSQLiteOpenHelper.getWritableDatabase();
        db.execSQL("DELETE FROM tasks WHERE create_time=?",new Object[]{
                create_time
        });
        notifyObservers();
    }

    public void importData(List<Item> items){
        SQLiteDatabase db= mSQLiteOpenHelper.getWritableDatabase();
        for(Item item:items){
            db.execSQL(
                    "INSERT INTO tasks (content,is_finished,deadline,create_time,last_modified_time,is_encrypted,title) VALUES(?,?,?,?,?,?,?)",
                    new Object[]{
                            item.content,
                            item.isFinished,
                            item.deadLine.getTime(),
                            item.createTime.getTime(),
                            item.lastModifiedTime.getTime(),
                            0,
                            item.title});
        }
        notifyObservers();

    }

    public void createTasks(List<Item> items){
        SQLiteDatabase db= mSQLiteOpenHelper.getWritableDatabase();
        long current=System.currentTimeMillis();
        for(Item item:items){
            db.execSQL(
                    "INSERT INTO tasks (content,is_finished,deadline,create_time,last_modified_time,is_encrypted,title) VALUES(?,?,?,?,?,?,?)",
                    new Object[]{
                            item.content,
                            0,
                            item.deadLine.getTime(),
                            current,
                            current,
                            0,
                            item.title});
        }
        notifyObservers();

    }
    public void createTask(Item item){
        SQLiteDatabase db= mSQLiteOpenHelper.getWritableDatabase();
        long current=System.currentTimeMillis();
        db.execSQL(
                "INSERT INTO tasks (content,is_finished,deadline,create_time,last_modified_time,is_encrypted,title) VALUES(?,?,?,?,?,?,?)",
                new Object[]{
                        item.content,
                        0,
                        item.deadLine.getTime(),
                        current,
                        current,
                        0,
                        item.title});
        notifyObservers();

    }
    @Override
    public List<Observer> getObserverList() {
        return observerList==null? new ArrayList<>():observerList;
    }

    public void setObserverList(List<Observer> observerList) {
        this.observerList = observerList;
    }

    private static class SQLiteOpenHelper1 extends SQLiteOpenHelper {
        public SQLiteOpenHelper1(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            getReadableDatabase();
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(
                    "CREATE TABLE IF NOT EXISTS tasks (create_time INTEGER PRIMARY KEY NOT NULL,title TEXT,content TEXT,is_finished INTEGER,deadline INTEGER,last_modified_time INTEGER,is_encrypted INTEGER)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        }


    }
}
