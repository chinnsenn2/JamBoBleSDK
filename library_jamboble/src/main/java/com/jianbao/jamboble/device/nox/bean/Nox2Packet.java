package com.jianbao.jamboble.device.nox.bean;


import android.text.TextUtils;

import com.jianbao.jamboble.device.nox.DeviceType;
import com.jianbao.jamboble.device.nox.interfaces.INoxManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;

import static com.jianbao.doctor.bluetooth.device.nox.bean.Nox2Packet.OperateType.SET;


public class Nox2Packet extends DataPacket {

    /**
     * 操作类型
     */
    public static class OperateType {
        public static final byte GET = 0x00;
        public static final byte SET = 0x01;
    }

    public static class PacketMsgType {
        public static final byte HEARTBEAT = 0x00;

        public static final byte TIME_SYNC = 0x10; // 时间校准
        public static final byte DEVICE_INFO = 0x11; // 系统信息
        public static final byte DEVICE_VER = 0x12; // 版本信息
        public static final byte RECOVERY = 0x13; // 恢复出厂设置

        // 配置类
        public static final byte LOGIN = 0x20; // 用户信息
        public static final byte SCENE_CFG = 0x21; // 场景配置
        public static final byte SET_ALARM_CFG = 0x23; // 闹钟配置
        public static final byte SET_GESTURE = 0x24; // 手势配置
        public static final byte SET_WIFI_INFO = 0x25; // WIFI配置
        public static final byte SET_MUSIC_LIST = 0x27; // 专辑声音列表配置
        public static final byte SET_LIGHT_NIGHT = 0x26; // 小夜灯配置
        public static final byte SET_GESTURE_COLOR_LIST = 0x28; // 设备挥手颜色列表配置
        public static final byte SET_MUSIC_OUTPUT_TYPE = 0x29; //音乐外放传输模式
        public static final byte SET_GESTURE_ALBUM_LIST = 0x2A; //手势切换专辑列表设置
        public static final byte SET_LIGHT_SOUND_TUTORIAL = 0x2B; //声光教程设置
        public static final byte SET_BUTTON_FUNCTION = 0x2C; //中心按钮功能设置
        public static final byte SET_AROMATHERAY_TIMER = 0x2D; //香薰定时器设置
        public static final byte SET_NET = 0x2E; //网络控制方式设置
        /**
         * 灯相关操作
         */
        public static final byte CMD_LIGHT = 0x30;
        /**
         * 场景相关操作
         */
        public static final byte CMD_SCENE = 0x32;
        /**
         * 助眠相关操作
         */
        public static final byte CMD_SLEEP_AID = 0x33;
        /**
         * 音乐相关操作
         */
        public static final byte CMD_MUSIC = 0x31;
        /**
         * 闹钟相关操作
         */
        public static final byte CMD_ALARM = 0x34;
        /**
         * 预览操作
         */
        public static final byte CMD_PREVIEW = 0x35;
        /**
         * 蓝牙播放模式控制操作
         */
        public static final byte CMD_BLUETOOTH_PLAY_MODE_CTL = 0x36;

        /**
         * 香薰操作
         */
        public static final byte CMD_AROMATHERAPY = 0x37;
        /**
         * 一键操作
         */
        public static final byte CMD_ONE_KEY = 0x38;
        // 查询类

        public static final byte WORK_MODE_QUERY = 0x41; // 工作模式查询
        public static final byte WIFI_STATE_QUERY = 0x43; // WIFI状态查询
        public static final byte UPDATE_STATE_QUERY = 0x4F; // 升级状态查询
        public static final byte MUSIC_PLAY_STATE_QUERY = 0x45; // 音乐播放状态查询

        public static final byte WIFI_IP_QUERY = 0x4E; // WIFI信息查询
        public static final byte UPDATE_SUMM_DATA = 0x52; // 升级概要数据
        public static final byte UPDATE_DETAIL_DATA = 0x53; // 升级明细数据
        public static final byte NOX2W_UPDATE_FIREWARE_DATA = 0x51; //Nox2W固件升级信息
        public static final byte LOG_GET = 0x5F; //日志获取
    }


