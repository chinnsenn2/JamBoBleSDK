package com.jianbao.jamboble.device;

import com.jianbao.jamboble.data.BloodPressureData;

/**
 * Created by 毛晓飞 on 2016/12/12.
 */

public class FetalHeartLciFMDevice extends BTDevice {

    /**
     *iFM10B2015060784
     */
    private static final long serialVersionUID = 1L;

    public FetalHeartLciFMDevice() {
        super("胎心仪",
                "LciFM",
                "0000fff0-0000-1000-8000-00805f9b34fb",
                "0000fff1-0000-1000-8000-00805f9b34fb");
    }

    @Override
    public BloodPressureData paserData(byte[] data) {
        return null;
    }

    @Override
    public int getImageResource() {
        return 0;
    }
}