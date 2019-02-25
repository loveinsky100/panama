package org.leo.server.panama.client;

import org.leo.server.panama.core.connector.Response;

public interface ClientResponseDelegate<E extends Response> {
    /**
     * 一次请求分为多个包，是否接收到每个包都进行回调doPerResponse
     * @return
     */
    default boolean shouldDoPerResponse() {
        return false;
    }

    /**
     * 一次请求分为多个包，是否接收到所有的包后调用doCompleteResponse
     * @return
     */
    default boolean shouldDoCompleteResponse() {
        return true;
    }

    /**
     * 响应信息处理结束
     * @param client
     */
    default void onResponseComplete(Client client) {
        //
    }

    /**
     * 响应信息数据接收结束
     * @param client
     * @param response
     */
    default void doCompleteResponse(Client client, E response) {
        //
    }

    /**
     * 单个响应数据包接收结束
     * @param client
     * @param response
     */
    default void doPerResponse(Client client, E response) {
        //
    }

    /**
     * 关闭连接事件
     * @param client
     */
    default void onConnectClosed(Client client) {
        client.setClose(true);
    }
}
