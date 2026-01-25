// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.

package org.acoustid.chromaprint;

public class FilterUtils {
    
    private static double subtractLog(double a, double b) {
        if (a < b) {
            return 0.0;
        }
        return Math.log(1.0 + a - b);
    }
    
    public static double filter0(RollingIntegralImage image, int x, int y, int width, int height) {
        double area = image.area(y, x, y + height, x + width);
        return subtractLog(area, 0.0);
    }
    
    public static double filter1(RollingIntegralImage image, int x, int y, int width, int height) {
        double area1 = image.area(y, x, y + height / 2, x + width);
        double area2 = image.area(y + height / 2, x, y + height, x + width);
        return subtractLog(area1, area2);
    }
    
    public static double filter2(RollingIntegralImage image, int x, int y, int width, int height) {
        double area1 = image.area(y, x, y + height, x + width / 2);
        double area2 = image.area(y, x + width / 2, y + height, x + width);
        return subtractLog(area1, area2);
    }
    
    public static double filter3(RollingIntegralImage image, int x, int y, int width, int height) {
        double area1 = image.area(y, x, y + height / 2, x + width / 2);
        double area2 = image.area(y, x + width / 2, y + height / 2, x + width);
        double area3 = image.area(y + height / 2, x, y + height, x + width / 2);
        double area4 = image.area(y + height / 2, x + width / 2, y + height, x + width);
        return subtractLog(area1 + area4, area2 + area3);
    }
    
    public static double filter4(RollingIntegralImage image, int x, int y, int width, int height) {
        double area1 = image.area(y, x, y + height / 3, x + width);
        double area2 = image.area(y + height / 3, x, y + 2 * height / 3, x + width);
        double area3 = image.area(y + 2 * height / 3, x, y + height, x + width);
        return subtractLog(area1, area2) + subtractLog(area2, area3);
    }
    
    public static double filter5(RollingIntegralImage image, int x, int y, int width, int height) {
        double area1 = image.area(y, x, y + height, x + width / 3);
        double area2 = image.area(y, x + width / 3, y + height, x + 2 * width / 3);
        double area3 = image.area(y, x + 2 * width / 3, y + height, x + width);
        return subtractLog(area1, area2) + subtractLog(area2, area3);
    }
}
