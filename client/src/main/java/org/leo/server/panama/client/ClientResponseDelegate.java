package org.leo.server.panama.client;

import org.leo.server.panama.core.connector.Response;

public interface ClientResponseDelegate<E extends Response> {
    default boolean shouldDoPerResponse() {
        return false;
    }

    default boolean shouldDoCompleteResponse() {
        return true;
    }

    default void onResponseComplete(Client client) {
        //
    }

    default void doCompleteResponse(Client client, E response) {
        //
    }

    default void doPerResponse(Client client, E response) {
        //
    }

    default void onConnectClosed(Client client) {
        //
    }
}
