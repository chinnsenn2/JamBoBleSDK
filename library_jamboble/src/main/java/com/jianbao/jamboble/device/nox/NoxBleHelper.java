package com.jianbao.jamboble.device.nox;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.jianbao.jamboble.device.nox.interfaces.IBleCallBack;
import com.jianbao.jamboble.device.nox.manager.BleManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * <p>
 * Title: BleHelper
 * </p>
 * <p>
 * Description:与蓝牙通信的帮助类
 * </p>
 */

@SuppressLint("NewApi")
public class NoxBleHelper {
    private static final String TAG = NoxBleHelper.class.getSimpleName();
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;

    public static final int SCAN_PERIOD = 5000;// 扫描时长，单位ms
    public static final int CONNECT_TIMEOUT = 20000;//连接超时时间

    /**
     * 是否正在扫描设备
     */
    private boolean mScanning;

    private boolean mSendOk;

    public final static UUID BLE_NOTIFY_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_NOTIFY_SERVER_UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_NOTIFY_CHARACTERISTIC_UUID = UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_WRITE_SERVER_UUID = UUID.fromString("0000ffe5-0000-1000-8000-00805f9b34fb");
    public final static UUID BLE_WRITE_UUID = UUID.fromString("0000ffe9-0000-1000-8000-00805f9b34fb");
    public final static UUID FILE_UUID = UUID.fromString("00001105-0000-1000-8000-00805F9B34FB");

    public static final String ACTION_FULL_POWER = "com.medica.xiangshui.Device_FullPower";

    private BluetoothGattCharacteristic gcWrite, gcNotify;


    private final Handler mHandler = new Handler();
    public static final int REQCODE_OPEN_BT = 0x999;

    private static NoxBleHelper instance;
    private static final byte[] mLock = new byte[0];
    private ArrayList<IBleCallBack> mListeners;

    private BluetoothDevice mBluetoothDevice;
    BluetoothManager mBluetoothManager;

    private boolean isConnected = false;

    private NoxBleHelper(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);

