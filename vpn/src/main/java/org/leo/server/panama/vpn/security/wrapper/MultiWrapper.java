package org.leo.server.panama.vpn.security.wrapper;

import java.util.Arrays;

public class MultiWrapper extends Wrapper {
    private Wrapper[] wrappers;

    // inner_wrapper -> outer_wrapper
    public MultiWrapper(Wrapper... wrappers) {
        this.wrappers = wrappers;
    }

    @Override
    public byte[] wrap(final byte[] bytes) {
        byte[] tmp = Arrays.copyOf(bytes, bytes.length);
        // wrap: data) wrap0) wrap1)... wrapN)
        for (Wrapper wrapper : wrappers) {
            tmp = wrapper.wrap(tmp);
        }
        return tmp;
    }

    @Override
    public byte[] unwrap(final byte[] bytes) {
        byte[] tmp = Arrays.copyOf(bytes, bytes.length);
        // unwrap: (wrapN ... (wrap1 (wrap0 (data
        for (int i = 0; i < wrappers.length; i++) {
            tmp = wrappers[wrappers.length - i - 1].unwrap(tmp);
        }
        return tmp;
    }
}
