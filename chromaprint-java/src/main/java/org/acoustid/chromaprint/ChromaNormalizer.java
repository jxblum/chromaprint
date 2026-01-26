// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

import java.util.Collections;
import java.util.List;

/**
 * @author Lukas Lalinsky
 * @author OpenAI (ChatGPT 5.2)
 * @author John Blum
 * @see FeatureVectorConsumer
 */
public class ChromaNormalizer implements FeatureVectorConsumer {

    private static final double DEFAULT_THRESHOLD = 0.01d;

    private final FeatureVectorConsumer consumer;

    public ChromaNormalizer(FeatureVectorConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void consume(List<Double> features) {
        double euclideanNorm = euclideanNorm(features);
        List<Double> normalizedFeatures = normalizeValues(features, euclideanNorm, DEFAULT_THRESHOLD);
        this.consumer.consume(normalizedFeatures);
    }

    private double euclideanNorm(List<Double> values) {

        double squaresSum = values.stream()
            .map(this::squared)
            .reduce(Double::sum)
            .orElse(0.0d);

        return squaresSum > 0.0d
            ? Math.sqrt(squaresSum)
            : 0.0d;
    }

    private List<Double> normalizeValues(List<Double> values, double norm, double threshold) {

        return norm >= threshold
            ? values.stream().map(value -> value / norm).toList()
            : Collections.nCopies(values.size(), 0.0d);
    }

    private double squared(double value) {
        return value * value;
    }

    public void reset() {
        // Reset internal state
    }
}
