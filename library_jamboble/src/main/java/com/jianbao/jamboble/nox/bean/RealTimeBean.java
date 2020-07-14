package com.jianbao.jamboble.nox.bean;


import java.nio.ByteBuffer;

/**
 * <p>Title: RealTimeBean </p>
 * <p>Description:实时数据的封装对象 </p>
 *
 * @author wenlong
 *         <p>
 *         2015年10月20日
 */
public class RealTimeBean {

    /**
     * 描述：心跳速率
     */
    private short heartRate;

    /**
     * 描述：呼吸速率
     */
    private short breathRate;

    /**
     * 描述：状态
     */
    private byte status;

    /**
     * 描述：状态值
     */
    private int statusValue;

    /**
     * 描述：原始信号
     */
    private int raw;

    /**
     * 描述：呼吸原始信号
     */
    private int breathRaw;

    /**
     * 描述：心跳原始信号
     */
    private int heartRaw;

    /**
     * 入睡标识，1标识入睡，0未入睡
     */
    private int sleepFlag;

    /**
     * 清醒标识，1标识清醒，0未清醒
     */
    private int wakeFlag;


    public int getBreathRaw() {
        return breathRaw;
    }

    public void setBreathRaw(int breathRaw) {
        this.breathRaw = breathRaw;
    }

    public int getHeartRaw() {
        return heartRaw;
    }

    public void setHeartRaw(int heartRaw) {
        this.heartRaw = heartRaw;
    }

    /**
     * <p>环境温度</p>
     */
    private float eTemp;

    /**
     * <p>环境湿度</p>
     */
    private int eWet;

    /**
     * <p>环境光强</p>
     */
    private int eLight;

    /**
     * <p>环境co2</p>
     */
    private int eCo2;

    /**
     * <p>环境噪音</p>
     */
    private short eNoise;

    /**
     * <p>床垫温度</p>
     */
    private float bedTemp;

    /**
     * <p>床垫湿度</p>
     */
    private byte bedWet;


    private int happenTime;

    /**
     * <p>设备类型</p>
     */
    private short deviceState = -1;


    public short getBreathRate() {
        return breathRate;
    }

    public void setBreathRate(byte breathRate) {
        this.breathRate = breathRate;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public int getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(int statusValue) {
        this.statusValue = statusValue;
    }

    public short getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(short heartRate) {
        this.heartRate = heartRate;
    }

    public int getRaw() {
        return raw;
    }

    public void setRaw(int raw) {
        this.raw = raw;
    }

    public float geteTemp() {
        return eTemp;
    }

    public void seteTemp(float eTemp) {
        this.eTemp = eTemp;
    }

    public int geteWet() {
        return eWet;
    }

    public void seteWet(int eWet) {
        this.eWet = eWet;
    }

    public int geteLight() {
        return eLight;
    }

    public void seteLight(int eLight) {
        this.eLight = eLight;
    }

    public int geteCo2() {
        return eCo2;
    }

    public void seteCo2(int eCo2) {
        this.eCo2 = eCo2;
    }

    public short geteNoise() {
        return eNoise;
    }

    public void seteNoise(short eNoise) {
        this.eNoise = eNoise;
    }

    public float getBedTemp() {
        return bedTemp;
    }

    public void setBedTemp(float bedTemp) {
        this.bedTemp = bedTemp;
    }

    public byte getBedWet() {
        return bedWet;
    }

    public void setBedWet(byte bedWet) {
        this.bedWet = bedWet;
    }

    /**
     * byte数组转化为 RealTimeBean
     * @return
     */
    public static RealTimeBean byte2RealTimeBean(ByteBuffer buffer) {
        RealTimeBean bean = new RealTimeBean();
        bean.happenTime = buffer.getInt();
        bean.heartRate = buffer.get();
        bean.breathRate = buffer.get();
        bean.status = buffer.get();
        bean.statusValue = buffer.getShort();
        bean.eTemp = buffer.getShort();
        bean.eWet = buffer.get();
        bean.eLight = buffer.getShort();
        bean.eCo2 = buffer.getShort();
        bean.eNoise = buffer.get();
        bean.bedTemp = buffer.getShort();
        bean.bedWet = buffer.get();
        return bean;
    }

    public int getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(int happenTime) {
        this.happenTime = happenTime;
    }


    public int getSleepFlag() {
        return sleepFlag;
    }

    public void setSleepFlag(int sleepFlag) {
        this.sleepFlag = sleepFlag;
    }

    public int getWakeFlag() {
        return wakeFlag;
    }

    public void setWakeFlag(int wakeFlag) {
        this.wakeFlag = wakeFlag;
    }


    @Override
    public String toString() {
        return "RealTimeBean [heartRate=" + heartRate + ", breathRate="
				+ breathRate + ", status=" + status + ", statusValue="
				+ statusValue + ", raw=" + raw + ", breathRaw=" + breathRaw
				+ ", heartRaw=" + heartRaw + ", sleepFlag=" + sleepFlag
				+ ", wakeFlag=" + wakeFlag + ", eTemp=" + eTemp + ", eWet="
				+ eWet + ", eLight=" + eLight + ", eCo2=" + eCo2 + ", eNoise="
				+ eNoise + ", bedTemp=" + bedTemp + ", bedWet=" + bedWet
				+ ", happenTime=" + happenTime + ", deviceState=" + deviceState
				+ "]";
       // return "RealTimeBean [heartRate=" + heartRate + ", breathRate=" + breathRate + ", status=" + status + ", statusValue=" +
         //       statusValue + ", sleepFlag=" + sleepFlag + ", wakeFlag=" + wakeFlag + ", deviceState=" + deviceState + "]";
    }

    public short getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(short deviceState) {
        this.deviceState = deviceState;
    }


}
