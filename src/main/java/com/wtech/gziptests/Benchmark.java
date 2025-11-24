package com.wtech.gziptests;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class Benchmark {

    /**
     * Size of primitive array
     */
    static final int SIZE = 100_000_000;

    public static void main(String[] args) throws Exception {
        System.out.println("[WALLY] ########## TEST SERIALIZATION 1: ##########");
        test1();
        System.out.println("[WALLY] ########## TEST SERIALIZATION 2: ##########");
        test2();
    }

    public static void test1() throws Exception {
        CountingOutputStream count;
        long start, elapsed;

        Object obj = new double[SIZE];

        count = new CountingOutputStream();
        start = System.nanoTime();
        try (ObjectOutputStream oos = new ObjectOutputStream(count)) {
            oos.writeObject(obj);
        }
        elapsed = System.nanoTime() - start;
        System.out.println("STD " + count.count +
                " bytes (" + FileUtils.byteCountToDisplaySize(count.count) + ") written in " + (elapsed / 1000000L)
                + "ms");
    }

    public static void test2() throws Exception {

        CountingOutputStream count;
        long start, elapsed;

        double[] obj = new double[10_000];
        ByteArrayOutputStream baos = new ByteArrayOutputStream(4 * obj.length);

        start = System.nanoTime();
        for (int n = 0; n < 10000; n++) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(obj);
            }
            // baos.reset();
        }
        elapsed = System.nanoTime() - start;
        // System.out.println("STD :" + (elapsed / 1000000L) + "ms");
        System.out.println("STD " + baos.size() +
                " bytes (" + FileUtils.byteCountToDisplaySize(baos.size()) + ") written in " + (elapsed / 1000000L)
                + "ms");
    }

}
