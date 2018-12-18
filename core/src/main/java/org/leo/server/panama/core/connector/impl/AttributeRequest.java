package org.leo.server.panama.core.connector.impl;

import org.leo.server.panama.core.connector.Request;

import java.util.HashMap;
import java.util.Map;

public abstract class AttributeRequest implements Attribute, Request {

    private Map<String, String> attributeMap = new HashMap<String, String>();

    @Override
    public void setMessage(String message) {
        String query = (String) message;
        if (null == query || query.length() == 0) {
            return;
        }

        String []queries = query.split("&");
        if (null == queries || queries.length == 0) {
            return;
        }

        for (String qry : queries) {
            String []nameAndValue = qry.split("=", 2);
            if (null == nameAndValue || nameAndValue.length != 2) {
                continue;
            }

            putAttribute(nameAndValue[0], nameAndValue[1]);
        }
    }

    @Override
    public String getAttribute(String name) {
        if (null == name || name.length() == 0) {
            return null;
        }

        return attributeMap.get(name);
    }

    @Override
    public void putAttribute(String name, String value) {
        if (null == name || name.length() == 0) {
            return;
        }

        if (null == value) {
            attributeMap.remove(value);
        }

        attributeMap.put(name, value);
    }
}
