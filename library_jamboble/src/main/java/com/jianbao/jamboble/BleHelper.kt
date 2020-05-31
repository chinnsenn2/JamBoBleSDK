package com.jianbao.jamboble

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.LeScanCallback
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import com.jianbao.jamboble.BTControlManager.BTControlListener
import com.jianbao.jamboble.data.BTData
import com.jianbao.jamboble.data.BloodPressureData
import com.jianbao.jamboble.data.BloodSugarData
import com.jianbao.jamboble.data.FatScaleData
import com.jianbao.jamboble.device.BTDevice
import com.jianbao.jamboble.device.BTDeviceSupport
import java.lang.ref.WeakReference

/**
 * Created by zhangmingyao
 * date: 2018/7/20.
 * Email:501863760@qq.com
 */
class BleHelper(activity: Activity, deviceType: BTDeviceSupport.DeviceType) {
    private var mBluetoothAdapter //蓝牙适配器，单例唯一
            : BluetoothAdapter? = null
    private var mBTControlManager: BTControlManager? = null
    private var mBluetoothStateReceiver: BluetoothStateReceiver? = null
    private var mLeScanCallback //蓝牙设备搜索结果
            : LeScanCallback? = null
    private val mBTControlListener: BTControlListener
    private var mHandler: Handler? = null
    private var mScanning = false

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

    private val mLockAutoScan = Any()
    private val activityReference = WeakReference(activity)
    private val mBleWeightCallback: BleWeightCallback? = null
    private val mBloodPressureCallback: BleBloodPressureCallback? = null
    private val mBloodSugarCallback: BleBloodSugarCallback? = null
    private var mBleStatusCallback: IBleStatusCallback? = null
    private val mDeviceType = deviceType
    private var mScanRunnable: ScanRunnable? = null

    init {
        mHandler = ScanHandler(this@BleHelper)
        registerReceiver()
    }

    /**
     * 初始化蓝牙对象，我们采用的蓝牙4.0，android 4.3以上支持
     */
    fun openBluetooth() {
        //检测手机是否支持蓝牙设备连接
        activityReference.get()?.also {
            if (!it.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(it, "您的手机不支持蓝牙功能", Toast.LENGTH_SHORT).show()
            } else {
                checkBle(it)
            }
        }
    }

