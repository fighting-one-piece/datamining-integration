package org.project.modules.clustering.spectral.data;

public class Data {

	private String[] row = null;
	
	private String[] column = null;
	
	private double[][] values = null;
	
	private double[][] original = null;

	public String[] getRow() {
		return row;
	}

	public void setRow(String[] row) {
		this.row = row;
	}

	public String[] getColumn() {
		return column;
	}

	public void setColumn(String[] column) {
		this.column = column;
	}

	public double[][] getValues() {
		return values;
	}

	public void setValues(double[][] values) {
		this.values = values;
	}
	
	
	public double[][] getOriginal() {
		return original;
	}

	public void setOriginal(double[][] original) {
		this.original = original;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, il = row.length; i < il; i++) {
			for (int j = 0, jl = column.length; j < jl; j++) {
				sb.append(values[i][j] + "  ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
}
