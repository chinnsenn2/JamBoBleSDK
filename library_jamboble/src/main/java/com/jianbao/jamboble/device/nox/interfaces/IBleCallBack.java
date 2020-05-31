package com.jianbao.jamboble.device.nox.interfaces;

import android.bluetooth.BluetoothDevice;

import com.jianbao.jamboble.device.nox.ConnectionState;


/**
 * Created by hao on 16/6/8.
 * 蓝牙底层回调
 */

public interface IBleCallBack {
    /**
     *
     * @param bluetoothDevice
     * @param i
     * @param bytes
     */
    void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes);

    /**
     * 处理数据
     *
     * @param data
     */
    void handleLeData(byte[] data);

    /**
     *
     * @param state
     */
    void onBleStateChanged(ConnectionState state);
}
