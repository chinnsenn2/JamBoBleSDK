package com.jianbao.fastble;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.jianbao.fastble.callback.BleGattCallback;
import com.jianbao.fastble.callback.BleNotifyCallback;
import com.jianbao.fastble.callback.BleScanCallback;
import com.jianbao.fastble.callback.BleWriteCallback;
import com.jianbao.fastble.data.BleDevice;
import com.jianbao.fastble.exception.BleException;
import com.jianbao.fastble.scan.BleScanRuleConfig;
import com.jianbao.jamboble.BleState;
import com.jianbao.jamboble.BuildConfig;
import com.jianbao.jamboble.SampleGattAttributes;
import com.jianbao.jamboble.callbacks.BleDataCallback;
import com.jianbao.jamboble.callbacks.IBleStatusCallback;
import com.jianbao.jamboble.callbacks.UnSteadyValueCallBack;
import com.jianbao.jamboble.data.BTData;
import com.jianbao.jamboble.data.CholestenoneData;
import com.jianbao.jamboble.data.QnUser;
import com.jianbao.jamboble.data.UricAcidData;
import com.jianbao.jamboble.device.BTDevice;
import com.jianbao.jamboble.device.BTDeviceSupport;
import com.jianbao.jamboble.device.OnCallBloodSugar;
import com.jianbao.jamboble.device.SannuoAnWenBloodSugar;
import com.jianbao.jamboble.device.oximeter.OxiMeterHelper;
import com.jianbao.jamboble.device.oximeter.OximeterDevice;
import com.jianbao.jamboble.fatscale.JamboQnHelper;
import com.jianbao.jamboble.utils.LogUtils;
import com.yolanda.health.qnblesdk.listener.QNResultCallback;
import com.yolanda.health.qnblesdk.out.QNBleApi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;

public class JamBoHelper {

    private JamBoHelper() {

    }

    private static class SingleTon {
        private final static JamBoHelper instance = new JamBoHelper();
    }

    static BleScanRuleConfig fatScaleBleScanRuleConfig = new BleScanRuleConfig.Builder()
            .setDeviceName(
                    false,
                    "Yolanda-CS20F2",
                    "QN-Scale",
                    "Yolanda-CS20G2",
                    "Yolanda-CS10C1"
            ) // 只扫描指定广播名的设备，可选
            .setAutoConnect(true) // 连接时的autoConnect参数，可选，默认false
            .setScanTimeOut(30000) // 扫描超时时间，可选，默认10秒F
            .build();

    static BleScanRuleConfig bloodPressureBleScanRuleConfig = new BleScanRuleConfig.Builder()
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
            .build();
    static BleScanRuleConfig bloodSugarBleScanRuleConfig = new BleScanRuleConfig.Builder()
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
            .build();
    static BleScanRuleConfig uricAcidBleScanRuleConfig = new BleScanRuleConfig.Builder()
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
            .build();
    static BleScanRuleConfig oxiMeterBleScanRuleConfig = new BleScanRuleConfig.Builder()
            .setDeviceName(
                    true,
                    "PC-60NW-1", "POD", " POD", "PC-68B", "PC-60F"
            ) // 只扫描指定广播名的设备，可选
            .setAutoConnect(true) // 连接时的autoConnect参数，可选，默认false
            .setScanTimeOut(30000) // 扫描超时时间，可选，默认10秒
            .build();
    static BleScanRuleConfig threeOnOneBleScanRuleConfig = new BleScanRuleConfig.Builder()
            .setDeviceName(
                    true,
                    "BeneCheck TC-B DONGLE", "BeneCheck"
            ) // 只扫描指定广播名的设备，可选
            .setAutoConnect(true) // 连接时的autoConnect参数，可选，默认false
            .setScanTimeOut(30000) // 扫描超时时间，可选，默认10秒
            .build();
    static BleScanRuleConfig sleepLightBleScanRuleConfig = new BleScanRuleConfig.Builder()
            .setAutoConnect(true) // 连接时的autoConnect参数，可选，默认false
            .setScanTimeOut(30000) // 扫描超时时间，可选，默认10秒
            .build();

    public static JamBoHelper getInstance() {
        return SingleTon.instance;
    }

    private JamboBleScanCallback mJamboBleScanCallback;