    private fun checkBle(activity: Activity) {
        // 初始化 Bluetooth adapter,
        // 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        if (mBluetoothAdapter == null) {
            val bluetoothManager =
                activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            mBluetoothAdapter = bluetoothManager.adapter
            // 检查设备上是否支持蓝牙
            if (mBluetoothAdapter == null) {
                Toast.makeText(activity, "您的手机不支持蓝牙功能", Toast.LENGTH_SHORT).show()
            } else {

                //在scanLeDevice之前,避免连接时BluetoothLeService仍为null
                if (mBTControlManager == null) {
                    mBTControlManager = BTControlManager(activity)
                        .addBtControlListener(mBTControlListener)
                        .addServiceConnect {
                            registerReceiver()
                            if (mBluetoothAdapter!!.isEnabled) {
                                scanLeDevice(true)
                            }
                        }
                        .init()
                } else {
                    registerReceiver()
                    if (mBluetoothAdapter!!.isEnabled) {
                        scanLeDevice(true)
                    }
                }
            }
        }

        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (mBluetoothAdapter != null) {
            val enabled = mBluetoothAdapter!!.isEnabled
            //            mBleDataCallback.onLocalBTEnabled(enabled);
            if (!enabled) {
                onBTStateChanged(IBleStatusCallback.State.NOT_FOUND)
                val enableBtIntent = Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE
                )
                activity.startActivityForResult(
                    enableBtIntent,
                    REQUEST_ENABLE_BT
                )
            }
        }
    }

    /**
     * 蓝牙扫描
     *
     * @param enable
     */
    fun scanLeDevice(enable: Boolean) {
        mHandler!!.removeMessages(MESSAGE_SCAN)
        if (mBluetoothAdapter != null) {
            if (enable) {
                if (!mBluetoothAdapter!!.isEnabled) {
                    return
                }
            }
            if (mLeScanCallback == null) {
                mLeScanCallback = BLECallback(this)
            }
            if (enable) {
                if (!mScanning) {
                    mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
                    val ret = mBluetoothAdapter!!.startLeScan(mLeScanCallback)
                    if (ret) {
                        mScanning = true
                        onBTStateChanged(IBleStatusCallback.State.SCAN_START)
                    } else {
                        mHandler!!.sendEmptyMessageDelayed(MESSAGE_SCAN, 1000)
                    }
                    // resetMessage();
                    Log.i(TAG, "正在查找设备...$ret")
                } else {
                    Log.i(TAG, "正在查找设备..." + "已启动")
                }
            } else {
                if (mScanning) {
                    if (mBluetoothAdapter!!.isEnabled) {
                        mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
                    }
                    mScanning = false
                    Log.i(TAG, "===停止查找设备===")
                }
            }
        }
    }

    /**
     * 注册广播，监听蓝牙开关状态
     */
    fun registerReceiver() {
        activityReference.get()?.also {
            if (mBluetoothStateReceiver == null) {
                mBluetoothStateReceiver = BluetoothStateReceiver(this)
                val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                it.registerReceiver(mBluetoothStateReceiver, filter)
            }
        }
    }

    /**
     * 注销广播
     */
    fun unregisterReceiver() {
        activityReference.get()?.also {
            if (mBluetoothStateReceiver != null) {
                it.unregisterReceiver(mBluetoothStateReceiver)
            }
        }
    }

    val isEnable: Boolean
        get() = if (mBluetoothAdapter != null) {
            mBluetoothAdapter!!.isEnabled
        } else false

    fun doReSearch() {
        activityReference.get()?.also {
            // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
            if (mBluetoothAdapter != null) {
                val enabled = mBluetoothAdapter!!.isEnabled
                if (!enabled) {
                    val enableBtIntent = Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE
                    )
                    it.startActivityForResult(
                        enableBtIntent,
                        REQUEST_ENABLE_BT
                    )
                } else {
                    onBTStateChanged(IBleStatusCallback.State.SCAN_START)
                    scanLeDevice(true)
                }
            } else {
                openBluetooth()
            }
        }

    }

    protected fun connectDevice(device: BTDevice?, adress: String?): Boolean {
        scanLeDevice(false)
        return mBTControlManager!!.connect(device, adress)
    }

    private fun handlerScanResult(
        device: BluetoothDevice,
        rssi: Int,
        scanRecord: ByteArray
    ) {
        activityReference.get()?.also {
            if (mScanRunnable == null) {
                mScanRunnable = ScanRunnable(this)
            }
            mScanRunnable!!.setDevice(device, rssi, scanRecord)
            it.runOnUiThread(mScanRunnable)
        }
    }

    fun destroy() {
        scanLeDevice(false)

        //注销广播
        unregisterReceiver()

        //释放资源
        if (mBTControlManager != null) {
            mBTControlManager!!.dispose()
            mBTControlManager = null
        }
        if (mLeScanCallback != null) {
            mLeScanCallback = null
        }
        if (mBleStatusCallback != null) {
            mBleStatusCallback = null
        }
        //        if (mBleDataCallback != null) {
//            mBleDataCallback = null;
//        }
    }

    private fun receive(state: Int) {
        when (state) {
            BluetoothAdapter.STATE_ON -> //                mBleDataCallback.onLocalBTEnabled(true);
                scanLeDevice(true)
            BluetoothAdapter.STATE_TURNING_OFF, BluetoothAdapter.STATE_OFF -> {
                //                mBleDataCallback.onLocalBTEnabled(false);
                onBTStateChanged(IBleStatusCallback.State.NOT_FOUND)
                scanLeDevice(false)
            }
            else -> {
            }
        }
    }

    private fun checkDevice(
        device: BluetoothDevice?,
        rssi: Int,
        scanRecord: ByteArray
    ) {
        val btDevice = BTDeviceSupport.checkSupport(device, mDeviceType)
        if (btDevice != null && mBleStatusCallback != null) {
            println("device.getAddress() = " + device!!.address)
            scanLeDevice(false)
            Log.i(TAG, "已找到设备，准备连接...")
            mBleStatusCallback!!.onBTDeviceFound(device)
            if (BTDeviceSupport.isYolandaFatScale(btDevice)) {
                if (mBTControlManager != null) {
                    mBTControlManager!!.connectDevice = btDevice
                }
                mBleStatusCallback!!.doByThirdSdk(device, btDevice, rssi, scanRecord)
            } else {
                if (mBTControlManager != null) {
                    mBTControlManager!!.connect(btDevice, device.address)
                }
            }
        }
    }

    private fun onConnectChanged(connected: Boolean) {
        if (!connected && isAutoScanWhenDisconnected) {
            scanLeDevice(true)
        } else if (connected) {
            //连接成功
            onBTStateChanged(IBleStatusCallback.State.CONNECTED)
        }
    }

    /*******************蓝牙回调 */
    fun onBTStateChanged(state: Int) {
        mBleWeightCallback?.onBTStateChanged(state)
        mBloodPressureCallback?.onBTStateChanged(state)
        mBloodSugarCallback?.onBTStateChanged(state)
    }

    fun onBTDataReceived(btData: BTData?) {
        when (btData) {
            is FatScaleData ->{
                mBleWeightCallback?.onBTDataReceived(btData)
            }
            is BloodPressureData ->{
                mBloodPressureCallback?.onBTDataReceived(btData)
            }
            is BloodSugarData ->{
                mBloodSugarCallback?.onBTDataReceived(btData)
            }
        }
    }

    fun onLocalBTEnabled(enabled: Boolean) {
//        mBleDataCallback.onLocalBTEnabled(enabled);
    }

    fun onUnsteadyValue(value: Float) {
        mBleWeightCallback?.onUnsteadyValue(value)
    }

    fun onBTDeviceFound(device: BluetoothDevice?) {
        mBleStatusCallback!!.onBTDeviceFound(device)
    }

    fun onNotification() {
        mBleStatusCallback!!.onNotification()
    }

    fun doByThirdSdk(
        device: BluetoothDevice?,
        btDevice: BTDevice?,
        rssi: Int,
        scanRecord: ByteArray?
    ) {
        mBleStatusCallback!!.doByThirdSdk(device, btDevice, rssi, scanRecord)
    }

    /**
     * 蓝牙广播监听
     *
     * @author 毛晓飞
     */
    private class BluetoothStateReceiver(helper: BleHelper) : BroadcastReceiver() {
        private val mReference = WeakReference(helper)
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
            mReference.get()?.receive(state)
        }

    }

    /******************静态内部类的处理 */
    class BLECallback internal constructor(callback: BleHelper) : LeScanCallback {
        private val reference = WeakReference(callback)
        override fun onLeScan(
            device: BluetoothDevice,
            rssi: Int,
            scanRecord: ByteArray
        ) {
            reference.get()?.handlerScanResult(device, rssi, scanRecord)
        }

    }

    class ScanRunnable internal constructor(helper: BleHelper) : Runnable {
        private var device: BluetoothDevice? = null
        private var rssi = 0
        private lateinit var scanRecord: ByteArray
        private val mReference = WeakReference(helper)
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
            mReference.get()?.also { it.checkDevice(device, rssi, scanRecord) }
        }
    }

    private class ScanHandler internal constructor(helper: BleHelper) : Handler() {
        var reference = WeakReference(helper)
        override fun handleMessage(msg: Message) {
            if (msg.what == MESSAGE_SCAN) {
                reference.get()?.scanLeDevice(true)
            }
        }

    }

    private class BTListener internal constructor(helper: BleHelper) : BTControlListener {
        private val mWeakReference = WeakReference(helper)
        override fun onDataReceived(btData: BTData) {
            mWeakReference.get()?.onBTDataReceived(btData)
        }

        override fun onActionNotification() {
            val bleHelper = mWeakReference.get()
            bleHelper?.onNotification()
        }

        override fun onConnectChanged(connected: Boolean) {
            Log.i(
                TAG,
                if (connected) "已成功连接，可以开始测量" else "连接失败，请重新连接"
            )
            val bleHelper = mWeakReference.get()
            bleHelper?.onConnectChanged(connected)
        }

    }

    companion object {
        private const val TAG = "BleHelper"
        private const val MESSAGE_SCAN = 0
        private const val REQUEST_ENABLE_BT = 1
    }

    /**
     * 构造函数
     *
     * @param activity
     */
    init {
        mBTControlListener = BTListener(this)
    }
}