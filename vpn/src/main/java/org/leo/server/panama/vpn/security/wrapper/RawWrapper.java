package org.leo.server.panama.vpn.security.wrapper;

public class RawWrapper extends Wrapper {
    @Override
    public byte[] wrap(final byte[] bytes) {
        return bytes;
    }

    @Override
    public byte[] unwrap(final byte[] bytes) {
        return bytes;
    }
}
