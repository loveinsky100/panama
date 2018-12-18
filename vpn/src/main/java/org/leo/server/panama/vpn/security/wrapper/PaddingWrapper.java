package org.leo.server.panama.vpn.security.wrapper;

public abstract class PaddingWrapper extends Wrapper {
    protected int paddingThreshold;
    protected int paddingRange;
    protected int headerLength;

    // if size < threshold + range, then do padding,
    // no package will lower then threshold,
    // no package with padding will above threshold + range
    // +---------+-----+-------------+---------------------+------------+
    // |  size:  |  4  |  threshold  |  threshold + range  |  infinite  |
    // +---------+-----+-------------+---------------------+------------+
    // | option: | len |            with padding           | no padding |
    // +---------+-----+-----------------------------------+------------+
    public PaddingWrapper(int paddingThreshold, int paddingRange) {
        if (paddingThreshold < paddingRange || paddingRange < 4)
            throw new RuntimeException("bad padding range, 4 to threshold is accepted");
        this.paddingThreshold = paddingThreshold;
        this.paddingRange = paddingRange;
        this.headerLength = 4; // 4 bytes integer (default)
    }

}
