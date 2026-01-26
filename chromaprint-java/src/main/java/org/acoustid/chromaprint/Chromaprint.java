// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

import java.util.ArrayList;
import java.util.List;

import org.acoustid.chromaprint.utils.Base64;

import lombok.Getter;

/**
 * Main {@literal Chromaprint} API class for generating audio fingerprints.
 * <p>
 * {@literal Chromaprint} is a library for generating audio fingerprints, mainly to be used
 * with the {@literal AcoustID} service. It needs raw audio stream (16-bit signed int) on input.
 *
 * @author Lukas Lalinsky
 * @author Cursor AI
 * @author John Blum
 * @see Fingerprinter
 */
@Getter
public class Chromaprint {

    public static final int VERSION_MAJOR = 1;
    public static final int VERSION_MINOR = 6;
    public static final int VERSION_PATCH = 0;

    public static final int ALGORITHM_TEST1 = 0;
    public static final int ALGORITHM_TEST2 = 1;
    public static final int ALGORITHM_TEST3 = 2;
    public static final int ALGORITHM_TEST4 = 3;  // removes leading silence
    public static final int ALGORITHM_TEST5 = 4;
    public static final int ALGORITHM_DEFAULT = ALGORITHM_TEST2;

    private final int algorithm;

    private final Fingerprinter fingerprinter;

    private final FingerprintCompressor compressor;

    /**
     * Create a new {@link Chromaprint} context.
     *
     * @param algorithm the fingerprint algorithm version to use, or ALGORITHM_DEFAULT
     */
    public Chromaprint(int algorithm) {
        this.algorithm = algorithm;
        this.fingerprinter = new Fingerprinter(FingerprinterConfigurationFactory.create(algorithm));
        this.compressor = new FingerprintCompressor();
    }

    /**
     * Create a new Chromaprint context with the default algorithm.
     */
    public Chromaprint() {
        this(ALGORITHM_DEFAULT);
    }

    /**
     * Get the version string.
     */
    public static String getVersion() {
        return "%s.%s.%s".formatted(VERSION_MAJOR, VERSION_MINOR, VERSION_PATCH);
    }

    /**
     * Set a configuration option for the selected fingerprint algorithm.
     *
     * DO NOT USE THIS IF YOU ARE PLANNING TO USE THE GENERATED FINGERPRINTS
     * WITH THE ACOUSTID SERVICE.
     *
     * @param name option name (e.g., "silence_threshold")
     * @param value option value
     * @return true on success, false on error
     */
    public boolean setOption(String name, int value) {
        return fingerprinter.setOption(name, value);
    }

    /**
     * Get the number of channels that is internally used for fingerprinting.
     */
    public int getNumChannels() {
        return 1;
    }

    /**
     * Get the sampling rate that is internally used for fingerprinting.
     */
    public int getSampleRate() {
        return fingerprinter.getConfig().getSampleRate();
    }

    /**
     * Get the duration of one item in the raw fingerprint in samples.
     */
    public int getItemDuration() {
        return fingerprinter.getConfig().getItemDuration();
    }

    /**
     * Get the duration of one item in the raw fingerprint in milliseconds.
     */
    public int getItemDurationMs() {
        return (int)(fingerprinter.getConfig().getItemDurationInSeconds() * 1000);
    }

    /**
     * Get the duration of internal buffers that the fingerprinting algorithm uses.
     */
    public int getDelay() {
        return fingerprinter.getConfig().getDelay();
    }

    /**
     * Get the duration of internal buffers in milliseconds.
     */
    public int getDelayMs() {
        return (int)(fingerprinter.getConfig().getDelayInSeconds() * 1000);
    }

    /**
     * Restart the computation of a fingerprint with a new audio stream.
     *
     * @param sampleRate sample rate of the audio stream (in Hz)
     * @param numChannels number of channels in the audio stream (1 or 2)
     * @return true on success, false on error
     */
    public boolean start(int sampleRate, int numChannels) {
        return fingerprinter.start(sampleRate, numChannels);
    }

