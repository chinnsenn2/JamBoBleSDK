package com.jianbao.jamboble.device.nox.bean;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hao on 2016/8/8.
 */

public class NoxWorkMode extends BaseBean {

    /**
     * 工作模式：正常模式(没有任何场景运行)
     */
    public static final byte MODE_NORMAL = 0;
    /**
     * 工作模式：场景模式
     */
    public static final byte MODE_SCENE = 1;
    /**
     * 工作模式：预览模式
     */
    public static final byte MODE_PREVIEW = 2;

    /**
     * 工作模式
     **/
    public byte mode;

    /**
     * 场景个数
     */
    public byte sceneNum;
    /**
     * 场景的详细状态
     */
    public List<SceneStatus> sceneStatuses = new ArrayList<>();

    /**
     * 0: 未运行 1: 运行 (睡觉场景时有效)
     */
    public byte sleepAidStatus;
    /**
     * 睡眠辅助运行剩余时长， 单位：分钟
     * 定时睡眠辅助时有效
     */
    public byte sleepAidLeave;
    /**
     * 0: 未运行 1: 运行 (睡觉场景时有效)
     */
    public byte alarmStatus;
    /**
     * 闹钟编号 (闹钟响时有效)
     */
    public long alarmId;

//    灯状态	UINT8	1	0: 熄灭 1: 亮起
//    灯亮度	UINT8	1	亮度值
//    灯光模式	UINT8	1	0: 白光
//    1: 色彩光(助眠，闹钟运行状态下均为色彩光)
//    2: 固定流光
//
//    注意：不同的灯光对应不同的结构
//    灯光结构	结构	n	灯光结构 参照场景配置的灯光结构

    public NoxLight light;

    public short deviceType;

    /*
    当前是否播放的是声光专辑,0不是声光专辑，1是声光专辑
    * */
    public byte isShengguangAlbum;

    public boolean isSceneRun(int sceneId) {
        for (SceneStatus status : sceneStatuses) {
            if (status.sceneSeqid == sceneId) {
                return true;
            }
        }
        return false;
    }

    public void parseWorkmode(ByteBuffer buffer) {
        try {
            mode = buffer.get();
            sceneNum = buffer.get();
            if (sceneNum > 0) {
                sceneStatuses.clear();
                for (int i = 0; i < sceneNum; i++) {
                    SceneStatus sceneStatus = new SceneStatus();
                    sceneStatus.sceneSeqid = buffer.getLong();
                    sceneStatus.sceneType = buffer.get();
                    sceneStatuses.add(sceneStatus);
                }
            }
            sleepAidStatus = buffer.get();
            sleepAidLeave = buffer.get();
            alarmStatus = buffer.get();
            alarmId = buffer.getLong();
            if (light == null) {
                light = new NoxLight();
            }
            light.lightFlag = buffer.get();
            light.brightness = buffer.get();
            light.lightMode = buffer.get();
            if (light.lightMode == NoxLight.LightMode.FIXED_STREAMER) {
                light.fixed_streamer_id = buffer.get();
            } else {
                light.r = buffer.get();
                light.g = buffer.get();
                light.b = buffer.get();
                light.w = buffer.get();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "解析工作模式异常：" + Arrays.toString(buffer.array()));
        }
    }


    public static class SceneStatus {

        /**
         * 场景编号 场景全网唯一ID
         */
        public long sceneSeqid;
        /**
         * 场景类型  0:睡觉场景类型 1:照明场景类型(特殊设备场景，单独出来) 3.普通设备场景类型
         */
        public byte sceneType;

        @Override
        public String toString() {
            return "SceneStatus{" +
                    "sceneSeqid=" + sceneSeqid +
                    ", sceneType=" + sceneType +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "NoxWorkMode{" +
                "deviceType=" + deviceType +
                ", mode=" + mode +
                ", sceneNum=" + sceneNum +
                ", sceneStatuses=" + sceneStatuses +
                ", sleepAidStatus=" + sleepAidStatus +
                ", sleepAidLeave=" + sleepAidLeave +
                ", alarmStatus=" + alarmStatus +
                ", alarmId=" + alarmId +
                ", light=" + light.toString() +
                ", isShengguangAlbum=" + isShengguangAlbum +
                '}';
    }
}