    public static class ErrType {
        public static final byte ERR_CODE_OK = 0x00;
        public static final byte ERR_TYPE = 0x01;
        public static final byte ERR_PARAM = 0x02;
        public static final byte ERR_NOTLOGIN = 0x03;
        public static final byte ERR_NODATA = 0x04;
        public static final byte ERR_MODEL = 0x05;
        public static final byte ERR_UPDATE_CHECK = 0x06;
        public static final byte ERR_DEV_INFO = 0x07;
        public static final byte ERR_SCENE_NOT_FOUND = 0x08;

        public static final byte ERR_UNKNOWN = (byte) 0xFF; // 未知错误
    }


    public Nox2Packet() {
        this.buffer = ByteBuffer.allocate(1 * 1024);
        this.buffer.order(ByteOrder.BIG_ENDIAN);
    }

    public Nox2Packet(int size) {
        this.buffer = ByteBuffer.allocate(size);
        this.buffer.order(ByteOrder.BIG_ENDIAN);
    }

    public boolean check() {
        return check(this.buffer);
    }

    @Override
    public boolean check(ByteBuffer buffer) {
        if (buffer != null) {
            CRC32 crc32 = new CRC32();
            crc32.update(buffer.array(), 0, buffer.limit() - 8);
            return (int) (crc32.getValue() & 0xFFFFFFFF) == buffer.getInt(buffer.limit() - 8);
        }
        return false;
    }

    @Override
    public boolean parse(ByteBuffer buffer) {
        if (buffer != null) {
            parseBuffer(buffer);
            return true;
        }
        return false;
    }

    public ByteBuffer parseBuffer(ByteBuffer buffer) {
        this.head = new Nox2PacketHead();
        this.msg = new Nox2PacketBody();
        this.head.parseBuffer(buffer);

        if (this.msg.parseBuffer(this.head, buffer) == null)
            return null;

        this.crc32 = buffer.getInt(buffer.limit() - 4);
        return buffer;
    }

    @Override
    public boolean fill(byte btType, byte btSeq) {
        this.head = new Nox2PacketHead(btType, btSeq);
        buffer.position(0);
        fillBuffer(this.buffer);

        CRC32 crc32 = new CRC32();
        crc32.update(buffer.array(), 0, buffer.position());
        this.crc32 = (int) (crc32.getValue() & 0xFFFFFFFF);
        buffer.putInt(this.crc32);
        buffer.put(new byte[]{0x24, 0x5F, 0x40, 0x2D});
        buffer.limit(buffer.position());
        return true;
    }

    @Override
    public ByteBuffer fillBuffer(ByteBuffer buffer) {
        buffer.position(0);
        this.head.fillBuffer(buffer);
        this.msg.fillBuffer(this.head, buffer);
        return buffer;
    }

    public static class Nox2PacketHead extends PacketHead {
        public static final int SIZE = 1 + 1 + 1 + 1 + 1 + 2;


        public Nox2PacketHead() {
        }

        public Nox2PacketHead(byte btType, byte btSeq) {
            init(btType, btSeq);
        }

        public void init(byte type, byte senquence) {
            this.version = VER;
            this.type = type;
            this.btCount = 1;
            this.btIndex = 0;
            this.senquence = senquence;
            this.deviceType = DeviceType.DEVICE_TYPE_NOX_2B;
        }

        public ByteBuffer parseBuffer(ByteBuffer buffer) {
            buffer.position(0);
            this.version = buffer.get();
            this.type = buffer.get();
            this.btCount = buffer.get();
            this.btIndex = buffer.get();
            this.senquence = buffer.get();
            this.deviceType = buffer.getShort();
            return buffer;
        }

        public ByteBuffer fillBuffer(ByteBuffer buffer) {
            buffer.position(0);
            buffer.put(VER);
            buffer.put(this.type);
            buffer.put(this.btCount);
            buffer.put(this.btIndex);
            buffer.put(this.senquence);
            buffer.putShort(this.deviceType);
            return buffer;
        }
    }

    public static class Nox2PacketBody extends PacketBody {

