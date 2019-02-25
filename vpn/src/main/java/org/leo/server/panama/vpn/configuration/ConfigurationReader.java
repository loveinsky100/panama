package org.leo.server.panama.vpn.configuration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xuyangze
 * @date 2018/11/22 4:27 PM
 */
public class ConfigurationReader {
    private static final String CONFIG_FILE_NAME = "panama.config";

    public static ShadowSocksConfiguration read() {
        InputStream inputStream = ConfigurationReader.class.getClassLoader().getResourceAsStream("panama.config");
        String result = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining(System.lineSeparator()));

        Map<String, String> data = PropertiesLoader.loadText(result);
        return null;
    }
}
