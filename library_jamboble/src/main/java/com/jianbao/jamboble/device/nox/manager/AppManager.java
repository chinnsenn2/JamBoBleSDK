package com.jianbao.jamboble.device.nox.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.appbase.utils.TimeUtil;
import com.jianbao.jamboble.device.nox.ConnectionState;
import com.jianbao.jamboble.device.nox.Device;
import com.jianbao.jamboble.device.nox.DeviceService;
import com.jianbao.jamboble.device.nox.DeviceType;
import com.jianbao.jamboble.device.nox.bean.CallbackData;
import com.jianbao.jamboble.device.nox.bean.RealTimeBean;
import com.jianbao.jamboble.device.nox.bean.SleepSceneConfig;
import com.jianbao.jamboble.device.nox.interfaces.IMonitorManager;
import com.jianbao.jamboble.device.nox.interfaces.ISleepAidManager;
import com.jianbao.jamboble.device.nox.utils.NoxGlobalInfo;
import com.jianbao.jamboble.device.nox.utils.SceneUtils;
import com.medica.xiangshui.jni.AlgorithmUtils;
import com.medica.xiangshui.jni.phone.PhoneAlgorithmIn;
import com.medica.xiangshui.jni.phone.PhoneAlgorithmOut;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static com.jianbao.doctor.bluetooth.device.nox.utils.SceneUtils.SLEEP_SCENE_ID;


public class AppManager extends DeviceManager implements IMonitorManager, ISleepAidManager {

    public static final int MIN_SLEEP_TIME = 10;
    //protected Handler mHandler = new Handler();
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
//    AudioManager mAudioManager;
    /**
     * 最少检测的时间
     */
    private static final byte MIN_REALTIME = 10;

    /**
     * 监测类型，睡眠
     */
    public static final byte TYPE_MONITOR_SLEEP = 2;
    /**
     * 监测类型，测试
     */
    public static final byte TYPE_MONITOR_TEST = 5;

    /**
     * 标志，启用
     */
    public static final int FLAG_ENABLE = 1;
    /**
     * 标志，禁用
     */
    public static final int FLAG_DISABLE = 0;

    /**
     * 监测类型，实际睡眠还是测试
     * 传递给 app算法的
     *
     * @see #TYPE_MONITOR_SLEEP
     * @see #TYPE_MONITOR_TEST
     */
    private byte mMonitorType = TYPE_MONITOR_SLEEP;
    private String musicStartPlayTime;
    private long currentTimeMillis;
    private int musicdAlbumId;

    public PhoneAlgorithmOut getmAlgorithOut() {
        return mAlgorithOut;
    }

    public void setAlgorithOut(PhoneAlgorithmOut mAlgorithOut) {
        this.mAlgorithOut = mAlgorithOut;
    }

    private PhoneAlgorithmOut mAlgorithOut;

    /**
     * 助眠智能结束，最多助眠时间，单位：分钟
     */
    private final static int MAX_SLEEP_AID_TIME = 45;
    /**
     * 最多监测时间，超过这个时间自动停止,单位：小时
     */
    private final static int MAX_SLEEP_TIME = 16;

    public static int getmMonitorStartTime() {
        return mMonitorStartTime;
    }

    public static void setmMonitorStartTime(int mMonitorStartTime) {
        AppManager.mMonitorStartTime = mMonitorStartTime;
    }

    /**
     * 当前开始睡眠的时间点
     */
    private static int mMonitorStartTime = 0;


    private final int frequency = 31250;// 单位微妙
    // private final int maxBatchReportLatency = 10000000;// 单位微妙，10s处理一次

    private DeviceService mService;

    private static AppManager instance;

    private boolean isBound = false;

//    private int tempPlayPosition = -1;

//    private float volume=0;


    /**
     * 主要用于播放喜马拉雅音乐
     * 播放音乐操作是否响应，在未响应的情况下，同一首音乐，不接受再次点播
     * true: 响应成功，可以接收新的点播任务
     * false: 还未响应，不可接收新的点播任务，需等待播放响应结果，回调中可接收
     */
    private boolean playMusicOperResponse = true;

    private static final int UPDATE = 1;
//    protected List<IXmPlayerStatusListener> mXmPlayerStatusListeners = new ArrayList<>();

    private AppServiceConnection appServiceConnection;

