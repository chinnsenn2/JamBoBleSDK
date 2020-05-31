package com.jianbao.jamboble.device;

import com.jianbao.jamboble.data.BTData;

/**
 * Created by zhangmingyao on 2017/9/25.
 */

public class LakalaBandDevice extends BTDevice{

    private String deviceId;
    private String deviceSn;
    private String deviceNo;

    public LakalaBandDevice() {
        super("拉卡拉手环",
                "LakalaB3_440",
                "0000fee7-0000-1000-8000-00805f9b34fb",
                "0000fec9-0000-1000-8000-00805f9b34fb");
        deviceId = "0ee1ad0f6c9bf15f3ee0e6c1f3724814";
        deviceSn = "LAKALA_BAND";
    }

    @Override
    public BTData paserData(byte[] data) {
        return null;
    }

    @Override
    public int getImageResource() {
        return 0;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }
}
