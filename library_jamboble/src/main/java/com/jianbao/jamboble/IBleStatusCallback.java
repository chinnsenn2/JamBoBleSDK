package com.jianbao.jamboble;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.IntDef;

import com.jianbao.jamboble.device.BTDevice;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zhangmingyao
 * date: 2018/7/20.
 * Email:501863760@qq.com
 */
public interface IBleStatusCallback {
    void onBTStateChanged(int state);

    void onBTDeviceFound(BluetoothDevice device);

    void onNotification();

    void doByThirdSdk(BluetoothDevice device, BTDevice btDevice, int rssi, byte[] scanRecord);

    @Retention(RetentionPolicy.SOURCE)
    @IntDef
    @interface State {
        int NOT_FOUND = -1;
        int SCAN_START = 0;
        int CONNECTED = 1;
        int TIMEOUT = 2;
    }

}
