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

package com.jianbao.jamboble;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.jianbao.jamboble.data.BTData;
import com.jianbao.jamboble.data.BTWriteData;
import com.jianbao.jamboble.device.BTDevice;
import com.jianbao.jamboble.device.CavyBandDevice;
import com.jianbao.jamboble.device.OnCallBloodSugar;
import com.jianbao.jamboble.device.SannuoAnWenBloodSugar;
import com.jianbao.jamboble.device.oximeter.OxiMeterHelper;
import com.jianbao.jamboble.device.oximeter.OximeterDevice;
import com.jianbao.jamboble.utils.LogUtils;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * For a given BLE device, this Activity provides the user interface to connect,
 * display data, and display GATT services and characteristics supported by the
 * device. The Activity communicates with {@code BluetoothLeService}, which in
 * turn interacts with the Bluetooth LE API.
 */
public class BTControlManager {

    public interface BTControlListener {
        void onConnectChanged(boolean connected);

        void onDataReceived(@Nullable BTData data);

        void onActionNotification();

    }

    public interface ServiceConnectListener {
        void onServiceConnected();
    }

    private final static String TAG = "DeviceControlManager";

    public BluetoothLeService getBluetoothLeService() {
        return mBluetoothLeService;
    }

    private BluetoothLeService mBluetoothLeService;
    private BTDevice mSelectBTDevice;
    private List<BluetoothGattCharacteristic> mNotifyCharacteristics;
    private BluetoothGattCharacteristic mWriteCharacteristic;

    //    private final Context mContext;
    private final WeakReference<Context> mWeakContext;
    private final ExecutorService mExecutorService;
    private BTControlListener mBTControlListener;
    private boolean mHasInit = false;
    private ServiceConnectListener mServiceConnectListener;


    // Code to manage Service lifecycle.
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {
                LogUtils.e("Unable to initialize Bluetooth");
            }

