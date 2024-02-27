package com.hnidesu.taskmanager.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hnidesu.taskmanager.BuildConfig;
import com.hnidesu.taskmanager.R;
import com.hnidesu.taskmanager.component.Item;
import com.hnidesu.taskmanager.dialog.ImportDataDialog;
import com.hnidesu.taskmanager.utility.DBUtil;
import com.hnidesu.taskmanager.utility.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class SettingFragment extends Fragment {
    Button btnImportData;
    Button btnExportData;

    public SettingFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_setting, container, false);
        btnExportData=rootView.findViewById(R.id.btn_export_data);
        btnImportData=rootView.findViewById(R.id.btn_import_data);

        btnExportData.setOnClickListener(view -> {
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_CREATE_DOCUMENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_TITLE, "database_"+BuildConfig.APPLICATION_ID+".db");
            startActivityForResult(intent,4096);
        });
        btnImportData.setOnClickListener(view -> {
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(intent,4097);
        });
        return rootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==4096 && resultCode== Activity.RESULT_OK){//导出数据
            File file=getContext().getDatabasePath(DBUtil.dbName);
            byte[] buffer=null;
            try(InputStream inputStream=new FileInputStream(file)){
                buffer=new byte[inputStream.available()];
                inputStream.read(buffer);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.ToastLong(getString(R.string.save_data_failed));
                return;
            }
            try(OutputStream outputStream=getContext().getContentResolver().openOutputStream(data.getData())){
                outputStream.write(buffer);
            }catch (Exception e){
                e.printStackTrace();
                ToastUtil.ToastLong(getString(R.string.save_data_failed));
            }

        }else if(requestCode==4097 && resultCode== Activity.RESULT_OK){//读取数据
            List<Item> list=null;
            File file=new File(getContext().getCacheDir().getAbsolutePath()+"/temp_database.db") ;
            if(file.exists())
                file.delete();
            try {
                file.createNewFile();
                InputStream inputStream= getContext().getContentResolver().openInputStream(data.getData());
                byte[] buffer=new byte[inputStream.available()];
                inputStream.read(buffer);
                inputStream.close();
                OutputStream outputStream=new FileOutputStream(file);
                outputStream.write(buffer);
                outputStream.close();
                SQLiteDatabase database= SQLiteDatabase.openDatabase(file.getAbsolutePath(),null,SQLiteDatabase.OPEN_READONLY);
                try(Cursor cursor=database.rawQuery("SELECT * FROM tasks",new String[]{})){
                    if(cursor.getColumnCount()!=7){
                        throw new SQLException();
                    }
                    list= DBUtil.getItemsFromCursor(cursor,null);
                }
                database.close();

            } catch (IOException | SQLException e) {
                e.printStackTrace();
                Toast.makeText(SettingFragment.this.getContext(),getString(R.string.invalie_database_type),Toast.LENGTH_LONG).show();
            } finally {
                if(file.exists())
                    file.delete();
            }
            new ImportDataDialog(SettingFragment.this.getContext(),list==null? new ArrayList<>():list).show();

        }
    }
}