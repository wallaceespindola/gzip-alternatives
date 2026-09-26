![Java](https://cdn.icon-icons.com/icons2/2699/PNG/512/java_logo_icon_168609.png)

# GZIP Alternatives Benchmark

![Apache 2.0 License](https://img.shields.io/badge/License-Apache2.0-orange)
![Java](https://img.shields.io/badge/Built_with-Java-blue)
![Maven](https://img.shields.io/badge/Powered_by-Maven-green)
![Build Status](https://github.com/wallaceespindola/gzip-alternatives/actions/workflows/maven.yml/badge.svg)

## Table of Contents

- [Purpose](#purpose)
- [Highlights](#highlights)
- [Supported Algorithms](#supported-algorithms)
- [Tech Stack](#tech-stack)
- [Methodology](#methodology)
- [Requirements](#requirements)
- [Usage](#usage)
- [Known Issues](#known-issues)
- [Project Structure](#project-structure)

## Purpose

The primary goal of this project is to benchmark and compare various Java compression libraries to identify faster and more efficient alternatives to the standard GZIP implementation for stream compression. It evaluates performance based on:
- **Compression Speed**: How fast the data can be compressed.
- **Decompression Speed**: How fast the data can be decompressed.
- **Compression Ratio**: The reduction in file size.

## Highlights

- 24 compressor variants plus an uncompressed baseline, defined in [SerializationType.java](src/main/java/com/wtech/gziptests/SerializationType.java)
- Each run tries several buffer sizes (KB and MB) per algorithm and reports the top results
- Results printed to the console, including a CSV block ready to paste into a spreadsheet
- Predefined algorithm groups (`BEST_CANDIDATES`, `BROTLI_TYPES`, `LZ4_TYPES`, `GZIP_TYPES`, `ALL_TYPES`, ...) to switch what gets measured
- Small standalone round-trip checks per library (LZ4, Brotli, Pack200, Zip4j)

## Supported Algorithms

The benchmark covers a wide array of algorithms, including:

- **GZIP**: Standard JDK, Parallel GZIP, Apache Commons Compress.
- **Brotli**: Brotli4j (Google), JvmBrotli, each at quality levels 2, 4, 6 and 8.
- **LZ4**: LZ4 Java (Block & Frame), Apache Commons LZ4 (Block & Framed).
- **Snappy**: Apache Commons Compress Snappy (Framed & Unframed).
- **Zstandard (Zstd)**: Zstd-jni.
- **Others**: BZIP2, XZ, LZMA, Deflate, Pack200.

## Tech Stack

| Library                     | Version  | Used for                                   |
|-----------------------------|----------|--------------------------------------------|
| Java                        | 21       | Runtime and `java.util.zip` GZIP / Deflate |
| Apache Commons Compress     | 1.28.0   | GZIP, BZIP2, Deflate, XZ, LZMA, LZ4, Snappy, Pack200 streams |
| parallelgzip (anarres)      | 1.0.5    | Parallel GZIP                              |
| Brotli4j                    | 1.23.0   | Brotli (native)                            |
| JvmBrotli                   | 0.2.0    | Brotli (native)                            |
| lz4-java                    | 1.8.1    | LZ4 Block and Frame                        |
| zstd-jni                    | 1.5.7-18 | Zstandard                                  |
| XZ for Java (tukaani)       | 1.12     | XZ / LZMA backend                          |
| Zip4j                       | 2.11.6   | ZIP round-trip check                       |
| Commons IO / Lang3 / Codec, AssertJ | see [pom.xml](pom.xml) | File helpers, serialization, MD5, assertions |

## Methodology

The benchmarks are run in two main modes:
1. **Text Mode**: Serializing and compressing a large text string (loaded from `src/main/resources`, `test-file-20mb.txt` by default).
2. **Byte Mode**: Serializing and compressing a large array of doubles.

The results are output to the console, showing the execution time and the resulting file size for each algorithm.

Example output from `TestCompression` (default settings: `BROTLI_FAST_TYPES`, `TestType.SHORT`, text mode, 20 MB input):

```text
[WALLY] %%%%%%%%%% CSV RESULTS:
Duration; Original MB; Actual MB; Original Bytes; Actual Bytes; Compression; Type;
92 ms;20 MB;13 MB;20985710;13695998;34,74 %;Brotli4J Compressor Quality 2;
114 ms;20 MB;13 MB;20985710;13694803;34,74 %;Brotli4J Compressor Quality 4;
```

Timings depend on the machine; run the benchmarks locally rather than relying on these numbers.

## Requirements

- Java 21 or higher
- Maven 3.6 or higher

## Usage

### Build

```bash
mvn clean install
```

Run the commands below from the project root: the benchmarks read input from `./src/main/resources/` and write output
files to `./testResults/`.

### Run Benchmarks

To run the basic tests:

```bash
mvn exec:java -Dexec.mainClass="com.wtech.gziptests.TestBasics"
```

To run the main benchmark:

```bash
mvn exec:java -Dexec.mainClass="com.wtech.gziptests.Benchmark"
```

Other entry points (all in `com.wtech.gziptests`, run with the same `mvn exec:java -Dexec.mainClass=...` pattern):

| Class                                  | What it runs                                                                 |
|----------------------------------------|------------------------------------------------------------------------------|
| `Benchmark`                            | Baseline Java serialization of large `double[]` arrays (no compression)      |
| `TestBasics`                           | Standard vs Apache `SerializationUtils` serialization of the test object     |
| `TestCompression`                      | Serialization + compression across buffer sizes, top results and CSV output |
| `TestDecompression`                    | Compression and decompression timings per algorithm                          |
| `TestCompressionDecompression`         | Compress/decompress round trip for `ALL_TYPES`                               |
| `TestCompressionAllDecompressionGzip`  | Compress with every type in `ALL_TYPES`, decompress with GZIP                |
| `TestLz4`, `TestBrotli`, `TestPack200`, `TestZip4J` | Small round-trip checks for a single library                    |

### Configure a Run

The benchmark selection is set in code, in each class's `main` method:

- `SerializationType` group, e.g. `SerializationType.BROTLI_FAST_TYPES` in `TestCompression`, `LZ4_TYPES` in `TestDecompression`
- `TestType`: `SHORT`, `STANDARD` or `LONG`
- `isTextMode`: `true` for text mode, `false` for byte mode
- Input file: `test-file-20mb.txt` (default) or `test-file-50mb.txt` in `SerializationTestUtils`

## Known Issues

- Apache Commons LZ4 (`LZ4_COMPRESSOR_BLOCK`, `LZ4_COMPRESSOR_FRAMED`) is very slow and excluded from `ALL_TYPES`.
- `PACK_200_COMPRESSOR` reports a suspiciously small output size; it is listed in `SerializationType.IN_ERROR`.
- There are no unit tests; CI ([maven.yml](.github/workflows/maven.yml)) runs `mvn -B package` on JDK 21.
- Running the benchmarks creates `testResults/` and `src/main/resources/test.obj` (both git-ignored).

## Project Structure

```text
gzip-alternatives/
├── src/main/java/com/wtech/gziptests/
│   ├── SerializationType.java        # algorithm enum and predefined groups
│   ├── SerializationTestUtils.java   # stream factory, serialization, measurement
│   ├── TestCompression.java          # main compression benchmark
│   ├── TestDecompression.java        # compression + decompression benchmark
│   ├── Benchmark.java, TestBasics.java, Test*.java   # other entry points
│   └── Measure*.java, CountingOutputStream.java, ...  # helpers
├── src/main/resources/
│   ├── test-file-20mb.txt            # default text-mode input
│   └── test-file-50mb.txt
├── .github/workflows/maven.yml
└── pom.xml
```

## Author

- Wallace Espindola, Sr. Software Engineer / Java & Python Dev
- E-mail: wallace.espindola@gmail.com
- LinkedIn: https://www.linkedin.com/in/wallaceespindola/
- GitHub: https://github.com/wallaceespindola/
- Gravatar: https://gravatar.com/wallacese
- Website: https://wtechitsolutions.com/

## License

- This project is released under the Apache 2.0 License. See the [LICENSE](LICENSE) file for details.
- Copyright © 2023 [Wallace Espindola](https://github.com/wallaceespindola/).
