package com.jianbao.jamboble.device;

/**
 * 云康宝脂肪称， 型号Yolanda-CS20G2
 * @author 毛晓飞
 *
 */
public class YolandaFatScale20G2 extends YolandaFatScale {
	/**
	 */
	private static final long serialVersionUID = 1L;
	public YolandaFatScale20G2() {
		super();
		deviceName = "Yolanda-CS20G2";
	}

	@Override
	public int getBTDeviceID(){
		return 4;
	}
}