        public Nox2PacketBody() {
            super();
        }

        public Nox2PacketBody(byte type, BasePacket content) {
            super(type, content);
        }

        public ByteBuffer parseBuffer(PacketHead head, ByteBuffer buffer) { // 解析数据类型
            this.type = buffer.get();
//            LogUtil.logE("解析NOX2数据：头类型：" + Integer.toHexString(head.type) + "   数据类型：" + Integer.toHexString(type));
            if (head.type == PacketType.FA_ACK) {
                this.content = new BaseRspPack();
            } else if (head.type == PacketType.FA_RESPONSE || head.type == PacketType.FA_POST) {
                switch (this.type) {
                    case PacketMsgType.DEVICE_INFO:
                        this.content = new DeviceInfoRsp();
                        break;
                    case PacketMsgType.LOGIN:
                        this.content = new UserCfgRsp();
                        break;
                    case PacketMsgType.WORK_MODE_QUERY:
                        this.content = new WorkModeRsp(head);
                        break;
                    case PacketMsgType.WIFI_STATE_QUERY:
                        this.content = new WifiStatusGetRsp();
                        break;
                    case PacketMsgType.UPDATE_STATE_QUERY:
                        this.content = new UpdateStatusRsp();
                        break;
                    case PacketMsgType.WIFI_IP_QUERY:
                        this.content = new WifiIpGetRsp();
                        break;
                    default:
                        this.content = new BaseRspPack();
                        break;
                }
            } else {
                return null;
            }
            try {
                this.content.parseBuffer(buffer);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return buffer;
        }

        public ByteBuffer fillBuffer(PacketHead head, ByteBuffer buffer) {
            buffer.put(this.type);
            content.fillBuffer(buffer);
            return buffer;
        }
    }

    public static class DeviceInfo extends BasePacket {
        public byte[] xDeviceName = new byte[14];
        public byte[] xDeviceId = new byte[14];
        public short usModel;
        public short usVerCode;
        public short usFactoryVerCode;
        public int ulFactoryTime;
        public short usFactoryNum;
        public short usCoopNum;
        public short pcbCode;
        public byte[] addr = new byte[6];

        public String deviceName;
        public String deviceId;
        public String versionName;
        public String btAddress;

        public DeviceInfo() {
        }

        public DeviceInfo(byte[] xDeviceName, byte[] xDeviceId, short usModel, short usVerCode, short usFactoryVerCode, int ulFactoryTime,
                          short usFactoryNum, short usCoopNum, short pcbCode) {
            this.xDeviceName = xDeviceName;
            this.xDeviceId = xDeviceId;
            this.usModel = usModel;
            this.usVerCode = usVerCode;
            this.usFactoryVerCode = usFactoryVerCode;
            this.ulFactoryTime = ulFactoryTime;
            this.usFactoryNum = usFactoryNum;
            this.usCoopNum = usCoopNum;
            this.pcbCode = pcbCode;
        }

        @Override
        public ByteBuffer parseBuffer(ByteBuffer buffer) {
            buffer.get(this.xDeviceName);
            buffer.get(this.xDeviceId);
            this.usModel = buffer.getShort();
            this.usVerCode = buffer.getShort();
            this.usFactoryVerCode = buffer.getShort();
            this.ulFactoryTime = buffer.getInt();
            this.usFactoryNum = buffer.getShort();
            this.usCoopNum = buffer.getShort();
            this.pcbCode = buffer.getShort();
            buffer.get(addr);

            this.deviceName = new String(xDeviceName).trim();
            this.deviceId = new String(xDeviceId).trim();
            this.versionName = String.format("%d.%02d", usVerCode / 100, usVerCode % 100);
            this.btAddress = String.format("%02X:%02X:%02X:%02X:%02X:%02X", addr[5] & 0xFF, addr[4] & 0xFF, addr[3] & 0xFF, addr[2] & 0xFF, addr[1] & 0xFF, addr[0] & 0xFF);

            return buffer;
        }

        @Override
        public ByteBuffer fillBuffer(ByteBuffer buffer) {
            return buffer;
        }

