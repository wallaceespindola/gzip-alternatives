package com.wtech.gziptests;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class TestZip4J {

    public static void main(String[] args) throws Exception {

        System.out.println("########## Test Zip4J zip and unzip ##########.");
        testZip(CompressionMethod.STORE);
        testZip(CompressionMethod.DEFLATE);
    }

    private static void testZip(CompressionMethod compressMethod) throws IOException {

        System.out.println("\n########## Testing: " + compressMethod);

        byte[] data = ("test 1234567890 abcdefghijk " + compressMethod).getBytes(StandardCharsets.UTF_8);

        File fileOrig = new File("Zip4J-" + compressMethod + ".txt");
        System.out.println("Writing test file on: " + fileOrig.getAbsolutePath());
        OutputStream outStream = new FileOutputStream(fileOrig);
        outStream.write(data);
        outStream.close();
        System.out.println("Outstream closed. File created.");

        ZipParameters zipParams = new ZipParameters();
        zipParams.setCompressionMethod(compressMethod);

        System.out.println("Zipping...");
        ZipFile zip = new ZipFile("Zip4J-" + compressMethod + ".zip");
        zip.addFile(fileOrig.getPath(), zipParams);
        System.out.println("File " + compressMethod + " zipped.");

        renameFileTo(fileOrig, compressMethod + "-orig-file");

        System.out.println("Unzipping...");
        String destPath = fileOrig.getPath();
        zip.extractAll(destPath);
        System.out.println("File Zip4J " + compressMethod + " unzipped to: " + destPath);
    }

    private static void renameFileTo(File fileOrig, String name) {
        String nameDest = "Zip4J-" + name + ".txt";
        File fileDest = new File(nameDest);
        fileOrig.renameTo(fileDest);
        System.out.println("renamed from " + fileOrig.getName() + " to " + nameDest + ".");
    }
}
