package com.jianbao.jamboble.device.nox.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.SystemClock;
import android.util.Log;

import com.jianbao.jamboble.device.nox.BleDevice;

import java.lang.reflect.Method;


/**
 * Created by Administrator on 2016/9/1.
 */

public class BluetoothUtil {

    private static final String TAG = BluetoothUtil.class.getSimpleName();

    public static boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.isEnabled();
        }
        return false;
    }

    public static boolean isBleEnable() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.enable();
        }
        return false;
    }


    public static boolean createBond(BluetoothDevice bluetoothdevice) throws Exception {
        return ((Boolean) bluetoothdevice.getClass().getMethod("createBond", new Class[0]).invoke(bluetoothdevice, new Object[0])).booleanValue();
    }


    public static void setPairingConfirmation(Class<?> btClass,BluetoothDevice device, boolean isConfirm)throws Exception {
        Method setPairingConfirmation = btClass.getDeclaredMethod("setPairingConfirmation", boolean.class);
        Boolean res = (Boolean) setPairingConfirmation.invoke(device,isConfirm);
        Log.d(TAG," setPairingConfirmation res:" + res);
    }

    public static boolean setPin(Class<? extends BluetoothDevice> btClass, BluetoothDevice btDevice, String str) throws Exception {
        try {
            Method removeBondMethod = btClass.getDeclaredMethod("setPin", new Class[] {byte[].class});
            Boolean res = (Boolean) removeBondMethod.invoke(btDevice, new Object[] {str.getBytes("UTF-8")});
//            Log.d(TAG," setPin res:" + res);
        } catch (SecurityException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }


    // 取消用户输入
    public static boolean cancelPairingUserInput(Class btClass, BluetoothDevice device) throws Exception {
        Method createBondMethod = btClass.getMethod("cancelPairingUserInput");
        // cancelBondProcess()
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }


    public static void connectBluetooth(BleDevice bleDevice){
        Log.d(TAG," connectBluetooth device:" + bleDevice);
        if (bleDevice != null && BluetoothAdapter.checkBluetoothAddress(bleDevice.btAddress)) {
            final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            final BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(bleDevice.btAddress);
            if (bluetoothDevice != null) {
                Log.d(TAG, " connectBluetooth bound1 state:" + bluetoothDevice.getBondState());
                if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                    try {
                        BluetoothUtil.createBond(bluetoothDevice);
                        Log.d(TAG, " bound ok----------");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.d(TAG, " bound fail----------" + e.getMessage());
                    }

                    new Thread(){
                        @Override
                        public void run() {
                            int count = 500;
                            int tryCount = 0;
                            while (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED && count>0) {
                                SystemClock.sleep(20);
                                count--;
                                if(bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE){//说明配对失败
                                    if(tryCount < 3){
                                        Log.d(TAG," connectBluetooth bound try:" + tryCount+",count:"+ count);
                                        try {
                                            BluetoothUtil.createBond(bluetoothDevice);
                                            tryCount ++;
                                            count += 500;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }else{
                                        break;
                                    }
                                }
                            }

                            Log.d(TAG, " connectBluetooth bound2 state:" + bluetoothDevice.getBondState());

                            if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                                connectA2DP(bluetoothAdapter, bluetoothDevice);
                            }
                        }
                    }.start();
                }else{
                    connectA2DP(bluetoothAdapter, bluetoothDevice);
                }
            }
        }
    }


    public static void connectA2DP(final BluetoothAdapter adapter, final BluetoothDevice device){

        if(adapter == null || device == null){
            return;
        }

//        adapter.getProfileProxy(App.context, new BluetoothProfile.ServiceListener() {
//            @Override
//            public void onServiceConnected(int profile, BluetoothProfile proxy) {
//                if(profile == BluetoothProfile.A2DP){
//                    try {
//                        BluetoothA2dp mBluetoothA2dp = (BluetoothA2dp)proxy;
//                        if(mBluetoothA2dp.getConnectionState(device) != BluetoothProfile.STATE_CONNECTED){
//                            Method connect = mBluetoothA2dp.getClass().getDeclaredMethod("connect", BluetoothDevice.class);
//                            connect.setAccessible(true);
//                            Object res = connect.invoke(mBluetoothA2dp, device);
//                            Log.d(TAG," connectBluetooth res:" + res);
//                        }else{
//                            Log.d(TAG," connectBluetooth already connected");
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.d(TAG," connectBluetooth fail----------");
//                    }
//                }
//            }
//
//            @Override
//            public void onServiceDisconnected(int profile) {
//
//            }
//        },BluetoothProfile.A2DP);
    }

}
