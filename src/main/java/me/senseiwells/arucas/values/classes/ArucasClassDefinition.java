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
import me.senseiwells.arucas.values.functions.EmbeddableFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.*;

public class ArucasClassDefinition extends AbstractClassDefinition {
	private final ArucasFunctionMap<ClassMemberFunction> methods;
	private final Map<String, Node> staticMemberVariableNodes;
	private final List<Node> staticInitializers;
	protected final ArucasFunctionMap<ClassMemberFunction> constructors;
	protected final Map<String, EmbededNode> memberVariables;
	protected final ArucasOperatorMap<ClassMemberFunction> operatorMap;

	public ArucasClassDefinition(String name) {
		super(name);
		this.methods = new ArucasFunctionMap<>();
		this.staticMemberVariableNodes = new HashMap<>();
		this.staticInitializers = new ArrayList<>();
		this.constructors = new ArucasFunctionMap<>();
		this.memberVariables = new LinkedHashMap<>();
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

	public void addMemberVariableNode(boolean isStatic, String name, Node value) {
		if (isStatic) {
			this.staticMemberVariableNodes.put(name, value);
			return;
		}
		this.memberVariables.put(name, new EmbededNode(value, null));
	}

	public void addEmbededMemberVariableNode(AbstractClassDefinition definition, String name, Node value) {
		this.memberVariables.put(name, new EmbededNode(value, definition));
	}

	protected void addClassProperties(ArucasClassValue thisValue, Context context) throws ThrowValue, CodeError {
		// Add methods
		for (ClassMemberFunction function : this.getMethods()) {
			function = function.copy(thisValue);
			function.setLocalContext(context);
			thisValue.addMethod(function);
		}

		// Add member variables
		for (Map.Entry<String, EmbededNode> entry : this.memberVariables.entrySet()) {
			String name = entry.getKey();
			EmbededNode embededNode = entry.getValue();

			Value<?> value = embededNode.node.visit(context);
			thisValue.addMemberVariable(name, value);

			if (embededNode.definition == null) {
				continue;
			}

			for (FunctionValue function : embededNode.definition.getMethods()) {
				if (thisValue.hasMember(function.getName(), function.getParameterCount())) {
					continue;
				}
				if (function instanceof EmbeddableFunction embeddableFunction) {
					embeddableFunction.setCallingMember(() -> thisValue.getMember(name));
					embeddableFunction.setDefinition(embededNode.definition);
					thisValue.addMethod(function);
				}
			}
		}

		this.operatorMap.forEach((type, function) -> {
			function = function.copy(thisValue);
			function.setLocalContext(context);
			thisValue.addOperatorMethod(type, function);
		});
	}

	@Override
	public ArucasFunctionMap<ClassMemberFunction> getMethods() {
		return this.methods;
	}

	@Override
	protected void initialiseStatics(Context context) throws CodeError, ThrowValue {
		for (Map.Entry<String, Node> entry : this.staticMemberVariableNodes.entrySet()) {
			this.getStaticMemberVariables().put(entry.getKey(), entry.getValue().visit(context));
		}

		for (Node staticNode : this.staticInitializers) {
			staticNode.visit(context);
		}
	}

	@Override
	public ArucasClassValue createNewDefinition(Context ctx, List<Value<?>> parameters, ISyntax syntaxPosition) throws CodeError, ThrowValue {
		Context context = this.getLocalContext(ctx);

		ArucasClassValue thisValue = new ArucasClassValue(this);

		this.addClassProperties(thisValue, context);

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

	protected record EmbededNode(Node node, AbstractClassDefinition definition) { }
}
