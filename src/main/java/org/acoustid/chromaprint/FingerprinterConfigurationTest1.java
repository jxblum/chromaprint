// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

public class FingerprinterConfigurationTest1 extends FingerprinterConfiguration {
    private static final int DEFAULT_FRAME_SIZE = 4096;
    private static final int DEFAULT_FRAME_OVERLAP = DEFAULT_FRAME_SIZE - DEFAULT_FRAME_SIZE / 3;
    private static final int CHROMA_FILTER_SIZE = 5;
    private static final double[] CHROMA_FILTER_COEFFICIENTS = {0.25, 0.75, 1.0, 0.75, 0.25};
    
    private static final Classifier[] CLASSIFIERS = {
        new Classifier(new Filter(0, 0, 3, 15), new Quantizer(2.10543, 2.45354, 2.69414)),
        new Classifier(new Filter(1, 0, 4, 14), new Quantizer(-0.345922, 0.0463746, 0.446251)),
        new Classifier(new Filter(1, 4, 4, 11), new Quantizer(-0.392132, 0.0291077, 0.443391)),
        new Classifier(new Filter(3, 0, 4, 14), new Quantizer(-0.192851, 0.00583535, 0.204053)),
        new Classifier(new Filter(2, 8, 2, 4), new Quantizer(-0.0771619, -0.00991999, 0.0575406)),
        new Classifier(new Filter(5, 6, 2, 15), new Quantizer(-0.710437, -0.518954, -0.330402)),
        new Classifier(new Filter(1, 9, 2, 16), new Quantizer(-0.353724, -0.0189719, 0.289768)),
        new Classifier(new Filter(3, 4, 2, 10), new Quantizer(-0.128418, -0.0285697, 0.0591791)),
        new Classifier(new Filter(3, 9, 2, 16), new Quantizer(-0.139052, -0.0228468, 0.0879723)),
        new Classifier(new Filter(2, 1, 3, 6), new Quantizer(-0.133562, 0.00669205, 0.155012)),
        new Classifier(new Filter(3, 3, 6, 2), new Quantizer(-0.0267, 0.00804829, 0.0459773)),
        new Classifier(new Filter(2, 8, 1, 10), new Quantizer(-0.0972417, 0.0152227, 0.129003)),
        new Classifier(new Filter(3, 4, 4, 14), new Quantizer(-0.141434, 0.00374515, 0.149935)),
        new Classifier(new Filter(5, 4, 2, 15), new Quantizer(-0.64035, -0.466999, -0.285493)),
        new Classifier(new Filter(5, 9, 2, 3), new Quantizer(-0.322792, -0.254258, -0.174278)),
        new Classifier(new Filter(2, 1, 8, 4), new Quantizer(-0.0741375, -0.00590933, 0.0600357))
    };
    
    public FingerprinterConfigurationTest1() {
        setClassifiers(CLASSIFIERS, CLASSIFIERS.length);
        setFilterCoefficients(CHROMA_FILTER_COEFFICIENTS, CHROMA_FILTER_SIZE);
        setInterpolate(false);
        setFrameSize(DEFAULT_FRAME_SIZE);
        setFrameOverlap(DEFAULT_FRAME_OVERLAP);
    }
}
