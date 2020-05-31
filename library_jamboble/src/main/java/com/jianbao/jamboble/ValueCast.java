package com.jianbao.jamboble;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class ValueCast {

	public static short stringToShort(String value) {
		short ret = 0;
		try {
			ret = Short.valueOf(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static int stringToInt(String value) {
		int ret = 0;
		try {
			ret = Integer.parseInt(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static float stringToFloat(String value) {
		float ret = 0.0f;
		try {
			double f = Double.valueOf(value);
			BigDecimal b = new BigDecimal(f);
			ret = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static double stringToDouble(String value) {
		double ret = 0.0f;
		try {
			ret = Double.valueOf(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static String intToString(int value) {
		String ret = "";
		try {
			ret = String.valueOf(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static String floatToString(float value) {
		String ret = "";
		try {
			ret = String.valueOf(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 
	 * @param value
	 * @param precision:保留位数
	 * @return
	 */
	public static float makePrecision(float value, int precision){
		float ret = 0.0f;
		try{
			BigDecimal b = new BigDecimal(value);  
			ret =  b.setScale(precision, BigDecimal.ROUND_HALF_UP).floatValue(); 
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	
	  /** 
     * 使用java正则表达式去掉多余的.与0 
     * @param s 
     * @return  
     */  
    public static String subZeroAndDot(String s){  
        if(s.indexOf(".") > 0){  
            s = s.replaceAll("0+?$", "");//去掉多余的0  
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉  
        }  
        return s;  
    }

	public static String makePrecisionDoubleZero(double value){
		DecimalFormat fnum = new DecimalFormat("##0.00");
		return subZeroAndDot(fnum.format(value));
	}

    public static String makePrecisionZero(float value){
		DecimalFormat fnum = new DecimalFormat("##0.00");
		return fnum.format(value);
	}


	public static String subZeroAndDot(float value) {
		float ret = makePrecision(value, 2);
		return subZeroAndDot(String.valueOf(ret));
	}

	public static String formateMoneyDouble(Double money) {
		if (money == null) {
			return "0";
		}

		return formatMoney(new BigDecimal(money.doubleValue()));
	}

	public static String formatMoney(BigDecimal bigDecimal) {
		if (bigDecimal == null) {
			return "0";
		}
		BigDecimal temp = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
		return subZeroAndDot(temp.toPlainString());
	}


}