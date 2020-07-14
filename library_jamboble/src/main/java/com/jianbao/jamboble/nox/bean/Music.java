package com.jianbao.jamboble.nox.bean;


import android.util.Log;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by Hao on 2016/8/4.
 */

public class Music extends BaseBean {


    /**
     * 音乐类型
     */
    public MusicType musicType = MusicType.SLEEP_HELPER;
    /**
     * 循环模式
     * 0: 循环播放
     * 1: 不循环播放
     */
    public byte circle;
    /**
     * 音量
     */
    public byte voloume = 80;
    /**
     * 音乐来源,文档里面的音乐类型
     */
    public MusicFrom musicFrom;
    /**
     * 音乐结构
     */
    public MusicFromConfig musicFromConfig;

    /**
     * 音乐开关1开0关
     */
    public byte musicOpenFlag = 0;
    /**
     * 音乐播放位置，用于单曲之间播放的切换
     */
    public int playPostion;

    /**
     * 声光标识  -1 默认值  0 不是声光音乐  1 声光音乐
     */
    private byte soundLightFlag = -1;
    /**
     * 单曲当前播放位置,秒值
     */
    public short seek;
    /**
     * 0停止，1播放，2暂停，0xf缓冲
     */
    public PlayState playState;

    public enum PlayState {
        //        0停止，1播放，2暂停，0xf缓冲
        STOP((byte) 0), PLAYING((byte) 1), PAUSE((byte) 2), BUFFERING((byte) 0xf);
        public byte value;

        PlayState(byte value) {
            this.value = value;
        }

        public static PlayState value2State(byte value) {
            switch (value) {
                case 1:
                    return PLAYING;
                case 2:
                    return PAUSE;
                case 0xf:
                    return BUFFERING;
                default:
                    return STOP;
            }
        }

    }


    public byte getSoundLightFlag() {
        return soundLightFlag;
    }

    public void setSoundLightFlag(byte soundLightFlag) {
        this.soundLightFlag = soundLightFlag;
    }

    /**
     * 手势识别专辑的初始化类型
     */
    public GestureAlbumListType getstureAlbumListType;
    /**
     * 专辑图片地址，用作播放页面背景
     */
    public String albumImgUrl;

    public String trackImgUrl;
    /**
     * 专辑名称
     */
    public String albumName;

    public String description;

    /**
     * 是否是Sleepace音乐或自定义专辑，用于播放页面展示控制，Sleepace音乐相关信息都是通过后台配置，展示时也是显示的后台配置的信息。
     * 喜马拉雅音乐则通过喜马拉雅api展示相关信息
     * 注意：Sleepace自定义专辑中可能会有喜马拉雅专辑，但是展示时需要以Sleepace配置的信息为准。
     */
    public boolean isSleepaceMusic;

    /**
     * 教程自定义喜马拉雅音乐，有后台配置信息，以Sleepace配置信息展示，传递给固件的也是Sleepace配置信息
     */
    public boolean isCourseXimalaYaMusic;

    /**
     * 声音的播放方式
     */
    public PlayWay playWay = PlayWay.PHONE;

    /**
     * 音乐是否加载中
     */
    public boolean isLoading;


    public Music() {

    }

    public Music(Music music) {
        if (music != null) {
            musicType = music.musicType;
            circle = music.circle;
            voloume = music.voloume;
            musicFrom = music.musicFrom;
            if (music.musicFromConfig != null && music.musicFromConfig instanceof MusicFromConfigAlbum) {
                MusicFromConfigAlbum album = (MusicFromConfigAlbum) music.musicFromConfig;
                musicFromConfig = new MusicFromConfigAlbum(album.id, album.curMusicId, album.curPosition, album.playMode);
            } else {
                musicFromConfig = music.musicFromConfig;
            }

            musicOpenFlag = music.musicOpenFlag;
            playPostion = music.playPostion;
            soundLightFlag = music.soundLightFlag;
            getstureAlbumListType = music.getstureAlbumListType;
            albumImgUrl = music.albumImgUrl;
            trackImgUrl = music.trackImgUrl;
            albumName = music.albumName;
            description = music.description;
            isSleepaceMusic = music.isSleepaceMusic;
            playWay = music.playWay;
            isLoading = music.isLoading;
            isCourseXimalaYaMusic=music.isCourseXimalaYaMusic;
            seek=music.seek;
        }
    }