        @Override
        public String toString() {
            return "DeviceInfo{" +
                    "xDeviceName=" + Arrays.toString(xDeviceName) +
                    ", xDeviceId=" + Arrays.toString(xDeviceId) +
                    ", deviceType=" + usModel +
                    ", usVerCode=" + usVerCode +
                    ", usFactoryVerCode=" + usFactoryVerCode +
                    ", ulFactoryTime=" + ulFactoryTime +
                    ", usFactoryNum=" + usFactoryNum +
                    ", usCoopNum=" + usCoopNum +
                    ", pcbCode=" + pcbCode +
                    ", addr=" + Arrays.toString(addr) +
                    ", deviceName='" + deviceName + '\'' +
                    ", deviceId='" + deviceId + '\'' +
                    ", versionName='" + versionName + '\'' +
                    ", btAddress='" + btAddress + '\'' +
                    '}';
        }
    }

    public static class DeviceInfoRsp extends BaseRspPack {

        public DeviceInfo xDeviceInfo;

        public DeviceInfoRsp() {
        }

        @Override
        public ByteBuffer parseBuffer(ByteBuffer buffer) {
            rspCode = buffer.get();
            if (rspCode == ErrType.ERR_CODE_OK) {
                this.xDeviceInfo = new DeviceInfo();
                this.xDeviceInfo.parseBuffer(buffer);
            }
            return buffer;
        }

        @Override
        public String toString() {
            return "DeviceInfoRsp{" +
                    "xDeviceInfo=" + xDeviceInfo +
                    '}';
        }
    }

    public static class WifiIpGetRsp extends BaseRspPack {
        public String ipPort;

        @Override
        public ByteBuffer parseBuffer(ByteBuffer buffer) {
            rspCode = buffer.get();
            byte[] ipBytes = new byte[32];
            buffer.get(ipBytes);
            ipPort = new String(ipBytes);
            return buffer;
        }
    }



    public static class GetDevVerInfoRsp extends BaseRspPack {

        public short usVerCode;
        public String deviceVersion;

        public GetDevVerInfoRsp() {
        }

        public ByteBuffer parseBuffer(ByteBuffer buffer) {
            rspCode = buffer.get();
            if (rspCode == ErrType.ERR_CODE_OK) {
                this.usVerCode = buffer.getShort();
                deviceVersion = String.format("%d.%02d", usVerCode / 100, usVerCode % 100);
            }
            return buffer;
        }
    }

    public static class WeekBase {
        public boolean bMON;
        public boolean bTUE;
        public boolean bWED;
        public boolean bTHU;
        public boolean bFRI;
        public boolean bSTA;
        public boolean bSUN;

        public WeekBase() {
        }

        public WeekBase parse(byte btWeek) {
            bMON = ((btWeek & (0x01 << 0)) != 0);
            bTUE = ((btWeek & (0x01 << 1)) != 0);
            bWED = ((btWeek & (0x01 << 2)) != 0);
            bTHU = ((btWeek & (0x01 << 3)) != 0);
            bFRI = ((btWeek & (0x01 << 4)) != 0);
            bSTA = ((btWeek & (0x01 << 5)) != 0);
            bSUN = ((btWeek & (0x01 << 6)) != 0);

            return this;
        }

        public byte getByte() {
            byte btVal = 0;

            btVal |= (byte) (bMON ? (0x01 << 0) : 0);
            btVal |= (byte) (bTUE ? (0x01 << 1) : 0);
            btVal |= (byte) (bWED ? (0x01 << 2) : 0);
            btVal |= (byte) (bTHU ? (0x01 << 3) : 0);
            btVal |= (byte) (bFRI ? (0x01 << 4) : 0);
            btVal |= (byte) (bSTA ? (0x01 << 5) : 0);
            btVal |= (byte) (bSUN ? (0x01 << 6) : 0);

            return btVal;
        }

        public String toString() {
            return (this.bMON ? "1" : "0") + "," + (this.bTUE ? "1" : "0") + "," + (this.bWED ? "1" : "0") + "," + (this.bTHU ? "1" : "0") + ","
                    + (this.bFRI ? "1" : "0") + "," + (this.bSTA ? "1" : "0") + "," + (this.bSUN ? "1" : "0");
        }
    }

