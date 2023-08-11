package com.wtech.gziptests;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.ObjectOutputStream;

import static com.wtech.gziptests.SerializationTestUtils.*;

public class TestBasics {

    public static void main(String[] args) {

        SerializationTestUtils testUtils = new SerializationTestUtils();
        MyFile myFile = testUtils.saveAndRetrieveMyFile();

        long start;
        String description = "STD Standard";
        System.out.println(SERIALIZATION_HEADER + description);
        CountingOutputStream countStream = new CountingOutputStream();
        start = System.nanoTime();

        try (ObjectOutputStream oos = new ObjectOutputStream(countStream)) {
            oos.writeObject(myFile);
        } catch (IOException pE) {
            System.out.println(CAUGHT_ERROR_PROCESSING + "serialization " + description + " : " + pE);
        }
        logDuration(start, SERIALIZATION + description, countStream.count);

        description = "Apache SerializationUtils";
        System.out.println(SERIALIZATION_HEADER + description);
        start = System.nanoTime();
        byte[] objectBytes = SerializationUtils.serialize(myFile);
        logDuration(start, SERIALIZATION + description, objectBytes.length);
    }

    private static void logDuration(long start, String description, long bytesCount) {
        long elapsed = System.nanoTime() - start;
        System.out.println("[WALLY] " + description +
                " - " + bytesCount + " bytes (" + FileUtils.byteCountToDisplaySize(bytesCount) + ")" +
                " - executed in " + (elapsed / 1000000L) + " ms");
    }
}