    /**
     * 填入音乐类型，音乐结构
     *
     * @param buffer
     */
    public void fillBuffer(ByteBuffer buffer) {
//        byte from = musicFrom == null ? -1 : musicFrom.value;
        //LogUtil.log(TAG+" fillBuffer musicFrom:" + from+",musicFromConfig:"+musicFromConfig+",circle:" + circle);

        if (musicFrom != null) {
            Log.e(TAG, "-----fillBuffer----musicFrom.value:  " + musicFrom.value);
            buffer.put(musicFrom.value);
        } else {
            Log.e(TAG, "  音乐来源类型未设置");
        }
        if (musicFromConfig != null) {
            musicFromConfig.fillBuffer(buffer);
        } else {
            Log.e(TAG, "  音乐结构未设置");
        }
        buffer.put(circle);
    }

    /**
     * 填入音乐类型，音乐结构
     *
     * @param buffer
     */
    public void parseBuffer(ByteBuffer buffer) {
        switch (musicFrom) {
            case LOCAL:
            case OTHER:
                musicFromConfig = new MusicFromConfigLocalOther();
                break;
            case CUSTOMIZED_XMLY_ALBUM:
            case  CUSTOMIZED_LOCAL:
                musicFromConfig = new MusicFromConfigCustomizedAlbumSingle();//新增加喜马拉雅-sleepace音乐结构
            case XMLY_ALBUM:
            case SLEEPACE_ALBUM:
                musicFromConfig = new MusicFromConfigAlbum();
                break;
            case XMLY_SINGLE:
                musicFromConfig = new MusicFromConfigXMLASingle();
                break;
            case SLEEPACE_SINGLE:
                musicFromConfig = new MusicFromConfigSleepaceSingle();
                break;
        }
        if (musicFromConfig != null) {
            musicFromConfig.parseBuffer(buffer);
        }
        circle = buffer.get();
    }

    public enum GestureAlbumListType {
        //表示列表为硬件初始列表
        DEVICE_INIT((byte) 0),
        //表示列表为APP设置的列表
        APP_INIT((byte) 1);
        public byte value;

        GestureAlbumListType(byte value) {
            this.value = value;
        }

        public static GestureAlbumListType value2Type(byte value) {
            switch (value) {
                case 0:
                    return DEVICE_INIT;
                default:
                    return APP_INIT;
            }
        }
    }

    /**
     * 是否是喜马拉雅音乐
     *
     * @return
     */
    public boolean isXMLYMusic() {
        return musicFrom == Music.MusicFrom.XMLY_ALBUM || musicFrom == Music.MusicFrom.XMLY_SINGLE || musicFrom== MusicFrom.CUSTOMIZED_XMLY_ALBUM ;
    }

    /**
     * 是否是喜马拉雅音乐
     *
     * @return
     */
    public static boolean isXMLYMusic(MusicFrom musicFrom) {
        return musicFrom == Music.MusicFrom.XMLY_ALBUM || musicFrom == Music.MusicFrom.XMLY_SINGLE;
    }

    /**
     * 音乐外放传输模式
     */
    public enum MusicOutputType {
        WIFI((byte) 0), BLUETOOTH((byte) 1);
        public byte value;

        MusicOutputType(byte value) {
            this.value = value;
        }

        public static MusicOutputType value2Type(byte value) {
            if (value == WIFI.value) {
                return WIFI;
            }
            return BLUETOOTH;
        }
    }


    public enum MusicFrom {
//        0xFF: 无音乐
//        0: 设备本地音乐
//        1: 外部音乐(DLNA、AirPlay)
//        2：喜马拉雅专辑
//        3：喜马拉雅单曲
//        4：Sleepace专辑
//        5：Sleepace单曲
//        6 :自定义专辑-喜马拉雅(喜马拉雅单曲组成的专辑)
//        7：自定义专辑-本地(设备本地音乐组成的专辑)
//        注意：可能会再细分，处理时注意扩展性

        EMPTY((byte) 0xFF), LOCAL((byte) 0), OTHER((byte) 1), XMLY_ALBUM((byte) 2), XMLY_SINGLE((byte) 3), SLEEPACE_ALBUM((byte) 4), SLEEPACE_SINGLE((byte) 5), CUSTOMIZED_XMLY_ALBUM((byte) 6), CUSTOMIZED_LOCAL((byte) 7);
        public byte value;

        MusicFrom(byte value) {
            this.value = value;
        }

        public static MusicFrom value2From(byte value) {
            switch (value) {
                case 0:
                    return LOCAL;
                case 1:
                    return OTHER;
                case 2:
                    return XMLY_ALBUM;
                case 3:
                    return XMLY_SINGLE;
                case 4:
                    return SLEEPACE_ALBUM;
                case 5:
                    return SLEEPACE_SINGLE;
                case 6:
                    return CUSTOMIZED_XMLY_ALBUM;
                case 7:
                    return CUSTOMIZED_LOCAL;
                default:
                    return EMPTY;
            }
        }

