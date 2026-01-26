// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

import org.cp.elements.lang.Assert;

import lombok.Getter;

/**
 * Component used to perform a calculation that constrain a {@link Double numerical value}
 * to within a pre-defined range of {@link Integer integral values}.
 *
 * @author Lukas Lalinsky
 * @author Cursor AI
 * @author John Blum
 */
@Getter
public class Quantizer {

    private final double t0;
    private final double t1;
    private final double t2;

    public Quantizer(double t0, double t1, double t2) {
        Assert.isTrue(t0 > t1 || t1 > t2, "Thresholds must be in order: t0 <= t1 <= t2");
        this.t0 = t0;
        this.t1 = t1;
        this.t2 = t2;
    }

    public int quantize(double value) {

        if (value < getT0()) {
            return 0;
        }
        else if (value < getT1()) {
            return 1;
        }
        else if (value < getT2()) {
            return 2;
        }
        else {
            return 3;
        }
    }
}
