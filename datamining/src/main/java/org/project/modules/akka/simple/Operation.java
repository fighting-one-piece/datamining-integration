package org.project.modules.akka.simple;

public class Operation {
	
	private final int param1;
	private final int param2;
	private final OperationSymbol symbol;
	
	public Operation(int param1, int param2, OperationSymbol symbol) {
		this.param1 = param1;
		this.param2 = param2;
		this.symbol = symbol;
	}
	
	public int getParam1() {
		return param1;
	}
	
	public int getParam2() {
		return param2;
	}
	
	public OperationSymbol getOperationSymbol() {
		return symbol;
	}
	
	public int execute() {
		int result = 0;
		String name = symbol.getName();
		if (name.equalsIgnoreCase(OperationSymbol.ADD.getName())) {
			result = this.param1 + this.param2;
		} else if (name.equalsIgnoreCase(OperationSymbol.SUB.getName())) {
			result = this.param1 - this.param2;
		} else if (name.equalsIgnoreCase(OperationSymbol.MUL.getName())) {
			result = this.param1 * this.param2;
		} else {
			result = this.param1 / this.param2;
		}
		return result;
	}

}
