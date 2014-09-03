package org.project.common.matrix;

import Jama.EigenvalueDecomposition;
import Jama.SingularValueDecomposition;

public class EigTest {

	public static void print(double[][] values) {
		for (int i = 0, il = values.length; i < il; i++) {
			for (int j = 0, jl = values[0].length; j < jl; j++) {
				System.out.print(values[i][j] + "  ");
			}
			System.out.println("\n");
		}
	}

	public static void a() {
		double[][] l = new double[][] { new double[] { 2, -1, -1, 0, 0, 0, 0 },
				new double[] { -1, 3, -1, -1, 0, 0, 0 },
				new double[] { -1, -1, 2, 0, 0, 0, 0 },
				new double[] { 0, -1, 0, 4, -1, -1, -1 },
				new double[] { 0, 0, 0, -1, 2, -1, 0 },
				new double[] { 0, 0, 0, -1, -1, 3, -1 },
				new double[] { 0, 0, 0, -1, 0, -1, 2 }, };
		Jama.Matrix L = new Jama.Matrix(l);
		EigenvalueDecomposition eig = L.eig();
		print(eig.getD().getArray());
		System.out.println("--------");
		print(eig.getV().getArray());
		SingularValueDecomposition svd = L.svd();
		for (double sv : svd.getSingularValues()) {
			System.out.print(sv + ",");
		}
		System.out.println();
		System.out.println("-----------");
		print(svd.getS().getArray());
	}
	
	public static void b() {
		double[][] l = new double[][] { new double[] { 2, -1, -1, 0, 0, 0, 0 },
				new double[] { -1, 3, -1, -1, 0, 0, 0 },
				new double[] { -1, -1, 2, 0, 0, 0, 0 },
				new double[] { 0, -1, 0, 4, -1, -1, -1 },
				new double[] { 0, 0, 0, -1, 2, -1, 0 },
				new double[] { 0, 0, 0, -1, -1, 3, -1 },
				new double[] { 0, 0, 0, -1, 0, -1, 2 }, };
		Jama.Matrix L = new Jama.Matrix(l);
		EigenvalueDecomposition eig = L.eig();
		print(eig.getD().getArray());
		System.out.println("--------");
		double[][] v = eig.getV().getArray();
		print(v);
		System.out.println("--------");
		double[][] ev = new double[7][4];
		for (int i = 0, il = 7; i < il; i++) {
			for (int j = 1, jl = 5; j < jl; j++) {
				ev[i][j-1] = v[i][j];
			}
		}
		Jama.Matrix E = new Jama.Matrix(ev);
		Jama.Matrix V = L.times(E);
		print(V.getArray());
	}

	public static void main(String[] args) {
		b();
	}
}
