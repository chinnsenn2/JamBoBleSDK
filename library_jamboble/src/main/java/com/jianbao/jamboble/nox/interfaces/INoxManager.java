package com.jianbao.jamboble.nox.interfaces;

import com.jianbao.jamboble.nox.Device;
import com.jianbao.jamboble.nox.bean.CallbackData;
import com.jianbao.jamboble.nox.bean.NoxLight;
import com.jianbao.jamboble.nox.bean.NoxWorkMode;
import com.jianbao.jamboble.nox.bean.SceneConfig;

import java.util.List;

/**
 * Created by admin on 2016/8/24.
 */
public interface INoxManager extends ICentralManager {


    /**
     * 闹钟相关操作
     */
    class PostAlarmControl {
        /**
         * 闹钟停止使用
         */
        public final static byte STOP = 0x0000;
        /**
         * 闹钟开始启用
         */
        public final static byte START = 0x0001;
        /**
         * 贪睡
         */
        public final static byte LAYZY = 0x0002;
        /**
         * 预览
         */
        public final static byte PREVIEW = 0x0003;
        /**
         * 停止预览
         */
        public final static byte STOP_PREVIEW = 0x0004;
        /**
         * 删除闹钟(当前该闹钟在运行，则停止)
         */
        public final static byte DELETE = (byte) (0xE5 & 0xff);
        /**
         * 停用闹钟(当前该闹钟在运行，则停止)
         */
        public final static byte DISABLE = (byte) (0xE6 & 0xff);
        /**
         * 启用闹钟(只启用, 不开闹钟)
         */
        public final static byte ENABLE = (byte) (0xE7 & 0xff);

    }

    /**
     * 场景相关操作
     */
    class PostSceneControl {
        /**
         * 0x00: 关闭场景模式(当前不是该场景，则忽略)
         */
        public final static byte CLOSE = 0x00;
        /**
         * 打开该场景模式(已开此场景, 则忽略)
         */
        public final static byte OPEN = 0x01;
        /**
         * 重启(当前不是该场景，则打开)
         */
        public final static byte RESTART = 0x02;
        /**
         * 暂停(当前不是该场景运行，则忽略)
         */
        public final static byte PAUSE = 0x03;
        /**
         * 恢复(当前不是该场景暂停，则忽略)
         */
        public final static byte RESUME = 0x04;
        /**
         * 删除场景(当前是该场景，则停止)
         */
        public final static byte DELETE = (byte) 0xE5;
        /**
         * 停用场景(当前是该场景，则停止)
         */
        public final static byte DISABLE = (byte) 0xE6;
        /**
         * 启用场景(只启用,不打开)
         */
        public final static byte ENABLE = (byte) 0xE7;

    }

    /**
     * 灯相关操作
     */
    class PostLightControl {
        /**
         * 关灯
         */
        public final static byte CLOSE = 0x00;
        /**
         * 开灯
         */
        public final static byte OPEN = 0x01;
        /**
         * 亮度调节(开灯后有效，只使用亮度字段)
         */
        public final static byte BRIGHTNESS_CONTROL = 0x02;
    }

    class PostMusicControl {
        /**
         * 停止(停止后播放为从头播放)
         */
        public final static byte STOP = 0x00;
        /**
         * 播放
         */
        public final static byte START = 0x01;
        /**
         * 暂停(暂停后播放为继续播放)
         */
        public final static byte PAUSE = 0x02;
        /**
         * 音量调节(播放时有效，只使用音量字段)
         */
        public final static byte VOLOUM = 0x03;
        /**
         * 播放模式调节(播放时有效，只使用音量字段)
         */
        public final static byte PLAYMODE = 0x04;
        /**
         * 播放位置调节
         */
        public final static byte SEEKTO = 0x05;
    }

    class PostSleepAidControl {

