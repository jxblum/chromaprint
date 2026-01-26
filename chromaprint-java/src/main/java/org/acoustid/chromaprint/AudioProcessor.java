// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

/**
 * @author Lukas Lalinsky
 * @author Cursor AI
 * @author John Blum
 * @see AudioConsumer
 */
public class AudioProcessor implements AudioConsumer {

    private int sampleRate;

    private final AudioConsumer consumer;

    public AudioProcessor(int sampleRate, AudioConsumer consumer) {
        this.sampleRate = sampleRate;
        this.consumer = consumer;
    }

    @Override
    public void consume(short[] input, int length) {
        // TODO: Process audio and forward to consumer
        this.consumer.consume(input, length);
    }

    public void flush() {
        // Flush any remaining buffered audio
    }

    public boolean reset(int sampleRate, int numChannels) {
        this.sampleRate = sampleRate;
        return true;
    }
}
