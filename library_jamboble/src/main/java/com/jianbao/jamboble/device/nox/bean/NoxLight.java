package com.jianbao.jamboble.device.nox.bean;


import com.appbase.utils.TimeUtil;
import com.google.gson.annotations.Expose;
import com.jianbao.jamboble.device.nox.interfaces.INoxManager;

import java.nio.ByteBuffer;
import java.util.Calendar;

/**
 * Created by Hao on 2016/8/4.
 */

public class NoxLight extends BaseBean{

    @Expose
    public int seqId;
    /**
     * 灯光开关
     * 0灯光关  1灯光开
     */
    @Expose
    public byte lightFlag;
    /**
     * 灯光亮度(0-100) 0:不亮
     */
    @Expose
    public byte brightness;

    /**
     * 参数值详情见：{@link NoxLight.LightMode}
     */
    @Expose
    public byte lightMode;
    /**
     * 灯光颜色R分量 0-255
     */
    @Expose
    public byte r;
    /**
     * 灯光颜色R分量 0-255
     */
    @Expose
    public byte g;
    /**
     * 灯光颜色R分量 0-255
     */
    @Expose
    public byte b;
    /**
     * 灯光颜色w分量 0-255
     */
    @Expose
    public byte w;
    /**
     * 内定流光编号(颜色顺序由固定)
     */
    @Expose
    public byte fixed_streamer_id;

    @Expose
    public byte startHour;

    @Expose
    public byte startMinute;

    @Expose
    public byte endHour;

    @Expose
    public byte endMinute;

    @Expose
    public INoxManager.SleepAidCtrlMode ctrlMode = INoxManager.SleepAidCtrlMode.LIGHT;

    /**
     * 返回持续时长，单位分钟
     * @return
     */
    public short getContinueTime(){
        Calendar c1 = TimeUtil.getCalendar(-100);
        c1.set(Calendar.HOUR_OF_DAY, startHour);
        c1.set(Calendar.MINUTE, startMinute);
        c1.set(Calendar.SECOND, 0);
        long stime = c1.getTimeInMillis();

        c1.set(Calendar.HOUR_OF_DAY, endHour);
        c1.set(Calendar.MINUTE, endMinute);
        long etime = c1.getTimeInMillis();

        if(etime - stime <= 0){
            etime += 24 * 60 * 60 * 1000;
        }

        return (short) ((etime - stime) / 1000 / 60);
    }

    /**
     * 获取不同灯光模式下的数据长度
     *
     * @return
     */
    public int getLightModeDataLength() {
        if (lightMode == LightMode.FIXED_STREAMER) {
            return 1;
        }
        return 4;
    }

    public ByteBuffer fillLightMode(ByteBuffer buffer) {
        buffer.put(lightMode);
        if (lightMode == LightMode.LIGHT_WHITE || lightMode == LightMode.LIGHT_COLOR) {
            buffer.put(r);
            buffer.put(g);
            buffer.put(b);
            buffer.put(w);
        } else {
            buffer.put(fixed_streamer_id);
        }
        return buffer;
    }


    /**
     * 灯光模式,注意：不同的灯光对应不同的结构
     */
    public static class LightMode {
        /**
         * 白光
         */
        public final static byte LIGHT_WHITE = 0x00;
        /**
         * 色彩光
         */
        public final static byte LIGHT_COLOR = 0x01;
        /**
         * 固定流光
         */
        public final static byte FIXED_STREAMER = 0x02;
    }


    @Override
    public String toString() {
        return "NoxLight{" +
                "lightFlag=" + lightFlag +
                ", brightness=" + brightness +
                ", lightMode=" + lightMode +
                ", ctrlMode=" + ctrlMode +
                ", r=" + (r & 0xFF) +
                ", g=" + (g & 0xFF) +
                ", b=" + (b & 0xFF) +
                ", w=" + (w & 0xFF) +
                ", rgb=" + (((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff)) +
                ", fixed_streamer_id=" + fixed_streamer_id +
                ", startHour=" + startHour +
                ", startMinute=" + startMinute +
                ", endHour=" + endHour +
                ", endMinute=" + endMinute +
                ", continueTime=" + getContinueTime() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || !(o instanceof NoxLight)){
            return false;
        }
        NoxLight light = (NoxLight) o;
        if(light.lightFlag != lightFlag){
            return false;
        }
        if(light.lightMode != lightMode){
            return false;
        }
        if(light.brightness != brightness){
            return false;
        }
        if(light.r != r){
            return false;
        }
        if(light.g != g){
            return false;
        }
        if(light.b != b){
            return false;
        }
        if(light.w != w){
            return false;
        }
        if(light.startHour != startHour){
            return false;
        }
        if(light.startMinute != startMinute){
            return false;
        }
        if(light.endHour != endHour){
            return false;
        }
        return light.endMinute == endMinute;
    }
}
