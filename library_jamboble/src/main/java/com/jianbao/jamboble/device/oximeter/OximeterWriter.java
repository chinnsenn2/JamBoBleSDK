package com.jianbao.jamboble.device.oximeter;

import com.creative.base.Isender;

import java.io.IOException;

/**
 * Created by zhangmingyao on 2017/8/11 09:26
 * Email:501863760@qq.com
 */

public class OximeterWriter implements Isender{

    private OxiMeterHelper mHelper;

    public OximeterWriter(OxiMeterHelper helper) {
        mHelper = helper;
    }

    @Override
    public void send(byte[] d) throws IOException {
        if (mHelper != null) {
            mHelper.write(d);
        }
    }

    @Override
    public void close() {
        mHelper = null;
    }
}
