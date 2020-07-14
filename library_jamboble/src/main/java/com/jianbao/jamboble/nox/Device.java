package com.jianbao.jamboble.nox;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.jianbao.jamboble.nox.bean.BaseBean;

import java.util.Arrays;

/**
 * 默认如果deviceId和address相同就认为是同一设备
 */
public class Device extends BaseBean {

    /**
     * <p>初始值</p>
     */
    public static final byte COLLECT_STATUS_INIT_VALUE = -1;
    /**
     * <p>设备没有采集</p>
     */
    public static final byte COLLECT_STATUS_NO_COLLECT = 0x00;
    /**
     * <p>设备正在采集中</p>
     */
    public static final byte COLLECT_STATUS_COLEECTING = 0x01;

    /**
     * 设备采集状态,常量
     */
    public byte collectStatus = COLLECT_STATUS_INIT_VALUE;

    private static final long serialVersionUID = 1L;
    /**
     * 蓝牙地址或者wifi的mac地址
     */
    @Expose
    public String address;

    /**
     * 密文设备ID，如：xdfjsajfdsdf, Z1的密文id和设备名称一样
     */
    @Expose
    public String deviceId;
    /**
     * 设备名称，如：Z2-1410000888
     */
    @Expose
    public String deviceName;
    /**
     * 产品名称，如：Nox智能床头灯
     */
    public String productName;

    public int[] iconRes;       //0表示可选状态  1表示不可选状态（灰色）

    public short deviceType;

    public float versionCode;

    public String versionName;
    /**
     * 渠道商，默认10000
     */
    public String deviceChannel;
    //    细分    0，一般设备的默认值    1，nox2-wifi版设备的默认值     2，女性    3，儿童
    public int deviceSupportType;
    /**
     * 设备材质 Z400T 材质 3 展示温湿度     材质 4 不展示温湿度
     */
    public int material;

    /**
     * 材质类型Z400T，有温湿度
     */
    public static final int MATERIAL_TYPE_Z400T = 3;
    /**
     * 材质类型Z400，没有温湿度
     */
    public static final int MATERIAL_TYPE_Z400 = 4;

    public boolean isMonitor; //是否可用于监测
    public boolean isHelper;  //是否可用于助眠
    public boolean isWakupor;// 是否可用于唤醒（闹铃）
    //在场景添加设备时设备排序列表
    public int order;

    public Device() {
    }

    public float getVersionCode() {
        float verCode = 0f;
        if (!TextUtils.isEmpty(versionName)) {
            verCode = Float.valueOf(versionName);
        }
        return verCode;
    }
    @Override
    public boolean equals(Object o) {

        if (o == null || getClass() != o.getClass()) return false;

        if (this == o) return true;

        Device device = (Device) o;

        if (!TextUtils.isEmpty(address) && address.equals(device.address)) {
            return true;
        }

        if (deviceId.equals(device.deviceId)) {
            return true;
        }

        return deviceName.equals(device.deviceName);

    }

    /*@Override
    public int hashCode() {
        int result = address.hashCode();
        result = 31 * result + deviceId.hashCode();
        return result;
    }*/

    @Override
    public String toString() {
        return "Device{" +
                "collectStatus=" + collectStatus +
                ", address='" + address + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", productName='" + productName + '\'' +
                ", iconRes=" + Arrays.toString(iconRes) +
                ", deviceType=" + deviceType +
                ", versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", deviceChannel='" + deviceChannel + '\'' +
                ", deviceSupportType=" + deviceSupportType +
                ", material=" + material +
                ", isMonitor=" + isMonitor +
                ", isHelper=" + isHelper +
                ", isWakupor=" + isWakupor +
                ", order=" + order +
                '}';
    }
}