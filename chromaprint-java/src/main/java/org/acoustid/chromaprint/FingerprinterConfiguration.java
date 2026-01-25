// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

public class FingerprinterConfiguration {
    private static final int DEFAULT_SAMPLE_RATE = 11025;
    
    protected int numClassifiers;
    protected int maxFilterWidth;
    protected Classifier[] classifiers;
    protected int numFilterCoefficients;
    protected double[] filterCoefficients;
    protected boolean interpolate;
    protected boolean removeSilence;
    protected int silenceThreshold;
    protected int frameSize;
    protected int frameOverlap;
    
    public FingerprinterConfiguration() {
        this.numClassifiers = 0;
        this.classifiers = null;
        this.removeSilence = false;
        this.silenceThreshold = 0;
        this.frameSize = 0;
        this.frameOverlap = 0;
        this.interpolate = false;
    }
    
    public int getNumFilterCoefficients() {
        return numFilterCoefficients;
    }
    
    public double[] getFilterCoefficients() {
        return filterCoefficients;
    }
    
    public void setFilterCoefficients(double[] filterCoefficients, int size) {
        this.filterCoefficients = filterCoefficients;
        this.numFilterCoefficients = size;
    }
    
    public int getNumClassifiers() {
        return numClassifiers;
    }
    
    public Classifier[] getClassifiers() {
        return classifiers;
    }
    
    public int getMaxFilterWidth() {
        return maxFilterWidth;
    }
    
    public void setClassifiers(Classifier[] classifiers, int size) {
        this.classifiers = classifiers;
        this.numClassifiers = size;
        maxFilterWidth = 0;
        for (int i = 0; i < size; i++) {
            maxFilterWidth = Math.max(maxFilterWidth, classifiers[i].getFilter().getWidth());
        }
    }
    
    public boolean isInterpolate() {
        return interpolate;
    }
    
    public void setInterpolate(boolean value) {
        this.interpolate = value;
    }
    
    public boolean isRemoveSilence() {
        return removeSilence;
    }
    
    public void setRemoveSilence(boolean value) {
        this.removeSilence = value;
    }
    
    public int getSilenceThreshold() {
        return silenceThreshold;
    }
    
    public void setSilenceThreshold(int value) {
        this.silenceThreshold = value;
    }
    
    public int getFrameSize() {
        return frameSize;
    }
    
    public void setFrameSize(int value) {
        this.frameSize = value;
    }
    
    public int getFrameOverlap() {
        return frameOverlap;
    }
    
    public void setFrameOverlap(int value) {
        this.frameOverlap = value;
    }
    
    public int getSampleRate() {
        return DEFAULT_SAMPLE_RATE;
    }
    
    public int getItemDuration() {
        return frameSize - frameOverlap;
    }
    
    public double getItemDurationInSeconds() {
        return getItemDuration() * 1.0 / getSampleRate();
    }
    
    public int getDelay() {
        return ((numFilterCoefficients - 1) + (maxFilterWidth - 1)) * getItemDuration() + frameOverlap;
    }
    
    public double getDelayInSeconds() {
        return getDelay() * 1.0 / getSampleRate();
    }
}
