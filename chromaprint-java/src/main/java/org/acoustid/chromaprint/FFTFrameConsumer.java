// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

public interface FFTFrameConsumer {
    void consume(FFTFrame frame);
}
