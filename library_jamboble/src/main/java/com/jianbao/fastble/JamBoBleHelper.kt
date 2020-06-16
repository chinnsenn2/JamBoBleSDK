package com.jianbao.fastble

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.widget.Toast
import com.jianbao.fastble.callback.BleGattCallback
import com.jianbao.fastble.callback.BleNotifyCallback
import com.jianbao.fastble.callback.BleScanCallback
import com.jianbao.fastble.data.BleDevice
import com.jianbao.fastble.exception.BleException
import com.jianbao.fastble.scan.BleScanRuleConfig
import com.jianbao.jamboble.BleState
import com.jianbao.jamboble.BuildConfig
import com.jianbao.jamboble.callbacks.BleDataCallback
import com.jianbao.jamboble.callbacks.IBleStatusCallback
import com.jianbao.jamboble.callbacks.UnSteadyValueCallBack
import com.jianbao.jamboble.data.BTData
import com.jianbao.jamboble.data.CholestenoneData
import com.jianbao.jamboble.data.QnUser
import com.jianbao.jamboble.data.UricAcidData
import com.jianbao.jamboble.device.BTDevice
import com.jianbao.jamboble.device.BTDeviceSupport
import com.jianbao.jamboble.device.oximeter.OxiMeterHelper
import com.jianbao.jamboble.device.oximeter.OximeterDevice
import com.jianbao.jamboble.fatscale.JamboQnHelper
import com.jianbao.jamboble.utils.LogUtils
import com.yolanda.health.qnblesdk.out.QNBleApi
import java.lang.ref.WeakReference

class JamBoBleHelper {
    var mDataCallback: BleDataCallback? = null
    var mBleStatusCallback: IBleStatusCallback? = null
    var mUnSteadyValueCallBack: UnSteadyValueCallBack? = null

    private val mJamboBleScanCallback by lazy {
        JamboBleScanCallback(this)
    }

    private object Singleton {
        val instance = JamBoBleHelper()
    }

    companion object {
        @JvmStatic
        val instance = Singleton.instance

        private val fatScaleBleScanRuleConfig = BleScanRuleConfig.Builder()
            .setDeviceName(
                false,
                "Yolanda-CS20F2",
                "QN-Scale",
                "Yolanda-CS20G2",
                "Yolanda-CS10C1"
            ) // 只扫描指定广播名的设备，可选
            .setAutoConnect(true) // 连接时的autoConnect参数，可选，默认false
            .setScanTimeOut(30000) // 扫描超时时间，可选，默认10秒F
            .build()

        private val bloodPressureBleScanRuleConfig = BleScanRuleConfig.Builder()
            .setDeviceName(
                false,
                "Technaxx BP",
                "Yuwell BP-YE680A",
                "ClinkBlood",
                "Yuwell BloodPressure",
                "SerialCom",
                "BLT_WBP"
            ) // 只扫描指定广播名的设备，可选
            .setAutoConnect(true) // 连接时的autoConnect参数，可选，默认false
            .setScanTimeOut(30000) // 扫描超时时间，可选，默认10秒
            .build()

        private val bloodSugarBleScanRuleConfig = BleScanRuleConfig.Builder()
            .setDeviceName(
                false,
                "Sinocare",
                "Yuwell Glucose",
                "BDE_WEIXIN_TTM",
                "c14d2c0a-401f-b7a9-841f-e2e93b80f631",
                "BeneCheck"
            ) // 只扫描指定广播名的设备，可选
            .setAutoConnect(true) // 连接时的autoConnect参数，可选，默认false
            .setScanTimeOut(30000) // 扫描超时时间，可选，默认10秒
            .build()

        private val uricAcidBleScanRuleConfig = BleScanRuleConfig.Builder()
            .setDeviceName(
                false,
                "Sinocare",
                "Yuwell Glucose",
                "BDE_WEIXIN_TTM",
                "c14d2c0a-401f-b7a9-841f-e2e93b80f631",
                "BeneCheck"
            ) // 只扫描指定广播名的设备，可选
            .setAutoConnect(true) // 连接时的autoConnect参数，可选，默认false
            .setScanTimeOut(30000) // 扫描超时时间，可选，默认10秒
            .build()

        private val oxiMeterBleScanRuleConfig = BleScanRuleConfig.Builder()
            .setDeviceName(
                true,
                "PC-60NW-1", "POD", " POD", "PC-68B", "PC-60F"
            ) // 只扫描指定广播名的设备，可选
            .setAutoConnect(true) // 连接时的autoConnect参数，可选，默认false
            .setScanTimeOut(30000) // 扫描超时时间，可选，默认10秒
            .build()
    }

