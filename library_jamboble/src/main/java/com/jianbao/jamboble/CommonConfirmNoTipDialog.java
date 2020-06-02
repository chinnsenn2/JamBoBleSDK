package com.jianbao.jamboble;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;


/**
 * 确定，取消对话框
 *
 * @author zhangmingyao
 */
public class CommonConfirmNoTipDialog extends BaseAutoSizeDialog implements View.OnClickListener {

    private OnRightClickListener mRightClickListener;
    private OnLeftClickListener mLeftClickListener;

    private Button mBtnRight;

    private Button mBtnLeft;

    private TextView mTvTips;

    private int position;

    public CommonConfirmNoTipDialog(@NonNull Context context) {
        super(context, R.style.hkwbasedialog);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_common_confirm_no_tip;
    }

    @Override
    public void initView() {

        mBtnRight = findViewById(R.id.btn_confirm);
        mBtnRight.setOnClickListener(this);

        mBtnLeft = findViewById(R.id.btn_cancel);
        mBtnLeft.setOnClickListener(this);
        mTvTips = findViewById(R.id.tv_tips);
    }

    @Override
    public void initData() {
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (v.getId() == R.id.btn_confirm) {
            if (mRightClickListener != null) {
                mRightClickListener.onRightClick();
            }
        } else if (v.getId() == R.id.btn_cancel) {
            if (mLeftClickListener != null) {
                mLeftClickListener.onLeftClick();
            }
        }
    }


    public CommonConfirmNoTipDialog setRightClickListener(OnRightClickListener listener) {
        this.mRightClickListener = listener;
        return this;
    }

    public CommonConfirmNoTipDialog setLeftClickListener(OnLeftClickListener listener) {
        this.mLeftClickListener = listener;
        return this;
    }

    public interface OnRightClickListener {
        void onRightClick();
    }

    public interface OnLeftClickListener {
        void onLeftClick();
    }

    public CommonConfirmNoTipDialog setRightText(CharSequence text) {
        mBtnRight.setText(text);
        return this;
    }

    public CommonConfirmNoTipDialog setLeftText(CharSequence text) {
        mBtnLeft.setText(text);
        return this;
    }

    public CommonConfirmNoTipDialog setTips(String text) {
        mTvTips.setText(text);
        return this;
    }

    public CommonConfirmNoTipDialog showTipsLeft(String text) {
        setTips(text);
        mTvTips.setGravity(Gravity.LEFT);
        return this;
    }

    public CommonConfirmNoTipDialog showTipsLeftSpan(CharSequence text) {
        mTvTips.setText(text);
        mTvTips.setLineSpacing(10, 1f);
        mTvTips.setGravity(Gravity.LEFT);
        return this;
    }

    public CommonConfirmNoTipDialog showTipsCenter(String text) {
        setTips(text);
        mTvTips.setGravity(Gravity.CENTER);
        return this;
    }

}
