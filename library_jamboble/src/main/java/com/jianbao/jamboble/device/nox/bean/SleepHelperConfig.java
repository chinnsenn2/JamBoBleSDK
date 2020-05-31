package com.jianbao.jamboble.device.nox.bean;


import java.io.Serializable;

public class SleepHelperConfig implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 睡眠辅助智能停止时持续时间，单位分钟
     */
    public static final int SMART_STOP_TIME = 10;


    public static final int DEFAULT_VOLUME = 9;
    public static final int DEFAULT_LIGHT = 30;


    /**
     * 睡眠辅助状态信息 关闭状态
     */
    public static final int STATUS_OFF = 0;
    /**
     * 睡眠辅助状态信息 开启状态
     */
    public static final int STATUS_ON = 1;
    /**
     * 睡眠辅助状态信息 暂停状态
     */
    public static final int STATUS_PAUSE = 2;

    /**
     * 全局开关
     * 0：未开启
     * 1：开启
     */
    public int enable = 1;

    /**
     * 音乐开关
     */
    public int musicFlag;


    /**
     * 睡眠辅助状态信息
     */
    public int status = STATUS_OFF;


    private boolean lightState;

    /**
     * 音乐id
     */
    public short musicId;
    /**
     * 音量大小
     */
    public int volume;
    /**
     * 灯光亮度
     */
    public int light;
    /**
     * 持续时间，单位分钟
     */
    public int continueTime;


    public int lightR, lightG, lightB, lightW;


    public SleepHelperConfig() {
        init();
    }


    public void init() {
        enable = 1;
        musicFlag = 1;
        volume = DEFAULT_VOLUME;
        light = DEFAULT_LIGHT;
        continueTime = SMART_STOP_TIME;
    }


    public String getJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"flag\":").append(enable).append(",");
        sb.append("\"musicFlag\":").append(musicFlag).append(",");
        sb.append("\"musicSeqid\":").append(musicId).append(",");
        sb.append("\"volum\":").append(volume).append(",");
        sb.append("\"lightIntensity\":").append(light).append(",");
        sb.append("\"smartStopFlag\":").append(continueTime == SMART_STOP_TIME ? 1 : 0).append(",");
        sb.append("\"aidingTime\":").append(continueTime);
        sb.append("}");
        return sb.toString();
    }


    @Override
    public String toString() {
        return "SleepHelperConfig [flag=" + enable + ", musicFlag=" + musicFlag + ", status=" + status + ", lightState=" + lightState
                + ", musicId=" + musicId + ", volume=" + volume + ", light=" + light + ", continueTime=" + continueTime + ", lightR=" + lightR + ", lightG=" + lightG + ", lightB=" + lightB + ", lightW=" + lightW + "]";
    }


    public boolean isLightState() {
        return lightState;
    }


    public void setLightState(boolean lightState) {
        this.lightState = lightState;
    }

    public boolean isSmartStop() {
        return continueTime == SMART_STOP_TIME;
    }


    public void copy(SleepHelperConfig config) {
        if (config != null) {
            this.enable = config.enable;
            this.musicFlag = config.musicFlag;
            this.musicId = config.musicId;
            this.volume = config.volume;
            this.light = config.light;
            this.continueTime = config.continueTime;
            this.lightR = config.lightR;
            this.lightG = config.lightG;
            this.lightB = config.lightB;
            this.lightW = config.lightW;
        }
    }

}























