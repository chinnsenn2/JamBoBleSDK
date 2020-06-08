package com.jianbao.jamboble.device.nox.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Hao on 2016/7/11.
 * SharePrefence的工具类
 */

public class SPUtils {

    public static final String TAG = SPUtils.class.getSimpleName();

    private final static String PREFENRENCE_FILE = "appsetting";
    public static final String DESCRIPTOR = "com.umeng.share";
    public static final String SP_KEY_ACCOUNT = "account";
    public static final String SP_KEY_PSW = "password";
    public static final String SP_KEY_ALARMS = "sp_alarms";
    public static final String SP_KEY_SLEEP_CONFIGS = "sp_sleep_configs";
    public static final String SP_KEY_RESON = "sp_reston";
    public static final String SP_KEY_MILKY = "sp_milk";
    public static final String SP_KEY_SCENES = "sp_scenes";
    public static final String SP_KEY_GESTURE_INFO = "sp_key_gesture_info";
    public static final String SP_KEY_SLEEPHELPER_VOLUME = "sp_key_sleephelper_volume";
    public static final String SP_KEY_LOCAL_SLEEPAID_MUSIC_LIST = "sp_key_local_music_list";
    public static final String SP_KEY_SLEEPAID_MUSIC_IS_LOOP = "sp_key_sleepaid_music_is_loop";
    public static final String SP_KEY_SLEEPAID_MUSIC_PLAYING_POSIITION = "sp_key_sleepaid_music_playing_posiition";
    public static final String SP_KEY_SLEEPAID_MUSIC_LAST_ALBUMID = "sp_key_sleepaid_music_last_albumid";
    public static final String SP_KEY_IS_FIRST_CLICK_START = "is_first_time";
    public static final String SP_KEY_SYSTEM_VOLME = "sp_key_system_volume";

    /**
     * 用户助眠时灯光标记
     */
    public static final String KEY_LIGHT_FLAG = "key_light_flag";
    /**
     * 控制中心 香薰灯 成功保存中心键 value的数值
     * 本地保存
     */
    public static final String KEY_CENTER_KEY_VALUE = "key_center_key_value";
    public static final String KEY_CENTER_KEY_DEVICEID = "key_center_key_deviceid";
    public static final String KEY_CENTER_KEY_DEVICE_TYPE = "key_center_key_device_type";
    public static final String KEY_CENTER_KEY_SEQID = "key_center_key_seqid";
    public static final String KEY_CENTER_KEY_SETTING_ITEM = "key_center_key_setting_item";
    public static final String KEY_CENTER_KEY_USER_ID = "key_center_key_user_id";
    
    private static SharedPreferences getSharedPreferences(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREFENRENCE_FILE, Context.MODE_PRIVATE);
        return preferences;
    }
    
    /**
     * @param deviceType
     * @param key
     * @param value
     */
    public static void saveWithUserIdAndDeviceType(int deviceType, String key, Object value) {
        String saveKey = deviceType + "_" + key;
        saveWithUserId(saveKey, value);
    }

    public static <T> T getWithUserIdAndDeviceType(int deviceType, String key, T defaultValue) {
        String getKey = deviceType + "_" + key;
        return getWithUserId(getKey, defaultValue);
    }

    /**
     * 保存消息
     *
     * @param key   在Key的前面会自动加上userid
     * @param value 会根据value的类型选择保存
     */
    public static void saveWithUserId(String key, Object value) {

//        SharedPreferences sp = getSharedPreferences(App.context);
//        SharedPreferences.Editor edit = sp.edit();
//        if (value instanceof String) {
//            edit.putString(key, value.toString());
//        } else if (value instanceof Integer) {
//            edit.putInt(key, ((Integer) value).intValue());
//        } else if (value instanceof Float) {
//            edit.putFloat(key, ((Float) value).floatValue());
//        } else if (value instanceof Boolean) {
//            edit.putBoolean(key, ((Boolean) value).booleanValue());
//        } else if (value instanceof Long) {
//            edit.putLong(key, ((Long) value));
//        } else {
//            throw new RuntimeException("还没有添加这个类型数据的保存，请在代码里面添加");
//        }
//        edit.commit();
        //LogUtil.log(TAG+" saveWithUserId key:" + saveKey+",val:" + value);
    }

    /**
     * 保存消息
     *
     * @param key   Key
     * @param value 会根据value的类型选择保存
     */
    public static void save(String key, Object value) {
//        SharedPreferences sp = getSharedPreferences(App.context);
//        SharedPreferences.Editor edit = sp.edit();
//        String saveKey = key;
//        if (value instanceof String) {
//            edit.putString(saveKey, value.toString());
//        } else if (value instanceof Integer || value instanceof Byte) {
//            edit.putInt(saveKey, ((Integer) value).intValue());
//        } else if (value instanceof Float) {
//            edit.putFloat(saveKey, ((Float) value).floatValue());
//        } else if (value instanceof Boolean) {
//            edit.putBoolean(saveKey, ((Boolean) value).booleanValue());
//        } else if (value instanceof Long) {
//            edit.putLong(saveKey, ((Long) value));
//        } else {
//            throw new RuntimeException("还没有添加这个类型数据的保存，请在代码里面添加");
//        }
//        edit.commit();
    }

    /**
     * 获取保存的信息
     *
     * @param key     在key前面会加上userid
     * @param defalut 根据默认值传递进来的类型决定返回的类型
     * @param <T>
     * @return
     */
    public static <T> T getWithUserId(String key, T defalut) {
//        SharedPreferences sp = getSharedPreferences(App.context);
        T val = null;
//        if (defalut instanceof String) {
//            val = (T) sp.getString(key, defalut.toString());
//        } else if (defalut instanceof Integer) {
//            val = (T) ((Integer) sp.getInt(key, ((Integer) defalut).intValue()));
//        } else if (defalut instanceof Float) {
//            val = (T) ((Float) sp.getFloat(key, ((Float) defalut).floatValue()));
//        } else if (defalut instanceof Boolean) {
//            val = (T) ((Boolean) sp.getBoolean(key, ((Boolean) defalut).booleanValue()));
//        } else if (defalut instanceof Long) {
//            val = (T) ((Long) sp.getLong(key, ((Long) defalut).longValue()));
//        } else {
//            throw new RuntimeException("还没有添加这个类型数据的获取，请在代码里面添加");
//        }
//
//        //LogUtil.log(TAG+" getWithUserId key:" + getKey+",defalut:"+ defalut+",val:" + val);
        return val;
    }

    /**
     * 获取保存的信息
     *
     * @param key     key
//     * @param defalut 根据默认值传递进来的类型决定返回的类型
//     * @param <T>
     * @return
     */
