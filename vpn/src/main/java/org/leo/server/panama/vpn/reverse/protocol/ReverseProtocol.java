package org.leo.server.panama.vpn.reverse.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.leo.server.panama.util.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 内网穿透内网同外网协议
 * 前32位为TAG，标记客户端的请求信息
 * 32-64为body的长度
 *
 * |-----|--------|------|
 * | 32  |   32   |      |
 * |-----|--------|------|
 * | TAG |  SIZE  | BODY |
 * |-----|--------|------|
 *
 * @author xuyangze
 * @date 2019/7/16 2:52 PM
 */
public class ReverseProtocol {
    /**
     * 分装成协议
     * @param tag
     * @param body
     * @return
     */
    public static ByteBuf encodeProtocol(int tag, byte []body) {
        int size = body.length;
        byte []tagByte = NumberUtils.intToByteArray(tag);
        byte []sizeByte = NumberUtils.intToByteArray(size);

        return Unpooled.wrappedBuffer(tagByte, sizeByte, body);
    }

    /**
     * 解析协议信息，多个请求可能被粘包导致一个content实际存在多请求信息
     * @param content
     * @return
     */
    public static List<ReverseProtocolData> decodeProtocol(byte []content) {
        List<ReverseProtocolData> reverseProtocolDatas = new ArrayList<>(2);
        int start = 0;
        while (start < content.length) {
            // 取出tag
            int tag = NumberUtils.byteArrayToInt(content, start);

            start += 4;
            int size = NumberUtils.byteArrayToInt(content, start);

            start += 4;

            if (content.length < (start + size)) {
                break;
            }

            byte []data = new byte[size];
            System.arraycopy(content, start, data, 0, size);
            ReverseProtocolData reverseProtocolData = new ReverseProtocolData();
            reverseProtocolData.setTag(tag);
            reverseProtocolData.setData(data);

            reverseProtocolDatas.add(reverseProtocolData);
            start += size;
        }

        return reverseProtocolDatas;
    }

    public static class ReverseProtocolData {
        private int tag;

        private byte[] data;

        public int getTag() {
            return tag;
        }

        public void setTag(int tag) {
            this.tag = tag;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }
    }
}