    public final void init(@NotNull Application app) {
        BleManager.getInstance().init(app);
        if (BleManager.getInstance().isSupportBle()) {
            this.initQnSdk(app);
        }

        JamboQnHelper.getInstance().init(app);

        BleManager.getInstance().enableLog(BuildConfig.DEBUG);
        BleManager.getInstance().setReConnectCount(1);
        BleManager.getInstance().setConnectOverTime(3000L);
        BleManager.getInstance().setOperateTimeout(5000);
        mJamboBleScanCallback = new JamboBleScanCallback(this);
    }

    private int mQnInitTime;

    private void initQnSdk(final Application app) {
        String encryptPath = "file:///android_asset/hzyb20160314175503.qn";
        QNBleApi.getInstance((Context) app).initSdk("hzyb20160314175503", encryptPath,
                (QNResultCallback) (i, s) -> {
                    if (i != 0 && mQnInitTime < 3) {
                        mQnInitTime = mQnInitTime + 1;
                        initQnSdk(app);
                    }
                });
    }

    private BleDataCallback mDataCallback;
    private IBleStatusCallback mBleStatusCallback;
    private UnSteadyValueCallBack mUnSteadyValueCallBack;
    private BTDevice mConnectDevice;

    public BTDevice getBtDevice() {
        return mConnectDevice;
    }

    public void setBtDevice(BTDevice btDevice) {
        this.mConnectDevice = btDevice;
    }

    /**
     * 扫描体脂称设备
     */
    public void scanFatScaleDevice() {
        BleManager.getInstance().initScanRule(fatScaleBleScanRuleConfig);
        scan(BTDeviceSupport.DeviceType.FAT_SCALE);
    }

    /**
     * 扫描血压设备
     */
    public void scanBloodPressureDevice() {
        BleManager.getInstance().initScanRule(bloodPressureBleScanRuleConfig);
        scan(BTDeviceSupport.DeviceType.BLOOD_PRESSURE);
    }

    public void scanBloodSugarDevice() {
        BleManager.getInstance().initScanRule(bloodSugarBleScanRuleConfig);
        scan(BTDeviceSupport.DeviceType.BLOOD_SUGAR);
    }

    public void scanUricAcidDevice() {
        BleManager.getInstance().initScanRule(uricAcidBleScanRuleConfig);
        scan(BTDeviceSupport.DeviceType.URIC_ACID);
    }

    public void scanOxiMeterDevice() {
        BleManager.getInstance().initScanRule(oxiMeterBleScanRuleConfig);
        scan(BTDeviceSupport.DeviceType.OXIMETER);
    }

    public void scanThreeOnOneDevice() {
        BleManager.getInstance().initScanRule(threeOnOneBleScanRuleConfig);
        scan(BTDeviceSupport.DeviceType.THREEONONE);
    }

//    public void scanSleepLightDevice() {
//        BleManager.getInstance().initScanRule(sleepLightBleScanRuleConfig);
//        scan(BTDeviceSupport.DeviceType.SLEEPLIGHT);
//    }

    private void scan(BTDeviceSupport.DeviceType type) {
        if (!BleManager.getInstance().isBlueEnable()) {
            showToast("请先打开蓝牙");
            requestEnableBle();
            return;
        }
        mJamboBleScanCallback.setDeviceType(type);
        BleManager.getInstance().scan(mJamboBleScanCallback);
    }

