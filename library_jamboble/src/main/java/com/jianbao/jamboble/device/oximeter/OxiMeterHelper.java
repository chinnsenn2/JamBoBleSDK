package com.jianbao.jamboble.device.oximeter;

import android.annotation.SuppressLint;

import com.jianbao.fastble.BleManager;
import com.jianbao.fastble.callback.BleWriteCallback;
import com.jianbao.fastble.data.BleDevice;
import com.jianbao.fastble.exception.BleException;
import com.jianbao.jamboble.device.BTDevice;
import com.jianbao.jamboble.utils.LogUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zhangmingyao on 2017/8/10 18:00
 * Email:501863760@qq.com
 */

public class OxiMeterHelper {
    private BleDevice mDevice;
    private BTDevice mBtDevice;

    public OxiMeterHelper(BleDevice device, BTDevice btDevice) {
        this.mDevice = device;
        this.mBtDevice = btDevice;
    }

    @SuppressLint("NewApi")
    public void write(byte[] bytes) {
        synchronized (this) {
            BleManager.getInstance().write(mDevice, mBtDevice.serviceUUID, mBtDevice.writeCharacterUUID, bytes, new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    LogUtils.d("current = " + current + ", total = " + total + ", justWrite = " + Arrays.toString(justWrite));
                }

                @Override
                public void onWriteFailure(BleException exception) {
                    LogUtils.d(exception.getDescription());
                }
            });
        }
    }


    public int read(byte[] bytes) throws IOException {
        return readBuffer(bytes);
    }


    public void clean() {
        cleanBuffer();
    }

    public int available() throws IOException {
        return bufferAvailable();
    }


    /**
     * 将数据写入缓存
     */
    private LinkedBlockingQueue<byte[]> mBuffer = new LinkedBlockingQueue<byte[]>();

    public int readBuffer(byte[] dataBuffer) {
        if (mBuffer.size() > 0) {
            byte[] temp = mBuffer.poll();
            if (temp != null && temp.length > 0) {
                int len = Math.min(temp.length, dataBuffer.length);
                System.arraycopy(temp, 0, dataBuffer, 0, len);
                return len;
            }
        }
        return 0;
    }

    public void cleanBuffer() {
        if (mBuffer != null) {
            mBuffer.clear();
        }
    }

    public int bufferAvailable() throws IOException {
        if (mBuffer != null) {
            return mBuffer.size();
        }
        return 0;
    }

    public void addBuffer(byte[] data) {
        if (mBuffer != null) {
            mBuffer.add(data);
        }
    }


}
