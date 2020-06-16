package com.jianbao.jamboblesdk

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jianbao.fastble.JamBoBleHelper
import com.jianbao.jamboble.BleState
import com.jianbao.jamboble.callbacks.BleDataCallback
import com.jianbao.jamboble.data.BTData
import com.jianbao.jamboble.data.BloodPressureData
import com.jianbao.jamboble.data.BloodSugarData
import com.jianbao.jamboble.data.CholestenoneData

class BloodThreeOnOneActivity : AppCompatActivity() {
    private val mTvStatus by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_status) }
    private val mTvDataTitle by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_data_title) }
    private val mTvValue by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_value) }
    private val mBtnOpenBle by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_open_ble) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blood_three_on_one)

        JamBoBleHelper.instance.setBleDataCallBack(
            object : BleDataCallback {
                override fun onBTStateChanged(state: BleState) {
                    when (state) {
                        //未开启蓝牙
                        BleState.NOT_FOUND -> {
                            mTvStatus.text = "请打开蓝牙"
                        }
                        //正在扫描
                        BleState.SCAN_START -> {
                            mTvStatus.text = "开始扫描..."
                        }
                        //连接成功
                        BleState.CONNECTED -> {
                            mTvStatus.text = "连接设备成功"
                        }
                        //长时间未搜索到设备
                        BleState.TIMEOUT -> {
                            mTvStatus.text = "超时"
                        }
                    }
                }

                override fun onBTDataReceived(data: BTData?) {
                    when (data) {
                        is BloodPressureData -> {
                            mTvDataTitle.text = "血压数据结果"
                            mTvValue.text = data.toString()
                        }
                        is BloodSugarData -> {
                            mTvDataTitle.text = "血糖数据结果"
                            mTvValue.text = data.toString()
                        }
                        is CholestenoneData -> {
                            mTvDataTitle.text = "胆固醇数据结果"
                            mTvValue.text = data.toString()
                        }
                    }
                }

                override fun onLocalBTEnabled(enabled: Boolean) {
                    //蓝牙授权失败
                }

            }
        )

        mBtnOpenBle.setOnClickListener {
            JamBoBleHelper.instance.scanThreeOnOneDevice()
        }

    }

    override fun onDestroy() {
        //释放资源
        JamBoBleHelper.instance.destroy()
        super.onDestroy()
    }
}