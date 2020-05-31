package com.jianbao.jamboble.device.nox.bean;

import android.os.Environment;

/**
 * Created by admin on 2016/6/6.
 */

public class Constants {
    /**
     * 缓存文件根目录
     */
    public static final String CACHE_ROOT_DIR = Environment.getExternalStorageDirectory() + "/medica/SleepacePro/";
    /**
     * 音乐缓存文件
     */
    public static final String MUSIC_DIR = CACHE_ROOT_DIR + "/music/";

    /*
    * 广告图片缓存文件目录
    * */
    public static final String AD_BITMAP_DIR = CACHE_ROOT_DIR + "/bitmap/";

    /*
    * 广告图片缓存文件名
    * */
    public static final String AD_BITMAP_NAME = "ad.jpg";

    /**
     * 固件缓存文件
     */
    public static final String FIRMWARE_DIR = CACHE_ROOT_DIR + "/firmware/";
    /**
     * 广告图片缓存文件
     */
    public static final String AD_CACHE_DIR = CACHE_ROOT_DIR + "/ad/";
    /**
     * 固件缓存文件
     */
    public static final String OLD_FIRMWARE_DIR = Environment.getExternalStorageDirectory() + "/reston/";

    /**
     * 所有缓存的路径,好友报告，固件安装包
     */
    public static final String[] CACHE_PATHS = {FIRMWARE_DIR, OLD_FIRMWARE_DIR, AD_CACHE_DIR};
    public static final String SP_KEY_CLICK_STARTSLEEP_TIMESTEP = "click_startsleep_timestep";
    public static final String BROCAST_KEEP_ACTIVITY_FRONT_CHANGE = "brocast_keep_activity_front_change";


    /**
     * 喜马拉雅音乐播放器播放倒计时关闭时间
     */
    public static int[] MUSIC_PLAYER_TIMING_TIMES = {0, 20, 30, 60, 90};
    /**
     * 生成报告的最短时间，短于这个时间不生成报告,单位，分钟
     */
    public static final int LESS_REPORT_MIN = 180;

    /**
     * 统一渠道号，方便修改
     */
    //TODO:渠道号
    public static final String CHANNELID = "10000";
    //TODO:渠道名
    public static final String CHANNELNAME = "sleepace";

    public static final boolean DEBUG = true;

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
     * 推送类型，跳转App
     */
    public static final String PUSH_TYPE_TO_APP = "0";
    /**
     * 推送类型为App时，无跳转
     */
    public static final String PUSH_PAGE_IDLE = "0";
    /**
     * 推送类型为App时，页面跳转到开始监测
     * 2017.4.11 需求变更：1无跳转
     */
    public static final String PUSH_PAGE_TO_MONITOR = "1";
    /**
     * 推送类型为App时，页面跳转到信息中心
     */
    public static final String PUSH_PAGE_TO_MESSAGECENTER = "2";
    /**
     * 推送类型为App时，页面跳转到场景
     */
    public static final String PUSH_PAGE_TO_SCENE = "3";
    /**
     * 推送类型为App时，页面跳转到设备
     */
    public static final String PUSH_PAGE_TO_DEVICE = "4";
    /**
     * 推送类型为App时，页面跳转到报告
     */
    public static final String PUSH_PAGE_TO_REPORT = "5";
    /**
     * 推送类型为App时，页面跳转到发现
     */
    public static final String PUSH_PAGE_TO_FOUND = "6";


    /**
     * sharepreference命名
     */
    public static final String SP_NAME = "sleepace_sp";
    /**
     * 温度单位：摄氏度
     */
    public final static byte UNIT_TEMPERATURE_C = 1;

    /**
     * 温度单位：华氏度
     */
    public final static byte UNIT_TEMPERATURE_F = 2;
    /**
     * 枕扣最低电量提醒，电量低于这个的时候提醒低电量
     */
    public static final int SLEEPDOT_LOW_POWER_MIN = 10;


    /**
     * 消息跳转类型：APP内连接 跳转检测
     */
    public static final String MESSAGE_JUMP_MONITOR = "1";
    /**
     * 消息跳转类型：APP内连接 跳转消息中心
     */
    public static final String MESSAGE_JUMP_MESSAGE_CENTER = "2";
    /**
     * 环境
     */
    public static final String SP_KEY_ENVIRONMENT = "sp_key_environment";


    //http请求成功状态码
    public static final int HTTP_REQUEST_SUCCESS_STATUS_CODE = 0x00;
    /**
     * 用户相关信息文件的文件名，这种配置信息退出登录的时候需要清空
     */
    public static final String CONFIG_USER = "user";

