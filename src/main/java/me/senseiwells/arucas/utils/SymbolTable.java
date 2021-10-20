package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.Value;

import java.util.*;

public class SymbolTable {
	protected final Map<String, Value<?>> symbolMap;
	public SymbolTable parentTable;
	public Position position;
	
	protected final boolean breakableScope;
	protected final boolean continueScope;
	protected final boolean returnableScope;
	
	public SymbolTable(SymbolTable parent, Position position, boolean breakableScope, boolean continueScope, boolean returnableScope) {
		this.symbolMap = new HashMap<>();
		this.parentTable = parent;
		this.position = position;
		this.returnableScope = returnableScope;
		this.breakableScope = breakableScope;
		this.continueScope = continueScope;
	}

	public SymbolTable() {
		this(null, null, false, false, false);
	}

	public SymbolTable setDefaultSymbols(Context context) {
		if (!this.symbolMap.isEmpty())
			return this;
		
		for (BuiltInFunction function : BuiltInFunction.getBuiltInFunctions()) {
			this.set(function.value, function.setContext(context));
		}
		
		return this;
	}
	
	/**
	 * Returns the value of the variable name.
	 */
	public Value<?> get(String name) {
		Value<?> value = this.symbolMap.get(name);
		if (value != null)
			return value;
		
		SymbolTable parentTable = this.getParent(name);
		return parentTable != null ? parentTable.symbolMap.get(name):null;
	}
	
	/**
	 * Change the value of a variable called name.
	 */
	public void set(String name, Value<?> value) {
		SymbolTable parentTable = this.getParent(name);
		if (parentTable != null)
			parentTable.symbolMap.put(name, value);
		else
			this.symbolMap.put(name, value);
	}
	
	/**
	 * Returns the first parent that contains the value name.
	 */
	public SymbolTable getParent(String name) {
		if (this.parentTable != null) {
			if (this.parentTable.symbolMap.get(name) != null)
				return this.parentTable;
			else
				return this.parentTable.getParent(name);
		}
		return null;
	}
	
	/**
	 * Returns the root table.
	 */
	public SymbolTable getRoot() {
		return this.parentTable != null ? this.parentTable.getRoot():this;
	}
	
	public SymbolTable getReturnScope() {
		if (this.returnableScope)
			return this;
		
		if (this.parentTable != null)
			return this.parentTable.getReturnScope();
		
		return null;
	}
	
	public SymbolTable getBreakScope() {
		if (this.breakableScope)
			return this;
		
		if (this.parentTable != null)
			return this.parentTable.getBreakScope();
		
		return null;
	}
	
	public SymbolTable getContinueScope() {
		if (this.continueScope)
			return this;
		
		if (this.parentTable != null)
			return this.parentTable.getContinueScope();
		
		return null;
	}
	
	@Override
	public String toString() {
		return "%s%s".formatted(this.parentTable == null ? "RootTable":"SymbolTable", this.symbolMap);
	}
}
