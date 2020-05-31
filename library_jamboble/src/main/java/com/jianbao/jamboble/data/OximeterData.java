package com.jianbao.jamboble.data;

/**
 * Created by zhangmingyao on 2017/8/11 11:28
 * Email:501863760@qq.com
 */

public class OximeterData extends BTData{

    byte[] data;

    public OximeterData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
