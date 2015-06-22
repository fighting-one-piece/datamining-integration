package org.project.modules.akka.simple;

public class OperationResult {

	private final Operation operation;
	private final int result;
	
	public OperationResult(Operation operation, int result) {
		this.operation = operation;
		this.result = result;
	}
	
	@Override
	public String toString() {
		return operation.getParam1() + operation.getOperationSymbol().getName() 
				+ operation.getParam2() + "=" + result;
	}
	
}
