package org.leo.server.panama.core.connector;

public interface Response {
    /**
     * 获取响应信息
     * @return
     */
    String getMessage();

    /**
     * 获取byte信息
     * @return
     */
    default byte []getData() {
        return getMessage().getBytes();
    }
}
