package com.jianbao.jamboble.nox.bean;


import com.jianbao.jamboble.nox.Device;
import com.jianbao.jamboble.nox.DeviceType;
import com.jianbao.jamboble.nox.interfaces.INoxManager;
import com.jianbao.jamboble.nox.utils.SPUtils;
import com.jianbao.jamboble.nox.utils.SceneUtils;

import java.util.Date;

public class SceneConfigNox extends SceneConfigBase {

    /*
    * 	助眠 - 助眠时灯光是否开启 0:关 1:开 -1:无意义
    * */
    private int lightFlag;

    /*
    * 助眠 - 灯光亮度(0-100) 0:不亮
    */
    private int lightIntensity;

    /*
    * 助眠 - 灯光RGB
    */
    private String lightRGB;

    /*
    * 助眠 - 灯光W值
    */
    private int lightW;

    private short localMusicSeqid;//助眠 - 助眠音乐，目前只有nox2使用
    /**
     * 香薰开关，香薰灯有效
     */
    private int aromaFlag;
    /**
     * 香薰速度，香薰灯有效
     */
    private int aromaSpeed;

    /**
     * 时长，单位分钟
     */
    private short duration;

    /*
    * 	*/
    private Date updateDate;

    /*
    *
    */
    private Date createDate;


    public NoxLight getNoxLight() {
        NoxLight noxLight = new NoxLight();
        noxLight.lightFlag = (byte) lightFlag;
        noxLight.brightness = (byte) lightIntensity;
        noxLight.lightMode = NoxLight.LightMode.LIGHT_COLOR;
        noxLight.r = (byte) getRed();
        noxLight.g = (byte) getGreen();
        noxLight.b = (byte) getBlue();
        noxLight.w = 0;
        noxLight.fixed_streamer_id = 0;
        noxLight.ctrlMode = INoxManager.SleepAidCtrlMode.SLEEPAID;
        return noxLight;
    }

    private int getRed() {
        return Integer.parseInt(lightRGB.split(",")[0]);
    }

    private int getGreen() {
        return Integer.parseInt(lightRGB.split(",")[1]);
    }

    private int getBlue() {
        return Integer.parseInt(lightRGB.split(",")[2]);
    }


    public SceneConfigNox() {

    }

    public SceneConfigNox(SceneConfigNox sceneConfigNox) {
        super(sceneConfigNox);
        this.lightFlag = sceneConfigNox.getLightFlag();
        this.lightIntensity = sceneConfigNox.getLightIntensity();
        this.lightRGB = sceneConfigNox.getLightRGB();
        this.lightW = sceneConfigNox.getLightW();
        this.updateDate = sceneConfigNox.getUpdateDate();
        this.createDate = sceneConfigNox.getCreateDate();
    }

    public void copy(SceneConfigNox sceneConfigNox) {
        super.copy(sceneConfigNox);
        this.lightFlag = sceneConfigNox.getLightFlag();
        this.lightIntensity = sceneConfigNox.getLightIntensity();
        this.lightRGB = sceneConfigNox.getLightRGB();
        this.lightW = sceneConfigNox.getLightW();
        this.updateDate = sceneConfigNox.getUpdateDate();
        this.createDate = sceneConfigNox.getCreateDate();
    }

    public void init(Device device) {
        if(device == null) {
            return;
        }
        setSceneId(SceneUtils.SLEEP_SCENE_ID);
        setSceneSubId(0);
        setDeviceId(device.deviceId);
        setDeviceType(device.deviceType);
        setUserId(100);
        setSleepAidingflag(1);
        setMusicFlag(1);
        setSmartStopFlag(1);
        setAidingTime(30);
        setVolume(30);
        setMusicFrom(0);
        setMusicChannel("0");
        setMusicType("2");
        setLightFlag(1);
        setLightIntensity(30);
        setLightRGB(255 + "," + 35 + "," + 0);
        setLightW(0);

        if(device.deviceType == DeviceType.DEVICE_TYPE_NOX_PRO) {
            setMusicSeqid(Constants.DEFAUL_NOX_PRO_AID_MUSIC_ID);
        } else if(device.deviceType == DeviceType.DEVICE_TYPE_NOX_2B || device.deviceType == DeviceType.DEVICE_TYPE_NOX_2W){
            setMusicSeqid(Constants.DEFUAL_NOX_2_AID_MUSIC_ID);
        } else if(device.deviceType == DeviceType.DEVICE_TYPE_NOX_SAB || device.deviceType == DeviceType.DEVICE_TYPE_NOX_SAW) {
            setMusicSeqid(Constants.DEFUAL_NOX_2_AID_MUSIC_ID);
            setAromaFlag(0);
            setAromaSpeed(INoxManager.AromatherapySpeed.COMMON.value);
            SPUtils.save(Constants.SP_KEY_SLEEPHELPER_VOLUME, 30);
        }
    }
    public void setLightIntensity(int lightIntensity) {
        this.lightIntensity = lightIntensity;
    }

    public int getLightIntensity() {
        return this.lightIntensity;
    }

    public void setLightRGB(String lightRGB) {
        this.lightRGB = lightRGB;
    }

    public String getLightRGB() {
        int r = Integer.parseInt(lightRGB.split(",")[0]);
        int g = Integer.parseInt(lightRGB.split(",")[1]);
        int b = Integer.parseInt(lightRGB.split(",")[2]);
        //rgb转换为整数
        int red = r & 0xff;
        int green = g & 0xff;
        int blue = b & 0xff;
        //拼接
        String rgb = red + "," + green + "," + blue;
        //返回
//      LogUtil.showMsg("rgb   color "+rgb);
        return rgb;
    }

    public void setLightW(int lightW) {
        this.lightW = lightW;
    }

    public int getLightW() {
        return this.lightW;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getUpdateDate() {
        return this.updateDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public int getLightFlag() {
        return lightFlag;
    }

    public void setLightFlag(int lightFlag) {
        this.lightFlag = lightFlag;
    }

    public short getDuration() {
        return duration;
    }

    public void setDuration(short duration) {
        this.duration = duration;
    }

    public void setAromaFlag(int aromaFlag) {
        this.aromaFlag = aromaFlag;
    }

    public int getAromaFlag() {
        return aromaFlag;
    }

    public void setAromaSpeed(int aromaSpeed) {
        this.aromaSpeed = aromaSpeed;
    }

    public int getAromaSpeed() {
        return aromaSpeed;
    }

    @Override
    public String toString() {
        return super.toString() + "SceneConfigNox{" +
                ", lightIntensity=" + lightIntensity +
                ", lightRGB='" + lightRGB + '\'' +
                ", lightW=" + lightW +
                ", aromaFlag=" + aromaFlag +
                ", aromaSpeed=" + aromaSpeed +
                ", updateDate=" + updateDate +
                ", lightFlag=" + lightFlag +
                ", createDate=" + createDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SceneConfigNox that = (SceneConfigNox) o;

        if (lightFlag != that.lightFlag) return false;
        if (lightIntensity != that.lightIntensity) return false;
        if (lightW != that.lightW) return false;
        if (lightRGB != null ? !lightRGB.equals(that.lightRGB) : that.lightRGB != null)
            return false;
        if (updateDate != null ? !updateDate.equals(that.updateDate) : that.updateDate != null)
            return false;
        return createDate != null ? createDate.equals(that.createDate) : that.createDate == null;


    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + lightFlag;
        result = 31 * result + lightIntensity;
        result = 31 * result + (lightRGB != null ? lightRGB.hashCode() : 0);
        result = 31 * result + lightW;
        result = 31 * result + (updateDate != null ? updateDate.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        return result;
    }
}