    fun init(app: Application) {
        BleManager.getInstance().init(app)
        if (BleManager.getInstance().isSupportBle) {
            initQnSdk(app)
        }
        BleManager.getInstance().apply {
            enableLog(BuildConfig.DEBUG)
            reConnectCount = 1
            connectOverTime = 3000
            operateTimeout = 5_000
        }
        JamboQnHelper.instance.init(app)
    }

    //初始化轻牛sdk
    private var mQnInitTime = 0

    private fun initQnSdk(app: Application) {
        val encryptPath = "file:///android_asset/hzyb20160314175503.qn"
        QNBleApi.getInstance(app)
            .initSdk(
                "hzyb20160314175503", encryptPath
            ) { i, s ->
                if (i != 0) {
                    if (mQnInitTime < 3) {
                        mQnInitTime += 1
                        initQnSdk(app)
                    }
                }
            }
    }

    /**
     * 数据回调
     */
    fun setDataCallBack(callback: BleDataCallback) {
        this.mDataCallback = callback
    }

    /**
     * 蓝牙状态回调
     */
    fun setBleStatusCallback(callback: IBleStatusCallback) {
        this.mBleStatusCallback = callback
    }

    /**
     * 扫描体脂称设备
     */
    fun scanFatScaleDevice() {
        if (!JamboQnHelper.instance.isInitQnUser()) {
            showToast("请调用 updateQnUser 初始化用户数据")
            return
        }
        BleManager.getInstance().initScanRule(fatScaleBleScanRuleConfig)
        mJamboBleScanCallback.setType(BTDeviceSupport.DeviceType.FAT_SCALE)
        BleManager.getInstance().scan(mJamboBleScanCallback)
    }

    /**
     * 扫描血压设备
     */
    fun scanBloodPressureDevice() {
        BleManager.getInstance().initScanRule(bloodPressureBleScanRuleConfig)
        scan(BTDeviceSupport.DeviceType.FAT_SCALE)
    }

    fun scanBloodSugarDevice() {
        BleManager.getInstance().initScanRule(bloodSugarBleScanRuleConfig)
        scan(BTDeviceSupport.DeviceType.BLOOD_SUGAR)
    }

    fun scanUricAcidDevice() {
        BleManager.getInstance().initScanRule(uricAcidBleScanRuleConfig)
        scan(BTDeviceSupport.DeviceType.URIC_ACID)
    }

    fun scanOxiMeterDevice() {
        BleManager.getInstance().initScanRule(oxiMeterBleScanRuleConfig)
        scan(BTDeviceSupport.DeviceType.OXIMETER)
    }

    fun scan(type: BTDeviceSupport.DeviceType) {
        mJamboBleScanCallback.setType(type)
        BleManager.getInstance().scan(mJamboBleScanCallback)
    }

    /**
     * 测量体重需传入用户参数
     */
    fun updateQnUser(qnUser: QnUser) {
        JamboQnHelper.instance.updateQNUser(qnUser)
    }

    fun showToast(msg: String?) {
        Toast.makeText(BleManager.getInstance().context, msg, Toast.LENGTH_SHORT).show()
    }

    /**
     * 蓝牙回调
     * @param state BleState
     */
    fun onBTStateChanged(state: BleState) {
        mDataCallback?.onBTStateChanged(state)
    }

    fun onLocalBTEnabled(enabled: Boolean) {
        mDataCallback?.onLocalBTEnabled(enabled)
    }

    fun onBTDeviceFound(device: BluetoothDevice?) {
        mBleStatusCallback?.onBTDeviceFound(device)
    }

    /**
     * 动态数据回调（仅体重支持
     */
    fun setUnSteadyValueCallBack(callback: UnSteadyValueCallBack) {
        this.mUnSteadyValueCallBack = callback
    }

    fun onNotification() {
        mBleStatusCallback?.onNotification()
    }

    private var mLastDataFlag = ""

