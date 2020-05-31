package com.jianbao.jamboble.device.oximeter;

import com.jianbao.jamboble.data.BTData;
import com.jianbao.jamboble.data.OximeterData;
import com.jianbao.jamboble.device.BTDevice;

/**
 * Created by zhangmingyao on 2017/8/10 10:21
 * Email:501863760@qq.com
 */

public class OximeterDevice extends BTDevice {

    //PC-60NW-1
    //POD
    //PC-68B
    //PC-60F

    public enum OximeterName{
        PC60("PC-60NW-1"),
        POD("POD"),
        POD2(" POD"),
        PC_68B("PC-68B"),
        PC_60F("PC-60F");

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        OximeterName(String name) {
            this.name = name;
        }

    }


    private OximeterHelper mOximeterHelper;

    public OximeterDevice() {
        super("科瑞康血氧仪",
                "PC-60NW-1",
                "0000FFB0-0000-1000-8000-00805f9b34fb",
                "0000FFB1-0000-1000-8000-00805f9b34fb",
                "0000FFB2-0000-1000-8000-00805f9b34fb");
    }

    public OximeterDevice(String brandName,String devicesName,String serviceUUID, String notifyUUID, String writeUUID) {
        super(brandName,devicesName,serviceUUID,notifyUUID,writeUUID);
    }

    @Override
    public BTData paserData(byte[] data) {
        return new OximeterData(data);
    }

    @Override
    public int getImageResource() {
        return 0;
    }

    @Override
    public boolean needWriteCommand() {
        return true;
    }

    public void setOximeterHelper(OximeterHelper oximeterHelper) {
        mOximeterHelper = oximeterHelper;
    }

    public OximeterHelper getOximeterHelper() {
        return mOximeterHelper;
    }

}
