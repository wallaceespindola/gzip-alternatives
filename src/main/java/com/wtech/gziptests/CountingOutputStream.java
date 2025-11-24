package com.wtech.gziptests;

import java.io.OutputStream;

public class CountingOutputStream extends OutputStream {
    public long count = 0;

    @Override
    public void write(int b) {
        count++;
    }

    @Override
    public void write(byte[] b, int off, int len) {
        count += len;
    }
}
