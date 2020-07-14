package com.jianbao.jamboble.nox.manager;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.jianbao.jamboble.nox.BaseCallback;
import com.jianbao.jamboble.nox.BleDevice;
import com.jianbao.jamboble.nox.ConnectionState;
import com.jianbao.jamboble.nox.Device;
import com.jianbao.jamboble.nox.DeviceType;
import com.jianbao.jamboble.nox.bean.CallbackData;
import com.jianbao.jamboble.nox.bean.Nox;
import com.jianbao.jamboble.nox.interfaces.ICentralManager;
import com.jianbao.jamboble.nox.interfaces.IDeviceManager;
import com.jianbao.jamboble.nox.utils.ByteUtils;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hao on 2016/7/18.
 */

public abstract class DeviceManager implements IDeviceManager {
    /**
     * 默认等待设备回复时长,超过这个时长则为超时
     */
    protected final static int DEFAULT_WAIT_DEVICE_TIMEOUT = 5000;
    protected int mSendDuration = 30; //发送数据包间隔时间
    public static ExecutorService sTheadExecutor = Executors.newCachedThreadPool();
    public String TAG = getClass().getSimpleName();
    private boolean isSaveLog;//是否记录日志

    public enum ConnectType {
        BLE, TCP
    }


    public byte[] getDeviceId14Bytes() {
        if (getDevice() != null) {
            return ByteUtils.to14Bytes(getDevice().deviceId.getBytes());
        }
        return null;
    }

    @Override
    public void connectDevice(Device device) {
        connectDevice();
    }

    @Override
    public void connectDevice(ConnectType type) {
        connectDevice();
    }

    public float getVersionCode() {
        float verCode = 0f;
        if (getDevice() != null) {
            verCode = getDevice().getVersionCode();
        }
        return verCode;
    }
    public ConnectType mConnectType = ConnectType.BLE;

    /**
     * 默认设备
     */
    protected Device device;
    protected final ArrayList<SoftReference<BaseCallback>> mCallbacks = new ArrayList<>();
    public Context mContext;

    protected String sender;
    /**
     * 放在这里是因为：不同的蓝牙设备，连接状态不一样
     */
    protected ConnectionState mConnectionState = ConnectionState.DISCONNECT;

    /**
     * 获取连接状态
     *
     * @return
     */
    public ConnectionState getConnectionState() {
        return mConnectionState;
    }

    /**
     * 获取设备类型
     *
     * @return 设备类型
     */
    public short getDeviceType() {
        if (device == null) {
            return DeviceType.DEVICE_TYPE_INVALID;
        }
        return device.deviceType;
    }


    public synchronized static DeviceManager getManager(Context context, Device device) {
        DeviceManager manager = Nox2BManager.getInstance(context);
        if (manager != null) {
            //manager.mContext = context;
            manager.mContext = context;
            if (manager.device == null) {
                manager.device = device;
            } else {
                if (manager.device.deviceType != device.deviceType) {
                    //类似RestonManager对应多个设备，如果连着Z1就连不上Z400了
                    manager.disconnect();
                }
            }
            //替换设备类型，比如RestonManager对应了几个设备类型
            manager.device.deviceType = device.deviceType;
        }
        return manager;
    }

    /**
     * 扫描结果获取蓝牙设备模型
     *
     * @param device
     * @param deviceId
     * @return
     */
    public BleDevice getBleDevice(BluetoothDevice device, String deviceId, short deviceType) {
        BleDevice bleDevice = null;
        if (deviceType == DeviceType.DEVICE_TYPE_NOX_2B || deviceType == DeviceType.DEVICE_TYPE_NOX_2W) {
            bleDevice = new Nox();
        } else {
            bleDevice = new BleDevice();
        }

        bleDevice.modelName = device.getName();
        bleDevice.deviceId = deviceId;
        bleDevice.deviceName = deviceId;
        bleDevice.address = device.getAddress();
        bleDevice.deviceType = deviceType;
        return bleDevice;
    }

