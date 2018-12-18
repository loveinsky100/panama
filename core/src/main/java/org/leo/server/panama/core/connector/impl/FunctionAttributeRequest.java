package org.leo.server.panama.core.connector.impl;
public abstract class FunctionAttributeRequest extends AttributeRequest implements Function {
    private String function;

    @Override
    public void setMessage(String message) {

        String []uriAndQuery = message.split("\\?", 2);
        if (null != uriAndQuery && uriAndQuery.length == 2) {
            function = uriAndQuery[0];
            super.setMessage(uriAndQuery[1]);
        } else {
            function = message;
            super.setMessage(message);
        }
    }

    @Override
    public String function() {
        return function;
    }

    protected void setFunction(String function) {
        this.function = function;
    }
}
