package com.jianbao.jamboble.device.oximeter;

import com.creative.FingerOximeter.IFingerOximeterCallBack;
import com.creative.base.BaseDate;
import com.jianbao.jamboble.BleHelper;
import com.jianbao.jamboble.data.SpO2Data;
import com.jianbao.jamboble.utils.LogUtils;

import java.util.List;

/**
 * Created by zhangmingyao on 2017/8/16 11:55
 * Email:501863760@qq.com
 */

public class FingerOximeterCallback implements IFingerOximeterCallBack {

    public static final String TAG = FingerOximeterCallback.class.getName();

    BleHelper bleHelper;
    SpO2Data mSpO2Data = new SpO2Data();

    public FingerOximeterCallback(BleHelper bleHelper) {
        this.bleHelper = bleHelper;
    }

    @Override
    public void OnGetSpO2Param(int nSpO2, int nPR, float fPI, boolean nStatus, int nMode, float nPower, int powerLevel) {
        mSpO2Data.setSpO2(nSpO2);
        mSpO2Data.setPR(nPR);
        mSpO2Data.setPI(fPI);
        mSpO2Data.setStatus(nStatus);
        mSpO2Data.setMode(nMode);
        mSpO2Data.setPower(nPower);
        bleHelper.onBTDataReceived(mSpO2Data);
    }

    @Override
    public void OnGetSpO2Wave(List<BaseDate.Wave> waves) {

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