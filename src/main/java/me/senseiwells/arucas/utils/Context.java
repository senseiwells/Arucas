package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.extensions.BuiltInFunction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Runtime context class of the programming language
 */
public class Context {
	private final Set<String> builtInFunctions;
	private final List<IArucasExtension> extensions;
	
	private final String displayName;
	private final Context parentContext;
	private SymbolTable symbolTable;
	private boolean isDebug;
	
	private Context(String displayName, Context parentContext, List<IArucasExtension> extensions) {
		this.builtInFunctions = new HashSet<>();
		
		this.displayName = displayName;
		this.symbolTable = new SymbolTable();
		this.parentContext = parentContext;
		this.extensions = extensions;
		
		for (IArucasExtension extension : extensions) {
			for (BuiltInFunction function : extension.getDefinedFunctions()) {
				this.builtInFunctions.add(function.value);
				this.symbolTable.set(function.value, function);
			}
		}
	}
	
	public Context(String displayName, List<IArucasExtension> extensions) {
		this(displayName, null, extensions);
	}
	
	private Context(Context branch, SymbolTable symbolTable) {
		this.displayName = branch.displayName;
		this.symbolTable = symbolTable;
		this.extensions = branch.extensions;
		this.builtInFunctions = branch.builtInFunctions;
		this.parentContext = branch.parentContext;
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
		return new Context(displayName, this, this.extensions);
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
	
	public void pushWhileScope(Position position) {
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
