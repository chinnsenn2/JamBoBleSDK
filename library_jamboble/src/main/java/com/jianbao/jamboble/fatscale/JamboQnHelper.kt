package com.jianbao.jamboble.fatscale

import android.app.Application
import android.widget.Toast
import com.jianbao.fastble.BleManager
import com.jianbao.fastble.JamBoHelper
import com.jianbao.fastble.data.BleDevice
import com.jianbao.jamboble.BleState
import com.jianbao.jamboble.data.FatScaleData
import com.jianbao.jamboble.data.QnUser
import com.jianbao.jamboble.utils.LogUtils
import com.yolanda.health.qnblesdk.constant.QNIndicator
import com.yolanda.health.qnblesdk.listener.QNBleConnectionChangeListener
import com.yolanda.health.qnblesdk.listener.QNScaleDataListener
import com.yolanda.health.qnblesdk.out.*
import java.lang.ref.WeakReference

class JamboQnHelper {
    private var context: Application? = null
    private var mQNUser: QNUser? = null
    private var mJamBoBleHelper: JamBoHelper? = null
    private var mQNBleConnectionChangeListener = JamboQNBleConnectionChangeListener(this)
    private var mQNDataListener = JamboQNScaleDataListener(this)

    private object Singleton {
        val instance = JamboQnHelper()
    }

    companion object {
        @JvmStatic
        val instance = Singleton.instance
    }

    fun init(app: Application) {
        this.context = app
        QNBleApi.getInstance(context).also {
            it.setBleConnectionChangeListener(mQNBleConnectionChangeListener)
            it.setDataListener(mQNDataListener)
        }
    }

    fun connectDevice(
        helper: JamBoHelper,
        bleDevice: BleDevice?
    ) {
        this.mJamBoBleHelper = helper
        this.mQNUser?.also { user ->
            QNBleApi.getInstance(context).also {
                bleDevice?.also { bd ->
                    it.buildDevice(bd.device, bd.rssi, bd.scanRecord) { _, _ -> }
                        .also { qd ->
                            it.connectDevice(qd, user) { _, _ -> }
                        }
                }
            }
        }?:also {
            Toast.makeText(BleManager.getInstance().context, "请初始化用户数据", Toast.LENGTH_SHORT).show()
        }

    }

    fun updateQNUser(qNUser: QnUser) {
        this.mQNUser = QNBleApi.getInstance(context).buildUser(
            qNUser.user_id,
            qNUser.height,
            qNUser.gender,
            qNUser.birthday
        ) { _, _ -> }
    }

    fun isInitQnUser(): Boolean {
        return (this.mQNUser != null)
    }

    class JamboQNBleConnectionChangeListener(qnHelper: JamboQnHelper) : QNBleConnectionChangeListener {
        private var mConnectTime = 0
        private val weakReference = WeakReference(qnHelper)

        override fun onConnecting(qnBleDevice: QNBleDevice) {

        }

        override fun onConnected(qnBleDevice: QNBleDevice) {
            weakReference.get()?.also {
                it.mJamBoBleHelper?.onBTStateChanged(BleState.CONNECTED)
            }
        }

        override fun onServiceSearchComplete(qnBleDevice: QNBleDevice) {}
        override fun onDisconnecting(qnBleDevice: QNBleDevice) {}
        override fun onDisconnected(qnBleDevice: QNBleDevice) {}
        override fun onConnectError(qnBleDevice: QNBleDevice, i: Int) {

            weakReference.get()?.also {
                if (mConnectTime < 3) {
                    it.mJamBoBleHelper?.scanFatScaleDevice()
                    ++mConnectTime
                }
            }
        }

    }

    class JamboQNScaleDataListener(qnHelper: JamboQnHelper) : QNScaleDataListener {
        private val weakReference = WeakReference(qnHelper)

        override fun onScaleStateChange(p0: QNBleDevice?, p1: Int) {

        }

        override fun onGetStoredScale(
            p0: QNBleDevice?,
            p1: MutableList<QNScaleStoreData>?
        ) {
        }

        override fun onGetElectric(p0: QNBleDevice?, p1: Int) {
        }

        override fun onGetUnsteadyWeight(
            qnBleDevice: QNBleDevice,
            v: Double
        ) {
            weakReference.get()?.also {
                it.mJamBoBleHelper?.onUnsteadyValue(v.toFloat())
            }
        }

        override fun onGetScaleData(
            qnBleDevice: QNBleDevice,
            qnScaleData: QNScaleData
        ) {
            LogUtils.d("onReceivedData: ")
            val list =
                qnScaleData.allItem
            val fatData = FatScaleData()
            if (list != null) {
                //StringBuilder builder = new StringBuilder();
                var i = 0
                val size = list.size
                while (i < size) {
                    val data = list[i]
                    when (data.type) {
                        QNIndicator.TYPE_BMI -> {
                            fatData.bmi = data.value.toFloat()
                        }
                        QNIndicator.TYPE_MUSCLE -> {
                            fatData.skeletal = data.value.toFloat()
                        }
                        QNIndicator.TYPE_PROTEIN -> {
                            fatData.proteins = data.value.toFloat()
                        }
                        QNIndicator.TYPE_WEIGHT -> {
                            fatData.weight = data.value.toFloat()
                        }
                        QNIndicator.TYPE_BMR -> {
                            fatData.metabolic = data.value.toFloat()
                        }
                        QNIndicator.TYPE_SUBFAT -> {
                            fatData.subcutaneousfat = data.value.toFloat()
                        }
                        QNIndicator.TYPE_VISFAT -> {
                            fatData.viscerallevel = data.value.toFloat()
                        }
                        QNIndicator.TYPE_WATER -> {
                            fatData.tbw = data.value.toFloat()
                        }
                        QNIndicator.TYPE_BODYFAT -> {
                            fatData.fat = data.value.toFloat()
                        }
                        QNIndicator.TYPE_BONE -> {
                            fatData.bonemass = data.value.toFloat()
                        }
                        QNIndicator.TYPE_BODY_AGE -> {
                            fatData.bodyage = data.value.toFloat()
                        }
                        QNIndicator.TYPE_SCORE -> {
                            fatData.score = data.value.toFloat()
                        }
                        QNIndicator.TYPE_BODY_SHAPE -> {
                            val bodyshapes =
                                arrayOf(
                                    "未知体型",
                                    "隐形肥胖型",
                                    "运动不足型",
                                    "偏瘦型",
                                    "标准型",
                                    "偏瘦肌肉型",
                                    "肥胖型",
                                    "偏胖型",
                                    "标准肌肉型",
                                    "非常肌肉型"
                                )
                            fatData.bodyshape = bodyshapes[data.value.toInt()]
                        }
                    }
                    i++
                }
            }

            weakReference.get()?.also {
                it.mJamBoBleHelper?.onBTDataReceived(fatData)
            }
        }

    }

}