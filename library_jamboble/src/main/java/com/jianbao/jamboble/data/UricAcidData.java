package com.jianbao.jamboble.data;

/**
 * 来自尿酸检测仪的尿酸数据
 * 
 * @author 毛晓飞
 *
 */
public class UricAcidData extends BTData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int mYear;// 年份
	public int mMonth;// 月份
	public int mday;// 天
	public int mHour;// 时
	public int mMinute;// 分
	public float mUricAcid;// 尿酸值

	public String toString() {
		return "UricAcid [mYear=" + mYear + ", mMonth=" + mMonth + ", mday=" + mday + ", mHour=" + mHour + ", mMinute="
				+ mMinute + ", mUricAcid=" + mUricAcid + "]";
	}

}