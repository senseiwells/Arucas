package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.api.ArucasThreadHandler;
import me.senseiwells.arucas.api.IArucasOutput;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.*;

/**
 * Runtime context class of the programming language.
 *
 * A context is never shared across two threads and is
 * threadsafe.
 */
public class Context {
	private final ArucasThreadHandler threadHandler;
	private final ArucasFunctionMap<AbstractBuiltInFunction<?>> extensions;
	private final IArucasOutput arucasOutput;
	
	private final String displayName;
	private final Context parentContext;
	private StackTable stackTable;
	private boolean isDebug;
	private boolean suppressDeprecated;
	
	private final ThrowValue.Continue continueThrowable = new ThrowValue.Continue();
	private final ThrowValue.Break breakThrowable = new ThrowValue.Break();
	private final ThrowValue.Return returnThrowable = new ThrowValue.Return(NullValue.NULL);
	
	private Context(String displayName, Context parentContext, ArucasFunctionMap<AbstractBuiltInFunction<?>> extensions, ArucasClassDefinitionMap classDefinitions, ArucasThreadHandler threadHandler, IArucasOutput arucasOutput) {
		this.extensions = extensions;
		this.arucasOutput = arucasOutput;
		this.threadHandler = threadHandler;
		
		this.displayName = displayName;
		this.parentContext = parentContext;
		this.stackTable = new StackTable();
		
		// Initialize the class definitions map by inserting the previous table values
		this.stackTable.classDefinitions = new ArucasClassDefinitionMap();
		this.stackTable.classDefinitions.insertAll(classDefinitions);
	}
	
	public Context(String displayName, ArucasFunctionMap<AbstractBuiltInFunction<?>> extensions, ArucasClassDefinitionMap classDefinitions, ArucasThreadHandler threadHandler, IArucasOutput arucasOutput) {
		this(displayName, null, extensions, classDefinitions, threadHandler, arucasOutput);
	}
	
	private Context(Context branch, StackTable stackTable) {
		this.displayName = branch.displayName;
		this.stackTable = stackTable;
		this.threadHandler = branch.threadHandler;
		this.arucasOutput = branch.arucasOutput;
		this.extensions = branch.extensions;
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
		return new Context(displayName, this, this.extensions, this.stackTable.getRoot().classDefinitions, this.threadHandler, this.arucasOutput);
	}
	
	public ThrowValue.Continue getContinueThrowable() {
		return this.continueThrowable;
	}
	
	public ThrowValue.Break getBreakThrowable() {
		return this.breakThrowable;
	}
	
	public ThrowValue.Return getReturnThrowable(Value<?> value) {
		this.returnThrowable.setReturnValue(value);
		return this.returnThrowable;
	}
	
	/**
	 * Returns this thread handler.
	 */
	public ArucasThreadHandler getThreadHandler() {
		return this.threadHandler;
	}
	
	/**
	 * Returns this contexts output object.
	 */
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
	
	public void pushScope(ISyntax syntaxPosition) {
		this.stackTable = new StackTable(this.stackTable, syntaxPosition, false, false, false);
	}
	
	public void pushLoopScope(ISyntax syntaxPosition) {
		this.stackTable = new StackTable(this.stackTable, syntaxPosition, true, true, false);
	}
	
	public void pushSwitchScope(ISyntax syntaxPosition) {
		this.stackTable = new StackTable(this.stackTable, syntaxPosition, true, false, false);
	}
	
	public void pushFunctionScope(ISyntax syntaxPosition) {
		this.stackTable = new FunctionStackTable(this.stackTable, syntaxPosition);
	}
	
	public void popScope() {
		this.stackTable = this.stackTable.getParentTable();
	}
	
	public void moveScope(StackTable stackTable) {
		// We do not want to jump to an arbitrary stackTable
		
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
		return this.extensions.has(name);
	}

	public boolean isDefinedClass(String name) {
		return this.stackTable.hasClassDefinition(name);
	}

	public void throwIfStackNameTaken(String name, ISyntax syntaxPosition) throws CodeError {
		if (this.isBuiltInFunction(name)) {
			throw new CodeError(
				CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
				"%s() is already defined as a built in function".formatted(name),
				syntaxPosition
			);
		}
	}

	public void throwIfClassNameTaken(String name, ISyntax syntaxPosition) throws CodeError {
		if (this.isDefinedClass(name)) {
			throw new CodeError(
				CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
				"%s is already defined as a class".formatted(name),
				syntaxPosition
			);
		}
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

	public void printDeprecated(String message) {
		if (!this.suppressDeprecated) {
			this.getOutput().println(message);
		}
	}
	
	public AbstractClassDefinition getClassDefinition(String name) {
		return this.stackTable.getClassDefinition(name);
	}
	
	public void addClassDefinition(AbstractClassDefinition definition) {
		this.stackTable.addClassDefinition(definition);
	}

	public AbstractBuiltInFunction<?> getBuiltInFunction(String methodName, int parameters) {
		return this.extensions.get(methodName, parameters);
	}

	public FunctionValue getMemberFunction(Value<?> value, String methodName, int parameters) {
		List<AbstractClassDefinition> definitions = this.stackTable.getRoot().classDefinitions.get(value.getClass());
		
		// TODO: Make this O(1) for builtIn classes
		for (AbstractClassDefinition definition : definitions) {
			FunctionValue targetMethod = definition.getMethods().get(methodName, parameters);
			if (targetMethod != null) {
				return targetMethod;
			}
		}
		
		return null;
	}
	
	@Deprecated(forRemoval = true)
	public void dumpScopes() {
		StringBuilder sb = new StringBuilder();
		sb.append("----------------------------\n");
		
		Iterator<StackTable> iter = this.stackTable.iterator();
		while (iter.hasNext()) {
			sb.append(iter.next()).append("\n");
		}
		
		this.stackTable.getRoot().classDefinitions.iterator().forEachRemaining(System.out::println);
		System.out.println(sb);
	}
}
