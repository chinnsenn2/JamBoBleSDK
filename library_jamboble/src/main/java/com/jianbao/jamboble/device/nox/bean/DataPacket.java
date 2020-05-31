package com.jianbao.jamboble.device.nox.bean;

import com.jianbao.jamboble.data.BTData;

import java.nio.ByteBuffer;

public abstract class DataPacket extends BTData {

    public String TAG = getClass().getSimpleName();

    public static final byte MAX_WRITE_SIZE = 20;

    /**
     * post ->ack 无msgtype
     * requ ->resp 有msgtype
     */
    public static class PacketType {
        public static final byte FA_ACK = 0x00;
        public static final byte FA_POST = 0x01;
        public static final byte FA_REQUEST = 0x02;
        public static final byte FA_RESPONSE = 0x03;
    }

    public static class BasePacket {
        public ByteBuffer parseBuffer(ByteBuffer buffer) {
            return buffer;
        }

        public ByteBuffer fillBuffer(ByteBuffer buffer) {
            return buffer;
        }

    }


    public abstract static class PacketHead {
        private static byte mSenquence = 0;
        public static final byte VER = 0;
        public byte version;
        public byte type;
        public byte senquence;
        public byte btCount;
        public byte btIndex;
        public short deviceType;

        public abstract ByteBuffer parseBuffer(ByteBuffer buffer);

        public abstract ByteBuffer fillBuffer(ByteBuffer buffer);

        public synchronized static byte getSenquence() {
            return mSenquence++;
        }

        @Override
        public String toString() {
            return "type:" + type + ",sec:" + senquence;
        }
    }


    public static class PacketBody {
        public byte type; // 消息类型
        public BasePacket content; // 消息正文

        public PacketBody() {
        }

        public PacketBody(byte type, BasePacket content) {
            this.type = type;
            this.content = content;
        }

        public ByteBuffer parseBuffer(PacketHead head, ByteBuffer buffer) {
            return buffer;
        }


        public ByteBuffer fillBuffer(PacketHead head, ByteBuffer buffer) {
            return buffer;
        }

        @Override
        public String toString() {
            return "type:0x" + Integer.toHexString(type);
        }
    }


    public PacketHead head;
    public PacketBody msg;
    public int crc32;
    public ByteBuffer buffer;

    public abstract boolean check(ByteBuffer buffer);

    public abstract boolean parse(ByteBuffer buffer);

    public abstract ByteBuffer parseBuffer(ByteBuffer buffer);

    public abstract boolean fill(byte btType, byte btSeq);

    public abstract ByteBuffer fillBuffer(ByteBuffer buffer);


    @Override
    public String toString() {
        return "Head[type:0x" + Integer.toHexString(head.type) + ",sec:" + head.senquence + "],Body[" + msg + "]";
    }


    public static class BaseRspPack extends BasePacket {
        public static final byte SUCCESS = 0x0;

        public byte type;
        public byte rspCode;

        public BaseRspPack() {
        }

        public BaseRspPack(byte type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "BaseRspPack{" +
                    "type=" + type +
                    ",rspCode=" + rspCode +
                    '}';
        }

        @Override
        public ByteBuffer parseBuffer(ByteBuffer buffer) {
            this.rspCode = buffer.get();
            return buffer;
        }

        @Override
        public ByteBuffer fillBuffer(ByteBuffer buffer) {
            buffer.put(this.rspCode);
            return buffer;
        }
    }

}













