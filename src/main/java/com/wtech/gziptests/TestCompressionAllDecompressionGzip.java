package com.wtech.gziptests;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

public class TestCompressionAllDecompressionGzip {

    public static void main(String[] args) throws Exception {

        TestCompressionAllDecompressionGzip test = new TestCompressionAllDecompressionGzip();

        // Change value of SerializationType to have other serialization tests
        SerializationType.ALL_TYPES.forEach(test::compressAllDecompressGzip);
    }

    private void compressAllDecompressGzip(SerializationType serialType) {

        try {
            System.out.println("\n########## Test compress and decompress: " + serialType);
            byte[] data = "1234567890".getBytes(StandardCharsets.UTF_8);
            final int decompressedLength = data.length;

            serializeToFile(serialType, data);

            byte[] restored = new byte[decompressedLength];
            deserializeFromFileUncompressingWithGzip(serialType, restored);

            System.out.println("Byte array comparison: " + Arrays.equals(data, restored));

            if (!Arrays.equals(data, restored)) { // LOG assertion
                System.out.println(SerializationTestUtils.CAUGHT_ERROR_PROCESSING + serialType + " Byte arrays in and out should be equal !!!");
            }
        } catch (IOException ex) {
            System.out.println(SerializationTestUtils.CAUGHT_ERROR_PROCESSING + serialType + " compressDecompress : " + ex);
        }
    }

    private static void deserializeFromFileUncompressingWithGzip(SerializationType serialType, byte[] restored) throws IOException {
        File fileRead = new File(serialType.name() + ".obj");
        System.out.println("Reading file test.obj on: " + fileRead.getAbsolutePath());
        InputStream inStream = new GZIPInputStream(new FileInputStream(fileRead));
        inStream.read(restored);
        inStream.close();
        System.out.println("Instream closed.");
    }

    private static void serializeToFile(SerializationType serialType, byte[] data) throws IOException {
        File fileWrite = new File(serialType.name() + ".obj");
        System.out.println("Writing file test.obj on: " + fileWrite.getAbsolutePath());
        TestCompressionAllDecompressionGzip test = new TestCompressionAllDecompressionGzip();
        OutputStream outStream = SerializationTestUtils.instantiateOutputStreamByType(serialType, new FileOutputStream(fileWrite));
        outStream.write(data);
        outStream.close();
        System.out.println("Outstream closed.");
    }
}
