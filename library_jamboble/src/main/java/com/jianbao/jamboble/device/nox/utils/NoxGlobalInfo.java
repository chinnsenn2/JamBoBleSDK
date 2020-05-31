package com.jianbao.jamboble.device.nox.utils;

import android.util.Log;

import com.jianbao.jamboble.device.nox.bean.NoxWorkMode;
import com.jianbao.jamboble.device.nox.bean.SceneBase;
import com.jianbao.jamboble.device.nox.bean.SceneConfigBase;
import com.jianbao.jamboble.device.nox.bean.SleepHelperConfig;

import java.util.ArrayList;
import java.util.List;

public class NoxGlobalInfo {
    private static final String TAG = NoxGlobalInfo.class.getSimpleName();
    public static final boolean IS_INTERNATIONAL_VERSION = false;


    private static int DeviceOnLineState = 0;
    public static int getDeviceOnLineState() {
        return DeviceOnLineState;
    }

    public static void setDeviceOnLineState(int deviceOnLineState) {
        DeviceOnLineState = deviceOnLineState;
    }

    /**
     * 日志上传标记
     * true上传，false不上传
     */
    public static boolean logFlag;
    /**
     * 临时变量，用来显示google和Facebook的登陆接口
     * 测试要求
     * 9.6  现在不需要，要求屏蔽
     */
    public static final boolean TEMP_STATUS = false;


    public static String lastUseNoxId, lastUseNoxName;

    public static final SleepHelperConfig sleepConfig = new SleepHelperConfig();//睡眠辅助配置参数

    public static void clearCache() {
        NoxGlobalInfo.setSceneStatus(false);
    }

    public static int mUnReadMessage = 0;
    public static boolean mHaveNewFriendRequest = false;

    //睡眠系统
    public static boolean sleepSystem;
    //春雨医生
    public static boolean chunyu;
    //问卷
    public static String wenjuan;
    //睡眠计划
    public static boolean smplan;
    //喜马拉雅
    private static boolean xmly;

    public static boolean isXmly() {
        return xmly;
    }

    public static void setXmly(boolean xmly) {
        NoxGlobalInfo.xmly = xmly;
    }

    /**
     * 摄氏度
     */
    public final static byte TempUnit_C = 1;

    /**
     * 华氏度
     */
    public final static byte TempUnit_F = 2;


    /**
     * 蓝牙设备是否正在升级，如果正在升级，即使app切换到后台，也不能断开蓝牙
     */
    public static boolean BLE_DEVICE_UPGRADING = false;

    /**
     * APP正在升级，即使app切换到后台，不要回收
     */
    public static boolean APP_IS_UPGRADE = false;


    //是否是第一次登陆，只针对微信等三方登陆，如果是第一次登陆则调整到信息完善界面
    public static boolean isFirstLogin;

    //闹铃的开启标记，场景启动成功了
    private static boolean mIsPhoneAlarmValid;

    public static boolean isPhoneAlarmValid() {
        return mIsPhoneAlarmValid;
    }

    public static void setPhoneAlarmValid(boolean mIsPhoneAlarmValid) {
        NoxGlobalInfo.mIsPhoneAlarmValid = mIsPhoneAlarmValid;
    }

    /*
        纽扣监测设备
        if (有开启标记 && 在这个监测范围内){
            为开启状态
        }else{
            为停止状态
        }

        1:开始标记
        2:闹铃停止的时候 开启标记被重置为无
        */
    //开启标记
    public static boolean mStart;

    //这个主要记录是否点击过停止场景按钮，主要用于纽扣。未点击过停止，且在监测时间范围，显示正在睡觉
    public static boolean mStop;

    /**
     * 睡觉场景是否启动
     */
    private static boolean sSceneSleepStarted;

    public static void setSceneStatus(boolean sceneStart) {
        //LogUtil.whereCall();
        if(sSceneSleepStarted != sceneStart){
            sSceneSleepStarted = sceneStart;
            Log.d(TAG, "   改变场景状态：" + sceneStart/*+",caller:" + LogUtil.getCaller()*/);
//            LogUtil.log(TAG+" setSceneStatus:" + sceneStart/*+",caller:" + LogUtil.getCaller()*/);
        }
    }

