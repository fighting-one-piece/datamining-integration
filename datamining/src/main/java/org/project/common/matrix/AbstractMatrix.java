package org.project.common.matrix;

import java.util.Map;

public class AbstractMatrix implements Matrix {

	protected Map<String, Integer> columnLabelBindings;
	protected Map<String, Integer> rowLabelBindings;
	protected int rows;
	protected int columns;

	protected AbstractMatrix(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
	}
	
	@Override
	public int getRows() {
		return this.rows;
	}
	
	@Override
	public int getColumns() {
		return this.columns;
	}
}
