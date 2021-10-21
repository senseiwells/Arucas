package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.BuiltInFunction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Runtime context class of the programming language
 */
public class Context {
	private final Set<String> builtInFunctions;
	
	private final String displayName;
	private SymbolTable symbolTable;
	private boolean isDebug;
	
	public Context(String displayName, Set<IArucasExtension> extensions) {
		this.builtInFunctions = new HashSet<>();
		
		this.displayName = displayName;
		this.symbolTable = new SymbolTable();
		
		for (IArucasExtension extension : extensions) {
			for (BuiltInFunction function : extension.getDefinedFunctions()) {
				this.builtInFunctions.add(function.value);
				this.symbolTable.set(function.value, function.setContext(this));
			}
		}
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public SymbolTable getSymbolTable() {
		return symbolTable;
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
	
	public Value<?> getVariable(String name) {
		return this.symbolTable.get(name);
	}
}
