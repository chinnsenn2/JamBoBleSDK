package com.jianbao.jamboble.device;

import com.jianbao.jamboble.R;
import com.jianbao.jamboble.data.BloodPressureData;

/**
 * 海尔血压计
 * @author 毛晓飞
 *
 */
public class HaierBloodPressure extends BTDevice {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HaierBloodPressure() {
		super("海尔血压计",
			"SerialCom",
			"0000fbb0-494C-4F47-4943-544543480000",
			"0000fbb1-494C-4F47-4943-544543480000");
	}
	
	@Override
	public BloodPressureData paserData(byte[] data){


		//血压数据解析
		//示列：68 83 00 05 00 08 31 A9 75 45 74 16 
		if (data != null && data.length == 12) {
			if (data[3]==0x5 && data[5] == 0x8 && data[6] == 0x31){
				// 收缩压
				int systolicPressure =  0xFF & data[7];
				// 舒张压
				int diastolicPressure = 0xFF & data[8];
				// 心率
				int heartRate = 0xFF & data[9];
				
				BloodPressureData btData = new BloodPressureData();
				btData.systolicPressure = systolicPressure;
				btData.diastolicPressure = diastolicPressure;
				btData.heartRate = heartRate;
				btData.setDeviceID(getBTDeviceID());
				return btData;
			}
		}
		return null;
	}

	@Override
	public int getImageResource() {
		return R.drawable.blood_pressure_sphygmomanometer_one;
	}
}