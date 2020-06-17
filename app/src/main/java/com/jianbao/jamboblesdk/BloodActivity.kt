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

class BloodActivity : AppCompatActivity() {

    private val mTvValue by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_value) }
    private val mTvStatus by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_status) }
    private val mBtnOpenBle by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_open_ble) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blood)
        title = "血压测量"
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
                            mBtnOpenBle.text = "停止扫描"
                            mTvStatus.text = "开始扫描..."
                        }
                        //连接成功
                        BleState.CONNECTED -> {
                            mTvStatus.text = "连接设备成功"
                        }
                        //长时间未搜索到设备
                        BleState.TIMEOUT -> {
                            mBtnOpenBle.text = "开始扫描"
                            mTvStatus.text = "超时"
                        }
                        BleState.CONNECT_FAILED -> {
                            mTvStatus.text = "连接失败"
                        }
                        BleState.CONNECTEING -> {
                            mTvStatus.text = "连接设备中"
                        }
                        BleState.DISCONNECT -> {
                            mTvStatus.text = "断开连接"
                        }
                    }
                }

                override fun onBTDataReceived(data: BTData?) {
                    if (data is BloodPressureData) {
                        /*
                        //收缩压
                        public int systolicPressur
                        //舒张压
                        public int diastolicPressu
                        //心率
                        public int heartRate;
                         */
                        mTvValue.text = data.toString()
                    }
                    //血糖数据
//                    if (data is BloodSugarData) {
                    //data.bloodSugar 血糖值 单位 Mmol
//                        mTvValue.text = data.toString()
//                    }
                    //尿酸数据
//                    if (data is UricAcidData) {
                    //data.mUricAcid 尿酸值 单位mmol/L
//                        mTvValue.text = data.toString()
//                    }
                }

                override fun onLocalBTEnabled(enabled: Boolean) {
                }

            }
        )

        mBtnOpenBle.setOnClickListener {
            JamBoBleHelper.instance.scanBloodPressureDevice()
//            JamBoBleHelper.instance.scanBloodSugarDevice()
//            JamBoBleHelper.instance.scanUricAcidDevice()
        }
    }

    override fun onDestroy() {
        //释放资源
        JamBoBleHelper.instance.destroy()
        super.onDestroy()
    }
}