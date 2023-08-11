package com.wtech.gziptests;

import net.jpountz.lz4.LZ4FrameInputStream;
import net.jpountz.lz4.LZ4FrameOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.wtech.gziptests.SerializationTestUtils.TEST_PATH;
import static com.wtech.gziptests.SerializationTestUtils.logDuration;
import static org.assertj.core.api.Assertions.assertThat;

public class TestLz4 {

    public static void main(String[] args) throws Exception {

        System.out.println("[WALLY] Test LZ4 compression and decompression...");
        byte[] data = "1234567890".getBytes(StandardCharsets.UTF_8);
        final int decompressedLength = data.length;

        SerializationTestUtils.createTestDir();

        long start = System.nanoTime();
        File fileWrite = new File(TEST_PATH + "test.lz4");
        System.out.println("Writing test file on: " + fileWrite.getAbsolutePath());
        LZ4FrameOutputStream outStream = new LZ4FrameOutputStream(new FileOutputStream(fileWrite));
        outStream.write(data);
        outStream.close();
        System.out.println("Outstream closed.");
        logDuration(start, "compress and serialize with LZ4 Frame");

        start = System.nanoTime();
        byte[] restored = new byte[decompressedLength];
        File fileRead = new File(TEST_PATH + "test.lz4");
        System.out.println("Reading test file on: " + fileRead.getAbsolutePath());
        LZ4FrameInputStream inStream = new LZ4FrameInputStream(new FileInputStream(fileRead));
        inStream.read(restored);
        inStream.close();
        System.out.println("Instream closed.");
        logDuration(start, "uncompress and deserialize with LZ4 Frame");

        System.out.println("Byte array comparison: " + Arrays.equals(data, restored));
        assertThat(Arrays.equals(data, restored)).as("Byte array comparison should be true").isTrue();
    }
}
