package com.jianbao.jamboble.device;

import com.jianbao.jamboble.R;
import com.jianbao.jamboble.data.BloodSugarData;

/**
 * 鱼跃血糖仪
 * @author 毛晓飞
 *
 */
public class YuwellBloodSugar extends BTDevice {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public YuwellBloodSugar() {
		super("鱼跃血糖仪",
			"Yuwell Glucose",
			"00001808-0000-1000-8000-00805f9b34fb",
			"00002a18-0000-1000-8000-00805f9b34fb");
	}
	
	@Override
	public BloodSugarData paserData(byte[] data){


		//血糖数据解析
		//示列：06 00 00 DE 07     01 03 0B 21 1B    24 C0 11 
		if (data != null) {
			if (data[0]==0x06){
				//幂次方数
				int powUnit = (0xFF & data[11]) >> 4;
				//血糖, 单位mmol/L
				float bloodSugar = (float) (((0x0F & data[11]) << 8 | 0xFF & data[10]) * 1000 / Math.pow(10, 16-powUnit)) ;
				
				BloodSugarData btData = new BloodSugarData();
				btData.bloodSugar = bloodSugar;
				btData.setDeviceID(getBTDeviceID());
				return btData;
			}
		}
		return null;
	}

	@Override
	public int getImageResource() {
		return R.drawable.blood_pressure_blood_glucose_meter_one;
	}
}