![Java](https://cdn.icon-icons.com/icons2/2699/PNG/512/java_logo_icon_168609.png)

# GZIP Alternatives Benchmark

![Apache 2.0 License](https://img.shields.io/badge/License-Apache2.0-orange)
![Java](https://img.shields.io/badge/Built_with-Java-blue)
![Maven](https://img.shields.io/badge/Powered_by-Maven-green)
![Build Status](https://github.com/wallaceespindola/gzip-alternatives/actions/workflows/maven.yml/badge.svg)

## Purpose

The primary goal of this project is to benchmark and compare various Java compression libraries to identify faster and more efficient alternatives to the standard GZIP implementation for stream compression. It evaluates performance based on:
- **Compression Speed**: How fast the data can be compressed.
- **Decompression Speed**: How fast the data can be decompressed.
- **Compression Ratio**: The reduction in file size.

## Supported Algorithms

The benchmark covers a wide array of algorithms, including:

- **GZIP**: Standard JDK, Parallel GZIP, Apache Commons Compress.
- **Brotli**: Brotli4j (Google), JvmBrotli.
- **LZ4**: LZ4 Java (Fast & High compression), Apache Commons LZ4.
- **Snappy**: Google Snappy (Framed & Unframed).
- **Zstandard (Zstd)**: Zstd-jni.
- **Others**: BZIP2, XZ, LZMA, Deflate, Pack200.

## Methodology

The benchmarks are run in two main modes:
1. **Text Mode**: Serializing and compressing a large text string (loaded from `src/main/resources`).
2. **Byte Mode**: Serializing and compressing a large array of doubles.

The results are output to the console, showing the execution time and the resulting file size for each algorithm.


## Requirements

- Java 21 or higher
- Maven 3.6 or higher

## Usage

### Build

```bash
mvn clean install
```

### Run Benchmarks

To run the basic tests:

```bash
mvn exec:java -Dexec.mainClass="com.wtech.gziptests.TestBasics"
```

To run the main benchmark:

```bash
mvn exec:java -Dexec.mainClass="com.wtech.gziptests.Benchmark"
```


## Author

- Wallace Espindola, Sr. Software Engineer / Java & Python Dev
- E-mail: wallace.espindola@gmail.com
- LinkedIn: https://www.linkedin.com/in/wallaceespindola/
- Gravatar: https://gravatar.com/wallacese
- Website: https://wtechitsolutions.com/

## License

- This project is released under the Apache 2.0 License. See the [LICENSE](LICENSE) file for details.
- Copyright © 2023 [Wallace Espindola](https://github.com/wallaceespindola/).
