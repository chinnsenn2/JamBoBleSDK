package com.jianbao.jamboble.nox.bean;

/**
 * Created by Administrator on 2016/7/22.
 */

public class SceneConfigBase extends BaseBean {

    /*
    *
    */
    private long seqid;

    /**
     * 是否启用该场景，0：不启用，1：启用，默认：1
     */
    private byte enable = 1;

    /*
    * 场景Id
    */
    private int sceneId;

    /*
    * 子场景Id
    */
    private int sceneSubId;

    /**
     * 场景类型
     */
    private byte sceneType;

    /*
    * 设备Id
    */
    private String deviceId;

    /*
    *
    */
    private short deviceType;

    /*
    *
    */
    private long userId;

    /*
    * 助眠 - 总开关 0:关 其他:开
    */
    private int sleepAidingflag = 1;

    /*
    * 助眠 - 音乐开关  -1:无意义
    */
    private int musicFlag = 1;

    /*
    * 助眠 - musicSeqid  -1:无意义
    */
    private long musicSeqid;

    private int volum;

    /*
    * 智能停止 - 开关,0:关 其他:开  -1:无意义
    */
    private int smartStopFlag;

    /*
    * 智能停止 - 辅助停止时长 单位:分钟  -1:无意义
    */
    private int aidingTime;

    /**
    * 助眠 - 1:本地，2:外部蓝牙
    */
    private int musicFrom;

    private String musicChannel;//如果musicFrom=1时，请传入音乐来源，例如musicChannel=1000（喜马拉雅）
    private String musicType;//如果musicChannel有值时，请音乐类型：1 专辑 2 声音



    public SceneConfigBase() {

    }

    public SceneConfigBase(SceneConfigBase sceneConfigBase) {
        this.seqid = sceneConfigBase.getSeqid();
        this.sceneId = sceneConfigBase.getSceneId();
        this.sceneSubId = sceneConfigBase.getSceneSubId();
        this.deviceId = sceneConfigBase.getDeivceId();
        this.deviceType = sceneConfigBase.getDeviceType();
        this.userId = sceneConfigBase.getUserId();
        this.sleepAidingflag = sceneConfigBase.getSleepAidingflag();
        this.musicFlag = sceneConfigBase.getMusicFlag();
        this.musicSeqid = sceneConfigBase.getMusicSeqid();
        this.smartStopFlag = sceneConfigBase.getSmartStopFlag();
        this.aidingTime = sceneConfigBase.getAidingTime();
        this.volum = sceneConfigBase.getVolume();
        this.musicFrom = sceneConfigBase.getMusicFrom();
        this.musicChannel = sceneConfigBase.getMusicChannel();
        this.musicType = sceneConfigBase.getMusicType();
    }

    public void copy(SceneConfigBase sceneConfigBase) {
        this.seqid = sceneConfigBase.getSeqid();
        this.sceneId = sceneConfigBase.getSceneId();
        this.sceneSubId = sceneConfigBase.getSceneSubId();
        this.deviceId = sceneConfigBase.getDeivceId();
        this.deviceType = sceneConfigBase.getDeviceType();
        this.userId = sceneConfigBase.getUserId();
        this.sleepAidingflag = sceneConfigBase.getSleepAidingflag();
        this.musicFlag = sceneConfigBase.getMusicFlag();
        this.musicSeqid = sceneConfigBase.getMusicSeqid();
        this.smartStopFlag = sceneConfigBase.getSmartStopFlag();
        this.aidingTime = sceneConfigBase.getAidingTime();
        this.volum = sceneConfigBase.getVolume();
        this.musicFrom = sceneConfigBase.getMusicFrom();
        this.musicChannel = sceneConfigBase.getMusicChannel();
        this.musicType = sceneConfigBase.getMusicType();
    }

    public void setVolume(int volume) {
        this.volum = volume;
    }

    public int getVolume() {
        return volum;
    }

    public int getAidingTime() {
        return aidingTime;
    }

    public void setAidingTime(int aidingTime) {
        this.aidingTime = aidingTime;
    }

    public int getSmartStopFlag() {
        return smartStopFlag;
    }

