package org.project.modules.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDF;

public class AddFunction extends UDF {

	public int evaluate(Integer a, Integer b) {
		if (null == a || null == b) {
			return 0;
		}
		return a + b;
	}
	
	public double evaluate(Double a, Double b) {
		if (null == a || null == b) {
			return 0;
		}
		return a + b;
	}

}
