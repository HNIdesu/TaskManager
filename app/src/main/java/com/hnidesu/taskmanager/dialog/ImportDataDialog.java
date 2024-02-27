package com.hnidesu.taskmanager.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hnidesu.taskmanager.R;
import com.hnidesu.taskmanager.component.Item;
import com.hnidesu.taskmanager.utility.DBUtil;
import com.hnidesu.taskmanager.utility.ToastUtil;

import java.util.List;

public class ImportDataDialog extends Dialog {
    TextView textViewImportInfo;
    Button btnOverride,btnAppend;
    public ImportDataDialog(@NonNull Context context, List<Item> items) {
        super(context);
        setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_import_data,null));
        textViewImportInfo=findViewById(R.id.textView_importInfo);
        btnOverride=findViewById(R.id.btn_override);
        btnAppend=findViewById(R.id.btn_append);
        setOnDismissListener(dialogInterface -> ToastUtil.ToastLong(context.getString(R.string.cancelled)));
        btnAppend.setOnClickListener(view -> {
            try{
                DBUtil.getInstance().importData(items);
                ToastUtil.ToastLong(context.getString(R.string.import_succeed));
            }catch (Exception e){
                ToastUtil.ToastLong(context.getString(R.string.import_failed));
            }
            ImportDataDialog.this.dismiss();

        });
        btnOverride.setOnClickListener(view -> {
            try{
                DBUtil.getInstance().clear();
                DBUtil.getInstance().importData(items);
                ToastUtil.ToastLong(context.getString(R.string.import_succeed));
            }catch (Exception e){
                ToastUtil.ToastLong(context.getString(R.string.import_failed));
            }
            ImportDataDialog.this.dismiss();

        });
        textViewImportInfo.setText(String.format(getContext().getString(R.string.notice_import_data),items.size()));
    }

}
