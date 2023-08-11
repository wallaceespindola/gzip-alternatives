package com.wtech.gziptests;

import org.anarres.parallelgzip.ParallelGZIPOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPOutputStream;

import static com.wtech.gziptests.SerializationTestUtils.*;
import static com.wtech.gziptests.SerializationType.BYTE_ARRAY;

/**
 * Class used to test multiple serialization and compression alternatives to gzip.
 * <p>
 * TESTS DONE USING:
 * org.apache.commons.compress.compressors v1.23.0 (bzip2, deflate, gzip, lz4.block, lz4.framed, lzma, pack200, snappy.framed, snappy, xz, zstandard)
 * org.lz4.lz4-java v1.8.0
 * com.nixxcode.jvmbrotli v0.2.0
 * com.aayushatharva.brotli4j v1.12.0
 * org.anarres.parallelgzip v1.0.5 (also Shevek parallel gzip)
 */
public class TestCompression {

    private int originalSerialFileSizeBytes;
    private static final Map<Long, Measure> benchmarks = new TreeMap<>(); // Sorted by key

    public TestCompression() {
    }

    public static void main(String[] args) {

        TestCompression test = new TestCompression();
        SerializationTestUtils testUtils = new SerializationTestUtils();

        MyFile myFile = testUtils.saveAndRetrieveMyFile();
        test.setOriginalSerialFileSizeBytes(testUtils.getOriginalSerialFileSizeBytes());

        EnumSet<SerializationType> serializationType = SerializationType.LZ4_TYPES; // Change it to have other serialization tests
        TestType testType = TestType.SHORT; // Change it to have other test types

        System.out.println("\n[WALLY] ########## TESTING TYPE: " + testType);
        switch (testType) {
            case SHORT:
                serializationType.forEach(serialType -> test.testShortSerialization(myFile, serialType));
                break;
            case STANDARD:
                serializationType.forEach(serialType -> test.testStandardSerialization(myFile, serialType));
                break;
            case LONG:
                serializationType.forEach(serialType -> test.testStandardSerializationLong(myFile, serialType));
                break;
            default:
                System.out.println(WARNING_NOT_A_KNOWN_TYPE);
                break;
        }

        System.out.println("\n[WALLY] >>>>>>>>>>>>>>> FINISHED SERIALIZATION AND COMPRESSION TESTS <<<<<<<<<<<<<<<");
        testUtils.printBenchmarks(benchmarks);
    }

    private void setOriginalSerialFileSizeBytes(int originalSerialFileSizeBytes) {
        this.originalSerialFileSizeBytes = originalSerialFileSizeBytes;
    }

    /**
     * Test for Gzip all combinations: KB x MB buffers
     *
     * @param myFile
     * @param type
     */
    public void testStandardSerialization(MyFile myFile, SerializationType type) {
        System.out.println(STARTING_TEST + type);
        testStandardSerialization(myFile, type, ByteSizeType.BYTE_SIZE_KB, ByteSizeType.BYTE_SIZE_KB);
        testStandardSerialization(myFile, type, ByteSizeType.BYTE_SIZE_KB, ByteSizeType.BYTE_SIZE_MB);
        testStandardSerialization(myFile, type, ByteSizeType.BYTE_SIZE_MB, ByteSizeType.BYTE_SIZE_KB);
        testStandardSerialization(myFile, type, ByteSizeType.BYTE_SIZE_MB, ByteSizeType.BYTE_SIZE_MB);
    }

    /**
     * Test for Gzip long all combinations: KB x MB buffers
     *
     * @param myFile
     * @param type
     */
    public void testStandardSerializationLong(MyFile myFile, SerializationType type) {
        System.out.println(STARTING_TEST + type);
        testStandardSerializationLong(myFile, type, ByteSizeType.BYTE_SIZE_KB, ByteSizeType.BYTE_SIZE_KB);
        testStandardSerializationLong(myFile, type, ByteSizeType.BYTE_SIZE_KB, ByteSizeType.BYTE_SIZE_MB);
        testStandardSerializationLong(myFile, type, ByteSizeType.BYTE_SIZE_MB, ByteSizeType.BYTE_SIZE_KB);
        testStandardSerializationLong(myFile, type, ByteSizeType.BYTE_SIZE_MB, ByteSizeType.BYTE_SIZE_MB);
    }

    /**
     * Test simplified symmetric combination for buffers: KB-KB, MB-MB
     *
     * @param myFile
     * @param type
     */
    public void testShortSerialization(MyFile myFile, SerializationType type) {
        System.out.println(STARTING_TEST + type);
        testShortSerialization(myFile, type, ByteSizeType.BYTE_SIZE_KB);
        testShortSerialization(myFile, type, ByteSizeType.BYTE_SIZE_MB);
    }

    public void testShortSerialization(MyFile myFile, SerializationType type, ByteSizeType byteSizeType) {
        for (int i = 0; i <= 4; i++) {
            int buffer = (int) Math.pow(2, i); // from the for it gives: 1, 2, 4, 8, 16

            Measure measure = new MeasureBuilder()
                    .type(type)
                    .singleBufferValue(buffer)
                    .singleBufferSizeType(byteSizeType)
                    .originalSerialFileSizeBytes(originalSerialFileSizeBytes)
                    .createMeasure();

            testSerializationByType(myFile, measure);
        }
    }

