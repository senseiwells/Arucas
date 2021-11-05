package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;

import java.util.*;
import java.util.function.Consumer;

/**
 * Runtime context class of the programming language
 */
public class Context {
	private final Set<String> builtInFunctions;
	private final List<IArucasExtension> extensions;
	private final Map<String, Class<? extends Value<?>>> valueMap;
	private final Consumer<String> printDeprecated;
	
	private final String displayName;
	private final Context parentContext;
	private SymbolTable symbolTable;
	private boolean isDebug;
	private boolean suppressDeprecated;

	private Context(String displayName, Context parentContext, List<IArucasExtension> extensions, Map<String, Class<? extends Value<?>>> valueMap, Consumer<String> printDeprecated) {
		this.builtInFunctions = new HashSet<>();
		this.extensions = extensions;
		this.valueMap = valueMap;
		this.printDeprecated = printDeprecated;
		
		this.displayName = displayName;
		this.symbolTable = new SymbolTable();
		this.parentContext = parentContext;
		
		for (IArucasExtension extension : extensions) {
			for (AbstractBuiltInFunction<?> function : extension.getDefinedFunctions()) {
				this.builtInFunctions.add(function.value);
				this.symbolTable.set(function.value, function);
			}
		}
	}
	
	public Context(String displayName, List<IArucasExtension> extensions, Map<String, Class<? extends Value<?>>> valueMap, Consumer<String> printDeprecated) {
		this(displayName, null, extensions, valueMap, printDeprecated);
	}
	
	private Context(Context branch, SymbolTable symbolTable) {
		this.displayName = branch.displayName;
		this.symbolTable = symbolTable;
		this.extensions = branch.extensions;
		this.builtInFunctions = branch.builtInFunctions;
		this.valueMap = branch.valueMap;
		this.parentContext = branch.parentContext;
		this.printDeprecated = branch.printDeprecated;
	}

	@SuppressWarnings("unused")
	public Context createBranch() {
		return new Context(this, this.symbolTable);
	}

	@SuppressWarnings("unused")
	public Context createRootBranch() {
		return new Context(this, this.symbolTable.getRoot());
	}

	@SuppressWarnings("unused")
	public Context createBranchFromPosition(SymbolTable symbolTable) {
		if (this.symbolTable.getRoot() == symbolTable.getRoot())
			return new Context(this, symbolTable);
		return null;
	}
	
	public Context createChildContext(String displayName) {
		return new Context(displayName, this, this.extensions, this.valueMap, this.printDeprecated);
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	public SymbolTable getSymbolTable() {
		return this.symbolTable;
	}
	
	public void pushScope(Position position) {
		this.symbolTable = new SymbolTable(this.symbolTable, position, false, false, false);
	}
	
	public void pushLoopScope(Position position) {
		this.symbolTable = new SymbolTable(this.symbolTable, position, true, true, false);
	}
	
	public void pushFunctionScope(Position position) {
		this.symbolTable = new FunctionSymbolTable(this.symbolTable, position);
	}
	
	public void popScope() {
		this.symbolTable = this.symbolTable.getParentTable();
	}
	
	public void moveScope(SymbolTable symbolTable) {
		// We do not want to jump to an arbitrary symbolTable
		
		Iterator<SymbolTable> iter = this.symbolTable.iterator();
		while (iter.hasNext()) {
			SymbolTable table = iter.next();
			if (table == symbolTable) {
				this.symbolTable = table;
				return;
			}
		}
		
		// This should throw an exception
	}
	
	public void setDebug(boolean debug) {
		this.isDebug = debug;
	}
	
	public boolean isDebug() {
		return this.isDebug;
	}

	public void setSuppressDeprecated(boolean suppressed) {
		this.suppressDeprecated = suppressed;
	}

	public boolean isSuppressDeprecated() {
		return this.suppressDeprecated;
	}

	public boolean isBuiltInFunction(String name) {
		return this.builtInFunctions.contains(name);
	}
	
	public void setVariable(String name, Value<?> value) {
		this.symbolTable.set(name, value);
	}
	
	public void setLocal(String name, Value<?> value) {
		this.symbolTable.setLocal(name, value);
	}
	
	public Value<?> getVariable(String name) {
		return this.symbolTable.get(name);
	}

	public Class<? extends Value<?>> getValueClassFromString(String string) {
		return this.valueMap.get(string);
	}

	public void printDeprecated(String message) {
		this.printDeprecated.accept(message);
	}
	
	@Deprecated
	public void dumpScopes() {
		StringBuilder sb = new StringBuilder();
		sb.append("----------------------------\n");
		
		Iterator<SymbolTable> iter = this.symbolTable.iterator();
		while (iter.hasNext()) {
			sb.append(iter.next()).append("\n");
		}
		
		System.out.println(sb.toString());
	}
}
