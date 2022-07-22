package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StackTable {
	private final StackTable parentTable;
	private final ISyntax syntaxPosition;
	private final StackTable rootTable;

	// Lazy generated
	/**
	 * This is the symbol map that gets the value related to identifiers
	 */
	private Map<String, Value> symbolMap;

	/**
	 * These are the definitions that are currently available in the stack
	 */
	protected ArucasClassDefinitionMap classDefinitions;

	/**
	 * These are the importable built-in definitions
	 */
	protected Map<String, ArucasClassDefinitionMap> importableDefinitions;

	/**
	 * These are definitions cached when importing from files
	 */
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
	public Value get(String name) {
		if (this.symbolMap != null) {
			Value value = this.symbolMap.get(name);
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
	public void set(String name, Value value) {
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
	public void setLocal(String name, Value value) {
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
		if (this == this.rootTable) {
			return this.classDefinitions.getFunctionForClass(clazz, name, parameters);
		}
		return this.rootTable.getClassFunction(clazz, name, parameters);
	}

	/**
	 * Gets a class definitions if it's available in the stack
	 */
	public AbstractClassDefinition getClassDefinition(String name) {
		if (this.classDefinitions != null) {
			AbstractClassDefinition definition = this.classDefinitions.get(name);
			if (definition != null) {
				return definition;
			}
		}

		return this.parentTable != null ? this.parentTable.getClassDefinition(name) : null;
	}

	/**
	 * Gets a class definitions if it's available in the stack
	 */
	public AbstractClassDefinition getClassDefinition(Class<? extends Value> clazz) {
		if (this.classDefinitions != null) {
			AbstractClassDefinition definition = this.classDefinitions.get(clazz);
			if (definition != null) {
				return definition;
			}
		}

		return this.parentTable != null ? this.parentTable.getClassDefinition(clazz) : null;
	}

	/**
	 * Checks whether a class definitions is available in the stack
	 */
	public boolean hasClassDefinition(String name) {
		if (this.classDefinitions == null || !this.classDefinitions.has(name)) {
			return this.parentTable != null && this.parentTable.hasClassDefinition(name);
		}

		return true;
	}

	/**
	 * Adds a class definitions to the stack
	 */
	public void addClassDefinition(AbstractClassDefinition definition) {
		if (this.classDefinitions == null) {
			this.classDefinitions = new ArucasClassDefinitionMap();
		}

		this.classDefinitions.add(definition);
	}

	/**
	 * Adds class definitions map without sorting
	 */
	public void insertAllClassDefinitions(ArucasClassDefinitionMap definitions) {
		if (this.classDefinitions == null) {
			this.classDefinitions = new ArucasClassDefinitionMap();
		}

		this.classDefinitions.insertAll(definitions);
	}

	/**
	 * Adds a class definitions map to the cache (used for importing)
	 */
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

	/**
	 * Tries to get a class definitions map if it's in the cache or if it's importable
	 */
	public ArucasClassDefinitionMap getCachedDefinitionMap(String fileName) {
		if (this.rootTable == this) {
			// Built-in importable definitions takes precedence
			if (this.importableDefinitions != null) {
				ArucasClassDefinitionMap definitions = this.importableDefinitions.get(fileName);
				if (definitions != null) {
					return definitions;
				}
			}
			return this.cachedDefinitions == null ? null : this.cachedDefinitions.get(fileName);
		}
		return this.rootTable.getCachedDefinitionMap(fileName);
	}

	/**
	 * Clears the cache
	 */
	public void clearCachedDefinitions() {
		if (this.cachedDefinitions != null) {
			this.cachedDefinitions.clear();
			this.cachedDefinitions = null;
		}
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
			private StackTable nextStack = StackTable.this;

			@Override
			public boolean hasNext() {
				return this.nextStack != null && this.nextStack != this.nextStack.rootTable;
			}

			@Override
			public StackTable next() {
				StackTable current = this.nextStack;
				this.nextStack = this.nextStack.parentTable;
				return current;
			}
		};
	}

	@Override
	public String toString() {
		return (this.parentTable == null ? "RootTable" : "StackTable") + (this.symbolMap == null ? "{}" : this.symbolMap.toString()) +
			" Parent: " + (this.parentTable != null ? this.parentTable.toString() : "");
	}
}