    /**
     * 请求码：好友请求
     */
    public static final int CODE_FRIEND_REQUEST = 1000;
    /**
     * 请求码：同意好友请求
     */
    public static final int CODE_ACCEPT_FRIEND_REQUEST = 1001;
    /**
     * 请求码：查询好友
     */
    public static final int CODE_SEARCH_FRIEND = 1002;
    /**
     * 请求码：添加好友
     */
    public static final int CODE_FRIEND_DETAIL_OPERATION = 1003;
    /**
     * 请求码：意见反馈
     */
    public static final int CODE_SUGGESTION_FEEDBACK = 1004;
    /**
     * 请求码：修改密码
     */
    public static final int CODE_EDIT_PSW = 1005;
    /**
     * 请求码：绑定帐号第一步
     */
    public static final int CODE_ACCOUNT_BIND_STEP1 = 1006;
    /**
     * 请求码：绑定帐号第二步
     */
    public static final int CODE_ACCOUNT_BIND_STEP2 = 1007;
    /**
     * 请求码：解绑帐号
     */
    public static final int CODE_ACCOUNT_UNBIND = 1008;
    /**
     * 请求码：第三方绑定
     */
    public static final int CODE_ACCOUNT_THIRD_BIND = 1009;
    /**
     * 请求码：第三方解绑帐号
     */
    public static final int CODE_ACCOUNT_THIRD_UNBIND = 1010;
    /**
     * 请求码：报告中，编辑图表顺序
     */
    public static final int CODE_EDIT_GRAPHVIEW_ORDER = 1011;
    /**
     * 请求码：验证号码是否存在
     */
    public static final int CODE_ACCOUNT_EXIST = 1012;
    /**
     * 请求码：获取好友列表
     */
    public static final int CODE_FRIEND_LIST = 1013;
    /**
     * 请求码：编辑用户信息
     */
    public static final int CODE_EDIT_USER = 1014;
    /**
     * 请求码：更新升级
     */
    public static final int CODE_UPGRADE = 1015;
    /**
     * 请求码：选择设备
     */
    public static final int CODE_SELECT_DEVICE = 1016;
    /**
     * 请求码：选择音乐
     */
    public static final int CODE_SELECT_MUSIC = 1017;
    /**
     * 请求码：编辑场景
     */
    public static final int CODE_EDIT_SCENE = 1018;
    /**
     * 请求码：解绑设备
     */
    public static final int CODE_UNBIND_DEVICE = 1019;
    /**
     * 请求码：照明灯光设置
     */
    public static final int CODE_LIGHT_SETTING = 1020;
    /**
     * 请求码：小夜灯灯光设置
     */
    public static final int CODE_SMALL_LIGHT_SETTING = 1021;
    /**
     * 请求码：选择开始结束时间
     */
    public static final int CODE_SELECT_START_END_TIME = 1022;

    /**
     * Nox2手势控制信息设置
     */
    public static final int CODE_NOX2_GESTURE_INFO_SET = 1024;
    /**
     * 获取Banner广告地址
     */
    public static final int CODE_DISCOVERY_GET_BANNER_ADS = 1025;
    /**
     * 获取发现模块热文地址
     */
    public static final int CODE_DISCOVERY_GET_NEWS = 1026;
    /**
     * 收藏音乐，取消收藏
     */
    public static final int CODE_COLLECT_MUSIC = 1027;
    /**
     * 获取收藏音乐
     */
    public static final int CODE_GET_MUSIC_COLLECTION = 1028;
    /**
     * 从场景专辑页面返回
     */
    public static final int CODE_BACK_FROM_SCENE_ALBUM = 1029;
    /**
     * 智能模式设置
     */
    public static final int CODE_SMART_MODE_SETTING = 1030;
    /**
     * 选择更多主题
     */
    public static final int CODE_SELECT_THEME = 1031;
    /**
     * 设置纽扣睡眠时间
     */
    public static final int CODE_EDIT_SLEEPDOT_TIME = 1032;
    /**
     * 设备控制
     */
    public static final int CODE_DEVICE_SETTING = 1033;

    /*
    * 从场景主页无闹钟的时候去编辑闹钟请求码
    * */
    public static final int CODE_EDIT_ALARM = 1034;
    /*
    * 修改环境温度单位
    * */
    public static final int CODE_CHANGE_TEMP_UNIT = 1035;

    /**
     * 获取关于悬停，挥手的设置(v2)
     */
    public static final int CODE_NOX2_GESTURE_INFO_GET_V2 = 1036;

    /*
    *请求码，请求闹钟唤醒方式
    * */
    public static final int CODE_SELECT_ALARM_WAKEUP_WAY = 1037;


    /**
     * 绑定谷歌账号状态码
     */
    public static final int CODE_BIND_GOOGLE=1038;

    /**
     * 绑定faceBook状态码
     */
    public static final int CODE_BIND_FACEBOOK=1039;

    /**
     * 解绑谷歌
     */
    public static final int CODE_UNBIND_GOOGLE=1040;
    /**
     * 解绑facebook
     */
    public static final int CODE_UNBIND_FACEBOOK=1041;

    /**
     * //服务器连接失败，请重新登录
     */
    public static final short SocketBreakPleaseRetry = 0xfe;

    /**
     * SharePrefence保存最新消息ID的key
     */
    public static final String SP_KEY_NEWEST_MSG_ID = "sp_key_newest_msg_id";

