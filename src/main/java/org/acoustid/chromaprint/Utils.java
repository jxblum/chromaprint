// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

public class Utils {
    private static final byte[] GRAY_CODES = {0, 1, 3, 2};
    
    public static int grayCode(int i) {
        if (i >= 0 && i < GRAY_CODES.length) {
            return GRAY_CODES[i];
        }
        return 0;
    }
    
    public static double indexToFreq(int i, int frameSize, int sampleRate) {
        return (double)i * sampleRate / frameSize;
    }
    
    public static int freqToIndex(double freq, int frameSize, int sampleRate) {
        return (int)Math.round(frameSize * freq / sampleRate);
    }
    
    public static boolean isNaN(double value) {
        return Double.isNaN(value);
    }
    
    public static double freqToBark(double f) {
        double z = (26.81 * f) / (1960.0 + f) - 0.53;
        
        if (z < 2.0) {
            z = z + 0.15 * (2.0 - z);
        } else if (z > 20.1) {
            z = z + 0.22 * (z - 20.1);
        }
        
        return z;
    }
    
    public static int countSetBits(long v) {
        return Long.bitCount(v);
    }
    
    public static int hammingDistance(long a, long b) {
        return countSetBits(a ^ b);
    }
}
