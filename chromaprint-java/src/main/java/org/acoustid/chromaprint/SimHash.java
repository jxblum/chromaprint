// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

import java.util.List;

public class SimHash {
    public static long simHash(long[] data) {
        return simHash(data, data.length);
    }
    
    public static long simHash(long[] data, int size) {
        int[] v = new int[32];
        
        for (int i = 0; i < size; i++) {
            long localHash = data[i];
            for (int j = 0; j < 32; j++) {
                v[j] += (int)((localHash >> j) & 1);
            }
        }
        
        int threshold = size / 2;
        long hash = 0;
        for (int i = 0; i < 32; i++) {
            int b = v[i] > threshold ? 1 : 0;
            hash |= ((long)b << i);
        }
        
        return hash;
    }
    
    public static long simHash(List<Long> data) {
        if (data.isEmpty()) {
            return 0;
        }
        long[] array = new long[data.size()];
        for (int i = 0; i < data.size(); i++) {
            array[i] = data.get(i);
        }
        return simHash(array);
    }
}
