package com.jianbao.jamboble.device;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattDescriptor;

import com.jianbao.jamboble.data.BloodPressureData;

/**
 * 鱼跃血压计
 *
 * @author 毛晓飞
 */
public class YuwellBloodPressure extends BTDevice {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    //Yuwell BP-YE680A
    //Yuwell BloodPressure

    public YuwellBloodPressure() {
        super("鱼跃血压计",
                "Yuwell BloodPressure",
                "00001810-0000-1000-8000-00805f9b34fb",
                "00002a35-0000-1000-8000-00805f9b34fb");
    }

    @Override
    public BloodPressureData paserData(byte[] data) {
        //血压数据解析
        if (data != null && data.length == 19) {
            // 收缩压
            int systolicPressure = ((0xFF & data[2]) << 8)
                    | ((0xFF & data[1]) << 0);
            // 舒张压
            int diastolicPressure = ((0xFF & data[4]) << 8)
                    | ((0xFF & data[3]) << 0);
            // 心率
            int heartRate = (0xFF & data[14]);

            BloodPressureData btData = new BloodPressureData();
            btData.systolicPressure = systolicPressure;
            btData.diastolicPressure = diastolicPressure;
            btData.heartRate = heartRate;
            btData.deviceID = getBTDeviceID();
            return btData;
        }
        return null;
    }

    @SuppressLint("NewApi")
    public byte[] getDescriptorEnabledValue() {
        return BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
    }

    @Override
    public int getImageResource() {
        return 0;
    }
}