    /**
     * 登录回传参数未读好友条数
     */
    public static final String KEY_LOGIN_PARAM_UN_READ_FRIEND_REQUEST = "unReadFriendRequest";
    /**
     * 登录回传参数好友
     */
    public static final String SP_KEY_FRIENDS = "friends";
    /**
     * 登录回传参数用户信息
     */
    public static final String SP_KEY_USER_INFO = "userInfo";
    /**
     * 登录回传参数设备信息
     */
    public static final String SP_KEY_DEVICE_INFO = "deviceInfo";
    /**
     * nox时钟休眠的设备信息
     */
    public static final String SP_KEY_CLOCK_SLEEP = "clocksleep";

    /*
    * 闹钟音量
    * */
    public static final String SP_KEY_ALARM_VOLUME = "alarm_volume";

    /*
    * 登陆回传的系统信息
    *
    * */
    public static final String SP_KEY_SYSTEM = "system";

    /**
     * 登录回传参数第三方登录信息
     */
    public static final String SP_KEY_PLATFORMS_INFO = "platforms";

    /**
     * 是否是自动开始监测的key
     */
    //public static final String SP_KEY_IS_AUTO_START_MONITOR = "is_auto_start_monitor";
    /**
     * 设备版本信息
     */
    public static final String SP_KEY_DEVICE_VERSION = "sp_key_device_version";


    /**
     * 登录回传参数未读消息条数
     */
    public static final String KEY_LOGIN_PARAM_UN_READ_MESSAGE = "unReadMessage";

    /**
     * 春雨医生
     */
    public static final String KEY_LOGIN_PARAM_CHUN_YU = "chunyu3.0";

    /**
     * 是否显示睡眠评估体系
     */
    public static final String KEY_LOGIN_PARAM_SLEEP_SYSTEM = "sleepSystem";

    /**
     * 是否显示睡眠计划
     */
    public static final String KEY_LOGIN_PARAM_SLEEP_PLAN = "smplan";

    /**
     * 是否显示喜马拉雅
     */
    public static final String KEY_LOGIN_PARAM_XMLY = "xmly";


    /**
     * 问卷
     */
    public static final String KEY_LOGIN_PARAM_WENJUAN = "wenjuan";

    /**
     * 推送给MainActivity传的Extra type
     */
    public static final String BROCAST_GET_NEW_MESSAGE = "BROCAST_GET_NEW_MESSAGE";
    /*
* 邮箱注册返回码，邮箱已经注册了
* */
    public static final int EMAIL_ALREADY_REGISTED_CODE = 2009;
    /*
    * 忘记密码，第二步设置密码的时候验证码错误返回码
    * */
    public static final int FORGET_PASSWORD_STEP_SECOND_CODE = 2013;
    /**
     * 推送给MainActivity传的Extra page
     */
    public static final String PUSH_EXTRA_PAGE = "extra_page";
    /**
     * 推送给MainActivity传的Extra type
     */
    public static final String PUSH_EXTRA_TYPE = "extra_type";
    /**
     * 提示对话框显示时间，单位毫秒 ms
     */
    public static final int DIALOG_SHOW_TIME = 0;

    /**
     * 登陆页面，延迟时间
     */
    public static final int LOGIN_DELAY_TIME = 15 * 1000;

    /**
     * discovery页面，延迟时间
     */
    public static final int DISCOVERY_LOGIN_DELAY_TIME = 15 * 1000;
    /*
 * 登陆回传参数，是否是第一次登陆
 * */
    public static final String KEY_LOGIN_PARAM_IS_FIRST_LOGIN = "firstLogin";

    //身高单位cm，只是用户展示的单位，上传到服务器的值还是厘米
    public static final int UNIT_TYPE_HEIGHT_CM = 1;
    //身高单位ft,in，只是用户展示的单位，上传到服务器的还是厘米
    public static final int UNIT_TYPE_HEIGHT_FT_IN = 2;
    //体重单位kg,只是用户展示的单位，上传到服务器的还是kg
    public static final int UNIT_TYPE_WEIGHT_KG = 1;
    //体重单位lb，只是用户展示的单位，上传到服务器的还是lb
    public static final int UNIT_TYPE_WEIGHT_LB = 2;

    public static final String CHECK_METHOD = "CHECK_METHOD";
    public static final String USER_ID = "USER_ID";

    /**
     * 密码设置的KEY
     */
    public static final String EXTRA_KEY_SET_PASSWORD = "SET_PASSWORD";
    /**
     * 密码设置的KEY
     */
    public static final String EXTRA_VALUE_SET_PASSWORD = "set_psd";
    /**
     * 注册方式的KEY
     */
    public static final String KEY_REGISTER_METHOD = "USER_METHOD";
    /**
     * Extra的设备key
     */
    public static final String EXTRA_KEY_DEVICE = "extra_device";

    /**
     * 闹钟响起的时间戳序列
     */
    public static final String SP_KEY_CLOCK_RING_TIMESTEP = "CLOCK_RING_TIMESTEP";

