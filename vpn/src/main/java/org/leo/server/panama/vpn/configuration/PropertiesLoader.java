package org.leo.server.panama.vpn.configuration;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author xuyangze
 * @date 2018/10/10 下午3:16
 */
public class PropertiesLoader {
    private static final String ANNOTATION1 = "#";
    private static final String ANNOTATION2 = "//";

    public static Map<String, String> loadText(String data) {
        if (isEmpty(data)) {
            return null;
        }

        String []lines = data.split("\n");
        if (null == lines || lines.length == 0) {
            return null;
        }

        Map<String, String> dataMap = Maps.newHashMap();
        for (String line : lines) {
            if (line.startsWith(ANNOTATION1) || line.startsWith(ANNOTATION2)) {
                continue;
            }

            String[] keyAndValue = line.split("=");
            if (null == keyAndValue || keyAndValue.length <= 1) {
                continue;
            }

            String key = keyAndValue[0].trim();
            String value = keyAndValue[1];

            if (value.endsWith("\r")) {
                value = value.substring(0, value.length() - 1);
            }

            dataMap.put(key, value);
        }

        return dataMap;
    }

    private static boolean isEmpty(String data) {
        return null == data || data.length() == 0;
    }
}
