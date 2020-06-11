package com.jianbao.jamboble.callbacks

import android.bluetooth.BluetoothDevice
import com.jianbao.jamboble.device.BTDevice

/**
 * Created by zhangmingyao
 * date: 2018/7/20.
 * Email:501863760@qq.com
 */
interface IBleStatusCallback {
    fun onBTDeviceFound(device: BluetoothDevice?)
    fun onNotification()
}