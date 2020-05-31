package com.jianbao.jamboble.data;



/**
 * 来自蓝牙设备的血压数据
 * @author 毛晓飞
 *
 */
public class BloodSugarData extends BTData{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//血糖
	public float bloodSugar;
	//是否使用Mmol单位
	public boolean useMmolUnit = true;

	public int mYear;// 年份
	public int mMonth;// 月份
	public int mday;// 天
	public int mHour;// 时
	public int mMinute;// 分

	@Override
	public String toString() {
		return "BloodSugarData [bloodSugar=" + bloodSugar + "]";
	}
}