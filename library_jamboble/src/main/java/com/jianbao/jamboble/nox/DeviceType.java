package com.jianbao.jamboble.nox;

/**
 * Created by 阿豪 on 16/6/17.
 */

public class DeviceType {
    /**
     * <p>
     * 设备型号 手机监测睡眠
     * </p>
     */
    public static final short DEVICE_TYPE_PHONE = -1;

    /**
     * <p>
     * 设备型号 RestOn Z1
     * </p>
     */
    public static final short DEVICE_TYPE_RESTON_Z1 = 0x0001;
    /**
     * <p>
     * 设备型号 Nox,nox一代
     * </p>
     */
    public static final short DEVICE_TYPE_NOX_PRO = 0x0002;

    /**
     * <p>
     * 设备型号 智能枕头
     * </p>
     */
    public static final short DEVICE_TYPE_PILLOW = 0x0003;

    public static final short DEVICE_TYPE_RESTON_WIFI = 0x4;

    /**
     * 设备类型，单人版 蓝牙床垫
     */
    public static final short DEVICE_TYPE_BLEREST_SIMPLE = 5;

    /**
     * 设备类型，单人版 wifi床垫
     */
    public static final short DEVICE_TYPE_M500 = 6;

    /**
     * 设备类型，双人版 wifi床垫
     */
    public static final short DEVICE_TYPE_BLEREST_DOUBLE = 7;

    /**
     * 设备类型，双人版 wifi床垫
     */
    public static final short DEVICE_TYPE_M600 = 8;
    /**
     * 设备类型，RestOn Z2
     */
    public static final short DEVICE_TYPE_RESTON_Z2 = 9;
    /**
     * 设备类型，枕扣
     */
    public static final short DEVICE_TYPE_SLEEPDOT = 10;
    /**
     * <p>
     * 设备型号 Nox2 蓝牙版
     * </p>
     */
    public static final short DEVICE_TYPE_NOX_2B = 11;
    /**
     * 设备类型，纽扣 B501-2
     */
    public static final short DEVICE_TYPE_SLEEPDOT_502 = 0x10;
    /**
     * 设备类型，纽扣 B502T，有温湿度
     */
    public static final short DEVICE_TYPE_SLEEPDOT_502T = 0x11;
    /**
     * 设备型号 Nox2 wifi版
     */
    public static final short DEVICE_TYPE_NOX_2W = 0x00c;
    /**
     * 设备型号Reston Z4系列
     */
    public static final short DEVICE_TYPE_RESTON_Z4 = 0x16;
    /**
     * 设备型号NoxSAW 香薰灯国际版（WiFi）
     */
    public static final short DEVICE_TYPE_NOX_SAW = 0x17;
    /**
     * 设备型号NoxSAB 香薰灯国内电商版
     */
    public static final short DEVICE_TYPE_NOX_SAB = 0x18;

    //--------------------------------------------------------------------------------

    /*
    * 场景里面，可以选择无设备，无设备类型
    * */
    public static final short DEVICE_TYPE_NULLL = 20000;

    /*
    * 无效的设备类型
    * */
    public static final short DEVICE_TYPE_INVALID = 0;

    /**
     * 眼罩
     */
    public static final short DEVICE_TYPE_PATCH = 0x0012;


    public static boolean isMonitorDevice(short deviceType) {
        return isReston(deviceType) || isSleepDot(deviceType) || isPillow(deviceType) || isPhone(deviceType);
    }

    public static boolean isSleepAidDevice(short deviceType){
        return isPhone(deviceType) || isNox(deviceType);
    }


    /**
     * 判断参数的设备类型是否是支持心率呼吸率的监测设备
     * @param deviceType
     */
    public static boolean isHeartBreathDevice(int deviceType) {
        return isReston(deviceType) || isPillow(deviceType);
    }

    public static boolean isPillow(int deviceType){
        return deviceType == DEVICE_TYPE_PILLOW;
    }

    /**
     * 是否是BLE设备
     * @param deviceType
     * @return
     */
    public static boolean isBleDevice(int deviceType) {
        return isSleepDot(deviceType) || isReston(deviceType) || isPillow(deviceType) || deviceType == DEVICE_TYPE_NOX_2B || deviceType == DEVICE_TYPE_NOX_SAB;
    }


    /**
     * 是否是Reston系列设备
     * @param deviceType
     * @return
     */
    public static boolean isReston(int deviceType) {
        return deviceType == DEVICE_TYPE_RESTON_Z1
                || deviceType == DEVICE_TYPE_RESTON_Z2
                || deviceType == DEVICE_TYPE_RESTON_Z4;
    }

    public static boolean isNox(int deviceType) {
        return  isNox1(deviceType) || isNox2(deviceType) || isNoxSa(deviceType);
    }

    public static boolean isNox1(int deviceType) {
        return deviceType == DEVICE_TYPE_NOX_PRO;
    }

    public static boolean isNox2(int deviceType) {
        return deviceType == DEVICE_TYPE_NOX_2B
                || deviceType == DEVICE_TYPE_NOX_2W;
    }

    public static boolean isNoxSa(int deviceType) {
        return deviceType == DEVICE_TYPE_NOX_SAB
                || deviceType == DEVICE_TYPE_NOX_SAW;
    }

    public static boolean isSleepDot(int deviceType) {
        return  deviceType == DEVICE_TYPE_SLEEPDOT
                || deviceType == DEVICE_TYPE_SLEEPDOT_502
                || deviceType == DEVICE_TYPE_SLEEPDOT_502T;
    }

    public static boolean isPhone(int deviceType) {
        return  deviceType == DEVICE_TYPE_PHONE;
    }


}
