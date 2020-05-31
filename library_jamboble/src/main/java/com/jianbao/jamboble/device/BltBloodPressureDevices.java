package com.jianbao.jamboble.device;

import com.jianbao.jamboble.data.BTData;
import com.jianbao.jamboble.data.BloodPressureData;

/**
 * Created by zhangmingyao on 2017/8/31.
 */

public class BltBloodPressureDevices extends BTDevice {


    //写 "0000fff1"
    public BltBloodPressureDevices() {
        super("宝莱特血压计",
                "BLT_WBP",
                "0000fff0-0000-1000-8000-00805f9b34fb",
                "0000fff4-0000-1000-8000-00805f9b34fb");
    }

    @Override
    public BTData paserData(byte[] data) {

        if (data != null && data.length == 15) {

            //aa d 29 20 0 80 4c 0 44 4d f9 3a 21 0 7
            if (data[0] == (byte) 0xAA && data[1] == (byte) 0x0d && data[2] == 0x29 && data[3] == 0x20) {
                BloodPressureData bloodPressureData = new BloodPressureData();
                bloodPressureData.systolicPressure = 0xFF & data[5];
                bloodPressureData.diastolicPressure = 0xFF & data[6];
                bloodPressureData.heartRate = 0xFF & data[8];
                return bloodPressureData;
            }
        }

        return null;
    }

    @Override
    public int getImageResource() {
        return 0;
    }
}
