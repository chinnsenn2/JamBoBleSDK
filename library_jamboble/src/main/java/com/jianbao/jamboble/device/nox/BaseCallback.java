package com.jianbao.jamboble.device.nox;

import com.jianbao.jamboble.device.nox.bean.CallbackData;
import com.jianbao.jamboble.device.nox.interfaces.IDeviceManager;

public abstract class BaseCallback {
    private String sender;
    /**
     * 状态回调
     *
     * @param state
     */
    public abstract void onStateChange(IDeviceManager manager, String sender, ConnectionState state);

    /**
     * 数据回调
     *
     * @param callbackData
     */
    public abstract void onDataCallback(CallbackData callbackData);

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "{hashCode:"+ hashCode()+",sender:" + sender+"}";
    }
}