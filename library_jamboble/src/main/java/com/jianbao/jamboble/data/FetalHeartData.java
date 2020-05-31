package com.jianbao.jamboble.data;

/**
 * Created by 毛晓飞 on 2016/12/20.
 */

public class FetalHeartData extends BTData{
    public int fhr1 = 0;
    public int fhr2 = 0;
    public byte toco = 0;
    public byte afm = 0;
    public byte fhrSignal = 0;
    public byte afmFlag = 0;
    public byte fmFlag = 0;
    public byte tocoFlag = 0;
    public byte devicePower = 0;
    public byte isHaveFhr1 = 0;
    public byte isHaveFhr2 = 0;
    public byte isHaveToco = 0;
    public byte isHaveAfm = 0;

    @Override
    public String toString() {
        return "FetalHeartData{" +
                "afm=" + afm +
                ", fhr1=" + fhr1 +
                ", fhr2=" + fhr2 +
                ", toco=" + toco +
                ", fhrSignal=" + fhrSignal +
                ", afmFlag=" + afmFlag +
                ", fmFlag=" + fmFlag +
                ", tocoFlag=" + tocoFlag +
                ", devicePower=" + devicePower +
                ", isHaveFhr1=" + isHaveFhr1 +
                ", isHaveFhr2=" + isHaveFhr2 +
                ", isHaveToco=" + isHaveToco +
                ", isHaveAfm=" + isHaveAfm +
                '}';
    }
}
