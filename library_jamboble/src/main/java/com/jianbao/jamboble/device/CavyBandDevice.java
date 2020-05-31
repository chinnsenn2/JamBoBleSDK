package com.jianbao.jamboble.device;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattDescriptor;

import com.jianbao.jamboble.LogUtils;
import com.jianbao.jamboble.data.BTData;
import com.jianbao.jamboble.data.CavyBandData;
import com.jianbao.jamboble.data.CavyBatteryData;
import com.jianbao.jamboble.data.CavyRealData;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by 毛晓飞 on 2016/12/6.
 */

public class CavyBandDevice extends BTDevice {
    private static final byte PACKET_DATA_SYSTEM = (byte) 0xC1;
    private static final byte PACKET_DATA_DATA_SYNC = (byte) 0xDA;
    private static final byte PACKET_DATA_BATTERY = (byte) 0xB1;

    private HashMap<Integer, CavyBandData> mYestodayData = new HashMap<Integer, CavyBandData>();
    private HashMap<Integer, CavyBandData> mTodayData = new HashMap<Integer, CavyBandData>();
    /**
     *
     */
    private static final long serialVersionUID = 1L;


    public final static String DEVICE_NAME = "Cavy";

    public CavyBandDevice() {
        super("JAMBO体感手环",
                DEVICE_NAME,
                "14839AC4-7D7E-415C-9A42-167340CF2339",
                "0734594A-A8E7-4B1A-A6B1-CD5243059A57",
                "8B00ACE7-EB0B-49B0-BBE9-9AEE0A26E1A3");
        initCavyBandData();
    }

    /**
     * 初始化手环数据
     */
    private void initCavyBandData() {
        for (int i = 1; i < 145; i++) {
            CavyBandData temp = new CavyBandData();
            temp.time = i;
            temp.dayType = 1;
            mYestodayData.put(i, temp);
            CavyBandData temp2 = new CavyBandData();
            temp2.time = i;
            temp2.dayType = 2;
            mTodayData.put(i, temp2);
        }
    }

    @Override
    public BTData paserData(byte[] data) {
        if (data != null) {

            if (data[1] == PACKET_DATA_SYSTEM) {
                if ((0xFF & data[3]) != 118) {//表示手环第一次使用
                    enableSystemFunction();
                }
                enableCavyBandMode();
                setDateTime();
                getBattery();
                syncBandData(1, 0);
            }

            if (data[1] == PACKET_DATA_BATTERY) {
                /*返回数据格式：
                $ 0xB1 [contain] [voltage msb] [voltage lsb] [status] B[6]~B[16]
                contain : battery life time
                voltage : 256*[voltage msb] + [voltage lsb]
                status : 3 = good (life time > 60%)
                2 = normal (life time between10%~60%)
                1 = low (life time <10%) )
                B[16] : checksum*/
                int batteryPercent = 0xFF & data[2];
                CavyBatteryData batteryData = new CavyBatteryData();
                batteryData.batteryPercent = batteryPercent;
                LogUtils.d("Test", "displayData batteryPercent = " + batteryPercent);
                return batteryData;
            }

            if (data[1] == PACKET_DATA_DATA_SYNC && data.length == 20) {
                CavyBandData item = null;
                int dayType = data[2];

                //最后一条数据，第三个和第四个byte均为0xFF，需要过滤掉
                if (!(((0xFF & data[2]) == 0xFF) && ((0xFF & data[3]) == 0xFF))) {
                    for (int i = 0; i < 4; i++) {
                        int time = 0xFF & data[(i + 1) * 4] + 1;
                        int tilts = (0xFF & data[(i + 1) * 4 + 1]);
                        int steps = (0xFF & data[(i + 1) * 4 + 2]) << 8 | (0xFF & data[(i + 1) * 4 + 3]);

                        if (i != 0 && time == 1) {
                            //排除末尾默认无效的数据
                            continue;
                        }

                        if (dayType == 1) {
                            item = mYestodayData.get(time);
                            item.steps = steps;
                            item.tilts = tilts;
                            item.time = time;
                        } else if (dayType == 2) {
                            item = mTodayData.get(time);
                            item.steps = steps;
                            item.tilts = tilts;
                            item.time = time;
                        }
                    }
                }

                //表示同步数据已经结束
                if (((0xFF & data[2]) == 0xFF) && ((0xFF & data[3]) == 0xFF)) {
                    int yesTodayTotalStep = 0;
                    int todayTotalStep = 0;
                    ArrayList<CavyBandData> sleepList = new ArrayList<CavyBandData>();

                    for (int i = 0; i < 144; i++) {
                        //统计昨天的步数
                        CavyBandData temp = mYestodayData.get(i + 1);
                        yesTodayTotalStep = yesTodayTotalStep + temp.steps;

                        //收集前一天晚上21点以后的数据
                        if (i >= 126) {
                            sleepList.add(temp);
                        }
                    }

                    for (int i = 0; i < 144; i++) {
                        //统计当天的步数
                        CavyBandData temp = mTodayData.get(i + 1);
                        todayTotalStep = todayTotalStep + temp.steps;

                        //收集当天早上9点之前的数据
                        if (i < 54) {
                            sleepList.add(temp);
                        }
                    }

                    CavyRealData realData = calculateSleep(sleepList);
                    realData.yesTodayTotalStep = yesTodayTotalStep;
                    realData.todayTotalStep = todayTotalStep;
                    realData.setDeviceID(getBTDeviceID());
                    LogUtils.d("Test", "displayData yesTodayTotalStep = " + yesTodayTotalStep
                            + ", todayTotalStep = " + todayTotalStep);
                    return realData;
                }
            }
        }
        return null;
    }

