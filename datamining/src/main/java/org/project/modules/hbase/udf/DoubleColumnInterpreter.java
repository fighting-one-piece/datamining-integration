package org.project.modules.hbase.udf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.coprocessor.ColumnInterpreter;

public class DoubleColumnInterpreter implements
		ColumnInterpreter<Double, Double> {

	@Override
	public void write(DataOutput out) throws IOException {
	}

	@Override
	public void readFields(DataInput in) throws IOException {
	}

	@Override
	public Double getValue(byte[] colFamily, byte[] colQualifier, KeyValue kv)
			throws IOException {
		if (kv == null)
			return null;
		// 临时解决方案，如果采用Bytes.toDouble(kv.getValue())会报错，偏移量大于总长度。
		// toDouble(getBuffer(), getValueOffset)，偏移量也不对。
		return Double.valueOf(new String(kv.getValue()));
	}

	@Override
	public Double add(Double l1, Double l2) {
		if (l1 == null ^ l2 == null) {
			return l1 == null ? l2 : l1;
		} else if (l1 == null) {
			return null;
		}
		return l1 + l2;
	}

	@Override
	public Double getMaxValue() {
		return null;
	}

	@Override
	public Double getMinValue() {
		return null;
	}

	@Override
	public Double multiply(Double o1, Double o2) {
		if (o1 == null ^ o2 == null) {
			return o1 == null ? o2 : o1;
		} else if (o1 == null) {
			return null;
		}
		return o1 * o2;
	}

	@Override
	public Double increment(Double o) {
		return null;
	}

	@Override
	public Double castToReturnType(Double o) {
		return o.doubleValue();
	}

	@Override
	public int compare(Double l1, Double l2) {
		if (l1 == null ^ l2 == null) {
			return l1 == null ? -1 : 1; // either of one is null.
		} else if (l1 == null)
			return 0; // both are null
		return l1.compareTo(l2); // natural ordering.
	}

	@Override
	public double divideForAvg(Double o, Long l) {
		return (o == null || l == null) ? Double.NaN : (o.doubleValue() / l
				.doubleValue());
	}
}
