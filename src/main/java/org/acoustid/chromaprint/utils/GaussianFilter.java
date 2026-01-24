// Copyright (C) 2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint.utils;

import java.util.ArrayList;
import java.util.List;

public class GaussianFilter {
    
    private static class ReflectIterator {
        private int size;
        private int pos;
        private boolean forward;
        
        ReflectIterator(int size) {
            this.size = size;
            this.pos = 0;
            this.forward = true;
        }
        
        void moveForward() {
            if (forward) {
                if (pos + 1 == size) {
                    forward = false;
                } else {
                    pos++;
                }
            } else {
                if (pos == 0) {
                    forward = true;
                } else {
                    pos--;
                }
            }
        }
        
        void moveBack() {
            if (forward) {
                if (pos == 0) {
                    forward = false;
                } else {
                    pos--;
                }
            } else {
                if (pos + 1 == size) {
                    forward = true;
                } else {
                    pos++;
                }
            }
        }
        
        int safeForwardDistance() {
            if (forward) {
                return size - pos - 1;
            }
            return 0;
        }
    }
    
    private static void boxFilter(List<Float> input, List<Float> output, int w) {
        int size = input.size();
        output.clear();
        for (int i = 0; i < size; i++) {
            output.add(0.0f);
        }
        
        if (w == 0 || size == 0) {
            return;
        }
        
        int wl = w / 2;
        int wr = w - wl;
        
        ReflectIterator it1 = new ReflectIterator(size);
        ReflectIterator it2 = new ReflectIterator(size);
        for (int i = 0; i < wl; i++) {
            it1.moveBack();
            it2.moveBack();
        }
        
        float sum = 0.0f;
        for (int i = 0; i < w; i++) {
            sum += input.get(it2.pos);
            it2.moveForward();
        }
        
        int outIdx = 0;
        if (size > w) {
            for (int i = 0; i < wl; i++) {
                output.set(outIdx++, sum / w);
                sum += input.get(it2.pos) - input.get(it1.pos);
                it1.moveForward();
                it2.moveForward();
            }
            // Middle section uses direct position increment (not reflect iterator)
            for (int i = 0; i < size - w - 1; i++) {
                output.set(outIdx++, sum / w);
                sum += input.get(it2.pos) - input.get(it1.pos);
                it1.pos++;
                it2.pos++;
            }
            for (int i = 0; i < wr + 1; i++) {
                output.set(outIdx++, sum / w);
                sum += input.get(it2.pos) - input.get(it1.pos);
                it1.moveForward();
                it2.moveForward();
            }
        } else {
            for (int i = 0; i < size; i++) {
                output.set(outIdx++, sum / w);
                sum += input.get(it2.pos) - input.get(it1.pos);
                it1.moveForward();
                it2.moveForward();
            }
        }
    }
    
    public static void gaussianFilter(List<Float> input, List<Float> output, double sigma, int n) {
        int w = (int)Math.floor(Math.sqrt(12 * sigma * sigma / n + 1));
        int wl = w - (w % 2 == 0 ? 1 : 0);
        int wu = wl + 2;
        
        int m = (int)Math.round((12 * sigma * sigma - n * wl * wl - 4 * n * wl - 3 * n) / (-4.0 * wl - 4));
        
        List<Float> data1 = new ArrayList<>(input);
        List<Float> data2 = new ArrayList<>();
        
        int i = 0;
        for (; i < m; i++) {
            boxFilter(data1, data2, wl);
            List<Float> temp = data1;
            data1 = data2;
            data2 = temp;
        }
        for (; i < n; i++) {
            boxFilter(data1, data2, wu);
            List<Float> temp = data1;
            data1 = data2;
            data2 = temp;
        }
        
        if (data1 != output) {
            output.clear();
            output.addAll(data1);
        }
    }
}
