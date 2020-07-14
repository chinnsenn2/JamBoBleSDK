/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jianbao.jamboble.device;

import android.text.TextUtils;

import com.jianbao.fastble.data.BleDevice;
import com.jianbao.jamboble.device.oximeter.OximeterDevice;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class BTDeviceSupport {
    public enum DeviceType {
        BLOOD_PRESSURE,
        BLOOD_SUGAR,
        FAT_SCALE,
        URIC_ACID,
        WRIST_BANDS,
        OXIMETER,
        SLEEPLIGHT,
        THREEONONE,
        FETAL_HEART
    }

    public static HashMap<String, BTDevice> mBloodPressureDevice
            = new HashMap<>();

    public static HashMap<String, BTDevice> mBloodSugarDevice
            = new HashMap<>();

    public static HashMap<String, BTDevice> mFatScaleDevice
            = new HashMap<>();

    public static HashMap<String, BTDevice> mUricAcidDevice
            = new HashMap<>();

    public static HashMap<String, BTDevice> mWristBandDevice
            = new HashMap<>();

    public static HashMap<String, BTDevice> mOximeterDevice
            = new HashMap<>();

    public static HashMap<String, BTDevice> mThreeOnOneDevice
            = new HashMap<>();

    public static HashMap<String, BTDevice> mFetalHeartDevice
            = new HashMap<>();


    /**
     * 设备型号定义
     *
     2-海尔血压计
     3-鱼跃血压计
     4-捷美瑞血压计
     5-鱼跃血压计（Yuwell BP-YE680A）
     6-攀高血压仪
     7-宝莱特血压计
     =======
     2-三诺血糖仪
     3-鱼跃血糖仪
     =======
     2-云康宝体脂秤（Yolanda-CS20F2）
     3-云康宝体脂秤（Yolanda-CS10C1）
     4-云康宝体脂秤（Yolanda-CS20G2）
     =======
     2-百捷尿酸检测仪
     =======
     1-手机
     2-豚鼠手环
     =======
     2-科瑞康血氧仪 PC-60NW-1
     3-科瑞康血氧仪 POD
     4-科瑞康血氧仪 PC-68B
     */
    static {
        //血压设备
        BTDevice device = new CigiiBloodPressure();
        device.setBTDeviceID(4);
        mBloodPressureDevice.put(device.deviceName, device);

        device = new YuwellBloodPressureV2();
        device.setBTDeviceID(5);
        mBloodPressureDevice.put(device.deviceName, device);

        device = new  PanGaoBloodPressure();
        device.setBTDeviceID(6);
        mBloodPressureDevice.put(device.deviceName, device);

        device = new YuwellBloodPressureV1();
        device.setBTDeviceID(3);
        mBloodPressureDevice.put(device.deviceName, device);

        device = new HaierBloodPressure();
        device.setBTDeviceID(2);
        mBloodPressureDevice.put(device.deviceName, device);

        device = new BltBloodPressureDevices();
        device.setBTDeviceID(7);
        mBloodPressureDevice.put(device.deviceName, device);

        //血糖设备
        device = new SannuoBloodSugar();
        device.setBTDeviceID(2);
        mBloodSugarDevice.put(device.deviceName, device);

        device = new YuwellBloodSugar();
        device.setBTDeviceID(3);
        mBloodSugarDevice.put(device.deviceName, device);

        device = new SannuoAnWenBloodSugar();
        device.setBTDeviceID(4);
        mBloodSugarDevice.put(device.deviceName, device);

        device = new OnCallBloodSugar();
        device.setBTDeviceID(5);
        mBloodSugarDevice.put(device.deviceName, device);

        device = new BCBloodSugar();
        device.setBTDeviceID(6);
        mBloodSugarDevice.put(device.deviceName, device);

//		device = new BCBloodSugar();
//		mBloodSugarDevice.put(device.deviceName, device);

        //脂肪秤
        device = new YolandaFatScale();
        device.setBTDeviceID(2);
        mFatScaleDevice.put(device.deviceName, device);

        device = new YolandaFatScale();
        device.deviceName = "QN-Scale";
        device.setBTDeviceID(5);
        mFatScaleDevice.put(device.deviceName, device);

        device = new YolandaFatScale20G2();
        device.setBTDeviceID(4);
        mFatScaleDevice.put(device.deviceName, device);

        device = new YolandaFatScale10C1();
        device.setBTDeviceID(3);
        mFatScaleDevice.put(device.deviceName, device);

        ///尿酸检测仪
        device = new BeneCheckUa();
        device.setBTDeviceID(2);
        mUricAcidDevice.put(device.deviceName, device);

        device = new BCUricAcid();
        device.setBTDeviceID(3);
        mUricAcidDevice.put(device.deviceName, device);

        //=====三合一======
        device = new BeneCheckUa();
        device.setBTDeviceID(2);
        mThreeOnOneDevice.put(device.deviceName, device);

        device = new BCUricAcid();
        device.setBTDeviceID(3);
        mThreeOnOneDevice.put(device.deviceName, device);

        device = new BeneCheckCholestenone();
        device.setBTDeviceID(4);
        mThreeOnOneDevice.put(device.deviceName, device);
        //===========
        //手环
        device = new CavyBandDevice();
        device.setBTDeviceID(2);
        mWristBandDevice.put(device.deviceName, device);
        device = new LakalaBandDevice();
        device.setBTDeviceID(3);
        mWristBandDevice.put(device.deviceName, device);
        //血氧仪
        device = new OximeterDevice();
        device.setBTDeviceID(2);
        device.deviceName = OximeterDevice.OximeterName.PC60.getName();
        mOximeterDevice.put(device.deviceName, device);

        device = new OximeterDevice();
        device.setBTDeviceID(3);
        device.deviceName = OximeterDevice.OximeterName.POD.getName();
        mOximeterDevice.put(device.deviceName, device);

        device = new OximeterDevice();
        device.setBTDeviceID(4);
        device.deviceName = OximeterDevice.OximeterName.PC_68B.getName();
        mOximeterDevice.put(device.deviceName, device);

        device = new OximeterDevice("科瑞康血氧仪",
                "PC-60F",
                "6E400001-B5A3-F393-E0A9-E50E24DCCA9E",
                "6E400003-B5A3-F393-E0A9-E50E24DCCA9E",
                "6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
        device.setBTDeviceID(5);
        device.deviceName = OximeterDevice.OximeterName.PC_60F.getName();
        mOximeterDevice.put(device.deviceName, device);

        device = new OximeterDevice();
        device.setBTDeviceID(6);
        device.deviceName = OximeterDevice.OximeterName.POD2.getName();
        mOximeterDevice.put(device.deviceName, device);

        device = new FetalHeartiFMDevice();
        device.setBTDeviceID(2);
        mFetalHeartDevice.put(device.deviceName, device);

        device = new FetalHeartLciFMDevice();
        device.setBTDeviceID(3);
        mFetalHeartDevice.put(device.deviceName, device);
    }

    /**
     * 判断是不是我们支持的硬件设备
     *
     * @param device
     * @param deviceType
     * @return
     */
    public static BTDevice checkSupport(BleDevice device, DeviceType deviceType) {
        if (TextUtils.isEmpty(device.getName())) {
            return null;
        }
        if (deviceType == DeviceType.BLOOD_PRESSURE) {
            if (mBloodPressureDevice.containsKey(device.getName())) {
                return mBloodPressureDevice.get(device.getName());
            }
        }

        if (deviceType == DeviceType.BLOOD_SUGAR) {
            Set<Map.Entry<String, BTDevice>> entrySet = mBloodSugarDevice.entrySet();
            for (Map.Entry<String, BTDevice> entry : entrySet) {
                if (device.getName().contains(entry.getKey())) {
                    return mBloodSugarDevice.get(entry.getKey());
                }
            }
        }

        if (deviceType == DeviceType.FAT_SCALE) {
            if (mFatScaleDevice.containsKey(device.getName())) {
                return mFatScaleDevice.get(device.getName());
            }
        }

        if (deviceType == DeviceType.URIC_ACID) {
            Set<Map.Entry<String, BTDevice>> entrySet = mUricAcidDevice.entrySet();
            for (Map.Entry<String, BTDevice> entry : entrySet) {
                if (device.getName().contains(entry.getKey())) {
                    return mUricAcidDevice.get(entry.getKey());
                }
            }
        }

        if (deviceType == DeviceType.WRIST_BANDS) {
            Set<Map.Entry<String, BTDevice>> entryseSet = mWristBandDevice.entrySet();
            for (Map.Entry<String, BTDevice> entry : entryseSet) {
                if (device.getName().contains(entry.getKey())) {
                    return mWristBandDevice.get(entry.getKey());
                }
            }
        }

        if (deviceType == DeviceType.OXIMETER) {
            Set<Map.Entry<String, BTDevice>> entryseSet = mOximeterDevice.entrySet();
            for (Map.Entry<String, BTDevice> entry : entryseSet) {
                if (TextUtils.equals(entry.getKey(), OximeterDevice.OximeterName.PC_60F.getName())) {
                    if (device.getName().contains(entry.getKey())) {
                        return entry.getValue();
                    }
                } else {
                    if (device.getName().startsWith(entry.getKey())) {
                        return entry.getValue();
                    }
                }
            }
        }

        if (deviceType == DeviceType.THREEONONE) {
            Set<Map.Entry<String, BTDevice>> entrySet = mThreeOnOneDevice.entrySet();
            for (Map.Entry<String, BTDevice> entry : entrySet) {
                if (device.getName().contains(entry.getKey())) {
                    return mThreeOnOneDevice.get(entry.getKey());
                }
            }
        }

        if (deviceType == DeviceType.FETAL_HEART) {
            Set<Map.Entry<String, BTDevice>> entryseSet = mFetalHeartDevice.entrySet();
            for (Map.Entry<String, BTDevice> entry : entryseSet) {
                if (device.getName().startsWith(entry.getKey())) {
                    return mFetalHeartDevice.get(entry.getKey());
                }
            }
        }

        if (deviceType == DeviceType.SLEEPLIGHT) {
            String deviceId = formatDeviceID(device.getScanRecord());
            if (!TextUtils.isEmpty(deviceId)) {
                return new NoxSleepLightDevice();
            }
        }

        return null;
    }

    /**
     * 判断是不是云康宝的设备
     *
     * @param btDevice
     * @return
     */
    public static boolean isYolandaFatScale(BTDevice btDevice) {
        if (btDevice != null) {
            return btDevice instanceof YolandaFatScale;
        }
        return false;
    }

    public static boolean isFetalHeart(BTDevice btDevice) {
        return btDevice instanceof FetalHeartiFMDevice || btDevice instanceof FetalHeartLciFMDevice;
    }

    public static boolean isMiaoSdk(BTDevice btDevice) {
        if (btDevice != null) {
            return btDevice instanceof LakalaBandDevice;
        }

        return false;
    }

    /**
     * 格式化设备ID
     *
     * @param scanRecord
     * @return
     */
    public static String formatDeviceID(byte[] scanRecord) {
        String deviceId = null;
        String str = getBleDeviceName(0xff, scanRecord);
        if (str != null) {
            //902B是之前做的，还是加-
            if (str.length() >= 13 && !str.startsWith("SN21")) {
                deviceId = str;
            } else {
                deviceId = str.substring(0, 2).toUpperCase() + "-" + str.substring(2);
            }
        }
        return deviceId;
    }

    public static String getBleDeviceName(int type, byte[] record) {
        byte[] data = null;
        int index = 0;
        while (index < record.length) {
            int len = record[index] & 0xFF;
            int tp = record[index + 1] & 0xFF;
            if (index + len + 1 > 31) {
                break;
            } else if (len == 0) {
                break;
            }
            if (type == tp) {
                data = new byte[len - 1];
                for (int i = 0; i < len - 1; i++) {
                    data[i] = record[index + 2 + i];
                }
                break;
            }
            index += (len + 1);
        }

        if (data != null) {
            return new String(data);
        }
        return null;
    }
}