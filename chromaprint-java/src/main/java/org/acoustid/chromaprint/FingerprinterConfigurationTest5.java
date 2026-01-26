// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

/**
 * @author Lukas Lalinsky
 * @author Cursor AI
 * @author John Blum
 */
public class FingerprinterConfigurationTest5 extends FingerprinterConfigurationTest2 {

    public FingerprinterConfigurationTest5() {
        setFrameSize(DEFAULT_FRAME_SIZE / 2);
        setFrameOverlap(DEFAULT_FRAME_SIZE / 2 - DEFAULT_FRAME_SIZE / 4);
    }
}
