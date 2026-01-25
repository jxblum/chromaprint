// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

public class Classifier {
    private Filter filter;
    private Quantizer quantizer;
    
    public Classifier(Filter filter, Quantizer quantizer) {
        this.filter = filter;
        this.quantizer = quantizer;
    }
    
    public Classifier() {
        this(new Filter(), new Quantizer());
    }
    
    public int classify(RollingIntegralImage image, int offset) {
        double value = filter.apply(image, offset);
        return quantizer.quantize(value);
    }
    
    public Filter getFilter() {
        return filter;
    }
    
    public Quantizer getQuantizer() {
        return quantizer;
    }
}
