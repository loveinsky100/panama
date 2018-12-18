package org.leo.server.panama.vpn.security.chipher;

import javax.crypto.SecretKey;

public abstract class Cipher {
    protected SecretKey key;
    protected byte[] iv;
    protected boolean isEncrypt;

    public void init(boolean isEncrypt, byte[] iv) {
        if (this.iv == null) {
            this.isEncrypt = isEncrypt;
            this.iv = iv;
            _init(isEncrypt, iv);
        } else {
            throw new RuntimeException("cipher cannot reinitiate");
        }
    }

    public byte[] encrypt(byte[] originData) {
        if (this.iv == null)
            throw new CipherNotInitializedException();
        if (!isEncrypt)
            throw new RuntimeException("cannot encrypt in decrypt mode");
        return _encrypt(originData);
    }

    public byte[] decrypt(byte[] encryptedData) {
        if (this.iv == null)
            throw new CipherNotInitializedException();
        if (isEncrypt)
            throw new RuntimeException("cannot decrypt in encrypt mode");
        return _decrypt(encryptedData);
    }

    public SecretKey getKey() {
        return key;
    }

    public byte[] getIV() {
        return iv;
    }

    public abstract int getIVLength();

    protected abstract void _init(boolean isEncrypt, byte[] iv);

    protected abstract byte[] _encrypt(final byte[] originData);

    protected abstract byte[] _decrypt(final byte[] encryptedData);

}