    public static class SleepCfg extends BasePacket {
        public byte btHour;
        public byte btMin;
        public short usCount;
        public WeekBase xWeek;

        public SleepCfg() {
        }

        public SleepCfg(byte btHour, byte btMin, short usCount, WeekBase xWeek) {
            this.btHour = btHour;
            this.btMin = btMin;
            this.usCount = usCount;
            this.xWeek = xWeek;
        }

        @Override
        public ByteBuffer parseBuffer(ByteBuffer buffer) {
            this.btHour = buffer.get();
            this.btMin = buffer.get();
            this.usCount = buffer.getShort();

            this.xWeek = new WeekBase();
            this.xWeek.parse(buffer.get());
            return buffer;
        }

        @Override
        public ByteBuffer fillBuffer(ByteBuffer buffer) {
            buffer.put(this.btHour);
            buffer.put(this.btMin);
            buffer.putShort(this.usCount);
            buffer.put(this.xWeek.getByte());
            return buffer;
        }
    }

    public static class LightNightReq extends BasePacket {
        NoxLight light;

        public LightNightReq(NoxLight light) {
            if (light == null) {
                throw new RuntimeException("LightNightReq 灯光参数为空，无法初始化");
            }
            this.light = light;
        }

        @Override
        public ByteBuffer fillBuffer(ByteBuffer buffer) {
            buffer.put(SET);
            buffer.put(light.lightFlag);
            buffer.put(light.brightness);
            buffer.put(light.r);
            buffer.put(light.g);
            buffer.put(light.b);
            buffer.put(light.w);
            buffer.put(light.startHour);
            buffer.put(light.startMinute);
            buffer.putShort(light.getContinueTime());
            return buffer;
        }
    }

    public static class Nox2WUpdateFirewareReq extends BasePacket {

        short curVerCode;
        short updateVerCode;
        byte updateType;
        String fileUrl;

        public Nox2WUpdateFirewareReq(short curVerCode, short updateVerCode, byte updateType, String fileUrl) {
            this.curVerCode = curVerCode;
            this.updateType = updateType;
            this.updateVerCode = updateVerCode;
            this.fileUrl = fileUrl;
        }

        @Override
        public ByteBuffer fillBuffer(ByteBuffer buffer) {
            buffer.putShort(curVerCode);
            buffer.putShort(updateVerCode);
            buffer.put(updateType);
            buffer.put(fileUrl.getBytes());
            return super.fillBuffer(buffer);
        }
    }

    public static class GestureColorListReq extends BasePacket {
        List<NoxLight> lights;

        public GestureColorListReq(List<NoxLight> lights) {
            if (lights == null) {
                throw new RuntimeException("GestureColorListReq 灯光列表参数为空，无法初始化");
            }
            this.lights = lights;
        }


        @Override
        public ByteBuffer fillBuffer(ByteBuffer buffer) {
            buffer.put(SET);
            buffer.put((byte) lights.size());
            for (NoxLight light : lights) {
                light.fillLightMode(buffer);
            }
            return super.fillBuffer(buffer);
        }
    }


    public static class WifiStatusGetRsp extends BaseRspPack {
        //        0x00:未连接
//        0x01:正在连接
//        0x02:已连接
        public byte status;

        public WifiStatusGetRsp() {
        }

        @Override
        public String toString() {
            return "WifiStatusGetRsp{" +
                    "status=" + status +
                    '}';
        }

        @Override
        public ByteBuffer parseBuffer(ByteBuffer buffer) {
            rspCode = buffer.get();
            if (rspCode == ErrType.ERR_CODE_OK) {
                this.status = buffer.get();
            }

            return buffer;
        }
    }

    public static class WifiInfo extends BasePacket {
        public byte[] szSSID;
        public byte btPskMode;
        public byte[] szPsk;
        public byte btIPMode;
        public byte[] btIP;
        public byte[] btMask;
        public byte[] btGWIP;
        public byte[] btDNS1;
        public byte[] btDNS2;

