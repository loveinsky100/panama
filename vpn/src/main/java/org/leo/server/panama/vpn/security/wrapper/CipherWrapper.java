package org.leo.server.panama.vpn.security.wrapper;

import org.leo.server.panama.vpn.security.chipher.Cipher;
import org.leo.server.panama.vpn.security.chipher.KeyHelper;

import java.util.Arrays;

public class CipherWrapper extends Wrapper {
    private final Cipher encipher;
    private final Cipher decipher;
    private byte[] encipherIv;
    private byte[] decipherIv;

    public CipherWrapper(Cipher encipher, Cipher decipher) {
        if (encipher.getClass() != decipher.getClass())
            throw new RuntimeException("cipher type not match");

        this.encipher = encipher;
        this.decipher = decipher;
    }

    @Override
    public byte[] wrap(final byte[] bytes) {
        if (encipherIv == null) {
            int ivLength = encipher.getIVLength();
            this.encipherIv = KeyHelper.generateRandomBytes(ivLength);
            encipher.init(true, encipherIv);
            byte[] encryptedBytes = new byte[ivLength + bytes.length];
            System.arraycopy(encipherIv, 0, encryptedBytes, 0, ivLength);
            System.arraycopy(encipher.encrypt(bytes), 0, encryptedBytes, ivLength, bytes.length);
            return encryptedBytes;
        }
        return encipher.encrypt(bytes);
    }

    @Override
    public byte[] unwrap(final byte[] bytes) {
        if (decipherIv == null) {
            int ivLength = decipher.getIVLength();
            if (bytes.length < ivLength)
                throw new RuntimeException("invalid encrypted data");

            this.decipherIv = Arrays.copyOfRange(bytes, 0, ivLength);
            decipher.init(false, decipherIv);
            byte[] encryptedBytes = new byte[bytes.length - ivLength];
            System.arraycopy(bytes, ivLength, encryptedBytes, 0, encryptedBytes.length);
            return decipher.decrypt(encryptedBytes);
        }
        return decipher.decrypt(bytes);
    }
}
