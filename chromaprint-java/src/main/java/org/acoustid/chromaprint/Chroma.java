// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

public class Chroma implements FFTFrameConsumer {

    private final int frameSize;
    private final int minFreq;
    private final int maxFreq;
    private final int sampleRate;

    private FeatureVectorConsumer consumer;

    public Chroma(int minFreq, int maxFreq, int frameSize, int sampleRate, FeatureVectorConsumer consumer) {
        this.minFreq = minFreq;
        this.maxFreq = maxFreq;
        this.frameSize = frameSize;
        this.sampleRate = sampleRate;
        this.consumer = consumer;
    }

    @Override
    public void consume(FFTFrame frame) {
        // Simplified chroma calculation
        // This is a placeholder that allows compilation
        // TODO: Full implementation would compute chroma features from FFT
    }

    public void reset() {
        // Reset internal state
    }
}
