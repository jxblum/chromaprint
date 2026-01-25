// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

public class FingerprinterConfigurationTest4 extends FingerprinterConfigurationTest2 {
    public FingerprinterConfigurationTest4() {
        super();
        setRemoveSilence(true);
        setSilenceThreshold(50);
    }
}