        public WifiInfo() {
        }

        public WifiInfo(String szSSID, String szPsk) {
            this.szSSID = szSSID.getBytes();
            if (TextUtils.isEmpty(szPsk)) {
                this.btPskMode = 0;
            } else {
                this.btPskMode = 1;
            }

            this.szPsk = szPsk.getBytes();
            this.btIPMode = 0;
            this.btIP = new byte[]{0, 0, 0, 0};
            this.btMask = new byte[]{0, 0, 0, 0};
            this.btGWIP = new byte[]{0, 0, 0, 0};
            this.btDNS1 = new byte[]{0, 0, 0, 0};
            this.btDNS2 = new byte[]{0, 0, 0, 0};
        }

        @Override
        public ByteBuffer parseBuffer(ByteBuffer buffer) {
            this.szSSID = new byte[33];
            buffer.get(this.szSSID);
            this.btPskMode = buffer.get();
            this.szPsk = new byte[65];
            buffer.get(this.szPsk);
            this.btIPMode = buffer.get();
            this.btIP = new byte[4];
            buffer.get(this.btIP);
            this.btMask = new byte[4];
            buffer.get(this.btMask);
            this.btGWIP = new byte[4];
            buffer.get(this.btGWIP);
            this.btDNS1 = new byte[4];
            buffer.get(this.btDNS1);
            this.btDNS2 = new byte[4];
            buffer.get(this.btDNS2);
            return buffer;
        }

        @Override
        public ByteBuffer fillBuffer(ByteBuffer buffer) {
            buffer.put((byte) 1);
            buffer.put(this.szSSID);
            for (int i = this.szSSID.length; i < 33; i++) {
                buffer.put((byte) 0);
            }
            buffer.put(this.btPskMode);
            buffer.put(this.szPsk);
            for (int i = this.szPsk.length; i < 65; i++) {
                buffer.put((byte) 0);
            }
            buffer.put(this.btIPMode);
            buffer.put(this.btIP);
            for (int i = this.btIP.length; i < 4; i++) {
                buffer.put((byte) 0);
            }
            buffer.put(this.btMask);
            for (int i = this.btMask.length; i < 4; i++) {
                buffer.put((byte) 0);
            }
            buffer.put(this.btGWIP);
            for (int i = this.btGWIP.length; i < 4; i++) {
                buffer.put((byte) 0);
            }
            buffer.put(this.btDNS1);
            for (int i = this.btDNS1.length; i < 4; i++) {
                buffer.put((byte) 0);
            }
            buffer.put(this.btDNS2);
            for (int i = this.btDNS2.length; i < 4; i++) {
                buffer.put((byte) 0);
            }
            return buffer;
        }
    }

    public static class UserCfgRsp extends BaseRspPack {
        public byte btCfgMode;
        public int ulUserId;

        public UserCfgRsp() {
        }

        @Override
        public ByteBuffer parseBuffer(ByteBuffer buffer) {
            this.rspCode = buffer.get();
            if (this.rspCode == ErrType.ERR_CODE_OK) {
                this.btCfgMode = buffer.get();
                if (0 == this.btCfgMode) {
                    this.ulUserId = buffer.getInt();
                }
            }
            return buffer;
        }
    }

    public static class UpdateStatusRsp extends BaseRspPack {

        public byte status;
        public short ver;
        public int progress;

        public UpdateStatusRsp() {
        }

        @Override
        public ByteBuffer parseBuffer(ByteBuffer buffer) {
            this.rspCode = buffer.get();
            if (this.rspCode == ErrType.ERR_CODE_OK) {
                this.status = buffer.get();
                this.ver = buffer.getShort();
                this.progress = buffer.getInt();
            }
            return buffer;
        }

        @Override
        public String toString() {
            return "UpdateStatusRsp{" +
                    "status=" + status +
                    ", ver=" + ver +
                    ", progress=" + progress +
                    '}';
        }
    }

    /**
     */
    public static class WorkModeRsp extends BaseRspPack {
        public NoxWorkMode workMode;
        public short deviceType;

