package com.jianbao.jamboble.nox.manager;

import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import com.google.gson.Gson;
import com.jianbao.jamboble.nox.BaseCallback;
import com.jianbao.jamboble.nox.ConnectionState;
import com.jianbao.jamboble.nox.Device;
import com.jianbao.jamboble.nox.DeviceType;
import com.jianbao.jamboble.nox.bean.CallbackData;
import com.jianbao.jamboble.nox.bean.NoxWorkMode;
import com.jianbao.jamboble.nox.bean.SceneConfig;
import com.jianbao.jamboble.nox.bean.SleepSceneConfig;
import com.jianbao.jamboble.nox.interfaces.ICentralManager;
import com.jianbao.jamboble.nox.interfaces.IDeviceManager;
import com.jianbao.jamboble.nox.interfaces.IMonitorManager;
import com.jianbao.jamboble.nox.interfaces.INoxManager;
import com.jianbao.jamboble.nox.interfaces.ISleepAidManager;
import com.jianbao.jamboble.nox.utils.NoxGlobalInfo;
import com.jianbao.jamboble.nox.utils.SPUtils;
import com.jianbao.jamboble.nox.utils.SceneUtils;

import java.util.ArrayList;


/**
 * Created by Hao on 2016/8/8.
 */

public class CentralManager extends DeviceManager implements ICentralManager {


    protected Device monitorDevice;
    protected Device alarmDevice;
    protected Device sleepAidDevice;
    /**
     * 这个值是接到回调后赋值，如果没有调用sleepAidIsRunning判断就不会赋值，不一定准确
     * 改成用方法 @link{sleepAidIsRunningSync()}
     */
    @Deprecated
    private boolean sleepAidIsRunning;
    private int type;//专辑类型
    private String musicStartPlayTime;
    private long currentTimeMillis;
    private String musicStartPlayTempTime;
    private long tempTimeMillis;

    public IMonitorManager getMonitorManager() {
        return monitorManager;
    }

    public ISleepAidManager getSleepAidManager() {
        return sleepAidManager;
    }

    private IMonitorManager monitorManager;
    private ISleepAidManager sleepAidManager;


    private static CentralManager sInstance;

    protected SleepSceneConfig mCurSleepConfig;

    public static synchronized CentralManager getsInstance(Context context, Device sleepAidDevice, Device monitorDevice, Device alarmDevice) {
        if (sInstance == null) {
            sInstance = new CentralManager();
        }
        sInstance.mContext = context;
        sInstance.setSleepAidDevice(sleepAidDevice);
        sInstance.setMonitorDevice(monitorDevice);
        //初始化AppManager ，防止在子线程内初始化
        AppManager.getInstance(context);
        return sInstance;
    }

    private CentralManager() {

    }

    public Device getMonitorDevice() {
        return monitorDevice;
    }


    public void setMonitorDevice(Device monitorDevice) {
        if (!isDeviceNull(monitorDevice)) {
            if (this.monitorDevice != null && !this.monitorDevice.equals(monitorDevice)) {
                if (monitorManager != null) {
                    monitorManager.unRegistCallBack(mCallback);
                }
            }
            monitorManager = (IMonitorManager) getManager(mContext, monitorDevice);
        } else {
            monitorManager = null;
        }
        this.monitorDevice = monitorDevice;
    }

    public Device getAlarmDevice() {
        return alarmDevice;
    }

    /**
     * CentralManager没有设备类型，不要调用
     *
     * @return
     */
    @Override
    @Deprecated
    public short getDeviceType() {
        return DeviceType.DEVICE_TYPE_PHONE;

    }

    protected BaseCallback mCallback = new BaseCallback() {
        @Override
        public void onStateChange(IDeviceManager deviceManager, String sender, ConnectionState state) {
            onStateChangeCallBack(deviceManager, sender, state);
        }

        @Override
        public void onDataCallback(CallbackData callbackData) {
            dataCallback(callbackData);
        }
    };


    @Override
    public void registCallBack(BaseCallback callBack, String sender) {
        super.registCallBack(callBack, sender);
        //比如nox1+reston的时候，因为没有一个设备是Appmanager,所以播喜马拉雅音乐的时候没有相关回调，所以加入这个
        AppManager.getInstance(mContext).registCallBack(mCallback, sender);
        if (sleepAidManager != null) {
            sleepAidManager.registCallBack(mCallback, sender);
        }
        if (monitorManager != null) {
            monitorManager.registCallBack(mCallback, sender);
        }
    }

