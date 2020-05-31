package com.jianbao.jamboble.device.nox.bean;


import com.jianbao.jamboble.device.nox.DeviceType;

import static com.jianbao.doctor.bluetooth.device.nox.utils.SceneUtils.SLEEP_SCENE_ID;

public class SceneConfigMobile extends SceneConfigBase{

	public SceneConfigMobile() {

	}

	public SceneConfigMobile(SceneConfigMobile sceneConfigMobile) {
		super(sceneConfigMobile);
	}

	public void copy(SceneConfigMobile sceneConfigMobile) {
		super.copy(sceneConfigMobile);
	}
	public void init() {
		setSeqid(100);
		setEnable((byte) 1);
		setSceneId(SLEEP_SCENE_ID);
		setSceneSubId(0);
		//setSceneType((byte) 0);
		setDeivceId(String.valueOf(1));
		setDeviceType(DeviceType.DEVICE_TYPE_PHONE);
		setUserId(1);
		setMusicFlag(1);
		setMusicSeqid((short) 1);
		setSmartStopFlag(1);
		setAidingTime(15);
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		SceneConfigMobile that = (SceneConfigMobile) o;
		return true;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return super.toString() + "SceneConfigMobile{" +
				'}';
	}
}