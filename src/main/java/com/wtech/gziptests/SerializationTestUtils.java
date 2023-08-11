package com.wtech.gziptests;

import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.decoder.BrotliInputStream;
import com.aayushatharva.brotli4j.encoder.BrotliOutputStream;
import com.aayushatharva.brotli4j.encoder.Encoder;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4FrameInputStream;
import net.jpountz.lz4.LZ4FrameOutputStream;
import org.anarres.parallelgzip.ParallelGZIPOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorInputStream;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SerializationTestUtils {

    public static final String BYTE_ARRAY_BUFFER = "ByteArray_Buffer (";
    public static final String COMPRESS_BUFFER = "Compress_Buffer (";
    public static final int KB_SIZE = 1024;
    public static final String CAUGHT_ERROR_PROCESSING = "[WALLY] CAUGHT AN ERROR processing ";
    public static final String SERIALIZATION_HEADER = "\n[WALLY] ########## SERIALIZATION ";
    public static final String WARNING_NOT_A_KNOWN_TYPE = "WARNING: NOT A KNOWN TYPE";
    public static final String STARTING_TEST = "\n[WALLY] ########## STARTING TEST: ";
    public static final String KB = " KB)";
    public static final String MB = " MB)";
    public static final int MB_SIZE = 1024 * 1024;
    public static final String SERIALIZATION = "Serialization: ";
    public static final String TEST_PATH = "./testResults/";
    private int originalSerialFileSizeBytes;

    public static String getKbyteOrMegabyteString(ByteSizeType byteSizeType) {
        return byteSizeType == ByteSizeType.BYTE_SIZE_KB ? KB : MB;
    }

    public int getOriginalSerialFileSizeBytes() {
        return originalSerialFileSizeBytes;
    }

    public static int calculateKbyteOrMegabyteBufferSize(ByteSizeType byteSizeType, int bufferSize) {
        return byteSizeType == ByteSizeType.NONE ? 0 :
                (byteSizeType == ByteSizeType.BYTE_SIZE_KB ? bufferSize * KB_SIZE : bufferSize * MB_SIZE);
    }

    public MyFile saveAndRetrieveMyFile() {
        System.out.println("[WALLY] START saveAndRetrieveMyFile");

        // directory from where the program was launched
        String dir = System.getProperty("user.dir");
        System.out.println("My working directory : " + dir);

        createTestDir();

        String fileName = "test-file-20mb.txt";
        //String fileName = "test-file-50mb.txt";
        String filePath = "./src/main/resources/";
        String completeFilePath = filePath + fileName;

        File testFile = new File(completeFilePath);
        System.out.println(">>>>> Test file " + (testFile.exists() ? "exists: " : "missing: ") + testFile.getAbsolutePath());

        MyFile myFile = new MyFile(getTextFromFilePath(testFile.getName()));

        try {
            if (testFile.exists()) {
                // Serialize object in disk
                serializeMyFile(completeFilePath, myFile);
            } else {
                System.out.println(CAUGHT_ERROR_PROCESSING + "test file from disk : FILE NOT FOUND");
            }
            System.out.println(">>>>> AFTER saveAndRetrieveMyFile : " + myFile);
        } catch (Exception ex) {
            System.out.println(CAUGHT_ERROR_PROCESSING + "saveAndRetrieveMyFile : " + ex);
        }
        System.out.println("[WALLY] END saveAndRetrieveMyFile");

        return myFile;
    }

    private void serializeMyFile(String completeFilePath, MyFile myFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(completeFilePath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(myFile);
        oos.flush();
        oos.close();
        byte[] objectBytes = SerializationUtils.serialize(myFile);
        originalSerialFileSizeBytes = objectBytes.length;
        System.out.println(">>>>> MyFile object serialized : " + originalSerialFileSizeBytes + " bytes ("
                + FileUtils.byteCountToDisplaySize(originalSerialFileSizeBytes) + ") to path: " + completeFilePath);
    }

    public static void createTestDir() {
        File testDir = new File(TEST_PATH);
        testDir.mkdir();
        try {
            testDir.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    String getTextFromFilePath(String fileName) {
        StringBuilder txt = new StringBuilder();
        InputStream res = getClass().getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(res))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                txt.append(sCurrentLine + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return txt.toString();
    }

    public void printBenchmarks(Map<Long, Measure> benchmarks, String... optionalTitle) {
        System.out.println("\n[WALLY] ###################### PRINT BENCHMARKS:" + addOptionalTitle(optionalTitle) + " ######################");

        for (SerializationType type : SerializationType.values()) {
            if (benchmarks.values().stream().anyMatch(measure -> measure.getType().equals(type))) {
                System.out.println("\n[WALLY] ========== TOP 3 RESULTS " + type + ":" + addOptionalTitle(optionalTitle));
            }
            benchmarks.values().stream().filter(measure -> measure.getType().equals(type)).limit(3)
                    .forEach(measure -> System.out.println(measure.description()));
        }

        for (SerializationType type : SerializationType.values()) {
            if (benchmarks.values().stream()
                    .anyMatch(measure -> measure.getType().equals(type))) {
                System.out.println("\n[WALLY] >>>>>>>>>> TOP RESULT " + type + ":" + addOptionalTitle(optionalTitle));
            }
            benchmarks.values().stream()
                    .filter(measure -> measure.getType().equals(type)).limit(1)
                    .forEach(measure -> System.out.println(measure.description()));
        }

        System.out.println("\n[WALLY] %%%%%%%%%% CSV RESULTS:" + addOptionalTitle(optionalTitle));
        System.out.println("Duration; Original MB; Actual MB; Original Bytes; Actual Bytes; Compression; Type;");
        Map<Long, Measure> benchmarksCsv = new TreeMap<>();
        for (SerializationType type : SerializationType.values()) {
            benchmarks.values().stream()
                    .sorted(Comparator.comparingLong(Measure::getElapsed)) // Sorted by duration
                    .filter(measure -> measure.getType().equals(type))
                    .limit(1)
                    .forEach(measure -> benchmarksCsv.put(measure.getElapsed(), measure));
        }
        // Not necessary to sort for TreeMap: .sorted(Comparator.comparingLong(Measure::getElapsed))
        benchmarksCsv.values().forEach(measure -> System.out.println(measure.csvDescription()));
    }

    private static String addOptionalTitle(String... optionalTitle) {
        return (optionalTitle.length > 0 && StringUtils.isNotBlank(optionalTitle[0])) ? " " + optionalTitle[0] : "";
    }

    public static class CountingOutputStream extends OutputStream {
        public long count = 0;

        @Override
        public void write(int b) {
            count++;
        }
    }

    public static OutputStream instantiateOutputStreamByType(SerializationType serialType, FileOutputStream fos) {
        try {
            switch (serialType) {
                case GZIP_STANDARD:
                    return new GZIPOutputStream(fos, KB_SIZE);
                case GZIP_PARALLEL_SHEVEK:
                    return new MyParallelGZIPOutputStream(fos);
                case GZIP_PARALLEL:
                    return new ParallelGZIPOutputStream(fos);
                case GZIP_COMPRESSOR_AC:
                    GzipParameters gp = new GzipParameters();
                    gp.setBufferSize(KB_SIZE);
                    return new GzipCompressorOutputStream(fos, gp);
                case BZIP2_COMPRESSOR:
                    return new BZip2CompressorOutputStream(fos);
                case SNAPPY_COMPRESSOR:
                    return new SnappyCompressorOutputStream(fos, "1234567890".getBytes(StandardCharsets.UTF_8).length);
                case DEFLATE_COMPRESSOR:
                    return new DeflateCompressorOutputStream(fos);
                case XZ_COMPRESSOR:
                    return new XZCompressorOutputStream(fos);
                case LZMA_COMPRESSOR:
                    return new LZMACompressorOutputStream(fos);
                case ZSTD_COMPRESSOR:
                    return new ZstdCompressorOutputStream(fos);
                case PACK_200_COMPRESSOR:
                    return new Pack200CompressorOutputStream(fos);
                case FRAMED_SNAPPY_COMPRESSOR:
                    return new FramedSnappyCompressorOutputStream(fos);
                case BROTLI_COMPRESSOR_BROTLI4J_Q2:
                    return getBrotli4jOutputStream(fos, 2);
                case BROTLI_COMPRESSOR_BROTLI4J_Q4:
                    return getBrotli4jOutputStream(fos, 4);
                case BROTLI_COMPRESSOR_BROTLI4J_Q6:
                    return getBrotli4jOutputStream(fos, 6);
                case BROTLI_COMPRESSOR_BROTLI4J_Q8:
                    return getBrotli4jOutputStream(fos, 8);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q2:
                    return getJvmBrotliOutputStream(fos, 2);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q4:
                    return getJvmBrotliOutputStream(fos, 4);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q6:
                    return getJvmBrotliOutputStream(fos, 6);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q8:
                    return getJvmBrotliOutputStream(fos, 8);
                case LZ4_COMPRESSOR_BLOCK:
                    return new BlockLZ4CompressorOutputStream(fos);
                case LZ4_COMPRESSOR_FRAMED:
                    return new FramedLZ4CompressorOutputStream(fos);
                case LZ4_ORG_BLOCK:
                    return new LZ4BlockOutputStream(fos);
                case LZ4_ORG_FRAME:
                    return new LZ4FrameOutputStream(fos);
                case BYTE_ARRAY:
                    // No compression output stream added
                    return fos;
            }
        } catch (IOException ex) {
            System.out.println(SerializationTestUtils.CAUGHT_ERROR_PROCESSING + serialType + " instantiateOutputStreamByType : " + ex);
        }
        return null;
    }

    public static OutputStream instantiateOutputStreamByType(Measure measure, BufferedOutputStream fos) {
        try {
            switch (measure.getType()) {
                case GZIP_STANDARD:
                    return new GZIPOutputStream(fos, measure.getBufferCompressCalculated());
                case GZIP_PARALLEL_SHEVEK:
                    return new MyParallelGZIPOutputStream(fos);
                case GZIP_PARALLEL:
                    return new ParallelGZIPOutputStream(fos);
                case GZIP_COMPRESSOR_AC:
                    GzipParameters gp = new GzipParameters();
                    gp.setBufferSize(measure.getBufferCompressCalculated());
                    return new GzipCompressorOutputStream(fos, gp);
                case BZIP2_COMPRESSOR:
                    return new BZip2CompressorOutputStream(fos);
                case SNAPPY_COMPRESSOR:
                    return new SnappyCompressorOutputStream(fos, measure.getOriginalSerialFileSizeBytes());
                case DEFLATE_COMPRESSOR:
                    return new DeflateCompressorOutputStream(fos);
                case XZ_COMPRESSOR:
                    return new XZCompressorOutputStream(fos);
                case LZMA_COMPRESSOR:
                    return new LZMACompressorOutputStream(fos);
                case ZSTD_COMPRESSOR:
                    return new ZstdCompressorOutputStream(fos);
                case PACK_200_COMPRESSOR:
                    return new Pack200CompressorOutputStream(fos);
                case FRAMED_SNAPPY_COMPRESSOR:
                    return new FramedSnappyCompressorOutputStream(fos);
                case BROTLI_COMPRESSOR_BROTLI4J_Q2:
                    return getBrotli4jOutputStream(measure, fos, 2);
                case BROTLI_COMPRESSOR_BROTLI4J_Q4:
                    return getBrotli4jOutputStream(measure, fos, 4);
                case BROTLI_COMPRESSOR_BROTLI4J_Q6:
                    return getBrotli4jOutputStream(measure, fos, 6);
                case BROTLI_COMPRESSOR_BROTLI4J_Q8:
                    return getBrotli4jOutputStream(measure, fos, 8);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q2:
                    return getJvmBrotliOutputStream(measure, fos, 2);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q4:
                    return getJvmBrotliOutputStream(measure, fos, 4);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q6:
                    return getJvmBrotliOutputStream(measure, fos, 6);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q8:
                    return getJvmBrotliOutputStream(measure, fos, 8);
                case LZ4_COMPRESSOR_BLOCK:
                    return new BlockLZ4CompressorOutputStream(fos);
                case LZ4_COMPRESSOR_FRAMED:
                    return new FramedLZ4CompressorOutputStream(fos);
                case LZ4_ORG_BLOCK:
                    return new LZ4BlockOutputStream(fos);
                case LZ4_ORG_FRAME:
                    return new LZ4FrameOutputStream(fos);
                case BYTE_ARRAY:
                    // No compression output stream added, but need to adapt measure object to make sure it is consistent
                    measure.cleanBufferCompress();
                    measure.cleanBufferCompressSizeType();
                    return fos;
            }
        } catch (IOException ex) {
            System.out.println(SerializationTestUtils.CAUGHT_ERROR_PROCESSING + measure.getType() + " instantiateOutputStreamByType : " + ex);
        }
        return null;
    }

    public static InputStream instantiateInputStreamByType(SerializationType serialType, FileInputStream fis) {
        try {
            switch (serialType) {
                case GZIP_STANDARD:
                    return new GZIPInputStream(fis, KB_SIZE);
                case GZIP_PARALLEL_SHEVEK:
                    return new GZIPInputStream(fis); // can be used here normally
                case GZIP_PARALLEL:
                    return new ParallelGZIPInputStream(fis);
                case GZIP_COMPRESSOR_AC:
                    GzipParameters gp = new GzipParameters();
                    return new GzipCompressorInputStream(fis);
                case BZIP2_COMPRESSOR:
                    return new BZip2CompressorInputStream(fis);
                case SNAPPY_COMPRESSOR:
                    return new SnappyCompressorInputStream(fis, "1234567890".getBytes(StandardCharsets.UTF_8).length);
                case DEFLATE_COMPRESSOR:
                    return new DeflateCompressorInputStream(fis);
                case XZ_COMPRESSOR:
                    return new XZCompressorInputStream(fis);
                case LZMA_COMPRESSOR:
                    return new LZMACompressorInputStream(fis);
                case ZSTD_COMPRESSOR:
                    return new ZstdCompressorInputStream(fis);
                case PACK_200_COMPRESSOR:
                    return new Pack200CompressorInputStream(fis);
                case FRAMED_SNAPPY_COMPRESSOR:
                    return new FramedSnappyCompressorInputStream(fis);
                case BROTLI_COMPRESSOR_BROTLI4J_Q2:
                    return getBrotli4jInputStream(fis, 2);
                case BROTLI_COMPRESSOR_BROTLI4J_Q4:
                    return getBrotli4jInputStream(fis, 4);
                case BROTLI_COMPRESSOR_BROTLI4J_Q6:
                    return getBrotli4jInputStream(fis, 6);
                case BROTLI_COMPRESSOR_BROTLI4J_Q8:
                    return getBrotli4jInputStream(fis, 8);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q2:
                    return getJvmBrotliInputStream(fis, 2);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q4:
                    return getJvmBrotliInputStream(fis, 4);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q6:
                    return getJvmBrotliInputStream(fis, 6);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q8:
                    return getJvmBrotliInputStream(fis, 8);
                case LZ4_COMPRESSOR_BLOCK:
                    return new BlockLZ4CompressorInputStream(fis);
                case LZ4_COMPRESSOR_FRAMED:
                    return new FramedLZ4CompressorInputStream(fis);
                case LZ4_ORG_BLOCK:
                    return new LZ4BlockInputStream(fis);
                case LZ4_ORG_FRAME:
                    return new LZ4FrameInputStream(fis);
                case BYTE_ARRAY:
                    // No input stream added
                    return fis;
            }
        } catch (IOException ex) {
            System.out.println(SerializationTestUtils.CAUGHT_ERROR_PROCESSING + serialType + " instantiateInputStreamByType : " + ex);
        }
        return null;
    }

    public static InputStream instantiateInputStreamByType(Measure measure, BufferedInputStream bis) {
        try {
            switch (measure.getType()) {
                case GZIP_STANDARD:
                    return new GZIPInputStream(bis, measure.getBufferCompressCalculated());
                case GZIP_PARALLEL_SHEVEK:
                    return new GZIPInputStream(bis); // can be used here normally
                case GZIP_PARALLEL:
                    return new ParallelGZIPInputStream(bis);
                case GZIP_COMPRESSOR_AC:
                    GzipParameters gp = new GzipParameters();
                    return new GzipCompressorInputStream(bis);
                case BZIP2_COMPRESSOR:
                    return new BZip2CompressorInputStream(bis);
                case SNAPPY_COMPRESSOR:
                    return new SnappyCompressorInputStream(bis, measure.getOriginalSerialFileSizeBytes());
                case DEFLATE_COMPRESSOR:
                    return new DeflateCompressorInputStream(bis);
                case XZ_COMPRESSOR:
                    return new XZCompressorInputStream(bis);
                case LZMA_COMPRESSOR:
                    return new LZMACompressorInputStream(bis);
                case ZSTD_COMPRESSOR:
                    return new ZstdCompressorInputStream(bis);
                case PACK_200_COMPRESSOR:
                    return new Pack200CompressorInputStream(bis);
                case FRAMED_SNAPPY_COMPRESSOR:
                    return new FramedSnappyCompressorInputStream(bis);
                case BROTLI_COMPRESSOR_BROTLI4J_Q2:
                    return getBrotli4jInputStream(measure, bis, 2);
                case BROTLI_COMPRESSOR_BROTLI4J_Q4:
                    return getBrotli4jInputStream(measure, bis, 4);
                case BROTLI_COMPRESSOR_BROTLI4J_Q6:
                    return getBrotli4jInputStream(measure, bis, 6);
                case BROTLI_COMPRESSOR_BROTLI4J_Q8:
                    return getBrotli4jInputStream(measure, bis, 8);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q2:
                    return getJvmBrotliInputStream(measure, bis, 2);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q4:
                    return getJvmBrotliInputStream(measure, bis, 4);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q6:
                    return getJvmBrotliInputStream(measure, bis, 6);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q8:
                    return getJvmBrotliInputStream(measure, bis, 8);
                case LZ4_COMPRESSOR_BLOCK:
                    return new BlockLZ4CompressorInputStream(bis);
                case LZ4_COMPRESSOR_FRAMED:
                    return new FramedLZ4CompressorInputStream(bis);
                case LZ4_ORG_BLOCK:
                    return new LZ4BlockInputStream(bis);
                case LZ4_ORG_FRAME:
                    return new LZ4FrameInputStream(bis);
                case BYTE_ARRAY:
                    // No input stream added
                    return bis;
            }
        } catch (IOException ex) {
            System.out.println(SerializationTestUtils.CAUGHT_ERROR_PROCESSING + measure.getType() + " instantiateInputStreamByType : " + ex);
        }
        return null;
    }

    /**
     * Instantiate the OutputStream by type SerializationType.
     * Pay attention that it is not always possible to pass the Buffer Compress Size as input parameter.
     *
     * @param measure the Measure
     * @param baos    the ByteArrayOutputStream
     * @return the selected OutputStream
     */
    public static OutputStream instantiateOutputStreamByMeasureType(Measure measure, ByteArrayOutputStream baos) {
        try {
            switch (measure.getType()) {
                case GZIP_STANDARD:
                    return new GZIPOutputStream(baos, measure.getBufferCompressCalculated());
                case GZIP_PARALLEL_SHEVEK:
                    return new MyParallelGZIPOutputStream(baos);
                case GZIP_PARALLEL:
                    return new ParallelGZIPOutputStream(baos);
                case GZIP_COMPRESSOR_AC:
                    GzipParameters gp = new GzipParameters();
                    gp.setBufferSize(measure.getBufferCompressCalculated());
                    return new GzipCompressorOutputStream(baos, gp);
                case BZIP2_COMPRESSOR:
                    return new BZip2CompressorOutputStream(baos);
                case SNAPPY_COMPRESSOR:
                    return new SnappyCompressorOutputStream(baos, baos.size());
                case DEFLATE_COMPRESSOR:
                    return new DeflateCompressorOutputStream(baos);
                case XZ_COMPRESSOR:
                    return new XZCompressorOutputStream(baos);
                case LZMA_COMPRESSOR:
                    return new LZMACompressorOutputStream(baos);
                case ZSTD_COMPRESSOR:
                    return new ZstdCompressorOutputStream(baos);
                case PACK_200_COMPRESSOR:
                    return new Pack200CompressorOutputStream(baos);
                case FRAMED_SNAPPY_COMPRESSOR:
                    return new FramedSnappyCompressorOutputStream(baos);
                case BROTLI_COMPRESSOR_BROTLI4J_Q2:
                    return getBrotli4jOutputStream(measure, baos, 2);
                case BROTLI_COMPRESSOR_BROTLI4J_Q4:
                    return getBrotli4jOutputStream(measure, baos, 4);
                case BROTLI_COMPRESSOR_BROTLI4J_Q6:
                    return getBrotli4jOutputStream(measure, baos, 6);
                case BROTLI_COMPRESSOR_BROTLI4J_Q8:
                    return getBrotli4jOutputStream(measure, baos, 8);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q2:
                    return getJvmBrotliOutputStream(measure, baos, 2);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q4:
                    return getJvmBrotliOutputStream(measure, baos, 4);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q6:
                    return getJvmBrotliOutputStream(measure, baos, 6);
                case BROTLI_COMPRESSOR_JVMBROTLI_Q8:
                    return getJvmBrotliOutputStream(measure, baos, 8);
                case LZ4_COMPRESSOR_BLOCK:
                    return new BlockLZ4CompressorOutputStream(baos);
                case LZ4_COMPRESSOR_FRAMED:
                    return new FramedLZ4CompressorOutputStream(baos);
                case LZ4_ORG_BLOCK:
                    return new LZ4BlockOutputStream(baos);
                case LZ4_ORG_FRAME:
                    return new LZ4FrameOutputStream(baos);
                case BYTE_ARRAY:
                    // No compression output stream added, but need to adapt measure object to make sure it is consistent
                    measure.cleanBufferCompress();
                    measure.cleanBufferCompressSizeType();
                    return baos;
            }
        } catch (IOException ex) {
            System.out.println(SerializationTestUtils.CAUGHT_ERROR_PROCESSING + measure + " : " + ex);
        }
        return null;
    }

    private static BrotliOutputStream getBrotli4jOutputStream(Measure measure, ByteArrayOutputStream baos, int quality) throws IOException {
        // Load the native library
        Brotli4jLoader.ensureAvailability();
        // If being used to compress streams in real-time, I do not advise a quality setting above 4 due to performance
        Encoder.Parameters bp = new Encoder.Parameters().setQuality(quality);
        return new BrotliOutputStream(baos, bp, measure.getBufferCompressCalculated());
    }

    private static BrotliOutputStream getBrotli4jOutputStream(Measure measure, BufferedOutputStream bos, int quality) throws IOException {
        // Load the native library
        Brotli4jLoader.ensureAvailability();
        // If being used to compress streams in real-time, I do not advise a quality setting above 4 due to performance
        Encoder.Parameters bp = new Encoder.Parameters().setQuality(quality);
        return new BrotliOutputStream(bos, bp, measure.getBufferCompressCalculated());
    }

    private static com.nixxcode.jvmbrotli.enc.BrotliOutputStream getJvmBrotliOutputStream(Measure measure, ByteArrayOutputStream baos, int quality) throws IOException {
        // Load the native library
        com.nixxcode.jvmbrotli.common.BrotliLoader.isBrotliAvailable();
        // If being used to compress streams in real-time, I do not advise a quality setting above 4 due to performance
        com.nixxcode.jvmbrotli.enc.Encoder.Parameters jbp = new com.nixxcode.jvmbrotli.enc.Encoder.Parameters().setQuality(quality);
        return new com.nixxcode.jvmbrotli.enc.BrotliOutputStream(baos, jbp, measure.getBufferCompressCalculated());
    }

    private static com.nixxcode.jvmbrotli.enc.BrotliOutputStream getJvmBrotliOutputStream(Measure measure, BufferedOutputStream bos, int quality) throws IOException {
        // Load the native library
        com.nixxcode.jvmbrotli.common.BrotliLoader.isBrotliAvailable();
        // If being used to compress streams in real-time, I do not advise a quality setting above 4 due to performance
        com.nixxcode.jvmbrotli.enc.Encoder.Parameters jbp = new com.nixxcode.jvmbrotli.enc.Encoder.Parameters().setQuality(quality);
        return new com.nixxcode.jvmbrotli.enc.BrotliOutputStream(bos, jbp, measure.getBufferCompressCalculated());
    }

    private static BrotliInputStream getBrotli4jInputStream(FileInputStream fis, int quality) throws IOException {
        // Load the native library
        Brotli4jLoader.ensureAvailability();
        // If being used to compress streams in real-time, I do not advise a quality setting above 4 due to performance
        Encoder.Parameters bp = new Encoder.Parameters().setQuality(quality);
        return new BrotliInputStream(fis, KB_SIZE);
    }

    private static BrotliInputStream getBrotli4jInputStream(Measure measure, BufferedInputStream bis, int quality) throws IOException {
        // Load the native library
        Brotli4jLoader.ensureAvailability();
        // If being used to compress streams in real-time, I do not advise a quality setting above 4 due to performance
        Encoder.Parameters bp = new Encoder.Parameters().setQuality(quality);
        return new BrotliInputStream(bis, measure.getBufferCompressCalculated());
    }

    private static com.nixxcode.jvmbrotli.dec.BrotliInputStream getJvmBrotliInputStream(FileInputStream fis, int quality) throws IOException {
        // Load the native library
        com.nixxcode.jvmbrotli.common.BrotliLoader.isBrotliAvailable();
        // If being used to compress streams in real-time, I do not advise a quality setting above 4 due to performance
        com.nixxcode.jvmbrotli.enc.Encoder.Parameters jbp = new com.nixxcode.jvmbrotli.enc.Encoder.Parameters().setQuality(quality);
        return new com.nixxcode.jvmbrotli.dec.BrotliInputStream(fis, KB_SIZE);
    }

    private static com.nixxcode.jvmbrotli.dec.BrotliInputStream getJvmBrotliInputStream(Measure measure, BufferedInputStream bis, int quality) throws IOException {
        // Load the native library
        com.nixxcode.jvmbrotli.common.BrotliLoader.isBrotliAvailable();
        // If being used to compress streams in real-time, I do not advise a quality setting above 4 due to performance
        com.nixxcode.jvmbrotli.enc.Encoder.Parameters jbp = new com.nixxcode.jvmbrotli.enc.Encoder.Parameters().setQuality(quality);
        return new com.nixxcode.jvmbrotli.dec.BrotliInputStream(bis, measure.getBufferCompressCalculated());
    }

    private static BrotliOutputStream getBrotli4jOutputStream(FileOutputStream fos, int quality) throws IOException {
        // Load the native library
        Brotli4jLoader.ensureAvailability();
        // If being used to compress streams in real-time, I do not advise a quality setting above 4 due to performance
        Encoder.Parameters bp = new Encoder.Parameters().setQuality(quality);
        return new BrotliOutputStream(fos, bp, KB_SIZE);
    }

    private static com.nixxcode.jvmbrotli.enc.BrotliOutputStream getJvmBrotliOutputStream(FileOutputStream fos, int quality) throws IOException {
        // Load the native library
        com.nixxcode.jvmbrotli.common.BrotliLoader.isBrotliAvailable();
        // If being used to compress streams in real-time, I do not advise a quality setting above 4 due to performance
        com.nixxcode.jvmbrotli.enc.Encoder.Parameters jbp = new com.nixxcode.jvmbrotli.enc.Encoder.Parameters().setQuality(quality);
        return new com.nixxcode.jvmbrotli.enc.BrotliOutputStream(fos, jbp, KB_SIZE);
    }

    public static void logDuration(long start, Measure measure, Map<Long, Measure> benchmarks) {
        long elapsed = System.nanoTime() - start;
        measure.setElapsed(elapsed);
        benchmarks.put(elapsed, measure);
        System.out.println(measure.description());
    }

    public static void logDuration(long start, String description) {
        long elapsed = System.nanoTime() - start;
        System.out.println("[WALLY] " + description + " - executed in " + (elapsed / 1000000L) + " ms");
    }

    public static void serializeFile(MyFile myFile, Measure measure, OutputStream os) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(myFile);
            oos.close();
        } catch (IOException ex) {
            System.out.println(CAUGHT_ERROR_PROCESSING + " serialization of " + measure + " : " + ex);
        }
    }

    public static MyFile deserializeFile(InputStream inStream) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inStream);
        MyFile outputFile = (MyFile) objectInputStream.readObject();
        objectInputStream.close();
        return outputFile;
    }

    public void testSerializationByType(MyFile myFile, Measure measure) {
        byte[] myFileBytes = SerializationUtils.serialize(myFile);
        compressDecompress(myFile, measure, myFileBytes);
    }

    public void compressDecompress(MyFile myFile, Measure measure, byte[] originalFileBytes) {

        try {
            System.out.println("\n[WALLY] ########## Test compress and decompress: " + measure.fileDescription());

            compressAndSerializeToFile(myFile, measure);

            MyFile outputFile = UncompressAndDeserializeFromFile(measure);

            byte[] outputFileBytes = SerializationUtils.serialize(outputFile);

            boolean lenghtComparison = (originalFileBytes.length == outputFileBytes.length);

            System.out.println("Byte array length comparison: " + lenghtComparison);
            System.out.println("Byte array comparison: " + Arrays.equals(originalFileBytes, outputFileBytes));

            if (!Arrays.equals(originalFileBytes, outputFileBytes)) { // LOG assertion
                System.out.println(CAUGHT_ERROR_PROCESSING + measure.getType() + " : Byte arrays in and out should be equal !!!");
            }
        } catch (Exception ex) {
            System.out.println(CAUGHT_ERROR_PROCESSING + measure.getType() + " compressDecompress : " + ex);
        }
    }

    public static void compressAndSerializeToFile(MyFile myFile, Measure measure) throws IOException {
        long start = System.nanoTime();
        File fileWrite = new File(TEST_PATH + "File_" + measure.fileDescription() + ".obj");
        System.out.println("Writing .obj file to path: " + fileWrite.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(fileWrite);
        BufferedOutputStream bufferedFos = new BufferedOutputStream(fos, measure.getBufferCompressCalculated());
        OutputStream os = instantiateOutputStreamByType(measure, bufferedFos);
        serializeFile(myFile, measure, os);
        measure.setBytesCount(Files.size(fileWrite.toPath()));
        logDuration(start, measure.getType().name());
    }

    public static MyFile UncompressAndDeserializeFromFile(Measure measure) throws IOException, ClassNotFoundException {
        long start = System.nanoTime();
        File fileRead = new File(TEST_PATH + "File_" + measure.fileDescription() + ".obj");
        long bytes = Files.size(fileRead.toPath());
        System.out.println("Reading .obj file: " + bytes + " bytes (" + FileUtils.byteCountToDisplaySize(bytes) + ") from path: " + fileRead.getAbsolutePath());
        FileInputStream fis = new FileInputStream(fileRead);
        BufferedInputStream bufferedFis = new BufferedInputStream(fis, measure.getBufferCompressCalculated());
        InputStream inStream = instantiateInputStreamByType(measure, bufferedFis);
        measure.setBytesCount(bytes);
        MyFile outputFile = deserializeFile(inStream);
        logDuration(start, measure.getType().name());
        return outputFile;
    }
}
