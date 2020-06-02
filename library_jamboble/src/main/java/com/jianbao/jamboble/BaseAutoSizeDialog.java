package com.jianbao.jamboble;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

public abstract class BaseAutoSizeDialog extends Dialog {
    private View mRootView;
    protected Context mContext;

    public BaseAutoSizeDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public BaseAutoSizeDialog(@NonNull Context context, int themeResId) {
        super(context,themeResId);
        init(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void init(Context context) {
        mContext = context;
        mRootView = LayoutInflater.from(context).inflate(getLayoutId(), null);
        setContentView(mRootView);
    }

    protected void setFullWidth(){
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
    }

    protected void setFullHeight(){
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
    }

    protected void setGravityBottom(){
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        lp.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
    }

    public View getRootView() {
        return mRootView;
    }

    public abstract int getLayoutId();

    public abstract void initView();

    public abstract void initData();


    public <T extends View> T find(@IdRes int id) {
        return mRootView.findViewById(id);
    }

    public void setWidth(int width) {
        final Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = width;
        window.setAttributes(params);
    }

    public void setOnClickListener(View.OnClickListener listener, View... views) {
        for (View view : views) {
            view.setOnClickListener(listener);
        }
    }
}
