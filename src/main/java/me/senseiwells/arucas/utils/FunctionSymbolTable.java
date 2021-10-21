package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.Value;

/**
 * This class is a special symbol table that only allows changing global and local variables.
 */
public class FunctionSymbolTable extends SymbolTable {
	private final SymbolTable root;
	
	public FunctionSymbolTable(SymbolTable parent, Position position) {
		super(parent, position, false, false, true);
		this.root = parent.getRoot();
	}
	
	@Override
	public Value<?> get(String name) {
		Value<?> value = this.symbolMap.get(name);
		if (value == null)
			return this.root.get(name);
		return value;
	}
	
	@Override
	public void set(String name, Value<?> value) {
		if (this.root.get(name) != null)
			this.root.set(name, value);
		else
			this.symbolMap.put(name, value);
	}
	
	@Override
	public SymbolTable getParent(String name) {
		if (this.root.get(name) != null)
			return this.root;
		
		return null;
	}
	
	@Override
	public SymbolTable getRoot() {
		return this.root;
	}
	
	@Override
	public SymbolTable getBreakScope() {
		return null;
	}
	
	@Override
	public SymbolTable getContinueScope() {
		return null;
	}
	
	@Override
	public SymbolTable getReturnScope() {
		return this;
	}
	
	@Override
	public String toString() {
		return "FunctionSymbolTable%s".formatted(this.symbolMap);
	}
}
