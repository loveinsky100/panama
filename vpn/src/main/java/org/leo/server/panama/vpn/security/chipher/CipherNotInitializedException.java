package org.leo.server.panama.vpn.security.chipher;

public class CipherNotInitializedException extends RuntimeException {
    public CipherNotInitializedException() {
        super();
    }

    public CipherNotInitializedException(String s) {
        super(s);
    }
}
