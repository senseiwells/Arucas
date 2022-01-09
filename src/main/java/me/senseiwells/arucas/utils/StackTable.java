package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.values.Value;

import java.util.*;

public class StackTable {
	// Lazy generated
	protected Map<String, Value<?>> symbolMap;
	protected ArucasClassDefinitionMap classDefinitions;
	private final StackTable parentTable;
	private final ISyntax syntaxPosition;
	private final StackTable rootTable;
	
	protected final boolean canContinue;
	protected final boolean canBreak;
	protected final boolean canReturn;
	
	public StackTable(StackTable parent, ISyntax syntaxPosition, boolean canBreak, boolean canContinue, boolean canReturn) {
		this.parentTable = parent;
		this.syntaxPosition = syntaxPosition;
		this.canContinue = canContinue;
		this.canReturn = canReturn;
		this.canBreak = canBreak;
		this.rootTable = parent == null ? this : parent.rootTable;
	}

	public StackTable() {
		this(null, ISyntax.empty(), false, false, false);
	}
	
	/**
	 * Returns the scope position that created this table.
	 */
	public final ISyntax getPosition() {
		return this.syntaxPosition;
	}
	
	/**
	 * Returns the value of the variable name.
	 */
	public Value<?> get(String name) {
		if (this.symbolMap != null) {
			Value<?> value = this.symbolMap.get(name);
			if (value != null) {
				return value;
			}
		}
		
		if (this.parentTable != null) {
			return this.parentTable.get(name);
		}
		
		return null;
	}
	
	/**
	 * Change the value of a variable called name.
	 */
	public void set(String name, Value<?> value) {
		StackTable parentTable = this.getParent(name);
		if (parentTable != null) {
			// If a parentTable was found then symbolMap is not null
			parentTable.symbolMap.put(name, value);
		}
		else {
			this.setLocal(name, value);
		}
	}
	
	/**
	 * Change the value of a local variable called name.
	 */
	public void setLocal(String name, Value<?> value) {
		if (this.symbolMap == null) {
			this.symbolMap = new HashMap<>();
		}
		
		this.symbolMap.put(name, value);
	}
	
	/**
	 * Returns the first parent that contains the value name.
	 */
	public StackTable getParent(String name) {
		if (this.parentTable != null) {
			if (this.parentTable.symbolMap == null
			|| this.parentTable.symbolMap.get(name) == null) {
				return this.parentTable.getParent(name);
			}
			
			return this.parentTable;
		}
		
		return null;
	}
	
	public AbstractClassDefinition getClassDefinition(String name) {
		if (this.classDefinitions != null) {
			AbstractClassDefinition definition = this.classDefinitions.get(name);
			if(definition != null) {
				return definition;
			}
		}
		
		return this.parentTable != null ? this.parentTable.getClassDefinition(name) : null;
	}

	public boolean hasClassDefinition(String name) {
		return this.classDefinitions != null && this.classDefinitions.has(name);
	}
	
	public void addClassDefinition(AbstractClassDefinition definition) {
		if (this.classDefinitions == null) {
			this.classDefinitions = new ArucasClassDefinitionMap();
		}
		
		this.classDefinitions.add(definition);
	}

	/**
	 * Returns the root table.
	 */
	public StackTable getRoot() {
		return this.rootTable;
	}

	public StackTable getParentTable() {
		return this.parentTable;
	}
	
	public StackTable getReturnScope() {
		return this.canReturn ? this : this.parentTable != null ? this.parentTable.getReturnScope() : null;
	}
	
	public StackTable getBreakScope() {
		return this.canBreak ? this : this.parentTable != null ? this.parentTable.getBreakScope() : null;
	}
	
	public StackTable getContinueScope() {
		return this.canContinue ? this : this.parentTable != null ? this.parentTable.getContinueScope() : null;
	}
	
	public Iterator<StackTable> iterator() {
		return new Iterator<>() {
			private StackTable object = StackTable.this;
			
			@Override
			public boolean hasNext() {
				return this.object != null;
			}
			
			@Override
			public StackTable next() {
				StackTable current = this.object;
				this.object = this.object.parentTable;
				return current;
			}
		};
	}

	@Override
	public int hashCode() {
		// TODO: Remove this hashCode
		return this.symbolMap == null ? 0 : this.symbolMap.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Remove this equals
		return this == object;
		/*
		if (this == object) {
			return true;
		}
		if (object instanceof StackTable otherTable) {
			if (this.symbolMap.size() != otherTable.symbolMap.size()) {
				return false;
			}
			for (Map.Entry<String, Value<?>> entry : this.symbolMap.entrySet()) {
				if (!otherTable.get(entry.getKey()).equals(entry.getValue())) {
					return false;
				}
			}
			return true;
		}
		return false;
	  */
	}

	@Override
	public String toString() {
		return (this.parentTable == null ? "RootTable" : "StackTable") + (this.symbolMap == null ? "{}" : this.symbolMap.toString());
	}
}
