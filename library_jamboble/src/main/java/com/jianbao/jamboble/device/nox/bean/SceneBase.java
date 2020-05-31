package com.jianbao.jamboble.device.nox.bean;

import android.util.Log;

import com.jianbao.jamboble.device.nox.DeviceType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/22.
 */

public class SceneBase extends BaseBean{

    private long seqId;

    private int sceneId;

    private long userId;

    private List<SceneDevice> devices;

    //nox+手机或者nox+无
    public boolean isOnlyNOx() {
        boolean result = (getMonitorDeviceType() == DeviceType.DEVICE_TYPE_NULLL || getMonitorDeviceType() == DeviceType.DEVICE_TYPE_PHONE);
        return result;
    }

    public boolean hasDevice(){
        return devices != null && devices.size() > 0;
    }



    public boolean hasDevice(short deviceType){
        if(devices != null){
            for (SceneDevice d : devices) {
                if (d.getDeviceType() == deviceType && !d.getDeviceRole().equals(SceneDevice.DEVICE_ROLE_CLOCK)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isAllNull() {
        if(devices != null){
            for (SceneDevice device : devices) {
                if(device.getDeviceType() != DeviceType.DEVICE_TYPE_NULLL) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getMonitorDeviceId() {
        if(devices != null){
            for (SceneDevice device : devices) {
                if(device != null && device.getDeviceRole().equals(SceneDevice.DEVICE_ROLE_MONITOR)) {
                    return device.getDeviceId();
                }
            }
        }
        return "";
    }

    public short getMonitorDeviceType() {
        if(devices != null){
            for (SceneDevice device : devices) {
                if(device != null && device.getDeviceRole().equals(SceneDevice.DEVICE_ROLE_MONITOR)) {
                    return device.getDeviceType();
                }
            }
        }
        return DeviceType.DEVICE_TYPE_NULLL;
    }

    public void setMonitorDeviceId(String monitorDeviceId) {
        if(devices != null){
            for (SceneDevice device : devices) {
                if(device != null && device.getDeviceRole().equals(SceneDevice.DEVICE_ROLE_MONITOR)) {
                    device.setDeviceId(monitorDeviceId);
                    break;
                }
            }
        }
    }

    public void changeMonitorDevice(String monitorDeviceId, short deviceType) {
        if(devices != null){
            for (SceneDevice device : devices) {
                if(device != null && device.getDeviceRole().equals(SceneDevice.DEVICE_ROLE_MONITOR)) {
                    device.setDeviceId(monitorDeviceId);
                    device.setDeviceType(deviceType);
                    //HomeFragment.setNeedConfigScene(true);
                    return;
                }
            }
        }else{
            devices = new ArrayList<>();
        }

        SceneDevice monitorDevice = new SceneDevice();
        monitorDevice.setDeviceRole(SceneDevice.DEVICE_ROLE_MONITOR);
        monitorDevice.setDeviceId(monitorDeviceId);
        monitorDevice.setDeviceType(deviceType);
        monitorDevice.setSceneId(sceneId);
        monitorDevice.setSceneSubId(0);
        devices.add(monitorDevice);
        //HomeFragment.setNeedConfigScene(true);
    }

    public void removeMonitorDevice() {
        ArrayList<SceneDevice> delList = new ArrayList<>();
        if(devices != null) {
            for (SceneDevice device : devices) {
                if(device != null && device.getDeviceRole().equals(SceneDevice.DEVICE_ROLE_MONITOR)) {
                    delList.add(device);
                }
            }
            devices.removeAll(delList);
        }
    }

    public void setMonitorDeviceType(short deviceType) {
        if(devices != null){
            for (SceneDevice device : devices) {
                if(device != null && device.getDeviceRole().equals(SceneDevice.DEVICE_ROLE_MONITOR)) {
                    device.setDeviceType(deviceType);
                    break;
                }
            }
        }
    }

    public String getSleepAidDeviceId() {
        if(devices != null){
            for(SceneDevice device : devices) {
                if(device != null && device.getDeviceRole().equals(SceneDevice.DEVICE_ROLE_SLEEPAID)) {
                    return device.getDeviceId();
                }
            }
        }
        return null;
    }

    public short getSleepAidDeviceType() {
        Log.e(TAG,"-----getSleepAidDeviceType----SceneBase:  "+this);
        if(devices != null){
            for(SceneDevice device : devices) {
                if(device != null && device.getDeviceRole().equals(SceneDevice.DEVICE_ROLE_SLEEPAID)) {
                    return device.getDeviceType();
                }
            }
        }
        return 11;
    }

    public void setSleepAidDeviceId(String sleepAidDeviceId) {
        if(devices != null){
            for(SceneDevice device : devices) {
                if(device != null && device.getDeviceRole().equals(SceneDevice.DEVICE_ROLE_SLEEPAID)) {
                    device.setDeviceId(sleepAidDeviceId);
                    break;
                }
            }
        }
    }

    public void setSleepAidDeviceType(short deviceType) {
        if(devices != null){
            for(SceneDevice device : devices) {
                if(device != null && device.getDeviceRole().equals(SceneDevice.DEVICE_ROLE_SLEEPAID)) {
                    device.setDeviceType(deviceType);
                    break;
                }
            }
        }
    }

    public void changeSleepAidDevice(String sleepAidDeviceId, short deviceType) {
        if(devices != null){
            for(SceneDevice device : devices) {
                if(device != null && device.getDeviceRole().equals(SceneDevice.DEVICE_ROLE_SLEEPAID)) {
                    device.setDeviceType(deviceType);
                    device.setDeviceId(sleepAidDeviceId);
                    //HomeFragment.setNeedConfigScene(true);
                    return;
                }
            }
        }else{
            devices = new ArrayList<>();
        }
        SceneDevice sleepAidDevice = new SceneDevice();
        sleepAidDevice.setDeviceRole(SceneDevice.DEVICE_ROLE_SLEEPAID);
        sleepAidDevice.setDeviceId(sleepAidDeviceId);
        sleepAidDevice.setDeviceType(deviceType);
        sleepAidDevice.setSceneId(sceneId);
        sleepAidDevice.setSceneSubId(0);
        devices.add(sleepAidDevice);
        //HomeFragment.setNeedConfigScene(true);
    }

    public void removeSleepAidDevice() {
        ArrayList<SceneDevice> delList = new ArrayList<>();
        if(devices != null){
            for(SceneDevice device : devices) {
                if(device != null && device.getDeviceRole().equals(SceneDevice.DEVICE_ROLE_SLEEPAID)) {
                    delList.add(device);
                }
            }
            devices.removeAll(delList);
        }
    }


    public void changeAlarmDevice(String alarmDeviceId, short deviceType) {
        if(devices != null){
            for(SceneDevice device : devices) {
                if(device != null && device.getDeviceRole().equals(SceneDevice.DEVICE_ROLE_CLOCK)) {
                    device.setDeviceType(deviceType);
                    device.setDeviceId(alarmDeviceId);
                    return;
                }
            }
        }else{
            devices = new ArrayList<>();
        }

        SceneDevice alarmDevice = new SceneDevice();
        alarmDevice.setDeviceRole(SceneDevice.DEVICE_ROLE_CLOCK);
        alarmDevice.setDeviceId(alarmDeviceId);
        alarmDevice.setDeviceType(deviceType);
        alarmDevice.setSceneId(sceneId);
        alarmDevice.setSceneSubId(0);
        devices.add(alarmDevice);
    }


    public void removeAlarmDevice() {
        ArrayList<SceneDevice> delList = new ArrayList<>();
        if(devices != null){
            for(SceneDevice device : devices) {
                if(device != null && device.getDeviceRole().equals(SceneDevice.DEVICE_ROLE_CLOCK)) {
                    delList.add(device);
                }
            }
            devices.removeAll(delList);
        }
    }

    public void setClockDeviceId(String deviceId) {
        if(devices != null){
            for (SceneDevice device : devices) {
                if(device != null && device.getDeviceRole().equals(SceneDevice.DEVICE_ROLE_CLOCK)) {
                    device.setDeviceId(deviceId);
                    break;
                }
            }
        }
    }

    public void setClockDeviceType(short deviceType) {
        if(devices != null){
            for (SceneDevice device : devices) {
                if(device != null && device.getDeviceRole().equals(SceneDevice.DEVICE_ROLE_CLOCK)) {
                    device.setDeviceType(deviceType);
                    break;
                }
            }
        }
    }

    public List<SceneDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<SceneDevice> devices) {
        this.devices = devices;
    }

    public long getSeqId() {
        return seqId;
    }

    public void setSeqId(long seqId) {
        this.seqId = seqId;
    }

    public int getSceneId() {
        return sceneId;
    }

    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "SceneBase{" +
                "seqId=" + seqId +
                ", sceneId=" + sceneId +
                ", userId=" + userId +
                ", devices=" + devices +
                '}';
    }
}