        //        0x00:正常操作,如果辅助已经停止，设备会用后续参数会重新打开睡眠辅助
        public final static byte CONTROL = 0x00;
        //        0x01:重新开启,会停止当前辅助流程，用后续参数开启睡眠辅助
        public final static byte RESTAET = 0x01;
        //        0x02:停止助眠
        public final static byte STOP = 0x02;
        //        0x03:暂停助眠
        public final static byte PAUSE = 0x03;
        //        0x04:恢复助眠
        public final static byte RESUME = 0x04;
        //        0x05:缓慢停止辅助,缓慢停止辅助用于检测到睡着后调用，防止惊醒用户
        public final static byte LOWLY_STOP = 0x05;

        //后续控制指令,第三位，第四位
//        灯开关 byte	0: 关 1: 开 0xFF: 保持原状态
        public final static byte CMD_LIGHT_CLOSE = 0x00;
        public final static byte CMD_LIGHT_OPEN = 0x01;
        public final static byte CMD_LIGHT_STAY = (byte) 0xFF;

        //        音乐开关	byte	0: 停止 1: 暂停 2: 播放 0xFF: 保持原状态
        public final static byte CMD_MUSIC_STOP = 0x00;
        public final static byte CMD_MUSIC_PAUSE = 0x01;
        public final static byte CMD_MUSIC_START = 0x01;
        public final static byte CMD_MUSIC_STAY = (byte) 0xFF;
    }

    class PreviewOperFlag {
        public final static byte EXIT_PREVIEW = 0x00;
        public final static byte ENTER_PREVIEW = 0x01;
    }

    class PreviewExitFlag {
        public final static byte EXIT = 0x00;
        public final static byte SAVE_EXIT = 0x01;
    }

    class PreviewRebootFlag {
        public final static byte UNREBOOT = 0x00;
        public final static byte REBOOT = 0x01;
    }

    /**
     * 预助眠操作模式
     */
    enum SleepAidCtrlMode {
        //        0: 普通(普通，用于固件不需要特殊处理的操作)
        COMMON(0),
        //1: 照明(存储照明灯颜色，并决定是否开启小夜灯)
        LIGHT(1),
        //2: 助眠(决定是否开启/关闭助眠)
        SLEEPAID(2),
        //3: 预助眠(让设备知晓并存储，不进行助眠操作)
        PRE_SLEEPAID(3);
        public byte value;

        SleepAidCtrlMode(int value) {
            this.value = (byte) value;
        }

        public static SleepAidCtrlMode fromValue(byte value) {
            switch (value) {
                case 1:
                    return LIGHT;
                case 2:
                    return SLEEPAID;
                case 3:
                    return PRE_SLEEPAID;
                default:
                    return COMMON;

            }
        }



    }

    /**
     * 香薰灯相关操作
     */
    class AromatherapyControl {
        /**
         * 关
         */
        public final static byte CLOSE = 0x00;
        /**
         * 开
         */
        public final static byte OPEN = 0x01;
    }

    /**
     * 香薰操作速度
     */
    enum AromatherapySpeed {
        //0:关闭
        CLOSE(0),
        //1: 慢
        SLOW(1),
        //2: 一般
        COMMON(2),
        //3:快
        FAST(3),
        //3:原状态
        STAY(0xff);

        public byte value;

        AromatherapySpeed(int value) {
            this.value = (byte) value;
        }

        public static AromatherapySpeed fromValue(byte value) {
            switch (value) {
                case 0:
                    return CLOSE;
                case 1:
                    return SLOW;
                case 2:
                    return COMMON;
                case 3:
                    return FAST;
                default:
                    return STAY;

            }
        }
    }

    /**
     * 香薰操作模式
     */
    enum AromatherapyTimeCmd {
        //      0x00: 无效
        INVALID(0),
        //0x01: 删除所有(无后续参数)
        DELETE_ALL(1),
        //0x02: 删除一个(后续跟时间)
        DELETE_ONE(2),
        //0x03: 修改列表(数目>=1) 有则改，无则加
        UPDATE_LIST(3),
        //0x04: 设置列表(数目>=1)
        ADD_LIST(4),
        //0x05: 启用(后续跟时间)
        ENABLE_ONE(5),
        //0x06: 停用(后续跟时间)
        DISABLE_ONE(6);
        public byte value;

