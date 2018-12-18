package org.leo.server.panama.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipUtils {

    /**
     * gzip加密
     * @param data
     * @return
     * @throws Exception
     * byte[]
     */
    public static byte[] gzip(byte[] data) throws Exception {  
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(data);  
        gzip.finish();  
        gzip.close();  
        byte[] ret = bos.toByteArray();  
        bos.close();  
        return ret;  
    }  

    /**
     *  gzip解密
     * @param data
     * @return
     * @throws Exception
     * byte[]
     */
    public static byte[] ungzip(byte[] data) throws Exception {  
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        GZIPInputStream gzip = new GZIPInputStream(bis);
        byte[] buf = new byte[1024];  
        int num = -1;  
        ByteArrayOutputStream bos = new ByteArrayOutputStream();  
        while ((num = gzip.read(buf, 0, buf.length)) != -1) {  
            bos.write(buf, 0, num);  
        }  
        gzip.close();  
        bis.close();  
        byte[] ret = bos.toByteArray();  
        bos.flush();  
        bos.close();  
        return ret;  
    }  
    
    
    /**
     * gizp数据解压
     * @param bytes
     * @return
     * @throws IOException
     * String
     */
    public static String uncompress(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();   
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);   
        GZIPInputStream gunzip = new GZIPInputStream(in);   
        byte[] buffer = new byte[256];   
        int n;   
        while ((n = gunzip.read(buffer))>= 0) {   
            out.write(buffer, 0, n);   
        }   
        // toString()使用平台默认编码，也可以显式的指定如toString(&quot;GBK&quot;)   
        return out.toString();   
    }
    
    
    
    /**
     * gizp解压
     * @param buf
     * @return
     * @throws IOException
     * byte[]
     */
    public static byte[] unGzip(byte[] buf) throws IOException {  
        GZIPInputStream gzi = null;  
        ByteArrayOutputStream bos = null;  
        try {  
            gzi = new GZIPInputStream(new ByteArrayInputStream(buf));  
            bos = new ByteArrayOutputStream(buf.length);  
            int count = 0;  
            byte[] tmp = new byte[2048];  
            while ((count = gzi.read(tmp)) != -1) {  
                bos.write(tmp, 0, count);  
            }  
            buf = bos.toByteArray();  
        } finally {  
            if (bos != null) {  
                bos.flush();  
                bos.close();  
            }  
            if (gzi != null)  
                gzi.close();  
        }  
        return buf;  
    }

}