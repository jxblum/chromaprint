// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

import java.util.List;

public interface FeatureVectorConsumer {
    void consume(List<Double> features);
}
