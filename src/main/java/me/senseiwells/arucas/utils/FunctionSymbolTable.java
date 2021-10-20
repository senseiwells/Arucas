package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.BuiltInFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a special symbol table that only allows changing global and local variables.
 */
public class FunctionSymbolTable extends SymbolTable {
	public FunctionSymbolTable(SymbolTable parent, Position position) {
		super(parent, position, false, false, true);
	}
	
	@Override
	public SymbolTable setDefaultSymbols(Context context) {
		return this;
	}
	
	@Override
	public Value<?> get(String name) {
		Value<?> value = this.symbolMap.get(name);
		if (value == null)
			return this.getRoot().get(name);
		return value;
	}
	
	@Override
	public void set(String name, Value<?> value) {
		SymbolTable parent = this.getParent(name);
		if (parent != null)
			parent.set(name, value);
		else
			this.symbolMap.put(name, value);
	}
	
	@Override
	public SymbolTable getParent(String name) {
		SymbolTable root = this.getRoot();
		if (root.get(name) != null)
			return root;
		
		return null;
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
