package org.leo.server.panama.vpn.security.wrapper;

public interface Parcelable {
    byte[] wrap(final byte[] bytes);

    byte[] unwrap(final byte[] bytes);
}
