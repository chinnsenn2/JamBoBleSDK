package com.jianbao.jamboble.device;

import com.jianbao.jamboble.R;

/**
 * 云康宝脂肪称， 型号Yolanda-CS10C1
 * @author 毛晓飞
 *
 */
public class YolandaFatScale10C1 extends YolandaFatScale {
	/**
	 */
	private static final long serialVersionUID = 1L;
	public YolandaFatScale10C1() {
		super();
		deviceName = "Yolanda-CS10C1";
	}

	@Override
	public int getImageResource() {
		return R.drawable.blood_pressure_scale_one;
	} 
}