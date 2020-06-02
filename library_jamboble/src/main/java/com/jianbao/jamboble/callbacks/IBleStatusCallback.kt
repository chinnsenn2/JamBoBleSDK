package com.jianbao.jamboble.callbacks

import android.bluetooth.BluetoothDevice
import androidx.annotation.IntDef
import com.jianbao.jamboble.device.BTDevice

/**
 * Created by zhangmingyao
 * date: 2018/7/20.
 * Email:501863760@qq.com
 */
interface IBleStatusCallback {
    fun onBTStateChanged(state: Int)
    fun onBTDeviceFound(device: BluetoothDevice?)
    fun onNotification()
    fun doByThirdSdk(
        device: BluetoothDevice?,
        btDevice: BTDevice?,
        rssi: Int,
        scanRecord: ByteArray?
    )

    @IntDef
    annotation class State {
        companion object {
            @JvmStatic
            var NOT_FOUND = -1
            @JvmStatic
            var SCAN_START = 0
            @JvmStatic
            var CONNECTED = 1
            @JvmStatic
            var TIMEOUT = 2
        }
    }
}