        AromatherapyTimeCmd(int value) {
            this.value = (byte) value;
        }
    }

    /**
     * 服务网络类型
     */
    enum ServerNetType {
        // 0x00: 局域网/广域网同时控制（目前采用这种）
        WAN_LAN(0),
        //0x01: 单独局域网控制
        LAN(1),
        //0x02: 单独广域网控制
        WAN(2);


        public byte value;

        ServerNetType(int value) {
            this.value = (byte) value;
        }
    }

    /**
     * 回调类型，灯光预览
     */
    int TYPE_METHOD_LIGHT_PREVIEW = 7000;
    /**
     * 回调类型，灯光打开
     */
    int TYPE_METHOD_LIGHT_OPEN = 7001;
    /**
     * 回调类型，灯光关闭
     */
    int TYPE_METHOD_LIGHT_CLOSE = 7002;

    /**
     * 回调类型，灯光设置
     */
    int TYPE_METHOD_LIGHT_BRIGHTNESS_SET = 7003;
    /**
     * 回调类型，手势设置
     */
    int TYPE_METHOD_GESTURE_SET = 7004;
    /**
     * 回调类型，蓝牙播放模式控制设置
     */
    int TYPE_METHOD_BLUETOOTH_PLAY_MODE_CTL = 7005;
    /**
     * 回调类型，小夜灯设置
     */
    int TYPE_METHOD_LIGHT_NIGHT_SET = 7006;

    /**
     * 回调类型，手势控制灯光列表
     */
    int TYPE_METHOD_GESTURE_LIGHT_LIST_SET = 7007;
    /**
     * 回调类型，手势控制专辑列表
     */
    int TYPE_METHOD_GESTURE_ALBUM_LIST_SET = 7008;
    /**
     * 回调类型，手势控制专辑列表
     */
    int TYPE_METHOD_GESTURE_ALBUM_LIST_GET = 7009;
    /**
     * 回调类型，声光教程设置
     */
    int TYPE_METHOD_LIGHT_SOUND_TUTORIAL = 7010;
    /**
     * 回调类型，香薰开
     */
    int TYPE_METHOD_AROMATHERERAPY_START = 7011;
    /**
     * 回调类型，香薰关
     */
    int TYPE_METHOD_AROMATHERERAPY_STOP = 7012;
    /**
     * 回调类型，中控按钮功能设置
     */
    int TYPE_METHOD_BUTTON_FUNCTION_SET = 7013;
    /**
     * 回调类型，香薰定时器设置
     */
    int TYPE_METHOD_AROMATHERERAPY_TIMER_SET = 7014;
    /**
     * 回调类型，一键开启
     */
    int TYPE_METHOD_ONE_KEY_OPEN = 7015;
    /**
     * 回调类型，一键关闭
     */
    int TYPE_METHOD_ONE_KEY_CLOSE = 7016;

    /*
    * 回调类型，香薰开关开
    * */
    int TYPE_METHOD_AROMA_LIGHT_OPEN = 7015;

    /*
    * 回调类型，香薰开关关
    * */
    int TYPE_METHOD_AROMA_LIGHT_CLOSE = 7016;
    /*
    * 回调类型，设备网络类型设置
    * */
    int TYPE_METHOD_NET_SET = 7017;

    /*
       * 回调类型，获取设备日记
       * */
    int TYPE_METHOD_LOG_GET = 7018;
    /**
     * 预览模式操作
     * @param operation  预览操作，参考{@link PreviewOperFlag}
     * @param exitFlag   退出标志，参考{@link PreviewExitFlag}
     * @param rebootFlag 重启标志，参考{@link PreviewRebootFlag}
     */
    void preview(byte operation, byte exitFlag, byte rebootFlag);

