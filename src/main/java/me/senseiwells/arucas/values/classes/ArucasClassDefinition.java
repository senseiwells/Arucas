package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.nodes.Node;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.ClassMemberFunction;

import java.util.*;

public class ArucasClassDefinition extends AbstractClassDefinition {
	private final Set<ClassMemberFunction> methods;
	private final Set<ClassMemberFunction> constructors;
	private final Map<String, Node> memberVariables;
	private final Map<String, Node> staticMemberVariableNodes;
	private final List<Node> staticInitialisers;
	private final Map<Token.Type, ClassMemberFunction> operatorMethods;
	
	public ArucasClassDefinition(String name) {
		super(name);
		this.methods = new HashSet<>();
		this.constructors = new HashSet<>();
		this.memberVariables = new HashMap<>();
		this.staticMemberVariableNodes = new HashMap<>();
		this.staticInitialisers = new ArrayList<>();
		this.operatorMethods = new HashMap<>();
	}

	public void addMethod(ClassMemberFunction method) {
		this.methods.add(method);
	}

	public void addConstructor(ClassMemberFunction constructor) {
		this.constructors.add(constructor);
	}

	public void addStaticInitialiser(Node node) {
		this.staticInitialisers.add(node);
	}

	public void addOperatorMethod(Token.Type tokenType, ClassMemberFunction method) {
		this.operatorMethods.put(tokenType, method);
	}

	@Override
	public Set<ClassMemberFunction> getMethods() {
		return this.methods;
	}

	public void initialiseStatics(Context context) throws ThrowValue, CodeError {
		for (Map.Entry<String, Node> entry : this.staticMemberVariableNodes.entrySet()) {
			this.getStaticMemberVariables().put(entry.getKey(), entry.getValue().visit(context));
		}
		for (Node staticNode : this.staticInitialisers) {
			staticNode.visit(context);
		}
	}

	@SuppressWarnings("UnusedReturnValue")
	public Node addMemberVariableNode(boolean isStatic, String name, Node value) {
		return isStatic ? this.staticMemberVariableNodes.put(name, value) : this.memberVariables.put(name, value);
	}
	
	public ArucasClassValue createNewDefinition(Context context, List<Value<?>> parameters, ISyntax syntaxPosition) throws CodeError, ThrowValue {
		ArucasClassValue thisValue = new ArucasClassValue(this);
		// Add methods
		for (ClassMemberFunction function : this.getMethods()) {
			thisValue.addMethod(function.copy(thisValue));
		}

		// Add operator methods
		for (Map.Entry<Token.Type, ClassMemberFunction> entry : this.operatorMethods.entrySet()) {
			thisValue.addOperatorMethods(entry.getKey(), entry.getValue().copy(thisValue));
		}

		// Add member variables
		for (Map.Entry<String, Node> entry : this.memberVariables.entrySet()) {
			thisValue.addMemberVariable(entry.getKey(), entry.getValue().visit(context));
		}

		int parameterCount = parameters.size() + 1;
		if (this.constructors.isEmpty() && parameterCount == 1) {
			return thisValue;
		}
		// Finding the constructor with the correct amount of parameters
		boolean matched = false;
		for (ClassMemberFunction constructor : this.constructors) {
			if (parameterCount != constructor.getParameterCount()) {
				continue;
			}
			matched = true;
			constructor.copy(thisValue).call(context, parameters, false);
			break;
		}
		if (!matched) {
			throw new RuntimeError("No such constructor for %s".formatted(this.getName()), syntaxPosition, context);
		}
		
		return thisValue;
	}

	@Override
	public Class<ArucasClassValue> getValueClass() {
		return ArucasClassValue.class;
	}
}