    /**
     * <p>
     * 更细实时数据
     * </p>
     */
    public static final byte UpDateRealTime = 0x03;

    /**
     * 报告Demo数据的开始时间，单位秒
     */
    public static final int DEMO_DATA_STARTTIME = 973955349;

    /**
     * 枕扣最低电量提醒，电量低于这个的时候提醒低电量
     */
    public static final int MILKY_LOW_POWER_MIN = 10;

    public static final String NOX_SSID = "Sleepace Nox";

    /**
     * 异常说明以及名词解释等链接地址
     */
    public static final String KEY_URL_INTERPRETATION = "url_interpretation";

    /**
     * 红灯闪烁的字段      纽扣1代
     */
    public static final String VALUE_SLEEPDOT_REDLED = "sleepdot_redled";

    /**
     * 红灯闪烁的字段          纽扣2代
     */
    public static final String VALUE_SLEEPDOT2_REDLED = "sleepdot2_redled";

    /**
     * 享睡纽扣没有报告H5           纽扣1代
     */
    public static final String VALUE_SLEEPDOT_NO_REPORT = "sleepdot_no_report";
    /**
     * 享睡纽扣没有报告H5           纽扣2代
     */
    public static final String VALUE_SLEEPDOT2_NO_REPORT = "sleepdot2_no_report";


    /**
     * 连接享睡纽扣失败的原因及解决办法             纽扣1代
     */
    public static final String VALUE_SLEEPDOT_CONNECT_FAIL = "sleepdot_connect_fail";

    /**
     * 连接享睡纽扣2失败的原因及解决办法         纽扣2代
     */
    public static final String VALUE_SLEEPDOT2_CONNECT_FAIL = "sleepdot2_connect_fail";

    /**
     * 更换享睡纽扣电池             纽扣1代
     */
    public static final String VALUE_SLEEPDOT_CHANGE_BATTERY = "sleepdot_change_battery";

    /**
     * 更换享睡纽扣2电池             纽扣2代
     */
    public static final String VALUE_SLEEPDOT2_CHANGE_BATTERY = "sleepdot2_change_battery";

    /**
     * RestOn红灯闪烁
     */
    public static final String VALUE_RESTON_REDLED = "reston_redled";

    /**
     * RestOn-Z4红灯闪烁
     */
    public static final String VALUE_RESTON_Z4_REDLED = "restonz4_redled";

    /**
     * 忆眠枕红灯闪烁
     */
    public static final String VALUE_PILLOW_REDLED = "pillow_redled";


    /**
     * RestOn没有报告的原因
     */
    public static final String VALUE_RESTON_NO_REPORT = "reston_no_report";
    /**
     * RestOn-Z400没有报告的原因
     */
    public static final String VALUE_RESTONZ4_NO_REPORT = "restonz4_no_report";

    /**
     * 忆眠枕没有报告的原因
     */
    public static final String VALUE_PILLOW_NO_REPORT = "pillow_no_report";

    /**
     * 连接RestOn失败的原因及解决办法
     */
    public static final String VALUE_RESTON_CONNECT_FAIL = "reston_connect_fail";

    /*
    * RestonZ400/Z400T
    *
    * */
    public static final String VALUE_RESTON_Z400_CONNECT_FAIL = "restonz4_connect_fail";

    /**
     * 连接忆眠枕失败的原因及解决办法
     */
    public static final String VALUE_PILLOW_CONNECT_FAIL = "pillow_connect_fail";

    /**
     * 连接Nox失败的原因及解决办法
     */
    public static final String VALUE_NOX_CONNECT_FAIL = "nox_connect_fail";

    /**
     * 连接Nox2失败的原因及解决办法
     */
    public static final String VALUE_NOX2_CONNECT_FAIL = "nox2_connect_fail";
    /**
     * 连接Nox2WIFI失败的原因及解决办法
     */
    public static final String VALUE_NOX2W_CONNECT_FAIL = "nox2w_connect_fail";
    /**
     * 连接Nox2WIFI失败的原因及解决办法
     */
    public static final String VALUE_NOX_CONNECT_ALEXA = "nox_connect_alexa";

    /**
     * 环境解释页面
     */
    public static final String VALUE_NOX_ENVIRONMENT = "ambient";

    /**
     * 没有报告的原因
     */
    public static final String VALUE_NON_NO_REPORT = "non_no_report";

    /**
     * 心率
     */
    public static final String VALUE_HEARTRATE = "heartrate";

    /**
     * 呼吸率
     */
    public static final String VALUE_BREATHRATE = "breathrate";


    /**
     * APP没有报告原因
     */
    public static final String VALUE_APP_NO_REPORT = "app_no_report";

    /**
     * 手机闹钟无法响起的原因及解决办法
     */
    public static final String VALUE_PHONE_NO_RING = "phone_no_ring";


    public static final String STATUS_IGNOR_APP_VER = "old_ignor_app_version";
    public static final String STATUS_IGNOR_NOX_VER = "old_ignor_nox_version";

