package com.jianbao.jamboblesdk

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jianbao.jamboble.BleHelper
import com.jianbao.jamboble.BleState
import com.jianbao.jamboble.QnUser
import com.jianbao.jamboble.callbacks.BleDataCallback
import com.jianbao.jamboble.callbacks.UnSteadyValueCallBack
import com.jianbao.jamboble.data.BTData
import com.jianbao.jamboble.data.FatScaleData
import java.util.*

class WeightActivity : AppCompatActivity() {
    //初始化 blehelper
    private val mBleHelper by lazy(LazyThreadSafetyMode.NONE) { BleHelper.getWeightInstance(this) }

    private val mTvStatus by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_status) }
    private val mTvValue by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_value) }
    private val mBtnOpenBle by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_open_ble) }
    private val mTvValueRealtime by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_value_realtime) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight)

        title = "体重测量"

        mBtnOpenBle.setOnClickListener {
            mBleHelper.doReSearch()
        }

        //体重测量必须设置
        mBleHelper.updateQnUser(QnUser("1", "male", 180, Date()))

        //体重实时数据回调（仅支持体重测量
        mBleHelper.setUnSteadyValueCallBack(
            object : UnSteadyValueCallBack {
                override fun onUnsteadyValue(value: Float) {
                    mTvValueRealtime.setText("$value kg")
                }
            }
        )

        //通用数据回调
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
                    if (data is FatScaleData) {
                        mTvValue.text = data.toString()
                    }
                }

            })
    }

    override fun onDestroy() {
        //释放资源
        mBleHelper.destroy()
        super.onDestroy()
    }
}