    @Override
    protected synchronized void dataCallback(CallbackData callbackData) {
        callbackData.setDeviceType(DeviceType.DEVICE_TYPE_PHONE);
        super.dataCallback(callbackData);
    }

    public static AppManager getInstance(Context context) {
        if (instance == null) {
            synchronized (AppManager.class) {
                if (instance == null) {
                    instance = new AppManager(context);
                }
            }
        }
        return instance;
    }

    private PowerManager.WakeLock mWakeLock;


    private AppManager(Context context) {
        //this.mContext = context;
        this.mContext = context.getApplicationContext();//获取Application的context避免内存泄漏
        connAppService();
    }

    private int mSleepAidStarTime;
    int mSleepAidTime;
    /**
     * 这个是秒值
     */
    int mTotalSleepAidTimeSecond;


    private int mSleepAidCountTimeMinute;
    private SleepSceneConfig mSleepConfig;
    private boolean mIsSleepAidRun;

//    private MediaPlayer mMediaPlayer;

    private BlockingQueue<float[]> dataQueue;

    private boolean writeOver = false;
    private int failCount = 0;

    @Override
    public void sleepAidControl(byte lightControl, byte musicContrl) {

    }

    @Override
    public void environmentDataGet() {

    }

    private SleepCallBack mTestCallback;

