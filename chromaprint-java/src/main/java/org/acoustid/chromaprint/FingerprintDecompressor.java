// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

import java.util.ArrayList;
import java.util.List;

import org.acoustid.chromaprint.utils.PackIntArray;

public class FingerprintDecompressor {
    private static final int MAX_NORMAL_VALUE = 7;
    private static final int NORMAL_BITS = 3;
    private static final int EXCEPTION_BITS = 5;

    private List<Long> output;
    private int size;
    private int algorithm;
    private List<Byte> bits;
    private List<Byte> exceptionalBits;

    public FingerprintDecompressor() {
        this.output = new ArrayList<>();
        this.size = 0;
        this.algorithm = -1;
        this.bits = new ArrayList<>();
        this.exceptionalBits = new ArrayList<>();
    }

    public boolean decompressHeader(byte[] input) {
        if (input.length < 4) {
            return false;
        }

        algorithm = input[0] & 0xFF;
        size = ((input[1] & 0xFF) << 16) |
               ((input[2] & 0xFF) << 8) |
               (input[3] & 0xFF);

        return true;
    }

    public boolean decompress(byte[] input) {
        if (!decompressHeader(input)) {
            return false;
        }

        int offset = 4;
        if (input.length < offset) {
            return false;
        }

        byte[] normalData = new byte[input.length - offset];
        System.arraycopy(input, offset, normalData, 0, normalData.length);
        byte[] unpacked = PackIntArray.unpackInt3Array(normalData);

        bits.clear();
        for (byte b : unpacked) {
            bits.add(b);
        }

        int foundValues = 0;
        int numExceptionalBits = 0;
        for (int i = 0; i < bits.size(); i++) {
            if (bits.get(i) == 0) {
                foundValues++;
                if (foundValues == size) {
                    bits = bits.subList(0, i + 1);
                    break;
                }
            } else if (bits.get(i) == MAX_NORMAL_VALUE) {
                numExceptionalBits++;
            }
        }

        if (foundValues != size) {
            return false;
        }

        offset += PackIntArray.getPackedInt3ArraySize(bits.size());
        if (input.length < offset + PackIntArray.getPackedInt5ArraySize(numExceptionalBits)) {
            return false;
        }

        if (numExceptionalBits > 0) {
            byte[] exceptionalData = new byte[PackIntArray.getPackedInt5ArraySize(numExceptionalBits)];
            System.arraycopy(input, offset, exceptionalData, 0, exceptionalData.length);
            byte[] unpackedExceptional = PackIntArray.unpackInt5Array(exceptionalData);

            exceptionalBits.clear();
            for (byte b : unpackedExceptional) {
                exceptionalBits.add(b);
            }

            for (int i = 0, j = 0; i < bits.size(); i++) {
                if (bits.get(i) == MAX_NORMAL_VALUE) {
                    bits.set(i, (byte)(bits.get(i) + exceptionalBits.get(j++)));
                }
            }
        }

        output.clear();
        for (int i = 0; i < size; i++) {
            output.add(0L);
        }

        unpackBits();
        return true;
    }

    private void unpackBits() {
        int i = 0;
        int lastBit = 0;
        long value = 0;
        for (int j = 0; j < bits.size(); j++) {
            int bit = bits.get(j) & 0xFF;
            if (bit == 0) {
                output.set(i, value);
                lastBit = 0;
                i++;
            } else {
                lastBit += bit;
                value ^= 1L << (lastBit - 1);
            }
        }
    }

    public List<Long> getOutput() {
        return new ArrayList<>(output);
    }

    public int getSize() {
        return size;
    }

    public int getAlgorithm() {
        return algorithm;
    }
}
