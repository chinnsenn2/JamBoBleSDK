package com.jianbao.jamboble.device;

import com.jianbao.jamboble.utils.ValueCast;
import com.jianbao.jamboble.data.UricAcidData;

/**
 * 尿酸
 * 
 * @author 毛晓飞
 *
 */
public class BeneCheckUa extends BTDevice {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BeneCheckUa() {
		super("尿酸检测仪",
				"BeneCheck TC-B DONGLE",
				"00001000-0000-1000-8000-00805f9b34fb",
				"00001002-0000-1000-8000-00805f9b34fb");
	}

	@Override
	public UricAcidData paserData(byte[] data) {

		// 尿酸数据解析
		// 24-50-43-4c尿酸设备固定标识
		// 51-00 Command
		// 00-00 Parameter
		// 09-00 长度
		// 00-00 Index(2 bytes)Low byte first
		// 0F-09-0A-07-29 年-月-日-时-分
		// 3c-00 尿酸值
		// C7 校验和

		if (data != null) {
			if (data[0] == 0x24 && data[1] == 0x50 && data[2] == 0x43 && data[3] == 0x4C && data[4] == 0x51
					&& data[5] == 0x00) {// 尿酸设备数据标识（固定）
				int year = 2000 + ((0xFF & data[12]));
				int month = (0xFF & data[13]);
				int day = (0xFF & data[14]);

				int hour = (0xFF & data[15]);
				int minute = (0xFF & data[16]);
				// 单位mmol/L
				float uaValues = (float) ((((0xFF & data[18]) << 8 | 0xFF & data[17]) * 0.1) / 16.81);
				UricAcidData uaData = new UricAcidData();
				uaData.mYear = year;
				uaData.mMonth = month;
				uaData.mday = day;
				uaData.mHour = hour;
				uaData.mMinute = minute;
				uaData.mUricAcid = ValueCast.makePrecision(uaValues, 2);
				uaData.deviceID = getBTDeviceID();
				return uaData;
			}

		}
		return null;
	}

	@Override
	public int getImageResource() {
		return 0;
	}
}