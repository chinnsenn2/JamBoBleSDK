package com.jianbao.jamboble.callbacks;

import com.jianbao.fastble.data.BleDevice;

import java.util.List;

public interface IBleStatusCallback {

    void onBTDeviceFound(List<BleDevice> list);

    void onNotification();
}
