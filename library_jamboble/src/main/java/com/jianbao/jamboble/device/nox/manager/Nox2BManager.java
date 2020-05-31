package com.jianbao.jamboble.device.nox.manager;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.appbase.utils.TimeUtil;
import com.jianbao.jamboble.device.nox.Device;
import com.jianbao.jamboble.device.nox.bean.CallbackData;
import com.jianbao.jamboble.device.nox.bean.DataPacket;
import com.jianbao.jamboble.device.nox.bean.Nox2Packet;
import com.jianbao.jamboble.device.nox.bean.NoxLight;
import com.jianbao.jamboble.device.nox.bean.NoxWorkMode;
import com.jianbao.jamboble.device.nox.bean.SceneConfig;
import com.jianbao.jamboble.device.nox.bean.SleepSceneConfig;
import com.jianbao.jamboble.device.nox.interfaces.ICentralManager;
import com.jianbao.jamboble.device.nox.interfaces.IMonitorManager;
import com.jianbao.jamboble.device.nox.interfaces.INoxManager;
import com.jianbao.jamboble.device.nox.utils.SceneUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/6/7.
 */

public class Nox2BManager extends BleManager implements INoxManager {
    private static Nox2BManager sManager;

    private SleepSceneConfig mSleepSceneConfig;

    protected Nox2BManager(Context context) {
        super(context);
    }

    @Override
    public void release() {
        super.release();
        sManager = null;
    }

    public synchronized static Nox2BManager getInstance(Context context) {
        if (sManager == null) {
            synchronized (Nox2BManager.class) {
                if (sManager == null) {
                    sManager = new Nox2BManager(context);
                }
            }
        }
        return sManager;
    }


    //接收到的蓝牙包
    private Nox2Packet mReceivePack = new Nox2Packet();
    /**
     * 最新的工作模式缓存
     */
    NoxWorkMode mWorkMode;
    /*public String formatDeviceID(byte[] scanRecord) {
        String deviceId = super.formatDeviceID(scanRecord);
        if(deviceId != null){
            deviceId = deviceId.replace("-", "");
        }
        return deviceId;
    }*/


    @Override
    public String getDeviceMDIDSync() {
        /*String deviceID = null;
        CallbackData data = requestDevice(SleepDotPacket.PacketMsgType.DEVICE_INFO);
        SleepDotPacket.SleepDotDeviceInfoRsp rsp = (SleepDotPacket.SleepDotDeviceInfoRsp) data.getResult();
        if (rsp != null) {
            deviceID = rsp.deviceId;
        // LogUtil.showMsg("getDeviceMDIDSync deviceID:" + deviceID);
        return deviceID;*/

        //登录设备的时候已经获取了设备id信息
        String deviceID = getDevice().deviceId;
        Log.d(TAG, "getDeviceMDIDSync deviceID:" + deviceID);
        return deviceID;
    }


    @Override
    public void handleLeData(byte[] data) {
//        Log.d(TAG,TAG + " handleLeData:" + Arrays.toString(data));
        for (int i = 0; i < data.length; i++) {

//            if (!mReceivePack.buffer.hasRemaining()) {
//                mReceivePack.buffer.get();
//            }

            mReceivePack.buffer.put(data[i]);

            if (mReceivePack.buffer.position() >= 4 && mReceivePack.buffer.getInt(mReceivePack.buffer.position() - 4) == 0x245F402D) {
                mReceivePack.buffer.limit(mReceivePack.buffer.position());
                if (mReceivePack.check() && mReceivePack.parse(mReceivePack.buffer)) {
                    if (mReceivePack.head.type == DataPacket.PacketType.FA_RESPONSE) {
                        mReceiveDataPack.offer(mReceivePack);
                    } else if (mReceivePack.head.type == Nox2Packet.PacketType.FA_POST) {
                        //LogUtil.showMsg(TAG+" work mode type:" + mReceivePack.head.type);

                        CallbackData callbackData = new CallbackData();
                        callbackData.setSender(sender);
                        callbackData.setType(mReceivePack.msg.type);
                        if (mReceivePack.msg.type == Nox2Packet.PacketMsgType.WORK_MODE_QUERY) {
                            //如果是工作状态推送，改变类型
                            //LogUtil.logTemp("收到Nox2助眠工作模式推送：" + mReceivePack.msg.content);
                            callbackData.setType(TYPE_METHOD_WORK_MODE_GET);

                            Nox2Packet.WorkModeRsp rsp = (Nox2Packet.WorkModeRsp) mReceivePack.msg.content;
                            NoxWorkMode workMode = rsp.workMode;
                            checkSleepAidStop(workMode);
                            callbackData.setResult(workMode);
                        }
                        callbackData.setStatus(CallbackData.STATUS_OK);
                        dataCallback(callbackData);
                    }
                }
                mReceivePack = new Nox2Packet();
                mReceivePack.head = new Nox2Packet.Nox2PacketHead();
            }
        }
    }