            if (mServiceConnectListener != null) {
                mServiceConnectListener.onServiceConnected();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            dispose();
        }
    };

    public BTControlManager(Context context) {
        mWeakContext = new WeakReference<>(context);
//        mContext = context;
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    public BTControlManager addBtControlListener(BTControlListener btControlListener) {
        mBTControlListener = btControlListener;
        return this;
    }

    public BTControlManager addServiceConnect(ServiceConnectListener serviceConnectListener) {
        mServiceConnectListener = serviceConnectListener;
        return this;
    }

    public BTControlManager init() {
        if (mWeakContext.get() == null) {
            mHasInit = false;
            return this;
        }
        if (!mWeakContext.get().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            mHasInit = false;
            return this;
        }

        // 注册广播监听
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_CHARACTER_NOTIFICATION);
        mWeakContext.get().registerReceiver(mGattUpdateReceiver, intentFilter);

        try {
            // 绑定服务
            Intent gattServiceIntent = new Intent(mWeakContext.get(), BluetoothLeService.class);
            mWeakContext.get().bindService(gattServiceIntent, mServiceConnection, Activity.BIND_AUTO_CREATE);

        } catch (Exception e
        ) {
            System.out.println(e.getMessage());
        }
        mHasInit = true;
        mNotifyCharacteristics = new ArrayList<>();
        return this;
    }

    public void dispose() {
        if (!mHasInit) {
            return;
        }

        if (mWeakContext.get() == null) {
            return;
        }
        // 注销广播监听
        if (mGattUpdateReceiver != null) {
            mWeakContext.get().unregisterReceiver(mGattUpdateReceiver);
            mGattUpdateReceiver = null;
        }

        // 关闭Notification
        if (mNotifyCharacteristics != null && mNotifyCharacteristics.size() > 0 && mBluetoothLeService != null) {
            for (BluetoothGattCharacteristic b : mNotifyCharacteristics) {
                mBluetoothLeService.setCharacteristicNotification(b, false, null);
            }
            mNotifyCharacteristics.clear();
            mNotifyCharacteristics = null;
        }

        // 然后关闭蓝牙连接
        disconnect();

        mWeakContext.get().unbindService(mServiceConnection);
        mServiceConnection = null;
        // 解绑服务
        if (mExecutorService != null) {
            mExecutorService.shutdown();
        }
        mWeakContext.clear();
    }

    /**
     * 连接指定的蓝牙设备
     *
     * @param address
     * @return
     */
    public boolean connect(BTDevice btDevice, String address) {
        setConnectDevice(btDevice);

        if (!mHasInit) {
            return false;
        }

        if (mBluetoothLeService != null) {
            return mBluetoothLeService.connect(address);
        }
        return false;
    }

    /**
     * 设置连接的设备
     *
     * @param btDevice
     */
    public void setConnectDevice(BTDevice btDevice) {
        mSelectBTDevice = btDevice;
    }

    /**
     * 获取已经连上的设备
     *
     * @return
     */
    public BTDevice getConnectDevice() {
        return mSelectBTDevice;
    }

    /**
     * 断开当前连接
     */
    public void disconnect() {
        if (!mHasInit) {
            return;
        }

        if (mSelectBTDevice != null) {
            mSelectBTDevice.setBTControlManager(null);
        }

        if (mBluetoothLeService != null) {
            mBluetoothLeService.disconnect();
        }
    }

    // Demonstrates how to iterate through the supported GATT
    // Services/Characteristics.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    // In this sample, we populate the data structure that is bound to the
    // ExpandableListView
    // on the UI.
    @SuppressLint("NewApi")
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (mSelectBTDevice == null) {
            return;
        }

        if (gattServices == null)
            return;

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            String uuid = gattService.getUuid().toString();
            if (!uuid.equalsIgnoreCase(mSelectBTDevice.serviceUUID) && mSelectBTDevice.sameServiceUUID()) {
                continue;
            }

            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                uuid = gattCharacteristic.getUuid().toString();
                if (uuid.equalsIgnoreCase(mSelectBTDevice.notifyCharacterUUID)) {// 血压测量特征UUID
                    if (mSelectBTDevice.needCheckProperties()) {
                        final int charaProp = gattCharacteristic.getProperties();
                        boolean property_notify = (charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0;
                        if (property_notify) {
                            dealNotifyCharacteristic(gattCharacteristic);
                            break;
                        }
                    } else {
                        dealNotifyCharacteristic(gattCharacteristic);
                        break;
                    }
                }
            }

            //针对有些手机不需要写命令
            if (mSelectBTDevice.needWriteCommand()) {
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    uuid = gattCharacteristic.getUuid().toString();
                    if (uuid.equalsIgnoreCase(mSelectBTDevice.writeCharacterUUID)) {// 血压测量特征UUID
                        if (mSelectBTDevice.needCheckProperties()) {
                            final int charaProp = gattCharacteristic.getProperties();
                            boolean property_write = (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0
                                    | (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
                            if (property_write) {
                                dealWriteCharacteristic(gattCharacteristic);
                                break;
                            }
                        } else {
                            dealWriteCharacteristic(gattCharacteristic);
                            break;
                        }
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void dealNotifyCharacteristic(
            final BluetoothGattCharacteristic characteristic) {
        mNotifyCharacteristics.add(characteristic);
        mBluetoothLeService.setCharacteristicNotification(characteristic,
                true, mSelectBTDevice.getDescriptorEnabledValue());
    }

    @SuppressLint("NewApi")
    private void dealWriteCharacteristic(
            final BluetoothGattCharacteristic characteristic) {
        mWriteCharacteristic = characteristic;
        if (mSelectBTDevice != null) {
            mSelectBTDevice.setBTControlManager(this);
        }
        //针对艾科血糖仪的特殊处理，先发送一条命令给血糖设备
        if (mSelectBTDevice != null && mSelectBTDevice instanceof OnCallBloodSugar) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            characteristic.setValue(((OnCallBloodSugar) mSelectBTDevice).getStartCommand());
            mBluetoothLeService.writeCharacteristic(characteristic);
        }

        if (mSelectBTDevice != null && mSelectBTDevice instanceof CavyBandDevice) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            characteristic.setValue(((CavyBandDevice) mSelectBTDevice).getStartCommand());
            mBluetoothLeService.writeCharacteristic(characteristic);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("NewApi")
    private void dealDescriptorWrite(String uuid) {

        LogUtils.i("ACTION_CHARACTER_NOTIFICATION --- " + uuid);

        if (mBluetoothLeService != null) {
            if (mSelectBTDevice != null && mSelectBTDevice instanceof OximeterDevice) {

                List<BluetoothGattService> gattServices = mBluetoothLeService.getSupportedGattServices();

                if (gattServices == null) {
                    return;
                }


                if (mSelectBTDevice.notifyCharacterUUID.equalsIgnoreCase(uuid)) {
                    //启用写特征
                    //不需要发送命令，配置写特征即可
                    mBluetoothLeService.setCharacteristicNotification(
                            mWriteCharacteristic,
                            true,
                            mSelectBTDevice.getDescriptorEnabledValue());
                    //调用
                    if (TextUtils.equals(mSelectBTDevice.deviceName, OximeterDevice.OximeterName.PC_60F.getName())) {
                        if (mBTControlListener != null) {
                            //初始化inputStream和outputStream
                            OxiMeterHelper mOxiMeterHelper = new OxiMeterHelper(
                                    mBluetoothLeService,
                                    mSelectBTDevice.serviceUUID,
                                    mSelectBTDevice.writeCharacterUUID);
                            ((OximeterDevice) mSelectBTDevice).setOximeterHelper(mOxiMeterHelper);

                            //回调开始检测
                            mBTControlListener.onActionNotification();
                        }
                    }
                    return;
                }

                if (mSelectBTDevice.writeCharacterUUID.equalsIgnoreCase(uuid)) {
                    LogUtils.i(mSelectBTDevice.writeCharacterUUID);
                    //调用
                    if (mBTControlListener != null) {
                        //初始化inputStream和outputStream
                        OxiMeterHelper mOxiMeterHelper = new OxiMeterHelper(
                                mBluetoothLeService,
                                mSelectBTDevice.serviceUUID,
                                mSelectBTDevice.writeCharacterUUID);
                        ((OximeterDevice) mSelectBTDevice).setOximeterHelper(mOxiMeterHelper);

                        //回调开始检测
                        mBTControlListener.onActionNotification();
                    }
                }
            } else if (mSelectBTDevice != null && mSelectBTDevice instanceof SannuoAnWenBloodSugar) {
                List<BluetoothGattService> gattServices = mBluetoothLeService.getSupportedGattServices();
                if (gattServices == null) {
                    return;
                }
                if (mSelectBTDevice.notifyCharacterUUID.equalsIgnoreCase(uuid)) {
                    if (mSelectBTDevice instanceof SannuoAnWenBloodSugar) {
                        //启用写特征
                        //不需要发送命令，配置写特征即可
                        mBluetoothLeService.setCharacteristicNotification(
                                mWriteCharacteristic,
                                true,
                                mSelectBTDevice.getDescriptorEnabledValue());
                    }
                }
            }
        }
    }


    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a
    // result of read
    // or notification operations.
    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case BluetoothLeService.ACTION_GATT_CONNECTED:
                    mBTControlListener.onConnectChanged(true);
                    break;
                case BluetoothLeService.ACTION_GATT_DISCONNECTED:
                    if (mBTControlListener != null) {
                        mBTControlListener.onConnectChanged(false);
                    }
                    break;
                case BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED:
                    // Show all the supported services and characteristics on the
                    // user interface.
                    if (mBluetoothLeService != null) {
                        displayGattServices(mBluetoothLeService
                                .getSupportedGattServices());
                    }
                    break;
                case BluetoothLeService.ACTION_DATA_AVAILABLE:
                    if (mSelectBTDevice != null) {
                        byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                        BTData btData = mSelectBTDevice.paserData(data);
                        if (btData instanceof BTWriteData) {
                            BTWriteData wData = (BTWriteData) btData;
                            if (mBluetoothLeService != null && mWriteCharacteristic != null) {
                                mWriteCharacteristic.setValue(wData.command);
                                mBluetoothLeService.writeCharacteristic(mWriteCharacteristic);
                            }
                        } else {
                            if (mBTControlListener != null) {
                                mBTControlListener.onDataReceived(btData);
                            }
                        }
                    }
                    break;
                case BluetoothLeService.ACTION_CHARACTER_NOTIFICATION:
                    String uuid = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                    dealDescriptorWrite(uuid);
                    break;
            }
        }
    };


    @SuppressLint("NewApi")
    public void sendCommand(final String command) {
        if (mExecutorService != null) {
            mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    if (command != null && mBluetoothLeService != null && mWriteCharacteristic != null) {
                        try {
                            mWriteCharacteristic.setValue(command.getBytes("utf-8"));
                            mBluetoothLeService.writeCharacteristic(mWriteCharacteristic);
                            SystemClock.sleep(1000);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @SuppressLint("NewApi")
    public void sendCommand(final byte[] command, final int sendDuration) {
        if (mExecutorService != null) {
            mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    if (command != null && mBluetoothLeService != null && mWriteCharacteristic != null) {
                        mWriteCharacteristic.setValue(command);
                        mBluetoothLeService.writeCharacteristic(mWriteCharacteristic);
                        SystemClock.sleep(sendDuration);
                    }
                }
            });
        }
    }
}
