package org.leo.server.panama.core.connector.impl;

import org.leo.server.panama.core.connector.Request;

public interface WebSocketRequest extends Request, Attribute, Function {
    String message();
}
