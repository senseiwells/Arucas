package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.api.ArucasThreadHandler;
import me.senseiwells.arucas.api.IArucasAPI;
import me.senseiwells.arucas.api.IArucasOutput;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.classes.ArucasWrapperCreator;
import me.senseiwells.arucas.values.classes.WrapperClassDefinition;
import me.senseiwells.arucas.values.classes.WrapperClassValue;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Runtime context class of the programming language.
 * <p>
 * A context is never shared across two threads and is
 * threadsafe.
 */
public class Context {
	private final ArucasThreadHandler threadHandler;
	private final ArucasFunctionMap<FunctionValue> extensions;
	private final IArucasAPI arucasAPI;
	private final ValueConverter converter;
	private final UUID contextId;

	private final String displayName;
	private final Context parentContext;

	private StackTable stackTable;
	private Context inheritedContext;
	private boolean isDebug;
	private boolean isExperimental;
	private boolean suppressDeprecated;
	private boolean isMain;

	// Creating exceptions is very expensive, so these are lazy
	private ThrowValue.Continue continueThrowable;
	private ThrowValue.Break breakThrowable;
	private ThrowValue.Return returnThrowable;

	private Context(String name, Context parent, ArucasFunctionMap<FunctionValue> extensions, StackTable table, ArucasThreadHandler handler, ValueConverter converter, IArucasAPI api) {
		this.extensions = extensions;
		this.arucasAPI = api;
		this.threadHandler = handler;
		this.converter = converter;
		this.contextId = parent != null ? parent.contextId : UUID.randomUUID();

		this.displayName = name;
		this.parentContext = parent;
		this.suppressDeprecated = parent != null && parent.suppressDeprecated;
		this.isMain = true;
		this.stackTable = table;
	}

	public Context(String displayName, Context parent, ArucasFunctionMap<FunctionValue> extensions, ArucasThreadHandler handler, ValueConverter converter, IArucasAPI api) {
		this(displayName, parent, extensions, new StackTable(), handler, converter, api);
	}

