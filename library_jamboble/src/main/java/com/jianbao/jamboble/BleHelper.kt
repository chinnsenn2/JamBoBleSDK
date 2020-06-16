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
    private var mIsAutoConnect = true

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

    /**
     * 搜索设备不自动链接
     */
    fun doSearch(activity: FragmentActivity?, deviceType: BTDeviceSupport.DeviceType) {
        this.mDeviceType = deviceType
        this.mIsAutoConnect = false
        doSearch(activity)
    }

    /**
     * 搜索设备自动连接
     */
    fun doSearchAutoConnect(activity: FragmentActivity?, deviceType: BTDeviceSupport.DeviceType) {
        this.mDeviceType = deviceType
        this.mIsAutoConnect = true
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
            println("blDevice = [${blDevice.name}]")
            val btDevice = BTDeviceSupport.checkSupport(blDevice, mDeviceType)
            btDevice?.also { bd ->
                println("device.getAddress() = " + blDevice.address)
                onBTDeviceFound(blDevice)
                scanLeDevice(false)
                if (mIsAutoConnect) {
                    Log.i(TAG, "已找到设备，准备连接...")
                    when {
                        BTDeviceSupport.isYolandaFatScale(bd) -> {
                            mBTControlManager?.connectDevice = bd
                            mRegisterActReference?.get()?.also {
                                it.runOnUiThread {
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
    }

    /**
     * 蓝牙回调
     * @param state BleState
     */
    override fun onBTStateChanged(state: BleState) {
        mDataCallbackList.forEach {
            it.onBTStateChanged(state)
        }
    }

    override fun onLocalBTEnabled(enabled: Boolean) {
        mDataCallbackList.forEach {
            it.onLocalBTEnabled(enabled)
        }
    }

    override fun onBTDeviceFound(device: BluetoothDevice?) {
        mBleStatusCallbackList.forEach {
            it.onBTDeviceFound(device)
        }
    }

    override fun onNotification() {
        mBleStatusCallbackList.forEach {
            it.onNotification()
        }
    }

    private var mLastDataFlag = ""

    override fun onBTDataReceived(btData: BTData?) {
        btData?.also {
            val lastTime: String? = when (it) {
                is UricAcidData -> {
                    it.mYear.toString() + it.mMonth.toString() + it.mday.toString() + it.mHour.toString() + it.mMinute.toString() + it.mUricAcid.toString()
                }
                is CholestenoneData -> {
                    it.mYear.toString() + it.mMonth.toString() + it.mday.toString() + it.mHour.toString() + it.mMinute.toString() + it.cholestenone.toString()
                }
                else -> null
            }
            lastTime?.also { dataTime ->
                if (dataTime != mLastDataFlag) {
                    mDataCallbackList.forEach { callback ->
                        callback.onBTDataReceived(it)
                    }
                    mLastDataFlag = dataTime
                }
            } ?: also { _ ->
                mDataCallbackList.forEach { callback ->
                    callback.onBTDataReceived(it)
                }
            }

        }
    }

    fun onUnsteadyValue(value: Float) {
        mUnSteadyValueCallBack?.onUnsteadyValue(value)
    }

    override fun destroy() {
        mRegisterActReference?.get()?.also {
            QnHelper.getInstance(it).dispose()
        }
        super.destroy()
    }


}