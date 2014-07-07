package org.project.common.function;

public abstract class DoubleFunction {

	public abstract double apply(double value);

	public boolean isDensifying() {
		return Math.abs(apply(0.0)) != 0.0;
	}
}
