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
		return value != null ? value : this.parentTable != null ? this.parentTable.get(name) : null;
	}
	
	/**
	 * Change the value of a variable called name.
	 */
	public void set(String name, Value<?> value) {
		SymbolTable parentTable = this.getParent(name);
		Objects.requireNonNullElse(parentTable, this).symbolMap.put(name, value);
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
		return this.parentTable != null ?
			this.parentTable.symbolMap.get(name) != null ?
			this.parentTable : this.parentTable.getParent(name) : null;
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
		return this.canReturn ? this : this.parentTable != null ? this.parentTable.getReturnScope() : null;
	}
	
	public SymbolTable getBreakScope() {
		return this.canBreak ? this : this.parentTable != null ? this.parentTable.getBreakScope() : null;
	}
	
	public SymbolTable getContinueScope() {
		return this.canContinue ? this : this.parentTable != null ? this.parentTable.getContinueScope() : null;
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
