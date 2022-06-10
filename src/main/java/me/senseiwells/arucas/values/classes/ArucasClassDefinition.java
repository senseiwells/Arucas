package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.nodes.Node;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.ArucasOperatorMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.UserDefinedClassFunction;
import me.senseiwells.arucas.values.functions.UserDefinedFunction;

import java.util.*;

public class ArucasClassDefinition extends AbstractClassDefinition {
	private final ArucasFunctionMap<UserDefinedClassFunction> methods;
	private final Map<String, Node> staticMemberVariableNodes;
	private final List<Node> staticInitializers;
	private final Map<String, Node> memberVariables;
	protected final ArucasFunctionMap<UserDefinedClassFunction> constructors;
	protected final ArucasOperatorMap<UserDefinedClassFunction> operatorMap;

	public ArucasClassDefinition(String name) {
		super(name);
		this.methods = new ArucasFunctionMap<>();
		this.staticMemberVariableNodes = new HashMap<>();
		this.staticInitializers = new ArrayList<>();
		this.memberVariables = new LinkedHashMap<>();
		this.constructors = new ArucasFunctionMap<>();
		this.operatorMap = new ArucasOperatorMap<>();
	}

	public void addMethod(UserDefinedClassFunction method) {
		this.methods.add(method);
	}

	public void addConstructor(UserDefinedClassFunction constructor) {
		this.constructors.add(constructor);
	}

	public void addStaticInitializer(Node node) {
		this.staticInitializers.add(node);
	}

	public void addOperatorMethod(Token.Type tokenType, UserDefinedClassFunction method) {
		this.operatorMap.add(tokenType, method);
	}

	public void addMemberVariableNode(boolean isStatic, String name, Node value) {
		if (isStatic) {
			this.staticMemberVariableNodes.put(name, value);
			return;
		}
		this.memberVariables.put(name, value);
	}

	public boolean hasMemberVariable(boolean isStatic, String name) {
		return isStatic ? this.staticMemberVariableNodes.containsKey(name) : this.memberVariables.containsKey(name);
	}

	protected void addClassProperties(ArucasClassValue thisValue, Context context) throws ThrowValue, CodeError {
		// Add member variables
		for (Map.Entry<String, Node> entry : this.memberVariables.entrySet()) {
			String name = entry.getKey();
			Node node = entry.getValue();

			Value value = node.visit(context);
			thisValue.addMemberVariable(name, value);
		}
	}

	@Override
	public ArucasFunctionMap<UserDefinedClassFunction> getMethods() {
		return this.methods;
	}

	@Override
	public ArucasFunctionMap<UserDefinedClassFunction> getConstructors() {
		return this.constructors;
	}

	@Override
	protected void initialiseStatics(Context context) throws CodeError, ThrowValue {
		// Set local context for functions
		for (FunctionValue functionValue : this.getStaticMethods()) {
			if (functionValue instanceof UserDefinedFunction userFunction) {
				userFunction.setLocalContext(context);
			}
		}
		for (UserDefinedFunction function : this.methods) {
			function.setLocalContext(context);
		}

		for (Map.Entry<String, Node> entry : this.staticMemberVariableNodes.entrySet()) {
			this.getStaticMemberVariables().put(entry.getKey(), entry.getValue().visit(context));
		}
		this.staticMemberVariableNodes.clear();

		for (Node staticNode : this.staticInitializers) {
			staticNode.visit(context);
		}
		this.staticInitializers.clear();
	}

	@Override
	public ArucasClassValue createNewDefinition(Context ctx, List<Value> parameters, ISyntax syntaxPosition) throws CodeError, ThrowValue {
		Context context = this.getLocalContext(ctx);

		ArucasClassValue thisValue = new ArucasClassValue(this);

		this.addClassProperties(thisValue, context);

		int parameterCount = parameters.size() + 1;
		if (this.constructors.isEmpty() && parameterCount == 1) {
			return thisValue;
		}

		UserDefinedClassFunction constructor = this.constructors.get(this.getName(), parameterCount);
		if (constructor == null) {
			throw new RuntimeError("No such constructor for %s".formatted(this.getName()), syntaxPosition, context);
		}

		parameters.add(0, thisValue);
		constructor.call(context, parameters, true);

		return thisValue;
	}

	@Override
	public Class<ArucasClassValue> getValueClass() {
		return ArucasClassValue.class;
	}
}
