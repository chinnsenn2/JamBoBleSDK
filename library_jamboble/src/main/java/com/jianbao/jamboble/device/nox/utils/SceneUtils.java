package com.jianbao.jamboble.device.nox.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jianbao.jamboble.device.nox.Device;
import com.jianbao.jamboble.device.nox.DeviceType;
import com.jianbao.jamboble.device.nox.bean.NoxLight;
import com.jianbao.jamboble.device.nox.bean.SceneBase;
import com.jianbao.jamboble.device.nox.bean.SceneConfig;
import com.jianbao.jamboble.device.nox.bean.SceneConfigBase;
import com.jianbao.jamboble.device.nox.bean.SceneConfigMobile;
import com.jianbao.jamboble.device.nox.bean.SceneConfigNox;
import com.jianbao.jamboble.device.nox.bean.SceneDevice;
import com.jianbao.jamboble.device.nox.bean.SceneSleep;
import com.jianbao.jamboble.device.nox.bean.SleepSceneConfig;
import com.jianbao.jamboble.device.nox.manager.CentralManager;
import com.jianbao.jamboble.device.nox.manager.DeviceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by Administrator on 2016/7/22.
 */

public class SceneUtils {
    private static final String TAG = SceneUtils.class.getSimpleName();

    //场景里面添加设备类型，有监测设备，助眠设备和闹钟设备
    //监测设备
    public static final int SCENE_SELECT_DEVICE_TYPE_MONITOR = 1;
    //助眠设备
    public static final int SCENE_SELECT_DEVICE_TYPE_SLEEP_HELPER = 2;
    //闹钟设备
    public static final int SCENE_SELECT_DEVICE_TYPE_ALARM = 3;

    public static final int SCENE_SELECT_DEVICE_TYPE_OTHER = 4;

    /**
     * <p>
     * 更细实时数据
     * </p>
     */
    public static final byte UpDateRealTime = 0x03;

    /*
    * 发现里面闹钟音乐的收藏的场景ID
    * */
    public static final int DISCOVER_ALARM_MUSIC_SCENE_ID = 98;

    /**
     * 发现里面的场景ID,用来区分场景收藏和发现里面的收藏
     */
    public static final int DISCOVERY_SCENE_ID = 99;
    /**
     * 睡眠场景id
     */
    public static final int SLEEP_SCENE_ID = 100;

    /**
     * 照明场景id
     */
    public static final int LIGHTING_SCENE_ID = 101;

    /**
     * 阅读场景id
     */
    public static final int READING_SCENE_ID = 102;

    /**
     * 氛围场景id
     */
    public static final int ATOMSPHERE_SCENE_ID = 103;

    //重新设置排序  主要用于设备管理页面排序问题
    public static final int ORDER_NULL = 0;
    public static final int ORDER_PHONE = 1;
    public static final int ORDER_SA = 2;      //香薰灯
    public static final int ORDER_NOX1 = 3;
    public static final int ORDER_NOX2 = 4;
    public static final int ORDER_NOX2W = 5;
    public static final int ORDER_PATCH = 6;
    public static final int ORDER_RESTON = 7;
    public static final int ORDER_RESTON_Z400 = 7;
    public static final int ORDER_SLEEPDOT = 8;
    public static final int ORDER_SLEEPDOT2 = 8;
    public static final int ORDER_PILLOW = 9;

    //生成报告成功的时间，用于跳转到报告页面
    public static final String KEY_SP_REPROT_START_TIME = "report_start_time";
    //不再提醒的key
    public static final String KEY_SP_NOT_SHOW_AGAIN = "not_show_again";
    //reston/手机监测时间少于十分钟
    public static final int REPORT_FAIL_MONITOR_TIME_LESS_LIMIT = 0;
    //生成报告成功
    public static final int REPORT_SUCCESS = 3;
    //RestOn/纽扣硬件端启动睡觉场景（此时闹钟无效）需要在场景主页显示tips位置显示：未开启智能闹钟功能（样式与tips一致，红色
    public static final int TIPS_REASON_AUTO_START_SCENE = 0x4;

