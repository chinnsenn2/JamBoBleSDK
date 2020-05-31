package com.jianbao.jamboble.device.nox.interfaces;

import com.jianbao.jamboble.device.nox.bean.SleepSceneConfig;

/**
 * Created by Hao on 2016/8/9.
 */

public interface ISleepAidManager extends IDeviceManager {
    /**
     * 回调类型，睡眠辅助是否还在继续
     */
    int TYPE_METHOD_SLEEP_AID_ISRUNNING = 8000;
    /**
     * 回调类型，睡眠辅助开始
     */
    int TYPE_METHOD_SLEEP_AID_START = 8001;
    /**
     * 回调类型，睡眠辅助结束
     */
    int TYPE_METHOD_SLEEP_AID_STOP = 8002;
    /**
     * 回调类型，睡眠辅助暂停
     */
    int TYPE_METHOD_SLEEP_AID_PAUSE = 8003;
    /**
     * 回调类型，睡眠辅助重启
     */
    int TYPE_METHOD_SLEEP_AID_RESUME = 8004;


    /**
     * 睡眠辅助开始
     */
    void sleepAidStart(SleepSceneConfig config);

    /**
     * 睡眠辅助结束
     *
     * @param isSlowlyStop 是否需要缓慢停止
     */
    void sleepAidStop(boolean isSlowlyStop);

    /**
     * 睡眠辅助暂停
     */
    void sleepAidPause();

    /**
     * 睡眠辅助重启
     */
    void sleepAidResume();

    /**
     * 睡眠辅助是否在运行   回调类型，TYPE_METHOD_SLEEP_AID_ISRUNNING
     */
    void sleepAidIsRunning();

    /**
     * 睡眠辅助是否在运行
     */
    boolean sleepAidIsRunningSync();
    /**
     * 助眠是否智能停止
     *
     * @return
     */
    boolean sleepAidIsSmartStop();

    /**
     * 睡眠辅助正常操作
     *
     * @param lightControl
     * @param musicContrl
     */
    void sleepAidControl(final byte lightControl, final byte musicContrl);
}
