// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Lukas Lalinsky
 * @author Cursor AI
 * @author John Blum
 */
@Getter
@Setter
public class FingerprinterConfiguration {

    // Half of MP3 Sample Rate (22,050)
    private static final int DEFAULT_SAMPLE_RATE = 11025;

    protected static final double[] CHROMA_FILTER_COEFFICIENTS = { 0.25, 0.75, 1.0, 0.75, 0.25 };

    protected static final int CHROMA_FILTER_SIZE = 5;
    protected static final int DEFAULT_FRAME_SIZE = 4096;
    protected static final int DEFAULT_FRAME_OVERLAP = DEFAULT_FRAME_SIZE - DEFAULT_FRAME_SIZE / 3;

    protected boolean interpolate;
    protected boolean removeSilence;

    protected double[] filterCoefficients;

    protected int frameSize;
    protected int frameOverlap;
    protected int numClassifiers;
    protected int numFilterCoefficients;
    protected int maxFilterWidth;
    protected int silenceThreshold;

    protected Classifier[] classifiers;

    public FingerprinterConfiguration() {
        this.numClassifiers = 0;
        this.classifiers = null;
        this.removeSilence = false;
        this.silenceThreshold = 0;
        this.frameSize = 0;
        this.frameOverlap = 0;
        this.interpolate = false;
    }

    public void setFilterCoefficients(double[] filterCoefficients, int size) {
        this.filterCoefficients = filterCoefficients;
        this.numFilterCoefficients = size;
    }

    public void setClassifiers(Classifier[] classifiers, int size) {
        this.classifiers = classifiers;
        this.numClassifiers = size;
        maxFilterWidth = 0;
        for (int i = 0; i < size; i++) {
            maxFilterWidth = Math.max(maxFilterWidth, classifiers[i].getFilter().getWidth());
        }
    }

    public int getDelay() {
        return ((this.numFilterCoefficients - 1) + (this.maxFilterWidth - 1)) * getItemDuration() + this.frameOverlap;
    }

    public double getDelayInSeconds() {
        return getDelay() * 1.0 / getSampleRate();
    }

    public int getItemDuration() {
        return this.frameSize - this.frameOverlap;
    }

    public double getItemDurationInSeconds() {
        return getItemDuration() * 1.0 / getSampleRate();
    }

    public int getSampleRate() {
        return DEFAULT_SAMPLE_RATE;
    }
}
