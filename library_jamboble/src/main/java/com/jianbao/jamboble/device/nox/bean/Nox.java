package com.jianbao.jamboble.device.nox.bean;


import com.jianbao.jamboble.device.nox.BleDevice;
import com.jianbao.jamboble.device.nox.DeviceType;

/**
 *nox的wifi版的封装对象，包含一代，二代的wifi版
 */
public class Nox extends BleDevice {

    /**
     * 温度单位：摄氏度
     */
    public final static byte UNIT_TEMPERATURE_C = 1;

    /**
     * 温度单位：华氏度
     */
    public final static byte UNIT_TEMPERATURE_F = 2;

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 温度显示单位
     */
    public byte tempUnit = UNIT_TEMPERATURE_C;

    /**
     * 时钟休眠的信息
     */
    public NoxClockSleep noxClockSleep = new NoxClockSleep();
    /**
     * 小夜灯配置
     */
    public NoxLight smallLight;
    //wifi名称，nox1和nox2 wifi版才有
    public String wifiName;
    @Override
    public String toString() {
        return "Nox{" +
                "hashCode=" + hashCode() +
                ", smallLight=" + smallLight +
                ", tempUnit=" + tempUnit +
                ", noxClockSleep=" + noxClockSleep +
                ", wifiName=" + wifiName +
                "  super.toString():  "+super.toString()+
                '}';
    }

    public Nox(){
        this.deviceType = DeviceType.DEVICE_TYPE_NOX_PRO;
    }


}
