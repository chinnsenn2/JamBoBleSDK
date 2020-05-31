package com.jianbao.jamboble.device.nox;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import com.appbase.utils.GsonHelper;
import com.jianbao.jamboble.device.nox.bean.CallbackData;
import com.jianbao.jamboble.device.nox.bean.SleepSceneConfig;
import com.jianbao.jamboble.device.nox.interfaces.IDeviceManager;
import com.jianbao.jamboble.device.nox.interfaces.INoxManager;
import com.jianbao.jamboble.device.nox.manager.AppManager;
import com.jianbao.jamboble.device.nox.manager.CentralManager;
import com.jianbao.jamboble.device.nox.manager.Nox2BManager;
import com.jianbao.jamboble.device.nox.utils.SceneUtils;

import jianbao.PreferenceUtils;

public class DeviceService extends Service {

    private static final String TAG = DeviceService.class.getSimpleName();
//    public static final String ACTION_BROCAST_ALARM_STOP = "action_brocast_alarm_stop";
    private static final int WHAT_BROCAST_RECEIVE = 90001;
    private final IBinder mBinder = new LocalBinder();
//    private static final String ACTION_BROCAST_TIME_TICK = "action_brocast_time_tick";
//    public static final String ACTION_BROCAST_STOP_TIME_TICK = "action_brocast_stop_time_tick";
//    public static final String ACTION_BROCAST_START_TIME_TICK = "action_brocast_start_time_tick";
//    public static final String ACTION_BROCAST_SEVICE_SCENE_STOP = "action_brocast_sevice_scene_stop";
    public static final String ACTION_BROCAST_SEVICE_AID_STOP = "action_brocast_sevice_aid_stop";

    //private Notification realTimeNotify, clockNotify;
    //private int clockNotifyId = -120110, realTimeNotifyId = -120111;
    //private WakeLock wakeLock;


