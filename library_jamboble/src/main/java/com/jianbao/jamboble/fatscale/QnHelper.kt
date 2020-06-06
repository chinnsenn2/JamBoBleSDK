package com.jianbao.jamboble.fatscale

import android.bluetooth.BluetoothDevice
import com.jianbao.jamboble.App
import com.jianbao.jamboble.BleHelper
import com.jianbao.jamboble.QnUser
import com.jianbao.jamboble.callbacks.IBleStatusCallback
import com.jianbao.jamboble.data.FatScaleData
import com.jianbao.jamboble.device.BTDevice
import com.jianbao.jamboble.utils.LogUtils
import com.yolanda.health.qnblesdk.constant.QNIndicator
import com.yolanda.health.qnblesdk.listener.QNBleConnectionChangeListener
import com.yolanda.health.qnblesdk.listener.QNScaleDataListener
import com.yolanda.health.qnblesdk.out.*
import java.lang.ref.WeakReference

class QnHelper private constructor() {

    private var mQNBleConnectionChangeListener = JamboQNBleConnectionChangeListener(this)
    private var mQNDataListener = JamboQNScaleDataListener(this)
    private var mConnectTime = 0
    private var mBleHelper: BleHelper? = null
    private var mBtDevice: BTDevice? = null
    private var mQNUser: QNUser? = null

    companion object {
        @JvmStatic
        val instance = SingleTonHolder.instance
    }

    init {
        QNBleApi.getInstance(App.context).also {
            it.setBleConnectionChangeListener(mQNBleConnectionChangeListener)
            it.setDataListener(mQNDataListener)
        }
    }

    object SingleTonHolder {
        val instance: QnHelper = QnHelper()
    }

    fun connectDevice(
        bleHelper: BleHelper,
        qNUser: QnUser,
        device: BluetoothDevice,
        btDevice: BTDevice,
        rssi: Int,
        scanRecord: ByteArray
    ) {
        this.mBleHelper = bleHelper
        this.mBtDevice = btDevice
        QNBleApi.getInstance(App.context).also {
            mQNUser = it.buildUser(
                qNUser.user_id,
                qNUser.height,
                qNUser.gender,
                qNUser.birthday
            ) { _, _ -> }
        }
        QNBleApi.getInstance(App.context).also {
            it.buildDevice(device, rssi, scanRecord) { _, _ -> }
                .also { qd ->
                    mQNUser?.let { qnUser -> it.connectDevice(qd, qnUser) { _, _ -> } }
                        ?: also { qnHelper ->
                            qnHelper.mBleHelper?.scanLeDevice(true)
                        }
                }
        }
    }

    fun updateQNUser(qNUser: QnUser) {
        QNBleApi.getInstance(App.context).also {
            mQNUser = it.buildUser(
                qNUser.user_id,
                qNUser.height,
                qNUser.gender,
                qNUser.birthday
            ) { _, _ -> }
        }
    }

    class JamboQNBleConnectionChangeListener(qnHelper: QnHelper) : QNBleConnectionChangeListener {
        private val weakReference = WeakReference(qnHelper)

        override fun onConnecting(qnBleDevice: QNBleDevice) {

        }

        override fun onConnected(qnBleDevice: QNBleDevice) {
            weakReference.get()?.also {
                it.mBleHelper?.onBTStateChanged(IBleStatusCallback.State.CONNECTED)
            }

        }

        override fun onServiceSearchComplete(qnBleDevice: QNBleDevice) {}
        override fun onDisconnecting(qnBleDevice: QNBleDevice) {}
        override fun onDisconnected(qnBleDevice: QNBleDevice) {}
        override fun onConnectError(qnBleDevice: QNBleDevice, i: Int) {

            weakReference.get()?.also {
                if (it.mConnectTime < 3) {
                    it.mBleHelper?.scanLeDevice(true)
                    ++it.mConnectTime
                }
            }
        }

    }

    class JamboQNScaleDataListener(qnHelper: QnHelper) : QNScaleDataListener {
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
                it.mBleHelper?.onUnsteadyValue(v.toFloat())
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
                    /*if (data.getType() == QNIndicator.TYPE_MUSCLE){
                        fatData.mus = data.getValue();
                    }else */if (data.type == QNIndicator.TYPE_BMI) {
                        fatData.bmi = data.value.toFloat()
                    } else if (data.type == QNIndicator.TYPE_MUSCLE) {
                        fatData.skeletal = data.value.toFloat()
                    } else if (data.type == QNIndicator.TYPE_PROTEIN) {
                        fatData.proteins = data.value.toFloat()
                    } else if (data.type == QNIndicator.TYPE_WEIGHT) {
                        fatData.weight = data.value.toFloat()
                    } else if (data.type == QNIndicator.TYPE_BMR) {
                        fatData.metabolic = data.value.toFloat()
                    } else if (data.type == QNIndicator.TYPE_SUBFAT) {
                        fatData.subcutaneousfat = data.value.toFloat()
                    } else if (data.type == QNIndicator.TYPE_VISFAT) {
                        fatData.viscerallevel = data.value.toFloat()
                    } else if (data.type == QNIndicator.TYPE_WATER) {
                        fatData.tbw = data.value.toFloat()
                    } else if (data.type == QNIndicator.TYPE_BODYFAT) {
                        fatData.fat = data.value.toFloat()
                    } else if (data.type == QNIndicator.TYPE_BONE) {
                        fatData.bonemass = data.value.toFloat()
                    } else if (data.type == QNIndicator.TYPE_BODY_AGE) {
                        fatData.bodyage = data.value.toFloat()
                    } else if (data.type == QNIndicator.TYPE_SCORE) {
                        fatData.score = data.value.toFloat()
                    } else if (data.type == QNIndicator.TYPE_BODY_SHAPE) {
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
                    i++
                }
            }

            weakReference.get()?.also {
                fatData.deviceID = it.mBtDevice?.btDeviceID!!
                it.mBleHelper?.onBTDataReceived(fatData)
            }
        }

    }

    fun dispose() {
        mBleHelper = null
        mBtDevice = null
        mQNUser = null
    }
}