    //是否是第一次点击睡眠场景的key
    public static String KEY_SP_IS_FIRST_CLICK_SLEEP_SCENE = "isFirstClickSleepScene";
    //是否是第一次进入场景主页
    public static String KEY_SP_IS_FIRST_IN_SCENE = "is_first_time_in";
    //是否添加了新设备，添加了新设备进入场景主页会显示引导页
    public static String KEY_SP_ADD_NEW_DEVICE = "add_new_device";
    //nox2详情引导
    public static String KEY_SP_NOX2_GUIDE = "nox2_guide_show";
    //睡觉场景点击秒懂后是否进行进入设备选择页面的key
    public static String KEY_SP_IS_GO_TO_SELECT_DEVICE = "isGotoSelectDevice";
    //场景选择设备的时候是否需要显示下一步按钮key
    public static String KEY_SP_SHOW_NEXT = "showNex";
    //nox2闹钟时间戳key，通过update获取的有时候会相差一秒
    public static String KEY_SP_NOX2_ALARM_TIME = "nox2_alarm_time";
    //场景信息本地key
    public static String KEY_SP_SCENE_INFOS = "sceneInfos";
    //是否第一次进入开始睡觉页面
    public static String KEY_SP_IS_FIRST_IN_START_SLEEP = "is_first_in_sleep";
    //是否是第一次进入日报告夜睡眠页面
    public static String KEY_SP_IS_FIRST_IN_DAY_REPORT = "is_first_in_day_report";
    //是否第一次进入日报告夜睡眠更多报告页面
    public static String KEY_SP_IS_FIRST_IN_DAY_MORE_REPRORT = "is_first_in_day_more_report";
    //是否第一次进入日报告波段图页面
    public static String KEY_SP_IS_FIRST_IN_DAY_REPORT_WAVE = "is_first_in_day_report_wave";
    //是否第一次使用监测设备reston 查看实时数据波形
    public static String KEY_SP_IS_FIRSR_IN_RESTON_WAVE = "is_first_in_reston_wave";
    //是否第一次进入分数明细睡眠感受
    public static String KEY_SP_IS_FIRST_IN_SCORE_SLEEP_FEELING = "is_first_in_score_feelings";
    //是否第一次进入分数明细扣分因素
    public static String KEY_SP_IS_FIRST_IN_SUBTRACT_SCORE = "is_first_in_subtract_score";
    //是否第一次显示睡眠统计
    public static String KEY_SP_IS_FIRST_IN_SLEEP_STATISTICS = "is_first_in_sleep_statistics";

    public static String KEY_SP_IS_FIRST_IN_WEEK_REPORT = "is_first_in_week_report";
    public static String KEY_SP_IS_FIRST_IN_DAY_REPORT_TITILE = "is_first_in_day_report_title";

    //排序
    public static ArrayList<Device> sort(ArrayList<Device> devices, Device selectedDevice) {
        Device device;
        Collections.sort(devices, new Comparator<Device>() {
            @Override
            public int compare(Device device, Device t1) {
                Device device1 = device;
                Device device2 = t1;
                return device1.order - device2.order;
            }
        });
        return devices;
    }

    //排序
    public static ArrayList<Device> sort(ArrayList<Device> devices) {
        Device device;
        Collections.sort(devices, new Comparator<Device>() {
            @Override
            public int compare(Device device, Device t1) {
                Device device1 = device;
                Device device2 = t1;
                return device1.order - device2.order;
            }
        });
        return devices;
    }

    //逆·排序
    public static ArrayList<Device> sort2(ArrayList<Device> devices) {
        Device device;
        Collections.sort(devices, new Comparator<Device>() {
            @Override
            public int compare(Device device, Device t1) {
                Device device1 = device;
                Device device2 = t1;
                return device2.order - device1.order;
            }
        });
        return devices;
    }


    /**
     * 获取中控设备
     *
     * @param sceneId
     * @param context
     * @return
     */
    public static CentralManager getCenteralManager(Context context, int sceneId) {
        SceneBase mSleepScene = SceneUtils.getScene(sceneId);
        Device mMonitorDevice = SceneUtils.getDevice(mSleepScene.getMonitorDeviceId());
        Device mSleepHelperDevice = SceneUtils.getDevice(mSleepScene.getSleepAidDeviceId());
        //3.2版本，睡觉场景里面只有选择监测设备和助眠设备，现在把获取CentralManager的时候的闹钟设备也用助眠设置替换，这样还可以保持以前的逻辑，智能闹钟等不需要修改，这种方式改动最小
        //Device mAlarmDevice = SceneUtils.getDevice(mSleepScene.getClockDeviceId());
        return DeviceManager.getCentralManager(context, mSleepHelperDevice, mMonitorDevice, mSleepHelperDevice);
    }



