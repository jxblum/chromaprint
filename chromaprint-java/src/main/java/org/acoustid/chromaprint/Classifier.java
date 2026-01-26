// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

import lombok.Getter;

/**
 * @author Lukas Lalinsky
 * @author Cursor AI
 * @author John Blum
 */
@Getter
public class Classifier {

    private final Filter filter;
    private final Quantizer quantizer;

    public Classifier(Filter filter, Quantizer quantizer) {
        this.filter = filter;
        this.quantizer = quantizer;
    }

    public int classify(RollingIntegralImage image, int offset) {
        double value = getFilter().apply(image, offset);
        return getQuantizer().quantize(value);
    }
}
