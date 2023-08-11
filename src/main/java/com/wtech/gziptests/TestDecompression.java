package com.wtech.gziptests;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;

import static com.wtech.gziptests.SerializationTestUtils.*;

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
public class TestDecompression {

    private int originalSerialFileSizeBytes;
    private static final Map<Long, Measure> benchmarksCompress = new TreeMap<>(); // Sorted by key
    private static final Map<Long, Measure> benchmarksUncompress = new TreeMap<>(); // Sorted by key

    public TestDecompression() {
    }

    public static void main(String[] args) {

        TestDecompression test = new TestDecompression();
        SerializationTestUtils testUtils = new SerializationTestUtils();

        MyFile myFile = testUtils.saveAndRetrieveMyFile();
        test.setOriginalSerialFileSizeBytes(testUtils.getOriginalSerialFileSizeBytes());

        EnumSet<SerializationType> serializationType = SerializationType.LZ4_TYPES; // Change it to have other serialization tests
        TestType testType = TestType.SHORT; // Change it to have other test types (there is only short and long here)

        System.out.println("\n[WALLY] ########## TESTING TYPE: " + testType);
        switch (testType) {
            case SHORT:
                serializationType.forEach(serialType -> test.testSerializationShort(myFile, serialType));
                break;
            case LONG:
                serializationType.forEach(serialType -> test.testSerializationLong(myFile, serialType));
                break;
            default:
                System.out.println(WARNING_NOT_A_KNOWN_TYPE);
                break;
        }

        System.out.println("\n[WALLY] >>>>>>>>>>>>>>> FINISHED COMPRESSION TESTS <<<<<<<<<<<<<<<");
        testUtils.printBenchmarks(benchmarksCompress, "COMPRESS");

        System.out.println("\n[WALLY] >>>>>>>>>>>>>>> FINISHED DECOMPRESSION TESTS <<<<<<<<<<<<<<<");
        testUtils.printBenchmarks(benchmarksUncompress, "DECOMPRESS");
    }

    private void setOriginalSerialFileSizeBytes(int originalSerialFileSizeBytes) {
        this.originalSerialFileSizeBytes = originalSerialFileSizeBytes;
    }

    /**
     * Test simplified symmetric combination for buffers: KB-KB, MB-MB
     *
     * @param myFile
     * @param type
     */
    public void testSerializationShort(MyFile myFile, SerializationType type) {
        System.out.println(STARTING_TEST + type);
        testSerializationShort(myFile, type, ByteSizeType.BYTE_SIZE_KB);
        testSerializationShort(myFile, type, ByteSizeType.BYTE_SIZE_MB);
    }

    /**
     * Test long for all combinations: KB x MB buffers
     *
     * @param myFile
     * @param type
     */
    public void testSerializationLong(MyFile myFile, SerializationType type) {
        System.out.println(STARTING_TEST + type);
        testSerializationLong(myFile, type, ByteSizeType.BYTE_SIZE_KB);
        testSerializationLong(myFile, type, ByteSizeType.BYTE_SIZE_MB);
    }

    public void testSerializationShort(MyFile myFile, SerializationType type, ByteSizeType byteSizeType) {
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

    /**
     * All combinations of buffer size for byteArrayOutputStream and gzipOutputStream, made by 2 for loops.
     *
     * @param myFile
     * @param type
     * @param byteSizeType
     */
    public void testSerializationLong(MyFile myFile, SerializationType type, ByteSizeType byteSizeType) {

        for (int i = 1; i <= 16; i++) {
            int buffer = i;

            Measure measure = new MeasureBuilder().type(type)
                    .singleBufferValue(buffer)
                    .singleBufferSizeType(byteSizeType)
                    .originalSerialFileSizeBytes(originalSerialFileSizeBytes)
                    .createMeasure();

            testSerializationByType(myFile, measure);
        }
    }

    public void testSerializationByType(MyFile myFile, Measure measure) {
        byte[] myFileBytes = SerializationUtils.serialize(myFile);
        compressDecompress(myFile, measure, myFileBytes);
    }

    private void compressDecompress(MyFile myFile, Measure measure, byte[] originalFileBytes) {

        try {
            System.out.println("\n[WALLY] ########## Test compress and decompress: " + measure.fileDescription());

            compressAndSerializeToFile(myFile, measure);

            MyFile outputFile = UncompressAndDeserializeFromFile(measure);

            byte[] outputFileBytes = SerializationUtils.serialize(outputFile);

            boolean lenghtComparison = (originalFileBytes.length == outputFileBytes.length);

            System.out.println("Byte array length comparison: " + lenghtComparison);
            System.out.println("Byte array comparison: " + Arrays.equals(originalFileBytes, outputFileBytes));

            if (!Arrays.equals(originalFileBytes, outputFileBytes)) { // LOG assertion
                System.out.println(CAUGHT_ERROR_PROCESSING + measure.getType() + " : Byte arrays in and out should be equal !!!");
            }
        } catch (Exception ex) {
            System.out.println(CAUGHT_ERROR_PROCESSING + measure.getType() + " compressDecompress : " + ex);
        }
    }

    private static void compressAndSerializeToFile(MyFile myFile, Measure measure) throws IOException {
        long start = System.nanoTime();
        File fileWrite = new File(TEST_PATH + "File_" + measure.fileDescription() + ".obj");
        System.out.println("Writing .obj file to path: " + fileWrite.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(fileWrite);
        BufferedOutputStream bufferedFos = new BufferedOutputStream(fos, measure.getBufferCompressCalculated());
        OutputStream os = instantiateOutputStreamByType(measure, bufferedFos);
        serializeFile(myFile, measure, os);
        measure.setBytesCount(Files.size(fileWrite.toPath()));
        logDuration(start, measure, benchmarksCompress);
    }

    private static MyFile UncompressAndDeserializeFromFile(Measure measure) throws IOException, ClassNotFoundException {
        long start = System.nanoTime();
        File fileRead = new File(TEST_PATH + "File_" + measure.fileDescription() + ".obj");
        long bytes = Files.size(fileRead.toPath());
        System.out.println("Reading .obj file: " + bytes + " bytes (" + FileUtils.byteCountToDisplaySize(bytes) + ") from path: " + fileRead.getAbsolutePath());
        FileInputStream fis = new FileInputStream(fileRead);
        BufferedInputStream bufferedFis = new BufferedInputStream(fis, measure.getBufferCompressCalculated());
        InputStream inStream = instantiateInputStreamByType(measure, bufferedFis);
        measure.setBytesCount(bytes);
        MyFile outputFile = deserializeFile(inStream);
        logDuration(start, measure, benchmarksUncompress);
        return outputFile;
    }
}