    //通过场景id获取相应的场景信息
    public static SceneBase getScene(int sceneId) {
        for (SceneBase baseSceneBean : NoxGlobalInfo.scenes) {
            if (baseSceneBean.getSceneId() == sceneId) {
                return baseSceneBean;
            }
        }
        if (sceneId == SLEEP_SCENE_ID) {//返回一个默认的睡眠场景信息,手机+手机+手机
            return new SceneSleep();
        } else {
            return new SceneBase();
        }
    }


    //通过场景id获取助眠设置信息
    public static SceneConfigBase getSceneSleepHelperConfig(int sceneId, short sleepHelperDeviceType) {
//        Log.d(TAG," getSceneSleepHelperConfig sleepHelperDeviceType:"+sleepHelperDeviceType+",size:" + User.sleepHelperConfigs.size()+",configs:" + User.sleepHelperConfigs);
        for (SceneConfigBase baseSceneSleepHelperConfig : NoxGlobalInfo.sleepHelperConfigs) {
            if (baseSceneSleepHelperConfig != null && sleepHelperDeviceType == baseSceneSleepHelperConfig.getDeviceType() && baseSceneSleepHelperConfig.getSceneId() == sceneId) {
                return baseSceneSleepHelperConfig;
            }
        }

        //从本地化获取
        SceneConfigBase configBase = getSleepSceneConfigFromLocal(sleepHelperDeviceType);
        if(configBase != null){
            return configBase;
        }

        Log.d(TAG," getSceneSleepHelperConfig init");

        if (sleepHelperDeviceType == DeviceType.DEVICE_TYPE_PHONE) {
            SceneConfigMobile sceneConfigMobile = new SceneConfigMobile();
            sceneConfigMobile.init();
            return sceneConfigMobile;
        }

        if (DeviceType.isNox(sleepHelperDeviceType)) {
            SceneConfigNox sceneConfigNox = new SceneConfigNox();
//            sceneConfigNox.init(Nox2BManager.getInstance(App.context).getDevice());
            return sceneConfigNox;
        }

        if (sleepHelperDeviceType == DeviceType.DEVICE_TYPE_NULLL) {
            return null;
        }

        return new SceneConfigBase();
    }

    public static Calendar getCalendar(int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        return cal;
    }

    //通过场景id，获取监测设备的类型
    public static short getMonitorDeviceType(int sceneId) {
//        SceneBase sceneBase = getScene(sceneId);
//        if (sceneBase != null) {
//            return sceneBase.getMonitorDeviceType();
//        }
        return DeviceType.DEVICE_TYPE_PHONE;
    }

    //通过场景id，获取监测设备的id
    public static String getMonitorDeviceId(int sceneId) {
        SceneBase sceneBase = getScene(sceneId);
        if (sceneBase != null) {
            return sceneBase.getMonitorDeviceId();
        }
        return "";
    }

    //通过场景id,获取助眠设备的id
    public static String getSleepHelpDeviceId(int sceneId) {
        SceneBase sceneBase = getScene(sceneId);
        if (sceneBase != null) {
            return sceneBase.getSleepAidDeviceId();
        }
        return "";
    }

    //通过场景id,获取助眠设备的类型
    public static short getSleepHelpDeviceType(int sceneId) {
//        SceneBase sceneBase = getScene(sceneId);
//        if (sceneBase != null) {
//            if (GlobalInfo.user.hasDevice(sceneBase.getSleepAidDeviceType())) {//防止强绑
//                return sceneBase.getSleepAidDeviceType();
//            }
//        }
        return DeviceType.DEVICE_TYPE_NOX_2B;
    }

    //通过场景id,获取闹钟设备的类型,3.2开始，闹钟设备是独立的，这个时候返回助眠设备的类型
    public static int getAlarmDeviceType(int sceneId) {
        SceneSleep sceneSleep = (SceneSleep) getScene(SLEEP_SCENE_ID);
        //Device alarmDevice = getDevice(sceneSleep.getClockDeviceId());
        Device sleepHelperDevice = getDevice(sceneSleep.getSleepAidDeviceId());
        int alarmDeviceType = DeviceType.DEVICE_TYPE_NULLL;
        if (sleepHelperDevice != null) {
            alarmDeviceType = sleepHelperDevice.deviceType;
        }
        return alarmDeviceType;
    }

