package com.jianbao.jamboble.fetalheart;

import android.widget.Toast;

import com.jianbao.fastble.BleManager;
import com.jianbao.fastble.callback.BleScanCallback;
import com.jianbao.fastble.data.BleDevice;
import com.jianbao.fastble.scan.BleScanRuleConfig;
import com.jianbao.jamboble.BleState;
import com.jianbao.jamboble.callbacks.FetalHeartBleCallback;
import com.jianbao.jamboble.data.FetalHeartData;

import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.jianbao.jamboble.fetalheart.FetalHeartConnector.CONNECT_SUCCESS;
import static com.jianbao.jamboble.fetalheart.FetalHeartConnector.DISCONNECT;

public class FetalHeartHelper {

    private FetalHeartHelper() {
        mFetalHeartConnector = new FetalHeartConnector();
        mFetalHeartConnector.setCallback(new FetalHeartConnector.Callback() {
            @Override
            public void dispInfor(@Nullable FetalHeartData data) {
                mFetalHeartBleCallback.onBTDataReceived(data);
            }

            @Override
            public void dispServiceStatus(int status) {
                if (status == CONNECT_SUCCESS) {
                    mFetalHeartBleCallback.onBTStateChanged(BleState.CONNECTED);
                } else if (status == FetalHeartConnector.CONNECT_FAILED || status == FetalHeartConnector.READ_DATA_FAILED) {
                    mFetalHeartBleCallback.onBTStateChanged(BleState.CONNECT_FAILED);
                } else if (status == DISCONNECT) {
                    mBleDevice = null;
                    mFetalHeartBleCallback.onBTStateChanged(BleState.DISCONNECT);
                }
            }
        });
        mFetalHeartBleScanCallback = new FetalHeartBleScanCallback(this);
    }

    static BleScanRuleConfig fetalHeartBleScanRuleConfig = new BleScanRuleConfig.Builder()
            .setDeviceName(
                    true,
                    "iFM", "LCiFM"
            ) // 只扫描指定广播名的设备，可选
            .setAutoConnect(true) // 连接时的autoConnect参数，可选，默认false
            .setScanTimeOut(30000) // 扫描超时时间，可选，默认10秒
            .build();

    private static class Singleton {
        private final static FetalHeartHelper HELPER = new FetalHeartHelper();
    }

    public static FetalHeartHelper getInstance() {
        return Singleton.HELPER;
    }

    private FetalHeartBleScanCallback mFetalHeartBleScanCallback;
    private FetalHeartBleCallback mFetalHeartBleCallback;
    private FetalHeartConnector mFetalHeartConnector;
    private BleDevice mBleDevice;

    public void scanFetalHeartDevice() {
        if (!BleManager.getInstance().isBlueEnable()) {
            showToast("请先打开蓝牙");
            return;
        }
        BleManager.getInstance().initScanRule(fetalHeartBleScanRuleConfig);
        BleManager.getInstance().scan(mFetalHeartBleScanCallback);
    }

    public void stopScan() {
        if (!BleManager.getInstance().isBlueEnable()) {
            showToast("请先打开蓝牙");
            return;
        }
        BleManager.getInstance().cancelScan();
    }

    public void setFetalHeartBleCallback(FetalHeartBleCallback fetalHeartBleCallback) {
        this.mFetalHeartBleCallback = fetalHeartBleCallback;
    }

    public void showToast(String msg) {
        Toast.makeText(BleManager.getInstance().getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void connectDevice(BleDevice device) {
        if (!BleManager.getInstance().isBlueEnable()) {
            showToast("请先打开蓝牙");
            return;
        }
        this.mBleDevice = device;
        mFetalHeartConnector.setBluetoothDevice(device.getDevice());
        mFetalHeartConnector.start();
    }

    public void disconnect() {
        mFetalHeartConnector.cancel();
    }

    public BleDevice getConnectedDevice() {
        return this.mBleDevice;
    }

    public void destroy() {
        mFetalHeartBleScanCallback = null;
        mFetalHeartBleCallback = null;
        mBleDevice = null;
        if (mFetalHeartConnector != null) {
            mFetalHeartConnector.destroy();
            mFetalHeartConnector = null;
        }
        BleManager.getInstance().destroy();
    }
    
    private static class FetalHeartBleScanCallback extends BleScanCallback {
        private WeakReference<FetalHeartHelper> mJamBoHelperWeakReference;

        public FetalHeartBleScanCallback(FetalHeartHelper helper) {
            this.mJamBoHelperWeakReference = new WeakReference<>(helper);
        }
        
        @Override
        public void onScanFinished(List<BleDevice> scanResultList) {
            mJamBoHelperWeakReference.get().mFetalHeartBleCallback.onBTStateChanged(BleState.SCAN_STOP);
            mJamBoHelperWeakReference.get().mFetalHeartBleCallback.onBTDeviceFound(scanResultList);
        }

        @Override
        public void onScanStarted(boolean success) {
            mJamBoHelperWeakReference.get().mFetalHeartBleCallback.onBTStateChanged(BleState.SCAN_START);
        }

        @Override
        public void onScanning(BleDevice bleDevice) {
            mJamBoHelperWeakReference.get().mFetalHeartBleCallback.onBTDeviceScanning(bleDevice);
        }
    }
}
