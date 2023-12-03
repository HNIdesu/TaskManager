package com.hnidesu.taskmanager.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hnidesu.taskmanager.R;

public class SimplePopupWindow extends PopupWindow {

    public interface OnDialogResultListener{
        void onCancel(SimplePopupWindow dialog);
        void onOk(SimplePopupWindow dialog);
    }
    public SimplePopupWindow(@NonNull Context ctx,String message,OnDialogResultListener listener){
        super(ctx);
        View view= LayoutInflater.from(ctx).inflate(R.layout.window_popup,null);
        this.setContentView(view);
        view.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancel(SimplePopupWindow.this);
                SimplePopupWindow.this.dismiss();
            }
        });
        view.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onOk(SimplePopupWindow.this);
                SimplePopupWindow.this.dismiss();
            }
        });
        ((TextView)view.findViewById(R.id.textview_msg)).setText(message);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
    }
    public void popup(@NonNull View view){
        this.showAtLocation(view, Gravity.CENTER,0,0);
    }
}