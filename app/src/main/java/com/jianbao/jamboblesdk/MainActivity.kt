package com.jianbao.jamboblesdk

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jianbao.jamboble.BleHelper
import com.jianbao.jamboble.callbacks.IBleStatusCallback.State.*
import com.jianbao.jamboble.data.BloodPressureData
import com.jianbao.jamboble.device.BTDeviceSupport

class MainActivity : AppCompatActivity() {
    private val mBleWeightHelper = BleHelper(this, BTDeviceSupport.DeviceType.BLOOD_PRESSURE)
    private val mTvStatus by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_status) }
    private val mTvValue by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_value) }
    private val mBtnOpenBle by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_open_ble) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBtnOpenBle.setOnClickListener {
            mBleWeightHelper.openBluetooth()
        }

//        mBleWeightHelper.setBloodPressCallBack(
//            object : BleBloodPressureCallback {
//                override fun onBTDataReceived(fatScaleData: BloodPressureData) {
//                    mTvValue.text = fatScaleData.toString()
//                }
//
//                override fun onBTStateChanged(state: Int) {
//                    println("MainActivity.onBTStateChanged $state")
//                    when (state) {
//                        NOT_FOUND -> {
//                            mTvStatus.text = "未找到设备"
//                        }
//                        SCAN_START -> {
//                            mTvStatus.text = "开始扫描..."
//                        }
//                        CONNECTED -> {
//                            mTvStatus.text = "连接设备成功"
//                        }
//                        TIMEOUT -> {
//                            mTvStatus.text = "超时"
//                        }
//                    }
//                }
//
//            })
    }
}
