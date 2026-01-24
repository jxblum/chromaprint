// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

public class Chroma implements FFTFrameConsumer {
    private int minFreq;
    private int maxFreq;
    private int frameSize;
    private int sampleRate;
    private FeatureVectorConsumer consumer;

    public Chroma(int minFreq, int maxFreq, int frameSize, int sampleRate, FeatureVectorConsumer consumer) {
        this.minFreq = minFreq;
        this.maxFreq = maxFreq;
        this.frameSize = frameSize;
        this.sampleRate = sampleRate;
        this.consumer = consumer;
    }

    public void reset() {
        // Reset internal state
    }

    @Override
    public void consume(FFTFrame frame) {
        // Simplified chroma calculation
        // This is a placeholder that allows compilation
        // TODO: Full implementation would compute chroma features from FFT
    }
}