    /**
     * 计算睡眠结果，深睡眠和浅睡眠
     *
     * @param dlist
     */
    private CavyRealData calculateSleep(ArrayList<CavyBandData> dlist) {
        CavyRealData realData = new CavyRealData();
        int count = 0;     //标志位

        ArrayList<CavyBandData> newlist = new ArrayList<CavyBandData>();
        ArrayList<CavyBandData> finallist = new ArrayList<CavyBandData>();
        if (dlist != null) {
            LogUtils.d("Test", "displayda listSzie = " + dlist.size());
            for (int i = 1; i < dlist.size(); i++) {
                if (dlist.get(i).getTilts() + dlist.get(i).getSteps() == 0) {
                    count++;
                    if (count >= 9) { //已经超过一个半小时
                        if ((i + 1) < dlist.size()) {
                            if ((dlist.get(i + 1).getTilts() + dlist.get(i + 1).getSteps()) != 0) {
                                for (int j = 0; j <= count; j++) {
                                    newlist.add(dlist.get(i - j));
                                }
                                count = 0;
                            }
                        }

//                        if (i == dlist.size()) {
//                            for (int j = 0; j < count; j++) {
//                                newlist.add(dlist.get(i - j));
//                            }
//                            count = 0;
//                        }

                        if (count == dlist.size() - 1) {
                            for (int j = 0; j < count + 1; j++) {
                                newlist.add(dlist.get(i - j));
                            }
                            count = 0;
                        }
                    }
                } else {
                    count = 0;
                }
            }
            dlist.removeAll(newlist);
            LogUtils.d("Test", "displayda  newlistSzie = " + newlist.size());


            //条件1：之前20分钟tilt总量+当前10分钟tilt总量 +之后20分钟tilt总量<40
            //条件2：当前10分钟tilt<15
            //条件3：当前10分钟step<30
            CavyBandData ds20 = null;
            CavyBandData ds10 = null;
            CavyBandData dt10 = null;
            CavyBandData dt20 = null;

            int tilts = 0;
            int steps = 0;
            int time = 0;
            int dayType = 0;
            for (int i = 0; i < dlist.size(); i++) {
                tilts = dlist.get(i).getTilts();
                steps = dlist.get(i).getSteps();
                time = dlist.get(i).getTime(); //time取值范围为[1-144]
                dayType = dlist.get(i).getDayType();//1表示昨天，2表示今天

                if (tilts >= 15 || steps >= 30) {
                    LogUtils.d("Test", "displayda  not allow = " + time + ", tilts =" + tilts + ", steps =" + steps);
                    continue;
                }

                if (dayType == 1) {//昨天的数据
                    //取前20分钟数据
                    ds20 = mYestodayData.get(time - 2);
                    //取前10分钟数据
                    ds10 = mYestodayData.get(time - 1);

                    //取后10分钟数据
                    if (time == 144) {
                        dt10 = mTodayData.get(1);
                    } else {
                        dt10 = mYestodayData.get(time + 1);
                    }

                    //取后20分钟数据
                    if (time == 143) {
                        dt20 = mTodayData.get(1);
                    } else if (time == 144) {
                        dt20 = mTodayData.get(2);
                    } else {
                        dt20 = mYestodayData.get(time + 2);
                    }
                } else if (dayType == 2) {//今天的数据
                    if (time == 1) {
                        ds20 = mYestodayData.get(143);
                    } else if (time == 2) {
                        ds20 = mYestodayData.get(144);
                    } else {
                        ds20 = mTodayData.get(time - 2);
                    }

                    if (time == 1) {
                        ds10 = mYestodayData.get(144);
                    } else {
                        ds10 = mTodayData.get(time - 1);
                    }

                    dt10 = mTodayData.get(time + 1);
                    dt20 = mTodayData.get(time + 2);
                } else {
                    ds20 = null;
                    ds10 = null;
                    dt10 = null;
                    dt20 = null;
                }


                //条件1：之前20分钟tilt总量+当前10分钟tilt总量 +之后20分钟tilt总量<40
                if (ds20 != null && ds10 != null && dt20 != null && dt10 != null) {
                    int tempTilts = ds20.getTilts() + ds10.getTilts() + dt20.getTilts()
                            + dt10.getTilts() + dlist.get(i).getTilts();
                    if (tempTilts < 40) {
                        LogUtils.d("Test", "displayda accept " + time + ", tempTilts =" + tempTilts);
                        finallist.add(dlist.get(i));
                    } else {
                        LogUtils.d("Test", "displayda not accept " + time + ", tempTilts =" + tempTilts);
                    }
                }
            }
        }

        /**
         * finallist 为排除无睡眠状态和满足条件1,2,3,4的值
         */

        int dd = 0;
        for (int i = 0; i < finallist.size(); i++) {
            if (finallist.get(i).getSteps() + finallist.get(i).getTilts() == 0) {
                dd++;
            }
        }

//        d*0.8=深睡时长
//        S-d*0.8=浅睡时长
//        单位都是分钟

        int deepSleep = (int) (dd * 10 * 0.8);
        int lightSleep = finallist.size() * 10 - deepSleep;

        LogUtils.d("Test", "displayda dd" + dd + ", setDeeptime = " + deepSleep + ", setNormaltime =" + lightSleep);

//        SleepRetrun sr = new SleepRetrun();
//        sr.setSleeptime(finallist.size() * 10);
//        sr.setDeeptime(v);
//        sr.setNormaltime(q);
        realData.deepSleep = deepSleep;
        realData.lightSleep = lightSleep;
        return realData;

    }

