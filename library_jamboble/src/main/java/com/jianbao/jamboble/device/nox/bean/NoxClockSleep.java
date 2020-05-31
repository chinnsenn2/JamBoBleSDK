package com.jianbao.jamboble.device.nox.bean;

import java.io.Serializable;

/**
 * 
*<p>Title: NoxClockSleep </p>
*<p>Description: 包含时钟休眠的全部信息的封装类</p> 
 * @author wenlong
 *
 * 2015年12月29日
 */
public class NoxClockSleep  implements  Cloneable , Serializable{
	
	/**
	 * 是否打开时钟休眠的功能
	 */
	public boolean isClockSleep;
	/**
	 * 24小时制
	 */
	public byte startHour=23;
	public byte startMinute=0;
	/**
	 * 24小时制
	 */
	public byte endHour=8;
	public byte endMinute=0;
    
	
    
	@Override
	public String toString() {
		return "NoxClockSleep [isClockSleep=" + isClockSleep + ", startTime="
				+ startHour + ", startMinute=" + startMinute + ", endHour="
				+ endHour + ", endMinute=" + endMinute + "]";
	}

	/**
	 * noxOld,noxNew里面的时钟休眠设置信息是否相同
	* <p> </p>
	* <p> </p>
	 * 2015年12月28日   wenlong
	 */
	public boolean isSame(NoxClockSleep noxOld,NoxClockSleep noxNew) {
		if (noxOld!=null&&noxNew!=null) {
            return noxOld.isClockSleep == noxNew.isClockSleep &&
                    noxOld.startHour == noxNew.startHour &&
                    noxOld.startMinute == noxNew.startMinute &&
                    noxOld.endHour == noxNew.endHour &&
                    noxOld.endMinute == noxNew.endMinute;
		}
         return false;
	}
	
	@Override
	public NoxClockSleep clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return (NoxClockSleep) super.clone();
	}
	
	
	public void clear() {
			  isClockSleep=false;
			  startHour=23;
			  startMinute=0;
			  endHour=8;
			  endMinute=0;
	}
	
}
