// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

import lombok.Getter;

/**
 * Implementation of {@literal Fast Fourier Transform}.
 *
 * @author Lukas Lalinsky
 * @author Cursor AI
 * @author John Blum
 * @see AudioConsumer
 */
@Getter
public class FFT implements AudioConsumer {

    private final int frameSize;
    private final int frameOverlap;

    private final FFTFrameConsumer consumer;

    public FFT(int frameSize, int frameOverlap, FFTFrameConsumer consumer) {
        this.frameSize = frameSize;
        this.frameOverlap = frameOverlap;
        this.consumer = consumer;
    }

    @Override
    public void consume(short[] input, int length) {
        // Simplified FFT implementation
        // This is a placeholder that allows compilation
        // TODO: Full implementation would use JTransforms or similar library
    }

    public void reset() {
        // Reset internal state
    }
}
