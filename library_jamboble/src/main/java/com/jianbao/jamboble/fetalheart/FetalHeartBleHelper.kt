package com.jianbao.jamboble.fetalheart

import android.Manifest
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.fragment.app.FragmentActivity
import com.jianbao.jamboble.BaseBleHelper
import com.jianbao.jamboble.BleState
import com.jianbao.jamboble.data.BTData
import com.jianbao.jamboble.data.FetalHeartData
import com.jianbao.jamboble.device.BTDeviceSupport
import com.jianbao.jamboble.utils.permissions.PermissionsUtil
import java.lang.ref.WeakReference

class FetalHeartBleHelper private constructor() : BaseBleHelper() {

    private var mSearchBluetoothDevice: BluetoothDevice? = null
    private var mFetalHeartEngineService: FetalHeartEngineService? = null
    private var mFetalHeartServiceConnection: FetalHeartServiceConnection? = null

    private var mBindActivityReference: WeakReference<FragmentActivity>? = null

    private object Singleton {
        val instance = FetalHeartBleHelper()
    }

    companion object {
        @JvmStatic
        val instance = Singleton.instance
    }

    /**
     * 连接设备
     * @param activity 用于绑定 Service
     *
     * 必须先用 openBluetooth 搜索到设备，或者 setBlueToothDevice
     * @see openBluetooth
     * @see setBlueToothDevice
     */
    fun connect(
        activity: FragmentActivity?
    ) {
        mSearchBluetoothDevice?.also {
            PermissionsUtil.requestDonotHandler(
                activity, PermissionsUtil.OnPermissionDonotHandler { _, granted ->
                    if (granted) {
                        mFetalHeartEngineService?.also {
                            destroy()
                        }
                        mFetalHeartServiceConnection = FetalHeartServiceConnection(this)
                        activity?.also {
                            this.mBindActivityReference = WeakReference(it)
                            Intent(it, FetalHeartEngineService::class.java).also { i ->
                                it.bindService(
                                    i,
                                    mFetalHeartServiceConnection!!,
                                    Service.BIND_AUTO_CREATE
                                )
                            }
                        }
                    } else {
                        onLocalBTEnabled(false)
                    }
                },
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } ?: also {
            showToast(activity, "未搜索到设备")
        }
    }


    override val TAG: String = "FetalHeartHelper"

    override val mHandler: ScanHandler by lazy {
        ScanHandler(this)
    }

    fun getBlueToothDevice() = mSearchBluetoothDevice

    fun setBlueToothDevice(btd: BluetoothDevice) {
        this.mSearchBluetoothDevice = btd
    }

    override fun checkDevice(device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray) {
        device?.also { d ->
            println(d.toString())
            val btDevice = BTDeviceSupport.checkSupport(d, BTDeviceSupport.DeviceType.FETAL_HEART)
            btDevice?.also {
                this.mSearchBluetoothDevice = d
                onBTDeviceFound(d)
            }
        }
    }

    override fun onBTStateChanged(state: BleState) {
        mDataCallback?.onBTStateChanged(state)
    }

    override fun onLocalBTEnabled(enabled: Boolean) {
        mDataCallback?.onLocalBTEnabled(enabled)
    }

    override fun onBTDataReceived(btData: BTData?) {
        mDataCallback?.onBTDataReceived(btData)
    }

    override fun onBTDeviceFound(device: BluetoothDevice?) {
        mBleStatusCallback?.onBTDeviceFound(device)
    }

    override fun onNotification() {
        mBleStatusCallback?.onNotification()
    }

    override fun destroy() {
        super.destroy()
        mBindActivityReference?.also { r ->
            r.get()?.also { act ->
                try {
                    act.unbindService(mFetalHeartServiceConnection!!)
                } catch (e: Exception) {

                }
            }
            r.clear()
            mSearchBluetoothDevice = null
            mFetalHeartEngineService = null
            mFetalHeartServiceConnection = null
        }
    }

    class FetalHeartServiceConnection(heartHelper: FetalHeartBleHelper) : ServiceConnection {
        private val mWeakReference = WeakReference(heartHelper)

        override fun onServiceDisconnected(name: ComponentName?) {
            mWeakReference.clear()
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mWeakReference.get()?.also {
                it.mFetalHeartEngineService =
                    (service as FetalHeartEngineService.BluetoothBinder).service
                        .also { service ->
                            service.setCallback(
                                object : FetalHeartEngineService.Callback {
                                    override fun dispServiceStatus(status: Int) {
                                        if (status == FetalHeartEngineService.CONNECT_SUCCESS) {
                                            it.onBTStateChanged(BleState.CONNECTED)
                                        } else if (status == FetalHeartEngineService.CONNECT_FAILED
                                            || status == FetalHeartEngineService.READ_DATA_FAILED
                                        ) {
                                            it.onBTStateChanged(BleState.TIMEOUT)
                                        }

                                    }

                                    override fun dispInfor(data: FetalHeartData?) {
                                        it.onBTDataReceived(data)
                                    }

                                }
                            )
                            service.setBluetoothDevice(it.mSearchBluetoothDevice)
                            service.start()
                        }
            }
        }

    }
}