    //同种设备只能有一个
    public static Device getDevice(int deviceType) {
//        for (Device device : user.getDevices()) {
//            if (device.deviceType == deviceType) {
//                return device;
//            }
//        }
//        return user.phoneDevice;
        return new Device();
    }

    //通过deviceId获取Device
    public static Device getDevice(String deviceId) {
//        for (Device device : user.getDevices()) {
//            //临时方案，纽扣大多数情况下上传到服务端的deviceId是明文的，
//            if (device.deviceId.equals(deviceId) || device.deviceName.equals(deviceId)) {
//                return device;
//            }
//        }
//        //手机设备和无设备没有在user.devices中，再继续判断，默认返回一个设备
//        if (TextUtils.isEmpty(deviceId) || deviceId.equals("NULL") || deviceId.equals(String.valueOf(user.getUserId()))) {
//            if (user.phoneDevice == null) {
//                initPhoneDevice();
//            }
//            user.phoneDevice.deviceId = String.valueOf(user.getUserId());
//            return user.phoneDevice;
//        } else {
//            if (user.nullDevice == null) {
//                SceneUtils.initNullDevice();
//            }
//            return user.nullDevice;
//        }
        return new Device();
    }

    public static ArrayList<Device> sortDevices(ArrayList<Device> devices) {
        if (devices == null) return new ArrayList<Device>();
        int[] arr = new int[devices.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = devices.get(i).order;
        }
        Arrays.sort(arr);
        ArrayList<Device> results = new ArrayList<Device>(arr.length);
        for (int i = 0; i < arr.length; i++) {
            for (Device device : devices) {
                if (device.order == arr[i]) {
                    results.add(i, device);
                }
            }
        }
        return results;
    }

    public static ArrayList<Device> getAllMonitorDevices() {
        ArrayList<Device> results = new ArrayList<Device>();
//        for (Device device : user.getDevices()) {
//            if (device != null && device.isMonitor == true) {
//                results.add(device);
//            }
//        }
        addPhoneDevice(results);
        addNullDevice(results);
        results = sortDevices(results);
        return results;
    }

    public static ArrayList<Device> getAllSleepHelperDevices() {
        ArrayList<Device> results = new ArrayList<Device>();
//        for (Device device : user.getDevices()) {
//            if (device != null && device.isHelper == true) {
//                results.add(device);
//            }
//        }
        addPhoneDevice(results);
        addNullDevice(results);
        return results;
    }

    //添加手机设备
    public static ArrayList<Device> addPhoneDevice(ArrayList<Device> devices) {
//        if (user.phoneDevice == null) {
//            initPhoneDevice();
//        }
//        if (!devices.contains(user.phoneDevice)) {
//            devices.add(user.phoneDevice);
//        }
        return devices;
    }

    //添加无设备
    public static ArrayList<Device> addNullDevice(ArrayList<Device> devices) {
//        if (user.nullDevice == null) {
//            initNullDevice();
//        }
//        //添加无设备
//        if (!devices.contains(user.nullDevice)) {
//            devices.add(user.nullDevice);
//        }
        return devices;
    }


    public static ArrayList<Device> getAllAlarmDevices() {
        ArrayList<Device> results = new ArrayList<Device>();
//        for (Device device : user.getDevices()) {
//            if (device != null && device.isWakupor == true) {
//                results.add(device);
//            }
//        }
        addPhoneDevice(results);
        addNullDevice(results);
        results = sortDevices(results);
        return results;
    }


    public static synchronized boolean parseSceneInfos(String sceneInfos) {
        return parseSceneInfos(sceneInfos, false);
    }

