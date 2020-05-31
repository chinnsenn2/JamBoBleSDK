package com.jianbao.jamboble.data;

import java.io.Serializable;

/**
 * Created by 毛晓飞 on 2016/12/28.
 */

public class CavyBatteryData extends BTData implements Serializable {
    /**
     * 电池电量，范围[0-100]
     */
    public int batteryPercent;
}
