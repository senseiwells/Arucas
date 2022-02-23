package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.ArrayList;
import java.util.List;

public class StaticCallNode extends Node {
	private final AbstractClassDefinition classDefinition;
	private final List<Node> argumentNodes;

	public StaticCallNode(Token token, AbstractClassDefinition classDefinition, List<Node> argumentNodes) {
		super(token);
		this.classDefinition = classDefinition;
		this.argumentNodes = argumentNodes;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		this.keepRunning();
		int arguments = this.argumentNodes.size();

		FunctionValue method = this.classDefinition.getMember(this.token.content, arguments);

		if (method == null) {
			String parameters = (arguments == 0) ? "" : " with %d parameter%s".formatted(arguments, arguments == 1 ? "" : "s");
			throw new RuntimeError("Member function '%s'%s was not defined for the type '%s'".formatted(
				this.token.content,
				parameters,
				this.classDefinition.getName()
			), this.syntaxPosition, context);
		}

		List<Value<?>> argumentValues = new ArrayList<>();
		for (Node node : this.argumentNodes) {
			argumentValues.add(node.visit(context));
		}

		context.pushScope(this.syntaxPosition);
		Value<?> result = method.call(context, argumentValues);
		context.popScope();
		return result;
	}
}
