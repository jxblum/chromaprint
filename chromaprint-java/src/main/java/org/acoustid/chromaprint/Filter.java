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
public class Filter {

    private final int type;
    private final int width;
    private final int height;
    private final int y;

    public Filter(int type, int width, int height, int y) {
        this.type = type;
		this.width = width;
		this.height = height;
        this.y = y;
    }

    public double apply(RollingIntegralImage image, int x) {

        // This is a simplified version - the actual implementation uses FilterUtils
        // which has complex filter operations.

		return switch (getType()) {
			case 0 -> FilterUtils.filter0(image, x, getY(), getWidth(), getHeight());
			case 1 -> FilterUtils.filter1(image, x, getY(), getWidth(), getHeight());
			case 2 -> FilterUtils.filter2(image, x, getY(), getWidth(), getHeight());
			case 3 -> FilterUtils.filter3(image, x, getY(), getWidth(), getHeight());
			case 4 -> FilterUtils.filter4(image, x, getY(), getWidth(), getHeight());
			case 5 -> FilterUtils.filter5(image, x, getY(), getWidth(), getHeight());
			default -> 0.0;
		};
    }
}
