package com.jianbao.jamboble.nox.interfaces;

import android.os.Handler;

import com.jianbao.jamboble.nox.bean.NoxWorkMode;

/**
 * Created by Hao on 2016/8/3.
 */

public interface IMonitorManager extends IDeviceManager {

    /**
     * 睡眠标记回调
     */
    int TYPE_METHOD_SLEEP_STATUS = 10001;

    int TYPE_METHOD_COLLECT_STATUS = 10002;

    int TYPE_METHOD_COLLECT_START = 10003;

    int TYPE_METHOD_COLLECT_STOP = 10004;

    /**
     * 回调类型：查看实时数据
     */
    int TYPE_METHOD_REAL_DATA_VIEW = 10005;
    /**
     * 回调类型：停止查看实时数据
     */
    int TYPE_METHOD_REAL_DATA_STOP_VIEW = 10006;
    /**
     * 回调类型：电量查询
     */
    int TYPE_METHOD_POWER_GET = 10007;

    /**
     * 回调类型：工作模式获取
     */
    int TYPE_METHOD_WORK_MODE_GET = 10008;
    /**
     * 回调类型：闹钟设置结果
     */
    int TYPE_METHOD_MONITOR_ALARM_SET = 10009;
    /*
    * 回调类型，查看原始数据
    * */
    int TYPE_METHOD_SEE_RAW_DATA = 10010;
    /*
    * 回调类型，环境参数获取
    * */
    int TYPE_METHOD_MONITOR_ENVIRONMENT_DATA_GET = 10011;
    /*
    * 回调类型，查看原始数据
    * */
    int TYPE_METHOD_STOP_SEE_RAW_DATA = 10012;
    /**
     * 同步数据,配合@{@link }使用
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    void downHistory(int startTime, int endTime, Handler handler);

    /**
     * 获取采集状态,TYPE_METHOD_COLLECT_STATUS
     */
    void collectStatusGet();

    /**
     * 开始采集数据,TYPE_METHOD_COLLECT_START
     */
    void collectStart();

    /*
    * 开始采集数据，返回是否成功，同步方法
    * */
    boolean collectStartSyn();

    /**
     * 停止采集，TYPE_METHOD_COLLECT_STOP
     */
    void collectStop();

    /*
    *停止采集，同步方法，返回是否成功
    * */
    boolean collectStopSyn();

    /**
     * 查看实时数据 TYPE_METHOD_REAL_DATA_VIEW
     */
    void realDataView();

    /*
    * 查看实时数据，同步方法,返回是否成功
    * */
    boolean realDataViewSyn();

    /**
     * 停止查看实时数据  TYPE_METHOD_REAL_DATA_STOP_VIEW
     */
    void realDataStopView();

    /*
    * 停止查看实时数据，同步方法，返回是否成功
    * */

    boolean realDataStopViewSyn();

    void startSeeRawData();

    void  stopSeeRawData();

    /**
     * 获取电量,TYPE_METHOD_POWER_GET
     */
    void powerGet();

    /**
     * 查询设备的工作模式 TYPE_METHOD_WORK_MODE_GET
     * 回调结果result @{@link NoxWorkMode}
     */
    void workModeGet();

    /**
     * 环境监测参数获取 TYPE_METHOD_MONITOR_ENVIRONMENT_DATA_GET
     * 回调结果 {@link}
     */
    void environmentDataGet();

}
