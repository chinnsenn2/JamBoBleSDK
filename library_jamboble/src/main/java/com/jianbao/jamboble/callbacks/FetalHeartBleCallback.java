package com.jianbao.jamboble.callbacks;

import com.jianbao.fastble.data.BleDevice;

import java.util.List;

/**
 * 胎心设备扫描回调
 */
public interface FetalHeartBleCallback extends BleDataCallback {
    void onBTDeviceFound(List<BleDevice> list);

    void onBTDeviceScanning(BleDevice device);
}
