package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.Value;

/**
 * This class is a special symbol table that only allows changing global and local variables.
 */
public class FunctionStackTable extends StackTable {
	private final StackTable root;
	
	public FunctionStackTable(StackTable parent, Position position) {
		super(parent, position, false, false, true);
		this.root = parent.getRoot();
	}
	
	@Override
	public Value<?> get(String name) {
		Value<?> value = this.symbolMap.get(name);
		return value == null ? this.root.get(name) : value;
	}
	
	@Override
	public void set(String name, Value<?> value) {
		if (this.root.get(name) != null) {
			this.root.set(name, value);
			return;
		}
		this.symbolMap.put(name, value);
	}
	
	@Override
	public StackTable getParent(String name) {
		return this.root.get(name) != null ? this.root : null;
	}
	
	@Override
	public StackTable getRoot() {
		return this.root;
	}
	
	@Override
	public StackTable getBreakScope() {
		return null;
	}
	
	@Override
	public StackTable getContinueScope() {
		return null;
	}
	
	@Override
	public StackTable getReturnScope() {
		return this;
	}
	
	@Override
	public String toString() {
		return "FunctionStackTable%s".formatted(this.symbolMap);
	}
}
