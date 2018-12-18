package org.leo.server.panama.core.handler;

import io.netty.channel.ChannelHandlerContext;
import org.leo.server.panama.core.connector.Request;

public interface RequestHandler<T extends Request> {
    default void onConnect(ChannelHandlerContext ctx) {

    }

    void doRequest(T request);

    default void onClose(ChannelHandlerContext ctx) {

    }
}