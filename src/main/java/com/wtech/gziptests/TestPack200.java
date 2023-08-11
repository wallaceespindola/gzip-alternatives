package com.wtech.gziptests;

import org.apache.commons.compress.compressors.pack200.Pack200CompressorInputStream;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.wtech.gziptests.SerializationTestUtils.TEST_PATH;
import static org.assertj.core.api.Assertions.assertThat;

public class TestPack200 {

    public static void main(String[] args) throws Exception {

        System.out.println("Test Pack200 compression and decompression...");
        byte[] data = "1234567890".getBytes(StandardCharsets.UTF_8);
        final int decompressedLength = data.length;

        SerializationTestUtils.createTestDir();

        File fileWrite = new File(TEST_PATH + "test.p200");
        System.out.println("Writing file test.p200 on: " + fileWrite.getAbsolutePath());
        Pack200CompressorOutputStream outStream = new Pack200CompressorOutputStream(new FileOutputStream(fileWrite));
        outStream.write(data);
        outStream.close();
        System.out.println("Outstream closed.");

        byte[] restored = new byte[decompressedLength];
        File fileRead = new File(TEST_PATH + "test.p200");
        System.out.println("Reading file test.p200 on: " + fileRead.getAbsolutePath());
        Pack200CompressorInputStream inStream = new Pack200CompressorInputStream(new FileInputStream(fileRead));
        inStream.read(restored);
        inStream.close();
        System.out.println("Instream closed.");

        System.out.println("Byte array comparison: " + Arrays.equals(data, restored));
        assertThat(Arrays.equals(data, restored)).as("Byte array comparison should be true").isTrue();
    }
}