    @Override
    public void unRegistCallBack(BaseCallback callBack) {
        super.unRegistCallBack(callBack);

        AppManager.getInstance(mContext).unRegistCallBack(callBack);

        if (sleepAidManager != null) {
            sleepAidManager.unRegistCallBack(callBack);
        }
        if (monitorManager != null) {
            monitorManager.unRegistCallBack(callBack);
        }
    }

    public Device getSleepAidDevice() {
        return sleepAidDevice;
    }

    public void setSleepAidDevice(Device sleepAidDevice) {
        if (!isDeviceNull(sleepAidDevice)) {
            if (this.sleepAidDevice != null && !this.sleepAidDevice.equals(sleepAidDevice)) {
                if (sleepAidManager != null) {
                    sleepAidManager.unRegistCallBack(mCallback);
                }
            }
            sleepAidManager = (ISleepAidManager) getManager(mContext, sleepAidDevice);
        } else {
            sleepAidManager = null;
        }
        this.sleepAidDevice = sleepAidDevice;
    }

    /**
     * 设置场景状态
     *
     * @param sceneStatus
     */
    public void setSceneStatus(boolean sceneStatus) {
//        if (NoxGlobalInfo.getSceneStatus() != sceneStatus) {
//            NoxGlobalInfo.setSceneStatus(sceneStatus);
//            //场景开启，如果是手机监测需要开启闹钟检测
//            if (monitorManager instanceof AppManager) {
//                if (sceneStatus) {
//                    mContext.sendBroadcast(new Intent(DeviceService.ACTION_BROCAST_START_TIME_TICK));
//                } else {
//                    monitorManager.collectStop();
//                }
//            }
//        }
    }

    long mGetSleepStatusTime;

