// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

import java.util.ArrayList;
import java.util.List;

public class RollingIntegralImage {
    private int maxRows;
    private int numColumns;
    private int numRows;
    private List<Double> data;
    
    public RollingIntegralImage(int maxRows) {
        this.maxRows = maxRows + 1;
        this.numColumns = 0;
        this.numRows = 0;
        this.data = new ArrayList<>();
    }
    
    public int getNumColumns() {
        return numColumns;
    }
    
    public int getNumRows() {
        return numRows;
    }
    
    public void reset() {
        data.clear();
        numRows = 0;
        numColumns = 0;
    }
    
    public double area(int r1, int c1, int r2, int c2) {
        if (r1 == r2 || c1 == c2) {
            return 0.0;
        }
        
        if (r1 == 0) {
            List<Double> row = getRow(r2 - 1);
            if (c1 == 0) {
                return row.get(c2 - 1);
            } else {
                return row.get(c2 - 1) - row.get(c1 - 1);
            }
        } else {
            List<Double> row1 = getRow(r1 - 1);
            List<Double> row2 = getRow(r2 - 1);
            if (c1 == 0) {
                return row2.get(c2 - 1) - row1.get(c2 - 1);
            } else {
                return row2.get(c2 - 1) - row1.get(c2 - 1) - row2.get(c1 - 1) + row1.get(c1 - 1);
            }
        }
    }
    
    public void addRow(List<Double> row) {
        int size = row.size();
        if (numColumns == 0) {
            numColumns = size;
            data = new ArrayList<>(maxRows * numColumns);
            for (int i = 0; i < maxRows * numColumns; i++) {
                data.add(0.0);
            }
        }
        
        List<Double> currentRow = getRow(numRows);
        double sum = 0.0;
        for (int i = 0; i < size; i++) {
            sum += row.get(i);
            currentRow.set(i, sum);
        }
        
        if (numRows > 0) {
            List<Double> lastRow = getRow(numRows - 1);
            for (int i = 0; i < numColumns; i++) {
                currentRow.set(i, currentRow.get(i) + lastRow.get(i));
            }
        }
        
        numRows++;
    }
    
    private List<Double> getRow(int i) {
        i = i % maxRows;
        int start = i * numColumns;
        int end = start + numColumns;
        return data.subList(start, end);
    }
}
