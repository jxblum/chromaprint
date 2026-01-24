// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

import java.util.ArrayList;
import java.util.List;

public class FingerprintCalculator implements FeatureVectorConsumer {
    private Classifier[] classifiers;
    private int numClassifiers;
    private int maxFilterWidth;
    private RollingIntegralImage image;
    private List<Long> fingerprint;
    
    public FingerprintCalculator(Classifier[] classifiers, int numClassifiers) {
        this.classifiers = classifiers;
        this.numClassifiers = numClassifiers;
        this.image = new RollingIntegralImage(256);
        this.fingerprint = new ArrayList<>();
        
        maxFilterWidth = 0;
        for (int i = 0; i < numClassifiers; i++) {
            maxFilterWidth = Math.max(maxFilterWidth, classifiers[i].getFilter().getWidth());
        }
    }
    
    private long calculateSubfingerprint(int offset) {
        long bits = 0;
        for (int i = 0; i < numClassifiers; i++) {
            int classification = classifiers[i].classify(image, offset);
            bits = (bits << 2) | Utils.grayCode(classification);
        }
        return bits;
    }
    
    public void reset() {
        image.reset();
        fingerprint.clear();
    }
    
    @Override
    public void consume(List<Double> features) {
        image.addRow(features);
        if (image.getNumRows() >= maxFilterWidth) {
            fingerprint.add(calculateSubfingerprint(image.getNumRows() - maxFilterWidth));
        }
    }
    
    public List<Long> getFingerprint() {
        return new ArrayList<>(fingerprint);
    }
    
    public void clearFingerprint() {
        fingerprint.clear();
    }
}
