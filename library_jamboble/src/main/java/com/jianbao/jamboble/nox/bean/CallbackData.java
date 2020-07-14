package com.jianbao.jamboble.nox.bean;


/**
 * Created by hao on 16/6/12.
 * 数据回调
 * 如果结果为null,说明操作失败,可根据结果状态传递原因
 * 如果结果不为null,则返回相对应的结果类型,可根据方法调用说明强转
 */

public class CallbackData {

    public CallbackData() {
        status = STATUS_FAILED;
    }


    /**
     * OK
     */
    public final static int STATUS_OK = 0x00;
    /**
     * 超时
     */
    public final static int STATUS_TIMEOUT = 0x01;
    /**
     * 网络或者蓝牙未连接
     */
    public final static int STATUS_DISCONNECT = 0x02;
    /**
     * 网络或者蓝牙操作失败或者无效
     */
    public final static int STATUS_FAILED = 0x03;
    /**
     * 设备不支持的API
     */
    public final static int STATUS_NO_SUPPORT = 0x04;
    /*
    * 以下返回码是nox1才有的，标识操作的返回结果
    * */
    public final static int STATUS_SERVER_ERROR = 0x01;//服务器错误

    public final static int STATUS_NO_LOGIN = 0x02;//未登录

    public final static int STATUS_NOX_NO_BIND_USER = 0x03;//等未绑定用户

    public final static int STATUS_RESTON_NO_BIND_UER = 0x04;//reston未绑定用户

    public final static int STATUS_RESON_ALARY_BIND = 0x05;//reston已绑定

    public final static int STATUS_NOX_UNDER_LINE = 0x06;//灯未上线

    public final static int STATUS_APP_UNDER_LINE = 0x07;//APP未上线

    public final static int STATUS_RESTON_UNDER_LINE = 0x08;//reston未上线

    public final static int STATUS_REQUEST_DATA_NO_EXIST = 0x09;//请求数据不存在

    public final static int STATUS_NO_AUTHORITY = 0x0A;//权限不足

    public final static int STATUS_OPERATION_FAIL = 0x0B;//操作失败

    public final static int STATUS_CAN_NOT_FIND_MONITOR_DEVICE = 0x0C;//找不到监测设备

    public final static int STATUS_MONITOR_CONNECT_FAIL = 0x0D;//监测设备连接失败

    public final static int STATUS_BLUE_MAIN_BUSISS = 0x0E;//蓝牙主模块忙

    public final static int STATUS_UPDATING = 0x0F;//正在升级，请等待
    /**
     * 一首音乐播放完了
     */
    public final static int STATUS_MUAIC_PLAY_OVER = 0x10;



    /**
     * 操作是否成功
     *
     * @return
     */
    public boolean isSuccess() {
        return status == STATUS_OK;
    }


    /**
     * 返回消息类型
     */
    private int type;
    /**
     * 通知类型(选项,有可能为空)
     */
    private int notifyType;

    private short deviceType;

    /**
     * 结果状态常量,成功返回0
     */
    private int status = -1;
    /**
     * 返回结果
     */
    private Object result;

    private int errCode = -1;

    /**
     * 发送主体
     */
    private String sender;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 设置操作的结果状态
     * @return
     */
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getResult() {
        return result;
    }

    public int getErrorCode() {
        if (errCode == -1 && result != null) {
            String str = result.toString();
            int idx = str.indexOf(":");
            if (idx != -1) {
                errCode = Integer.valueOf(str.substring(idx + 1));
            }
        }
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public void setResult(Object result) {
        this.result = result;
    }


    public int getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(int notifyType) {
        this.notifyType = notifyType;
    }

    @Override
    public String toString() {
        return "CallbackData{" +
                "type=" + type +
                ", notifyType=" + notifyType +
                ", status=" + status +
                ", result=" + result +
                ", deviceType=" + deviceType +
                ", sender=" + sender +
                '}';
    }

    public short getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(short deviceType) {
        this.deviceType = deviceType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public static CallbackData noSupportData(String sender, int callbackType) {
        CallbackData data = new CallbackData();
        data.setType(callbackType);
        data.setSender(sender);
        data.setStatus(STATUS_NO_SUPPORT);
        return data;
    }

    public static CallbackData timeOutData(String sender, int callbackType) {
        CallbackData data = new CallbackData();
        data.setType(callbackType);
        data.setSender(sender);
        data.setStatus(STATUS_TIMEOUT);
        return data;
    }
}
