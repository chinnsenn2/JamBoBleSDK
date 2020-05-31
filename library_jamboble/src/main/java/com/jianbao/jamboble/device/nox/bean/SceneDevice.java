package com.jianbao.jamboble.device.nox.bean;

public class SceneDevice extends BaseBean{
	public static final String DEVICE_ROLE_MONITOR = "monitor";
	public static final String DEVICE_ROLE_SLEEPAID = "sleepAid";
	public static final String DEVICE_ROLE_CLOCK = "clock";
	/** 场景小夜灯配置 */
	public static final String DEVICE_ROLE_SMALL_NIGHT_LIGHT = "nightLight";
	/** 照明场景配置 */
	public static final String DEVICE_ROLE_LIGHT = "light";
	/** 阅读，氛围场景配置 */
	public static final String DEVICE_ROLE_DEFAULT = "default";
	/*
	* 
	*/
	private long seqId;
	
	/*
	* 
	*/
	private int sceneId;
	
	/*
	* 子场景Id
	*/
	private int sceneSubId;
	
	/*
	* 
	*/
	private long userId;
	
	/*
	* 设备在这个场景作用
	*/
	private String deviceRole;
	
	/*
	* 设备Id
	*/
	private String deviceId;
	
	/*
	* 设备类型
	*/
	private short deviceType;
	


	public void setSeqId(long  seqId)
	{
		this.seqId=seqId;
	}
	
	public long getSeqId ()
	{
		return this.seqId;
	}
	
	public void setSceneId(int  sceneId)
	{
		this.sceneId=sceneId;
	}
	
	public int getSceneId ()
	{
		return this.sceneId;
	}
	
	public void setSceneSubId(int  sceneSubId)
	{
		this.sceneSubId=sceneSubId;
	}
	
	public int getSceneSubId ()
	{
		return this.sceneSubId;
	}
	
	public void setUserId(long  userId)
	{
		this.userId=userId;
	}
	
	public long getUserId ()
	{
		return this.userId;
	}
	
	public void setDeviceRole(String  deviceRole)
	{
		this.deviceRole=deviceRole;
	}
	
	public String getDeviceRole ()
	{
		return this.deviceRole;
	}
	
	public void setDeviceId(String  deviceId)
	{
		this.deviceId=deviceId;
	}
	
	public String getDeviceId ()
	{
		return this.deviceId;
	}
	
	public void setDeviceType(short  deviceType)
	{
		this.deviceType=deviceType;
	}
	
	public short getDeviceType ()
	{
		return this.deviceType;
	}

	@Override
	public String toString() {
		return "SceneDevice{" +
				"seqId=" + seqId +
				", sceneId=" + sceneId +
				", sceneSubId=" + sceneSubId +
				", userId=" + userId +
				", deviceRole='" + deviceRole + '\'' +
				", deviceId='" + deviceId + '\'' +
				", deviceType=" + deviceType +
				'}';
	}
}