    private void checkSleepAidStop(NoxWorkMode mode) {
        if (mode == null) return;
        if (mWorkMode != null && mWorkMode.sleepAidStatus == 1 && mode.sleepAidStatus == 0) {
            Log.d(TAG, "  检测到工作模式中助眠标志从1到0，发送助眠结束回调");
            //说明助眠状态从1变0，助眠结束
            CallbackData sleepaidCallback = new CallbackData();
            sleepaidCallback.setType(TYPE_METHOD_SLEEP_AID_STOP);
            sleepaidCallback.setStatus(CallbackData.STATUS_OK);
            sleepaidCallback.setSender(sender);
            sleepaidCallback.setResult(true);
            dataCallback(sleepaidCallback);
        }
        mWorkMode = mode;
    }

    @Override
    public DataPacket buildDataPacket(byte packetType, byte messageType, DataPacket.
            BasePacket basePack) {
        byte btSeq = DataPacket.PacketHead.getSenquence();
        Nox2Packet packet = new Nox2Packet();
        packet.msg = new Nox2Packet.Nox2PacketBody(messageType, basePack);
        packet.fill(packetType, btSeq);
        return packet;
    }

    public void infoSync() {
        Log.d(TAG, TAG + " infoSync device:" + device);
//        getDeviceVerSync();
//        setSyncTime();
        workModeGetSyn();
    }

    public void configDeviceAfterBindSync() {
        Log.d(TAG, "   设置绑定后配置信息");
        SleepSceneConfig config = SceneUtils.getSleepSceneConfig(getDeviceType());
        sceneConfigSetSync(config);
    }


    /**
     * 清除闹钟,回调result为BaseRspPack
     */
    private void clearAlarmTime() {
        //setAlarmTimeSync(null);
    }

    /**
     * 同步时间,没有回调
     */
    protected void setSyncTime() {
        int ulTimestamp = (int) (System.currentTimeMillis() / 1000);
        int nTimezone = (int) (TimeUtil.getTimeZone() * 60 * 60);
//        int nTimezone = TimeZone.getDefault().getRawOffset() / 1000;
        Nox2Packet.TimeSyncReq req = new Nox2Packet.TimeSyncReq(ulTimestamp, nTimezone, (byte) 0, 0);
        requestDevice(Nox2Packet.PacketMsgType.TIME_SYNC, req, false);
    }


