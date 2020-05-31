package com.jianbao.jamboble.device.nox.interfaces;


import com.jianbao.jamboble.device.nox.BaseCallback;
import com.jianbao.jamboble.device.nox.ConnectionState;
import com.jianbao.jamboble.device.nox.Device;
import com.jianbao.jamboble.device.nox.manager.DeviceManager;

/**
 * Created by Hao on 2016/8/3.
 */

public interface IDeviceManager {
    /**
     * 睡眠标记回调
     */
    int TYPE_METHOD_BIND_PROGRESS = 20001;
    int TYPE_METHOD_UPLOAD_BIND_INFO = 20002;

    /**
     * 注册监听，如果sender为null则回调所有存在的callback,否则回调本类
     *
     * @param callBack
     * @param sender   发送者，建议填类的simpleName，只回调给本类,如果为null则回调所有存在的callback
     */
    void registCallBack(BaseCallback callBack, String sender);

    /**
     * 移除回调
     *
     * @param callBack
     */
    void unRegistCallBack(BaseCallback callBack);

    /**
     * 连接设备
     */
    //void connectDevice(Device device);

    /**
     * 连接设备
     */
    void connectDevice();

    void connectDevice(Device device);

    void connectDevice(DeviceManager.ConnectType type);
    /**
     * 获取连接状态
     *
     * @return
     */
    ConnectionState getConnectionState();

    /**
     * 是否连接成功
     *
     * @return
     */
    boolean isConnected();

    /**
     * 断开连接，监听还在
     */
    void disconnect();

    /**
     * 关闭连接，释放资源，监听释放
     */
    void release();

    void setSender(String sender);

    String getSender();


    /**
     * 绑定设备后需要配置的信息
     */
    void configDeviceAfterBindSync();

    void setDevice(Device device);

    float getVersionCode();

    short getDeviceType();
}
