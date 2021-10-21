package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.Value;

import java.util.*;

public class SymbolTable {
	protected final Map<String, Value<?>> symbolMap;
	private final SymbolTable parentTable;
	private final Position position;
	
	protected final boolean canContinue;
	protected final boolean canBreak;
	protected final boolean canReturn;
	
	public SymbolTable(SymbolTable parent, Position position, boolean canBreak, boolean canContinue, boolean canReturn) {
		this.symbolMap = new HashMap<>();
		this.parentTable = parent;
		this.position = position;
		this.canContinue = canContinue;
		this.canReturn = canReturn;
		this.canBreak = canBreak;
	}

	public SymbolTable() {
		this(null, new Position(0, 0, 0, ""), false, false, false);
	}
	
	/**
	 * Returns the scope position that created this table.
	 */
	public final Position getPosition() {
		return this.position;
	}
	
	/**
	 * Returns the value of the variable name.
	 */
	public Value<?> get(String name) {
		Value<?> value = this.symbolMap.get(name);
		if (value != null)
			return value;
		
		if (this.parentTable != null)
			return this.parentTable.get(name);
		
		return null;
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
	 * Change the value of a local variable called name.
	 */
	public void setLocal(String name, Value<?> value) {
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
	
	public SymbolTable getParentTable() {
		return this.parentTable;
	}
	
	public SymbolTable getReturnScope() {
		if (this.canReturn)
			return this;
		
		if (this.parentTable != null)
			return this.parentTable.getReturnScope();
		
		return null;
	}
	
	public SymbolTable getBreakScope() {
		if (this.canBreak)
			return this;
		
		if (this.parentTable != null)
			return this.parentTable.getBreakScope();
		
		return null;
	}
	
	public SymbolTable getContinueScope() {
		if (this.canContinue)
			return this;
		
		if (this.parentTable != null)
			return this.parentTable.getContinueScope();
		
		return null;
	}
	
	public Iterator<SymbolTable> iterator() {
		return new Iterator<>() {
			private SymbolTable object = SymbolTable.this;
			
			@Override
			public boolean hasNext() {
				return this.object != null;
			}
			
			@Override
			public SymbolTable next() {
				SymbolTable current = this.object;
				this.object = this.object.parentTable;
				return current;
			}
		};
	}
	
	@Override
	public String toString() {
		return "%s%s".formatted(this.parentTable == null ? "RootTable":"SymbolTable", this.symbolMap);
	}
}
