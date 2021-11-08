package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.api.IArucasOutput;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;

import java.util.*;

/**
 * Runtime context class of the programming language
 */
public class Context {
	private final Set<String> builtInFunctions;
	private final List<IArucasExtension> extensions;
	private final Map<String, Class<?>> valueMap;
	private final IArucasOutput arucasOutput;
	
	private final String displayName;
	private final Context parentContext;
	private StackTable stackTable;
	private boolean isDebug;
	private boolean suppressDeprecated;

	private Context(String displayName, Context parentContext, List<IArucasExtension> extensions, Map<String, Class<?>> valueMap, IArucasOutput arucasOutput) {
		this.builtInFunctions = new HashSet<>();
		this.extensions = extensions;
		this.valueMap = valueMap;
		this.arucasOutput = arucasOutput;
		
		this.displayName = displayName;
		this.stackTable = new StackTable();
		this.parentContext = parentContext;
		
		for (IArucasExtension extension : extensions) {
			for (AbstractBuiltInFunction<?> function : extension.getDefinedFunctions()) {
				this.builtInFunctions.add(function.value);
				this.stackTable.set(function.value, function);
			}
		}
	}
	
	public Context(String displayName, List<IArucasExtension> extensions, Map<String, Class<?>> valueMap, IArucasOutput arucasOutput) {
		this(displayName, null, extensions, valueMap, arucasOutput);
	}
	
	private Context(Context branch, StackTable stackTable) {
		this.displayName = branch.displayName;
		this.stackTable = stackTable;
		this.arucasOutput = branch.arucasOutput;
		this.extensions = branch.extensions;
		this.builtInFunctions = branch.builtInFunctions;
		this.valueMap = branch.valueMap;
		this.parentContext = branch.parentContext;
	}

	@SuppressWarnings("unused")
	public Context createBranch() {
		return new Context(this, this.stackTable);
	}

	@SuppressWarnings("unused")
	public Context createRootBranch() {
		return new Context(this, this.stackTable.getRoot());
	}

	@SuppressWarnings("unused")
	public Context createBranchFromPosition(StackTable stackTable) {
		if (this.stackTable.getRoot() == stackTable.getRoot()) {
			return new Context(this, stackTable);
		}
		return null;
	}
	
	public Context createChildContext(String displayName) {
		return new Context(displayName, this, this.extensions, this.valueMap, this.arucasOutput);
	}
	
	public IArucasOutput getOutput() {
		return this.arucasOutput;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	public StackTable getStackTable() {
		return this.stackTable;
	}
	
	public StackTable getBreakScope() {
		return this.stackTable.getBreakScope();
	}
	
	public StackTable getContinueScope() {
		return this.stackTable.getContinueScope();
	}
	
	public StackTable getReturnScope() {
		return this.stackTable.getReturnScope();
	}
	
	public void pushScope(Position position) {
		this.stackTable = new StackTable(this.stackTable, position, false, false, false);
	}
	
	public void pushLoopScope(Position position) {
		this.stackTable = new StackTable(this.stackTable, position, true, true, false);
	}
	
	public void pushFunctionScope(Position position) {
		this.stackTable = new FunctionStackTable(this.stackTable, position);
	}
	
	public void popScope() {
		this.stackTable = this.stackTable.getParentTable();
	}
	
	public void moveScope(StackTable stackTable) {
		// We do not want to jump to an arbitrary symbolTable
		
		Iterator<StackTable> iter = this.stackTable.iterator();
		while (iter.hasNext()) {
			StackTable table = iter.next();
			if (table == stackTable) {
				this.stackTable = table;
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
		this.stackTable.set(name, value);
	}
	
	public void setLocal(String name, Value<?> value) {
		this.stackTable.setLocal(name, value);
	}
	
	public Value<?> getVariable(String name) {
		return this.stackTable.get(name);
	}

	public Class<?> getValueClassFromString(String string) {
		return this.valueMap.get(string);
	}

	public void printDeprecated(String message) {
		if (!this.suppressDeprecated) {
			this.getOutput().print(message);
		}
	}
	
	@Deprecated(forRemoval = true)
	public void dumpScopes() {
		StringBuilder sb = new StringBuilder();
		sb.append("----------------------------\n");
		
		Iterator<StackTable> iter = this.stackTable.iterator();
		while (iter.hasNext()) {
			sb.append(iter.next()).append("\n");
		}
		
		System.out.println(sb);
	}
}