    public void testStandardSerialization(MyFile myFile, SerializationType type,
                                          ByteSizeType byteArrayOutputStreamType, ByteSizeType gzipOutputStreamType) {
        for (int i = 0; i <= 4; i++) {
            int bufferByteArray = (int) Math.pow(2, i); // from the for it gives: 1, 2, 4, 8, 16

            for (int j = 0; j <= 4; j++) {
                int bufferGzip = (int) Math.pow(2, j); // from the for it gives: 1, 2, 4, 8, 16

                Measure measure = new MeasureBuilder()
                        .type(type)
                        .bufferByteArray(bufferByteArray)
                        .bufferByteArraySizeType(byteArrayOutputStreamType)
                        .bufferCompress(bufferGzip)
                        .bufferCompressSizeType(gzipOutputStreamType)
                        .originalSerialFileSizeBytes(originalSerialFileSizeBytes)
                        .createMeasure();

                testSerializationByType(myFile, measure);
            }
        }
    }

    /**
     * All combinations of buffer size for byteArrayOutputStream and gzipOutputStream, made by 2 for loops.
     *
     * @param myFile
     * @param type
     * @param byteArrayOutputStreamType
     * @param gzipOutputStreamType
     */
    public void testStandardSerializationLong(MyFile myFile, SerializationType type,
                                              ByteSizeType byteArrayOutputStreamType, ByteSizeType gzipOutputStreamType) {
        int byteArrayBufferSize, gzipBufferSize;

        for (int i = 1; i <= 16; i++) {
            byteArrayBufferSize = i;

            for (int j = 1; j <= 16; j++) {
                gzipBufferSize = j;

                Measure measure = new MeasureBuilder().type(type)
                        .bufferByteArray(byteArrayBufferSize)
                        .bufferByteArraySizeType(byteArrayOutputStreamType)
                        .bufferCompress(gzipBufferSize)
                        .bufferCompressSizeType(gzipOutputStreamType)
                        .originalSerialFileSizeBytes(originalSerialFileSizeBytes)
                        .createMeasure();

                testSerializationByType(myFile, measure);
            }
        }
    }

    public void testByteArrayOutputStream(MyFile myFile) {
        int bufferSize;
        for (int i = 1; i <= 16; i++) {
            bufferSize = i;
            Measure measure = new MeasureBuilder()
                    .type(BYTE_ARRAY)
                    .bufferByteArray(bufferSize)
                    .bufferByteArraySizeType(ByteSizeType.BYTE_SIZE_KB)
                    .originalSerialFileSizeBytes(originalSerialFileSizeBytes)
                    .createMeasure();
            testByteArraySerialization(myFile, measure);
        }
        for (int i = 1; i <= 16; i++) {
            bufferSize = i;
            Measure measure = new MeasureBuilder()
                    .type(BYTE_ARRAY)
                    .bufferByteArray(bufferSize)
                    .bufferByteArraySizeType(ByteSizeType.BYTE_SIZE_MB)
                    .originalSerialFileSizeBytes(originalSerialFileSizeBytes)
                    .createMeasure();
            testByteArraySerialization(myFile, measure);
        }
    }

    public void testByteArraySerialization(MyFile myFile, Measure measure) {
        long start = System.nanoTime();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(measure.getBufferByteArrayCalculated());
        serializeFile(myFile, measure, baos);
        measure.setBytesCount(baos.size());
        logDuration(start, measure, benchmarks);
    }

    public void testSerializationByType(MyFile myFile, Measure measure) {
        long start = System.nanoTime();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(measure.getBufferByteArrayCalculated());
        OutputStream os = instantiateOutputStreamByMeasureType(measure, baos);
        serializeFile(myFile, measure, os);
        measure.setBytesCount(baos.size());
        logDuration(start, measure, benchmarks);
    }

    public void testGzipSerialization(MyFile myFile, Measure measure) {
        try {
            long start = System.nanoTime();
            ByteArrayOutputStream baos = new ByteArrayOutputStream(measure.getBufferByteArrayCalculated());
            GZIPOutputStream os = new GZIPOutputStream(baos, measure.getBufferCompressCalculated());
            serializeFile(myFile, measure, os);
            measure.setBytesCount(baos.size());
            logDuration(start, measure, benchmarks);
        } catch (IOException ex) {
            System.out.println(CAUGHT_ERROR_PROCESSING + measure + " : " + ex);
        }
    }

    public void testParallelGzipSerialization(MyFile myFile, Measure measure) {
        try {
            long start = System.nanoTime();
            ByteArrayOutputStream baos = new ByteArrayOutputStream(measure.getBufferByteArrayCalculated());
            ParallelGZIPOutputStream os = new ParallelGZIPOutputStream(baos, measure.getBufferCompressCalculated());
            serializeFile(myFile, measure, os);
            measure.setBytesCount(baos.size());
            logDuration(start, measure, benchmarks);
        } catch (IOException ex) {
            System.out.println(CAUGHT_ERROR_PROCESSING + measure + " : " + ex);
        }
    }
}