    @Override
    protected void dataCallback(CallbackData callbackData) {
        //Log.d(TAG, "   收到回调类型:" + callbackData.getType());

//        if(SceneUtils.hasNox1()){
//            if(callbackData.getStatus() == CallbackData.STATUS_NOX_UNDER_LINE){
//                setOnlineSatus(DeviceType.DEVICE_TYPE_NOX_PRO, 0);
//            }
//        }

        //一分钟处理一次入睡和清醒标识
        if (callbackData.getType() == TYPE_METHOD_SLEEP_STATUS && System.currentTimeMillis() - mGetSleepStatusTime > 60000) {
            return;
//            mGetSleepStatusTime = System.currentTimeMillis();
//            //Log.d(TAG, " sleep status:" + callbackData);
//            if (callbackData.isSuccess()) {
//                RealTimeBean bean = (RealTimeBean) callbackData.getResult();
//                if (bean.getSleepFlag() == 1) {
//                    Log.d(TAG, "---收到入睡标志,是否智能停止：" + sleepAidIsSmartStop() + "   睡眠辅助是否在跑：" + sleepAidIsRunningSync());
//                    Log.d(TAG, " sleep smart sleep---------");
//                    //已入睡
//                    if (sleepAidIsSmartStop() && sleepAidIsRunningSync()) {
//                        sleepAidStop(true);
//                        if (SceneUtils.getAlarmDeviceType(SLEEP_SCENE_ID) != DeviceType.DEVICE_TYPE_PHONE) {
//                            //助眠结束，如果闹钟不是手机，停止检查闹钟
//                            mContext.sendBroadcast(new Intent(DeviceService.ACTION_BROCAST_STOP_TIME_TICK));
//                        }
//                    }
//                }
//
//                if (bean.getWakeFlag() == 1) {
//                    Log.d(TAG, "---收到清醒标志");
////                    Log.d(TAG, " sleep smart wake---------");
//                    //已清醒
//                }
//            }

        } else if (callbackData.getType() == TYPE_METHOD_WORK_MODE_GET) {
            Log.d(TAG,"工作模式 sceneStatus:" + NoxGlobalInfo.getSceneStatus() + ",workMode:" + callbackData);
//            Log.d(TAG, " workMode cd:" + callbackData);
            if (callbackData.isSuccess() && callbackData.getResult() != null && callbackData.getResult() instanceof NoxWorkMode) {
                Log.e(TAG, "====工作模式回调===:" + callbackData);
                NoxWorkMode workMode = (NoxWorkMode) callbackData.getResult();
                // Log.d(TAG, " workMode sceneStatus:" + GlobalInfo.getSceneStatus() + ",mCurSleepAidAlbumMusic:" + mCurSleepAidAlbumMusic+"===workMode===："+workMode);
                if (SceneUtils.hasNox()) {
                    boolean sleepscenRun = workMode.isSceneRun(SceneUtils.SLEEP_SCENE_ID);
                    
                    if (sleepscenRun) {
                        setSceneStatus(sleepscenRun);
                    } else {
                        if (NoxGlobalInfo.getSceneStatus() && workMode.alarmStatus == 4) {//以前是启动，现在是停止
                            if (SceneUtils.getMonitorDeviceType(SceneUtils.SLEEP_SCENE_ID) == DeviceType.DEVICE_TYPE_PHONE) {
                                collectStop();
                            }
                            setSceneStatus(sleepscenRun);
//                            //Nox端停止场景，无需报告提示，只有手动停止睡觉场景才有报告提示
//                            GlobalInfo.setShowReportResult(false);
//                            SleepDataRemarkDao remark = new SleepDataRemarkDao();
//                            final int starttime = remark.getSleepData(SleepaceApplication.getInstance().getCurrentUserMemberID());
//                            final int endtime = (int) (System.currentTimeMillis() / 1000);
//                            downHistory(starttime + 1, endtime, null);
                        }
                    }
                }
                //               boolean sleepAidStatus = (workMode.sleepAidStatus == 1 ? true : false); //这个判断是多余的，智能结束收到助眠结束的标志会停掉音乐，如果设置了自动关闭时间timeTick会停止音乐
                //用户通过操作Nox2端停止助眠或者助眠到时间停止后，需要关闭音乐
//                if (!sleepAidStatus ) {
//                    //Nox2在线音乐助眠时，助眠结束，停止播放本地音乐(下载到本地的非喜马拉雅音乐也需要调用停止音乐接口)
//                    if (GlobalInfo.getSceneStatus() && (SceneUtils.hasNox2B() || SceneUtils.hasNoxSab()) && mCurSleepAidAlbumMusic != null && (mCurSleepAidAlbumMusic.isXMLYMusic() || mCurSleepAidAlbumMusic.playWay == Music.PlayWay.PHONE)) {
//                        AppManager.getInstance(mContext).musicStop(null, false);
//                    }
//                }

                NoxGlobalInfo.mIsLightOpen = (workMode.light != null && workMode.light.lightFlag == 1);
            }
        } else if (callbackData.getType() == INoxManager.TYPE_METHOD_LIGHT_CLOSE) {
//            Log.d(TAG, " colse light cd:" + callbackData);
            if (NoxGlobalInfo.mIsLightOpen) {
                NoxGlobalInfo.mIsLightOpen = !callbackData.isSuccess();
            }
        } else if (callbackData.getType() == ICentralManager.TYPE_METHOD_COLLECT_STATUS) {
            Log.d(TAG, " coll status:" + callbackData);
            if (callbackData.isSuccess() && SceneUtils.hasHeartBreathDevice()) {
                NoxGlobalInfo.isMonitorDeviceWorking = ((byte) callbackData.getResult() == 1);
                if (!SceneUtils.hasNox() && NoxGlobalInfo.isMonitorDeviceWorking) {
                    setSceneStatus(true);
                }
            }
        } else if (callbackData.getType() == ICentralManager.TYPE_METHOD_SCENE_START) {
            Log.d(TAG,"start scene cd:" + callbackData);
            if (callbackData.isSuccess()) {
                NoxGlobalInfo.isStartSleepAidFromPhone = true;
            }
            if (SceneUtils.hasHeartBreathDevice()) {
                NoxGlobalInfo.isMonitorDeviceWorking = callbackData.isSuccess();
            }
            if (SceneUtils.hasNox() && callbackData.isSuccess()) {
                NoxGlobalInfo.mIsLightOpen = SPUtils.getWithUserId(SPUtils.KEY_LIGHT_FLAG, 1) == 1;
            }
        } else if (callbackData.getType() == ICentralManager.TYPE_METHOD_SCENE_STOP) {
            Log.d(TAG, " scene stop:" + callbackData);
            if ((int) callbackData.getResult() == SceneUtils.SLEEP_SCENE_ID) {
                boolean stopSceneRestult = callbackData.isSuccess();
                //手机+nox的时候，停止场景，只要第一步手机停止采集成功就认为是成功，这个时候去拿报告，这种情况特殊处理
                if (!stopSceneRestult && SceneUtils.hasNox() && SceneUtils.getMonitorDeviceType(SceneUtils.SLEEP_SCENE_ID) == DeviceType.DEVICE_TYPE_PHONE) {
                    stopSceneRestult = true;
                    callbackData.setStatus(CallbackData.STATUS_OK);
                }

                setSceneStatus(!stopSceneRestult);
                if (stopSceneRestult) {
                    NoxGlobalInfo.isStartSleepFromNox = false;
                    if (SceneUtils.hasNox2W()) {
                        NoxGlobalInfo.mIsMuiscRunnging = false;
                    }
                }

                if (SceneUtils.hasHeartBreathDevice() && stopSceneRestult) {
                    NoxGlobalInfo.isMonitorDeviceWorking = false;
                }
            }
            //移除助眠后断开设备的定时
//            SleepaceApplication.getInstance().mHandler.removeCallbacks(mDisconnectRunnable);
            NoxGlobalInfo.isPressCentralKey = false;//结束场景重置参数
        } else if (callbackData.getType() == TYPE_METHOD_SLEEP_AID_ISRUNNING) {
            if (callbackData.getResult() != null) {
                sleepAidIsRunning = (boolean) callbackData.getResult();
            } else {
                sleepAidIsRunning = false;
            }
        }  else if (callbackData.getType() == TYPE_METHOD_SLEEP_AID_STOP) {
            sleepAidIsRunning = false;
            delayDisconnectAfterSleepAidStop();
        } else if (callbackData.getType() == TYPE_METHOD_SLEEP_AID_START || callbackData.getType() == TYPE_METHOD_SLEEP_AID_RESUME) {
            sleepAidIsRunning = true;
        }
        super.dataCallback(callbackData);
    }

