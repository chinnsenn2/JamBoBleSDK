package com.jianbao.jamboble.device;

/**
 * Created by zhangmingyao
 * date: 2018/2/26.
 * Email:501863760@qq.com
 * 百捷三合一
 * BeneCheck-0270  结果第二个字节 41代表血糖 51尿酸 61胆固醇
 */

public abstract class BeneCheckThreeInOne extends BTDevice{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public BeneCheckThreeInOne(String deviceAlias) {
        super(deviceAlias,
                "BeneCheck"/*-0270*/,
                "00001808-0000-1000-8000-00805f9b34fb",
                "00002a18-0000-1000-8000-00805f9b34fb");
    }

    protected float getResultValue(byte[] data) {
        //幂次方数
        int powUnit = (0xFF & data[11]) >> 4;//右移4位，取得原来的高位值
        //血糖, 单位mmol/L
        return (float) (((0x0F & data[11]) << 8 | 0xFF & data[10]) * 1000 / Math.pow(10, 16 - powUnit));
    }

    @Override
    public int getImageResource() {
        return 0;
    }
}
