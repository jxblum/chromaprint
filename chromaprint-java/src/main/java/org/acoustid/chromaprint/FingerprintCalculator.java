// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Calculator used to calculate the fingerprint of a digital signal.
 *
 * @author Lukas Lalinsky
 * @author Cursor AI
 * @author John Blum
 */
@Getter
public class FingerprintCalculator implements FeatureVectorConsumer {

    private final int maxFilterWidth;
    private final int numClassifiers;

    private final Classifier[] classifiers;

    private final List<Long> fingerprint;

    private final RollingIntegralImage image;

    public FingerprintCalculator(Classifier[] classifiers, int numClassifiers) {

        this.classifiers = classifiers;
         this.numClassifiers = Math.min(Math.max(numClassifiers, 0), classifiers.length);
        this.fingerprint = new ArrayList<>();
        this.image = new RollingIntegralImage(256);

        int maxFilterWidth = 0;

        for (int index = 0; index < numClassifiers; index++) {
            int classifierFilterWidth = classifiers[index].getFilter().getWidth();
            maxFilterWidth = Math.max(maxFilterWidth, classifierFilterWidth);
        }

        this.maxFilterWidth = maxFilterWidth;
    }

    public List<Long> getFingerprint() {
        return new ArrayList<>(this.fingerprint);
    }

    @Override
    public void consume(List<Double> features) {

        int maxFilterWidth = getMaxFilterWidth();
        RollingIntegralImage image = getImage();

        image.addRow(features);

        if (image.getNumRows() >= maxFilterWidth) {
            this.fingerprint.add(calculateSubfingerprint(image.getNumRows() - maxFilterWidth));
        }
    }

    private long calculateSubfingerprint(int offset) {

        Classifier[] classifiers = getClassifiers();
        RollingIntegralImage image = getImage();

        long bits = 0;

        for (int index = 0, size = getNumClassifiers(); index < size; index++) {
            int classification = classifiers[index].classify(image, offset);
            bits = (bits << 2) | Utils.grayCode(classification);
        }

        return bits;
    }

    public void clearFingerprint() {
        this.fingerprint.clear();
    }

    public void reset() {
        getImage().reset();
        clearFingerprint();
    }
}