    /**
     * Send audio data to the fingerprint calculator.
     *
     * @param data raw audio data, should be an array of 16-bit signed integers
     * @param size size of the data buffer (in samples)
     * @return true on success, false on error
     */
    public boolean feed(short[] data, int size) {
        if (data == null || size < 0 || size > data.length) {
            return false;
        }
        fingerprinter.consume(data, size);
        return true;
    }

    /**
     * Process any remaining buffered audio data.
     *
     * @return true on success, false on error
     */
    public boolean finish() {
        fingerprinter.finish();
        return true;
    }

    /**
     * Return the calculated fingerprint as a compressed base64-encoded string.
     *
     * @return the fingerprint string, or null on error
     */
    public String getFingerprint() {
        List<Long> rawFp = fingerprinter.getFingerprint();
        byte[] compressed = compressor.compress(rawFp, algorithm);
        return Base64.encode(compressed);
    }

    /**
     * Return the calculated fingerprint as an array of 32-bit integers.
     *
     * @return the raw fingerprint array
     */
    public long[] getRawFingerprint() {
        List<Long> fp = fingerprinter.getFingerprint();
        long[] result = new long[fp.size()];
        for (int i = 0; i < fp.size(); i++) {
            result[i] = fp.get(i);
        }
        return result;
    }

    /**
     * Return the length of the current raw fingerprint.
     *
     * @return number of items in the current raw fingerprint
     */
    public int getRawFingerprintSize() {
        return this.fingerprinter.getFingerprint().size();
    }

    /**
     * Return 32-bit hash of the calculated fingerprint.
     *
     * @return the hash value
     */
    public long getFingerprintHash() {
        return SimHash.simHash(this.fingerprinter.getFingerprint());
    }

    /**
     * Clear the current fingerprint, but allow more data to be processed.
     */
    public void clearFingerprint() {
        this.fingerprinter.clearFingerprint();
    }

    /**
     * Compress and optionally base64-encode a raw fingerprint.
     *
     * @param fp array of 32-bit integers representing the raw fingerprint
     * @param algorithm Chromaprint algorithm version which was used to generate the raw fingerprint
     * @param base64 whether to return base64-encoded ASCII data or binary data
     * @return the encoded fingerprint
     */
    public static byte[] encodeFingerprint(long[] fp, int algorithm, boolean base64) {
        List<Long> fpList = new ArrayList<>();
        for (long v : fp) {
            fpList.add(v);
        }
        FingerprintCompressor compressor = new FingerprintCompressor();
        byte[] compressed = compressor.compress(fpList, algorithm);
        if (base64) {
            return Base64.encode(compressed).getBytes();
        }
        return compressed;
    }

    /**
     * Uncompress and optionally base64-decode an encoded fingerprint.
     *
     * @param encodedFp the encoded fingerprint
     * @param base64 whether the encodedFp contains base64-encoded ASCII data
     * @return a FingerprintResult containing the decoded fingerprint, size, and algorithm
     */
    public static FingerprintResult decodeFingerprint(byte[] encodedFp, boolean base64) {
        byte[] data = encodedFp;
        if (base64) {
            data = Base64.decode(new String(encodedFp));
        }
        FingerprintDecompressor decompressor = new FingerprintDecompressor();
        if (!decompressor.decompress(data)) {
            return null;
        }
        return new FingerprintResult(
            toPrimitiveLongArray(decompressor.getOutput()),
            decompressor.getAlgorithm()
        );
    }

    private static long[] toPrimitiveLongArray(List<Long> list) {
        long[] array = new long[list.size()];
        for (int index= 0; index < array.length; index++) {
            array[index] = list.get(index);
        }
        return array;
    }

    /**
     * Generate a single 32-bit hash for a raw fingerprint.
     *
     * @param fp array of 32-bit integers representing the raw fingerprint
     * @return the hash value
     */
    public static long hashFingerprint(long[] fp) {
        return SimHash.simHash(fp);
    }

    /**
         * Result of decoding a fingerprint.
         */
    public record FingerprintResult(long[] fingerprint, int algorithm) {

    }
}
