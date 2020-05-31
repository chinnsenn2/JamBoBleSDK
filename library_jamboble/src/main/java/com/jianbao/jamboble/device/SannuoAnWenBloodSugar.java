package com.jianbao.jamboble.device;

import com.jianbao.jamboble.R;
import com.jianbao.jamboble.data.BloodSugarData;

/**
 * 三诺血糖仪
 *
 * @author 毛晓飞
 */
public class SannuoAnWenBloodSugar extends BTDevice {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public SannuoAnWenBloodSugar() {
        super("三诺血糖仪",
                "BDE_WEIXIN_TTM",
                "0000ffb0-0000-1000-8000-00805f9b34fb",
                "0000ffb1-0000-1000-8000-00805f9b34fb",
                "0000ffb2-0000-1000-8000-00805f9b34fb");
    }

    @Override
    public BloodSugarData paserData(byte[] data) {


        //血糖数据解析
        // 8.6 mmol/L
        //示列：53 4E 12 00 12 04 11 05 10 02 04 1D 00 56 00 01 1E 09 00 00
        if (data != null) {
            if (data[0] == 0x53 && data[1] == 0x4E && data[2] == 0x12) {
                //血糖
                float bloodSugar;
                if (data[19] == 0x00) {
                    bloodSugar = (float) (((0xFF00 & (data[12] << 8)) | data[13]) / 10.0);
                } else {
                    bloodSugar = (float) (((0xFF00 & (data[12] << 8)) | data[13]) / 10.0 / 18.0);
                }
                BloodSugarData btData = new BloodSugarData();
                btData.bloodSugar = bloodSugar;
                btData.setDeviceID(getBTDeviceID());
                return btData;
            }
        }
        return null;
    }

    @Override
    public int getImageResource() {
        return R.drawable.blood_pressure_blood_glucose_meter;
    }
}