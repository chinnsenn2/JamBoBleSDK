package com.jianbao.jamboble;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.jianbao.jamboble.data.FetalHeartData;
import com.jianbao.jamboble.utils.IoUtils;
import com.jianbao.jamboble.utils.LogUtils;
import com.luckcome.lmtpdecorder.LMTPDecoder;
import com.luckcome.lmtpdecorder.LMTPDecoderListener;
import com.luckcome.lmtpdecorder.data.FhrData;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.UUID;

/**
 * Created by 毛晓飞 on 2016/12/20.
 */

public class FetalHeartEngineService extends Service {

    public static final String FILE_DIR = Environment.getExternalStorageDirectory() + "/com.jianbao.doctor/download/.audio/";

    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int CONNECT_SUCCESS = 1;
    public static final int CONNECT_FAILED = 2;
    public static final int READ_DATA_SUCCESS = 3;
    public static final int READ_DATA_FAILED = 4;

    public static final int READ_INTERVAL = 30;//读取间隔

    /**
     * 消息编号，连接完成
     */
    private static final int MSG_CONNECT_FINISHED = 10;
    private static final int MSG_NOTIFY_STATUS = 11;
    // 蓝牙适配器
    //private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int MSG_NOTIFY_DATA = 12;
    public BluetoothBinder mBinder = new BluetoothBinder();
    // 服务的回调接口
    private Callback mCallback = null;
    // 服务当前连接的蓝牙设备
    private BluetoothDevice mBtDevice;
    // 蓝牙 socket
    private BluetoothSocket mBluetoothSocket = null;
    // 蓝牙输出流
    private OutputStream mOutputStream = null;
    // 是否保存标志
    private boolean isRecord = false;
    /**
     * 蓝牙终端协议解析器
     */
    private LMTPDecoder mLMTPDecoder = null;
    /**
     * 数据解析器回调接口
     */
    private LMTPDListener mLMTPDListener = null;

    private ConnectThread mConnectThread = null;
    private boolean isReading = false;
    private ReadThread mReadThread = null;
    private NotifyHandler mNotifyHandler;

    public static class NotifyHandler extends Handler {
        private WeakReference<FetalHeartEngineService> weakReference;