    /**
     * 描述型H5数据保存
     */
    public static final String VALUE_DESC_URL = "url_description_value";

    /**
     * 描述型H5
     */
    public static final String KEY_DESC_VERSION = "describe";

    public static final byte MAX_VOLUME = 16;

    public static final byte DEFAULT_SLEEP_SCENE_VOLUME = 6;
    public static final byte DEFAULT_OTHER_SCENE_VOLUME = 12;

    /*
    是否是第一次将手机作为助眠设备，第一次的话音量需要从云端获取
     */
    public static final String SP_KEY_FIRST_USER_PHONE_AS_SLEEPAID = "is_first_user_phone_as_sleepaid";


    /**
     * 发送颜色值给硬件时，控制发送频率————> 1 为 1°：只有间距为1°的时候才会发送过去
     */
    public static final int VALUE_COLOR_SEND_SPACE = 1;
    //w值变化的最小阈值
    public static final int VALUE_W_MIN_SPACE = 1;

    /**
     * 渐变圆半径
     */
    public static final int GRADIENT_RADUS = 200;

    /**
     * 圆环半径
     */
    public static final float GRADIENT_CIRCLE_RADUS = 24f;
    //音乐渠道id
    public static final int MUSIC_CHANNEL_XIMALAYA = 1000;
    public static final int MUSIC_CHANNEL_SLEEPACE = 10000;
    public static final int MUSIC_CHANNEL_SLEEPACE_XIMALAYA = 1001;//喜马拉雅-sleepace专辑


    public static final int MUSIC_TYPE_ALBUM = 1;
    public static final int MUSIC_TYPE_MUSIC = 2;

    /**
     * 教程图文卡片key
     */
    public static final String KEY_COURSE_PIC = "course_pic";

    /**
     * 教程图文视频 key
     */
    public static final String KEY_COURSE_VIDEO = "course_video";

    /**
     * 描述内容 key  排行版
     */
    public static final String KEY_DESC_URL_RANK = "rank";

    /**
     * 描述内容 key chunyu      春雨医生
     */
    public static final String KEY_DESC_URL_CHUNYU = "chunyu";

    /**
     * 描述内容 key 帮助中心
     */
    public static final String KEY_DESC_URL_HELP_CENTER = "faqs";

    /**
     * 描述内容 key 产品(Reston、nox、nox2、milky)常见问题
     */
    public static final String KEY_DESC_URL_COMMON_QUESTION = "pro_faqs";

    /**
     * 描述内容 key  特殊场景入口
     */
    public static final String KEY_DESC_URL_SPECTIAL_ENTER = "site_faq";

    /**
     * 描述内容 key  设备使用教程
     */
    public static final String KEY_DESC_URL_SPECTIAL_MANUAL = "manual";


    /**
     * 描述内容 key  使用条款
     */
    public static final String KEY_DESC_URL_TERMS = "terms";

    /**
     * 描述内容 key  关于
     */
    public static final String KEY_DESC_URL_ABOUT = "about";

    /**
     * Nox配置失败 key  查看更多原因
     */
    public static final String KEY_DESC_URL_ADD_NOX_FAIL = "nox_add_fail";

    /**
     * 睡眠评估系统链接 key
     */
    public static final String KEY_SLEEP_SYSTEM = "sleep_system";

    /**
     * nox2 wifi版连接失败 原因
     */
    public static final String KEY_NOX2_WIFI_FAIL = "noxw_connect_fail_detail";

    //public static final String KEY_ALBUM_NAME = "album_name";
    //public static final String KEY_TRACK_NAME = "track_name";

    /**
     * 本地最新description 描述信息的最新版本号
     */
    public static final String SP_KEY_LOCAL_DESC_VERSION = "newest_desc_version";
    /**
     * nox2wifi版IP缓存
     */
    public static final String SP_KEY_NOX2_TEMP_IP = "nox2_temp_ip";

    /**
     * Nox1 wifi版中 nox详情设置 本地保存当前温度的单位
     */
    public static final String SP_KEY_ETEMP_UNIT = "the_temperature_unit";
    public static final int CELSIUS = 1;    //摄氏度
    public static final int FAHRENEHIT = 2; //华氏度

    public static final int DEFAUL_NOX_PRO_ALARM_MUSIC_ID = 2;
    public static final int DEFAUL_NOX_2_ALARM_MUSIC_ID = 31001;
    public static final int DEFAULT_NOX_SAW_ALARM_MUSIC_ID = 31051;
    public static final int DEFAULT_NOX_SAB_ALARM_MUSIC_ID = 31001;
    public static final int DEFAULT_NOX_2_WIFI_ALARM_MUSIC_ID = 2;
    public static final int DEFAUL_NOX_PRO_AID_MUSIC_ID = 1;
    public static final int DEFUAL_NOX_2_AID_MUSIC_ID = 31001;

