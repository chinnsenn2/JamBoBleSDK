package com.jianbao.jamboble.data;

import java.io.Serializable;

/**
 * Created by 毛晓飞 on 2016/12/28.
 */

public class CavyRealData extends BTData implements Serializable {

    /**
     * 昨天的运动步数
     */
    public int yesTodayTotalStep;

    /**
     * 今天的运动步数
     */
    public int todayTotalStep;


    /**
     * 浅睡时间
     */
    public int lightSleep;
    /**
     * 深谁时间
     */
    public int deepSleep;
}
