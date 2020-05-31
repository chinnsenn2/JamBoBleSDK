package com.jianbao.jamboble.device;


import com.jianbao.jamboble.data.BTData;
import com.jianbao.jamboble.data.BTWriteData;
import com.jianbao.jamboble.data.BloodPressureData;

/**
 * 攀高血压计
 * Created by 毛晓飞 on 2017/7/24.
 */

public class PanGaoBloodPressure extends BTDevice {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public PanGaoBloodPressure() {
        super("攀高血压计",
                "ClinkBlood",
                "00005970-6D75-4753-5053-676E6F6C7553",
                "02005970-6D75-4753-5053-676E6F6C7553",
                "01005970-6D75-4753-5053-676E6F6C7553");
    }

    @Override
    public BTData paserData(byte[] data) {
        //血压数据解析
        //示列：68 83 00 05 00 08 31 A9 75 45 74 16
        if (data != null) {
            final int length = data.length;
            //04 3A B1 EF
            if (length > 4 && data[0]==0x05 && data[1] == 0x55 && data[2] == 0x3A && data[3] == (byte)0xA3 && data[4] == 0x37) {
                BTWriteData wData = new BTWriteData();
                wData.command = new byte[]{0x04, (byte) 0xA3, (byte) 0xA0, 0x47};
                return wData;
            } else if (length>2 && data[0]==0x08 && data[1] == 0x3A && data[2] == (byte)0xB8) {
                BloodPressureData _data = new BloodPressureData();
                _data.systolicPressure = 0xFF & data[4];
                _data.diastolicPressure = 0xFF & data[5];
                _data.heartRate = 0xFF & data[6];
                return _data;
            }
        }
        return null;
    }

    @Override
    public boolean needWriteCommand() {
        return true;
    }

    public byte[] getStartCommand() {
        return new byte[]{0x04, 0x55, (byte) 0xAA, 0x03};
    }

    @Override
    public int getImageResource() {
        return -1;
    }
}