    /**
     * 打开灯光 回调类型 TYPE_METHOD_LIGHT_SET,Result为boolean
     *
     * @param noxLight 灯光结构
     */
    void lightOpen(NoxLight noxLight);

    /**
     * 灯光颜色设置
     *
     * @param noxLight 灯光结构
     */
    void lightColorSet(NoxLight noxLight);

    /**
     * 关闭灯光 回调类型 TYPE_METHOD_LIGHT_CLOSE,Result为boolean
     */
    void lightClose(NoxLight light);

    /**
     * 灯光亮度关设置，回调类型：{@link #TYPE_METHOD_LIGHT_BRIGHTNESS_SET}
     *
     * @param light
     */
    void lightBrightness(NoxLight light);


    /**
     * 设置监测设备
     *
     * @param monitorDevice
     */
    void setMonitorDevice(Device monitorDevice);

    /**
     * 蓝牙播放模式Play控制 TYPE_METHOD_BLUETOOTH_PLAY_MODE_CTL
     *
     * @param open true打开，false 关闭
     */
    void bluthoothPlayModeControl(boolean open);

    /**
     * 小夜灯设置 TYPE_METHOD_LIGHT_NIGHT_SET
     *
     * @param light 小夜灯
     */
    void lightNightSet(NoxLight light);

    CallbackData lightNightSetSync(final NoxLight light);

    CallbackData sceneConfigSetSync(SceneConfig config);

    /**
     * 手势控制灯光列表设置 TYPE_METHOD_GESTURE_LIGHT_LIST_SET
     *
     * @param lights 灯光列表
     */
    void gestureLightListSet(List<NoxLight> lights);


    /**
     * 手势控制专辑列表获取 TYPE_METHOD_GESTURE_ALBUM_LIST_GET
     */
    void gestureAlbumListGet();
    /**
     * 升级进度查询
     */
    CallbackData updateStatusGet();

    /**
     * 声光教程设置  TYPE_METHOD_LIGHT_SOUND_TUTORIAL
     *
     * @param path
     */
    void lightSoundTutorialSet(String path);

    /**
     * 香薰开 TYPE_METHOD_AROMATHERERAPY_START
     *
     * @param mode
     * @param speed
     */
    void aromatherapyStart(SleepAidCtrlMode mode, AromatherapySpeed speed);

    /**
     * 香薰关  TYPE_METHOD_AROMATHERERAPY_STOP
     *
     * @param mode
     */
    void aromatherapyStop(SleepAidCtrlMode mode);

    /**
     * 中心按钮控制  TYPE_METHOD_BUTTON_FUNCTION_SET
     *
     * @param isLightStart        是否开启灯
     * @param isMusticStart       是否开启音乐
     * @param isAromatherapyStart 是否开启香薰
     */
    void buttonFuctionSet(boolean isLightStart, boolean isMusticStart, boolean isAromatherapyStart);

    /**
     * 一键开 TYPE_METHOD_ONE_KEY_OPEN
     *
     * @param mode
     * @param speed
     * @param light
     */
    void onekeyOpen(SleepAidCtrlMode mode, AromatherapySpeed speed, NoxLight light);

    /**
     * 一键关  TYPE_METHOD_ONE_KEY_CLOSE
     *
     * @param mode
     */
    void onekeyClose(SleepAidCtrlMode mode);

    /**
     * 设备联网控制方式设置  TYPE_METHOD_NET_SET
     *
     * @param type
     * @param ip
     * @param port
     * @param httpHost
     */
    void netSet(ServerNetType type, String ip, int port, String httpHost);

    /**
     * 设备联网控制方式设置  TYPE_METHOD_NET_SET
     * 默认局域网和广域网一起，ip和端口自动获取
     */
    void netSet();


    /*恢复出厂设置
    * */
//    void recoveryMode(RecoveryMode mode);


    void workModeGetSyn();

    NoxWorkMode getCacheWorkWork();
}
