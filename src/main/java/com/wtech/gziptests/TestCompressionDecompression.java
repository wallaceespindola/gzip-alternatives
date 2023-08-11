package com.wtech.gziptests;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.wtech.gziptests.SerializationTestUtils.*;

public class TestCompressionDecompression {

    public static void main(String[] args) throws Exception {

        TestCompressionDecompression test = new TestCompressionDecompression();

        // Change value of SerializationType to have other serialization tests
        SerializationType.ALL_TYPES.forEach(test::compressDecompress);
    }

    private void compressDecompress(SerializationType serialType) {

        try {
            System.out.println("\n[WALLY] ########## Test compress and decompress: " + serialType);
            byte[] data = "1234567890".getBytes(StandardCharsets.UTF_8);
            final int decompressedLength = data.length;

            serializeToFileAndCompress(serialType, data);

            byte[] restored = new byte[decompressedLength];

            deserializeFromFileAndUncompress(serialType, restored);

            System.out.println("Byte array comparison: " + Arrays.equals(data, restored));

            if (!Arrays.equals(data, restored)) { // LOG assertion
                System.out.println(CAUGHT_ERROR_PROCESSING + serialType + " Byte arrays in and out should be equal !!!");
            }
        } catch (IOException ex) {
            System.out.println(CAUGHT_ERROR_PROCESSING + serialType + " compressDecompress : " + ex);
        }
    }

    private static void deserializeFromFileAndUncompress(SerializationType serialType, byte[] restored) throws IOException {
        File fileRead = new File(serialType.name() + ".obj");
        System.out.println("Reading file test.obj on: " + fileRead.getAbsolutePath());
        InputStream inStream = instantiateInputStreamByType(serialType, new FileInputStream(fileRead));
        inStream.read(restored);
        inStream.close();
        System.out.println("Instream closed.");
    }

    private static void serializeToFileAndCompress(SerializationType serialType, byte[] data) throws IOException {
        File fileWrite = new File(serialType.name() + ".obj");
        System.out.println("Writing file test.obj on: " + fileWrite.getAbsolutePath());
        OutputStream outStream = instantiateOutputStreamByType(serialType, new FileOutputStream(fileWrite));
        outStream.write(data);
        outStream.close();
        System.out.println("Outstream closed.");
    }
}
