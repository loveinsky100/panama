package org.leo.server.panama.vpn.configuration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.leo.server.panama.vpn.util.FileUtils;
/**
 * @author xuyangze
 * @date 2018/11/22 4:27 PM
 */
public class ConfigurationReader {
    private static final String CONFIG_FILE_NAME = "panama.config";

    private static final String DEFAULT_PANAMA_CONFIG = JSON.toJSONString(new ShadowSocksConfiguration(), SerializerFeature.PrettyFormat);

    public static ShadowSocksConfiguration read() {
        return read(null);
    }

    public static ShadowSocksConfiguration read(String configFileName) {
        if (null == configFileName || configFileName.length() == 0) {
            configFileName = CONFIG_FILE_NAME;
        }

        String config = FileUtils.read(configFileName, DEFAULT_PANAMA_CONFIG);
        ShadowSocksConfiguration shadowSocksConfiguration = JSON.parseObject(config, ShadowSocksConfiguration.class);
        return shadowSocksConfiguration;
    }

    public static void main(String []args) {
        ShadowSocksConfiguration shadowSocksConfiguration = read();
        System.out.println(JSON.toJSONString(shadowSocksConfiguration));
    }
}
