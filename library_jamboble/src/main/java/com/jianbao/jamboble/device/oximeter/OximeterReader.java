package com.jianbao.jamboble.device.oximeter;

import com.creative.base.Ireader;

import java.io.IOException;

/**
 * Created by zhangmingyao on 2017/8/11 09:25
 * Email:501863760@qq.com
 */

public class OximeterReader implements Ireader{

    private OximeterHelper mHelper;

    public OximeterReader(OximeterHelper helper) {
        mHelper = helper;
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        if (mHelper != null) {
            return mHelper.read(buffer);
        }
        return 0;
    }

    @Override
    public void close() {
        mHelper = null;
    }

    @Override
    public void clean() {
        if (mHelper != null) {
            mHelper.clean();
        }
    }

    @Override
    public int available() throws IOException {
        if(mHelper!=null){
            return mHelper.available();
        }
        return 0;
    }
}