    /**
     * 获取设备版本信息,回调SleepDotGetDevVerInfoRsp
     */
    /*public void getDeviceVer() {
        requestAsycDevice(SleepDotPacket.PacketMsgType.SLEEPDOT_MSG_TYPE_DEVICE_VER);
        version = String.format("%d.%02d", rsp.usVerCode / 100, rsp.usVerCode % 100);
    }*/
    public void getDeviceVerSync() {
        CallbackData cd = requestDevice(Nox2Packet.PacketMsgType.DEVICE_INFO, false);
        Log.d(TAG, "  固件信息回调：" + cd);
        Nox2Packet.DeviceInfoRsp rsp = null;
        if (cd.getStatus() == CallbackData.STATUS_OK) {
            if (cd.getResult() instanceof Nox2Packet.DeviceInfoRsp) {
                rsp = (Nox2Packet.DeviceInfoRsp) cd.getResult();
            }
        }

        if (rsp != null) {
            //getDevice().deviceId = rsp.xDeviceInfo.deviceId;
//            getDevice().deviceName = rsp.xDeviceInfo.deviceName;
//            getDevice().deviceName = rsp.xDeviceInfo.deviceName.substring(0, 2).toUpperCase() + "-" + rsp.xDeviceInfo.deviceName.substring(2);
            getDevice().deviceId = rsp.xDeviceInfo.deviceId;
            getDevice().versionName = rsp.xDeviceInfo.versionName;
            getDevice().versionCode = Float.valueOf(rsp.xDeviceInfo.versionName);
            getDevice().btAddress = rsp.xDeviceInfo.btAddress;

            //Log.d(TAG,TAG+" dVer:" + getDevice().versionCode+",verCode:"+ver.curVerCode+",hasDevice:"+GlobalInfo.user.hasDevice(getDeviceType()));

        }

        Log.d(TAG, TAG + " getDeviceVerSync rsp:" + rsp + ",cd:" + cd + ",device:" + getDevice());
    }


    @Override
    public void downHistory(final int startTime, final int endTime, final Handler handler) {


    }

    @Override
    public void collectStatusGet() {
        //requestAsycDevice(SleepDotPacket.PacketMsgType.SLEEPDOT_MSG_TYPE_COLLECTSTATE_QUERY);
    }

    @Override
    public void collectStart() {
    }

    @Override
    public boolean collectStartSyn() {
        return true;
    }

    @Override
    public void collectStop() {
    }

    @Override
    public boolean collectStopSyn() {
        return true;
    }

    @Override
    public void startSeeRawData() {

    }

    @Override
    public void stopSeeRawData() {

    }

    @Override
    public void realDataView() {

    }

    @Override
    public boolean realDataViewSyn() {
        return true;
    }

    @Override
    public void realDataStopView() {

    }

    @Override
    public boolean realDataStopViewSyn() {
        return true;
    }


    @Override
    public void powerGet() {

    }


    @Override
    public void workModeGetSyn() {
        CallbackData data = requestDevice(Nox2Packet.PacketMsgType.WORK_MODE_QUERY);
        if (data.isSuccess() && data.getResult() != null && data.getResult() instanceof Nox2Packet.WorkModeRsp) {
            Nox2Packet.WorkModeRsp rsp = (Nox2Packet.WorkModeRsp) data.getResult();
            NoxWorkMode workMode = rsp.workMode;
            checkSleepAidStop(workMode);

            data.setResult(workMode);
        } else {
            data.setStatus(CallbackData.STATUS_FAILED);
        }
        data.setType(IMonitorManager.TYPE_METHOD_WORK_MODE_GET);
        dataCallback(data);
    }

    @Override
    public NoxWorkMode getCacheWorkWork() {
        return mWorkMode;
    }

