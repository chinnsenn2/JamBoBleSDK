package com.jianbao.jamboble

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.jianbao.jamboble.callbacks.UnSteadyValueCallBack
import com.jianbao.jamboble.data.BTData
import com.jianbao.jamboble.data.CholestenoneData
import com.jianbao.jamboble.data.QnUser
import com.jianbao.jamboble.data.UricAcidData
import com.jianbao.jamboble.device.BTDevice
import com.jianbao.jamboble.device.BTDeviceSupport
import com.jianbao.jamboble.fatscale.QnHelper

/**
 * Created by zhangmingyao
 * date: 2018/7/20.
 * Email:501863760@qq.com
 */
class BleHelper private constructor() : BaseBleHelper() {

    private var mQnUser: QnUser? = null
    private var mUnSteadyValueCallBack: UnSteadyValueCallBack? = null
    private var mDeviceType = BTDeviceSupport.DeviceType.FAT_SCALE

    private object Singleton {
        val instance = BleHelper()
    }

    companion object {
        @JvmStatic
        val instance = Singleton.instance
    }

    /**
     * 动态数据回调（仅体重支持
     */
    fun setUnSteadyValueCallBack(callback: UnSteadyValueCallBack) {
        this.mUnSteadyValueCallBack = callback
    }

    /**
     * 测量体重需传入用户参数
     */
    fun updateQnUser(qnUser: QnUser) {
        if (mDeviceType != BTDeviceSupport.DeviceType.FAT_SCALE) {
            mRegisterActReference?.get()?.also {
                showToast(it, "非体重测量无需设置用户参数")
            }
            return
        }
        this.mQnUser = qnUser
        mRegisterActReference?.get()?.also {
            QnHelper.getInstance(it).updateQNUser(qnUser)
        }
    }

    fun doSearch(activity: FragmentActivity?, deviceType: BTDeviceSupport.DeviceType) {
        this.mDeviceType = deviceType
        doSearch(activity)
    }

    protected fun connectDevice(device: BTDevice?, adress: String?): Boolean {
        scanLeDevice(false)
        return mBTControlManager?.connect(device, adress) ?: false
    }

    fun getConnectedDevice(): BTDevice? {
        return mBTControlManager?.connectDevice
    }

    fun getDeviceType() = mDeviceType

    override val TAG = javaClass.name

    override fun checkDevice(device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray) {
        device?.also { blDevice ->
            val btDevice = BTDeviceSupport.checkSupport(blDevice, mDeviceType)
            btDevice?.also { bd ->
                println("device.getAddress() = " + blDevice.address)
                onBTDeviceFound(blDevice)
                scanLeDevice(false)
                Log.i(TAG, "已找到设备，准备连接...")
                when {
                    BTDeviceSupport.isYolandaFatScale(bd) -> {
                        mBTControlManager?.connectDevice = bd
                        mRegisterActReference?.get()?.also {
                            it.runOnUiThread{
                                QnHelper.getInstance(it).also { helper ->
                                    helper.connectDevice(
                                        this,
                                        mQnUser!!,
                                        blDevice,
                                        bd,
                                        rssi,
                                        scanRecord
                                    )
                                }
                            }
                        }

                    }
                    else -> {
                        mBTControlManager?.connect(bd, blDevice.address)
                    }
                }
            }
        }
    }

    /**
     * 蓝牙回调
     * @param state BleState
     */
    override fun onBTStateChanged(state: BleState) {
        mDataCallback?.onBTStateChanged(state)
    }

    override fun onLocalBTEnabled(enabled: Boolean) {
        mDataCallback?.onLocalBTEnabled(enabled)
    }

    private var mLastDataFlag = ""

    override fun onBTDataReceived(btData: BTData?) {
        btData?.also {
            when (it) {
                is UricAcidData -> {
                    val lastTime: String =
                        it.mYear.toString() + it.mMonth.toString() + it.mday.toString() + it.mHour.toString() + it.mMinute.toString() + it.mUricAcid.toString()
                    if (lastTime != mLastDataFlag) {
                        mDataCallback?.onBTDataReceived(it)
                        mLastDataFlag = lastTime
                    }
                }
                is CholestenoneData -> {
                    val lastTime: String =
                        it.mYear.toString() + it.mMonth.toString() + it.mday.toString() + it.mHour.toString() + it.mMinute.toString() + it.cholestenone.toString()
                    if (lastTime != mLastDataFlag) {
                        mDataCallback?.onBTDataReceived(it)
                        mLastDataFlag = lastTime
                    }
                }
                else -> {
                    mDataCallback?.onBTDataReceived(it)
                }
            }

        }
    }

    fun onUnsteadyValue(value: Float) {
        mUnSteadyValueCallBack?.onUnsteadyValue(value)
    }

    override fun onBTDeviceFound(device: BluetoothDevice?) {
        mBleStatusCallback?.onBTDeviceFound(device)
    }

    override fun onNotification() {
        mBleStatusCallback?.onNotification()
    }

    override fun destroy() {
        mRegisterActReference?.get()?.also {
            QnHelper.getInstance(it).dispose()
        }
        super.destroy()
        //释放资源
        if (mBTControlManager != null) {
            mBTControlManager?.dispose()
            mBTControlManager = null
        }
        if (mLeScanCallback != null) {
            mLeScanCallback = null
        }
        if (mBleStatusCallback != null) {
            mBleStatusCallback = null
        }
    }


}