	private Context(Context branch, StackTable stackTable) {
		this.displayName = branch.displayName;
		this.stackTable = stackTable;
		this.threadHandler = branch.threadHandler;
		this.converter = branch.converter;
		this.arucasAPI = branch.arucasAPI;
		this.extensions = branch.extensions;
		this.parentContext = branch.parentContext;
		this.isDebug = branch.isDebug;
		this.isExperimental = branch.isExperimental;
		this.suppressDeprecated = branch.suppressDeprecated;
		this.isMain = branch.isMain;
		this.contextId = branch.contextId;
	}

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
		StackTable root = this.stackTable.getRoot();
		Context context = new Context(displayName, this, this.extensions, this.threadHandler, this.converter, this.arucasAPI);
		context.isDebug = this.isDebug;
		context.isMain = false;
		return context.setStackTable(root.classDefinitions, root.importableDefinitions, root.cachedDefinitions, false);
	}

	/**
	 * We have special context for parser since we want to be able to add class definitions the
	 * existing stack table which will be used at runtime. This is needed for imports.
	 */
	public Context createParserContext() {
		StackTable root = this.stackTable.getRoot();
		Context context = new Context("Parser Context", this, this.extensions, this.threadHandler, this.converter, this.arucasAPI);
		return context.setStackTable(root.classDefinitions, root.importableDefinitions, root.cachedDefinitions, true);
	}

	public ThrowValue.Continue getContinueThrowable() {
		if (this.continueThrowable == null) {
			this.continueThrowable = new ThrowValue.Continue();
		}
		return this.continueThrowable;
	}

	public ThrowValue.Break getBreakThrowable() {
		if (this.breakThrowable == null) {
			this.breakThrowable = new ThrowValue.Break();
		}
		return this.breakThrowable;
	}

	public ThrowValue.Return getReturnThrowable(Value value) {
		if (this.returnThrowable == null) {
			this.returnThrowable = new ThrowValue.Return(value);
		}
		else {
			this.returnThrowable.setReturnValue(value);
		}
		return this.returnThrowable;
	}

	/**
	 * Returns this thread handler.
	 */
	public ArucasThreadHandler getThreadHandler() {
		return this.threadHandler;
	}

	/**
	 * Returns the current API
	 */
	public IArucasAPI getAPI() {
		return this.arucasAPI;
	}

	/**
	 * Returns this contexts output object.
	 */
	public IArucasOutput getOutput() {
		return this.arucasAPI.getOutput();
	}

	/**
	 * Returns the contexts UUID
	 */
	@SuppressWarnings("unused")
	public UUID getContextId() {
		return this.contextId;
	}

	public Path getImportPath() throws IOException {
		Path importPath = this.arucasAPI.getImportPath();
		if (!Files.exists(importPath) && !ExceptionUtils.runSafe(() -> Files.createDirectories(importPath))) {
			throw new IOException("Failed to import '%s'".formatted(importPath.toAbsolutePath()));
		}
		return importPath;
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

	public void pushRunScope() {
		this.stackTable = new StackTable(this.stackTable, ISyntax.empty(), false, false, true);
	}

	public void pushLoopScope(ISyntax syntaxPosition) {
		this.stackTable = new StackTable(this.stackTable, syntaxPosition, true, true, false);
	}

	public void pushSwitchScope(ISyntax syntaxPosition) {
		this.stackTable = new StackTable(this.stackTable, syntaxPosition, true, false, false);
	}

	public void pushFunctionScope(ISyntax syntaxPosition) {
		this.stackTable = new StackTable(this.stackTable, syntaxPosition, false, false, true);
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

	public void setExperimental(boolean experimental) {
		this.isExperimental = experimental;
	}

	public boolean isExperimental() {
		return this.isExperimental;
	}

	public void setSuppressDeprecated(boolean suppressed) {
		this.suppressDeprecated = suppressed;
	}

	public boolean isSuppressDeprecated() {
		return this.suppressDeprecated;
	}

	public boolean isMain() {
		return this.isMain;
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
		if (this.isDefinedClass(name)) {
			throw new CodeError(
				CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
				"%s is already defined as a class".formatted(name),
				syntaxPosition
			);
		}
	}

	public void setVariable(String name, Value value) {
		this.stackTable.set(name, value);
	}

	public void setLocal(String name, Value value) {
		this.stackTable.setLocal(name, value);
	}

	public Value getVariable(String name) {
		return this.stackTable.get(name);
	}

	public void printDeprecated(String message, Object... objects) {
		if (!this.suppressDeprecated) {
			this.getOutput().println(this.arucasAPI.getOutput().addErrorFormattingBold(message.formatted(objects)));
		}
	}

	@SuppressWarnings("unused")
	public WrapperClassValue createWrapperClass(Class<? extends IArucasWrappedClass> clazz, List<Value> parameters, ISyntax syntaxPosition) throws CodeError {
		String wrapperName = ArucasWrapperCreator.getWrapperName(clazz);

		if (wrapperName == null) {
			throw new RuntimeError("No such wrapper class exists", syntaxPosition, this);
		}

		AbstractClassDefinition classDefinition = this.getClassDefinition(wrapperName);
		if (classDefinition instanceof WrapperClassDefinition wrappedClassDefinition) {
			return wrappedClassDefinition.createNewDefinition(this, parameters, syntaxPosition);
		}
		throw new RuntimeError("No such wrapper class exists", syntaxPosition, this);
	}

	public Value convertValue(Object object) throws CodeError {
		return this.converter.convertFrom(object, this);
	}

	public AbstractClassDefinition getClassDefinition(String name) {
		return this.stackTable.getClassDefinition(name);
	}

	public ArucasClassDefinitionMap getCachedDefinitions(String fileName) {
		return this.stackTable.getCachedDefinitionMap(fileName);
	}

	public ArucasClassDefinitionMap getAllClassDefinitions() {
		return this.stackTable.classDefinitions;
	}

	public void addClassDefinition(AbstractClassDefinition definition) {
		this.stackTable.addClassDefinition(definition);
	}

	public void clearCachedDefinitions() {
		this.stackTable.clearCachedDefinitions();
	}

	public void addCachedDefinition(String fileName, ArucasClassDefinitionMap definitions) {
		this.stackTable.addCachedDefinitionMap(fileName, definitions);
	}

	public FunctionValue getBuiltInFunction(String methodName, int parameters) {
		return this.extensions.get(methodName, parameters);
	}

	public FunctionValue getMemberFunction(Class<?> clazz, String methodName, int parameters) {
		return this.stackTable.getClassFunction(clazz, methodName, parameters);
	}

	private Context setStackTable(ArucasClassDefinitionMap definitions, Map<String, ArucasClassDefinitionMap> imports, Map<String, ArucasClassDefinitionMap> cached, boolean force) {
		this.stackTable.importableDefinitions = imports;
		this.stackTable.cachedDefinitions = cached;
		if (force) {
			this.stackTable.classDefinitions = definitions;
			return this;
		}
		this.stackTable.insertAllClassDefinitions(definitions);
		return this;
	}

	public Context setStackTable(ArucasClassDefinitionMap definitions, Map<String, ArucasClassDefinitionMap> imports, Map<String, ArucasClassDefinitionMap> cached) {
		return this.setStackTable(definitions, imports, cached, false);
	}

	@Deprecated(forRemoval = true)
	public void dumpScopes() {
		StringBuilder sb = new StringBuilder();
		sb.append("----------------------------\n");

		Iterator<StackTable> iter = this.stackTable.iterator();
		while (iter.hasNext()) {
			sb.append(iter.next()).append("\n");
		}

		ArucasClassDefinitionMap definitions = this.stackTable.getRoot().classDefinitions;
		if (definitions != null) {
			this.stackTable.getRoot().classDefinitions.iterator().forEachRemaining(System.out::println);
		}
		System.out.println(sb);
	}
}
