# AGENTS.md

## Project Overview
This project is a benchmarking suite for various Java compression libraries and algorithms, specifically focusing on alternatives to standard GZIP for stream compression. It measures serialization and compression performance (time and size) for different data types (text and byte arrays).

## Key Components

### Core Classes
- **`Benchmark.java`**: The main entry point for running benchmarks. It contains `test1` and `test2` methods for different scenarios.
- **`SerializationTestUtils.java`**: A utility class that handles the core logic for serialization, compression, decompression, and measurement. It manages the different `SerializationType` enums and instantiates the appropriate input/output streams.
- **`SerializationType.java`**: An enum defining all supported compression algorithms and libraries (e.g., GZIP, Brotli, LZ4, Snappy, Zstd).
- **`CountingOutputStream.java`**: A simple `OutputStream` implementation that counts the number of bytes written, used for measuring compressed sizes.
- **`TestBasics.java`**: A simpler test class for basic verification of serialization and compression.

### Supported Algorithms
The project supports a wide range of compression algorithms, including:
- **GZIP**: Standard, Parallel, and Apache Commons implementations.
- **Brotli**: Brotli4j and JvmBrotli implementations with various quality levels (Q2-Q8).
- **LZ4**: Block and Framed implementations (both from `org.lz4` and Apache Commons).
- **Snappy**: Standard and Framed implementations.
- **Others**: BZIP2, Deflate, XZ, LZMA, Zstd, Pack200.

### Build and Run
- **Build**: `mvn clean install`
- **Run Main Benchmark**: `mvn exec:java -Dexec.mainClass="com.wtech.gziptests.Benchmark"`
- **Run Basic Test**: `mvn exec:java -Dexec.mainClass="com.wtech.gziptests.TestBasics"`

## Known Issues / Notes
- **LZ4 Commons Compress**: The `LZ4_COMPRESSOR_BLOCK` and `LZ4_COMPRESSOR_FRAMED` (Apache Commons) implementations are noted as being very slow or producing suspicious results in `SerializationType.java`.
- **Pack200**: Also noted as producing suspicious results (very small size).
- **Java Version**: The project requires Java 21.

## Directory Structure
- `src/main/java/com/wtech/gziptests/`: Contains all source code.
- `src/main/resources/`: Contains test data files (e.g., `test-file-20mb.txt`).
- `testResults/`: Directory where benchmark output files (`.obj`) are written.
