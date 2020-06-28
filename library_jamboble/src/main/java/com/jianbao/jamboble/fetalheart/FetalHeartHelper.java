package com.jianbao.jamboble.fetalheart;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
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
                    setBleDevice(null);
                } else if (status == DISCONNECT) {
                    mBleDevice.setConnected(false);
                    setBleDevice(null);
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

    /**
     * 扫描胎心设备
     */
    public void scanFetalHeartDevice() {
        if (!BleManager.getInstance().isBlueEnable()) {
            showToast("请先打开蓝牙");
            requestEnableBle();
//            BleManager.getInstance().enableBluetooth();
            return;
        }
        BleManager.getInstance().initScanRule(fetalHeartBleScanRuleConfig);
        BleManager.getInstance().scan(mFetalHeartBleScanCallback);
    }

    private void requestEnableBle() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BleManager.getInstance().getContext().startActivity(intent);
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        if (!BleManager.getInstance().isBlueEnable()) {
            showToast("请先打开蓝牙");
            requestEnableBle();
//            BleManager.getInstance().enableBluetooth();
            return;
        }
        BleManager.getInstance().cancelScan();
    }

    /**
     * 设置回调
     *
     * @param fetalHeartBleCallback
     */
    public void setFetalHeartBleCallback(FetalHeartBleCallback fetalHeartBleCallback) {
        this.mFetalHeartBleCallback = fetalHeartBleCallback;
    }

    public void showToast(String msg) {
        Toast.makeText(BleManager.getInstance().getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 连接胎心设备
     *
     * @param device
     */
    public void connectDevice(BleDevice device) {
        if (!BleManager.getInstance().isBlueEnable()) {
            showToast("请先打开蓝牙");
            requestEnableBle();
//            BleManager.getInstance().enableBluetooth();
            return;
        }
        if (this.mBleDevice != null) {
            showToast("请先断开连接的设备");
            return;
        }
        this.mBleDevice = device;
        mFetalHeartConnector.setBluetoothDevice(device.getDevice());
        mFetalHeartConnector.start();
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        mFetalHeartConnector.cancel();
    }

    /**
     * 获取已连接设备
     *
     * @return
     */
    public BleDevice getConnectedDevice() {
        return this.mBleDevice;
    }

    public void setBleDevice(BleDevice mBleDevice) {
        this.mBleDevice = mBleDevice;
    }

    /**
     * 启动录制功能
     * @return 录制音频的路径
     */
    public String startRecord() {
       return mFetalHeartConnector.recordStart();
    }

    /**
     * 结束录制
     */
    public void finishRecord() {
        mFetalHeartConnector.recordFinished();
    }

    //获取录制状态
    public boolean getRecordStatus() {
        return mFetalHeartConnector.getRecordStatus();
    }

    /**
     * 设置宫缩复位
     * @param value 宫缩复位的值
     */
    public void setTocoReset(int value) {
        mFetalHeartConnector.setTocoReset(value);
    }

    /**
     * 设置胎心音量
     * @param value 音量大小的值
     */
    public void setFhrVolume(int value) {
        mFetalHeartConnector.setFhrVolume(value);
    }

    /**
     * 释放资源
     */
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