    //H5弹窗 消失型
    public static final String H5_DIALOG_MISS_TIPS_TYPE = "notification";
    //H5弹窗 弹窗型 01 确认 取消
    public static final String H5_DIALOG_SELECT_SURE_OR_CANCEL_TYPE = "button";
    public static String SP_KEY_KEEP_ACTIVITY_FRONT = "keep_activity_froent";

    /*  public static final String STORE_REGISTER_INFOS_PHONENUM = "register_phone_num";
      public static final String STORE_REGISTER_INFOS_CODE = "register_phone_code";
      public static final String STORE_REGISTER_INFOS_EMAIL = "register_email";
      public static final String STORE_REGISTER_INFOS_EMAIL_PSW = "register_email_psw";*/
    public static final String SP_WIFI_NAME = "wifi_name";
    /**
     * 设备改变wifi结果 true----wifi更改成功，false----更改失败  默认true
     */
    public static final String SP_WIFI_CHANGE_RESULT = "wifi_name_change_result";
    public static final String SP_PRE_WIFI_NAME = "device_pre_wifi_name";   //设备改变wifi前的wifi名字
    /**
     * Nox控制页面 切换tab 最后的位置
     */
    public static final String SP_NOX_FRAGMENT_LAST_POSITION = "nox_fragment_last_position";

    /**
     * Nox2控制页面 切换tab 最后的位置
     */
    public static final String SP_NOX2_FRAGMENT_LAST_POSITION = "nox2_fragment_last_position";

    /*
    * 每个闹钟设备最多运行添加的闹钟数量，目前最多是5个闹钟
    * */
    public static final int MAX_ALARM_COUNT_PER_DEVICE = 5;


    public static final byte MSG_TYPE_HISTORY_DATA = 0x50; // 历史概要数据

    /**
     * 睡眠主题中显示的数据条数
     */
    public static final int MAX_SHOW_SLEEP_THEME_MUSIC_NUM = 10;


    /**
     * 新手引导状态
     */

    public static final String TAG_MAIN_ACTIVITY = "MainActivity";

    public static final String TAG_CONTROL_ACTIVITY = "DevicesControlCenterActivity";

    public static final String TAG_SLEEP_ACTIVITY = "SleepActivity";

    public static final String TAG_CLOCKLIST_ACTIVITY = "ClockListActivity";

    public static final String EXTRA_FROM_BIND_DEVICE = "from_bind_device";

    public static final String KEY_NOX_GUIDE = "key_nox_guide";

    /**
     * 用户教程信息
     */
    public static final String KEY_USER_COURSE_LIST = "user_course_list";

    /**
     * 用户专辑信息
     */
    public static final String KEY_USER_ALBUM_LIST = "user_album_list";
    /**
     * 用户选择的默认专辑ID
     */
    public static final String KEY_DEFAULT_ALBUM_ID = "default_album_id";
    /**
     * 音乐播放的循环模式
     */
    public static final String KEY_MUSIC_CURRENT_PLAY_MODE = "KEY_MUSIC_CURRENT_PLAY_MODE";
    /**
     * 音乐播放的开关
     */
    public static final String KEY_MUSIC_SWITCH = "KEY_MUSIC_SWITCH";
    /**
     * 正在播放音乐在音乐列表的位置索引
     */
    public static final String KEY_MUSIC_SELECTION = "KEY_MUSIC_SELECTION";
    /**
     * 音乐播放的音量
     */
    public static final String KEY_MUSIC_VOLUME = "KEY_MUSIC_VOLUME";
    /**
     * 音乐播放的来源
     */
    public static final String KEY_MUSIC_FROM = "KEY_MUSIC_FROM";
    /**
     * 主要是存储助眠音乐是否第一次进来播放
     */
    public static final String KEY_MUSIC_FIRST_PLAY = "KEY_MUSIC_FIRST_PLAY";
    /**
     * 用户助眠时灯光标记
     */
    public static final String KEY_LIGHT_FLAG = "key_light_flag";
    /**
     * 专辑音乐开关标记
     */
    //public static final String KEY_MUSIC_FLAG = "key_music_flag";
    /**
     * nox小夜灯配置
     */
    public static final String KEY_SMALL_LIGHT_CONFIG = "key_small_light_config";
    /**
     * 无限音频播放方式的名字
     */
    public static final String KEY_WIRELESS_AUTO_NAME = "collectType";
    /**
     * 无限音频播放方式
     */
    public static final String KEY_WIRELESS_AUTO_MODE = "wireless_auto_mode";
    /**
     * Nox2w 自定义专辑
     */
    public static final String KEY_NOX2W_CUSTOM_ALBUM = "key_nox2w_custom_album";

    /*
    * Nox2W ip地址key
    * */
    public static final String KEY_NOX2W_IP = "key_nox2w_ip";
    /**
     * Nox2w 挥手自定义颜色
     */
    public static final String KEY_NOX2W_GESTURE_CUSTOM_COLOR = "key_nox2w_gesture_custom_color";
    /**
     * 专辑名称
     */
    public static final String KEY_XMLY_ALBUM_NAME = "key_xmly_album_name";
    /**
     * 专辑背景
     */
    public static final String KEY_XMLY_ALBUM_PLAYERBG = "key_xmly_album_playerbg";

