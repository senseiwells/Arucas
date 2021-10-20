package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.Value;

public class Context {
	public String displayName;
	public Context parentContext;
	public SymbolTable symbolTable;

	public Context(String displayName, Context parent) {
		this.displayName = displayName;
		this.parentContext = parent;
		this.symbolTable = null;
	}
	
	public SymbolTable getSymbolTable() {
		return symbolTable;
	}
	
	public void pushScope(Position position) {
		this.symbolTable = new SymbolTable(this.symbolTable, position, false, false, false);
	}
	
	public void pushWhileScope(Position position) {
		this.symbolTable = new SymbolTable(this.symbolTable, position, true, true, false);
	}
	
	public void pushFunctionScope(Position position) {
		this.symbolTable = new FunctionSymbolTable(this.symbolTable, position);
	}
	
	public void popScope() {
		this.symbolTable = this.symbolTable.parentTable;
	}
	
	public void moveScope(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}
	
	public void setVariable(String name, Value<?> value) {
		this.symbolTable.set(name, value);
	}
	
	public Value<?> getVariable(String name) {
		return this.symbolTable.get(name);
	}
	
	@Deprecated
	public void dumpScopes() {
		StringBuilder sb = new StringBuilder();
		sb.append("----------------------------\n");
		SymbolTable table = symbolTable;
		while (table != null) {
			sb.append(table).append("\n");
			table = table.parentTable;
		}
		
		System.out.println(sb.toString());
	}
}
