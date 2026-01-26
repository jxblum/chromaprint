// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

/**
 * Factory used to construct new {@link FingerprinterConfiguration} used to initialize a {@link Fingerprinter}.
 *
 * @author Lukas Lalinsky
 * @author Cursor AI
 * @author John Blum
 * @see FingerprinterConfiguration
 */
public class FingerprinterConfigurationFactory {

    public static FingerprinterConfiguration create(int algorithm) {
		return switch (algorithm) {
			case Chromaprint.ALGORITHM_TEST1 -> new FingerprinterConfigurationTest1();
			case Chromaprint.ALGORITHM_TEST2 -> new FingerprinterConfigurationTest2();
			case Chromaprint.ALGORITHM_TEST3 -> new FingerprinterConfigurationTest3();
			case Chromaprint.ALGORITHM_TEST4 -> new FingerprinterConfigurationTest4();
			case Chromaprint.ALGORITHM_TEST5 -> new FingerprinterConfigurationTest5();
			default -> new FingerprinterConfigurationTest2();
		};
    }
}
