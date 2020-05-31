package com.jianbao.jamboble.device;

import com.jianbao.jamboble.R;
import com.jianbao.jamboble.data.BTData;
import com.jianbao.jamboble.data.BloodPressureData;

/**
 * Created by zhangmingyao on 2017/4/20 14:47
 * Email:501863760@qq.com
 */

public class CigiiBloodPressure extends BTDevice{

    public static final String TAG = CigiiBloodPressure.class.getName();

    public CigiiBloodPressure() {
        super("捷美瑞血压计",
                "Technaxx BP",
                "000018f0-0000-1000-8000-00805f9b34fb",
                "00002af0-0000-1000-8000-00805f9b34fb");
    }

    @Override
    public BTData paserData(byte[] data) {
        if(data != null && data.length==17){

            if(data[4]==0x1c){
                // 收缩压
                int systolicPressure =  (0xFF & data[5]) << 8 | (0xFF & data[6]);
                // 舒张压
                int diastolicPressure = (0xFF & data[7]) << 8 | (0xFF & data[8]);
                // 心率
                int heartRate = (0xFF & data[11]) << 8 | (0xFF & data[12]);

                BloodPressureData btData = new BloodPressureData();
                btData.systolicPressure = systolicPressure;
                btData.diastolicPressure = diastolicPressure;
                btData.heartRate = heartRate;
                btData.setDeviceID(getBTDeviceID());
                return btData;
            }

        }
        return null;
    }

    @Override
    public int getImageResource() {
        return R.drawable.blood_pressure_sphygmomanometer_one;
    }

}
