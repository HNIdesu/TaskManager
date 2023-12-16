package com.hnidesu.taskmanager.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.hnidesu.taskmanager.R;
import com.hnidesu.taskmanager.menu.EditTaskPopupMenu;
import com.hnidesu.taskmanager.ui.EditTextEx;
import com.hnidesu.taskmanager.utility.DBUtil;
import com.hnidesu.taskmanager.utility.HashUtil;
import com.hnidesu.taskmanager.utility.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class EditTaskActivity extends AppCompatActivity {
    private long mRawTextHash=0;
    private EditTextEx editText;
    private TextView tvTitle;
    private ImageButton btnMenu;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        onExit();
    }

    private void onExit(){
        try {
            JSONObject obj = new JSONObject(getIntent().getExtras().get("task").toString());
            String temp=editText.getText().toString();
            if(HashUtil.CRC32Digest(temp)!=mRawTextHash){
                new AlertDialog.Builder(EditTaskActivity.this).
                        setCancelable(false).
                        setMessage(R.string.request_content_change).
                        setPositiveButton("保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try{
                                    DBUtil.getInstance().updateContent(obj.getLong("create_time"),temp);
                                    EditTaskActivity.this.finish();
                                }catch (Exception e){
                                    ToastUtil.ToastLong("保存失败");
                                }

                            }
                        }).setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditTaskActivity.this.finish();
                            }
                        }).create().show();
            }else
                EditTaskActivity.this.finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        editText =findViewById(R.id.edittext);
        tvTitle=findViewById(R.id.textview_title);
        btnMenu=findViewById(R.id.button_menu);



        try {
            JSONObject obj=new JSONObject(getIntent().getExtras().get("task").toString());
            String content= obj.getString("content");
            mRawTextHash= HashUtil.CRC32Digest(content);
            String title=obj.getString("title");
            editText.setTextChangeListener(new EditTextEx.TextChangeListener() {
                @Override
                public void onTextChange(View view, CharSequence text) {
                    long hash=HashUtil.CRC32Digest(text);
                    if(hash!=mRawTextHash)
                        tvTitle.setText(String.format("%s*",title) );
                    else
                        tvTitle.setText(title);
                }
            });
            tvTitle.setText(title);
            editText.setText(content);
            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditTaskPopupMenu menu=new EditTaskPopupMenu(EditTaskActivity.this, btnMenu, new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            try{
                                if(item.getItemId()==R.id.option_close){
                                    onExit();
                                    return true;
                                }else if(item.getItemId()==R.id.option_save){
                                    CharSequence temp=editText.getText();
                                    DBUtil.getInstance().updateContent(obj.getLong("create_time"),temp.toString());
                                    mRawTextHash= HashUtil.CRC32Digest(temp);
                                    tvTitle.setText(title);
                                    return true;
                                }
                            }catch (Exception e){
                                ToastUtil.ToastShort("保存失败");
                            }
                            return false;

                        }
                    });
                    menu.show();
                }
            });
        } catch (JSONException e) {
            editText.setText("编辑器打开失败");
        }




    }


}