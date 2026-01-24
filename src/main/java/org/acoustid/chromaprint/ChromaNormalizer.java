// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

import java.util.ArrayList;
import java.util.List;

public class ChromaNormalizer implements FeatureVectorConsumer {
    private FeatureVectorConsumer consumer;
    
    public ChromaNormalizer(FeatureVectorConsumer consumer) {
        this.consumer = consumer;
    }
    
    public void reset() {
        // Reset internal state
    }
    
    @Override
    public void consume(List<Double> features) {
        // Normalize features
        List<Double> normalized = new ArrayList<>(features);
        // Simplified normalization - full implementation would normalize vector
        consumer.consume(normalized);
    }
}
