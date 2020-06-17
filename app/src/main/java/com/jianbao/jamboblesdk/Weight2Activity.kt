package com.jianbao.jamboblesdk

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jianbao.fastble.JamBoBleHelper
import com.jianbao.jamboble.BleState
import com.jianbao.jamboble.callbacks.BleDataCallback
import com.jianbao.jamboble.callbacks.UnSteadyValueCallBack
import com.jianbao.jamboble.data.BTData
import com.jianbao.jamboble.data.FatScaleData
import com.jianbao.jamboble.data.QnUser
import java.util.*

class Weight2Activity : AppCompatActivity() {
    private val mTvStatus by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_status) }
    private val mTvValue by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_value) }
    private val mBtnOpenBle by lazy(LazyThreadSafetyMode.NONE) { findViewById<Button>(R.id.btn_open_ble) }
    private val mTvValueRealtime by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.tv_value_realtime) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight)

        title = "体重测量"


        JamBoBleHelper.instance.updateQnUser(QnUser("1", "male", 180, Date()))
        JamBoBleHelper.instance.setUnSteadyValueCallBack(
            object : UnSteadyValueCallBack {
                override fun onUnsteadyValue(value: Float) {
                    mTvValueRealtime.text = "$value kg"
                }
            }
        )

        JamBoBleHelper.instance.setBleDataCallBack(
            object : BleDataCallback {
                override fun onBTStateChanged(state: BleState) {
                    runOnUiThread {
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
                        }
                    }
                }

                override fun onBTDataReceived(data: BTData?) {
                    println("WeightActivity.onBTDataReceived ... ${Thread.currentThread().name}")
                    if (data is FatScaleData) {
                        mTvValue.text = data.toString()
                    }
                    /*
                    //体重
                    public float weight;
                    //脂肪
                    public float fat;
                    //水分
                    public float tbw;
                    //BMI
                    public float bmi;
                    //皮下脂肪率
                    public float subcutaneousfat;
                    //内脏脂肪等级
                    public float viscerallevel;
                    //骨骼肌率
                    public float skeletal;
                    //骨量
                    public float bonemass;
                    //蛋白质含量
                    public float proteins;
                    //基础代谢
                    public float metabolic;
                    //体年龄
                    public float bodyage;
                    //分数
                    public float score;
                    //体型
                    public String bodyshape;
                     */
                }

                override fun onLocalBTEnabled(enabled: Boolean) {

                }

            }
        )

        mBtnOpenBle.apply {
            setOnClickListener {
                when (text) {
                    "开始扫描" -> {
                        JamBoBleHelper.instance.scanFatScaleDevice()
                    }
                    "停止扫描" -> {
                        JamBoBleHelper.instance.stopScan()
                    }
                }
            }
        }
    }
}