    @Override
    public void workModeGet() {
        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                CallbackData data = requestDevice(Nox2Packet.PacketMsgType.WORK_MODE_QUERY);
                if (data.isSuccess() && data.getResult() != null && data.getResult() instanceof Nox2Packet.WorkModeRsp) {
                    Nox2Packet.WorkModeRsp rsp = (Nox2Packet.WorkModeRsp) data.getResult();
                    NoxWorkMode workMode = rsp.workMode;
                    checkSleepAidStop(workMode);

                    data.setResult(workMode);
                } else {
                    data.setStatus(CallbackData.STATUS_FAILED);
                }
                data.setType(IMonitorManager.TYPE_METHOD_WORK_MODE_GET);
                dataCallback(data);
            }
        });
    }

    @Override
    public void sleepAidStart(SleepSceneConfig config) {
        sleepAidControl(PostSleepAidControl.CONTROL, config.light.lightFlag, (byte) 0, TYPE_METHOD_SLEEP_AID_START);
    }

    @Override
    public void sleepAidStop(final boolean isSlowlyStop) {
        //Nox2在线音乐助眠，停止助眠时，停止本地音乐，蓝牙版添加了可下载的音乐类型(非喜马拉雅音乐,但是手机播放)，所以需要多一个判断标志：是否是手机播放
        if (mSleepSceneConfig != null) {
            AppManager appManager = AppManager.getInstance(mContext);
            appManager.sleepAidStop(isSlowlyStop);
        }

        if (isSlowlyStop) {
            sleepAidControl(PostSleepAidControl.LOWLY_STOP, PostSleepAidControl.CMD_LIGHT_CLOSE, PostSleepAidControl.CMD_MUSIC_STOP, TYPE_METHOD_SLEEP_AID_STOP);
        } else {
            sleepAidControl(PostSleepAidControl.STOP, PostSleepAidControl.CMD_LIGHT_CLOSE, PostSleepAidControl.CMD_MUSIC_STOP, TYPE_METHOD_SLEEP_AID_STOP);
        }
    }


    @Override
    public void sleepAidPause() {
        sleepAidControl(PostSleepAidControl.PAUSE, PostSleepAidControl.CMD_LIGHT_CLOSE, PostSleepAidControl.CMD_MUSIC_PAUSE, TYPE_METHOD_SLEEP_AID_PAUSE);
    }

    @Override
    public void sleepAidResume() {
        sleepAidControl(PostSleepAidControl.RESUME, PostSleepAidControl.CMD_LIGHT_OPEN, PostSleepAidControl.CMD_MUSIC_START, TYPE_METHOD_SLEEP_AID_RESUME);
    }

    @Override
    public void sleepAidIsRunning() {
        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                CallbackData data = requestDevice(Nox2Packet.PacketMsgType.WORK_MODE_QUERY);
                data.setType(TYPE_METHOD_SLEEP_AID_ISRUNNING);
                if (data.isSuccess()) {
                    if (data.getResult() instanceof Nox2Packet.WorkModeRsp) {
                        Nox2Packet.WorkModeRsp mode = (Nox2Packet.WorkModeRsp) data.getResult();
                        checkSleepAidStop(mode.workMode);
                        data.setResult(mode.workMode.sleepAidStatus == 1);
                    } else {
                        data.setResult(false);
                    }
                }
                dataCallback(data);
            }
        });
    }

    @Override
    public boolean sleepAidIsRunningSync() {
        if (mWorkMode != null) {
            return mWorkMode.sleepAidStatus == 1;
        }
        return false;
    }

    public void sleepAidControl(final byte operation, final byte lightControl, final byte musicContrl, final int callbackType) {
        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Nox2Packet.SleepAidOperationReq req = new Nox2Packet.SleepAidOperationReq(operation, lightControl, musicContrl);
                CallbackData data = requestDevice(Nox2Packet.PacketMsgType.CMD_SLEEP_AID, req);
                Log.d(TAG, "  Nox2发送助眠操作指令：" + operation + "   结果：" + data);
                data.setType(callbackType);
                dataCallback(data);
            }
        });
    }

    public void sleepAidControl(final byte lightControl, final byte musicContrl) {
        sleepAidControl(PostSleepAidControl.CONTROL, lightControl, musicContrl, -1);
    }

    @Override
    public void sceneStart(final int sceneId, final boolean isAuto, final SceneConfig config) {
        Log.d(TAG, TAG + " sceneStart seqid:" + sceneId + ",isAuto:" + isAuto);
        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                sceneStartSyn(sceneId, isAuto, config);
            }
        });
    }

    @Override
    public boolean sceneStartSyn(int sceneId, boolean isAuto, SceneConfig config) {
        mSleepSceneConfig = (SleepSceneConfig) config;
        Log.d(TAG, "   设置场景参数：" + config);
        // 设置场景参数，SAB蓝牙版添加了可下载的音乐类型(非喜马拉雅音乐,但是手机播放)，所以需要多一个判断标志：是否是手机播放
//        if (mSleepSceneConfig != null) {
//            AppManager appManager = AppManager.getInstance(mContext);
//            appManager.sleepAidStart(mSleepSceneConfig);
//        }

        Nox2Packet.SceneOperationReq req = new Nox2Packet.SceneOperationReq(PostSceneControl.OPEN, sceneId, config);
        CallbackData data = requestDevice(Nox2Packet.PacketMsgType.CMD_SCENE, req);
        //LogUtil.showMsg(TAG + " sceneStart sceneId:" + sceneId + ",data:" + data);
        data.setType(ICentralManager.TYPE_METHOD_SCENE_START);
        data.setResult(sceneId);
        dataCallback(data);
        return data.isSuccess();
    }

    @Override
    public void sceneDelete(final int sceneId) {
        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Nox2Packet.SceneOperationReq req = new Nox2Packet.SceneOperationReq(PostSceneControl.DELETE, sceneId);
                CallbackData data = requestDevice(Nox2Packet.PacketMsgType.CMD_SCENE, req);
                data.setType(ICentralManager.TYPE_METHOD_SCENE_DELETE);
                data.setResult(sceneId);
                dataCallback(data);
            }
        });
    }

    @Override
    public void sceneStop(final int sceneId) {
        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Nox2Packet.SceneOperationReq req = new Nox2Packet.SceneOperationReq(PostSceneControl.CLOSE, sceneId);
                CallbackData data = requestDevice(Nox2Packet.PacketMsgType.CMD_SCENE, req);
                data.setType(ICentralManager.TYPE_METHOD_SCENE_STOP);
                data.setResult(sceneId);
                dataCallback(data);

                if (data.isSuccess()) {
                    Log.d(TAG, "stop success");
                } else {
                    Log.d(TAG, "stop failed");
                }
            }
        });
    }

    @Override
    public void scenePause(long sceneSeqId) {

    }

    @Override
    public void sceneResume(long sceneSeqId) {

    }

    @Override
    public void preview(final byte operation, final byte exitFlag, final byte rebootFlag) {
        //Log.d(TAG,TAG + " preview oper:" + operation + ",exit:" + exitFlag + ",reboot:" + rebootFlag +",caller:" + LogUtil.getCaller());
        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Nox2Packet.LightPreviewReq req = new Nox2Packet.LightPreviewReq(operation, exitFlag, rebootFlag);
                CallbackData cd = requestDevice(Nox2Packet.PacketMsgType.CMD_PREVIEW, req);
                Log.d(TAG, TAG + " preview oper:" + operation + ",exit:" + exitFlag + ",reboot:" + rebootFlag + ",cd:" + cd);
                cd.setType(TYPE_METHOD_LIGHT_PREVIEW);
                dataCallback(cd);
                //MusicUtils.nox2BluetoothPlayModeCtl(mContext, true);
            }
        });
    }

    @Override
    public void lightOpen(final NoxLight noxLight) {
        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Nox2Packet.LightOperationReq req = new Nox2Packet.LightOperationReq(PostLightControl.OPEN, noxLight);
                requestPureDevice(Nox2Packet.PacketMsgType.CMD_LIGHT, req);
                CallbackData data = requestDevice(Nox2Packet.PacketMsgType.CMD_LIGHT, req);
                if (data.getStatus() != CallbackData.STATUS_OK) {
                    int retry = 3;
                    for (int i = 0; i < retry; i++) {
                        data = requestDevice(Nox2Packet.PacketMsgType.CMD_LIGHT, req);
                        if (data.getStatus() == CallbackData.STATUS_OK) {
                            break;
                        }
                        SystemClock.sleep(100);
                    }
                }
                data.setType(TYPE_METHOD_LIGHT_OPEN);
                dataCallback(data);
            }
        });
    }

    @Override
    public void lightColorSet(NoxLight noxLight) {
        Nox2Packet.LightOperationReq req = new Nox2Packet.LightOperationReq(PostLightControl.OPEN, noxLight);
        requestPureDevice(Nox2Packet.PacketMsgType.CMD_LIGHT, req);
    }

    @Override
    public void lightClose(final NoxLight light) {
        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Nox2Packet.LightOperationReq req = new Nox2Packet.LightOperationReq(PostLightControl.CLOSE, light);
                CallbackData data = requestDevice(Nox2Packet.PacketMsgType.CMD_LIGHT, req);
                data.setType(TYPE_METHOD_LIGHT_CLOSE);
                Log.d(TAG, TAG + " lightClose data:" + data);
                dataCallback(data);
            }
        });
    }

    @Override
    public void lightBrightness(final NoxLight light) {
        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Nox2Packet.LightOperationReq req = new Nox2Packet.LightOperationReq(PostLightControl.BRIGHTNESS_CONTROL, light);
                requestPureDevice(Nox2Packet.PacketMsgType.CMD_LIGHT, req);
                CallbackData data = requestDevice(Nox2Packet.PacketMsgType.CMD_LIGHT, req);
                data.setType(TYPE_METHOD_LIGHT_BRIGHTNESS_SET);
                dataCallback(data);
            }
        });
    }


    @Override
    public boolean sleepAidIsSmartStop() {
        return mSleepSceneConfig != null && mSleepSceneConfig.sleepAidSmartFlag == 1;
    }


    @Override
    public void sceneSleepConfigSet(SleepSceneConfig config) {
//        LogUtil.logE(TAG + "   重置睡眠参数：" + config);
        mSleepSceneConfig = config;
        sceneConfigSet(config);
    }


    @Override
    public void setMonitorDevice(Device monitorDevice) {

    }


    @Override
    public void bluthoothPlayModeControl(final boolean open) {
        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Nox2Packet.BluetoothPlayModeCtlReq req = new Nox2Packet.BluetoothPlayModeCtlReq(open);
                CallbackData data = null;
                int tryCount = 5;
                for (int i = 0; i < tryCount; i++) {
                    data = requestDevice(Nox2Packet.PacketMsgType.CMD_BLUETOOTH_PLAY_MODE_CTL, req);
                    if (data.isSuccess()) {
                        break;
                    } else {
                        if (i < tryCount - 1) {
                            SystemClock.sleep(100);
                        }
                    }
                }

                Log.d(TAG, TAG + " bluthoothPlayModeControl enter:" + open + ",data:" + data);
                data.setType(TYPE_METHOD_BLUETOOTH_PLAY_MODE_CTL);
                dataCallback(data);
            }
        });
    }

    @Override
    public void lightNightSet(final NoxLight light) {
        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                CallbackData data = lightNightSetSync(light);
                dataCallback(data);
            }
        });

    }

    @Override
    public CallbackData lightNightSetSync(final NoxLight light) {
        Nox2Packet.LightNightReq req = new Nox2Packet.LightNightReq(light);
        CallbackData data = requestDevice(Nox2Packet.PacketMsgType.SET_LIGHT_NIGHT, req);
        data.setType(TYPE_METHOD_LIGHT_NIGHT_SET);
        return data;
    }

    @Override
    public CallbackData sceneConfigSetSync(SceneConfig config) {
        if (config instanceof SleepSceneConfig) {
            mSleepSceneConfig = (SleepSceneConfig) config;
        }
        Log.d(TAG, "  设置场景信息：" + config);
        ArrayList<SceneConfig> configs = new ArrayList<>();
        if (config != null) {
            configs.add(config);
        }
        Nox2Packet.SceneConfigReq req = new Nox2Packet.SceneConfigReq(configs);
        CallbackData data = requestDevice(Nox2Packet.PacketMsgType.SCENE_CFG, req);
        data.setType(TYPE_METHOD_SCENE_CONFIG_SET);
//        Log.d(TAG,TAG+" sceneConfigSetSync cd:" + data);
        return data;
    }

    @Override
    public void gestureLightListSet(final List<NoxLight> lights) {
        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                CallbackData data = gestureLightListSetSync(lights);
                dataCallback(data);
            }
        });
    }

    @Override
    public void gestureAlbumListGet() {

    }


    public CallbackData gestureLightListSetSync(final List<NoxLight> lights) {
        Nox2Packet.GestureColorListReq req = new Nox2Packet.GestureColorListReq(lights);
        CallbackData data = requestDevice(Nox2Packet.PacketMsgType.SET_GESTURE_COLOR_LIST, req);
        data.setType(TYPE_METHOD_GESTURE_LIGHT_LIST_SET);
        if (data.isSuccess()) {
            Log.d(TAG, " success");
        } else {
            Log.d(TAG, "stop failed");
        }
        return data;
    }

    /**
     * 升级进度信息查询
     */
    @Override
    public CallbackData updateStatusGet() {
        //超时时间3秒就好
        return requestDevice(Nox2Packet.PacketMsgType.UPDATE_STATE_QUERY, 3000);
    }

    @Override
    public void lightSoundTutorialSet(String path) {

    }


    @Override
    public void aromatherapyStart(SleepAidCtrlMode mode, AromatherapySpeed speed) {
        dataCallback(CallbackData.noSupportData(sender, TYPE_METHOD_AROMATHERERAPY_START));
    }

    @Override
    public void aromatherapyStop(SleepAidCtrlMode mode) {
        dataCallback(CallbackData.noSupportData(sender, TYPE_METHOD_AROMATHERERAPY_STOP));
    }

    @Override
    public void buttonFuctionSet(boolean isLightStart, boolean isMusticStart, boolean isAromatherapyStart) {
        dataCallback(CallbackData.noSupportData(sender, TYPE_METHOD_BUTTON_FUNCTION_SET));
    }

    @Override
    public void onekeyOpen(SleepAidCtrlMode mode, AromatherapySpeed speed, NoxLight light) {
        dataCallback(CallbackData.noSupportData(sender, TYPE_METHOD_ONE_KEY_OPEN));
    }

    @Override
    public void onekeyClose(SleepAidCtrlMode mode) {
        dataCallback(CallbackData.noSupportData(sender, TYPE_METHOD_ONE_KEY_CLOSE));
    }

    @Override
    public void netSet(ServerNetType type, String ip, int port, String httpHost) {
        dataCallback(CallbackData.noSupportData(sender, TYPE_METHOD_NET_SET));
    }

    @Override
    public void netSet() {
        dataCallback(CallbackData.noSupportData(sender, TYPE_METHOD_NET_SET));
    }

    @Override
    public void sceneConfigSet(final SceneConfig config) {
        Log.d(TAG, " sceneConfigSet config:" + config);
        ArrayList<SceneConfig> configs = new ArrayList<>();
        if (config != null) {
            configs.add(config);
        }
        sceneConfigSet(configs);
    }


    @Override
    public void sceneConfigSet(final ArrayList<SceneConfig> configs) {
        Log.d(TAG, " sceneConfigSet configs:" + configs);
        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Nox2Packet.SceneConfigReq req = new Nox2Packet.SceneConfigReq(configs);
                CallbackData data = requestDevice(Nox2Packet.PacketMsgType.SCENE_CFG, req);
                data.setType(TYPE_METHOD_SCENE_CONFIG_SET);
                dataCallback(data);
                if (data.isSuccess()) {
                    Log.d(TAG, "set success");
                } else {
                    Log.d(TAG, "set failed");
                }
            }
        });
    }

    @Override
    public void environmentDataGet() {

    }
}
