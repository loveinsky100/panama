package org.leo.server.panama.core.connector.impl;

public interface Attribute {
    String getAttribute(String name);
    void putAttribute(String name, String value);
}