//    public static <T> T get(String key, T defalut) {
//        SharedPreferences sp = getSharedPreferences(App.context);
//        String getKey = key;
//        if (defalut instanceof String) {
//            return (T) sp.getString(getKey, defalut.toString());
//        } else if (defalut instanceof Integer || defalut instanceof Byte) {
//            return (T) ((Integer) sp.getInt(getKey, ((Integer) defalut).intValue()));
//        } else if (defalut instanceof Float) {
//            return (T) ((Float) sp.getFloat(getKey, ((Float) defalut).floatValue()));
//        } else if (defalut instanceof Boolean) {
//            return (T) ((Boolean) sp.getBoolean(getKey, ((Boolean) defalut).booleanValue()));
//        } else if (defalut instanceof Long) {
//            return (T) ((Long) sp.getLong(getKey, ((Long) defalut).longValue()));
//        } else {
//            throw new RuntimeException("还没有添加这个类型数据的获取，请在代码里面添加");
//        }
//    }

    public static void removeWithUserId(String key) {
//        SharedPreferences sp = getSharedPreferences(App.context);
//        sp.edit().remove(key).commit();
    }

    public static void removeWithUserIdAndDeviceType(int deviceType, String key) {
        key = deviceType + "_" + key;
        removeWithUserId(key);
    }

    /**
     * 清除saw sab本地保存的绑定信息
     *
     * @param deviceType
     */
    public static void removeSAWBDeviceInfos(short deviceType) {
        String key1 = KEY_CENTER_KEY_VALUE + deviceType;
        String key2 = KEY_CENTER_KEY_DEVICEID + deviceType;
        String key3 = KEY_CENTER_KEY_DEVICE_TYPE + deviceType;
        String key4 = KEY_CENTER_KEY_SEQID + deviceType;
        String key5 = KEY_CENTER_KEY_SETTING_ITEM + deviceType;
        String key6 = KEY_CENTER_KEY_USER_ID + deviceType;

        removeWithUserId(key1);
        removeWithUserId(key2);
        removeWithUserId(key3);
        removeWithUserId(key4);
        removeWithUserId(key5);
        removeWithUserId(key6);

//                SPUtils.saveWithUserId(KEY_CENTER_KEY_VALUE + currentDevicetype, sb.toString());
//                SPUtils.saveWithUserId(KEY_CENTER_KEY_DEVICEID + currentDevicetype, deviceId);
//                SPUtils.saveWithUserId(KEY_CENTER_KEY_DEVICE_TYPE + currentDevicetype, deviceType);
//                SPUtils.saveWithUserId(KEY_CENTER_KEY_SEQID + currentDevicetype, seqid);
//                SPUtils.saveWithUserId(KEY_CENTER_KEY_SETTING_ITEM + currentDevicetype, settingItem);
//                SPUtils.saveWithUserId(KEY_CENTER_KEY_USER_ID + currentDevicetype, userId);
    }
    

    /**
     * 保存点击开始助眠的时间戳精确到分钟值
     */
    public static void saveFlagTimestap(String key) {
//        String data = getSharedPreferences(App.context).getString(key, "");
//        SharedPreferences.Editor edit = getSharedPreferences(App.context).edit();
//        int timestap = (int) (System.currentTimeMillis() / 1000 / 60);
//        if ("".equals(data)) {
//            data += (timestap + "");
//        } else {
//            data += ("-" + timestap);
//        }
//        edit.putString(key, data);
//        Log.d(TAG,key + "--保存时间戳：" + data);
//        edit.commit();
    }

    /**
     * 获取点击开始助眠的时间戳精确到分钟值
     */
    public static List<Integer> getFlagTimestaps(String key) {
        List<Integer> timestaps = new ArrayList<>();
//        String data = getSharedPreferences(App.context).getString(key, "");
//        if (!TextUtils.isEmpty(data)) {
//            try {
//                String[] strTimestaps = data.split("-");
//                for (int i = 0; i < strTimestaps.length; i++) {
//                    String str = strTimestaps[i];
//                    if (!"".equals(str)) {
//                        int timestap = Integer.valueOf(str);
//                        timestaps.add(timestap);
//                    }
//                }
//                Log.d(TAG,key + "---获取时间戳数据：" + Arrays.toString(timestaps.toArray()));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        return timestaps;
    }

    /**
     * 清空点击开始助眠的时间戳精确到分钟值
     */
    public static void clearFlagTimestaps(String key) {
//        SharedPreferences.Editor edit = getSharedPreferences(App.context).edit();
//        edit.putString(key, "");
//        Log.d(TAG,key + "---清空时间戳数据");
//        edit.commit();
    }


}
