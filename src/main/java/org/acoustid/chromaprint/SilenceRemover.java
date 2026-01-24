// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

public class SilenceRemover implements AudioConsumer {
    private AudioConsumer consumer;
    private int threshold;
    
    public SilenceRemover(AudioConsumer consumer) {
        this.consumer = consumer;
        this.threshold = 0;
    }
    
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
    
    @Override
    public void consume(short[] input, int length) {
        // Remove silence from audio
        // Simplified implementation - full version would detect and remove silence
        consumer.consume(input, length);
    }
}
