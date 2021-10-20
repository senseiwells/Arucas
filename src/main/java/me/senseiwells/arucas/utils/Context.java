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
	
	public Position getEntryPosition() {
		return symbolTable.position;
	}
	
	public Context pushScope(Position position) {
		this.symbolTable = new SymbolTable(this.symbolTable, position, false, false, false);
		return this;
	}
	
	public Context pushWhileScope(Position position) {
		this.symbolTable = new SymbolTable(this.symbolTable, position, true, true, false);
		return this;
	}
	
	public Context pushFunctionScope(Position position) {
		this.symbolTable = new FunctionSymbolTable(this.symbolTable, position);
		return this;
	}
	
	public Context popScope() {
		this.symbolTable = this.symbolTable.parentTable;
		return this;
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
