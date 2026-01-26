// Copyright (C) 2010-2016  Lukas Lalinsky
// Distributed under the MIT license, see the LICENSE file for details.
package org.acoustid.chromaprint;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * @author Lukas Lalinsky
 * @author Cursor AI
 * @author John Blum
 */
@Getter
public class RollingIntegralImage {

	private final int maxRows;

	private int numColumns;
	private int numRows;

	private List<Double> data;

	public RollingIntegralImage(int maxRows) {
		this.maxRows = maxRows + 1;
		this.numColumns = 0;
		this.numRows = 0;
		this.data = new ArrayList<>();
	}

	private List<Double> getRow(int index) {
		index = index % getMaxRows();
		int start = index * getNumColumns();
		int end = start + getNumColumns();
		return this.data.subList(start, end);
	}

	public double area(int rowOne, int columnOne, int rowTwo, int columnTwo) {

		if (rowOne == rowTwo || columnOne == columnTwo) {
			return 0.0;
		}

		if (rowOne == 0) {
			List<Double> row = getRow(rowTwo - 1);
			if (columnOne == 0) {
				return row.get(columnTwo - 1);
			}
			else {
				return row.get(columnTwo - 1) - row.get(columnOne - 1);
			}
		}
		else {
			List<Double> row1 = getRow(rowOne - 1);
			List<Double> row2 = getRow(rowTwo - 1);
			if (columnOne == 0) {
				return row2.get(columnTwo - 1) - row1.get(columnTwo - 1);
			}
			else {
				return row2.get(columnTwo - 1) - row1.get(columnTwo - 1)
					- row2.get(columnOne - 1) + row1.get(columnOne - 1);
			}
		}
	}

	public void addRow(List<Double> row) {

		int rowSize = row.size();

		if (this.numColumns == 0) {
			this.numColumns = rowSize;
			int dataSize = this.maxRows * this.numColumns;
			this.data = new ArrayList<>(dataSize);
			for (int count = 0; count < dataSize; count++) {
				this.data.add(0.0);
			}
		}

		List<Double> currentRow = getRow(getNumRows());
		double sum = 0.0;

		for (int index = 0; index < rowSize; index++) {
			sum += row.get(index);
			currentRow.set(index, sum);
		}

		if (getNumRows() > 0) {
			List<Double> lastRow = getRow(getNumRows() - 1);
			for (int index = 0; index < getNumColumns(); index++) {
				currentRow.set(index, currentRow.get(index) + lastRow.get(index));
			}
		}

		this.numRows++;
	}

	public void reset() {
		this.data.clear();
		this.numRows = 0;
		this.numColumns = 0;
	}
}
