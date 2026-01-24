// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

import java.util.ArrayList;
import java.util.List;

import org.acoustid.chromaprint.utils.PackIntArray;

public class FingerprintCompressor {
    private static final int NORMAL_BITS = 3;
    private static final int MAX_NORMAL_VALUE = (1 << NORMAL_BITS) - 1;

    private List<Byte> normalBits;
    private List<Byte> exceptionalBits;

    public FingerprintCompressor() {
        this.normalBits = new ArrayList<>();
        this.exceptionalBits = new ArrayList<>();
    }

    public byte[] compress(List<Long> fingerprint, int algorithm) {
        normalBits.clear();
        exceptionalBits.clear();

        if (fingerprint.isEmpty()) {
            return createOutput(algorithm, 0);
        }

        processSubfingerprint(fingerprint.get(0));
        for (int i = 1; i < fingerprint.size(); i++) {
            processSubfingerprint(fingerprint.get(i) ^ fingerprint.get(i - 1));
        }

        return createOutput(algorithm, fingerprint.size());
    }

    private void processSubfingerprint(long x) {
        int bit = 1;
        int lastBit = 0;
        while (x != 0) {
            if ((x & 1) != 0) {
                int value = bit - lastBit;
                if (value >= MAX_NORMAL_VALUE) {
                    normalBits.add((byte)MAX_NORMAL_VALUE);
                    exceptionalBits.add((byte)(value - MAX_NORMAL_VALUE));
                } else {
                    normalBits.add((byte)value);
                }
                lastBit = bit;
            }
            x >>>= 1;
            bit++;
        }
        normalBits.add((byte)0);
    }

    private byte[] createOutput(int algorithm, int size) {
        byte[] packedNormal = PackIntArray.packInt3Array(normalBits);
        byte[] packedExceptional = PackIntArray.packInt5Array(exceptionalBits);

        byte[] output = new byte[4 + packedNormal.length + packedExceptional.length];
        output[0] = (byte)(algorithm & 255);
        output[1] = (byte)((size >> 16) & 255);
        output[2] = (byte)((size >> 8) & 255);
        output[3] = (byte)(size & 255);

        System.arraycopy(packedNormal, 0, output, 4, packedNormal.length);
        System.arraycopy(packedExceptional, 0, output, 4 + packedNormal.length, packedExceptional.length);

        return output;
    }
}
