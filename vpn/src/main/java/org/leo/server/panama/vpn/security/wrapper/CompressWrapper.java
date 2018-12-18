package org.leo.server.panama.vpn.security.wrapper;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CompressWrapper extends Wrapper {
    private ByteArrayOutputStream wrapBuffer;
    private ByteArrayOutputStream unwrapBuffer;
    private Deflater deflater;
    private Inflater inflater;

    public CompressWrapper() {
        this.wrapBuffer = new ByteArrayOutputStream();
        this.unwrapBuffer = new ByteArrayOutputStream();
        this.deflater = new Deflater(Deflater.BEST_SPEED);
        this.inflater = new Inflater();
    }

    @Override
    public byte[] wrap(byte[] bytes) {
        deflater.setInput(bytes);
        deflater.finish();
        int len;
        byte[] buffer = new byte[bytes.length];
        while (!deflater.finished()) {
            len = deflater.deflate(buffer, 0, buffer.length);
            if (len > 0)
                wrapBuffer.write(buffer, 0, len);
        }
        buffer = wrapBuffer.toByteArray();
        deflater.reset();
        wrapBuffer.reset();
        return buffer;
    }

    @Override
    public byte[] unwrap(byte[] bytes) {
        inflater.setInput(bytes);
        try {
            int len;
            byte[] buffer = new byte[bytes.length];
            while (!inflater.finished()) {
                len = inflater.inflate(buffer, 0, buffer.length);
                if (len > 0)
                    unwrapBuffer.write(buffer, 0, len);
            }
            return unwrapBuffer.toByteArray();
        } catch (DataFormatException e) {
            throw new RuntimeException("unknown format: " + e.getMessage());
        } finally {
            inflater.reset();
            unwrapBuffer.reset();
        }
    }

}
