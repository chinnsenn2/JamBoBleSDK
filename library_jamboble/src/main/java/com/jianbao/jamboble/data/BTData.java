package com.jianbao.jamboble.data;

import java.io.Serializable;

public class BTData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int deviceID = 1;

	public int getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(int deviceID) {
		this.deviceID = deviceID;
	}
}