    fun onBTDataReceived(btData: BTData?) {
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
                    mDataCallback?.onBTDataReceived(it)
                    mLastDataFlag = dataTime
                }
            } ?: also { _ ->
                mDataCallback?.onBTDataReceived(it)
            }

        }
    }

    fun onUnsteadyValue(value: Float) {
        mUnSteadyValueCallBack?.onUnsteadyValue(value)
    }

    class JamboBleScanCallback(
        helper: JamBoBleHelper,
        type: BTDeviceSupport.DeviceType = BTDeviceSupport.DeviceType.FAT_SCALE
    ) :
        BleScanCallback() {
        private val mWeakReference = WeakReference(helper)
        private var mType = type

        private val mJamboBleGattCallback by lazy {
            mWeakReference.get()?.let { JamboBleGattCallback(it) }
        }

        fun setType(type: BTDeviceSupport.DeviceType) {
            this.mType = type
        }

        override fun onScanFinished(scanResultList: MutableList<BleDevice>?) {

        }

        override fun onScanStarted(success: Boolean) {
            mWeakReference.get()?.onBTStateChanged(BleState.SCAN_START)
        }

        override fun onScanning(bleDevice: BleDevice?) {
            BTDeviceSupport.checkSupport(
                bleDevice?.device,
                mType
            )?.also { btDevice ->
                mWeakReference.get()?.also {
                    mJamboBleGattCallback?.setBTDevice(btDevice)
                    when (mType) {
                        BTDeviceSupport.DeviceType.FAT_SCALE -> {
                            BleManager.getInstance().cancelScan()
                            JamboQnHelper.instance.connectDevice(it, bleDevice)
                        }
                        BTDeviceSupport.DeviceType.BLOOD_PRESSURE -> {
                            BleManager.getInstance().connect(bleDevice, mJamboBleGattCallback)
                        }
                        BTDeviceSupport.DeviceType.BLOOD_SUGAR -> {
                            BleManager.getInstance().connect(bleDevice, mJamboBleGattCallback)
                        }
                        BTDeviceSupport.DeviceType.URIC_ACID -> {
                            BleManager.getInstance().connect(bleDevice, mJamboBleGattCallback)
                        }
                        BTDeviceSupport.DeviceType.OXIMETER -> {
                            BleManager.getInstance().connect(bleDevice, mJamboBleGattCallback)
                        }
                        BTDeviceSupport.DeviceType.SLEEPLIGHT -> TODO()
                        BTDeviceSupport.DeviceType.THREEONONE -> TODO()
                        BTDeviceSupport.DeviceType.FETAL_HEART -> TODO()
                    }
                }
            }
        }

    }

    class JamboBleGattCallback(helper: JamBoBleHelper) :
        BleGattCallback() {
        private val mWeakReference = WeakReference(helper)
        private var mBleDevice: BleDevice? = null
        private var mBTDevice: BTDevice? = null

        fun setBleDevice(bleDevice: BleDevice?) {
            this.mBleDevice = bleDevice
        }

        fun setBTDevice(btDevice: BTDevice?) {
            this.mBTDevice = btDevice
        }

        override fun onStartConnect() {
            mWeakReference.get()?.onBTStateChanged(BleState.CONNECTEING)
        }

        override fun onDisConnected(
            isActiveDisConnected: Boolean,
            device: BleDevice?,
            gatt: BluetoothGatt?,
            status: Int
        ) {
            BleManager.getInstance().stopNotify(
                mBleDevice,
                mBTDevice?.serviceUUID,
                mBTDevice?.notifyCharacterUUID,
                false
            )
            mWeakReference.get()?.onBTStateChanged(BleState.DISCONNECT)
        }

        override fun onConnectSuccess(bleDevice: BleDevice?, gatt: BluetoothGatt?, status: Int) {
            mWeakReference.get()?.onBTStateChanged(BleState.CONNECTED)
            BleManager.getInstance().cancelScan()
            setBleDevice(bleDevice)
            mBTDevice?.also {
                BleManager.getInstance().notify(bleDevice,
                    it.serviceUUID,
                    it.notifyCharacterUUID,
                    object : BleNotifyCallback() {
                        override fun onCharacteristicChanged(data: ByteArray?) {
                            data?.also { d ->
                                val btData: BTData? = it.paserData(d)
                                mWeakReference.get()?.onBTDataReceived(btData)
                            }
                        }

                        override fun onNotifyFailure(exception: BleException?) {
                            LogUtils.e(exception?.description)
                        }

                        override fun onNotifySuccess() {
                            if (it is OximeterDevice) {
                                //初始化inputStream和outputStream
                                val mOxiMeterHelper = OxiMeterHelper(bleDevice, it)
                                it.oximeterHelper = mOxiMeterHelper
                                mWeakReference.get()?.onNotification()
                            }
                        }

                    })

            }

        }

        override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
            mWeakReference.get()?.onBTStateChanged(BleState.CONNECT_FAILED)
            LogUtils.e(exception?.description)
        }

    }

    fun destroy() {
        mDataCallback = null
        mBleStatusCallback = null
        mUnSteadyValueCallBack = null
        BleManager.getInstance().destroy()
    }

}