    private Runnable run = new Runnable() {
        public void run() {

            int count = 0;
            try {
                while (!writeOver) {
                    count = 0;
                    mIsWakeup = false;
                    while (true) {
                        //监测逻辑
                        in.flag_control = mMonitorType;

                        float[] data = dataQueue
                                .poll(20, TimeUnit.MILLISECONDS);

                        if (data == null) {

                            break;
                        }

                        in.xdata = data[0];
                        in.ydata = data[1];
                        in.zdata = data[2];
                        in.alarmrange = 0;
                        in.num_SDT = mMonitorStartTime;


                        PhoneAlgorithmOut out = AlgorithmUtils.phone(in);

                        if (count % 2 == 0) {
                            checkToReport(out);
                        }

                        if (in.flag_control == TYPE_MONITOR_SLEEP) {
                            count++;
                        }
                        //如果大于16小时监测，自动结束
//                        LogUtil.logE(TAG+"  监测自动结束时间："+(TimeUtil.getCurrentTimeInt() - mMonitorStartTime));
                        if (TimeUtil.getCurrentTimeInt() - mMonitorStartTime > MAX_SLEEP_TIME * 60 * 60) {
//                        if (TimeUtil.getCurrentTimeInt() - mMonitorStartTime > 11 * 60) {
                            Log.d(TAG, "  超过16小时自动结束，生成报告");
                            CentralManager manager = SceneUtils.getCenteralManager(mContext, SLEEP_SCENE_ID);
                            manager.sceneStop(SLEEP_SCENE_ID);
                            downHistory(0, 0, null);
                        }

                    }

                    if (in.flag_control == TYPE_MONITOR_SLEEP) {
                        SystemClock.sleep(5000);
                        if (count == 0) {// 说明手机锁屏
                            failCount++;
                            if (failCount == 2) {//

                            } else if (failCount >= 5) {
                                writeOver = true;
                                writeThread = null;
                                break;
                            }
                        } else {
                            failCount = 0;
                        }
                    }

//                    Log.d(TAG,"手机监测在采集数据,超过五条空数据，自动结束监测，数据条数：" + count);
                }

                String log = TAG + " collect data over------fail:" + failCount;
                Log.d(TAG, log);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "手机监测启动异常：" + e.toString());
            }
        }


    };
    /**
     * 发送睡眠标记计数器
     */
    private int mSleepNoticeCount;
    /**
     * 是否已入睡
     */
    private boolean mIsSleep;
    /**
     * 发送清醒标记计数器
     */
    private int mWakeupNoticeCount;
    /**
     * 是否已清醒
     */
    private boolean mIsWakeup;


    private void checkToReport(PhoneAlgorithmOut outAct) {
        if (outAct != null) {
//            String log = "手机睡眠监测 清醒标志（1唤醒）:" + outAct.getFlag_alarmclock() + ",助眠标志（0停止）:" + outAct.getFlag_sleepmusic() + "  mIsWakeUp:" + mIsWakeup + "   mIsSleep:" + mIsSleep;
//            Log.e(TAG, log);
//            Log.d(TAG, "睡眠提示次数 ---------外------- " + mSleepNoticeCount);
            if (outAct.getFlag_sleepmusic() == 0 && !mIsSleep) {// 不助眠
                Log.d(TAG , "   发送入睡标志 mIsSleep:" + mIsSleep + ",mSleepNoticeCount:" + mSleepNoticeCount);
//                RealTimeBean bean = new RealTimeBean();
//                bean.setDeviceState(DeviceType.DEVICE_TYPE_PHONE);
//                bean.setSleepFlag(1);
//                putRealTime(bean);
                mSleepNoticeCount++;
                if (mSleepNoticeCount > 10) {
                    mService.sendBroadcast(new Intent(DeviceService.ACTION_BROCAST_SEVICE_AID_STOP));
                    mIsSleep = true;
                }
            }

            // 唤醒
//            if (outAct.getFlag_alarmclock() == 0) {// 不唤醒
//
//                count++;
//                if (count % 20 == 0) {
//                    // LogCustom.i(TAG, "---不唤醒---002");
//                    RealTimeBean bean = new RealTimeBean();
//                    bean.setSleepFlag(0);
//                    bean.setDeviceState(DeviceType.DEVICE_TYPE_PHONE);
//                    putRealTime(bean);
//                    count = 0;
//                }
//
//            } else
//            if (outAct.getFlag_alarmclock() == 1 && !mIsWakeup) {// 唤醒
//                Log.d(TAG, "   发送唤醒标志 mIsWakeup:" + mIsWakeup + ",mWakeupNoticeCount:" + mWakeupNoticeCount);
//                RealTimeBean bean = new RealTimeBean();
//                bean.setDeviceState(DeviceType.DEVICE_TYPE_PHONE);
//                bean.setWakeFlag(1);
//                putRealTime(bean);
//                mWakeupNoticeCount++;
//                if (mWakeupNoticeCount > 1) {
//                    mIsWakeup = true;
//                }
//            }

        }

    }

    private void putRealTime(RealTimeBean bean) {
        CallbackData data = new CallbackData();
        data.setType(TYPE_METHOD_SLEEP_STATUS);
        data.setSender(getSender());
        data.setStatus(CallbackData.STATUS_OK);
        data.setResult(bean);
        dataCallback(data);
    }


    /**
     * 采集的状态
     */
    private boolean collectState = false;

    private Thread writeThread;


    /**
     * <h3>开始睡觉，开始采集数据</h3>
     * <ul>
     * <li>注册 传感器加速度 监听器</li>
     * </ul>
     */
    public boolean startSleep(boolean isTest) {
        Log.d(TAG, "  App开始监测");
        String log = TAG + " startMonitor test:" + isTest + ",collS:"
                + collectState;
        Log.d(TAG, log);
        boolean result = false;
        if (collectState) {
            return true;
        }

        mMonitorType = isTest ? TYPE_MONITOR_TEST : TYPE_MONITOR_SLEEP;
        collectState = true;
        // 先释放算法的内存
        phoneFreeMemory();
        // 给算法分配内存
        phoneInitMemory();

        mMonitorStartTime = TimeUtil.getCurrentTimeInt();

        PowerManager manager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);// CPU保存运行
        mWakeLock.acquire();

        registerLis();

        failCount = 0;
        writeOver = false;
        if (writeThread != null && writeThread.isAlive()) {
            Log.d(TAG, TAG + " run alive----------");
        } else {
            writeThread = new Thread(run);
            writeThread.start();
        }

        mIsSleep = false;
        mIsWakeup = false;
        mWakeupNoticeCount = 0;
        mSleepNoticeCount = 0;

        result = true;
        return result;
    }


    /**
     * <h3>给算法分配内存</h3>
     * <ul>
     * <li></li>
     * </ul>
     */
    public void phoneInitMemory() {
        PhoneAlgorithmIn in = new PhoneAlgorithmIn();
        in.flag_control = 1;
        in.xdata = 0;
        in.ydata = 0;
        in.zdata = 0;
        in.num_SDT = mMonitorStartTime;
        AlgorithmUtils.phone(in);
    }

    /**
     * <h3>让算法释放内存</h3>
     * <ul>
     * <li></li>
     * </ul>
     */
    public void phoneFreeMemory() {
        PhoneAlgorithmIn in = new PhoneAlgorithmIn();
        in.flag_control = 0;
        in.xdata = 0;
        in.ydata = 0;
        in.zdata = 0;
        AlgorithmUtils.phone(in);
    }

    /**
     * 睡眠结束
     *
     * @param phoneStartTime 睡眠开始时间
     * @param monitorSeconds 睡眠持续的秒数，flag_control = 3计算时传入，为实际总监测时长，单位:s.
     */
    public PhoneAlgorithmOut phoneSleepOver(int phoneStartTime, int monitorSeconds) {
        PhoneAlgorithmIn in = new PhoneAlgorithmIn();
        in.flag_control = 3;
        in.xdata = 0;
        in.ydata = 0;
        in.zdata = 0;
        in.calibsecs = monitorSeconds;
        in.num_SDT = phoneStartTime;
//        in.calibsecs = TimeUtil.getCurrentTimeInt() - phoneStartTime;
//        in.num_SDT = phoneStartTime;
        PhoneAlgorithmOut phoneOut = AlgorithmUtils.phone(in);
        if (phoneOut.sleep_stage == null
                || (phoneOut.sleep_stage.length > 0 && phoneOut.sleep_stage[0] > 0)) {
            phoneOut.monitormins = -1;
        }
        phoneFreeMemory();
        System.gc();
        Log.d(TAG, phoneOut.toString());
        return phoneOut;
    }


    public void registerLis() {
//        LogUtil.showMsg(TAG + " registerLis-------------------------");
        // if(!flag){
        sensorManager.registerListener(sensorEventListener,
                accelerometerSensor, frequency);
        // }
    }

    public void beginTest(SleepCallBack testCallback) {
        this.mTestCallback = testCallback;
        startSleep(true);
    }

    public void endTest() {
        stopSleep(true);
        if (mTestCallback != null) {
            this.mTestCallback = null;
        }
    }

    public boolean getCollectStatus() {
        return collectState;
    }

    /**
     * <h3>结束采集</h3>
     * <ul>
     * <li>取消注册 传感器加速度 监听器</li>
     * </ul>
     */
    public boolean stopSleep(boolean isTest) {
        Log.d(TAG, "  App结束监测,监测状态：" + collectState + "  开始监测时间：" + mMonitorStartTime + "   监测持续时间：" + (TimeUtil.getCurrentTimeInt() - mMonitorStartTime));
        String log = TAG + " stopMonitor test:" + isTest + ",collS:" + collectState;
        Log.d(TAG, log);

        boolean result = false;
        if (!collectState) {
            return result;
        }
        collectState = false;


        try {
            if (mWakeLock != null) {
                mWakeLock.release();
                mWakeLock = null;
            }
            sensorManager.unregisterListener(sensorEventListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        writeOver = true;
        writeThread = null;
        dataQueue.clear();
        System.gc();
        mAlgorithOut = phoneSleepOver(mMonitorStartTime, TimeUtil.getCurrentTimeInt() - mMonitorStartTime);
        result = mAlgorithOut != null;
        CallbackData callbackData = new CallbackData();
        callbackData.setSender(sender);
        callbackData.setDeviceType(DeviceType.DEVICE_TYPE_PHONE);
        callbackData.setType(TYPE_METHOD_STOP_SEE_RAW_DATA);
        callbackData.setResult(mAlgorithOut);
        dataCallback(callbackData);
        return result;
    }

    /**
     * 获取到的加速度 计算器
     */
    private PhoneAlgorithmIn in = new PhoneAlgorithmIn();

    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
//            Log.d(TAG, "  Sensor有数据：" + event.sensor.getName() + "  数据：" + Arrays.toString(event.values));
            if (event.sensor == accelerometerSensor && collectState) {// 只检查加速度的变化
                // SleepLog.e(AppManager.class,
                // "采集到数据了1111");
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                // g=9.80665 m/s^2 1/g = 0.10197162f
                float g1 = 0.10197162f;
                float[] data = new float[3];
                data[0] = x * g1;
                data[1] = y * g1;
                data[2] = z * g1;
                dataQueue.offer(data);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


//    public void upLoadHistory(final String deviceId, final int[] romTemp, final int romWet[], final int[] noise, final int[] light, final Handler mhandler) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if (GlobalInfo.getSceneStatus()) {
//                    Log.d(TAG, " 手机监测,场景在监测，同步云端报告");
//                    HistoryDataServer data = new HistoryDataServer();
//                    data.downHistoryCount(mhandler);
//                } else {
//                    int resultCount = 0;
//                    Log.d(TAG, " 手机监测睡眠结果：" + mAlgorithOut);
//
//                    if (mAlgorithOut != null && mAlgorithOut.monitormins >= MIN_REALTIME && mAlgorithOut.monitormins <= MAX_SLEEP_TIME * 60) {
//
//                        ClientAnalyzeData client = new ClientAnalyzeData();
//                        client.analyzeAppRealData(mMonitorStartTime, SleepaceApplication.getInstance().getCurrentUserMemberID(), deviceId, mAlgorithOut, romTemp, romWet, noise, light);
//                        resultCount = 1;
//                        StatisticsLog.statisticsOperationAction(StatisticsConstants.STATISTICS_TYPE_OPERATION, StatisticsLog.getUserId(), StatisticsConstants.FROM_PHONE, StatisticsLog.getCurrentPlayTime(), StatisticsConstants.SYNCHRONIZE_REPORT, DeviceType.DEVICE_TYPE_PHONE, mContext.getString(R.string.synchronize_report_success));
//                    } else {
//                        StatisticsLog.statisticsOperationAction(StatisticsConstants.STATISTICS_TYPE_OPERATION, StatisticsLog.getUserId(), StatisticsConstants.FROM_PHONE, StatisticsLog.getCurrentPlayTime(), StatisticsConstants.SYNCHRONIZE_REPORT, DeviceType.DEVICE_TYPE_PHONE, mContext.getString(R.string.no_report1));
//
//                    }
//                    Log.d(TAG, "手机生成报告是否成功:" + (resultCount == 1));
//                    if (mhandler != null) {
//                        mhandler.obtainMessage(SynDataActivity.MSG_WHAT_DOWNLOAING, resultCount, resultCount).sendToTarget();
//                    }
//                    mAlgorithOut = null;
//                }
//            }
//        }).start();
//    }


    @Override
    public void downHistory(int startTime, int endTime, final Handler handler) {
//        upLoadHistory(getDevice().deviceId, null, null, null, null, mhandler);
    }

    @Override
    public void collectStatusGet() {
        CallbackData data = new CallbackData();
        data.setType(TYPE_METHOD_COLLECT_STATUS);
        data.setStatus(CallbackData.STATUS_OK);
        data.setSender(sender);
        byte result = (byte) (collectState ? 1 : 0);
        data.setResult(result);
        dataCallback(data);
    }

    @Override
    public void collectStart() {

        CallbackData data = new CallbackData();
        data.setType(TYPE_METHOD_COLLECT_START);
        if (startSleep(false)) {
            data.setStatus(CallbackData.STATUS_OK);
            data.setResult(true);
        } else {
            data.setStatus(CallbackData.STATUS_FAILED);
            data.setResult(false);
        }
        dataCallback(data);
        mIsSleepAidRun = true;
    }

    @Override
    public boolean collectStartSyn() {
        CallbackData data = new CallbackData();
        data.setType(TYPE_METHOD_COLLECT_START);
        boolean success = startSleep(false);
        if (success) {
            data.setStatus(CallbackData.STATUS_OK);
            data.setResult(true);
        } else {
            data.setStatus(CallbackData.STATUS_FAILED);
            data.setResult(false);
        }
        dataCallback(data);
        mIsSleepAidRun = true;
        return success;
    }

    @Override
    public void collectStop() {
        CallbackData data = new CallbackData();
        data.setType(TYPE_METHOD_COLLECT_STOP);
        if (collectStopSyn()) {
            data.setStatus(CallbackData.STATUS_OK);
            data.setResult(true);
        } else {
            data.setStatus(CallbackData.STATUS_FAILED);
            data.setResult(false);
        }
        mIsSleepAidRun = false;
        dataCallback(data);
    }

    @Override
    public boolean collectStopSyn() {
        Log.d(TAG, "   当前采集状态：" + collectState);
        CallbackData data = new CallbackData();
        data.setSender(sender);
        data.setType(TYPE_METHOD_COLLECT_STOP);
        Log.d(TAG, TAG + " collectStopSyn collS:" + collectState);
        if (!collectState) {
            data.setStatus(CallbackData.STATUS_OK);
            data.setResult(true);
        } else {
            if (stopSleep(false)) {
                data.setStatus(CallbackData.STATUS_OK);
                data.setResult(true);
            } else {
                data.setStatus(CallbackData.STATUS_FAILED);
                data.setResult(false);
            }
        }
        dataCallback(data);
        Log.d(TAG, "   停止采集：" + data.isSuccess());
        Log.d(TAG, TAG + " collectStopSyn res:" + data);
        mIsSleepAidRun = false;
        return data.isSuccess();
    }

    private boolean isRealDataView;

    @Override
    public void realDataView() {
        isRealDataView = true;
    }

    @Override
    public boolean realDataViewSyn() {
        CallbackData data = new CallbackData();
        data.setType(TYPE_METHOD_REAL_DATA_VIEW);
        data.setStatus(CallbackData.STATUS_OK);
        data.setResult(true);
        dataCallback(data);
        return true;
    }

    @Override
    public void realDataStopView() {
        isRealDataView = false;
    }

    @Override
    public boolean realDataStopViewSyn() {
        CallbackData data = new CallbackData();
        data.setType(TYPE_METHOD_REAL_DATA_STOP_VIEW);
        data.setStatus(CallbackData.STATUS_OK);
        data.setResult(true);
        dataCallback(data);
        return true;
    }

    @Override
    public void startSeeRawData() {

    }

    @Override
    public void stopSeeRawData() {

    }

    @Override
    public void powerGet() {

    }

    @Override
    public void workModeGet() {

    }


    @Override
    public void connectDevice() {
        onStateChangeCallBack(ConnectionState.CONNECTED);
    }

    @Override
    public void connectDevice(Device device) {

    }


    @Override
    public ConnectionState getConnectionState() {
        return ConnectionState.CONNECTED;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void disconnect() {
        onStateChangeCallBack(ConnectionState.DISCONNECT);
    }

    @Override
    public void release() {
        writeOver = true;
        writeThread = null;
        if (isBound) {
            isBound = false;
            mContext.unbindService(appServiceConnection);
            mService = null;
        }
    }

    @Override
    public void configDeviceAfterBindSync() {

    }

    private int type;


    @Override
    public boolean sleepAidIsSmartStop() {
        return mSleepConfig != null && mSleepConfig.sleepAidSmartFlag == 1;
    }

    @Override
    public void sleepAidStart(SleepSceneConfig config) {
        Log.d(TAG, "------sleepAidStart---0----config:  " + config + "   mIsSleepAidRun:  " + mIsSleepAidRun);
        if (config == null) {
            return;
        }

        String log = TAG + " sleepAidStart config:" + config;
        Log.d(TAG, log);

        resetSleepAidConfig(config, false);
        mIsSleepAidRun = true;
    }


    /**
     * 重置助眠参数，
     *
     * @param config
     * @param needCheck 是否需要检查助眠参数是否相同 ，相同不重置
     */
    public void resetSleepAidConfig(SleepSceneConfig config, boolean needCheck) {
        if (config == null || (needCheck && config.isSleepAidConfigEquals(mSleepConfig))) {
            return;
        }

//        LogUtil.whereStartMe("哪里调用开始助眠");
        Log.d(TAG, "   重置手机助眠参数:" + config);
//        if (mSleepConfig != null && config.music.musicId != mSleepConfig.music.musicId && mIsSleepAidRun) {
//            //如果音乐已经修改，播放新音乐
//            musicStart(config.music);
//        }
        mSleepConfig = config;

        if (mSleepConfig.sleepAidSmartFlag == FLAG_ENABLE) {
            mSleepAidCountTimeMinute = MAX_SLEEP_AID_TIME;
        } else {
            mSleepAidCountTimeMinute = mSleepConfig.sleepAidCountTime;
//            mSleepAidCountTimeMinute = 1;
        }

        mTotalSleepAidTimeSecond = 0;
        mSleepAidStarTime = TimeUtil.getCurrentTimeInt();
        mSleepAidTime = mSleepAidStarTime;
    }

//    @Override
//    public void sleepAidControl(byte lightControl, byte musicContrl) {
//        sleepAidStart(mSleepConfig);
//    }

    @Override
    public void sleepAidStop(boolean isSlowlyStop) {
        String log = TAG + " sleepAidStop isSlowlyStop:" + isSlowlyStop + ",mIsSleepAidRun:" + mIsSleepAidRun;
        Log.d(TAG, log);
        mIsSleepAidRun = false;
        mSleepAidCountTimeMinute = 0;
        //连接的时候已经有判断是否在助眠
//        DeviceService.setConnectSleepDotEnable(false);
        CallbackData data = new CallbackData();
        data.setSender(sender);
        data.setType(TYPE_METHOD_SLEEP_AID_STOP);
        data.setStatus(CallbackData.STATUS_OK);
        data.setResult(true);
        dataCallback(data);
//        mContext.sendBroadcast(new Intent(ACTION_SLEEP_AID_STOP));
    }

    @Override
    public void sleepAidPause() {
        String log = TAG + " sleepAidPause-----------";
        Log.d(TAG, log);
    }


    @Override
    public void sleepAidResume() {
        String log = TAG + " sleepAidResume sceneStatus:" + NoxGlobalInfo.getSceneStatus() + ",mIsSleepAidRun:" + mIsSleepAidRun + ",totalTmie:" + mTotalSleepAidTimeSecond + ",aidTime" + mSleepAidCountTimeMinute + ",mSleepConfig:" + mSleepConfig;
        Log.d(TAG, log);

        if (NoxGlobalInfo.getSceneStatus() && !mIsSleepAidRun && (mTotalSleepAidTimeSecond / 60) < mSleepAidCountTimeMinute) {
            mIsSleepAidRun = true;

        }
    }

    /**
     * 一分钟跳动一次，服务内调用
     */
    public void timeTick() {
//        Log.d(TAG, "mIsSleepAidRun ---- " + mIsSleepAidRun);
//        //助眠时间统计逻辑
//        if (mIsSleepAidRun) {
//            mTotalSleepAidTimeSecond += ((TimeUtil.getCurrentTimeInt() - mSleepAidTime));
//
//            int delay = mTotalSleepAidTimeSecond / 60;
//            Log.d(TAG, "助眠剩余时间----mSleepAidCountTimeMinute:" + mSleepAidCountTimeMinute + "----delay:" + delay + "----助眠了多长：" + mTotalSleepAidTimeSecond);
//            if (mSleepAidCountTimeMinute <= delay) {
//                //停止助眠
//                sleepAidStop(true);
//                if (SceneUtils.getAlarmDeviceType(SLEEP_SCENE_ID) != DeviceType.DEVICE_TYPE_PHONE) {
//                    //助眠结束，如果闹钟不是手机，停止检查闹钟
//                    mContext.sendBroadcast(new Intent(DeviceService.ACTION_BROCAST_STOP_TIME_TICK));
//                }
//            }
//            mSleepAidTime = TimeUtil.getCurrentTimeInt();
//        }
    }

    @Override
    public boolean sleepAidIsRunningSync() {
        return mIsSleepAidRun;
    }

    public void setSleepAidRun(boolean mIsSleepAidRun) {
        this.mIsSleepAidRun = mIsSleepAidRun;
    }

    @Override
    public void sleepAidIsRunning() {
        CallbackData data = new CallbackData();
        data.setType(TYPE_METHOD_SLEEP_AID_ISRUNNING);
        data.setStatus(CallbackData.STATUS_OK);
        data.setResult(mIsSleepAidRun);
        data.setSender(sender);
        dataCallback(data);

        //Log.d(TAG,TAG+" sleepAidIsRunning run:" + mIsSleepAidRun);
    }


    private class AppServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBound = true;
            Log.e(TAG, "onServiceConnected======================= System.currentTime() = " + System.currentTimeMillis());
            mService = ((DeviceService.LocalBinder) service).getService();
            mService.setAppManager(AppManager.this);
            sensorManager = (SensorManager) mService
                    .getSystemService(Context.SENSOR_SERVICE);
            accelerometerSensor = sensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            dataQueue = new LinkedBlockingDeque<>(1024 * 10);
        }
    }

    public void connAppService() {
        Log.d(TAG, TAG + " connAppService================mService:" + (mService == null));
        if (mService == null) {
            Intent intent = new Intent(mContext, DeviceService.class);
            intent.setAction(TAG);
            appServiceConnection = new AppServiceConnection();
            mContext.bindService(intent, appServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public interface SleepCallBack {
        void sleepCallBack(int result, Object obj);
    }
}
