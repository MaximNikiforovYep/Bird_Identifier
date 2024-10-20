package com.example.birdidentifier.containers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;

public class ShortArray {
    protected short[] buf;
    protected int count;

    public ShortArray() {
        this(32);
    }

    public ShortArray(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: "
                    + size);
        }
        buf = new short[size];
    }


    private void ensureCapacity(int minCapacity) {
        if (minCapacity - buf.length > 0)
            grow(minCapacity);
    }

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = buf.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        buf = Arrays.copyOf(buf, newCapacity);
    }

    public void write(short b) {
        ensureCapacity(count + 1);
        buf[count] = b;
        count += 1;
    }


    public void write(short[] b, int off, int len) {
        Objects.checkFromIndexSize(off, len, b.length);
        ensureCapacity(count + len);
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }


    public void writeBytes(short[] b) {
        write(b, 0, b.length);
    }

    public void reset() {
        count = 0;
    }

    public short[] toShortArray() {
        return Arrays.copyOf(buf, count);
    }

    public int size() {
        return count;
    }

}
