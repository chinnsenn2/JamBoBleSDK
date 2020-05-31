package com.jianbao.jamboble.device.nox.manager;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.jianbao.jamboble.device.nox.BleDevice;
import com.jianbao.jamboble.device.nox.NoxBleHelper;
import com.jianbao.jamboble.device.nox.ConnectionState;
import com.jianbao.jamboble.device.nox.Device;
import com.jianbao.jamboble.device.nox.DeviceType;
import com.jianbao.jamboble.device.nox.bean.CallbackData;
import com.jianbao.jamboble.device.nox.bean.DataPackBlockingQueue;
import com.jianbao.jamboble.device.nox.bean.DataPacket;
import com.jianbao.jamboble.device.nox.interfaces.IBleCallBack;
import com.jianbao.jamboble.device.nox.utils.BluetoothUtil;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2016/6/6.
 */

public abstract class BleManager extends DeviceManager implements IBleCallBack {

    protected final DataPackBlockingQueue mReceiveDataPack = new DataPackBlockingQueue(TAG);
    protected NoxBleHelper mNoxBleHelper;

    /**
     * 超时次数，如果超时次数超过3次，就断开设备连接，认为设备掉线了
     */
    int timeoutCount;

    Thread mWriteThread;
    BlockingQueue<DataPacket> mWaitSendQueue;
//    Object mWaitLock = new Object();
//    boolean isWaitLock = false;
//    boolean isSendOk = true;

    /**
     * 设置包发送的间隔时长
     *
     * @param sendDuration
     */
    public void setSendDuration(int sendDuration) {
        mSendDuration = sendDuration;
    }

    public int getSendDuration() {
        return mSendDuration;
    }

//    protected List<DataPacket> mWaitingSendPack;

    /**
     * 蓝牙设备是否已经登录成功
     */
    protected boolean mIsLogin;

    protected boolean isReconnect;
    /**
     * 重连的时候是否发现了设备
     */
    protected boolean reConnectGetDevice = false;

    /**
     * 连接设备过程是否完成，一个连接过程完成后才能进行下一个连接过程。（连接中有判断状态，如果已经连接上，则直接返回）
     * 该变量主要用于防止多个连接任务同时进行，可能导致的连接失败问题
     * {@link #connectDevice()} 方法中本来可以用ConnectState_Connecting判断，但是还有Scanning StopScan状态
     * 尤其是StopScan状态很难控制
     */
    protected boolean connectComplete = true;


    protected DeviceFoundListener deviceListener;

    protected BleManager(Context context) {
        mNoxBleHelper = NoxBleHelper.getInstance(context);
        registerBleListener();
    }

    public void start() {
        Log.e(TAG, "  开启写线程");
        mWaitSendQueue = new ArrayBlockingQueue<>(50);
        mWriteThread = new Thread(mSendRunable);
        mWriteThread.start();
    }


    protected void registerBleListener() {
        mNoxBleHelper.registListener(this);
    }


    public void setDeviceListener(DeviceFoundListener deviceListener) {
        this.deviceListener = deviceListener;
    }


