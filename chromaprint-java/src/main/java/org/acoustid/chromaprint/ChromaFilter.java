// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author Lukas Lalinsky
 * @author OpenAI (ChatGPT 5.2)
 * @author John Blum
 * @see FeatureVectorConsumer
 */
@Getter(AccessLevel.PROTECTED)
public class ChromaFilter implements FeatureVectorConsumer {

    private final double[] coefficients;

    private int bufferOffset;
    private int bufferSize;
    private final int length;

    private final FeatureVectorConsumer consumer;

    private final List<List<Double>> buffer;
    private final List<Double> result;

    public ChromaFilter(double[] coefficients, int length, FeatureVectorConsumer consumer) {
        this.coefficients = coefficients;
        this.length = length;
        this.consumer = consumer;
        this.bufferOffset = 0;
        this.bufferSize = 1;
        this.buffer = new ArrayList<>(Collections.nCopies(8, null));
        this.result = new ArrayList<>(Collections.nCopies(12, 0.0d));
    }

    @Override
    public void consume(List<Double> features) {

        int bufferOffset = getBufferOffset();
        int bufferSize = getBufferSize();
        int length = getLength();

        // Write into circular buffer
        this.buffer.set(bufferOffset, new ArrayList<>(features));
        this.bufferOffset = (bufferOffset + 1) % 8;

        if (bufferSize >= length) {
            int offset = (bufferOffset + 8 - length) % 8;

            // Zero out result
            Collections.fill(result, 0.0);

            // 12-band loop (same as C++)
            for (int i = 0; i < 12; i++) {
                double sum = 0.0;
                for (int j = 0; j < length; j++) {
                    List<Double> frame = this.buffer.get((offset + j) % 8);
                    sum += frame.get(i) * this.coefficients[j];
                }
                this.result.set(i, sum);
            }

            // Forward result to downstream consumer
            if (consumer != null) {
                consumer.consume(result);
            }

        }
        else {
            this.bufferSize = ++bufferSize;
        }
    }

    public void reset() {
        this.bufferOffset = 0;
        this.bufferSize = 1;
    }
}
