// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint.utils;

import java.util.List;

public class Gradient {
    
    public static void gradient(List<Float> input, List<Float> output) {
        int size = input.size();
        output.clear();
        for (int i = 0; i < size; i++) {
            output.add(0.0f);
        }
        
        if (size == 0) {
            return;
        }
        
        if (size == 1) {
            output.set(0, 0.0f);
            return;
        }
        
        float f0 = input.get(0);
        float f1 = input.get(1);
        output.set(0, f1 - f0);
        
        if (size == 2) {
            output.set(1, f1 - f0);
            return;
        }
        
        float f2 = input.get(2);
        int outIdx = 1;
        
        while (true) {
            output.set(outIdx++, (f2 - f0) / 2.0f);
            if (outIdx >= size - 1) {
                break;
            }
            f0 = f1;
            f1 = f2;
            f2 = input.get(outIdx + 1);
        }
        
        output.set(size - 1, f2 - f1);
    }
}
