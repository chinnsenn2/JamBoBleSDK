package com.jianbao.jamboblesdk

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jianbao.fastble.JamBoBleHelper
import com.jianbao.jamboble.BleState
import com.jianbao.jamboble.callbacks.BleDataCallback
import com.jianbao.jamboble.callbacks.IBleStatusCallback
import com.jianbao.jamboble.data.BTData

class FetalHeartActivity : AppCompatActivity() {
    private val mTvStatus by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_status) }
    private val mTvValueRealtime by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_value_realtime) }
    private val mTvValue by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_value) }
    private val mBtnOpenBle by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_open_ble) }
    private val mBtnConnectBle by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_connect_ble) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fetal_heart)

        /*
         * 考虑到搜索设备的界面和连接设备的界面可能不是同一个界面，所以 openBluetooth 不会自动连接设备
         *
         * 而是在 IBleStatusCallback#onBTDeviceFound 返回合适的设备 BluetoothDevice ，也可以通过 getBluetoothDevice 获取
         */
        mBtnOpenBle.setOnClickListener {
            JamBoBleHelper.instance.scanFetalHeartDevice()
        }

        mBtnConnectBle.setOnClickListener {

        }

        //数据回调
        JamBoBleHelper.instance.setBleDataCallBack(
            object : BleDataCallback {
                override fun onBTStateChanged(state: BleState) {
                    println("FetalHeartActivity.onBTStateChanged")
                }

                override fun onBTDataReceived(data: BTData?) {
                    println("data = [${data}]")
                }

                override fun onLocalBTEnabled(enabled: Boolean) {
                    println("enabled = [${enabled}]")
                }

            }
        )

        //蓝牙设备状态回调
        JamBoBleHelper.instance.setBleStatusCallback(
            object : IBleStatusCallback {
                override fun onBTDeviceFound(device: BluetoothDevice?) {
                    println("device = [${device}]")
                }

                override fun onNotification() {

                }

            }
        )
    }

    override fun onDestroy() {
        JamBoBleHelper.instance.destroy()
        super.onDestroy()
    }

}