        public NotifyHandler(FetalHeartEngineService service) {
            weakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            FetalHeartEngineService service = weakReference.get();
            if (weakReference == null) {
                return;
            }
            switch (msg.what) {
                case MSG_CONNECT_FINISHED:
                    //开始同步数据
                    service.isReading = true;
                    service.mReadThread = new ReadThread(service);
                    service.mReadThread.start();
                    break;
                case MSG_NOTIFY_STATUS:
                    if (service.mCallback != null) {
                        LogUtils.d("dispServiceStatus %s", String.valueOf(((int) msg.obj)));
                        service.mCallback.dispServiceStatus((int) msg.obj);
                    }
                    break;
                case MSG_NOTIFY_DATA:
                    if (service.mCallback != null) {
                        service.mCallback.dispInfor((FetalHeartData) msg.obj);
                    }
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        cancel();
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        mLMTPDecoder = new LMTPDecoder();
        mLMTPDListener = new LMTPDListener();
        mLMTPDecoder.setLMTPDecoderListener(mLMTPDListener);
        mLMTPDecoder.prepare();
        mNotifyHandler = new NotifyHandler(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLMTPDecoder != null) {
            mLMTPDecoder.release();
            mLMTPDecoder = null;
        }
        mLMTPDListener = null;
    }

    /**
     * 设置要连接的蓝牙设备
     *
     * @param device
     */
    public void setBluetoothDevice(BluetoothDevice device) {
        mBtDevice = device;
    }

    /**
     * 设置回调接口
     *
     * @param cb
     */
    public void setCallback(Callback cb) {
        mCallback = cb;
    }

    /**
     * 启动连接线程
     */
    public void start() {
        if (mConnectThread == null) {
            mConnectThread = new ConnectThread(mBtDevice);
        }
        mConnectThread.start();
        mLMTPDecoder.startWork();
    }

    /**
     * 停止数据读取和解析
     */
    public void cancel() {
        isReading = false;
        if (mBluetoothSocket != null) {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mConnectThread = null;
        mReadThread = null;
        mLMTPDecoder.stopWork();
    }

    /**
     * 启动记录功能
     */
    public String recordStart() {
        File path = getRecordFilePath();
        String fname = "" + System.currentTimeMillis();

        mLMTPDecoder.beginRecordWave(path, fname);
        isRecord = true;
        return path + "/" + fname + ".wav";//固定此格式
    }

    public static File getRecordFilePath() {
        File path = new File(FILE_DIR);
        if (!path.exists()) {
            try {
                path.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return path;
    }

    /**
     * 结束记录
     */
    public void recordFinished() {
        isRecord = false;
        mLMTPDecoder.finishRecordWave();
    }


    /**
     * 获取记录状态
     *
     * @return
     */
    public boolean getRecordStatus() {
        return isRecord;
    }

    /**
     * 设置宫缩复位
     *
     * @param value 宫缩复位的值
     */
    public void setTocoReset(int value) {
        mLMTPDecoder.sendTocoReset(value);
    }

    /**
     * 设置一次手动胎动
     */
    public void setFM() {
        mLMTPDecoder.setFM();
    }

    /**
     * 设置胎心音量
     *
     * @param value 胎心音量大小
     */
    public void setFhrVolume(int value) {
        mLMTPDecoder.sendFhrVolue(value);
    }

    private void notifyStatus(int status) {
        Message msg = new Message();
        msg.what = MSG_NOTIFY_STATUS;
        msg.obj = status;
        mNotifyHandler.sendMessage(msg);
    }

    private void nitifyData(FetalHeartData data) {
        Message msg = new Message();
        msg.what = MSG_NOTIFY_DATA;
        msg.obj = data;
        mNotifyHandler.sendMessage(msg);
    }

    /**
     * 获取工作状态
     *
     * @return
     */
    public boolean getReadingStatus() {
        return isReading;
    }

    /**
     * 服务的回调接口定义
     */
    public interface Callback {
        /**
         * 主要显示监护数据信息
         *
         * @param data
         */
        void dispInfor(FetalHeartData data);

        /**
         * 主要显示记录状态
         *
         * @param status
         */
        void dispServiceStatus(int status);
    }


    public class BluetoothBinder extends Binder {
        public FetalHeartEngineService getService() {
            return FetalHeartEngineService.this;
        }
    }

    //=====================蓝牙数据解析回调监听========================

    /**
     * 胎心数据解析，回调接口
     *
     * @author Administrator
     */
    private class LMTPDListener implements LMTPDecoderListener {

        /**
         * 有新数据产生的时候回调
         */
        @Override
        public void fhrDataChanged(FhrData fhrData) {
//            String infor = String.format(
//                    "FHR1:%-3d%nTOCO:%-3d%n AFM:%-3d%nSIGN:%-3d%nBATT:%-3d%n"
//                            + "isFHR1:%-3d%nisTOCO:%-3d%n isAFM:%-3d%n",
//                    fhrData.fhr1, fhrData.toco, fhrData.afm, fhrData.fhrSignal, fhrData.devicePower,
//                    fhrData.isHaveFhr1, fhrData.isHaveToco, fhrData.isHaveAfm
//            );
            FetalHeartData data = new FetalHeartData();
            data.fhr1 = fhrData.fhr1;
            data.fhr2 = fhrData.fhr2;
            data.toco = fhrData.toco;
            data.afm = fhrData.afm;
            data.fhrSignal = fhrData.fhrSignal;
            data.afmFlag = fhrData.afmFlag;
            data.fmFlag = fhrData.fmFlag;
            data.tocoFlag = fhrData.tocoFlag;
            data.devicePower = fhrData.devicePower;
            data.isHaveFhr1 = fhrData.isHaveFhr1;
            data.isHaveFhr2 = fhrData.isHaveFhr2;
            data.isHaveToco = fhrData.isHaveToco;
            data.isHaveAfm = fhrData.isHaveAfm;

            if (fhrData.fmFlag != 0)
                Log.v("LMTPD", "LMTPD...1...fm");

            if (fhrData.tocoFlag != 0)
                Log.v("LMTPD", "LMTPD...2...toco");

            nitifyData(data);
        }

        /**
         * 有命令产生的时候回调
         */
        @Override
        public void sendCommand(byte[] cmd) {
            // 这里添加从蓝牙发送数据的代码
            if (mOutputStream != null) {
                try {
                    mOutputStream.write(cmd);
                    mOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //=====================连接蓝牙线程========================

    /**
     * 连接蓝牙线程
     *
     * @author 毛晓飞
     */
    private class ConnectThread extends Thread {
        private BluetoothDevice mDevice;
        private BluetoothSocket tmp = null;

        public ConnectThread(BluetoothDevice device) {
            mDevice = mBtDevice;
        }

        @Override
        public void run() {

            //第一步，获取BluetoothSocket对象
            try {
                tmp = mDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                notifyStatus(CONNECT_FAILED);
            }

            mBluetoothSocket = tmp;
            //mBluetoothAdapter.cancelDiscovery();

            //第二步，BluetoothSocket连接
            try {
                mBluetoothSocket.connect();
                notifyStatus(CONNECT_SUCCESS);
                mNotifyHandler.sendEmptyMessage(MSG_CONNECT_FINISHED);
            } catch (IOException e) {
                notifyStatus(CONNECT_FAILED);
            } catch (Exception e) {
                notifyStatus(CONNECT_FAILED);
            }

            //第三步，获取BluetoothSocket输出流
            try {
                mOutputStream = mBluetoothSocket.getOutputStream();
            } catch (IOException e) {
                mOutputStream = null;
                e.printStackTrace();
            } catch (Exception e) {
                mOutputStream = null;
                e.printStackTrace();
            }
        }

    }

    //=====================读取蓝牙数据线程========================

    /**
     * 读取蓝牙数据线程
     *
     * @author 毛晓飞
     */
    private static class ReadThread extends Thread {

        private WeakReference<FetalHeartEngineService> weakReference;

        public ReadThread(FetalHeartEngineService service) {
            weakReference = new WeakReference<>(service);
        }

        private InputStream mInputStream = null;

        @Override
        public void run() {
            FetalHeartEngineService service = weakReference.get();
            if (service == null) {
                return;
            }
            try {
                //第一步，获取输入流
                mInputStream = service.mBluetoothSocket.getInputStream();
                service.notifyStatus(READ_DATA_SUCCESS);

                //第二步，每30毫秒读取一次数据
                int len;
                byte[] buffer = new byte[2048];
                while (service.isReading) {
                    try {
                        len = mInputStream.read(buffer);

                        //将数据设置到胎心仪解析器
                        service.mLMTPDecoder.putData(buffer, 0, len);
                        try {
                            Thread.sleep(READ_INTERVAL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } catch (IOException e) {
                        service.notifyStatus(READ_DATA_FAILED);
                        service.isReading = false;
                    }
                }

            } catch (IOException e) {
                service.notifyStatus(READ_DATA_FAILED);
                service.isReading = false;
            } finally {
                if (mInputStream != null) {
                    IoUtils.closeSilently(mInputStream);
                }
                //关闭蓝牙套接字
                if (service.mBluetoothSocket != null) {
                    IoUtils.closeSilently(service.mBluetoothSocket);
                }
            }
        }
    }

    //==================================================


}