    /**
     * 助眠结束后3分钟，如果锁屏，断开部分蓝牙设备连接
     */
    private void delayDisconnectAfterSleepAidStop() {
        Log.d(TAG, "   助眠结束，三分钟后尝试断开设备，助眠设备：" + sleepAidDevice + "   场景状态：" + NoxGlobalInfo.getSceneStatus());
        //修改成所有设备都断开
        if (sleepAidDevice != null && NoxGlobalInfo.getSceneStatus()) {
//            Handler handler = SleepaceApplication.getInstance().mHandler;
//            handler.removeCallbacks(mDisconnectRunnable);
            //手机辅助，nox2b辅助，停止助眠三分钟后断开设备
//            handler.postDelayed(mDisconnectRunnable, 180000);
        }
    }


    Runnable mDisconnectRunnable = new Runnable() {
        @Override
        public void run() {
            PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            boolean isScreenOff = !pm.isScreenOn();
            Log.d(TAG, "   助眠结束三分钟后，是否灭屏：" + isScreenOff + "   如果灭屏，断开连接");
            if (isScreenOff) {
                //三分钟后，如果是灭屏状态下断开连接
                disconnect();
            }
        }
    };

    @Override
    public void downHistory(final int startTime, final int endTime, final Handler handler) {
        //   LogUtil.whereCall();
//        Log.d(TAG, "  开始同步数据，开始时间：" + startTime + "    结束时间：" + endTime + "   回调handler是否为空：" + (handler == null));
//        if (sleepAidManager instanceof NoxManager || alarmManager instanceof NoxManager) {
//            final NoxManager noxManager = (NoxManager) (sleepAidManager != null ? sleepAidManager : alarmManager);
//            if (monitorManager == null) {
//                if (handler != null) {
//                    handler.obtainMessage(SynDataActivity.MSG_WHAT_DOWNLOAING, SynDataActivity.MSG_ARGS_NO_DATA, SynDataActivity.MSG_ARGS_NO_DATA).sendToTarget();
//                }
//            } else {
//                noxManager.downHistory(startTime, endTime, handler);
//            }
//        } else {
//            if (monitorManager != null) {
//                monitorManager.downHistory(startTime, endTime, handler);
//            } else {
//                if (handler != null) {
//                    handler.obtainMessage(SynDataActivity.MSG_WHAT_DOWNLOAING, SynDataActivity.MSG_ARGS_NO_DATA, SynDataActivity.MSG_ARGS_NO_DATA).sendToTarget();
//                }
//            }
//
//        }

    }


