// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

public class FFTFrame {
    private double[] data;
    
    public FFTFrame(double[] data) {
        this.data = data;
    }
    
    public double[] getData() {
        return data;
    }
}
