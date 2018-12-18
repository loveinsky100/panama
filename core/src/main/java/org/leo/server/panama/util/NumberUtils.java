package org.leo.server.panama.util;

/**
 * @author xuyangze
 * @date 2018/10/9 上午11:45
 */
public class NumberUtils {
    public static int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static Integer toInteger(String value, Integer defaultValue) {
        try {
            defaultValue = Integer.parseInt(value);
        } catch (Exception e) {
            //
        }

        return defaultValue;
    }
}