    /**
     * 专辑介绍
     */
    public static final String KEY_XMLY_ALBUM_DESC = "key_xmly_album_desc";
    /**
     * 教程channel id
     */
    public static final String KEY_COURSE_CHANNEL_ID = "key_course_channel_id";

    /**
     * 教程播放器页面碟片图片
     */
    public static final String KEY_COURSE_PLAYER_IMG = "key_course_player_img";

    /**
     * 最后一次同步的温度
     */
    public static final String KEY_LAST_TEMP = "key_last_temp";
    /**
     * 最后一次同步的湿度
     */
    public static final String KEY_LAST_HUMIDITY = "key_last_humidity";
    /**
     * 最后一次同步的时间
     */
    public static final String KEY_LAST_UPDATE_TIME = "key_last_update_time";


    /**
     * 蓝牙播放
     */
    public static final int MSG_WIRELESS_AUTO_MODE_BLE = 1;
    /**
     * 安卓Dlan播放
     */
    public static final int MSG_WIRELESS_AUTO_MODE_DLAN = 2;

    /**
     * Nox2 W 挥手编辑专辑中顶部原始数据key
     */
    public static final String KEY_SWITCH_ALBUM_TOP_MUSICS = "edit_album_top_musics";

    /**
     * 第三方登陆  邮箱或手机密码保存
     */
    public static final String KEY_BIND_SET_PSW = "extra_bind_set_psw";

    public static final String SCENE_MUSIC_TIME = "scene_music_time";

    public static final String XIMALAY_MUSIC_TIME = "ximalaya_music_time";

    /**
     * 教程最后一次播放时间戳
     */
    public static final String KEY_COURSE_MUSIC_ID = "courseMusicId";
    public static final String COURSE_MUSIC_TIME = "course_music_time";

    public static final String KEY_COURSE_MUSIC_PIC_URL = "picUrl";
    /**
     * 教程最后一次播放的音乐id
     */
    public static final String COURSE_CURRENT_MUSIC_ID = "course_music_id";
    /**
     * 教程最后一次播放音乐的所在位置
     */
    public static final String COURSE_CURRENT_MUSIC_POSITION = "course_music_position";

    /**
     * 当前系统语言和前次系统语言对比
     * 主要用于描述文案的拉取（国际版）
     */
    public static final String KEY_PRE_LANGUAGE = "prelanguage";
    public static final String COURSE_ALBUM_NAME = "course_album_name";
    /**
     * 教程最后一次播放的教程id
     */
    public static final String COURSE_CURRENT_TUTORIA_ID = "course_tutoria_id";

    /**
     * 喜马拉雅最后一次播放的音乐id
     */
    public static final String XMLY_CURRENT_MUSIC_ID = "ximalaya_music_id";

    /**
     * 喜马拉雅最后一次播放的所在位置
     */
    public static final String XMLY_CURRENT_MUSIC_POSITION = "ximalaya_music_position";

    /**
     * 喜马拉雅最后一次播放的专辑id
     */
    public static final String XMLY_CURRENT_ALBUM_ID = "ximalya_music_album_id";

    /**
     * 喜马拉雅播放的时间戳
     */
    public static final String XMLY_MUSIC_TIME_STAMP = "ximalay_music_time_stamp";

    /**
     * 喜马拉雅最后一次播放的大背景图
     */
    public static final String XMLY_MUSIC_BG_URL = "ximalay_music_bg_url";


    /**
     * 喜马拉雅最后一次播放的是描述信息
     */
    public static final String XMLY_MUSIC_DESCRIPTION = "ximalaya_music_description";

    /**
     * 喜马拉雅最后一次播放的专辑名字
     */
    public static final String XMLY_MUSIC_ALBUM_NAME = "ximalay_music_album_name";


    /**
     * 谷歌飞度是否授权
     */
    public static final String KEY_GOOGLE_FIT_AUTH = "google_fit_auto";


    /**
     * 开启时间
     */
    public static final String KEY_GOOGLE_FIT_STARTTIME = "google_fit_startTime";
    /**
     * 心率数据
     */
    public static final String KEY_GOOGLE_FIT_HEART_DATA = "google_fit_heart_data";

    /**
     * 记录开启睡觉场景时候的语言
     */
    public static final String SLEEP_SCENE_LANGUAGE_KEY = "sleep_scene_language_key";

    /**
     * 记录当前音乐的播放位置
     */
    public static final String SLEEP_MUSIC_DURATION = "sleep_music_duration";

    /**
     * 记录当前音乐的id
     */
    public static final String SLEEP_MUSIC_ID = "sleep_music_id";

    /**
     * 记录当前音乐的总时长
     */
    public static final String SLEEP_MUSIC_TOTAL_DURATION = "sleep_music_total_duration";

    /**
     * 设备列表需要的版本信息
     * 记录版本信息，保存本地
     * 比较每次登陆返回的版本号
     * 确认是否需要重新加载覆盖相关信息
     */
    public static final String KEY_DEVICES_VERSION_CODE = "device_version_code";

