// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

public class FingerprinterConfigurationTest2 extends FingerprinterConfiguration {
    private static final int DEFAULT_FRAME_SIZE = 4096;
    private static final int DEFAULT_FRAME_OVERLAP = DEFAULT_FRAME_SIZE - DEFAULT_FRAME_SIZE / 3;
    private static final int CHROMA_FILTER_SIZE = 5;
    private static final double[] CHROMA_FILTER_COEFFICIENTS = {0.25, 0.75, 1.0, 0.75, 0.25};
    
    private static final Classifier[] CLASSIFIERS = {
        new Classifier(new Filter(0, 4, 3, 15), new Quantizer(1.98215, 2.35817, 2.63523)),
        new Classifier(new Filter(4, 4, 6, 15), new Quantizer(-1.03809, -0.651211, -0.282167)),
        new Classifier(new Filter(1, 0, 4, 16), new Quantizer(-0.298702, 0.119262, 0.558497)),
        new Classifier(new Filter(3, 8, 2, 12), new Quantizer(-0.105439, 0.0153946, 0.135898)),
        new Classifier(new Filter(3, 4, 4, 8), new Quantizer(-0.142891, 0.0258736, 0.200632)),
        new Classifier(new Filter(4, 0, 3, 5), new Quantizer(-0.826319, -0.590612, -0.368214)),
        new Classifier(new Filter(1, 2, 2, 9), new Quantizer(-0.557409, -0.233035, 0.0534525)),
        new Classifier(new Filter(2, 7, 3, 4), new Quantizer(-0.0646826, 0.00620476, 0.0784847)),
        new Classifier(new Filter(2, 6, 2, 16), new Quantizer(-0.192387, -0.029699, 0.215855)),
        new Classifier(new Filter(2, 1, 3, 2), new Quantizer(-0.0397818, -0.00568076, 0.0292026)),
        new Classifier(new Filter(5, 10, 1, 15), new Quantizer(-0.53823, -0.369934, -0.190235)),
        new Classifier(new Filter(3, 6, 2, 10), new Quantizer(-0.124877, 0.0296483, 0.139239)),
        new Classifier(new Filter(2, 1, 1, 14), new Quantizer(-0.101475, 0.0225617, 0.231971)),
        new Classifier(new Filter(3, 5, 6, 4), new Quantizer(-0.0799915, -0.00729616, 0.063262)),
        new Classifier(new Filter(1, 9, 2, 12), new Quantizer(-0.272556, 0.019424, 0.302559)),
        new Classifier(new Filter(3, 4, 2, 14), new Quantizer(-0.164292, -0.0321188, 0.0846339))
    };
    
    public FingerprinterConfigurationTest2() {
        setClassifiers(CLASSIFIERS, CLASSIFIERS.length);
        setFilterCoefficients(CHROMA_FILTER_COEFFICIENTS, CHROMA_FILTER_SIZE);
        setInterpolate(false);
        setFrameSize(DEFAULT_FRAME_SIZE);
        setFrameOverlap(DEFAULT_FRAME_OVERLAP);
    }
}
