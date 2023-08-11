package com.wtech.gziptests;

import java.util.Objects;

public class MeasureBuilder {
    private SerializationType type;
    private int bufferByteArray;
    private int bufferCompress;
    private ByteSizeType bufferByteArraySizeType;
    private ByteSizeType bufferCompressSizeType;
    private int originalSerialFileSizeBytes;

    public MeasureBuilder type(SerializationType type) {
        this.type = type;
        return this;
    }

    public MeasureBuilder bufferByteArray(int bufferByteArray) {
        this.bufferByteArray = bufferByteArray;
        return this;
    }

    public MeasureBuilder bufferCompress(int bufferCompress) {
        this.bufferCompress = bufferCompress;
        return this;
    }

    public MeasureBuilder singleBufferValue(int buffer) {
        this.bufferByteArray = buffer;
        this.bufferCompress = buffer;
        return this;
    }

    public MeasureBuilder bufferByteArraySizeType(ByteSizeType bufferByteArraySizeType) {
        this.bufferByteArraySizeType = bufferByteArraySizeType;
        return this;
    }

    public MeasureBuilder bufferCompressSizeType(ByteSizeType bufferCompressSizeType) {
        this.bufferCompressSizeType = bufferCompressSizeType;
        return this;
    }

    public MeasureBuilder singleBufferSizeType(ByteSizeType bufferSizeType) {
        this.bufferByteArraySizeType = bufferSizeType;
        this.bufferCompressSizeType = bufferSizeType;
        return this;
    }

    public MeasureBuilder originalSerialFileSizeBytes(int originalSerialFileSizeBytes) {
        this.originalSerialFileSizeBytes = originalSerialFileSizeBytes;
        return this;
    }

    public Measure createMeasure() {
        Objects.requireNonNull(type);
        return new Measure(type, bufferByteArray, bufferCompress, bufferByteArraySizeType, bufferCompressSizeType, originalSerialFileSizeBytes);
    }
}