        public WorkModeRsp(PacketHead head) {
            super(head.type);
            this.deviceType = head.deviceType;
        }

        @Override
        public String toString() {
            return "WorkModeRsp{" +
                    "rspCode=" + rspCode +
                    ",workMode=" + workMode +
                    '}';
        }

        @Override
        public ByteBuffer parseBuffer(ByteBuffer buffer) {

            if (type != PacketType.FA_POST) {
                buffer = super.parseBuffer(buffer);
            }

            if (this.rspCode == ErrType.ERR_CODE_OK) {
                workMode = new NoxWorkMode();
                workMode.mode = buffer.get();
                workMode.sceneNum = buffer.get();
                workMode.sceneStatuses.clear();
                for (int i = 0; i < workMode.sceneNum; i++) {
                    NoxWorkMode.SceneStatus status = new NoxWorkMode.SceneStatus();
                    status.sceneSeqid = buffer.getLong();
                    status.sceneType = buffer.get();//场景类型
                    workMode.sceneStatuses.add(status);
                }
                workMode.sleepAidStatus = buffer.get();
                workMode.sleepAidLeave = buffer.get();
                workMode.alarmStatus = buffer.get();
                workMode.alarmId = buffer.getLong();
                workMode.light = new NoxLight();
                workMode.light.lightFlag = buffer.get();
                workMode.light.brightness = buffer.get();
                workMode.light.lightMode = buffer.get();
                if (workMode.light.lightMode == NoxLight.LightMode.FIXED_STREAMER) {
                    workMode.light.fixed_streamer_id = buffer.get();
                } else {
                    workMode.light.r = buffer.get();
                    workMode.light.g = buffer.get();
                    workMode.light.b = buffer.get();
                    workMode.light.w = buffer.get();
                }
            }

            return buffer;
        }
    }

    public static class SceneConfigReq extends BasePacket {

        public ArrayList<SceneConfig> configs;

        public SceneConfigReq(ArrayList<SceneConfig> configs) {
            this.configs = configs;
        }

        @Override
        public ByteBuffer fillBuffer(ByteBuffer buffer) {
            int size = configs == null ? 0 : configs.size();
            buffer.put(SET);
            buffer.put((byte) size);
//            LogUtil.logE("设置场景参数，场景个数：" + size);
            for (int i = 0; i < size; i++) {
                SceneConfig config = configs.get(i);
//                LogUtil.logE("场景 " + i + "  参数：" + config);
                config.fillBuffer(buffer);
            }
            return buffer;
        }
    }

    public static class BluetoothPlayModeCtlReq extends BasePacket {

        public byte cmd;

        public BluetoothPlayModeCtlReq(boolean open) {
            cmd = (byte) (open ? 0x1 : 0x0);
        }

        @Override
        public ByteBuffer fillBuffer(ByteBuffer buffer) {
            buffer.put(cmd);
            return buffer;
        }
    }

    public static class SceneOperationReq extends BasePacket {
        public byte operation;
        public long sceneId;
        public SceneConfig config;

        public SceneOperationReq() {

        }

        public SceneOperationReq(byte operation, long sceneId, SceneConfig config) {
            this.operation = operation;
            this.sceneId = sceneId;
            this.config = config;
        }

        public SceneOperationReq(byte operation, long sceneId) {
            this.operation = operation;
            this.sceneId = sceneId;
        }

        @Override
        public ByteBuffer fillBuffer(ByteBuffer buffer) {
            buffer.put(operation);
            buffer.putLong(sceneId);

            //无效音乐，开启场景后再播音乐
            buffer.put((byte) 0x00);
            buffer.put((byte) 0x75);
            buffer.put((byte) 0x31);
            buffer.put((byte) 0x01);
            buffer.put((byte) 0x01);
            buffer.put((byte) 0x01);

//            if (config != null && config.light != null) {
//                buffer.put(config.light.lightFlag);
//                //buffer.put(config.music.musicOpenFlag);
//                buffer.put((byte) 0xff);
//            } else {
//                buffer.put((byte) 0x00);
//                buffer.put((byte) 0xff);
//            }

            return buffer;
        }
    }