    /**
     * 格式化设备ID
     *
     * @param scanRecord
     * @return
     */
    public String formatDeviceID(byte[] scanRecord) {
        /*if (scanRecord.length < 23)
            return null;
        byte[] scans = new byte[12];
        for (int i = 11; i < 23; i++) {
            scans[i - 11] = scanRecord[i];
        }
        String str = new String(scans);// Z1140900000

        String deviceId = str.substring(0, 2).toUpperCase() + "-" + str.substring(2);
        return deviceId;*/
        //20170420  朱杰
//        后续新产品(包括不同材质的枕头类型、B501-2)---固件均会广播完整设备名称
//
//        APP 可以按以下情况做兼容：
//        如果广播内容长度为12，则需要追加 -
//        如果广播内容长度为13，则广播内容就是完整名称

        String deviceId = null;
        String str = getBleDeviceName(0xff, scanRecord);
        if (str != null) {
            //902B是之前做的，还是加-
            if (str.length() >= 13 && !str.startsWith("SN21")) {
                deviceId = str;
            } else {
                deviceId = str.substring(0, 2).toUpperCase() + "-" + str.substring(2);
            }
        }
        return deviceId;
    }

    /**
     * 是否正在连接
     *
     * @return
     */
    public boolean isConnected() {
        return getConnectionState() == ConnectionState.CONNECTED;
    }

    public boolean checkCallbackDataStatus(CallbackData cd, int msgType) {
        return checkCallbackDataStatus(cd) && cd.getType() == msgType;
    }

    public boolean checkCallbackDataStatus(CallbackData cd) {
        return cd != null && cd.isSuccess();
    }


