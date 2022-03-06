package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.values.Value;

import java.util.HashMap;

/**
 * This class is a special symbol table that only allows changing global and local variables.
 */
@Deprecated(forRemoval = true)
public class FunctionStackTable extends StackTable {
	private final StackTable root;
	
	public FunctionStackTable(StackTable parent, ISyntax syntaxPosition) {
		super(parent, syntaxPosition, false, false, true);
		this.root = parent.getGlobalRoot();
	}
	
	@Override
	public Value<?> get(String name) {
		if (this.symbolMap == null) {
			return this.root.get(name);
		}
		
		Value<?> value = this.symbolMap.get(name);
		return value == null ? this.root.get(name) : value;
	}
	
	@Override
	public void set(String name, Value<?> value) {
		if (this.root.get(name) != null) {
			this.root.set(name, value);
			return;
		}
		
		if (this.symbolMap == null) {
			this.symbolMap = new HashMap<>();
		}
		
		this.symbolMap.put(name, value);
	}
	
	@Override
	public StackTable getParent(String name) {
		return this.root.get(name) != null ? this.root : null;
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
		return "FunctionStackTable" + (this.symbolMap == null ? "{}" : this.symbolMap.toString());
	}
}