        public static MusicFrom getFromByChannelId(int channelId) {
            switch (channelId) {
                case Constants.MUSIC_CHANNEL_XIMALAYA :
                    return XMLY_ALBUM;
                case Constants.MUSIC_CHANNEL_SLEEPACE_XIMALAYA :
                    return CUSTOMIZED_XMLY_ALBUM;//喜马拉雅-sleepace专辑 实际上也是喜马拉雅专辑，只是通过音乐id获取的列表，音乐信息是isSleepaceMusic 。
                case Constants.MUSIC_CHANNEL_SLEEPACE:
                    return CUSTOMIZED_LOCAL;
                default:
                    return SLEEPACE_ALBUM;
            }
        }


        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    public static abstract class MusicFromConfig implements Serializable {

        public MusicFromConfig() {
        }

        public MusicFromConfig(int id) {
            this.id = id;
        }

        public int id;

        /**
         * 填入buffer
         *
         * @param buffer
         */
        public abstract void fillBuffer(ByteBuffer buffer);

        /**
         * 解析buffer
         *
         * @param buffer
         */
        public abstract void parseBuffer(ByteBuffer buffer);

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof MusicFromConfig)) {
                return false;
            }
            return this.id == ((MusicFromConfig) obj).id;
        }

        @Override
        public String toString() {
            return "id=" + id;
        }
    }

    /**
     * 本地音乐/外部音乐 结构
     */
    public static class MusicFromConfigLocalOther extends MusicFromConfig {

        /**
         * 本地音乐/外部音乐 结构
         */
        public MusicFromConfigLocalOther() {

        }

        public MusicFromConfigLocalOther(int id) {
            super(id);
        }

        @Override
        public void fillBuffer(ByteBuffer buffer) {
            buffer.putShort((short) id);
        }

        @Override
        public void parseBuffer(ByteBuffer buffer) {
            this.id = buffer.getShort();
        }
    }

    /**
     * 喜马拉雅单曲 结构
     */
    public static class MusicFromConfigXMLASingle extends MusicFromConfig {
        public MusicFromConfigXMLASingle() {

        }

        public MusicFromConfigXMLASingle(int id) {
            super(id);
        }

        @Override
        public void fillBuffer(ByteBuffer buffer) {
            buffer.putInt(id);
        }

        @Override
        public void parseBuffer(ByteBuffer buffer) {
            this.id = buffer.getInt();
        }
    }

    /**
     * 喜马拉雅/Sleepace 专辑结构
     */
    public static class MusicFromConfigAlbum extends MusicFromConfig {
        /**
         * 播放音乐的Id
         */
        private int curMusicId;
        /**
         * 播放音乐的序号，当前序号从0开始，表示该单曲在整个专辑中的位置 伪代码：index=n+page_num*page_size
         */
        public short curPosition;
        public MusicFromConfigAlbumPlayMode playMode = MusicFromConfigAlbumPlayMode.SEQUENCE;
        /**
         * sleepace专辑音乐列表，喜马拉雅音乐不用管
         */
        public List<MusicFromConfigSleepaceSingle> list;

        /**
         * 自定义专辑单曲(喜马拉雅/本地) 结构
         */
        public List<MusicFromConfigCustomizedAlbumSingle> customizedAlbumSingleTypeList;

        /**
         * 喜马拉雅/Sleepace 专辑结构
         */
        public MusicFromConfigAlbum() {

        }

        public int getCurMusicId() {
            return curMusicId;
        }

        public void setCurMusicId(int curMusicId) {
            this.curMusicId = curMusicId;
//            LogUtil.whereCall();
        }

        public enum MusicFromConfigAlbumPlayMode {
            /**
             * 0：顺序播放
             * 1: 随机播放
             * 2: 单曲播放
             */
            SEQUENCE((byte) 0), RANDOM((byte) 1), SINGLE((byte) 2);
            public byte value;

            MusicFromConfigAlbumPlayMode(byte value) {
                this.value = value;
            }

            public static MusicFromConfigAlbumPlayMode value2PlayMode(byte value) {
                switch (value) {
                    case 0:
                        return SEQUENCE;
                    case 1:
                        return RANDOM;
                    case 2:
                    default:
                        return SINGLE;
                }
            }

        }


        public MusicFromConfigAlbum(int albumTd, int curMusicId, short curPosition, MusicFromConfigAlbumPlayMode playMode) {
            this.id = albumTd;
            this.curMusicId = curMusicId;
            this.curPosition = curPosition;
            this.playMode = playMode;
        }

        @Override
        public void fillBuffer(ByteBuffer buffer) {
//            LogUtil.logE("Music MusicFromConfigAlbum  fillBuffer   填充的时候配置：" + toString());
            buffer.putInt(id);
            buffer.putInt(curMusicId);
            buffer.putShort(curPosition);
            buffer.put(playMode.value);
        }

        @Override
        public void parseBuffer(ByteBuffer buffer) {
            this.id = buffer.getInt();
            this.curMusicId = buffer.getInt();
            this.curPosition = buffer.getShort();
            this.playMode = MusicFromConfigAlbumPlayMode.value2PlayMode(buffer.get());
        }

        @Override
        public boolean equals(Object obj) {
            boolean flag = super.equals(obj);
            if (!flag) {
                return flag;
            }

            if (!(obj instanceof MusicFromConfigAlbum)) {
                return false;
            }

            MusicFromConfigAlbum albumConfig = (MusicFromConfigAlbum) obj;
            return curPosition == albumConfig.curPosition && curMusicId == albumConfig.curMusicId;
        }

        @Override
        public String toString() {
            return "MusicFromConfigAlbum{" + super.toString() +
                    ", curMusicId=" + curMusicId +
                    ", curPosition=" + curPosition +
                    ", playMode=" + playMode +
                    ", list=" + list +
                    ", customizedAlbumSingleTypeList=" + customizedAlbumSingleTypeList +
                    '}';
        }
    }

    /**
     * Sleepace单曲 结构
     */
    public static class MusicFromConfigSleepaceSingle extends MusicFromConfig {
        public MusicFromConfigSleepaceSingle() {

        }

        public enum MusicFromConfigSleepaceSingleType {
            /**
             * 0: 设备本地音乐
             * 1: 喜马拉雅流媒体音乐
             * 2：Sleepace流媒体音乐(暂无)
             */
            LOCAL((byte) 0), XMLA_STREAM((byte) 1), SLEEPACE_STREAM((byte) 2);
            public byte value;

            MusicFromConfigSleepaceSingleType(byte value) {
                this.value = value;
            }

            public static MusicFromConfigSleepaceSingleType value2Type(byte value) {
                switch (value) {
                    case 0:
                        return LOCAL;
                    case 1:
                        return XMLA_STREAM;
                    case 2:
                    default:
                        return SLEEPACE_STREAM;
                }
            }
        }


        MusicFromConfigSleepaceSingleType musicType;

        public MusicFromConfigSleepaceSingle(MusicFromConfigSleepaceSingleType musicType, int id) {
            this.id = id;
            this.musicType = musicType;
        }

        @Override
        public void fillBuffer(ByteBuffer buffer) {
            buffer.put(musicType.value);
            buffer.putInt(id);
        }

        @Override
        public void parseBuffer(ByteBuffer buffer) {
            this.musicType = MusicFromConfigSleepaceSingleType.value2Type(buffer.get());
            this.id = buffer.getInt();
        }

        @Override
        public String toString() {
            return "MusicFromConfigSleepaceSingle{" +
                    "musicType=" + musicType +
                    ", id=" + id +
                    '}';
        }
    }

    /**
     * 自定义专辑单曲(喜马拉雅/本地) 不需要传单曲类型给设备
     */
    public static class MusicFromConfigCustomizedAlbumSingle extends MusicFromConfig {

        public MusicFromConfigCustomizedAlbumSingle() {

        }


        public MusicFromConfigCustomizedAlbumSingle( int id) {
            this.id = id;
        }

        @Override
        public void fillBuffer(ByteBuffer buffer) {
            buffer.putInt(id);
        }

        @Override
        public void parseBuffer(ByteBuffer buffer) {
            this.id = buffer.getInt();
        }

        @Override
        public String toString() {
            return "MusicFromConfigSleepaceSingle{" +
                    ", id=" + id +
                    '}';
        }
    }




    /**
     * 调用该方法之前需要先给musicFrom赋值
     *
     * @param id
     * @return
     */
    public MusicFromConfig getMusicFromConfig(int id) {
        if (musicFrom != null) {
            switch (musicFrom) {
                case LOCAL:
                case OTHER:
                    return new MusicFromConfigLocalOther(id);
                case CUSTOMIZED_XMLY_ALBUM:
                case CUSTOMIZED_LOCAL:
                case XMLY_ALBUM:
                case SLEEPACE_ALBUM:
                    return new MusicFromConfigAlbum(id, 0, (short) 0, MusicFromConfigAlbum.MusicFromConfigAlbumPlayMode.SEQUENCE);
                case XMLY_SINGLE:
                    return new MusicFromConfigXMLASingle(id);
                case SLEEPACE_SINGLE:
                    return new MusicFromConfigSleepaceSingle(MusicFromConfigSleepaceSingle.MusicFromConfigSleepaceSingleType.LOCAL, id);

            }
        }
        return null;
    }


    /**
     * @author Simle
     * @date 2016年5月12日 上午11:04:13
     * @Description 音乐类型，分为助眠音乐，闹钟音乐，场景音乐
     */
    public enum MusicType {

        /**
         * 助眠音乐
         */
        SLEEP_HELPER((byte) 1),
        /**
         * 闹钟音乐
         */
        ALARM((byte) 2),
        /**
         * 场景音乐
         */
        SCENE((byte) 3),
        /**
         * 发现里面的喜马拉雅音乐
         */
        DISCOVERY((byte) 4),
        /**
         * 本地音乐即sleepace音乐
         */
        SLEEP_LOCAL((byte) 6),
        /**
         * 这个类型用于选择音乐时，退出选择模式需要关闭该类型音乐（其他场景或类型的音乐有在播放时，其他场景或类型的音乐不受影响）
         * Nox固件有做预览和退出预览模式，APP没有，所以需要该类型支持
         */
        TEMP((byte) 5);

        public byte value;

        MusicType(byte value) {
            this.value = value;
        }

        public static MusicType value2Type(byte value) {
            switch (value) {
                case 1:
                    return SLEEP_HELPER;
                case 2:
                    return ALARM;
                default:
                    return SCENE;
            }
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Music)) {
            Log.e(TAG, " eqs obj type err-------");
            return false;
        }

        Music m = (Music) o;

        if (m.musicOpenFlag != musicOpenFlag) {
            Log.e(TAG, " eqs openFlag-------");
            return false;
        }

        if (m.musicType != musicType) {
            Log.e(TAG, " eqs music type err-------");
            return false;
        }

        if (m.musicFrom != musicFrom) {
            Log.e(TAG, " eqs music from err-------");
            return false;
        }

        Log.e(TAG, " eqs musicFromConfig:" + musicFromConfig + ",m musicFromConfig:" + m.musicFromConfig);

        if ((musicFromConfig != m.musicFromConfig) || (musicFromConfig != null && !musicFromConfig.equals(m.musicFromConfig))) {
            return false;
        }

        return m.voloume == voloume;