    public short getDeviceTypeById(String deviceId) {
        short deviceType = -1;
        if (checkNox1(deviceId)) {
            deviceType = DeviceType.DEVICE_TYPE_NOX_PRO;
        } else if (checkNox2(deviceId)) {
            deviceType = DeviceType.DEVICE_TYPE_NOX_2B;
        } else if (checkRestOnZ1(deviceId)) {
            deviceType = DeviceType.DEVICE_TYPE_RESTON_Z1;
        } else if (checkRestOnZ2(deviceId)) {
            deviceType = DeviceType.DEVICE_TYPE_RESTON_Z2;
        } else if (checkPillowID(deviceId)) {
            deviceType = DeviceType.DEVICE_TYPE_PILLOW;
        } else if (checkSleepDot502ID(deviceId)) {
            deviceType = DeviceType.DEVICE_TYPE_SLEEPDOT_502;
        } else if (checkSleepDot502TID(deviceId)) {
            deviceType = DeviceType.DEVICE_TYPE_SLEEPDOT_502T;
        } else if (checkSleepDotID(deviceId)) {
            deviceType = DeviceType.DEVICE_TYPE_SLEEPDOT;
        } else if (checkNox2w(deviceId)) {
            deviceType = DeviceType.DEVICE_TYPE_NOX_2W;
        } else if (checkNoxSAW(deviceId)) {
            deviceType = DeviceType.DEVICE_TYPE_NOX_SAW;
        } else if (checkNoxSAB(deviceId)) {
            deviceType = DeviceType.DEVICE_TYPE_NOX_SAB;
        } else if (checkRestOnZ400T(deviceId)) {
            deviceType = DeviceType.DEVICE_TYPE_RESTON_Z4;
        }

        return deviceType;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public synchronized void registCallBack(BaseCallback callBack, String sender) {
        //LogUtil.eThrowable(TAG,"----registCallBack---sender:  "+sender+"    callBack:  "+callBack);
        if (callBack == null) return;
//        String log1 = TAG + " registCallBack1 cb:" + callBack + ",sender:" + sender + ",size:" + mCallbacks.size();
//        LogUtil.log(log1);

        setSender(sender);
        callBack.setSender(sender);
//        LogUtil.logE(TAG + "   registCallBack：" + callBack);
        SoftReference<BaseCallback> reference = new SoftReference<>(callBack);
        Iterator<SoftReference<BaseCallback>> iterator = mCallbacks.iterator();
        while (iterator.hasNext()){
            SoftReference<BaseCallback> childCallback = iterator.next();
//            LogUtil.logE(TAG + "    列表内Callback：" + childCallback.get());
            if ((childCallback.get() == null) || (childCallback.get() != null && childCallback.get().getSender() == null && sender == null)) {
                iterator.remove();
//                LogUtil.log(TAG + " remove common1 callBack:" + callBack);
            } else if (callBack == childCallback.get()) {
                iterator.remove();
//                LogUtil.log(TAG + " remove common2 callBack:" + callBack);
            }
        }

        boolean res = mCallbacks.add(reference);
        String log2 = TAG + " registCallBack res:" + res+",size:" + mCallbacks.size();
        Log.d(TAG,log2);
    }


    @Override
    public synchronized void unRegistCallBack(BaseCallback callBack) {
        if (callBack == null) return;
        int count = 0;
        Iterator<SoftReference<BaseCallback>> iterator = mCallbacks.iterator();
        while (iterator.hasNext()){
            SoftReference<BaseCallback> childCallback = iterator.next();
            if (callBack.equals(childCallback.get())) {
                iterator.remove();
                //break; //通过测试部提供的log，发现会有多个相同的回调，但是底层只发了一次数据，猜测是监听器的问题，这里移除时，把所有相同的都移除掉
                count++;
            }
        }
//        LogUtil.logTemp(TAG+" unRegistCallBack cb:"+ callBack+",count:" + count+",size:" + mCallbacks.size());
    }

    /**
     * 数据回调
     *
     * @param callbackData 根据type来区分回调消息类型,根据status来判断处理结果,result为回调结果
     */
    protected synchronized void dataCallback(final CallbackData callbackData) {
        if (callbackData != null && device != null && callbackData.getDeviceType() == DeviceType.DEVICE_TYPE_INVALID) {
            callbackData.setDeviceType(device.deviceType);
        }
//        LogUtil.log(TAG + " dataCallback size:" + mCallbacks.size() + ",sender:" + sender);
        if (mCallbacks.size() > 0) {
            Iterator<SoftReference<BaseCallback>> iterator = mCallbacks.iterator();
            while (iterator.hasNext()) {
                BaseCallback callback = iterator.next().get();
//                LogUtil.log(TAG + " dataCallback cb:" + callback+"," + callbackData);
                if (callback == null) {
                    iterator.remove();
                    continue;
                }
                //LogUtil.logE(TAG + "   回调：" + callback + "   sender:" + sender + "   数据：" + callbackData.getSender());
                if (callback.getSender() == null || sender == null || callback.getSender().equals(callbackData.getSender()) || callbackData.getType() == ICentralManager.TYPE_METHOD_SCENE_START || callbackData.getType() == ICentralManager.TYPE_METHOD_SCENE_STOP) {//有些手机生命周期函数走错了，导致启动或停止场景MainActivit一直没有收到回调，一直loading
//                   LogUtil.logE(TAG + "   sender:" + sender + "  回调数据：" + callbackData);
                    callback.onDataCallback(callbackData);
                }

            }
        }
    }

    /**
     * 连接状态回调
     *
     * @param state
     */
    protected synchronized void onStateChangeCallBack(final ConnectionState state) {
//        if (mConnectionState != state) {
        mConnectionState = state;
//        LogUtil.log(TAG + " onStateChangeCallBack:" + state +",sender:" + sender + ",size:"+mCallbacks.size());
        if (mCallbacks.size() > 0) {
            for (SoftReference<BaseCallback> callback : mCallbacks) {
                BaseCallback cb = callback.get();
//                LogUtil.log(TAG + " onStateChangeCallBack cb:" + cb +",sender:"+ sender);
                if (cb != null && (TextUtils.isEmpty(cb.getSender()) || sender == null || (cb.getSender() != null && cb.getSender().equals(sender)))) {
                    if (state == ConnectionState.CONNECTED) {
                        if(!isSaveLog){
                            isSaveLog=true;
                            //设备连接成功
                            if(mConnectType== ConnectType.BLE){
                                Log.d(TAG, "BLE");
                            }else {
                                Log.d(TAG, "TCP");
                            }
                        }
                        Log.d(TAG, " 设备连接成功");
//                        if (this instanceof NoxManager) {//绑定的时候设备还未加入到睡觉场景中，绑定成功后才加入，故不能加上条件 SceneUtils.hasNox
//                            GlobalInfo.setDeviceOnLineState(1);
//                        }
                    } else if (state == ConnectionState.DISCONNECT) {
                        if(isSaveLog){
                            //设备连接成功
                            if(mConnectType== ConnectType.BLE){
                                Log.d(TAG, "BLE");
                            }else {
                                Log.d(TAG, "TCP");
                            }
                            isSaveLog=false;
                        }
                        Log.d(TAG, " 设备掉线了");
                    }
                    cb.onStateChange(DeviceManager.this, sender, state);
                }else{
//                    LogUtil.log(TAG + " onStateChangeCallBack not-----------cb" + cb +",sender:"+ sender);
                }
            }
        }
//        }
    }

    /**
     * 连接状态回调
     *
     * @param state
     */
    protected synchronized void onStateChangeCallBack(IDeviceManager deviceManager, String sender, final ConnectionState state) {
//        if (mConnectionState != state) {
        mConnectionState = state;
//        LogUtil.log(TAG + " onStateChangeCallBack:" + state + ",sender:" + sender+",size:"+mCallbacks.size());
        if (mCallbacks.size() > 0) {
            for (SoftReference<BaseCallback> callback : mCallbacks) {
                BaseCallback cb = callback.get();
                if (cb != null && (TextUtils.isEmpty(cb.getSender()) || sender == null || (cb.getSender() != null && cb.getSender().equals(sender)))) {
                    if (state == ConnectionState.CONNECTED) {
                        Log.d(TAG, " 设备连接成功");
                    }
                    cb.onStateChange(deviceManager, sender, state);
                }
            }
        }
//        }
    }

    /**
     * 检查RestOn设备ID是否合法
     *
     * @param deviceId 设备ID
     * @return true:合法；false:非法
     */

    public static boolean checkRestOnZ1(String deviceId) {
        if (deviceId == null) return false;
        Pattern p = Pattern.compile("^(Z1-)[0-9a-zA-Z]{10}$");
        Matcher m = p.matcher(deviceId);
        return m.matches();
    }

    public static boolean checkRestOnZ2(String deviceId) {
        if (deviceId == null) return false;
        Pattern p = Pattern.compile("^(Z2-)[0-9a-zA-Z]{10}$");
        Matcher m = p.matcher(deviceId);
        return m.matches();
    }

//    public static boolean checkRestOnZ400(String deviceId) {
//        if (deviceId == null) return false;
//        Pattern p = Pattern.compile("^(Z4-)[0-9a-zA-Z]{10}$");
//        Matcher m = p.matcher(deviceId);
//        return m.matches();
//    }

    public static boolean checkRestOnZ400T(String deviceId) {
        if (deviceId == null) return false;
        Pattern p = Pattern.compile("^(Z4)[-0-9a-zA-Z]{11}$");
        Matcher m = p.matcher(deviceId);
        return m.matches();
    }

    public static boolean checkRestOn(String deviceId) {
        if (deviceId == null) return false;
        Pattern p = Pattern.compile("^(Z[1-9]-)[0-9a-zA-Z]{10}$");
        Matcher m = p.matcher(deviceId);
        return m.matches();
    }


    public static boolean checkPillowID(String deviceId) {
        if (deviceId == null) return false;
        Pattern p = Pattern.compile("^(P)[-0-9a-zA-Z]{12}$");
        Matcher m = p.matcher(deviceId);
        return m.matches();
    }

    public static boolean checkSleepDotID(String deviceId) {
        if (deviceId == null) return false;
        Pattern p = Pattern.compile("^(B[1-9]-)[0-9a-zA-Z]{10}$");
        Matcher m = p.matcher(deviceId);
        return m.matches();
    }

    public static boolean checkSleepDot502ID(String deviceId) {
        if (deviceId == null) return false;
        //Pattern p = Pattern.compile("^(B5012)[0-9a-zA-Z]{8}$");
        Pattern p = Pattern.compile("^(B5-02)[0-9a-zA-Z]{8}$");
        Matcher m = p.matcher(deviceId);
        return m.matches();
    }

    public static boolean checkSleepDot502TID(String deviceId) {
        if (deviceId == null) return false;
        Pattern p = Pattern.compile("^(B502T)[0-9a-zA-Z]{8}$");
        Matcher m = p.matcher(deviceId);
        return m.matches();
    }
    public static boolean checkNox1(String deviceId) {
        if (deviceId == null) return false;
        Pattern p = Pattern.compile("^(N1-)[0-9a-zA-Z]{10}$");
        Matcher m = p.matcher(deviceId);
        return m.matches();
    }

    public static boolean checkNox2(String deviceId) {
        if (deviceId == null) return false;
        Pattern p = Pattern.compile("^(SN-21)[0-9a-zA-Z]{9}$");
        Matcher m = p.matcher(deviceId);
        return m.matches();
    }

    public static boolean checkNox2w(String deviceId) {
        if (deviceId == null) return false;
        Pattern p = Pattern.compile("^(SN22)[0-9a-zA-Z]{9}$");
        Matcher m = p.matcher(deviceId);
        return m.matches();
    }

    public static boolean checkNoxSAW(String deviceId) {
        if (deviceId == null) return false;
        Pattern p = Pattern.compile("^(SA11)[0-9a-zA-Z]{9}$");
        Matcher m = p.matcher(deviceId);
        return m.matches();
    }

    public static boolean checkNoxSAB(String deviceId) {
        if (deviceId == null) return false;
        Pattern p = Pattern.compile("^(SA12)[0-9a-zA-Z]{9}$");
        Matcher m = p.matcher(deviceId);
        return m.matches();
    }

    public static boolean checkNox(String deviceId) {
        return checkNox1(deviceId) || checkNox2(deviceId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceManager manager = (DeviceManager) o;

        return TAG != null ? TAG.equals(manager.TAG) : manager.TAG == null;

    }

    @Override
    public int hashCode() {
        return TAG != null ? TAG.hashCode() : 0;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public boolean isDeviceNull(Device device) {
        return device == null || device.deviceType == DeviceType.DEVICE_TYPE_NULLL;
    }

    @Override
    public String toString() {
        return TAG+" connS:" + getConnectionState();
    }

    public static String getBleDeviceName(int type, byte[] record) {
        byte[] data = null;
        int index = 0;
        while (index < record.length) {
            int len = record[index] & 0xFF;
            int tp = record[index + 1] & 0xFF;
            if (index + len + 1 > 31) {
                break;
            } else if (len == 0) {
                break;
            }
            if (type == tp) {
                data = new byte[len - 1];
                for (int i = 0; i < len - 1; i++) {
                    data[i] = record[index + 2 + i];
                }
                break;
            }
            index += (len + 1);
        }

        if (data != null) {
            return new String(data);
        }

        return null;
    }

    /**
     * 获取中控设备manager
     *
     * @param context
     * @param sleepAidDevice 没有就设置null
     * @param monitorDevice  没有就设置null
     * @param alarmDevice    没有就设置null
     * @return
     */
    public static CentralManager getCentralManager(Context context, Device sleepAidDevice, Device monitorDevice, Device alarmDevice) {
        return CentralManager.getsInstance(context, sleepAidDevice, monitorDevice, alarmDevice);
    }
}
