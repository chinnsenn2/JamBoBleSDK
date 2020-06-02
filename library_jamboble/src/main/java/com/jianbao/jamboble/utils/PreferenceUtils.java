package com.jianbao.jamboble.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferenceUtils {
	private final static String PREFENRENCE_FILE = "appsetting";
	public final static String KEY_HOME_GUIDE 	= "home_guide_v4";
	public final static String KEY_FIRST_USE 	= "first_use";

	public final static String KEY_LOCATION_LATITUDE 	= "loction_latitude";
	public final static String KEY_LOCATION_LONGITUDE	= "loction_longtitude";
	
	public final static String KEY_LOCATION_ADDRESS		= "loction_address";
	public final static String KEY_LOCATION_CITY_NAME	= "loction_city_name";
	public final static String KEY_LOCATION_CITY_ID = "loction_cityid";
	
	public final static String KEY_SELECT_CITY_NAME = "select_city_name";
	public final static String KEY_SELECT_CITY_ID = "select_cityid";
	public final static String KEY_LOCAL_CITY_IS_O2O = "local_city_is_o2o";
	public final static String KEY_SELECT_CITY_IS_O2O = "select_city_is_o2o";
	public final static String KEY_SELECT_IGNORE = "select_ignore";
	
	//是否第一次使用
	public final static String KEY_GET_SUGAR 	= "first_get_sugar";
	public final static String KEY_GET_PRESSURE = "first_get_pressure";
	public final static String KEY_GET_WEIGHT 	= "first_get_weight";
	public final static String KEY_GET_URIC 	= "first_get_uric";
	public final static String KEY_GET_OXYGEN   = "first_get_oxygen";
	public final static String KEY_WEIGHT_GUIDE_SHOW  = "weight_guide";
	public final static String KEY_FINGERPRINT_SHOW  = "fingerprint";
	public final static String KEY_GET_WEIGHT_V3 = "first_get_weight_v3";
	public final static String KEY_GET_SPORT = "first_get_sport";
	public final static String KEY_GET_BEAN_SHOW = "first_get_bean";
	public final static String KEY_GET_CHOLESTEROL_SHOW = "first_get_cholesterol";

	public final static String KEY_GET_SPORT_GUIDE = "first_get_sport_guide";
	
	//老年版
	public final static String KEY_OLD_LOGIN = "old_login";
	public final static String KEY_OLD_VERSION = "old_version";
	
	//消息提醒设置
	public final static String KEY_VIBRATE_ENABLED = "vibrate_enabled";
	public final static String KEY_RING_ENABLED = "ring_enabled";
	
	//服务器上的版本号
	public final static String KEY_NEW_VERSION_CODE = "new_version_code";
	public final static String KEY_NEW_VERSION_INFO = "new_version_info";
	
	//体重测量间隔次数
	public final static String KEY_WEIGHT_MEASURE_INTERVAL = "weight_measure_interval";

	public final static String KEY_APPOINTMENT_CITY_ID = "appointment_city_id";

	public final static String KEY_FETAL_HEART_RECORD_TIME = "fetal_heart_record_time";
	public final static String KEY_FETAL_HEART_GUIDE = "first_fetal_heart_guide";//胎心仪首页引导
	public final static String KEY_FETAL_HEART_DEVICE_GUIDE = "first_fetal_heart_device_guide";//胎心仪设备引导
	public final static String KEY_FETAL_HEART_UPLOAD_GUIDE = "first_fetal_heart_upload_guide";

	public static final String NEW_FAMILY_LIST="new_family_list";//新版家人
	//家庭圈缓存
	public static final String FAMILY_CIRCLE_LIST = "family_circle_list";
	public static final String FAMILY_MEASURE_COUNT = "family_measure_count";
	public static final String MINE_MEASURE_COUNT = "mine_measure_count";
	public static final String MINE_DEFAULT_COVER = "mine_default_count";

	//视频播放
	public static final String KEY_VIDEO_WEIGHT_PLAYED = "video_weight_played";
	public static final String KEY_VIDEO_BLOODPRESSURE_PLAYED = "video_bloodpressure_played";
	public static final String KEY_VIDEO_BLOODSUGAR_PLAYED = "video_bloodsugar_played";
	public static final String KEY_VIDEO_UIC_PLAYED = "video_uic_played";

	public static final String KEY_WEIGHT_MANAGER_GUIDE = "weight_manager_guide";
	public static final String KEY_BLOODPRESSURE_MANAGER_GUIDE = "bloodpressure_manager_guide";
	public static final String KEY_BLOODSUGAR_MANAGER_GUIDE = "bloodsugar_manager_guide";
	public static final String KEY_UIC_MANAGER_GUIDE = "uic_manager_guide";

	public static final String KEY_OXYGEN_GUIDE = "oxygen_guide";

	public static final String KEY_MEASURETYPE_LIST = "measure_type_list";

	public static final String KEY_LOGISTICE_RED_TIME = "logistics_red_time";//我的物流红点
	public static final String KEY_COUPONS_RED_TIME = "coupon_red_time";//领券红点提示

	public static final String KEY_HOME_NOTICE = "home_notice";//是否需要通告
	public static final String KEY_HOME_NOTICE_AD = "home_notice_ad";//是否需要通告

	public static final String KEY_LAOBAI_TOAST = "laobai_toast";

	public static final String KEY_NOX_INFO = "nox_info";
	public static final String KEY_NOX_SLEEP_SCENE = "nox_sleep_scene";

	public final static String KEY_FIRST_ADD_NOX = "irst_add_nox";//第一次添加睡眠设备
	public final static String KEY_FIRST_SLEEP_HISTORY 	= "first_sleep_history";//第一次使用睡眠监测
	public final static String KEY_FIRST_SLEEP_AID 	= "first_sleep_aid";//第一次开启助眠

	public static final String KEY_XN_UNREAD_MSG_COUNT = "key_xn_unread_msg_count";

	public static final String KEY_STEP_FOR_MONEY_SHOWED = "step_for_money_showed";

	public static final String KEY_FIRST_CHECK_OPEN_NOTIFICATION = "first_check_open_notification";
	public static final String KEY_FIRST_CHECK_LAOBAI = "first_check_laobai";

	public static final String KEY_INSTALL_APK_PATH = "install_apk_path";
	public static final String KEY_NO_CHECK_CARD = "no_check_card";

	public final static String KEY_FIRST_SHOW_JFSC_WINDOW = "KEY_FIRST_SHOW_jfsc_window";
	public final static String KEY_FIRST_SHOW_MBGL_WINDOW = "KEY_FIRST_SHOW_MBGL_window";
	public final static String KEY_REG_FORM_INFO = "key_reg_form_info";

	public final static String KEY_URLS_MAP = "key_urls_map";

	public final static String KEY_FIRST_INSTALL_NEW_BANK = "first_install_new_bank_v2";
	public static final String KEY_LAST_EXIT_TIME_STAMP = "last_exit_time_stamp";
	public static final String KEY_APP_IN_FORGROUND_FIRST = "app_in_forground";
	public static final String KEY_APP_IN_BACKGROUND = "app_in_background";
	public static final String KEY_PROTOCOLS_MODIFIED_DATE = "protocols_modified_date";
	public static final String KEY_ADDITIONAL_INFO = "additional_info";
	public static final String KEY_ADDITIONAL_NEED_CONFIRMED = "additional_need_confirmed";
	public static final String KEY_LAUNCH_VIDEO = "launch_video_v1";
	public static final String KEY_RETAIL_SHOP_LOCATION = "tail_shop_location";
	public static final String KEY_MEDICAL_HOME = "medical_home";


	private static SharedPreferences getSharedPreferences(Context context){
		return context.getSharedPreferences(PREFENRENCE_FILE, Context.MODE_PRIVATE);
	}
	
	public static void putDouble(Context context, String key, double value){
		putString(context, key, String.valueOf(value));
	}

	public static double getDouble(Context context, String key, double defaultValue){
		return Double.parseDouble(getString(context, key, String.valueOf(defaultValue)));
	}

	public static void putString(Context context, String key, String value){
		SharedPreferences preferences = getSharedPreferences(context);
		preferences.edit().putString(key, value).apply();
	}

	public static String getString(Context context, String key, String defaultVlaue){
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getString(key, defaultVlaue);
	}
	
	
	public static void putBoolean(Context context, String key, boolean value){
		SharedPreferences preferences = getSharedPreferences(context);
		preferences.edit().putBoolean(key, value).apply();
	}

	public static boolean getBoolean(Context context, String key, boolean defaultVlaue){
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getBoolean(key, defaultVlaue);
	}
	
	
	public static void putInt(Context context, String key, int value){
		SharedPreferences preferences = getSharedPreferences(context);
		preferences.edit().putInt(key, value).apply();
	}

	public static int getInt(Context context, String key, int defaultVlaue){
		SharedPreferences preferences = getSharedPreferences(context);
		return preferences.getInt(key, defaultVlaue);
	}
}