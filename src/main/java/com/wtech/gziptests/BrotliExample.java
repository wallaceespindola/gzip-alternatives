package com.wtech.gziptests;

import com.aayushatharva.brotli4j.encoder.BrotliOutputStream;
import org.apache.commons.compress.compressors.brotli.BrotliCompressorInputStream;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BrotliExample {
    public static void main(String[] args) throws IOException {
        // Brotli
        try (FileInputStream in = new FileInputStream("input.txt");
             FileOutputStream out = new FileOutputStream("output.br");
             BrotliOutputStream brotliOut = new BrotliOutputStream(out)) { // Brotli4J
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                brotliOut.write(buffer, 0, len);
            }
        }

        // Brotli
        try (FileInputStream in = new FileInputStream("output.br");
             FileOutputStream out = new FileOutputStream("output.txt");
             BrotliCompressorInputStream brotliIn = new BrotliCompressorInputStream(in)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = brotliIn.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        }
    }
}
