package com.hnidesu.taskmanager.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.hnidesu.taskmanager.MyApplication;
import com.hnidesu.taskmanager.component.Item;
import com.hnidesu.taskmanager.component.Observable;
import com.hnidesu.taskmanager.component.Observer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DBUtil extends Observable {
    public enum SortType{
        Creation,Modified,Deadline
    }
    private SortType mSortType;
    public void setSortType(SortType type){
        mSortType=type;
        SharedPreferences.Editor editor= mContext.getSharedPreferences("setting",Context.MODE_PRIVATE).edit();
        editor.putString("sort_type",type.toString());
        editor.commit();
    }
    public final static String dbName="database.db";
    private static DBUtil instance;
    private Context mContext;
    public SQLiteOpenHelper1 sQLiteOpenHelper;
    private List<Observer> observerList;
    private DBUtil(Context ctx){
        mContext=ctx;
        sQLiteOpenHelper=new SQLiteOpenHelper1(mContext,dbName,null,1);
    }


    public void setContext(Context context) {
        mContext = context;
    }



    public static DBUtil getInstance(){
        if(instance==null){
            instance=new DBUtil(MyApplication.getInstance().getApplicationContext());
            instance.setSortType(SortType.valueOf(instance.mContext.getSharedPreferences("setting", Context.MODE_PRIVATE).getString("sort_type","Creation")));
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

    public ArrayList<Item> getAllTasks(){
        SQLiteDatabase db=sQLiteOpenHelper.getReadableDatabase();
        ArrayList<Item> list=new ArrayList<>();
        Cursor cursor= db.rawQuery("SELECT content,deadline,create_time,last_modified_time,is_finished,title FROM tasks"+getAdditionalSql(),new String[]{});
        while (cursor.moveToNext()){
            Item item=new Item();
            item.content=cursor.getString(0);
            item.deadLine=new Date(cursor.getLong(1));
            item.createTime=new Date(cursor.getLong(2));
            item.lastModifiedTime=new Date(cursor.getLong(3));
            item.isFinished=cursor.getInt(4)==1;
            item.title=cursor.getString(5);
            list.add(item);
        }
        cursor.close();
        return list;
    }

    public ArrayList<Item> getUnfinishedTasks(boolean showObsolete){
        SQLiteDatabase db=sQLiteOpenHelper.getReadableDatabase();
        ArrayList<Item> list=new ArrayList<>();

        Cursor cursor= db.rawQuery("SELECT content,deadline,create_time,last_modified_time,title FROM tasks where is_finished = 0"+getAdditionalSql(),new String[]{});
        while (cursor.moveToNext()){
            if(!showObsolete && (System.currentTimeMillis()-cursor.getLong(1)>5000))
                continue;
            Item item=new Item();
            item.isFinished=false;
            item.content=cursor.getString(0);
            item.deadLine=new Date(cursor.getLong(1));
            item.createTime=new Date(cursor.getLong(2));
            item.lastModifiedTime=new Date(cursor.getLong(3));
            item.title=cursor.getString(4);
            list.add(item);
        }
        cursor.close();
        return list;
    }

    public ArrayList<String> getEndingTasks(int ms){
        SQLiteDatabase db=sQLiteOpenHelper.getReadableDatabase();

        ArrayList<String> list=new ArrayList<>();
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

        SQLiteDatabase db=sQLiteOpenHelper.getWritableDatabase();
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
        SQLiteDatabase db=sQLiteOpenHelper.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT title,content,last_modified_time,is_finished,deadline,create_time FROM tasks WHERE create_time=?",new String[]{
                Long.toString(create_time)
        });
        if(!cursor.moveToNext())
            return null;
        Item item=new Item();
        item.isFinished=cursor.getInt(3)==1;
        item.content=cursor.getString(1);
        item.deadLine=new Date(cursor.getLong(4));
        item.createTime=new Date(cursor.getLong(5));
        item.lastModifiedTime=new Date(cursor.getLong(2));
        item.title=cursor.getString(0);
        cursor.close();
        return item;
    }

    public void updateContent(long create_time,String content){
        SQLiteDatabase db=sQLiteOpenHelper.getWritableDatabase();
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
        SQLiteDatabase db=sQLiteOpenHelper.getWritableDatabase();
        db.execSQL("DELETE FROM tasks WHERE create_time=?",new Object[]{
                create_time
        });
        notifyObservers();
    }

    public void addTask(Item item){
        SQLiteDatabase db=sQLiteOpenHelper.getWritableDatabase();
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

    public class SQLiteOpenHelper1 extends SQLiteOpenHelper {

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
