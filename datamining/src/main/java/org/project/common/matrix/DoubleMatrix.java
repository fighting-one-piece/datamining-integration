package org.project.common.matrix;

public class DoubleMatrix extends AbstractMatrix {

	private double[][] values = null;
	
	protected DoubleMatrix(int rows, int columns) {
		super(rows, columns);
	}

	public double[][] getValues() {
		return values;
	}

	public void setValues(double[][] values) {
		this.values = values;
	}
	
	

}
