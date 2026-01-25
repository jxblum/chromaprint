# Chromaprint Java

This is a Java port of the Chromaprint audio fingerprinting library, originally written in C++.

## Building

This project uses Maven for building. To build the project:

```bash
mvn clean compile
```

To build a JAR file:

```bash
mvn clean package
```

To run tests:

```bash
mvn test
```

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Usage

Basic usage example:

```java
import org.acoustid.chromaprint.Chromaprint;

// Create a Chromaprint context
Chromaprint ctx = new Chromaprint(Chromaprint.ALGORITHM_DEFAULT);

// Initialize with audio stream parameters
int sampleRate = 44100;
int numChannels = 2;
ctx.start(sampleRate, numChannels);

// Feed audio data (16-bit signed integers)
short[] audioData = ...; // Your audio samples
ctx.feed(audioData, audioData.length);

// Finish processing
ctx.finish();

// Get the fingerprint
String fingerprint = ctx.getFingerprint();
System.out.println(fingerprint);

// Or get raw fingerprint
long[] rawFingerprint = ctx.getRawFingerprint();
```

## Status

This is a conversion of the C++ Chromaprint library to Java by Cursor.

The core fingerprinting algorithms have been ported, but some advanced features (like full FFT implementation, audio decoding) may need additional work for production use.

## Differences from C++ Version

- The Java version uses JTransforms for FFT operations (instead of FFmpeg/FFTW3)
- Audio decoding is not included - you'll need to provide raw 16-bit PCM audio samples
- Some low-level optimizations from the C++ version may not be present

## License

Same as the original Chromaprint project - MIT license (with LGPL 2.1 for included FFmpeg code in the original).
