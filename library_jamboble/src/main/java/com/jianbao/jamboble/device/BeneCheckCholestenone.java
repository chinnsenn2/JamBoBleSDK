package com.jianbao.jamboble.device;

import com.jianbao.jamboble.data.BTData;
import com.jianbao.jamboble.data.BloodSugarData;
import com.jianbao.jamboble.data.CholestenoneData;
import com.jianbao.jamboble.data.UricAcidData;
import com.jianbao.jamboble.common.ValueCast;

public class BeneCheckCholestenone extends BeneCheckThreeInOne {
    public BeneCheckCholestenone() {
        super("百捷三合一尿酸仪");
    }

    @Override
    public BTData paserData(byte[] data) {
        //胆固醇：06 61 00 E3 07 04 04 09 28 1B 1D B1 11
        if (data != null && data.length == 13) {
            if (data[0] == 0x06) {
                int year = ((0xFF & data[3])) | ((0xFF & data[4]) << 8);
                int month = (0xFF & data[5]);
                int day = (0xFF & data[6]);
                int hour = (0xFF & data[7]);
                int minute = (0xFF & data[8]);
                if (data[1] == 0x61) { //血糖
                    CholestenoneData btData = new CholestenoneData();
                    btData.cholestenone = ValueCast.makePrecision(getResultValue(data), 2);
                    btData.mYear = year;
                    btData.mMonth = month;
                    btData.mday = day;
                    btData.mHour = hour;
                    btData.mMinute = minute;
                    btData.setDeviceID(getBTDeviceID());
                    return btData;
                } else if (data[1] == 0x41) { //血糖
                    BloodSugarData btData = new BloodSugarData();
                    btData.mYear = year;
                    btData.mMonth = month;
                    btData.mday = day;
                    btData.mHour = hour;
                    btData.mMinute = minute;
                    btData.setDeviceID(getBTDeviceID());
                    btData.bloodSugar = ValueCast.makePrecision(getResultValue(data), 1);
                    return btData;
                } else if (data[1] == 0x51) { //尿酸
                    UricAcidData uaData = new UricAcidData();
                    uaData.mYear = year;
                    uaData.mMonth = month;
                    uaData.mday = day;
                    uaData.mHour = hour;
                    uaData.mMinute = minute;
                    uaData.mUricAcid = ValueCast.makePrecision(getResultValue(data), 2);
                    uaData.setDeviceID(getBTDeviceID());
                    return uaData;
                }
            }
        }
        return null;
    }
}
