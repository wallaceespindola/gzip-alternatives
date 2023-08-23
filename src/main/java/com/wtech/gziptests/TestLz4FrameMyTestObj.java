package com.wtech.gziptests;

import org.apache.commons.lang3.SerializationUtils;

public class TestLz4FrameMyTestObj {

    public static void main(String[] args) throws Exception {

        System.out.println("[WALLY] Test LZ4 Frame compression and decompression...");

        SerializationTestUtils utils = new SerializationTestUtils();
        MyTestObject myTestObj = utils.saveAndRetrieveMyTestObj();

        byte[] data = SerializationUtils.serialize(myTestObj);
        final int decompressedLength = data.length;

        Measure measure = new MeasureBuilder().type(SerializationType.LZ4_ORG_FRAME)
                .singleBufferValue(1)
                .singleBufferSizeType(ByteSizeType.BYTE_SIZE_KB)
                .originalSerialFileSizeBytes(decompressedLength)
                .createMeasure();

        utils.testSerializationByType(myTestObj, measure);
    }
}
