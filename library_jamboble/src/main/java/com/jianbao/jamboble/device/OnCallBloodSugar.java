package com.jianbao.jamboble.device;

import com.jianbao.jamboble.data.BTData;
import com.jianbao.jamboble.data.BTWriteData;
import com.jianbao.jamboble.data.OnCallBloodSugarData;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 艾科乐易捷血糖计, 它的Notify和Write UUID是一样的
 * @author 江俊杰
 *
 */
public class OnCallBloodSugar extends BTDevice {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String mDate;
	private String mGlucose;
	private ArrayList<String> mDataList = new ArrayList<String>();

	public OnCallBloodSugar() {
		super("101C00028D9",
			"c14d2c0a-401f-b7a9-841f-e2e93b80f631",
			"81eb77bd-89b8-4494-8a09-7f83d986ddc7",
			"81eb77bd-89b8-4494-8a09-7f83d986ddc7");
	}
	
	@Override
	public boolean needCheckProperties(){
		return true;
	}
	
	/**
	 * 需要输入上一次测量的时间，血糖值
	 * @param date
	 * @param glucose
	 */
	public void setLastData(String date, String glucose){
		
	}
	
	/**
	 * 设备连接以后，先发送命令给血糖仪
	 * @return
	 */
	public byte[] getStartCommand(){
		try {
			return "&TB 50299".getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 结果保留一位小数
	 * @param data
	 * @return
	 */
	private float calculate(String data){
		float value = (float) (Integer.parseInt(data) / 18.0);
		BigDecimal b = new BigDecimal(value);  
		return b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();  
	}
	
	@Override
	public BTData paserData(byte[] data){
		if (data == null){
			return null;
		}

		String record = null;
		try {
			record = new String(data,"utf-8");
			if (record.startsWith("&N")) {
				mDataList.add(record);
				if (mDataList.size() == 3) {
					String[] str = mDataList.get(1).split(" ");

					OnCallBloodSugarData btData = new OnCallBloodSugarData();
					btData.strBloodSugar = str[1];
					btData.strDate = str[0].substring(2);
					btData.bloodSugar = calculate(str[1]);
					btData.deviceID = getBTDeviceID();
					return btData;
				}
			}

			if (record.startsWith("&M")) {
				mDataList.clear();

				if (mDate == null) {
					BTWriteData wData = new BTWriteData();
					wData.command = getSendByte("&N0 ");
					return wData;
				} else {
					BTWriteData wData = new BTWriteData();
					wData.command = getSendByte("&D" + mDate + " ");
					return wData;
				}
			}
			if (record.startsWith("&D")) {
				BTWriteData wData = new BTWriteData();
				wData.command = getSendByte("&R" + mGlucose + " ");
				return wData;
			}
			if (record.startsWith("&R")) {
				BTWriteData wData = new BTWriteData();
				wData.command = getSendByte("&N0 ");
				return wData;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	private byte[] getSendByte(String str){
		try {
			str = str + CRC16.getCRC16Result(str.getBytes("utf-8"));
			return str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getImageResource() {
		// TODO Auto-generated method stub
		return 0;
	}
}