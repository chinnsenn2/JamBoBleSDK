package com.jianbao.jamboble.device;

import com.jianbao.jamboble.data.UricAcidData;
import com.jianbao.jamboble.common.ValueCast;

/**
 * Created by zhangmingyao
 * date: 2018/2/26.
 * Email:501863760@qq.com
 */

public class BCUricAcid extends BeneCheckThreeInOne{

    public BCUricAcid() {
        super("百捷三合一尿酸仪");
    }

    @Override
    public UricAcidData paserData(byte[] data) {

        //血糖数据解析
        //示列：
        //尿酸：06 51 00 E3 07 04 04 06 22 1B 89 A1 11
        //胆固醇：06 61 00 E3 07 04 04 09 28 1B 1D B1 11
        if (data != null && data.length == 13) {
            if (data[0] == 0x06) {
                if (data[1] == 0x51) { //尿酸

                    int year = ((0xFF & data[3])) | ((0xFF & data[4]) << 8);
                    int month = (0xFF & data[5]);
                    int day = (0xFF & data[6]);

                    int hour = (0xFF & data[7]);
                    int minute = (0xFF & data[8]);

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
