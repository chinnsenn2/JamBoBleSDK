package com.jianbao.jamboble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.dovar.dtoast.DToast
import com.jianbao.jamboble.callbacks.BleDataCallback
import com.jianbao.jamboble.callbacks.IBleStatusCallback
import com.jianbao.jamboble.data.BTData
import com.jianbao.jamboble.utils.permissions.PermissionsUtil
import java.lang.ref.WeakReference
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseBleHelper {
    abstract val TAG: String

    abstract fun checkDevice(
        device: BluetoothDevice?,
        rssi: Int,
        scanRecord: ByteArray
    )

    /**
     * 蓝牙回调
     * @param state BleState
     */
    abstract fun onBTStateChanged(state: BleState)

    /**
     * 蓝牙可用性
     */
    abstract fun onLocalBTEnabled(enabled: Boolean)

    /**
     * 蓝牙数据回调
     */
    abstract fun onBTDataReceived(btData: BTData?)

    /**
     * 找到设备
     */
    abstract fun onBTDeviceFound(device: BluetoothDevice?)

    abstract fun onNotification()

    companion object {
        const val MESSAGE_SCAN = 1000
        const val MESSAGE_TIMEOUT = 1001
        private const val REQUEST_ENABLE_BT = 0x2
    }

    val mLockAutoScan = Any()
    var mScanning = false
    var mRegisterActReference: WeakReference<FragmentActivity>? = null
    var mBluetoothAdapter: BluetoothAdapter? = null //蓝牙适配器，单例唯一 = false
    var mBTControlManager: BTControlManager? = null
    var mBluetoothStateReceiver: BluetoothStateReceiver? = null
    var mNewScanCallback: ScanCallback? = null
    var mLeScanCallback: BluetoothAdapter.LeScanCallback? = null

    var mBleStatusCallback: IBleStatusCallback? = null
    var mDataCallback: BleDataCallback? = null

    private var mRegistered = AtomicBoolean(false)
    private var mExecutor = Executors.newCachedThreadPool()
    private val mBTControlListener by lazy {
        BTListener(this)
    }
    private val mScanRunnable: ScanRunnable by lazy(LazyThreadSafetyMode.NONE) {
        ScanRunnable(this)
    }
    val mHandler: ScanHandler by lazy {
        ScanHandler(this)
    }

    val isEnable: Boolean
        get() = mBluetoothAdapter?.isEnabled ?: false

    /**
     * 断开蓝牙时是否需要自动重新扫描
     *
     * @param autoScanWhenDisconnected
     */
    private var isAutoScanWhenDisconnected: Boolean = true
        get() {
            synchronized(mLockAutoScan) { return field }
        }
        set(autoScanWhenDisconnected) {
            synchronized(mLockAutoScan) { field = autoScanWhenDisconnected }
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

    fun doSearch(activity: FragmentActivity?) {
        activity?.also {
            mRegisterActReference?.get()?.also { ra ->
                if (it != ra) {
                    destroy()
                }
            }
            this.mRegisterActReference = WeakReference(it)
            // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
            mBluetoothAdapter?.also { adapter ->
                val enabled = adapter.isEnabled
                if (!enabled) {
                    val enableBtIntent = Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE
                    )
                    it.startActivityForResult(
                        enableBtIntent,
                        REQUEST_ENABLE_BT
                    )
                } else {
                    onBTStateChanged(BleState.SCAN_START)
                    scanLeDevice(true)
                }
            } ?: also {
                openBluetooth()
            }
        }
    }

    /**
     * 初始化蓝牙对象，我们采用的蓝牙4.0，android 4.3以上支持
     * @param activity 用于查询蓝牙支持信息，以及注册 BroadCastReceiver
     */
    fun openBluetooth() {
        //检测手机是否支持蓝牙设备连接
        mRegisterActReference?.get()?.also {
            it.runOnUiThread {
                if (it.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    checkBle()
                } else {
                    showToast(it, "您的手机不支持蓝牙功能")
                }
            }
        }
    }

    fun checkBle() {
        // 初始化 Bluetooth adapter,
        // 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        mRegisterActReference?.get()?.also { activity ->
            PermissionsUtil.requestDonotHandler(
                activity, PermissionsUtil.OnPermissionDonotHandler { _, granted ->
                    if (granted) {
                        initBluetooth()
                    } else {
                        onLocalBTEnabled(false)
                    }
                },
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun initBluetooth() {
        if (mBluetoothAdapter == null) {
            mRegisterActReference?.get()?.also {
                val bluetoothManager =
                    it.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                mBluetoothAdapter = bluetoothManager.adapter
                mBluetoothAdapter?.also { adapter ->
                    //在scanLeDevice之前,避免连接时BluetoothLeService仍为null
                    if (mBTControlManager == null) {
                        mBTControlManager = BTControlManager(it)
                            .addBtControlListener(mBTControlListener)
                            .addServiceConnect {
                                registerReceiver()
                                if (adapter.isEnabled) {
                                    scanLeDevice(true)
                                }
                            }
                            .init()
                    } else {
                        registerReceiver()
                        if (adapter.isEnabled) {
                            scanLeDevice(true)
                        }
                    }
                } ?: also { helper ->
                    helper.destroy()
                    showToast(it, "您的手机不支持蓝牙功能")
                }

                // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
                mBluetoothAdapter?.also { adapter ->
                    val enabled = adapter.isEnabled
                    if (!enabled) {
                        onBTStateChanged(BleState.NOT_FOUND)
                        val enableBtIntent = Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE
                        )
                        it.startActivityForResult(
                            enableBtIntent,
                            REQUEST_ENABLE_BT
                        )
                    }
                }
            }

        }

    }

    fun showToast(activity: FragmentActivity?, msg: String) {
        DToast.make(activity).setText(R.id.tv_content_default, msg).show()
    }

    open fun destroy() {
        mRegisterActReference?.also {
            it.get()?.also {
                //注销广播
                unregisterReceiver()
            }
            it.clear()
        }

        scanLeDevice(false)
        mRegisterActReference = null
        mBluetoothAdapter = null
        mBTControlManager = null
        mBluetoothStateReceiver = null
        mNewScanCallback = null
        mLeScanCallback = null
        mBleStatusCallback = null
        mDataCallback = null
    }

    /**
     * 蓝牙扫描
     *
     * @param enable
     */
    fun scanLeDevice(enable: Boolean) {
        mHandler.removeMessages(MESSAGE_SCAN)
        removeMessageTimeout()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scanLeDeviceNewApi(enable)
        } else {
            scanLeDeviceLowApi(enable)
        }
    }

    private fun handlerScanResult(
        device: BluetoothDevice,
        rssi: Int,
        scanRecord: ByteArray
    ) {
        mScanRunnable.also {
            it.setDevice(device, rssi, scanRecord)
            mExecutor.execute(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun scanLeDeviceNewApi(enable: Boolean) {
        mBluetoothAdapter?.also {
            if (enable) {
                if (!it.isEnabled) {
                    return
                }
            }
            if (mNewScanCallback == null) {
                mNewScanCallback = BLENewCallBack(this)
            }
            if (enable) {
                val scanner = it.bluetoothLeScanner
                if (mScanning) {
                    scanner.stopScan(mNewScanCallback)
                    Log.i(TAG, "正在查找设备..." + "已启动")
                }
                mScanning = true
                scanner.startScan(mNewScanCallback)
                onBTStateChanged(BleState.SCAN_START)
                sendMessageTimeout()
            } else {
                if (mScanning) {
                    if (it.isEnabled) {
                        it.bluetoothLeScanner.stopScan(mNewScanCallback)
                    }
                    mScanning = false
                    Log.i(TAG, "===停止查找设备===")
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun scanLeDeviceLowApi(enable: Boolean) {
        mBluetoothAdapter?.also {
            if (enable) {
                if (!it.isEnabled) {
                    return
                }
            }

            if (mLeScanCallback == null) {
                mLeScanCallback = BLECallback(this)
            }

            if (enable) {
                if (mScanning) {
                    it.stopLeScan(mLeScanCallback)
                    Log.i(TAG, "正在查找设备..." + "已启动")
                }
                val ret = it.startLeScan(mLeScanCallback)
                if (ret) {
                    mScanning = true
                    onBTStateChanged(BleState.SCAN_START)
                    sendMessageTimeout()
                } else {
                    mHandler.sendEmptyMessageDelayed(MESSAGE_SCAN, 1000)
                }
                Log.i(TAG, "正在查找设备...$ret")
            } else {
                if (mScanning) {
                    if (it.isEnabled) {
                        it.stopLeScan(mLeScanCallback)
                    }
                    mScanning = false
                    Log.i(TAG, "===停止查找设备===")
                }
            }
        }
    }

    fun removeMessageTimeout() {
        mHandler.removeMessages(MESSAGE_TIMEOUT)
    }

    private fun sendMessageTimeout() {
        removeMessageTimeout()
        mHandler.sendEmptyMessageDelayed(
            MESSAGE_TIMEOUT,
            30000
        )
    }

    private fun receive(state: Int) {
        when (state) {
            BluetoothAdapter.STATE_ON ->
                scanLeDevice(true)
            BluetoothAdapter.STATE_TURNING_OFF, BluetoothAdapter.STATE_OFF -> {
                onBTStateChanged(BleState.NOT_FOUND)
                scanLeDevice(false)
            }
            else -> {
            }
        }
    }

    private fun onConnectChanged(connected: Boolean) {
        if (!connected && isAutoScanWhenDisconnected) {
            scanLeDevice(true)
        } else if (connected) {
            //连接成功
            onBTStateChanged(BleState.CONNECTED)
        }
    }

    /**
     * 注册广播，监听蓝牙开关状态
     */
    fun registerReceiver() {
        if (mRegistered.compareAndSet(false, false)) {
            mRegisterActReference?.get()?.also {
                if (mBluetoothStateReceiver == null) {
                    mBluetoothStateReceiver = BluetoothStateReceiver(this)
                    val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                    it.registerReceiver(mBluetoothStateReceiver, filter)
                    mRegistered.set(true)
                }
            }
        }
    }

    /**
     * 注销广播
     */
    fun unregisterReceiver() {
        if (mRegistered.compareAndSet(true, true)) {
            mRegisterActReference?.get()?.also {
                mBluetoothStateReceiver?.also { receiver ->
                    it.unregisterReceiver(receiver)
                    mRegistered.set(false)
                }
            }
        }
    }

    /**
     * 蓝牙广播监听
     *
     * @author 毛晓飞
     */
    class BluetoothStateReceiver internal constructor(bleHelper: BaseBleHelper) :
        BroadcastReceiver() {
        private val mReference = WeakReference(bleHelper)
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1).also {
                mReference.get()?.receive(it)
            }
        }

    }

    /******************静态内部类的处理 */
    class BLECallback internal constructor(callback: BaseBleHelper) :
        BluetoothAdapter.LeScanCallback {
        private val reference = WeakReference(callback)
        override fun onLeScan(
            device: BluetoothDevice,
            rssi: Int,
            scanRecord: ByteArray
        ) {
            reference.get()?.handlerScanResult(device, rssi, scanRecord)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    class BLENewCallBack internal constructor(bleHelper: BaseBleHelper) : ScanCallback() {

        private val reference = WeakReference(bleHelper)

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                it.scanRecord?.bytes?.let { it1 ->
                    reference.get()?.handlerScanResult(
                        it.device, it.rssi,
                        it1
                    )
                }
            }
        }
    }

    class ScanRunnable internal constructor(bleHelper: BaseBleHelper) : Runnable {
        private var device: BluetoothDevice? = null
        private var rssi = 0
        private lateinit var scanRecord: ByteArray
        private val mReference = WeakReference(bleHelper)
        fun setDevice(
            device: BluetoothDevice?,
            rssi: Int,
            scanRecord: ByteArray
        ) {
            this.device = device
            this.rssi = rssi
            this.scanRecord = scanRecord
        }

        override fun run() {
            mReference.get()?.checkDevice(device, rssi, scanRecord)
        }
    }

    class ScanHandler internal constructor(bleHelper: BaseBleHelper) : Handler() {
        var reference = WeakReference(bleHelper)
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_SCAN -> {
                    reference.get()?.scanLeDevice(true)
                }
                MESSAGE_TIMEOUT -> {
                    reference.get()?.onBTStateChanged(BleState.TIMEOUT)
                    reference.get()?.scanLeDevice(false)
                }
            }
        }

    }

    class BTListener internal constructor(bleHelper: BaseBleHelper) :
        BTControlManager.BTControlListener {
        private val mWeakReference = WeakReference(bleHelper)
        override fun onDataReceived(btData: BTData?) {
            mWeakReference.get()?.onBTDataReceived(btData)
        }

        override fun onActionNotification() {
            mWeakReference.get()?.onNotification()
        }

        override fun onConnectChanged(connected: Boolean) {
            mWeakReference.get()?.also {
                Log.i(
                    it.TAG,
                    if (connected) "已成功连接，可以开始测量" else "连接失败，请重新连接"
                )
                it.onConnectChanged(connected)
            }
        }

    }
}