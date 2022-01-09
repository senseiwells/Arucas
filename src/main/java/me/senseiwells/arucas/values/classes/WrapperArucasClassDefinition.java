package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.nodes.Node;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.ClassMemberFunction;
import me.senseiwells.arucas.values.functions.WrapperClassMemberFunction;

import java.util.*;
import java.util.function.Supplier;

public class WrapperArucasClassDefinition extends ArucasClassDefinition {
	private final Supplier<IArucasWrappedClass> supplier;
	
	public WrapperArucasClassDefinition(String name, Supplier<IArucasWrappedClass> supplier) {
		super(name);
		this.supplier = supplier;
	}
	
	@Override
	public void addMethod(ClassMemberFunction method) {
		// Make sure that method is an instance of the Wrapper members
		this.methods.add((WrapperClassMemberFunction)method);
	}
	
	@Override
	public void addConstructor(ClassMemberFunction constructor) {
		// Make sure that method is an instance of the Wrapper members
		this.constructors.add((WrapperClassMemberFunction)constructor);
	}
	
	@Override
	public void addOperatorMethod(Token.Type tokenType, ClassMemberFunction method) {
		// Make sure that method is an instance of the Wrapper members
		this.operatorMethods.put(tokenType, (WrapperClassMemberFunction)method);
	}
	
	public ArucasClassValue createNewDefinition(Context context, List<Value<?>> parameters, ISyntax syntaxPosition) throws CodeError, ThrowValue {
		ArucasClassValue thisValue = new ArucasClassValue(this);
		IArucasWrappedClass wrappedClass = this.supplier.get();
		
		for (ClassMemberFunction function : this.methods) {
			thisValue.addMethod(((WrapperClassMemberFunction)function).copy(wrappedClass));
		}
		
		// TODO: There are no Nodes inside a wrapped arucas class
		// Add operator methods
		for (Map.Entry<Token.Type, ClassMemberFunction> entry : this.operatorMethods.entrySet()) {
			thisValue.addOperatorMethods(entry.getKey(), ((WrapperClassMemberFunction)entry.getValue()).copy(wrappedClass));
		}
		
		// Add member variables
		for (Map.Entry<String, Node> entry : this.memberVariables.entrySet()) {
			thisValue.addMemberVariable(entry.getKey(), entry.getValue().visit(context));
		}
		
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
}