    /**
     * 登陆或者注册成功之后
     * 服务端返回的设备version值的key
     */
    public static final String KEY_DEVICE_SERVER_HOST_VERSION = "deviceListVersion";

    /**
     * 登陆或者注册 返回的的设备信息存储KEY
     */
    public static final String KEY_DEVICE_SERVER_INFOS = "server_devices_info";

    /**
     * 登陆返回的信息
     */
    public static final String KEY_SERVER_INFOS = "server_infos";

    /**
     * 连接智能香薰灯失败的原因及解决办法         SAW
     */
    public static final String KEY_CONNECT_AROMA_LIGHT_ERROR_AND_RESOLVENT = "fragrance1001_connect_fail";

    /**
     * 连接智能香薰灯失败的原因及解决办法        SAB
     */
    public static final String KEY_CONNECT_AROMA_LIGHT_ERROR_AND_RESOLVENT_SAB = "sa1001_2_connect_fail";


    /**
     * 智能香薰灯绑定失败的原因及解决办法
     */
    public static final String KEY_AROMA_LIGHT_BIND_ERROR_AND_RESOLVENT = "fragrance1001_add_fail";
    /**
     * 如何连接Amazon Alexa
     */
    public static final String KEY_THE_TO_CONNECT_AROMA_LIGHT = "fragrance1001_connect_alexa";
    /**
     * 香薰灯wifi版无法通过手机恢复出厂设置
     */
    public static final String KEY_UNABLE_TO_REST_BY_PHONE = "fragrance1001_reset";

    /*
    * 香薰灯蓝牙版无法通过手机恢复出厂设置
    * */
    public static final String KEY_AROM_LIGHT_BLE_UNABLE_TO_REST_BY_PHONE = "sa1001_2_reset";

    /**
     * 智能香薰灯Wifi常见问题
     */
    public static final String KEY_AROMA_LIGHT_COMM_QUESTION = "aroma";

    /*
    *智能香薰灯蓝牙版常见问题
    * */
    public static final String KEY_AROMA_LIGHT_BLE_COMM_QUESTION = "aroma_sa1001_2";

    /**
     * 服务端返回的页面文案信息保存key
     */
    public static final String KEY_WEB_TXT_INFOS = "web_txt_infos";


    /**
     * 保存本地的香薰弹窗关闭时间
     */
    public static final String KEY_AROMA_REMIND_LOCAL_TIME = "aroma_remind_local_time";

    /**
     * 香薰灯定时开启列表key
     */
    public static final String KEY_AROMA_TIMING_OPEN_LIST = "key_aroma_timing_open_list";

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

    public static final String KEY_CONCTROL_SELECTED_DEVICE_TYPE = "key_control_selected_device_type";
    public static final String KEY_CENTER_KEY_SEQID_LAST = "key_center_key_seqid_last";

    /**
     * 主要用于已绑定香薰灯用户，首次登陆
     */
    public static final String KEY_AROMA_LIGHT_DEVICE_FIRST_TIME = "aroma_light_device_first_time";

    /**
     * 选择设备列表的宽
     */
    public static final String KEY_IMAGE_SIZE = "key_image_size";

    /**
     * 主页nox设备引导框
     */
    public static final String KEY_HOME_GUIDE = "key_home_guide";

    /**
     * 京东APP包名
     */
    public static final String OUTSIDE_PLAT_JD = "com.jingdong.app.mall";
    /**
     * 淘宝APP包名
     */
    public static final String OUTSIDE_PLAT_TAOBAO = "com.taobao.taobao";

    /**
     * 天猫APP 包名
     */
    public static final String OUTSIDE_PLAT_TMALL = "com.tmall.wireless";

    /**
     * 发现商品
     */
    public static final String KEY_RECOMMEND_GOODS="key_recommend_goods";


    /**
     * 发现新闻
     */
    public static final String SP_KEY_LOCAL_NEWS = "sp_key_local_news";

    /**
     * 发现轮播图
     */
    public static final String SP_KEY_LOCAL_ADS = "sp_key_local_ads";

    /**
     * 只进入一次App视频播放
     */
    public static final String KEY_WELCOME_TO_VEDIO = "key_had_gone_to_vedio";

    /**
     * 微信登录时，点击广告页跳转记录
     */
    public static final String KEY_WEIXIN_AD = "weixin_login_ad";
    /**
    引导页面帧动画一帧持续的时长
     */
    public static final int NOVIGATION_ANIMATION_DURATION_200 = 200;
    public static final int NOVIGATION_ANIMATION_DURATION_500 = 500;

    /**
     * 微信平台状态码
     */
    public static final int PLATFORM_WEI_XIN=100;
    /**
     * facebook平台状态码
     */
    public static final int PLATFORM_FACE_BOOK=104;
    /**
     * 谷歌平台状态码
     */
    public static final int PLATFORM_GOOGLE=105;
}
