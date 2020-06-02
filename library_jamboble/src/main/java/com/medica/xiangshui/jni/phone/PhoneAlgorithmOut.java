package com.medica.xiangshui.jni.phone;

import java.util.Arrays;

/**
 * Created by admin on 2016/6/14.
 */

public class PhoneAlgorithmOut {
    String analysVer;// 算法版本

    public String getAnalysVer() {
        return analysVer;
    }

    public void setAnalysVer(String analysVer) {
        this.analysVer = analysVer;
    }

    public short exceptioncode; // 计算异常退出代码
    public short flag_sleepmusic; // 助眠音乐控制标志 (0:不助眠; 1:助眠.)
    public short flag_alarmclock; // 闹铃唤醒控制标志 (0:不唤醒; 1:唤醒.)
    public short flag_placetest; // 摆放位置测试标志 (0:不响铃; 1:响铃.)
    public short monitormins; // 睡眠监测时长(单位:min，最大表示时长:32767 Minutes)
    public short sleepmins; // 有效睡眠时长(单位:min，浅/中/深睡眠时间总和)
    public short sleepefficient; // 睡眠效率(100.0*sleepmins/monitormins)
    public short latencymins; // 入睡时长(单位:min)
    public short wakeminsdurs; // 睡中清醒时长
    public short waketimesdurs; // 睡中清醒次数
    public short getupwakemins; // 起床前清醒时长
    public short wakemins; // 总清醒时长
    public short lightmins; // 总浅睡时长
    public short transmins; // 总中睡时长
    public short deepmins; // 总深睡时长
    public short wakepercent; // 清醒百分比
    public short lightpercent; // 浅睡百分比
    public short transpercent; // 中睡百分比
    public short deeppercent; // 深睡百分比
    public short motionfreq; // 体动频率分布：-2:体动极少；-1:体动较少；0:体动正常；1体动过多；2体动极多

    public short sleepscore; // 睡眠得分（0-100分）
    public short markitem01; // 扣分01:有效睡眠时长（过长:>00；过短:<00）
    public short markitem02; // 扣分项2:入睡时长
    public short markitem03; // 扣分项3:清醒次数
    public short markitem04; // 扣分项4:睡眠效率
    public short markitem05; // 扣分05:良性睡眠（中睡+深睡）占总监测时间百分比
    // 附加:深睡占良性睡眠比率（Ratio = Deeppercent /(Transpercent +
    // Deeppercent)）
    public short markitem06; // 扣分06:体动频率分布（过多:05/10）
    public short markitem07; // 扣分07:晚睡扣分，当天23点过后，第二天12点前，5分/小时线性扣分，上限15分。

    public float[] sleep_curve; // 睡眠状态曲线（输出范围:0-3）
    public short[] sleep_event; // 睡眠事件标志（0x01-入睡点，0x02-起床标志）
    public short[] sleep_stage; // 睡眠状态分期(0-清醒/1-浅睡/2-中睡/3-深睡)
    public short[] motion_intensity;// 体动强度分布（归一化体动强度，输出范围:0-250）
    // 基于Epoch_AbsSum -
    // Min_AbsSum，处理上限Temp_MaxAbsSum_MovRange。
    public short[] motion_density; // 体动密度(基于movsec_epo的每分钟体动秒个数，输出范围:0-60)

    // **加速度计噪声分布**//
    public float Mea_Noise; // 加速度计噪声均值
    public float Min_Noise; // 加速度计噪声偏移

    public short getExceptioncode() {
        return exceptioncode;
    }

    public void setExceptioncode(short exceptioncode) {
        this.exceptioncode = exceptioncode;
    }

    public short getFlag_sleepmusic() {
        return flag_sleepmusic;
    }

    public void setFlag_sleepmusic(short flag_sleepmusic) {
        this.flag_sleepmusic = flag_sleepmusic;
    }

    public short getFlag_alarmclock() {
        return flag_alarmclock;
    }

    public void setFlag_alarmclock(short flag_alarmclock) {
        this.flag_alarmclock = flag_alarmclock;
    }

    public short getFlag_placetest() {
        return flag_placetest;
    }

    public void setFlag_placetest(short flag_placetest) {
        this.flag_placetest = flag_placetest;
    }

    public short getMonitormins() {
        return monitormins;
    }

    public void setMonitormins(short monitormins) {
        this.monitormins = monitormins;
    }

    public short getSleepmins() {
        return sleepmins;
    }

    public void setSleepmins(short sleepmins) {
        this.sleepmins = sleepmins;
    }

    public short getSleepefficient() {
        return sleepefficient;
    }

    public void setSleepefficient(short sleepefficient) {
        this.sleepefficient = sleepefficient;
    }

    public short getLatencymins() {
        return latencymins;
    }

    public void setLatencymins(short latencymins) {
        this.latencymins = latencymins;
    }

    public short getWakeminsdurs() {
        return wakeminsdurs;
    }

    public void setWakeminsdurs(short wakeminsdurs) {
        this.wakeminsdurs = wakeminsdurs;
    }

    public short getWaketimesdurs() {
        return waketimesdurs;
    }

    public void setWaketimesdurs(short waketimesdurs) {
        this.waketimesdurs = waketimesdurs;
    }

    public short getGetupwakemins() {
        return getupwakemins;
    }

    public void setGetupwakemins(short getupwakemins) {
        this.getupwakemins = getupwakemins;
    }

    public short getWakemins() {
        return wakemins;
    }

