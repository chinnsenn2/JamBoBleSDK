package com.medica.xiangshui.jni.phone;

/**
 * Created by admin on 2016/6/14.
 */

public class PhoneAlgorithmIn {
    public short flag_control; // 函数行为控制标志
    // 0:释放计算内存; 1:分配计算内存&初始化;
    // 2:实时信号处理;
    // 3:实时测量完成后续计算总睡眠特征输出;
    // 4:加速度传感器噪声水平提取;
    // 5:摆放位置有效性测试.
    public  float xdata; // 加速度计x轴数据
    public float ydata; // 加速度计y轴数据
    public float zdata; // 加速度计z轴数据
    public  int alarmrange; // 闹铃区间跨度，传入唤醒区间时长秒数
    // <=0:不进行唤醒；>0值:唤醒区间时长，单位:秒；
    public   int calibsecs; // 校准时间。单位: Second.
    public  int num_SDT; // 数据采集起始日期时刻类型值
    public short getFlag_control() {
        return flag_control;
    }
    public void setFlag_control(short flag_control) {
        this.flag_control = flag_control;
    }
    public float getXdata() {
        return xdata;
    }
    public void setXdata(float xdata) {
        this.xdata = xdata;
    }
    public float getYdata() {
        return ydata;
    }
    public void setYdata(float ydata) {
        this.ydata = ydata;
    }
    public float getZdata() {
        return zdata;
    }
    public void setZdata(float zdata) {
        this.zdata = zdata;
    }
    public int getAlarmrange() {
        return alarmrange;
    }
    public void setAlarmrange(int alarmrange) {
        this.alarmrange = alarmrange;
    }
    public int getCalibsecs() {
        return calibsecs;
    }
    public void setCalibsecs(int calibsecs) {
        this.calibsecs = calibsecs;
    }
    public int getNum_SDT() {
        return num_SDT;
    }
    public void setNum_SDT(int num_SDT) {
        this.num_SDT = num_SDT;
    }

}
