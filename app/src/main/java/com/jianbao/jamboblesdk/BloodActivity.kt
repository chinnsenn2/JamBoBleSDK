package com.jianbao.jamboblesdk

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jianbao.jamboble.BleHelper
import com.jianbao.jamboble.BleState
import com.jianbao.jamboble.callbacks.BleDataCallback
import com.jianbao.jamboble.data.BTData
import com.jianbao.jamboble.data.BloodPressureData

class BloodActivity : AppCompatActivity() {
    //初始化 血压 blehelper
    private val mBleHelper by lazy(LazyThreadSafetyMode.NONE) {
        BleHelper.getBloodPressureInstance(this)
       /* //初始化 血糖 blehelper
         BleHelper.getBloodSugarInstance(this)
        //初始化 尿酸 blehelper
         BleHelper.getUricAcidInstance(this)*/
    }

    private val mTvValue by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_value) }
    private val mTvStatus by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_status) }
    private val mBtnOpenBle by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_open_ble) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blood)

        title = "血压测量"

        mBleHelper.setDataCallBack(
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
                    if (data is BloodPressureData) {
                        mTvValue.text = data.toString()
                    }
                    //血糖数据
//                    if (data is BloodSugarData) {
//                        mTvValue.text = data.toString()
//                    }
                    //尿酸数据
//                    if (data is UricAcidData) {
//                        mTvValue.text = data.toString()
//                    }
                }

            }
        )

        mBtnOpenBle.setOnClickListener {
            mBleHelper.doReSearch()
        }
    }

    override fun onDestroy() {
        //释放资源
        mBleHelper.destroy()
        super.onDestroy()
    }
}