// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

public class FingerprinterConfigurationFactory {
    public static FingerprinterConfiguration create(int algorithm) {
        switch (algorithm) {
            case Chromaprint.ALGORITHM_TEST1:
                return new FingerprinterConfigurationTest1();
            case Chromaprint.ALGORITHM_TEST2:
                return new FingerprinterConfigurationTest2();
            case Chromaprint.ALGORITHM_TEST3:
                return new FingerprinterConfigurationTest3();
            case Chromaprint.ALGORITHM_TEST4:
                return new FingerprinterConfigurationTest4();
            case Chromaprint.ALGORITHM_TEST5:
                return new FingerprinterConfigurationTest5();
            default:
                return new FingerprinterConfigurationTest2();
        }
    }
}
