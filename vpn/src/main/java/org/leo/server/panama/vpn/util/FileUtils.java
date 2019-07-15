package org.leo.server.panama.vpn.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.stream.Collectors;

/**
 * @author xuyangze
 * @date 2019/6/7 12:02 PM
 */
public class FileUtils {
    private final static Logger log = Logger.getLogger(FileUtils.class);

    private static File CURRENT = new File("");

    public static String read(String fileName, String defaultValue) {
        String currentPath = getCurrentPath();
        if (null == currentPath || currentPath.length() == 0) {
            return null;
        }

        File file = new File(currentPath + "/" + fileName);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    log.error("create config file false");
                    return null;
                }

                writeToFile(file, defaultValue);
                return defaultValue;
            } catch (Exception e) {
                log.error("create config file error", e);
            }
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String result = bufferedReader.lines().map(value -> value + "\n").collect(Collectors.joining());
            if (null == result || result.length() == 0) {
                writeToFile(file, defaultValue);
                return defaultValue;
            }

            return result;
        } catch (Exception e) {
            log.error("read config file error", e);
        }

        log.error("read config file error, use default value");
        return defaultValue;
    }

    public static String getCurrentPath() {
        try {
            return CURRENT.getCanonicalPath();
        } catch (IOException e) {
            log.error("can not read current path", e);
        }

        return null;
    }

    private static void writeToFile(File file, String value) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            for (byte aByte : value.getBytes()) {
                fileOutputStream.write(aByte);
            }
        } catch (Exception e) {
            log.error("write to file error", e);
        }  finally {
            if (null != fileOutputStream) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    log.error("close fileOutputStream error", e);
                }
            }
        }

    }

    public static void main(String []args) {
        System.out.println(read("panama.config", "hello world"));
    }
}