    @Override
    public void connectDevice() {
        Log.d(TAG, "  连接设备：" + sleepAidDevice);
        if (!isDeviceNull(sleepAidDevice) && !(sleepAidManager instanceof AppManager)) {
            sleepAidManager.connectDevice();
        }else if (!isDeviceNull(monitorDevice) && !(monitorManager instanceof AppManager)) {
            monitorManager.connectDevice();
        } else {
            onStateChangeCallBack(ConnectionState.CONNECTED);
        }
    }


    /**
     * 所有设备是否已经连接
     *
     * @return
     */
    @Override
    public boolean isConnected() {
        Log.d(TAG, "判断连接： 辅助manager:" + sleepAidManager);
        if (sleepAidManager != null && !(sleepAidManager instanceof AppManager)) {
            return sleepAidManager.isConnected();
        } else if (monitorManager != null && !(monitorManager instanceof AppManager)) {
            return monitorManager.isConnected();
        } else {
            if (sleepAidManager != null) {
                return sleepAidManager.isConnected();
            } else if (monitorManager != null) {
                return monitorManager.isConnected();
            }
        }
        return false;
    }


    @Override
    public ConnectionState getConnectionState() {
        if (sleepAidManager != null && !(sleepAidManager instanceof AppManager)) {
            return sleepAidManager.getConnectionState();
        } else if (monitorManager != null && !(monitorManager instanceof AppManager)) {
            return monitorManager.getConnectionState();
        } else {
            if (sleepAidManager != null) {
                return sleepAidManager.getConnectionState();
            } else if (monitorManager != null) {
                return monitorManager.getConnectionState();
            }
        }
        return ConnectionState.CONNECTED;
    }


    @Override
    public void disconnect() {
        if (monitorManager != null) {
            monitorManager.disconnect();
        }
        if (sleepAidManager != null) {
            sleepAidManager.disconnect();
        }
        //闹钟设备已经和辅助设备一起，所以不用断开闹钟设备
    }

    @Override
    public void release() {
        if (sleepAidManager != null) {
            sleepAidManager.release();
        }
        if (monitorManager != null) {
            monitorManager.release();
        }
    }

    @Override
    public void setSender(String sender) {
        super.setSender(sender);
        if (sleepAidManager != null) {
            sleepAidManager.setSender(sender);
        }
        if (monitorManager != null) {
            monitorManager.setSender(sender);
        }
    }

    @Override
    public void configDeviceAfterBindSync() {

    }

    @Override
    public void collectStatusGet() {
        if (monitorManager != null) {
            monitorManager.collectStatusGet();
        }
    }

    @Override
    public void collectStart() {
        if (monitorManager != null) {
            monitorManager.collectStart();
        }
    }

    @Override
    public boolean collectStartSyn() {
        if (monitorManager != null) {
            return monitorManager.collectStartSyn();
        }
        return true;
    }


    @Override
    public void collectStop() {
        if (monitorManager != null) {
            monitorManager.collectStop();
        }
    }

    @Override
    public boolean collectStopSyn() {
        Log.d(TAG, " collectStopSyn monitorManager:" + monitorManager);
        if (monitorManager != null) {
            return monitorManager.collectStopSyn();
        }
        return true;
    }

    @Override
    public void startSeeRawData() {
        if (monitorManager != null) {
            monitorManager.startSeeRawData();
        }
    }

    @Override
    public void stopSeeRawData() {
        if (monitorManager != null) {
            monitorManager.stopSeeRawData();
        }
    }

    @Override
    public void realDataView() {
        if (monitorManager != null) {
            monitorManager.realDataView();
        }
    }