    private void requestEnableBle() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BleManager.getInstance().getContext().startActivity(intent);
    }

    public void stopScan() {
        if (!BleManager.getInstance().isBlueEnable()) {
            showToast("请先打开蓝牙");
            requestEnableBle();
            return;
        }
        BleManager.getInstance().cancelScan();
    }

    public final void onBTStateChanged(@NotNull BleState state) {
        BleDataCallback callback = this.mDataCallback;
        if (callback != null) {
            callback.onBTStateChanged(state);
        }

    }

    public void onLocalBTEnabled(boolean enabled) {
        BleDataCallback callback = this.mDataCallback;
        if (callback != null) {
            callback.onLocalBTEnabled(enabled);
        }

    }

    public void onUnsteadyValue(float value) {
        if (mUnSteadyValueCallBack != null) {
            mUnSteadyValueCallBack.onUnsteadyValue(value);
        }
    }

    public void onBTDeviceFound(@Nullable List<BleDevice> device) {
        IBleStatusCallback callback = this.mBleStatusCallback;
        if (callback != null) {
            callback.onBTDeviceFound(device);
        }

    }

    public void setBleDataCallBack(@NotNull BleDataCallback callback) {
        this.mDataCallback = callback;
    }

    public void setBleStatusCallback(@NotNull IBleStatusCallback callback) {
        this.mBleStatusCallback = callback;
    }

    public void setUnSteadyValueCallBack(@NotNull UnSteadyValueCallBack callback) {
        this.mUnSteadyValueCallBack = callback;
    }

    private String mLastDataFlag = "";

    public void onBTDataReceived(@Nullable BTData btData) {
        if (btData != null) {
            if (btData instanceof UricAcidData) {
                String lastTime = ((UricAcidData) btData).mYear
                        + String.valueOf(((UricAcidData) btData).mMonth)
                        + ((UricAcidData) btData).mday
                        + ((UricAcidData) btData).mHour
                        + ((UricAcidData) btData).mMinute
                        + ((UricAcidData) btData).mUricAcid;
                if (!TextUtils.equals(lastTime, this.mLastDataFlag)) {
                    if (this.mDataCallback != null) {
                        this.mDataCallback.onBTDataReceived(btData);
                    }
                    this.mLastDataFlag = lastTime;
                }
            } else if (btData instanceof CholestenoneData) {
                String lastTime = ((CholestenoneData) btData).mYear
                        + String.valueOf(((CholestenoneData) btData).mMonth)
                        + ((CholestenoneData) btData).mday
                        + ((CholestenoneData) btData).mHour
                        + ((CholestenoneData) btData).mMinute
                        + ((CholestenoneData) btData).cholestenone;
                if (!TextUtils.equals(lastTime, this.mLastDataFlag)) {
                    if (this.mDataCallback != null) {
                        this.mDataCallback.onBTDataReceived(btData);
                    }
                    this.mLastDataFlag = lastTime;
                }
            } else {
                if (mDataCallback != null) {
                    mDataCallback.onBTDataReceived(btData);
                }
            }
        }

    }

    public final void onNotification() {
        IBleStatusCallback callback = this.mBleStatusCallback;
        if (callback != null) {
            callback.onNotification();
        }

    }

    /**
     * 测量体重需传入用户参数
     */
    public void updateQnUser(QnUser qnUser) {
        JamboQnHelper.getInstance().updateQNUser(qnUser);
    }

    public void showToast(String msg) {
        Toast.makeText(BleManager.getInstance().getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void destroy() {
        mDataCallback = null;
        mBleStatusCallback = null;
        mUnSteadyValueCallBack = null;
        BleManager.getInstance().destroy();
    }

    private static class JamboBleScanCallback extends BleScanCallback {
        private WeakReference<JamBoHelper> mJamBoHelperWeakReference;
        private JamboBleGattCallback jamboBleGattCallback;
        private BTDeviceSupport.DeviceType mDeviceType;

        public JamboBleScanCallback(JamBoHelper helper) {
            this.mJamBoHelperWeakReference = new WeakReference<>(helper);
            jamboBleGattCallback = new JamboBleGattCallback(mJamBoHelperWeakReference.get());
        }

        public void setDeviceType(BTDeviceSupport.DeviceType deviceType) {
            this.mDeviceType = deviceType;
        }

        @Override
        public void onScanFinished(List<BleDevice> scanResultList) {
            mJamBoHelperWeakReference.get().onBTDeviceFound(scanResultList);
        }

        @Override
        public void onScanStarted(boolean success) {
            mJamBoHelperWeakReference.get().onBTStateChanged(BleState.SCAN_START);
        }

        @Override
        public void onScanning(BleDevice bleDevice) {
            BTDevice device = BTDeviceSupport.checkSupport(bleDevice, mDeviceType);
            if (device != null) {
                jamboBleGattCallback.setBtDevice(device);
                switch (mDeviceType) {
                    case FAT_SCALE:
                        mJamBoHelperWeakReference.get().onBTStateChanged(BleState.SCAN_STOP);
                        BleManager.getInstance().cancelScan();
                        JamboQnHelper.getInstance().connectDevice(mJamBoHelperWeakReference.get(), bleDevice);
                        break;
                    case BLOOD_PRESSURE:
                    case BLOOD_SUGAR:
                    case URIC_ACID:
                    case OXIMETER:
                    case THREEONONE:
                        BleManager.getInstance().connect(bleDevice, jamboBleGattCallback);
                        break;
                    case SLEEPLIGHT:
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private static class JamboBleGattCallback extends BleGattCallback {
        private WeakReference<JamBoHelper> mJamBoHelperWeakReference;
        private BleDevice mBleDevice;
        private BTDevice mBtDevice;

        public JamboBleGattCallback(JamBoHelper jamBoHelper) {
            this.mJamBoHelperWeakReference = new WeakReference<>(jamBoHelper);
        }

        public void setBleDevice(BleDevice mBleDevice) {
            this.mBleDevice = mBleDevice;
        }

        public void setBtDevice(BTDevice mBtDevice) {
            this.mBtDevice = mBtDevice;
        }

        @Override
        public void onStartConnect() {
            BleManager.getInstance().cancelScan();
            mJamBoHelperWeakReference.get().onBTStateChanged(BleState.CONNECTING);
        }

        @Override
        public void onConnectFail(BleDevice bleDevice, BleException exception) {
            JamBoHelper helper = mJamBoHelperWeakReference.get();
            if (helper != null) {
                helper.onBTStateChanged(BleState.CONNECT_FAILED);
            }
            LogUtils.e(exception != null ? exception.getDescription() : null);
        }

        @Override
        public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
            JamBoHelper helper = mJamBoHelperWeakReference.get();
            if (helper != null) {
                helper.onBTStateChanged(BleState.CONNECTED);
                this.setBleDevice(bleDevice);
                if (mBtDevice != null) {
                    helper.setBtDevice(mBtDevice);
                    BleManager.getInstance().notify(bleDevice,
                            mBtDevice.serviceUUID,
                            mBtDevice.notifyCharacterUUID, new BleNotifyCallback() {

                                @Override
                                public void onNotifySuccess() {
                                    if (mBtDevice instanceof OximeterDevice) {
                                        //初始化inputStream和outputStream
                                        OxiMeterHelper mOxiMeterHelper = new OxiMeterHelper(bleDevice, mBtDevice);
                                        ((OximeterDevice) mBtDevice).setOximeterHelper(mOxiMeterHelper);
                                        mJamBoHelperWeakReference.get().onNotification();
                                    }
                                }

                                @Override
                                public void onNotifyFailure(BleException exception) {
                                    LogUtils.e(exception != null ? exception.getDescription() : null);
                                }

                                @Override
                                public void onCharacteristicChanged(byte[] data) {
                                    if (data != null) {
                                        BTData btData = mBtDevice.paserData(data);
                                        helper.onBTDataReceived(btData);
                                    }
                                }
                            });

                    //不需要发送命令，配置写特征即可
                    if (mBtDevice.needWriteCommand()) {
                        if (mBtDevice instanceof SannuoAnWenBloodSugar) {
                            List<BluetoothGattService> services = gatt.getServices();
                            for (BluetoothGattService service : services) {
                                List<BluetoothGattCharacteristic> gattCharacteristics = service.getCharacteristics();
                                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                                    String uuid = gattCharacteristic.getUuid().toString();
                                    if (uuid.equalsIgnoreCase(mBtDevice.writeCharacterUUID)) {// 血压测量特征UUID
                                        if (mBtDevice.needCheckProperties()) {
                                            final int charaProp = gattCharacteristic.getProperties();
                                            boolean property_write = (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0
                                                    | (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
                                            if (!property_write) {
                                                break;
                                            }
                                        }
                                        dealDescriptorWrite(gatt, mBtDevice, gattCharacteristic, true);
                                        break;
                                    }
                                }
                            }
                        } else if (mBtDevice instanceof OnCallBloodSugar) {
                            BleManager.getInstance().write(bleDevice, mBtDevice.serviceUUID, mBtDevice.writeCharacterUUID, ((OnCallBloodSugar) mBtDevice).getStartCommand(), new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                        }
                    }
                }
            }

        }

        @Override
        public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
            JamBoHelper helper = (JamBoHelper) this.mJamBoHelperWeakReference.get();
            BleManager.getInstance().stopNotify(this.mBleDevice, mBtDevice.serviceUUID, mBtDevice.notifyCharacterUUID, false);
            if (helper != null) {
                helper.setBtDevice(null);
                helper.onBTStateChanged(BleState.DISCONNECT);
            }
        }


        private void dealDescriptorWrite(BluetoothGatt gatt, BTDevice btDevice, BluetoothGattCharacteristic characteristic, boolean enable) {
            gatt.setCharacteristicNotification(characteristic, enable);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            if (descriptor != null) {
                if (enable) {
                    descriptor.setValue(btDevice.getDescriptorEnabledValue());
                } else {
                    descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                }
                gatt.writeDescriptor(descriptor);
            }
        }
    }

}
