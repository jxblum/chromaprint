// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

public class FFT implements AudioConsumer {
    private int frameSize;
    private int frameOverlap;
    private FFTFrameConsumer consumer;

    public FFT(int frameSize, int frameOverlap, FFTFrameConsumer consumer) {
        this.frameSize = frameSize;
        this.frameOverlap = frameOverlap;
        this.consumer = consumer;
    }

    public void reset() {
        // Reset internal state
    }

    @Override
    public void consume(short[] input, int length) {
        // Simplified FFT implementation
        // This is a placeholder that allows compilation
        // TODO: Full implementation would use JTransforms or similar library
    }
}
