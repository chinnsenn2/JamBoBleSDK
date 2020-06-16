package com.jianbao.jamboble.device;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattDescriptor;

import com.jianbao.jamboble.utils.LogUtils;
import com.jianbao.jamboble.data.BTData;
import com.jianbao.jamboble.data.BTWriteData;
import com.jianbao.jamboble.data.FatScaleData;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * 云康宝脂肪称
 *
 * @author 毛晓飞
 */
public class YolandaFatScale extends BTDevice {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private boolean mMark1 = false; //判断收到了结果数据包1
    private boolean mMark2 = false; //判断收到了结果数据包2
//    private int mScaleDivision = 0;

    private byte mAge = 0x20;
    private byte mHeight = (byte) 0xB0;
    private byte mSex = 0x00;

    public YolandaFatScale() {
        super("云康宝体脂秤",
                "Yolanda-CS20F2",
                "0000feb3-0000-1000-8000-00805f9b34fb",
                "0000fed6-0000-1000-8000-00805f9b34fb",
                "0000fed5-0000-1000-8000-00805f9b34fb");
    }

    /**
     * 需要输入年龄，身高，性别
     *
     * @param age
     * @param height
     * @param sex
     */
    public void setParams(int age, int height, int sex) {
        //待完善
        if (age > 0) {
            mAge = (byte) age;
        }
        if (height > 0) {
            mHeight = (byte) height;
        }
        mSex = (byte) sex;
    }

