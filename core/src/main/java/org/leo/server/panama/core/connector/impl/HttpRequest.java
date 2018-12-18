package org.leo.server.panama.core.connector.impl;

import org.leo.server.panama.core.connector.Request;

public interface HttpRequest extends Request, io.netty.handler.codec.http.HttpRequest, Attribute, Function {

}