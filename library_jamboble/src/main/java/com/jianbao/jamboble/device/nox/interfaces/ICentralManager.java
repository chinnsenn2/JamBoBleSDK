package com.jianbao.jamboble.device.nox.interfaces;

import com.jianbao.jamboble.device.nox.bean.SceneConfig;
import com.jianbao.jamboble.device.nox.bean.SleepSceneConfig;

import java.util.ArrayList;

/**
 * Created by Hao on 2016/8/3.
 * 中控设备具有助眠，闹钟属性
 */

public interface ICentralManager extends ISleepAidManager, IMonitorManager {

    /**
     * 回调类型，场景开始
     */
    int TYPE_METHOD_SCENE_START = 6000;
    /**
     * 回调类型，场景暂停
     */
    int TYPE_METHOD_SCENE_PAUSE = 6002;
    /**
     * 回调类型，场景停止
     */
    int TYPE_METHOD_SCENE_STOP = 6003;
    /**
     * 回调类型，场景恢复
     */
    int TYPE_METHOD_SCENE_RESUME = 6004;

    /**
     * 回调类型，场景参数配置
     */
    int TYPE_METHOD_SCENE_CONFIG_SET = 6012;



    /**
     * 回调类型，场景移除
     */
    int TYPE_METHOD_SCENE_DELETE = 6015;


    /**
     * 开始场景，回调类型 TYPE_METHOD_SCENE_START
     *
     * @param sceneId 场景全网唯一ID
     */
    void sceneStart(int sceneId, boolean isAuto, SceneConfig config);

    /**
     * 开始场景，同步方法，回调类型TYPE_METHOD_SCENE_START
     *
     */
    boolean sceneStartSyn(int sceneId, boolean isAuto, SceneConfig config);

    /**
     * 开始场景，回调类型 TYPE_METHOD_SCENE_DELETE
     *
     * @param sceneId 场景全网唯一ID
     */
    void sceneDelete(int sceneId);

    /**
     * 结束场景 回调类型 TYPE_METHOD_SCENE_STOP
     *
     * @param sceneId 场景全网唯一ID
     */
    void sceneStop(int sceneId);

    /**
     * 暂停场景 回调类型 TYPE_METHOD_SCENE_PAUSE
     *
     * @param sceneSeqId 场景全网唯一ID
     */
    void scenePause(long sceneSeqId);

    /**
     * 恢复场景 回调类型 TYPE_METHOD_SCENE_RESUME
     *
     * @param sceneSeqId 场景全网唯一ID
     */
    void sceneResume(long sceneSeqId);


    /**
     * 睡眠场景参数配置 回调类型 TYPE_METHOD_SCENE_CONFIG_SET,Result为boolean
     *
     * @param config
     */
    void sceneSleepConfigSet(SleepSceneConfig config);

    /**
     * 普通场景参数配置 回调类型 TYPE_METHOD_SCENE_CONFIG_SET,Result为boolean
     *
     * @param config
     */
    void sceneConfigSet(SceneConfig config);

    void sceneConfigSet(ArrayList<SceneConfig> configs);

    /**
     * 同步数据,配合@{@link }使用
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
//    void downHistory(int startTime, int endTime, Handler handler);

}