    public static synchronized boolean parseSceneInfos(String sceneInfos, boolean isOffline) {
//        if (!TextUtils.isEmpty(sceneInfos)) {
//            SleepaceApplication mApp = SleepaceApplication.getInstance();
//            mApp.mSp.edit().putString("scene_" + user.getUserId(), sceneInfos).commit();
//            try {
//                JSONObject json = new JSONObject(sceneInfos);
//                if (json.optInt("status") == SleepConfig.STATUS_SUCCESS) {
//                    GlobalInfo.scenes.clear();
//                    GlobalInfo.sleepHelperConfigs.clear();
//                    JSONArray scenes = json.optJSONArray("data");
//                    int count = scenes == null ? 0 : scenes.length();
//                    for (int i = 0; i < count; i++) {
//                        JSONObject scene = scenes.optJSONObject(i);
//                        if (scene == null) {
//                            continue;
//                        }
//                        parseOneScene(scene, isOffline);
//                    }
//
//                    SceneUtils.sceneInitLocal(GlobalInfo.scenes);
//                    SceneUtils.sceneConfigInitLocal(GlobalInfo.sleepHelperConfigs);
//                    return true;
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        return false;
    }

    private static void parseOneScene(JSONObject scene, boolean isOffline) {
        SceneBase baseScene = new SceneBase();
        try {
            JSONObject sceneBase = scene.getJSONObject("sceneBase");
            JSONObject deviceConfigs = scene.getJSONObject("deviceConfigs");
            int sceneId = sceneBase.optInt("sceneId");
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            //根据场景id不一样，用不同的子类bean去解析
            switch (sceneId) {
                //睡觉场景
                case SLEEP_SCENE_ID:
                    baseScene = gson.fromJson(sceneBase.toString(), SceneSleep.class);
                    break;
                //照明场景
//                case LIGHTING_SCENE_ID:
//                    baseScene = gson.fromJson(sceneBase.toString(), SceneLighting.class);
//                    break;
//                //阅读场景
//                case READING_SCENE_ID:
//                    baseScene = gson.fromJson(sceneBase.toString(), SceneReading.class);
//                    break;
//                //氛围场景
//                case ATOMSPHERE_SCENE_ID:
//                    baseScene = gson.fromJson(sceneBase.toString(), SceneAtmosphere.class);
//                    break;
            }
            NoxGlobalInfo.scenes.add(baseScene);
            //Log.showMsg(TAG + " parseOneScene:" + baseScene);
            //解析场景里面的助眠配置和监测配置，闹钟另外提供接口key由deviceRole-deviceId组成,根据api文档
            //根据devicee用不同的助眠配置子类和监测配置子类去解析

            for (SceneDevice sceneDevice : baseScene.getDevices()) {
                //只解析助眠和监测配置
                //助眠只有nox和手机,
                int deviceType = sceneDevice.getDeviceType();
                String deviceRole = sceneDevice.getDeviceRole();
                String key = deviceRole + "-" + sceneDevice.getDeviceId();
                //助眠
                if (deviceRole.equals(SceneDevice.DEVICE_ROLE_SLEEPAID)) {if (deviceType == DeviceType.DEVICE_TYPE_PHONE) {
                        SceneConfigMobile sceneConfigMobile = gson.fromJson(deviceConfigs.getString(key), SceneConfigMobile.class);
                        Log.d(TAG, " parse scene sceneConfigMobile:" + sceneConfigMobile);
                        if (sceneConfigMobile != null) {
                            if (sceneConfigMobile.getAidingTime() == 90 || sceneConfigMobile.getAidingTime() == 120) {//从2.x升级到3.x，90和120助眠时长改为45
                                sceneConfigMobile.setAidingTime(45);
                            }
                            sceneConfigMobile.setMusicFlag(1);
                            NoxGlobalInfo.sleepHelperConfigs.add(sceneConfigMobile);
                            if (getSleepHelpDeviceType(SLEEP_SCENE_ID) == DeviceType.DEVICE_TYPE_PHONE) {
//                                SPUtils.save(SPUtils.SP_KEY_SLEEPHELPER_VOLUME, sceneConfigMobile.getVolume());
//                                SPUtils.save(SP_KEY_FIRST_USER_PHONE_AS_SLEEPAID, false);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取睡觉场景配置信息,用户启动场景前的配置参数
     */
    public static SleepSceneConfig getSleepSceneConfig() {
        return getSleepSceneConfig(getSleepHelpDeviceType(SLEEP_SCENE_ID));
    }

    /**
     * 获取睡觉场景配置信息,用户启动场景前的配置参数
     */
    public static SleepSceneConfig getSleepSceneConfig(short sleepHelperDeviceType) {
        short monitrorDeviceType = getMonitorDeviceType(SLEEP_SCENE_ID);
        return getSleepSceneConfig(sleepHelperDeviceType, monitrorDeviceType);
    }

    public static SleepSceneConfig getSleepSceneConfig(short sleepHelperDeviceType, short monitorDeviceType){
        SleepSceneConfig sleepSceneConfig = new SleepSceneConfig();
        sleepSceneConfig.seqId = SLEEP_SCENE_ID;
        sleepSceneConfig.sceneId = SLEEP_SCENE_ID;
        sleepSceneConfig.sceneType = SceneConfig.SceneType.SLEEP;

        SceneConfigBase sceneConfigBase = getSceneSleepHelperConfig(SLEEP_SCENE_ID, sleepHelperDeviceType);

        Log.d(TAG," getSleepSceneConfig sleepHelperDeviceType:" + sleepHelperDeviceType+",monitorDeviceType:" + monitorDeviceType+",sceneConfigBase:" + sceneConfigBase);

        Device monitorDevice = getDevice(monitorDeviceType);

        if (sleepHelperDeviceType == DeviceType.DEVICE_TYPE_NULLL) {
            sleepSceneConfig.sleepAidFlag = 0;
        }

        sleepSceneConfig.monitorDeviceType = monitorDeviceType;
        if (monitorDeviceType != DeviceType.DEVICE_TYPE_NULLL) {
            sleepSceneConfig.monitorFlag = 1;
        } else {
            sleepSceneConfig.monitorFlag = 0;
        }

        if (sleepHelperDeviceType == DeviceType.DEVICE_TYPE_NOX_PRO) {
            if (monitorDevice != null) {
                sleepSceneConfig.monitorDeviceName = monitorDevice.deviceName;
            } else {
                sleepSceneConfig.monitorDeviceName = "0000000000000";
            }
        }

        if (monitorDevice != null) {
            sleepSceneConfig.monitorDeviceId = monitorDevice.deviceId;
        } else {
            sleepSceneConfig.monitorDeviceId = getMonitorDeviceId(SLEEP_SCENE_ID);
        }

        if (monitorDeviceType == DeviceType.DEVICE_TYPE_PHONE) {
            sleepSceneConfig.monitorDeviceId = "0000000000000";
        }

        if (DeviceType.isNox(sleepHelperDeviceType)) {
            sleepSceneConfig.smartAlarmFlag = 1;
        }

        return sleepSceneConfig;
    }


    /**
     * 判断这个场景是否有nox1设备
     *
     * @return
     */
    public static boolean hasNox1() {
        return hasDevice(SLEEP_SCENE_ID, DeviceType.DEVICE_TYPE_NOX_PRO);
    }

    /**
     * 判断这个场景是否有nox2设备
     *
     * @return
     */
    public static boolean hasNox2B() {
        return hasDevice(SLEEP_SCENE_ID, DeviceType.DEVICE_TYPE_NOX_2B);
    }

    /**
     * 判断这个场景是否有nox2 wifi版设备
     */
    public static boolean hasNox2W() {
        return hasDevice(SLEEP_SCENE_ID, DeviceType.DEVICE_TYPE_NOX_2W);
    }

    /**
     * 判断这个场景是否有 NoxSAB
     * @return
     */
    public static boolean hasNoxSab() {
        return hasDevice(SLEEP_SCENE_ID, DeviceType.DEVICE_TYPE_NOX_SAB);
    }

    /**
     * 判断这个场景是否有 NoxSAW
     * @return
     */
    public static boolean hasNoxSaw() {
        return hasDevice(SLEEP_SCENE_ID, DeviceType.DEVICE_TYPE_NOX_SAW);
    }

    public static boolean hasNox() {
        return hasNox1() || hasNox2B() || hasNox2W() || hasNoxSab() || hasNoxSaw();
    }

    /**
     * 判断这个场景是否有reston设备
     *
     * @return
     */
    public static boolean hasRestOn() {
        return hasZ1() || hasZ2() || hasZ4();
    }

    public static boolean hasZ1() {
        return hasDevice(SLEEP_SCENE_ID, DeviceType.DEVICE_TYPE_RESTON_Z1);
    }

    public static boolean hasZ2() {
        return hasDevice(SLEEP_SCENE_ID, DeviceType.DEVICE_TYPE_RESTON_Z2);
    }

    public static boolean hasZ4() {
        return hasDevice(SLEEP_SCENE_ID, DeviceType.DEVICE_TYPE_RESTON_Z4);
    }

    /**
     * 判断睡觉场景中是否有监测设备（可监测心率呼吸率）
     *
     * @return
     */
    public static boolean hasHeartBreathDevice() {
        return hasRestOn() || hasPillow();
    }

    /**
     * 判断睡觉场景中是否有纽扣（纽扣，纽扣2）
     *
     * @return
     */
    public static boolean hasSleepDot() {
        return hasDevice(SLEEP_SCENE_ID, DeviceType.DEVICE_TYPE_SLEEPDOT)
                || hasDevice(SLEEP_SCENE_ID, DeviceType.DEVICE_TYPE_SLEEPDOT_502)
                || hasSleepDotB502T();
    }

    public static boolean hasSleepDotB502T() {
        return hasDevice(SLEEP_SCENE_ID, DeviceType.DEVICE_TYPE_SLEEPDOT_502T);
    }

    /**
     * 判断睡觉场景中是否有Pillow
     *
     * @return
     */
    public static boolean hasPillow() {
        return hasDevice(SLEEP_SCENE_ID, DeviceType.DEVICE_TYPE_PILLOW);
    }


    public static boolean hasDevice(int sceneId, short deviceType) {
//        SceneBase sceneBase = getScene(sceneId);
//        if (sceneBase != null) {
//            if (sceneBase.hasDevice(deviceType) && GlobalInfo.user.hasDevice(deviceType)) {
//                return true;
//            }
//        }
        return true;
    }


    /**
     * 判断场景的监测设备是否是无
     */
    public static boolean isMonitorDeviceNull() {
        return isSceneMonitorDevice(SLEEP_SCENE_ID, DeviceType.DEVICE_TYPE_NULLL);
    }

    /**
     * 判断场景的监测设备是否是手机
     */
    public static boolean isMonitorDevicePhone() {
        return isSceneMonitorDevice(SLEEP_SCENE_ID, DeviceType.DEVICE_TYPE_PHONE);
    }


    public static boolean isSceneMonitorDevice(int sceneId, short deviceType) {
        SceneBase sceneBase = getScene(sceneId);
        if (sceneBase != null) {
            return sceneBase.getMonitorDeviceType() == deviceType;
        }
        return false;
    }


    /**
     * 初始化闹钟
     *
     * @param
     */
    public static boolean sceneConfigInitLocal(List<SceneConfigBase> sleepHelperConfigs) {
        if (sleepHelperConfigs == null) return false;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        for (SceneConfigBase sceneConfigBase : sleepHelperConfigs) {
            if (sceneConfigBase != null) {
//                SPUtils.saveWithUserId(SP_KEY_SLEEP_CONFIGS + sceneConfigBase.getDeviceType(), gson.toJson(sceneConfigBase));
            }
        }
        return true;
    }

    public static SceneConfigBase getSleepSceneConfigFromLocal(short sleepHelperDeviceType) {
//        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
//        String config = SPUtils.getWithUserId(SP_KEY_SLEEP_CONFIGS + sleepHelperDeviceType, "");
        //场景信息参数获取异常，添加log跟踪
//        Log.logTemp(TAG+"未获取到云端的场景信息，从手机本地获取场景："+config);
//        if (!TextUtils.isEmpty(config)) {
//            if(DeviceType.isNox(sleepHelperDeviceType)){
//                return gson.fromJson(config, SceneConfigNox.class);
//            }else if(DeviceType.isPhone(sleepHelperDeviceType)){
//                return gson.fromJson(config, SceneConfigMobile.class);
//            }
//        }
        return null;
    }


    public static boolean sceneInitLocal(List<SceneBase> sceneBases) {
//        if (sceneBases == null || sceneBases.size() == 0) return false;
//        SPUtils.removeWithUserId(SP_KEY_SCENES + SLEEP_SCENE_ID);
//        SPUtils.removeWithUserId(SP_KEY_SCENES + LIGHTING_SCENE_ID);
//        SPUtils.removeWithUserId(SP_KEY_SCENES + READING_SCENE_ID);
//        SPUtils.removeWithUserId(SP_KEY_SCENES + ATOMSPHERE_SCENE_ID);
//
//        Gson gson = new Gson();
//        List<SceneBase> myList = new CopyOnWriteArrayList<SceneBase>();
//        myList.addAll(sceneBases);
//        for (SceneBase sceneBase : myList) {
//            SPUtils.saveWithUserId(SP_KEY_SCENES + sceneBase.getSceneId(), gson.toJson(sceneBase));
//        }
        return true;
    }

    public static String toJson(Object object) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return gson.toJson(object);
    }

    public static String[] HourIs12(int hours, int minute) {

        StringBuffer sb = new StringBuffer();
        String[] strs = new String[2];

        if (hours < 12 || hours == 24)
            strs[1] = "AM";
        else
            strs[1] = "PM";

        if (hours == 12 || hours == 0 || hours == 24) {
            hours = 12;
        } else {
            hours = hours % 12;
        }

        if (hours < 10)
            sb.append("0");
        sb.append(hours);
        sb.append(":");

        if (minute < 10)
            sb.append("0");
        sb.append(minute);

        strs[0] = sb.toString();

        return strs;
    }


    /**
     * 白灯，w=0
     */
    public static final int LIGHT_TYPE_WHITE = 0;
    /**
     * 彩灯，需要设置w
     */
    public static final int LIGHT_TYPE_COLOR = 0x01;

    /**
     * 睡眠辅助
     */
    public static final int LIGHT_TYPE_SLEEP = 0x03;


    /**
     * 反算角度
     *
     * @param lightConfig
     * @param lightType
     * @return
     */
    public static float getAngle(NoxLight lightConfig, int lightType) {
        //取出r g b
        int r = lightConfig.r & 0xff;
        int g = lightConfig.g & 0xff;
        int b = lightConfig.b & 0xff;
        int w = lightConfig.w & 0xff;
        float angle = 0;

        if (lightType == LIGHT_TYPE_SLEEP) {
            //橘黄色的助眠设置中的光
            if (g > 0 && g < 120) {       //    (120-color_g)*180/120
                angle = (120 - g) * 180f / 120;
            }
        } else if (lightType == LIGHT_TYPE_COLOR) {
            //彩光
            int per = (int) (255 / 60f);
            if (r == 255 && b == 0) {   //60°的
                angle = g / (per + 0.5f);
            } else if (g == 255 && b == 0) { //60--120
                angle = (255f - r) / per + 60;
            } else if (r == 0 && b == 255) { // 180-240
                angle = (255f - g) / per + 180;
            } else if (r == 0 && g == 255) { //120-180
                angle = b / per + 60;
            } else if (g == 0 && b == 255) { // 240-300
                angle = r / per + 240;
            } else if (r == 255 && g == 0) { //300-360
                angle = (255f - b) / per + 300;
            }
            angle = angle % 360;
        } else if (lightType == LIGHT_TYPE_WHITE) {
            //白光
            //白光
            if (g > 0 && g < 120) {       //    (120-color_g)*180/120
                angle = (120 - g) * 180f / 120;
            }
//            float per = 256 / 180f; //0--255,256份，分布到180°里面
//            angle = w / per;  //计算角度
//
//            if (w == 255) {
//                angle = 0;
//            } else if (w >=0 && w < 255){
//
//                angle = (257-w)*(180/256f);
////                angle = w * (180 / 256f) + 180;
//
//            }
        }

        return angle;
    }

    public static boolean isSleepHelperDevice(short deviceType) {
        boolean result = false;
        if (deviceType == DeviceType.DEVICE_TYPE_PHONE || DeviceType.isNox(deviceType)) {
            result = true;
        }
        return result;
    }

    public static void updateSceneStatus() {

        Log.d(TAG, " updateSceneStatus:" + NoxGlobalInfo.getSceneStatus()/*+",caller:"+Log.getCaller()*/);

        if (NoxGlobalInfo.getSceneStatus()) {
            NoxGlobalInfo.setPhoneAlarmValid(true);
            NoxGlobalInfo.mStart = true;
        } else {
            NoxGlobalInfo.setPhoneAlarmValid(false);
            NoxGlobalInfo.mStart = false;
            NoxGlobalInfo.mStop = true;
        }
    }

}
