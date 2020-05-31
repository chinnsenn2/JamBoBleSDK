package com.jianbao.jamboble.data;



/**
 * 来自蓝牙设备的血压数据
 * @author 毛晓飞
 *
 */
public class BloodPressureData extends BTData{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//收缩压
	public int systolicPressure;
	//舒张压
	public int diastolicPressure;
	//心率
	public int heartRate;
	
	@Override
	public String toString() {
		return "BloodPressureData [systolicPressure=" + systolicPressure
				+ ", diastolicPressure=" + diastolicPressure + ", heartRate="
				+ heartRate + "]";
	}
}