package com.hnidesu.taskmanager.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.hnidesu.taskmanager.R;
import com.hnidesu.taskmanager.menu.EditTaskPopupMenu;
import com.hnidesu.taskmanager.utility.DBUtil;
import com.hnidesu.taskmanager.utility.LogUtil;
import com.hnidesu.taskmanager.utility.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class EditTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        EditText editText=findViewById(R.id.edittext);
        TextView titleView=findViewById(R.id.textview_title);
        ImageButton menuButton=findViewById(R.id.button_menu);
        try {
            JSONObject obj=new JSONObject(getIntent().getExtras().get("task").toString());
            String content= obj.getString("content");
            titleView.setText(obj.getString("title"));
            editText.setText(content);
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditTaskPopupMenu menu=new EditTaskPopupMenu(EditTaskActivity.this, menuButton, new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            try{
                                switch (item.getItemId()){
                                    case R.id.option_exit:
                                        EditTaskActivity.this.finish();
                                        return true;
                                    case R.id.option_save:
                                        DBUtil.getInstance().updateContent(obj.getLong("create_time"),editText.getText().toString());
                                        return true;
                                }
                            }catch (Exception e){
                                ToastUtil.ToastShort("保存失败");
                                LogUtil.Error("保存失败",e);
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