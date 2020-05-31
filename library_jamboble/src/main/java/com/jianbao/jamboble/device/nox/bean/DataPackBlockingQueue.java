package com.jianbao.jamboble.device.nox.bean;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 2016/8/31.
 */
public class DataPackBlockingQueue extends HashMap<Byte, DataPacket> {
    public static final String TAG = DataPackBlockingQueue.class.getSimpleName();
    private ArrayList<String> mSendPackHeadSeq = new ArrayList<>();
    private String owner;

    public DataPackBlockingQueue(String owner) {
        this.owner = owner;
    }

    public synchronized void addSendPack(DataPacket sendPack) {
        if (sendPack == null) {
            return;
        }

        String sendReq = sendPack.head.senquence + "";
        if (!mSendPackHeadSeq.contains(sendReq)) {
            mSendPackHeadSeq.add(sendReq);
        }
    }


    public boolean offer(DataPacket o) {
        boolean result = false;
        String receiveSeq = o.head.senquence + "";
        Log.d(TAG,owner + " 收到消息类型：0x" + Integer.toHexString(o.msg.type) + "   序列号：" + receiveSeq );
        if (mSendPackHeadSeq.contains(receiveSeq)) {
            put(o.head.senquence, o);
            mSendPackHeadSeq.remove(receiveSeq);
        }
        return result;
    }

    public synchronized DataPacket peek(byte senquence) {
        DataPacket pack = get(senquence);
        if (pack != null) {
            remove(senquence);
        }
        return pack;
    }
}
