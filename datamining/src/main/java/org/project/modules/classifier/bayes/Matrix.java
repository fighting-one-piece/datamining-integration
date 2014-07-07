package org.project.modules.classifier.bayes;

import java.util.Arrays;

public class Matrix<T> {

	private int row = 0;

	private int column = 0;

	private T values[][] = null;

	public Matrix() {

	}

	public Matrix(T[][] values) {
		this.row = values.length;
		this.column = values[0].length;
		this.values = values;
	}

	public Matrix(int row, int column, T[][] values) {
		this.row = row;
		this.column = column;
		this.values = Arrays.copyOf(values, values.length);
	}

	public void print() {
		System.out.println("matrix row: " + row + " column: " + column);
		for (T[] row : values) {
			System.out.print("|");
			for (T column : row) {
				System.out.print(" " + column + " ");
			}
			System.out.println("|");
		}
	}
	
}
