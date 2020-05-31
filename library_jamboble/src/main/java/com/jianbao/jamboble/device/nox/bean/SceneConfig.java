package com.jianbao.jamboble.device.nox.bean;


import android.util.Log;

import com.google.gson.annotations.Expose;

import java.nio.ByteBuffer;

/**
 * Created by Hao on 2016/8/6.
 */

public class SceneConfig extends BaseBean {
    /**
     * 场景ID，参考：
     */
    @Expose
    public int sceneId;
    /**
     * 场景编号
     */
    @Expose
    public long seqId;
    /**
     * 场景是否启用，预留 0关1开
     */
    @Expose
    public byte enable = 1;
    /**
     * 场景类型 @{@link SceneType}
     */
    @Expose
    public byte sceneType = SceneType.SLEEP;
    /**
     * 单位：分钟， 表示场景持续的时长  0表示不关闭
     */
    @Expose
    public short countTime;
    /**
     * 音乐
     */
    @Expose
    public Music music;
    /**
     * 灯光，NOX下有效，其他设备可忽略
     */
    @Expose
    public NoxLight light;
    /**
     * 是否为空config,用于设置场景个数为1后追加的空参数
     */
    @Expose
    public boolean isNullConfig;

    public static class SceneType {
        /**
         * 场景类型
         * 0: 睡觉场景类型
         * 1: 照明场景类型(特殊设备场景，单独出来)
         * 2: 普通设备场景类型
         */
        public static final byte SLEEP = 0;
        public static final byte LIGHTING = 1;
        public static final byte COMMON = 2;
    }

    @Override
    public String toString() {
        return TAG + "{" +
                "sceneId=" + sceneId +
                ", seqId=" + seqId +
                ", enable=" + enable +
                ", sceneType=" + sceneType +
                ", countTime=" + countTime +
                ", music=" + music +
                ", light=" + light +
                '}';
    }

    public ByteBuffer fillBuffer(ByteBuffer buffer) {

        if (isNullConfig) {
            Log.e(TAG,"空的场景参数，不加如buffer数据");
            return buffer;
        }

        //LogUtil.log(TAG+" fillBuffer config:"+ toString());

        buffer.putLong(sceneId);
        buffer.put(enable);
        buffer.put(sceneType);
        //配置结构
//                音乐开关	UINT8	1	是否启用音乐
//                音量大小	UINT8	1	音量(0-16) 0: 静音
//                音乐类型	UINT8	1	0: 设备本地音乐
//                1: 外部音乐
//
//                注意：可能会再细分，处理时注意扩展性
//                音乐编号	USHORT	2	音乐唯一编号 (本地音乐时有效)
//                灯光开关	UINT8	1	0: 关   1: 开
//                灯光亮度	UINT8	1	灯光亮度(0-100) 0:不亮
//                灯光模式	UINT8	1	0: 白光
//                1: 色彩光
//                2: 固定流光
//
//                注意：不同的灯光对应不同的结构
//                灯光结构	结构	n	参照表格下方的结构
//                时长	USHORT	2	单位：分钟， 表示场景持续的时长  0表示不关闭

        if(music != null) {
            buffer.put(music.musicOpenFlag);
            buffer.put(music.voloume);
            music.fillBuffer(buffer);
        } else {
            //固件需要需要在只有一个场景或闹钟的时候添加一个全零的
            buffer.put((byte) 0);
            buffer.put((byte) 0);
            buffer.put((byte)0);
            buffer.putShort((short) 0);
        }
        if(light != null) {
            buffer.put(light.lightFlag);
            buffer.put(light.brightness);
            light.fillLightMode(buffer);
            buffer.putShort(countTime);
        } else {
            //固件需要需要在只有一个场景或闹钟的时候添加一个全零的
            buffer.put((byte)0);
            buffer.put((byte)0);
            buffer.put((byte)0);
            buffer.put((byte)0);
            buffer.put((byte)0);
            buffer.put((byte)0);
            buffer.put((byte)0);
            buffer.putShort((short) 0);
        }
        return buffer;
    }


    @Override
    public boolean equals(Object o) {
        if(o == null || !(o instanceof  SceneConfig)){
            return false;
        }

        SceneConfig config = (SceneConfig) o;
        if(config.countTime != countTime){
            return false;
        }

        if(config.music != null){
            if(!config.music.equals(music)){
                return false;
            }
        }

        if(config.light != null){
            return config.light.equals(light);
        }

        return true;
    }
    //初始化一个全0的
    public void init() {
        sceneId = 0;
        enable = 0;
        sceneType = 0;
        music = null;
        light = null;
    }
}