    @Override
    public boolean realDataViewSyn() {
        if (monitorManager != null) {
            monitorManager.realDataViewSyn();
        }
        return true;
    }

    @Override
    public void realDataStopView() {
        if (monitorManager != null) {
            monitorManager.realDataStopView();
        }
    }

    @Override
    public boolean realDataStopViewSyn() {
        if (monitorManager != null) {
            return monitorManager.realDataStopViewSyn();
        }
        return true;
    }


    @Override
    public void powerGet() {
        if (monitorManager != null) {
            monitorManager.powerGet();
        }
    }

    @Override
    public void workModeGet() {
        if (sleepAidManager instanceof INoxManager) {
            INoxManager iNoxManager = null;
            if (sleepAidManager != null && sleepAidManager instanceof INoxManager) {
                iNoxManager = (INoxManager) sleepAidManager;
            }

            String log = TAG + " workModeGet iNoxManager:" + iNoxManager;
            Log.d(TAG, log);

            if (iNoxManager != null) {
                iNoxManager.workModeGet();
            }
        } else {
            String log = TAG + " workModeGet err";
            Log.d(TAG, log);
        }
    }

    @Override
    public void environmentDataGet() {
        if (sleepAidManager instanceof INoxManager && sleepAidDevice.deviceType != DeviceType.DEVICE_TYPE_NOX_2B) {
            //Nox和NoxW获取数据
            ((INoxManager) sleepAidManager).environmentDataGet();
        } else if (monitorManager != null) {
            monitorManager.environmentDataGet();
        }
    }

    @Override
    public void sleepAidStart(SleepSceneConfig config) {
        Log.d(TAG, "   助眠开启：" + config);
//        LogUtil.whereCall();
        sleepAidIsRunning = true;
        mCurSleepConfig = config;
        if (sleepAidManager == null) return;
        sleepAidManager.sleepAidStart(config);
        //移除助眠结束后三分钟锁屏断开设备
//        SleepaceApplication.getInstance().mHandler.removeCallbacks(mDisconnectRunnable);
    }

    @Override
    public void sleepAidStop(boolean isSlowlyStop) {
        sleepAidIsRunning = false;
        if (sleepAidManager == null) return;
        sleepAidManager.sleepAidStop(isSlowlyStop);
    }

    @Override
    public void sleepAidPause() {
        if (sleepAidManager == null) return;
        sleepAidManager.sleepAidPause();
    }

    @Override
    public void sleepAidResume() {
        if (sleepAidManager == null) return;

        sleepAidManager.sleepAidResume();

    }

    @Override
    public void sleepAidIsRunning() {
        if (sleepAidManager == null) {
            CallbackData data = new CallbackData();
            data.setType(TYPE_METHOD_SLEEP_AID_ISRUNNING);
            data.setStatus(CallbackData.STATUS_OK);
            data.setResult(false);
            data.setSender(sender);
            dataCallback(data);
            return;
        }
        sleepAidManager.sleepAidIsRunning();
    }

    @Override
    public boolean sleepAidIsRunningSync() {
        if (sleepAidManager != null) {
            return sleepAidManager.sleepAidIsRunningSync();
        }
        return false;
    }