    /**
     * 获取睡觉场景是否启动
     *
     * @return
     */
    public static boolean getSceneStatus() {
        return sSceneSleepStarted;
    }

    /**
     * Nox1设备的在线状态，仅Nox1有这个状态
     */
    private static int sSleepHelperDeviceLineState;

    public static int getsSleepHelperDeviceLineState() {
        return sSleepHelperDeviceLineState;
    }

    public static void setsSleepHelperDeviceLineState(int sSleepHelperDeviceLineState) {
        NoxGlobalInfo.sSleepHelperDeviceLineState = sSleepHelperDeviceLineState;
    }

    /**
     * 是否点击过固件升级对话框中的关闭，关闭了之后在kill掉app之前不再提示。全局内存有效
     */
    public static boolean mClickedCloseUpdate;
    //硬件端启动的场景，智能闹钟无效提示，在app生命周期中只显示一次
    public static boolean mNoStartAlarmShow = true;
    //灯光状态标记，多个页面需要用到，切换页面的时候重新获取状态需要时间，会出现状态切换的问题，产品和测试无法接受，需要全局保存
    public static boolean mIsLightOpen;
    //音乐状态标记，多个页面需要用到，切换页面的时候重新获取需要时间，会出现状态切换的问题，产品和测试无法接受，需要全局保存
    public static boolean mIsMuiscRunnging;

    //是否显示同步报告的结果。现在要求只有手动停止场景才显示，其他情况都不显示。
    private static boolean showReportResult = false;
    /**
     * 这个标识用来确定是否按了SAB中心键
     */
    public static boolean isPressCentralKey=false;
    public static boolean isShowReportResult() {
        return showReportResult;
    }

    public static void setShowReportResult(boolean showReportResult) {
        NoxGlobalInfo.showReportResult = showReportResult;
    }

    /**
     * 是否从Nox端开启睡觉场景
     */
    public static boolean isStartSleepFromNox = false;

    /**
     * 是否播放了闹钟音乐
     */
    public static boolean isPlayClockMusic=false;

    /**
     * true：nox2w在手机APP端开始播放音乐
     * false: nox2w在硬件端开始播放音乐
     */
    public static boolean isNox2WStartMusicFromPhone = false;

    /**
     * 是否通过手势控制关闭音乐
     */
    public static boolean isCloseMusicFromGesture = false;

    /**
     * 是否手势切歌
     */
    public static boolean isGestureChangeMusic = false;

    public static NoxWorkMode noxWorkMode;


    /**
     * 记录睡觉场景是否是自动开始
     */
    private static boolean isSceneAutoStart = false;

    public static boolean isSceneAutoStart() {
        return isSceneAutoStart;
    }

    public static void setSceneAutoStart(boolean isSceneAutoStart) {
        NoxGlobalInfo.isSceneAutoStart = isSceneAutoStart;
    }


    /**
     * 是否需要播放音乐，针对Sleepace专辑音乐，且app生命周期内有效
     * ------------------------------------------------------
     * 公元2017.03.16 17:00:16，测试部卢少红同志郑重声明，APP生命周期内不再记录上次的播放状态，每次进入都需要播放，
     * 所以此变量始终设置true，也可以去掉。目前暂做保留，所有赋值的地方都注释，以防她们反悔。
     */
    public static final boolean needPlayMusic = true;

    /**
     * 监测设备的监测状态，是否正在监测？true：正在监测，false：未在监测
     */
    public static boolean isMonitorDeviceWorking = false;
    /**
     * 是否在手机端启动了助眠,这个值主要是用来处理SAB手机端开启了场景，中心键关闭助眠之后，再次打开助眠没有同步设备的音乐信息。bug 13611
     */
    public static boolean  isStartSleepAidFromPhone=false;
    /**
     * 是否拒绝打开蓝牙，true：拒绝，false：同意打开
     * 拒绝后不再提示打开蓝牙
     */
    //public static boolean isRefuseOpenBT = false;

    //场景列表
    public static List<SceneBase> scenes = new ArrayList<>();


    //场景助眠配置列表
    public static List<SceneConfigBase> sleepHelperConfigs = new ArrayList<>();

}







































