package com.jianbao.jamboble.device.nox;


import com.google.gson.annotations.Expose;

public class BleDevice extends Device {
    /**
     * 如果正在采集，开始采集时刻
     */
    public int startCollectTime;
    /**
     * 如果正在采集，采集了多久
     */
    public int collectCountTime;
    /**
     * 普通蓝牙地址，用于Nox2升级用
     */
    @Expose
    public String btAddress;


    /**
     * 设备型号名称，如：Sleepace Z, Sleepace Z2, Sleepace B100
     */
    @Expose
    public String modelName;


    @Override
    public String toString() {
        return super.toString() + "BleDevice{" +
                "btAddress='" + btAddress + '\'' +
                ", modelName='" + modelName + '\'' +
                '}';
    }



}
