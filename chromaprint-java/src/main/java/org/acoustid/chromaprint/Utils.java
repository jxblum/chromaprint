// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

/**
 * @author Lukas Lalinsky
 * @author Cursor AI
 * @author John Blum
 */
public class Utils {

	private static final byte[] GRAY_CODES = { 0, 1, 3, 2 };

	public static int countSetBits(long v) {
		return Long.bitCount(v);
	}

	public static double frequencyToBark(double frequency) {

		double z = (26.81 * frequency) / (1960.0 + frequency) - 0.53;

		if (z < 2.0) {
			z = z + 0.15 * (2.0 - z);
		}
		else if (z > 20.1) {
			z = z + 0.22 * (z - 20.1);
		}

		return z;
	}

	public static int frequencyToIndex(double frequency, int frameSize, int sampleRate) {
		return (int) Math.round(frameSize * frequency / sampleRate);
	}

	public static double indexToFrequency(int index, int frameSize, int sampleRate) {
		double numerator = index * sampleRate;
		return numerator / frameSize;
	}

	public static int grayCode(int i) {

		if (i >= 0 && i < GRAY_CODES.length) {
			return GRAY_CODES[i];
		}

		return 0;
	}

	public static int hammingDistance(long a, long b) {
		return countSetBits(a ^ b);
	}
}