    public void setWakemins(short wakemins) {
        this.wakemins = wakemins;
    }

    public short getLightmins() {
        return lightmins;
    }

    public void setLightmins(short lightmins) {
        this.lightmins = lightmins;
    }

    public short getTransmins() {
        return transmins;
    }

    public void setTransmins(short transmins) {
        this.transmins = transmins;
    }

    public short getDeepmins() {
        return deepmins;
    }

    public void setDeepmins(short deepmins) {
        this.deepmins = deepmins;
    }

    public short getWakepercent() {
        return wakepercent;
    }

    public void setWakepercent(short wakepercent) {
        this.wakepercent = wakepercent;
    }

    public short getLightpercent() {
        return lightpercent;
    }

    public void setLightpercent(short lightpercent) {
        this.lightpercent = lightpercent;
    }

    public short getTranspercent() {
        return transpercent;
    }

    public void setTranspercent(short transpercent) {
        this.transpercent = transpercent;
    }

    public short getDeeppercent() {
        return deeppercent;
    }

    public void setDeeppercent(short deeppercent) {
        this.deeppercent = deeppercent;
    }

    public short getMotionfreq() {
        return motionfreq;
    }

    public void setMotionfreq(short motionfreq) {
        this.motionfreq = motionfreq;
    }

    public short getSleepscore() {
        return sleepscore;
    }

    public void setSleepscore(short sleepscore) {
        this.sleepscore = sleepscore;
    }


    public short getMarkitem01() {
        return markitem01;
    }

    public void setMarkitem01(short markitem01) {
        this.markitem01 = markitem01;
    }

    public short getMarkitem02() {
        return markitem02;
    }

    public void setMarkitem02(short markitem02) {
        this.markitem02 = markitem02;
    }

    public short getMarkitem03() {
        return markitem03;
    }

    public void setMarkitem03(short markitem03) {
        this.markitem03 = markitem03;
    }

    public short getMarkitem04() {
        return markitem04;
    }

    public void setMarkitem04(short markitem04) {
        this.markitem04 = markitem04;
    }

    public short getMarkitem05() {
        return markitem05;
    }

    public void setMarkitem05(short markitem05) {
        this.markitem05 = markitem05;
    }

    public short getMarkitem06() {
        return markitem06;
    }

    public void setMarkitem06(short markitem06) {
        this.markitem06 = markitem06;
    }

    public short getMarkitem07() {
        return markitem07;
    }

    public void setMarkitem07(short markitem07) {
        this.markitem07 = markitem07;
    }

    public float[] getSleep_curve() {
        return sleep_curve;
    }

    public void setSleep_curve(float[] sleep_curve) {
        this.sleep_curve = sleep_curve;
    }

    public short[] getSleep_event() {
        return sleep_event;
    }

    public void setSleep_event(short[] sleep_event) {
        this.sleep_event = sleep_event;
    }

    public short[] getSleep_stage() {
        return sleep_stage;
    }

    public void setSleep_stage(short[] sleep_stage) {
        this.sleep_stage = sleep_stage;
    }

    public short[] getMotion_intensity() {
        return motion_intensity;
    }

    public void setMotion_intensity(short[] motion_intensity) {
        this.motion_intensity = motion_intensity;
    }

    public short[] getMotion_density() {
        return motion_density;
    }

    public void setMotion_density(short[] motion_density) {
        this.motion_density = motion_density;
    }

    public float getMea_Noise() {
        return Mea_Noise;
    }

    public void setMea_Noise(float mea_Noise) {
        Mea_Noise = mea_Noise;
    }

    public float getMin_Noise() {
        return Min_Noise;
    }

    public void setMin_Noise(float min_Noise) {
        Min_Noise = min_Noise;
    }

    @Override
    public String toString() {
        return "Algorithm_OUT_ACT [analysVer=" + analysVer + ", exceptioncode="
                + exceptioncode + ", flag_sleepmusic=" + flag_sleepmusic
                + ", flag_alarmclock=" + flag_alarmclock + ", flag_placetest="
                + flag_placetest + ", monitormins=" + monitormins
                + ", sleepmins=" + sleepmins + ", sleepefficient="
                + sleepefficient + ", latencymins=" + latencymins
                + ", wakeminsdurs=" + wakeminsdurs + ", waketimesdurs="
                + waketimesdurs + ", getupwakemins=" + getupwakemins
                + ", wakemins=" + wakemins + ", lightmins=" + lightmins
                + ", transmins=" + transmins + ", deepmins=" + deepmins
                + ", wakepercent=" + wakepercent + ", lightpercent="
                + lightpercent + ", transpercent=" + transpercent
                + ", deeppercent=" + deeppercent + ", motionfreq=" + motionfreq
                + ", sleepscore=" + sleepscore + ", markitem01=" + markitem01
                + ", markitem02=" + markitem02 + ", markitem03=" + markitem03
                + ", markitem04=" + markitem04 + ", markitem05=" + markitem05
                + ", markitem06=" + markitem06 + ", markitem07=" + markitem07
                + ", sleep_curve=" + Arrays.toString(sleep_curve)
                + ", sleep_event=" + Arrays.toString(sleep_event)
                + ", sleep_stage=" + Arrays.toString(sleep_stage)
                + ", motion_intensity=" + Arrays.toString(motion_intensity)
                + ", motion_density=" + Arrays.toString(motion_density)
                + ", Mea_Noise=" + Mea_Noise + ", Min_Noise=" + Min_Noise + "]";
    }
}
