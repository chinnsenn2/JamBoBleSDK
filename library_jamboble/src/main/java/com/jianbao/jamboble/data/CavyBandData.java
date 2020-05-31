package com.jianbao.jamboble.data;

import java.io.Serializable;

/**
 * Created by 毛晓飞 on 2016/12/7.
 */

/**
 * 手环每10分钟生成一条记录，一天最多生成144条记录
 */
public class CavyBandData extends BTData implements Serializable {
    /**
     * 1-昨天，2-今天
     */
    public int dayType;
    /**
     * 记录手环倾斜45度的次数
     */
    public int tilts;
    /**
     * 记录的步数
     */
    public int steps;
    /**
     * 记录的Index，[1-144]
     */
    public int time;

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getDayType() {
        return dayType;
    }

    public void setDayType(int dayType) {
        this.dayType = dayType;
    }

    public int getTilts() {
        return tilts;
    }

    public void setTilts(int tilts) {
        this.tilts = tilts;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