    @Override
    public int getImageResource() {
        return 0;
    }

    @SuppressLint("NewApi")
    public byte[] getDescriptorEnabledValue() {
        return BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
    }

    public byte[] getStartCommand() {
        String _CmdStr = "?SYSTEM%n";
        try {
            return _CmdStr.getBytes("utf-8");//String.format("%%SYNC=%d,%d\n", 1, 0).getBytes();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询系统功能开启状态，手环出厂时，默认功能都是关闭
     */
    public void querySystemStatus() {
        sendCommand("?SYSTEM%n");
    }

    /**
     * 设置手环的功能为开启状态
     * <p>
     * command:
     * %CFG=func(1~9), enable(0~1)\n
     * ex:
     * %CFG=2,1\n //clock alarm on
     * %CFG=3,1\n //生活手环模式下，断线后震动提示
     * %CFG=4,1\n //Tilt function on
     * %CFG=5,1\n //计步 function on
     * %CFG=6,1\n //connect alert function on
     * <p>
     * 0：X
     * 1: time
     * 2: alarm
     * 3: 断线震动
     * 4：tilt
     * 5: step
     * 6: 连线震动
     * 7：X
     */
    public void enableSystemFunction() {
        int enable = 1;
        for (int i = 2; i <= 6; i++) {
            int functionIndex = i;
            if (i == 3) {//手环断线后，不震动
                sendCommand(String.format("%%CFG=%d,%d%n", functionIndex, 0));
            } else {
                sendCommand(String.format("%%CFG=%d,%d%n", functionIndex, enable));
            }
        }
    }

    /**
     * 设置手环的时间和日期
     */
    public void setDateTime() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;//系统得到的月份要+1
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int realMinute = (hour * 60) + minute;
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        //设置时间
        sendCommand(String.format("%%TIME=%d,%d%n", realMinute, dayOfWeek));
        //设置日期
        sendCommand(String.format("%%DATE=%d,%d,%d%n", year, month, day));
    }

    /**
     * 同步手环的步数
     *
     * @param day:                                               只能设置1或者2，1表示昨天，2表示今天
     * @param startTime：范围为[0-143]，手环一天144条记录，startTime表示取值的起始位置
     */
    public void syncBandData(int day, int startTime) {
        if (day < 1 || day > 2) {
            return;
        }
        if (startTime < 0 || startTime > 143) {
            return;
        }
        sendCommand(String.format("%%SYNC=%d,%d%n", day, startTime));
    }

    /**
     * 获取手环电量
     */
    public void getBattery() {
        sendCommand("?BAT%n");
    }

    /**
     * 手环会有游戏模式，休眠模式，生活模式
     * 设置成2，1，500就是生活模式
     */
    public void enableCavyBandMode() {
        sendCommand(String.format("%%OPR=%d,%d,%d%n", 2, 1, 500));
    }
}
