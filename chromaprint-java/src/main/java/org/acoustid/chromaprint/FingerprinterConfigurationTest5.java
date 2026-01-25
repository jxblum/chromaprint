// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

public class FingerprinterConfigurationTest5 extends FingerprinterConfigurationTest2 {
    private static final int DEFAULT_FRAME_SIZE = 4096;
    
    public FingerprinterConfigurationTest5() {
        super();
        setFrameSize(DEFAULT_FRAME_SIZE / 2);
        setFrameOverlap(DEFAULT_FRAME_SIZE / 2 - DEFAULT_FRAME_SIZE / 4);
    }
}
