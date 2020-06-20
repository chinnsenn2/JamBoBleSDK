package com.jianbao.jamboble.device;

import com.jianbao.jamboble.data.BTData;

public class NoxSleepLightDevice extends BTDevice {

    public NoxSleepLightDevice() {
        super("SN", "SN", "0000ffe0-0000-1000-8000-00805f9b34fb", "0000ffe4-0000-1000-8000-00805f9b34fb", "0000ffe9-0000-1000-8000-00805f9b34fb");
    }

    @Override
    public BTData paserData(byte[] data) {
        return null;
    }

    @Override
    public int getImageResource() {
        return 0;
    }
}
