package org.project.common.matrix;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

public class MatrixTest {

	public static void main(String[] args) throws Exception {
		int M = 8, N = 5;
		Matrix B = Matrix.random(5, 3);
		Matrix A = Matrix.random(M, N).times(B).times(B.transpose());
		System.out.print("A = ");
		A.print(9, 6);

		// compute the singular vallue decomposition
		System.out.println("A = U S V^T");
		System.out.println();
		SingularValueDecomposition s = A.svd();
		System.out.print("U = ");
		Matrix U = s.getU();
		U.print(9, 6);
		System.out.print("Sigma = ");
		Matrix S = s.getS();
		S.print(9, 6);
		System.out.print("V = ");
		Matrix V = s.getV();
		V.print(9, 6);
		System.out.println("rank = " + s.rank());
		System.out.println("condition number = " + s.cond());
		System.out.println("2-norm = " + s.norm2());

		// print out singular values
		System.out.print("singular values = ");
		Matrix svalues = new Matrix(s.getSingularValues(), 1);
		svalues.print(9, 6);
		
	}
}