//        if(m.xmlyTracks != null){
//            if (xmlyTracks == null) {
//                return false;
//            }
//
//            if (m.xmlyTracks.size() != xmlyTracks.size()) {
//                return false;
//            }
//
//            if (m.xmlyTracks.size() > 0 && m.xmlyTracks.get(0).getDataId() != xmlyTracks.get(0).getDataId()) {
//                return false;
//            }
//        }
    }

    @Override
    public String toString() {
        return "Music{" +
//                "hashCode=" + hashCode() +
                "musicType=" + musicType +
                ", circle=" + circle +
                ", voloume=" + voloume +
                ", musicFrom=" + musicFrom +
                //  ", xmlyTracks,size=" + xmlyTracks == null ? null : xmlyTracks.size() +
                ", musicOpenFlag=" + musicOpenFlag +
                ", albumName=" + albumName +
//                ", isSleepaceMusic=" + isSleepaceMusic +
//                ", albumImgUrl=" + albumImgUrl +
//                ", trackImgUrl=" + trackImgUrl +
                ", playWay=" + playWay +
                ", isLoading=" + isLoading +
                ", musicFromConfig=" + musicFromConfig +
                ", soundLightFlag=" + soundLightFlag +
                ", isCourseXimalaYaMusic="+isCourseXimalaYaMusic+
                ", seek=" + seek +
                '}';
    }

    /**
     * Created by wangyong on 2017.03.23
     * 声音（这里主要是专辑）的播放方式，手机播放，设备播放，其他待扩展
     * 设计这个主要用于Nox2蓝牙版，Sleepace音乐，设备内置音乐的播放逻辑判断，因为他们的渠道都是{@link Constants#MUSIC_CHANNEL_SLEEPACE}
     * 但是他们一个是手机播放，一个是设备播放。另外，有了这个标记，其他播放逻辑相关判断将会变得更加简单。
     */
    public enum PlayWay {
        PHONE,
        DEVICE
    }
}
