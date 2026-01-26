// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

import java.util.ArrayList;
import java.util.List;

public class ChromaFilter implements FeatureVectorConsumer {

    private final double[] filterCoefficients;
    private final int numFilterCoefficients;

    private final FeatureVectorConsumer consumer;

    public ChromaFilter(double[] filterCoefficients, int numFilterCoefficients, FeatureVectorConsumer consumer) {
        this.filterCoefficients = filterCoefficients;
        this.numFilterCoefficients = numFilterCoefficients;
        this.consumer = consumer;
    }

    @Override
    public void consume(List<Double> features) {
        // Apply filter to features
        List<Double> filtered = new ArrayList<>(features);
        // TODO: Simplified filtering - full implementation would apply convolution
        this.consumer.consume(filtered);
    }

    public void reset() {
        // Reset internal state
    }
}
