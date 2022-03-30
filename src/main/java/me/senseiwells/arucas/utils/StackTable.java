package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StackTable {
	private final StackTable parentTable;
	private final ISyntax syntaxPosition;
	private final StackTable rootTable;

	// Lazy generated
	protected Map<String, Value<?>> symbolMap;
	protected ArucasClassDefinitionMap classDefinitions;
	protected Map<String, ArucasClassDefinitionMap> cachedDefinitions;
	
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
		if (parentTable != null && parentTable.symbolMap != null) {
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
			if (this.parentTable.symbolMap == null || this.parentTable.symbolMap.get(name) == null) {
				return this.parentTable.getParent(name);
			}
			
			return this.parentTable;
		}
		
		return null;
	}

	public FunctionValue getClassFunction(Class<?> clazz, String name, int parameters) {
		FunctionValue functionValue = null;
		if (this.classDefinitions != null) {
			functionValue = this.classDefinitions.getFunctionForClass(clazz, name, parameters);
		}
		if (functionValue == null && this.parentTable != null) {
			return this.parentTable.getClassFunction(clazz, name, parameters);
		}

		return functionValue;
	}
	
	public AbstractClassDefinition getClassDefinition(String name) {
		if (this.classDefinitions != null) {
			AbstractClassDefinition definition = this.classDefinitions.get(name);
			if (definition != null) {
				return definition;
			}
		}
		
		return this.parentTable != null ? this.parentTable.getClassDefinition(name) : null;
	}

	public boolean hasClassDefinition(String name) {
		if (this.classDefinitions == null || !this.classDefinitions.has(name)) {
			return this.parentTable != null && this.parentTable.hasClassDefinition(name);
		}

		return true;
	}
	
	public void addClassDefinition(AbstractClassDefinition definition) {
		if (this.classDefinitions == null) {
			this.classDefinitions = new ArucasClassDefinitionMap();
		}
		
		this.classDefinitions.add(definition);
	}

	public void replaceClassDefinition(AbstractClassDefinition definition) {
		if (this.classDefinitions == null) {
			this.classDefinitions = new ArucasClassDefinitionMap();
		}

		this.classDefinitions.replace(definition);
	}

	public void addCachedDefinitionMap(String fileName, ArucasClassDefinitionMap definitions) {
		if (this.rootTable == this) {
			if (this.cachedDefinitions == null) {
				this.cachedDefinitions = new HashMap<>();
			}

			this.cachedDefinitions.put(fileName, definitions);
			return;
		}
		this.rootTable.addCachedDefinitionMap(fileName, definitions);
	}

	public ArucasClassDefinitionMap getCachedDefinitionMap(String fileName) {
		if (this.rootTable == this) {
			if (this.cachedDefinitions == null) {
				return null;
			}
			return this.cachedDefinitions.get(fileName);
		}
		return this.rootTable.getCachedDefinitionMap(fileName);
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
	public String toString() {
		return (this.parentTable == null ? "RootTable" : "StackTable") + (this.symbolMap == null ? "{}" : this.symbolMap.toString());
	}
}
