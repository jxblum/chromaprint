// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

public class Quantizer {
    private double t0;
    private double t1;
    private double t2;
    
    public Quantizer(double t0, double t1, double t2) {
        if (t0 > t1 || t1 > t2) {
            throw new IllegalArgumentException("Thresholds must be in order: t0 <= t1 <= t2");
        }
        this.t0 = t0;
        this.t1 = t1;
        this.t2 = t2;
    }
    
    public Quantizer() {
        this(0.0, 0.0, 0.0);
    }
    
    public int quantize(double value) {
        if (value < t1) {
            if (value < t0) {
                return 0;
            }
            return 1;
        } else {
            if (value < t2) {
                return 2;
            }
            return 3;
        }
    }
    
    public double getT0() {
        return t0;
    }
    
    public void setT0(double t0) {
        this.t0 = t0;
    }
    
    public double getT1() {
        return t1;
    }
    
    public void setT1(double t1) {
        this.t1 = t1;
    }
    
    public double getT2() {
        return t2;
    }
    
    public void setT2(double t2) {
        this.t2 = t2;
    }
}
