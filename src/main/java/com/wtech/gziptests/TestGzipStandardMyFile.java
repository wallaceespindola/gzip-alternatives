package com.wtech.gziptests;

import org.apache.commons.lang3.SerializationUtils;

public class TestGzipStandardMyFile {

    public static void main(String[] args) throws Exception {

        System.out.println("[WALLY] Test Gzip Standard compression and decompression...");

        SerializationTestUtils utils = new SerializationTestUtils();
        MyFile myfile = utils.saveAndRetrieveMyFile();

        byte[] data = SerializationUtils.serialize(myfile);
        final int decompressedLength = data.length;

        Measure measure = new MeasureBuilder().type(SerializationType.GZIP_STANDARD)
                .singleBufferValue(1)
                .singleBufferSizeType(ByteSizeType.BYTE_SIZE_KB)
                .originalSerialFileSizeBytes(decompressedLength)
                .createMeasure();

        utils.testSerializationByType(myfile, measure);
    }
}
