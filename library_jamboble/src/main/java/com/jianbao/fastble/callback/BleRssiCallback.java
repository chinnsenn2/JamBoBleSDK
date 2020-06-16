package com.jianbao.fastble.callback;


import com.jianbao.fastble.exception.BleException;

public abstract class BleRssiCallback extends BleBaseCallback {

    public abstract void onRssiFailure(BleException exception);

    public abstract void onRssiSuccess(int rssi);

}