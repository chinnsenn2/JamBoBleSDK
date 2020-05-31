package com.jianbao.jamboble.device.oximeter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;

import com.jianbao.jamboble.BluetoothLeService;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zhangmingyao on 2017/8/10 18:00
 * Email:501863760@qq.com
 */

public class OximeterHelper {

    private BluetoothLeService mBluetoothLeService;
    private String mUuidService;
    private String mUuidWriteCharacter;

    public OximeterHelper(BluetoothLeService bluetoothLeService, String uuidService, String uuidWriteCharacter) {
        mBluetoothLeService = bluetoothLeService;
        mUuidService = uuidService;
        mUuidWriteCharacter = uuidWriteCharacter;
    }

    @SuppressLint("NewApi")
    public void write(byte[] bytes) {
        synchronized (this) {
            if(mBluetoothLeService!=null){
                BluetoothGattCharacteristic chara = mBluetoothLeService.getGattCharacteristic(
                        UUID.fromString(mUuidService),
                        UUID.fromString(mUuidWriteCharacter));
                if(chara !=null){
                    mBluetoothLeService.write(chara,bytes);
                }
            }
        }
    }


    public int read(byte[] bytes) throws IOException {
        int temp=0;
        if(mBluetoothLeService!=null){
            temp= readBuffer(bytes);
        }
        return temp;
    }


    public void clean() {

        cleanBuffer();
    }

    public int available() throws IOException {
       return bufferAvailable();
    }


    /**
     *
     * 将数据写入缓存
     *
     */
    private LinkedBlockingQueue<byte[]> mBuffer = new LinkedBlockingQueue<byte[]>();

    public int readBuffer(byte[] dataBuffer){
        if(mBuffer.size()>0){
            byte[] temp = mBuffer.poll();
            if(temp!=null && temp.length>0){
                int len = (temp.length < dataBuffer.length)? temp.length:dataBuffer.length;
                for(int j=0;j<len;j++){
                    dataBuffer[j]=temp[j];
                }
                return len;
            }
        }
        return 0;
    }

    public void cleanBuffer() {
        if(mBuffer!=null){
            mBuffer.clear();
        }
    }

    public int bufferAvailable() throws IOException {
        if(mBuffer!=null){
            return mBuffer.size();
        }
        return 0;
    }

    public void addBuffer(byte[] data){
        if (mBuffer != null) {
            mBuffer.add(data);
        }
    }


}
