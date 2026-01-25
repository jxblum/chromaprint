// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

public class AudioProcessor implements AudioConsumer {
    private int sampleRate;
    private AudioConsumer consumer;
    
    public AudioProcessor(int sampleRate, AudioConsumer consumer) {
        this.sampleRate = sampleRate;
        this.consumer = consumer;
    }
    
    public boolean reset(int sampleRate, int numChannels) {
        this.sampleRate = sampleRate;
        // Reset internal state
        return true;
    }
    
    @Override
    public void consume(short[] input, int length) {
        // Process audio and forward to consumer
        consumer.consume(input, length);
    }
    
    public void flush() {
        // Flush any remaining buffered audio
    }
}