    protected void callbackState(final ConnectionState state) {
        if (state == ConnectionState.STOP_SCAN) {
            if (isReconnect && !reConnectGetDevice) {
                //如果停止扫描还是没有扫描到设备，直接连接设备的地址
                connectDevice(getDevice());
            } else {
                onStateChangeCallBack(state);
            }
        } else if (state == ConnectionState.CONNECTED) {
            start();
            sTheadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(100);
                    onStateChangeCallBack(state);
                    loginComplete = true;
                    connectComplete = true;
                }
            });

        } else {
            onStateChangeCallBack(state);
            if (state == ConnectionState.DISCONNECT) {
                mIsLogin = false;
                connectComplete = true;
            }
        }
    }


    /**
     * 蓝牙设备登录操作是否完成，蓝牙设备存在一种情况，连上后会立即断开，概率性出现。由于断开后会重连，连上又会登录
     * 所以，如果登录不做限制，会出现连续2次登录，造成部分接口调用失败。
     */
    private boolean loginComplete = true;


    @Override
    public BleDevice getDevice() {
        //LogUtil.eThrowable(TAG," getDevice super:" + super.getDevice());
        return (BleDevice) super.getDevice();
    }

    public void setCollectState(byte collectState) {
        if (getDevice() != null) {
            getDevice().collectStatus = collectState;
        }
    }


    public void release() {
        mNoxBleHelper.unRegistListener(this);
        disconnect(false);
    }


    public abstract String getDeviceMDIDSync();


    /**
     * 同步方法请求数据
     *
     * @param messageType
     * @return
     */
    public CallbackData requestDevice(byte messageType, int timeout) {
        return requestDevice(messageType, new DataPacket.BasePacket(), timeout);
    }

    /**
     * 同步方法请求数据
     *
     * @param messageType
     * @return
     */
    public CallbackData requestDevice(byte messageType) {
        return requestDevice(messageType, new DataPacket.BasePacket());
    }

    /**
     * 同步方法请求数据
     *
     * @param messageType
     * @return
     */
    public CallbackData requestDevice(byte messageType, boolean needCheckLogin) {
        return requestDevice(messageType, new DataPacket.BasePacket(), needCheckLogin);
    }

    /**
     * 同步方法报送消息
     *
     * @param messageType
     * @return
     */
    public CallbackData postDevice(byte messageType) {
        return postDevice(messageType, new DataPacket.BasePacket());
    }

    /**
     * 同步方法请求数据
     *
     * @param messageType
     * @return
     */
    public CallbackData requestDevice(byte messageType, DataPacket.BasePacket basePack) {
        return sendDevice(DataPacket.PacketType.FA_REQUEST, messageType, basePack, true);
    }

    /**
     * 同步方法请求数据
     *
     * @param messageType
     * @return
     */
    public CallbackData requestDevice(byte messageType, DataPacket.BasePacket basePack, boolean needCheckLoin) {
        return sendDevice(DataPacket.PacketType.FA_REQUEST, messageType, basePack, needCheckLoin);
    }


    /**
     * 同步方法请求数据
     *
     * @param messageType
     * @param timeout     超时
     * @return
     */
    public CallbackData requestDevice(byte messageType, DataPacket.BasePacket basePack, int timeout) {
        return sendDevice(DataPacket.PacketType.FA_REQUEST, messageType, basePack, timeout);
    }

    /**
     * 同步方法报送消息
     *
     * @param messageType
     * @param basePack
     * @return
     */
    public CallbackData postDevice(byte messageType, DataPacket.BasePacket basePack) {
        return sendDevice(DataPacket.PacketType.FA_POST, messageType, basePack, true);
    }

    /**
     * 同步方法报送消息
     *
     * @param messageType
     * @param basePack
     * @return
     */
    public CallbackData postDevice(byte messageType, DataPacket.BasePacket basePack, boolean needCheckLogin) {
        return sendDevice(DataPacket.PacketType.FA_POST, messageType, basePack, needCheckLogin);
    }

    /**
     * 异步方法请求数据
     *
     * @param messageType
     * @return
     */
    public void requestAsycDevice(final byte messageType) {
        requestAsycDevice(messageType, new DataPacket.BasePacket());
    }

    /**
     * 异步方法请求数据
     *
     * @param messageType
     * @return
     */
    public void requestAsycDevice(final byte messageType, boolean needCheckLogin) {
        requestAsycDevice(messageType, new DataPacket.BasePacket(), needCheckLogin);
    }

    /**
     * 异步方法报送消息
     *
     * @param messageType
     * @return
     */
    public void postAsycDevice(byte messageType) {
        postAsycDevice(messageType, new DataPacket.BasePacket());
    }

    /**
     * 异步方法请求数据
     *
     * @param messageType
     * @param basePack
     * @return
     */
    public void requestAsycDevice(final byte messageType, final DataPacket.BasePacket basePack, final boolean needCheckLogin) {

        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {

                CallbackData result = sendDevice(DataPacket.PacketType.FA_REQUEST, messageType, basePack, needCheckLogin);
//                LogUtil.showMsg(TAG + " requestAsycDevice res:" + result);
                dataCallback(result);

            }
        });
    }

    /**
     * 异步方法请求数据
     *
     * @param messageType
     * @param basePack
     * @return
     */
    public void requestAsycDevice(final byte messageType, final DataPacket.BasePacket basePack) {

        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                CallbackData result = sendDevice(DataPacket.PacketType.FA_REQUEST, messageType, basePack, true);
                //LogUtil.showMsg(TAG + " requestAsycDevice res:" + result);
                dataCallback(result);
            }
        });
    }

    /**
     * 异步设置数据，不要求回调
     *
     * @param messageType
     */
    public void requestPureDevice(byte messageType) {
        requestPureDevice(messageType, new DataPacket.BasePacket());
    }

    /**
     * 异步设置数据，不要求回调
     *
     * @param messageType
     * @param basePack
     */
    public void requestPureDevice(final byte messageType, DataPacket.BasePacket basePack) {
        sendPureDevice(DataPacket.PacketType.FA_REQUEST, messageType, basePack);
    }


    /**
     * 异步方法报送消息
     *
     * @param messageType
     * @param basePack
     * @return
     */
    public void postAsycDevice(final byte messageType, final DataPacket.BasePacket basePack, final boolean needCheckLoin) {
        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {

                CallbackData result = sendDevice(DataPacket.PacketType.FA_POST, messageType, basePack, needCheckLoin);
                dataCallback(result);

            }
        });
    }

    /**
     * 异步方法报送消息
     *
     * @param messageType
     * @param basePack
     * @return
     */
    public void postAsycDevice(final byte messageType, final DataPacket.BasePacket basePack) {
        sTheadExecutor.execute(new Runnable() {
            @Override
            public void run() {

                CallbackData result = sendDevice(DataPacket.PacketType.FA_POST, messageType, basePack, true);
                dataCallback(result);

            }
        });
    }

    /**
     * 发送数据
     *
     * @param packetType
     * @param messageType
     * @param basePack
     * @return
     */
    protected CallbackData osendDevice(byte packetType, byte messageType, DataPacket.BasePacket basePack, int timeout, boolean needCheckLogin) {
        DataPacket pack = buildDataPacket(packetType, messageType, basePack);
        return sendPacket(pack, timeout, needCheckLogin);
    }

    /**
     * 发送数据
     *
     * @param packetType
     * @param messageType
     * @param basePack
     * @return
     */
    protected CallbackData sendDevice(byte packetType, byte messageType, DataPacket.BasePacket basePack, int timeout) {
        DataPacket pack = buildDataPacket(packetType, messageType, basePack);
        return sendPacket(pack, timeout, true);
    }

    /**
     * 发送数据
     *
     * @param packetType
     * @param messageType
     * @param basePack
     * @return
     */
    protected CallbackData sendDevice(byte packetType, byte messageType, DataPacket.BasePacket basePack, boolean needCheckLogin) {
        DataPacket pack = buildDataPacket(packetType, messageType, basePack);
        return sendPacket(pack, needCheckLogin);
    }


    protected void sendPureDevice(byte packetType, byte messageType, DataPacket.BasePacket basePack) {
        if (isDeviceConnected()) {
            DataPacket pack = buildDataPacket(packetType, messageType, basePack);
            sendPurePack(pack);
        }
    }


    /**
     * 给蓝牙发送数据包
     *
     * @param pack
     * @return
     */
    public CallbackData sendPacket(DataPacket pack, boolean needCheckLogin) {
        return sendPacket(pack, DEFAULT_WAIT_DEVICE_TIMEOUT, needCheckLogin);
    }

    @Override
    public boolean isConnected() {
        if (!(isDeviceConnected() || mConnectionState == ConnectionState.CONNECTING)) {
            mConnectionState = ConnectionState.DISCONNECT;
        }
        return super.isConnected() && isDeviceConnected();
    }

    private Runnable mSendRunable = new Runnable() {
        @Override
        public void run() {
            while (isDeviceConnected()) {
                try {
                    DataPacket dataPacket = mWaitSendQueue.poll(1, TimeUnit.MINUTES);
                    if (dataPacket != null) {
                        sendPurePack(dataPacket);
//                        if (!isWaitLock) {
//                            synchronized (mWaitLock) {
//                                isWaitLock = true;
////                                Log.e(TAG, "     锁 等");
//                                mWaitLock.wait(DEFAULT_WAIT_DEVICE_TIMEOUT);
//                            }
//                        }
                        SystemClock.sleep(50);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public static String byte2hex(byte[] buffer) {
        StringBuilder h = new StringBuilder();

        for (byte aBuffer : buffer) {
            String temp = Integer.toHexString(aBuffer & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            } else if (temp.length() == 2) {
                temp = "" + temp;
            }
            h.append(" ").append(temp);
        }

        return h.toString();

    }

    /**
     * 给蓝牙发送数据包
     *
     * @param pack
     * @param timeout
     * @return
     */
    public CallbackData sendPacket(DataPacket pack, int timeout, boolean needCheckLogin) {
        CallbackData callbackData = new CallbackData();
        callbackData.setType(pack.msg.type);
        Log.d(TAG, "   发送数据类型：0x" + Integer.toHexString(pack.msg.type) + "   发送者：" + sender + "   设备连接状态：" + isDeviceConnected() + "  序列号：" + pack.head.senquence);
        callbackData.setSender(sender);

        //字节打印测试
//        byte[] buf = pack.buffer.array();
//        int totalLength = pack.buffer.limit();
//
//        byte[] bytes = new byte[totalLength];
//        System.arraycopy(buf, 0, bytes, 0, totalLength);
//        Log.i(TAG, "  发送数据：" + byte2hex(bytes));
        //end

//        Log.e(TAG, " sendPacket msgType:" + pack.msg.type + ",connS:" + mBleHelper.getConnectionState());

        if (isDeviceConnected()) {
            mReceiveDataPack.addSendPack(pack);
            //  sendPurePack(pack);
            if (mWaitSendQueue == null) {
                start();
            }
            mWaitSendQueue.offer(pack);
            long startTime = System.currentTimeMillis();
            while (true) {
                DataPacket result = mReceiveDataPack.peek(pack.head.senquence);
                if (result != null) {
                    DataPacket.BaseRspPack rsp = (DataPacket.BaseRspPack) result.msg.content;
                    Log.e(TAG, " sendPacket sendPack:" + pack + ",receivePack:" + result + "rsp:" + rsp);
                    if (rsp.rspCode == DataPacket.BaseRspPack.SUCCESS) {
                        callbackData.setStatus(CallbackData.STATUS_OK);
                    } else {
                        callbackData.setStatus(CallbackData.STATUS_FAILED);
                        callbackData.setErrCode(rsp.rspCode);
                    }
                    callbackData.setResult(rsp);
                    timeoutCount = 0;
                    return callbackData;
                }
                if (System.currentTimeMillis() - startTime >= timeout) {
                    break;
                }
                SystemClock.sleep(10);
            }
            timeoutCount++;
            callbackData.setStatus(CallbackData.STATUS_TIMEOUT);
            if (timeoutCount > 2) {
                //如果超时次数等于三次，断开连接
                disconnect();
            }
        } else {
            callbackData.setStatus(CallbackData.STATUS_DISCONNECT);
        }
        return callbackData;
    }

    protected boolean isDeviceConnected() {
        return mNoxBleHelper.getConnectionState() == BluetoothProfile.STATE_CONNECTED && mNoxBleHelper.getBtAddress().equals(getDevice().address);
    }


    /**
     * 单纯发送包，不用管回调
     *
     * @param pack
     */
    protected void sendPurePack(DataPacket pack) {
        if (pack == null || pack.buffer == null) {
            Log.d(TAG, "发送空的数据包");
            return;
        }

        byte[] buf = pack.buffer.array();
        int totalLength = pack.buffer.limit();

//        byte[] bytes = new byte[totalLength];
//        System.arraycopy(buf, 0, bytes, 0, totalLength);
//        Log.e(TAG, "  发送数据：" + Arrays.toString(bytes));

        int offset = 0;
        int remaining = totalLength - offset;
        int countWrite = 0;
        do {
            countWrite = (remaining < DataPacket.MAX_WRITE_SIZE ? remaining : DataPacket.MAX_WRITE_SIZE);
            byte[] send_buf = new byte[countWrite];
            System.arraycopy(buf, offset, send_buf, 0, countWrite);
            mNoxBleHelper.send(send_buf, mSendDuration);
            //LogUtil.showMsg(TAG + " sendPurePack res:" + res + ",data:" + Arrays.toString(send_buf));
            offset += countWrite;
            remaining -= countWrite;
        } while (remaining > 0);
    }


    public boolean isBluetoothOpen() {
        return mNoxBleHelper.isBluetoothOpen();
    }

    /**
     * 判断设备是否支持Ble
     *
     * @return 返回boolean值
     */
    public boolean isSupportBle() {
        return mNoxBleHelper.isSupportBle();
    }

    /**
     * 连接设备
     *
     * @param device
     */
    public void connectDevice(Device device) {
        Log.d(TAG, "   带参连接设备：" + device);
        Log.d(TAG, " connectDevice2 connS:" + getConnectionState() + ",isDeviceConnected:" + isDeviceConnected() + ",connectComplete:" + connectComplete);
        //更新manager的mac地址，因为ios不传，所以IOS的绑的设备我们判断 isDeviceConnected()不通过
        this.device.address = device.address;
        mConnectType = ConnectType.BLE;

        if (isDeviceConnected()) {
            connectComplete = true;
            return;
        }

        if (mNoxBleHelper.getConnectionState() == BluetoothGatt.STATE_DISCONNECTED) {
            isReconnect = false;
            boolean res = mNoxBleHelper.connectDevice(device.address);
            Log.d(TAG, " connectDevice2 res:" + res + ",device:" + device);
            if (!res) {
                callbackState(ConnectionState.DISCONNECT);
                connectComplete = true;
            }
        }
    }


    @Override
    public void connectDevice() {
        mConnectType = ConnectType.BLE;
        Log.d(TAG, " 当前连接状态:" + isDeviceConnected() + "  连接设备：" + getDevice() + "   connectComplete:" + connectComplete + "  蓝牙是否可用：" + BluetoothUtil.isBluetoothEnabled())
        ;
        Log.d(TAG, " connectDevice1 connS:" + getConnectionState() + ",isDeviceConnected:" + isDeviceConnected() + ",connectComplete:" + connectComplete);

        if (isDeviceConnected()) {
            connectComplete = true;
            return;
        }

        if (!connectComplete) {
            return;
        }

        connectComplete = false;
        Device device = getDevice();

        Log.d(TAG, " connectDevice1 connS:" + getConnectionState() + ",device:" + getDevice());

        if (device == null || !BluetoothUtil.isBluetoothEnabled()) {

            onStateChangeCallBack(ConnectionState.DISCONNECT);
            connectComplete = true;
            return;
        }

        isReconnect = true;

        scan();
    }

    /**
     * 用默认扫描超时时间扫描
     */
    public void scan() {
        reConnectGetDevice = false;
        mNoxBleHelper.scanBleDevice();
    }

    public void scan(int timeout) {
        mNoxBleHelper.scanBleDevice(timeout);
    }

    public void stopScan() {
        mNoxBleHelper.stopScan();
    }

    public void disconnect() {
        connectComplete = true;
        mNoxBleHelper.disconnect();
        mWriteThread = null;
        if (mWaitSendQueue != null) {
            mWaitSendQueue.clear();
        }
    }

    public void disconnect(boolean needCallback) {
        connectComplete = true;
        mNoxBleHelper.disconnect(needCallback);
    }

    private boolean isScanBleDevice;//避免多次打印日志，定义这个布尔值作为扫描不到设备的拦截条件。

    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
        synchronized (TAG) {
            final String deviceId = formatDeviceID(bytes);
            Log.d(TAG, TAG + " onLeScan deviceId:" + deviceId);
            if (deviceId != null) {
                short deviceType = getDeviceTypeById(deviceId);
                if (DeviceType.DEVICE_TYPE_NOX_2B == deviceType) {
                    BleDevice bleDevice = getBleDevice(bluetoothDevice, deviceId, deviceType);
//                    LogUtil.showMsg(TAG + " onLeScan isReconnect:" + isReconnect + ",d1:" + bleDevice + ",d2:" + getDevice());
                    if (isReconnect) {
                        if (bleDevice.address.equals(getDevice().address) || bleDevice.deviceName.equals(getDevice().deviceName)) {
                            Log.e(TAG, "   重连，扫描到设备：" + bleDevice.deviceName + "   准备连接");
                            //如果是扫描过来重新连接的，不回调设备发现
                            reConnectGetDevice = true;
                            connectDevice(bleDevice);
                            stopScan();
                            isScanBleDevice = true;
                        } else {
                            //重连，扫描不到设备
                            if (!isScanBleDevice) {
                                Log.e(TAG, "   重连，扫描不到设备");
                            }
                            isScanBleDevice = true;
                        }
                    } else if (deviceListener != null) {
                        deviceListener.onDeviceFound(bleDevice);
                    }
                }
            }
        }
    }


    @Override
    public void onBleStateChanged(ConnectionState state) {
        Log.e(TAG, "onBleStateChanged================ state :" + state);
        callbackState(state);
    }

    /**
     * 组包
     */
    public abstract DataPacket buildDataPacket(byte packetType, byte messageType, DataPacket.BasePacket basePack);

    public interface DeviceFoundListener {
        void onDeviceFound(BleDevice device);
    }

}