    @Override
    public void sceneStart(final int sceneId, final boolean isAuto, final SceneConfig config) {
        Log.d(TAG, "   开启场景，助眠设备：" + sleepAidDevice.deviceName + "  监测设备：" + monitorDevice.deviceName + "   闹钟设备：" + alarmDevice.deviceName);
        Log.d(TAG, "   开启场景，助眠设备：" + sleepAidDevice.deviceName + "  监测设备：" + monitorDevice.deviceName + "   闹钟设备：" + alarmDevice.deviceName);
//        SleepaceApplication.getInstance().mHandler.removeCallbacks(mDisconnectRunnable);
        Log.d(TAG, " sceneStart sceneId isAuto:" + isAuto + ",monitorDevice:" + monitorDevice + ",sleepAidDevice:" + sleepAidDevice + ",config:" + config);
        //setCurSleepAidAlbumMusic(config.music);
        if (config instanceof SleepSceneConfig) {
            mCurSleepConfig = (SleepSceneConfig) config;
        }
        sleepAidIsRunning = true;
        //场景开启，需要把所有非一次性闹钟重新置为可用

        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (sleepAidManager instanceof INoxManager) {
                    INoxManager iNoxManager = (INoxManager) sleepAidManager;
                    if (iNoxManager != null) {
                        if (monitorDevice != null && monitorDevice.deviceType == DeviceType.DEVICE_TYPE_PHONE) {
                            iNoxManager.sceneStartSyn(sceneId, isAuto, config);
                            //如果监测设备是手机，需要我们主动去开始采集动作
                            collectStartSyn();
                        } else if (monitorDevice != null && (monitorDevice.deviceType == DeviceType.DEVICE_TYPE_RESTON_Z1 || monitorDevice.deviceType == DeviceType.DEVICE_TYPE_RESTON_Z2)) {
                            //如果监测设备是reston，启动场景需要主动调用查看实时数据接口才能查看到实时数据
                            if (iNoxManager.sceneStartSyn(sceneId, isAuto, config)) {
                                iNoxManager.realDataView();
                            }
                        } else {
                            iNoxManager.sceneStart(sceneId, isAuto, config);
                        }
                    }
                } else {
                    CallbackData data = new CallbackData();
                    data.setSender(sender);
                    boolean collRes = collectStartSyn();
                    Log.d(TAG, " sceneStart collRes:" + collRes);
                    if (collRes) {//开始采集
                        boolean realDataRes = realDataViewSyn();
                        Log.d(TAG, " sceneStart realDataRes:" + realDataRes);

                        if (!isAuto) {
                            sleepAidStart((SleepSceneConfig) config);
                        }
                        setSceneStatus(true);
                        data.setType(TYPE_METHOD_SCENE_START);
                        data.setStatus(CallbackData.STATUS_OK);
                        data.setResult(sceneId);
                        dataCallback(data);
                    } else {//开始采集失败，
                        data.setType(TYPE_METHOD_SCENE_START);
                        data.setStatus(CallbackData.STATUS_FAILED);
                        data.setResult(sceneId);
                        dataCallback(data);
                    }
                }
            }
        });

    }

    @Override
    public boolean sceneStartSyn(int sceneId, boolean isAuto, SceneConfig config) {
        return false;
    }

    @Override
    public void sceneDelete(int sceneId) {
        if (sleepAidManager instanceof INoxManager) {
            INoxManager iNoxManager = (INoxManager) sleepAidManager;
            iNoxManager.sceneDelete(sceneId);
        }
    }

    @Override
    public void sceneStop(final int sceneId) {
        NoxGlobalInfo.isStartSleepAidFromPhone = false;
        Log.d(TAG, "  停止场景,助眠设备：" + (sleepAidDevice == null ? "空" : sleepAidDevice.deviceName) + "   检测设备：" + (monitorDevice == null ? "空" : monitorDevice.deviceName) + "   闹钟设备：" + (alarmDevice == null ? "空" : alarmDevice.deviceName));
        if (!NoxGlobalInfo.getSceneStatus()) {
            Log.d(TAG, "   场景已经关闭，不重复关闭");
            CallbackData data = new CallbackData();
            data.setType(TYPE_METHOD_SCENE_STOP);
            data.setStatus(CallbackData.STATUS_OK);
            data.setResult(sceneId);
            dataCallback(data);
            return;
        }

        sleepAidIsRunning = false;
//        Intent intent = new Intent(DeviceService.ACTION_BROCAST_STOP_TIME_TICK);
//        mContext.sendBroadcast(intent);
//        sTheadExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                if (sleepAidManager instanceof INoxManager) {
//                    INoxManager iNoxManager = (INoxManager) sleepAidManager;
//                    //如果监测设备是手机，需要我们主动去停止
//                    if (monitorDevice != null && monitorDevice.deviceType == DeviceType.DEVICE_TYPE_PHONE) {
//                        collectStopSyn();
//                    }
//                    //如果睡眠辅助设备是别的设备
//                    iNoxManager.sceneStop(sceneId);
//                    //Log.d(TAG, "   停止场景，音乐类型：" + mCurSleepAidAlbumMusic.musicFrom);
//                } else {
//                    CallbackData data = new CallbackData();
//                    data.setSender(sender);
//                    if (collectStopSyn()) {//停止采集
//                        //指令不能发太密集，要不部分设备处理不过来
//                        SystemClock.sleep(50);
//                        if (realDataStopViewSyn()) {//停止查看实时数据
//                            SystemClock.sleep(50);
//                            sleepAidStop(false);
//                            Log.d(TAG, "   停止场景成功");
//                            data.setType(TYPE_METHOD_SCENE_STOP);
//                            data.setStatus(CallbackData.STATUS_OK);
//                            data.setResult(sceneId);
//                            setSceneStatus(false);
//                            NoxGlobalInfo.mStart = false;
//                            NoxGlobalInfo.setPhoneAlarmValid(false);
//                            dataCallback(data);
//                        } else {
//                            Log.d(TAG, "停止场景失败---停止实时数据查看失败");
//                            data.setType(TYPE_METHOD_SCENE_STOP);
//                            data.setStatus(CallbackData.STATUS_FAILED);
//                            data.setResult(sceneId);
//                            dataCallback(data);
//                        }
//                    } else {
//                        Log.d(TAG, "停止场景失败---停止采集失败");
//                        data.setType(TYPE_METHOD_SCENE_STOP);
//                        data.setStatus(CallbackData.STATUS_FAILED);
//                        data.setResult(sceneId);
//                        dataCallback(data);
//                    }
//                }
//            }
//        });

    }

    @Override
    public void scenePause(long sceneSeqId) {
        if (sleepAidManager instanceof INoxManager) {
            //如果睡眠辅助设备是别的设备
            ((INoxManager) sleepAidManager).scenePause(sceneSeqId);
        } else {

        }
    }

    @Override
    public void sceneResume(long sceneSeqId) {
        if (sleepAidManager instanceof INoxManager) {
            //如果睡眠辅助设备是别的设备
            ((INoxManager) sleepAidManager).sceneResume(sceneSeqId);
        } else {

        }
    }

    @Override
    public boolean sleepAidIsSmartStop() {
        if (sleepAidManager != null) {
            return sleepAidManager.sleepAidIsSmartStop();
        }
        return false;
    }

    @Override
    public void sleepAidControl(byte lightControl, byte musicContrl) {
        if (sleepAidManager != null) {
            sleepAidManager.sleepAidControl(lightControl, musicContrl);
        }
    }

    @Override
    public void sceneSleepConfigSet(final SleepSceneConfig config) {
        //Log.d(TAG,TAG+" sceneSleepConfigSet sleepAidManager:"+sleepAidManager+",config:" + config);
        if (sleepAidManager instanceof INoxManager) {
            //如果睡眠辅助设备是别的设备
            INoxManager iNoxManager = (INoxManager) (sleepAidManager);
            if (iNoxManager != null) {
                iNoxManager.sceneSleepConfigSet(config);
            }
        } else {
            sTheadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    CallbackData data = new CallbackData();
                    data.setSender(sender);
                    data.setType(TYPE_METHOD_SCENE_CONFIG_SET);
                    Gson gson = new Gson();
                    SPUtils.saveWithUserId(config.seqId + "", gson.toJson(config));

                    data.setStatus(CallbackData.STATUS_OK);
                    dataCallback(data);
                    if (sleepAidManager instanceof AppManager) {
                        ((AppManager) sleepAidManager).resetSleepAidConfig(config, true);
                    }
                }
            });
        }
    }


    @Override
    public void sceneConfigSet(final SceneConfig config) {
        //Log.d(TAG,TAG+" sceneConfigSet sleepAidManager:"+sleepAidManager+",config:" + config);
        if (sleepAidManager instanceof INoxManager) {
            ((INoxManager) sleepAidManager).sceneConfigSet(config);
        }
    }

    @Override
    public void sceneConfigSet(ArrayList<SceneConfig> configs) {
        if (sleepAidManager instanceof INoxManager) {
            INoxManager iNoxManager = (INoxManager) sleepAidManager;
            iNoxManager.sceneConfigSet(configs);
        }
    }


    public void setCollectState(byte collectState) {
        if (getDevice() != null) {
            getDevice().collectStatus = collectState;
        }
    }

    public SleepSceneConfig getCurSleepConfig() {
        if (mCurSleepConfig == null) {
            mCurSleepConfig = SceneUtils.getSleepSceneConfig();
        }
        return mCurSleepConfig;
    }

}
