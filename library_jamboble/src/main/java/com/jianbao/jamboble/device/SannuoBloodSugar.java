package com.jianbao.jamboble.device;

import com.jianbao.jamboble.data.BloodSugarData;

/**
 * 三诺血糖仪
 * @author 毛晓飞
 *
 */
public class SannuoBloodSugar extends BTDevice {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SannuoBloodSugar() {
		super("三诺血糖仪",
			"Sinocare",
			"0000ffe0-0000-1000-8000-00805f9b34fb",
			"0000ffe1-0000-1000-8000-00805f9b34fb");
	}
	
	@Override
	public BloodSugarData paserData(byte[] data){


		//血糖数据解析
		//示列：53 4E 0E 00 04 04 0B 03 1E 08 07 00 2F 19 01 2E C8 
		if (data != null) {
			if (data[0]==0x53 && data[1] == 0x4E && data[5] == 0x04){
				//血糖
				float bloodSugar = (float) (((0xFF00 & (data[11] << 8)) | data[12]) / 10.0);
				BloodSugarData btData = new BloodSugarData();
				btData.bloodSugar = bloodSugar;
				btData.deviceID = getBTDeviceID();
				return btData;
			}
		}
		return null;
	}

	@Override
	public int getImageResource() {
		return 0;
	}
}