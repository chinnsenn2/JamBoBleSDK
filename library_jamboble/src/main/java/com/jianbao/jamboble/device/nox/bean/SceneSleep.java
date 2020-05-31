package com.jianbao.jamboble.device.nox.bean;

public class SceneSleep extends SceneBase {
    /**
     * 监测设备是否打开 0关1开
     */
    public int monitorOpenFlag;
    /**
     * 监测设备类型
     */
    public int monitorDevictType;
    /**
     * 监测设备ID
     */
    public String monitorDeviceId;
    /**
     * 睡眠辅助设备是否打开，0开1关
     */
    public int sleepAidOpenFlag;
    /**
     * 睡眠辅助设备参数
     */
    public SceneConfigBase sleepAidConfig;
    /**
     * 智能闹钟是否打开
     */
    public int smartAlarmOpenFlag;

    @Override
    public String toString() {
        return  super.toString() + "SceneSleep{" +
                "monitorOpenFlag=" + monitorOpenFlag +
                ", monitorDevictType=" + monitorDevictType +
                ", monitorDeviceId='" + monitorDeviceId + '\'' +
                ", sleepAidOpenFlag=" + sleepAidOpenFlag +
                ", sleepAidConfig=" + sleepAidConfig +
                ", smartAlarmOpenFlag=" + smartAlarmOpenFlag +
                '}';
    }
}