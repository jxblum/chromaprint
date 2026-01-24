// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

public class Filter {
    private int type;
    private int y;
    private int height;
    private int width;
    
    public Filter(int type, int y, int height, int width) {
        this.type = type;
        this.y = y;
        this.height = height;
        this.width = width;
    }
    
    public Filter() {
        this(0, 0, 0, 0);
    }
    
    public double apply(RollingIntegralImage image, int x) {
        // This is a simplified version - the actual implementation uses FilterUtils
        // which has complex filter operations. For a complete conversion, 
        // FilterUtils.java would need to be implemented.
        switch (type) {
            case 0:
                return FilterUtils.filter0(image, x, y, width, height);
            case 1:
                return FilterUtils.filter1(image, x, y, width, height);
            case 2:
                return FilterUtils.filter2(image, x, y, width, height);
            case 3:
                return FilterUtils.filter3(image, x, y, width, height);
            case 4:
                return FilterUtils.filter4(image, x, y, width, height);
            case 5:
                return FilterUtils.filter5(image, x, y, width, height);
            default:
                return 0.0;
        }
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
}
