package com.wtech.gziptests;

import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.decoder.BrotliInputStream;
import com.aayushatharva.brotli4j.encoder.BrotliOutputStream;
import com.aayushatharva.brotli4j.encoder.Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class TestBrotli {

    public static void main(String[] args) throws Exception {

        // Load the native library
        Brotli4jLoader.ensureAvailability();
        // If being used to compress streams in real-time, I do not advise a quality setting above 4 due to performance
        Encoder.Parameters bp = new Encoder.Parameters().setQuality(4);

        System.out.println("Test Brotli compression and decompression...");
        byte[] data = "1234567890".getBytes(StandardCharsets.UTF_8);
        final int decompressedLength = data.length;

        File fileWrite = new File("test.btl");
        System.out.println("Writing file test.btl on: " + fileWrite.getAbsolutePath());
        BrotliOutputStream outStream = new BrotliOutputStream(new FileOutputStream(fileWrite), bp);
        outStream.write(data);
        outStream.close();
        System.out.println("Outstream closed.");

        byte[] restored = new byte[decompressedLength];
        File fileRead = new File("test.btl");
        System.out.println("Reading file test.btl on: " + fileRead.getAbsolutePath());
        BrotliInputStream inStream = new BrotliInputStream(new FileInputStream(fileRead));
        inStream.read(restored);
        inStream.close();
        System.out.println("Instream closed.");

        System.out.println("Byte array comparison: " + Arrays.equals(data, restored));
        assertThat(Arrays.equals(data, restored)).as("Byte array comparison should be true").isTrue();
    }
}
