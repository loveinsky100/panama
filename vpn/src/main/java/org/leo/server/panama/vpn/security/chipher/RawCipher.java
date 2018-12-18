package org.leo.server.panama.vpn.security.chipher;

public class RawCipher extends Cipher {
    @Override
    protected void _init(boolean isEncrypt, byte[] iv) {
        this.iv = new byte[0];
    }

    @Override
    protected byte[] _encrypt(final byte[] originData) {
        return originData;
    }

    @Override
    protected byte[] _decrypt(final byte[] encryptedData) {
        return encryptedData;
    }

    @Override
    public int getIVLength() {
        return 0;
    }
}
