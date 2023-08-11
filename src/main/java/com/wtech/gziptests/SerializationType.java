package com.wtech.gziptests;

import java.util.EnumSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SerializationType {
    GZIP_STANDARD("GZIP Standard"),
    GZIP_PARALLEL_SHEVEK("GZIP Parallel Shevek"),
    GZIP_PARALLEL("GZIP Parallel"),
    GZIP_COMPRESSOR_AC("GZIP Compressor Apache"),
    BZIP2_COMPRESSOR("BZIP2 Compressor"),
    DEFLATE_COMPRESSOR("Deflate Compressor"),
    XZ_COMPRESSOR("XZ Compressor"),
    LZMA_COMPRESSOR("LZMA Compressor"),
    ZSTD_COMPRESSOR("ZSTD Compressor"),
    PACK_200_COMPRESSOR("Pack 200 Compressor"),
    LZ4_COMPRESSOR_BLOCK("LZ4 Block Compressor"),
    LZ4_COMPRESSOR_FRAMED("LZ4 Framed Compressor"),
    LZ4_ORG_BLOCK("LZ4.org Block"),
    LZ4_ORG_FRAME("LZ4.org Frame"),
    SNAPPY_COMPRESSOR("Snappy Compressor"),
    FRAMED_SNAPPY_COMPRESSOR("Framed Snappy"),
    BROTLI_COMPRESSOR_BROTLI4J_Q2("Brotli4J Compressor Quality 2"),
    BROTLI_COMPRESSOR_BROTLI4J_Q4("Brotli4J Compressor Quality 4"),
    BROTLI_COMPRESSOR_BROTLI4J_Q6("Brotli4J Compressor Quality 6"),
    BROTLI_COMPRESSOR_BROTLI4J_Q8("Brotli4J Compressor Quality 8"),
    BROTLI_COMPRESSOR_JVMBROTLI_Q2("JvmBrotli Compressor Quality 2"),
    BROTLI_COMPRESSOR_JVMBROTLI_Q4("JvmBrotli Compressor Quality 4"),
    BROTLI_COMPRESSOR_JVMBROTLI_Q6("JvmBrotli Compressor Quality 6"),
    BROTLI_COMPRESSOR_JVMBROTLI_Q8("JvmBrotli Compressor Quality 8"),
    BYTE_ARRAY("ByteArray (No compression)");

    private final String mDescription;

    SerializationType(String pDescription) {
        mDescription = pDescription;
    }

    public String description() {
        return mDescription;
    }

    public boolean isByteArray() {
        return this == BYTE_ARRAY;
    }

    public static final EnumSet<SerializationType> GZIP_TYPES = EnumSet.of(GZIP_STANDARD, GZIP_COMPRESSOR_AC, GZIP_PARALLEL, GZIP_PARALLEL_SHEVEK);

    public static final EnumSet<SerializationType> QUICK_TYPES = EnumSet.of(LZ4_ORG_BLOCK, GZIP_PARALLEL, BROTLI_COMPRESSOR_BROTLI4J_Q2);

    public static final EnumSet<SerializationType> BROTLI_TYPES = EnumSet.of(BROTLI_COMPRESSOR_JVMBROTLI_Q2, BROTLI_COMPRESSOR_JVMBROTLI_Q4, BROTLI_COMPRESSOR_JVMBROTLI_Q6, BROTLI_COMPRESSOR_JVMBROTLI_Q8, BROTLI_COMPRESSOR_BROTLI4J_Q2, BROTLI_COMPRESSOR_BROTLI4J_Q4, BROTLI_COMPRESSOR_BROTLI4J_Q6, BROTLI_COMPRESSOR_BROTLI4J_Q8);

    public static final EnumSet<SerializationType> LZ4_TYPES = EnumSet.of(LZ4_ORG_FRAME, LZ4_ORG_BLOCK);

    // Those are too slow, in error, or suspicious result, maybe tuning needed
    //[WALLY] ########## TESTING LZ4_COMPRESSOR_BLOCK: 791369 ms ?!?!?
    //[WALLY] 791369 ms : LZ4 Block Compressor - ByteArrayOutputStream_Buffer (1 KB) - CompressOutputStream_Buffer (1 KB) -  original (14 MB) -  actual (8 MB) -  compression (43.81 %)
    //[WALLY] ########## TESTING LZ4_COMPRESSOR_FRAMED: 617876 ms ?!?!?
    //[WALLY] 617876 ms : LZ4 Framed Compressor - ByteArrayOutputStream_Buffer (1 KB) - CompressOutputStream_Buffer (1 KB) -  original (14 MB) -  actual (7 MB) -  compression (48.82 %)
    //[WALLY] ########## TOP RESULT PACK_200_COMPRESSOR: 20 bytes ?!?!?
    //[WALLY] 71 ms : Serialization: Pack 200 Compressor - ByteArrayOutputStream_BufferSize (8 KB) - CompressOutputStream_BufferSize (8 KB) - 20 bytes (20 bytes)
    public static final EnumSet<SerializationType> IN_ERROR = EnumSet.of(PACK_200_COMPRESSOR, LZ4_COMPRESSOR_BLOCK, LZ4_COMPRESSOR_FRAMED);

    public static final EnumSet<SerializationType> BEST_CANDIDATES = EnumSet.of(LZ4_ORG_BLOCK, LZ4_ORG_FRAME, GZIP_PARALLEL, BROTLI_COMPRESSOR_JVMBROTLI_Q2, BROTLI_COMPRESSOR_JVMBROTLI_Q4, BROTLI_COMPRESSOR_BROTLI4J_Q2, BROTLI_COMPRESSOR_BROTLI4J_Q4);

    public static final EnumSet<SerializationType> ALL_TYPES = Stream.of(SerializationType.values())
            .filter(type -> !type.name().contains("LZ4_COMPRESSOR")) // remove the ones in error or super slow
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(SerializationType.class)));

    public static final EnumSet<SerializationType> OTHER_TYPES = EnumSet.of(BYTE_ARRAY);

    public static final EnumSet<SerializationType> SLOW_TYPES = EnumSet.of(SNAPPY_COMPRESSOR, BZIP2_COMPRESSOR, LZMA_COMPRESSOR, XZ_COMPRESSOR, BROTLI_COMPRESSOR_BROTLI4J_Q6, BROTLI_COMPRESSOR_BROTLI4J_Q8, BROTLI_COMPRESSOR_JVMBROTLI_Q6, BROTLI_COMPRESSOR_JVMBROTLI_Q8);
}
