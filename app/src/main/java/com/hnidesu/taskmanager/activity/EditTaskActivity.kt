package com.hnidesu.taskmanager.activity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hnidesu.taskmanager.R;
import com.hnidesu.taskmanager.menu.EditTaskPopupMenu;
import com.hnidesu.taskmanager.ui.EditTextEx;
import com.hnidesu.taskmanager.utility.DBUtil;
import com.hnidesu.taskmanager.utility.HashUtil;
import com.hnidesu.taskmanager.utility.LogUtil;
import com.hnidesu.taskmanager.utility.ToastUtil;

public class EditTaskActivity extends AppCompatActivity {
    private long mRawTextHash=0;
    private EditTextEx editText;
    private TextView tvTitle;
    private ImageButton btnMenu;

    private void onExit(){
        Bundle bundle = (Bundle) getIntent().getExtras().get("task");
        String temp=editText.getText().toString();
        if(HashUtil.CRC32Digest(temp)!=mRawTextHash){
            new AlertDialog.Builder(EditTaskActivity.this).
                setCancelable(false).
                setMessage(R.string.request_content_change).
                setPositiveButton(getString(R.string.save), (dialogInterface, i) -> {
                    try{
                        DBUtil.getInstance().updateContent(bundle.getLong("create_time"),temp);
                        EditTaskActivity.this.finish();
                    }catch (Exception e){
                        ToastUtil.ToastLong(getString(R.string.save_failed));
                        LogUtil.Error(R.string.save_failed,e);
                    }
                    }).setNegativeButton(R.string.do_not_save, (dialogInterface, i) -> EditTaskActivity.this.finish())
                    .create().show();
            }else
                EditTaskActivity.this.finish();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        editText =findViewById(R.id.edittext);
        tvTitle=findViewById(R.id.textview_title);
        btnMenu=findViewById(R.id.button_menu);
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onExit();
            }
        });
        try {
            Bundle bundle=(Bundle) getIntent().getExtras().get("task");
            String content= bundle.getString("content");
            mRawTextHash= HashUtil.CRC32Digest(content);
            String title=bundle.getString("title");
            editText.setTextChangeListener((view, text) -> {
                long hash=HashUtil.CRC32Digest(text);
                if(hash!=mRawTextHash)
                    tvTitle.setText(title+"*");
                else
                    tvTitle.setText(title);
            });
            tvTitle.setText(title);
            editText.setText(content);
            btnMenu.setOnClickListener(v -> {
                EditTaskPopupMenu menu=new EditTaskPopupMenu(EditTaskActivity.this, btnMenu, item -> {
                    if(item.getItemId()==R.id.option_close){
                        onExit();
                        return true;
                    }else if(item.getItemId()==R.id.option_save){
                        CharSequence temp=editText.getText();
                        DBUtil.getInstance().updateContent(bundle.getLong("create_time"),temp.toString());
                        mRawTextHash= HashUtil.CRC32Digest(temp);
                        tvTitle.setText(title);
                        return true;
                    }
                    return false;
                });
                menu.show();
            });
        } catch (Exception e) {
            editText.setText(R.string.open_editor_failed);
            LogUtil.Error(R.string.open_editor_failed,e);
        }




    }


}