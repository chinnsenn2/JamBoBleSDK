package com.jianbao.jamboble.data;

/**
 * Created by zhangmingyao on 2017/8/16 11:49
 * Email:501863760@qq.com
 */

public class SpO2Data extends BTData{


    private int mSpO2;
    private int mPR;
    private float mPI;
    private boolean mStatus;
    private float mPower;
    private int mMode;

    public int getSpO2() {
        return mSpO2;
    }

    public void setSpO2(int spO2) {
        mSpO2 = spO2;
    }

    public int getPR() {
        return mPR;
    }

    public void setPR(int PR) {
        mPR = PR;
    }

    public float getPI() {
        return mPI;
    }

    public void setPI(float PI) {
        mPI = PI;
    }

    public boolean isStatus() {
        return mStatus;
    }

    public void setStatus(boolean status) {
        mStatus = status;
    }

    public float getPower() {
        return mPower;
    }

    public void setPower(float power) {
        mPower = power;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        mMode = mode;
    }
}
