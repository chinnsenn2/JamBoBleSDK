package com.jianbao.jamboble.nox.bean;

import android.util.Log;

import com.google.gson.annotations.Expose;

import java.nio.ByteBuffer;

/**
 * Created by Hao on 2016/8/6.
 */

public class SleepSceneConfig extends SceneConfig {
    public static final String TAG = SleepSceneConfig.class.getSimpleName();
    //睡眠监测开关
    @Expose
    public byte monitorFlag;
    //监测设备类型
    @Expose
    public short monitorDeviceType;
    //监测设备ID
    @Expose
    public String monitorDeviceId;
    //监测设备名称
    @Expose
    public String monitorDeviceName;

    //助眠开关
    @Expose
    public byte sleepAidFlag;
    //睡眠辅助结构总开关
    @Expose
    public byte sleepAidOpenFlag;
    //睡眠辅助结构智能停止开关
    @Expose
    public byte sleepAidSmartFlag;
    //睡眠辅助结构辅助停止时长
    @Expose
    public byte sleepAidCountTime;
    //智能闹钟开关
    @Expose
    public byte smartAlarmFlag;

    //色环坐标
    @Expose
    public float pickerX;
    @Expose
    public float pickerY;

    /**
     * 助眠参数是否相等
     *
     * @param config
     * @return
     */
    public boolean isSleepAidConfigEquals(SleepSceneConfig config) {
        if (config == null) return false;
        if (sleepAidFlag != config.sleepAidFlag) return false;
        if (sleepAidOpenFlag != config.sleepAidOpenFlag) return false;
        if (sleepAidSmartFlag != config.sleepAidSmartFlag) return false;
        return sleepAidCountTime == config.sleepAidCountTime;
    }


    @Override
    public String toString() {
        return super.toString() + TAG + "{" +
                "monitorFlag=" + monitorFlag +
                ", monitorDeviceType=" + monitorDeviceType +
                ", monitorDeviceId='" + monitorDeviceId + '\'' +
                ", monitorDeviceName='" + monitorDeviceName + '\'' +
                ", sleepAidFlag=" + sleepAidFlag +
                ", sleepAidOpenFlag=" + sleepAidOpenFlag +
                ", sleepAidSmartFlag=" + sleepAidSmartFlag +
                ", sleepAidCountTime=" + sleepAidCountTime +
                ", smartAlarmFlag=" + smartAlarmFlag +
                '}';
    }

    @Override
    public ByteBuffer fillBuffer(ByteBuffer buffer) {

        Log.d(TAG, "  睡眠参数：" + toString());

        //场景结构
//        场景编号	UINT64	8	场景全网唯一编号
//        启用	UINT8	1	是否启用该场景
//        场景类型	UINT8	1	0: 睡觉场景类型
//        1: 照明场景类型(特殊设备场景，单独出来)
//        2: 普通设备场景类型
//        配置结构	结构	n	不同类型对应不同结构，注意解析

        buffer.putLong(seqId);
        //预留后期需要

        buffer.put(enable);
        buffer.put(sceneType);
        //配置结构
//                睡眠监测
//                开关	UINT8	1	0: 无   1: 开
//                        睡眠监测
//                设备类型	USHORT	2
//                睡眠监测
//                设备ID	字串	14	密文ID， 类型是APP填全0(结束符)
//                助眠开关	UINT8	1	0: 关   1: 开
//                        表明是否启用助眠
//                睡眠辅助结构	结构	13	对应的睡眠辅助的结构
//                        智能闹钟
//                开关	UINT8	1	0: 关   1: 开
//                        表明是否启用智能闹钟


        buffer.put(monitorFlag);
        buffer.putShort((short) 0);

        buffer.put(new byte[14]);

        buffer.put(sleepAidFlag);
        buffer.put(sleepAidOpenFlag);

        //LogUtil.logE("睡眠辅助结构：音乐:" + music + "------灯光:" + light + "musicLightFlag = " + musicLightFlag + "light.brightness: " + light.brightness);

        buffer.put((byte) 0x03);
        buffer.put((byte) 0x8);
        if (light != null) {
            buffer.put(light.brightness);
            buffer.put(light.r);
            buffer.put(light.g);
            buffer.put(light.b);
            buffer.put(light.w);
        } else {
            buffer.put((byte) 0);
            buffer.put((byte) 0);
            buffer.put((byte) 0);
            buffer.put((byte) 0);
            buffer.put((byte) 0);
        }
        buffer.put(sleepAidSmartFlag);
        buffer.put(sleepAidCountTime);
        buffer.put(smartAlarmFlag);
        return buffer;
    }


}
