package com.jianbao.jamboble.device.oximeter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.creative.FingerOximeter.IFingerOximeterCallBack;
import com.creative.base.BaseDate;
import com.jianbao.jamboble.utils.LogUtils;
import com.jianbao.jamboble.data.SpO2Data;
import com.jianbao.jamboble.draw.DrawThreadNW;

import java.util.List;

/**
 * Created by zhangmingyao on 2017/8/16 11:55
 * Email:501863760@qq.com
 */

public class FingerOximeterCallback implements IFingerOximeterCallBack {

    public static final String TAG = FingerOximeterCallback.class.getName();

    /**
     * 血氧参数
     */
    public static final byte MSG_DATA_SPO2_PARA = 0x01;
    /**
     * 血氧波形数据
     */
    public static final byte MSG_DATA_SPO2_WAVE = 0x02;
    /**
     * 导联脱落
     */
    public static final byte MSG_PROBE_OFF = 0x06;


    SpO2Data mSpO2Data = new SpO2Data();

    private Handler mHandler;
    private DrawThreadNW mDrawThreadNW;

    public FingerOximeterCallback(Handler handler, DrawThreadNW mDrawThreadNW) {
        this.mHandler = handler;
        this.mDrawThreadNW = mDrawThreadNW;
    }

    @Override
    public void OnGetSpO2Param(int nSpO2, int nPR, float fPI, boolean nStatus, int nMode, float nPower, int powerLevel) {
        Message msg = mHandler.obtainMessage(MSG_DATA_SPO2_PARA);
        Bundle data = new Bundle();
        mSpO2Data.setSpO2(nSpO2);
        mSpO2Data.setPR(nPR);
        mSpO2Data.setPI(fPI);
        mSpO2Data.setStatus(nStatus);
        mSpO2Data.setMode(nMode);
        mSpO2Data.setPower(nPower);
        data.putSerializable("data", mSpO2Data);
        msg.setData(data);
        mHandler.sendMessage(msg);

    }

    @Override
    public void OnGetSpO2Wave(List<BaseDate.Wave> waves) {
        mDrawThreadNW.addWaveData(waves);
        mHandler.sendEmptyMessage(MSG_DATA_SPO2_WAVE);
    }

    @Override
    public void OnGetDeviceVer(String hardVer, String softVer, String deviceName) {

    }

    @Override
    public void OnConnectLose() {
        System.out.println("OnConnectLose");
        LogUtils.i("OnConnectLose");
    }
}