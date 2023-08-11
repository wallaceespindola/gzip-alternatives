package com.wtech.gziptests;

import org.apache.commons.io.FileUtils;

import static com.wtech.gziptests.SerializationTestUtils.getKbyteOrMegabyteString;

public class Measure {

    private final SerializationType type;
    private final int bufferByteArray;
    private int bufferCompress;
    private final ByteSizeType bufferByteArraySizeType;
    private ByteSizeType bufferCompressSizeType;
    private final int originalSerialFileSizeBytes;
    private long bytesCount;
    private long elapsed;

    public Measure(SerializationType type, int bufferByteArray, int bufferCompress, ByteSizeType bufferByteArraySizeType, ByteSizeType bufferCompressSizeType, int originalSerialFileSizeBytes) {
        this.type = type;
        this.bufferByteArray = bufferByteArray;
        this.bufferCompress = bufferCompress;
        this.bufferByteArraySizeType = bufferByteArraySizeType;
        this.bufferCompressSizeType = bufferCompressSizeType;
        this.originalSerialFileSizeBytes = originalSerialFileSizeBytes;
    }

    public SerializationType getType() {
        return this.type;
    }

    public long getElapsed() {
        return this.elapsed;
    }

    public void setBytesCount(long bytesCount) {
        this.bytesCount = bytesCount;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    public int getOriginalSerialFileSizeBytes() {
        return originalSerialFileSizeBytes;
    }

    public int getBufferByteArrayCalculated() {
        return SerializationTestUtils.calculateKbyteOrMegabyteBufferSize(bufferByteArraySizeType, bufferByteArray);
    }

    public int getBufferCompressCalculated() {
        return SerializationTestUtils.calculateKbyteOrMegabyteBufferSize(bufferCompressSizeType, bufferCompress);
    }

    public String getDuration() {
        return (elapsed / 1000000L) + " ms";
    }

    public String getBytesCountToDisplay() {
        return FileUtils.byteCountToDisplaySize(bytesCount);
    }

    public String getOriginalBytesCountToDisplay() {
        return FileUtils.byteCountToDisplaySize(originalSerialFileSizeBytes);
    }

    public String getCompressionRate() {
        return type.isByteArray() ? "0 %" :
                String.format("%.2f", ((1 - (float) bytesCount / (float) originalSerialFileSizeBytes)) * 100.0f) + " %";
    }

    private String getFormattedByteArrayBuffer() {
        return SerializationTestUtils.BYTE_ARRAY_BUFFER + bufferByteArray + getKbyteOrMegabyteString(bufferByteArraySizeType);
    }

    private String getFormattedCompressBuffer() {
        return type.isByteArray() ? "No compression buffer" :
                SerializationTestUtils.COMPRESS_BUFFER + bufferCompress + getKbyteOrMegabyteString(bufferCompressSizeType);
    }

    private String getFormattedOriginalBytes() {
        return "original (" + getOriginalBytesCountToDisplay() + ")";
    }

    private String getFormattedBytes() {
        return "actual (" + getBytesCountToDisplay() + ")";
    }

    private String getFormattedCompressionRate() {
        return "compression (" + getCompressionRate() + ")";
    }

    /**
     * Returns the description like below:
     * 36 ms : ByteArray Serialization - ByteArrayOutputStream_Buffer (16 MB) - CompressOutputStream_BufferSize (0) - 15100723 bytes (14 MB)
     * 71 ms : Pack 200 Compressor - ByteArrayOutputStream_Buffer (8 KB) - CompressOutputStream_BufferSize (8 KB) - 20 bytes (20 bytes)
     * 88 ms : GZIP Parallel - ByteArrayOutputStream_Buffer (16 KB) - CompressOutputStream_BufferSize (16 KB) - 6950584 bytes (6 MB)
     *
     * @return the description of the measure made
     */
    public String description() {
        return getDuration() +
                " : " + type.description() +
                " - " + getFormattedByteArrayBuffer() +
                " - " + getFormattedCompressBuffer() +
                " - " + getFormattedOriginalBytes() +
                " - " + getFormattedBytes() +
                " - " + getFormattedCompressionRate();
    }

    public String fileDescription() {
        return type.name() +
                " - " + getFormattedByteArrayBuffer() +
                " - " + getFormattedCompressBuffer();
    }

    /**
     * Returns the CSV description like below:
     * GZIP Parallel;1 ms;2 MB;
     * GZIP Standard;1 ms;6 MB;
     * GZIP Compressor Apache;8 ms;8 MB;
     *
     * @return the description of the measure made
     */
    public String csvDescription() {
        return getDuration() + ";" +
                getOriginalBytesCountToDisplay() + ";" +
                getBytesCountToDisplay() + ";" +
                originalSerialFileSizeBytes + ";" +
                bytesCount + ";" +
                getCompressionRate() + ";" +
                type.description() + ";";
    }

    @Override
    public String toString() {
        return "Measure{" +
                "type=" + type +
                ", bufferByteArray=" + bufferByteArray +
                ", bufferCompress=" + bufferCompress +
                ", originalSerialFileSizeBytes=" + bufferByteArraySizeType +
                ", bufferCompressSizeType=" + bufferCompressSizeType +
                ", bufferByteArraySizeType=" + originalSerialFileSizeBytes +
                ", bytesCount=" + bytesCount +
                ", elapsed=" + elapsed +
                '}';
    }

    public void cleanBufferCompress() {
        bufferCompress = 0;
    }

    public void cleanBufferCompressSizeType() {
        bufferCompressSizeType = ByteSizeType.NONE;
    }
}