    public void setSmartStopFlag(int smartStopFlag) {
        this.smartStopFlag = smartStopFlag;
    }

    public long getSeqid() {
        return seqid;
    }

    public void setSeqid(long seqid) {
        this.seqid = seqid;
    }

    public int getSceneId() {
        return sceneId;
    }

    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }

    public int getSceneSubId() {
        return sceneSubId;
    }

    public void setSceneSubId(int sceneSubId) {
        this.sceneSubId = sceneSubId;
    }

    public String getDeivceId() {
        return deviceId;
    }

    public void setDeivceId(String deivceId) {
        this.deviceId = deivceId;
    }

    public short getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(short deviceType) {
        this.deviceType = deviceType;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getSleepAidingflag() {
        return sleepAidingflag;
    }

    public void setSleepAidingflag(int sleepAidingflag) {
        this.sleepAidingflag = sleepAidingflag;
    }

    public int getMusicFlag() {
        return musicFlag;
    }

    public void setMusicFlag(int musicFlag) {
        this.musicFlag = musicFlag;
    }

    public long getMusicSeqid() {
        return musicSeqid;
    }

    public void setMusicSeqid(long musicSeqid) {
        this.musicSeqid = musicSeqid;
    }

    public void setEnable(byte enable) {
        this.enable = enable;
    }

    public byte getEnable() {
        return enable;
    }

    public void setSceneType(byte sceneType) {
        this.sceneType = sceneType;
    }

    public byte getSceneType() {
        return sceneType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getMusicFrom() {
        return musicFrom;
    }

    public void setMusicFrom(int musicFrom) {
        this.musicFrom = musicFrom;
    }

    public String getMusicChannel() {
        return musicChannel;
    }

    public void setMusicChannel(String musicChannel) {
        this.musicChannel = musicChannel;
    }

    public String getMusicType() {
        return musicType;
    }

    public void setMusicType(String musicType) {
        this.musicType = musicType;
    }

    @Override
    public String toString() {
        return "SceneConfigBase{" +
                "seqid=" + seqid +
                ", enable=" + enable +
                ", sceneId=" + sceneId +
                ", sceneSubId=" + sceneSubId +
                ", sceneType=" + sceneType +
                ", deviceId='" + deviceId + '\'' +
                ", deviceType=" + deviceType +
                ", userId=" + userId +
                ", sleepAidingflag=" + sleepAidingflag +
                ", musicFlag=" + musicFlag +
                ", musicSeqid=" + musicSeqid +
                ", volum=" + volum +
                ", smartStopFlag=" + smartStopFlag +
                ", aidingTime=" + aidingTime +
                ", musicFrom=" + musicFrom +
                ", musicChannel='" + musicChannel + '\'' +
                ", musicType='" + musicType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SceneConfigBase that = (SceneConfigBase) o;

        if (seqid != that.seqid) return false;
        if (enable != that.enable) return false;
        if (sceneId != that.sceneId) return false;
        if (sceneSubId != that.sceneSubId) return false;
        if (sceneType != that.sceneType) return false;
        if (deviceType != that.deviceType) return false;
        if (userId != that.userId) return false;
        if (sleepAidingflag != that.sleepAidingflag) return false;
        if (musicFlag != that.musicFlag) return false;
        if (musicSeqid != that.musicSeqid) return false;
        if (volum != that.volum) return false;
        if (smartStopFlag != that.smartStopFlag) return false;
        if (aidingTime != that.aidingTime) return false;
        return deviceId != null ? deviceId.equals(that.deviceId) : that.deviceId == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (seqid ^ (seqid >>> 32));
        result = 31 * result + (int) enable;
        result = 31 * result + sceneId;
        result = 31 * result + sceneSubId;
        result = 31 * result + (int) sceneType;
        result = 31 * result + (deviceId != null ? deviceId.hashCode() : 0);
        result = 31 * result + (int) deviceType;
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        result = 31 * result + sleepAidingflag;
        result = 31 * result + musicFlag;
        result = 31 * result + (int) musicSeqid;
        result = 31 * result + volum;
        result = 31 * result + smartStopFlag;
        result = 31 * result + aidingTime;
        return result;
    }
}