        this.mBluetoothAdapter = mBluetoothManager.getAdapter();
        mListeners = new ArrayList<>();
    }

    boolean mNeedRetry;

    public boolean registListener(IBleCallBack listener) {

        //如果已经存在就不重复添加了
        mListeners.remove(listener);
        Log.e(TAG, "   注册BleHelper监听");
        return mListeners.add(listener);
    }

    public boolean unRegistListener(IBleCallBack listener) {
        Log.e(TAG, "   注销BleHelper监听");
        return mListeners.remove(listener);
    }

    /**
     * Clears the internal cache and forces a refresh of the services from the
     * remote device.
     */
    public boolean refreshDeviceCache() {
        if (mBluetoothGatt != null) {
            try {
                BluetoothGatt localBluetoothGatt = mBluetoothGatt;
                Method localMethod = localBluetoothGatt.getClass().getMethod("refresh");
                if (localMethod != null) {
                    boolean bool = ((Boolean) localMethod.invoke(
                            localBluetoothGatt)).booleanValue();
                    Log.d(TAG, " refreshDeviceCache res:" + bool);
                    return bool;
                }
            } catch (Exception e) {
                Log.d(TAG, " An exception occured while refreshing device");
            }
        }
        return false;
    }


    /**
     * 实现了接收蓝牙扫描结果的接口
     */
    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {// Z1-140900000
//            LogUtil.showMsg(TAG + " onLeScan dname:" + device.getName() + " listerCount：" + mListeners.size());
            for (IBleCallBack listener : mListeners) {
                //Log.e(TAG, "Listener" + listener + "--M1:" + listener.getMaster() + "---M2:" + master);
                listener.onLeScan(device, rssi, scanRecord);

            }
        }
    };

    private void callbackState(ConnectionState state) {
        Log.e(TAG, "callbackState============== state:" + state);
        for (IBleCallBack listener : mListeners) {
            Log.d(TAG, " callbackState state:" + state + ",listener:" + listener);
            listener.onBleStateChanged(state);
        }
    }

    int mRetryTime;

    private boolean tryReconnect() {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOff = !pm.isScreenOn();
        Log.e(TAG, "    尝试重连次数：" + mRetryTime + "   是否需要重连：" + mNeedRetry + "   屏幕是否关闭：" + isScreenOff);
        Log.d(TAG, " tryReconnect mNeedRetry:" + mNeedRetry + ",mRetryTime:" + mRetryTime);

        if (mRetryTime < 6 && mNeedRetry && !isScreenOff) {
            if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                return false;
            }
            try {
                mBluetoothGatt.disconnect();// 断开与远程设备的GATT连接
                mBluetoothGatt.close();// 关闭GATT Client端
            } catch (Exception e) {
                e.printStackTrace();
            }
            SystemClock.sleep(100);
            connect(btAddress);
            mRetryTime++;
            return true;
        }
        return false;
    }


    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (gatt == mBluetoothGatt) {
                String log = TAG + " onConnectionStateChange status:" + status + ",newState:" + newState;
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    isConnected = true;
                    SystemClock.sleep(200);
                    boolean result = mBluetoothGatt.discoverServices();
                    mRetryTime = 0;
                    Log.d(TAG, " discoverServices--" + result);
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    mConnectionState = newState;
                    if (!isBluetoothOpen()) {
                        callbackState(ConnectionState.DISCONNECT);
                        Log.e(TAG, "bluetooth is turn off!");
                    } else if (!tryReconnect()) {
                        mRetryTime = 0;
                        disconnect();
                    }
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, " onServicesDiscovered----------");
            if (gatt == mBluetoothGatt && status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService writeService = gatt.getService(BLE_WRITE_SERVER_UUID);
                BluetoothGattService notifyService = gatt.getService(BLE_NOTIFY_SERVER_UUID);
                if (writeService != null) {
                    gcWrite = writeService.getCharacteristic(BLE_WRITE_UUID);
                    Log.d(TAG, " onServicesDiscovered write----------");
                }

                if (notifyService != null) {
                    gcNotify = notifyService.getCharacteristic(BLE_NOTIFY_CHARACTERISTIC_UUID);
                    if (gcNotify != null) {
                        mHandler.removeCallbacks(connectTimeoutTask);
                        mConnectionState = BluetoothProfile.STATE_CONNECTED;
                        int charaRxProp = gcNotify.getProperties();
                        if ((charaRxProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            setCharacteristicNotification(gcNotify, true);
                        }
                        mBluetoothDevice = mBluetoothGatt.getDevice();
                        mRetryTime = 0;
                        Log.e(TAG, "   设备已连接");
                        Log.d(TAG, " onServicesDiscovered connected----------");
                        callbackState(ConnectionState.CONNECTED);
                    } else {
                        Log.d(TAG, " onServicesDiscovered notify set fail----------");
                        disconnect();
                    }
                } else {
                    Log.e(TAG, "  获取设备服务失败");
                    Log.d(TAG, " onServicesDiscovered notify fail----------");
                    disconnect();
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            //LogUtil.log(TAG+" onCharacteristicChanged------uuid:"+characteristic.getUuid()+",gatt==mGatt:"+(gatt == mBluetoothGatt));

            if (gatt == mBluetoothGatt && BLE_NOTIFY_CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                byte[] data = characteristic.getValue();
                int len = data == null ? 0 : data.length;

//                String hex = "";
//                for( int i = 0; i < data.length; i++) {
//                    hex += String.format("%02X ", data[i] & 0xFF );
//                }
                Log.e(TAG, " onCharacteristicChanged:" + len + ",data:" + BleManager.byte2hex(data));

                if (len > 0) {
                    for (IBleCallBack listener : mListeners) {
                        listener.handleLeData(data);
                    }
                }
            }
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            mSendOk = true;
            Log.e(TAG,
                    "onCharacteristicWrite---BluetoothGatt.GATT_SUCCESS==status:" +
                            (BluetoothGatt.GATT_SUCCESS == status));
        }
    };

    /**
     * @param context 上下文对象
     * @return 返回该类的实例对象
     */
    public static NoxBleHelper getInstance(Context context) {
//        LogUtil.showMsg(TAG + " getInstance instance:" + (instance == null));
        if (instance == null) {
            synchronized (mLock) {
                if (instance == null) {
                    instance = new NoxBleHelper(context);
                }
            }
        }
        return instance;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public boolean isBluetoothOpen() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    /**
     * 判断设备是否支持Ble
     *
     * @return 返回boolean值
     */
    public boolean isSupportBle() {
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * 扫描智能床垫的硬件设备，扫描前请先确认打开蓝牙开关
     */
    public void scanBleDevice() {
        scanBleDevice(SCAN_PERIOD);
    }

    /**
     * 扫描智能床垫的硬件设备，扫描前请先确认打开蓝牙开关
     *
     * @param scanTime 扫描时长，单位ms
     */
    public synchronized void scanBleDevice(int scanTime) {
        Log.e(TAG, "   开始扫描设备");

        if (!mScanning && isBluetoothOpen()) {
            mScanning = true;
            callbackState(ConnectionState.SCANNING);
            mHandler.postDelayed(stopScanTask, scanTime);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }


    /**
     * 停止扫描设备
     */
    public void stopScan() {
        //if (getMaster() != manager) return;
        if (mScanning) {
            Log.e(TAG, "   停止扫描设备");
            mScanning = false;
            mHandler.removeCallbacks(stopScanTask);
            //由于stopScan是延时后的操作，为避免断开或其他情况时把对象置空，所以以下2个对象都需要非空判断
            if (mBluetoothAdapter != null && mLeScanCallback != null) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
            callbackState(ConnectionState.STOP_SCAN);
        }
    }

    /*
    * 停止扫描,是否扫描到了设备,扫描到了就不调用CONNECTION_STATE.STOP_SCAN
    * */
    /*public void stopScan(boolean scened) {
        if (mScanning) {
            mScanning = false;
            mHandler.removeCallbacks(stopScanTask);
            if (mLeScanCallback != null) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
            if (!scened) {
                callbackState(CONNECTION_STATE.STOP_SCAN);
            }
        }
    }*/

    private String btAddress;
    private int mConnectionState;

    public String getBtAddress() {
        return btAddress;
    }

    public int getConnectionState() {
        //添加一个实时监测的条件，因为有时候手机把App休眠了，蓝牙设备断开了，状态回调不到
        if (mConnectionState == BluetoothProfile.STATE_CONNECTED) {
            if (mBluetoothDevice == null || (mBluetoothManager.getConnectionState(mBluetoothDevice, BluetoothProfile.GATT) != BluetoothProfile.STATE_CONNECTED)) {
                return BluetoothProfile.STATE_DISCONNECTED;
            }
        }
        return mConnectionState;
    }

    /**
     * 连接设备
     *
     * @param address
     * @return 返回结果只关注false情况，说明连接失败，true：表示连接中，需要等待连接结果
     */
    public boolean connectDevice(String address) {
        if (address == null || mBluetoothAdapter == null) {
            return false;
        }

        Log.d(TAG, " connectDevice addr1:" + address + ",cacheAddr:" + btAddress + ",connS:" + mConnectionState);
        //连接Nox2的蓝牙设备时，禁用纽扣后台连接

        if (!address.equals(btAddress)) {
            mRetryTime = 0;
            return connect(address);
        } else {
            if (mConnectionState == BluetoothGatt.STATE_CONNECTING) {
                return true;
            } else if (mConnectionState == BluetoothGatt.STATE_CONNECTED) {
                callbackState(ConnectionState.CONNECTED);
                return true;
            } else if (mConnectionState == BluetoothGatt.STATE_DISCONNECTED) {
                return connect(address);
            }
        }
        return false;
    }


    private boolean connect(String address) {

        Log.e(TAG, "   连接设备：" + address);
        Log.d(TAG, " connect address:" + address);
        mNeedRetry = true;
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            return false;
        }
        this.btAddress = address;
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

        mConnectionState = BluetoothGatt.STATE_CONNECTING;
        mHandler.postDelayed(connectTimeoutTask, CONNECT_TIMEOUT);

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device != null) {
            callbackState(ConnectionState.CONNECTING);
            mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);// 建立GATT连接
            return true;
        }

        return false;
    }


    private Runnable connectTimeoutTask = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, " connect timeout----------------");
            disconnect();
        }
    };


    /**
     * 断开设备连接
     */
    public synchronized void disconnect() {
        disconnect(true);
    }

    /**
     * 断开设备连接
     */
    public synchronized void disconnect(boolean needCallback) {
        mNeedRetry = false;
        mHandler.removeCallbacks(connectTimeoutTask);

        mConnectionState = BluetoothProfile.STATE_DISCONNECTED;
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            if (needCallback) {
                callbackState(ConnectionState.DISCONNECT);
            }
            return;
        }

        try {
            //refreshDeviceCache();
            mBluetoothGatt.disconnect();// 断开与远程设备的GATT连接
            mBluetoothGatt.close();// 关闭GATT Client端
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBluetoothGatt = null;
        if (needCallback) {
            callbackState(ConnectionState.DISCONNECT);
        }
    }


    /**
     * 停止扫描的操作
     */
    private final Runnable stopScanTask = new Runnable() {
        @Override
        public void run() {
            stopScan();

        }
    };


    private void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(BLE_NOTIFY_DESCRIPTOR_UUID);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
    }


    /**
     * 给蓝牙发送数据
     *
     * @param send_buf
     * @param sendDuration 包与包的发送间隔，时间毫秒
     * @return
     */
    public boolean send(final byte[] send_buf, int sendDuration) {
        boolean res = false;
        if (mBluetoothGatt != null && gcWrite != null) {
            gcWrite.setValue(send_buf);
//            gcWrite.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            int nRetry = 10;
            mSendOk = false;
            while (nRetry-- > 0 && mBluetoothGatt != null && !mSendOk && mConnectionState == BluetoothGatt.STATE_CONNECTED) {
                res = mBluetoothGatt.writeCharacteristic(gcWrite);
                SystemClock.sleep(sendDuration);// 适当延时
            }
        }
        return res;
    }

    public void restartBle() {
        if (null != mBluetoothAdapter) {
            Log.e(TAG, "重启蓝牙：");
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();

                // TODO: display some kind of UI about restarting BLE
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!mBluetoothAdapter.isEnabled()) {
                            mBluetoothAdapter.enable();
                        } else {
                            mHandler.postDelayed(this, 2500);
                        }
                    }
                }, 2500);
            }
        }
    }


    /**
     * 获取系统是否连接蓝牙设备 (部分手机通过反射可能会拿不到当前连接的系统蓝牙设备)
     *
     * @return
     */
    public boolean isConnectSystemBluetooth() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
        try {//得到连接状态的方法
            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            //打开权限
            method.setAccessible(true);
            int state = (int) method.invoke(adapter, (Object[]) null);

            if (state == BluetoothAdapter.STATE_CONNECTED) {
                Set<BluetoothDevice> devices = adapter.getBondedDevices();
                Log.d(TAG, "蓝牙设备数量:" + devices.size());

                for (BluetoothDevice device : devices) {
                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    method.setAccessible(true);
                    boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                    if (isConnected) {
                        Log.d(TAG, "当前连接的系统蓝牙名:" + device.getName());
                        return true;
                    } else {
                        return false;
                    }
                }
            }

        } catch (Exception e) {
            Log.d(TAG, "====获取系统是否连接蓝牙设备异常====");
            e.printStackTrace();
        }
        return false;
    }
}