    /**
     * app监测睡眠的服务
     */
    private AppManager mAppManager;

//    boolean mNeedNextTick = true;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_BROCAST_RECEIVE) {
                String action = msg.obj.toString();
                handleBrocast(action);

            }
        }
    };


    /**
     * 处理广播
     *
     * @param action
     */
    private void handleBrocast(String action) {
        Log.d(TAG, "设备服务收到广播：" + action);
        if (TextUtils.equals(ACTION_BROCAST_SEVICE_AID_STOP, action)) {
            SleepSceneConfig config;
            String sceneStr = PreferenceUtils.getString(this.getApplicationContext(), PreferenceUtils.KEY_NOX_SLEEP_SCENE, "");
            if (!TextUtils.isEmpty(sceneStr)) {
                config = (SleepSceneConfig) GsonHelper.stringToBean(sceneStr, SleepSceneConfig.class);
            } else {
                config = new SleepSceneConfig();
                config.monitorDeviceType = DeviceType.DEVICE_TYPE_PHONE;
                config.sleepAidFlag = 1;
                config.sleepAidOpenFlag = 1;
                config.sleepAidSmartFlag = 1;
                config.sleepAidCountTime = 15;
                config.smartAlarmFlag = 0;
                config.countTime = 1;
            }

            if (config.sleepAidSmartFlag == 0) {
                return;
            }
            Nox2BManager.getInstance(this).sleepAidControl(INoxManager.PostSleepAidControl.STOP, (byte) 0, (byte) 0, -1);
        }
//        if (ACTION_BROCAST_TIME_TICK.equals(action)) {
//            startNextTick(getApplicationContext(), true);
//            if (mAppManager != null) {
////                mAppManager.timeTick();
//            }
//        } else if (ACTION_BROCAST_START_TIME_TICK.equals(action)) {
//            setConnectSleepDotEnable(true);
//            startNextTick(getApplicationContext(), false);
//            //confirmClock();
//        } else if (ACTION_BROCAST_STOP_TIME_TICK.equals(action)) {
//
//        } else if (ACTION_BROCAST_ALARM_STOP.equals(action)) {
//            phoneAlarmStop();
//        } else if (Intent.ACTION_SCREEN_ON.equals(action) || Intent.ACTION_SCREEN_OFF.equals(action) || Intent.ACTION_BATTERY_CHANGED.equals(action)) {
//            //预防措施，手机亮屏，锁屏，电量变化的时候都去检测
//            sendBroadcast(new Intent(ACTION_BROCAST_TIME_TICK));
//        }
    }

    public void startNextTick(Context context, boolean needCheck) {

//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent tickIntent = new Intent(DeviceService.ACTION_BROCAST_TIME_TICK);
//        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, tickIntent, PendingIntent.FLAG_ONE_SHOT);

//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.SECOND, 0);
//        long nextTickTime = calendar.getTimeInMillis() + 60 * 1000;
//
//        if (Build.VERSION.SDK_INT >= 19) {
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextTickTime, pIntent);
//        } else {
//            alarmManager.set(AlarmManager.RTC_WAKEUP, nextTickTime, pIntent);
//        }
    }

    private BaseCallback mCallback = new BaseCallback() {
        @Override
        public void onStateChange(IDeviceManager deviceManager, String sender, ConnectionState state) {

            if (state == ConnectionState.CONNECTED) {
                CentralManager centralManager = SceneUtils.getCenteralManager(DeviceService.this, SceneUtils.SLEEP_SCENE_ID);
                if (mConnectedSeeRealData) {
                    mConnectedSeeRealData = false;
                    Log.d(TAG, " onStateChange see realtime data----------------");
                    Log.d(TAG, TAG + "开始查看实时数据===========");//熄灭会停止查看实时数据，导致智能闹钟范围内收不到入睡与清醒标志，入睡与清醒标志是以实时数据的形式推送的

                    centralManager.realDataView();
                } else if (mConnected2SceneStop) {
                    mConnected2SceneStop = false;
                    centralManager.sceneStop(SceneUtils.SLEEP_SCENE_ID);
                }
            } else if (state == ConnectionState.DISCONNECT) {
                mConnectedSeeRealData = false;
                mConnected2SceneStop = false;
            }
        }

        @Override
        public void onDataCallback(CallbackData callbackData) {
            Log.d(TAG, " onDataCallback " + callbackData);
//            if (callbackData.getType() == ICentralManager.TYPE_METHOD_SCENE_STOP) {
////                GlobalInfo.setPhoneAlarmValid(false);
//                SceneUtils.updateSceneStatus();
//
//                //只处理从闹钟页面过来的回调
//                if (callbackData.isSuccess() && TAG.equals(callbackData.getSender())) {
//                    CentralManager manager = SceneUtils.getCenteralManager(getApplicationContext(), SceneUtils.SLEEP_SCENE_ID);
//                    SystemClock.sleep(100);
//                    //停止场景成功，发送广播给页面刷新状态
//                    sendBroadcast(new Intent(ACTION_BROCAST_SEVICE_SCENE_STOP));
//                }
//            }
        }
    };
    private PowerManager.WakeLock wakeLock;

    public AppManager getAppManager() {
        return mAppManager;
    }

    //改成静态，是方便外部调用，因为该对象的实例获取比较麻烦
    private static boolean mScanSleepdotEnable = true;


    @Override
    public void onCreate() {
        super.onCreate();
        //acquireWakeLock();
        Log.d(TAG, "    设备服务启动");
        Log.d(TAG, " onCreate----------");
        IntentFilter filter = new IntentFilter(ACTION_BROCAST_SEVICE_AID_STOP);
//        filter.addAction(ACTION_BROCAST_STOP_TIME_TICK);
//        filter.addAction(ACTION_BROCAST_START_TIME_TICK);
//        filter.addAction(ACTION_BROCAST_ALARM_STOP);
//        filter.addAction(Intent.ACTION_SCREEN_ON);
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(timeTickReceiver, filter);

        //开始Tick
//        sendBroadcast(new Intent(DeviceService.ACTION_BROCAST_TIME_TICK));
    }

    public void acquireWakeLock() {
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Sleepace");
        }
        wakeLock.acquire();
    }

    public void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, 0, startId);
    }

    private BroadcastReceiver timeTickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, TAG + "   --广播接收Action:" + intent.getAction());
            //说明已经退出登录，或者场景没开始，不干任何事
            mHandler.obtainMessage(WHAT_BROCAST_RECEIVE, intent.getAction()).sendToTarget();
        }
    };

    /**
     * 手机闹钟结束后调用方法
     */
    private void phoneAlarmStop() {
        Log.d(TAG, " phoneAlarmStop-------------");
        CentralManager manager = SceneUtils.getCenteralManager(this, SceneUtils.SLEEP_SCENE_ID);
        //设置Sender这样就不回调到其他页面了
        manager.registCallBack(mCallback, TAG);
        //如果设备连接才去下载报告
        if (manager.isConnected()) {
            manager.sceneStop(SceneUtils.SLEEP_SCENE_ID);
        } else {
            mConnected2SceneStop = true;
            manager.connectDevice();
        }
    }

    private boolean mConnectedSeeRealData;
    private boolean mConnected2SceneStop;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "    设备服务销毁");
        unregisterReceiver(timeTickReceiver);
        //  releaseWakeLock();
    }

    public void setAppManager(AppManager mAppManager) {
        this.mAppManager = mAppManager;
    }

    /**
     * 设置是否可以连接纽扣，true:可以连接纽扣。false:不可连接
     *
     * @param enable
     */
    public static void setConnectSleepDotEnable(boolean enable) {
        mScanSleepdotEnable = enable;
    }

    public class LocalBinder extends Binder {
        public DeviceService getService() {
            return DeviceService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


}
