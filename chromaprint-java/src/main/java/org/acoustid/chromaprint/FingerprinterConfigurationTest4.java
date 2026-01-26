// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

/**
 * @author Lukas Lalinsky
 * @author Cursor AI
 * @author John Blum
 */
public class FingerprinterConfigurationTest4 extends FingerprinterConfigurationTest2 {

    public FingerprinterConfigurationTest4() {
        setRemoveSilence(true);
        setSilenceThreshold(50);
    }
}