    public static class LightOperationReq extends BasePacket {
        public byte operation;
        public NoxLight light;

        public LightOperationReq() {
        }

        public LightOperationReq(byte operation, NoxLight light) {
            this.operation = operation;
            this.light = light;
        }

        @Override
        public ByteBuffer parseBuffer(ByteBuffer buffer) {
            return buffer;
        }

        @Override
        public ByteBuffer fillBuffer(ByteBuffer buffer) {
            if (light != null) {
                buffer.put((byte) (operation | light.ctrlMode.value << 4));
            } else {//有些操作，例如关灯，没有穿light参数
                buffer.put(operation);
            }

            if (operation != INoxManager.PostLightControl.CLOSE) {
                buffer.put(light.brightness);
                light.fillLightMode(buffer);
            }
            return buffer;
        }
    }

    public static class LightPreviewReq extends BasePacket {
        public byte operation;
        public byte exitFlag;
        public byte rebootFlag;

        public LightPreviewReq() {
        }

        public LightPreviewReq(byte operation, byte exitFlag, byte rebootFlag) {
            this.operation = operation;
            this.exitFlag = exitFlag;
            this.rebootFlag = rebootFlag;
        }

        @Override
        public ByteBuffer parseBuffer(ByteBuffer buffer) {
            return buffer;
        }

        @Override
        public ByteBuffer fillBuffer(ByteBuffer buffer) {
            buffer.put(operation);
            buffer.put(exitFlag);
            buffer.put(rebootFlag);
            return buffer;
        }
    }

    public static class ButtonFunctionReq extends BasePacket {
        public boolean light;
        public boolean music;
        public boolean aromatherapy;

        public ButtonFunctionReq() {
        }

        public ButtonFunctionReq(boolean light, boolean music, boolean aromatherapy) {
            this.light = light;
            this.music = music;
            this.aromatherapy = aromatherapy;
        }

        @Override
        public ByteBuffer parseBuffer(ByteBuffer buffer) {
            return buffer;
        }

        @Override
        public ByteBuffer fillBuffer(ByteBuffer buffer) {
            buffer.put(SET);
            buffer.put((byte) (light ? 1 : 0));
            buffer.put((byte) (music ? 1 : 0));
            buffer.put((byte) (aromatherapy ? 1 : 0));
            return buffer;
        }
    }

    public static class SleepAidOperationReq extends BasePacket {
        public byte operation;
        public byte lightControl, musicControl;

        public SleepAidOperationReq() {
        }

        public SleepAidOperationReq(byte operation, byte lightControl, byte musicControl) {
            this.operation = operation;
            this.lightControl = lightControl;
            this.musicControl = musicControl;
        }

        @Override
        public ByteBuffer parseBuffer(ByteBuffer buffer) {
            return buffer;
        }

        @Override
        public ByteBuffer fillBuffer(ByteBuffer buffer) {
            buffer.put(operation);
//            灯开关	UINT8	1	0: 关 1: 开 0xFF: 保持原状态
//            音乐开关	UINT8	1	0: 停止 1: 暂停 2: 播放 0xFF: 保持原状态
            buffer.put(lightControl);
            buffer.put(musicControl);
            return buffer;
        }
    }


    public static class TimeSyncReq extends BasePacket {
        public int ulTimestamp;
        public int nTimezone;
        public byte btSpecial;
        public int nSpecialOfs;

        public TimeSyncReq() {
        }

        public TimeSyncReq(int ulTimestamp, int nTimezone, byte btSpecial, int nSpecialOfs) {
            this.ulTimestamp = ulTimestamp;
            this.nTimezone = nTimezone;
            this.nTimezone = nTimezone;
            this.nSpecialOfs = nSpecialOfs;
        }

        @Override
        public ByteBuffer fillBuffer(ByteBuffer buffer) {
            buffer.putInt(this.ulTimestamp);
            buffer.putInt(this.nTimezone);
            buffer.put(this.btSpecial);
            buffer.putInt(this.nSpecialOfs);
            return buffer;
        }
    }
}