    @Override
    public BTData paserData(byte[] data) {
        if (data[0] == 0x02) {
            int cmd = 0xFF & data[3];
            int cnt = 0xFF & data[4];
            int deviceType = 0xFF & data[5];  //DeviceType：0x15，代表脂肪秤
            LogUtils.d("Test", "displayData cmd = " + cmd + ", cnt = " + cnt + ", deviceType = " + deviceType);

            //第一个收到的数据包
            if (cmd == 0x12 && cnt == 0x0F && deviceType == 0x15 && data[15] == 0x00) {
                //若秤端分辨度为0.1kg，则APP收到重量数据后除以10；若秤端分辨度为0.01kg，则APP收到重量数据后除以100.
//				byte[] macAddress = new byte[6]; //代表BLE模块的mac地址
//				for (int i=0; i<6; i++){
//					macAddress[i] = data[6 + i];
//				}

                //int scaleVer = 0xFF & data[12]; //秤体端软件版本(01,02,03...表示)
//                int scaleDivision = 0xFF & data[13]; //ScaleDivision：秤端重量分辨度(00：0.1kg；01：0.01kg)
                //int bleVer =  0xFF & data[14];//蓝牙端软件版本(01,02,03....表示)
                //int scaleStatus =  0xFF & data[15];//秤体状态(00-开机,01-关机)；
                //int storeCount =  0xFF & data[16];//本地存储测量笔数。
//                mScaleDivision = scaleDivision;

                byte[] wByte = new byte[12];
                wByte[0] = 0x02;
                wByte[1] = 0x00;
                wByte[2] = 0x09;
                wByte[3] = 0x13;
                wByte[4] = 0x09;
                wByte[5] = 0x15;
                wByte[6] = 0x01; //用来设置秤体端单位。01：kg；02：lb
                wByte[7] = 0x14; //用来设置秤体端结果显示关屏时间，0.5S为计数单位。最小值10，最大值20（即5 - 10S）
                wByte[8] = mHeight; //用来下传用户的身高参数，单位cm，分辨度1cm（80-220cm）
                wByte[9] = mAge; //用来下传用户的年龄参数，分辨度1岁（18-80岁）
                wByte[10] = mSex; //用来下传用户的性别参数，男--0x00，女--0x01
                wByte[11] = 0; //checksum
                for (int i = 0; i < 8; i++) {
                    wByte[11] = (byte) (wByte[11] + wByte[3 + i]);
                }

                BTWriteData wData = new BTWriteData();
                wData.command = wByte;
                return wData;
            }

            //第二个收到的数据包
            if (cmd == 0x14 && cnt == 0x0B && deviceType == 0x15) {
                int requireID = 0xFF & data[8]; //RequireID：01h，表示设置成功；00h，表示设置失败

                if (requireID == 0x01) {
                    //设置时间
                    byte[] wByte = new byte[12];
                    wByte[0] = 0x02;
                    wByte[1] = 0x00;
                    wByte[2] = 0x08;
                    wByte[3] = 0x20;
                    wByte[4] = 0x08;
                    wByte[5] = 0x15;

                    //UTC时间，以2000年为基准，与现在时间的差值秒和，低字节在前
                    Calendar calendar = Calendar.getInstance();
                    calendar.clear();
                    calendar.set(Calendar.YEAR, 2000);
                    long millsSecond = calendar.getTimeInMillis();

                    calendar = Calendar.getInstance();
                    calendar.clear();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    long duration = calendar.getTimeInMillis() - millsSecond;

                    byte[] time = int2Bytes((int) (duration / 1000));
                    wByte[6] = time[0];
                    wByte[7] = time[1];
                    wByte[8] = time[2];
                    wByte[9] = time[3];
                    wByte[10] = 0; //checksum
                    for (int i = 0; i < 7; i++) {
                        wByte[10] = (byte) (wByte[10] + wByte[3 + i]);
                    }

                    BTWriteData wData = new BTWriteData();
                    wData.command = wByte;
                    return wData;
                }
            }

            //收到的第三个数据包
            if (cmd == 0x21 && cnt == 0x05 && deviceType == 0x15) {
                //int retvalue = 0xFF & data[6]; //1，设置成功；0，设置失败
            }

            //收到的测量数据包
            if (cmd == 0x10 && cnt == 0x11 && deviceType == 0x15) {
                //0x00：表示实时的重量数据(RequireID 后面的数据无需解析)；0x01，表示测量完毕，上传的第一包结果数据(RequireID后面的数据有效)
                //0x02，上传的第二包结果数据(RequireID后面的数据有效)

                int requireID = 0xFF & data[8];
                if (requireID == 0x00) {//数值在变化中

                } else if (requireID == 0x01) {//数值稳定
                    mMark1 = true;
                    int weight = (0xFF & data[6]) << 8 | (0xFF & data[7]); //体重
                    int fat = (0xFF & data[13]) << 8 | (0xFF & data[14]); //体脂， 除以10，算百分比
                    int tbw = (0xFF & data[15]) << 8 | (0xFF & data[16]); //水分， 除以10，算百分比
                    int mus = (0xFF & data[17]) << 8 | (0xFF & data[18]); //肌肉， 除以10，算百分比
                    LogUtils.d("Test", "displayData weight = " + weight + ", fat = " + fat + ", tbw = " + tbw + ", mus =" + mus);

                    //返回测量数据
                    FatScaleData fatData = new FatScaleData();
                    fatData.weight = makePrecision(1.0f * weight / 10);
                    fatData.fat = makePrecision(1.0f * fat / 10);
                    fatData.tbw = makePrecision(1.0f * tbw / 10);
                    fatData.deviceID = getBTDeviceID();
                    //fatData.mus = makePrecision(1.0f * mus / 10);
                    return fatData;

                } else if (requireID == 0x02) {
                    mMark2 = true;

                    if (mMark1 && mMark2) {
                        mMark1 = false;
                        mMark2 = false;

                        byte[] wByte = new byte[8];
                        wByte[0] = 0x02;
                        wByte[1] = 0x00;
                        wByte[2] = 0x05;
                        wByte[3] = 0x1F;
                        wByte[4] = 0x05;
                        wByte[5] = 0x15;
                        wByte[6] = 0x10;
                        wByte[7] = 0; //checksum
                        for (int i = 0; i < 4; i++) {
                            wByte[7] = (byte) (wByte[7] + wByte[3 + i]);
                        }

                        BTWriteData wData = new BTWriteData();
                        wData.command = wByte;
                        return wData;
                    }
                }
            }
        }
        return null;
    }


    @SuppressLint("NewApi")
    public byte[] getDescriptorEnabledValue() {
        return BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
    }

    /**
     * 低位从0开始
     *
     * @param num
     * @return
     */
    public static byte[] int2Bytes(int num) {
        byte[] byteNum = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = i * 8;
            byteNum[i] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }

    private float makePrecision(float value) {
        float ret = 0.0f;
        try {
            BigDecimal b = new BigDecimal(value);
            ret = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();  //保留1位小数，四舍五入
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public int getImageResource() {
        return 0;
    }
}