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
import me.senseiwells.arucas.values.functions.ClassMemberFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArucasClassDefinition extends AbstractClassDefinition {
	private final ArucasFunctionMap<ClassMemberFunction> methods;
	private final Map<String, Node> staticMemberVariableNodes;
	private final List<Node> staticInitializers;
	protected final ArucasFunctionMap<ClassMemberFunction> constructors;
	protected final Map<String, Node> memberVariables;
	protected final ArucasOperatorMap<ClassMemberFunction> operatorMap;

	public ArucasClassDefinition(String name) {
		super(name);
		this.methods = new ArucasFunctionMap<>();
		this.staticMemberVariableNodes = new HashMap<>();
		this.staticInitializers = new ArrayList<>();
		this.constructors = new ArucasFunctionMap<>();
		this.memberVariables = new HashMap<>();
		this.operatorMap = new ArucasOperatorMap<>();
	}

	public void addMethod(ClassMemberFunction method) {
		this.methods.add(method);
	}

	public void addConstructor(ClassMemberFunction constructor) {
		this.constructors.add(constructor);
	}

	public void addStaticInitializer(Node node) {
		this.staticInitializers.add(node);
	}

	public void addOperatorMethod(Token.Type tokenType, ClassMemberFunction method) {
		this.operatorMap.add(tokenType, method);
	}

	@SuppressWarnings("UnusedReturnValue")
	public Node addMemberVariableNode(boolean isStatic, String name, Node value) {
		return isStatic ? this.staticMemberVariableNodes.put(name, value) : this.memberVariables.put(name, value);
	}

	@Override
	public ArucasFunctionMap<ClassMemberFunction> getMethods() {
		return this.methods;
	}

	@Override
	public void initialiseStatics(Context context) throws CodeError, ThrowValue {
		for (Map.Entry<String, Node> entry : this.staticMemberVariableNodes.entrySet()) {
			this.getStaticMemberVariables().put(entry.getKey(), entry.getValue().visit(context));
		}

		for (Node staticNode : this.staticInitializers) {
			staticNode.visit(context);
		}
	}

	@Override
	public ArucasClassValue createNewDefinition(Context context, List<Value<?>> parameters, ISyntax syntaxPosition) throws CodeError, ThrowValue {
		ArucasClassValue thisValue = new ArucasClassValue(this);
		// Add methods
		for (ClassMemberFunction function : this.getMethods()) {
			thisValue.addMethod(function.copy(thisValue));
		}

		// Add member variables
		for (Map.Entry<String, Node> entry : this.memberVariables.entrySet()) {
			thisValue.addMemberVariable(entry.getKey(), entry.getValue().visit(context));
		}

		this.operatorMap.forEach((type, function) -> thisValue.addOperatorMethod(type, function.copy(thisValue)));

		int parameterCount = parameters.size() + 1;
		if (this.constructors.isEmpty() && parameterCount == 1) {
			return thisValue;
		}

		ClassMemberFunction constructor = this.constructors.get(this.getName(), parameterCount);
		if (constructor == null) {
			throw new RuntimeError("No such constructor for %s".formatted(this.getName()), syntaxPosition, context);
		}

		constructor.copy(thisValue).call(context, parameters, false);
		return thisValue;
	}

	@Override
	public Class<ArucasClassValue> getValueClass() {
		return ArucasClassValue.class;
	}
}
