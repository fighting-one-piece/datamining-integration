package org.project.modules.akka.simple;

public enum OperationSymbol {

	ADD("+"), SUB("-"), MUL("*"), DEV("/");
	
	private String name = null;
	
	private OperationSymbol(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
