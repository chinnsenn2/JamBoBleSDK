package com.jianbao.jamboble.device;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattDescriptor;

import com.jianbao.jamboble.BTControlManager;
import com.jianbao.jamboble.data.BTData;

import java.io.Serializable;

public abstract class BTDevice implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//别名
	public String deviceAlias;
	//设备名
	public String deviceName;
	//服务UUID
	public String serviceUUID;
	//特征UUID
	public String notifyCharacterUUID;
	//
	public String writeCharacterUUID;

	private BTControlManager mBTControlManager;

	private int bTDeviceID = -1;

	public BTDevice(String deviceAlias, String deviceName, String serviceUUID, String characterUUID) {
		this.deviceAlias = deviceAlias;
		this.deviceName = deviceName;
		this.serviceUUID = serviceUUID;
		this.notifyCharacterUUID = characterUUID;
	}

	public BTDevice(String deviceAlias, String deviceName, String serviceUUID, String notifyUUID, String writeUUID) {
		this.deviceAlias = deviceAlias;
		this.deviceName = deviceName;
		this.serviceUUID = serviceUUID;
		this.notifyCharacterUUID = notifyUUID;
		this.writeCharacterUUID = writeUUID;
	}
	
	/**
	 * 是否需要写命令
	 * @return
	 */
	public boolean needWriteCommand(){
        return writeCharacterUUID != null;
    }

	/**
	 * 有些设备(如:Nox 902B BLE 791)的 writeUUID 和 nitifyUUID 不在一个 serviceUUID 下
	 * @return
	 */
	public boolean sameServiceUUID() {
		return true;
	}

	/**
	 * 是否需要通过判断属性，来区分读、写、通知
	 * 针对艾康的血糖仪，写和通知的character的UUID一样
	 * @return
	 */
	public boolean needCheckProperties(){
		return false;
	}
	
	public abstract BTData paserData(byte[] data);
	public abstract int getImageResource();

	@SuppressLint("NewApi")
	public byte[] getDescriptorEnabledValue(){
		return BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
	}

	/**
	 * 设置写入的Characteristic
     */
	public void setBTControlManager(BTControlManager controlManager){
		mBTControlManager = controlManager;
	}

	@SuppressLint("NewApi")
	protected void sendCommand(String command){
		if (command != null && mBTControlManager != null){
			mBTControlManager.sendCommand(command);
		}
	}

	@SuppressLint("NewApi")
	protected void sendCommand(byte[] command, int sendDuration){
		if (command != null && mBTControlManager != null){
			mBTControlManager.sendCommand(command,sendDuration);
		}
	}

	public int getBTDeviceID() {
		return bTDeviceID;
	}

	public void setBTDeviceID(int bTDeviceID) {
		this.bTDeviceID = bTDeviceID;
	}

}